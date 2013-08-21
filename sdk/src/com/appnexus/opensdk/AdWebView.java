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

import com.appnexus.opensdk.AdView.BrowserStyle;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.SslErrorHandler;
import android.net.http.SslError;

/**
 * @author jshufro@appnexus.com
 * 
 */
@SuppressLint("ViewConstructor")
// This will only be constructed by AdFetcher.
public class AdWebView extends WebView implements Displayable {
	private boolean failed = false;
	private AdView destination;

	public AdWebView(AdView owner) {
		super(owner.getContext());
		destination = owner;
		setup();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void setup() {
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

		setWebChromeClient(new VideoEnabledWebChromeClient((Activity) destination.getContext()));

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

				if (destination.getOpensNativeBrowser()) {
					Clog.d(Clog.baseLogTag,
							Clog.getString(R.string.opening_native));
					Intent intent = new Intent(Intent.ACTION_VIEW,
							Uri.parse(url));
					getContext().startActivity(intent);
				} else {
					Clog.d(Clog.baseLogTag,
							Clog.getString(R.string.opening_inapp));
					Intent intent = new Intent(destination.getContext(),
							BrowserActivity.class);
					intent.putExtra("url", url);
					if (destination.getBrowserStyle() != null) {
						String i = "" + this.hashCode();
						intent.putExtra("bridgeid", i);
						AdView.BrowserStyle.bridge
								.add(new Pair<String, BrowserStyle>(i,
										destination.getBrowserStyle()));
					}
					destination.getContext().startActivity(intent);
				}
				
				AdWebView.this.destination.adListener.onAdClicked(AdWebView.this.destination);
				//If it's an IAV, prevent it from closing
		                if (AdWebView.this.destination instanceof InterstitialAdView) {
					InterstitialAdView iav = (InterstitialAdView)AdWebView.this.destination;
					if(iav!=null){
						iav.interacted();
					}
				}
				
				return true;
			}

		});

	}

	public void loadAd(AdResponse ad) {
		if (ad.getBody().equals("")) {
			fail();
			return;
		}

		String body = "<html><head /><body style='margin:0;padding:0;'>"
				+ ad.getBody() + "</body></html>";
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
