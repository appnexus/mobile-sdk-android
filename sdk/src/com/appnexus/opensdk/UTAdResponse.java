package com.appnexus.opensdk;

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

import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.JsonUtil;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.StringUtil;
import com.appnexus.opensdk.vastdata.AdModel;
import com.appnexus.opensdk.vastdata.VastResponseParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;

class UTAdResponse {

    private static final String UTF_8 = "UTF-8";

    private static final String RESPONSE_KEY_TAGS = "tags";
    private static final String RESPONSE_KEY_AD = "ad";
    private static final String RESPONSE_KEY_VIDEO = "video";
    private static final String RESPONSE_KEY_BANNER = "banner";
    private static final String RESPONSE_KEY_CONTENT = "content";
    private static final String RESPONSE_KEY_WIDTH = "width";
    private static final String RESPONSE_KEY_HEIGHT = "height";
    public static final String RESPONSE_KEY_NO_BID = "nobid";

    //UT-V2
    private static final String RESPONSE_KEY_RTB = "rtb";
    private static final String RESPONSE_KEY_ADS = "ads";
    private static final String RESPONSE_KEY_NOTIFY_URL = "notify_url";

    private AdModel vastAdResponse;
    private String content;
    private int height;
    private int width;
    private MediaType mediaType;

    private HashMap<String, Object> extras = new HashMap<String, Object>();

    private boolean containsAds = false;

    private boolean isHttpError = false;


    public UTAdResponse(String body, MediaType mediaType) {
        if (StringUtil.isEmpty(body)) {
            Clog.clearLastResponse();
            return;
        }

        Clog.setLastResponse(body);

        Clog.i(Clog.httpRespLogTag, Clog.getString(R.string.response_body, body));
        Clog.i(Clog.httpRespLogTag, "Media type: "+mediaType);

        this.mediaType = mediaType;
        if(Settings.useUniversalTagV2){
            parseResponseV2(body);
        }else {
            parseResponse(body);
        }
    }


    public UTAdResponse(boolean isHttpError) {
        this.isHttpError = isHttpError;
    }


    private void parseResponse(String body) {
        JSONObject response;

        try {
            if (!StringUtil.isEmpty(body)) {
                response = new JSONObject(body);
            } else {
                return;
            }
        } catch (JSONException e) {
            Clog.e(Clog.httpRespLogTag,  Clog.getString(R.string.response_json_error, body));
            return;
        }

        try {
            JSONArray tagsArray = JsonUtil.getJSONArray(response, RESPONSE_KEY_TAGS);
            if(tagsArray != null) {

                JSONObject tagObject = JsonUtil.getJSONObjectFromArray(tagsArray, 0);

                // If it contains nobid response, don't parse further.
                if (JsonUtil.getJSONBoolean(tagObject, RESPONSE_KEY_NO_BID)){
                    return;
                }

                JSONObject adObject = JsonUtil.getJSONObject(tagObject, RESPONSE_KEY_AD);
                if (adObject != null) {
                    if(adObject.has(RESPONSE_KEY_BANNER)) {
                        Clog.i(Clog.httpReqLogTag, "it's an HTML Ad");
                        parseHTMLAd(adObject);
                    }else{
                        Clog.i(Clog.httpReqLogTag, "it's a Video Ad");
                        parseVastVideoAd(adObject);
                    }
                }
            }
        } catch (Exception e) {
            // Catches XMLPullParserException, JSONException, NullPointerException and IOException
            Clog.e(Clog.httpReqLogTag, "Error parsing the ad response: " + e.getMessage());
            containsAds = false;
        }
    }

    private void parseResponseV2(String body) {
        JSONObject response;

        try {
            if (!StringUtil.isEmpty(body)) {
                response = new JSONObject(body);
            } else {
                Clog.e(Clog.httpRespLogTag,  "NO Response: "+ body);
                return;
            }
        } catch (JSONException e) {
            Clog.e(Clog.httpRespLogTag,  Clog.getString(R.string.response_json_error, body));
            return;
        }

        try {
            JSONArray tagsArray = JsonUtil.getJSONArray(response, RESPONSE_KEY_TAGS);
            if(tagsArray != null) {
                JSONObject tagObject = JsonUtil.getJSONObjectFromArray(tagsArray, 0);
                // If it contains nobid response, don't parse further.
                if (JsonUtil.getJSONBoolean(tagObject, RESPONSE_KEY_NO_BID)){
                    return;
                }

                JSONArray adArray = JsonUtil.getJSONArray(tagObject, RESPONSE_KEY_ADS);
                JSONObject adObject = JsonUtil.getJSONObjectFromArray(adArray, 0);

                JSONObject rtbObject = JsonUtil.getJSONObject(adObject, RESPONSE_KEY_RTB);
                if (rtbObject != null) {
                    if(rtbObject.has(RESPONSE_KEY_BANNER)) {
                        Clog.i(Clog.httpReqLogTag, "it's an HTML Ad");
                        parseHTMLAdV2(rtbObject);
                    }else{
                        Clog.i(Clog.httpReqLogTag, "it's a Video Ad");
                        parseVastVideoAdV2(rtbObject, adObject.getString(RESPONSE_KEY_NOTIFY_URL));
                    }
                }
            }
        } catch (Exception e) {
            // Catches XMLPullParserException, JSONException, NullPointerException and IOException
            Clog.e(Clog.httpReqLogTag, "Error parsing the ad response: " + e.getMessage());
            containsAds = false;
        }
    }

