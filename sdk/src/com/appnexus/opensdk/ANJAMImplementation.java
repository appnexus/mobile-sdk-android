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
import android.net.Uri;
import android.util.Pair;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.appnexus.opensdk.utils.*;
import org.apache.http.message.BasicNameValuePair;

import java.util.LinkedList;
import java.util.List;

public class ANJAMImplementation {
    private static final String CALL_MAYDEEPLINK = "MayDeepLink";
    private static final String CALL_DEEPLINK = "DeepLink";
    private static final String CALL_EXTERNALBROWSER = "ExternalBrowser";
    private static final String CALL_INTERNALBROWSER = "InternalBrowser";
    private static final String CALL_RECORDEVENT = "RecordEvent";
    private static final String CALL_DISPATCHAPPEVENT = "DispatchAppEvent";
    private static final String CALL_GETDEVICEID = "GetDeviceID";

    private static final String KEY_CALLER = "caller";

    static void handleUrl(AdWebView webView, String url) {
        Uri uri = Uri.parse(url);
        String call = uri.getHost();
        if (CALL_MAYDEEPLINK.equals(call)) {
            callMayDeepLink(webView, uri);
        } else if (CALL_DEEPLINK.equals(call)) {
            callDeepLink(webView, uri);
        } else if (CALL_EXTERNALBROWSER.equals(call)) {
            callExternalBrowser(webView, uri);
        } else if (CALL_INTERNALBROWSER.equals(call)) {
            callInternalBrowser(webView, uri);
        } else if (CALL_RECORDEVENT.equals(call)) {
            callRecordEvent(webView, uri);
        } else if (CALL_DISPATCHAPPEVENT.equals(call)) {
            callDispatchAppEvent(webView, uri);
        } else if (CALL_GETDEVICEID.equals(call)) {
            callGetDeviceID(webView, uri);
        } else {
            Clog.w(Clog.baseLogTag, "ANJAM called with unsupported function: " + call);
        }
    }

    // Deep Link

    private static void callMayDeepLink(WebView webView, Uri uri) {
        boolean mayDeepLink;
        String cb = uri.getQueryParameter("cb");
        String urlParam = uri.getQueryParameter("url");

        if ((webView.getContext() == null)
                || (webView.getContext().getPackageManager() == null)
                || (urlParam == null)) {
            mayDeepLink = false;
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Uri.decode(urlParam)));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mayDeepLink = intent.resolveActivity(webView.getContext().getPackageManager()) != null;
        }

        LinkedList<BasicNameValuePair> list = new LinkedList<BasicNameValuePair>();
        list.add(new BasicNameValuePair(KEY_CALLER, CALL_MAYDEEPLINK));
        list.add(new BasicNameValuePair("mayDeepLink", String.valueOf(mayDeepLink)));
        loadResult(webView, cb, list);
    }

    private static void callDeepLink(WebView webView, Uri uri) {
        String cb = uri.getQueryParameter("cb");
        String urlParam = uri.getQueryParameter("url");

        LinkedList<BasicNameValuePair> list = new LinkedList<BasicNameValuePair>();
        list.add(new BasicNameValuePair(KEY_CALLER, CALL_DEEPLINK));

        if ((webView.getContext() == null)
                || (urlParam == null)) {
            loadResult(webView, cb, list);
            return;
        }
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Uri.decode(urlParam)));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            webView.getContext().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            loadResult(webView, cb, list);
        }
    }

    // Launch Browser

    private static void callExternalBrowser(WebView webView, Uri uri) {
        String urlParam = uri.getQueryParameter("url");

        if ((webView.getContext() == null)
                || (urlParam == null)
                || (!urlParam.startsWith("http"))) {
            return;
        }
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Uri.decode(urlParam)));
            webView.getContext().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(webView.getContext(),
                    R.string.action_cant_be_completed,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private static void callInternalBrowser(AdWebView webView, Uri uri) {
        String urlParam = uri.getQueryParameter("url");

        if ((webView.getContext() == null)
                || (urlParam == null)
                || (!urlParam.startsWith("http"))) {
            return;
        }

        String url = Uri.decode(urlParam);

        Intent intent = new Intent(webView.getContext(), AdActivity.class);
        intent.putExtra(AdActivity.INTENT_KEY_ACTIVITY_TYPE, AdActivity.ACTIVITY_TYPE_BROWSER);

        WebView browserWebView = new WebView(webView.getContext());
        WebviewUtil.setWebViewSettings(browserWebView);
        AdWebView.BROWSER_QUEUE.add(browserWebView);
        browserWebView.loadUrl(url);

        if (webView.adView.getBrowserStyle() != null) {
            String i = "" + browserWebView.hashCode();
            intent.putExtra("bridgeid", i);
            AdView.BrowserStyle.bridge
                    .add(new Pair<String, AdView.BrowserStyle>(i,
                            webView.adView.getBrowserStyle()));
        }

        try {
            webView.getContext().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(webView.getContext(),
                    R.string.action_cant_be_completed,
                    Toast.LENGTH_SHORT).show();
            Clog.w(Clog.baseLogTag, Clog.getString(R.string.adactivity_missing));
            AdWebView.BROWSER_QUEUE.remove();
        }
    }

    // Record Event

    private static void callRecordEvent(AdWebView webView, Uri uri) {
        String urlParam = uri.getQueryParameter("url");

        if ((urlParam == null)
                || (!urlParam.startsWith("http"))) {
            return;
        }

        // Create a invisibile webview to fire the url
        WebView recordEventWebView = new WebView(webView.getContext());
        recordEventWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Clog.d(Clog.baseLogTag, "RecordEvent completed loading: " + url);

                CookieSyncManager csm = CookieSyncManager.getInstance();
                if (csm != null) csm.sync();
            }
        });
        recordEventWebView.loadUrl(urlParam);
        recordEventWebView.setVisibility(View.GONE);
        webView.addView(recordEventWebView);
    }

    // Dispatch App Event

    private static void callDispatchAppEvent(AdWebView webView, Uri uri) {
        String event = uri.getQueryParameter("event");
        String data = uri.getQueryParameter("data");

        webView.adView.getAdDispatcher().onAppEvent(event, data);
    }

    // Get Device ID

    private static void callGetDeviceID(WebView webView, Uri uri) {
        String cb = uri.getQueryParameter("cb");
        String idValue;
        String idNameValue;

        if (!StringUtil.isEmpty(Settings.getSettings().aaid)) {
            idValue = Settings.getSettings().aaid;
            idNameValue = "aaid";
        } else {
            idValue = Settings.getSettings().hidsha1;
            idNameValue = "sha1udid";
        }

        LinkedList<BasicNameValuePair> list = new LinkedList<BasicNameValuePair>();
        list.add(new BasicNameValuePair(KEY_CALLER, CALL_GETDEVICEID));
        list.add(new BasicNameValuePair("idname", idNameValue));
        list.add(new BasicNameValuePair("id", idValue));
        loadResult(webView, cb, list);
    }

    // Send the result back to JS

    private static void loadResult(WebView webView, String cb, List<BasicNameValuePair> paramsList) {
        StringBuilder params = new StringBuilder();
        params.append("cb=").append(cb != null ? cb : "-1");
        if (paramsList != null) {
            for (BasicNameValuePair pair : paramsList) {
                if ((pair.getName() != null) && (pair.getValue() != null)) {
                    params.append("&").append(pair.getName())
                            .append("=").append(Uri.encode(pair.getValue()));
                }
            }
        }
        String url = String.format("javascript:window.sdkjs.client.result(\"%s\")", params.toString());
        webView.loadUrl(url);
    }
}
