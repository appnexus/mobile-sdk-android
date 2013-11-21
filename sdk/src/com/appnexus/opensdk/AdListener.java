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
 * Implement this interface and pass it to your {@link BannerAdView} and {@link  InterstitialAdView}
 * objects to receive events on the status of the ad.
 */
public interface AdListener {
    /**
     * Called when an ad has successfully been loaded from the server.
     *
     * @param adView The {@link AdView} that loaded the ad.
     */
    public void onAdLoaded(AdView adView);

    /**
     * Called when an ad request has failed.
     * Ad request failures may include no ad available, or networking errors
     *
     * @param adView The {@link AdView} that loaded the ad.
     */
    public void onAdRequestFailed(AdView adView);

    /**
     * Called when an ad expands due to user interaction. MRAID ads that expand
     * the screen generate these events. This event may fire from both Banner and Interstitial ads.
     * This would be a good time to stop or pause your application due 
     * to the user interacting with the ad. 
     *
     * @param adView The {@link AdView} that loaded the ad.
     */
    public void onAdExpanded(AdView adView);

    /**
     * Called when an ad is closed/unexpanded. The user has stopped interacting 
     * with the ad. This is the corollary of onAdExpanded.
     *
     * @param adView The {@link AdView} that loaded the ad.
     */
    public void onAdCollapsed(AdView adView);

    /**
     * Called when an ad is clicked. The current activity will be paused 
     * as the user switches activities to the activity launched from 
     * the ad interaction. For example the user clicked a link that 
     * opened a web browser, or the user touched a click to call link 
     * which launched the phone dialer. 
     *
     * @param adView The {@link AdView} that loaded the ad.
     */
    public void onAdClicked(AdView adView);
}
