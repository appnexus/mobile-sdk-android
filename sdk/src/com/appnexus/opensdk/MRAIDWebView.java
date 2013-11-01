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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;

@SuppressLint("ViewConstructor")
public class MRAIDWebView extends WebView implements Displayable {
    private MRAIDImplementation implementation;
    final AdView owner;
    private int default_width;
    private int default_height;
    protected boolean isFullScreen = false;

    public MRAIDWebView(AdView owner) {
        super(owner.getContext());
        this.owner = owner;
        setup();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setup() {
        this.getSettings().setJavaScriptEnabled(true);
        this.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        // this.setInitialScale(100);
        this.getSettings().setPluginState(WebSettings.PluginState.ON);
        this.getSettings().setBuiltInZoomControls(false);
        this.getSettings().setLightTouchEnabled(false);
        this.getSettings().setLoadsImagesAutomatically(true);
        this.getSettings().setSupportZoom(false);
        this.getSettings().setUseWideViewPort(false);
        this.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        setHorizontalScrollbarOverlay(false);
        setHorizontalScrollBarEnabled(false);
        setVerticalScrollbarOverlay(false);
        setVerticalScrollBarEnabled(false);

        setBackgroundColor(Color.TRANSPARENT);
        setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
    }

    void setImplementation(MRAIDImplementation imp) {
        this.implementation = imp;
        this.setWebViewClient(imp.getWebViewClient());
        this.setWebChromeClient(imp.getWebChromeClient());
    }

    MRAIDImplementation getImplementation() {
        return implementation;
    }

    public void loadAd(AdResponse ar) {
        String html = ar.getContent();

        if (html.contains("mraid.js")) {
            setImplementation(new MRAIDImplementation(this));
        }

        if (implementation != null) {
            html = implementation.onPreLoadContent(this, html);
        }

        final float scale = owner.getContext().getResources()
                .getDisplayMetrics().density;
        int rheight = (int) (ar.getHeight() * scale + 0.5f);
        int rwidth = (int) (ar.getWidth() * scale + 0.5f);
        int rgravity = Gravity.CENTER;
        AdView.LayoutParams resize = new AdView.LayoutParams(rwidth, rheight,
                rgravity);
        this.setLayoutParams(resize);

        this.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
    }

    @Override
    public void onVisibilityChanged(View view, int visibility) {
        if (implementation != null) {
            if (visibility == View.VISIBLE) {
                implementation.onVisible();
            } else {
                implementation.onInvisible();
            }
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(0, 0);
    }

    // w,h in dips. this function converts to pixels
    void expand(int w, int h, boolean cust_close,
                MRAIDImplementation caller) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(metrics);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                this.getLayoutParams());
        default_width = lp.width;
        default_height = lp.height;

        if (h == -1 || w == -1) {
            if (owner != null) {
                isFullScreen = true;
            }
        }
        if (h != -1) {
            h = (int) (h * metrics.density + 0.5);
        }
        if (w != -1) {
            w = (int) (w * metrics.density + 0.5);
        }


        lp.height = h;
        lp.width = w;
        lp.gravity = Gravity.CENTER;

        if (owner != null) {
            owner.expand(w, h, cust_close, caller);
        }

        //If it's an IAV, prevent it from closing
        if (owner instanceof InterstitialAdView) {
            ((InterstitialAdView) owner).interacted();
        }

        this.setLayoutParams(lp);
    }

    void hide() {
        if (owner != null) {
            owner.hide();
        }
    }

    void show() {
        if (owner != null) {
            owner.expand(default_width, default_height, true, null);
        }
    }

    void close() {
        boolean isInterstitial = false;
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                this.getLayoutParams());
        lp.height = default_height;
        lp.width = default_width;
        lp.gravity = Gravity.CENTER;

        if (owner != null) {
            owner.close(default_width, default_height, implementation);
        }


        this.setLayoutParams(lp);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public boolean failed() {
        return false;
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right,
                         int bottom) {
        if (changed) {
            implementation.setCurrentPosition(this);
        }
    }

    public void resize(int w, int h, int offset_x, int offset_y, MRAIDImplementation.CUSTOM_CLOSE_POSITION custom_close_position, boolean allow_offscrean) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(metrics);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                this.getLayoutParams());
        if(!implementation.resized){
            default_width = lp.width;
            default_height = lp.height;
        }


        if (h != -1) {
            h = (int) (h * metrics.density + 0.5);
        }
        if (w != -1) {
            w = (int) (w * metrics.density + 0.5);
        }


        lp.height = h;
        lp.width = w;
        lp.gravity = Gravity.CENTER;

        if (owner != null) {
            owner.resize(w, h, offset_x, offset_y, custom_close_position, allow_offscrean, implementation);
        }

        //If it's an IAV, prevent it from closing
        if (owner instanceof InterstitialAdView) {
            ((InterstitialAdView) owner).interacted();
        }

        this.setLayoutParams(lp);
    }
}
