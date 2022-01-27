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

import java.util.ArrayList;


public class TestResponsesUT {

    public static final String RESPONSE_URL_PATH = "response_url?";
    public static final String IMPRESSION_URL_PATH = "impression_url?";
    public static final String NO_AD_URL_PATH = "no_ad?";
    public static final String NOTIFY_URL_PATH = "vast_track/v2?info&notifyURL";
    public static final String SSM_URL_PATH = "ssm?";
    public static final String REQUEST_URL = "http://mobile.devnxs.net/request_url?";
    private static final String ICON_URL = "http://vcdn.adnxs.com/p/creative-image/2f/3d/a4/8d/2f3da48d-f786-4361-ab03-e9c0c6e941e4.png";
    private static final String IMAGE_URL = "http://vcdn.adnxs.com/p/creative-image/d0/4a/de/2d/d04ade2d-52dd-4b84-9aa9-6eaef24f0eda.png";
    public static String NO_AD_URL = "http://mobile.devnxs.net/no_ad_url?";
    public static String NOTIFY_URL = "http://mobile.devnxs.net/vast_track/v2?info&notifyURL";
    public static String IMPRESSION_URL = "";
    public static String RESPONSE_URL = "";
    public static final String NO_BID_TRUE = "true";
    public static final String NO_BID_FALSE = "false";
    public static String SSM_URL = "http://nym1-mobile.adnxs.com/ssm";
    public static final String SSM_NO_URL = "";

    public static void setTestURL(String url) {
        RESPONSE_URL = url + RESPONSE_URL_PATH;
        NO_AD_URL = url + NO_AD_URL_PATH;
        NOTIFY_URL = url + NOTIFY_URL_PATH;
        SSM_URL = url + SSM_URL_PATH;
        IMPRESSION_URL = url + IMPRESSION_URL_PATH;
    }

    public static final String DUMMY_BANNER_CONTENT = "<script type=\\\"text/javascript\\\">document.write('<div style=\\\"background-color:#EF8200;height:1000px;width:1000px;\\\"><p>%s</p></div>');</script>";
    //    private static final String AN_NATIVE_RESPONSE = "[{\"type\":\"%s\",\"title\":\"%s\",\"description\":\"%s\", \"desc2\":\"%s\",\"full_text\":\"%s\",\"context\":\"%s\",\"icon_img_url\":\"%s\",\"main_media\":%s,\"cta\":\"%s\",\"click_trackers\":[%s],\"impression_trackers\":[%s],\"rating\":%s,\"click_url\":\"%s\",\"click_fallback_url\":\"%s\",\"sponsored_by\":\"%s\",\"custom\":%s}]";
    private static final String AN_NATIVE_RESPONSE = "{\"title\": \"%s\", \"desc\": \"%s\", \"sponsored\": \"%s\", \"ctatext\": \"%s\", \"rating\": \"%s\", \"icon\": {\"url\": \"%s\", \"width\": %d, \"height\": %d}, \"main_img\": {\"url\": \"%s\", \"width\": %d, \"height\": %d},  \"link\": {\"url\": \"%s\", \"click_trackers\": [\"%s\"]}, \"impression_trackers\": [\"%s\"], \"id\": %d, \"desc2\": \"%s\"}";
    public static final String AN_NATIVE_VIDEO_RESPONSE = "{ \"title\": \"%s\", \"desc\": \"%s\", \"sponsored\": \"%s\", \"ctatext\": \"%s\", \"rating\": \"%d\", \"icon\": { \"url\": \"%s\", \"width\": %d, \"height\": %d }, \"main_img\": { \"url\": \"%s\", \"width\": %d, \"height\": %d }, \"link\": { \"url\": \"%s\", \"fallback_url\": \"%s\", \"click_trackers\": [ \"%s\", \"%s\", \"%s\" ] }, \"impression_trackers\": [ \"%s\", \"%s\", \"%s\", \"%s\" ], \"javascript_trackers\": \"%s\", \"id\": %d, \"displayurl\": \"%s\", \"likes\": \"%d\", \"downloads\": \"%d\", \"price\": \"%d\", \"saleprice\": \"%d\", \"phone\": \"%d\", \"address\": \"%s\", \"desc2\": \"%s\", \"video\": { \"content\": \"%s\" }, \"privacy_link\": \"%s\" }";
    private static final String MRAID_CONTENT = "<script type=\\\"text/javascript\\\" src=\\\"mraid.js\\\"></script><script type=\\\"text/javascript\\\">document.write('<div style=\\\"background-color:#EF8200;height:1000px;width:1000px;\\\"><p>%s</p></div>');</script>";
    private static final String NATIVE_MAIN_MEDIA = "[{\"url\":\"%s\",\"width\":%d,\"height\":%d,\"label\":\"default\"},{\"url\":\"%s\",\"width\":%d,\"height\":%d},{\"url\":\"%s\",\"width\":%d,\"height\":%d}]";
    private static final String NATIVE_RATING = "{\"value\":%.2f,\"scale\":%.2f}";
    private static final String RTB_NATIVE_VIEWABILITY_CONFIG = "<script type=\\\"text/javascript\\\" async=\\\"true\\\" src=\\\"https://acdn.adnxs.com/mobile/omsdk/test/omid-validation-verification-script-1.2.5.js#v;vk=dummyVendor;tv=cet=0;cecb=\\\"></script>";
    private static final String RTB_NATIVE_RENDERER_VIEWABILITY_CONFIG = "<script type=\\\"text/javascript\\\" async=\\\"true\\\" src=\\\"https://acdn.adnxs.com/mobile/omsdk/test/omid-validation-verification-script-1.2.5.js#v;vk=dummyVendorRenderer;tv=cet=0;cecb=\\\"></script>";
    private static final String CSM_NATIVE_VIEWABILITY_CONFIG = "<script type=\\\"text/javascript\\\" async=\\\"true\\\" src=\\\"https://acdn.adnxs.com/mobile/omsdk/test/omid-validation-verification-script-1.2.5.js#v;vk=dummyVendorCSM;tv=cet=0;cecb=\\\"></script>";
    // template strings
    private static final String CLASSNAME = "com.appnexus.opensdk.testviews.%s";

    //Cookie Strings
    public static final String UUID_COOKIE_1 = "uuid2=1263546692102051030; Path=/; Max-Age=7776000; Expires=Wed, 07-Dec-2025 16:23:26 GMT; Domain=.adnxs.com; HttpOnly";
    public static final String UUID_COOKIE_RESET = "uuid2=-1; Path=/; Max-Age=314496000; Expires=Thu, 27-Aug-2026 18:28:50 GMT; Domain=.adnxs.com; HttpOnly";

    // UT Response - Template String
    public static final String RESPONSE = "{\"version\":\"3.0.0\",\"tags\":[{\"tag_id\":123456,\"auction_id\":\"123456789\",\"nobid\":\"%s\",\"no_ad_url\":\"%s\",\"timeout_ms\":10000,\"ad_profile_id\":98765,%s}]}";
    public static final String RESPONSE_ = "{\"version\":\"3.0.0\",\"tags\":[{\"tag_id\":987654,\"auction_id\":\"123456789\",\"nobid\":\"%s\",\"no_ad_url\":\"%s\",\"timeout_ms\":10000,\"ad_profile_id\":98765,%s}]}";

    public static final String ADS = "\"ads\":[%s]";

