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
import android.util.Pair;

import com.appnexus.opensdk.MediatedInterstitialAdView;
import com.appnexus.opensdk.MediatedInterstitialAdViewController;
import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.StringUtil;
import com.chartboost.sdk.CBLocation;
import com.chartboost.sdk.Chartboost;

import java.lang.ref.WeakReference;

/**
 * This class is the Chartboost interstitial adaptor it provides the functionality needed to allow
 * an application using the App Nexus SDK to load an interstitial ad through the Chartboost SDK. The
 * instantiation of this class is done in response from the AppNexus server for an interstitial
 * placement that is configured to use Chartboost to serve it. This class is never directly instantiated
 * by the developer.
 */
public class ChartboostInterstitial implements MediatedInterstitialAdView {
    WeakReference<Activity> weakActivity;
    String location = null;
    ChartboostListener listener;

    /**
     * Called by the AppNexus SDK to load an interstitial ad from Chartboost
     *
     * @param mIC       A controller through which the adapter must send events to the AppNexus SDK.
     * @param activity  The activity that the app launch interstitial ad from.
     * @param parameter An optional opaque string passed from the Ad Network Manager, this can be used
     *                  to define SDK-specific parameters such as additional targeting information.
     *                  The encoding of the contents of this string are entirely up to the implementation
     *                  of the third-party SDK adaptor.
     * @param uid       The network ID for this ad call.  This ID is opaque to the AppNexus SDK; the
     *                  ID's contents and their encoding are up to the implementation of the
     * @param tp        Targeting parameters passed from AN SDK. Important: Please pass in the location
     *                  by setting the customKeywords in the InterstitialAdView using key
     *                  ChartboostSetting.KEY_CHARTBOOST_LOCATION.
     */
    @Override
    public void requestAd(MediatedInterstitialAdViewController mIC, Activity activity, String parameter,
                          String uid, TargetingParameters tp) {
        weakActivity = new WeakReference<Activity>(activity);
        Chartboost.setAutoCacheAds(false);
        if (tp != null && tp.getCustomKeywords() != null) {
            for (Pair<String, String> p : tp.getCustomKeywords()) {
                if (p.first == ChartboostSettings.KEY_CHARTBOOST_LOCATION) {
                    if (!StringUtil.isEmpty(p.second)) {
                        location = p.second;
                    }
                }
            }
        }
        if (StringUtil.isEmpty(location)) {
            Clog.w(Clog.mediationLogTag, "Chartboost location was not passed in, using Default.");
            location = CBLocation.LOCATION_DEFAULT;
        }
        listener = new ChartboostListener(location, mIC);
        ChartboostDelegateBridge.getInstance().cacheInterstitialWithListener(location, listener);
    }

    @Override
    public void show() {
        if (isReady()) {
            Clog.d(Clog.mediationLogTag, "Showing Chartboost interstitial ad.");
            Chartboost.showInterstitial(location);
        } else {
            Clog.e(Clog.mediationLogTag, "Chartboost interstitial ad not ready for location " + location);
        }
    }

    @Override
    public boolean isReady() {
        return Chartboost.hasInterstitial(location);
    }

    @Override
    public void destroy() {
        ChartboostDelegateBridge.getInstance().remove(location, listener);
        listener = null;
    }

    @Override
    public void onPause() {
        Activity activity = this.weakActivity.get();
        if (activity != null) {
            Chartboost.onPause(activity);
        }
    }

    @Override
    public void onResume() {
        Activity activity = this.weakActivity.get();
        if (activity != null) {
            Chartboost.onResume(activity);
        }
    }

    @Override
    public void onDestroy() {
        Activity activity = this.weakActivity.get();
        if (activity != null) {
            Chartboost.onDestroy(activity);
        }
        destroy();
    }
}
