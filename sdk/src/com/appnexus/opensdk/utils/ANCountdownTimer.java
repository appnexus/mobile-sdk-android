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
package com.appnexus.opensdk.utils;

import android.os.CountDownTimer;


public abstract class ANCountdownTimer {
    private long pauseTimeMillis = 0;
    private long countdownInterval = 0;
    private CountDownTimer countdownTimer;

    public ANCountdownTimer(long millisInFuture, long countdownInterval) {
        initiateCountdownTimer(millisInFuture, countdownInterval);
    }

    private void initiateCountdownTimer(final long millisInFuture, final long countdownInterval) {
        ANCountdownTimer.this.countdownInterval = countdownInterval;
        ANCountdownTimer.this.countdownTimer = new CountDownTimer(millisInFuture, countdownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                pauseTimeMillis = millisUntilFinished;
                ANCountdownTimer.this.onTick(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                ANCountdownTimer.this.onFinish();
            }
        };

    }

    public void startTimer(){
        if(countdownTimer != null) {
            countdownTimer.start();
        }
    }

    public void pauseTimer(){
        if (countdownTimer != null) {
            countdownTimer.cancel();
        }
    }

    public void cancelTimer(){
        if (countdownTimer != null) {
            countdownTimer.cancel();
        }
        pauseTimeMillis = 0;
        countdownInterval = 0;
        countdownTimer = null;
    }

    public void resumeTimer(){
        initiateCountdownTimer(pauseTimeMillis, countdownInterval);
        startTimer();
    }

    public abstract void onTick(long millisUntilFinished);

    public abstract void onFinish();
}