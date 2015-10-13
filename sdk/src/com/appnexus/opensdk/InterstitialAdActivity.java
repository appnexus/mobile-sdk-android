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
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.ViewUtil;

import java.lang.ref.WeakReference;

class InterstitialAdActivity implements AdActivity.AdActivityImplementation {
    private Activity adActivity;
    private AdWebView webView;

    private FrameLayout layout;
    private long now;
    private InterstitialAdView adView;
    private static final int CLOSE_BUTTON_MESSAGE_ID = 8000;
    private ImageButton closeButton;


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

        // Add a close button after a delay.
        int closeButtonDelay = adActivity.getIntent().getIntExtra(
                InterstitialAdView.INTENT_KEY_CLOSE_BUTTON_DELAY,
                Settings.DEFAULT_INTERSTITIAL_CLOSE_BUTTON_DELAY);

        new CloseButtonHandler(this).sendEmptyMessageDelayed(CLOSE_BUTTON_MESSAGE_ID, closeButtonDelay);
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

        // cleanup adView
        if (adView != null) {
            adView.setAdImplementation(null);
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
                && (now - iAQE.getTime() > InterstitialAdView.MAX_AGE || now-iAQE.getTime()<0)) {
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

    // add the close button if it hasn't been added already
    private void addCloseButton() {
        if ((layout == null) || (closeButton != null)) return;

        closeButton = ViewUtil.createCloseButton(adActivity, false);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adActivity != null) adActivity.finish();
            }
        });

        layout.addView(closeButton);
    }

    static class CloseButtonHandler extends Handler{
        WeakReference<InterstitialAdActivity> weakReferenceIAA;
        public CloseButtonHandler(InterstitialAdActivity a){
            weakReferenceIAA=new WeakReference<InterstitialAdActivity>(a);
        }

        @Override
        public void handleMessage(Message msg) {
            InterstitialAdActivity iAA = weakReferenceIAA.get();
            if (msg.what == CLOSE_BUTTON_MESSAGE_ID && iAA!=null) iAA.addCloseButton();
        }
    }
}
