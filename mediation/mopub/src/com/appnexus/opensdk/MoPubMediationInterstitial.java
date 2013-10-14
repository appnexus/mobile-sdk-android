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

import android.content.Context;
import com.mopub.mobileads.CustomEventInterstitial;
import com.mopub.mobileads.MoPubErrorCode;

import java.util.Map;

public class MoPubMediationInterstitial extends CustomEventInterstitial implements AdListener {
    InterstitialAdView iad;
    public static final String APID_KEY = "id";
    CustomEventInterstitialListener listener;

    @Override
    protected void loadInterstitial(Context context, CustomEventInterstitialListener customEventInterstitialListener, Map<String, Object> localExtras, Map<String, String> serverExtras) {
        listener = customEventInterstitialListener;
        String apid;
        if (extrasAreValid(serverExtras)) {
            apid = serverExtras.get(APID_KEY);
        } else {
            listener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        iad = new InterstitialAdView(context);
        iad.setPlacementID(apid);
        iad.setAdListener(this);
        iad.loadAd();
    }

    private boolean extrasAreValid(Map<String, String> serverExtras) {
        return serverExtras.containsKey(APID_KEY);
    }

    @Override
    protected void showInterstitial() {
        if (iad != null)
            iad.show();
    }

    @Override
    protected void onInvalidate() {

    }

    @Override
    public void onAdLoaded(AdView adView) {
        if (listener != null) listener.onInterstitialLoaded();
    }

    @Override
    public void onAdRequestFailed(AdView adView) {
        if (listener != null) listener.onInterstitialFailed(MoPubErrorCode.UNSPECIFIED);
    }

    @Override
    public void onAdExpanded(AdView adView) {
        if (listener != null) listener.onInterstitialShown();
    }

    @Override
    public void onAdCollapsed(AdView adView) {
        if (listener != null) listener.onInterstitialDismissed();
    }

    @Override
    public void onAdClicked(AdView adView) {
        if (listener != null) listener.onInterstitialClicked();
    }
}
