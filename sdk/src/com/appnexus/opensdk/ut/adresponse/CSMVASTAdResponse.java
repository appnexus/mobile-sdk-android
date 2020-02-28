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

package com.appnexus.opensdk.ut.adresponse;

import com.appnexus.opensdk.ANAdResponseInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.UUID;


public class CSMVASTAdResponse extends BaseAdResponse {

    private int tag_id;
    private String auction_id;
    private int timeout_ms;
    private JSONObject adJSONContent;
    private String uuid;

    public CSMVASTAdResponse(int width, int height, String adType, ArrayList<String> impressionURLs, ANAdResponseInfo adResponseInfo, String uuid) {
        super(width, height, adType, impressionURLs , adResponseInfo);
        this.uuid = uuid;
    }

    public JSONObject getAdJSONContent() {
        return adJSONContent;
    }

    public void setAdJSONContent(JSONObject adContent) {
        this.adJSONContent = adContent;
    }

    public int getTag_id() {
        return tag_id;
    }

    public void setTag_id(int tag_id) {
        this.tag_id = tag_id;
    }

    public String getAuction_id() {
        return auction_id;
    }

    public void setAuction_id(String auction_id) {
        this.auction_id = auction_id;
    }

    public int getTimeout_ms() {
        return timeout_ms;
    }

    public void setTimeout_ms(int timeout_ms) {
        this.timeout_ms = timeout_ms;
    }

    public String getCSMVASTAdResponse() {
        JSONObject tag = new JSONObject();
        JSONArray ads = new JSONArray();
        try {
            tag.put("uuid", getUuid());
            tag.put("auction_id",getAuction_id());
            tag.put("tag_id",getTag_id());
            tag.put("timeout_ms",getTimeout_ms());
            ads.put(getAdJSONContent());
            tag.put("ads",ads);
            String escapedJSON = "";
            try {
                escapedJSON= URLEncoder.encode(tag.toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return escapedJSON;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getUuid() {
        return uuid;
    }
}


