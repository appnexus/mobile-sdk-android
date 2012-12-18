package com.appnexus.opensdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
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

	private void readAttributesFromXML(Context context, AttributeSet attrs) {

		TypedArray a = context
				.obtainStyledAttributes(attrs, R.styleable.AdView);

		final int N = a.getIndexCount();
		for (int i = 0; i < N; ++i) {
			int attr = a.getIndex(i);
			switch (attr) {
			case R.styleable.AdView_placement_id:
				Settings.getSettings().placement_id = a.getString(attr);
				Log.d("OPENSDK", "PLACEMENT="
						+ Settings.getSettings().placement_id);
				break;
			case R.styleable.AdView_app_id:
				Settings.getSettings().app_id = a.getString(attr);
				Log.d("OPENSDK", "APPID=" + Settings.getSettings().app_id);
				break;
			case R.styleable.AdView_refresh_rate_ms:
				Settings.getSettings().refresh_rate_ms = a.getInt(attr,
						60 * 1000);
				break;
			case R.styleable.AdView_test:
				Settings.getSettings().test_mode = a.getBoolean(attr, false);
				break;
			}
		}
		a.recycle();
	}

	private void setup(Context context) {
		// Determine if this is the first launch.
		String pack = context.getPackageName();
		Log.d("OPENSDK", "Package name is "+pack);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context); 
				//context.getSharedPreferences(
				//pack, Context.MODE_PRIVATE);
		boolean firstUse = prefs.getBoolean("opensdk_first_launch", true);
		if(firstUse){
			Settings.getSettings().first_launch = true;
			prefs.edit().putBoolean("opensdk_first_launch", false).commit();
		}else{
			Settings.getSettings().first_launch = false;
		}

	}

	public AdView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setup(context);
		readAttributesFromXML(context, attrs);
		mAdWebView = new AdWebView(context);
	}

	private AdView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setup(context);
		readAttributesFromXML(context, attrs);
		mAdWebView = new AdWebView(context);
	}

	// TODO programmatic creation
	public AdView(Context context, String app_id, String placement_id) {
		super(context);
		setup(context);
		mAdWebView = new AdWebView(context);
		Settings.getSettings().placement_id = placement_id;
		Settings.getSettings().app_id = app_id;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (this.getChildCount() == 0)
			this.addView(mAdWebView);
	}
}
