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
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.StringUtil;
import com.inmobi.ads.InMobiNative;

/**
 * This class is the InMobi native ad adapter - it allows an application that integrates with AppNexus
 * to request a native ad from the InMobi Android SDK. A developer needs to set up an account in the
 * AppNexus console in order to serve ads from InMobi. This class is never instantiated by the application.
 */
public class InMobiNativeAd implements MediatedNativeAd {

    /**
     * Called by the AN SDK to request a native ad
     *
     * @param context the context from which this class is launched
     * @param uid     property id from InMobi
     * @param mBC     the controller that AN uses to manage callbacks
     * @param tp      targeting parameters that developer passes in
     * @return native ad response from InMobi
     */
    @Override
    public void requestNativeAd(Context context, String uid, MediatedNativeAdController mBC, TargetingParameters tp) {
        if (mBC != null) {
            if (StringUtil.isEmpty(InMobiSettings.INMOBI_APP_ID)) {
                Clog.e(Clog.mediationLogTag, "InMobi mediation failed. Call InMobiSettings.setInMobiAppId(String key, Context context) to set the app id.");
                mBC.onAdFailed(ResultCode.MEDIATED_SDK_UNAVAILABLE);
                return;
            }
            try {
                long placementID = Long.parseLong(uid);
                InMobiNativeAdListener nativeAdListener = new InMobiNativeAdListener(mBC);
                InMobiNative nativeAd = new InMobiNative(placementID, nativeAdListener);
                InMobiSettings.setTargetingParams(tp);
                nativeAd.load();
            } catch (NumberFormatException e) {
                mBC.onAdFailed(ResultCode.INVALID_REQUEST);
            }
        }

    }

}
