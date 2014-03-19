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
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.HTTPResponse;
import com.appnexus.opensdk.utils.StringUtil;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;

@SuppressLint("NewApi")
class AdResponse {
    private String content;
    private int height;
    private int width;
    private String type;

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

    private static final String RESPONSE_VALUE_ERROR = "error";
    private static final String RESPONSE_VALUE_ANDROID = "android";

    static final String EXTRAS_KEY_MRAID = "MRAID";
    static final String EXTRAS_KEY_ORIENTATION = "ORIENTATION";

    public AdResponse(String body, Header[] headers) {
        if (StringUtil.isEmpty(body)) {
            Clog.clearLastResponse();
            return;
        }

        Clog.setLastResponse(body);

        Clog.d(Clog.httpRespLogTag,
                Clog.getString(R.string.response_body, body));

        printHeaders(headers);
        parseResponse(body);
    }

    public AdResponse(HTTPResponse httpResponse) {
        printHeaders(httpResponse.getHeaders());
        parseResponse(httpResponse.getResponseBody());
    }

    public AdResponse(boolean isHttpError) {
        this.isHttpError = isHttpError;
    }

    // minimal constructor for protected loadAdFromHtml function
    protected AdResponse(String content, int width, int height) {
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
            Clog.e(Clog.httpRespLogTag,
                Clog.getString(R.string.response_json_error, body));
            return;
        }
        // response will never be null at this point

        // stop parsing if status is not valid
        if (!checkStatusIsValid(response)) return;
        // stop parsing if we get an ad from ads[]
        if (handleStdAds(response)) return;
        // stop parsing if we get an ad from mediated[]
        if (handleMediatedAds(response)) return;
    }

    // returns true if no error in status. don't fail on null or missing status
    private boolean checkStatusIsValid(JSONObject response) {
        String status = getJSONString(response, RESPONSE_KEY_STATUS);
        if (status != null) {
            if (status.equals(RESPONSE_VALUE_ERROR)) {
                String error = getJSONString(response, RESPONSE_KEY_ERROR_MESSAGE);
                Clog.e(Clog.httpRespLogTag,
                        Clog.getString(R.string.response_error, error));
                return false;
            }
        }
        return true;
    }

    // returns true if response contains an ad, false if not
    private boolean handleStdAds(JSONObject response) {
        JSONArray ads = getJSONArray(response, RESPONSE_KEY_ADS);
        if (ads != null) {
            // take the first ad
            JSONObject firstAd = getJSONObjectFromArray(ads, 0);
            type = getJSONString(firstAd, RESPONSE_KEY_TYPE);
            height = getJSONInt(firstAd, RESPONSE_KEY_HEIGHT);
            width = getJSONInt(firstAd, RESPONSE_KEY_WIDTH);
            content = getJSONString(firstAd, RESPONSE_KEY_CONTENT);
            if (content == null || content.equals("")) {
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

    // returns true if response contains an ad, false if not
    private boolean handleMediatedAds(JSONObject response) {
        JSONArray mediated = getJSONArray(response, RESPONSE_KEY_MEDIATED_ADS);
        if (mediated != null) {
            mediatedAds = new LinkedList<MediatedAd>();
            for (int i = 0; i < mediated.length(); i++) {
                // parse through the elements of the mediated array for handlers
                JSONObject mediatedElement = getJSONObjectFromArray(mediated, i);
                if (mediatedElement != null) {
                    JSONArray handler = getJSONArray(mediatedElement, RESPONSE_KEY_HANDLER);
                    if (handler != null) {
                        for (int j = 0; j < handler.length(); j++) {
                            // get mediatedAd fields from handlerElement if available
                            JSONObject handlerElement = getJSONObjectFromArray(handler, j);
                            if (handlerElement != null) {
                                // we only care about handlers for android
                                String type = getJSONString(handlerElement, RESPONSE_KEY_TYPE);
                                if (type != null) {
                                    type = type.toLowerCase(Locale.US);
                                }
                                if ((type != null) && type.equals(RESPONSE_VALUE_ANDROID)) {
                                    String className = getJSONString(handlerElement, RESPONSE_KEY_CLASS);
                                    String param = getJSONString(handlerElement, RESPONSE_KEY_PARAM);
                                    int height = getJSONInt(handlerElement, RESPONSE_KEY_HEIGHT);
                                    int width = getJSONInt(handlerElement, RESPONSE_KEY_WIDTH);
                                    String adId = getJSONString(handlerElement, RESPONSE_KEY_ID);
                                    String resultCB = getJSONString(mediatedElement, RESPONSE_KEY_RESULT_CB);

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

    public String getContent() {
        return content != null ? content : "";
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    // banner, interstitial
    public String getType() {
        return type;
    }

    public LinkedList<MediatedAd> getMediatedAds() {
        return mediatedAds;
    }

    public boolean containsAds() {
        return containsAds;
    }

    public boolean isHttpError() {
        return isHttpError;
    }

    public HashMap<String, Object> getExtras() {
        return extras;
    }

    public void addToExtras(String key, Object value) {
        extras.put(key, value);
    }

    // also returns null if array is empty
    private static JSONArray getJSONArray(JSONObject object, String key) {
        if (object == null) return null;
        try {
            JSONArray array =  object.getJSONArray(key);
            return array.length() > 0 ? array : null;
        } catch (JSONException ignored) {}
        return null;
    }

    private static JSONObject getJSONObjectFromArray(JSONArray array, int index) {
        if (array == null) return null;
        try {
            return array.getJSONObject(index);
        } catch (JSONException ignored) {}
        return null;
    }

    private static String getJSONString(JSONObject object, String key) {
        if (object == null) return null;
        try {
            return object.getString(key);
        } catch (JSONException ignored) {}
        return null;
    }

    private static int getJSONInt(JSONObject object, String key) {
        if (object == null) return -1;
        try {
            return object.getInt(key);
        } catch (JSONException ignored) {}
        return -1;
    }
}
