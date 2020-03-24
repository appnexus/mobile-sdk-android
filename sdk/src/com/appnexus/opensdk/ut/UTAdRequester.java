/*
 *    Copyright 2017 APPNEXUS INC
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

package com.appnexus.opensdk.ut;

import com.appnexus.opensdk.ANAdResponseInfo;
import com.appnexus.opensdk.AdResponse;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.ut.adresponse.BaseAdResponse;

import java.util.LinkedList;

public interface UTAdRequester {

    /**
     * Called when the request made by the requester fails.
     *
     * @param code reason why the request fails.
     */
    void failed(ResultCode code, ANAdResponseInfo responseInfo);

    void onReceiveAd(AdResponse ad);

    /**
     * Cancels the request, both to AppNexus server and to mediated networks
     */
    void cancel();

    /**
     * Executes the request
     */
    void execute();

    /**
     * returns the ad list array
     * @return Array of Ads list
     */

    LinkedList<BaseAdResponse> getAdList();

    /**
     * returns the request parameters object
     * @return  (UTRequestParameters)
     */

    UTRequestParameters getRequestParams();

    /**
     * Called when a Universal Tag response from AppNexus server is received
     * @param response UTAdResponse which was received.
     *
     */
    void onReceiveUTResponse(UTAdResponse response);

    /**
     * Called when there is a failure/ No AD response from AppNexus server
     * @param reason ResultCode of the failure
     */
    void continueWaterfall(ResultCode reason);

    /*
    * Called when the RendererUrl is invalid / fails to load
    * */
    void nativeRenderingFailed();
}
