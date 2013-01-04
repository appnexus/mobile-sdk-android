/**
 * 
 */
package com.appnexus.opensdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * @author jshufro@appnexus.com
 *
 */
public class AdWebView extends WebView {
	
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
	}
	
	protected void loadAd(Ad ad){
		this.loadData(ad.getBody(), "text/html", "UTF-8");
	}

}
