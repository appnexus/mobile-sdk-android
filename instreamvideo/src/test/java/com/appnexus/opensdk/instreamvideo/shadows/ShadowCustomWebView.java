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

package com.appnexus.opensdk.instreamvideo.shadows;

import android.os.Build;
import android.webkit.WebView;

import com.appnexus.opensdk.instreamvideo.util.TestUtil;
import com.appnexus.opensdk.utils.Clog;

import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowWebView;

@Implements(value = WebView.class, callThroughByDefault = true)
public class ShadowCustomWebView extends ShadowWebView {


    private WebView webView;

    @Override
    public void loadUrl(String url) {
        super.loadUrl(url);
        webView = new WebView(RuntimeEnvironment.application);
        Clog.w(TestUtil.testLogTag, "ShadowCustomWebView loadUrl");
        // Just send back adReady notification from here since this is unit tests webview is not loading complete.
        this.getWebViewClient().shouldOverrideUrlLoading(webView,"video://{\"event\":\"adReady\",\"params\":{\"creativeUrl\":\"http://vcdn.adnxs.com/p/creative-video/05/64/6d/99/05646d99.webm\",\"duration\":96000}}");

    }

    protected void injectJavaScript(String url) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript(url, null);
        } else {
            loadUrl(url);
        }
    }
}