    // Ad objects
    public static final String RTB_BANNER = "{\"cpm\":0.000010,\"cpm_publisher_currency\":0.000010,\"publisher_currency_code\":\"$\",\"content_source\":\"rtb\",\"ad_type\":\"banner\",\"buyer_member_id\":123,\"creative_id\":6332753,\"media_type_id\":1,\"media_subtype_id\":1,\"client_initiated_ad_counting\":true,\"rtb\":{\"banner\":{\"content\":\"%s\",\"width\":%d,\"height\":%d},\"trackers\":[{\"impression_urls\":[\"%s\"],\"video_events\":{}}]}}";
    public static final String RTB_BANNER_ = "{\"cpm\":0.000010,\"cpm_publisher_currency\":0.000010,\"publisher_currency_code\":\"$\",\"content_source\":\"rtb\",\"ad_type\":\"banner\",\"buyer_member_id\":456,\"creative_id\":1234567,\"media_type_id\":1,\"media_subtype_id\":1,\"client_initiated_ad_counting\":true,\"rtb\":{\"banner\":{\"content\":\"%s\",\"width\":%d,\"height\":%d},\"trackers\":[{\"impression_urls\":[\"%s\"],\"video_events\":{}}]}}";
    public static final String CSM_BANNER = "{\"cpm\":0.000010,\"cpm_publisher_currency\":0.000010,\"publisher_currency_code\":\"$\",\"content_source\":\"csm\",\"ad_type\":\"banner\",\"buyer_member_id\":123,\"creative_id\":44863345,\"media_type_id\":1,\"media_subtype_id\":1,\"client_initiated_ad_counting\":false,\"csm\":{\"banner\":{\"content\":\"%s\",\"width\":10,\"height\":10},\"timeout_ms\":500,\"handler\":[{\"param\":\"%s\",\"height\":\"%d\",\"width\":\"%d\",\"id\":\"%s\",\"type\":\"%s\",\"class\":\"%s\"},{\"param\":\"#{PARAM}\",\"height\":\"50\",\"width\":\"320\",\"id\":\"163441140754789_163441480754755\",\"type\":\"ios\",\"class\":\"DummyIOSClass\"}],\"trackers\":[{\"impression_urls\":[\"%s\"],\"video_events\":{}}],\"request_url\":\"%s\",\"response_url\":\"%s\"}}";
    public static final String CSM_BANNER_TIMEOUT_ZERO = "{\"cpm\":0.000010,\"cpm_publisher_currency\":0.000010,\"publisher_currency_code\":\"$\",\"content_source\":\"csm\",\"ad_type\":\"banner\",\"buyer_member_id\":123,\"creative_id\":44863345,\"media_type_id\":1,\"media_subtype_id\":1,\"client_initiated_ad_counting\":false,\"csm\":{\"banner\":{\"content\":\"%s\",\"width\":10,\"height\":10},\"timeout_ms\":0,\"handler\":[{\"param\":\"%s\",\"height\":\"%d\",\"width\":\"%d\",\"id\":\"%s\",\"type\":\"%s\",\"class\":\"%s\"},{\"param\":\"#{PARAM}\",\"height\":\"50\",\"width\":\"320\",\"id\":\"163441140754789_163441480754755\",\"type\":\"ios\",\"class\":\"DummyIOSClass\"}],\"trackers\":[{\"impression_urls\":[\"%s\"],\"video_events\":{}}],\"request_url\":\"%s\",\"response_url\":\"%s\"}}";
    public static final String CSM_BANNER_TIMEOUT_NON_ZERO = "{\"cpm\":0.000010,\"cpm_publisher_currency\":0.000010,\"publisher_currency_code\":\"$\",\"content_source\":\"csm\",\"ad_type\":\"banner\",\"buyer_member_id\":123,\"creative_id\":44863345,\"media_type_id\":1,\"media_subtype_id\":1,\"client_initiated_ad_counting\":false,\"csm\":{\"banner\":{\"content\":\"%s\",\"width\":10,\"height\":10},\"timeout_ms\":200,\"handler\":[{\"param\":\"%s\",\"height\":\"%d\",\"width\":\"%d\",\"id\":\"%s\",\"type\":\"%s\",\"class\":\"%s\"},{\"param\":\"#{PARAM}\",\"height\":\"50\",\"width\":\"320\",\"id\":\"163441140754789_163441480754755\",\"type\":\"ios\",\"class\":\"DummyIOSClass\"}],\"trackers\":[{\"impression_urls\":[\"%s\"],\"video_events\":{}}],\"request_url\":\"%s\",\"response_url\":\"%s\"}}";
    public static final String SSM_BANNER = "{\"cpm\":0.000010,\"cpm_publisher_currency\":0.000010,\"publisher_currency_code\":\"$\",\"content_source\":\"ssm\",\"ad_type\":\"banner\",\"buyer_member_id\":123,\"creative_id\":44863345,\"media_type_id\":1,\"media_subtype_id\":1,\"client_initiated_ad_counting\":false,\"ssm\":{\"banner\":{\"content\":\"%s\",\"width\":10,\"height\":10},\"timeout_ms\":500,\"handler\":[{\"url\":\"%s\"}],\"trackers\":[{\"impression_urls\":[\"%s\"],\"video_events\":{}}],\"request_url\":\"%s\",\"response_url\":\"%s\"}}";
    public static final String SSM_BANNER_TIMEOUT_ZERO = "{\"cpm\":0.000010,\"cpm_publisher_currency\":0.000010,\"publisher_currency_code\":\"$\",\"content_source\":\"ssm\",\"ad_type\":\"banner\",\"buyer_member_id\":123,\"creative_id\":44863345,\"media_type_id\":1,\"media_subtype_id\":1,\"client_initiated_ad_counting\":false,\"ssm\":{\"banner\":{\"content\":\"%s\",\"width\":10,\"height\":10},\"timeout_ms\":0,\"handler\":[{\"url\":\"%s\"}],\"trackers\":[{\"impression_urls\":[\"%s\"],\"video_events\":{}}],\"request_url\":\"%s\",\"response_url\":\"%s\"}}";
    public static final String SSM_BANNER_TIMEOUT_NON_ZERO = "{\"cpm\":0.000010,\"cpm_publisher_currency\":0.000010,\"publisher_currency_code\":\"$\",\"content_source\":\"ssm\",\"ad_type\":\"banner\",\"buyer_member_id\":123,\"creative_id\":44863345,\"media_type_id\":1,\"media_subtype_id\":1,\"client_initiated_ad_counting\":false,\"ssm\":{\"banner\":{\"content\":\"%s\",\"width\":10,\"height\":10},\"timeout_ms\":200,\"handler\":[{\"url\":\"%s\"}],\"trackers\":[{\"impression_urls\":[\"%s\"],\"video_events\":{}}],\"request_url\":\"%s\",\"response_url\":\"%s\"}}";
    public static final String RTB_NATIVE = "{\"cpm\":0.000010,\"cpm_publisher_currency\":0.000010,\"publisher_currency_code\":\"$\",\"content_source\":\"rtb\",\"ad_type\":\"native\",\"buyer_member_id\":958,\"creative_id\":47772560,\"media_type_id\":12,\"media_subtype_id\":65,\"client_initiated_ad_counting\":true,\"viewability\":{\"config\":\"%s\"},\"rtb\":{\"native\":%s}}";
    public static final String RTB_NATIVE_RENDERER = "{\"cpm\":0.000010,\"cpm_publisher_currency\":0.000010,\"publisher_currency_code\":\"$\",\"content_source\":\"rtb\",\"ad_type\":\"native\",\"buyer_member_id\":958,\"creative_id\":47772560,\"media_type_id\":12,\"media_subtype_id\":65,\"renderer_url\": \"http://dcdn.adnxs.com/renderer-content/59929529-1dfd-49c3-a19d-f863befc96d7\", \"renderer_id\": 88, \"client_initiated_ad_counting\":true,\"viewability\":{\"config\":\"%s\"},\"rtb\":{\"native\":%s}}";
    public static final String CSM_NATIVE = "{\"cpm\":0.000010,\"cpm_publisher_currency\":0.000010,\"publisher_currency_code\":\"$\",\"content_source\":\"csm\",\"ad_type\":\"native\",\"buyer_member_id\":958,\"creative_id\":44863492,\"media_type_id\":12,\"media_subtype_id\":65,\"client_initiated_ad_counting\":true,\"viewability\":{\"config\":\"%s\"},\"csm\": {\"timeout_ms\":500,\"handler\": [{\"type\": \"android\",\"class\": \"%s\",\"param\": \"%s\",\"id\": \"%s\"},{\"type\": \"ios\",\"class\": \"DummyIOSClass\",\"param\": \"#{PARAM}\",\"id\": \"210827375150_10154672419150151\"}],\"trackers\":[{\"impression_urls\":[\"%s\"],\"video_events\":{}}],\"request_url\": \"%s\",\"response_url\": \"%s\"}}";
    public static final String CSM_NATIVE_TIMEOUT_ZERO = "{\"cpm\":0.000010,\"cpm_publisher_currency\":0.000010,\"publisher_currency_code\":\"$\",\"content_source\":\"csm\",\"ad_type\":\"native\",\"buyer_member_id\":958,\"creative_id\":44863492,\"media_type_id\":12,\"media_subtype_id\":65,\"client_initiated_ad_counting\":true,\"viewability\":{\"config\":\"%s\"},\"csm\": {\"timeout_ms\":0,\"handler\": [{\"type\": \"android\",\"class\": \"%s\",\"param\": \"%s\",\"id\": \"%s\"},{\"type\": \"ios\",\"class\": \"DummyIOSClass\",\"param\": \"#{PARAM}\",\"id\": \"210827375150_10154672419150151\"}],\"trackers\":[{\"impression_urls\":[\"%s\"],\"video_events\":{}}],\"request_url\": \"%s\",\"response_url\": \"%s\"}}";
    public static final String CSM_NATIVE_TIMEOUT_NON_ZERO = "{\"cpm\":0.000010,\"cpm_publisher_currency\":0.000010,\"publisher_currency_code\":\"$\",\"content_source\":\"csm\",\"ad_type\":\"native\",\"buyer_member_id\":958,\"creative_id\":44863492,\"media_type_id\":12,\"media_subtype_id\":65,\"client_initiated_ad_counting\":true,\"viewability\":{\"config\":\"%s\"},\"csm\": {\"timeout_ms\":200,\"handler\": [{\"type\": \"android\",\"class\": \"%s\",\"param\": \"%s\",\"id\": \"%s\"},{\"type\": \"ios\",\"class\": \"DummyIOSClass\",\"param\": \"#{PARAM}\",\"id\": \"210827375150_10154672419150151\"}],\"trackers\":[{\"impression_urls\":[\"%s\"],\"video_events\":{}}],\"request_url\": \"%s\",\"response_url\": \"%s\"}}";
    public static final String NO_BID = "{\"cpm\":0.000010,\"cpm_publisher_currency\":0.000010,\"publisher_currency_code\":\"$\",\"version\":\"3.0.0\",\"tags\":[{\"tag_id\":123456789,\"auction_id\":\"3552547938089377051000000\",\"nobid\":true,\"ad_profile_id\":2707239}]}";
    public static final String NO_TAGS = "{\"error\": \"unknown\"}";
    public static final String RTB_VIDEO = "{\"cpm\":0.000010,\"cpm_publisher_currency\":0.000010,\"publisher_currency_code\":\"$\",\"content_source\":\"rtb\",\"ad_type\":\"video\",\"notify_url\":\"%s\",\"buyer_member_id\":123,\"creative_id\":6332753,\"media_type_id\":4,\"media_subtype_id\":64,\"client_initiated_ad_counting\":true,\"rtb\":{\"video\":{\"content\":\"%s\",\"duration_ms\":100}}}";
    public static final String CSR_NATIVE = "{\"cpm\":0.000010,\"cpm_publisher_currency\":0.000010,\"publisher_currency_code\":\"$\",\"version\":\"3.0.0\",\"tags\":[{\"tag_id\":16268678,\"auction_id\":\"4050477843877235823\",\"nobid\":false,\"no_ad_url\":\"https://nym1-mobile.adnxs.com/it\",\"timeout_ms\":0,\"ad_profile_id\":1266762,\"rtb_video_fallback\":false,\"ads\":[{\"content_source\":\"csr\",\"ad_type\":\"native\",\"buyer_member_id\":10094,\"creative_id\":163940558,\"media_type_id\":12,\"media_subtype_id\":65,\"brand_category_id\":17,\"client_initiated_ad_counting\":true,\"viewability\":{\"config\":\"<script></script>\"},\"csr\":{\"timeout_ms\":500,\"handler\":[{\"type\":\"android\",\"class\":\"%s\",\"payload\":\"{\\\"placement_id\\\":\\\"333673923704415_469697383435401\\\"}\",\"id\":\"333673923704415_469697383435401\"},{\"type\":\"ios\",\"class\":\"ANAdAdapterCSRNativeBannerFacebook\",\"payload\":\"test param\",\"id\":\"333673923704415_469697383435401\"}],\"trackers\":[{\"impression_urls\":[\"https://nym1-mobile.adnxs.com/it\"],\"video_events\":{}}],\"request_url\":\"https://nym1-mobile.adnxs.com/mediation/v2/log_req\",\"response_url\":\"https://nym1-mobile.adnxs.com/mediation/v2/log_resp\"}}]}]}";

