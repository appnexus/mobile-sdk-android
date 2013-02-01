package com.appnexus.opensdk;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;

/**
 * @author jshufro@appnexus.com
 * 
 */
@SuppressLint("ViewConstructor") //This will only be constructed by AdFetcher.
public class AdWebView extends WebView implements Displayable {
	private boolean failed=false;
	private AdView destination;

	protected AdWebView(AdView owner) {
		super(owner.getContext());
		destination=owner;
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
		this.getSettings().setUseWideViewPort(true);
		this.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		this.getSettings().setUseWideViewPort(false);
		
		setHorizontalScrollbarOverlay(false);
		setHorizontalScrollBarEnabled(false);
		setVerticalScrollbarOverlay(false);
		setVerticalScrollBarEnabled(false);

		setBackgroundColor(Color.TRANSPARENT);
		setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);

		setOnTouchListener(new OnTouchListener() {

			// Disables all dragging in the webview. Might interfere with
			// interactive creatives. TODO?
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return (event.getAction() == MotionEvent.ACTION_MOVE);
			}
		});
		
	}
	
	@Override
	public void flingScroll(int vx, int vy){
		return;
	}

	protected void loadAd(AdResponse ad) {
		if(ad.getBody()==""){
			fail();
			return;
		}

		String body = "<html><head /><body style='margin:0;padding:0;'>"
				+ ad.getBody() + "</body></html>";
		Clog.v(Clog.baseLogTag, Clog.getString(R.string.webview_loading, body));
		this.loadData(body, "text/html", "UTF-8");
		
		FrameLayout.LayoutParams resize = new FrameLayout.LayoutParams(destination.getLayoutParams());
		
		final float scale = destination.getContext().getResources().getDisplayMetrics().density;
		resize.height = (int)(ad.getHeight()*scale+0.5f);
		resize.width = (int)(ad.getWidth()*scale+0.5f);
		resize.gravity=Gravity.CENTER;
		
		this.setLayoutParams(resize);
	}

	@Override
	public View getView() {
		return this;
	}

	@Override
	public void fail() {
		failed=true;
	}

	@Override
	public boolean failed() {
		return failed;
	}
}
