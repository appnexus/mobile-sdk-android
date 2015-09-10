package com.appnexus.opensdk;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.appnexus.opensdk.utils.Drawables;
import com.appnexus.opensdk.utils.VastVideoUtil;

import java.lang.ref.WeakReference;
import java.util.Formatter;
import java.util.Locale;

public class VideoControllerBarView extends RelativeLayout {
    public static final int CONTROLLER_DISPLAY_TIMEOUT = 3600000;
    private IMediaPlayerControl mediaPlayerControl;
	private Context context;
	private ViewGroup anchor;
	private View rootView;
	public ProgressBar progressBar;
	private TextView endTime, currentTime;
	private boolean isShowing;
	private boolean isDragging;
	private static final int DEFAULT_TIMEOUT = 0;
	private static final int FADE_OUT = 1;
	private static final int SHOW_PROGRESS = 2;
	private StringBuilder formatBuilder;
	private Formatter formatter;
	public ImageView pause;
	private ImageView fullscreen;
	private ImageView mute;
	private Handler mHandler = new MessageHandler(this);
	public static boolean isrewind;


	@Override
	protected void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	public VideoControllerBarView(Context context, boolean isUseFastForward) {
		super(context);
		setLayoutParams(new LayoutParams(360, 100));
		this.context = context;
        rootView = null;
	}

	public VideoControllerBarView(Context context) {
		this(context, true);

	}

	@Override
	public void onFinishInflate() {
        super.onFinishInflate();
		if (rootView != null)
			initControllerView(rootView);
	}

	public void setMediaPlayer(IMediaPlayerControl player) {
		mediaPlayerControl = player;
		updatePausePlay();
		updateFullScreen();
	}

	/**
	 * Set the view that acts as the anchor for the control view. This can for
	 * example be a VideoView, or your Activity's main view.
	 *
	 * @param view The view to which to anchor the controller when it is visible.
	 * @param vastVideoPlayer
	 */
	public void setAnchorView(ViewGroup view, VastVideoPlayer vastVideoPlayer) {
		anchor = view;

		LayoutParams frameParams = new LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		removeAllViews();
		View v = makeControllerView(vastVideoPlayer);
		v.setLayoutParams(frameParams);
		addView(v);
	}

	/**
	 * Create the view that holds the widgets that control playback. Derived
	 * classes can override this to create their own.
	 * @param vastVideoPlayer
	 *
	 * @return The controller view.
	 * @hide This doesn't work as advertised
	 */
	private View makeControllerView(VastVideoPlayer vastVideoPlayer) {
		rootView = getControllerView(vastVideoPlayer);
		initControllerView(rootView);

		return rootView;
	}

	/**
	 * Initialize video controller view and set the related listener to each
	 * controller.
	 *
	 * @param v
	 */
	private void initControllerView(View v) {

        int videoMuteId = VastVideoUtil.VIDEO_MUTE;
        mute = (ImageView) v.findViewById(videoMuteId);
		if (mute != null) {
			mute.requestFocus();
			mute.setOnClickListener(mMuteListener);
		}

        int videoPauseId = VastVideoUtil.VIDEO_PAUSE;
        pause = (ImageView) v.findViewById(videoPauseId);
		if (pause != null) {
			pause.requestFocus();
			pause.setOnClickListener(mPauseListener);
		}

        int videoFullscreenId = VastVideoUtil.VIDEO_FULLSCREEN;
        fullscreen = (ImageView) v.findViewById(videoFullscreenId);
		if (fullscreen != null) {
			fullscreen.requestFocus();
			fullscreen.setOnClickListener(mFullscreenListener);
		}

        int videoMediacontrollerProgressId = VastVideoUtil.VIDEO_MEDIACONTROLLER_PROGRESS;
        progressBar = (ProgressBar) v
				.findViewById(videoMediacontrollerProgressId);
		if (progressBar != null) {
			if (progressBar instanceof SeekBar) {
				SeekBar seeker = (SeekBar) progressBar;
				seeker.setOnSeekBarChangeListener(mSeekListener);
			}
			progressBar.setMax(1000);
		}
        int videoTimeId = VastVideoUtil.VIDEO_TIME;
        endTime = (TextView) v.findViewById(videoTimeId);
        int videoTimeCurrentId = VastVideoUtil.VIDEO_TIME_CURRENT;
        currentTime = (TextView) v.findViewById(videoTimeCurrentId);
		formatBuilder = new StringBuilder();
		formatter = new Formatter(formatBuilder, Locale.getDefault());
	}

