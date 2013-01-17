package com.appnexus.opensdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebView;
import android.widget.FrameLayout;

public class AdView extends FrameLayout {

	private AdFetcher mAdFetcher;
	private int period;
	private boolean auto_refresh=true;
	private String placementID;
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
		// Determine if this is the first launch.
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		if (prefs.getBoolean("opensdk_first_launch", true)) {
			//This is the first launch, store a value to remember
			Clog.v("OPENSDK", "This is the first time OpenSDK has been launched in this app.");
			Settings.getSettings().first_launch = true;
			prefs.edit().putBoolean("opensdk_first_launch", false).commit();
		} else {
			//Found the stored value, this is NOT the first launch
			Clog.v("OPENSDK", "This is not the first OpenSDK launch in this app.");
			Settings.getSettings().first_launch = false;
		}
		
		loadVariablesFromXML(context, attrs);
		
		// Hide the layout until an ad is loaded
		hide();
		
		// Store the UA in the settings
		Settings.getSettings().ua=new WebView(context).getSettings().getUserAgentString();

		// Register a broadcast receiver to pause add refresh when the phone is locked
		setupBroadcast(context);
		
		// Make an AdFetcher - Continue the creation pass
		mAdFetcher = new AdFetcher(this);
		mAdFetcher.setPeriod(period);
		mAdFetcher.setAutoRefresh(getAutoRefresh());
		
		// Start the ad pass
		start();
	}
	
	public void setupBroadcast(Context context){
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		BroadcastReceiver receiver = new BroadcastReceiver(){

			@Override
			public void onReceive(Context context, Intent intent) {
				if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
					stop();
					Log.d("OPENSDK", "Stopped ad requests since screen is off");
				}else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
					start();
					Log.d("OPENSDK", "Started ad requests since screen is on");
				}//TODO: Airplane mode
				
			}
			
		};
		context.registerReceiver(receiver, filter);
	}

	public void start(){
		mAdFetcher.start();
	}
	
	public void pause(){
		//huge TODO?
	}
	
	public void stop(){
		mAdFetcher.stop();
	}
	
	private void loadVariablesFromXML(Context context, AttributeSet attrs){
		TypedArray a = context
				.obtainStyledAttributes(attrs, R.styleable.AdView);

		final int N = a.getIndexCount();
		for (int i = 0; i < N; ++i) {
			int attr = a.getIndex(i);
			switch (attr) {
			case R.styleable.AdView_placement_id:
				setPlacementID(a.getString(attr));
				Clog.d("OPENSDK", "PLACEMENT="
						+ getPlacementID());
				break;
			case R.styleable.AdView_app_id:
				Settings.getSettings().app_id = a.getString(attr);
				Clog.d("OPENSDK", "APPID=" + Settings.getSettings().app_id);
				break;
			case R.styleable.AdView_period:
				setPeriod(a.getInt(attr,
						60 * 1000));
				break;
			case R.styleable.AdView_test:
				Settings.getSettings().test_mode = a.getBoolean(attr, false);
				break;
			case R.styleable.AdView_auto_refresh:
				setAutoRefresh(a.getBoolean(attr, true));
				break;
			}
		}
		a.recycle();
	}
	
	/** End Construction **/

	
	protected void display(Displayable d){
		if(d.failed()) return; //The displayable has failed to be parsed or turned into a View.
		this.removeAllViews();
		this.addView(d.getView());
		show();
	}

	protected void show() {
		setVisibility(VISIBLE);
	}

	protected void hide() {
		setVisibility(GONE);
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	public boolean getAutoRefresh() {
		return auto_refresh;
	}

	public void setAutoRefresh(boolean auto_refresh) {
		this.auto_refresh = auto_refresh;
	}

	public String getPlacementID() {
		return placementID;
	}

	public void setPlacementID(String placementID) {
		this.placementID = placementID;
	}
}
