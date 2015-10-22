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
import com.appnexus.opensdk.utils.HTTPResponse;
import com.appnexus.opensdk.utils.JsonUtil;
import com.appnexus.opensdk.utils.StringUtil;
import com.appnexus.opensdk.vastdata.AdModel;
import com.appnexus.opensdk.vastdata.VastResponseParser;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;

class UTAdResponse {

    public static final String UT_TAGS = "tags";
    public static final String UT_AD = "ad";
    public static final String UT_VIDEO = "video";
    public static final String UT_BANNER = "banner";
    public static final String UT_CONTENT = "content";
    public static final String UTF_8 = "UTF-8";
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
        parseResponse(body);
    }

    public UTAdResponse(HTTPResponse httpResponse, MediaType mediaType) {
        this.mediaType = mediaType;
        parseResponse(httpResponse.getResponseBody());
    }

    public UTAdResponse(boolean isHttpError) {
        this.isHttpError = isHttpError;
    }

    // minimal constructor for protected loadAdFromHtml function
    protected UTAdResponse(String content, int width, int height) {
        this.content = content;
        this.width = width;
        this.height = height;
    }

    private void printHeaders(Header[] headers) {
        if (headers != null) {
            for (Header h : headers) {
                Clog.v(Clog.httpRespLogTag,
                        Clog.getString(R.string.response_header, h.getName(),
                                h.getValue()));
            }
        }
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

        // stop parsing if status is not valid
        if (!checkStatusIsValid(response)) return;
        if (mediaType == MediaType.INTERSTITIAL) {
            // stop parsing if we get an ad from ads[]
            if (handleAdResponse(response)) return;
        }
    }


    // returns true if no error in status. don't fail on null or missing status
    private boolean checkStatusIsValid(JSONObject response) {
        String status = JsonUtil.getJSONString(response, ServerResponse.RESPONSE_KEY_STATUS);
        if (status != null) {
            if (status.equals(ServerResponse.RESPONSE_VALUE_ERROR)) {
                String error = JsonUtil.getJSONString(response, ServerResponse.RESPONSE_KEY_ERROR_MESSAGE);
                Clog.e(Clog.httpRespLogTag,
                        Clog.getString(R.string.response_error, error));
                return false;
            }
        }
        return true;
    }

    // returns true if response contains an ad, false if not
    private boolean handleAdResponse(JSONObject response) {
        try {
            JSONArray tagsArray = JsonUtil.getJSONArray(response, UT_TAGS);
            if(tagsArray != null) {
                JSONObject tagObject = (JSONObject) tagsArray.get(0);
                JSONObject adObject = JsonUtil.getJSONObject(tagObject, UT_AD);
                if (adObject != null) {
                    if(adObject.has(UT_BANNER)) {
                        Clog.i(Clog.httpReqLogTag, "it's an HTML Ad");
                        return parseHTMLAd(adObject);
                    }else{
                        Clog.i(Clog.httpReqLogTag, "it's a Video Ad");
                        return parseVastVideoAd(adObject);
                    }
                }
            }
        } catch (Exception e) {
            Clog.e(Clog.httpReqLogTag, "Error parsing the ad response: " + e.getMessage());
            containsAds = false;
        }
        return false;

    }

    /**
     *
     * @param adObject
     * @return
     */
    private boolean parseHTMLAd(JSONObject adObject) {
        JSONObject bannerObject = JsonUtil.getJSONObject(adObject, UT_BANNER);
        if(bannerObject != null) {
            height = JsonUtil.getJSONInt(bannerObject, ServerResponse.RESPONSE_KEY_HEIGHT);
            width = JsonUtil.getJSONInt(bannerObject, ServerResponse.RESPONSE_KEY_WIDTH);
            content = JsonUtil.getJSONString(bannerObject, UT_CONTENT);
            if (StringUtil.isEmpty(content)) {
                Clog.e(Clog.httpRespLogTag, Clog.getString(R.string.blank_ad));
            } else {
                if (content.contains(ServerResponse.MRAID_JS_FILENAME)) {
                    addToExtras(ServerResponse.EXTRAS_KEY_MRAID, true);
                }
                Clog.i(Clog.httpReqLogTag, "parseHTMLAd: true");
                containsAds = true;
                return true;
            }
        }
        return false;
    }

    private boolean parseVastVideoAd(JSONObject adObject) throws Exception {
        VastResponseParser vastResponseParser = new VastResponseParser();
        JSONObject videoObject = JsonUtil.getJSONObject(adObject, UT_VIDEO);
        if(videoObject != null) {
            String vastResponse = JsonUtil.getJSONString(videoObject, UT_CONTENT);
            if(!StringUtil.isEmpty(vastResponse)) {
                InputStream stream = new ByteArrayInputStream(vastResponse.getBytes(Charset.forName(UTF_8)));
                this.vastAdResponse = vastResponseParser.readVAST(stream);
                if(this.vastAdResponse != null && this.vastAdResponse.containsLinearAd()) {
                    containsAds = true;
                    Clog.i(Clog.httpReqLogTag, "Vast response parsed");
                    return true;
                }
            }
        }
        return false;
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
