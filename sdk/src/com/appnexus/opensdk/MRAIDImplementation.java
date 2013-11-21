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
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.net.http.SslError;
import android.view.Gravity;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.appnexus.opensdk.utils.Clog;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

@SuppressLint("InlinedApi")
class MRAIDImplementation {
    private final MRAIDWebView owner;
    private boolean readyFired = false;
    boolean expanded = false;
    private boolean hidden = false;
    private int default_width;
    private int default_height;

    public MRAIDImplementation(MRAIDWebView owner) {
        this.owner = owner;
    }

    // The webview about to load the ad, and the html ad content
    String onPreLoadContent(WebView wv, String html) {
        // Check to ensure <html> tags are present
        if (!html.contains("<html>")) {
            html = "<html><head></head><body style='padding:0;margin:0;'>"
                    + html + "</body></html>";
        } else if (!html.contains("<head>")) {
            // The <html> tags are present, but there is no <head> section to
            // inject the mraid js
            html = html.replace("<html>", "<html><head></head>");
        }

        // Insert mraid script source
        html = html.replace("<head>",
                "<head><script>" + getMraidDotJS(wv.getResources())
                        + "</script>");

        return html;
    }

    String getMraidDotJS(Resources r) {
        InputStream ins = r.openRawResource(R.raw.mraid);
        try {
            byte[] buffer = new byte[ins.available()];
            if ( ins.read(buffer) > 0) {
                return new String(buffer, "UTF-8");
            }
        } catch (IOException e) {

        }
        return null;
    }

    protected void onReceivedError(WebView view, int errorCode, String desc,
                                   String failingUrl) {
        Clog.w(Clog.mraidLogTag, Clog.getString(
                R.string.webview_received_error, errorCode, desc, failingUrl));
    }

    WebViewClient getWebViewClient() {

        return new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!url.startsWith("mraid:") && !url.startsWith("javascript:")) {
                	Intent intent;
                    if (owner.owner.getOpensNativeBrowser()) {
                        intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(url));
                    } else {
                        intent = new Intent(owner.getContext(),
                                BrowserActivity.class);
                        intent.putExtra("url", url);
                    }
                    try {
                    	owner.getContext().startActivity(intent);
                    } catch(ActivityNotFoundException e) {
                    	Clog.w(Clog.mraidLogTag, Clog.getString(R.string.opening_url_failed,url));
                    }
                    return true;
                } else if (url.startsWith("mraid://")) {
                    MRAIDImplementation.this.dispatch_mraid_call(url);

                    return true;
                }

                // See if any native activities can handle the Url
                try {
                    owner.getContext().startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    // If it's an IAV, prevent it from closing
                    if (owner.owner instanceof InterstitialAdView) {
                        ((InterstitialAdView) (owner.owner)).interacted();
                    }
                    return true;
                } catch (ActivityNotFoundException e) {
                    return false;
                }
            }

            @Override
            public void onReceivedSslError(WebView view,
                                           SslErrorHandler handler, SslError error) {
                Clog.e(Clog.httpRespLogTag,
                        Clog.getString(R.string.webclient_error,
                                error.getPrimaryError(), error.toString()));
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingURL) {
                Clog.e(Clog.httpRespLogTag, Clog.getString(
                        R.string.webclient_error, errorCode, description));
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // Fire the ready event only once
                if (!readyFired) {
                    view.loadUrl(
                            String.format("javascript:window.mraid.util.setPlacementType('%s')",
                                    owner.owner.isBanner() ? "inline" : "interstitial"));
                    view.loadUrl("javascript:window.mraid.util.setIsViewable(true)");
                    view.loadUrl("javascript:window.mraid.util.stateChangeEvent('default')");
                    view.loadUrl("javascript:window.mraid.util.readyEvent();");

                    // Store width and height for close()
                    default_width = owner.getLayoutParams().width;
                    default_height = owner.getLayoutParams().height;

                    readyFired = true;
                }
            }
        };
    }

	WebChromeClient getWebChromeClient() {
		return new VideoEnabledWebChromeClient((Activity) owner.getContext());
	}

    void onVisible() {
        if (readyFired)
            owner.loadUrl("javascript:window.mraid.util.setIsViewable(true)");

    }

    void onInvisible() {
        if (readyFired)
            owner.loadUrl("javascript:window.mraid.util.setIsViewable(false)");
    }

    void close() {
        if (expanded) {
            AdView.LayoutParams lp = new AdView.LayoutParams(
                    owner.getLayoutParams());
            lp.height = default_height;
            lp.width = default_width;
            lp.gravity = Gravity.CENTER;
            owner.setLayoutParams(lp);
            owner.close();
            this.owner
                    .loadUrl("javascript:window.mraid.util.stateChangeEvent('default');");
            this.owner.owner.adListener.onAdCollapsed(this.owner.owner);

            // Allow orientation changes
            Activity a = ((Activity) this.owner.getContext());
            if (a != null)
                a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            expanded = false;
        } else {
            // state must be default
            owner.hide();
            hidden = true;
        }
    }

    void expand(ArrayList<BasicNameValuePair> parameters) {
        if (!hidden) {
            int width = owner.getLayoutParams().width;// Use current height and
            // width as expansion
            // defaults.
            int height = owner.getLayoutParams().height;
            boolean useCustomClose = false;
            for (BasicNameValuePair bnvp : parameters) {
                if (bnvp.getName().equals("w"))
                    try {
                        width = Integer.parseInt(bnvp.getValue());
                    } catch (NumberFormatException e) {
                        // Do nothing
                    }
                else if (bnvp.getName().equals("h"))
                    try {
                        height = Integer.parseInt(bnvp.getValue());
                    } catch (NumberFormatException e) {
                        // Do nothing
                    }
                else if (bnvp.getName().equals("useCustomClose"))
                    useCustomClose = Boolean.parseBoolean(bnvp.getValue());
            }

            owner.expand(width, height, useCustomClose, this);
            // Fire the stateChange to MRAID
            this.owner
                    .loadUrl("javascript:window.mraid.util.stateChangeEvent('expanded');");
            expanded = true;

            // Fire the AdListener event
            if (this.owner.owner.adListener != null) {
                this.owner.owner.adListener.onAdExpanded(this.owner.owner);
            }

            // Lock the orientation
            AdActivity.lockOrientation((Activity) this.owner.getContext());

        } else {
            owner.show();
            hidden = false;
        }
    }

    void dispatch_mraid_call(String url) {
        // Remove the fake protocol
        url = url.replaceFirst("mraid://", "");

        // Separate the function from the parameters
        String[] qMarkSplit = url.split("\\?");
        String func = qMarkSplit[0].replaceAll("/", "");
        String params;
        ArrayList<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
        if (qMarkSplit.length > 1) {
            params = url.substring(url.indexOf("?") + 1);

            for (String s : params.split("&")) {
                if (s.split("=").length < 2) {
                    continue;
                }
                parameters.add(new BasicNameValuePair(s.split("=")[0], s
                        .split("=")[1]));
            }
        }

        if (func.equals("expand")) {
            expand(parameters);
        } else if (func.equals("close")) {
            close();
        }
    }
}
