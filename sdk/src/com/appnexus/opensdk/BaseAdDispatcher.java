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

package com.appnexus.opensdk;

public interface BaseAdDispatcher {

    /**
     * Called when an ad is ready to be used, indicates a successful ad request
     */
    public void onAdLoaded();

    /**
     * Called when the Ad Request has ended in a failure
     *
     * @param errorCode the error code describing the failure.
     */
    public void onAdFailed(ResultCode errorCode, ANAdResponseInfo responseInfo);

    /**
     * Called when the ad being clicked
     * and the ClickThroughAction is set as either ANClickThroughAction.OPEN_DEVICE_BROWSER
     * or ANClickThroughAction.OPEN_SDK_BROWSER
     * {@link ANClickThroughAction}
     */
    public void onAdClicked();

    /**
     * Called when the ad being clicked
     * and the ClickThroughAction is set as ANClickThroughAction.RETURN_URL
     * {@link ANClickThroughAction}
     */
    public void onAdClicked(String clickUrl);

    /**
     * Called on ad impression
     */
    public void onAdImpression();
}
