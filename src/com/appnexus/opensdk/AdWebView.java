/**
 * 
 */
package com.appnexus.opensdk;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * @author jshufro@appnexus.com
 *
 */
public class AdWebView extends WebView {

	private AdFetcher mAdFetcher = null;
	
	/**
	 * @param context
	 */
	protected AdWebView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param attrs
	 */
	protected AdWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context The context for the AdWebView. Should be in an AdView
	 * @param attrs 
	 * @param defStyle
	 */
	protected AdWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	public void startServing(){
		
	}
	

}
