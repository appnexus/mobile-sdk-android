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

package com.appnexus.opensdk.mediatedviews;

import android.app.Activity;
import com.appnexus.opensdk.MediatedInterstitialAdView;
import com.appnexus.opensdk.MediatedInterstitialAdViewController;
import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.utils.Clog;
import com.mopub.mobileads.MoPubInterstitial;

/**
 * This class is the MoPub interstitial adaptor it provides the functionality needed to allow
 * an application using the AppNexus SDK to load an interstitial ad through the MoPub SDK. The instantiation
 * of this class is done in response from the AppNexus server for a interstitial placement that is configured
 * to use MoPub to serve it. This class is never directly instantiated by the application.
 *
 * This class also serves as an example of how to write a Mediation adaptor for the AppNexus
 * SDK.
 *
 */
public class MoPubInterstitialAdView implements MediatedInterstitialAdView {

    private MoPubInterstitial interstitialAd;

    @Override
    public void requestAd(MediatedInterstitialAdViewController mIC, Activity activity, String parameter, String uid, TargetingParameters targetingParameters) {
        MoPubListener mpListener = new MoPubListener(mIC, this.getClass().getSimpleName());

        interstitialAd = new MoPubInterstitial(activity, uid);
        interstitialAd.setInterstitialAdListener(mpListener);

        if (targetingParameters != null) {
            // MoPubInterstitial setLocation not supported.
            /*if (targetingParameters.getLocation() != null) {
                interstitialAd.setLocation(targetingParameters.getLocation());
            }*/

            String keywords = MoPubListener.keywordsFromTargetingParameters(targetingParameters);
            interstitialAd.setKeywords(keywords);
        }

        interstitialAd.load();
    }

    @Override
    public void show() {
        Clog.d(Clog.mediationLogTag, "show called");
        if (!this.isReady()) {
            Clog.d(Clog.mediationLogTag, "show called while interstitial ad view was unavailable");
            return;
        }

        boolean success = interstitialAd.show();

        if (success)
            Clog.d(Clog.mediationLogTag, "display called successfully");
        else
            Clog.d(Clog.mediationLogTag, "display call failed");
    }

    @Override
    public boolean isReady() {
        return (interstitialAd != null) && (interstitialAd.isReady());
    }

    @Override
    public void destroy() {
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
    }
}
