/*
*    Copyright 2013 APPNEXUS INC
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
import android.util.Pair;
import com.appnexus.opensdk.MediatedInterstitialAdView;
import com.appnexus.opensdk.MediatedInterstitialAdViewController;
import com.appnexus.opensdk.TargetingParameters;
import com.google.ads.AdRequest;
import com.google.ads.InterstitialAd;
import com.google.ads.doubleclick.DfpExtras;

/**
 * This class is the Google DFP interstitial adaptor it provides the functionality needed to allow
 * an application using the App Nexus SDK to load a banner ad through the Google DFP SDK. The instantiation
 * of this class is done in response from the AppNexus server for a banner placement that is configured
 * to use AdMob to serve it. This class is never instantiated by the developer.
 * <p/>
 * This class also serves as an example of how to write a Mediation adaptor for the AppNexus
 * SDK.
 */
public class LegacyDFPInterstitial implements MediatedInterstitialAdView {
    private InterstitialAd iad;
    private AdMobAdListener adListener;

    @Override
    public void requestAd(MediatedInterstitialAdViewController mIC, Activity activity, String parameter, String uid, TargetingParameters targetingParameters) {
        adListener = new AdMobAdListener(mIC, super.getClass().getSimpleName());
        adListener.printToClog(String.format("requesting an ad: [%s, %s]", parameter, uid));
        iad = new InterstitialAd(activity, uid);

        AdRequest ar = new AdRequest();

        switch (targetingParameters.getGender()) {
            case UNKNOWN:
                break;
            case FEMALE:
                ar.setGender(AdRequest.Gender.FEMALE);
                break;
            case MALE:
                ar.setGender(AdRequest.Gender.MALE);
                break;
        }
        DfpExtras extras = new DfpExtras();
        if (targetingParameters.getAge() != null) {
            extras.addExtra("Age", targetingParameters.getAge());
        }
        if (targetingParameters.getLocation() != null) {
            ar.setLocation(targetingParameters.getLocation());
        }
        for (Pair<String, String> p : targetingParameters.getCustomKeywords()) {
            extras.addExtra(p.first, p.second);
        }
        ar.setNetworkExtras(extras);

        iad.setAdListener(adListener);

        iad.loadAd(ar);
    }

    @Override
    public void show() {
        adListener.printToClog("show called");
        if (iad == null) {
            adListener.printToClogError("show called while interstitial ad view was null");
            return;
        }
        if (!iad.isReady()) {
            adListener.printToClogError("show called while interstitial ad view was not ready");
            return;
        }

        iad.show();
        adListener.printToClog("interstitial ad shown");
    }

    @Override
    public boolean isReady() {
        return (iad != null) && (iad.isReady());
    }
}
