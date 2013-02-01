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
	private boolean shouldReloadOnResume=false;
	
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
		// Store self.context in the settings for errors
		Clog.error_context = this.getContext();
		
		Clog.d(Clog.publicFunctionsLogTag, Clog.getString(R.string.new_adview));
		// Determine if this is the first launch.
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		if (prefs.getBoolean("opensdk_first_launch", true)) {
			// This is the first launch, store a value to remember
			Clog.v(Clog.baseLogTag,Clog.getString(R.string.first_opensdk_launch));
			Settings.getSettings().first_launch = true;
			prefs.edit().putBoolean("opensdk_first_launch", false).commit();
		} else {
			// Found the stored value, this is NOT the first launch
			Clog.v(Clog.baseLogTag,Clog.getString(R.string.not_first_opensdk_launch));
			Settings.getSettings().first_launch = false;
		}

		// Load user variables only if attrs isn't null
		if(attrs!=null) loadVariablesFromXML(context, attrs);

		// Hide the layout until an ad is loaded
		//hide();

		// Store the UA in the settings
		Settings.getSettings().ua = new WebView(context).getSettings()
				.getUserAgentString();
		Clog.v(Clog.baseLogTag, Clog.getString(R.string.ua, Settings.getSettings().ua));

		// Store the AppID in the settings
		Settings.getSettings().app_id = context.getApplicationContext()
				.getPackageName();
		Clog.v(Clog.baseLogTag, Clog.getString(R.string.appid, Settings.getSettings().app_id));
		
		Clog.v(Clog.baseLogTag, Clog.getString(R.string.making_adman));
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
				Clog.e(Clog.baseLogTag, Clog.getString(R.string.adsize_too_big, measuredWidth, measuredHeight, width, height));
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
			// Start the ad pass if auto is enabled
			if(this.auto_refresh){
				if(!receiverRegistered){
					setupBroadcast(getContext());
					receiverRegistered=true;
				}
				start();
			}
		}
		if(running){
			if(!receiverRegistered){
				setupBroadcast(getContext());
				receiverRegistered=true;
			}
			start();
		}
	}
	
	public void loadAd(){
		if(this.getWindowVisibility()==VISIBLE){
			stop();
			start();
		}else{
			running=true;
		}
	}
	
	public void loadAd(String placementID, int width, int height){
		this.setAdHeight(height);
		this.setAdWidth(width);
		this.setPlacementID(placementID);
		loadAd();
	}

	private void setupBroadcast(Context context) {
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
					stop();
					Clog.d(Clog.baseLogTag, Clog.getString(R.string.screen_off_stop));
				} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
					if(auto_refresh)
						start(); //TODO unpause
					else if (shouldReloadOnResume)
						start();
					Clog.d(Clog.baseLogTag, Clog.getString(R.string.screen_on_start));
				}// TODO: Airplane mode

			}

		};
		context.registerReceiver(receiver, filter);
	}
	
	private void dismantleBroadcast(){
		getContext().unregisterReceiver(receiver);
	}

	public void start() {
		Clog.d(Clog.publicFunctionsLogTag, Clog.getString(R.string.start));
		mAdFetcher.start();
		running=true;
	}

	public void stop() {
		Clog.d(Clog.publicFunctionsLogTag, Clog.getString(R.string.stop));
		mAdFetcher.stop();
		running=false;
	}

	private void loadVariablesFromXML(Context context, AttributeSet attrs) {
		TypedArray a = context
				.obtainStyledAttributes(attrs, R.styleable.AdView);

		final int N = a.getIndexCount();
		Clog.v(Clog.xmlLogTag, Clog.getString(R.string.found_n_in_xml, N));
		for (int i = 0; i < N; ++i) {
			int attr = a.getIndex(i);
			switch (attr) {
			case R.styleable.AdView_placement_id:
				setPlacementID(a.getString(attr));
				Clog.d(Clog.xmlLogTag, Clog.getString(R.string.placement_id, this.placementID));
				break;
			case R.styleable.AdView_auto_refresh_interval:
				setAutoRefreshInterval(a.getInt(attr, 60 * 1000));
				Clog.d(Clog.xmlLogTag, Clog.getString(R.string.xml_set_period, period));
				break;
			case R.styleable.AdView_test:
				Settings.getSettings().test_mode = a.getBoolean(attr, false);
				Clog.d(Clog.xmlLogTag, Clog.getString(R.string.xml_set_test, Settings.getSettings().test_mode));
				break;
			case R.styleable.AdView_auto_refresh:
				setAutoRefresh(a.getBoolean(attr, false));
				Clog.d(Clog.xmlLogTag, Clog.getString(R.string.xml_set_auto_refresh, auto_refresh));
				break;
			case R.styleable.AdView_width:
				setAdWidth(a.getInt(attr, -1));
				Clog.d(Clog.xmlLogTag, Clog.getString(R.string.xml_ad_width, width));
				break;
			case R.styleable.AdView_height:
				setAdHeight(a.getInt(attr, -1));
				Clog.d(Clog.xmlLogTag, Clog.getString(R.string.xml_ad_height, height));
				break;
			case R.styleable.AdView_should_reload_on_resume:
				setShouldReloadOnResume(a.getBoolean(attr, false));
				Clog.d(Clog.xmlLogTag, Clog.getString(R.string.xml_set_should_reload, shouldReloadOnResume));
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
		if (getVisibility() != VISIBLE){
			this.requesting_visible = true;
			setVisibility(VISIBLE);
		}
	}

	protected void hide() {
		if (getVisibility() != GONE)
			setVisibility(GONE);
	}

	public int getAutoRefreshInterval() {
		Clog.d(Clog.publicFunctionsLogTag, Clog.getString(R.string.get_period, period));
		return period;
	}

	public void setAutoRefreshInterval(int period) {
		Clog.d(Clog.publicFunctionsLogTag, Clog.getString(R.string.set_period, period));
		this.period = period;
	}

	public boolean getAutoRefresh() {
		Clog.d(Clog.publicFunctionsLogTag, Clog.getString(R.string.get_auto_refresh, auto_refresh));
		return auto_refresh;
	}

	public void setAutoRefresh(boolean auto_refresh) {
		Clog.d(Clog.publicFunctionsLogTag, Clog.getString(R.string.set_auto_refresh, auto_refresh));
		this.auto_refresh = auto_refresh;
		if(!running){
			running=true;
			start();
		}
	}

	public String getPlacementID() {
		Clog.d(Clog.publicFunctionsLogTag, Clog.getString(R.string.get_placement_id, placementID));
		return placementID;
	}

	public void setPlacementID(String placementID) {
		Clog.d(Clog.publicFunctionsLogTag, Clog.getString(R.string.set_placement_id, placementID));
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
	
	private boolean requesting_visible=false;
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
			Clog.d(Clog.baseLogTag, Clog.getString(R.string.unhidden));
			if (mAdFetcher != null && running && shouldReloadOnResume && !requesting_visible)
				mAdFetcher.start();
			else{
				//Were' not displaying the adview, the system is
				requesting_visible=false;
			}
		} else {
			//Unregister the receiver to prevent a leak.
			if(receiverRegistered){
				dismantleBroadcast();
				receiverRegistered=false;
			}
			Clog.d(Clog.baseLogTag, Clog.getString(R.string.hidden));
			if (mAdFetcher != null && running){
				mAdFetcher.stop();
				running = false;
			}
		}
	}
	
	public void setAdHeight(int h){
		Clog.d(Clog.baseLogTag, Clog.getString(R.string.set_height, h));
		height=h;
	}
	
	public void setAdWidth(int w){
		Clog.d(Clog.baseLogTag, Clog.getString(R.string.set_width, w));
		width=w;
	}
	
	public int getAdHeight(){
		Clog.d(Clog.baseLogTag, Clog.getString(R.string.get_height, height));
		return height;
	}
	
	public int getAdWidth(){
		Clog.d(Clog.baseLogTag, Clog.getString(R.string.get_width, width));
		return width;
	}
	
	protected int getContainerWidth(){
		return measuredWidth;
	}
	
	protected int getContainerHeight(){
		return measuredHeight;
	}

	@SuppressWarnings("unused")
	private boolean getShouldReloadOnResume() {
		Clog.d(Clog.publicFunctionsLogTag, Clog.getString(R.string.get_should_resume, shouldReloadOnResume));
		return shouldReloadOnResume;
	}

	private void setShouldReloadOnResume(boolean shouldReloadOnResume) {
		Clog.d(Clog.publicFunctionsLogTag, Clog.getString(R.string.set_should_resume, shouldReloadOnResume));
		this.shouldReloadOnResume = shouldReloadOnResume;
	}
	
}