    public static String mediationNoFillThenCSRSuccessfull() {
        return "{\"version\":\"3.0.0\",\"tags\":[{\"tag_id\":16268678,\"auction_id\":\"4050477843877235823\",\"nobid\":false,\"no_ad_url\":\"https://nym1-mobile.adnxs.com/it\",\"timeout_ms\":0,\"ad_profile_id\":1266762,\"rtb_video_fallback\":false,\"ads\":[{\"content_source\":\"csm\",\"ad_type\":\"native\",\"buyer_member_id\":10094,\"creative_id\":163940558,\"media_type_id\":12,\"media_subtype_id\":65,\"brand_category_id\":17,\"client_initiated_ad_counting\":true,\"viewability\":{\"config\":\"<script></script>\"},\"csm\":{\"timeout_ms\":500,\"handler\":[{\"type\":\"android\",\"class\":\"com.appnexus.opensdk.testviews.MediatedNativeNoFill\",\"param\":\"test param\",\"id\":\"2038077109846299_2317914228529251\"},{\"type\":\"ios\",\"class\":\"ANAdAdapterNativeFacebook\",\"param\":\"test param\",\"id\":\"2038077109846299_2317914228529251\"}],\"trackers\":[{\"impression_urls\":[\"https://nym1-mobile.adnxs.com/it\"],\"video_events\":{}}],\"request_url\":\"https://nym1-mobile.adnxs.com/mediation/v2/log_req\",\"response_url\":\"https://nym1-mobile.adnxs.com/mediation/v2/log_resp\"}},{\"content_source\":\"csr\",\"ad_type\":\"native\",\"buyer_member_id\":10094,\"creative_id\":163940558,\"media_type_id\":12,\"media_subtype_id\":65,\"brand_category_id\":17,\"client_initiated_ad_counting\":true,\"viewability\":{\"config\":\"<script></script>\"},\"csr\":{\"timeout_ms\":500,\"handler\":[{\"type\":\"android\",\"class\":\"com.appnexus.opensdk.testviews.CSRNativeSuccessful\",\"payload\":\"{\\\"placement_id\\\":\\\"333673923704415_469697383435401\\\"}\",\"id\":\"333673923704415_469697383435401\"},{\"type\":\"ios\",\"class\":\"ANAdAdapterCSRNativeBannerFacebook\",\"payload\":\"test param\",\"id\":\"333673923704415_469697383435401\"}],\"trackers\":[{\"impression_urls\":[\"https://nym1-mobile.adnxs.com/it\"],\"video_events\":{}}],\"request_url\":\"https://nym1-mobile.adnxs.com/mediation/v2/log_req\",\"response_url\":\"https://nym1-mobile.adnxs.com/mediation/v2/log_resp\"}}]}]}";
    }

    public static String csrNoFillThenMediationSuccessfull() {
        return "{\"version\":\"3.0.0\",\"tags\":[{\"tag_id\":16268678,\"auction_id\":\"4050477843877235823\",\"nobid\":false,\"no_ad_url\":\"https://nym1-mobile.adnxs.com/it\",\"timeout_ms\":0,\"ad_profile_id\":1266762,\"rtb_video_fallback\":false,\"ads\":[{\"content_source\":\"csr\",\"ad_type\":\"native\",\"buyer_member_id\":10094,\"creative_id\":163940558,\"media_type_id\":12,\"media_subtype_id\":65,\"brand_category_id\":17,\"client_initiated_ad_counting\":true,\"viewability\":{\"config\":\"<script></script>\"},\"csr\":{\"timeout_ms\":500,\"handler\":[{\"type\":\"android\",\"class\":\"com.appnexus.opensdk.testviews.CSRNativeNoFill\",\"payload\":\"{\\\"placement_id\\\":\\\"333673923704415_469697383435401\\\"}\",\"id\":\"333673923704415_469697383435401\"},{\"type\":\"ios\",\"class\":\"ANAdAdapterCSRNativeBannerFacebook\",\"payload\":\"test param\",\"id\":\"333673923704415_469697383435401\"}],\"trackers\":[{\"impression_urls\":[\"https://nym1-mobile.adnxs.com/it\"],\"video_events\":{}}],\"request_url\":\"https://nym1-mobile.adnxs.com/mediation/v2/log_req\",\"response_url\":\"https://nym1-mobile.adnxs.com/mediation/v2/log_resp\"}},{\"content_source\":\"csm\",\"ad_type\":\"native\",\"buyer_member_id\":10094,\"creative_id\":163940558,\"media_type_id\":12,\"media_subtype_id\":65,\"brand_category_id\":17,\"client_initiated_ad_counting\":true,\"viewability\":{\"config\":\"<script></script>\"},\"csm\":{\"timeout_ms\":500,\"handler\":[{\"type\":\"android\",\"class\":\"com.appnexus.opensdk.testviews.MediatedNativeSuccessful\",\"param\":\"test param\",\"id\":\"2038077109846299_2317914228529251\"},{\"type\":\"ios\",\"class\":\"ANAdAdapterNativeFacebook\",\"param\":\"test param\",\"id\":\"2038077109846299_2317914228529251\"}],\"trackers\":[{\"impression_urls\":[\"https://nym1-mobile.adnxs.com/it\"],\"video_events\":{}}],\"request_url\":\"https://nym1-mobile.adnxs.com/mediation/v2/log_req\",\"response_url\":\"https://nym1-mobile.adnxs.com/mediation/v2/log_resp\"}}]}]}";
    }

    public static String csrNativeNofill() {
        String className = createClassName("CSRNativeNoFill");
        return String.format(CSR_NATIVE, className);
    }

    public static String csrNativeSuccessful() {
        String className = createClassName("CSRNativeSuccessful");
        return String.format(CSR_NATIVE, className);
    }

