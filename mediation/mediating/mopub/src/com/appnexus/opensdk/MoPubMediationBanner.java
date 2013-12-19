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
import com.mopub.mobileads.CustomEventBanner;
import com.mopub.mobileads.MoPubErrorCode;

import java.util.Map;

public class MoPubMediationBanner extends CustomEventBanner implements AdListener {
    private BannerAdView bav;
    public static final String PLACEMENTID_KEY = "id";
    public static final String AD_WIDTH_KEY = "width";
    public static final String AD_HEIGHT_KEY = "height";
    CustomEventBannerListener listener;

    @Override
    protected void loadBanner(Context context, CustomEventBannerListener customEventBannerListener, Map<String, Object> localExtras, Map<String, String> serverExtras) {
        Clog.d(Clog.mediationLogTag, "Initializing ANBanner via MoPub SDK");
        this.listener = customEventBannerListener;

        String placementID;
        int width;
        int height;

        if (extrasAreValid(serverExtras)) {
            placementID = serverExtras.get(PLACEMENTID_KEY);
            width = Integer.parseInt(serverExtras.get(AD_WIDTH_KEY));
            height = Integer.parseInt(serverExtras.get(AD_HEIGHT_KEY));
            Clog.d(Clog.mediationLogTag, String.format("Server extras were valid: placementID: %s, width: %s, height: %s", placementID, width, height));
        } else {
            listener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            Clog.e(Clog.mediationLogTag, "Failed to parse server extras. Check setup of placement in MoPub.");
            return;
        }

        bav = new BannerAdView(context);
        bav.setPlacementID(placementID);
        bav.setAdSize(width, height);
        bav.setShouldServePSAs(false);
        bav.setAdListener(this);

        Clog.d(Clog.mediationLogTag, "Load ANBanner");
        bav.loadAdOffscreen();
    }

    private boolean extrasAreValid(Map<String, String> serverExtras) {
        try {
            Integer.parseInt(serverExtras.get(AD_WIDTH_KEY));
            Integer.parseInt(serverExtras.get(AD_HEIGHT_KEY));
        } catch (NumberFormatException e) {
            return false;
        }

        return serverExtras.containsKey(PLACEMENTID_KEY);
    }

    @Override
    protected void onInvalidate() {
        if (bav != null)
            bav.setAdListener(null);
        bav = null;
    }

    @Override
    public void onAdLoaded(AdView adView) {
        Clog.d(Clog.mediationLogTag, "ANBanner loaded successfully");
        if (listener != null) listener.onBannerLoaded(bav);
    }

    @Override
    public void onAdRequestFailed(AdView adView) {
        Clog.d(Clog.mediationLogTag, "ANBanner failed to load");
        if (listener != null) listener.onBannerFailed(MoPubErrorCode.UNSPECIFIED);
    }

    @Override
    public void onAdExpanded(AdView adView) {
        if (listener != null) listener.onBannerExpanded();
    }

    @Override
    public void onAdCollapsed(AdView adView) {
        if (listener != null) listener.onBannerCollapsed();
    }

    @Override
    public void onAdClicked(AdView adView) {
        if (listener != null) listener.onBannerClicked();
    }
}
