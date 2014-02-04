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
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.appnexus.opensdk.AdView.BrowserStyle;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.WebviewUtil;

@SuppressLint("ViewConstructor")
// This will only be constructed by AdFetcher.
class AdWebView extends WebView implements Displayable {
    private boolean failed = false;
    private AdView destination;


    public AdWebView(AdView owner) {
        super(owner.getContext());
        destination = owner;
        setupSettings();
        setup();
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("SetJavaScriptEnabled")
    protected void setupSettings(){
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

        setWebChromeClient(new VideoEnabledWebChromeClient(destination));

        setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingURL) {
                Clog.e(Clog.httpRespLogTag, Clog.getString(
                        R.string.webclient_error, errorCode, description));
            }

            @Override
            public void onReceivedSslError(WebView view,
                                           SslErrorHandler handler, SslError error) {
                AdWebView.this.fail();
                Clog.e(Clog.httpRespLogTag,
                        Clog.getString(R.string.webclient_error,
                                error.getPrimaryError(), error.toString()));
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("javascript:") || url.startsWith("mraid:"))
                    return false;

                loadURLInCorrectBrowser(url);

                fireAdClicked();

                return true;
            }

            @Override
            public void onLoadResource (WebView view, String url) {
                if (url.startsWith("http")) {
                    if (getHitTestResult() == null) {
                        return;
                    }

                    switch(getHitTestResult().getType()){
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
        });

    }

    private void fireAdClicked(){
        if (destination != null) {
            destination.getAdDispatcher().onAdClicked();
        }

        // If it's an IAV, prevent it from closing
        if (destination instanceof InterstitialAdView) {
            ((InterstitialAdView) destination).interacted();
        }
    }

    protected void loadURLInCorrectBrowser(String url){
        Intent intent = null;
        // open the in-app browser
        if (!AdWebView.this.destination.getOpensNativeBrowser() && url.startsWith("http")) {
            Clog.d(Clog.baseLogTag,
                    Clog.getString(R.string.opening_inapp));
            //Create a 1x1 webview somewhere invisible to load the landing page and detect if we're redirecting to a market url

            WebView fwdWebView = new WebView(this.getContext());

            fwdWebView.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url){
                    if(url.contains("play.google.com") || url.contains("market://")){

                        Clog.d(Clog.baseLogTag,
                                Clog.getString(R.string.opening_app_store));
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));

                        try {
                            AdWebView.this.destination.getContext().startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            Clog.w(Clog.baseLogTag,
                                    Clog.getString(R.string.opening_url_failed, url));
                        }
                        return true;
                    }
                    return false;
                }

                @Override
                public void onPageFinished(WebView view, String url){
                    Intent intent = new Intent(AdWebView.this.destination.getContext(),
                            BrowserActivity.class);
                    intent.putExtra("url", url);
                    if (AdWebView.this.destination.getBrowserStyle() != null) {
                        String i = "" + super.hashCode();
                        intent.putExtra("bridgeid", i);
                        AdView.BrowserStyle.bridge
                                .add(new Pair<String, BrowserStyle>(i,
                                        AdWebView.this.destination.getBrowserStyle()));
                    }
                    try {
                        AdWebView.this.destination.getContext().startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Clog.w(Clog.baseLogTag,
                                Clog.getString(R.string.opening_url_failed, url));
                    }
                }
            });

            fwdWebView.loadUrl(url);

        } else {
            Clog.d(Clog.baseLogTag,
                    Clog.getString(R.string.opening_native));
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
        }

        if(intent!=null){
            try {
                AdWebView.this.destination.getContext().startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Clog.w(Clog.baseLogTag,
                        Clog.getString(R.string.opening_url_failed, url));
            }
        }
    }

    public void loadAd(AdResponse ad) {
        if (ad.getContent().equals("")) {
            fail();
            return;
        }

        String body = "<html><head /><body style='margin:0;padding:0;'>"
                + ad.getContent() + "</body></html>";
        Clog.v(Clog.baseLogTag, Clog.getString(R.string.webview_loading, body));
        this.loadDataWithBaseURL("http://mobile.adnxs.com", body, "text/html",
                "UTF-8", null);

        final float scale = destination.getContext().getResources()
                .getDisplayMetrics().density;
        int rheight = (int) (ad.getHeight() * scale + 0.5f);
        int rwidth = (int) (ad.getWidth() * scale + 0.5f);
        int rgravity = Gravity.CENTER;
        AdView.LayoutParams resize = new AdView.LayoutParams(rwidth, rheight,
                rgravity);
        this.setLayoutParams(resize);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            WebviewUtil.onResume(this);
        } else {
            WebviewUtil.onPause(this);
        }
    }

    @Override
    public View getView() {
        return this;
    }

    private void fail() {
        failed = true;
    }

    @Override
    public boolean failed() {
        return failed;
    }
}