	/**
	 * Show the controller on screen. It will go away automatically after 3
	 * seconds of inactivity.
	 */
	public void show() {
		show(DEFAULT_TIMEOUT);
	}

	/**
	 * Disable pause or seek buttons if the stream cannot be paused or seeked.
	 * This requires the control interface to be a MediaPlayerControlExt
	 */
	private void disableUnsupportedButtons() {
		if (mediaPlayerControl == null) {
			return;
		}

		try {
			if (pause != null && !mediaPlayerControl.canPause()) {
				pause.setEnabled(false);
			}

			if (progressBar != null ) {
				progressBar.setEnabled(false);
			}
		} catch (IncompatibleClassChangeError ex) {
		}
	}

	/**
	 * Show the controller on screen. It will go away automatically after
	 * 'timeout' milliseconds of inactivity.
	 *
	 * @param timeout
	 *            The timeout in milliseconds. Use 0 to show the controller
	 *            until hide() is called.
	 */
	public void show(int timeout) {
		if (!isShowing && anchor != null) {
			setProgress();
			if (pause != null) {
				pause.requestFocus();
			}
			disableUnsupportedButtons();
			LayoutParams param = new LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
            param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			anchor.addView(this, param);
			isShowing = true;
		}
		updatePausePlay();
		updateFullScreen();

		// cause the progress bar to be updated even if isShowing
		// was already true. This happens, for example, if we're
		// paused with the progress bar showing the user hits play.
		mHandler.sendEmptyMessage(SHOW_PROGRESS);

		Message msg = mHandler.obtainMessage(FADE_OUT);
		if (timeout != 0) {
			mHandler.removeMessages(FADE_OUT);
			mHandler.sendMessageDelayed(msg, timeout);
		}
	}

	public boolean isShowing() {
		return isShowing;
	}

	/**
	 * Remove the controller from the screen.
	 */
	public void hide() {
		if (anchor == null) {
			return;
		}

		try {
			anchor.removeView(this);
			mHandler.removeMessages(SHOW_PROGRESS);
		} catch (IllegalArgumentException ex) {
			Log.w("MediaController", "already removed");
		}
		isShowing = false;
	}

	/**
	 *
	 * @param timeMs
	 * @return
	 */
	private String stringForTime(int timeMs) {
		int totalSeconds = timeMs / 1000;

		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;

		formatBuilder.setLength(0);
		if (hours > 0) {
			return formatter.format("%d:%02d:%02d", hours, minutes, seconds)
					.toString();
		} else {
			return formatter.format("%02d:%02d", minutes, seconds).toString();
		}
	}

	/**
	 *
	 * @return
	 */
	public int setProgress() {
		if (mediaPlayerControl == null || isDragging) {
			return 0;
		}

		int position = mediaPlayerControl.getCurrentPosition();
		int duration = mediaPlayerControl.getDuration();
		if (progressBar != null) {
			if (duration > 0) {
				long pos = 1000L * position / duration;
				progressBar.setProgress((int) pos);
			}
			int percent = mediaPlayerControl.getBufferPercentage();
			progressBar.setSecondaryProgress(percent * 10);
		}

		if (endTime != null)
			endTime.setText(stringForTime(duration));
		if (currentTime != null)
			currentTime.setText(stringForTime(position));

		return position;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		show(DEFAULT_TIMEOUT);
		return true;
	}

