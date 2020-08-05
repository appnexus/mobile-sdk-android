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

package com.appnexus.opensdk.mediatedviews;

import android.app.Activity;

import com.appnexus.opensdk.MediatedInterstitialAdView;
import com.appnexus.opensdk.MediatedInterstitialAdViewController;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.mediatednativead.InMobiSettings;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.StringUtil;


/**
 * This class is InMobi interstitial adapter - it provides the functionality needed to allow an application
 * using the AppNexus SDK to load an interstitial ad from InMobi. A developer needs to set up an account
 * with AppNexus in order to request ads from InMobi. This class is never directly instantiated by an application.
 */
public class InMobiInterstitial implements MediatedInterstitialAdView {

    com.inmobi.ads.InMobiInterstitial iad;

    /**
     * Called by the AppNexus SDK to request an Interstitial ad
     *
     * @param mIC       A controller through which the adapter must send events to the AppNexus SDK.
     * @param activity  The activity from which this class is instantiated.
     * @param parameter An optional opaque string passed from the Ad Network Manager, this can be used to define
     *                  SDK-specific parameters such as additional targeting information.  The encoding of the
     *                  contents of this string are entirely up to the implementation of the third-party SDK adaptor.
     * @param uid       The network ID for this ad call.  This ID is opaque to the AppNexus SDK; the ID's contents and their
     *                  encoding are up to the implementation of InMobi.
     * @param tp        Targeting parameter that are passed in from AppNexus SDK public API.
     */

    @Override
    public void requestAd(MediatedInterstitialAdViewController mIC, Activity activity,
                          String parameter, String uid, TargetingParameters tp) {
        if (mIC != null) {
            if (StringUtil.isEmpty(InMobiSettings.INMOBI_APP_ID)) {
                Clog.e(Clog.mediationLogTag, "InMobi mediation failed. Call InMobiSettings.setInMobiAppId(String key, Context context) to set the app id.");
                mIC.onAdFailed(ResultCode.getNewInstance(ResultCode.MEDIATED_SDK_UNAVAILABLE));
                return;
            }
            try {
                long placementID = Long.parseLong(uid);
                InMobiInterstitialAdListener listener = new InMobiInterstitialAdListener(mIC);
                iad = new com.inmobi.ads.InMobiInterstitial(activity, placementID, listener);
                InMobiSettings.setTargetingParams(tp);
                iad.load();
            } catch (NumberFormatException e) {
                mIC.onAdFailed(ResultCode.getNewInstance(ResultCode.INVALID_REQUEST));
            }
        }
    }

    @Override
    public void show() {
        Clog.d(Clog.mediationLogTag, "show() called on InMobi interstitial ad");
        if (isReady()) {
            iad.show();
        } else {
            Clog.d(Clog.mediationLogTag, "InMobi interstitial ad view was unavailable");
        }
    }

    @Override
    public boolean isReady() {
        return (iad != null) && (iad.isReady());
    }

    @Override
    public void destroy() {
        if (iad != null) {
            iad = null;
        }
    }

    @Override
    public void onPause() {
        //InMobi lacks a pause api
    }

    @Override
    public void onResume() {
        //InMobi lacks a resume api
    }

    @Override
    public void onDestroy() {
        destroy();
    }
}
