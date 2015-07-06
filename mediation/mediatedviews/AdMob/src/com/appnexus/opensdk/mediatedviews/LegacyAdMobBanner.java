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
import android.view.View;
import com.appnexus.opensdk.MediatedBannerAdView;
import com.appnexus.opensdk.MediatedBannerAdViewController;
import com.appnexus.opensdk.TargetingParameters;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.ads.mediation.admob.AdMobAdapterExtras;

/**
 * This class is the Google AdMob banner adaptor it provides the functionality needed to allow
 * an application using the App Nexus SDK to load a banner ad through the Google SDK. The instantiation
 * of this class is done in response from the AppNexus server for a banner placement that is configured
 * to use AdMob to serve it. This class is never instantiated by the developer.
 * <p/>
 * This class also serves as an example of how to write a Mediation adaptor for the AppNexus
 * SDK.
 */
public class LegacyAdMobBanner implements MediatedBannerAdView {
    private AdMobAdListener adListener;
    private AdView admobAV;

    /**
     * Interface called by the AN SDK to request an ad from the mediating SDK.
     *
     * @param mBC       the object which will be called with events from the 3d party SDK
     * @param activity  the activity from which this is launched
     * @param parameter String parameter received from the server for instantiation of this object
     * @param adUnitID  The 3rd party placement , in adMob this is the adUnitID
     * @param width     Width of the ad
     * @param height    Height of the ad
     */
    @Override
    public void requestAd(MediatedBannerAdViewController mBC, Activity activity, String parameter, String adUnitID,
                          int width, int height, TargetingParameters targetingParameters) {
        if (mBC != null) {

            adListener = new AdMobAdListener(mBC, super.getClass().getSimpleName());
            adListener.printToClog(String.format(" - requesting an ad: [%s, %s, %dx%d]",
                    parameter, adUnitID, width, height));

            admobAV = new AdView(activity, new AdSize(width, height), adUnitID);
            admobAV.setAdListener(adListener);
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
            AdMobAdapterExtras extras = new AdMobAdapterExtras();
            if (targetingParameters.getAge() != null) {
                extras.addExtra("Age", targetingParameters.getAge());
            }

            for (Pair<String, String> p : targetingParameters.getCustomKeywords()) {
                extras.addExtra(p.first, p.second);
            }
            if (targetingParameters.getLocation() != null) {
                ar.setLocation(targetingParameters.getLocation());
            }
            ar.setNetworkExtras(extras);

            mBC.setView(admobAV);
            admobAV.loadAd(ar);
        }
    }

    @Override
    public void destroy() {
        if (admobAV != null) {
            admobAV.destroy();
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
        destroy();
    }
}