    public static String csrNativeSuccesfulWithMockTrackers(String impression, String click, String requestUrl, String responseUrl) {
        String template = "{\"version\":\"3.0.0\",\"tags\":[{\"tag_id\":16268678,\"auction_id\":\"4050477843877235823\",\"nobid\":false,\"no_ad_url\":\"https://nym1-mobile.adnxs.com/it\",\"timeout_ms\":0,\"ad_profile_id\":1266762,\"rtb_video_fallback\":false,\"ads\":[{\"content_source\":\"csr\",\"ad_type\":\"native\",\"buyer_member_id\":10094,\"creative_id\":163940558,\"media_type_id\":12,\"media_subtype_id\":65,\"brand_category_id\":17,\"client_initiated_ad_counting\":true,\"viewability\":{\"config\":\"<script></script>\"},\"csr\":{\"timeout_ms\":500,\"handler\":[{\"type\":\"android\",\"class\":\"%s\",\"payload\":\"{\\\"placement_id\\\":\\\"333673923704415_469697383435401\\\"}\",\"id\":\"333673923704415_469697383435401\"},{\"type\":\"ios\",\"class\":\"ANAdAdapterCSRNativeBannerFacebook\",\"payload\":\"test param\",\"id\":\"333673923704415_469697383435401\"}],\"trackers\":[{\"impression_urls\":[\"%s\"],\"video_events\":{},\"click_urls\":[\"%s\"]}],\"request_url\":\"%s\",\"response_url\":\"%s\"}}]}]}";
        return String.format(template, createClassName("CSRNativeSuccessful"), impression, click, requestUrl, responseUrl);
    }

    public static String blank() {
        return "";
    }

    public static String noResponse() {
        return "{\"version\":\"3.0.0\",\"tags\":[{\"tag_id\":123456,\"auction_id\":\"1234567890\",\"nobid\":true,\"ad_profile_id\":98765}]}";
    }

    /**
     * Returns a RTB HTML Banner UT Response
     */
    public static String banner() {
        String bannerContent = String.format(DUMMY_BANNER_CONTENT, "Test Banner Content");
        return templateBannerRTBAdsResponse(bannerContent, 320, 50, IMPRESSION_URL);
    }

    /**
     * Returns a RTB HTML Banner UT Response
     */
    public static String banner_() {
        String bannerContent = String.format(DUMMY_BANNER_CONTENT, "Test Banner Content");
        return templateBannerRTBAdsResponse_(bannerContent, 320, 50, IMPRESSION_URL);
    }

    public static String blankBanner() {
        return templateBannerRTBAdsResponse("", 320, 50, IMPRESSION_URL);
    }

    public static String invalidBanner() {
        return templateBannerRTBAdsResponse("Error", 320, 50, IMPRESSION_URL);
    }

    public static String mraidBanner(String name) {
        return mraidBanner(name, 320, 50);
    }

    public static String mraidBanner(String name, int width, int height) {
        String mraidContent = String.format(MRAID_CONTENT, name);
        return templateBannerRTBAdsResponse(mraidContent, width, height, IMPRESSION_URL);
    }


    public static String mediatedSuccessfulBanner() {
        return templateSingleCSMAdResponseBannerInterstitial(createClassName("MediatedBannerSuccessful"), RESPONSE_URL);
    }

    public static String mediatedSuccessfulBannerTimeout() {
        return templateSingleCSMAdResponseBannerInterstitialTimeout(createClassName("MediatedBannerSuccessful"), RESPONSE_URL);
    }

    public static String mediatedSuccessfulBannerTimeoutNonZero() {
        return templateSingleCSMAdResponseNativeTimeoutNonZero(createClassName("MediatedBannerSuccessful"), RESPONSE_URL);
    }


    public static String mediatedNoRequestBanner() {
        return templateSingleCSMAdResponseBannerInterstitial(createClassName("MediatedBannerNoRequest"), RESPONSE_URL);
    }

    public static String mediatedOutOfMemoryBanner() {
        return templateSingleCSMAdResponseBannerInterstitial(createClassName("MediatedBannerOOM"), RESPONSE_URL);
    }

    public static String mediatedNoFillBanner() {
        return templateSingleCSMAdResponseBannerInterstitial(createClassName("MediatedBannerNoFillView"), RESPONSE_URL);
    }

    public static String mediatedSuccessfulInterstitial() {
        return templateSingleCSMAdResponseBannerInterstitial(createClassName("MediatedInterstitialSuccessful"), RESPONSE_URL);
    }
    public static String mediatedSuccessfulInterstitialTimeout() {
        return templateSingleCSMAdResponseBannerInterstitialTimeout(createClassName("MediatedInterstitialSuccessful"), RESPONSE_URL);
    }

    public static String mediatedSuccessfulInterstitialTimeoutNonZero() {
        return templateSingleCSMAdResponseBannerInterstitialTimeoutNonZero(createClassName("MediatedInterstitialSuccessful"), RESPONSE_URL);
    }

    public static String mediatedNoRequestInterstitial() {
        return templateSingleCSMAdResponseBannerInterstitial(createClassName("MediatedInterstitialNoRequest"), RESPONSE_URL);
    }

    public static String mediatedOutOfMemoryInterstitial() {
        return templateSingleCSMAdResponseBannerInterstitial(createClassName("MediatedInterstitialOOM"), RESPONSE_URL);
    }

    public static String mediatedNoFillInterstitial() {
        return templateSingleCSMAdResponseBannerInterstitial(createClassName("MediatedInterstitialNoFillView"), RESPONSE_URL);
    }

    public static String mediatedFakeClassBannerInterstitial() {
        return templateSingleCSMAdResponseBannerInterstitial(createClassName("FakeClass"), RESPONSE_URL);
    }

    public static String mediatedDummyClassBannerInterstitial() {
        return templateSingleCSMAdResponseBannerInterstitial(createClassName("DummyClass"), RESPONSE_URL);
    }


    public static String mediatedSuccessfulNative() {
        return templateSingleCSMAdResponseNative(createClassName("MediatedNativeSuccessful"), RESPONSE_URL);
    }


    public static String mediatedSuccessfulNativeTimeout() {
        return templateSingleCSMAdResponseNativeTimeout(createClassName("MediatedNativeSuccessful"), RESPONSE_URL);
    }


    public static String mediatedSuccessfulNativeTimeoutNonZero() {
        return templateSingleCSMAdResponseNativeTimeoutNonZero(createClassName("MediatedNativeSuccessful"), RESPONSE_URL);
    }


    public static String mediatedNoRequestNative() {
        return templateSingleCSMAdResponseNative(createClassName("MediatedNativeNoRequest"), RESPONSE_URL);
    }

    public static String mediatedOutOfMemoryNative() {
        return templateSingleCSMAdResponseNative(createClassName("MediatedNativeOOM"), RESPONSE_URL);
    }

    public static String mediatedNoFillNative() {
        return templateSingleCSMAdResponseNative(createClassName("MediatedNativeNoFill"), RESPONSE_URL);
    }


    public static String mediatedFakeClass_Native() {
        return templateSingleCSMAdResponseNative(createClassName("FakeClass"), RESPONSE_URL);
    }

    public static String mediatedDummyClass_Native() {
        return templateSingleCSMAdResponseNative(createClassName("DummyClass"), RESPONSE_URL);
    }

    public static String mediatedSSMBanner() {
        return templateSingleSSMAdResponse();
    }


    public static String mediatedSSMBannerWithTimeoutZero() {
        return templateSingleSSMAdResponseWithTimeoutZero();
    }

    public static String mediatedNoSSMBanner() {
        return templateNoURLSSMResponse();
    }

    public static String noFillCSM_RTBBanner() {
        //Create a CSM - Ad
        String csmAd = templateSingleCSMAdResponseBannerInterstitial(createClassName("MediatedBannerNoFillView"), 320, 50, IMPRESSION_URL, REQUEST_URL, RESPONSE_URL, "", "", "android");

        // Create a RTB Banner Ad
        String bannerContent = String.format(DUMMY_BANNER_CONTENT, "Test Banner Content");
        String bannerAd = singleRTBBanner(bannerContent, 320, 50, IMPRESSION_URL);

        ArrayList<String> adsArray = new ArrayList<String>(2);
        adsArray.add(csmAd);
        adsArray.add(bannerAd);

        //Return a WaterFall response
        return templateMediatedWaterFallResponses(adsArray.toArray(new String[adsArray.size()]));
    }

    public static String noFillCSMBanner() {
        //Create a CSM - Ad
        String csmAd = templateSingleCSMAdResponseBannerInterstitial(createClassName("MediatedBannerNoFillView"), 320, 50, IMPRESSION_URL, REQUEST_URL, RESPONSE_URL, "", "", "android");

        ArrayList<String> adsArray = new ArrayList<String>(1);
        adsArray.add(csmAd);

        //Return a WaterFall response
        return templateMediatedWaterFallResponses(adsArray.toArray(new String[adsArray.size()]));
    }

    public static String noFillCSM_RTBInterstitial() {
        //Create a CSM - Ad
        String csmAd = templateSingleCSMAdResponseBannerInterstitial(createClassName("MediatedInterstitialNoFillView"), 320, 480, IMPRESSION_URL, REQUEST_URL, RESPONSE_URL, "", "", "android");

        // Create a RTB Banner Ad
        String bannerConetent = String.format(DUMMY_BANNER_CONTENT, "Test Banner Content");
        String bannerAd = singleRTBBanner(bannerConetent, 320, 480, IMPRESSION_URL);

        ArrayList<String> adsArray = new ArrayList<String>(2);
        adsArray.add(csmAd);
        adsArray.add(bannerAd);

        //Return a WaterFall response
        return templateMediatedWaterFallResponses(adsArray.toArray(new String[adsArray.size()]));
    }


