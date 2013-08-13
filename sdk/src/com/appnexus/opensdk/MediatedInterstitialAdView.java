package com.appnexus.opensdk;

import com.appnexus.opensdk.MediatedInterstitialAdViewController;

import android.app.Activity;
import android.view.View;

public interface MediatedInterstitialAdView {
    public View requestAd(MediatedInterstitialAdViewController mIC, Activity activity, String parameter, String uid);

    public void show();
}
