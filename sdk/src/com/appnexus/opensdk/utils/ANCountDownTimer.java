package com.appnexus.opensdk.utils;

import android.os.CountDownTimer;


/**
 * Created by Ramit on 22/09/15.
 */
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
