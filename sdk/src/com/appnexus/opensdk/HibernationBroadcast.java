package com.appnexus.opensdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.appnexus.opensdk.utils.Clog;

class HibernationBroadcast extends BroadcastReceiver {
	 public HibernationListener hibernationListener;
	 
	 /**
	  * Constructor to initialize hibernation broadcast
	  * @param hibernationListener - listener to receive callbacks of hibernation related events
	  */
	 public HibernationBroadcast(HibernationListener hibernationListener){
		 this.hibernationListener = hibernationListener;
	 }
	 
	 
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
			Clog.d("HibernationBroadcast", "HibernationBroadcast ACTION_SCREEN_OFF");
            if (hibernationListener != null) {
				hibernationListener.onScreenDisplayOff();
			}
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Clog.d("HibernationBroadcast", "HibernationBroadcast ACTION_SCREEN_ON");
            if (hibernationListener != null) {
				hibernationListener.onScreenDisplayOn();
			}
        }else if (intent.getAction().equals(Intent.ACTION_MAIN)) {
            Clog.d("HibernationBroadcast", "HibernationBroadcast ACTION_MAIN");
        }
	}

}