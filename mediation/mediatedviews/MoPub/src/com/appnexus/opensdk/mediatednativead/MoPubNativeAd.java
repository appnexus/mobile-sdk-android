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
import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.mediatedviews.MoPubListener;
import com.mopub.nativeads.MoPubNative;
import com.mopub.nativeads.RequestParameters;

import java.util.EnumSet;

/**
 * This class is the MoPub native ad adaptor - it provides the functionality needed to allow
 * an application using the AppNexus SDK to load a native ad through the MoPub SDK. The instantiation
 * of this class is done in response from the AppNexus server for a native placement that is configured
 * to use MoPub to serve it. This class is never directly instantiated by the application.
 * <p/>
 * This class also serves as an example of how to write a Mediation adaptor for the AppNexus
 * SDK.
 */
public class MoPubNativeAd implements MediatedNativeAd {
    /**
     * AppNexus SDK calls this method to request a native ad from MoPub.
     *
     * @param context The context from which this class is instantiated.
     * @param uid     MoPub ad unit id.
     * @param mBC     The controller that passes callbacks back to AppNexus SDK.
     * @param tp      Targeting parameters that were passed through AppNexus public API.
     * @return AppNexus NativeAdResponse that wraps a MoPub native ad.
     */
    @Override
    public void requestNativeAd(Context context, String uid, MediatedNativeAdController mBC, TargetingParameters tp) {
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
        MoPubNativeAdListener listener = new MoPubNativeAdListener(mBC);
        MoPubNative moPubNative = new MoPubNative(context, uid, listener);
        moPubNative.setNativeEventListener(listener);
        moPubNative.makeRequest(requestParameters);
    }
}
