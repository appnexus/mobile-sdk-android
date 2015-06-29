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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.amazon.device.ads.AdLayout;
import com.amazon.device.ads.AdSize;
import com.amazon.device.ads.AdTargetingOptions;
import com.appnexus.opensdk.MediatedBannerAdView;
import com.appnexus.opensdk.MediatedBannerAdViewController;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.TargetingParameters;

/**
 * This class is the Amazon banner adaptor it provides the functionality needed to allow
 * an application using the AppNexus SDK to load a banner ad through the Amazon API. The instantiation
 * of this class is done in response from the AppNexus server for a banner placement, that is configured
 * to use the Amazon Network to fill. This class is never instantiated by the developer.
 */
public class AmazonBanner implements MediatedBannerAdView {
    AmazonListener amazonListener = null;
    AdLayout adView = null;

    /**
     * Called by the AN SDK to request a Banner ad from the Amazon SDK. .
     *
     * @param mBC       the object which will be called with events from the Amazon SDK
     * @param activity  the activity from which this is launched
     * @param parameter String parameter received from the server for instantiation of this object
     *                  optional server side parameters to control this call.
     * @param uid       The 3rd party placement , in adMob this is the adUnitID
     * @param width     Width of the ad
     * @param height    Height of the ad
     */
    @Override
    public void requestAd(MediatedBannerAdViewController mBC, Activity activity, String parameter, String uid, int width, int height, TargetingParameters tp) {
        adView = new AdLayout(activity, new AdSize(width, height));

        this.amazonListener = new AmazonListener(mBC, AmazonBanner.class.getSimpleName());

        adView.setListener(this.amazonListener);

        AdTargetingOptions targetingOptions = AmazonTargeting.createTargeting(adView, tp, parameter);

        //Amazon won't load ads unless layout parameters are set
        adView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));

        if (mBC != null) {
            mBC.setView(adView);
            if (!adView.loadAd(targetingOptions)) {
                this.amazonListener.printToClogError("loadAd() call rejected");
                mBC.onAdFailed(ResultCode.UNABLE_TO_FILL);
                adView = null;
            }
        }
    }

    /**
     * The interface called by the AN SDK to destroy a mediated banner.
     */
    @Override
    public void destroy() {
        if (adView != null) {
            try {
                adView.setListener(null);
            } catch (NullPointerException npe) {
                //This only seems to happen in interstitials
                //catch to be safe
            }
            adView.destroy();
            amazonListener = null;
            adView = null;
        }
    }

    @Override
    public void onPause() {
        //Amazon has no onPause function!
    }

    @Override
    public void onResume() {
        //Amazon has no onResume function!
    }

    @Override
    public void onDestroy() {
        destroy();
    }

}
