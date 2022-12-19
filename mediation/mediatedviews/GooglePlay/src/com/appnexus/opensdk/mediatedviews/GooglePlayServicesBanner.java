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
import android.app.Application;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;

import com.appnexus.opensdk.MediatedBannerAdView;
import com.appnexus.opensdk.MediatedBannerAdViewController;
import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.utils.StringUtil;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

import java.util.ArrayList;

/**
 * This class is the Google Play Services banner adaptor it provides the functionality needed to allow
 * an application using the App Nexus SDK to load a banner ad through the Google Play Services SDK. The instantiation
 * of this class is done in response from the AppNexus server for a banner placement that is configured
 * to use AdMob to serve it. This class is never instantiated by the developer.
 * <p/>
 * This class also serves as an example of how to write a Mediation adaptor for the AppNexus
 * SDK.
 */
public class GooglePlayServicesBanner implements MediatedBannerAdView {
    private AdView adView;
    private Application.ActivityLifecycleCallbacks activityListener;
    private GooglePlayAdListener adListener;

    /**
     * Interface called by the AN SDK to request an ad from the mediating SDK.
     *
     * @param mBC                 the object which will be called with events from the 3rd party SDK
     * @param activity            the activity from which this is launched
     * @param parameter           String parameter received from the server for instantiation of this object
     * @param adUnitID            The 3rd party placement , in adMob this is the adUnitID
     * @param width               Width of ad
     * @param height              Height of ad
     * @param targetingParameters targetingParameters
     */
    @Override
    public View requestAd(MediatedBannerAdViewController mBC, Activity activity, String parameter,
                          String adUnitID, int width, int height, TargetingParameters targetingParameters) {
        adListener = new GooglePlayAdListener(mBC, super.getClass().getSimpleName());
        adListener.printToClog(String.format(" - requesting an ad: [%s, %s, %dx%d]",
                parameter, adUnitID, width, height));

        adView = new AdView(activity);
        adView.setAdUnitId(adUnitID);
        adView.setAdSize(new AdSize(width, height));
        adView.setAdListener(adListener);

        try {
            adView.loadAd(buildRequest(targetingParameters));
        } catch (NoClassDefFoundError e) {
            // This can be thrown by Play Services on Honeycomb.
            adListener.onAdFailedToLoad(new LoadAdError(AdRequest.ERROR_CODE_NO_FILL, e.getMessage(), "", null, null));
        }

        return adView;
    }

    @Override
    public void destroy() {
        if (adView != null) {
            adView.destroy();
            adView.setAdListener(null);
        }
        adListener = null;
        adView = null;
    }

    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
    }

    @Override
    public void onResume() {
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    public void onDestroy() {
        destroy();
    }

    public static AdRequest buildRequest(TargetingParameters targetingParameters) {
        AdRequest.Builder builder = new AdRequest.Builder();

        if (targetingParameters != null) {
            Bundle bundle = new Bundle();

            if (!StringUtil.isEmpty(targetingParameters.getAge())) {
                bundle.putString("Age", targetingParameters.getAge());
            }

            for (Pair<String, String> p : targetingParameters.getCustomKeywords()) {
                if (p.first.equals("content_url")) {
                    if (!StringUtil.isEmpty(p.second)) {
                        builder.setContentUrl(p.second);
                    }
                } else {
                    if (bundle.containsKey(p.first)) {
                        ArrayList<String> listValues = new ArrayList<>();
                        Object value = bundle.get(p.first);
                        if (value instanceof String) {
                            listValues.add((String) value);
                        } else if (value instanceof ArrayList) {
                            listValues.addAll((ArrayList<String>) value);
                        }
                        listValues.add(p.second);
                        bundle.putStringArrayList(p.first, listValues);
                    } else {
                        bundle.putString(p.first, p.second);
                    }
                }
            }
            builder.addNetworkExtrasBundle(AdMobAdapter.class, bundle);
        }


        return builder.build();
    }

}
