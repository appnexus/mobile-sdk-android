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

package com.appnexus.opensdk;

import android.app.Activity;
import com.appnexus.opensdk.utils.Clog;
import com.google.ads.mediation.MediationAdRequest;
import com.google.ads.mediation.customevent.CustomEventInterstitial;
import com.google.ads.mediation.customevent.CustomEventInterstitialListener;

public class AdMobMediationInterstitial implements CustomEventInterstitial, AdListener {
    InterstitialAdView iav;
    CustomEventInterstitialListener listener;

    @Override
    public void requestInterstitialAd(CustomEventInterstitialListener listener,
                                      Activity activity, String label, String placement_id, MediationAdRequest mediationAdRequest,
                                      Object extra) {
        Clog.d(Clog.mediationLogTag, "Initializing ANInterstitial via AdMob SDK");
        this.listener = listener;

        iav = new InterstitialAdView(activity);
        iav.setPlacementID(placement_id);
        iav.setShouldServePSAs(false);
        iav.setAdListener(this);

        switch (mediationAdRequest.getGender()) {
            case MALE:
                iav.setGender(AdView.GENDER.MALE);
                break;
            case FEMALE:
                iav.setGender(AdView.GENDER.FEMALE);
                break;
            default:
                // unknown case passes nothing
                break;
        }
        if (mediationAdRequest.getAgeInYears() != null) {
            iav.setAge(String.valueOf(mediationAdRequest.getAgeInYears()));
        }
        SDKSettings.setLocation(mediationAdRequest.getLocation());


        Clog.d(Clog.mediationLogTag, "Fetch ANInterstitial");
        iav.loadAd();
    }

    @Override
    public void destroy() {
        if (iav != null) iav.destroy();
    }

    @Override
    public void showInterstitial() {
        if (iav != null) iav.show();
    }

    // AppNexus SDK events

    @Override
    public void onAdLoaded(AdView adView) {
        Clog.d(Clog.mediationLogTag, "ANInterstitial loaded successfully");
        if (listener != null) listener.onReceivedAd();
    }

    @Override
    public void onAdRequestFailed(AdView adView, ResultCode resultCode) {
        Clog.d(Clog.mediationLogTag, "ANInterstitial failed to load: " + resultCode);
        if (listener != null) listener.onFailedToReceiveAd();
    }

    @Override
    public void onAdExpanded(AdView adView) {
        Clog.d(Clog.mediationLogTag, "ANInterstitial expanded");
        if (listener != null) listener.onPresentScreen();
    }

    @Override
    public void onAdCollapsed(AdView adView) {
        Clog.d(Clog.mediationLogTag, "ANInterstitial collapsed");
        if (listener != null) listener.onDismissScreen();
    }

    @Override
    public void onAdClicked(AdView adView) {
        Clog.d(Clog.mediationLogTag, "ANInterstitial was clicked");
        if (listener != null) listener.onLeaveApplication();
    }

}