    /**
     * Parses HTML ad response
     * @param adObject
     * @throws Exception
     */
    private void parseHTMLAd(JSONObject adObject) throws Exception{
        JSONObject bannerObject = JsonUtil.getJSONObject(adObject, RESPONSE_KEY_BANNER);
        if(bannerObject != null) {
            height = JsonUtil.getJSONInt(bannerObject, RESPONSE_KEY_HEIGHT);
            width = JsonUtil.getJSONInt(bannerObject, RESPONSE_KEY_WIDTH);
            content = JsonUtil.getJSONString(bannerObject, RESPONSE_KEY_CONTENT);
            if (StringUtil.isEmpty(content)) {
                Clog.e(Clog.httpRespLogTag, Clog.getString(R.string.blank_ad));
            } else {
                if (content.contains(ServerResponse.MRAID_JS_FILENAME)) {
                    addToExtras(ServerResponse.EXTRAS_KEY_MRAID, true);
                }
                Clog.i(Clog.httpReqLogTag, "parseHTMLAd: true");
                containsAds = true;
            }
        }
    }


    /**
     * Parses VAST ad response
     * @param adObject
     * @throws Exception
     */
    private void parseVastVideoAd(JSONObject adObject) throws Exception {

        JSONObject videoObject = JsonUtil.getJSONObject(adObject, RESPONSE_KEY_VIDEO);
        if(videoObject != null) {
            String vastResponse = JsonUtil.getJSONString(videoObject, RESPONSE_KEY_CONTENT);
            if(!StringUtil.isEmpty(vastResponse)) {
                InputStream stream = new ByteArrayInputStream(vastResponse.getBytes(Charset.forName(UTF_8)));

                VastResponseParser vastResponseParser = new VastResponseParser();
                this.vastAdResponse = vastResponseParser.readVAST(stream);
                if(this.vastAdResponse != null && this.vastAdResponse.containsLinearAd()) {
                    containsAds = true;
                    Clog.i(Clog.httpReqLogTag, "Vast response parsed");
                }
            }
        }
    }


    /**
     * Parse UT-V2 HTML response
     * @param rtbObject
     * @throws Exception
     */
    private void parseHTMLAdV2(JSONObject rtbObject) throws Exception{
        JSONObject bannerObject = JsonUtil.getJSONObject(rtbObject, RESPONSE_KEY_BANNER);
        if(bannerObject != null) {
            height = JsonUtil.getJSONInt(bannerObject, RESPONSE_KEY_HEIGHT);
            width = JsonUtil.getJSONInt(bannerObject, RESPONSE_KEY_WIDTH);
            content = JsonUtil.getJSONString(bannerObject, RESPONSE_KEY_CONTENT);
            if (StringUtil.isEmpty(content)) {
                Clog.e(Clog.httpRespLogTag, Clog.getString(R.string.blank_ad));
            } else {
                if (content.contains(ServerResponse.MRAID_JS_FILENAME)) {
                    addToExtras(ServerResponse.EXTRAS_KEY_MRAID, true);
                }
                Clog.i(Clog.httpReqLogTag, "parseHTMLAd: true");
                containsAds = true;
            }
        }
    }

    /**
     *  Parse UT-V2 VAST response
     * @param rtbObject
     * @throws Exception
     */
    private void parseVastVideoAdV2(JSONObject rtbObject, String impressionUrl) throws Exception {

        JSONObject videoObject = JsonUtil.getJSONObject(rtbObject, RESPONSE_KEY_VIDEO);
        if(videoObject != null) {
            String vastResponse = JsonUtil.getJSONString(videoObject, RESPONSE_KEY_CONTENT);
            if(!StringUtil.isEmpty(vastResponse)) {
                InputStream stream = new ByteArrayInputStream(vastResponse.getBytes(Charset.forName(UTF_8)));

                VastResponseParser vastResponseParser = new VastResponseParser();
                this.vastAdResponse = vastResponseParser.readVAST(stream);
                if(this.vastAdResponse != null && this.vastAdResponse.containsLinearAd()) {
                    containsAds = true;
                    if(impressionUrl != null) {
                        this.vastAdResponse.getImpressionArrayList().add(impressionUrl);
                    }
                    Clog.i(Clog.httpReqLogTag, "Vast response parsed");
                }
            }
        }
    }




    MediaType getMediaType() {
        return mediaType;
    }

    AdModel getVastAdResponse() {
        return vastAdResponse;
    }

    String getContent() {
        return content != null ? content : "";
    }

    int getHeight() {
        return height;
    }

    int getWidth() {
        return width;
    }

    boolean containsAds() {
        return containsAds;
    }

    boolean isHttpError() {
        return isHttpError;
    }

    HashMap<String, Object> getExtras() {
        return extras;
    }

    void addToExtras(String key, Object value) {
        extras.put(key, value);
    }

}
