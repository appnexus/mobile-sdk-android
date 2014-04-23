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
import com.google.ads.AdSize;
import com.google.ads.mediation.MediationAdRequest;
import com.google.ads.mediation.customevent.CustomEventBanner;
import com.google.ads.mediation.customevent.CustomEventBannerListener;

public class AdMobMediationBanner implements CustomEventBanner, AdListener {

    CustomEventBannerListener listener;

    @Override
    public void requestBannerAd(CustomEventBannerListener listener, final Activity activity,
                                String label, String serverParameter, AdSize adSize, MediationAdRequest mediationAdRequest,
                                Object extra) {
        Clog.d(Clog.mediationLogTag, "Initializing ANBanner via AdMob SDK");
        this.listener = listener;

        BannerAdView appNexusAdView = new BannerAdView(activity);
        appNexusAdView.setPlacementID(serverParameter);
        appNexusAdView.setAdSize(adSize.getWidth(), adSize.getHeight());
        appNexusAdView.setShouldServePSAs(false);
        appNexusAdView.setAdListener(this);

        switch (mediationAdRequest.getGender()) {
            case MALE:
                appNexusAdView.setGender(AdView.GENDER.MALE);
                break;
            case FEMALE:
                appNexusAdView.setGender(AdView.GENDER.FEMALE);
                break;
            default:
                // unknown case passes nothing
                break;
        }

        if (mediationAdRequest.getAgeInYears() != null) {
            appNexusAdView.setAge(String.valueOf(mediationAdRequest.getAgeInYears()));
        }
        SDKSettings.setLocation(mediationAdRequest.getLocation());

        Clog.d(Clog.mediationLogTag, "Load ANBanner");
        appNexusAdView.loadAdOffscreen();
    }

    @Override
    public void destroy() {
    }

    // AppNexus SDK events

    @Override
    public void onAdLoaded(AdView adView) {
        Clog.d(Clog.mediationLogTag, "ANBanner loaded successfully");
        if (listener != null) listener.onReceivedAd(adView);
    }

    @Override
    public void onAdRequestFailed(AdView adView) {
        Clog.d(Clog.mediationLogTag, "ANBanner failed to load");
        if (listener != null) listener.onFailedToReceiveAd();
    }

    @Override
    public void onAdExpanded(AdView adView) {
        Clog.d(Clog.mediationLogTag, "ANBanner expanded");
        if (listener != null) listener.onPresentScreen();
    }

    @Override
    public void onAdCollapsed(AdView adView) {
        Clog.d(Clog.mediationLogTag, "ANBanner collapsed");
        if (listener != null) listener.onDismissScreen();
    }

    @Override
    public void onAdClicked(AdView adView) {
        Clog.d(Clog.mediationLogTag, "ANBanner was clicked");
        if (listener != null) listener.onClick();
    }
}
