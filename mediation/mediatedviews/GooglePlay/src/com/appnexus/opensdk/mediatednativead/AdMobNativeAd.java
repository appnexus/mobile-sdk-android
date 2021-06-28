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

import android.content.Context;

import com.appnexus.opensdk.MediatedNativeAd;
import com.appnexus.opensdk.MediatedNativeAdController;
import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.mediatedviews.GooglePlayServicesBanner;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.nativead.NativeAdOptions;

/**
 * This class is AdMob native ad adapter - it allows an application that integrates with AppNexus
 * to request an native ad from AdMob network. A developer needs to set up on AppNexus server side in
 * order to request ads from AdMob. This class is never instantiated by the application.
 */
public class AdMobNativeAd implements MediatedNativeAd {

    /**
     * AppNexus SDk calls this method to request a native ad from AdMob
     *
     * @param context The context from which this class is instantiated.
     * @param uid     AdMob ad unit id, app developer needs to set up account with AdMob.
     * @param mBC     The controller that passes callbacks to AppNexus SDK
     * @param tp      Targeting parameters that were set in AppNexus API.
     */
    @Override
    public void requestNativeAd(Context context, String parameterString, String uid, MediatedNativeAdController mBC, TargetingParameters tp) {
        if (mBC != null) {

            AdMobNativeListener adMobNativeListener = new AdMobNativeListener(mBC);

            NativeAdOptions.Builder adOptionsBuilder = new NativeAdOptions.Builder();

            if(!AdMobNativeSettings.enableMediaView){
                adOptionsBuilder.setReturnUrlsForImageAssets(true);
            }

            if(AdMobNativeSettings.videoOptions!=null) {
                adOptionsBuilder.setVideoOptions(AdMobNativeSettings.videoOptions);
            }


            AdLoader.Builder builder = new AdLoader.Builder(context, uid);
            builder.withNativeAdOptions(adOptionsBuilder.build());
            builder.forNativeAd(adMobNativeListener);
            builder.build().loadAd(GooglePlayServicesBanner.buildRequest(tp));
        }


    }
}
