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

package com.appnexus.opensdk.instreamvideo.shadows;

import android.os.Handler;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import com.appnexus.opensdk.instreamvideo.util.TestUtil;
import com.appnexus.opensdk.utils.Clog;

import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowWebView;

@Implements(value = WebView.class, callThroughByDefault = true)
public class ShadowCustomClickThroughWebView extends ShadowWebView {

    private WebView webView;
    public static boolean simulateVideoError = false;
    public static boolean simulateDelayedVideoError = false;
    public static boolean simulateVideoAdClick = false;

    @Override
    public void loadUrl(String url) {
        super.loadUrl(url);
        webView = new WebView(RuntimeEnvironment.application);
        Clog.d(TestUtil.testLogTag, "ShadowCustomClickThroughWebView loadUrl");
        this.getWebViewClient().onPageFinished(webView, url);
    }

    @Override
    public void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String historyUrl) {
        super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
        webView = new WebView(RuntimeEnvironment.application);
        Clog.d(TestUtil.testLogTag, "ShadowCustomClickThroughWebView loadUrl");
        this.getWebViewClient().onPageFinished(webView, baseUrl);
    }

    @Override
    public void evaluateJavascript(String script, ValueCallback<String> callback) {
        super.evaluateJavascript(script, callback);
        Clog.d(TestUtil.testLogTag, "ShadowCustomClickThroughWebView evaluateJavascript: " + script);
        if (script.contains("createVastPlayerWithContent")) {
            Clog.d(TestUtil.testLogTag, "evaluateJavascript createVastPlayerWithContent");
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

            if (simulateVideoAdClick) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getWebViewClient().shouldOverrideUrlLoading(webView, "www.appnexus.com");
                    }
                }, 2000);
            }
        }
    }

}
