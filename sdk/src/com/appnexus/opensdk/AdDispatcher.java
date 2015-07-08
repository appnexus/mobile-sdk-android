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

interface AdDispatcher {
    /**
     * Called when an ad is ready to be used, indicates a successful ad
     * request
     *
     * @param ad the response from either AppNexus server or mediated networks
     */
    public void onAdLoaded(AdResponse ad);

    /**
     * Called when the mediation waterfall has ended in a failure
     *
     * @param errorCode the error code describing the failure.
     */
    public void onAdFailed(ResultCode errorCode);

    /**
     * Called when the third-party mediation network has alerted the
     * SDK of an ad being expanded
     */
    public void onAdExpanded();

    /**
     * Called when the third-party mediation network has alerted the
     * SDK of an ad being collapsed
     */
    public void onAdCollapsed();

    /**
     * Called when the third-party mediation network has alerted the
     * SDK of an ad being clicked
     */
    public void onAdClicked();

    /**
     * Called when the ad has sent the app an event via the
     * AppNexus Javascript API for Mobile
     */
    public void onAppEvent(String name, String data);
}
