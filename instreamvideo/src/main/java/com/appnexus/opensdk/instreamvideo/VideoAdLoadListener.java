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
package com.appnexus.opensdk.instreamvideo;

import com.appnexus.opensdk.ResultCode;

public interface VideoAdLoadListener {

    /**
     * Called when an ad has successfully been loaded from the server.
     *
     * @param videoAd The {@link VideoAd} that loaded the ad.
     */
    public void onAdLoaded(VideoAd videoAd);

    /**
     * Called when an ad request has failed.  Ad requests can fail
     * because no ad is available, or because of networking errors.
     *  @param videoAd   The {@link VideoAd} that loaded the ad.
     * @param errorCode the ResultCode describing the failure.
     */
    public void onAdRequestFailed(VideoAd videoAd, ResultCode errorCode);

}
