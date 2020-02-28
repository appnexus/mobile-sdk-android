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

package com.appnexus.opensdk.ut.adresponse;


import com.appnexus.opensdk.ANAdResponseInfo;

import java.util.ArrayList;

public class RTBVASTAdResponse extends BaseAdResponse {

    private String notifyUrl;


    public RTBVASTAdResponse(int width, int height, String adType, String notifyUrl, ArrayList<String> impressionURLs, ANAdResponseInfo adResponseInfo) {
        super(width, height, adType, impressionURLs, adResponseInfo);
        this.notifyUrl = notifyUrl;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

}