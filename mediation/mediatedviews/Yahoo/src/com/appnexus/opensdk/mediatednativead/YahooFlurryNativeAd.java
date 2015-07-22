/*
 *    Copyright 2015 APPNEXUS INC
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

import android.content.Context;

import com.appnexus.opensdk.MediatedNativeAd;
import com.appnexus.opensdk.MediatedNativeAdController;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.mediatedviews.YahooFlurrySettings;
import com.flurry.android.ads.FlurryAdErrorType;
import com.flurry.android.ads.FlurryAdNative;
import com.flurry.android.ads.FlurryAdNativeListener;

import java.lang.ref.WeakReference;

/**
 * This class is the AdColony native view adapter. It provides the functionality needed to allow
 * an application using the App Nexus SDK to load an native ad through the AdColony SDK. The
 * instantiation of this class is done in response from the AppNexus server for an native
 * placement that is configured to use AdColony to serve it. This class is never directly instantiated
 * by the developer.
 */

public class YahooFlurryNativeAd implements MediatedNativeAd, FlurryAdNativeListener {
    private FlurryAdNative mFlurryAdNative;
    private WeakReference<MediatedNativeAdController> controller;
    private WeakReference<YahooFlurryNativeAdResponse> response;

    @Override
    public void requestNativeAd(Context context, String uid, MediatedNativeAdController mBC, TargetingParameters tp) {
        if (mBC != null) {
            this.controller = new WeakReference<MediatedNativeAdController>(mBC);
            mFlurryAdNative = new FlurryAdNative(context, uid);
            mFlurryAdNative.setTargeting(YahooFlurrySettings.getFlurryAdTargeting(tp));
            mFlurryAdNative.setListener(this);
            mFlurryAdNative.fetchAd();
        }
    }

    @Override
    public void onFetched(FlurryAdNative flurryAdNative) {
        MediatedNativeAdController controller = this.controller.get();
        if (controller != null) {
            YahooFlurryNativeAdResponse response = YahooFlurryNativeAdResponse.create(mFlurryAdNative);
            if (response != null) {
                this.response = new WeakReference<YahooFlurryNativeAdResponse>(response);
                controller.onAdLoaded(response);
            } else {
                controller.onAdFailed(ResultCode.INTERNAL_ERROR);
            }
        }
    }

    @Override
    public void onShowFullscreen(FlurryAdNative flurryAdNative) {

    }

    @Override
    public void onCloseFullscreen(FlurryAdNative flurryAdNative) {

    }

    @Override
    public void onAppExit(FlurryAdNative flurryAdNative) {
        YahooFlurryNativeAdResponse response = this.response.get();
        if (response != null) {
            response.onAdWillLeaveApp();
        }
    }

    @Override
    public void onClicked(FlurryAdNative flurryAdNative) {
        YahooFlurryNativeAdResponse response = this.response.get();
        if (response != null) {
            response.onAdClicked();
        }
    }

    @Override
    public void onImpressionLogged(FlurryAdNative flurryAdNative) {

    }

    @Override
    public void onError(FlurryAdNative flurryAdNative, FlurryAdErrorType flurryAdErrorType, int i) {
        MediatedNativeAdController controller = this.controller.get();
        if (controller != null) {
            controller.onAdFailed(YahooFlurrySettings.errorCodeMapping(flurryAdErrorType, i));
        }
    }
}
