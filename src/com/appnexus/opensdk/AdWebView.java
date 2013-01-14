/**
 * 
 */
package com.appnexus.opensdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * @author jshufro@appnexus.com
 *
 */
public class AdWebView extends WebView implements Displayable{
	
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
	 * @param context The context for the AdWebView. Should be in an AdView
	 * @param attrs 
	 * @param defStyle
	 */
	protected AdWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setup();
		// TODO Auto-generated constructor stub
	}	
	
	@SuppressLint("SetJavaScriptEnabled")
	private void setup(){
		Settings.getSettings().ua=this.getSettings().getUserAgentString();
		this.getSettings().setJavaScriptEnabled(true);
		this.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		this.getSettings().setPluginState(WebSettings.PluginState.ON);
		this.getSettings().setBuiltInZoomControls(false);
		this.getSettings().setLightTouchEnabled(false);
		this.getSettings().setLoadsImagesAutomatically(true);
		this.getSettings().setSupportZoom(false);
		this.getSettings().setUseWideViewPort(true);
		
        setHorizontalScrollbarOverlay(false);
        setVerticalScrollbarOverlay(false);
        
        setBackgroundColor(Color.TRANSPARENT);
		
		/*this.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Toast.makeText(getContext(), "Touchy touchy", Toast.LENGTH_SHORT).show();
				return true;
			}
		});*/
	}
	
	protected void loadAd(AdResponse ad){
		this.loadData(ad.getBody(), "text/html", "UTF-8");
	}

	@Override
	public View getView() {
		return this;
	}

	

}
