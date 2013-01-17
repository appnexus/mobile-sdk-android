/**
 * 
 */
package com.appnexus.opensdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
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
public class AdWebView extends WebView implements Displayable {
	private boolean failed=false;
	/**
	 * @param context
	 */
	protected AdWebView(Context context) {
		super(context);
		setup();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param attrs
	 */
	protected AdWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setup();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 *            The context for the AdWebView. Should be in an AdView
	 * @param attrs
	 * @param defStyle
	 */
	protected AdWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setup();
		// TODO Auto-generated constructor stub
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
		if(ad.getBody()==null){
			fail();
			return;
		}
		
		String body = "<html><head /><body style='margin:0;padding:0;'>"
				+ ad.getBody() + "</body></html>";
		this.loadData(body, "text/html", "UTF-8");
		
		FrameLayout.LayoutParams resize = new FrameLayout.LayoutParams(this.getLayoutParams());
		
		// WHY DO I MULTIPLY THESE BY 2? Because for some reason I have to, that's why.
		resize.height=ad.getHeight()*2;
		resize.width=ad.getWidth()*2;
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
