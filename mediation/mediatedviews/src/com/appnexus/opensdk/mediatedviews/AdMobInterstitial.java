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

import android.util.Pair;
import com.appnexus.opensdk.MediatedAdViewController;
import com.appnexus.opensdk.MediatedInterstitialAdView;
import com.appnexus.opensdk.MediatedInterstitialAdViewController;
import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.utils.Clog;
import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.InterstitialAd;
import com.google.ads.mediation.admob.AdMobAdapterExtras;


/**
 * This class is the Google AdMob interstitial adaptor it provides the functionality needed to allow
 * an application using the App Nexus SDK to load a banner ad through the Google SDK. The instantiation
 * of this class is done in response from the AppNexus server for a banner placement that is configured
 * to use AdMob to serve it. This class is never instantiated by the developer.
 *
 * This class also serves as an example of how to write a Mediation adaptor for the AppNexus
 * SDK.
 *
 */
public class AdMobInterstitial implements MediatedInterstitialAdView,
        AdListener {
    private InterstitialAd iad;
    private MediatedInterstitialAdViewController mMediatedInterstitialAdViewController;

    public AdMobInterstitial() {
        super();
    }

    @Override
    public void requestAd(MediatedInterstitialAdViewController mIC, Activity activity, String parameter, String uid, TargetingParameters targetingParameters) {
        Clog.d(Clog.mediationLogTag, String.format("AdMobInterstitial - requesting an ad: [%s, %s]", parameter, uid));

        iad = new InterstitialAd(activity, uid);

        AdRequest ar = new AdRequest();


        switch(targetingParameters.getGender()){
            case UNKNOWN:
                break;
            case FEMALE:
                ar.setGender(AdRequest.Gender.FEMALE);
                break;
            case MALE:
                ar.setGender(AdRequest.Gender.MALE);
                break;
        }
        AdMobAdapterExtras extras = new AdMobAdapterExtras();
        if(targetingParameters.getAge()!=null){
            extras.addExtra("Age", targetingParameters.getAge());
        }

        for(Pair<String, String> p : targetingParameters.getCustomKeywords()){
            extras.addExtra(p.first, p.second);
        }
        ar.setNetworkExtras(extras);
        if(targetingParameters.getLocation()!=null){
            ar.setLocation(targetingParameters.getLocation());
        }
        iad.setAdListener(this);

        iad.loadAd(ar);
        mMediatedInterstitialAdViewController = mIC;
    }

    @Override
    public void onReceiveAd(Ad ad) {
        Clog.d(Clog.mediationLogTag, "AdMobInterstitial - onReceiveAd: " + ad);
        mMediatedInterstitialAdViewController.onAdLoaded();
    }

    @Override
    public void onDismissScreen(Ad arg0) {
        Clog.d(Clog.mediationLogTag, "AdMobInterstitial - onDismissScreen: " + arg0);
    }

    @Override
    public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
        Clog.d(Clog.mediationLogTag, String.format("AdMobInterstitial - onFailedToReceiveAd: %s with error: %s", arg0, arg1));

        MediatedAdViewController.RESULT code = MediatedAdViewController.RESULT.INTERNAL_ERROR;

        switch (arg1) {
            case INTERNAL_ERROR:
                code = MediatedAdViewController.RESULT.INTERNAL_ERROR;
                break;
            case INVALID_REQUEST:
                code = MediatedAdViewController.RESULT.INVALID_REQUEST;
                break;
            case NETWORK_ERROR:
                code = MediatedAdViewController.RESULT.NETWORK_ERROR;
                break;
            case NO_FILL:
                code = MediatedAdViewController.RESULT.UNABLE_TO_FILL;
                break;
            default:
                break;
        }

        if (mMediatedInterstitialAdViewController != null) {
            mMediatedInterstitialAdViewController.onAdFailed(code);
        }
    }

    @Override
    public void onLeaveApplication(Ad arg0) {
        Clog.d(Clog.mediationLogTag, "AdMobInterstitial - onLeaveApplication: " + arg0);
        if (mMediatedInterstitialAdViewController != null) {
            mMediatedInterstitialAdViewController.onAdClicked();
        }
    }

    @Override
    public void onPresentScreen(Ad arg0) {
        Clog.d(Clog.mediationLogTag, "AdMobInterstitial - onPresentScreen: " + arg0);
    }

    @Override
    public void show() {
        Clog.d(Clog.mediationLogTag, "AdMobInterstitial - show called");
        if (iad == null) {
            Clog.e(Clog.mediationLogTag, "AdMobInterstitial - show called while interstitial ad view was null");
            return;
        }
        if (!iad.isReady()) {
            Clog.e(Clog.mediationLogTag, "AdMobInterstitial - show called while interstitial ad was not ready");
            return;
        }

        iad.show();
        Clog.d(Clog.mediationLogTag, "AdMobInterstitial - interstitial ad shown");
    }

    @Override
    public boolean isReady() {
        return (iad != null) && (iad.isReady());
    }

}
