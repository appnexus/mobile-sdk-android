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
import android.content.MutableContextWrapper;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.appnexus.opensdk.utils.ANCountdownTimer;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.ViewUtil;

class InterstitialAdActivity implements AdActivity.AdActivityImplementation {
    private Activity adActivity;
    private AdWebView webView;

    private FrameLayout layout;
    private long now;
    private InterstitialAdView adView;
    final int COUNTDOWN_INTERVAL = 1;
    private CircularProgressBar countdownWidget;
    private ANCountdownTimer countdownTimer;
    private Handler autoDismissHandler;

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

        int dismissAdDelay = adActivity.getIntent().getIntExtra(
                InterstitialAdView.INTENT_KEY_AUTODISMISS_DELAY,
                -1);
        int dismissAdInterval = dismissAdDelay * 1000;


        int closeButtonDelay = adActivity.getIntent().getIntExtra(
                InterstitialAdView.INTENT_KEY_CLOSE_BUTTON_DELAY,
                Settings.DEFAULT_INTERSTITIAL_CLOSE_BUTTON_DELAY);

        displayCountdownWidget(dismissAdInterval, closeButtonDelay);


        if (adView != null && dismissAdDelay > -1) {
            autoDismissHandler = new Handler();
            autoDismissHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismissInterstitial();
                }
            }, dismissAdInterval);
        }
    }

    private void displayCountdownWidget(int dismissAdInterval, int closeButtonDelay) {

        // If the ad will auto-dismiss before the closeButtonDelay is hit, then show the countdown timer based on dismissAdInterval
        if ((dismissAdInterval > 0) && dismissAdInterval <= closeButtonDelay)
            closeButtonDelay = dismissAdInterval;
        Clog.e("displayCountdownWidget", closeButtonDelay + "");

        countdownWidget = ViewUtil.createCircularProgressBar(adActivity);
        layout.addView(countdownWidget);
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
                if (countdownWidget != null) {
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
        if (countdownWidget != null) {
            if (webView == null || !webView.isMRAIDUseCustomClose()) {
                countdownWidget.setProgress(0);
                countdownWidget.setTitle("X");
            } else {
                countdownWidget.setTransparent();
            }
            countdownWidget.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissInterstitial();
                }
            });
        }
    }

    @Override
    public void backPressed() {
        // By default the AdActivity will finish onBackKeyPress. So just trigger onAdCollapsed.
        if (adView != null && adView.getAdDispatcher() != null) {
            adView.getAdDispatcher().onAdCollapsed();
        }
    }

    @Override
    public void destroy() {
        // clean up webView
        if (webView != null) {
            ViewUtil.removeChildFromParent(webView);
            webView.destroy();
        }

        // cleanup adView
        if (adView != null) {
            adView.setAdImplementation(null);
        }

        dismissInterstitial();
    }

    @Override
    public void interacted() {
        if (autoDismissHandler != null) {
            autoDismissHandler.removeCallbacksAndMessages(null);
        }
    }


    @Override
    public void browserLaunched() {
        if (adView != null && adView.shouldDismissOnClick()) {
            dismissInterstitial();
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

        // Update the context
        if (webView.getContext() instanceof MutableContextWrapper) {
            ((MutableContextWrapper) webView.getContext()).setBaseContext(adActivity);
        }
        // lock orientation to ad request orientation
        //@TODO need to change this check condition to reflect MRAID spec
        if (!(webView.getCreativeWidth() == 1 && webView.getCreativeHeight() == 1)) {
            AdActivity.lockToConfigOrientation(adActivity, webView.getOrientation());
        }

        layout.addView(webView);
    }

    private void dismissInterstitial() {
        if (adActivity != null) {
            if (adView != null && adView.getAdDispatcher() != null) {
                adView.getAdDispatcher().onAdCollapsed();
            }
            if (autoDismissHandler != null) {
                autoDismissHandler.removeCallbacksAndMessages(null);
            }
            if (countdownTimer != null) {
                countdownTimer.cancelTimer();
            }
            adActivity.finish();
        }
    }
}