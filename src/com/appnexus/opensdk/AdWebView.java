/**
 * 
 */
package com.appnexus.opensdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

/**
 * @author jshufro@appnexus.com
 *
 */
public class AdWebView extends WebView implements Displayable{
	private AdView parent; //Do we need this?
	
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
	 */
	protected AdWebView(Context context, AdView parent) {
		super(context);
		this.parent=parent;
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
		this.getSettings().setBuiltInZoomControls(false);
		this.getSettings().setLightTouchEnabled(false);
		this.getSettings().setLoadsImagesAutomatically(true);
		this.getSettings().setSupportZoom(false);
		
		/*this.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Toast.makeText(getContext(), "Touchy touchy", Toast.LENGTH_SHORT).show();
				return true;
			}
		});*/
	}
	
	protected void loadAd(Ad ad){
		this.loadData(ad.getBody(), "text/html", "UTF-8");
	}

	@Override
	public View getView() {
		return this;
	}

	

}
