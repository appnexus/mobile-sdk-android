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
import com.inmobi.monetization.IMInterstitial;

/**
 * This class is InMobi interstitial adapter - it provides the functionality needed to allow an application
 * using the AppNexus SDK to load an interstitial ad from InMobi. A developer needs to set up an account
 * with AppNexus in order to request ads from InMobi. This class is never directly instantiated by an application.
 */
public class InMobiInterstitial implements MediatedInterstitialAdView {

    IMInterstitial iad;

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
        if (InMobiSettings.INMOBI_APP_ID == null || InMobiSettings.INMOBI_APP_ID.isEmpty()) {
            Clog.e(Clog.mediationLogTag, "InMobi mediation failed. Call InMobiSettings.setInMobiAppId(String key, Context context) to set the app id.");
            if (mIC != null) {
                mIC.onAdFailed(ResultCode.MEDIATED_SDK_UNAVAILABLE);
            }
            return;
        }
        if (uid != null && !uid.isEmpty()) {
            iad = new IMInterstitial(activity, uid);
        } else {
            iad = new IMInterstitial(activity, InMobiSettings.INMOBI_APP_ID);
        }
        iad.setIMInterstitialListener(new InMobiListener(mIC, this.getClass().getSimpleName()));
        InMobiSettings.setTargetingParams(tp);
        iad.loadInterstitial();
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
        return (iad != null) && (iad.getState() == IMInterstitial.State.READY);
    }

    @Override
    public void destroy() {
        if (iad != null) {
            iad.stopLoading();
            iad.setIMInterstitialListener(null);
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
