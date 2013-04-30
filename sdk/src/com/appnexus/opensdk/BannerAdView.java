package com.appnexus.opensdk;

import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.util.AttributeSet;

/**
 * This view is added to an existing layout in order to display ads.
 * @author Jacob Shufro
 *
 */
public class BannerAdView extends AdView {

	private int period;
	private boolean auto_refresh;
	private boolean running;
	private boolean shouldReloadOnResume;
	private BroadcastReceiver receiver;
	private boolean receiversRegistered;
	
	private void setDefaultsBeforeXML(){
		running=false;
		auto_refresh=false;
		shouldReloadOnResume=false;
		receiversRegistered=false;
	}
	
	/**
	 * Creates a new BannerAdView
	 * @param context	The context of the {@link ViewGroup} to which the BannerAdView is being added.
	 */
	public BannerAdView(Context context) {
		super(context);
	}

	/**
	 * Creates a new BannerAdView
	 * @param context	The context of the {@link ViewGroup} to which the BannerAdView is being added.
	 * @param attrs		The {@link AttributeSet} to use when creating the BannerAdView.
	 */
	public BannerAdView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * Creates a new BannerAdView
	 * @param context	The context of the {@link ViewGroup} to which the BannerAdView is being added.
	 * @param attrs		The {@link AttributeSet} to use when creating the BannerAdView.
	 * @param defStyle	The default style to apply to this view. If 0, no style will be applied (beyond what is included in the theme). This may either be an attribute resource, whose value will be retrieved from the current theme, or an explicit style resource.
	 */
	public BannerAdView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * Creates a new BannerAdView
	 * @param context		The context of the {@link ViewGroup} to which the BannerAdView is being added.
	 * @param placement_id	The AppNexus placement id to use for this BannerAdView.
	 */
	public BannerAdView(Context context, String placement_id) {
		super(context, placement_id);
	}

	/**
	 * Creates a new BannerAdView
	 * @param context		The context of the {@link ViewGroup} to which the BannerAdView is being added.
	 * @param placement_id	The AppNexus placement id to use for this BannerAdView.
	 * @param ad_width		The height of the ad to request. Note: This is not the same as layout_width.
	 * @param ad_height		Thewidth of the ad to request. Note: This is not the same as layout_height.
	 */
	public BannerAdView(Context context, String placement_id, int ad_width,
			int ad_height) {
		super(context, placement_id, ad_width, ad_height);
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
				}

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
			if (!receiversRegistered) {
				setupBroadcast(getContext());
				receiversRegistered = true;
			}
			start();
		}

	}

	// Make sure receive is registered.
	@Override
	protected void onFirstLayout() {
		super.onFirstLayout();
		if (this.auto_refresh) {
			if (!receiversRegistered) {
				setupBroadcast(getContext());
				receiversRegistered = true;
			}
		}
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

	/**
	 * 
	 * @return The interval, in milliseconds, at which the BannerAdView will request new ads, if autorefresh is enabled.
	 */
	public int getAutoRefreshInterval() {
		Clog.d(Clog.publicFunctionsLogTag,
				Clog.getString(R.string.get_period, period));
		return period;
	}

	/**
	 * 
	 * @param period The interval, in milliseconds, at which the BannerAdView will request new ads, if autorefresh is enabled.
	 */
	public void setAutoRefreshInterval(int period) {
		Clog.d(Clog.publicFunctionsLogTag,
				Clog.getString(R.string.set_period, period));
		this.period = period;
		if(mAdFetcher!=null) mAdFetcher.setPeriod(period);
	}

	/**
	 * 
	 * @return Whether this view should periodically request new ads.
	 */
	public boolean getAutoRefresh() {
		Clog.d(Clog.publicFunctionsLogTag,
				Clog.getString(R.string.get_auto_refresh, auto_refresh));
		return auto_refresh;
	}

	/**
	 * 
	 * @param auto_refresh	Whether this view should periodically request new ads.
	 */
	public void setAutoRefresh(boolean auto_refresh) {
		Clog.d(Clog.publicFunctionsLogTag,
				Clog.getString(R.string.set_auto_refresh, auto_refresh));
		this.auto_refresh = auto_refresh;
		if(mAdFetcher!=null) mAdFetcher.setAutoRefresh(auto_refresh);
		if (!running && mAdFetcher!=null) {
			start();
		}
	}
	
	/**
	 * 
	 * @return Whether or not this view should load a new ad if the user resumes use of the app from a screenlock or multitask.
	 */
	public boolean getShouldReloadOnResume() {
		Clog.d(Clog.publicFunctionsLogTag, Clog.getString(R.string.get_should_resume, shouldReloadOnResume));
		return shouldReloadOnResume;
	}

	/**
	 * 
	 * @param shouldReloadOnResume Whether or not this view should load a new ad if the user resumes use of the app from a screenlock or multitask.
	 */
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
			if(!receiversRegistered){
				setupBroadcast(getContext());
				receiversRegistered=true;
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
			if(receiversRegistered){
				dismantleBroadcast();
				receiversRegistered=false;
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
	
	/**
	 * Begins automatically reloading ads at the set period (60 seconds by default).
	 */
	public void startAutoRefresh(){
		this.setAutoRefresh(true);
	}
	
	/**
	 * Sets the autorefresh period and begins automatically reloading ads.
	 * @param interval
	 */
	public void startAutoRefresh(int interval){
		this.setAutoRefreshInterval(interval);
		this.startAutoRefresh();
	}
	
	@Override
	protected void unhide(){
		super.unhide();
		this.requesting_visible = true;
	}
	
	protected String getMRAIDAdType(){
		return "inline";
	}
}
