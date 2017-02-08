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

import java.util.LinkedList;

public interface AdRequester {
    /**
     * Called when the request made by the requester fails.
     *
     * @param code reason why the request fails.
     */
    public void failed(ResultCode code);

    /**
     * Called when a response from AppNexus server is received
     *
     * @param response ServerResponse which was received.
     */
    public void onReceiveServerResponse(ServerResponse response);


    public void onReceiveAd(AdResponse ad);
    /**
     * Mark the beginning of an ad request for latency recording
     */
    public void markLatencyStart();

    /**
     * Returns the difference from latency start to parameter `now`
     * @param now current time
     */
    public long getLatency(long now);

    /**
     * Cancels the request, both to AppNexus server and to mediated networks
     */
    public void cancel();

    /**
     * Executes the request
     */
    public void execute();

    public LinkedList<MediatedAd> getMediatedAds();

    public RequestParameters getRequestParams();

}
