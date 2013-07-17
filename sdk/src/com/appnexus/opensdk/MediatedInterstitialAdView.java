package com.appnexus.opensdk;

import android.app.Activity;

public interface MediatedInterstitialAdView {
	public void requestAd(Activity activity, String parameter, String uid, int width, int height);
}
