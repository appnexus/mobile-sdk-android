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

import com.appnexus.opensdk.MediatedBannerAdView;
import com.appnexus.opensdk.MediatedInterstitialAdView;

/**
 * Common class that facilitates the AppNexus SDK to Google adaptor bridge
 * Google Mobile Ads adaptor and the Google Play Services adaptor
 * for AdMob banners.
 */
class GoogleBridge {
    static final String AdMobBanner = "com.appnexus.opensdk.mediatedviews.LegacyAdMobBanner";
    static final String AdMobInterstitial = "com.appnexus.opensdk.mediatedviews.LegacyAdMobInterstitial";
    static final String DFPBanner = "com.appnexus.opensdk.mediatedviews.LegacyDFPBanner";
    static final String DFPInterstitial = "com.appnexus.opensdk.mediatedviews.LegacyDFPInterstitial";

    static final String GooglePlayBanner = "com.appnexus.opensdk.mediatedviews.GooglePlayServicesBanner";
    static final String GooglePlayInterstitial = "com.appnexus.opensdk.mediatedviews.GooglePlayServicesInterstitial";
    static final String GooglePlayDFPBanner = "com.appnexus.opensdk.mediatedviews.GooglePlayDFPBanner";
    static final String GooglePlayDFPInterstitial = "com.appnexus.opensdk.mediatedviews.GooglePlayDFPInterstitial";

    private static final String GooglePlayServicesVerificationClass = "com.google.android.gms.ads.AdListener";

    static boolean isGooglePlayServicesAvailable() {
        try {
            Class gpsClass = Class.forName(GooglePlayServicesVerificationClass);
            return gpsClass != null;
        } catch (ClassNotFoundException ignored) {
        }
        return false;
    }

    static MediatedBannerAdView bannerForClassName(String name) {
        if (name == null) {
            return null;
        }
        try {
            Class c = Class.forName(name);
            return (MediatedBannerAdView) c.newInstance();
        } catch (ClassNotFoundException ignored) {
        } catch (InstantiationException ignored) {
        } catch (IllegalAccessException ignored) {
        } catch (ClassCastException ignored) {
        }

        return null;
    }

    static MediatedInterstitialAdView interstitialForClassName(String name) {
        if (name == null) {
            return null;
        }
        try {
            Class c = Class.forName(name);
            return (MediatedInterstitialAdView) c.newInstance();
        } catch (ClassNotFoundException ignored) {
        } catch (InstantiationException ignored) {
        } catch (IllegalAccessException ignored) {
        } catch (ClassCastException ignored) {
        }

        return null;
    }

}
