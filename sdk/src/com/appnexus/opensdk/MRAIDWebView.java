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
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;

@SuppressLint("ViewConstructor")
public class MRAIDWebView extends WebView implements Displayable {
	private MRAIDImplementation implementation;
	private boolean failed = false;
	protected AdView owner;
	private int default_width;
	private int default_height;

	public MRAIDWebView(AdView owner) {
		super(owner.getContext());
		this.owner = owner;
		setup();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void setup() {
		this.getSettings().setJavaScriptEnabled(true);
		this.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		// this.setInitialScale(100);
		this.getSettings().setPluginState(WebSettings.PluginState.ON);
		this.getSettings().setBuiltInZoomControls(false);
		this.getSettings().setLightTouchEnabled(false);
		this.getSettings().setLoadsImagesAutomatically(true);
		this.getSettings().setSupportZoom(false);
		this.getSettings().setUseWideViewPort(false);
		this.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		setHorizontalScrollbarOverlay(false);
		setHorizontalScrollBarEnabled(false);
		setVerticalScrollbarOverlay(false);
		setVerticalScrollBarEnabled(false);

		setBackgroundColor(Color.TRANSPARENT);
		setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
	}

	protected void setImplementation(MRAIDImplementation imp) {
		this.implementation = imp;
		this.setWebViewClient(imp.getWebViewClient());
		this.setWebChromeClient(imp.getWebChromeClient());
	}

	protected MRAIDImplementation getImplementation() {
		return implementation;
	}

	public void loadAd(AdResponse ar) {
		String html = ar.getBody();

		if (html.contains("mraid.js")) {
			setImplementation(new MRAIDImplementation(this));
		}

		if (implementation != null) {
			html = implementation.onPreLoadContent(this, html);
		}

		final float scale = owner.getContext().getResources()
				.getDisplayMetrics().density;
		int rheight = (int) (ar.getHeight() * scale + 0.5f);
		int rwidth = (int) (ar.getWidth() * scale + 0.5f);
		int rgravity = Gravity.CENTER;
		AdView.LayoutParams resize = new AdView.LayoutParams(rwidth, rheight,
				rgravity);
		this.setLayoutParams(resize);

		this.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
	}

	@Override
	public void onVisibilityChanged(View view, int visibility) {
		if (visibility == View.VISIBLE) {
			if (implementation != null) {
				implementation.onVisible();
			}
		} else {
			if (implementation != null) {
				implementation.onInvisible();
			}
		}
	}

	// w,h in dips. this function converts to pixels
	protected void expand(int w, int h, boolean cust_close,
			MRAIDImplementation caller) {
		DisplayMetrics metrics = new DisplayMetrics();
		((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay().getMetrics(metrics);
		h = (int) (h * metrics.density + 0.5);
		w = (int) (w * metrics.density + 0.5);

		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
				this.getLayoutParams());
		default_width = lp.width;
		default_height = lp.height;
		lp.height = h;
		lp.width = w;
		lp.gravity = Gravity.CENTER;

		if (owner != null) {
			owner.expand(w, h, cust_close, caller);
		}
		
		//If it's an IAV, prevent it from closing
		if(owner instanceof InterstitialAdView){
			((InterstitialAdView)owner).interacted();
		}

		this.setLayoutParams(lp);
	}

	protected void hide() {
		owner.hide();
	}

	protected void show() {
		if (owner != null) {
			owner.expand(default_width, default_height, true, null);
		}
	}

	protected void close() {
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
				this.getLayoutParams());
		lp.height = default_height;
		lp.width = default_width;
		lp.gravity = Gravity.CENTER;

		if (owner != null) {
			owner.expand(default_width, default_height, true, null);
		}

		this.setLayoutParams(lp);
	}

	@Override
	public View getView() {
		return this;
	}

	@Override
	public boolean failed() {
		return failed;
	}

}
