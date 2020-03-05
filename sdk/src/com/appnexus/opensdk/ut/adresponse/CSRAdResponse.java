/*
 *    Copyright 2020 APPNEXUS INC
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

import org.json.JSONObject;

import java.util.ArrayList;

public class CSRAdResponse extends BaseAdResponse {

    private String className;
    private String payload;
    private String responseUrl;
    private JSONObject adObject;
    private ArrayList<String> clickUrls;

    public CSRAdResponse(int width, int height, String adType, String responseUrl, ArrayList<String> impressionURLs, ANAdResponseInfo adResponseInfo, JSONObject adObject) {
        super(width, height, adType, impressionURLs, adResponseInfo);
        this.adObject = adObject;
        this.responseUrl = responseUrl;
    }

    public JSONObject getAdObject() {
        return adObject;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public void setClickUrls(ArrayList<String> clickUrls) {
        this.clickUrls = clickUrls;
    }

    public ArrayList<String> getClickUrls(){
        return this.clickUrls;
    }

    public String getResponseUrl() {
        return responseUrl;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getPayload() {
        return payload;
    }


}
