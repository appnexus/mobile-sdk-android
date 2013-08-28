package com.appnexus.opensdk.mediatedviews;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.view.View;
import com.appnexus.opensdk.MediatedBannerAdView;
import com.appnexus.opensdk.MediatedBannerAdViewController;
import com.google.ads.*;
import com.google.ads.AdRequest.ErrorCode;

public class AdMobBanner implements MediatedBannerAdView, AdListener {
    MediatedBannerAdViewController mMediatedBannerAdViewController;

    public AdMobBanner() {
    }

    @Override
    public View requestAd(MediatedBannerAdViewController mBC, Activity activity, String parameter, String uid,
                          int width, int height, View adSpace) {
        AdView admobAV = new AdView(activity, new AdSize(width, height), uid);
        AdRequest ar = new AdRequest();

        mMediatedBannerAdViewController = mBC;

        // TODO remove
        ar.addTestDevice(((TelephonyManager) activity.getBaseContext()
                .getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId());

        admobAV.loadAd(ar);
        return admobAV;
    }

    @Override
    public void onDismissScreen(Ad arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
        if (mMediatedBannerAdViewController != null) {
            mMediatedBannerAdViewController.onAdFailed(MediatedBannerAdViewController.RESULT.INTERNAL_ERROR);
        }

    }

    @Override
    public void onLeaveApplication(Ad arg0) {
        if (mMediatedBannerAdViewController != null) {
            mMediatedBannerAdViewController.onAdClicked();
        }

    }

    @Override
    public void onPresentScreen(Ad arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onReceiveAd(Ad arg0) {
        if (mMediatedBannerAdViewController != null) {
            mMediatedBannerAdViewController.onAdLoaded();
        }
    }

}