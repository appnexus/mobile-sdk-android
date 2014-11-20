/*
 *    Copyright 2013 APPNEXUS INC
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

package com.appnexus.opensdk.mediatedviews;

import android.app.Activity;
import com.appnexus.opensdk.MediatedInterstitialAdView;
import com.appnexus.opensdk.MediatedInterstitialAdViewController;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.TargetingParameters;

/**
 * This class is an intermediate adaptor that chooses between the legacy
 * Google Mobile Ads adaptor and the Google Play Services adaptor
 * for AdMob interstitials.
 */
public class AdMobInterstitial implements MediatedInterstitialAdView {
    private MediatedInterstitialAdView adView;

    @Override
    public void requestAd(MediatedInterstitialAdViewController mIC, Activity activity, String parameter, String uid, TargetingParameters tp) {
        adView = GoogleBridge.interstitialForClassName(GoogleBridge.isGooglePlayServicesAvailable()
                ? GoogleBridge.GooglePlayInterstitial : GoogleBridge.AdMobInterstitial);
        if (adView == null) {
            if (mIC != null) {
                mIC.onAdFailed(ResultCode.MEDIATED_SDK_UNAVAILABLE);
            }
            return;
        }
        adView.requestAd(mIC, activity, parameter, uid, tp);
    }

    @Override
    public void show() {
        if (adView != null) {
            adView.show();
        }
    }

    @Override
    public boolean isReady() {
        return (adView != null) && adView.isReady();
    }

    @Override
    public void destroy() {
        if (adView != null) {
            adView.destroy();
            adView=null;
        }
    }

    @Override
    public void onPause() {
        if(adView!=null) {
            adView.onPause();
        }
    }

    @Override
    public void onResume() {
        if(adView!=null) {
            adView.onResume();
        }
    }

    @Override
    public void onDestroy() {
        if(adView!=null) {
            adView.onDestroy();
        }
    }
}
