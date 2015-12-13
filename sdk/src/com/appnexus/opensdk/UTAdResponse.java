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

import com.appnexus.opensdk.adresponsedata.BaseAdResponse;
import com.appnexus.opensdk.adresponsedata.CSMAdResponse;
import com.appnexus.opensdk.adresponsedata.RTBAdResponse;
import com.appnexus.opensdk.adresponsedata.SSMAdResponse;
import com.appnexus.opensdk.utils.ANConstants;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.JsonUtil;
import com.appnexus.opensdk.utils.StringUtil;
import com.appnexus.opensdk.vastdata.AdModel;
import com.appnexus.opensdk.vastdata.VastResponseParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;

class UTAdResponse {

    private static final String UTF_8 = "UTF-8";

    private static final String RESPONSE_KEY_TAGS = "tags";
    protected static final String RESPONSE_KEY_VIDEO = "video";
    protected static final String RESPONSE_KEY_BANNER = "banner";
    private static final String RESPONSE_KEY_CONTENT = "content";
    private static final String RESPONSE_KEY_WIDTH = "width";
    private static final String RESPONSE_KEY_HEIGHT = "height";
    public static final String RESPONSE_KEY_NO_BID = "nobid";

    private static final String RESPONSE_KEY_RTB = "rtb";
    private static final String RESPONSE_KEY_ADS = "ads";
    private static final String RESPONSE_KEY_NOTIFY_URL = "notify_url";
    private static final String RESPONSE_KEY_CONTENT_SOURCE = "content_source";

    private static final String RESPONSE_KEY_CLASS = "class";
    private static final String RESPONSE_KEY_PARAM = "param";
    private static final String RESPONSE_KEY_ID = "id";
    private static final String RESPONSE_KEY_HANDLER_URL = "url";
    private static final String RESPONSE_VALUE_ANDROID = "android";
    private static final String RESPONSE_KEY_TYPE = "type";
    private static final String RESPONSE_KEY_AD_TYPE = "ad_type";
    private static final String RESPONSE_KEY_CLIENT_SIDE_MEDIATION = "csm";
    private static final String RESPONSE_KEY_SERVER_SIDE_MEDIATION = "ssm";
    private static final String RESPONSE_KEY_HANDLER = "handler";
    private static final String RESPONSE_KEY_TRACKERS = "trackers";
    private static final String RESPONSE_KEY_IMPRESSION_URLS = "impression_urls";
    private static final String RESPONSE_KEY_TIMEOUT = "timeout_ms";
    private static final String RESPONSE_KEY_RESPONSE_URL = "response_url";

    private boolean containsAds = false;
    private boolean isHttpError = false;
    private LinkedList<BaseAdResponse> adList;



    public UTAdResponse(String body) {
        if (StringUtil.isEmpty(body)) {
            Clog.clearLastResponse();
            return;
        }

        Clog.setLastResponse(body);

        Clog.i(Clog.httpRespLogTag, Clog.getString(R.string.response_body, body));

        parseResponseV2(body);
    }


    public UTAdResponse(boolean isHttpError) {
        this.isHttpError = isHttpError;
    }


    private void parseResponseV2(String body) {
        JSONObject response;

        try {
            if (!StringUtil.isEmpty(body)) {
                response = new JSONObject(body);
            } else {
                Clog.e(Clog.httpRespLogTag,  "No Response: "+ body);
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

                handleAdResponse(tagObject);
            }
        } catch (Exception e) {
            // Catches XMLPullParserException, JSONException, NullPointerException and IOException
            Clog.e(Clog.httpReqLogTag, "Error parsing the ad response: " + e.getMessage());
            containsAds = false;
        }
    }


