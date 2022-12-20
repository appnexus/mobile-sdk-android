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
import android.os.Bundle;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.appnexus.opensdk.MediatedInterstitialAdView;
import com.appnexus.opensdk.MediatedInterstitialAdViewController;
import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.utils.StringUtil;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * This class is the Google AdMob interstitial adaptor it provides the functionality needed to allow
 * an application using the App Nexus SDK to load a banner ad through the Google SDK. The instantiation
 * of this class is done in response from the AppNexus server for a banner placement that is configured
 * to use AdMob to serve it. This class is never instantiated by the developer.
 * <p/>
 * This class also serves as an example of how to write a Mediation adaptor for the AppNexus
 * SDK.
 */
public class GooglePlayServicesInterstitial implements MediatedInterstitialAdView {
    private InterstitialAd interstitialAd;
    private GooglePlayAdListener adListener;
    private WeakReference<Activity> activityWeakReference;

    @Override
    public void requestAd(MediatedInterstitialAdViewController mIC, Activity activity,
                          String parameter, String adUnitId, TargetingParameters targetingParameters) {
        adListener = new GooglePlayAdListener(mIC, super.getClass().getSimpleName());
        adListener.printToClog(String.format(" - requesting an ad: [%s, %s]", parameter, adUnitId));
        activityWeakReference = new WeakReference<>(activity);

        try {
            InterstitialAd.load(activity, adUnitId, buildRequest(targetingParameters), new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    super.onAdLoaded(interstitialAd);
                    GooglePlayServicesInterstitial.this.interstitialAd = interstitialAd;
                    adListener.onAdLoaded();
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    adListener.onAdFailedToLoad(loadAdError);
                }
            });
        } catch (NoClassDefFoundError e) {
            // This can be thrown by Play Services on Honeycomb.
            adListener.onAdFailedToLoad(new LoadAdError(AdRequest.ERROR_CODE_NO_FILL, e.getMessage(), "", null, null));
        }
    }

    @Override
    public void show() {
        adListener.printToClog("show called");
        if (interstitialAd == null) {
            adListener.printToClogError("show called while interstitial ad view was null");
            return;
        }

        if (activityWeakReference != null && activityWeakReference.get() != null) {
            interstitialAd.show(activityWeakReference.get());
            adListener.printToClog("interstitial ad shown");
        } else {
            adListener.printToClog("Activity already garbage collected");
        }
    }

    @Override
    public boolean isReady() {
        return (interstitialAd != null);
    }

    private AdRequest buildRequest(TargetingParameters targetingParameters) {
        AdRequest.Builder builder = new AdRequest.Builder();

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

        //Since AdMobExtras is deprecated so we need to use below method
        builder.addNetworkExtrasBundle(com.google.ads.mediation.admob.AdMobAdapter.class, bundle);

        return builder.build();
    }

    @Override
    public void destroy() {
        if(interstitialAd!=null){
            interstitialAd=null;
            adListener=null;
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
