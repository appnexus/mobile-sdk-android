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

import com.adcolony.sdk.AdColony;
import com.adcolony.sdk.AdColonyInterstitialListener;
import com.adcolony.sdk.AdColonyZone;
import com.appnexus.opensdk.MediatedInterstitialAdView;
import com.appnexus.opensdk.MediatedInterstitialAdViewController;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.utils.Clog;

import java.lang.ref.WeakReference;

/**
 * This class is the AdColony interstitial adapter. It provides the functionality needed to allow
 * an application using the App Nexus SDK to load an interstitial ad through the AdColony SDK. The
 * instantiation of this class is done in response from the AppNexus server for an interstitial
 * placement that is configured to use AdColony to serve it. This class is never directly instantiated
 * by the developer.
 */

public class AdColonyInterstitial implements MediatedInterstitialAdView {

    String zoneId;
    WeakReference<Activity> weakActivity;
    com.adcolony.sdk.AdColonyInterstitial ad;
    AdColonyInterstitialListener listener;
    MediatedInterstitialAdViewController controller;

    @Override
    public void requestAd(MediatedInterstitialAdViewController mIC, Activity activity, String parameter, String uid, TargetingParameters tp) {
        zoneId = uid;
        controller = mIC;
        weakActivity = new WeakReference<Activity>(activity);
        listener = getAdColonyInterstitialListener();

        // Configure AdColony if its first Time
        if (!AdColonySettings.isConfigured()) {
            Clog.d(Clog.mediationLogTag, getClass() + " - AdColony not configured configuring AdColony");
            AdColony.configure(activity, AdColonySettings.getAdColonyAppOptions(tp), AdColonySettings.appID, AdColonySettings.zoneIds);
        }

        if(AdColonySettings.isAdColonyZoneValid(zoneId)) {
            AdColony.requestInterstitial(zoneId, listener);
        }else{
            mIC.onAdFailed(ResultCode.INVALID_REQUEST);
        }
    }

    @Override
    public void show() {
        if (!this.isReady()) {
            Clog.d(Clog.mediationLogTag, getClass() + " - show called while interstitial ad view was unavailable");
            return;
        }

        boolean success = ad.show();

        if (success) {
            Clog.d(Clog.mediationLogTag, getClass() + " - display called successfully");
        } else {
            Clog.d(Clog.mediationLogTag, getClass() + " - display call failed");
        }

    }

    @Override
    public boolean isReady() {
        if (ad != null && !ad.isExpired()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void destroy() {
        if (ad != null) {
            listener = null;
            ad.setListener(null);
            ad.destroy();
            ad = null;
        }
    }

    @Override
    public void onPause() {
        //AdColony lacks a pause api
    }

    @Override
    public void onResume() {
        //AdColony lacks a Resume api
    }

    @Override
    public void onDestroy() {
        destroy();
    }


    private AdColonyInterstitialListener getAdColonyInterstitialListener() {
        if (listener != null) {
            return listener;
        } else {
            return new AdColonyInterstitialListener() {
                @Override
                public void onRequestFilled(com.adcolony.sdk.AdColonyInterstitial interstitial) {
                    ad = interstitial;
                    Clog.d(Clog.mediationLogTag, getClass() + " - onRequestFilled");
                    if (controller != null) {
                        controller.onAdLoaded();
                    }
                }

                @Override
                public void onRequestNotFilled(AdColonyZone zone) {
                    Clog.e(Clog.mediationLogTag, getClass() + " - onRequestNotFilled");
                    if (controller != null) {
                        controller.onAdFailed(ResultCode.UNABLE_TO_FILL);
                    }
                }

                @Override
                public void onClosed(com.adcolony.sdk.AdColonyInterstitial ad) {
                    Clog.d(Clog.mediationLogTag, getClass() + " - onClosed");
                    if (controller != null) {
                        controller.onAdCollapsed();
                    }
                }

                @Override
                public void onOpened(com.adcolony.sdk.AdColonyInterstitial ad) {
                    Clog.d(Clog.mediationLogTag, getClass() + " - onOpened");
                    if (controller != null) {
                        controller.onAdExpanded();
                    }
                }

                @Override
                public void onExpiring(com.adcolony.sdk.AdColonyInterstitial ad) {
                    Clog.d(Clog.mediationLogTag, getClass() + " - onExpiring:: Requesting a new Ad");
                    AdColony.requestInterstitial(ad.getZoneID(), listener);
                }

                @Override
                public void onLeftApplication(com.adcolony.sdk.AdColonyInterstitial ad) {
                    Clog.d(Clog.mediationLogTag, getClass() + " - onLeftApplication");
                }

                @Override
                public void onClicked(com.adcolony.sdk.AdColonyInterstitial ad) {
                    Clog.d(Clog.mediationLogTag, getClass() + " - onClicked");
                    if (controller != null) {
                        controller.onAdClicked();
                    }
                }
            };
        }
    }
}
