/*
 *    Copyright 2013 APPNEXUS INC
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

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.HTTPResponse;
import com.appnexus.opensdk.utils.JsonUtil;
import com.appnexus.opensdk.utils.StringUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressLint("NewApi")
public class ServerResponse {
    private String content;
    private int height;
    private int width;
    private String type;
    private MediaType mediaType;

    private LinkedList<MediatedAd> mediatedAds;

    private HashMap<String, Object> extras = new HashMap<String, Object>();

    private boolean containsAds = false;

    private boolean isHttpError = false;

    private static final String MRAID_JS_FILENAME = "mraid.js";
    private static final String RESPONSE_KEY_STATUS = "status";
    private static final String RESPONSE_KEY_ERROR_MESSAGE = "errorMessage";
    private static final String RESPONSE_KEY_ADS = "ads";
    private static final String RESPONSE_KEY_TYPE = "type";
    private static final String RESPONSE_KEY_WIDTH = "width";
    private static final String RESPONSE_KEY_HEIGHT = "height";
    private static final String RESPONSE_KEY_CONTENT = "content";
    private static final String RESPONSE_KEY_MEDIATED_ADS = "mediated";
    private static final String RESPONSE_KEY_HANDLER = "handler";
    private static final String RESPONSE_KEY_CLASS = "class";
    private static final String RESPONSE_KEY_ID = "id";
    private static final String RESPONSE_KEY_PARAM = "param";
    private static final String RESPONSE_KEY_RESULT_CB = "result_cb";
    private static final String RESPONSE_KEY_NATIVE = "native";

    private static final String RESPONSE_VALUE_ERROR = "error";
    private static final String RESPONSE_VALUE_ANDROID = "android";

    static final String EXTRAS_KEY_MRAID = "MRAID";
    static final String EXTRAS_KEY_ORIENTATION = "ORIENTATION";

    public ServerResponse(String body, Map<String, List<String>> headers, MediaType mediaType) {
        if (StringUtil.isEmpty(body)) {
            Clog.clearLastResponse();
            return;
        }

        Clog.setLastResponse(body);

        Clog.d(Clog.httpRespLogTag,
                Clog.getString(R.string.response_body, body));

        this.mediaType = mediaType;
        printHeaders(headers);
        parseResponse(body);
    }

    public ServerResponse(HTTPResponse httpResponse, MediaType mediaType) {
        this.mediaType = mediaType;
        printHeaders(httpResponse.getHeaders());
        parseResponse(httpResponse.getResponseBody());
    }

    public ServerResponse(boolean isHttpError) {
        this.isHttpError = isHttpError;
    }

    // minimal constructor for protected loadAdFromHtml function
    protected ServerResponse(String content, int width, int height) {
        this.content = content;
        this.width = width;
        this.height = height;
    }

    private void printHeaders(Map<String, List<String>> headers) {
        if (headers != null) {
            for (Map.Entry<String, List<String>> header : headers.entrySet()) {
                if (header.getKey() != null) {
                    for (String valueStr : header.getValue()) {
                        if (!TextUtils.isEmpty(valueStr)) {
                            Clog.v(Clog.httpRespLogTag,
                                    Clog.getString(R.string.response_header, header.getKey(),
                                            valueStr));
                        }
                    }
                }
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
            Clog.e(Clog.httpRespLogTag,
                Clog.getString(R.string.response_json_error, body));
            return;
        }
        // response will never be null at this point

        // stop parsing if status is not valid
        if (!checkStatusIsValid(response)) return;
        if (mediaType != MediaType.NATIVE) {
            // stop parsing if we get an ad from ads[]
            if (handleStdAds(response)) return;
        } else {
            // stop parsing if we get an ad from native[]
            // the order needs to be handled
            if (handleNativeAds(response)) return;
        }

        // stop parsing if we get an ad from mediated[]
        if (handleMediatedAds(response)) return;
    }

    // returns true if no error in status. don't fail on null or missing status
    private boolean checkStatusIsValid(JSONObject response) {
        String status = JsonUtil.getJSONString(response, RESPONSE_KEY_STATUS);
        if (status != null) {
            if (status.equals(RESPONSE_VALUE_ERROR)) {
                String error = JsonUtil.getJSONString(response, RESPONSE_KEY_ERROR_MESSAGE);
                Clog.e(Clog.httpRespLogTag,
                        Clog.getString(R.string.response_error, error));
                return false;
            }
        }
        return true;
    }

    // returns true if response contains an ad, false if not
    private boolean handleStdAds(JSONObject response) {
        JSONArray ads = JsonUtil.getJSONArray(response, RESPONSE_KEY_ADS);
        if (ads != null) {
            // take the first ad
            JSONObject firstAd = JsonUtil.getJSONObjectFromArray(ads, 0);
            type = JsonUtil.getJSONString(firstAd, RESPONSE_KEY_TYPE);
            height = JsonUtil.getJSONInt(firstAd, RESPONSE_KEY_HEIGHT);
            width = JsonUtil.getJSONInt(firstAd, RESPONSE_KEY_WIDTH);
            content = JsonUtil.getJSONString(firstAd, RESPONSE_KEY_CONTENT);
            if (StringUtil.isEmpty(content)) {
                Clog.e(Clog.httpRespLogTag,
                        Clog.getString(R.string.blank_ad));
            }
            else {
                if (content.contains(MRAID_JS_FILENAME)) {
                    addToExtras(EXTRAS_KEY_MRAID, true);
                }
                containsAds = true;
                return true;
            }
        }
        return false;
    }

    private ANNativeAdResponse anNativeAdResponse;

    // returns true if response contains a native response, false if not
    private boolean handleNativeAds(JSONObject response) {
        JSONArray nativeAd = JsonUtil.getJSONArray(response, RESPONSE_KEY_NATIVE);
        if (nativeAd != null) {
            // take the first ad
            JSONObject firstAd = JsonUtil.getJSONObjectFromArray(nativeAd, 0);
            type = JsonUtil.getJSONString(firstAd, RESPONSE_KEY_TYPE);
            anNativeAdResponse = ANNativeAdResponse.create(firstAd);
            if (anNativeAdResponse != null){
                containsAds = true;
                return true;
            }
        }
        return false;
    }

    // returns true if response contains an ad, false if not
    private boolean handleMediatedAds(JSONObject response) {
        JSONArray mediated = JsonUtil.getJSONArray(response, RESPONSE_KEY_MEDIATED_ADS);
        if (mediated != null) {
            mediatedAds = new LinkedList<MediatedAd>();
            for (int i = 0; i < mediated.length(); i++) {
                // parse through the elements of the mediated array for handlers
                JSONObject mediatedElement = JsonUtil.getJSONObjectFromArray(mediated, i);
                if (mediatedElement != null) {
                    JSONArray handler = JsonUtil.getJSONArray(mediatedElement, RESPONSE_KEY_HANDLER);
                    if (handler != null) {
                        for (int j = 0; j < handler.length(); j++) {
                            // get mediatedAd fields from handlerElement if available
                            JSONObject handlerElement = JsonUtil.getJSONObjectFromArray(handler, j);
                            if (handlerElement != null) {
                                // we only care about handlers for android
                                String type = JsonUtil.getJSONString(handlerElement, RESPONSE_KEY_TYPE);
                                if (type != null) {
                                    type = type.toLowerCase(Locale.US);
                                }
                                if ((type != null) && type.equals(RESPONSE_VALUE_ANDROID)) {
                                    String className = JsonUtil.getJSONString(handlerElement, RESPONSE_KEY_CLASS);
                                    String param = JsonUtil.getJSONString(handlerElement, RESPONSE_KEY_PARAM);
                                    int height = JsonUtil.getJSONInt(handlerElement, RESPONSE_KEY_HEIGHT);
                                    int width = JsonUtil.getJSONInt(handlerElement, RESPONSE_KEY_WIDTH);
                                    String adId = JsonUtil.getJSONString(handlerElement, RESPONSE_KEY_ID);
                                    String resultCB = JsonUtil.getJSONString(mediatedElement, RESPONSE_KEY_RESULT_CB);

                                    if (!StringUtil.isEmpty(className)) {
                                        mediatedAds.add(new MediatedAd(className,
                                                param, width, height, adId,
                                                resultCB));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (!mediatedAds.isEmpty()) {
                containsAds = true;
                return true;
            }
        }
        return false;
    }

    NativeAdResponse getNativeAdResponse() {
        return anNativeAdResponse;
    }

    MediaType getMediaType() {
        return mediaType;
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

    LinkedList<MediatedAd> getMediatedAds() {
        return mediatedAds;
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
