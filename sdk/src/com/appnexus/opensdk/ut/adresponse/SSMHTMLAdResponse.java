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

import java.util.ArrayList;


public class SSMHTMLAdResponse extends BaseAdResponse {
    private String adUrl;
    private int ssmTimeout;
    private String responseURL;

    public SSMHTMLAdResponse(int width, int height, String adType, String responseURL, ArrayList<String> impressionURLs) {
        super(width, height, adType, impressionURLs);
        this.responseURL = responseURL;
    }

    public String getAdUrl() {
        return adUrl;
    }

    public void setAdUrl(String adUrl) {
        this.adUrl = adUrl;
    }

    public String getResponseURL() {return responseURL;}

    public int getSsmTimeout() {
        return ssmTimeout;
    }

    public void setSsmTimeout(int ssmTimeout) {
        this.ssmTimeout = ssmTimeout;
    }

}
