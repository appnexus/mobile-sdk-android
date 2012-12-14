package com.appnexus.opensdk;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Main view container for all ads.
 * This class will be instantiated by the developer
 * to automatically retrieve ads from the server and
 * display them on the device.
 * <p>
 * This view container will contain other views
 * including ViewPagers for animations between
 * ads, or just AdWebViews.
 * 
 * @author jshufro@appnexus.com
 *
 */
public class AdView extends ViewGroup {
	
	private AdWebView mAdWebView;
	private String mPublisherID="";
	private String mAdID="";
	
	
	private AdView(Context context) {
		super(context);
		mAdWebView=new AdWebView(context, mPublisherID, mAdID);
	}

	private AdView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mAdWebView=new AdWebView(context, mPublisherID, mAdID);
		// TODO Auto-generated constructor stub
	}

	private AdView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mAdWebView=new AdWebView(context, mPublisherID, mAdID);
		// TODO Auto-generated constructor stub
	}
	
	public AdView(Context context, String publisher_id, String ad_id){
		super(context);
		mAdWebView=new AdWebView(context, publisher_id, ad_id);
		mPublisherID=publisher_id;
		mAdID=ad_id;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		this.addView(mAdWebView);
		mAdWebView.startServing();

	}

}
