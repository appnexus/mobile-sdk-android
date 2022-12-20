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
import com.appnexus.opensdk.utils.JsonUtil;
import com.appnexus.opensdk.utils.StringUtil;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private static final String SECOND_PRICE_KEY = "anhb";
    private AdManagerAdView adView;
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

        adView = new AdManagerAdView(activity);
        adView.setAdUnitId(adUnitID);
        adView.setAdSizes(adSize);
        adView.setAdListener(adListener);
        adView.setAppEventListener(adListener);

        adView.loadAd(buildRequest(ssparm, targetingParameters));

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

    private AdManagerAdRequest buildRequest(DFBBannerSSParameters ssparm, TargetingParameters targetingParameters) {
        AdManagerAdRequest.Builder builder = new AdManagerAdRequest.Builder();
        if ((ssparm.test_device != null) && (ssparm.test_device.length() > 0)) {
            adListener.printToClog("test device " + ssparm.test_device);
            List<String> testDeviceIds = Arrays.asList(ssparm.test_device);
            RequestConfiguration configuration =
                    new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
            MobileAds.setRequestConfiguration(configuration);
        }

        Bundle bundle = new Bundle();

        if (!StringUtil.isEmpty(ssparm.secondPrice)) {
            try {
                double secondPriceCents = Double.parseDouble(ssparm.secondPrice) * 100;
                if (secondPriceCents >= 0) {
                    String secondPriceString = "anhb_" + Math.round(secondPriceCents);
                    builder.addCustomTargeting(SECOND_PRICE_KEY, secondPriceString);
                    adListener.printToClog("second price " + secondPriceString);
                    adListener.isSecondPriceAvailable = true;
                }
            } catch (NumberFormatException e) {
                adListener.printToClogError("While parsing secondPrice value: " + e.getMessage());
            }
        }

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

    /**
     * Class to extract optional server side parameters from passed in json string.
     * Supports
     * {
     * "second_price" : "0.50"
     * "swipeable" : false,
     * "smartbanner" : true
     * }
     * Or
     * {
     * "second_price" : "0.50"
     * "swipeable" : 1,
     * "smartbanner" : 0
     * }
     */
    class DFBBannerSSParameters {

        public DFBBannerSSParameters(String parameter) {

            if (!StringUtil.isEmpty(parameter)) {
                final String SWIPEABLE = "swipeable";
                final String SMARTBANNER = "smartbanner";
                final String SECONDPRICE = "second_price";

                try {
                    JSONObject req = new JSONObject(parameter);
                    isSmartBanner = getBoolean(req, SMARTBANNER);
                    isSwipeable = getBoolean(req, SWIPEABLE);
                    secondPrice = JsonUtil.getJSONString(req, SECONDPRICE);
                } catch (JSONException e) {
                }
            }
        }

        private boolean getBoolean(JSONObject object, String key) {
            try {
                return object.getBoolean(key);
            } catch (JSONException e) {
                try {
                    int i = object.getInt(key);
                    switch (i) {
                        case 1:
                            return true;
                        case 0:
                            return false;
                    }
                } catch (JSONException e1) {
                }
            }
            return false;
        }


        public boolean isSwipeable = false;
        public String test_device;
        public boolean isSmartBanner = false;
        public String secondPrice;
    }
}
