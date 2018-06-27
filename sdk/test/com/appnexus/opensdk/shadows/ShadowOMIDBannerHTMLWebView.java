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

import android.webkit.WebView;

import com.appnexus.opensdk.util.TestUtil;
import com.appnexus.opensdk.utils.Clog;

import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowWebView;

@Implements(value = WebView.class, callThroughByDefault = true)
public class ShadowOMIDBannerHTMLWebView extends ShadowWebView {

    private WebView webView;
    public static String omidInitString = "";
    public static String omidImpressionString = "";
    public static String omidStartSession = "";


    /*
    * OMID events call loadURL so its possible to store the values here and assert them against expected values.
     */
    @Override
    public void loadUrl(String url) {
        super.loadUrl(url);
        webView = new WebView(RuntimeEnvironment.application);
        Clog.d(TestUtil.testLogTag, "ShadowOMIDBannerHTMLWebView loadUrl::"+url);
        if(url.contains("omidBridge.init")){
            omidInitString = url;
        }

        if(url.contains("omidBridge.startSession")){
            omidStartSession = url;
        }

        if(url.contains("publishImpressionEvent")){
            omidImpressionString = url;
        }
    }


    /*
     * This makes it possible for onAdLoaded on Banner to be called
     */
    @Override
    public void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String historyUrl) {
        super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
        if(webView == null) {
            webView = new WebView(RuntimeEnvironment.application);
        }
        Clog.d(TestUtil.testLogTag, "ShadowOMIDBannerHTMLWebView loadDataWithBaseURL");
        this.getWebViewClient().onPageFinished(webView,baseUrl);
    }

}
