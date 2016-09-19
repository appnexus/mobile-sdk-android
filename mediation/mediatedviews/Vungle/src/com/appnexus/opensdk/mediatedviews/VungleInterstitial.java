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

import com.appnexus.opensdk.MediatedInterstitialAdView;
import com.appnexus.opensdk.MediatedInterstitialAdViewController;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.utils.Clog;
import com.vungle.publisher.EventListener;
import com.vungle.publisher.VunglePub;

import java.lang.ref.WeakReference;

/**
 * This class is the Vungle interstitial adapter - it provides the functionality needed to allow
 * an application using the AppNexus SDK to load an interstitial ad through the Vungle publisher SDK.
 * The instantiation of this class is done in response from the AppNexus server for an interstitial
 * placement that is configured to use Vungle to serve it. This class is never directly instantiated
 * by the application.
 * <p/>
 * This class also serves as an example of how to write a Mediation adaptor for the AppNexus
 * SDK.
 */
public class VungleInterstitial implements MediatedInterstitialAdView, EventListener {
    final VunglePub vunglePub = VunglePub.getInstance();
    WeakReference<Activity> weakActivity;
    String className = this.getClass().getSimpleName();

    @Override
    public void requestAd(MediatedInterstitialAdViewController mIC, Activity activity, String parameter, String uid, TargetingParameters tp) {
        weakActivity = new WeakReference<Activity>(activity);
        vunglePub.setEventListeners(this);
        if (vunglePub.isAdPlayable()) {
            if (mIC != null) {
                mIC.onAdLoaded();
            }
        } else {
            if (mIC != null) {
                mIC.onAdFailed(ResultCode.UNABLE_TO_FILL);
            }
        }
    }

    @Override
    public void show() {
        vunglePub.playAd();
    }

    @Override
    public boolean isReady() {
        return vunglePub.isAdPlayable();
    }

    @Override
    public void destroy() {
        vunglePub.removeEventListeners(this);
    }

    @Override
    public void onPause() {
        vunglePub.onPause();
    }

    @Override
    public void onResume() {
        vunglePub.onResume();
    }

    @Override
    public void onDestroy() {
        destroy();
    }

    @Override
    public void onAdEnd(boolean wasSuccessfulView, boolean wasCallToActionClicked) {
        Clog.d(Clog.mediationLogTag, className + ": onAdEnd wasSuccessfulView " + wasSuccessfulView +
                ": wasCallToActionClicked " + wasCallToActionClicked);
    }

    @Override
    public void onAdStart() {
        Clog.d(Clog.mediationLogTag, className + ": onAdStart");
    }

    @Override
    public void onAdUnavailable(String s) {
        Clog.d(Clog.mediationLogTag, className + ": onAdUnavailable " + s);
    }

    @Override
    public void onAdPlayableChanged(boolean b) {
        Clog.d(Clog.mediationLogTag, className + ": onAdPlayableChanged " + b);

    }

    @Override
    public void onVideoView(boolean b, int i, int i2) {
        Clog.d(Clog.mediationLogTag, className +
                ": video ad ended " + b + " total watched milliseconds is " + i + " ad duration is " + i2);
    }
}
