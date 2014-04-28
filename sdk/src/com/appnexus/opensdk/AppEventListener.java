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

/**
 * Implement this interface and pass it to your {@link BannerAdView} and
 * {@link InterstitialAdView} objects to receive app events from the ad.
 */
public interface AppEventListener {
    /**
     * Called when the ad has sent the app an event via the
     * AppNexus Javascript API for Mobile call
     * 'anjam.DispatchAppEvent(name, data);'
     *
     * A simple example:
     * <pre>
     * <code>
     *
     *  AppEventListener appEventListener = new AppEventListener() {
     *      {@literal @}Override
     *      public void onAppEvent(AdView adView, String name, String data) {
     *          Log.d("APP_TAG", "AppEvent received: " + name + ", " + data);
     *      }
     *  }
     * </code>
     * </pre>
     *
     * @param adView The {@link AdView} that loaded the ad.
     * @param name the event name passed by the ad
     * @param data the event data passed by the ad
     */
    public void onAppEvent(AdView adView, String name, String data);
}