    /**
     *
     * @param response
     * @return
     * @throws Exception
     */
    // returns true if response contains an ad, false if not
    private boolean handleAdResponse(JSONObject response) throws Exception {

        JSONArray ads = JsonUtil.getJSONArray(response, RESPONSE_KEY_ADS);
        if (ads != null) {
            adList = new LinkedList<BaseAdResponse>();
            for (int i = 0; i < ads.length(); i++) {
                // parse through the elements of the ads array for handlers
                JSONObject ad = JsonUtil.getJSONObjectFromArray(ads, i);
                String adType = JsonUtil.getJSONString(ad, RESPONSE_KEY_AD_TYPE);
                String notifyUrl = JsonUtil.getJSONString(ad, RESPONSE_KEY_NOTIFY_URL);
                String contentSource = JsonUtil.getJSONString(ad, RESPONSE_KEY_CONTENT_SOURCE);

                if (contentSource != null && contentSource.equalsIgnoreCase(ANConstants.CSM)){
                    handleCSM(ad, adType, notifyUrl);
                }else if(contentSource != null && contentSource.equalsIgnoreCase(ANConstants.SSM)){
                    handleSSM(ad, adType, notifyUrl);
                }else {
                    handleRTB(ad, adType, notifyUrl);
                }
            }

            if (!adList.isEmpty()) {
                containsAds = true;
                return true;
            }
        }
        return false;
    }

    private void handleRTB(JSONObject adObject, String adType, String notifyUrl) throws Exception {
        JSONObject rtbObject = JsonUtil.getJSONObject(adObject, RESPONSE_KEY_RTB);
        if (rtbObject != null) {
            if(rtbObject.has(RESPONSE_KEY_BANNER)) {
                Clog.i(Clog.httpReqLogTag, "it's an HTML Ad");
                parseHtmlAdResponse(rtbObject, adType, notifyUrl);
            }else{
                Clog.i(Clog.httpReqLogTag, "it's a Video Ad");
                parseVastAdReponse(rtbObject, adType, notifyUrl);
            }
        }
    }


    private void parseHtmlAdResponse(JSONObject rtbObject, String adType, String notifyUrl) throws Exception{
        JSONObject bannerObject = JsonUtil.getJSONObject(rtbObject, RESPONSE_KEY_BANNER);
        if(bannerObject != null) {
            int height = JsonUtil.getJSONInt(bannerObject, RESPONSE_KEY_HEIGHT);
            int width = JsonUtil.getJSONInt(bannerObject, RESPONSE_KEY_WIDTH);
            String content = JsonUtil.getJSONString(bannerObject, RESPONSE_KEY_CONTENT);

            if (StringUtil.isEmpty(content)) {
                Clog.e(Clog.httpRespLogTag, Clog.getString(R.string.blank_ad));
            } else {
                RTBAdResponse rtbAd = new RTBAdResponse(width, height, adType, notifyUrl, getImpressionUrls(rtbObject));
                rtbAd.setAdContent(content);
                rtbAd.setContentSource(ANConstants.RTB);
                if (content.contains(ServerResponse.MRAID_JS_FILENAME)) {
                    rtbAd.addToExtras(ServerResponse.EXTRAS_KEY_MRAID, true);
                }
                adList.add(rtbAd);
                Clog.i(Clog.httpReqLogTag, "parseHTMLAd: true");
                containsAds = true;
            }
        }
    }

    /**
     *  Parse UT-V2 VAST response
     * @param rtbObject
     * @param adType
     * @throws Exception
     */
    private void parseVastAdReponse(JSONObject rtbObject, String adType, String notifyUrl) throws Exception {

        JSONObject videoObject = JsonUtil.getJSONObject(rtbObject, RESPONSE_KEY_VIDEO);
        if(videoObject != null) {
            String vastResponse = JsonUtil.getJSONString(videoObject, RESPONSE_KEY_CONTENT);
            if(!StringUtil.isEmpty(vastResponse)) {
                InputStream stream = new ByteArrayInputStream(vastResponse.getBytes(Charset.forName(UTF_8)));

                VastResponseParser vastResponseParser = new VastResponseParser();
                AdModel vastAdResponse = vastResponseParser.readVAST(stream);
                if(vastAdResponse != null && vastAdResponse.containsLinearAd()) {
                    RTBAdResponse rtbAd = new RTBAdResponse(-1, -1, adType, notifyUrl, getImpressionUrls(rtbObject));
                    rtbAd.setVastAdResponse(vastAdResponse);
                    rtbAd.setContentSource(ANConstants.RTB);
                    adList.add(rtbAd);
                    containsAds = true;
                    Clog.i(Clog.httpReqLogTag, "Vast response parsed");
                }
            }
        }
    }


