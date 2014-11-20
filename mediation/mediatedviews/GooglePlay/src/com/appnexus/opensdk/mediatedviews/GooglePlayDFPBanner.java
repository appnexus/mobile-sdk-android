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
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.ads.mediation.admob.AdMobExtras;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class is the Google DFP banner adaptor it provides the functionality needed to allow
 * an application using the App Nexus SDK to load a banner ad through the Google/DFP SDK. The instantiation
 * of this class is done in response from the AppNexus server for a banner placement that is configured
 * to use DFP to serve it. This class is never instantiated by the developer.
 * <p/>
 * This class also serves as an example of how to write a Mediation adaptor for the AppNexus
 * SDK.
 */
public class GooglePlayDFPBanner implements MediatedBannerAdView {
    private PublisherAdView adView;
    private Activity adViewActivity;
    private Application.ActivityLifecycleCallbacks activityListener;
    private GooglePlayAdListener adListener;

    /**
     * Interface called by the AN SDK to request an ad from the mediating SDK.
     *
     * @param mBC                 the object which will be called with events from the 3rd party SDK
     * @param activity            the activity from which this is launched
     * @param parameter           String parameter received from the server for instantiation of this object
     * @param adUnitID            The 3rd party placement, in DFP this is the adUnitID
     * @param width               Width of the ad
     * @param height              Height of the ad
     * @param targetingParameters targetingParameters
     */
    @Override
    public View requestAd(MediatedBannerAdViewController mBC, Activity activity, String parameter, String adUnitID,
                          int width, int height, TargetingParameters targetingParameters) {
        adListener = new GooglePlayAdListener(mBC, super.getClass().getSimpleName());
        adListener.printToClog(String.format(" - requesting an ad: [%s, %s, %dx%d]",
                parameter, adUnitID, width, height));

        DFBBannerSSParameters ssparm = new DFBBannerSSParameters(parameter);
        AdSize adSize = ssparm.isSmartBanner ? AdSize.SMART_BANNER : new AdSize(width, height);

        adView = new PublisherAdView(activity);
        adView.setAdUnitId(adUnitID);
        adView.setAdSizes(adSize);
        adView.setAdListener(adListener);

        adView.loadAd(buildRequest(ssparm, targetingParameters));

        adViewActivity = activity;

        registerActivityCallbacks();

        return adView;
    }

    @Override
    public void destroy() {
        if (adView != null){
            adView.destroy();
            adView.setAdListener(null);
        }
        if ((adViewActivity != null) && (activityListener != null)) {
            if (Build.VERSION.SDK_INT > 13) {
                adViewActivity.getApplication().unregisterActivityLifecycleCallbacks(activityListener);
            }
        }
        adListener=null;
        adView=null;
    }

    @Override
    public void onPause() {
        if(adView!=null){
            adView.pause();
        }
    }

    @Override
    public void onResume() {
        if(adView!=null) {
            adView.resume();
        }
    }

    @Override
    public void onDestroy() {
        destroy();
    }

    private PublisherAdRequest buildRequest(DFBBannerSSParameters ssparm, TargetingParameters targetingParameters) {
        PublisherAdRequest.Builder builder = new PublisherAdRequest.Builder();
        if ((ssparm.test_device != null) && (ssparm.test_device.length() > 0)) {
            adListener.printToClog("test device " + ssparm.test_device);
            builder.addTestDevice(ssparm.test_device);
        }

        switch (targetingParameters.getGender()) {
            case UNKNOWN:
                builder.setGender(PublisherAdRequest.GENDER_UNKNOWN);
                break;
            case FEMALE:
                builder.setGender(PublisherAdRequest.GENDER_FEMALE);
                break;
            case MALE:
                builder.setGender(PublisherAdRequest.GENDER_MALE);
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

    /**
     * Class to extract optional server side parameters from passed in json string.
     * Supports
     * {
     * "swipeable" : 1,
     * "smartbanner" : 1
     * }
     */
    class DFBBannerSSParameters {

        public DFBBannerSSParameters(String parameter) {
            final String SWIPEABLE = "swipeable";
            final String SMARTBANNER = "smartbanner";

            do {
                JSONObject req = null;
                if (parameter == null || parameter.length() == 0) {
                    break;
                }
                try {
                    req = new JSONObject(parameter);
                } catch (JSONException e) {
                    // This is optional
                } finally {
                    if (req == null) {
                        break;
                    }
                }

                try {
                    isSwipeable = req.getBoolean(SWIPEABLE);
                } catch (JSONException e) {
                }
                try {
                    isSmartBanner = req.getBoolean(SMARTBANNER);
                } catch (JSONException e) {
                }

            } while (false);
        }


        public boolean isSwipeable;
        public String test_device;
        public boolean isSmartBanner;
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
                        Clog.d(Clog.mediationLogTag, "GooglePlayDFPBanner - onActivityResumed");
                        if (adView != null) adView.resume();
                    }
                }

                @Override
                public void onActivityPaused(Activity activity) {
                    if (adViewActivity == activity) {
                        Clog.d(Clog.mediationLogTag, "GooglePlayDFPBanner - onActivityPaused");
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
                        Clog.d(Clog.mediationLogTag, "GooglePlayDFPBanner - onActivityDestroyed");
                        if (adView != null) adView.destroy();
                    }
                    activity.getApplication().unregisterActivityLifecycleCallbacks(this);
                }
            };

            adViewActivity.getApplication().registerActivityLifecycleCallbacks(activityListener);
        }
    }
}
