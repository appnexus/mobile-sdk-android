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


public abstract class ANCountDownTimer {
    private long pauseTimeMillis = 0;
    private long countDownInterval = 0;
    private CountDownTimer countDownTimer;

    public ANCountDownTimer(long millisInFuture, long countDownInterval) {
        initiateCountDownTimer(millisInFuture, countDownInterval);
    }

    private void initiateCountDownTimer(final long millisInFuture, final long countDownInterval) {
        ANCountDownTimer.this.countDownInterval = countDownInterval;
        ANCountDownTimer.this.countDownTimer = new CountDownTimer(millisInFuture, countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                pauseTimeMillis = millisUntilFinished;
                ANCountDownTimer.this.onTick(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                ANCountDownTimer.this.onFinish();
            }
        };

    }

    public void startTimer(){
        if(countDownTimer != null) {
            countDownTimer.start();
        }
    }

    public void pauseTimer(){
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public void cancelTimer(){
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        pauseTimeMillis = 0;
        countDownInterval = 0;
        countDownTimer = null;
    }

    public void resumeTimer(){
        initiateCountDownTimer(pauseTimeMillis, countDownInterval);
        startTimer();
    }

    public abstract void onTick(long millisUntilFinished);

    public abstract void onFinish();
}
