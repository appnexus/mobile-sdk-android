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
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.MutableContextWrapper;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.appnexus.opensdk.AdView.BrowserStyle;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.HTTPGet;
import com.appnexus.opensdk.utils.HTTPResponse;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.StringUtil;
import com.appnexus.opensdk.utils.ViewUtil;
import com.appnexus.opensdk.utils.WebviewUtil;

import java.util.HashMap;

@SuppressLint("ViewConstructor")
class AdWebView extends WebView implements Displayable {
    private boolean failed = false;
    AdView adView;

    // MRAID variables
    private boolean isMRAIDEnabled;
    private MRAIDImplementation implementation;
    private int default_width;
    private int default_height;
    boolean isFullScreen = false;
    private boolean firstPageFinished;
    private int creativeWidth;
    private int creativeHeight;
    // for viewable event
    private boolean isOnscreen = false;
    private boolean isVisible = false;
    private Handler handler = new Handler();
    private boolean viewableCheckPaused = false;
    private int orientation;
    private ProgressDialog progressDialog;
    protected String initialMraidStateString;
    private boolean MRAIDUseCustomClose=false;

    // touch detection
    private boolean userInteracted = false;

    private int checkPositionTimeInterval = 1000;

    public AdWebView(AdView adView) {
        super(new MutableContextWrapper(adView.getContext()));
        this.adView = adView;
        this.initialMraidStateString = MRAIDImplementation.MRAID_INIT_STATE_STRINGS[
                MRAIDImplementation.MRAID_INIT_STATE.STARTING_DEFAULT.ordinal()];
        setupSettings();
        setup();
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("SetJavaScriptEnabled")
    protected void setupSettings() {
        Settings.getSettings().ua = this.getSettings().getUserAgentString();
        this.getSettings().setJavaScriptEnabled(true);
        this.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        this.getSettings().setBuiltInZoomControls(false);
        this.getSettings().setLightTouchEnabled(false);
        this.getSettings().setLoadsImagesAutomatically(true);
        this.getSettings().setSupportZoom(false);
        this.getSettings().setUseWideViewPort(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            this.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        this.getSettings().setAllowFileAccess(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            this.getSettings().setAllowContentAccess(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            this.getSettings().setAllowFileAccessFromFileURLs(false);
            this.getSettings().setAllowUniversalAccessFromFileURLs(false);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager cm = CookieManager.getInstance();
            if (cm != null) {
                cm.setAcceptThirdPartyCookies(this, true);
            } else {
                Clog.d(Clog.baseLogTag, "Failed to set Webview to accept 3rd party cookie");
            }
        }

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
        setWebChromeClient(new VideoEnabledWebChromeClient(this));
        setWebViewClient(new AdWebViewClient());
    }

    public void loadAd(ServerResponse ad) {
        if(ad==null){
            return;
        }
        String html = ad.getContent();
        // set creative size
        setCreativeHeight(ad.getHeight());
        setCreativeWidth(ad.getWidth());
        // Safety Check: content is verified in AdResponse, so this should never be empty
        if (StringUtil.isEmpty(html)) {
            fail();
            return;
        }

        Clog.v(Clog.baseLogTag, Clog.getString(R.string.webview_loading, html));

        parseAdResponseExtras(ad.getExtras());

        html = preLoadContent(html);
        html = prependRawResources(html);
        html = prependViewPort(html);

        final float scale = adView.getContext().getResources()
                .getDisplayMetrics().density;
        int rheight,rwidth;
        if(ad.getHeight()==1 && ad.getWidth() == 1){
            rwidth=ViewGroup.LayoutParams.MATCH_PARENT;
            rheight=ViewGroup.LayoutParams.MATCH_PARENT;
        }else{
            rheight = (int) (ad.getHeight() * scale + 0.5f);
            rwidth = (int) (ad.getWidth() * scale + 0.5f);
        }
        AdView.LayoutParams resize = new AdView.LayoutParams(rwidth, rheight,
                Gravity.CENTER);
        this.setLayoutParams(resize);

        this.loadDataWithBaseURL(Settings.getBaseUrl(), html, "text/html", "UTF-8", null);
    }

    // The webview about to load the ad, and the html ad content
    private String preLoadContent(String html) {
        if (!StringUtil.isEmpty(html)) {
            // trim leading and trailing spaces
            html.trim();
            // check to see if content is wrapped with <html> tag
            if (!html.startsWith("<html>")) {
                StringBuilder bodyBuilder = new StringBuilder();
                html = bodyBuilder.append("<html><body style='padding:0;margin:0;'>").append(html).append("</body></html>").toString();
            }
        }
        return html;
    }

    private String prependRawResources(String html) {
        if (!StringUtil.isEmpty(html)) {
            Resources res = getResources();
            StringBuilder htmlSB = new StringBuilder("<html><head><script>");

            // retrieve source from raw resources
            // insert sdkjs, anjam, mraid into html content, in that order
            if (res == null
                    || !StringUtil.appendRes(htmlSB, res, R.raw.sdkjs)
                    || !StringUtil.appendRes(htmlSB, res, R.raw.anjam)
                    || !StringUtil.appendRes(htmlSB, res, R.raw.apn_mraid)) {
                Clog.e(Clog.baseLogTag, "Error reading SDK's raw resources.");
                return html;
            }
            htmlSB.append("</script></head>");
            html = html.replaceFirst("<html>", htmlSB.toString());
        }
        return html;
    }

    private String prependViewPort(String html){
        if (!StringUtil.isEmpty(html)) {
            StringBuilder viewportSB = new StringBuilder("<head><meta name=\"viewport\" content=\"width=device-width,initial-scale=1.0,user-scalable=no\"/>");
            html = html.replaceFirst("<head>", viewportSB.toString());
        }
        return html;
    }


    private void parseAdResponseExtras(HashMap extras) {
        if(extras.isEmpty()) {
            return;
        }

        if (extras.containsKey(ServerResponse.EXTRAS_KEY_MRAID)) {
            isMRAIDEnabled = (Boolean) extras.get(ServerResponse.EXTRAS_KEY_MRAID);
        }

        if (extras.containsKey(ServerResponse.EXTRAS_KEY_ORIENTATION)
                && extras.get(ServerResponse.EXTRAS_KEY_ORIENTATION).equals("h")) {
            this.orientation = Configuration.ORIENTATION_LANDSCAPE;
        } else {
            this.orientation = Configuration.ORIENTATION_PORTRAIT;
        }
    }

    protected void loadUrlWithMRAID(final String url){
        new HTTPGet() {
            @Override
            protected void onPostExecute(HTTPResponse response) {
                if(response.getSucceeded()){
                    String html = preLoadContent(response.getResponseBody());
                    html = prependRawResources(html);
                    html = prependViewPort(html);

                    loadDataWithBaseURL(Settings.getBaseUrl(), html, "text/html", "UTF-8", null);
                    fireMRAIDEnabled();
                }
            }

            @Override
            protected String getUrl() {
                return url;
            }
        }.execute();

    }

    protected MRAIDImplementation getMRAIDImplementation() {
        return implementation;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        userInteracted = true;
        return super.onTouchEvent(event);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        checkPosition();
    }

    boolean getUserInteraction() {
        return userInteracted;
    }

    /**
     * AdWebViewClient for the webview
     */
    private class AdWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Clog.v(Clog.baseLogTag, "Loading URL: " + url);
            if (url.startsWith("javascript:")) {
                return false;
            }

            if (url.startsWith("mraid://")) {
                Clog.v(Clog.mraidLogTag, url);
                if (isMRAIDEnabled) {
                    implementation.dispatch_mraid_call(url, userInteracted);
                } else {
                    String host = Uri.parse(url).getHost();
                    if ((host != null) && host.equals("enable")) {
                        fireMRAIDEnabled();
                    }else if((host != null) && host.equals("open")){
                        implementation.dispatch_mraid_call(url, userInteracted);
                    }
                }
                return true;
            } else if (url.startsWith("anjam://")) {
                ANJAMImplementation.handleUrl(AdWebView.this, url);
                return true;
            } else if (url.startsWith("appnexuspb://")) {
                PBImplementation.handleUrl(AdWebView.this, url);
                return true;
            }

            loadURLInCorrectBrowser(url);
            fireAdClicked();

            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            String  javascript  = "javascript:window.mraid.util.pageFinished()";

            if (!firstPageFinished) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    try {
                        view.evaluateJavascript(javascript, null);
                    } catch (Exception exception) {
                        Clog.e(Clog.baseLogTag, "AdWebView.onPageFinished -- Caught EXCEPTION...", exception);
                        Clog.e(Clog.baseLogTag, "AdWebView.onPageFinished -- ...Recovering with view.loadUrl.");
                        view.loadUrl(javascript);
                    }

                } else {
                    view.loadUrl(javascript);
                }
                if (isMRAIDEnabled) {
                    implementation.webViewFinishedLoading(AdWebView.this, initialMraidStateString);
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
                    // check that the hitTestResult matches the url
                    if (hitTestResult.getExtra() == null
                            || !hitTestResult.getExtra().equals(url)) {
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
                    R.string.webview_received_error, errorCode, description, failingURL));
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
            adView.interacted();
        }
    }

