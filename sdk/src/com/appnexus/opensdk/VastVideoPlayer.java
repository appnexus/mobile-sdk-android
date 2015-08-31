package com.appnexus.opensdk;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.VideoView;

import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.StringUtil;
import com.appnexus.opensdk.utils.VastVideoUtil;
import com.appnexus.opensdk.utils.ViewUtil;
import com.appnexus.opensdk.utils.WebviewUtil;
import com.appnexus.opensdk.vastdata.AdModel;
import com.appnexus.opensdk.vastdata.LinearAdModel;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class VastVideoPlayer implements OnCompletionListener,
		VastVideoControllerView.IMediaPlayerControl,
		OnPreparedListener, OnClickListener, OnTouchListener,
		HibernationListener, GestureDetector.OnGestureListener,
		GestureDetector.OnDoubleTapListener {
    private String TAG = getClass().getSimpleName();
    public static final int INTERSTITIAL_EXPIRY_TIME = 30000;
    private static final float FIRST_QUARTER_MARKER = 0.25f;
    private static final float MID_POINT_MARKER = 0.50f;
    private static final float THIRD_QUARTER_MARKER = 0.75f;
    private static final long VIDEO_PROGRESS_TIMER_CHECKER_DELAY = 50;
    private static float SKIP_OFFSET = 1.1f;
    private Context context;
    private VideoView videoView;
	private Handler handler;
	private RelativeLayout relativeLayout;
	private VastVideoControllerView videoControllerView;
	private LinearAdModel linearAdModel;
	private Runnable videoProgressCheckerRunnable;
	private boolean isVideoProgressShouldBeChecked;
	private boolean isFirstMarkHit;
	private boolean isSecondMarkHit;
	private boolean isThirdMarkHit;
	private boolean isMuted;
	private boolean isPaused;
    protected boolean isVastInterstitial;
	private TextView countdownTimerTextView;
	private TextView skipButton;
	private double videoLength;
	private MediaPlayer mediaPlayer;
    private VideoAdEventsListener videoAdListener;
	private boolean isFullscreen;
	private VastVideoConfiguration videoConfiguration;
	private BroadcastReceiver mReceiver;
	private Timer videoDismissTimer;
	private TimerTask videoDismissTask;
	protected boolean isScreenDisplayOff;
	private int videoPausePosition;
	private int skipOffsetValue;
	private String parsedSkipOffset;
	private IUpdateCountdownTimerListener updateCountdownTimerListener;
	private boolean isFromBrowser;
	private int updateCounter;
	private GestureDetectorCompat mDetector;
	private LayoutParams originalLayoutParams;
	private int originalVideoId;
    private AdModel vastAd;
    private ProgressBar progressBar;


	public VastVideoPlayer() {
		handler = new Handler();
	}

    public void setVideoAdListener(VideoAdEventsListener videoAdListener) {
        this.videoAdListener = videoAdListener;
    }


    private void clearVastData() {
        vastAd = null;
        linearAdModel = null;
    }

    /**
	 * Method to show interstitial video view.
	 * 
	 * @param context
	 * @param relativeLayout
	 * @param videoConfiguration
	 */
	protected void initiateVASTVideoPlayer(Context context, VastVideoView videoView, RelativeLayout relativeLayout, VastVideoConfiguration videoConfiguration) {

        try {
            originalVideoId = videoView.getId();
            this.context = context;
            this.videoView = videoView;
            this.vastAd = videoView.getVastAd();
            this.relativeLayout = relativeLayout;
            this.videoConfiguration = videoConfiguration;
            isVastInterstitial = true;
            isFromBrowser = false;

            showLoader();
            this.videoView.setOnCompletionListener(this);
            this.videoView.getHolder().addCallback(surfaceHolderCB);
            this.videoView.setOnErrorListener(mediaErrorCB);
            this.videoView.setOnPreparedListener(this);

            if (vastAd != null
                    && vastAd.getCreativesArrayList() != null
                    && vastAd.getCreativesArrayList().size() > 0
                    && vastAd.getCreativesArrayList().get(0) != null
                    && vastAd.getCreativesArrayList().get(0).getLinearAdModel() != null) {

                linearAdModel = vastAd.getCreativesArrayList().get(0).getLinearAdModel();
                initializeVideoPlayer();

            }else{
                throw new Exception("Vast data not available");
            }

        }catch (Exception e){
            resetVideoView();
            Clog.e(TAG, "Exception initializing the video player: " + e.getMessage());
        }
	}

    MediaPlayer.OnErrorListener mediaErrorCB = new MediaPlayer.OnErrorListener() {

        @Override
        public boolean onError(MediaPlayer arg0, int what, int extra) {
            Clog.e(TAG, "onError what:" + what);
            Clog.e(TAG, "onError extra:" + extra);
            handleAdFinishScenario();
            return true;
        }
    };


    SurfaceHolder.Callback surfaceHolderCB = new SurfaceHolder.Callback() {

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Clog.d(TAG, "SurfaceDestroyed ");
            resumeMusicFromOtherApps();
            unregisterReceiver();
            if (getAdSlotConfiguration().shouldDismissVideoAdOnClick()) {
                clearVastData();
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Clog.i(TAG, "SurfaceCreated ");
            Clog.i(TAG, "Has returned from browser? " + isFromBrowser);

            try {
                registerForBroadCast();
                if (isFromBrowser) {
                    // Returned from browser
                    if (getAdSlotConfiguration().shouldDismissVideoAdOnClick()) {
                        isFromBrowser = false;
                        if (videoAdListener != null) {
                            videoAdListener.onVideoAdFinish();
                            Clog.d(TAG, "onVideoAdFinish");
                        }

                        if (VastVideoPlayer.this.videoView != null) {
                            VastVideoPlayer.this.videoView.setOnPreparedListener(null);
                            VastVideoPlayer.this.videoView.getHolder().removeCallback(this);
                        }
                    } else {
                        resumeVideoAd();
                    }
                } else {
                    // returned from background
                    resumeVideoAd();
                }
            }catch (Exception e){
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format,
        int width, int height) {

        }

    };
	
	
	private void registerForBroadCast() {
		try {
			IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
			filter.addAction(Intent.ACTION_SCREEN_OFF);
			filter.addAction(Intent.ACTION_MAIN);
			mReceiver = new HibernationBroadcast(this);
			context.registerReceiver(mReceiver, filter);
		} catch (Exception e) {
			Clog.e(TAG, "Exception occurred while registering for hibernation broadcast: " + e.getMessage());
		}
	}

	public void setUpdateCountdownTimerListener(
			IUpdateCountdownTimerListener updateCountdownTimerListener) {
		this.updateCountdownTimerListener = updateCountdownTimerListener;
	}

	/**
	 * 
	 * @return videoConfiguration
	 */
	public VastVideoConfiguration getAdSlotConfiguration() {
		return videoConfiguration;
	}

	/**
	 * method to play vast video ad.
	 */
	private void playVastVideoAd() throws Exception{
		Clog.i(TAG, "Attempting to play VAST video ad");
		int videoViewWidth = videoView.getWidth();

		if (videoViewWidth == 0) {
			videoViewWidth = VastVideoUtil.getScreenWidth(context);
		}

		Uri uri = Uri.parse(VastVideoUtil.getVASTVideoURL(
                linearAdModel.getMediaFilesArrayList(),
                VastVideoUtil.getPixelSize(context, videoViewWidth)));

		videoView.setVideoURI(uri);
		videoView.requestFocus();
		start();
	}

    private void showLoader() {
        progressBar = new ProgressBar(context);
        LayoutParams progressBarLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        progressBarLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        progressBar.setLayoutParams(progressBarLayoutParams);
        progressBar.setVisibility(View.VISIBLE);
        relativeLayout.addView(progressBar);
    }

    /**
	 * Get call on complete media player.
	 */
	@Override
	public void onCompletion(final MediaPlayer mp) {
		handleAdFinishScenario();
	}

	private void handleAdFinishScenario() {
        hideLoader();
		resumeMusicFromOtherApps();
        handleInterstitialFinish();
        unregisterReceiver();
	}

	private void handleInterstitialFinish() {
		Clog.d("onCompletion", "onCompletion VAST Interstitial released");
		handler.post(new Runnable() {
            public void run() {
                trackCompleteAdEvent();
                if (videoAdListener != null) {
                    videoAdListener.onVideoAdFinish();
                }
            }
        });
	}

	private void trackCompleteAdEvent() {
		isMuted = false;
        trackRequestInBackground(VastVideoUtil.getVastEventURLList(
                vastAd,
                VastVideoUtil.EVENT_COMPLETE));
		stopProgressChecker();
	}

	private void stopProgressChecker() {
		isVideoProgressShouldBeChecked = false;
		handler.removeCallbacks(videoProgressCheckerRunnable);
	}

	private void resumeVideoAd(){
		Clog.i(TAG, "Resuming video ad at: " + videoPausePosition);
        if(videoView != null) {
            videoView.seekTo(videoPausePosition);
            videoView.requestLayout();
            videoView.start();
        }
	}

	@Override
	public void start() {
        if (videoView != null) {
            pauseMusicFromOtherApps();
            videoView.start();
            if (isPaused()) {
                // Resume the video
                trackRequestInBackground(VastVideoUtil.getVastEventURLList(vastAd, VastVideoUtil.EVENT_RESUME));
                if (this.videoAdListener != null) {
                    this.videoAdListener.onVideoResume(getCurrentPosition());
                }
            } else {
                // Play video from beginning
                trackRequestInBackground(VastVideoUtil.getVastEventURLList(vastAd, VastVideoUtil.EVENT_START));
                if (this.videoAdListener != null) {
                    this.videoAdListener.onVideoPlay();
                }
            }
            isPaused = false;
        }

	}

	OnAudioFocusChangeListener audioFocusListener = new OnAudioFocusChangeListener() {
		@Override
		public void onAudioFocusChange(int focusChange) {
			Clog.i(TAG, "Focus change: " + focusChange);
		}
	};


	private void pauseMusicFromOtherApps() {
        try {
            if ((Build.VERSION.SDK_INT >= 11) && ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).isMusicActive()) {
                ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE))
                        .requestAudioFocus(audioFocusListener, AudioManager.STREAM_MUSIC,
                                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            }
        }catch (Exception e){
            Clog.e(TAG, "Exception occurred while pausing the music from other apps");
        }

	}

	private void resumeMusicFromOtherApps() {
        try {
            if ((Build.VERSION.SDK_INT >= 11) && ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).isMusicActive()) {
                ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE))
                        .abandonAudioFocus(audioFocusListener);

            }
        }catch (Exception e){
            Clog.e(TAG, "Exception occurred while resuming the music from other apps");
        }

	}


	@Override
	public void pause() {
        if(videoView != null) {
            videoView.pause();
            resumeMusicFromOtherApps();
            isPaused = true;
            trackRequestInBackground(VastVideoUtil.getVastEventURLList(vastAd, VastVideoUtil.EVENT_PAUSE));

            if (this.videoAdListener != null) {
                this.videoAdListener.onVideoPause(getCurrentPosition());
            }
        }
	}

	@Override
	public boolean isPaused() {
		return isPaused;
	}

	@Override
	public int getDuration() {
		return videoView.getDuration();
	}

	@Override
	public int getCurrentPosition() {
		return videoView.getCurrentPosition();
	}

	@Override
	public void seekTo(int pos) {
		videoView.seekTo(pos);
	}

	@Override
	public boolean isPlaying() {
		return videoView.isPlaying();
	}

	@Override
	public int getBufferPercentage() {
		return 0;
	}

	@Override
	public boolean canPause() {
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		return true;
	}

	@Override
	public boolean canSeekForward() {
		return true;
	}

	@Override
	public boolean isFullScreen() {
		return isFullscreen;
	}

	@Override
	public void toggleFullScreen() {
		if (isFullscreen) {
			collapseVideoView();
		} else {
			expandVideoToFullScreen();
		}
	}


	@Override
	public void mute() {
		if (mediaPlayer != null) {
			Clog.d(TAG, "Muting video");
			isMuted = true;
			mediaPlayer.setVolume(0, 0);
            trackRequestInBackground(VastVideoUtil.getVastEventURLList(
                    vastAd,
                    VastVideoUtil.EVENT_MUTE));

			if (this.videoAdListener != null) {
				this.videoAdListener.onMuteVideo();
			}
			Clog.d(TAG, "Video Muted");
		}
	}

	@Override
	public boolean isMute() {
		return isMuted;
	}

	@Override
	public void unMute() {

		if (mediaPlayer != null) {
            Clog.d(TAG, "Unmuting video");
			isMuted = false;
			mediaPlayer.setVolume(1, 1);
            trackRequestInBackground(VastVideoUtil.getVastEventURLList(vastAd,
                    VastVideoUtil.EVENT_UNMUTE));
			if (this.videoAdListener != null) {
				this.videoAdListener.onUnMuteVideo();
			}
            Clog.d(TAG, "Video Unmuted");
		}
	}

	@Override
	public void rewind() {
		if (videoView != null && videoView.isPlaying()) {
            trackRequestInBackground(
                    VastVideoUtil.getVastEventURLList(
                            vastAd,
                            VastVideoUtil.EVENT_REWIND));

			if (this.videoAdListener != null) {
				this.videoAdListener.onVideoPlayerRewind(getCurrentPosition(),
						getCurrentPosition());
			}

		}
	}

	protected void initializeVideoPlayer() {
        Clog.i(TAG, "initializeVideoPlayer");
		try {
			videoView.setId(originalVideoId);
			playVastVideoAd();
			isMuted = false;
			isVideoProgressShouldBeChecked = true;

			videoControllerView = new VastVideoControllerView(context, videoView.getId(), relativeLayout);
            int videoControllerId = VastVideoUtil.VIDEO_CONTROLLER;
            videoControllerView.setId(videoControllerId);

		} catch (Exception e) {
			String errorMessage = "Exception occurred while initializing video player - "+e.getMessage();
			Clog.e(TAG, errorMessage);
			resetVideoView();
			return;
		}
		
		videoProgressCheckerRunnable = new Runnable() {
			@Override
			public void run() {
				if (videoLength > 0) {
					int currentPosition = (int) (getCurrentPosition() / 1000);

					if (currentPosition > updateCounter) {

						if (isVastInterstitial) {
							updateVastInterstitialCountdownTimer();
						} else {
							updatePrerollCountdownTimer();
						}
					}
					trackQuartileEvents(currentPosition);
				}

				if (isVideoProgressShouldBeChecked) {
					handler.postDelayed(videoProgressCheckerRunnable,
							VIDEO_PROGRESS_TIMER_CHECKER_DELAY);
				}

			}
		};
        handler.post(videoProgressCheckerRunnable);

	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		try {
			this.mediaPlayer = mp;
			if (videoControllerView != null) {
				if (isFromBrowser || videoPausePosition > 0) {
                    handleVideoResume();
				}else{
                    handleVideoStart();
				}
			}
            hideLoader();
        } catch (Exception e) {
			String errorMessage = "Exception occurred while preparing video player - "+e.getMessage();
			Clog.e(TAG, errorMessage);
			resetVideoView();
		}

	}

    private void handleVideoResume() throws Exception{
        Clog.i(TAG, "About to resume video");
        if (isMuted && mediaPlayer != null){
            mediaPlayer.setVolume(0, 0);
        }
        skipOffsetValue = skipOffsetValue + 1;
        videoControllerView.setMediaPlayer(this);
        videoControllerView.show();
        if (videoControllerView.pause != null) {
            videoControllerView.pause.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_media_pause));
            videoControllerView.requestLayout();
        }
    }

    private void handleVideoStart() throws Exception{
        Clog.i(TAG, "About to display first frame of video");
        videoControllerView.setMediaPlayer(this);
        videoControllerView.setAnchorView(relativeLayout, this);

        videoView.setOnTouchListener(VastVideoPlayer.this);
        mDetector = new GestureDetectorCompat(context, this);
        mDetector.setOnDoubleTapListener(this);

        videoControllerView.show();
        videoView.bringToFront();
        videoControllerView.bringToFront();
        videoLength = videoView.getDuration();

        calculateSkipOffset();

        videoControllerView.pause.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_media_pause));
        videoControllerView.requestLayout();
        trackImpressionURL();

        Clog.d(TAG, "onPrepared skipOffsetValue " + skipOffsetValue);
    }

    private void hideLoader() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
            ViewUtil.removeChildFromParent(progressBar);
        }
    }

    private String getSkipOffsetFromConfiguration() {
		int videoLengthInSecs = (int) Math.round((videoLength / 1000));
		
		if (getAdSlotConfiguration().getSkipOffset() < 0 && getAdSlotConfiguration().getSkipOffset() != VastVideoUtil.DEFAULT_SKIP_OFFSET) {
            Clog.i(TAG, "Skip Offset is less than 0. Setting the default value as 0 seconds");
			getAdSlotConfiguration().setSkipOffset(0, getAdSlotConfiguration().getSkipOffsetType());
		}
		
		if ((getAdSlotConfiguration().getSkipOffsetType() == VastVideoConfiguration.SKIP_OFFSET_TYPE.RELATIVE && getAdSlotConfiguration().getSkipOffset() > 100)
				|| (getAdSlotConfiguration().getSkipOffsetType() == VastVideoConfiguration.SKIP_OFFSET_TYPE.ABSOLUTE && getAdSlotConfiguration().getSkipOffset() > videoLengthInSecs)) {
            Clog.i(TAG, "Skip Offset is greater than video length. Setting the total video length as skip offset");
			return null;
		}
		
		if (getAdSlotConfiguration().getSkipOffset() >= 0) {
			String skipOffset = String.valueOf(getAdSlotConfiguration().getSkipOffset());
			if (getAdSlotConfiguration().getSkipOffsetType() == VastVideoConfiguration.SKIP_OFFSET_TYPE.RELATIVE) {
				skipOffset = skipOffset+"%";
			}
			return skipOffset;
		}
		return null;
	}


	private void calculateSkipOffset() {
        Clog.d(TAG, "videoLength: " + videoLength);
		parsedSkipOffset = linearAdModel.getSkipOffset();
        Clog.d(TAG, "Parsed Skip Offset: " + parsedSkipOffset);
		
		if (parsedSkipOffset == null) {
			parsedSkipOffset = getSkipOffsetFromConfiguration();
            Clog.d(TAG, "Skip Offset from configuration: " + parsedSkipOffset);
		}
		
		if (!VastVideoUtil.isNullOrEmpty(parsedSkipOffset)) {
			if (parsedSkipOffset.contains("%")) {
				SKIP_OFFSET = (Float.valueOf(parsedSkipOffset.substring(0,
						parsedSkipOffset.length() - 1)) / 100);
				skipOffsetValue = (int) (SKIP_OFFSET * Math.round((videoLength / 1000)));
                Clog.d(TAG, "Relative skipOffsetValue: " + skipOffsetValue);
			} else {
				double skipOffset = Double.parseDouble(parsedSkipOffset);
				skipOffsetValue = (int) skipOffset;
				setSkipOffset(skipOffset * 1000, videoLength);
                Clog.d(TAG, "Absolute skipOffsetValue: " + skipOffsetValue);
			}

		} else {
			SKIP_OFFSET = 1.1f;
			skipOffsetValue = (int) Math.round((videoLength / 1000));
            Clog.d(TAG, "skipOffset default value for this video: " + skipOffsetValue);
		}
		
	}
	

	private void setSkipOffset(double skipDuration, double videoLength) {
		if (skipDuration < videoLength) {
			SKIP_OFFSET = (float) (skipDuration / videoLength);
		}
	}

	private void trackImpressionURL() {
			trackRequestInBackground(
                    vastAd.getImpressionArrayList()
            );
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
        Clog.i("onClick", "onClick id " + id);
		if (id == VastVideoUtil.VIDEO_SKIP) {
            skipVideo();
		}
	}

    protected void skipVideo() {
        trackRequestInBackground(VastVideoUtil.getVastEventURLList(vastAd, VastVideoUtil.EVENT_SKIP));
        if (this.videoAdListener != null) {
            this.videoAdListener.onVideoSkip(getCurrentPosition());
        }
        resetVideoView();
    }


    private void resetVideoView() {
        try {
            unregisterReceiver();
            clearVideoSurface();
			resumeMusicFromOtherApps();
			if (this.videoAdListener != null) {
				this.videoAdListener.onVideoAdFinish();
			}
            hideLoader();
			clearVastData();
			videoAdListener = null;
		} catch (Exception e) {
            Clog.e(TAG, "Exception occurred while resetting the VideoView: " + e.getMessage());
		}
	}

    private void clearVideoSurface() {
        try {
            videoView.stopPlayback();
            isVideoProgressShouldBeChecked = false;
            if (videoControllerView != null) {
                videoControllerView.hide();
            }
            videoView.setOnCompletionListener(null);
            videoView.setOnPreparedListener(null);
            videoView.setOnTouchListener(null);

            videoControllerView = null;
        }catch (Exception e){
            Clog.e(TAG, "Exception occurred while releasing video surface");
        }
    }


	private void openInExternalBrowser(String url, MotionEvent event) {
		if (!StringUtil.isEmpty(url) && context != null) {
			Intent inAppBrowserIntent = new Intent(Intent.ACTION_VIEW);
			inAppBrowserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			inAppBrowserIntent.setData(Uri.parse(url));
			context.startActivity(inAppBrowserIntent);
			isFromBrowser = true;
			if (getAdSlotConfiguration().shouldDismissVideoAdOnClick()) {
                clearVideoSurface();
			}
            if(videoAdListener != null){
                videoAdListener.onVideoClick(event);
            }
		}
	}


    private void openInAppBrowser(String clickThroughURL, MotionEvent event) {
        if (!StringUtil.isEmpty(clickThroughURL) && context != null) {
            WebView fwdWebView = new WebView(context);
            WebviewUtil.setWebViewSettings(fwdWebView);
            fwdWebView.loadUrl(clickThroughURL);

            Class<?> activity_clz = AdActivity.getActivityClass();

            Intent intent = new Intent(context, activity_clz);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(AdActivity.INTENT_KEY_ACTIVITY_TYPE, AdActivity.ACTIVITY_TYPE_BROWSER);

            BrowserAdActivity.BROWSER_QUEUE.add(fwdWebView);

            try {
                context.startActivity(intent);
                isFromBrowser = true;

                if (getAdSlotConfiguration().shouldDismissVideoAdOnClick()) {
                    clearVideoSurface();
                }
                if(videoAdListener != null){
                    videoAdListener.onVideoClick(event);
                }
            } catch (ActivityNotFoundException e) {
                Clog.w(TAG, Clog.getString(R.string.adactivity_missing, activity_clz.getName()));
                BrowserAdActivity.BROWSER_QUEUE.remove();
            }
        }
    }

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		this.mDetector.onTouchEvent(event);
		return true;
	}

	@Override
	public void onScreenDisplayOn() {
        try {
            Clog.i(TAG, "onScreenDisplayOn");
            if (videoView != null && videoControllerView != null && context != null) {
                videoControllerView.pause.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_media_play));
            }
            isScreenDisplayOff = false;
            cancelVideoDismissTask();
        }catch (Exception e){
            Clog.e(TAG,"Exception occurred while displaying the screen.");
        }
	}

	private void cancelVideoDismissTask() {
		if (videoDismissTask != null) {
			Clog.d(TAG, "cancelVideoDismissTask () : " + "Video Dismiss task cancelled");
			videoDismissTask.cancel();
			videoDismissTask = null;
		}
		
		if (videoDismissTimer != null) {
			videoDismissTimer.purge();
			videoDismissTimer.cancel();
			videoDismissTimer = null;
		}

	}

	@Override
	public void onScreenDisplayOff() {
		isScreenDisplayOff = true;
        Clog.i("", "onScreenDisplayOff");
		if (videoView != null && videoControllerView != null) {
			pause();
		}
		scheduleVideoDismissTask();
	}

	private void scheduleVideoDismissTask() {

		try {
			cancelVideoDismissTask();
			if (videoDismissTimer == null) {
				videoDismissTimer = new Timer();
			}
			TimerTask newMemoryCheckTask = new TimerTask() {
				@Override
				public void run() {
					handler.post(new Runnable() {
						public void run() {
							resetVideoView();
						}
					});
				}
			};
			videoDismissTimer.schedule(newMemoryCheckTask, INTERSTITIAL_EXPIRY_TIME);
			videoDismissTask = newMemoryCheckTask;

		} catch (Exception e) {
			Clog.e(TAG, "Exception scheduling video dismiss timer : " + e.getMessage());
		}
	}

	private void unregisterReceiver() {
		try {

			if (mReceiver != null && context != null) {
                Clog.i(TAG, "Unregistering the receiver ");
				context.unregisterReceiver(mReceiver);
				mReceiver = null;
			}
		} catch (Exception e) {
			Clog.e(TAG, "Exception occurred while unregistering the receiver: " + e.getMessage());
		}
		
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
			if (this.videoAdListener != null) {
				this.videoAdListener.onVideoClick(e);
			}
			
			Clog.i(TAG, "on Single Tap Confirmed");
			trackRequestInBackground(VastVideoUtil.getVastClickURLList(vastAd));
			
			try {
				if (linearAdModel.getVideoClicksArrayList() != null
						&& linearAdModel.getVideoClicksArrayList().size() > 0) {
                    String clickUrl = linearAdModel
                            .getVideoClicksArrayList().get(0)
                            .getClickThroughURL();
					if (videoView.isPlaying()) {
						videoView.pause();
					}
					videoPausePosition = getCurrentPosition();
					if (videoConfiguration.isOpenInExternalBrowser()) {
						openInExternalBrowser(clickUrl, e);
					} else {
						openInAppBrowser(clickUrl, e);
					}
				}
			} catch (Exception exp) {
				Clog.e(TAG, "Exception occurred while clicking the video - " + exp.getMessage());
			}

		return false;
	}

    /**
     * Sends tracking requests in the background thread.
     * @param urls
     */
    public void trackRequestInBackground(final List<String> urls) {
        if (urls == null || urls.size() == 0) {
            return;
        }
        for (final String url : urls) {
            if (url != null && url.trim().length()>0) {
                Log.v(TAG, "Tracking URL: " + url);
                new VastVideoTracker(url).execute();
            }
        }
    }

	private void updateVastInterstitialCountdownTimer() {
        if(videoView != null) {
            videoPausePosition = videoView.getCurrentPosition();
        }
		if (updateCountdownTimerListener != null) {
			videoControllerView.progressBar.setEnabled(false);
			updateCountdownTimerListener.onUpdateCountdownTimer(VastVideoUtil.convertIntToHHSS(skipOffsetValue));
		}
		skipOffsetValue = skipOffsetValue - 1;
		if (skipOffsetValue < 0) {
			videoControllerView.progressBar.setEnabled(true);
			if (updateCountdownTimerListener != null) {
				updateCountdownTimerListener.onDisplayCloseButton();
			}
		}
	}

	private void trackQuartileEvents(int currentPosition) {
		try {
			updateCounter = currentPosition;

			double progressPercentage = (double) getCurrentPosition() / videoLength;

			if (progressPercentage > FIRST_QUARTER_MARKER && !isFirstMarkHit) {
				isFirstMarkHit = true;
                trackRequestInBackground(VastVideoUtil.getVastEventURLList(vastAd, VastVideoUtil.EVENT_FIRST_QUARTILE));
                Clog.i(TAG, "Tracking First Quartile");
				if (videoAdListener != null) {
					videoAdListener.onQuartileFinish(1);
				}
			}

			if (progressPercentage > MID_POINT_MARKER && !isSecondMarkHit) {
				isSecondMarkHit = true;
                trackRequestInBackground(VastVideoUtil.getVastEventURLList(vastAd, VastVideoUtil.EVENT_MIDPOINT));
                Clog.i(TAG, "Tracking Second Quartile");
				if (videoAdListener != null) {
					videoAdListener.onQuartileFinish(2);
				}
			}

			if (progressPercentage > THIRD_QUARTER_MARKER && !isThirdMarkHit) {
				isThirdMarkHit = true;
                trackRequestInBackground(VastVideoUtil.getVastEventURLList(vastAd, VastVideoUtil.EVENT_THIRD_QUARTILE));
                Clog.i(TAG, "Tracking Third Quartile");
				if (videoAdListener != null) {
					videoAdListener.onQuartileFinish(3);
				}
			}

		} catch (Exception e) {
			Clog.e(TAG, "Exception occurred while tracking quartile events: " + e.getMessage());
		}
	}


    /**
     *  TODO: PRE-ROLL video related methods
     *
     */
    private void expandVideoToFullScreen() {
        try {
            originalLayoutParams = (LayoutParams) videoView.getLayoutParams();
            LayoutParams params = new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            videoView.setLayoutParams(params);

            refreshVideoViewLayout();

            adjustMediaContollerLayout();

            isFullscreen = true;
            trackRequestInBackground(
                    VastVideoUtil.getVastEventURLList(
                            vastAd,
                            VastVideoUtil.EVENT_FULLSCREEN));
        } catch (Exception e) {
            Clog.e(TAG, "Exception occurred while expanding videoview: " + e.getMessage());
        }


        if (this.videoAdListener != null) {
            this.videoAdListener.onVideoPlayerEnterFullScreenMode();
        }
    }

    private void refreshVideoViewLayout() {
        if (videoView == null) return;

        videoView.requestLayout();
        videoView.forceLayout();
        videoView.postInvalidate();
        videoView.refreshDrawableState();
        videoView.requestFocus();
        videoView.invalidate();
    }

    private void adjustMediaContollerLayout() {
        if (relativeLayout == null || videoControllerView == null) return;

        relativeLayout.removeView(videoControllerView);
        LayoutParams param = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        if (Configuration.ORIENTATION_LANDSCAPE == context.getResources()
                .getConfiguration().orientation) {
            param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        } else {
            param.addRule(RelativeLayout.BELOW, videoView.getId());
        }
        videoControllerView.setLayoutParams(param);
        relativeLayout.addView(videoControllerView);
        videoControllerView.setVisibility(View.VISIBLE);
        if (countdownTimerTextView.getVisibility() == View.VISIBLE) {
            videoControllerView.show();
        }

    }

    private void collapseVideoView() {

        try {
            Clog.d(TAG, "Collapsing video");

            videoView.setLayoutParams(originalLayoutParams);
            refreshVideoViewLayout();
            adjustMediaContollerLayout();
            isFullscreen = false;

            trackRequestInBackground(VastVideoUtil.getVastEventURLList(vastAd,
                    VastVideoUtil.EVENT_EXIT_FULLSCREEN));
            Clog.d(TAG, "Video Collapsed");
        } catch (Exception e) {
            Clog.e(TAG, "Exception occurred while collapsing videoview: " + e.getMessage());
        }

        if (this.videoAdListener != null) {
            this.videoAdListener.onVideoPlayerExitFullScreenMode();
        }

    }

    private void handlePrerollFinish() {
        handler.postDelayed(new Runnable() {
            public void run() {
                trackCompleteAdEvent();
                resetVideoView();
                resetVideoLayoutParams();
                Clog.d("onCompletion", "onCompletion Pre-roll ad view is released ");
            }
        }, 100);
    }


    protected void resetVideoLayoutParams() {
        try {
            Clog.d(TAG, "Resetting layout parameters of videoview :" + originalLayoutParams.width);
            if (originalLayoutParams != null) {
                videoView.setLayoutParams(originalLayoutParams);
            }

            refreshVideoViewLayout();
        } catch (Exception e) {
            Clog.e(TAG, "Exception occurred while restoring the original layout parameters of videoView: " + e.getMessage());
        }

    }

    private void updatePrerollCountdownTimer() {
        videoPausePosition = videoView.getCurrentPosition();
        videoControllerView.progressBar.setEnabled(false);
        countdownTimerTextView.setLayoutParams(getTimerPosition(videoConfiguration));
        countdownTimerTextView.bringToFront();
        countdownTimerTextView.setVisibility(View.VISIBLE);
        skipButton.setVisibility(View.INVISIBLE);
        countdownTimerTextView.setText(VastVideoUtil.convertIntToHHSS(skipOffsetValue - 1)+ "  ");
        skipOffsetValue = skipOffsetValue - 1;
        if (skipOffsetValue <= 0) {
            countdownTimerTextView.setText("");
            if (!VastVideoUtil.isNullOrEmpty(parsedSkipOffset)) {
                skipButton.setVisibility(View.VISIBLE);
                skipButton.bringToFront();
                videoControllerView.progressBar.setEnabled(true);
            }
        }
    }

    private LayoutParams getTimerPosition(VastVideoConfiguration adSlotConfig) {
        LayoutParams params = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        if (adSlotConfig.getCountdownLabelPosition() == null) {
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.addRule(RelativeLayout.ALIGN_TOP, videoView.getId());
            return params;
        }
        try {
            switch (adSlotConfig.getCountdownLabelPosition()) {
                case TOP_RIGHT:
                    //Top-right
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    params.addRule(RelativeLayout.ALIGN_TOP, videoView.getId());
                    break;

                case TOP_LEFT:
                    //Top-Left
                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    params.addRule(RelativeLayout.ALIGN_TOP, videoView.getId());
                    break;

                case TOP_CENTER:
                    //Top-center
                    params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    params.addRule(RelativeLayout.ALIGN_TOP, videoView.getId());
                    break;

                case BOTTOM_RIGHT:
                    //Bottom-right
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    params.addRule(RelativeLayout.ABOVE, videoControllerView.getId());
                    break;

                case BOTTOM_LEFT:
                    //Bottom-Left
                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    params.addRule(RelativeLayout.ABOVE, videoControllerView.getId());
                    break;

                case BOTTOM_CENTER:
                    //Bottom-center
                    params.addRule(RelativeLayout.ABOVE, videoControllerView.getId());
                    params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    break;

                default:
                    //Top-right
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    break;
            }
        } catch (Exception e) {
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        }

        return params;
    }
}
