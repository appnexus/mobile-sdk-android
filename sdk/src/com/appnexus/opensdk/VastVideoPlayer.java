package com.appnexus.opensdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.VideoView;

import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.VastVideoUtil;
import com.appnexus.opensdk.utils.ViewUtil;
import com.appnexus.opensdk.vastdata.AdModel;
import com.appnexus.opensdk.vastdata.LinearAdModel;

import java.util.List;

abstract public class VastVideoPlayer implements OnCompletionListener,
		VideoControllerBarView.IMediaPlayerControl,
		OnPreparedListener, OnClickListener, OnTouchListener,
		HibernationListener, GestureDetector.OnGestureListener,
		GestureDetector.OnDoubleTapListener {
    private String TAG = getClass().getSimpleName();
    private static final float FIRST_QUARTER_MARKER = 0.25f;
    private static final float MID_POINT_MARKER = 0.50f;
    private static final float THIRD_QUARTER_MARKER = 0.75f;
    private static final long VIDEO_PROGRESS_TIMER_CHECKER_DELAY = 50;
    protected Context context;
    protected VideoView videoView;
	protected Handler handler;
    protected RelativeLayout relativeLayout;
    protected VideoControllerBarView videoControllerBar;
	private LinearAdModel linearAdModel;
	private Runnable videoProgressCheckerRunnable;
	private boolean isVideoProgressShouldBeChecked;
	private boolean isFirstMarkHit;
	private boolean isSecondMarkHit;
	private boolean isThirdMarkHit;
	private boolean isMuted;
	private boolean isPaused;
	private double videoLength;
	private MediaPlayer mediaPlayer;
    private VideoAdEventsListener videoAdListener;
    protected VastVideoConfiguration videoConfiguration;
	private BroadcastReceiver mReceiver;
    private int videoPausePosition;
	private int skipOffsetValue;
	private String parsedSkipOffset;
	private boolean isFromBrowser;
	private int updateCounter;
	private GestureDetectorCompat mDetector;
    protected int originalVideoId;
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

	protected void initiateVASTVideoPlayer(Context context, VastVideoView videoView, RelativeLayout relativeLayout, VastVideoConfiguration videoConfiguration) {
        try {
            originalVideoId = videoView.getId();
            this.context = context;
            this.videoView = videoView;
            this.vastAd = videoView.getVastAd();
            this.relativeLayout = relativeLayout;
            this.videoConfiguration = videoConfiguration;
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
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Clog.i(TAG, "SurfaceCreated ");
            Clog.i(TAG, "Has returned from browser? " + isFromBrowser);

            try {
                registerForBroadcast();
                if (isFromBrowser || videoPausePosition > 0) {
                    // Returned from background
                    resumeVideoAd();
                }
            }catch (Exception e){
                Clog.e(TAG, "Exception occurred while creating video surface: "+e.getMessage());
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format,
        int width, int height) {
        }
    };

    protected void setVideoPausePosition(int videoPausePosition) {
        this.videoPausePosition = videoPausePosition;
    }

	private void registerForBroadcast() {
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

	@Override
	public void onCompletion(final MediaPlayer mp) {
		handleAdFinishScenario();
	}

	private void handleAdFinishScenario() {
        hideLoader();
		resumeMusicFromOtherApps();
        finishRenderingVideoAd();
        unregisterReceiver();
	}

	private void finishRenderingVideoAd() {
		Clog.d("onCompletion", "onCompletion VAST Interstitial released");
		handler.post(new Runnable() {
            public void run() {
            isMuted = false;
            trackRequestInBackground(VastVideoUtil.getVastEventURLList(vastAd, VastVideoUtil.EVENT_COMPLETE));
            stopProgressChecker();
            if (videoAdListener != null) {
                videoAdListener.onVideoAdFinish();
            }
            }
        });
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
                    this.videoAdListener.onVideoStart();
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
	public int getBufferPercentage() {return 0;}

	@Override
	public boolean canPause() {return true;}

	@Override
	public boolean isFullScreen() {return false;}

	@Override
	public void toggleFullScreen() {}

	@Override
	public void mute() {
        muteVideo();
    }

    @Override
	public boolean isMute() {
		return isMuted;
	}

	@Override
	public void unMute() {
        unmuteVideo();
    }

    @Override
	public void rewind() {
	}

	protected void initializeVideoPlayer() {
        Clog.i(TAG, "initializeVideoPlayer");
		try {
			videoView.setId(originalVideoId);
			playVastVideoAd();
			isMuted = false;
			isVideoProgressShouldBeChecked = true;

			videoControllerBar = new VideoControllerBarView(context);
            int videoControllerId = VastVideoUtil.VIDEO_CONTROLLER;
            videoControllerBar.setId(videoControllerId);

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
                    handleVideoProgress(currentPosition);
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

    private void handleVideoProgress(int currentPosition) {
        if (currentPosition > updateCounter) {
            if(videoView != null) {
                setVideoPausePosition(videoView.getCurrentPosition());
            }
            updateCountdownTimer(skipOffsetValue);
            skipOffsetValue = skipOffsetValue - 1;
            if (skipOffsetValue < 0) {
                videoControllerBar.progressBar.setEnabled(true);
                displayCloseButton();
            }
        }
    }

    abstract protected void updateCountdownTimer(int skipOffsetValue);

    abstract protected void displayCloseButton();

    private void muteVideo() {
        if (mediaPlayer != null) {
            Clog.d(TAG, "Muting video");
            isMuted = true;
            mediaPlayer.setVolume(0, 0);
            trackRequestInBackground(VastVideoUtil.getVastEventURLList(vastAd, VastVideoUtil.EVENT_MUTE));

            if (this.videoAdListener != null) {
                this.videoAdListener.onMuteVideo();
            }
            Clog.d(TAG, "Video Muted");
        }
    }

    private void unmuteVideo() {
        if (mediaPlayer != null) {
            Clog.d(TAG, "Unmuting video");
            isMuted = false;
            mediaPlayer.setVolume(1, 1);
            trackRequestInBackground(VastVideoUtil.getVastEventURLList(vastAd, VastVideoUtil.EVENT_UNMUTE));
            if (this.videoAdListener != null) {
                this.videoAdListener.onUnMuteVideo();
            }
            Clog.d(TAG, "Video Unmuted");
        }
    }

    @Override
	public void onPrepared(MediaPlayer mp) {
		try {
			this.mediaPlayer = mp;
			if (videoControllerBar != null) {
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
        videoControllerBar.setMediaPlayer(this);
        videoControllerBar.show();
        if (videoControllerBar.pause != null) {
            videoControllerBar.pause.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_media_pause));
            videoControllerBar.requestLayout();
        }
    }

    private void handleVideoStart() throws Exception{
        Clog.i(TAG, "About to display first frame of video");
        videoControllerBar.setMediaPlayer(this);
        videoControllerBar.setAnchorView(relativeLayout, this);

        videoView.setOnTouchListener(VastVideoPlayer.this);
        mDetector = new GestureDetectorCompat(context, this);
        mDetector.setOnDoubleTapListener(this);

        videoControllerBar.show();
        videoView.bringToFront();
        videoControllerBar.bringToFront();
        videoLength = videoView.getDuration();

        Clog.d(TAG, "videoLength: " + videoLength);
        parsedSkipOffset = linearAdModel.getSkipOffset();
        skipOffsetValue = VastVideoUtil.calculateSkipOffset(parsedSkipOffset, videoConfiguration, videoLength);

        videoControllerBar.pause.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_media_pause));
        videoControllerBar.requestLayout();
        trackRequestInBackground(vastAd.getImpressionArrayList());

        Clog.d(TAG, "onPrepared skipOffsetValue " + skipOffsetValue);
    }

    private void hideLoader() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
            ViewUtil.removeChildFromParent(progressBar);
        }
    }

	@Override
	public void onClick(View v) {
		int id = v.getId();
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

    protected void resetVideoView() {
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
            if (videoControllerBar != null) {
                videoControllerBar.hide();
            }
            videoView.setOnCompletionListener(null);
            videoView.setOnPreparedListener(null);
            videoView.setOnTouchListener(null);

            videoControllerBar = null;
        }catch (Exception e){
            Clog.e(TAG, "Exception occurred while releasing video surface");
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

            if (videoView != null && videoControllerBar != null && context != null) {
                videoControllerBar.pause.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_media_play));
            }
        }catch (Exception e){
            Clog.e(TAG,"Exception occurred while displaying the screen.");
        }

    }

	@Override
	public void onScreenDisplayOff() {
        if (videoView != null && videoControllerBar != null) {
            pause();
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
	public boolean onDown(MotionEvent e) {return false;}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {return false;}

	@Override
	public void onLongPress(MotionEvent e) {}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {return false;}

	@Override
	public void onShowPress(MotionEvent e) {}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {return false;}

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
            if (linearAdModel.getVideoClicksArrayList() != null && linearAdModel.getVideoClicksArrayList().size() > 0) {
                String clickUrl = linearAdModel.getVideoClicksArrayList().get(0).getClickThroughURL();
                if (videoView.isPlaying()) {
                    videoView.pause();
                }
                setVideoPausePosition(getCurrentPosition());

                if (AdUtil.openBrowser(context, clickUrl, videoConfiguration.openInNativeBrowser())) {
                    isFromBrowser = true;
                }
            }
        } catch (Exception exp) {
            Clog.e(TAG, "Exception occurred while clicking the video - " + exp.getMessage());
        }
		return false;
	}

    public void trackRequestInBackground(final List<String> urls) {
        if (urls == null || urls.size() == 0) {
            return;
        }
        for (final String url : urls) {
            if (url != null && url.trim().length()>0) {
                Clog.i(TAG, "Tracking URL: " + url);
                SharedNetworkManager.getInstance(context).addURL(url, context);
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
}
