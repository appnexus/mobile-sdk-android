package com.appnexus.opensdk.mediatedviews;

import android.app.Activity;
import android.view.View;
import com.appnexus.opensdk.MediatedInterstitialAdView;
import com.appnexus.opensdk.MediatedInterstitialAdViewController;
import com.millennialmedia.android.*;

public class MillenialMediaInterstitial implements MediatedInterstitialAdView, RequestListener {
    MMInterstitial iad;
    MediatedInterstitialAdViewController mMediatedInterstitialAdViewController;


    @Override
    public View requestAd(MediatedInterstitialAdViewController mIC, Activity activity, String parameter, String uid) {
        mMediatedInterstitialAdViewController = mIC;

        MMSDK.initialize(activity);

        iad = new MMInterstitial(activity);
        iad.setApid(uid);
        iad.setListener(this);
        iad.fetch();

        return null;
    }

    @Override
    public void show() {
        if (iad != null && iad.isAdAvailable())
            iad.display();
    }

    @Override
    public void MMAdOverlayLaunched(MMAd mmAd) {
        if (mMediatedInterstitialAdViewController != null)
            mMediatedInterstitialAdViewController.onAdExpanded();
    }

    // this callback doesn't seem to work (MM's fault)
    @Override
    public void MMAdOverlayClosed(MMAd mmAd) {
        if (mMediatedInterstitialAdViewController != null)
            mMediatedInterstitialAdViewController.onAdCollapsed();
    }

    // equivalent to a "interstitial is loading" state
    @Override
    public void MMAdRequestIsCaching(MMAd mmAd) {
    }

    @Override
    public void requestCompleted(MMAd mmAd) {
        if (mMediatedInterstitialAdViewController != null)
            mMediatedInterstitialAdViewController.onAdLoaded();
    }

    @Override
    public void requestFailed(MMAd mmAd, MMException e) {
        if (mMediatedInterstitialAdViewController != null)
            mMediatedInterstitialAdViewController.onAdFailed();
    }

    // this also doesn't work..
    @Override
    public void onSingleTap(MMAd mmAd) {
        if (mMediatedInterstitialAdViewController != null)
            mMediatedInterstitialAdViewController.onAdClicked();
    }
}