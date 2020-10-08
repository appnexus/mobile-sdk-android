/*
 *    Copyright 2014 APPNEXUS INC
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

/**
 * Define the events that an ad may fire.
 */
public interface NativeAdEventListener {
    /**
     * Called when the developer sets the ad click to be handled by the SDK
     */
    public void onAdWasClicked();

    /**
     * Called when the ad takes the user away from the application
     */
    public void onAdWillLeaveApplication();

    /**
     * Called when the developer sets the ad click to be handled by the app
     * @param clickUrl the click url
     * @param fallbackURL the fallback url
     */
    public void onAdWasClicked(String clickUrl,String fallbackURL);

    /**
     * Called when an impression is recorded for an native ad
     */
    public void onAdImpression();

    /**
     * Called when the native ad is 60 seconds to expire.
     * The 60 seconds here, can be modified using NATIVE_AD_RESPONSE_ON_AD_ABOUT_TO_EXPIRE_INTERVAL variable
     * in Settings class {@link com.appnexus.opensdk.utils.Settings}
     */
    public void onAdAboutToExpire();

    /**
     * Called when the native ad is expired.
     */
    public void onAdExpired();
}
