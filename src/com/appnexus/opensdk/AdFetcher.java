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
	private boolean shouldReset; // If the period changes, wait until the end of
									// the new period before resetting? //TODO
	private long lastFetchTime;

	// Fires requests whenever it receives a message
	public AdFetcher(AdView owner) {
		this.owner = owner;
		handler = new RequestHandler(this);
	}

	protected void setPeriod(int period) {
		this.period = period;
		shouldReset = true;
	}

	protected int getPeriod() {
		return period;
	}

	protected void stop() {
		if (tasker == null)
			return; // You can't stop the signal Mal
		tasker.shutdown();
		tasker = null;
	}

	protected void start() {
		if (tasker != null) {
			Clog.d("OPENSDK",
					"AdFetcher requested to start, but tasker already instantiated");
			return;
		}
		makeTasker();
	}

	private void makeTasker() {
		// Start a Scheduler to execute recurring tasks
		tasker = Executors.newScheduledThreadPool(1);

		// Get the period from the settings
		int msPeriod = period == -1 ? 60 * 1000 : period;

		if (!getAutoRefresh()) {
			Clog.d("OPENSDK", "AdFetcher started in single-use mode");
			// Request an ad once
			tasker.schedule(new Runnable() {
				@Override
				public void run() {
					Clog.v("OPENSDK", "AdRequest message passed to handler.");
					handler.sendEmptyMessage(0);
				}
			}, 0, TimeUnit.SECONDS);
		} else {
			Clog.d("OPENSDK", "AdFetcher started in autorefresh-mode");
			// Start recurring ad requests
			tasker.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					Clog.v("OPENSDK", "AdRequest message passed to handler.");
					handler.sendEmptyMessage(0);
				}
			}, 0, msPeriod, TimeUnit.MILLISECONDS);
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
		public void handleMessage(Message msg) {
			// Update last fetch time once
			Clog.i("OPENSDK", "Fetching a new ad for the first time in "+(int)(System.currentTimeMillis()-mFetcher.get().lastFetchTime)+"ms");
			mFetcher.get().lastFetchTime = System.currentTimeMillis();
			//If we need to reset, reset.
			if(mFetcher.get().shouldReset){
				mFetcher.get().shouldReset=false;
				mFetcher.get().stop();
				mFetcher.get().start();
			}
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
