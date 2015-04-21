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
import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.utils.Clog;
import com.vdopia.ads.lw.LVDOInterstitialAd;

/**
 * This class is the Vdopia interstitial adapter - it provides the functionality needed to allow
 * an application using the AppNexus SDK to load an interstitial ad through the Vdopia light weight SDK.
 * The instantiation of this class is done in response from the AppNexus server for an interstitial
 * placement that is configured to use Vdopia to serve it. This class is never directly instantiated
 * by the application.
 * <p/>
 * This class also serves as an example of how to write a Mediation adaptor for the AppNexus
 * SDK.
 */
public class VdopiaInterstitial implements MediatedInterstitialAdView {
    LVDOInterstitialAd interstitialAd;

    /**
     * @param mIC       A controller through which the adaptor must send
     *                  events to the AppNexus SDK.
     * @param activity  Activity that the this class is launched from.
     * @param parameter An optional opaque string passed from the Ad
     *                  Network Manager, this can be used to define
     *                  SDK-specific parameters such as additional
     *                  targeting information.  The encoding of the
     *                  contents of this string are entirely up to the
     *                  implementation of the third-party SDK adaptor.
     * @param uid       The network ID for this ad call.  This ID is opaque
     *                  to the AppNexus SDK; the ID's contents and their
     *                  encoding are up to the implementation of the
     * @param tp        Targeting parameters passed from AppNexus SDK, set
     *                  by the developer.
     */
    @Override
    public void requestAd(MediatedInterstitialAdViewController mIC, Activity activity,
                          String parameter, String uid, TargetingParameters tp) {
        interstitialAd = new LVDOInterstitialAd(activity, uid);
        VdopiaListener listener = new VdopiaListener(mIC, this.getClass().getSimpleName());
        interstitialAd.setAdListener(listener);
        interstitialAd.loadAd(VdopiaSettings.buildRequest(tp));
    }

    @Override
    public void show() {
        if (interstitialAd != null) {
            if (interstitialAd.isReady()) {
                interstitialAd.show();
            } else {
                Clog.d(Clog.mediationLogTag, "Called show() when interstitial is not ready.");
            }

        }
    }

    @Override
    public boolean isReady() {
        if (interstitialAd != null) {
            return interstitialAd.isReady();
        }
        return false;
    }

    @Override
    public void destroy() {
        if (interstitialAd != null) {
            interstitialAd.setAdListener(null);
            interstitialAd = null;
        }
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {

    }


}
