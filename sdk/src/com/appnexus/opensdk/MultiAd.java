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

import com.appnexus.opensdk.ut.UTAdRequester;
import com.appnexus.opensdk.ut.adresponse.BaseAdResponse;

/**
 * Define the basics for an ad, package only
 */
public interface MultiAd {

    ANMultiAdRequest getMultiAdRequest();

    /**
     * Not intended to be used outside of SDK.
     *
     * Call this to associate the Ad with the ANMultiAdRequest
     * @param anMultiAdRequest
     * */
    void associateWithMultiAdRequest(ANMultiAdRequest anMultiAdRequest);

    /**
     * Not intended to be used outside of SDK.
     *
     * Call this to disassociate the Ad from the ANMultiAdRequest
     * */
    void disassociateFromMultiAdRequest();

    /**
     * Not intended to be used outside of SDK.
     *
     * Call this to initiate the Vast Ad View
     * */
    void initiateVastAdView(BaseAdResponse baseAdResponse, AdViewRequestManager adViewRequestManager);

    /**
     * Not intended to be used outside of SDK.
     *
     * Call this to set the Request Manager
     * */
    void setRequestManager(UTAdRequester requester);

    /**
     * Not intended to be used outside of SDK.
     *
     * Call this to initialize the variables respective to each Ad Units while making an MAR request.
     * */
    void init();
}
