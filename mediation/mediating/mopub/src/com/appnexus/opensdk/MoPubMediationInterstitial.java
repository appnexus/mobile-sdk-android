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
import com.appnexus.opensdk.utils.Clog;
import com.mopub.mobileads.CustomEventInterstitial;
import com.mopub.mobileads.MoPubErrorCode;

import java.util.Map;

public class MoPubMediationInterstitial extends CustomEventInterstitial implements AdListener {
    InterstitialAdView iad;
    public static final String PLACEMENTID_KEY = "id";
    CustomEventInterstitialListener listener;

    @Override
    protected void loadInterstitial(Context context, CustomEventInterstitialListener customEventInterstitialListener, Map<String, Object> localExtras, Map<String, String> serverExtras) {
        Clog.d(Clog.mediationLogTag, "Initializing ANInterstitial via MoPub SDK");
        listener = customEventInterstitialListener;
        String placementID;
        if (extrasAreValid(serverExtras)) {
            placementID = serverExtras.get(PLACEMENTID_KEY);
            Clog.d(Clog.mediationLogTag, String.format("Server extras were valid: placementID: %s", placementID));
        } else {
            listener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            Clog.e(Clog.mediationLogTag, "Failed to parse server extras. Check setup of placement in MoPub.");
            return;
        }

        iad = new InterstitialAdView(context);
        iad.setPlacementID(placementID);
        iad.setShouldServePSAs(false);
        iad.setAdListener(this);

        Clog.d(Clog.mediationLogTag, "Fetch ANInterstitial");
        iad.loadAd();
    }

    private boolean extrasAreValid(Map<String, String> serverExtras) {
        return serverExtras.containsKey(PLACEMENTID_KEY);
    }

    @Override
    protected void showInterstitial() {
        if (iad != null && iad.isReady()) {
            Clog.d(Clog.mediationLogTag, "Show ANInterstitial");
            iad.show();
        } else {
            if (iad == null) {
                Clog.e(Clog.mediationLogTag, "Failed to show ANInterstitial; null object");
            } else if (!iad.isReady()) {
                Clog.e(Clog.mediationLogTag, "Failed to show ANInterstitial; ad unavailable");
            }
        }
    }

    @Override
    protected void onInvalidate() {
        if (iad != null)
            iad.setAdListener(null);
        iad = null;
    }

    @Override
    public void onAdLoaded(AdView adView) {
        Clog.d(Clog.mediationLogTag, "ANInterstitial loaded successfully");
        if (listener != null) listener.onInterstitialLoaded();
    }

    @Override
    public void onAdRequestFailed(AdView adView) {
        Clog.d(Clog.mediationLogTag, "ANInterstitial failed to load");
        if (listener != null) listener.onInterstitialFailed(MoPubErrorCode.UNSPECIFIED);
    }

    @Override
    public void onAdExpanded(AdView adView) {
        Clog.d(Clog.mediationLogTag, "ANInterstitial expanded");
        if (listener != null) listener.onInterstitialShown();
    }

    @Override
    public void onAdCollapsed(AdView adView) {
        Clog.d(Clog.mediationLogTag, "ANInterstitial collapsed");
        if (listener != null) listener.onInterstitialDismissed();
    }

    @Override
    public void onAdClicked(AdView adView) {
        Clog.d(Clog.mediationLogTag, "ANInterstitial was clicked");
        if (listener != null) listener.onInterstitialClicked();
    }
}
