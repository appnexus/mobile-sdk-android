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
import android.view.View;

import com.appnexus.opensdk.MediatedBannerAdView;
import com.appnexus.opensdk.MediatedBannerAdViewController;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.TargetingParameters;

/**
 * This class is an intermediate adaptor that chooses between the legacy
 * Google Mobile Ads adaptor and the Google Play Services adaptor
 * for DFP banners.
 */
public class DFPBanner implements MediatedBannerAdView {
    private MediatedBannerAdView mediatedAdView;

    @Override
    public View requestAd(MediatedBannerAdViewController mBC, Activity activity,
                          String parameter, String adUnitID, int width,
                          int height, TargetingParameters targetingParameters) {
        mediatedAdView = GoogleBridge.bannerForClassName(GoogleBridge.isGooglePlayServicesAvailable()
                ? GoogleBridge.GooglePlayDFPBanner : GoogleBridge.DFPBanner);
        if (mediatedAdView == null) {
            if (mBC != null) {
                mBC.onAdFailed(ResultCode.MEDIATED_SDK_UNAVAILABLE);
            }
            return null;
        }
        return mediatedAdView.requestAd(mBC, activity, parameter, adUnitID, width, height, targetingParameters);

    }

    @Override
    public void destroy() {
        if (mediatedAdView != null) {
            mediatedAdView.destroy();
            mediatedAdView =null;
        }
    }

    @Override
    public void onPause() {
        if(mediatedAdView !=null){
            mediatedAdView.onPause();
        }
    }

    @Override
    public void onResume() {
        if (mediatedAdView != null) {
            mediatedAdView.onResume();
        }
    }

    @Override
    public void onDestroy() {
        if(mediatedAdView !=null){
            mediatedAdView.onDestroy();
        }
    }
}
