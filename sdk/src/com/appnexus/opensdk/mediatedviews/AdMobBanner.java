package com.appnexus.opensdk.mediatedviews;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.view.View;
import com.appnexus.opensdk.MediatedBannerAdView;
import com.appnexus.opensdk.MediatedBannerAdViewController;
import com.appnexus.opensdk.utils.Clog;
import com.google.ads.*;
import com.google.ads.AdRequest.ErrorCode;

public class AdMobBanner implements MediatedBannerAdView, AdListener {
    MediatedBannerAdViewController mMediatedBannerAdViewController;

    public AdMobBanner() {
    }

    @Override
    public View requestAd(MediatedBannerAdViewController mBC, Activity activity, String parameter, String uid,
                          int width, int height, View adSpace) {
        if (mBC == null) {
            Clog.e(Clog.mediationLogTag, "AdMobBanner - requestAd called with null controller");
            return null;
        } else if (activity == null) {
            Clog.e(Clog.mediationLogTag, "AdMobBanner - requestAd called with null activity");
            return null;
        }
        Clog.d(Clog.mediationLogTag, String.format("AdMobBanner - requesting an ad: %s, %s, %s, %s, %dx%d, %s", mBC.toString(), activity.toString(), parameter, uid, width, height, adSpace));

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
        Clog.d(Clog.mediationLogTag, "AdMobBanner - onDismissScreen: " + arg0.toString());
    }

    @Override
    public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
        Clog.d(Clog.mediationLogTag, String.format("AdMobBanner - onFailedToReceiveAd: %s with error: %s", arg0.toString(), arg1));
        if (mMediatedBannerAdViewController != null) {
            mMediatedBannerAdViewController.onAdFailed(MediatedBannerAdViewController.RESULT.INTERNAL_ERROR);
        }

    }

    @Override
    public void onLeaveApplication(Ad arg0) {
        Clog.d(Clog.mediationLogTag, "AdMobBanner - onLeaveApplication: " + arg0.toString());
        if (mMediatedBannerAdViewController != null) {
            mMediatedBannerAdViewController.onAdClicked();
        }

    }

    @Override
    public void onPresentScreen(Ad arg0) {
        Clog.d(Clog.mediationLogTag, "AdMobBanner - onPresentScreen: " + arg0.toString());
    }

    @Override
    public void onReceiveAd(Ad arg0) {
        Clog.d(Clog.mediationLogTag, "AdMobBanner - onReceiveAd: " + arg0.toString());
        if (mMediatedBannerAdViewController != null) {
            mMediatedBannerAdViewController.onAdLoaded();
        }
    }

}