package com.appnexus.opensdk;

import com.appnexus.opensdk.utils.Clog;

import java.util.Timer;
import java.util.TimerTask;


public class InterstitialVideoPlayer extends VastVideoPlayer {

    private static final int INTERSTITIAL_EXPIRY_TIME = 30000;
    private IUpdateCountdownTimerListener updateCountdownTimerListener;
    private String TAG = getClass().getSimpleName();
    protected Timer videoDismissTimer;
    protected TimerTask videoDismissTask;

    @Override
    protected void updateCountdownTimer(int skipOffsetValue) {

        String skipOffsetInHHSS = VastVideoUtil.convertIntToHHSS(skipOffsetValue);
        if(videoControllerBar != null){
            videoControllerBar.progressBar.setEnabled(false);
        }
        if (updateCountdownTimerListener != null) {
            updateCountdownTimerListener.onUpdateCountdownTimer(skipOffsetInHHSS);
        }
    }

    @Override
    protected void displayCloseButton() {
        if (updateCountdownTimerListener != null) {
            updateCountdownTimerListener.onDisplayCloseButton();
        }
    }

    public void setUpdateCountdownTimerListener(
            IUpdateCountdownTimerListener updateCountdownTimerListener) {
        this.updateCountdownTimerListener = updateCountdownTimerListener;
    }

    @Override
    public void onScreenDisplayOn() {
        super.onScreenDisplayOn();
        Clog.i(TAG, "onScreenDisplayOn");
        cancelVideoDismissTask();
    }

    private void cancelVideoDismissTask() {
        if (videoDismissTask != null) {
            Clog.d(TAG, "Video Dismiss task cancelled");
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
        super.onScreenDisplayOff();
        Clog.i(TAG, "onScreenDisplayOff");
        scheduleVideoDismissTask();
    }

    private void scheduleVideoDismissTask() {
        try {
            cancelVideoDismissTask();
            if (videoDismissTimer == null) {
                videoDismissTimer = new Timer();
            }
            TimerTask videoDismissTask = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            resetVideoView();
                        }
                    });
                }
            };
            videoDismissTimer.schedule(videoDismissTask, INTERSTITIAL_EXPIRY_TIME);
            this.videoDismissTask = videoDismissTask;

        } catch (Exception e) {
            Clog.e(TAG, "Exception scheduling video dismiss timer : " + e.getMessage());
        }
    }

}
