package com.appnexus.opensdk;

import android.app.Activity;

interface MediatedInterstitialAdView extends MediatedAdView {
    public void requestAd(MediatedInterstitialAdViewController mIC, Activity activity, String parameter, String uid);

    public void show();
}
