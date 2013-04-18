package com.appnexus.opensdk;

import com.appnexus.opensdk.utils.Clog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class InstallTrackerPixel extends BroadcastReceiver{
	BroadcastReceiver receiver_install;

	@Override
	public void onReceive(Context context, final Intent intent) {
		new Thread(new Runnable(){

			@Override
			public void run() {
				Bundle extras = intent.getExtras();
				String referralString = extras.getString("referrer");

				Clog.e("OPENSDK-DEBUG", referralString);
				
			}
		}).run();

	}

}
