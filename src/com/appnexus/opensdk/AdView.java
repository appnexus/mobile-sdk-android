package com.appnexus.opensdk;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Main view container for all ads. This class will be instantiated by the
 * developer to automatically retrieve ads from the server and display them on
 * the device.
 * <p>
 * This view container will contain other views including ViewPagers for
 * animations between ads, or just AdWebViews.
 * 
 * @author jshufro@appnexus.com
 * 
 */
public class AdView extends FrameLayout {

	private AdWebView mAdWebView;
	private String mPublisherID = "";
	private String mAdID = "";

	private AdView(Context context) {
		super(context);
		mAdWebView = new AdWebView(context, mPublisherID, mAdID);
	}
	
	private void readAttributesFromXML(Context context, AttributeSet attrs){
		
		TypedArray a = context.obtainStyledAttributes(attrs,
			    R.styleable.AdView);
		
		final int N = a.getIndexCount();
		for (int i = 0; i < N; ++i)
		{
		    int attr = a.getIndex(i);
		    switch (attr)
		    {
		        case R.styleable.AdView_ad_id:
		            Settings.getSettings().ad_id = a.getString(attr);
		            break;
		        case R.styleable.AdView_publisher_id:
		            Settings.getSettings().publisher_id = a.getString(attr);
		            break;
		    }
		}
		a.recycle();

	}

	private AdView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mAdWebView = new AdWebView(context, mPublisherID, mAdID);
		readAttributesFromXML(context, attrs);
	}

	private AdView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mAdWebView = new AdWebView(context, mPublisherID, mAdID);
		readAttributesFromXML(context, attrs);
	}

	public AdView(Context context, String publisher_id, String ad_id) {
		super(context);
		mAdWebView = new AdWebView(context, publisher_id, ad_id);
		mPublisherID = publisher_id;
		mAdID = ad_id;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		this.addView(mAdWebView);
		mAdWebView.startServing();

	}
}
