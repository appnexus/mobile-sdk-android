/*
 *    Copyright 2015 APPNEXUS INC
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.VideoView;

import com.appnexus.opensdk.utils.ANCountdownTimer;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.ViewUtil;
import com.appnexus.opensdk.vastdata.AdModel;
import com.appnexus.opensdk.vastdata.LinearAdModel;

abstract class VastVideoPlayer implements OnCompletionListener,
        OnPreparedListener, OnClickListener, OnTouchListener,
        HibernationListener, GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {

    public static final int COUNTDOWN_INTERVAL = 10;
    protected Context context;
    protected VideoView videoView;
    protected Handler handler;
    protected RelativeLayout relativeLayout;
    private LinearAdModel linearAdModel;
    private boolean isMuted;
    private boolean isPaused;
    private double videoLength = 0;
    private MediaPlayer mediaPlayer;
    private VideoAdEventsListener videoAdListener;
    protected VastVideoConfiguration videoConfiguration;
    private BroadcastReceiver mReceiver;
    private int videoPausePosition;
    private long skipOffsetMillis;
    private boolean isSkipCountdownFinished;
    private long remainingMillis;
    private boolean isFromBrowser;
    private GestureDetectorCompat mDetector;
    protected int originalVideoId;
    private AdModel vastAd;
    private ProgressBar progressBar;
    private VideoEventHandler videoEventHandler;
    private ANCountdownTimer countDownTimer;
    private ImageView muteButton;

    public VastVideoPlayer(Context context, VastVideoView videoView, RelativeLayout relativeLayout, VastVideoConfiguration videoConfiguration) {
        handler = new Handler();
        originalVideoId = videoView.getId();
        this.context = context;
        this.videoView = videoView;
        this.vastAd = videoView.getVastAd();
        this.relativeLayout = relativeLayout;
        this.videoConfiguration = videoConfiguration;
    }

    public void setVideoAdListener(VideoAdEventsListener videoAdListener) {
        this.videoAdListener = videoAdListener;
    }

    private void clearVastData() {
        vastAd = null;
        linearAdModel = null;
    }

    protected void initiateVASTVideoPlayer() {
        try {
            if (vastAd != null && vastAd.containsLinearAd()) {
                showLoader();
                isFromBrowser = false;
                this.videoView.setOnCompletionListener(this);
                this.videoView.getHolder().addCallback(surfaceHolderCB);
                this.videoView.setOnErrorListener(mediaErrorCB);
                this.videoView.setOnPreparedListener(this);

                linearAdModel = vastAd.getCreativesArrayList().get(0).getLinearAdModel();
                videoEventHandler = new VideoEventHandler();
                videoView.setId(originalVideoId);
                addMuteButton();
                playVastVideoAd();

            } else {
                throw new Exception("Vast data not available");
            }
        } catch (Exception e) {
            resetVideoView();
            Clog.e(Clog.vastLogTag, "Exception initializing the video player: " + e.getMessage());
        }
    }

    MediaPlayer.OnErrorListener mediaErrorCB = new MediaPlayer.OnErrorListener() {

        @Override
        public boolean onError(MediaPlayer arg0, int what, int extra) {
            Clog.e(Clog.vastLogTag, "onError what:" + what);
            Clog.e(Clog.vastLogTag, "onError extra:" + extra);
            handleAdFinishScenario();
            return true;
        }
    };

    SurfaceHolder.Callback surfaceHolderCB = new SurfaceHolder.Callback() {
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Clog.d(Clog.vastLogTag, "SurfaceDestroyed ");
            resumeMusicFromOtherApps();
            unregisterReceiver();
            if (countDownTimer != null) {
                countDownTimer.pauseTimer();
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Clog.i(Clog.vastLogTag, "Has returned from browser? " + isFromBrowser);
            try {
                registerForBroadcast();
                if (isFromBrowser || videoPausePosition > 0) {
                    // Returned from background
                    resumeVideoAd();
                }
            } catch (Exception e) {
                Clog.e(Clog.vastLogTag, "Exception occurred while creating video surface: " + e.getMessage());
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
            Clog.e(Clog.vastLogTag, "Exception occurred while registering for hibernation broadcast: " + e.getMessage());
        }
    }

    private void playVastVideoAd() throws Exception {
        Clog.i(Clog.vastLogTag, "Attempting to play VAST video ad");
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
                stopProgressChecker();

                if (videoEventHandler != null) {
                    videoEventHandler.handleVideoCompleteEvent(videoAdListener, vastAd);
                }
            }
        });
    }

    private void stopProgressChecker() {
        if (countDownTimer != null) {
            countDownTimer.cancelTimer();
        }
    }

    private void resumeVideoAd() {
        Clog.i(Clog.vastLogTag, "Resuming video ad at: " + videoPausePosition);
        if (videoView != null) {
            videoView.seekTo(videoPausePosition);
            videoView.requestLayout();
            videoView.start();
        }
    }

    public void start() {
        if (videoView != null) {
            pauseMusicFromOtherApps();
            videoView.start();
            if (videoEventHandler != null) {
                if (isPaused) {
                    videoEventHandler.handleVideoResumeEvent(videoAdListener, vastAd, getCurrentPosition());
                } else {
                    videoEventHandler.handleVideoStartEvent(videoAdListener, vastAd);
                }
            }
            isPaused = false;
        }
    }

    OnAudioFocusChangeListener audioFocusListener = new OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            Clog.i(Clog.vastLogTag, "Focus change: " + focusChange);
        }
    };

    private void pauseMusicFromOtherApps() {
        try {
            if ((Build.VERSION.SDK_INT >= 11) && ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).isMusicActive()) {
                ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE))
                        .requestAudioFocus(audioFocusListener, AudioManager.STREAM_MUSIC,
                                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            }
        } catch (Exception e) {
            Clog.e(Clog.vastLogTag, "Exception occurred while pausing the music from other apps");
        }
    }

    private void resumeMusicFromOtherApps() {
        try {
            if ((Build.VERSION.SDK_INT >= 11) && ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).isMusicActive()) {
                ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE))
                        .abandonAudioFocus(audioFocusListener);
            }
        } catch (Exception e) {
            Clog.e(Clog.vastLogTag, "Exception occurred while resuming the music from other apps");
        }
    }

    public void pause() {
        if (videoView != null) {
            videoView.pause();
            resumeMusicFromOtherApps();
            isPaused = true;
            if (videoEventHandler != null) {
                videoEventHandler.handleVideoPauseEvent(videoAdListener, vastAd, getCurrentPosition());
            }
        }
    }

    public int getCurrentPosition() {
        return videoView.getCurrentPosition();
    }

    private void addMuteButton() {
        isMuted = false;
        muteButton = ViewUtil.createMuteButtonInRelativeLayout(context);
        muteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMuted) {
                    unmuteVideo();
                    muteButton.setImageResource(R.drawable.unmute);
                } else {
                    muteVideo();
                    muteButton.setImageResource(R.drawable.mute);
                }
            }
        });
        muteButton.setVisibility(View.GONE);
        relativeLayout.addView(muteButton);
    }

    private void startVideoCountDown() {

        startCountdownTimer((int) skipOffsetMillis);
        countDownTimer = new ANCountdownTimer((long) videoLength, COUNTDOWN_INTERVAL) {
            @Override
            public void onTick(long leftTimeInMilliseconds) {
                handleVideoProgress(leftTimeInMilliseconds);
                if (videoEventHandler != null) {
                    videoEventHandler.trackQuartileEvents(getCurrentPosition(), videoAdListener, vastAd, videoLength);
                }
            }

            @Override
            public void onFinish() {
                Clog.i(Clog.vastLogTag, "onFinish : ");
            }
        };
        countDownTimer.startTimer();
    }

    private void handleVideoProgress( long leftTimeInMilliseconds) {
        if (videoView != null) {
            setVideoPausePosition(videoView.getCurrentPosition());
        }
        skipOffsetMillis = (long) (leftTimeInMilliseconds - remainingMillis);
        if (!isSkipCountdownFinished) {
            if (skipOffsetMillis < 0) {
                isSkipCountdownFinished = true;
                displayCloseButton();
            } else {
                updateCountdownTimer((int) skipOffsetMillis);
            }
        }
    }


    abstract protected void startCountdownTimer(int skipOffsetValue);

    abstract protected void updateCountdownTimer(int skipOffsetValue);

    abstract protected void displayCloseButton();

    private void muteVideo() {
        if (mediaPlayer != null) {
            Clog.d(Clog.vastLogTag, "Muting video");
            isMuted = true;
            mediaPlayer.setVolume(0, 0);
            if (videoEventHandler != null) {
                videoEventHandler.handleVideoMuteEvent(videoAdListener, vastAd);
            }
            Clog.d(Clog.vastLogTag, "Video Muted");
        }
    }


    private void unmuteVideo() {
        if (mediaPlayer != null) {
            Clog.d(Clog.vastLogTag, "Unmuting video");
            isMuted = false;
            mediaPlayer.setVolume(1, 1);
            if (videoEventHandler != null) {
                videoEventHandler.handleVideoUnmuteEvent(videoAdListener, vastAd);
            }
            Clog.d(Clog.vastLogTag, "Video Unmuted");
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        try {
            this.mediaPlayer = mp;
            if (isFromBrowser || videoPausePosition > 0) {
                handleVideoResume();
            } else {
                handleVideoStart();
            }
            hideLoader();
            if (muteButton != null) {
                muteButton.setVisibility(View.VISIBLE);
                muteButton.bringToFront();
            }
        } catch (Exception e) {
            String errorMessage = "Exception occurred while preparing video player - " + e.getMessage();
            Clog.e(Clog.vastLogTag, errorMessage);
            resetVideoView();
        }
    }

    private void handleVideoResume() throws Exception {
        Clog.i(Clog.vastLogTag, "About to resume video");
        if (isMuted && mediaPlayer != null) {
            mediaPlayer.setVolume(0, 0);
        }

        if (countDownTimer != null) {
            countDownTimer.resumeTimer();
        }
    }

    private void handleVideoStart() throws Exception {
        Clog.i(Clog.vastLogTag, "About to display first frame of video");
        videoView.setOnTouchListener(VastVideoPlayer.this);
        mDetector = new GestureDetectorCompat(context, this);
        mDetector.setOnDoubleTapListener(this);

        videoView.bringToFront();
        videoLength = videoView.getDuration();

        Clog.d(Clog.vastLogTag, "videoLength: " + videoLength);
        String parsedSkipOffset = linearAdModel.getSkipOffset();
        skipOffsetMillis = VastVideoUtil.calculateSkipOffset(parsedSkipOffset, videoConfiguration, videoLength);
        remainingMillis = (long) (videoLength - skipOffsetMillis);
        if (videoEventHandler != null) {
            videoEventHandler.trackRequestInBackground(vastAd.getImpressionArrayList());
        }

        startVideoCountDown();
        Clog.i(Clog.vastLogTag, "onPrepared skipOffsetValue " + skipOffsetMillis);
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
        if (videoEventHandler != null) {
            videoEventHandler.handleSkipEvent(videoAdListener, vastAd, getCurrentPosition());
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
            Clog.e(Clog.vastLogTag, "Exception occurred while resetting the VideoView: " + e.getMessage());
        }
    }

    private void clearVideoSurface() {
        try {
            videoView.stopPlayback();
            videoView.setOnCompletionListener(null);
            videoView.setOnPreparedListener(null);
            videoView.setOnTouchListener(null);
            if (countDownTimer != null) {
                countDownTimer.cancelTimer();
            }
        } catch (Exception e) {
            Clog.e(Clog.vastLogTag, "Exception occurred while releasing video surface");
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
            if (countDownTimer != null) {
                countDownTimer.resumeTimer();
            }
            if (videoView != null) {
                videoView.start();
            }
        } catch (Exception e) {
            Clog.e(Clog.vastLogTag, "Exception occurred while displaying the screen.");
        }
    }

    @Override
    public void onScreenDisplayOff() {
        if (countDownTimer != null) {
            countDownTimer.pauseTimer();
        }
        if (videoView != null) {
            pause();
        }
    }

    private void unregisterReceiver() {
        try {
            if (mReceiver != null && context != null) {
                Clog.i(Clog.vastLogTag, "Unregistering the receiver ");
                context.unregisterReceiver(mReceiver);
                mReceiver = null;
            }
        } catch (Exception e) {
            Clog.e(Clog.vastLogTag, "Exception occurred while unregistering the receiver: " + e.getMessage());
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
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

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
        if (videoEventHandler != null) {
            videoEventHandler.handleVideoClickEvent(videoAdListener, e, vastAd);
        }
        try {
            if (linearAdModel.getVideoClicksArrayList() != null && linearAdModel.getVideoClicksArrayList().size() > 0) {
                String clickUrl = linearAdModel.getVideoClicksArrayList().get(0).getClickThroughURL();
                if (videoView.isPlaying()) {
                    videoView.pause();
                }
                if (countDownTimer != null) {
                    countDownTimer.pauseTimer();
                }
                setVideoPausePosition(getCurrentPosition());
                if (AdUtil.openBrowser(context, clickUrl, videoConfiguration.openInNativeBrowser())) {
                    isFromBrowser = true;
                }
            }
        } catch (Exception exp) {
            Clog.e(Clog.vastLogTag, "Exception occurred while clicking the video - " + exp.getMessage());
        }
        return false;
    }
}