    private void handleCSM(JSONObject ad, String adType, String notifyUrl) {
        JSONObject csm = JsonUtil.getJSONObject(ad, RESPONSE_KEY_CLIENT_SIDE_MEDIATION);

        if (csm != null) {
            JSONArray handler = JsonUtil.getJSONArray(csm, RESPONSE_KEY_HANDLER);
            ArrayList<String> impressionUrls = getImpressionUrls(csm);
            String responseUrl = JsonUtil.getJSONString(csm, RESPONSE_KEY_RESPONSE_URL);

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

                            if (!StringUtil.isEmpty(className)) {
                                CSMAdResponse csmAd = new CSMAdResponse(width, height, adType, notifyUrl, impressionUrls);
                                csmAd.setClassName(className);
                                csmAd.setId(adId);
                                csmAd.setParam(param);
                                csmAd.setResponseUrl(responseUrl);
                                csmAd.setContentSource(ANConstants.CSM);
                                adList.add(csmAd);
                            }
                        }
                    }
                }
            }
        }
    }


    private void handleSSM(JSONObject ad, String adType, String notifyUrl) {
        JSONObject ssm = JsonUtil.getJSONObject(ad, RESPONSE_KEY_SERVER_SIDE_MEDIATION);
        if (ssm != null) {
            JSONArray handler = JsonUtil.getJSONArray(ssm, RESPONSE_KEY_HANDLER);
            JSONObject banner = JsonUtil.getJSONObject(ssm, RESPONSE_KEY_BANNER);
            int ssmTimeout = JsonUtil.getJSONInt(ssm, RESPONSE_KEY_TIMEOUT);
            int height = JsonUtil.getJSONInt(banner, RESPONSE_KEY_HEIGHT);
            int width = JsonUtil.getJSONInt(banner, RESPONSE_KEY_WIDTH);

            ArrayList<String> impressionUrls = getImpressionUrls(ssm);
            if (handler != null) {
                for (int j = 0; j < handler.length(); j++) {
                    JSONObject handlerElement = JsonUtil.getJSONObjectFromArray(handler, j);
                    if (handlerElement != null) {
                        String handlerUrl = JsonUtil.getJSONString(handlerElement, RESPONSE_KEY_HANDLER_URL);
                        if (!StringUtil.isEmpty(handlerUrl)) {
                            SSMAdResponse ssmAd = new SSMAdResponse(width, height, adType, notifyUrl, impressionUrls);
                            ssmAd.setAdUrl(handlerUrl);
                            ssmAd.setSsmTimeout(ssmTimeout);
                            ssmAd.setContentSource(ANConstants.SSM);
                            adList.add(ssmAd);
                        }
                    }
                }
            }
        }
    }


    private ArrayList<String> getImpressionUrls(JSONObject contentSourceObject) {
        JSONArray trackers = JsonUtil.getJSONArray(contentSourceObject, RESPONSE_KEY_TRACKERS);

        ArrayList<String> impressionUrls = new ArrayList<String>();
        if (trackers != null) {
            JSONObject impressionsObj = JsonUtil.getJSONObjectFromArray(trackers, 0);
            JSONArray impressionsArray = JsonUtil.getJSONArray(impressionsObj, RESPONSE_KEY_IMPRESSION_URLS);
            impressionUrls = JsonUtil.getStringArrayList(impressionsArray);

        }
        return impressionUrls;
    }


    LinkedList<BaseAdResponse> getAdList(){
        return adList;
    }

    boolean containsAds() {
        return containsAds;
    }

    boolean isHttpError() {
        return isHttpError;
    }

}
