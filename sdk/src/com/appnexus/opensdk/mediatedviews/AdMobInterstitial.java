package com.appnexus.opensdk.mediatedviews;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import com.appnexus.opensdk.MediatedInterstitialAdView;
import com.appnexus.opensdk.MediatedInterstitialAdViewController;
import com.appnexus.opensdk.utils.Clog;
import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.InterstitialAd;

public class AdMobInterstitial implements MediatedInterstitialAdView,
        AdListener {
    InterstitialAd iad;
    MediatedInterstitialAdViewController mMediatedInterstitialAdViewController;

    public AdMobInterstitial() {
    }

    @Override
    public void requestAd(MediatedInterstitialAdViewController mIC, Activity activity, String parameter, String uid) {
        if (mIC == null) {
            Clog.e(Clog.mediationLogTag, "AdMobInterstitial - requestAd called with null controller");
            return;
        }

        if (activity == null) {
            Clog.e(Clog.mediationLogTag, "AdMobInterstitial - requestAd called with null activity");
            return;
        }
        Clog.d(Clog.mediationLogTag, String.format("AdMobInterstitial - requesting an ad: [%s, %s]", parameter, uid));

        iad = new InterstitialAd(activity, uid);

        AdRequest ar = new AdRequest();

        ar.addTestDevice(((TelephonyManager) activity.getBaseContext()
                .getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId());

        iad.setAdListener(this);

        iad.loadAd(ar);
        mMediatedInterstitialAdViewController = mIC;
    }

    @Override
    public void onReceiveAd(Ad ad) {
        Clog.d(Clog.mediationLogTag, "AdMobInterstitial - onReceiveAd: " + ad);
        mMediatedInterstitialAdViewController.onAdLoaded();
    }

    @Override
    public void onDismissScreen(Ad arg0) {
        Clog.d(Clog.mediationLogTag, "AdMobInterstitial - onDismissScreen: " + arg0);
    }

    @Override
    public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
        Clog.d(Clog.mediationLogTag, String.format("AdMobInterstitial - onFailedToReceiveAd: %s with error: %s", arg0, arg1));
        if (mMediatedInterstitialAdViewController != null) {
            mMediatedInterstitialAdViewController.onAdFailed(MediatedInterstitialAdViewController.RESULT.INTERNAL_ERROR);
        }
    }

    @Override
    public void onLeaveApplication(Ad arg0) {
        Clog.d(Clog.mediationLogTag, "AdMobInterstitial - onLeaveApplication: " + arg0);
        if (mMediatedInterstitialAdViewController != null) {
            mMediatedInterstitialAdViewController.onAdClicked();
        }
    }

    @Override
    public void onPresentScreen(Ad arg0) {
        Clog.d(Clog.mediationLogTag, "AdMobInterstitial - onPresentScreen: " + arg0);
    }

    @Override
    public void show() {
        Clog.d(Clog.mediationLogTag, "AdMobInterstitial - show called");
        if (iad == null) {
            Clog.e(Clog.mediationLogTag, "AdMobInterstitial - show called while interstitial ad view was null");
            return;
        }
        if (!iad.isReady()) {
            Clog.e(Clog.mediationLogTag, "AdMobInterstitial - show called while interstitial ad was not ready");
            return;
        }

        iad.show();
        Clog.d(Clog.mediationLogTag, "AdMobInterstitial - interstitial ad shown");
    }

}
