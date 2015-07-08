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

/**
 * Define the basics for an ad, package only
 */
interface Ad {
    /**
     * Media type can be Banner, Interstitial or Native
     *
     * @return the media type of this ad
     */
    public MediaType getMediaType();

    /**
     * Checks whether an ad is ready to load a new one
     *
     * @return true if settings are ready to load a new ad
     */
    public boolean isReadyToStart();

    /**
     * Call this to load a new ad
     *
     * @return true if ad request is scheduled successfully
     */
    public boolean loadAd();

    /**
     * Provide the ad dispatcher of this ad
     *
     * @return ad dispatcher
     */
    public AdDispatcher getAdDispatcher();
}
