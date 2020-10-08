/*
 *    Copyright 2014 APPNEXUS INC
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
import com.appnexus.opensdk.NativeAdResponse;
import com.appnexus.opensdk.TargetingParameters;
import com.facebook.ads.AdSettings;
import com.facebook.ads.NativeAd;

/**
 * This class is Facebook native ad adapter - it allows an application that integrates with AppNexus
 * to request an native ad from Facebook Audience Network. A developer needs to set up on AppNexus server side in
 * order to request ads from Facebook. This class is never instantiated by the application.
 */
public class FacebookNativeAd implements MediatedNativeAd {

    /**
     * AppNexus SDk calls this method to request a native ad from Facebook
     *
     * @param context The context from which this class is instantiated.
     * @param uid Facebook placement id, app developer needs to set up account with Facebook.
     * @param mBC The controller that passes callbacks to AppNexus SDK
     * @param tp Targeting parameters that were set in AppNexus API.
     * @return AppNexus NativeAdResponse that wraps Facebook ad.
     */

    @Override
    public void requestNativeAd(Context context, String parameterString, String uid, final MediatedNativeAdController mBC, TargetingParameters tp) {
        NativeAd nativeAd = new NativeAd(context, uid);
        FBNativeAdResponse response = new FBNativeAdResponse(nativeAd);
        nativeAd.loadAd(nativeAd.buildLoadAdConfig().withAdListener(new FacebookNativeAdListener(mBC, response)).build());
    }
}