    public static String noFillCSM_RTBNative() {
        //Create a CSM - Ad
        String csmAd = templateSingleCSMAdResponseNative(createClassName("MediatedNativeNoFill"), "", "", IMPRESSION_URL, REQUEST_URL, RESPONSE_URL);

        // Create a RTB Banner Ad
        String nativeResponse = templateNativeResponse("test title", "description", "desc2", "sponsored", "cta",
                "5", "http://path_to_icon.com", 100, 100, "http://path_to_main.com",
                300, 200, "http://www.appnexus.com", "http://ib.adnxs.com/click...",
                "http://ib.adnxs.com/it...", 111796070);
        System.out.println(nativeResponse + "\n");
        String nativeRTB = templateRTBNativeAdResponse(nativeResponse);

        ArrayList<String> adsArray = new ArrayList<String>(2);
        adsArray.add(csmAd);
        adsArray.add(nativeRTB);

        //Return a WaterFall response
        return templateMediatedWaterFallResponses(adsArray.toArray(new String[adsArray.size()]));
    }

    private static String createClassName(String className) {
        return String.format(CLASSNAME, className);
    }

    private static String resultCB(int code) {
        return String.format(RESPONSE_URL + "&reason=%d", code);
    }


    public static String waterfall_CSM_Banner_Interstitial(String[] classNames, String[] responseURLS) {
        if (classNames.length != responseURLS.length) {
            System.err.println("different numbers of class names and resultCBs");
            return "";
        }

        ArrayList<String> adsArray = new ArrayList<String>(classNames.length);

        for (int i = 0; i < classNames.length; i++) {
            String singleCSMAd = templateSingleCSMAdResponseBannerInterstitial(createClassName(classNames[i]), 320, 50, IMPRESSION_URL, REQUEST_URL, responseURLS[i], "", "", "android");
            adsArray.add(singleCSMAd);
        }

        return templateMediatedWaterFallResponses(adsArray.toArray(new String[adsArray.size()]));
    }


    // Just take in count here since SSM waterfall can be controlled by altering the response for  handler URL response.
    public static String waterfall_SSM_Banner_Interstitial(int count) {

        ArrayList<String> adsArray = new ArrayList<String>(count);

        for (int i = 0; i < count; i++) {
            String ssmAdTag = String.format(SSM_BANNER, DUMMY_BANNER_CONTENT, SSM_URL, IMPRESSION_URL, REQUEST_URL, RESPONSE_URL);
            adsArray.add(ssmAdTag);
        }

        return templateMediatedWaterFallResponses(adsArray.toArray(new String[adsArray.size()]));
    }


    public static String waterfall_CSM_Native(String[] classNames, String[] responseURLS) {
        if (classNames.length != responseURLS.length) {
            System.err.println("different numbers of class names and resultCBs");
            return "";
        }

        ArrayList<String> adsArray = new ArrayList<String>(classNames.length);

        for (int i = 0; i < classNames.length; i++) {
            String singleCSMAd = templateSingleCSMAdResponseNative(createClassName(classNames[i]), "", "", IMPRESSION_URL, REQUEST_URL, responseURLS[i]);
            adsArray.add(singleCSMAd);
        }

        return templateMediatedWaterFallResponses(adsArray.toArray(new String[adsArray.size()]));
    }


