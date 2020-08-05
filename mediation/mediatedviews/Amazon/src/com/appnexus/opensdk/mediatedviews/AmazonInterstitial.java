/*
 *    Copyright 2014 APPNEXUS INC
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

import com.amazon.device.ads.AdTargetingOptions;
import com.amazon.device.ads.InterstitialAd;
import com.appnexus.opensdk.MediatedInterstitialAdView;
import com.appnexus.opensdk.MediatedInterstitialAdViewController;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.TargetingParameters;



/**
 * This class is the Amazon interstitial adaptor it provides the functionality needed to allow
 * an application using the App Nexus SDK to load an interstitial ad through the Amazon SDK. The
 * instantiation of this class is done in response from the AppNexus server for an interstitial
 * placement that is configured to use Amazon to serve it. This class is never directly instantiated
 * by the developer.
 */
public class AmazonInterstitial implements MediatedInterstitialAdView {

    InterstitialAd iad;
    AmazonListener amazonListener;

    /**
     * Called by the AN SDK to request an Interstitial ad from the Amazon SDK. .
     *
     * @param mIC       the object which will be called with events from the Amazon SDK
     * @param activity  the activity from which this is launched
     * @param parameter String parameter received from the server for instantiation of this object
     *      optional server side parameters to control this call.
     * @param uid       The 3rd party placement , in adMob this is the adUnitID
     */
    @Override
    public void requestAd(MediatedInterstitialAdViewController mIC, Activity activity, String parameter, String uid, TargetingParameters tp) {
        this.iad = new InterstitialAd(activity);
        this.amazonListener = new AmazonListener(mIC,AmazonBanner.class.getSimpleName());
        this.iad.setListener(this.amazonListener);

        AdTargetingOptions targetingOptions = AmazonTargeting.createTargeting(this.iad, tp, parameter);

        if (!this.iad.loadAd(targetingOptions)) {
            if (mIC != null) {
                mIC.onAdFailed(ResultCode.getNewInstance(ResultCode.UNABLE_TO_FILL));
            }
            this.iad = null;
        }
    }

    /**
     * This is called in response to the application calling InterstitialAdView.show();
     */
    @Override
    public void show() {

        if (this.iad != null && !this.iad.isShowing()) {
            if ( this.iad.showAd()) {
                this.amazonListener.printToClog("show() called ad is now showing");
            } else {
                this.amazonListener.printToClogError("show() called showAd returned failure");
            }
        } else {
            if (this.iad == null) {
                this.amazonListener.printToClogError("show() called on a failed Interstitial");
            } else if (!this.iad.isShowing()) {
                this.amazonListener.printToClogError("show() called on a failed Interstitial");
            } else {
                this.amazonListener.printToClogError("show() failed");
            }
        }
    }

    @Override
    public boolean isReady() {
        boolean ready = iad != null && !iad.isLoading();
        this.amazonListener.printToClog("isReady() returned " + ready);
        return ready;
    }

    @Override
    public void destroy() {
        try{
            iad.setListener(null);
        }catch(NullPointerException npe){
            //Catch NPE until amazon updates SDK to handle nullness
        }
        iad=null;
        amazonListener=null;
    }

    @Override
    public void onPause() {
        //No public api
    }

    @Override
    public void onResume() {
        //No public api
    }

    @Override
    public void onDestroy() {
        destroy();
    }
}
