package com.appnexus.opensdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

public class AdView extends FrameLayout {

	private AdWebView mAdWebView;
	private AdFetcher mAdFetcher;
	/** Begin Construction **/
	
	public AdView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setup(context, attrs);
		
	}

	public AdView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setup(context, attrs);
	}

	private void setup(Context context, AttributeSet attrs) {
		Settings.getSettings().context=context;
		
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

		
		//Make an AdFetcher
		mAdWebView = new AdWebView(context, this);
		mAdFetcher=new AdFetcher(context, mAdWebView);
		
		mAdFetcher.start();
		
		//Hide the layout until an ad is loaded
		hide();
		
	}

	/** End Construction **/
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (this.getChildCount() == 0)
			this.addView(mAdWebView);
	}
	
	protected void show(){
		setVisibility(VISIBLE);
	}
	
	protected void hide(){
		//setVisibility(GONE);
	}
}
