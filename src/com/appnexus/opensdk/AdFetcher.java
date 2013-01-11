/**
 * 
 */
package com.appnexus.opensdk;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.res.Configuration;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

/**
 * @author jacob
 * 
 */
public class AdFetcher {
	private ScheduledExecutorService tasker;
	private AdWebView mAdWebView;

	private Context mContext;
	private AdWebView owner;

	/**
	 * 
	 */
	public AdFetcher(Context context, AdWebView owner) {
		mContext = context;
		this.owner = owner;
	}

	/**
	 * Sets the period. Default is one minute.
	 * 
	 * @param period
	 *            The period to refresh ads, in milliseconds.
	 */
	protected void setPeriod(int period) {
		Settings.getSettings().refresh_rate_ms = period;
	}

	protected int getPeriod() {
		return Settings.getSettings().refresh_rate_ms;
	}

	protected void pause() {
		tasker.shutdown();
	}

	protected void start() {
		if (tasker == null)
			tasker = Executors.newScheduledThreadPool(1);

		int msPeriod = Settings.getSettings().refresh_rate_ms == -1 ? 60 * 1000
				: Settings.getSettings().refresh_rate_ms;
		mAdWebView = owner;
		LocationManager lm = (LocationManager) mContext
				.getSystemService(Context.LOCATION_SERVICE);
		Location lastLocation = lm.getLastKnownLocation(lm.getBestProvider(
				new Criteria(), false));
		final String lat = lastLocation != null ? ""
				+ lastLocation.getLatitude() : null;
		final String lon = lastLocation != null ? ""
				+ lastLocation.getLongitude() : null;
		final String orientation = mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? "landscape"
				: "portrait";
		tasker.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				new AdRequest(new AdRequestParams(lat, lon, orientation)).execute();
			}
		}, 0, msPeriod, TimeUnit.MILLISECONDS);
	}

}
