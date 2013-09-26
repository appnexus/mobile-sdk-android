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
 * Interface for receiving callbacks from a MediatedAdViewController
 */
public interface MediatedAdViewControllerListener {

    /**
     * Called when the third-party mediation network has alerted the
     * SDK of a successful ad request
     */
    public void onAdLoaded();

    /**
     * Called when the third-party mediation network has alerted the
     * SDK of a failed ad request
     * @param noMoreAds true if the response contains no more ads
     *                  and we should call onAdRequestFailed
     */
    public void onAdFailed(boolean noMoreAds);

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
}
