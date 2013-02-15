package com.appnexus.opensdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.util.AttributeSet;

public class BannerAdView extends AdView {

	private int period;
	private boolean auto_refresh;
	private boolean running;
	private boolean shouldReloadOnResume;
	private BroadcastReceiver receiver;
	private boolean receiverRegistered;
	
	private void setDefaultsBeforeXML(){
		running=false;
		auto_refresh=false;
		shouldReloadOnResume=false;
		receiverRegistered=false;
	}
	
	public BannerAdView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public BannerAdView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public BannerAdView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public BannerAdView(Context context, String placement_id) {
		super(context, placement_id);
		// TODO Auto-generated constructor stub
	}

	public BannerAdView(Context context, String placement_id, int ad_width,
			int ad_height) {
		super(context, placement_id, ad_width, ad_height);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void setup(Context context, AttributeSet attrs) {
		super.setup(context, attrs);
		mAdFetcher.setPeriod(period);
		mAdFetcher.setAutoRefresh(getAutoRefresh());
	}

	protected void setupBroadcast(Context context) {
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
					stop();
					Clog.d(Clog.baseLogTag,
							Clog.getString(R.string.screen_off_stop));
				} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
					if (auto_refresh)
						start();
					else if (shouldReloadOnResume)
						stop();
						start();
					Clog.d(Clog.baseLogTag,
							Clog.getString(R.string.screen_on_start));
				}// TODO: Airplane mode

			}

		};
		context.registerReceiver(receiver, filter);
	}

	@Override
	public final void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		// Are we coming back from a screen/user presence change?
		if (running) {
			if (!receiverRegistered) {
				setupBroadcast(getContext());
				receiverRegistered = true;
			}
			start();
		}

	}

	// Make sure receive is registered.
	@Override
	protected void onFirstLayout() {
		super.onFirstLayout();
		if (this.auto_refresh) {
			if (!receiverRegistered) {
				setupBroadcast(getContext());
				receiverRegistered = true;
			}
		}
	}

	@Override
	public void loadAd() {
		if (getVisibility() != VISIBLE)
			running=true; // Load the ad when presence changes back
	}

	protected void start() {
		Clog.d(Clog.publicFunctionsLogTag, Clog.getString(R.string.start));
		mAdFetcher.start();
		running=true;
	}

	protected void stop() {
		Clog.d(Clog.publicFunctionsLogTag, Clog.getString(R.string.stop));
		mAdFetcher.stop();
		running=false;
	}

	@Override
	protected void loadVariablesFromXML(Context context, AttributeSet attrs) {
		//Defaults
		setDefaultsBeforeXML();
		
		TypedArray a = context
				.obtainStyledAttributes(attrs, R.styleable.BannerAdView);

		final int N = a.getIndexCount();
		Clog.v(Clog.xmlLogTag, Clog.getString(R.string.found_n_in_xml, N));
		for (int i = 0; i < N; ++i) {
			int attr = a.getIndex(i);
			switch (attr) {
			case R.styleable.BannerAdView_placement_id:
				setPlacementID(a.getString(attr));
				Clog.d(Clog.xmlLogTag,
						Clog.getString(R.string.placement_id, a.getString(attr)));
				break;
			case R.styleable.BannerAdView_auto_refresh_interval:
				setAutoRefreshInterval(a.getInt(attr, 60 * 1000));
				Clog.d(Clog.xmlLogTag,
						Clog.getString(R.string.xml_set_period, period));
				break;
			case R.styleable.BannerAdView_test:
				Settings.getSettings().test_mode = a.getBoolean(attr, false);
				Clog.d(Clog.xmlLogTag,
						Clog.getString(R.string.xml_set_test,
								Settings.getSettings().test_mode));
				break;
			case R.styleable.BannerAdView_auto_refresh:
				setAutoRefresh(a.getBoolean(attr, false));
				Clog.d(Clog.xmlLogTag, Clog.getString(
						R.string.xml_set_auto_refresh, auto_refresh));
				break;
			case R.styleable.BannerAdView_width:
				setAdWidth(a.getInt(attr, -1));
				Clog.d(Clog.xmlLogTag,
						Clog.getString(R.string.xml_ad_width, a.getInt(attr, -1)));
				break;
			case R.styleable.BannerAdView_height:
				setAdHeight(a.getInt(attr, -1));
				Clog.d(Clog.xmlLogTag,
						Clog.getString(R.string.xml_ad_height, a.getInt(attr, -1)));
				break;
			case R.styleable.BannerAdView_should_reload_on_resume:
				setShouldReloadOnResume(a.getBoolean(attr, false));
				Clog.d(Clog.xmlLogTag, Clog.getString(
						R.string.xml_set_should_reload, shouldReloadOnResume));
			}
		}
		a.recycle();
	}

	public int getAutoRefreshInterval() {
		Clog.d(Clog.publicFunctionsLogTag,
				Clog.getString(R.string.get_period, period));
		return period;
	}

	public void setAutoRefreshInterval(int period) {
		Clog.d(Clog.publicFunctionsLogTag,
				Clog.getString(R.string.set_period, period));
		this.period = period;
		if(mAdFetcher!=null) mAdFetcher.setPeriod(period);
	}

	public boolean getAutoRefresh() {
		Clog.d(Clog.publicFunctionsLogTag,
				Clog.getString(R.string.get_auto_refresh, auto_refresh));
		return auto_refresh;
	}

	public void setAutoRefresh(boolean auto_refresh) {
		Clog.d(Clog.publicFunctionsLogTag,
				Clog.getString(R.string.set_auto_refresh, auto_refresh));
		this.auto_refresh = auto_refresh;
		if(mAdFetcher!=null) mAdFetcher.setAutoRefresh(auto_refresh);
		if (!running && mAdFetcher!=null) {
			start();
		}
	}
	
	public boolean getShouldReloadOnResume() {
		Clog.d(Clog.publicFunctionsLogTag, Clog.getString(R.string.get_should_resume, shouldReloadOnResume));
		return shouldReloadOnResume;
	}

	public void setShouldReloadOnResume(boolean shouldReloadOnResume) {
		Clog.d(Clog.publicFunctionsLogTag, Clog.getString(R.string.set_should_resume, shouldReloadOnResume));
		this.shouldReloadOnResume = shouldReloadOnResume;
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
			if (mAdFetcher != null && (!requesting_visible || running || shouldReloadOnResume || auto_refresh))
				start();
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
				stop();
			}
		}
	}
	private void dismantleBroadcast(){
		getContext().unregisterReceiver(receiver);
	}
	
	public void startAutoRefresh(){
		this.setAutoRefresh(true);
	}
	
	public void startAutoRefresh(int interval){
		this.setAutoRefreshInterval(interval);
		this.startAutoRefresh();
	}
	
	@Override
	protected void unhide(){
		super.unhide();
		this.requesting_visible = true;
	}
}
