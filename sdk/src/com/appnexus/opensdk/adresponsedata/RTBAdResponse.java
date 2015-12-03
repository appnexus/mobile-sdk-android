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

package com.appnexus.opensdk.adresponsedata;

import com.appnexus.opensdk.vastdata.AdModel;

import java.util.ArrayList;


public class RTBAdResponse extends BaseAdResponse {

    private AdModel vastAdResponse;

    public RTBAdResponse(int width, int height, String adType, String notifyUrl, ArrayList<String> impressionURLs) {
        super(width, height, adType, notifyUrl, impressionURLs);
    }

    public AdModel getVastAdResponse() {
        return vastAdResponse;
    }

    public void setVastAdResponse(AdModel vastAdResponse) {
        this.vastAdResponse = vastAdResponse;
    }


}