    private static String templateMediatedWaterFallResponses(String[] adsArray) {
        StringBuilder sb = new StringBuilder();
        for (String handler : adsArray) {
            sb.append(handler).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return templateMediatedAdResponse(sb.toString());
    }


    public static String callbacks(int testNumber) {
        return templateSingleCSMAdResponseBannerInterstitial(createClassName("MediatedBannerCallbacksTestView"), RESPONSE_URL, String.valueOf(testNumber));
    }

    public static String anNativeTripleLift() {
        return anNative().replace("\"buyer_member_id\":958", "\"buyer_member_id\":11217");
    }

    public static String anNativeMSAN() {
        return anNative().replace("\"buyer_member_id\":958", "\"buyer_member_id\":12085");
    }

    public static String anNativeIndex() {
        return anNative().replace("\"buyer_member_id\":958", "\"buyer_member_id\":9642");
    }

    public static String anNativeInMobi() {
        return anNative().replace("\"buyer_member_id\":958", "\"buyer_member_id\":12317");
    }

    public static String anNative() {
        String nativeResponse = templateNativeResponse("test title", "test description", "additional test description", "sponsored", "cta",
                "5", "http://path_to_icon.com", 100, 150, "http://path_to_main.com",
                300, 200, "http://www.appnexus.com", "http://ib.adnxs.com/click...",
                "http://ib.adnxs.com/it...", 111796070);
        System.out.println(nativeResponse + "\n");
        String nativeRTB = templateRTBNativeAdResponse(nativeResponse);
        System.out.println(nativeRTB + "\n");
        String ads = String.format(ADS, nativeRTB);
        System.out.println(ads + "\n");
        return templateResponse(NO_BID_FALSE, NO_AD_URL, ads);
    }

    public static String anOMIDNativeRTB() {
        String nativeResponse = templateNativeResponse("test title", "test description", "additional test description", "sponsored", "cta",
                "5", "http://path_to_icon.com", 100, 150, "http://path_to_main.com",
                300, 200, "http://www.appnexus.com", "http://ib.adnxs.com/click...",
                "http://ib.adnxs.com/it...", 111796070);
        System.out.println(nativeResponse + "\n");
        String nativeRTB = templateRTBNativeAdResponse(nativeResponse);
        System.out.println(nativeRTB + "\n");
        String ads = String.format(ADS, nativeRTB);
        System.out.println(ads + "\n");
        return templateResponse(NO_BID_FALSE, NO_AD_URL, ads);
    }

    public static String anNativeVideo() {
        String nativeResponse = templateNativeVideoResponse("test title", "test description", "additional test description",
                "sponsored", "cta", 5, "http://path_to_icon.com", 100, 150, "http://path_to_main.com",
                300, 200, "http://www.appnexus.com", "http://ib.adnxs.com/fallback",
                "http://ib.adnxs.com/click...1", "http://ib.adnxs.com/click...2", "http://ib.adnxs.com/click...3",
                "http://ib.adnxs.com/it...1", "http://ib.adnxs.com/it...2", "http://ib.adnxs.com/it...3", "http://ib.adnxs.com/it...4",
                "http://ib.adnxs.com/jt...", 123456789, "http://ib.adnxs.com/display...", 10, 1000, 5, 4, 987654321,
                "AppNexus Address", "<VAST>content</VAST>", "http://ib.adnxs.com/privacy...");
        System.out.println(nativeResponse + "\n");
        String nativeRTB = templateRTBNativeAdResponse(nativeResponse);
        System.out.println(nativeRTB + "\n");
        String ads = String.format(ADS, nativeRTB);
        System.out.println(ads + "\n");
        return templateResponse(NO_BID_FALSE, NO_AD_URL, ads);
    }

    public static String anNativeRenderer() {
        String nativeResponse = templateNativeResponse("test title", "test description", "additional test description", "sponsored", "cta",
                "5", "http://path_to_icon.com", 100, 150, "http://path_to_main.com",
                300, 200, "http://www.appnexus.com", "http://ib.adnxs.com/click...",
                "http://ib.adnxs.com/it...", 111796070);
        System.out.println(nativeResponse + "\n");
        String nativeRTB = String.format(RTB_NATIVE_RENDERER, RTB_NATIVE_RENDERER_VIEWABILITY_CONFIG, nativeResponse);
        System.out.println(nativeRTB + "\n");
        String ads = String.format(ADS, nativeRTB);
        System.out.println(ads + "\n");
        return templateResponse(NO_BID_FALSE, NO_AD_URL, ads);
    }

    public static String anNativeWithoutImages() {
        String nativeResponse = templateNativeResponse("test title", "test description", "additional test description", "sponsored", "cta",
                "5", "", 0, 0, "http://path_to_main.com",
                300, 200, "http://www.appnexus.com", "http://ib.adnxs.com/click...",
                "http://ib.adnxs.com/it...", 111796070);
        System.out.println(nativeResponse + "\n");
        String nativeRTB = templateRTBNativeAdResponse(nativeResponse);
        System.out.println(nativeRTB + "\n");
        String ads = String.format(ADS, nativeRTB);
        System.out.println(ads + "\n");
        return templateResponse(NO_BID_FALSE, NO_AD_URL, ads);
    }

    public static String nativeResponseWithImageAndIconUrl() {
        String nativeResponse = templateNativeResponse("native", "test title", "test description", "additional test description", "full text", "newsfeed",
                ICON_URL, templateNativeMainMedia(IMAGE_URL, 300, 200, "http://path_to_main2.com", 50, 50, "http://path_to_main3.com", 250, 250),
                "install", "\"http://ib.adnxs.com/click...\"", "\"http://ib.adnxs.com/it...\"", templateNativeRating(4f, 5f), "http://www.appnexus.com", "http://www.google.com", "test sponsored by", "{\"key\":\"value\"}"
        );
        System.out.println(nativeResponse + "\n");
        String nativeRTB = templateRTBNativeAdResponse(nativeResponse);
        System.out.println(nativeRTB + "\n");
        String ads = String.format(ADS, nativeRTB);
        System.out.println(ads + "\n");
        return templateResponse(NO_BID_FALSE, NO_AD_URL, ads);
    }


    // templates

    private static String templateBannerRTBAdsResponse(String content, int width, int height, String impressionURL) {
        String rtbBanner = singleRTBBanner(content, width, height, impressionURL);
        String ads = String.format(ADS, rtbBanner);
        return templateResponse(NO_BID_FALSE, NO_AD_URL, ads);
    }

    private static String templateBannerRTBAdsResponse_(String content, int width, int height, String impressionURL) {
        String rtbBanner = singleRTBBanner_(content, width, height, impressionURL);
        String ads = String.format(ADS, rtbBanner);
        return templateResponse_(NO_BID_FALSE, NO_AD_URL, ads);
    }


    private static String templateRTBNativeAdResponse(String nativeResponse) {
        return String.format(RTB_NATIVE, RTB_NATIVE_VIEWABILITY_CONFIG, nativeResponse);
    }


    private static String singleRTBBanner(String content, int width, int height, String impressionURL) {
        return (String.format(RTB_BANNER, content, width, height, impressionURL));
    }

    private static String singleRTBBanner_(String content, int width, int height, String impressionURL) {
        return (String.format(RTB_BANNER_, content, width, height, impressionURL));
    }

    private static String singleRTBVideo(String content) {
        return (String.format(RTB_VIDEO, NOTIFY_URL, content));
    }


    private static String templateResponse(String noBid, String noAdURL, String ads) {
        System.out.println(String.format(RESPONSE, noBid, noAdURL, ads));
        return String.format(RESPONSE, noBid, noAdURL, ads);
    }

    private static String templateResponse_(String noBid, String noAdURL, String ads) {
        System.out.println(String.format(RESPONSE_, noBid, noAdURL, ads));
        return String.format(RESPONSE_, noBid, noAdURL, ads);
    }

    private static String templateSingleCSMAdResponseBannerInterstitial(String className, String response_url) {
        String csmBanner = templateSingleCSMAdResponseBannerInterstitial(className, 320, 50, IMPRESSION_URL, REQUEST_URL, response_url, "", "", "android");
        return templateMediatedAdResponse(csmBanner);
    }

    private static String templateSingleCSMAdResponseBannerInterstitialTimeout(String className, String response_url) {
        String csmBanner = templateSingleCSMAdResponseBannerInterstitialTimeout(className, 320, 50, IMPRESSION_URL, REQUEST_URL, response_url, "", "", "android");
        return templateMediatedAdResponse(csmBanner);
    }


    private static String templateSingleCSMAdResponseBannerInterstitialTimeoutNonZero(String className, String response_url) {
        String csmBanner = templateSingleCSMAdResponseBannerInterstitialTimeoutNonZero(className, 320, 50, IMPRESSION_URL, REQUEST_URL, response_url, "", "", "android");
        return templateMediatedAdResponse(csmBanner);
    }


    public static String templateSingleCSMAdResponseBannerInterstitial(String className, String response_url, String id) {
        String csmBanner = templateSingleCSMAdResponseBannerInterstitial(className, 320, 50, IMPRESSION_URL, REQUEST_URL, response_url, "", id, "android");
        return templateMediatedAdResponse(csmBanner);
    }

    private static String templateSingleCSMAdResponseNative(String className, String response_url) {
        String csmNative = templateSingleCSMAdResponseNative(className, "abc", "1234", IMPRESSION_URL, REQUEST_URL, response_url);
        return templateMediatedAdResponse(csmNative);
    }


    private static String templateSingleCSMAdResponseNativeTimeout(String className, String response_url) {
        String csmNative = templateSingleCSMAdResponseNativeTimeout(className, "abc", "1234", IMPRESSION_URL, REQUEST_URL, response_url);
        return templateMediatedAdResponse(csmNative);
    }

    private static String templateSingleCSMAdResponseNativeTimeoutNonZero(String className, String response_url) {
        String csmNative = templateSingleCSMAdResponseNativeTimeoutNonZero(className, "abc", "1234", IMPRESSION_URL, REQUEST_URL, response_url);
        return templateMediatedAdResponse(csmNative);
    }


    private static String templateSingleSSMAdResponseWithTimeoutZero() {
        String ssmAdTag = String.format(SSM_BANNER_TIMEOUT_ZERO, DUMMY_BANNER_CONTENT, SSM_URL, IMPRESSION_URL, REQUEST_URL, RESPONSE_URL);
        String ads = String.format(ADS, ssmAdTag);
        return String.format(RESPONSE, NO_BID_FALSE, NO_AD_URL, ads);
    }

    private static String templateSingleSSMAdResponse() {
        String ssmAdTag = String.format(SSM_BANNER, DUMMY_BANNER_CONTENT, SSM_URL, IMPRESSION_URL, REQUEST_URL, RESPONSE_URL);
        String ads = String.format(ADS, ssmAdTag);
        return String.format(RESPONSE, NO_BID_FALSE, NO_AD_URL, ads);
    }

    private static String templateNoURLSSMResponse() {
        String ssmAdTag = String.format(SSM_BANNER, DUMMY_BANNER_CONTENT, SSM_NO_URL, IMPRESSION_URL, REQUEST_URL, RESPONSE_URL);
        String ads = String.format(ADS, ssmAdTag);
        return String.format(RESPONSE, NO_BID_FALSE, NO_AD_URL, ads);
    }


    private static String templateSingleCSMAdResponseBannerInterstitial(String className, int width, int height, String impressionURL, String request_url, String response_url, String params, String id, String type) {
        return String.format(CSM_BANNER, DUMMY_BANNER_CONTENT, params, height, width, id, type, className, impressionURL, request_url, response_url);
    }

    private static String templateSingleCSMAdResponseBannerInterstitialTimeout(String className, int width, int height, String impressionURL, String request_url, String response_url, String params, String id, String type) {
        return String.format(CSM_BANNER_TIMEOUT_ZERO, DUMMY_BANNER_CONTENT, params, height, width, id, type, className, impressionURL, request_url, response_url);
    }

    private static String templateSingleCSMAdResponseBannerInterstitialTimeoutNonZero(String className, int width, int height, String impressionURL, String request_url, String response_url, String params, String id, String type) {
        return String.format(CSM_BANNER_TIMEOUT_NON_ZERO, DUMMY_BANNER_CONTENT, params, height, width, id, type, className, impressionURL, request_url, response_url);
    }
    private static String templateSingleCSMAdResponseNative(String className, String params, String id, String impression_url, String request_url, String response_url) {
        Clog.d("Native Ad", String.format(CSM_NATIVE, CSM_NATIVE_VIEWABILITY_CONFIG, className, params, id, impression_url, request_url, response_url));
        return String.format(CSM_NATIVE, CSM_NATIVE_VIEWABILITY_CONFIG, className, params, id, impression_url, request_url, response_url);
    }


    private static String templateSingleCSMAdResponseNativeTimeout(String className, String params, String id, String impression_url, String request_url, String response_url) {
        return String.format(CSM_NATIVE_TIMEOUT_ZERO, CSM_NATIVE_VIEWABILITY_CONFIG, className, params, id, impression_url, request_url, response_url);
    }



    private static String templateSingleCSMAdResponseNativeTimeoutNonZero(String className, String params, String id, String impression_url, String request_url, String response_url) {
        return String.format(CSM_NATIVE_TIMEOUT_NON_ZERO, CSM_NATIVE_VIEWABILITY_CONFIG, className, params, id, impression_url, request_url, response_url);
    }


    private static String templateMediatedAdResponse(String adsArray) {
        String ads = String.format(ADS, adsArray);
        return templateResponse(NO_BID_FALSE, NO_AD_URL, ads);
    }


    private static String templateNativeResponse(String type, String title, String description, String additionalDescription, String full_text, String context,
                                                 String icon, String main_media, String cta, String click_trackers,
                                                 String imp_trackers, String rating, String click_url,
                                                 String click_fallback_url, String sponsored_by, String custom) {
        return String.format(AN_NATIVE_RESPONSE, type, title, description, additionalDescription, full_text, context, icon, main_media, cta, click_trackers, imp_trackers, rating, click_url, click_fallback_url, sponsored_by, custom);


    }


    private static String templateNativeResponse(String title, String description, String additionalDescription, String sponsored, String cta,
                                                 String rating, String icon_url, int icon_width, int icon_height, String main_img_url,
                                                 int main_img_width, int main_img_height, String link_url, String click_trackers,
                                                 String imp_trackers, int id) {
        return String.format(AN_NATIVE_RESPONSE, title, description, sponsored, cta, rating, icon_url, icon_width, icon_height, main_img_url, main_img_width, main_img_height, link_url, click_trackers, imp_trackers, id, additionalDescription);


    }

    private static String templateNativeVideoResponse(String title, String description, String additionalDescription, String sponsored, String cta,
                                                      int rating, String icon_url, int icon_width, int icon_height, String main_img_url,
                                                      int main_img_width, int main_img_height, String link_url, String fallback_url, String click_trackers1,
                                                      String click_trackers2, String click_trackers3, String imp_tracker1, String impression_tracker2,
                                                      String impression_tracker3, String impression_tracker4, String javascript_trackers, int id,
                                                      String displayurl, int likes, int downloads, int price, int saleprice, int phone, String address,
                                                      String video_content, String privacy_link) {
        return String.format(AN_NATIVE_VIDEO_RESPONSE, title, description, sponsored, cta, rating, icon_url, icon_width, icon_height, main_img_url,
                main_img_width, main_img_height, link_url, fallback_url, click_trackers1, click_trackers2, click_trackers3, imp_tracker1,
                impression_tracker2, impression_tracker3, impression_tracker4, javascript_trackers, id, displayurl, likes, downloads, price,
                saleprice, phone, address, additionalDescription, video_content, privacy_link);


    }

    private static String templateNativeMainMedia(String url, int width, int height, String url2, int width2, int height2, String url3, int width3, int height3) {
        return String.format(NATIVE_MAIN_MEDIA, url, width, height, url2, width2, height2, url3, width3, height3);
    }

    private static String templateNativeRating(float value, float scale) {
        return String.format(NATIVE_RATING, value, scale);
    }


    private static final String DUMMY_VIDEO_CONTENT = "<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\" standalone=\\\"yes\\\"?>\n" +
            "<VAST version=\\\"2.0\\\">\n" +
            "    <Ad id=\\\"85346399\\\">\n" +
            "        <InLine>\n" +
            "            <AdSystem>adnxs</AdSystem>\n" +
            "            <AdTitle>\n" +
            "                <![CDATA[KungfuPandaWithAudio.mp4]]>\n" +
            "            </AdTitle>\n" +
            "            <Error>\n" +
            "                <![CDATA[http://nym1-ib.adnxs.com/vast_track/v2?info=YwAAAAMArgAFAQm5cDFaAAAAABE-5Ol-nFN6bhm4cDFaAAAAACDfkNkoKAAwvgc4vgdAyOc9SIzyyQFQwsv8BVgBYgItLWgBcAF4AIABAIgBAJABwAKYAeADoAEAqAHfkNko&event_type=4&error_code=[ERRORCODE]]]>\n" +
            "            </Error>\n" +
            "            <Impression id=\\\"adnxs\\\">\n" +
            "                <![CDATA[http://nym1-ib.adnxs.com/it?e=wqT_3QKPBmwPAwAAAwDWAAUBCLjhxdEFEL7Ip_fH85S9bhj_EQEwASotCXsUrkfheoQ_EREJBBkABQEI8D8hERIAKREJoDDCy_wFOL4HQL4HSAJQ35DZKFjLu05gAGiRQHi-uASAAQGKAQNVU0SSBQbwUJgBAaABAagBAbABALgBA8ABBMgBAtABANgBAOABAPABAIoCO3VmKCdhJywgMTc5Nzg2NSwgMTUxMzE4OTU2MCk7dWYoJ3InLCA4NTM0NjM5OTYeAPCckgL5ASEtem5KV0FpNDE3WUpFTi1RMlNnWUFDREx1MDR3QURnQVFBUkl2Z2RRd3N2OEJWZ0FZUF9fX184UGFBQndBWGdCZ0FFQmlBRUJrQUVCbUFFQm9BRUJxQUVEc0FFQXVRR1I3d3J3NFhxRVA4RUJrZThLOE9GNmhEX0pBUzZmM3llWTdld18yUUVBQUFBQUFBRHdQLUFCQVBVQgUPKEpnQ0FLQUNBTFVDBRAETDAJCPBMTUFDQWNnQ0FkQUNBZGdDQWVBQ0FPZ0NBUGdDQUlBREFaQURBSmdEQWFnRHVOZTJDYm9EQ1U1WlRUSTZNelU1TlEuLpoCLSFpQWxYcWc2_ADoeTd0T0lBUW9BRG9KVGxsTk1qb3pOVGsx2ALoB-ACx9MB6gI0aXR1bmVzLmFwcGxlLmNvbS91cy9hcHABBPCWbmV4dXMtc2RrLWFwcC9pZDczNjg2OTgzM4ADAYgDAZADAJgDF6ADAaoDAMADkBzIAwDYA_mjeuADAOgDAvgDAIAEAJIEBi91dC92MpgEAKIECjEwLjEuMTIuNjaoBACyBA4IABABGMACIOADMAA4ArgEAMAEAMgEANIECU5ZTTI6MzU5NdoEAggA4AQA8ATfkNkoggUJNxGGHIgFAZgFAKAFUcAY_wHABQDJBUWwGADwP9IFCQkJDEQAANgFAeAFAfAFAfoFBAgAEAA.&s=bc4092d95e0f3a01c7510f1047a61f57a3f64548&referrer=itunes.apple.com%2Fus%2Fapp%2Fappnexus-sdk-app%2Fid736869833]]>\n" +
            "            </Impression>\n" +
            "            <Creatives>\n" +
            "                <Creative id=\\\"49362\\\" AdID=\\\"85346399\\\">\n" +
            "                    <Linear>\n" +
            "                        <Duration>00:02:25</Duration>\n" +
            "                        <TrackingEvents>\n" +
            "                            <Tracking event=\\\"start\\\">\n" +
            "                                <![CDATA[http://nym1-ib.adnxs.com/vast_track/v2?info=YwAAAAMArgAFAQm5cDFaAAAAABE-5Ol-nFN6bhm4cDFaAAAAACDfkNkoKAAwvgc4vgdAyOc9SIzyyQFQwsv8BVgBYgItLWgBcAF4AIABAIgBAJABwAKYAeADoAEAqAHfkNko&event_type=2]]>\n" +
            "                            </Tracking>\n" +
            "                            <Tracking event=\\\"skip\\\">\n" +
            "                                <![CDATA[http://nym1-ib.adnxs.com/vast_track/v2?info=YwAAAAMArgAFAQm5cDFaAAAAABE-5Ol-nFN6bhm4cDFaAAAAACDfkNkoKAAwvgc4vgdAyOc9SIzyyQFQwsv8BVgBYgItLWgBcAF4AIABAIgBAJABwAKYAeADoAEAqAHfkNko&event_type=3]]>\n" +
            "                            </Tracking>\n" +
            "                            <Tracking event=\\\"firstQuartile\\\">\n" +
            "                                <![CDATA[http://nym1-ib.adnxs.com/vast_track/v2?info=YwAAAAMArgAFAQm5cDFaAAAAABE-5Ol-nFN6bhm4cDFaAAAAACDfkNkoKAAwvgc4vgdAyOc9SIzyyQFQwsv8BVgBYgItLWgBcAF4AIABAIgBAJABwAKYAeADoAEAqAHfkNko&event_type=5]]>\n" +
            "                            </Tracking>\n" +
            "                            <Tracking event=\\\"midpoint\\\">\n" +
            "                                <![CDATA[http://nym1-ib.adnxs.com/vast_track/v2?info=YwAAAAMArgAFAQm5cDFaAAAAABE-5Ol-nFN6bhm4cDFaAAAAACDfkNkoKAAwvgc4vgdAyOc9SIzyyQFQwsv8BVgBYgItLWgBcAF4AIABAIgBAJABwAKYAeADoAEAqAHfkNko&event_type=6]]>\n" +
            "                            </Tracking>\n" +
            "                            <Tracking event=\\\"thirdQuartile\\\">\n" +
            "                                <![CDATA[http://nym1-ib.adnxs.com/vast_track/v2?info=YwAAAAMArgAFAQm5cDFaAAAAABE-5Ol-nFN6bhm4cDFaAAAAACDfkNkoKAAwvgc4vgdAyOc9SIzyyQFQwsv8BVgBYgItLWgBcAF4AIABAIgBAJABwAKYAeADoAEAqAHfkNko&event_type=7]]>\n" +
            "                            </Tracking>\n" +
            "                            <Tracking event=\\\"complete\\\">\n" +
            "                                <![CDATA[http://nym1-ib.adnxs.com/vast_track/v2?info=YwAAAAMArgAFAQm5cDFaAAAAABE-5Ol-nFN6bhm4cDFaAAAAACDfkNkoKAAwvgc4vgdAyOc9SIzyyQFQwsv8BVgBYgItLWgBcAF4AIABAIgBAJABwAKYAeADoAEAqAHfkNko&event_type=8]]>\n" +
            "                            </Tracking>\n" +
            "                        </TrackingEvents>\n" +
            "                        <VideoClicks>\n" +
            "                            <ClickThrough>\n" +
            "                                <![CDATA[https://www.appnexus.com]]>\n" +
            "                            </ClickThrough>\n" +
            "                            <ClickTracking id=\\\"adnxs\\\">\n" +
            "                                <![CDATA[http://nym1-ib.adnxs.com/click?exSuR-F6hD97FK5H4XqEPwAAAAAAAPA_exSuR-F6hD97FK5H4XqEPz7k6X6cU3pu__________-4cDFaAAAAAMIlvwC-AwAAvgMAAAIAAABfSBYFy50TAAAAAABVU0QAVVNEAAEAAQARIAAAAAABAwQCAAAAAAAAPyUuWQAAAAA./cnd=%21iAlXqgi417YJEN-Q2SgYy7tOIAQoADoJTllNMjozNTk1/bn=72766/referrer=itunes.apple.com%2Fus%2Fapp%2Fappnexus-sdk-app%2Fid736869833/]]>\n" +
            "                            </ClickTracking>\n" +
            "                        </VideoClicks>\n" +
            "                        <MediaFiles>\n" +
            "                            <MediaFile id=\\\"612525\\\" delivery=\\\"progressive\\\" type=\\\"video/x-flv\\\" width=\\\"768\\\" height=\\\"432\\\" scalable=\\\"true\\\" bitrate=\\\"500\\\" maintainAspectRatio=\\\"true\\\">\n" +
            "                                <![CDATA[http://vcdn.adnxs.com/p/creative-video/ef/a6/d0/bb/efa6d0bb-8c19-44a8-b140-4b0bc2e02087/efa6d0bb-8c19-44a8-b140-4b0bc2e02087_768_432_500k.flv]]>\n" +
            "                            </MediaFile>\n" +
            "                            <MediaFile id=\\\"612526\\\" delivery=\\\"progressive\\\" type=\\\"video/mp4\\\" width=\\\"768\\\" height=\\\"432\\\" scalable=\\\"true\\\" bitrate=\\\"1700\\\" maintainAspectRatio=\\\"true\\\">\n" +
            "                                <![CDATA[http://vcdn.adnxs.com/p/creative-video/ef/a6/d0/bb/efa6d0bb-8c19-44a8-b140-4b0bc2e02087/efa6d0bb-8c19-44a8-b140-4b0bc2e02087_768_432_1700k.mp4]]>\n" +
            "                            </MediaFile>\n" +
            "                            <MediaFile id=\\\"612527\\\" delivery=\\\"progressive\\\" type=\\\"video/mp4\\\" width=\\\"768\\\" height=\\\"432\\\" scalable=\\\"true\\\" bitrate=\\\"500\\\" maintainAspectRatio=\\\"true\\\">\n" +
            "                                <![CDATA[http://vcdn.adnxs.com/p/creative-video/ef/a6/d0/bb/efa6d0bb-8c19-44a8-b140-4b0bc2e02087/efa6d0bb-8c19-44a8-b140-4b0bc2e02087_768_432_500k.mp4]]>\n" +
            "                            </MediaFile>\n" +
            "                            <MediaFile id=\\\"612528\\\" delivery=\\\"progressive\\\" type=\\\"video/webm\\\" width=\\\"1280\\\" height=\\\"720\\\" scalable=\\\"true\\\" bitrate=\\\"2000\\\" maintainAspectRatio=\\\"true\\\">\n" +
            "                                <![CDATA[http://vcdn.adnxs.com/p/creative-video/ef/a6/d0/bb/efa6d0bb-8c19-44a8-b140-4b0bc2e02087/efa6d0bb-8c19-44a8-b140-4b0bc2e02087_1280_720_2000k.webm]]>\n" +
            "                            </MediaFile>\n" +
            "                            <MediaFile id=\\\"612529\\\" delivery=\\\"progressive\\\" type=\\\"video/x-flv\\\" width=\\\"768\\\" height=\\\"432\\\" scalable=\\\"true\\\" bitrate=\\\"1100\\\" maintainAspectRatio=\\\"true\\\">\n" +
            "                                <![CDATA[http://vcdn.adnxs.com/p/creative-video/ef/a6/d0/bb/efa6d0bb-8c19-44a8-b140-4b0bc2e02087/efa6d0bb-8c19-44a8-b140-4b0bc2e02087_768_432_1100k.flv]]>\n" +
            "                            </MediaFile>\n" +
            "                            <MediaFile id=\\\"612530\\\" delivery=\\\"progressive\\\" type=\\\"video/webm\\\" width=\\\"1280\\\" height=\\\"720\\\" scalable=\\\"true\\\" bitrate=\\\"600\\\" maintainAspectRatio=\\\"true\\\">\n" +
            "                                <![CDATA[http://vcdn.adnxs.com/p/creative-video/ef/a6/d0/bb/efa6d0bb-8c19-44a8-b140-4b0bc2e02087/efa6d0bb-8c19-44a8-b140-4b0bc2e02087_1280_720_600k.webm]]>\n" +
            "                            </MediaFile>\n" +
            "                            <MediaFile id=\\\"612531\\\" delivery=\\\"progressive\\\" type=\\\"video/webm\\\" width=\\\"768\\\" height=\\\"432\\\" scalable=\\\"true\\\" bitrate=\\\"500\\\" maintainAspectRatio=\\\"true\\\">\n" +
            "                                <![CDATA[http://vcdn.adnxs.com/p/creative-video/ef/a6/d0/bb/efa6d0bb-8c19-44a8-b140-4b0bc2e02087/efa6d0bb-8c19-44a8-b140-4b0bc2e02087_768_432_500k.webm]]>\n" +
            "                            </MediaFile>\n" +
            "                            <MediaFile id=\\\"612532\\\" delivery=\\\"progressive\\\" type=\\\"video/mp4\\\" width=\\\"1280\\\" height=\\\"720\\\" scalable=\\\"true\\\" bitrate=\\\"500\\\" maintainAspectRatio=\\\"true\\\">\n" +
            "                                <![CDATA[http://vcdn.adnxs.com/p/creative-video/ef/a6/d0/bb/efa6d0bb-8c19-44a8-b140-4b0bc2e02087/efa6d0bb-8c19-44a8-b140-4b0bc2e02087_1280_720_500k.mp4]]>\n" +
            "                            </MediaFile>\n" +
            "                            <MediaFile id=\\\"612533\\\" delivery=\\\"progressive\\\" type=\\\"video/mp4\\\" width=\\\"768\\\" height=\\\"432\\\" scalable=\\\"true\\\" bitrate=\\\"1100\\\" maintainAspectRatio=\\\"true\\\">\n" +
            "                                <![CDATA[http://vcdn.adnxs.com/p/creative-video/ef/a6/d0/bb/efa6d0bb-8c19-44a8-b140-4b0bc2e02087/efa6d0bb-8c19-44a8-b140-4b0bc2e02087_768_432_1100k.mp4]]>\n" +
            "                            </MediaFile>\n" +
            "                            <MediaFile id=\\\"612534\\\" delivery=\\\"progressive\\\" type=\\\"video/webm\\\" width=\\\"768\\\" height=\\\"432\\\" scalable=\\\"true\\\" bitrate=\\\"1500\\\" maintainAspectRatio=\\\"true\\\">\n" +
            "                                <![CDATA[http://vcdn.adnxs.com/p/creative-video/ef/a6/d0/bb/efa6d0bb-8c19-44a8-b140-4b0bc2e02087/efa6d0bb-8c19-44a8-b140-4b0bc2e02087_768_432_1500k.webm]]>\n" +
            "                            </MediaFile>\n" +
            "                            <MediaFile id=\\\"612535\\\" delivery=\\\"progressive\\\" type=\\\"video/mp4\\\" width=\\\"1280\\\" height=\\\"720\\\" scalable=\\\"true\\\" bitrate=\\\"1100\\\" maintainAspectRatio=\\\"true\\\">\n" +
            "                                <![CDATA[http://vcdn.adnxs.com/p/creative-video/ef/a6/d0/bb/efa6d0bb-8c19-44a8-b140-4b0bc2e02087/efa6d0bb-8c19-44a8-b140-4b0bc2e02087_1280_720_1100k.mp4]]>\n" +
            "                            </MediaFile>\n" +
            "                        </MediaFiles>\n" +
            "                    </Linear>\n" +
            "                </Creative>\n" +
            "            </Creatives>\n" +
            "        </InLine>\n" +
            "    </Ad>\n" +
            "</VAST>";


    public static String rtbVASTVideo() {
        return templateVideoRTBAdsResponse(DUMMY_VIDEO_CONTENT);
    }


    private static String templateVideoRTBAdsResponse(String content) {
        String rtbVideo = singleRTBVideo(content);
        String ads = String.format(ADS, rtbVideo);
        return templateResponse(NO_BID_FALSE, NO_AD_URL, ads);
    }


}
