package com.appnexus.opensdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.widget.FrameLayout;

public class AdView extends FrameLayout {

	private AdFetcher mAdFetcher;
	private int period;
	private boolean auto_refresh = false;
	private String placementID;
	private int measuredWidth;
	private int measuredHeight;
	private boolean measured=false;
	private int width=-1;
	private int height=-1;
	private BroadcastReceiver receiver;
	private boolean receiverRegistered=false;
	private boolean running=false;
	
	/** Begin Construction **/

	public AdView(Context context){
		super(context, null);
		setup(context, null);
	}
	
	public AdView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setup(context, attrs);

	}

	public AdView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setup(context, attrs);
	}

	private void setup(Context context, AttributeSet attrs) {
		Clog.d("OPENSDK-INTERFACE", "new AdView()");
		// Determine if this is the first launch.
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		if (prefs.getBoolean("opensdk_first_launch", true)) {
			// This is the first launch, store a value to remember
			Clog.v("OPENSDK",
					"This is the first time OpenSDK has been launched in this app.");
			Settings.getSettings().first_launch = true;
			prefs.edit().putBoolean("opensdk_first_launch", false).commit();
		} else {
			// Found the stored value, this is NOT the first launch
			Clog.v("OPENSDK",
					"This is not the first OpenSDK launch in this app.");
			Settings.getSettings().first_launch = false;
		}

		// Load user variables only if attrs isn't null
		if(attrs!=null) loadVariablesFromXML(context, attrs);

		// Hide the layout until an ad is loaded
		//hide();

		// Store the UA in the settings
		Settings.getSettings().ua = new WebView(context).getSettings()
				.getUserAgentString();
		Clog.v("OPENSDK", "Your user-agent string is: "+Settings.getSettings().ua);

		// Store the AppID in the settings
		Settings.getSettings().app_id = context.getApplicationContext()
				.getPackageName();
		Clog.v("OPENSDK", "Saving "+Settings.getSettings().app_id+" as your app-id");

		Clog.v("OPENSDK", "Making an AdManager to begin fetching ads");
		// Make an AdFetcher - Continue the creation pass
		mAdFetcher = new AdFetcher(this);
		mAdFetcher.setPeriod(period);
		mAdFetcher.setAutoRefresh(getAutoRefresh());
		
		//We don't start the ad requesting here, since the view hasn't been sized yet.
	}
	
	@Override
	public final void onLayout(boolean changed, int left, int top, int right, int bottom){
		super.onLayout(changed, left, top, right, bottom);
		if(!measured || changed){
			//Convert to dips
			float density = getContext().getResources().getDisplayMetrics().density;
			measuredWidth = (int)((right - left)/density + 0.5f);
			measuredHeight = (int)((bottom - top)/density + 0.5f);
			if(measuredHeight<height || measuredWidth<width ){
				Clog.e("OPENSDK", "You requested an Ad larger than the measured ad space. Ad space size: "+measuredWidth+"x"+measuredHeight+", request size: "+width+"x"+height);
				//Hide the space, since no ad will be loaded due to error
				hide();
				//Stop any request in progress
				if(mAdFetcher!=null) mAdFetcher.stop();
				//Returning here allows the SDK to re-request when the layout next changes, and maybe the error will be amended. 
				return;
			}
			measured = true;
			
			// Hide the adview
			hide();
			// Start the ad pass
			start();
		}
	}

	private void setupBroadcast(Context context) {
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
					stop();
					Clog.d("OPENSDK", "Stopped ad requests since screen is off");
				} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
					start();
					Clog.d("OPENSDK", "Started ad requests since screen is on");
				}// TODO: Airplane mode

			}

		};
		context.registerReceiver(receiver, filter);
	}
	
	private void dismantleBroadcast(){
		getContext().unregisterReceiver(receiver);
	}

	public void start() {
		Clog.d("OPENSDK-INTERFACE", "start()");
		mAdFetcher.start();
		running=true;
	}

	public void pause() {
		// huge TODO?
	}

	public void stop() {
		Clog.d("OPENSDK-INTERFACE", "stop()");
		mAdFetcher.stop();
		running=false;
	}

	private void loadVariablesFromXML(Context context, AttributeSet attrs) {
		TypedArray a = context
				.obtainStyledAttributes(attrs, R.styleable.AdView);

		final int N = a.getIndexCount();
		Clog.v("OPENSDK-XML", "Found " + N + " variables to read from xml");
		for (int i = 0; i < N; ++i) {
			int attr = a.getIndex(i);
			switch (attr) {
			case R.styleable.AdView_placement_id:
				setPlacementID(a.getString(attr));
				Clog.d("OPENSDK-XML", "Placement id=" + getPlacementID());
				break;
			case R.styleable.AdView_period:
				setPeriod(a.getInt(attr, 60 * 1000));
				Clog.d("OPENSDK-XML", "Period set to "+getPeriod()+"ms in xml");
				break;
			case R.styleable.AdView_test:
				Settings.getSettings().test_mode = a.getBoolean(attr, false);
				Clog.d("OPENSDK-XML", "Test mode set to "+Settings.getSettings().test_mode+" in xml");
				break;
			case R.styleable.AdView_auto_refresh:
				setAutoRefresh(a.getBoolean(attr, false));
				Clog.d("OPENSDK-XML", "Auto-refresh is set to "+getAutoRefresh()+" in xml");
				break;
			case R.styleable.AdView_width:
				setAdWidth(a.getInt(attr, -1));
				Clog.d("OPENSDK-XML", "Ad width set to "+getAdWidth()+" in xml");
				break;
			case R.styleable.AdView_height:
				setAdHeight(a.getInt(attr, -1));
				Clog.d("OPENSDK-XML", "Ad height set to "+getAdWidth()+" in xml");
				break;
			}
		}
		a.recycle();
	}

	/** End Construction **/

	protected void display(Displayable d) {
		if (d.failed())
			return; // The displayable has failed to be parsed or turned into a
					// View.
		this.removeAllViews();
		this.addView(d.getView());
		show();
	}

	protected void show() {
		if (getVisibility() != VISIBLE)
			setVisibility(VISIBLE);
	}

	protected void hide() {
		if (getVisibility() != GONE)
			setVisibility(GONE);
	}

	public int getPeriod() {
		Clog.d("OPENSDK-INTERFACE", "getPeriod() returned:"+period);
		return period;
	}

	public void setPeriod(int period) {
		Clog.d("OPENSDK-INTERFACE", "setPeriod() to:"+period);
		this.period = period;
	}

	public boolean getAutoRefresh() {
		Clog.d("OPENSDK-INTERFACE", "getAutoRefresh() returned:"+auto_refresh);
		return auto_refresh;
	}

	public void setAutoRefresh(boolean auto_refresh) {
		Clog.d("OPENSDK-INTERFACE", "setAutoRefresh() to:"+auto_refresh);
		this.auto_refresh = auto_refresh;
	}

	public String getPlacementID() {
		Clog.d("OPENSDK-INTERFACE", "getPlacementID() returned:"+placementID);
		return placementID;
	}

	public void setPlacementID(String placementID) {
		Clog.d("OPENSDK-INTERFACE", "setPlacementID() to:"+placementID);
		this.placementID = placementID;
	}

	@Override
	protected void finalize() {
		try {
			super.finalize();
		} catch (Throwable e){}
		// Just in case, kill the adfetcher's service
		if (mAdFetcher != null)
			mAdFetcher.stop();
	}

	@Override
	public void onWindowVisibilityChanged(int visibility) {
		super.onWindowVisibilityChanged(visibility);
		if (visibility == VISIBLE) {
			// Register a broadcast receiver to pause add refresh when the phone is
			// locked
			if(!receiverRegistered){
				setupBroadcast(getContext());
				receiverRegistered=true;
			}
			Clog.d("OPENSDK", "The AdView has been unhidden.");
			if (mAdFetcher != null && running)
				mAdFetcher.start();
		} else {
			//Unregister the receiver to prevent a leak.
			if(receiverRegistered){
				dismantleBroadcast();
				receiverRegistered=false;
			}
			Clog.d("OPENSDK", "The AdView has been hidden.");
			if (mAdFetcher != null && running){
				mAdFetcher.stop();
				running = false;
			}
		}
	}
	
	public void setAdHeight(int h){
		Clog.d("OPENSDK-INTERFACE", "setAdHeight() to:"+h);
		height=h;
	}
	
	public void setAdWidth(int w){
		Clog.d("OPENSDK-INTERFACE", "setAdWidth() to:"+w);
		width=w;
	}
	
	public int getAdHeight(){
		Clog.d("OPENSDK-INTERFACE", "getAdHeight() returned:"+height);
		return height;
	}
	
	public int getAdWidth(){
		Clog.d("OPENSDK-INTERFACE", "getAdWidth() returned:"+width);
		return width;
	}
	
	protected int getContainerWidth(){
		return measuredWidth;
	}
	
	protected int getContainerHeight(){
		return measuredHeight;
	}
	
}
