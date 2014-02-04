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

import android.os.Handler;
import android.os.Message;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.ViewUtil;

public class InterstitialAdActivity implements AdActivity.AdActivityImplementation {
    private AdActivity adActivity;
    private WebView webView;

    private FrameLayout layout;
    private long now;
    private InterstitialAdView adView;
    private static final int CLOSE_BUTTON_MESSAGE_ID = 8000;
    private ImageButton closeButton;

    public InterstitialAdActivity(AdActivity adActivity) {
        this.adActivity = adActivity;
    }

    @Override
    public void create() {
        layout = new FrameLayout(adActivity);
        adActivity.setContentView(layout);

        AdActivity.lockToCurrentOrientation(adActivity);

        now = adActivity.getIntent().getLongExtra(InterstitialAdView.INTENT_KEY_TIME,
                System.currentTimeMillis());
        setIAdView(InterstitialAdView.INTERSTITIALADVIEW_TO_USE);

        // Add a close button after a delay.
        int closeButtonDelay = adActivity.getIntent().getIntExtra(
                InterstitialAdView.INTENT_KEY_CLOSE_BUTTON_DELAY,
                Settings.getSettings().DEFAULT_INTERSTITIAL_CLOSE_BUTTON_DELAY);

        Handler closeButtonHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == CLOSE_BUTTON_MESSAGE_ID) addCloseButton();
            }
        };
        closeButtonHandler.sendEmptyMessageDelayed(CLOSE_BUTTON_MESSAGE_ID, closeButtonDelay);
    }

    @Override
    public void backPressed() {
        // do nothing
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
            adView.setAdActivity(null);
        }
    }

    @Override
    public void interacted() {
        addCloseButton();
    }

    @Override
    public WebView getWebView() {
        return webView;
    }

    private void setIAdView(InterstitialAdView av) {
        adView = av;
        if (adView == null) return;

        adView.setAdActivity(adActivity);

        layout.setBackgroundColor(adView.getBackgroundColor());
        layout.removeAllViews();
        if (adView.getParent() != null) {
            ((ViewGroup) adView.getParent()).removeAllViews();
        }
        Pair<Long, Displayable> p = adView.getAdQueue().poll();
        while (p != null && p.second != null
                && now - p.first > InterstitialAdView.MAX_AGE) {
            Clog.w(Clog.baseLogTag, Clog.getString(R.string.too_old));
            p = adView.getAdQueue().poll();
        }
        if ((p == null) || (p.second == null)
                || !(p.second.getView() instanceof WebView))
            return;
        webView = (WebView) p.second.getView();
        layout.addView(webView);
    }

    // add the close button if it hasn't been added already
    private void addCloseButton() {
        if ((layout == null) || (closeButton != null)) return;

        closeButton = ViewUtil.createCloseButton(adActivity);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adActivity != null) adActivity.finish();
            }
        });

        layout.addView(closeButton);
    }
}
