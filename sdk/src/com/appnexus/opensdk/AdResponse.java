/*
 *    Copyright 2015 APPNEXUS INC
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

public interface AdResponse {
    /**
     * @return MediaType of the ad respone
     */
    public MediaType getMediaType();

    /**
     * @return true if the ad response comes a mediated network
     */
    public boolean isMediated();

    /**
     * For BannerAdView and InterstitialAdView to get displayable
     *
     * @return null if media type is not banner or interstitial
     */
    public Displayable getDisplayable();

    /**
     * For NativeAdRequest to retrieve native ad response
     *
     * @return null if media type is not native
     */
    public NativeAdResponse getNativeAdResponse();

    /**
     * Call this to destroy the ad response
     */
    public void destroy();
}
