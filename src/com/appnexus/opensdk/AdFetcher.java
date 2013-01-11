package com.appnexus.opensdk;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AdFetcher {
	private ScheduledExecutorService tasker;
	private AdView owner;
	
	private boolean running=false;

	public AdFetcher(AdView owner) {
		this.owner = owner;
	}

	protected void setPeriod(int period) {
		Settings.getSettings().refresh_rate_ms = period;
	}

	protected int getPeriod() {
		return Settings.getSettings().refresh_rate_ms;
	}

	protected void stop() {
		tasker.shutdown();
	}

	protected void start() {
		if(running) return;
		running=true;
		
		//Start a Scheduler to execute recurring tasks
		if (tasker == null)
			tasker = Executors.newScheduledThreadPool(1);

		//Get the period from the settings
		int msPeriod = Settings.getSettings().refresh_rate_ms == -1 ? 60 * 1000
				: Settings.getSettings().refresh_rate_ms;
		
		
		//Start recurring ad requests
		tasker.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				new AdRequest(owner).execute();
			}
		}, 0, msPeriod, TimeUnit.MILLISECONDS);
	}

}
