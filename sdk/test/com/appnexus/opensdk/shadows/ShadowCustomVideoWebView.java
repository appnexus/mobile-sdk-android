/*
 *    Copyright 2018 APPNEXUS INC
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

package com.appnexus.opensdk.shadows;

import android.os.Handler;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import com.appnexus.opensdk.util.TestUtil;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.StringUtil;

import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowWebView;

@Implements(value = WebView.class, callThroughByDefault = true)
public class ShadowCustomVideoWebView extends ShadowWebView {


    private WebView webView;
    public static boolean simulateVideoError = false;
    public static boolean simulateDelayedVideoError = false;
    private static final String AD_READY_CONSTANT = "{\"event\":\"adReady\",\"params\":{\"aspectRatio\":\"aspect_ratio\",\"vastCreativeUrl\":\"\"}}";
    public static String aspectRatio = "";

    @Override
    public void loadUrl(String url) {
        super.loadUrl(url);
        if (url.contains("file:///android_asset/apn_vastvideo.html")) {
            webView = new WebView(RuntimeEnvironment.application);
            Clog.d(TestUtil.testLogTag, "ShadowCustomWebView loadUrl");
            this.getWebViewClient().onPageFinished(webView, url);
        }
    }


    @Override
    public void evaluateJavascript(String script, ValueCallback<String> callback) {
        super.evaluateJavascript(script, callback);
        Clog.d(TestUtil.testLogTag, "ShadowCustomWebView evaluateJavascript");
        if (script.contains("createVastPlayerWithContent")) {
            Clog.d(TestUtil.testLogTag, "evaluateJavascript createVastPlayerWithContent");

            if (!StringUtil.isEmpty(aspectRatio)) {
                String adReady = AD_READY_CONSTANT.replace("aspect_ratio", aspectRatio);
                // Just send back adReady notification from here since this is unit tests webview is not loading complete.
                getWebViewClient().shouldOverrideUrlLoading(webView, String.format("video://%s", adReady));
            } else {
                if (!simulateVideoError) {
                    this.getWebViewClient().shouldOverrideUrlLoading(webView, "video://{\"event\":\"adReady\",\"params\":{\"creativeUrl\":\"http://vcdn.adnxs.com/p/creative-video/05/64/6d/99/05646d99.webm\",\"duration\":96000}}");
                } else {
                    this.getWebViewClient().shouldOverrideUrlLoading(webView, "video://{\"event\":\"video-error\",\"params\":{}}");
                }

                if (simulateDelayedVideoError) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getWebViewClient().shouldOverrideUrlLoading(webView, "video://{\"event\":\"video-error\",\"params\":{}}");
                        }
                    }, 1000);
                }
            }
        }
    }

}
