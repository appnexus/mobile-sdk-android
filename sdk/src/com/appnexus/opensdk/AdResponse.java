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

import com.appnexus.opensdk.utils.Clog;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

public class AdResponse {
    private String body;
    private int height;
    private int width;
    private String type;
    private boolean isMraid = false;

    private LinkedList<MediatedAd> mediatedAds;

    private boolean containsAds = false;

    private boolean isHttpError = false;
    private boolean isConnectivityRetry = false;
    private boolean isBlankRetry = false;

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

    public AdResponse(String body, Header[] headers) {
        this.body = body;

        if (body == null) {
            Clog.clearLastResponse();
            return;
        } else if (body.length() == 0) {
            Clog.w(Clog.httpRespLogTag, Clog.getString(R.string.response_blank));
            Clog.clearLastResponse();
            return;
        }

        Clog.setLastResponse(body);

        Clog.d(Clog.httpRespLogTag,
                Clog.getString(R.string.response_body, body));
        if (headers != null) {
            for (Header h : headers) {
                Clog.v(Clog.httpRespLogTag,
                        Clog.getString(R.string.response_header, h.getName(),
                                h.getValue()));
            }
        }

        parseResponse(body);

        isMraid = getBody().contains(MRAID_JS_FILENAME);

    }

    public AdResponse(boolean isHttpError, boolean isConnectivityRetry, boolean isBlankRetry) {
        this.isHttpError = isHttpError;
        this.isConnectivityRetry = isConnectivityRetry;
        this.isBlankRetry = isBlankRetry;
    }

    private void parseResponse(String body) {
        JSONObject response;

        try {
            response = new JSONObject(body);
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
            // for now, just take the first ad
            JSONObject firstAd = getJSONObjectFromArray(ads, 0);
            type = getJSONString(firstAd, RESPONSE_KEY_TYPE);
            height = getJSONInt(firstAd, RESPONSE_KEY_HEIGHT);
            width = getJSONInt(firstAd, RESPONSE_KEY_WIDTH);
            body = getJSONString(firstAd, RESPONSE_KEY_CONTENT);
            if (body == null || body.equals("")) {
                Clog.e(Clog.httpRespLogTag,
                        Clog.getString(R.string.blank_ad));
            }
            else {
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
                    // get mediatedAd fields from handler if available
                    JSONObject handler = getJSONObject(mediatedElement, RESPONSE_KEY_HANDLER);
                    if (handler != null) {
                        // we only care about handlers for android
                        String type = getJSONString(handler, RESPONSE_KEY_TYPE);
                        if ((type != null) && type.toLowerCase().equals(RESPONSE_VALUE_ANDROID)) {
                            String className = getJSONString(handler, RESPONSE_KEY_CLASS);
                            String param = getJSONString(handler, RESPONSE_KEY_PARAM);
                            int height = getJSONInt(handler, RESPONSE_KEY_HEIGHT);
                            int width = getJSONInt(handler, RESPONSE_KEY_WIDTH);
                            String adId = getJSONString(handler, RESPONSE_KEY_ID);
                            String resultCB = getJSONString(mediatedElement, RESPONSE_KEY_RESULT_CB);

                            mediatedAds.add(new MediatedAd(className,
                                    param, width, height, adId,
                                    resultCB));
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

    public String getBody() {
        if (body == null)
            return "";
        return body;
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

    public boolean isMraid() {
        return isMraid;
    }

    public boolean isConnectivityRetry() {
        return isConnectivityRetry;
    }

    public boolean isBlankRetry() {
        return isBlankRetry;
    }

    public boolean isHttpError() {
        return isHttpError;
    }

    /**
     * JSON parsing helper methods
     */

    private static JSONObject getJSONObject(JSONObject object, String key) {
        if (object == null) return null;
        try {
            return object.getJSONObject(key);
        } catch (JSONException ignored) {}
        return null;
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
