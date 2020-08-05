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

import com.appnexus.opensdk.MediatedNativeAdController;
import com.appnexus.opensdk.NativeAdEventListener;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.utils.Clog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

import java.lang.ref.WeakReference;

public class AdMobNativeListener extends AdListener implements UnifiedNativeAd.OnUnifiedNativeAdLoadedListener {
    MediatedNativeAdController mBC;
    private WeakReference<AdMobNativeAdResponse> weakReferenceAdMobNativeAdResponse;

    public AdMobNativeListener(MediatedNativeAdController mBC) {
        this.mBC = mBC;
    }

    @Override
    public void onAdFailedToLoad(int errorCode) {
        Clog.e(Clog.mediationLogTag, "AdMob - onAdFailedToLoad");
        if (mBC != null) {
            ResultCode code = ResultCode.getNewInstance(ResultCode.INTERNAL_ERROR);
            switch (errorCode) {
                case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                    code = ResultCode.getNewInstance(ResultCode.INTERNAL_ERROR);
                    break;
                case AdRequest.ERROR_CODE_INVALID_REQUEST:
                    code = ResultCode.getNewInstance(ResultCode.INVALID_REQUEST);
                    break;
                case AdRequest.ERROR_CODE_NETWORK_ERROR:
                    code = ResultCode.getNewInstance(ResultCode.NETWORK_ERROR);
                    break;
                case AdRequest.ERROR_CODE_NO_FILL:
                    code = ResultCode.getNewInstance(ResultCode.UNABLE_TO_FILL);
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
        if(mBC!=null) {
            mBC.onAdImpression();
        }
    }


    @Override
    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
        Clog.e(Clog.mediationLogTag, "AdMob - onUnifiedNativeAdLoaded");
        if (mBC != null) {
            AdMobNativeAdResponse response = new AdMobNativeAdResponse(unifiedNativeAd);
            weakReferenceAdMobNativeAdResponse = new WeakReference<AdMobNativeAdResponse>(response);
            mBC.onAdLoaded(response);
        }
    }
}