	@Override
	public boolean onTrackballEvent(MotionEvent ev) {
		show(DEFAULT_TIMEOUT);
		return false;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (mediaPlayerControl == null) {
			return true;
		}
		int keyCode = event.getKeyCode();
		final boolean uniqueDown = event.getRepeatCount() == 0
				&& event.getAction() == KeyEvent.ACTION_DOWN;
		if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK
				|| keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
				|| keyCode == KeyEvent.KEYCODE_SPACE) {
			if (uniqueDown) {
				doPauseResume();
				show(DEFAULT_TIMEOUT);
				if (pause != null) {
					pause.requestFocus();
				}
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
			if (uniqueDown && !mediaPlayerControl.isPlaying()) {
				mediaPlayerControl.start();
				updatePausePlay();
				show(DEFAULT_TIMEOUT);
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
				|| keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
			if (uniqueDown && mediaPlayerControl.isPlaying()) {
				mediaPlayerControl.pause();
				updatePausePlay();
				show(DEFAULT_TIMEOUT);
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
				|| keyCode == KeyEvent.KEYCODE_VOLUME_UP
				|| keyCode == KeyEvent.KEYCODE_VOLUME_MUTE) {
			return super.dispatchKeyEvent(event);
		} else if (keyCode == KeyEvent.KEYCODE_BACK
				|| keyCode == KeyEvent.KEYCODE_MENU) {
			if (uniqueDown) {
				hide();
			}
			return true;
		}
		show(DEFAULT_TIMEOUT);
		return super.dispatchKeyEvent(event);
	}

	private OnClickListener mPauseListener = new OnClickListener() {
		public void onClick(View v) {
			doPauseResume();
			show(DEFAULT_TIMEOUT);
		}
	};
	private OnClickListener mMuteListener = new OnClickListener() {
		public void onClick(View v) {
			doMuteUnMute();
			show(DEFAULT_TIMEOUT);
		}
	};
	private OnClickListener mFullscreenListener = new OnClickListener() {
		public void onClick(View v) {
			doToggleFullscreen();
			show(DEFAULT_TIMEOUT);
		}
	};

	public void updatePausePlay() {
		if (rootView == null || pause == null || mediaPlayerControl == null) {
			return;
		}
		if (mediaPlayerControl.isPlaying()) {
			pause.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_media_pause));
		} else {
			pause.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_media_play));
		}
	}

	public void updateMuteUnMute() {
		if (rootView == null || mute == null || mediaPlayerControl == null) {
			return;
		}
		if (mediaPlayerControl.isMute()) {
			mute.setImageDrawable(Drawables.MUTED.decodeImage(getContext()));
		} else {
			mute.setImageDrawable(Drawables.UNMUTED.decodeImage(getContext()));
		}
	}

	public void updateFullScreen() {
		if (rootView == null || fullscreen == null
				|| mediaPlayerControl == null) {
			return;
		}

		if (mediaPlayerControl.isFullScreen()) {
			fullscreen.setImageDrawable(Drawables.FULLSCREENSHRINK
					.decodeImage(getContext()));
		} else {
			fullscreen.setImageDrawable(Drawables.FULLSCREENSTRETCH
					.decodeImage(getContext()));
		}
	}

	private void doPauseResume() {
		if (mediaPlayerControl == null) {
			return;
		}
		if (mediaPlayerControl.isPlaying()) {
			mediaPlayerControl.pause();
		} else {
			mediaPlayerControl.start();
		}
		updatePausePlay();
	}

	private void doMuteUnMute() {
		if (mediaPlayerControl == null) {
			return;
		}

		if (mediaPlayerControl.isMute()) {
			mediaPlayerControl.unMute();
		} else {
			mediaPlayerControl.mute();
		}
		updateMuteUnMute();
	}

	private void dorewind() {
		if (mediaPlayerControl == null) {
			return;
		}
		if (mediaPlayerControl.isPlaying()) {
			mediaPlayerControl.rewind();
		}
		updaterewind();
	}

	private void updaterewind() {
		if (rootView == null || mute == null || mediaPlayerControl == null) {
			return;
		}
	}

	private void doToggleFullscreen() {
		if (mediaPlayerControl == null) {
			return;
		}
		mediaPlayerControl.toggleFullScreen();
		updateFullScreen();
	}



	private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
		public void onStartTrackingTouch(SeekBar bar) {
			show(CONTROLLER_DISPLAY_TIMEOUT);
			isDragging = true;
			mHandler.removeMessages(SHOW_PROGRESS);
		}

		public void onProgressChanged(SeekBar bar, int progress,
				boolean fromuser) {
			if (mediaPlayerControl == null) {
				return;
			}
			if (!fromuser) {
				return;
			}

			long duration = mediaPlayerControl.getDuration();
			long currentduration = mediaPlayerControl.getCurrentPosition();

			long newposition = (duration * progress) / 1000L;

			if (currentduration - newposition > 0) {
				if (mediaPlayerControl.isPlaying()) {
					isrewind = true;
				}
				updaterewind();
			}
			mediaPlayerControl.seekTo((int) newposition);
			if (currentTime != null)
				currentTime.setText(stringForTime((int) newposition));

		}

		public void onStopTrackingTouch(SeekBar bar) {
			isDragging = false;
			if (isrewind && mediaPlayerControl.isPlaying()) {
				mediaPlayerControl.rewind();
				isrewind = false;
			}
			setProgress();
			updatePausePlay();
			show(DEFAULT_TIMEOUT);
			Log.d("OnSeekBarChangeListener", "onStopTrackingTouch");
			mHandler.sendEmptyMessage(SHOW_PROGRESS);
		}
	};

	@Override
	public void setEnabled(boolean enabled) {
		if (pause != null) {
			pause.setEnabled(enabled);
		}

		if (progressBar != null) {
			progressBar.setEnabled(enabled);
		}
		disableUnsupportedButtons();
		super.setEnabled(enabled);
	}

	private OnClickListener mRewListener = new OnClickListener() {
		public void onClick(View v) {
			if (mediaPlayerControl == null) {
				return;
			}

			int pos = mediaPlayerControl.getCurrentPosition();
			int duration = mediaPlayerControl.getDuration();
			int factor = duration / 12;
			// pos -= 50; // milliseconds
			pos -= factor;
			mediaPlayerControl.seekTo(pos);
			setProgress();
			dorewind();
			show(DEFAULT_TIMEOUT);
		}
	};

	private OnClickListener mFfwdListener = new OnClickListener() {
		public void onClick(View v) {
			if (mediaPlayerControl == null) {
				return;
			}
			int pos = mediaPlayerControl.getCurrentPosition();
			int duration = mediaPlayerControl.getDuration();
			int factor = duration / 8;
			// pos += 50; // milliseconds
			pos += factor;
			mediaPlayerControl.seekTo(pos);
			setProgress();

			show(DEFAULT_TIMEOUT);
		}
	};


	public interface IMediaPlayerControl {
		void start();

		void pause();

		boolean isPaused();

		int getDuration();

		int getCurrentPosition();

		void seekTo(int pos);

		boolean isPlaying();

		int getBufferPercentage();

		boolean canPause();

		boolean isFullScreen();

		void toggleFullScreen();

		void mute();

		boolean isMute();

		void unMute();

		void rewind();

	}

	private static class MessageHandler extends Handler {
		private final WeakReference<VideoControllerBarView> mView;

		MessageHandler(VideoControllerBarView view) {
			mView = new WeakReference<VideoControllerBarView>(view);
		}

		@Override
		public void handleMessage(Message msg) {
			VideoControllerBarView view = mView.get();
			if (view == null || view.mediaPlayerControl == null) {
				return;
			}
			int pos;
			switch (msg.what) {
			case FADE_OUT:
				view.hide();
				break;
			case SHOW_PROGRESS:
				pos = view.setProgress();
				if (!view.isDragging && view.isShowing
						&& view.mediaPlayerControl.isPlaying()) {
					msg = obtainMessage(SHOW_PROGRESS);
					sendMessageDelayed(msg, 1000 - (pos % 1000));
				}
				break;
			}
		}
	}

	/**
	 * Create video Controller View
	 * @param vastVideoPlayer
	 *
	 * @return
	 */
	private View getControllerView(VastVideoPlayer vastVideoPlayer) {

		LinearLayout mainLinearLayout = new LinearLayout(context);
        mainLinearLayout.setBackgroundColor(Color.TRANSPARENT);

		mainLinearLayout.setOrientation(LinearLayout.VERTICAL);
		LayoutParams relativeLayout = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mainLinearLayout.setGravity(Gravity.BOTTOM);
		relativeLayout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		mainLinearLayout.setLayoutParams(relativeLayout);

		LinearLayout linearLayoutForButton = new LinearLayout(context);
		linearLayoutForButton.setGravity(Gravity.CENTER);
		linearLayoutForButton.setOrientation(LinearLayout.HORIZONTAL);
		LayoutParams layoutparamsForButton = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		linearLayoutForButton.setLayoutParams(layoutparamsForButton);


		ImageView pause = new ImageView(context);
        int videoPauseId = VastVideoUtil.VIDEO_PAUSE;
        pause.setId(videoPauseId);
		pause.setContentDescription(VastVideoUtil.STRING_MEDIA_CONTROLS);
		LayoutParams layoutparampause = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		pause.setLayoutParams(layoutparampause);
		linearLayoutForButton.addView(pause);

		mainLinearLayout.addView(linearLayoutForButton);

		RelativeLayout relativeLayoutText = new RelativeLayout(context);
		LayoutParams layoutparam = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		relativeLayoutText.setPadding(0, 0, 0,
				VastVideoUtil.getSizeInDP(context, 4));
		relativeLayoutText.setLayoutParams(layoutparam);

		TextView time_current = new TextView(context);
        int videoTimeCurrentId = VastVideoUtil.VIDEO_TIME_CURRENT;
        time_current.setId(videoTimeCurrentId);
		time_current.setPadding(VastVideoUtil.getSizeInDP(context, 4), 0,
                VastVideoUtil.getSizeInDP(context, 4),
                VastVideoUtil.getSizeInDP(context, 4));
		time_current.setText("00:00");
        time_current.setTextColor(Color.WHITE);
		time_current.setTextSize(14);
		LayoutParams layoutparamtimecurrent = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutparamtimecurrent.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		layoutparamtimecurrent.addRule(RelativeLayout.CENTER_VERTICAL);
		time_current.setLayoutParams(layoutparamtimecurrent);
		relativeLayoutText.addView(time_current);

		SeekBar mediacontroller_progress = new SeekBar(context);
        int videoMediacontrollerProgressId = VastVideoUtil.VIDEO_MEDIACONTROLLER_PROGRESS;
        mediacontroller_progress
				.setId(videoMediacontrollerProgressId);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				VastVideoUtil.getSizeInDP(context, 32));

		layoutParams.addRule(RelativeLayout.RIGHT_OF,
                videoTimeCurrentId);
        int videoTimeId = VastVideoUtil.VIDEO_TIME;
        layoutParams.addRule(RelativeLayout.LEFT_OF, videoTimeId);
		mediacontroller_progress.setLayoutParams(layoutParams);
		relativeLayoutText.addView(mediacontroller_progress);

		TextView time = new TextView(context);
		time.setId(videoTimeId);
		time.setPadding(VastVideoUtil.getSizeInDP(context, 4), 0,
                VastVideoUtil.getSizeInDP(context, 4),
                VastVideoUtil.getSizeInDP(context, 4));
		time.setText("00:00");
		time.setTextSize(14);
        time.setTextColor(Color.WHITE);

		LayoutParams layoutParamstime = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        int videoFullscreenId = VastVideoUtil.VIDEO_FULLSCREEN;
        layoutParamstime.addRule(RelativeLayout.LEFT_OF,
                videoFullscreenId);
		layoutParamstime.addRule(RelativeLayout.CENTER_VERTICAL);
		time.setLayoutParams(layoutParamstime);
		relativeLayoutText.addView(time);

		ImageButton fullscreen = new ImageButton(context);
		fullscreen.setId(videoFullscreenId);
        fullscreen.setBackgroundColor(Color.TRANSPARENT);
		fullscreen.setContentDescription(VastVideoUtil.STRING_MEDIA_CONTROLS);
		fullscreen.setPadding(0, VastVideoUtil.getSizeInDP(context, 4),
                VastVideoUtil.getSizeInDP(context, 10),
                VastVideoUtil.getSizeInDP(context, 4));
        fullscreen.setVisibility(GONE);

		LayoutParams layoutParamsfullscreen = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        int videoMuteId = VastVideoUtil.VIDEO_MUTE;
        layoutParamsfullscreen.addRule(RelativeLayout.LEFT_OF,
                videoMuteId);
		layoutParamsfullscreen.topMargin = VastVideoUtil
				.getSizeInDP(context, -7);
		fullscreen.setLayoutParams(layoutParamsfullscreen);
		relativeLayoutText.addView(fullscreen);

		ImageView mute = new ImageView(context);
		mute.setId(videoMuteId);
		mute.setContentDescription(VastVideoUtil.STRING_MEDIA_CONTROLS);
		mute.setImageDrawable(Drawables.UNMUTED.decodeImage(getContext()));
		LayoutParams layoutParamsmute = new LayoutParams(
				VastVideoUtil.getSizeInDP(context, 32),
				VastVideoUtil.getSizeInDP(context, 26));
		layoutParamsmute.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		layoutParamsmute.addRule(RelativeLayout.CENTER_VERTICAL);
		mute.setLayoutParams(layoutParamsmute);
		relativeLayoutText.addView(mute);
		mainLinearLayout.addView(relativeLayoutText);
		return mainLinearLayout;

	}
}