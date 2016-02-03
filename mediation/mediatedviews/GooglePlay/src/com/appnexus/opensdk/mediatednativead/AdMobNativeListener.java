package com.appnexus.opensdk.mediatednativead;

import com.appnexus.opensdk.MediatedNativeAdController;
import com.appnexus.opensdk.ResultCode;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeContentAd;

public class AdMobNativeListener extends AdListener implements NativeAppInstallAd.OnAppInstallAdLoadedListener, NativeContentAd.OnContentAdLoadedListener {
    MediatedNativeAdController mBC;

    public AdMobNativeListener(MediatedNativeAdController mBC) {
        this.mBC = mBC;
    }

    @Override
    public void onAdFailedToLoad(int errorCode) {
        if (mBC != null) {
            ResultCode code = ResultCode.INTERNAL_ERROR;
            switch (errorCode) {
                case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                    code = ResultCode.INTERNAL_ERROR;
                    break;
                case AdRequest.ERROR_CODE_INVALID_REQUEST:
                    code = ResultCode.INVALID_REQUEST;
                    break;
                case AdRequest.ERROR_CODE_NETWORK_ERROR:
                    code = ResultCode.NETWORK_ERROR;
                    break;
                case AdRequest.ERROR_CODE_NO_FILL:
                    code = ResultCode.UNABLE_TO_FILL;
                    break;
                default:
                    break;
            }
            mBC.onAdFailed(code);
        }
    }

    @Override
    public void onAppInstallAdLoaded(NativeAppInstallAd nativeAppInstallAd) {
        if (mBC != null) {
            mBC.onAdLoaded(new AdMobNativeAdResponse(nativeAppInstallAd, AdMobNativeSettings.AdMobNativeType.APP_INSTALL));
        }

    }

    @Override
    public void onContentAdLoaded(NativeContentAd nativeContentAd) {
        if (mBC != null) {
            mBC.onAdLoaded(new AdMobNativeAdResponse(nativeContentAd, AdMobNativeSettings.AdMobNativeType.CONTENT_AD));
        }
    }
}
