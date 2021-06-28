/*
 *    Copyright 2016 APPNEXUS INC
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.appnexus.opensdk.mediatednativead;

import androidx.annotation.NonNull;

import com.appnexus.opensdk.MediatedNativeAdController;
import com.appnexus.opensdk.NativeAdEventListener;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.utils.Clog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;

import java.lang.ref.WeakReference;

public class AdMobNativeListener extends AdListener implements NativeAd.OnNativeAdLoadedListener {
    MediatedNativeAdController mBC;
    private WeakReference<AdMobNativeAdResponse> weakReferenceAdMobNativeAdResponse;

    public AdMobNativeListener(MediatedNativeAdController mBC) {
        this.mBC = mBC;
    }

    @Override
    public void onAdFailedToLoad(LoadAdError errorCode) {
        Clog.e(Clog.mediationLogTag, "AdMob - onAdFailedToLoad");

        if (mBC != null) {
            ResultCode code = ResultCode.getNewInstance(ResultCode.INTERNAL_ERROR, errorCode.getResponseInfo().toString());

            switch (errorCode.getCode()) {
                case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                    code = ResultCode.getNewInstance(ResultCode.INTERNAL_ERROR, errorCode.getResponseInfo().toString());
                    break;
                case AdRequest.ERROR_CODE_INVALID_REQUEST:
                    code = ResultCode.getNewInstance(ResultCode.INVALID_REQUEST, errorCode.getResponseInfo().toString());
                    break;
                case AdRequest.ERROR_CODE_NETWORK_ERROR:
                    code = ResultCode.getNewInstance(ResultCode.NETWORK_ERROR, errorCode.getResponseInfo().toString());
                    break;
                case AdRequest.ERROR_CODE_NO_FILL:
                    code = ResultCode.getNewInstance(ResultCode.UNABLE_TO_FILL, errorCode.getResponseInfo().toString());
                    break;
                default:
                    break;
            }

            mBC.onAdFailed(code);
        }
    }

    @Override
    public void onAdClicked() {
        Clog.e(Clog.mediationLogTag, "AdMob - onAdClicked");
        AdMobNativeAdResponse response = this.weakReferenceAdMobNativeAdResponse.get();
        if (response != null) {
            NativeAdEventListener listener = response.getListener();
            if (listener != null) {
                listener.onAdWasClicked();
            }
        }
    }

    @Override
    public void onAdImpression() {
        Clog.e(Clog.mediationLogTag, "AdMob - onAdImpression");
        NativeAdEventListener listener = null;
        AdMobNativeAdResponse adMobNativeAdResponse = this.weakReferenceAdMobNativeAdResponse.get();
        if (adMobNativeAdResponse != null) {
            adMobNativeAdResponse.removeExpiryCallbacks();
            if (adMobNativeAdResponse.getListener() != null) {
                listener = adMobNativeAdResponse.getListener();
            }
        }
        if (mBC != null) {
            mBC.onAdImpression(listener);
        }
    }


    @Override
    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
        Clog.e(Clog.mediationLogTag, "AdMob - onNativeAdLoaded");
        if (mBC != null) {
            AdMobNativeAdResponse response = new AdMobNativeAdResponse(nativeAd);
            weakReferenceAdMobNativeAdResponse = new WeakReference<AdMobNativeAdResponse>(response);
            mBC.onAdLoaded(response);
        }
    }
}
