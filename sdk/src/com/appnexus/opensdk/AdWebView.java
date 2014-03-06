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
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.appnexus.opensdk.AdView.BrowserStyle;
import com.appnexus.opensdk.utils.*;

@SuppressLint("ViewConstructor")
class AdWebView extends WebView implements Displayable {
    protected static WebView REDIRECT_WEBVIEW;
    private boolean failed = false;
    AdView adView;

    // MRAID variables
    private boolean isMRAIDEnabled;
    private MRAIDImplementation implementation;
    private int default_width;
    private int default_height;
    boolean isFullScreen = false;
    private boolean firstPageFinished;
    // for viewable event
    private boolean isOnscreen = false;
    private boolean isVisible = false;
    private Handler handler = new Handler();
    private boolean viewableCheckPaused = false;

    public AdWebView(AdView adView) {
        super(adView.getContext());
        this.adView = adView;
        setupSettings();
        setup();
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("SetJavaScriptEnabled")
    protected void setupSettings() {
        Settings.getSettings().ua = this.getSettings().getUserAgentString();
        this.getSettings().setJavaScriptEnabled(true);
        this.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        this.getSettings().setPluginState(WebSettings.PluginState.ON);
        this.getSettings().setBuiltInZoomControls(false);
        this.getSettings().setLightTouchEnabled(false);
        this.getSettings().setLoadsImagesAutomatically(true);
        this.getSettings().setSupportZoom(false);
        this.getSettings().setUseWideViewPort(false);
        this.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        // this.setInitialScale(100);

        setHorizontalScrollbarOverlay(false);
        setHorizontalScrollBarEnabled(false);
        setVerticalScrollbarOverlay(false);
        setVerticalScrollBarEnabled(false);

        setBackgroundColor(Color.TRANSPARENT);
        setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("SetJavaScriptEnabled")
    protected void setup() {
        implementation = new MRAIDImplementation(this);
        setWebChromeClient(new VideoEnabledWebChromeClient(adView));
        setWebViewClient(new AdWebViewClient());
    }

    public void loadAd(AdResponse ad) {
        String html = ad.getContent();
        if (StringUtil.isEmpty(html)) {
            fail();
            return;
        }

        Clog.v(Clog.baseLogTag, Clog.getString(R.string.webview_loading, html));

        if (ad.isMraid()) {
            isMRAIDEnabled = true;
        }

        html = implementation.onPreLoadContent(this, html);

        final float scale = adView.getContext().getResources()
                .getDisplayMetrics().density;
        int rheight = (int) (ad.getHeight() * scale + 0.5f);
        int rwidth = (int) (ad.getWidth() * scale + 0.5f);
        AdView.LayoutParams resize = new AdView.LayoutParams(rwidth, rheight,
                Gravity.CENTER);
        this.setLayoutParams(resize);

        String baseUrl = isMRAIDEnabled ? null : Settings.getSettings().BASE_URL;
        this.loadDataWithBaseURL(baseUrl, html, "text/html", "UTF-8", null);
    }

    /**
     * AdWebViewClient for the webview
     */
    private class AdWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("javascript:")) {
                return false;
            }

            if (url.startsWith("mraid://")) {
                Clog.v(Clog.mraidLogTag, url);
                if (isMRAIDEnabled) {
                    implementation.dispatch_mraid_call(url);
                } else {
                    String host = Uri.parse(url).getHost();
                    if ((host != null) && host.equals("enable")) {
                        fireMRAIDEnabled();
                    }
                }
                return true;
            }

            loadURLInCorrectBrowser(url);
            fireAdClicked();

            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (!firstPageFinished) {
                if (isMRAIDEnabled) {
                    implementation.webViewFinishedLoading(AdWebView.this);
                    startCheckViewable();
                }
                firstPageFinished = true;
            }
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onLoadResource(WebView view, String url) {
            if (url.startsWith("http")) {
                HitTestResult hitTestResult;
                try {
                    hitTestResult = getHitTestResult();
                    if (hitTestResult == null) {
                        return;
                    }
                } catch (NullPointerException e) {
                    return;
                }

                switch (hitTestResult.getType()) {
                    default:
                        break;
                    case HitTestResult.ANCHOR_TYPE:
                    case HitTestResult.IMAGE_ANCHOR_TYPE:
                    case HitTestResult.SRC_ANCHOR_TYPE:
                    case HitTestResult.SRC_IMAGE_ANCHOR_TYPE:

                        loadURLInCorrectBrowser(url);
                        view.stopLoading();
                        fireAdClicked();
                        break;
                }
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingURL) {
            Clog.w(Clog.httpRespLogTag, Clog.getString(
                    R.string.webview_received_error, errorCode, description));
        }

        @Override
        public void onReceivedSslError(WebView view,
                                       SslErrorHandler handler, SslError error) {
            AdWebView.this.fail();
            Clog.w(Clog.httpRespLogTag,
                    Clog.getString(R.string.webclient_error,
                            error.getPrimaryError(), error.toString()));
        }
    }

    void fireAdClicked() {
        if (adView != null) {
            adView.getAdDispatcher().onAdClicked();
        }

        if (adView instanceof InterstitialAdView) {
            ((InterstitialAdView) adView).interacted();
        }
    }

    // returns success or failure
    private boolean openNativeIntent(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        try {
            adView.getContext().startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            Clog.w(Clog.baseLogTag,
                    Clog.getString(R.string.opening_url_failed, url));
            if (isMRAIDEnabled) {
                Toast.makeText(adView.getContext(),
                        R.string.action_cant_be_completed,
                        Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    }

    // returns success or failure
    private boolean checkStore(String url) {
        if (url.contains("://play.google.com") || url.contains("market://")) {
            Clog.d(Clog.baseLogTag,
                    Clog.getString(R.string.opening_app_store));
            return openNativeIntent(url);
        }
        return false;
    }

    private void openInAppBrowser(WebView fwdWebView) {
        // open the in-app browser
        Intent intent = new Intent(adView.getContext(), AdActivity.class);
        intent.putExtra(AdActivity.INTENT_KEY_ACTIVITY_TYPE, AdActivity.ACTIVITY_TYPE_BROWSER);

        AdWebView.REDIRECT_WEBVIEW = fwdWebView;
        if (adView.getBrowserStyle() != null) {
            String i = "" + super.hashCode();
            intent.putExtra("bridgeid", i);
            AdView.BrowserStyle.bridge
                    .add(new Pair<String, BrowserStyle>(i,
                            adView.getBrowserStyle()));
        }

        try {
            AdWebView.this.adView.getContext().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Clog.w(Clog.baseLogTag, Clog.getString(R.string.adactivity_missing));
            AdWebView.REDIRECT_WEBVIEW = null;
        }
    }

    // handles browser logic for shouldOverrideUrl
    void loadURLInCorrectBrowser(String url) {
        if (!AdWebView.this.adView.getOpensNativeBrowser()
                && url.startsWith("http")) {
            Clog.d(Clog.baseLogTag,
                    Clog.getString(R.string.opening_inapp));

            //If it's a direct URL to the play store, just open it.
            if (checkStore(url)) {
                return;
            }

            // Otherwise, create an invisible 1x1 webview to load the landing
            // page and detect if we're redirecting to a market url
            final WebView fwdWebView = new WebView(this.getContext());

            fwdWebView.setWebViewClient(new WebViewClient() {
                private boolean isOpeningAppStore = false;

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    isOpeningAppStore = checkStore(url);
                    return isOpeningAppStore;
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    if (isOpeningAppStore) {
                        isOpeningAppStore = false;
                        return;
                    }

                    openInAppBrowser(fwdWebView);
                }
            });

            fwdWebView.loadUrl(url);
        } else {
            Clog.d(Clog.baseLogTag,
                    Clog.getString(R.string.opening_native));
            openNativeIntent(url);
        }
    }

    private void fail() {
        failed = true;
    }

    // Displayable methods

    @Override
    public View getView() {
        return this;
    }

    @Override
    public boolean failed() {
        return failed;
    }

    @Override
    public void destroy() {
        super.destroy();
        stopCheckViewable();
        if (implementation != null) {
            implementation.destroy();
        }
    }

    // MRAID code

    private void handleVisibilityChangedEvent(int windowVisibility, int visibility) {
        if ((windowVisibility == VISIBLE) && (visibility == VISIBLE)) {
            WebviewUtil.onResume(this);
            isVisible = true;
            // only start checking if MRAID is enabled
            if (isMRAIDEnabled) startCheckViewable();
        } else {
            WebviewUtil.onPause(this);
            isVisible = false;
            stopCheckViewable();
        }
        implementation.fireViewableChangeEvent();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        handleVisibilityChangedEvent(visibility, getVisibility());
    }

    @Override
    public void onVisibilityChanged(View view, int visibility) {
        handleVisibilityChangedEvent(getWindowVisibility(), visibility);
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(0, 0);
    }

    public void fireMRAIDEnabled() {
        if (isMRAIDEnabled) return;

        isMRAIDEnabled = true;
        if (this.firstPageFinished) {
            implementation.webViewFinishedLoading(this);
            startCheckViewable();
        }
    }

    // w,h in dips. this function converts to pixels
    void expand(int w, int h, boolean cust_close, final MRAIDImplementation caller,
                final boolean allowOrientationChange, final AdActivity.OrientationEnum forceOrientation) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(metrics);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                this.getLayoutParams());
        if (!implementation.resized) {
            default_width = lp.width;
            default_height = lp.height;
        }

        if ((h == -1) && (w == -1)) {
            if (adView != null) {
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

        MRAIDFullscreenListener mraidFullscreenListener = null;
        if (isFullScreen) {
            // if fullscreen, create a listener to lock the activity when it is created
            mraidFullscreenListener = new MRAIDFullscreenListener() {
                @Override
                public void onCreateCompleted() {
                    // lock orientation if necessary
                    if ((caller != null) && (caller.getFullscreenActivity() != null)) {
                        lockOrientationFromExpand(caller.getFullscreenActivity(),
                                allowOrientationChange, forceOrientation);
                        AdView.mraidFullscreenListener = null; // only listen once
                    }
                }
            };
        } else {
            // otherwise, lock the current activity
            lockOrientationFromExpand((Activity) this.getContext(),
                    allowOrientationChange, forceOrientation);
        }

        if (adView != null) {
            adView.expand(w, h, cust_close, caller, mraidFullscreenListener);
        }

        if (adView instanceof InterstitialAdView) {
            ((InterstitialAdView) adView).interacted();
        }

        this.setLayoutParams(lp);
    }

    private void lockOrientationFromExpand(Activity containerActivity,
                                           boolean allowOrientationChange,
                                           AdActivity.OrientationEnum forceOrientation) {
        if (forceOrientation != AdActivity.OrientationEnum.none) {
            AdActivity.lockToMRAIDOrientation(containerActivity, forceOrientation);
        }

        if (allowOrientationChange) {
            AdActivity.unlockOrientation(containerActivity);
        } else if (forceOrientation == AdActivity.OrientationEnum.none) {
            // if forceOrientation was not none, it would have locked the orientation already
            AdActivity.lockToCurrentOrientation(containerActivity);
        }
    }

    void hide() {
        if (adView != null) {
            adView.hide();
        }
    }

    void show() {
        if (adView != null) {
            adView.expand(default_width, default_height, true, null, null);
        }
    }

    void close() {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                this.getLayoutParams());
        lp.height = default_height;
        lp.width = default_width;
        lp.gravity = Gravity.CENTER;

        if (adView != null) {
            adView.close(default_width, default_height, implementation);
        }


        this.setLayoutParams(lp);
    }

    private void checkPosition() {
        if (!(this.getContext() instanceof Activity)) return;

        // check whether newly drawn view is onscreen or not,
        // fires a viewableChangeEvent with the result
        int viewLocation[] = new int[2];
        this.getLocationOnScreen(viewLocation);

        int left = viewLocation[0];
        int right = viewLocation[0] + this.getWidth();
        int top = viewLocation[1];
        int bottom = viewLocation[1] + this.getHeight();

        int[] screenSize = ViewUtil.getScreenSizeAsPixels((Activity) this.getContext());

        this.isOnscreen = (right > 0) && (left < screenSize[0])
                && (bottom > 0) && (top < screenSize[1]);

        // update current position
        if (implementation != null) {
            implementation.fireViewableChangeEvent();
            implementation.setCurrentPosition(left, top, this.getWidth(), this.getHeight());
        }
    }

    boolean isViewable() {
        return isOnscreen && isVisible;
    }

    public void resize(int w, int h, int offset_x, int offset_y, MRAIDImplementation.CUSTOM_CLOSE_POSITION custom_close_position, boolean allow_offscrean) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(metrics);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                this.getLayoutParams());
        if (!implementation.resized) {
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

        if (adView != null) {
            adView.resize(w, h, offset_x, offset_y, custom_close_position, allow_offscrean, implementation);
        }

        if (adView instanceof InterstitialAdView) {
            ((InterstitialAdView) adView).interacted();
        }

        this.setLayoutParams(lp);
    }

    interface MRAIDFullscreenListener {
        void onCreateCompleted();
    }

    // Viewable timer code

    private final Runnable checkViewableRunnable = new Runnable() {
        @Override
        public void run() {
            if (viewableCheckPaused) return;
            checkPosition();
            handler.postDelayed(this, 1000);
        }
    };

    private void startCheckViewable() {
        // only start if webview is visible
        if (!isVisible) return;
        viewableCheckPaused = false;
        handler.removeCallbacks(checkViewableRunnable);
        handler.post(checkViewableRunnable);
    }

    private void stopCheckViewable() {
        viewableCheckPaused = true;
        handler.removeCallbacks(checkViewableRunnable);
    }
}
