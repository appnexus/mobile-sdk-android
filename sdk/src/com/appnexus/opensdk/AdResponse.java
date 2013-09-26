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
    public AdRequester requester;
    public String body;
    public int height;
    public int width;
    private String type;
    boolean isMraid = false;

    private LinkedList<MediatedAd> mediatedAds;

    private boolean containsAds = false;

    final static String http_error = "HTTP_ERROR";
    public static final String HTTP_OK = "200 OK";

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

    public AdResponse(AdRequester requester, String body, Header[] headers) {
        this.requester = requester;
        this.body = body;

        if (body == null) {
            Clog.clearLastResponse();
            return;
        } else if (body.equals(AdResponse.http_error)) {
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
        //TODO: review, do we really need to print this?
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

    private void parseResponse(String body) {
        JSONObject response = null;

        if (body.equals(AdRequest.RETRY) || body.equals(AdRequest.BLANK) || body.equals(HTTP_OK)) {
            return;
        }

        try {
            response = new JSONObject(body);
            if (!checkStatusIsValid(response))
                return;
        } catch (JSONException e) {
            Clog.e(Clog.httpRespLogTag,
                    Clog.getString(R.string.response_json_error, body), e);
            if (response == null)
                return;
        }

        try {
            if (handleStdAds(response)) return;
        } catch (JSONException e) {
            Clog.e(Clog.httpRespLogTag,
                    Clog.getString(R.string.response_json_error, body), e);
        }

        try {
            if (handleMediatedAds(response)) return;
        } catch (JSONException e) {
            Clog.e(Clog.httpRespLogTag,
                    Clog.getString(R.string.response_json_error, body), e);
        }
    }

    // returns true if no error in status. don't fail on null or missing status
    private boolean checkStatusIsValid(JSONObject response) throws JSONException {
        if (!response.isNull(RESPONSE_KEY_STATUS)) {
            String status = response.getString(RESPONSE_KEY_STATUS);
            if (status.equals(RESPONSE_VALUE_ERROR)) {
                String error = response.getString(RESPONSE_KEY_ERROR_MESSAGE);
                Clog.e(Clog.httpRespLogTag,
                        Clog.getString(R.string.response_error, error));
                return false;
            }
        }
        return true;
    }

    // returns true if response contains an ad, false if not
    private boolean handleStdAds(JSONObject response) throws JSONException {
        if (!response.isNull(RESPONSE_KEY_ADS) && response.getJSONArray(RESPONSE_KEY_ADS).length() > 0) {
            JSONArray ads = response.getJSONArray(RESPONSE_KEY_ADS);
            // for now, just take the first ad
            JSONObject firstAd = ads.getJSONObject(0);
            type = firstAd.getString(RESPONSE_KEY_TYPE);
            height = firstAd.getInt(RESPONSE_KEY_HEIGHT);
            width = firstAd.getInt(RESPONSE_KEY_WIDTH);
            body = firstAd.getString(RESPONSE_KEY_CONTENT);
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
    private boolean handleMediatedAds(JSONObject response) throws JSONException {
        if (!response.isNull(RESPONSE_KEY_MEDIATED_ADS) && response.getJSONArray(RESPONSE_KEY_MEDIATED_ADS).length() > 0) {
            JSONArray mediated = response.getJSONArray(RESPONSE_KEY_MEDIATED_ADS);
            mediatedAds = new LinkedList<MediatedAd>();
            for (int i = 0; i < mediated.length(); i++) {
                JSONObject handler = mediated.getJSONObject(i).getJSONObject(RESPONSE_KEY_HANDLER);
                if (handler.getString(RESPONSE_KEY_TYPE).toLowerCase().equals(RESPONSE_VALUE_ANDROID)) {
                    String className = handler.getString(RESPONSE_KEY_CLASS);
                    String param = handler.getString(RESPONSE_KEY_PARAM);
                    int height = handler.getInt(RESPONSE_KEY_HEIGHT);
                    int width = handler.getInt(RESPONSE_KEY_WIDTH);
                    String adId = handler.getString(RESPONSE_KEY_ID);
                    String resultCB = mediated.getJSONObject(i).getString(RESPONSE_KEY_RESULT_CB);

                    mediatedAds.add(new MediatedAd(className,
                            param, width, height, adId,
                            resultCB));
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
}
