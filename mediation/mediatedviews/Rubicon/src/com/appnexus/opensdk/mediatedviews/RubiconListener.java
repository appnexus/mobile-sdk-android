/*
 *    Copyright 2016 APPNEXUS INC
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

import android.view.View;

import com.appnexus.opensdk.MediatedAdViewController;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.utils.Clog;
import com.rfm.sdk.RFMAdView;
import com.rfm.sdk.RFMAdViewListener;
import com.rfm.sdk.RFMInterstitialAdViewListener;


public class RubiconListener implements RFMAdViewListener, RFMInterstitialAdViewListener{

    private final MediatedAdViewController mediatedAdViewController;
    private final String className;

    public RubiconListener(MediatedAdViewController mediatedAdViewController, String className) {
        this.mediatedAdViewController = mediatedAdViewController;
        this.className = className;
    }

    @Override
    public void onAdRequested(RFMAdView rfmAdView, String s, boolean b) {
        printToClog("onAdRequested - ad request url: "+s);
        rfmAdView.setVisibility(View.GONE);
    }

    @Override
    public void onAdReceived(RFMAdView rfmAdView) {
        printToClog("onAdReceived");
        rfmAdView.setVisibility(View.VISIBLE);
        if(this.mediatedAdViewController != null){
            this.mediatedAdViewController.onAdLoaded();
        }
    }

    @Override
    public void onAdFailed(RFMAdView rfmAdView) {
        printToClogError("onAdFailed");
        if(this.mediatedAdViewController != null){
            this.mediatedAdViewController.onAdFailed(ResultCode.UNABLE_TO_FILL);
        }
    }

    @Override
    public void onAdStateChangeEvent(RFMAdView rfmAdView, RFMAdViewEvent rfmAdViewEvent) {
        printToClog("onAdStateChangeEvent: "+rfmAdViewEvent.toString());
        switch(rfmAdViewEvent){
            case FULL_SCREEN_AD_DISPLAYED:
                if(this.mediatedAdViewController != null){
                    this.mediatedAdViewController.onAdExpanded();
                }
                break;
            case FULL_SCREEN_AD_DISMISSED:
                if(this.mediatedAdViewController != null){
                    this.mediatedAdViewController.onAdCollapsed();
                }
                break;
        }

    }

    @Override
    public void onAdResized(RFMAdView rfmAdView, int i, int i1) {
        printToClog("onAdResized");
    }

    @Override
    public void didDisplayAd(RFMAdView rfmAdView) {
        printToClog("didDisplayAd");
    }

    @Override
    public void didFailedToDisplayAd(RFMAdView rfmAdView, String s) {
        printToClogError("didFailedToDisplayAd: " + s);
    }

    @Override
    public void onInterstitialAdWillDismiss(RFMAdView rfmAdView) {
        printToClogError("onInterstitialAdWillDismiss");
    }

    @Override
    public void onInterstitialAdDismissed(RFMAdView rfmAdView) {
        printToClogError("onInterstitialAdDismissed");
        if(this.mediatedAdViewController != null){
            this.mediatedAdViewController.onDestroy();
        }
    }


    void printToClog(String s) {
        Clog.i(Clog.mediationLogTag, className + " - " + s);
    }

    void printToClogError(String s) {
        Clog.e(Clog.mediationLogTag, className + " - " + s);
    }

}
