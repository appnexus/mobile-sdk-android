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

import com.appnexus.opensdk.MediatedBannerAdViewController;
import com.appnexus.opensdk.MediatedInterstitialAdView;
import com.appnexus.opensdk.MediatedInterstitialAdViewController;
import com.appnexus.opensdk.TargetingParameters;
import com.flurry.android.FlurryAgent;
import com.flurry.android.ads.FlurryAdErrorType;
import com.flurry.android.ads.FlurryAdInterstitial;
import com.flurry.android.ads.FlurryAdInterstitialListener;

import java.lang.ref.WeakReference;

/**
 * This class is the Yahoo Flurry interstitial adapter. It provides the functionality needed to allow
 * an application using the AppNexus SDK to load an interstitial ad through the Yahoo Flurry SDK. The
 * instantiation of this class is done in response from the AppNexus server for an interstitial
 * placement that is configured to use Yahoo to serve it. This class is never directly instantiated
 * by the developer.
 */

public class YahooFlurryInterstitial implements MediatedInterstitialAdView, FlurryAdInterstitialListener {

    private FlurryAdInterstitial mInterstitial;
    private WeakReference<MediatedInterstitialAdViewController> controller;
    private WeakReference<Activity> activityWeak;

    @Override
    public void requestAd(MediatedInterstitialAdViewController mIC, Activity activity, String parameter, String uid, TargetingParameters tp) {
        if (mIC != null) {
            this.controller = new WeakReference<MediatedInterstitialAdViewController>(mIC);
            this.activityWeak = new WeakReference<Activity>(activity);
            mInterstitial = new FlurryAdInterstitial(activity, uid);
            mInterstitial.setTargeting(YahooFlurrySettings.getFlurryAdTargeting(tp));
            mInterstitial.setListener(this);
            mInterstitial.fetchAd();
        }
    }

    @Override
    public void show() {
        if (isReady()) {
            mInterstitial.displayAd();
        }
    }

    @Override
    public boolean isReady() {
        if (mInterstitial != null) {
            return mInterstitial.isReady();
        }
        return false;
    }

    @Override
    public void destroy() {
        if (mInterstitial != null) {
            mInterstitial.setListener(null);
            mInterstitial.destroy();
            mInterstitial = null;
        }
    }

    @Override
    public void onPause() {
        Activity activity = this.activityWeak.get();
        if (activity != null) {
            FlurryAgent.onEndSession(activity);
        }
    }

    @Override
    public void onResume() {
        Activity activity = this.activityWeak.get();
        if (activity != null) {
            FlurryAgent.onStartSession(activity);
        }
    }

    @Override
    public void onDestroy() {
        destroy();
    }

    @Override
    public void onFetched(FlurryAdInterstitial flurryAdInterstitial) {
        MediatedInterstitialAdViewController controller = this.controller.get();
        if (controller != null) {
            controller.onAdLoaded();
        }
    }

    @Override
    public void onRendered(FlurryAdInterstitial flurryAdInterstitial) {

    }

    @Override
    public void onDisplay(FlurryAdInterstitial flurryAdInterstitial) {
    }

    @Override
    public void onClose(FlurryAdInterstitial flurryAdInterstitial) {
        MediatedInterstitialAdViewController controller = this.controller.get();
        if (controller != null) {
            controller.onAdCollapsed();
        }
    }

    @Override
    public void onAppExit(FlurryAdInterstitial flurryAdInterstitial) {

    }

    @Override
    public void onClicked(FlurryAdInterstitial flurryAdInterstitial) {
        MediatedInterstitialAdViewController controller = this.controller.get();
        if (controller != null) {
            controller.onAdClicked();
        }
    }

    @Override
    public void onVideoCompleted(FlurryAdInterstitial flurryAdInterstitial) {

    }

    @Override
    public void onError(FlurryAdInterstitial flurryAdInterstitial, FlurryAdErrorType flurryAdErrorType, int i) {
        MediatedInterstitialAdViewController controller = this.controller.get();
        if (controller != null) {
            controller.onAdFailed(YahooFlurrySettings.errorCodeMapping(flurryAdErrorType, i));
        }
    }
}
