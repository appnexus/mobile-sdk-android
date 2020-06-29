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

import org.json.JSONObject;

import java.util.ArrayList;
import com.appnexus.opensdk.utils.Settings;


public class CSMSDKAdResponse extends BaseAdResponse {

    private String id;
    private String className;
    private String param;
    private String responseUrl;
    private JSONObject adObject;
    private long networkTimeout;

    public CSMSDKAdResponse(int width, int height, String adType, String responseUrl, ArrayList<String> impressionURLs , ANAdResponseInfo adResponseInfo, JSONObject adObject) {
        super(width, height, adType, impressionURLs , adResponseInfo);
        this.responseUrl = responseUrl;
        this.adObject = adObject;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }


    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getResponseUrl() {
        return responseUrl;
    }

    public JSONObject getAdObject() {
        return adObject;
    }

    public void setNetworkTimeout(int networkTimeout) {
        this.networkTimeout = networkTimeout;
    }

    public long getNetworkTimeout() {
        return networkTimeout;
    }
}
