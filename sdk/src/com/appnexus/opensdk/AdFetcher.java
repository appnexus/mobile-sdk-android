package com.appnexus.opensdk;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


import android.os.Handler;
import android.os.Message;

public class AdFetcher {
	private ScheduledExecutorService tasker;
	private AdView owner;
	private int period = -1;
	private boolean autoRefresh;
	private RequestHandler handler;
	private boolean shouldReset = false;
	private long lastFetchTime=-1;
	private long timePausedAt=-1;
	private long pauseDuration=Long.MAX_VALUE;

	// Fires requests whenever it receives a message
	public AdFetcher(AdView owner) {
		this.owner = owner;
		handler = new RequestHandler(this);
	}

	protected void setPeriod(int period) {
		this.period = period;
		if(tasker!=null)
			shouldReset = true;
	}

	protected int getPeriod() {
		return period;
	}

	protected void stop() {
		if (tasker == null)
			return; // You can't stop the signal Mal
		Clog.d(Clog.baseLogTag, Clog.getString(R.string.stop));
		tasker.shutdownNow();
		tasker = null;
		timePausedAt=System.currentTimeMillis();
		
	}
	
	private void requestFailed(){
		if(owner instanceof InterstitialAdView) ((InterstitialAdView) owner).fail();
	}

	protected void start() {
		//Better have a placement ID!
		if(owner.getPlacementID()==null){
			Clog.e(Clog.baseLogTag, Clog.getString(R.string.no_placement_id));
			requestFailed();
			return;
		}
		if (tasker != null) {
			Clog.d(Clog.baseLogTag,
					Clog.getString(R.string.moot_restart));
			requestFailed();
			return;
		}
		Clog.d(Clog.baseLogTag, Clog.getString(R.string.moot_restart));
		if(timePausedAt!=-1){
			pauseDuration+=System.currentTimeMillis()-timePausedAt;
		}
		makeTasker();
	}

	private void makeTasker() {
		// Start a Scheduler to execute recurring tasks
		tasker = Executors.newScheduledThreadPool(1);

		// Get the period from the settings
		final int msPeriod = period <= 0 ? 60 * 1000 : period;

		if (!getAutoRefresh()) {
			Clog.v(Clog.baseLogTag, Clog.getString(R.string.fetcher_start_single));
			// Request an ad once
			tasker.schedule(new Runnable() {
				@Override
				public void run() {
					Clog.v(Clog.baseLogTag, Clog.getString(R.string.handler_message_pass));
					handler.sendEmptyMessage(0);
				}
			}, 0, TimeUnit.SECONDS);
		} else {
			Clog.v(Clog.baseLogTag, Clog.getString(R.string.fetcher_start_auto));
			// Start recurring ad requests
			long stall = msPeriod-pauseDuration>0?msPeriod-pauseDuration:0;
			Clog.v(Clog.baseLogTag, Clog.getString(R.string.request_delayed_by_x_ms, stall));
			tasker.schedule(new Runnable(){
				@Override
				public void run(){
					Clog.v(Clog.baseLogTag, Clog.getString(R.string.request_delayed_by_x_ms, msPeriod));
					tasker.scheduleAtFixedRate(new Runnable() {
						@Override
						public void run() {
							Clog.v(Clog.baseLogTag, Clog.getString(R.string.handler_message_pass));
							handler.sendEmptyMessage(0);
						}
					}, 0, msPeriod, TimeUnit.MILLISECONDS);
				}
			}, stall, TimeUnit.MILLISECONDS);
		}
	}

	// Create a handler which will receive the AsyncTasks and spawn them from
	// the main thread.
	static class RequestHandler extends Handler {
		private final WeakReference<AdFetcher> mFetcher;

		RequestHandler(AdFetcher f) {
			mFetcher = new WeakReference<AdFetcher>(f);
		}

		@Override
		synchronized public void handleMessage(Message msg) {
			// If the adfetcher, for some reason, has vanished, do nothing with this message
			if(mFetcher.get()==null) return;
			
			//If we need to reset, reset.
			if(mFetcher.get().shouldReset){
				mFetcher.get().shouldReset=false;
				mFetcher.get().stop();
				mFetcher.get().start();
				return;
			}
			// Reset pause duration
			mFetcher.get().pauseDuration=0;
			
			// Update last fetch time once
			Clog.d(Clog.baseLogTag, Clog.getString(R.string.new_ad_since, (int)(System.currentTimeMillis()-mFetcher.get().lastFetchTime)));
			mFetcher.get().lastFetchTime = System.currentTimeMillis();
			
			// Spawn an AdRequest
			new AdRequest(mFetcher.get().owner).execute();
		}
	}

	protected boolean getAutoRefresh() {
		return autoRefresh;
	}

	protected void setAutoRefresh(boolean autoRefresh) {
		this.autoRefresh = autoRefresh;
		// Restart with new autorefresh setting, but only if auto-refresh was
		// set to true
		if (tasker != null) {
			if (autoRefresh == true) {
				stop();
				start();
			} else {
				// If we're setting it to false, just stop...
				stop();
			}
		}
	}
}
