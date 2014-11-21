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
import com.facebook.ads.NativeAd;

public class FacebookNativeAd implements MediatedNativeAd {
    NativeAd nativeAd;

    @Override
    public NativeAdResponse requestNativeAd(Context context, String uid, final MediatedNativeAdController mBC, TargetingParameters tp) {
        nativeAd = new NativeAd(context, uid);
        FBNativeAdResponse response = new FBNativeAdResponse(nativeAd);
        nativeAd.setAdListener(new FacebookNativeAdListener(mBC, response));
        nativeAd.loadAd();
        return response;
    }
}
