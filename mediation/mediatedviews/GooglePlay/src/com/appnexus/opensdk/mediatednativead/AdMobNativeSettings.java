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

import com.google.android.gms.ads.VideoOptions;

public class AdMobNativeSettings {
    public static String NATIVE_ELEMENT_STORE_KEY = "STORE";
    public static String NATIVE_ELEMENT_PRICE_KEY = "PRICE";
    public static String NATIVE_ELEMENT_ADVERTISER_KEY = "ADVERTISER";

    static VideoOptions videoOptions = null;
    static boolean enableMediaView = false;


    /**
     * Set the VideoOptions to be set on the NativeAdOptions when construction AdLoader.
     * @param videoOptions an instance of com.google.android.gms.ads.VideoOptions
     */
    public static void setVideoOptions(VideoOptions videoOptions) {
        AdMobNativeSettings.videoOptions = videoOptions;
    }

    /**
     * Pass true if you want to use MediaView to render the returned Video/ImageAssets.
     * If not enabled MediaView will not populate the image/video asset automatically since we set: setReturnUrlsForImageAssets(true);
     *
     * @param enableMediaView true to enable MediaView.
     *                        default is false.
     */
    public static void setEnableMediaView(boolean enableMediaView) {
        AdMobNativeSettings.enableMediaView = enableMediaView;
    }

}
