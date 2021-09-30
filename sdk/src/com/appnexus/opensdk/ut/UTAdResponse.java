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
package com.appnexus.opensdk.ut;

import android.text.TextUtils;

import com.appnexus.opensdk.ANAdResponseInfo;
import com.appnexus.opensdk.ANNativeAdResponse;
import com.appnexus.opensdk.AdType;
import com.appnexus.opensdk.MediaType;
import com.appnexus.opensdk.R;
import com.appnexus.opensdk.ut.adresponse.BaseAdResponse;
import com.appnexus.opensdk.ut.adresponse.CSMSDKAdResponse;
import com.appnexus.opensdk.ut.adresponse.CSMVASTAdResponse;
import com.appnexus.opensdk.ut.adresponse.CSRAdResponse;
import com.appnexus.opensdk.ut.adresponse.RTBHTMLAdResponse;
import com.appnexus.opensdk.ut.adresponse.RTBNativeAdResponse;
import com.appnexus.opensdk.ut.adresponse.RTBVASTAdResponse;
import com.appnexus.opensdk.ut.adresponse.SSMHTMLAdResponse;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.JsonUtil;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UTAdResponse {

    private static final String RESPONSE_KEY_TAGS = "tags";
    private static final String RESPONSE_KEY_CONTENT = "content";
    private static final String RESPONSE_KEY_WIDTH = "width";
    private static final String RESPONSE_KEY_HEIGHT = "height";
    private static final String RESPONSE_KEY_PLAYER_WIDTH = "player_width";
    private static final String RESPONSE_KEY_PLAYER_HEIGHT = "player_height";
    private static final String RESPONSE_KEY_NO_BID = "nobid";
    private static final String RESPONSE_KEY_CREATIVE_ID = "creative_id";


    private static final String RESPONSE_KEY_ADS = "ads";
    private static final String RESPONSE_KEY_NOTIFY_URL = "notify_url";
    private static final String RESPONSE_KEY_CONTENT_SOURCE = "content_source";


    private static final String RESPONSE_KEY_CLASS = "class";
    private static final String RESPONSE_KEY_PARAM = "param";
    private static final String RESPONSE_KEY_PAYLOAD = "payload";
    private static final String RESPONSE_KEY_ID = "id";
    private static final String RESPONSE_KEY_UUID = "uuid";
    private static final String RESPONSE_KEY_HANDLER_URL = "url";
    private static final String RESPONSE_VALUE_ANDROID = "android";
    private static final String RESPONSE_KEY_TYPE = "type";
    private static final String RESPONSE_KEY_AD_TYPE = "ad_type";
    private static final String RESPONSE_KEY_HANDLER = "handler";
    private static final String RESPONSE_KEY_TRACKERS = "trackers";
    private static final String RESPONSE_KEY_IMPRESSION_URLS = "impression_urls";
    private static final String RESPONSE_KEY_CLICK_URLS = "click_urls";
    private static final String RESPONSE_KEY_ERROR_URLS = "error_urls";
    private static final String RESPONSE_KEY_TIMEOUT = "timeout_ms";
    private static final String RESPONSE_KEY_RESPONSE_URL = "response_url";
    private static final String RESPONSE_KEY_NO_AD_URL = "no_ad_url";
    private static final String RESPONSE_KEY_TAG_ID = "tag_id";
    private static final String RESPONSE_KEY_AUCTION_ID = "auction_id";
    private static final String RESPONSE_KEY_SECOND_PRICE = "second_price";
    private static final String RESPONSE_KEY_BUYER_MEMBER_ID = "buyer_member_id";
    private static final String RESPONSE_KEY_CPM = "cpm";
    private static final String RESPONSE_KEY_CPM_PUBLISHER_CURRENCY = "cpm_publisher_currency";
    private static final String RESPONSE_KEY_CPM_CURRENCY_CODE = "publisher_currency_code";
    private JSONObject tag;
    private Integer key = null;


    private boolean isHttpError = false;
    private LinkedList<BaseAdResponse> adList;
    private String noAdUrl;
    private int tagId;
    private String uuid;
    private String auctionID;
    private int timeout;
    private MediaType mediaType;
    private String orientation;
    private ANAdResponseInfo adResponseInfo;

    public UTAdResponse(String body, Map<String, List<String>> headers, MediaType requestMediaType, String orientation) {
        this(body, null, headers, requestMediaType, orientation);
    }

    public UTAdResponse(boolean isHttpError) {
        this.isHttpError = isHttpError;
    }

    public UTAdResponse(String body, JSONObject tag, Map<String, List<String>> headers, MediaType requestMediaType, String orientation) {
        if (StringUtil.isEmpty(body)) {
            Clog.clearLastResponse();
            return;
        }
        this.tag = tag;
        this.mediaType = requestMediaType;
        this.orientation = orientation;

        Clog.setLastResponse(body);

        Clog.d(Clog.httpRespLogTag, Clog.getString(R.string.response_body, body));

        printHeaders(headers);

        parseResponseV2(body);
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


    private void parseResponseV2(String body) {
        JSONObject response;

        try {
            if (!StringUtil.isEmpty(body)) {
                response = new JSONObject(body);
            } else {
                Clog.e(Clog.httpRespLogTag, "No Response: " + body);
                return;
            }
        } catch (JSONException e) {
            Clog.e(Clog.httpRespLogTag, Clog.getString(R.string.response_json_error, body));
            return;
        }

        try {
            JSONArray tagsArray = JsonUtil.getJSONArray(response, RESPONSE_KEY_TAGS);
            if (tagsArray != null) {
                JSONObject tagObject = null;
                if(tag == null) {
                    tagObject = JsonUtil.getJSONObjectFromArray(tagsArray, 0);
                } else {
                    tagObject = tag;
                }
                // If it contains nobid response, don't parse further.
                if (JsonUtil.getJSONBoolean(tagObject, RESPONSE_KEY_NO_BID)) {
                    adResponseInfo = new ANAdResponseInfo();
                    tagId = JsonUtil.getJSONInt(tagObject, RESPONSE_KEY_TAG_ID);
                    auctionID = JsonUtil.getJSONString(tagObject, RESPONSE_KEY_AUCTION_ID);
                    adResponseInfo.setTagId(String.valueOf(tagId));
                    adResponseInfo.setAuctionId(auctionID);

                    return;
                }

                handleAdResponse(tagObject);
            }
        } catch (Exception e) {
            // Catches XMLPullParserException, JSONException, NullPointerException and IOException
            Clog.e(Clog.httpRespLogTag, "Error parsing the ad response: " + e.getMessage());
        }
    }

    /**
     * @param response (JSONObject)
     * @return (boolean)
     * @throws Exception
     */
    // returns true if response contains an ad, false if not
    private void handleAdResponse(JSONObject response) throws Exception {

        noAdUrl = JsonUtil.getJSONString(response, RESPONSE_KEY_NO_AD_URL);
        tagId = JsonUtil.getJSONInt(response, RESPONSE_KEY_TAG_ID);
        auctionID = JsonUtil.getJSONString(response, RESPONSE_KEY_AUCTION_ID);
        timeout = JsonUtil.getJSONInt(response, RESPONSE_KEY_TIMEOUT);
        uuid = JsonUtil.getJSONString(response, RESPONSE_KEY_UUID);
        JSONArray ads = JsonUtil.getJSONArray(response, RESPONSE_KEY_ADS);
        if (ads != null) {
            adList = new LinkedList<BaseAdResponse>();
            for (int i = 0; i < ads.length(); i++) {
                // parse through the elements of the ads array for handlers
                JSONObject ad = JsonUtil.getJSONObjectFromArray(ads, i);
                String adType = JsonUtil.getJSONString(ad, RESPONSE_KEY_AD_TYPE);
                String notifyUrl = JsonUtil.getJSONString(ad, RESPONSE_KEY_NOTIFY_URL);
                String contentSource = JsonUtil.getJSONString(ad, RESPONSE_KEY_CONTENT_SOURCE);
                String creativeId = JsonUtil.getJSONString(ad, RESPONSE_KEY_CREATIVE_ID);
                int memberId = JsonUtil.getJSONInt(ad, RESPONSE_KEY_BUYER_MEMBER_ID);
                double bidPriceCpm = JsonUtil.getJSONDouble(ad, RESPONSE_KEY_CPM);
                double bidPricePublisherCurrency = JsonUtil.getJSONDouble(ad, RESPONSE_KEY_CPM_PUBLISHER_CURRENCY);
                String bidPriceCurrencyCode = JsonUtil.getJSONString(ad, RESPONSE_KEY_CPM_CURRENCY_CODE);

                ANAdResponseInfo adResponseInfo = new ANAdResponseInfo();
                adResponseInfo.setAdType(getAdType(adType));
                adResponseInfo.setTagId(String.valueOf(tagId));
                adResponseInfo.setCreativeId(creativeId);
                adResponseInfo.setAuctionId(auctionID);
                adResponseInfo.setContentSource(contentSource);
                adResponseInfo.setBuyMemberId(memberId);
                adResponseInfo.setCpm(bidPriceCpm);
                adResponseInfo.setCpmPublisherCurrency(bidPricePublisherCurrency);
                adResponseInfo.setPublisherCurrencyCode(bidPriceCurrencyCode);


                if (contentSource != null && contentSource.equalsIgnoreCase(UTConstants.CSM)) {
                    handleCSM(ad, adType, notifyUrl, adResponseInfo);
                } else if (contentSource != null && contentSource.equalsIgnoreCase(UTConstants.SSM)) {
                    handleSSM(ad, adType, adResponseInfo);
                } else if (contentSource != null && contentSource.equalsIgnoreCase(UTConstants.RTB)) {
                    handleRTB(ad, adType, notifyUrl, adResponseInfo);
                } else if (UTConstants.CSR.equalsIgnoreCase(contentSource)) {
                    handleCSR(ad, adType, notifyUrl, adResponseInfo);
                } else {
                    Clog.e(Clog.httpRespLogTag, "handleAdResponse unknown content_source");
                }
            }
        }
    }

    private void handleCSR(JSONObject adObject, String adType, String notifyUrl, ANAdResponseInfo adResponseInfo) {
        JSONObject csrObject = JsonUtil.getJSONObject(adObject, UTConstants.CSR);
        if (csrObject != null) {
            parseCSRNativeBannerAd(adObject, adResponseInfo, adType, notifyUrl);
        }
    }

    private void parseCSRNativeBannerAd(JSONObject adObject, ANAdResponseInfo adResponseInfo, String adType, String notifyUrl) {
        JSONObject csr = JsonUtil.getJSONObject(adObject, UTConstants.CSR);

        if (csr != null) {
            JSONArray handler = JsonUtil.getJSONArray(csr, RESPONSE_KEY_HANDLER);
            ArrayList<String> impressionUrls = getImpressionUrls(csr);
            ArrayList<String> clickeUrls = getClickUrls(csr);
            String responseUrl = JsonUtil.getJSONString(csr, RESPONSE_KEY_RESPONSE_URL);

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
                            String payload = JsonUtil.getJSONString(handlerElement, RESPONSE_KEY_PAYLOAD);
                            int height = JsonUtil.getJSONInt(handlerElement, RESPONSE_KEY_HEIGHT);
                            int width = JsonUtil.getJSONInt(handlerElement, RESPONSE_KEY_WIDTH);
                            String adId = JsonUtil.getJSONString(handlerElement, RESPONSE_KEY_ID);


                            if (!StringUtil.isEmpty(className)) {
                                CSRAdResponse csrAd = new CSRAdResponse(width, height, adType, responseUrl, impressionUrls, adResponseInfo, adObject);
                                csrAd.setClassName(className);
                                csrAd.setClickUrls(clickeUrls);
                                csrAd.setPayload(payload);
                                csrAd.setContentSource(UTConstants.CSR);
                                adList.add(csrAd);
                            }
                        }
                    }
                }
            }
        }
    }

    private void handleRTB(JSONObject adObject, String adType, String notifyUrl, ANAdResponseInfo adResponseInfo) throws Exception {
        JSONObject rtbObject = JsonUtil.getJSONObject(adObject, UTConstants.RTB);
        if (rtbObject != null) {
            if (rtbObject.has(UTConstants.AD_TYPE_BANNER)) {
                Clog.i(Clog.httpRespLogTag, "it's an HTML Ad");
                parseHtmlAdResponse(rtbObject, adType, adResponseInfo);
            } else if (rtbObject.has(UTConstants.AD_TYPE_VIDEO)) {
                Clog.i(Clog.httpRespLogTag, "it's a Video Ad");
                parseVideoAdResponse(rtbObject, adType, notifyUrl, adResponseInfo);
            } else if (rtbObject.has(UTConstants.AD_TYPE_NATIVE)) {
                Clog.i(Clog.httpRespLogTag, "it's a NATIVE Ad");
                parseNativeAds(adObject, adResponseInfo, adType);
            } else {
                Clog.e(Clog.httpRespLogTag, "handleRTB UNKNOWN AD_TYPE");
            }
        }
    }


    private void parseHtmlAdResponse(JSONObject rtbObject, String adType, ANAdResponseInfo adResponseInfo) throws Exception {
        JSONObject bannerObject = JsonUtil.getJSONObject(rtbObject, UTConstants.AD_TYPE_BANNER);
        if (bannerObject != null) {
            int height = JsonUtil.getJSONInt(bannerObject, RESPONSE_KEY_HEIGHT);
            int width = JsonUtil.getJSONInt(bannerObject, RESPONSE_KEY_WIDTH);
            String content = JsonUtil.getJSONString(bannerObject, RESPONSE_KEY_CONTENT);

            if (StringUtil.isEmpty(content)) {
                Clog.e(Clog.httpRespLogTag, Clog.getString(R.string.blank_ad));
            } else {
                RTBHTMLAdResponse rtbAd = new RTBHTMLAdResponse(width, height, adType, getImpressionUrls(rtbObject), adResponseInfo);
                rtbAd.setAdContent(content);
                rtbAd.setContentSource(UTConstants.RTB);
                if (content.contains(UTConstants.MRAID_JS_FILENAME)) {
                    rtbAd.addToExtras(UTConstants.EXTRAS_KEY_MRAID, true);
                }
                rtbAd.addToExtras(UTConstants.EXTRAS_KEY_ORIENTATION, orientation);
                adList.add(rtbAd);
                Clog.d(Clog.httpRespLogTag, "Html response parsed");
            }
        }
    }

    /**
     * Parse UT-V3 VAST response
     *
     * @param rtbObject (JSONObject)
     * @param adType    (String)
     * @param adResponseInfo
     * @throws Exception
     */
    private void parseVideoAdResponse(JSONObject rtbObject, String adType, String notifyUrl, ANAdResponseInfo adResponseInfo) throws Exception {

        JSONObject videoObject = JsonUtil.getJSONObject(rtbObject, UTConstants.AD_TYPE_VIDEO);
        if (videoObject != null) {
            String vastResponse = JsonUtil.getJSONString(videoObject, RESPONSE_KEY_CONTENT);
            int height = JsonUtil.getJSONInt(videoObject, RESPONSE_KEY_PLAYER_HEIGHT);
            int width = JsonUtil.getJSONInt(videoObject, RESPONSE_KEY_PLAYER_WIDTH);
            //String vastResponse = JsonUtil.getJSONString(videoObject,RESPONSE_KEY_ASSET_URL);
            if (!StringUtil.isEmpty(vastResponse)) {
                RTBVASTAdResponse rtbAd = new RTBVASTAdResponse(width, height, adType, notifyUrl, getImpressionUrls(rtbObject), adResponseInfo);
                rtbAd.setAdContent(vastResponse);
                rtbAd.setContentSource(UTConstants.RTB);
                rtbAd.addToExtras(UTConstants.EXTRAS_KEY_MRAID, true);
                adList.add(rtbAd);
            }
        }
    }

    // returns true if response contains a native response, false if not
    private void parseNativeAds(JSONObject response, ANAdResponseInfo adResponseInfo, String adType) {
        if (response != null) {
            ANNativeAdResponse anNativeAdResponse = ANNativeAdResponse.create(response);
            if (anNativeAdResponse != null) {
                RTBNativeAdResponse nativeRTB = new RTBNativeAdResponse(1, 1, adType, anNativeAdResponse, null, adResponseInfo);
                nativeRTB.setContentSource(UTConstants.RTB);
                adList.add(nativeRTB);
            }
        }
    }

    private void handleCSM(JSONObject ad, String adType, String notifyUrl, ANAdResponseInfo adResponseInfo) {
        if (adType.equalsIgnoreCase(UTConstants.AD_TYPE_BANNER)) {
            Clog.i(Clog.httpRespLogTag, "Parsing SDK Mediation Ad");
            parseCSMSDKMediation(ad, adType, adResponseInfo);
        } else if (adType.equalsIgnoreCase(UTConstants.AD_TYPE_NATIVE)) {
            Clog.i(Clog.httpRespLogTag, "Parsing Native Mediation Ad");
            parseCSMSDKMediation(ad, adType, adResponseInfo);
        } else if (adType.equalsIgnoreCase(UTConstants.AD_TYPE_VIDEO)) {
            Clog.i(Clog.httpRespLogTag, "Parsing Video CSM Ad");
            parseVideoCSMResponse(ad, adType, notifyUrl, adResponseInfo);
        }
    }

    private void parseCSMSDKMediation(JSONObject ad, String adType, ANAdResponseInfo adResponseInfo) {

        JSONObject csm = JsonUtil.getJSONObject(ad, UTConstants.CSM);

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
                            String secondPrice = JsonUtil.getJSONString(handlerElement, RESPONSE_KEY_SECOND_PRICE);
                            int networkTimeout = JsonUtil.getJSONInt(csm, RESPONSE_KEY_TIMEOUT);
                            int csmTimeout = (networkTimeout > 0 &&  networkTimeout != 500) ? networkTimeout : (int) Settings.MEDIATED_NETWORK_TIMEOUT;


                            if (param.contains("\"optimized\":true") && !StringUtil.isEmpty(secondPrice)) {
                                try {
                                    JSONObject paramObj = new JSONObject(param);
                                    paramObj.put(RESPONSE_KEY_SECOND_PRICE, secondPrice);
                                    param = paramObj.toString();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (!StringUtil.isEmpty(className)) {
                                adResponseInfo.setNetworkName(className);
                                CSMSDKAdResponse csmAd = new CSMSDKAdResponse(width, height, adType, responseUrl, impressionUrls, adResponseInfo, ad);
                                csmAd.setClassName(className);
                                csmAd.setId(adId);
                                csmAd.setParam(param);
                                csmAd.setNetworkTimeout(csmTimeout);
                                csmAd.setContentSource(UTConstants.CSM);
                                adList.add(csmAd);
                            }
                        }
                    }
                }
            }
        }
    }

    private void parseVideoCSMResponse(JSONObject ad, String adType, String notifyUrl, ANAdResponseInfo adResponseInfo) {
        JSONObject csm = JsonUtil.getJSONObject(ad, UTConstants.CSM);
        if (csm != null) {
            JSONArray handler = JsonUtil.getJSONArray(csm, RESPONSE_KEY_HANDLER);
            if (handler != null) {
                CSMVASTAdResponse csmVideoAd = new CSMVASTAdResponse(-1, -1, adType, null, adResponseInfo, uuid);
                csmVideoAd.setAdJSONContent(ad);
                csmVideoAd.setAuction_id(String.valueOf(auctionID));
                csmVideoAd.setTag_id(tagId);
                csmVideoAd.setTimeout_ms(timeout);
                csmVideoAd.setContentSource(UTConstants.CSM_VIDEO);
                adList.add(csmVideoAd);
            }
        }
    }


    private void handleSSM(JSONObject ad, String adType, ANAdResponseInfo adResponseInfo) {
        JSONObject ssm = JsonUtil.getJSONObject(ad, UTConstants.SSM);
        if (ssm != null) {
            JSONArray handler = JsonUtil.getJSONArray(ssm, RESPONSE_KEY_HANDLER);
            JSONObject banner = JsonUtil.getJSONObject(ssm, UTConstants.AD_TYPE_BANNER);
            int networkTimeout = JsonUtil.getJSONInt(ssm, RESPONSE_KEY_TIMEOUT);
            int ssmTimeout = (networkTimeout > 0 &&  networkTimeout != 500) ? networkTimeout : (int) Settings.MEDIATED_NETWORK_TIMEOUT;

            int height = JsonUtil.getJSONInt(banner, RESPONSE_KEY_HEIGHT);
            int width = JsonUtil.getJSONInt(banner, RESPONSE_KEY_WIDTH);


            if (handler != null) {
                for (int j = 0; j < handler.length(); j++) {
                    JSONObject handlerElement = JsonUtil.getJSONObjectFromArray(handler, j);
                    if (handlerElement != null) {
                        String handlerUrl = JsonUtil.getJSONString(handlerElement, RESPONSE_KEY_HANDLER_URL);
                        if (!StringUtil.isEmpty(handlerUrl)) {
                            String responseUrl = JsonUtil.getJSONString(ssm, RESPONSE_KEY_RESPONSE_URL);
                            SSMHTMLAdResponse ssmAd = new SSMHTMLAdResponse(width, height, adType, responseUrl, getImpressionUrls(ssm), adResponseInfo);
                            ssmAd.setAdUrl(handlerUrl);
                            ssmAd.setNetworkTimeout(ssmTimeout);
                            ssmAd.setContentSource(UTConstants.SSM);
                            ssmAd.addToExtras(UTConstants.EXTRAS_KEY_ORIENTATION, orientation);
                            adList.add(ssmAd);
                        }
                    }
                }
            }
        }
    }


    private ArrayList<String> getClickUrls(JSONObject contentSourceObject) {
        JSONArray trackers = JsonUtil.getJSONArray(contentSourceObject, RESPONSE_KEY_TRACKERS);

        ArrayList<String> clickUrls = new ArrayList<String>();
        if (trackers != null) {
            for (int i = 0; i < trackers.length(); i++) {
                try {
                    JSONObject obj = trackers.getJSONObject(0);
                    JSONArray trackerClickUrls = JsonUtil.getJSONArray(obj, RESPONSE_KEY_CLICK_URLS);
                    ArrayList<String> trackerClickUrlsStringArray = JsonUtil.getStringArrayList(trackerClickUrls);
                    if (trackerClickUrlsStringArray != null) {
                        clickUrls.addAll(trackerClickUrlsStringArray);
                    }
                } catch (JSONException e) {
                }
            }
        }
        return clickUrls;
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

    private ArrayList<String> getErrorUrls(JSONObject contentSourceObject) {
        JSONArray trackers = JsonUtil.getJSONArray(contentSourceObject, RESPONSE_KEY_TRACKERS);

        ArrayList<String> impressionUrls = new ArrayList<String>();
        if (trackers != null) {
            JSONObject trackerObject = JsonUtil.getJSONObjectFromArray(trackers, 0);
            JSONArray urlJsonArray = JsonUtil.getJSONArray(trackerObject, RESPONSE_KEY_ERROR_URLS);
            impressionUrls = JsonUtil.getStringArrayList(urlJsonArray);

        }
        return impressionUrls;
    }

    public LinkedList<BaseAdResponse> getAdList() {
        return adList;
    }

    boolean isHttpError() {
        return isHttpError;
    }

    public MediaType getMediaType() {
        return mediaType;
    }


    public String getNoAdUrl() {
        return noAdUrl;
    }

    public ANAdResponseInfo getAdResponseInfo() {
        return adResponseInfo;
    }

    private AdType getAdType(String adTypeString){
        if (adTypeString.equalsIgnoreCase(UTConstants.AD_TYPE_BANNER)) {
            return AdType.BANNER;
        } else if (adTypeString.equalsIgnoreCase(UTConstants.AD_TYPE_VIDEO)) {
            return AdType.VIDEO;
        } else if (adTypeString.equalsIgnoreCase(UTConstants.AD_TYPE_NATIVE)) {
            return AdType.NATIVE;
        }
        return AdType.UNKNOWN;
    }
}
