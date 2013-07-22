package com.appnexus.opensdk;

import android.app.Activity;
import android.view.View;

public interface MediatedInterstitialAdView {
	public View requestAd(Activity activity, String parameter, String uid, int width, int height);
}
