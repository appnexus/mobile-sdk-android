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

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import com.appnexus.opensdk.MediatedBannerAdView;
import com.appnexus.opensdk.MediatedBannerAdViewController;
import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.utils.Clog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.mediation.admob.AdMobExtras;

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
    private Activity adViewActivity;
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
            adListener.onAdFailedToLoad(AdRequest.ERROR_CODE_NO_FILL);
        }

        adViewActivity = activity;

        registerActivityCallbacks();

        return adView;
    }

    @Override
    public void destroy() {
        if (adView != null) adView.destroy();
        if ((adViewActivity != null) && (activityListener != null)) {
            if (Build.VERSION.SDK_INT > 13) {
                adViewActivity.getApplication().unregisterActivityLifecycleCallbacks(activityListener);
            }
        }
    }

    private AdRequest buildRequest(TargetingParameters targetingParameters) {
        AdRequest.Builder builder = new AdRequest.Builder();

        switch (targetingParameters.getGender()) {
            case UNKNOWN:
                builder.setGender(AdRequest.GENDER_UNKNOWN);
                break;
            case FEMALE:
                builder.setGender(AdRequest.GENDER_FEMALE);
                break;
            case MALE:
                builder.setGender(AdRequest.GENDER_MALE);
                break;
        }

        Bundle bundle = new Bundle();

        if (targetingParameters.getAge() != null) {
            bundle.putString("Age", targetingParameters.getAge());
        }
        if (targetingParameters.getLocation() != null) {
            builder.setLocation(targetingParameters.getLocation());
        }
        for (Pair<String, String> p : targetingParameters.getCustomKeywords()) {
            bundle.putString(p.first, p.second);
        }

        builder.addNetworkExtras(new AdMobExtras(bundle));

        return builder.build();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void registerActivityCallbacks() {
        if (Build.VERSION.SDK_INT > 13) {
            activityListener = new Application.ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

                }

                @Override
                public void onActivityStarted(Activity activity) {

                }

                @Override
                public void onActivityResumed(Activity activity) {
                    if (adViewActivity == activity) {
                        Clog.d(Clog.mediationLogTag, "GooglePlayServicesBanner - onActivityResumed");
                        if (adView != null) adView.resume();
                    }
                }

                @Override
                public void onActivityPaused(Activity activity) {
                    if (adViewActivity == activity) {
                        Clog.d(Clog.mediationLogTag, "GooglePlayServicesBanner - onActivityPaused");
                        if (adView != null) adView.pause();
                    }
                }

                @Override
                public void onActivityStopped(Activity activity) {

                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

                }

                @Override
                public void onActivityDestroyed(Activity activity) {
                    if (adViewActivity == activity) {
                        Clog.d(Clog.mediationLogTag, "GooglePlayServicesBanner - onActivityDestroyed");
                        if (adView != null) adView.destroy();
                    }
                    activity.getApplication().unregisterActivityLifecycleCallbacks(this);
                }
            };

            adViewActivity.getApplication().registerActivityLifecycleCallbacks(activityListener);
        }
    }
}
