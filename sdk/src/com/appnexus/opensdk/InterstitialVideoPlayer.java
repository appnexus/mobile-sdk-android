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

import android.content.Context;
import android.widget.RelativeLayout;

import com.appnexus.opensdk.utils.Clog;

import java.util.Timer;
import java.util.TimerTask;


public class InterstitialVideoPlayer extends VastVideoPlayer {

    private static final int INTERSTITIAL_EXPIRY_TIME = 30000;
    private UpdateCountdownTimerListener updateCountdownTimerListener;
    private String TAG = getClass().getSimpleName();
    protected Timer videoDismissTimer;
    protected TimerTask videoDismissTask;

    public InterstitialVideoPlayer(Context context, VastVideoView videoView, RelativeLayout relativeLayout, VastVideoConfiguration videoConfiguration) {
        super(context, videoView, relativeLayout, videoConfiguration);
    }

    @Override
    protected void startCountdownTimer(int skipOffsetValue) {
        if (updateCountdownTimerListener != null) {
            updateCountdownTimerListener.onStartCountdownTimer(skipOffsetValue+"");
        }
    }

    @Override
    protected void updateCountdownTimer(int skipOffsetValue) {
        if (updateCountdownTimerListener != null) {
            updateCountdownTimerListener.onUpdateCountdownTimer(skipOffsetValue+"");
        }
    }

    @Override
    protected void displayCloseButton() {
        if (updateCountdownTimerListener != null) {
            updateCountdownTimerListener.onDisplayCloseButton();
        }
    }

    public void setUpdateCountdownTimerListener(
            UpdateCountdownTimerListener updateCountdownTimerListener) {
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
