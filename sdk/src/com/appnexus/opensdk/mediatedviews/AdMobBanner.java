package com.appnexus.opensdk.mediatedviews;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.view.View;

import com.google.ads.*;

import com.appnexus.opensdk.MediatedBannerAdView;

public class AdMobBanner implements MediatedBannerAdView {
	
	public AdMobBanner() {
	}

	@Override
	public View requestAd(Activity activity, String parameter, String uid,
			int width, int height, View adSpace) {
		AdView admobAV = new AdView(activity, new AdSize(width, height), uid);
		AdRequest ar = new AdRequest();
		
		//TODO remove
		ar.addTestDevice(((TelephonyManager)activity.getBaseContext().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId());
	
		admobAV.loadAd(ar);
		return admobAV;
	}

}