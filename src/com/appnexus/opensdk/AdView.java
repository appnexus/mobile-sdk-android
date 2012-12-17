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

	private AdView(Context context) {
		super(context);
		mAdWebView = new AdWebView(context);
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
		        case R.styleable.AdView_placement_id:
		            Settings.getSettings().placement_id = a.getString(attr);
		            break;
		        case R.styleable.AdView_app_id:
		            Settings.getSettings().app_id = a.getString(attr);
		            break;
		    }
		}
		a.recycle();

	}

	private AdView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mAdWebView = new AdWebView(context);
		readAttributesFromXML(context, attrs);
	}

	private AdView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mAdWebView = new AdWebView(context);
		readAttributesFromXML(context, attrs);
	}

	public AdView(Context context, String app_id, String placement_id) {
		super(context);
		mAdWebView = new AdWebView(context);
		Settings.getSettings().placement_id = placement_id;
		Settings.getSettings().app_id = app_id;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		this.addView(mAdWebView);
		mAdWebView.startServing();
	}
}
