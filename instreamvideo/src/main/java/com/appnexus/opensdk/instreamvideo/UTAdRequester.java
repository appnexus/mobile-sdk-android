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

import com.appnexus.opensdk.AdRequester;
import com.appnexus.opensdk.AdResponse;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.instreamvideo.adresponsedata.BaseAdResponse;
import com.appnexus.opensdk.instreamvideo.ut.UTAdResponse;

public interface UTAdRequester extends AdRequester {


    /**
     * Called when a Universal Tag response from AppNexus server is received
     * @param response UTAdResponse which was received.
     * @param resultCode ResultCode
     */
    public void onReceiveUTResponse(UTAdResponse response, ResultCode resultCode);

    /**
     * Called when there is a failure/ No AD response from AppNexus server
     * @param reason ResultCode of the failure
     */
    public void currentAdFailed(ResultCode reason);
}
