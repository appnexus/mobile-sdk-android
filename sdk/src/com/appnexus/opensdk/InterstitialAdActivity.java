/*
 *    Copyright 2013 APPNEXUS INC
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

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.appnexus.opensdk.utils.ANCountdownTimer;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.ViewUtil;

class InterstitialAdActivity implements AdActivity.AdActivityImplementation {
    public static final int COUNTDOWN_INTERVAL = 1;
    private Activity adActivity;
    private AdWebView webView;

    private FrameLayout layout;
    private long now;
    private InterstitialAdView adView;
    private ANCountdownTimer countdownTimer;
    private CircularProgressBar countdownWidget;

    public InterstitialAdActivity(Activity adActivity) {
        this.adActivity = adActivity;
    }

    @Override
    public void create() {
        layout = new FrameLayout(adActivity);
        adActivity.setContentView(layout);

        // set 'now' variable to filter expired ads
        now = adActivity.getIntent().getLongExtra(InterstitialAdView.INTENT_KEY_TIME,
                System.currentTimeMillis());
        setIAdView(InterstitialAdView.INTERSTITIALADVIEW_TO_USE);
        displayCountdownWidget();
    }

    private void displayCountdownWidget() {
        int closeButtonDelay = adActivity.getIntent().getIntExtra(
                InterstitialAdView.INTENT_KEY_CLOSE_BUTTON_DELAY,
                Settings.DEFAULT_INTERSTITIAL_CLOSE_BUTTON_DELAY);

        countdownWidget = ViewUtil.addCountdownWidget(adActivity, layout);
        countdownWidget.setMax(closeButtonDelay);
        countdownWidget.setProgress(closeButtonDelay);
        countdownWidget.setVisibility(View.VISIBLE);
        countdownWidget.bringToFront();

        startCountdownTimer(closeButtonDelay);
    }

    private void startCountdownTimer(final long closeButtonDelay) {
        countdownTimer = new ANCountdownTimer(closeButtonDelay, COUNTDOWN_INTERVAL) {
            @Override
            public void onTick(long leftTimeInMilliseconds) {
                if(countdownWidget != null) {
                    countdownWidget.setProgress((int) leftTimeInMilliseconds);
                    int seconds = (int) (leftTimeInMilliseconds / 1000) + 1;
                    countdownWidget.setTitle(String.valueOf(seconds));
                }
            }
            @Override
            public void onFinish() {
                showCloseButton();
            }
        };
        countdownTimer.startTimer();
    }

    private void showCloseButton() {
        if(countdownWidget != null) {
            countdownWidget.setTitle("X");
            countdownWidget.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (adActivity != null) {
                        if (adView != null && adView.getAdDispatcher() != null) {
                            adView.getAdDispatcher().onAdCollapsed();
                        }
                        adActivity.finish();
                    }
                }
            });
        }
    }

    @Override
    public boolean shouldHandleBackPress() {
        // do nothing
        return false;
    }

    @Override
    public void destroy() {
        // clean up webView
        if (webView != null) {
            ViewUtil.removeChildFromParent(webView);
            webView.destroy();
        }

        // clean up adView
        if (adView != null) {
            adView.setAdImplementation(null);
        }

        // clean up circular progressbar
        if(countdownWidget != null) {
            countdownWidget.setOnClickListener(null);
            ViewUtil.removeChildFromParent(countdownWidget);
            countdownWidget = null;
        }

        // cancel the countdown timer
        if(countdownTimer != null){
            countdownTimer.cancelTimer();
            countdownTimer = null;
        }
    }

    @Override
    public void interacted() {
        if (countdownTimer != null && countdownWidget != null) {
            countdownTimer.cancelTimer();
            countdownWidget.setProgress(0);
            showCloseButton();
        }
    }

    @Override
    public WebView getWebView() {
        return webView;
    }

    private void setIAdView(InterstitialAdView av) {
        adView = av;
        if (adView == null) return;

        adView.setAdImplementation(this);

        layout.setBackgroundColor(adView.getBackgroundColor());
        layout.removeAllViews();
        if (adView.getParent() != null) {
            ((ViewGroup) adView.getParent()).removeAllViews();
        }
        InterstitialAdQueueEntry iAQE = adView.getAdQueue().poll();

        // To be safe, ads from the future will be considered to have expired
        // if now-p.first is less than 0, the ad will be considered to be from the future
        while (iAQE != null
                && (now - iAQE.getTime() > InterstitialAdView.MAX_AGE || now - iAQE.getTime() < 0)) {
            Clog.w(Clog.baseLogTag, Clog.getString(R.string.too_old));
            iAQE = adView.getAdQueue().poll();
        }
        if ((iAQE == null)
                || !(iAQE.getView() instanceof AdWebView))
            return;
        webView = (AdWebView) iAQE.getView();

        // lock orientation to ad request orientation
        AdActivity.lockToConfigOrientation(adActivity, webView.getOrientation());

        layout.addView(webView);
    }
}
