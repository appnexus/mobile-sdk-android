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
import com.appnexus.opensdk.mediatedviews.MoPubListener;
import com.mopub.nativeads.MoPubNative;
import com.mopub.nativeads.RequestParameters;

import java.util.EnumSet;

public class MoPubNativeAd implements MediatedNativeAd{
    @Override
    public NativeAdResponse requestNativeAd(Context context, String uid, MediatedNativeAdController mBC, TargetingParameters tp) {
        RequestParameters requestParameters = null;
        if (tp != null) {
            final EnumSet<RequestParameters.NativeAdAsset> desired
                    = EnumSet.allOf(RequestParameters.NativeAdAsset.class);
            requestParameters = new RequestParameters.Builder()
                    .location(tp.getLocation())
                    .keywords(MoPubListener.keywordsFromTargetingParameters(tp))
                    .desiredAssets(desired)
                    .build();
        }
        MoPubNativeAdResponse response = new MoPubNativeAdResponse();
        MoPubNativeAdListener listener = new MoPubNativeAdListener(response, mBC);
        MoPubNative moPubNative = new MoPubNative(context, uid, listener);
        moPubNative.setNativeEventListener(listener);
        moPubNative.makeRequest(requestParameters);

        return response;
    }
}
