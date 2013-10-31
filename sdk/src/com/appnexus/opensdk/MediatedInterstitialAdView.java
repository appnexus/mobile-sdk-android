package com.appnexus.opensdk;

import android.app.Activity;

public interface MediatedInterstitialAdView extends MediatedAdView {
    public void requestAd(MediatedInterstitialAdViewController mIC, Activity activity, String parameter, String uid);

    public void show();
}
