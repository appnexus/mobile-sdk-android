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

import com.appnexus.opensdk.MediatedInterstitialAdView;
import com.appnexus.opensdk.MediatedInterstitialAdViewController;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.TargetingParameters;
import com.millennialmedia.InterstitialAd;
import com.millennialmedia.MMException;
import com.millennialmedia.MMSDK;
import com.millennialmedia.UserData;

import java.lang.ref.WeakReference;

/**
 * This class is the Millennial Media interstitial adaptor it provides the functionality needed to allow
 * an application using the App Nexus SDK to load an interstitial ad through the Millennial Media SDK. The instantiation
 * of this class is done in response from the AppNexus server for a banner placement that is configured
 * to use MM  to serve it. This class is never instantiated by the developer.
 * <p/>
 * This class also serves as an example of how to write a Mediation adaptor for the AppNexus
 * SDK.
 */
public class MillennialMediaInterstitial implements MediatedInterstitialAdView {
    private InterstitialAd interstitialAd;
    private MillennialMediaListener mmListener;
    private WeakReference<Activity> weakActivity;

    @Override
    public void requestAd(MediatedInterstitialAdViewController mIC, Activity activity, String parameter, String uid, TargetingParameters targetingParameters) {
        if (activity != null) {
            weakActivity = new WeakReference<Activity>(activity);
            mmListener = new MillennialMediaListener(mIC, super.getClass().getSimpleName());
            mmListener.printToClog(String.format("requesting an interstitial ad: [%s, %s]", parameter, uid));

            MMSDK.initialize(activity);
            if (!MillennialMediaSettings.siteId.isEmpty()) {
                MMSDK.setAppInfo(MillennialMediaSettings.getAppInfo());
            }
            // SDK must be initialized first before creating userdata instance
            UserData userData = MillennialMediaSettings.getUserData(targetingParameters, activity);
            try {
                interstitialAd = InterstitialAd.createInstance(uid);
                interstitialAd.setListener(mmListener);
                MMSDK.setUserData(userData);
                interstitialAd.load(activity, new InterstitialAd.InterstitialAdMetadata());
            } catch (MMException e) {
                if (mIC != null) {
                    mIC.onAdFailed(ResultCode.INTERNAL_ERROR);
                }
            }

        }
    }

    @Override
    public void show() {
        mmListener.printToClog("show called");
        if (interstitialAd == null) {
            mmListener.printToClogError("show called while interstitial ad view was null");
            return;
        }
        if (!interstitialAd.isReady()) {
            mmListener.printToClogError("show called while interstitial ad view was unavailable");
            return;
        }
        Activity activity = weakActivity.get();
        if (activity != null) {
            try {
                interstitialAd.show(activity);
                return;
            } catch (MMException e) {
            }
        }
        mmListener.printToClogError("display call failed");
    }

    @Override
    public boolean isReady() {
        if (interstitialAd != null) {
            return interstitialAd.isReady();
        }
        return false;
    }

    @Override
    public void destroy() {
        if (interstitialAd != null) {
            interstitialAd.setListener(null);
            interstitialAd = null;
            mmListener = null;
        }
    }

    @Override
    public void onPause() {
        // No public API call available
    }

    @Override
    public void onResume() {
        // No public API call available
    }

    @Override
    public void onDestroy() {
        destroy();
    }
}