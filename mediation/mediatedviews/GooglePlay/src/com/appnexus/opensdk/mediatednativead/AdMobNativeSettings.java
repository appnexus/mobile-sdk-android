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
package com.appnexus.opensdk.mediatednativead;

public class AdMobNativeSettings {

    public enum AdMobNativeType {
        APP_INSTALL,
        CONTENT_AD
    }

    public static String NATIVE_ELEMENT_STORE_KEY = "STORE";
    public static String NATIVE_ELEMENT_PRICE_KEY = "PRICE";
    public static String NATIVE_ELEMENT_ADVERTISER_KEY = "ADVERTISER";
    public static String NATIVE_ELEMENT_TYPE_KEY = "TYPE";

    static boolean enableContentAd = false;
    static boolean enableAppInstallAd = false;

    /**
     * Pass true to enable the adapter to load native app install add
     * @param enableAppInstallAd
     */
    public static void setEnableAppInstallAd(boolean enableAppInstallAd) {
        AdMobNativeSettings.enableAppInstallAd = enableAppInstallAd;
    }

    /**
     * Pass true to enable the adapter to load native content ad
     * @param enableContentAd
     */
    public static void setEnableContentAd(boolean enableContentAd) {
        AdMobNativeSettings.enableContentAd = enableContentAd;
    }
}
