package com.appnexus.opensdk.mediatednativead;

import android.content.Context;

import com.appnexus.opensdk.MediatedNativeAd;
import com.appnexus.opensdk.MediatedNativeAdController;
import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.mediatedviews.GooglePlayServicesBanner;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.formats.NativeAdOptions;

public class AdMobNativeAd implements MediatedNativeAd {

    @Override
    public void requestNativeAd(Context context, String uid, MediatedNativeAdController mBC, TargetingParameters tp) {
        AdMobNativeListener adMobNativeListener = new AdMobNativeListener(mBC);
        AdLoader adLoader = new AdLoader.Builder(context, uid)
                .forAppInstallAd(adMobNativeListener)
                .forContentAd(adMobNativeListener)
                .withAdListener(adMobNativeListener)
                .withNativeAdOptions(new NativeAdOptions.Builder().setReturnUrlsForImageAssets(true).build())
                .build();
        adLoader.loadAd(GooglePlayServicesBanner.buildRequest(tp));
    }
}