    // returns success or failure
    private boolean openNativeIntent(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
    private boolean checkForApp(String url) {
        if (url.contains("://play.google.com") || (!url.startsWith("http") && !url.startsWith("about:blank"))) {
            Clog.i(Clog.baseLogTag, Clog.getString(R.string.opening_app_store));
            return openNativeIntent(url);
        }

        return false;
    }

    private void openInAppBrowser(WebView fwdWebView) {
        Class<?> activity_clz = AdActivity.getActivityClass();

        Intent intent = new Intent(adView.getContext(), activity_clz);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(AdActivity.INTENT_KEY_ACTIVITY_TYPE, AdActivity.ACTIVITY_TYPE_BROWSER);

        BrowserAdActivity.BROWSER_QUEUE.add(fwdWebView);
        if (adView.getBrowserStyle() != null) {
            String i = "" + super.hashCode();
            intent.putExtra("bridgeid", i);
            AdView.BrowserStyle.bridge
                    .add(new Pair<String, BrowserStyle>(i,
                            adView.getBrowserStyle()));
        }

        try {
            adView.getContext().startActivity(intent);
            triggerBrowserLaunchEvent();
        } catch (ActivityNotFoundException e) {
            Clog.w(Clog.baseLogTag, Clog.getString(R.string.adactivity_missing, activity_clz.getName()));
            BrowserAdActivity.BROWSER_QUEUE.remove();
        }
    }

    // handles browser logic for shouldOverrideUrl
    void loadURLInCorrectBrowser(String url) {
        if (!adView.getOpensNativeBrowser()) {

            Clog.d(Clog.baseLogTag, Clog.getString(R.string.opening_inapp));

            //If it's a direct URL to the play store, just open it.
            if (checkForApp(url)) {
                return;
            }

            try {

                final WebView out;
                // Unless disabled by the user, handle redirects in background

                if (adView.getLoadsInBackground()) {
                    // Otherwise, create an invisible 1x1 webview to load the landing
                    // page and detect if we're redirecting to a market url
                    out = new RedirectWebView(this.getContext());
                    out.loadUrl(url);
                    out.setVisibility(View.GONE);
                    adView.addView(out);

                    if (this.adView.getShowLoadingIndicator()) {
                        //Show a dialog box
                        progressDialog = new ProgressDialog(this.getContextFromMutableContext());
                        progressDialog.setCancelable(true);
                        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                out.stopLoading();
                            }
                        });
                        progressDialog.setMessage(getContext().getResources().getString(R.string.loading));
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.show();
                    }
                } else {
                    // Stick the URL directly into the new activity.
                    out = new WebView(new MutableContextWrapper(getContext()));
                    WebviewUtil.setWebViewSettings(out);
                    out.loadUrl(url);
                    openInAppBrowser(out);
                }
            }catch (Exception e){
                // Catches PackageManager$NameNotFoundException for webview
                Clog.e(Clog.baseLogTag, "Exception initializing the redirect webview: " + e.getMessage());
            }
        } else {
            Clog.d(Clog.baseLogTag,
                    Clog.getString(R.string.opening_native));
            openNativeIntent(url);
            triggerBrowserLaunchEvent();
        }

    }

    private void fail() {
        failed = true;
    }

    // For interstitial ads

    int getOrientation() {
        return orientation;
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
        // in case `this` was not removed when destroy was called
        ViewUtil.removeChildFromParent(this);
        try {
            super.destroy();
        }
        // Fatal exception in android v4.x in TextToSpeech
        catch (IllegalArgumentException e){
            Clog.e(Clog.baseLogTag, Clog.getString(R.string.apn_webview_failed_to_destroy),e);
        }
        this.removeAllViews();
        stopCheckViewable();
    }

    private void setCreativeWidth(int w){
        this.creativeWidth = w;
    }

    @Override
    public int getCreativeWidth() {
        return this.creativeWidth;
    }

    private void setCreativeHeight(int h){
        this.creativeHeight = h;
    }

    @Override
    public int getCreativeHeight() {
        return this.creativeHeight;
    }

    @Override
    public void onDestroy() {
        destroy();
    }


    public boolean isMRAIDUseCustomClose() {
        return MRAIDUseCustomClose;
    }

    public void setMRAIDUseCustomClose(boolean MRAIDUseCustomClose) {
        this.MRAIDUseCustomClose = MRAIDUseCustomClose;
    }

    // MRAID code

    private void handleVisibilityChangedEvent(int windowVisibility, int visibility) {
        if ((windowVisibility == VISIBLE) && (visibility == VISIBLE)) {
            WebviewUtil.onResume(this);
            isVisible = true;
            // only start checking if MRAID is enabled
            if (isMRAIDEnabled && firstPageFinished) {
                startCheckViewable();
            }
        } else {
            WebviewUtil.onPause(this);
            isVisible = false;
            stopCheckViewable();
        }
        implementation.fireViewableChangeEvent();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        handleVisibilityChangedEvent(visibility, getVisibility());
    }

    @Override
    public void onVisibilityChanged(View view, int visibility) {
        super.onVisibilityChanged(view, visibility);
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
            implementation.webViewFinishedLoading(this, initialMraidStateString);
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
        // only lock orientation if we're in fullscreen mode
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
        }

        if (adView != null) {
            adView.expand(w, h, cust_close, caller, mraidFullscreenListener);
            adView.interacted();
        }

        this.setLayoutParams(lp);
    }

    protected void lockOrientationFromExpand(Activity containerActivity,
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

    void close() {
        if (adView != null) {
            adView.close(default_width, default_height, implementation);
        }
    }

    protected void checkPosition() {
        if (!(this.getContextFromMutableContext() instanceof Activity)) return;

        // check whether newly drawn view is onscreen or not,
        // fires a viewableChangeEvent with the result
        int viewLocation[] = new int[2];
        this.getLocationOnScreen(viewLocation);

        int left = viewLocation[0];
        int right = viewLocation[0] + this.getWidth();
        int top = viewLocation[1];
        int bottom = viewLocation[1] + this.getHeight();

        int[] screenSize = ViewUtil.getScreenSizeAsPixels((Activity) this.getContextFromMutableContext());

        this.isOnscreen = (right > 0) && (left < screenSize[0])
                && (bottom > 0) && (top < screenSize[1]);

        // update current position
        if (implementation != null) {
            implementation.fireViewableChangeEvent();
            implementation.setCurrentPosition(left, top, this.getWidth(), this.getHeight());
            int orientation = this.getContext().getResources().getConfiguration().orientation;
            implementation.onOrientationChanged(orientation);
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

        h = (int) (h * metrics.density + 0.5);
        w = (int) (w * metrics.density + 0.5);

        lp.height = h;
        lp.width = w;
        lp.gravity = Gravity.CENTER;

        if (adView != null) {
            adView.resize(w, h, offset_x, offset_y, custom_close_position, allow_offscrean, implementation);
        }

        if (adView != null) {
            adView.interacted();
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
            handler.postDelayed(this, checkPositionTimeInterval);
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

    private class RedirectWebView extends WebView {

        @SuppressLint("SetJavaScriptEnabled")
        public RedirectWebView(Context context) {
            super(new MutableContextWrapper(context));

            WebviewUtil.setWebViewSettings(this);
            this.setWebViewClient(new WebViewClient() {
                private boolean isOpeningAppStore = false;

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    Clog.v(Clog.browserLogTag, "Redirecting to URL: " + url);
                    isOpeningAppStore = checkForApp(url);

                    if (isOpeningAppStore) {
                        if(progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }

                    return isOpeningAppStore;
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    Clog.v(Clog.browserLogTag, "Opening URL: " + url);
                    ViewUtil.removeChildFromParent(RedirectWebView.this);

                    if(progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }

                    if (isOpeningAppStore) {
                        isOpeningAppStore = false;
                        RedirectWebView.this.destroy();
                        triggerBrowserLaunchEvent();
                        return;
                    }

                    RedirectWebView.this.setVisibility(View.VISIBLE);
                    openInAppBrowser(RedirectWebView.this);
                }
            });
        }
    }

    private void triggerBrowserLaunchEvent() {
        if (adView != null && adView instanceof InterstitialAdView) {
            ((InterstitialAdView)adView).browserLaunched();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    // Helper method for getting Context if it is of type MutableContextWrapper.
    protected Context getContextFromMutableContext() {
        if (this.getContext() instanceof MutableContextWrapper) {
           return  ((MutableContextWrapper) this.getContext()).getBaseContext();
        }
        return this.getContext();
    }

    protected void injectJavaScript(String url){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            try {
                evaluateJavascript(url, null);
            } catch (Exception exception) {
                Clog.e(Clog.baseLogTag, "AdWebView.injectJavaScript -- Caught EXCEPTION...", exception);
                Clog.e(Clog.baseLogTag, "AdWebView.injectJavaScript -- ...Recovering with loadUrl.");
                loadUrl(url);
            }
        } else {
            loadUrl(url);
        }
    }

    public void setCheckPositionTimeInterval(int interval) {
        checkPositionTimeInterval = interval;
        stopCheckViewable();
        startCheckViewable();
    }

}

class MRAIDTwoPartExpandWebView extends AdWebView{
    MRAIDImplementation firstPartImplementation;

    //The mraidimplementation parameter is NOT here to be reused by this webview.
    //this webview will make its own mraidimplementation
    //and only access the first one when it closes, below
    MRAIDTwoPartExpandWebView(AdView adView, MRAIDImplementation firstPartImplementation){
        super(adView);
        this.initialMraidStateString = MRAIDImplementation.MRAID_INIT_STATE_STRINGS[
                MRAIDImplementation.MRAID_INIT_STATE.STARTING_EXPANDED.ordinal()];
        this.firstPartImplementation=firstPartImplementation;
    }

    @Override
    void close() {
        super.close();
        firstPartImplementation.close();
    }
}
