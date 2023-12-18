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
package appnexus.com.trackertestapp;

import com.appnexus.opensdk.ut.UTConstants;
import com.appnexus.opensdk.utils.Clog;

import java.util.ArrayList;


public class TestResponsesUT {

    public static final long DELAY = 5000;
    public static final long DELAY_IP = 2000;
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
    private static final String RTB_NATIVE_VIEWABILITY_CONFIG = "<script type=\\\"text/javascript\\\" async=\\\"true\\\" src=\\\"https://acdn.adnxs.com/mobile/omsdk/validation-verification-scripts-fortesting/omsdk-js-1.4.9/Validation-Script/omid-validation-verification-script-v1.js#v;vk=dummyVendor;tv=cet=0;cecb=\\\"></script>";
    private static final String RTB_NATIVE_RENDERER_VIEWABILITY_CONFIG = "<script type=\\\"text/javascript\\\" async=\\\"true\\\" src=\\\"https://acdn.adnxs.com/mobile/omsdk/validation-verification-scripts-fortesting/omsdk-js-1.4.9/Validation-Script/omid-validation-verification-script-v1.js#v;vk=dummyVendorRenderer;tv=cet=0;cecb=\\\"></script>";
    private static final String CSM_NATIVE_VIEWABILITY_CONFIG = "<script type=\\\"text/javascript\\\" async=\\\"true\\\" src=\\\"https://acdn.adnxs.com/mobile/omsdk/validation-verification-scripts-fortesting/omsdk-js-1.4.9/Validation-Script/omid-validation-verification-script-v1.js#v;vk=dummyVendorCSM;tv=cet=0;cecb=\\\"></script>";
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
    public static final String RTB_BANNER = "{\"content_source\":\"rtb\",\"ad_type\":\"banner\",\"buyer_member_id\":123,\"creative_id\":6332753,\"media_type_id\":1,\"media_subtype_id\":1,\"client_initiated_ad_counting\":true,\"rtb\":{\"banner\":{\"content\":\"%s\",\"width\":%d,\"height\":%d},\"trackers\":[{\"impression_urls\":[\"%s\"],\"video_events\":{}}]}}";
    public static final String RTB_BANNER_ = "{\"content_source\":\"rtb\",\"ad_type\":\"banner\",\"buyer_member_id\":456,\"creative_id\":1234567,\"media_type_id\":1,\"media_subtype_id\":1,\"client_initiated_ad_counting\":true,\"rtb\":{\"banner\":{\"content\":\"%s\",\"width\":%d,\"height\":%d},\"trackers\":[{\"impression_urls\":[\"%s\"],\"video_events\":{}}]}}";
    public static final String CSM_BANNER = "{\"content_source\":\"csm\",\"ad_type\":\"banner\",\"buyer_member_id\":123,\"creative_id\":44863345,\"media_type_id\":1,\"media_subtype_id\":1,\"client_initiated_ad_counting\":false,\"csm\":{\"banner\":{\"content\":\"%s\",\"width\":10,\"height\":10},\"timeout_ms\":500,\"handler\":[{\"param\":\"%s\",\"height\":\"%d\",\"width\":\"%d\",\"id\":\"%s\",\"type\":\"%s\",\"class\":\"%s\"},{\"param\":\"#{PARAM}\",\"height\":\"50\",\"width\":\"320\",\"id\":\"163441140754789_163441480754755\",\"type\":\"ios\",\"class\":\"DummyIOSClass\"}],\"trackers\":[{\"impression_urls\":[\"%s\"],\"video_events\":{}}],\"request_url\":\"%s\",\"response_url\":\"%s\"}}";
    public static final String CSM_BANNER_TIMEOUT_ZERO = "{\"content_source\":\"csm\",\"ad_type\":\"banner\",\"buyer_member_id\":123,\"creative_id\":44863345,\"media_type_id\":1,\"media_subtype_id\":1,\"client_initiated_ad_counting\":false,\"csm\":{\"banner\":{\"content\":\"%s\",\"width\":10,\"height\":10},\"timeout_ms\":0,\"handler\":[{\"param\":\"%s\",\"height\":\"%d\",\"width\":\"%d\",\"id\":\"%s\",\"type\":\"%s\",\"class\":\"%s\"},{\"param\":\"#{PARAM}\",\"height\":\"50\",\"width\":\"320\",\"id\":\"163441140754789_163441480754755\",\"type\":\"ios\",\"class\":\"DummyIOSClass\"}],\"trackers\":[{\"impression_urls\":[\"%s\"],\"video_events\":{}}],\"request_url\":\"%s\",\"response_url\":\"%s\"}}";
    public static final String CSM_BANNER_TIMEOUT_NON_ZERO = "{\"content_source\":\"csm\",\"ad_type\":\"banner\",\"buyer_member_id\":123,\"creative_id\":44863345,\"media_type_id\":1,\"media_subtype_id\":1,\"client_initiated_ad_counting\":false,\"csm\":{\"banner\":{\"content\":\"%s\",\"width\":10,\"height\":10},\"timeout_ms\":200,\"handler\":[{\"param\":\"%s\",\"height\":\"%d\",\"width\":\"%d\",\"id\":\"%s\",\"type\":\"%s\",\"class\":\"%s\"},{\"param\":\"#{PARAM}\",\"height\":\"50\",\"width\":\"320\",\"id\":\"163441140754789_163441480754755\",\"type\":\"ios\",\"class\":\"DummyIOSClass\"}],\"trackers\":[{\"impression_urls\":[\"%s\"],\"video_events\":{}}],\"request_url\":\"%s\",\"response_url\":\"%s\"}}";
    public static final String SSM_BANNER = "{\"content_source\":\"ssm\",\"ad_type\":\"banner\",\"buyer_member_id\":123,\"creative_id\":44863345,\"media_type_id\":1,\"media_subtype_id\":1,\"client_initiated_ad_counting\":false,\"ssm\":{\"banner\":{\"content\":\"%s\",\"width\":10,\"height\":10},\"timeout_ms\":500,\"handler\":[{\"url\":\"%s\"}],\"trackers\":[{\"impression_urls\":[\"%s\"],\"video_events\":{}}],\"request_url\":\"%s\",\"response_url\":\"%s\"}}";
    public static final String SSM_BANNER_TIMEOUT_ZERO = "{\"content_source\":\"ssm\",\"ad_type\":\"banner\",\"buyer_member_id\":123,\"creative_id\":44863345,\"media_type_id\":1,\"media_subtype_id\":1,\"client_initiated_ad_counting\":false,\"ssm\":{\"banner\":{\"content\":\"%s\",\"width\":10,\"height\":10},\"timeout_ms\":0,\"handler\":[{\"url\":\"%s\"}],\"trackers\":[{\"impression_urls\":[\"%s\"],\"video_events\":{}}],\"request_url\":\"%s\",\"response_url\":\"%s\"}}";
    public static final String SSM_BANNER_TIMEOUT_NON_ZERO = "{\"content_source\":\"ssm\",\"ad_type\":\"banner\",\"buyer_member_id\":123,\"creative_id\":44863345,\"media_type_id\":1,\"media_subtype_id\":1,\"client_initiated_ad_counting\":false,\"ssm\":{\"banner\":{\"content\":\"%s\",\"width\":10,\"height\":10},\"timeout_ms\":200,\"handler\":[{\"url\":\"%s\"}],\"trackers\":[{\"impression_urls\":[\"%s\"],\"video_events\":{}}],\"request_url\":\"%s\",\"response_url\":\"%s\"}}";
    public static final String RTB_NATIVE = "{\"content_source\":\"rtb\",\"ad_type\":\"native\",\"buyer_member_id\":958,\"creative_id\":47772560,\"media_type_id\":12,\"media_subtype_id\":65,\"client_initiated_ad_counting\":true,\"viewability\":{\"config\":\"%s\"},\"rtb\":{\"native\":%s}}";
    public static final String RTB_NATIVE_RENDERER = "{\"content_source\":\"rtb\",\"ad_type\":\"native\",\"buyer_member_id\":958,\"creative_id\":47772560,\"media_type_id\":12,\"media_subtype_id\":65,\"renderer_url\": \"http://dcdn.adnxs.com/renderer-content/59929529-1dfd-49c3-a19d-f863befc96d7\", \"renderer_id\": 88, \"client_initiated_ad_counting\":true,\"viewability\":{\"config\":\"%s\"},\"rtb\":{\"native\":%s}}";
    public static final String CSM_NATIVE = "{\"content_source\":\"csm\",\"ad_type\":\"native\",\"buyer_member_id\":958,\"creative_id\":44863492,\"media_type_id\":12,\"media_subtype_id\":65,\"client_initiated_ad_counting\":true,\"viewability\":{\"config\":\"%s\"},\"csm\": {\"timeout_ms\":500,\"handler\": [{\"type\": \"android\",\"class\": \"%s\",\"param\": \"%s\",\"id\": \"%s\"},{\"type\": \"ios\",\"class\": \"DummyIOSClass\",\"param\": \"#{PARAM}\",\"id\": \"210827375150_10154672419150151\"}],\"trackers\":[{\"impression_urls\":[\"%s\"],\"video_events\":{}}],\"request_url\": \"%s\",\"response_url\": \"%s\"}}";
    public static final String CSM_NATIVE_TIMEOUT_ZERO = "{\"content_source\":\"csm\",\"ad_type\":\"native\",\"buyer_member_id\":958,\"creative_id\":44863492,\"media_type_id\":12,\"media_subtype_id\":65,\"client_initiated_ad_counting\":true,\"viewability\":{\"config\":\"%s\"},\"csm\": {\"timeout_ms\":0,\"handler\": [{\"type\": \"android\",\"class\": \"%s\",\"param\": \"%s\",\"id\": \"%s\"},{\"type\": \"ios\",\"class\": \"DummyIOSClass\",\"param\": \"#{PARAM}\",\"id\": \"210827375150_10154672419150151\"}],\"trackers\":[{\"impression_urls\":[\"%s\"],\"video_events\":{}}],\"request_url\": \"%s\",\"response_url\": \"%s\"}}";
    public static final String CSM_NATIVE_TIMEOUT_NON_ZERO = "{\"content_source\":\"csm\",\"ad_type\":\"native\",\"buyer_member_id\":958,\"creative_id\":44863492,\"media_type_id\":12,\"media_subtype_id\":65,\"client_initiated_ad_counting\":true,\"viewability\":{\"config\":\"%s\"},\"csm\": {\"timeout_ms\":200,\"handler\": [{\"type\": \"android\",\"class\": \"%s\",\"param\": \"%s\",\"id\": \"%s\"},{\"type\": \"ios\",\"class\": \"DummyIOSClass\",\"param\": \"#{PARAM}\",\"id\": \"210827375150_10154672419150151\"}],\"trackers\":[{\"impression_urls\":[\"%s\"],\"video_events\":{}}],\"request_url\": \"%s\",\"response_url\": \"%s\"}}";
    public static final String NO_BID = "{\"version\":\"3.0.0\",\"tags\":[{\"tag_id\":123456789,\"auction_id\":\"3552547938089377051000000\",\"nobid\":true,\"ad_profile_id\":2707239}]}";
    public static final String RTB_VIDEO = "{\"content_source\":\"rtb\",\"ad_type\":\"video\",\"notify_url\":\"%s\",\"buyer_member_id\":123,\"creative_id\":6332753,\"media_type_id\":4,\"media_subtype_id\":64,\"client_initiated_ad_counting\":true,\"rtb\":{\"video\":{\"content\":\"%s\",\"duration_ms\":100}}}";
    public static final String CSR_NATIVE = "{\"version\":\"3.0.0\",\"tags\":[{\"tag_id\":16268678,\"auction_id\":\"4050477843877235823\",\"nobid\":false,\"no_ad_url\":\"https://nym1-mobile.adnxs.com/it\",\"timeout_ms\":0,\"ad_profile_id\":1266762,\"rtb_video_fallback\":false,\"ads\":[{\"content_source\":\"csr\",\"ad_type\":\"native\",\"buyer_member_id\":10094,\"creative_id\":163940558,\"media_type_id\":12,\"media_subtype_id\":65,\"brand_category_id\":17,\"client_initiated_ad_counting\":true,\"viewability\":{\"config\":\"<script></script>\"},\"csr\":{\"timeout_ms\":500,\"handler\":[{\"type\":\"android\",\"class\":\"%s\",\"payload\":\"{\\\"placement_id\\\":\\\"333673923704415_469697383435401\\\"}\",\"id\":\"333673923704415_469697383435401\"},{\"type\":\"ios\",\"class\":\"ANAdAdapterCSRNativeBannerFacebook\",\"payload\":\"test param\",\"id\":\"333673923704415_469697383435401\"}],\"trackers\":[{\"impression_urls\":[\"https://nym1-mobile.adnxs.com/it\"],\"video_events\":{}}],\"request_url\":\"https://nym1-mobile.adnxs.com/mediation/v2/log_req\",\"response_url\":\"https://nym1-mobile.adnxs.com/mediation/v2/log_resp\"}}]}]}";


    //Tracker Test App Ad Responses
    public static final String BANNER_NATIVE_RENDERER = "{\n" +
                "  \"version\": \"3.0.0\",\n" +
                "  \"tags\": [\n" +
                "    {\n" +
                "      \"uuid\": \"2B8364F3-2F85-4B25-B56A-6107394844E5\",\n" +
                "      \"tag_id\": 20331545,\n" +
                "      \"auction_id\": \"8634877977927625253\",\n" +
                "      \"nobid\": false,\n" +
                "      \"no_ad_url\": \""+UTConstants.REQUEST_BASE_URL_UT+"/it?an_audit=0&referrer=itunes.apple.com%2Fus%2Fapp%2Fappnexus-sdk-app%2Fid736869833&e=wqT_3QLhCaDhBAAAAwDWAAUBCOPs6IEGEKXU4MnX-M_qdxjIxcGZj4HNjnMqNgkAAAkCABEJBwgAABkJCQjgPyEJCQgAACkRCQAxCQnwfeA_MJn42Ak47k5A7k5IAFAAWKusamAAaKODhAF4AIABAYoBAJIBA1VTRJgBAaABAagBAbABALgBAcABAMgBAtABANgBAOABAPABAIoCPHVmKCdhJywgMjk1ODQzMSwgMTYxNDQyNzc0Nyk7dWYoJ3InLCAyNDk2Njc3NDksIC4fAPC2kgLhAyFIRkpaVVFpSTI1ME1FS1hCaG5jWUFDQ3JyR293QURnQVFBUkk3azVRbWZqWUNWZ0FZUGdHYUFCd0JuaWtoZ1NBQVFhSUFhU0dCSkFCQUpnQkFLQUJBYWdCQTdBQkFMa0JkYXNOYkpxWnFUX0JBWFdyRFd5YW1ha195UUZsUHBfVmxvSDNQOWtCQUFBQUFBQUE4RF9nQVFEMUFRQUFBQUNZQWdDZ0FnQzFBZ0FBQUFDOUFnASbgRGdBZ0RvQWdENEFnQ0FBd0dZQXdHNkF3bFRTVTR6T2pVeU1qbmdBLWtxaUFRQWtBUUFtQVFCd1FRAT0JAQhNa0UJCQEBFERZQkFEeBWFKEFBQWlBWHRLS2tGAQwBARQ4RC14QlEBCgkBCHdRVQkJAQEATRkoDEFBRFIuKAAAMi4oAPBAT0FGaUNmd0JjZW1vd1A0QmRfSXRBR0NCZ05WVTBTSUJnQ1FCZ0dZQmdDaEJwcVptWm1abWFrX3FBWUJzZ1lrQ1EBbQkBAEUdjABHHQwASR0MOHVBWUuaAokBIThSTE1IdzblASxxNnhxSUFRb0FER2EFaVxabXBQem9KVTBsT016bzFNakk1UU9rcVMRUQxQQV9VEQwMQUFBVx0MAFkdDABhHQwAYx0M8NBlQUEu2AIA4ALKqE3qAjRpdHVuZXMuYXBwbGUuY29tL3VzL2FwcC9hcHBuZXh1cy1zZGstYXBwL2lkNzM2ODY5ODMzgAMAiAMBkAMAmAMXoAMBqgMAwAPgqAHIAwDYA_mjeuADAOgDAvgDAIAEAJIEBi91dC92M5gEAKIEDzEwMy4xMjEuMTE1LjIxMKgEqpkFsgQQCAAQARisAiD6ASgAMAA4ArgEAMAEAMgEANIEDzEwMDk0I1NJTjM6NTIyOdoEAggA4AQB8ASlwYZ3ggUJNxGWIIgFAZgFAKAF_xEBGAHABQDJBQAFARTwP9IFCQkFC3QAAADYBQHgBQHwBQH6BQQIABAAkAYBmAYAuAYAwQYBHzAAAPC_0AbWM9oGFgoQCREZAVwQABgA4AYM8gYCCACABwGIBwCgB0G6Bw8BSEgYACAAMAA46Q1AAMgHxbgF0gcNFXQBOAjaBwYJJyTgB_mjeuoHAggA&s=0f0d3018df2c386c9d1ac8e2086039441e1f0a19\",\n" +
                "      \"timeout_ms\": 0,\n" +
                "      \"ad_profile_id\": 1266762,\n" +
                "      \"rtb_video_fallback\": false,\n" +
                "      \"ads\": [\n" +
                "        {\n" +
                "          \"content_source\": \"rtb\",\n" +
                "          \"ad_type\": \"native\",\n" +
                "          \"buyer_member_id\": 10094,\n" +
                "          \"creative_id\": 249667749,\n" +
                "          \"media_type_id\": 12,\n" +
                "          \"media_subtype_id\": 65,\n" +
                "          \"brand_category_id\": 0,\n" +
                "          \"renderer_url\": \"https://dcdn.adnxs.com/renderer-content/d78931fc-805c-455d-967a-0eb7fe141470\",\n" +
                "          \"renderer_id\": 989,\n" +
                "          \"client_initiated_ad_counting\": true,\n" +
                "          \"viewability\": {\n" +
                "            \"config\": \"<script type=\\\"text/javascript\\\" async=\\\"true\\\" src=\\\"https://cdn.adnxs.com/v/app/202/trk.js#app;vk=appnexus.com-omid;tv=app-native-23hs;dom_id=%native_dom_id%;st=2;d=1x1;vc=iab;vid_ccr=1;tag_id=20331545;cb=https%3A%2F%2Fsin3-ib.adnxs.com%2Fvevent%3Fan_audit%3D0%26referrer%3Ditunes.apple.com%252Fus%252Fapp%252Fappnexus-sdk-app%252Fid736869833%26e%3DwqT_3QLpCcDpBAAAAwDWAAUBCOPs6IEGEKXU4MnX-M_qdxjIxcGZj4HNjnMqNgmamZmZmZmpPxGaAQgQmak_GQAFAQjgPyERGwApEQkAMQUauADgPzCZ-NgJOO5OQO5OSAJQpcGGd1irrGpgAGijg4QBeMW4BYABAYoBA1VTRJIBAQbwVZgBAaABAagBAbABALgBAcABBMgBAtABANgBAOABAPABAIoCPHVmKCdhJywgMjk1ODQzMSwgMTYxNDQyNzc0Nyk7dWYoJ3InLCAyNDk2Njc3NDksIDE2GR_wtpIC4QMhSEZKWlVRaUkyNTBNRUtYQmhuY1lBQ0Nyckdvd0FEZ0FRQVJJN2s1UW1mallDVmdBWVBnR2FBQndCbmlraGdTQUFRYUlBYVNHQkpBQkFKZ0JBS0FCQWFnQkE3QUJBTGtCZGFzTmJKcVpxVF9CQVhXckRXeWFtYWtfeVFGbFBwX1Zsb0gzUDlrQkFBQUFBQUFBOERfZ0FRRDFBUUFBQUFDWUFnQ2dBZ0MxQWdBQUFBQzlBZwEm4ERnQWdEb0FnRDRBZ0NBQXdHWUF3RzZBd2xUU1U0ek9qVXlNam5nQS1rcWlBUUFrQVFBbUFRQndRUQE9CQEITWtFCQkBARREWUJBRHgVhShBQUFpQVh0S0trRgEMAQEUOEQteEJRAQoJAQh3UVUJCQEBAE0ZKAxBQURSLigAADIuKADwQE9BRmlDZndCY2Vtb3dQNEJkX0l0QUdDQmdOVlUwU0lCZ0NRQmdHWUJnQ2hCcHFabVptWm1ha19xQVlCc2dZa0NRAW0JAQBFHYwARx0MAEkdDDh1QVlLmgKJASE4UkxNSHc25QEscTZ4cUlBUW9BREdhBWlcWm1wUHpvSlUwbE9Nem8xTWpJNVFPa3FTEVEMUEFfVREMDEFBQVcdDABZHQwAYR0MAGMdDPDQZUFBLtgCAOACyqhN6gI0aXR1bmVzLmFwcGxlLmNvbS91cy9hcHAvYXBwbmV4dXMtc2RrLWFwcC9pZDczNjg2OTgzM4ADAIgDAZADAJgDF6ADAaoDAMAD4KgByAMA2AP5o3rgAwDoAwL4AwCABACSBAYvdXQvdjOYBACiBA8xMDMuMTIxLjExNS4yMTCoBKqZBbIEEAgAEAEYrAIg-gEoADAAOAK4BADABADIBADSBA8xMDA5NCNTSU4zOjUyMjnaBAIIAeAEAfAEpcGGd4IFCTcRliCIBQGYBQCgBf8RARgBwAUAyQUABQEU8D_SBQkJBQt0AAAA2AUB4AUB8AUB-gUECAAQAJAGAZgGALgGAMEGAR8wAADwP9AG1jPaBhYKEAkRGQFcEAAYAOAGDPIGAggAgAcBiAcAoAdBugcPAUhIGAAgADAAOOkNQADIB8W4BdIHDRV0ATgI2gcGCSck4Af5o3rqBwIIAA..%26s%3D40340ac69fb948357bdaf3b945e841658ce3a6d6;ts=1614427747;cet=0;cecb=\\\"></script>\"\n" +
                "          },\n" +
                "          \"rtb\": {\n" +
                "            \"native\": {\n" +
                "              \"title\": \"This is a test creative\",\n" +
                "              \"desc\": \"This is body of a test creative\",\n" +
                "              \"sponsored\": \"Xandr-Test\",\n" +
                "              \"icon\": {\n" +
                "                \"url\": \"https://dcdn.adnxs.com/shftr/https%253A%252F%252Fcrcdn01.adnxs.com%252Fcreative%252Fp%252F10094%252F2020%252F10%252F9%252F21631506%252F2c29fe67-b1ff-493c-bd63-36f7c41e68a2.png/0/300/300\",\n" +
                "                \"width\": 300,\n" +
                "                \"height\": 300,\n" +
                "                \"prevent_crop\": true\n" +
                "              },\n" +
                "              \"link\": {\n" +
                "                \"url\": \"https://xandr.com\",\n" +
                "                \"click_trackers\": [\n" +
                "                  \""+UTConstants.REQUEST_BASE_URL_UT+"/click?mpmZmZmZqT-amZmZmZmpPwAAAAAAAOA_mpmZmZmZqT-amZmZmZmpPyUqOHnFP9V3yGIw8wg0HXNjNjpgAAAAABk8NgFuJwAAbicAAAIAAACloOEOK5YaAAAAAABVU0QAVVNEAAEAAQCjAQAAAAABAQQCAAAAAMIAxyLCugAAAAA./bcr=AAAAAAAA8D8=/cnd=%218RLMHwiI250MEKXBhncYq6xqIAQoADGamZmZmZmpPzoJU0lOMzo1MjI5QOkqSQAAAAAAAPA_UQAAAAAAAAAAWQAAAAAAAAAAYQAAAAAAAAAAaQAAAAAAAAAAcQAAAAAAAAAAeAA./cca=MTAwOTQjU0lOMzo1MjI5/bn=89157/\"\n" +
                "                ]\n" +
                "              },\n" +
                "              \"impression_trackers\": [\n" +
                "                \""+UTConstants.REQUEST_BASE_URL_UT+"/it?an_audit=0&referrer=itunes.apple.com%2Fus%2Fapp%2Fappnexus-sdk-app%2Fid736869833&e=wqT_3QLpCcDpBAAAAwDWAAUBCOPs6IEGEKXU4MnX-M_qdxjIxcGZj4HNjnMqNgmamZmZmZmpPxGaAQgQmak_GQAFAQjgPyERGwApEQkAMQUauADgPzCZ-NgJOO5OQO5OSAJQpcGGd1irrGpgAGijg4QBeMW4BYABAYoBA1VTRJIBAQbwVZgBAaABAagBAbABALgBAcABBMgBAtABANgBAOABAPABAIoCPHVmKCdhJywgMjk1ODQzMSwgMTYxNDQyNzc0Nyk7dWYoJ3InLCAyNDk2Njc3NDksIDE2GR_wtpIC4QMhSEZKWlVRaUkyNTBNRUtYQmhuY1lBQ0Nyckdvd0FEZ0FRQVJJN2s1UW1mallDVmdBWVBnR2FBQndCbmlraGdTQUFRYUlBYVNHQkpBQkFKZ0JBS0FCQWFnQkE3QUJBTGtCZGFzTmJKcVpxVF9CQVhXckRXeWFtYWtfeVFGbFBwX1Zsb0gzUDlrQkFBQUFBQUFBOERfZ0FRRDFBUUFBQUFDWUFnQ2dBZ0MxQWdBQUFBQzlBZwEm4ERnQWdEb0FnRDRBZ0NBQXdHWUF3RzZBd2xUU1U0ek9qVXlNam5nQS1rcWlBUUFrQVFBbUFRQndRUQE9CQEITWtFCQkBARREWUJBRHgVhShBQUFpQVh0S0trRgEMAQEUOEQteEJRAQoJAQh3UVUJCQEBAE0ZKAxBQURSLigAADIuKADwQE9BRmlDZndCY2Vtb3dQNEJkX0l0QUdDQmdOVlUwU0lCZ0NRQmdHWUJnQ2hCcHFabVptWm1ha19xQVlCc2dZa0NRAW0JAQBFHYwARx0MAEkdDDh1QVlLmgKJASE4UkxNSHc25QEscTZ4cUlBUW9BREdhBWlcWm1wUHpvSlUwbE9Nem8xTWpJNVFPa3FTEVEMUEFfVREMDEFBQVcdDABZHQwAYR0MAGMdDPDQZUFBLtgCAOACyqhN6gI0aXR1bmVzLmFwcGxlLmNvbS91cy9hcHAvYXBwbmV4dXMtc2RrLWFwcC9pZDczNjg2OTgzM4ADAIgDAZADAJgDF6ADAaoDAMAD4KgByAMA2AP5o3rgAwDoAwL4AwCABACSBAYvdXQvdjOYBACiBA8xMDMuMTIxLjExNS4yMTCoBKqZBbIEEAgAEAEYrAIg-gEoADAAOAK4BADABADIBADSBA8xMDA5NCNTSU4zOjUyMjnaBAIIAeAEAfAEpcGGd4IFCTcRliCIBQGYBQCgBf8RARgBwAUAyQUABQEU8D_SBQkJBQt0AAAA2AUB4AUB8AUB-gUECAAQAJAGAZgGALgGAMEGAR8wAADwP9AG1jPaBhYKEAkRGQFcEAAYAOAGDPIGAggAgAcBiAcAoAdBugcPAUhIGAAgADAAOOkNQADIB8W4BdIHDRV0ATgI2gcGCSck4Af5o3rqBwIIAA..&s=40340ac69fb948357bdaf3b945e841658ce3a6d6\"\n" +
                "              ],\n" +
                "              \"id\": 249667749\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

    public static final String BANNER_VIDEO_AD = "{\n" +
            "    \"version\": \"3.0.0\",\n" +
            "    \"tags\": [\n" +
            "        {\n" +
            "            \"tag_id\": 16417701,\n" +
            "            \"auction_id\": \"5531859120537927358\",\n" +
            "            \"nobid\": false,\n" +
            "            \"no_ad_url\": \""+UTConstants.REQUEST_BASE_URL_UT+"/it?an_audit=0&referrer=itunes.apple.com%2Fus%2Fapp%2Fappnexus-sdk-app%2Fid736869833&e=wqT_3QKSCXySBAAAAwDWAAUBCMP36fMFEL79q5n6kcfiTBgAKjYJAA0BABENCAQAGQkJCOA_IQkJCAAAKREJADEJCfCa4D8wpYfqBzi-B0C-B0gAUABYuaxgYABokUB4AIABAYoBAJIBA1VTRJgBAaABAagBAbABALgBA8ABAMgBAtABANgBAOABAPABAIoCWXVmKCdhJywgMjc2NzIwNywgMTU4NTA4NTM3OSk7dWYoJ2knLCAxNTUyMjgxLCAxNTg1MDg1Mzc5KTt1ZigncicsIDE3MjA1OTEyOCwgMTUZPPCfkgKhAyFYRWljcGdpcGw0TVBFUGpUaFZJWUFDQzVyR0F3QURnQVFBUkl2Z2RRcFlmcUIxZ0FZUF9fX184UGFBQndBWGdCZ0FFQmlBRUJrQUVCbUFFQm9BRUJxQUVEc0FFQXVRRXBpNGlEQUFEd1A4RUJLWXVJZ3dBQThEX0pBVGFSbXRSRm1PMF8yUUVBQUFBQUFBRHdQLUFCbWQ5ZTlRRQUUKG1BSUFvQUlBdFFJBRAAdg0ImHdBSUJ5QUlCMEFJQjJBSUI0QUlBNkFJQS1BSUFnQU1CbUFNQnFBTwXYoHVnTUpUbGxOTWpvME16WXg0QU9ISFlBRUFJZ0VBSkFFQUpnRUFjRUVBBWIBAQhESkIBBw0BCDBRUQkKJElBYVFOZ0VBUEUdLCBDSUJZa2lxUVUJJBhBRHdQN0VGDQ0UQUFBREJCDT8BAQB5FSgMQUFBTjIoAABaLigASDRBV2dqUVkumgKJASFHaENFRXc2pQEkdWF4Z0lBUW9BRBGMEER3UHpvMukAEFFJY2RTEX0MUEFfVREMDEFBQVcdDABZHQwAYR0MAGMNDKhnQnBBZUFBLtgC6AfgAsfTAeoCNGl0dW5lcy5hcHBsZS5jb20vdXMvYXBwAQTwvG5leHVzLXNkay1hcHAvaWQ3MzY4Njk4MzOAAwGIAwGQAwCYAxegAwGqAwDAA-CoAcgDANgD-aN64AMA6AMC-AMAgAQAkgQGL3V0L3YzmAQAogQLMTAuNzUuMTEuNDSoBACyBBcIABAEGMACIDIoASgCKAMoBCgFMAA4ArgEAMAEAMgEANIEDTk1OCNOWU0yOjQzNjHaBAIIAOAEAPAE-NOFUvoEEgkAAABAluRCQBEAAADAAppewIIFCTczNgmqIIgFAZgFAKAF_xEBGAHABQDJBQAFARDwP9IFCQFABQFo2AUB4AUB8AUB-gUECAAQAJAGAZgGALgGAMEGBSAsAPC_0AbWAtoGFgoQCREZAVwQABgA4AYE8gYCCACABwGIBwCgB0DIBwA.&s=571a47ebf4c02182eddc8edc156a3b1212484cbf\",\n" +
            "            \"timeout_ms\": 10000,\n" +
            "            \"ad_profile_id\": 27079,\n" +
            "            \"rtb_video_fallback\": false,\n" +
            "            \"ads\": [\n" +
            "                {\n" +
            "                    \"content_source\": \"rtb\",\n" +
            "                    \"ad_type\": \"video\",\n" +
            "                    \"notify_url\": \""+UTConstants.REQUEST_BASE_URL_UT+"/vast_track/v2?info=ZQAAAAMArgAFAQnDe3peAAAAABG-_iqjjxzFTBnDe3peAAAAACD404VSKAAwvgc4vgdAx9FMSPP7jAJQpYfqB1gBYgItLWgBcAF4AIABAogBAJABwAKYATKgAQCoAfjThVKwAQE.&s=e16d7e8c3217476e84c0bda1789fee8d39f0313d&event_type=1\",\n" +
            "                    \"usersync_url\": \"https%3A%2F%2Facdn.adnxs.com%2Fdmp%2Fasync_usersync.html\",\n" +
            "                    \"buyer_member_id\": 958,\n" +
            "                    \"creative_id\": 172059128,\n" +
            "                    \"media_type_id\": 4,\n" +
            "                    \"media_subtype_id\": 64,\n" +
            "                    \"brand_category_id\": 0,\n" +
            "                    \"client_initiated_ad_counting\": true,\n" +
            "                    \"rtb\": {\n" +
            "                        \"video\": {\n" +
            "                            \"player_width\": 300,\n" +
            "                            \"player_height\": 250,\n" +
            "                            \"duration_ms\": 32000,\n" +
            "                            \"playback_methods\": [\n" +
            "                                \"auto_play_sound_off\"\n" +
            "                            ],\n" +
            "                            \"frameworks\": [\n" +
            "                                \"vpaid_1_0\",\n" +
            "                                \"vpaid_2_0\",\n" +
            "                                \"mraid_1\",\n" +
            "                                \"mraid_2\",\n" +
            "                                \"ormma\"\n" +
            "                            ],\n" +
            "                            \"content\": \"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\" standalone=\\\"yes\\\"?><VAST version=\\\"2.0\\\"><Ad id=\\\"172059128\\\"><InLine><AdSystem>adnxs</AdSystem><AdTitle><![CDATA[NewOMIDTest]]></AdTitle><Error><![CDATA["+UTConstants.REQUEST_BASE_URL_UT+"/vast_track/v2?info=ZQAAAAMArgAFAQnDe3peAAAAABG-_iqjjxzFTBnDe3peAAAAACD404VSKAAwvgc4vgdAx9FMSPP7jAJQpYfqB1gBYgItLWgBcAF4AIABAogBAJABwAKYATKgAQCoAfjThVKwAQE.&s=e16d7e8c3217476e84c0bda1789fee8d39f0313d&event_type=4&error_code=[ERRORCODE]]]></Error><Impression id=\\\"adnxs\\\"><![CDATA["+UTConstants.REQUEST_BASE_URL_UT+"/it?an_audit=0&referrer=itunes.apple.com%2Fus%2Fapp%2Fappnexus-sdk-app%2Fid736869833&e=wqT_3QKaCXyaBAAAAwDWAAUBCMP36fMFEL79q5n6kcfiTBgAKjYJAAUBCPA_EQUIDADwPxkJCQjgPyEJCQjwPykRCQAxCQmo4D8wpYfqBzi-B0C-B0gCUPjThVJYuaxgYABokUB4vqcFgAEBigEDVVNEkgUG8EaYAQGgAQGoAQGwAQC4AQPAAQPIAQLQAQDYAQDgAQDwAQCKAll1ZignYScsIDI3NjcyMDcsIDE1ODUwODUzNzkpO3VmKCdpJwEUEDUyMjgxAQk2HQAwcicsIDE3MjA1OTEyODYfAPCfkgKhAyFYRWljcGdpcGw0TVBFUGpUaFZJWUFDQzVyR0F3QURnQVFBUkl2Z2RRcFlmcUIxZ0FZUF9fX184UGFBQndBWGdCZ0FFQmlBRUJrQUVCbUFFQm9BRUJxQUVEc0FFQXVRRXBpNGlEQUFEd1A4RUJLWXVJZ3dBQThEX0pBVGFSbXRSRm1PMF8yUUVBQUFBQUFBRHdQLUFCbWQ5ZTlRRQUUKG1BSUFvQUlBdFFJBRAAdg0ImHdBSUJ5QUlCMEFJQjJBSUI0QUlBNkFJQS1BSUFnQU1CbUFNQnFBTwXYoHVnTUpUbGxOTWpvME16WXg0QU9ISFlBRUFJZ0VBSkFFQUpnRUFjRUVBBWIBAQhESkIBBw0BCDBRUQkKJElBYVFOZ0VBUEUdLCBDSUJZa2lxUVUJJBhBRHdQN0VGDQ0UQUFBREJCDT8BAQB5FSgMQUFBTjIoAABaLigASDRBV2dqUVkumgKJASFHaENFRXc2pQEkdWF4Z0lBUW9BRBGMEER3UHpvMukAEFFJY2RTEX0MUEFfVREMDEFBQVcdDABZHQwAYR0MAGMNDKhnQnBBZUFBLtgC6AfgAsfTAeoCNGl0dW5lcy5hcHBsZS5jb20vdXMvYXBwAQTwvG5leHVzLXNkay1hcHAvaWQ3MzY4Njk4MzOAAwGIAwGQAwCYAxegAwGqAwDAA-CoAcgDANgD-aN64AMA6AMC-AMAgAQAkgQGL3V0L3YzmAQAogQLMTAuNzUuMTEuNDSoBACyBBcIABAEGMACIDIoASgCKAMoBCgFMAA4ArgEAMAEAMgEANIEDTk1OCNOWU0yOjQzNjHaBAIIAeAEAPAE-NOFUvoEEgkAAABAluRCQBEAAADAAppewIIFCTczNgmqIIgFAZgFAKAF_xEBGAHABQDJBQAFARDwP9IFCQFABQFo2AUB4AUB8AUB-gUECAAQAJAGAZgGALgGAMEGBSAsAPA_0AbWAtoGFgoQCREZAVwQABgA4AYE8gYCCACABwGIBwCgB0DIBwA.&s=2857b048eaf6856213c1791363b68bd81293ff2e]]></Impression><Creatives><Creative id=\\\"175308\\\" AdID=\\\"172059128\\\"><Linear><Duration>00:00:32</Duration><TrackingEvents><Tracking event=\\\"start\\\"><![CDATA["+UTConstants.REQUEST_BASE_URL_UT+"/vast_track/v2?info=ZQAAAAMArgAFAQnDe3peAAAAABG-_iqjjxzFTBnDe3peAAAAACD404VSKAAwvgc4vgdAx9FMSPP7jAJQpYfqB1gBYgItLWgBcAF4AIABAogBAJABwAKYATKgAQCoAfjThVKwAQE.&s=e16d7e8c3217476e84c0bda1789fee8d39f0313d&event_type=2]]></Tracking><Tracking event=\\\"skip\\\"><![CDATA["+UTConstants.REQUEST_BASE_URL_UT+"/vast_track/v2?info=ZQAAAAMArgAFAQnDe3peAAAAABG-_iqjjxzFTBnDe3peAAAAACD404VSKAAwvgc4vgdAx9FMSPP7jAJQpYfqB1gBYgItLWgBcAF4AIABAogBAJABwAKYATKgAQCoAfjThVKwAQE.&s=e16d7e8c3217476e84c0bda1789fee8d39f0313d&event_type=3]]></Tracking><Tracking event=\\\"firstQuartile\\\"><![CDATA["+UTConstants.REQUEST_BASE_URL_UT+"/vast_track/v2?info=ZQAAAAMArgAFAQnDe3peAAAAABG-_iqjjxzFTBnDe3peAAAAACD404VSKAAwvgc4vgdAx9FMSPP7jAJQpYfqB1gBYgItLWgBcAF4AIABAogBAJABwAKYATKgAQCoAfjThVKwAQE.&s=e16d7e8c3217476e84c0bda1789fee8d39f0313d&event_type=5]]></Tracking><Tracking event=\\\"midpoint\\\"><![CDATA["+UTConstants.REQUEST_BASE_URL_UT+"/vast_track/v2?info=ZQAAAAMArgAFAQnDe3peAAAAABG-_iqjjxzFTBnDe3peAAAAACD404VSKAAwvgc4vgdAx9FMSPP7jAJQpYfqB1gBYgItLWgBcAF4AIABAogBAJABwAKYATKgAQCoAfjThVKwAQE.&s=e16d7e8c3217476e84c0bda1789fee8d39f0313d&event_type=6]]></Tracking><Tracking event=\\\"thirdQuartile\\\"><![CDATA["+UTConstants.REQUEST_BASE_URL_UT+"/vast_track/v2?info=ZQAAAAMArgAFAQnDe3peAAAAABG-_iqjjxzFTBnDe3peAAAAACD404VSKAAwvgc4vgdAx9FMSPP7jAJQpYfqB1gBYgItLWgBcAF4AIABAogBAJABwAKYATKgAQCoAfjThVKwAQE.&s=e16d7e8c3217476e84c0bda1789fee8d39f0313d&event_type=7]]></Tracking><Tracking event=\\\"complete\\\"><![CDATA["+UTConstants.REQUEST_BASE_URL_UT+"/vast_track/v2?info=ZQAAAAMArgAFAQnDe3peAAAAABG-_iqjjxzFTBnDe3peAAAAACD404VSKAAwvgc4vgdAx9FMSPP7jAJQpYfqB1gBYgItLWgBcAF4AIABAogBAJABwAKYATKgAQCoAfjThVKwAQE.&s=e16d7e8c3217476e84c0bda1789fee8d39f0313d&event_type=8]]></Tracking></TrackingEvents><VideoClicks><ClickThrough><![CDATA[https://xandr.com]]></ClickThrough><ClickTracking id=\\\"adnxs\\\"><![CDATA["+UTConstants.REQUEST_BASE_URL_UT+"/click?AAAAAAAA8D8AAAAAAADwPwAAAAAAAOA_AAAAAAAA8D8AAAAAAADwP77-KqOPHMVMFQxL5j-P8SjDe3peAAAAAKWD-gC-AwAAvgMAAAIAAAD4aUEKORYYAAAAAABVU0QAVVNEAAEAAQARIAAAAAABAwMCAAAAAAAAPRet3wAAAAA./bcr=AAAAAAAA8D8=/cnd=%21GhCEEwipl4MPEPjThVIYuaxgIAQoADEAAAAAAADwPzoJTllNMjo0MzYxQIcdSQAAAAAAAPA_UQAAAAAAAAAAWQAAAAAAAAAAYQAAAAAAAAAAaQAAAAAAAAAAcQAAAAAAgBpAeAA./cca=OTU4I05ZTTI6NDM2MQ==/bn=86974/]]></ClickTracking></VideoClicks><MediaFiles><MediaFile id=\\\"1669967\\\" delivery=\\\"progressive\\\" type=\\\"video/mp4\\\" width=\\\"1280\\\" height=\\\"720\\\" scalable=\\\"true\\\" bitrate=\\\"1100\\\" maintainAspectRatio=\\\"true\\\"><![CDATA[http://crcdn09.adnxs.com/creative/p/958/2019/8/1/13424705/d8064604-7c8f-4584-b803-2be3ea2dda33_1280_720_1100k.mp4]]></MediaFile><MediaFile id=\\\"1669968\\\" delivery=\\\"progressive\\\" type=\\\"video/mp4\\\" width=\\\"1280\\\" height=\\\"720\\\" scalable=\\\"true\\\" bitrate=\\\"1100\\\" maintainAspectRatio=\\\"true\\\"><![CDATA[http://crcdn09.adnxs.com/creative/p/958/2019/8/1/13424705/d8064604-7c8f-4584-b803-2be3ea2dda33_1280_720_1100k.mp4]]></MediaFile><MediaFile id=\\\"1669969\\\" delivery=\\\"progressive\\\" type=\\\"video/x-flv\\\" width=\\\"768\\\" height=\\\"432\\\" scalable=\\\"true\\\" bitrate=\\\"500\\\" maintainAspectRatio=\\\"true\\\"><![CDATA[http://crcdn09.adnxs.com/creative/p/958/2019/8/1/13424705/d8064604-7c8f-4584-b803-2be3ea2dda33_768_432_500k.flv]]></MediaFile><MediaFile id=\\\"1669970\\\" delivery=\\\"progressive\\\" type=\\\"video/mp4\\\" width=\\\"1280\\\" height=\\\"720\\\" scalable=\\\"true\\\" bitrate=\\\"1700\\\" maintainAspectRatio=\\\"true\\\"><![CDATA[http://crcdn09.adnxs.com/creative/p/958/2019/8/1/13424705/d8064604-7c8f-4584-b803-2be3ea2dda33_1280_720_1700k.mp4]]></MediaFile><MediaFile id=\\\"1669971\\\" delivery=\\\"progressive\\\" type=\\\"video/x-flv\\\" width=\\\"768\\\" height=\\\"432\\\" scalable=\\\"true\\\" bitrate=\\\"500\\\" maintainAspectRatio=\\\"true\\\"><![CDATA[http://crcdn09.adnxs.com/creative/p/958/2019/8/1/13424705/d8064604-7c8f-4584-b803-2be3ea2dda33_768_432_500k.flv]]></MediaFile><MediaFile id=\\\"1669972\\\" delivery=\\\"progressive\\\" type=\\\"video/x-flv\\\" width=\\\"768\\\" height=\\\"432\\\" scalable=\\\"true\\\" bitrate=\\\"1100\\\" maintainAspectRatio=\\\"true\\\"><![CDATA[http://crcdn09.adnxs.com/creative/p/958/2019/8/1/13424705/d8064604-7c8f-4584-b803-2be3ea2dda33_768_432_1100k.flv]]></MediaFile><MediaFile id=\\\"1669973\\\" delivery=\\\"progressive\\\" type=\\\"video/mp4\\\" width=\\\"1280\\\" height=\\\"720\\\" scalable=\\\"true\\\" bitrate=\\\"1700\\\" maintainAspectRatio=\\\"true\\\"><![CDATA[http://crcdn09.adnxs.com/creative/p/958/2019/8/1/13424705/d8064604-7c8f-4584-b803-2be3ea2dda33_1280_720_1700k.mp4]]></MediaFile><MediaFile id=\\\"1669974\\\" delivery=\\\"progressive\\\" type=\\\"video/mp4\\\" width=\\\"768\\\" height=\\\"432\\\" scalable=\\\"true\\\" bitrate=\\\"1700\\\" maintainAspectRatio=\\\"true\\\"><![CDATA[http://crcdn09.adnxs.com/creative/p/958/2019/8/1/13424705/d8064604-7c8f-4584-b803-2be3ea2dda33_768_432_1700k.mp4]]></MediaFile><MediaFile id=\\\"1669975\\\" delivery=\\\"progressive\\\" type=\\\"video/x-flv\\\" width=\\\"768\\\" height=\\\"432\\\" scalable=\\\"true\\\" bitrate=\\\"1100\\\" maintainAspectRatio=\\\"true\\\"><![CDATA[http://crcdn09.adnxs.com/creative/p/958/2019/8/1/13424705/d8064604-7c8f-4584-b803-2be3ea2dda33_768_432_1100k.flv]]></MediaFile><MediaFile id=\\\"1669976\\\" delivery=\\\"progressive\\\" type=\\\"video/webm\\\" width=\\\"1280\\\" height=\\\"720\\\" scalable=\\\"true\\\" bitrate=\\\"2000\\\" maintainAspectRatio=\\\"true\\\"><![CDATA[http://crcdn09.adnxs.com/creative/p/958/2019/8/1/13424705/d8064604-7c8f-4584-b803-2be3ea2dda33_1280_720_2000k.webm]]></MediaFile><MediaFile id=\\\"1669977\\\" delivery=\\\"progressive\\\" type=\\\"video/mp4\\\" width=\\\"768\\\" height=\\\"432\\\" scalable=\\\"true\\\" bitrate=\\\"1700\\\" maintainAspectRatio=\\\"true\\\"><![CDATA[http://crcdn09.adnxs.com/creative/p/958/2019/8/1/13424705/d8064604-7c8f-4584-b803-2be3ea2dda33_768_432_1700k.mp4]]></MediaFile><MediaFile id=\\\"1669978\\\" delivery=\\\"progressive\\\" type=\\\"video/mp4\\\" width=\\\"768\\\" height=\\\"432\\\" scalable=\\\"true\\\" bitrate=\\\"1100\\\" maintainAspectRatio=\\\"true\\\"><![CDATA[http://crcdn09.adnxs.com/creative/p/958/2019/8/1/13424705/d8064604-7c8f-4584-b803-2be3ea2dda33_768_432_1100k.mp4]]></MediaFile><MediaFile id=\\\"1669979\\\" delivery=\\\"progressive\\\" type=\\\"video/webm\\\" width=\\\"768\\\" height=\\\"432\\\" scalable=\\\"true\\\" bitrate=\\\"1500\\\" maintainAspectRatio=\\\"true\\\"><![CDATA[http://crcdn09.adnxs.com/creative/p/958/2019/8/1/13424705/d8064604-7c8f-4584-b803-2be3ea2dda33_768_432_1500k.webm]]></MediaFile><MediaFile id=\\\"1669980\\\" delivery=\\\"progressive\\\" type=\\\"video/webm\\\" width=\\\"1280\\\" height=\\\"720\\\" scalable=\\\"true\\\" bitrate=\\\"2000\\\" maintainAspectRatio=\\\"true\\\"><![CDATA[http://crcdn09.adnxs.com/creative/p/958/2019/8/1/13424705/d8064604-7c8f-4584-b803-2be3ea2dda33_1280_720_2000k.webm]]></MediaFile><MediaFile id=\\\"1669981\\\" delivery=\\\"progressive\\\" type=\\\"video/mp4\\\" width=\\\"1280\\\" height=\\\"720\\\" scalable=\\\"true\\\" bitrate=\\\"2500\\\" maintainAspectRatio=\\\"true\\\"><![CDATA[http://crcdn09.adnxs.com/creative/p/958/2019/8/1/13424705/d8064604-7c8f-4584-b803-2be3ea2dda33_1280_720_2500k.mp4]]></MediaFile><MediaFile id=\\\"1669982\\\" delivery=\\\"progressive\\\" type=\\\"video/mp4\\\" width=\\\"768\\\" height=\\\"432\\\" scalable=\\\"true\\\" bitrate=\\\"1100\\\" maintainAspectRatio=\\\"true\\\"><![CDATA[http://crcdn09.adnxs.com/creative/p/958/2019/8/1/13424705/d8064604-7c8f-4584-b803-2be3ea2dda33_768_432_1100k.mp4]]></MediaFile><MediaFile id=\\\"1669983\\\" delivery=\\\"progressive\\\" type=\\\"video/mp4\\\" width=\\\"768\\\" height=\\\"432\\\" scalable=\\\"true\\\" bitrate=\\\"500\\\" maintainAspectRatio=\\\"true\\\"><![CDATA[http://crcdn09.adnxs.com/creative/p/958/2019/8/1/13424705/d8064604-7c8f-4584-b803-2be3ea2dda33_768_432_500k.mp4]]></MediaFile><MediaFile id=\\\"1669984\\\" delivery=\\\"progressive\\\" type=\\\"video/webm\\\" width=\\\"768\\\" height=\\\"432\\\" scalable=\\\"true\\\" bitrate=\\\"1500\\\" maintainAspectRatio=\\\"true\\\"><![CDATA[http://crcdn09.adnxs.com/creative/p/958/2019/8/1/13424705/d8064604-7c8f-4584-b803-2be3ea2dda33_768_432_1500k.webm]]></MediaFile><MediaFile id=\\\"1669985\\\" delivery=\\\"progressive\\\" type=\\\"video/webm\\\" width=\\\"1280\\\" height=\\\"720\\\" scalable=\\\"true\\\" bitrate=\\\"600\\\" maintainAspectRatio=\\\"true\\\"><![CDATA[http://crcdn09.adnxs.com/creative/p/958/2019/8/1/13424705/d8064604-7c8f-4584-b803-2be3ea2dda33_1280_720_600k.webm]]></MediaFile><MediaFile id=\\\"1669986\\\" delivery=\\\"progressive\\\" type=\\\"video/mp4\\\" width=\\\"1280\\\" height=\\\"720\\\" scalable=\\\"true\\\" bitrate=\\\"2500\\\" maintainAspectRatio=\\\"true\\\"><![CDATA[http://crcdn09.adnxs.com/creative/p/958/2019/8/1/13424705/d8064604-7c8f-4584-b803-2be3ea2dda33_1280_720_2500k.mp4]]></MediaFile><MediaFile id=\\\"1669987\\\" delivery=\\\"progressive\\\" type=\\\"video/webm\\\" width=\\\"768\\\" height=\\\"432\\\" scalable=\\\"true\\\" bitrate=\\\"500\\\" maintainAspectRatio=\\\"true\\\"><![CDATA[http://crcdn09.adnxs.com/creative/p/958/2019/8/1/13424705/d8064604-7c8f-4584-b803-2be3ea2dda33_768_432_500k.webm]]></MediaFile><MediaFile id=\\\"1669988\\\" delivery=\\\"progressive\\\" type=\\\"video/mp4\\\" width=\\\"768\\\" height=\\\"432\\\" scalable=\\\"true\\\" bitrate=\\\"500\\\" maintainAspectRatio=\\\"true\\\"><![CDATA[http://crcdn09.adnxs.com/creative/p/958/2019/8/1/13424705/d8064604-7c8f-4584-b803-2be3ea2dda33_768_432_500k.mp4]]></MediaFile><MediaFile id=\\\"1669989\\\" delivery=\\\"progressive\\\" type=\\\"video/mp4\\\" width=\\\"1920\\\" height=\\\"1080\\\" scalable=\\\"true\\\" bitrate=\\\"801\\\" maintainAspectRatio=\\\"true\\\"><![CDATA[http://crcdn09.adnxs.com/creative/p/958/2019/8/1/13424705/b038db70-89c2-4890-b1b3-0d8aae82a96a.mp4]]></MediaFile><MediaFile id=\\\"1669990\\\" delivery=\\\"progressive\\\" type=\\\"video/webm\\\" width=\\\"1280\\\" height=\\\"720\\\" scalable=\\\"true\\\" bitrate=\\\"600\\\" maintainAspectRatio=\\\"true\\\"><![CDATA[http://crcdn09.adnxs.com/creative/p/958/2019/8/1/13424705/d8064604-7c8f-4584-b803-2be3ea2dda33_1280_720_600k.webm]]></MediaFile><MediaFile id=\\\"1669991\\\" delivery=\\\"progressive\\\" type=\\\"video/webm\\\" width=\\\"768\\\" height=\\\"432\\\" scalable=\\\"true\\\" bitrate=\\\"500\\\" maintainAspectRatio=\\\"true\\\"><![CDATA[http://crcdn09.adnxs.com/creative/p/958/2019/8/1/13424705/d8064604-7c8f-4584-b803-2be3ea2dda33_768_432_500k.webm]]></MediaFile><MediaFile id=\\\"1669992\\\" delivery=\\\"progressive\\\" type=\\\"video/mp4\\\" width=\\\"1920\\\" height=\\\"1080\\\" scalable=\\\"true\\\" bitrate=\\\"801\\\" maintainAspectRatio=\\\"true\\\"><![CDATA[http://crcdn09.adnxs.com/creative/p/958/2019/8/1/13424705/b038db70-89c2-4890-b1b3-0d8aae82a96a.mp4]]></MediaFile></MediaFiles></Linear></Creative></Creatives><Extensions><Extension><Extension type=\\\"AdVerifications\\\"><AdVerifications><Verification vendor=\\\"iabtechlab.com-appnexus\\\"><JavaScriptResource apiFramework=\\\"omid\\\" browserOptional=\\\"true\\\"><![CDATA[https://acdn.adnxs.com/mobile/omsdk/validation-verification-scripts-fortesting/omsdk-js-1.4.9/Validation-Script/omid-validation-verification-script-v1.js]]></JavaScriptResource><TrackingEvents><Tracking event=\\\"verificationNotExecuted\\\"><![CDATA[]]></Tracking></TrackingEvents><VerificationParameters><![CDATA[\\\"iabtechlab-appnexus\\\"]]></VerificationParameters></Verification></AdVerifications></Extension></Extension><Extension type=\\\"AdVerifications\\\"><AdVerifications><Verification vendor=\\\"appnexus.com-omid\\\"><JavaScriptResource apiFramework=\\\"omid\\\" browserOptional=\\\"false\\\"><![CDATA[https://cdn.adnxs.com/v/appvid/185/trk.js]]></JavaScriptResource><VerificationParameters><![CDATA[appvid;tv=appvid1-24j;d=1x1;s=4406771;st=2;vctx=0;ts=1585085379;vc=iab;vid_ccr=1;cid=3;tag_id=16417701;cb=https%3A%2F%2Fnym1-ib.adnxs.com%2Fvevent%3Fan_audit%3D0%26referrer%3Ditunes.apple.com%252Fus%252Fapp%252Fappnexus-sdk-app%252Fid736869833%26e%3DwqT_3QKaCXyaBAAAAwDWAAUBCMP36fMFEL79q5n6kcfiTBgAKjYJAAUBCPA_EQUIDADwPxkJCQjgPyEJCQjwPykRCQAxCQmo4D8wpYfqBzi-B0C-B0gCUPjThVJYuaxgYABokUB4vqcFgAEBigEDVVNEkgUG8EaYAQGgAQGoAQGwAQC4AQPAAQPIAQLQAQDYAQDgAQDwAQCKAll1ZignYScsIDI3NjcyMDcsIDE1ODUwODUzNzkpO3VmKCdpJwEUEDUyMjgxAQk2HQAwcicsIDE3MjA1OTEyODYfAPCfkgKhAyFYRWljcGdpcGw0TVBFUGpUaFZJWUFDQzVyR0F3QURnQVFBUkl2Z2RRcFlmcUIxZ0FZUF9fX184UGFBQndBWGdCZ0FFQmlBRUJrQUVCbUFFQm9BRUJxQUVEc0FFQXVRRXBpNGlEQUFEd1A4RUJLWXVJZ3dBQThEX0pBVGFSbXRSRm1PMF8yUUVBQUFBQUFBRHdQLUFCbWQ5ZTlRRQUUKG1BSUFvQUlBdFFJBRAAdg0ImHdBSUJ5QUlCMEFJQjJBSUI0QUlBNkFJQS1BSUFnQU1CbUFNQnFBTwXYoHVnTUpUbGxOTWpvME16WXg0QU9ISFlBRUFJZ0VBSkFFQUpnRUFjRUVBBWIBAQhESkIBBw0BCDBRUQkKJElBYVFOZ0VBUEUdLCBDSUJZa2lxUVUJJBhBRHdQN0VGDQ0UQUFBREJCDT8BAQB5FSgMQUFBTjIoAABaLigASDRBV2dqUVkumgKJASFHaENFRXc2pQEkdWF4Z0lBUW9BRBGMEER3UHpvMukAEFFJY2RTEX0MUEFfVREMDEFBQVcdDABZHQwAYR0MAGMNDKhnQnBBZUFBLtgC6AfgAsfTAeoCNGl0dW5lcy5hcHBsZS5jb20vdXMvYXBwAQTwvG5leHVzLXNkay1hcHAvaWQ3MzY4Njk4MzOAAwGIAwGQAwCYAxegAwGqAwDAA-CoAcgDANgD-aN64AMA6AMC-AMAgAQAkgQGL3V0L3YzmAQAogQLMTAuNzUuMTEuNDSoBACyBBcIABAEGMACIDIoASgCKAMoBCgFMAA4ArgEAMAEAMgEANIEDTk1OCNOWU0yOjQzNjHaBAIIAeAEAPAE-NOFUvoEEgkAAABAluRCQBEAAADAAppewIIFCTczNgmqIIgFAZgFAKAF_xEBGAHABQDJBQAFARDwP9IFCQFABQFo2AUB4AUB8AUB-gUECAAQAJAGAZgGALgGAMEGBSAsAPA_0AbWAtoGFgoQCREZAVwQABgA4AYE8gYCCACABwGIBwCgB0DIBwA.%26s%3D2857b048eaf6856213c1791363b68bd81293ff2e;ts=1585085379;cet=0;cecb=;rdcb=https%3A%2F%2Fnym1-ib.adnxs.com%2Frd_log%3Fan_audit%3D0%26referrer%3Ditunes.apple.com%252Fus%252Fapp%252Fappnexus-sdk-app%252Fid736869833%26e%3DwqT_3QKnC3ynBQAAAwDWAAUBCMP36fMFEL79q5n6kcfiTBgAKjYJAAUBCPA_EQUIDADwPxkJCQjgPyEJCQjwPykRCQAxCQmo4D8wpYfqBzi-B0C-B0gCUPjThVJYuaxgYABokUB4vqcFgAEBigEDVVNEkgUG8EaYAQGgAQGoAQGwAQC4AQPAAQPIAQLQAQDYAQDgAQDwAQCKAll1ZignYScsIDI3NjcyMDcsIDE1ODUwODUzNzkpO3VmKCdpJwEUEDUyMjgxAQk2HQAwcicsIDE3MjA1OTEyODYfAPCfkgKhAyFYRWljcGdpcGw0TVBFUGpUaFZJWUFDQzVyR0F3QURnQVFBUkl2Z2RRcFlmcUIxZ0FZUF9fX184UGFBQndBWGdCZ0FFQmlBRUJrQUVCbUFFQm9BRUJxQUVEc0FFQXVRRXBpNGlEQUFEd1A4RUJLWXVJZ3dBQThEX0pBVGFSbXRSRm1PMF8yUUVBQUFBQUFBRHdQLUFCbWQ5ZTlRRQUUKG1BSUFvQUlBdFFJBRAAdg0ImHdBSUJ5QUlCMEFJQjJBSUI0QUlBNkFJQS1BSUFnQU1CbUFNQnFBTwXYoHVnTUpUbGxOTWpvME16WXg0QU9ISFlBRUFJZ0VBSkFFQUpnRUFjRUVBBWIBAQhESkIBBw0BCDBRUQkKJElBYVFOZ0VBUEUdLCBDSUJZa2lxUVUJJBhBRHdQN0VGDQ0UQUFBREJCDT8BAQB5FSgMQUFBTjIoAABaLigASDRBV2dqUVkumgKJASFHaENFRXc2pQEkdWF4Z0lBUW9BRBGMEER3UHpvMukAEFFJY2RTEX0MUEFfVREMDEFBQVcdDABZHQwAYR0MAGMNDKhnQnBBZUFBLtgC6AfgAsfTAeoCNGl0dW5lcy5hcHBsZS5jb20vdXMvYXBwAQSgbmV4dXMtc2RrLWFwcC9pZDczNjg2OTgzM_ICEQoGQURWX0lEEgcyNzZB0AUUCENQRwUUGDgzOTY3MDABFAgFQ1ABEzQIMzE1MDk0MTfyAg0KCAE8GEZSRVESATAFEBxSRU1fVVNFUgUQAAwJIBhDT0RFEgDyAQ8BVxEPEAsKB0NQFQ4QEAoFSU8BYAQHMWk6APIBIQRJTxUhOBMKD0NVU1RPTV9NT0RFTAErFADyAhoKFjIWABxMRUFGX05BTQVxCB4KGjYdAAhBU1QBPhBJRklFRAE-HA0KCFNQTElUAU3wpAEwgAMBiAMBkAMAmAMXoAMBqgMAwAPgqAHIAwDYA_mjeuADAOgDAvgDAIAEAJIEBi91dC92M5gEAKIECzEwLjc1LjExLjQ0qAQAsgQXCAAQBBjAAiAyKAEoAigDKAQoBTAAOAK4BADABADIBADSBA05NTgjTllNMjo0MzYx2gQCCAHgBADwBPjThVL6BBIJAAAAQJbkQkARAAAAwAKaXsCCBQk3My23IIgFAZgFAKAF_xEBGAHABQDJBQAFARTwP9IFCQkFC3QAAADYBQHgBQHwBQH6BQQIABAAkAYBmAYAuAYAwQYBHzAAAPA_0AbWAtoGFgoQCREZAVwQABgA4AYE8gYCCACABwGIBwCgB0DIBwA.%26s%3D1a0e1b0a6ea43a52172241d2d0143c0c7fee338b]]></VerificationParameters></Verification></AdVerifications></Extension></Extensions></InLine></Ad></VAST>\"\n" +
            "                        }\n" +
            "                    }\n" +
            "                }\n" +
            "            ]\n" +
            "        }\n" +
            "    ]\n" +
            "}\n";
    public  static  final String BANNER_OMID_Ad = "{\n" +
            "    \"version\": \"0.0.1\",\n" +
            "    \"tags\": [\n" +
            "             {\n" +
            "             \"tag_id\": 13457285,\n" +
            "             \"auction_id\": \"2291993975392492012\",\n" +
            "             \"nobid\": false,\n" +
            "             \"no_ad_url\": \""+UTConstants.REQUEST_BASE_URL_UT+"/it?e=wqT_3QLLBmxLAwAAAwDWAAUBCPqkptkFEOyz69SIjLPnHxj_EQEQASo2CQANAQARDQgEABkRCQAhEQkAKREJADERCfB7MIWvtQY4vgdAvgdIAFAAWPfiP2AAaJFAeACAAQGSAQNVU0SYAawCoAH6AagBAbABALgBAcABAMgBAtABANgBAOABAPABAIoCPHVmKCdhJywgMTEwNzYxNywgMTUyOTQ1MTEzMCk7dWYoJ3InLCAxMDIwNzA4MjksIDE1MhUf8JySAvkBIU5qdWd5Z2lmMnVrS0VLMzAxVEFZQUNEMzRqOHdBRGdBUUFSSXZnZFFoYS0xQmxnQVlQX19fXzhQYUFCd0FYZ0JnQUVCaUFFQmtBRUJtQUVCb0FFQnFBRURzQUVBdVFHUjd3cnc0WHFFUDhFQmtlOEs4T0Y2aERfSkFaZmk4WFhnd2UwXzJRRUFBQUFBQUFEd1AtQUJBUFVCBQ8oSmdDQUtBQ0FMVUMFEARMMAkI8ExNQUNBY2dDQWRBQ0FkZ0NBZUFDQU9nQ0FQZ0NBSUFEQVpBREFKZ0RBYWdEbjlycENyb0RDVTVaVFRJNk16WXpOUS4umgItIUd3cFB0Zzb8APDkOS1JX0lBUW9BRG9KVGxsTk1qb3pOak0x2ALoB-ACx9MB6gI9cGxheS5nb29nbGUuY29tL3N0b3JlL2FwcHMvZGV0YWlscz9pZD1jb20uYXBwbmV4dXMub3BlbnNka2FwcIADAYgDAZADAJgDF6ADAaoDAMADrALIAwDYA_zgWeADAOgDAvgDAIAEAJIEBi91dC92MpgEAKIECjEwLjEuMTMuNTaoBACyBBAIABABGKwCIPoBKAAwADgCuAQAwAQAyAQA0gQNOTU4I05ZTTI6MzYzNdoEAggA4AQB8ASt9NUw-gQSCUWHCEBKQEG0KMDMzCpAggUXY29tTq8AHIgFAZgFAKAFUfMY_wHABQDJBQVCFADwP9IFCXUCYNgFAeAFAfAFAfoFBAgAEACQBgCYBgC4BgE.&s=5fd01dbe8aba75c8a527ef1fa733fcb84ec8dd0c&referrer=play.google.com%2Fstore%2Fapps%2Fdetails%3Fid%3Dcom.appnexus.opensdkapp\",\n" +
            "             \"timeout_ms\": 10000,\n" +
            "             \"ad_profile_id\": 27079,\n" +
            "             \"ads\": [\n" +
            "                     {\n" +
            "                     \"content_source\": \"rtb\",\n" +
            "                     \"ad_type\": \"banner\",\n" +
            "                     \"buyer_member_id\": 958,\n" +
            "                     \"creative_id\": 102070829,\n" +
            "                     \"media_type_id\": 1,\n" +
            "                     \"media_subtype_id\": 1,\n" +
            "                     \"client_initiated_ad_counting\": true,\n" +
            "                     \"rtb\": {\n" +
            "                     \"banner\": {\n" +
            "                     \"content\": \"<!-- Creative 102070829 served by Member 958 via AppNexus --><html><body style=\\\"margin-left: 0%; margin-right: 0%; margin-top: 0%; margin-bottom: 0%\\\"><script type=\\\"text/javascript\\\">document.write('<script src=\\\\\\\"https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js\\\\\\\">\\\\n<\\\\/script>\\\\n<script src=\\\\\\\"mraid.js\\\\\\\"><\\\\/script>\\\\n\\\\n<!-- Dummy Background used to control the size of the Banner !-->\\\\n<body onclick=\\\\\\\"clickThrough();\\\\\\\">\\\\n<div id=\\\\\\\"background\\\\\\\">\\\\n\\\\t<style>\\\\n\\\\t\\\\t#background {\\\\n\\\\t\\\\t\\\\tbackground-color: #FF8700;\\\\n\\\\t\\\\t\\\\twidth: 100%;\\\\n\\\\t\\\\t\\\\theight: 100%;\\\\n\\\\t\\\\t\\\\tposition: fixed;\\\\n\\\\t\\\\t\\\\tz-index: -1;\\\\n\\\\t\\\\t\\\\topacity: 0;\\\\n\\\\t\\\\t\\\\tmargin: auto;\\\\n\\\\t\\\\t\\\\tcursor: pointer;\\\\n\\\\t\\\\t}\\\\n\\\\t</style>\\\\n\\\\t<div id=\\\\\\\"wrapper\\\\\\\">\\\\n\\\\t\\\\t<style>\\\\n\\\\t\\\\t\\\\t#wrapper {\\\\n\\\\t\\\\t\\\\t\\\\tmargin: 0;\\\\n\\\\t\\\\t\\\\t\\\\tposition: absolute;\\\\n\\\\t\\\\t\\\\t\\\\ttop: 50%;\\\\n\\\\t\\\\t\\\\t\\\\tleft: 50%;\\\\n\\\\t\\\\t\\\\t\\\\t-ms-transform: translate(-50%, -50%);\\\\n\\\\t\\\\t\\\\t\\\\ttransform: translate(-50%, -50%);\\\\n\\\\t\\\\t\\\\t\\\\topacity: 0;\\\\n\\\\t\\\\t\\\\t}\\\\n\\\\t\\\\t</style>\\\\n\\\\t\\\\t<div id=\\\\\\\"content\\\\\\\">\\\\n\\\\t\\\\t\\\\t<style>\\\\n\\\\t\\\\t\\\\t\\\\t#content {\\\\n\\\\t\\\\t\\\\t\\\\t\\\\tborder: 1px;\\\\n\\\\t\\\\t\\\\t\\\\t\\\\ttext-align: center;\\\\n\\\\t\\\\t\\\\t\\\\t\\\\tcolor: white;\\\\n\\\\t\\\\t\\\\t\\\\t\\\\tfont-size: 15px;\\\\n\\\\t\\\\t\\\\t\\\\t\\\\topacity: 1;\\\\n\\\\t\\\\t\\\\t\\\\t\\\\tpadding: 2px;\\\\n\\\\t\\\\t\\\\t\\\\t}\\\\n\\\\t\\\\t\\\\t</style>\\\\n\\\\t\\\\t</div>\\\\n\\\\t\\\\t<!-- Dummy div to show the Width and Height in the Ad!-->\\\\n\\\\t\\\\t<div id=\\\\\\\"size\\\\\\\">\\\\n\\\\t\\\\t\\\\t<style>\\\\n\\\\t\\\\t\\\\t\\\\t#size {\\\\n\\\\t\\\\t\\\\t\\\\t\\\\tborder: 1px;\\\\n\\\\t\\\\t\\\\t\\\\t\\\\ttext-align: center;\\\\n\\\\t\\\\t\\\\t\\\\t\\\\tcolor: white;\\\\n\\\\t\\\\t\\\\t\\\\t\\\\tfont-size: 15px;\\\\n\\\\t\\\\t\\\\t\\\\t\\\\topacity: 1;\\\\n\\\\t\\\\t\\\\t\\\\t\\\\tpadding: 2px;\\\\n\\\\t\\\\t\\\\t\\\\t}\\\\n\\\\t\\\\t\\\\t</style>\\\\n\\\\t\\\\t</div>\\\\n\\\\t</div>\\\\n</div>\\\\n</body>\\\\n<script>\\\\n\\\\tvar dur = 500;\\\\n\\\\n\\\\t\\$(document).ready(function() {\\\\n\\\\t\\\\tvar width = getWidth();\\\\n\\\\t\\\\tvar height = getHeight();\\\\n\\\\t\\\\t\\$(\\\\\\\"#background\\\\\\\")\\\\n\\\\t\\\\t.animate({\\\\n\\\\t\\\\t\\\\twidth: width,\\\\n\\\\t\\\\t\\\\theight: height,\\\\n\\\\t\\\\t\\\\topacity: 1\\\\n\\\\t\\\\t}, 200, function() {\\\\n\\\\t\\\\t\\\\tvar contentDiv = document.getElementById(\\\\\\\"content\\\\\\\");\\\\n\\\\t\\\\t\\\\tcontentDiv.innerHTML = getContent();\\\\n\\\\n\\\\t\\\\t\\\\tvar sizeDiv = document.getElementById(\\\\\\\"size\\\\\\\");\\\\n\\\\t\\\\t\\\\tsizeDiv.innerHTML = \\\\'Size = \\\\' + width + \\\\' x \\\\' + height;\\\\n\\\\n\\\\t\\\\t\\\\t\\$(\\\\\\\"#wrapper\\\\\\\").animate({\\\\n\\\\t\\\\t\\\\t\\\\topacity: 1\\\\n\\\\t\\\\t\\\\t});\\\\n\\\\t\\\\t});\\\\n\\\\t})\\\\n\\\\n\\\\tfunction getWidth() {\\\\n\\\\t\\\\t\\\\treturn 300;\\\\n\\\\t}\\\\n\\\\n\\\\tfunction getHeight() {\\\\n\\\\t\\\\t\\\\treturn 250;\\\\n\\\\t}\\\\n\\\\n\\\\tfunction getContent() {\\\\n\\\\t\\\\t\\\\treturn \\\\\\\"Open Measurement Test Creative\\\\\\\";\\\\n\\\\t}\\\\n\\\\n\\\\tfunction clickThrough() {\\\\n    mraid.open(\\\\'"+UTConstants.REQUEST_BASE_URL_UT+"/click?exSuR-F6hD97FK5H4XqEPwAAAAAAAAAAexSuR-F6hD97FK5H4XqEP-zZmopgzM4f__________96kilbAAAAAIVXzQC-AwAAvgMAAAIAAAAtehUGd_EPAAAAAABVU0QAVVNEACwB-gARIAAAAAABAQMCAAAAAAAAsCUtJgAAAAA./cnd=%21GwpPtgif2ukKEK301TAY9-I_IAQoADoJTllNMjozNjM1/cca=OTU4I05ZTTI6MzYzNQ==/bn=76231/referrer=play.google.com%2Fstore%2Fapps%2Fdetails%3Fid%3Dcom.appnexus.opensdkapp/clickenc=https://wiki.appnexus.com/display/sdk/Welcome\\\\');\\\\n\\\\t}\\\\n\\\\n<\\\\/script>\\\\n<script src=\\\\\\\"https://acdn.adnxs.com/mobile/omsdk/validation-verification-scripts-fortesting/omsdk-js-1.4.9/Validation-Script/omid-validation-verification-script-v1.js\\\\\\\" charset=\\\\\\\"utf-8\\\\\\\"><\\\\/script>\\\\n');</script></body></html><iframe src=\\\"http://acdn.adnxs.com/dmp/async_usersync.html\\\" width=\\\"1\\\" height=\\\"1\\\" frameborder=\\\"0\\\" scrolling=\\\"no\\\" marginheight=\\\"0\\\" marginwidth=\\\"0\\\" topmargin=\\\"0\\\" leftmargin=\\\"0\\\" style=\\\"position:absolute;overflow:hidden;clip:rect(0 0 0 0);height:1px;width:1px;margin:-1px;padding:0;border:0;\\\"></iframe><script>try {!function(){function e(e,t){return\\\"function\\\"==typeof __an_obj_extend_thunk?__an_obj_extend_thunk(e,t):e}function t(e,t){\\\"function\\\"==typeof __an_err_thunk&&__an_err_thunk(e,t)}function n(e,t){if(\\\"function\\\"==typeof __an_redirect_thunk)__an_redirect_thunk(e);else{var n=navigator.connection;navigator.__an_connection&&(n=navigator.__an_connection),window==window.top&&n&&n.downlinkMax<=.115&&\\\"function\\\"==typeof HTMLIFrameElement&&HTMLIFrameElement.prototype.hasOwnProperty(\\\"srcdoc\\\")?(window.__an_resize=function(e,t,n){var r=e.frameElement;r&&\\\"__an_if\\\"==r.getAttribute(\\\"name\\\")&&(t&&(r.style.width=t+\\\"px\\\"),n&&(r.style.height=n+\\\"px\\\"))},document.write('<iframe name=\\\"__an_if\\\" style=\\\"width:0;height:0\\\" srcdoc=\\\"<script type=\\\\'text/javascript\\\\' src=\\\\''+e+\\\"&\\\"+t.bdfif+\\\"=1'></sc\\\"),document.write('ript>\\\" frameborder=\\\"0\\\" scrolling=\\\"no\\\" marginheight=0 marginwidth=0 topmargin=\\\"0\\\" leftmargin=\\\"0\\\" allowtransparency=\\\"true\\\"></iframe>')):document.write('<script language=\\\"javascript\\\" src=\\\"'+e+'\\\"></scr'+'ipt>')}};var r=function(e){this.rdParams=e};r.prototype={constructor:r,walkAncestors:function(e){try{if(!window.location.ancestorOrigins)return;for(var t=0,n=window.location.ancestorOrigins.length;n>t;t++)e.call(null,window.location.ancestorOrigins[t],t)}catch(r){\\\"undefined\\\"!=typeof console}return[]},walkUpWindows:function(e){var t,n=[];do try{t=t?t.parent:window,e.call(null,t,n)}catch(r){return\\\"undefined\\\"!=typeof console,n.push({referrer:null,location:null,isTop:!1}),n}while(t!=window.top);return n},getPubUrlStack:function(e){var n,r=[],o=null,i=null,a=null,c=null,d=null,s=null,u=null;for(n=e.length-1;n>=0;n--){try{a=e[n].location}catch(l){\\\"undefined\\\"!=typeof console,t(l,\\\"AnRDModule::getPubUrlStack:: location\\\")}if(a)i=encodeURIComponent(a),r.push(i),u||(u=i);else if(0!==n){c=e[n-1];try{d=c.referrer,s=c.ancestor}catch(l){\\\"undefined\\\"!=typeof console,t(l,\\\"AnRDModule::getPubUrlStack:: prevFrame\\\")}d?(i=encodeURIComponent(d),r.push(i),u||(u=i)):s?(i=encodeURIComponent(s),r.push(i),u||(u=i)):r.push(o)}else r.push(o)}return{stack:r,detectUrl:u}},getLevels:function(){var e=this.walkUpWindows(function(e,n){try{n.push({referrer:e.document.referrer||null,location:e.location.href||null,isTop:e==window.top})}catch(r){n.push({referrer:null,location:null,isTop:e==window.top}),\\\"undefined\\\"!=typeof console,t(r,\\\"AnRDModule::getLevels\\\")}});return this.walkAncestors(function(t,n){e[n].ancestor=t}),e},getRefererInfo:function(){var e=\\\"\\\";try{var n=this.getLevels(),r=n.length-1,o=null!==n[r].location||r>0&&null!==n[r-1].referrer,i=this.getPubUrlStack(n);e=this.rdParams.rdRef+\\\"=\\\"+i.detectUrl+\\\"&\\\"+this.rdParams.rdTop+\\\"=\\\"+o+\\\"&\\\"+this.rdParams.rdIfs+\\\"=\\\"+r+\\\"&\\\"+this.rdParams.rdStk+\\\"=\\\"+i.stack+\\\"&\\\"+this.rdParams.rdQs}catch(a){e=\\\"\\\",\\\"undefined\\\"!=typeof console,t(a,\\\"AnRDModule::getRefererInfo\\\")}return e}};var o=function(n){var o=\\\"\\\";try{n=e(n,0);var i=e(new r(n),1);return i.getRefererInfo()}catch(a){o=\\\"\\\",\\\"undefined\\\"!=typeof console,t(a,\\\"AnRDModule::executeRD\\\")}return o};;var c=\\\"http://nym1-ib.adnxs.com/rd_log?e=wqT_3QKSB2ySAwAAAwDWAAUBCPqkptkFEOyz69SIjLPnHxj_EQEwASo2CXsUrkfheoQ_EREJBBkADQEAIRESACkRCQAxDRqoADCFr7UGOL4HQL4HSAJQrfTVMFj34j9gAGiRQHjH0wSAAQGKAQNVU0SSAQEG9GgBmAGsAqAB-gGoAQGwAQC4AQHAAQPIAQLQAQDYAQDgAQDwAQCKAjx1ZignYScsIDExMDc2MTcsIDE1Mjk0NTExMzApO3VmKCdyJywgMTAyMDcwODI5LCAxNTI5NDUxMTMwKTuSAvkBIU5qdWd5Z2lmMnVrS0VLMzAxVEFZQUNEMzRqOHdBRGdBUUFSSXZnZFFoYS0xQmxnQVlQX19fXzhQYUFCd0FYZ0JnQUVCaUFFQmtBRUJtQUVCb0FFQnFBRURzQUVBdVFHUjd3cnc0WHFFUDhFQmtlOEs4T0Y2aERfSkFaZmk4WFhnd2UwXzJRRUFBQUFBQUFEd1AtQUJBUFVCQUFBQUFKZ0NBS0FDQUxVQ0FBQUFBTDBDQUFBQUFNQUNBY2dDQWRBQ0FkZ0NBZUFDQU9nQ0FQZ0NBSUFEQVpBREFKZ0RBYWdEbjlycENyb0RDVTVaVFRJNk16WXpOUS4umgItIUd3cFB0Zzb8APQYATktSV9JQVFvQURvSlRsbE5Nam96TmpNMdgC6AfgAsfTAeoCPXBsYXkuZ29vZ2xlLmNvbS9zdG9yZS9hcHBzL2RldGFpbHM_aWQ9Y29tLmFwcG5leHVzLm9wZW5zZGthcHDyAhEKBkFEVl9JRBIHMTEwNzYxN_ICEQoGQ1BHX0lEEgczOTc5Mjky8gIRCgVDUF9JRBIIMjI3MDMzOTGAAwGIAwGQAwCYAxegAwGqAwDAA6wCyAMA2AP84FngAwDoAwL4AwCABACSBAYvdXQvdjKYBACiBAoxMC4xLjEzLjU2qAQAsgQQCAAQARisAiD6ASgAMAA4ArgEAMAEAMgEANIEDTk1OCNOWU0yOjM2MzXaBAIIAeAEAfAEQbAM-gQSCUXOPEBKQBEAAADAzMwqQIIFF2NW6wAciAUBmAUAoAVxOhj_AcAFAMkFBUIUAPA_0gUJCU5sAAAA2AUB4AUB8AUB-gUECAAQAJAGAJgGALgGAQ..&s=a4fa60c2fa9389a1d21c48300e1cad625c5dc713&referrer=play.google.com%2Fstore%2Fapps%2Fdetails%3Fid%3Dcom.appnexus.opensdkapp\\\";c+=\\\"&\\\"+o({rdRef:\\\"bdref\\\",rdTop:\\\"bdtop\\\",rdIfs:\\\"bdifs\\\",rdStk:\\\"bstk\\\",rdQs:\\\"\\\"}),n(c,{bdfif:\\\"bdfif\\\"})}();} catch (e) { }</script><script>try {document.write('<div name=\\\"anxhd\\\" width=\\\"0\\\" height=\\\"0\\\" style=\\\"display: block; margin: 0; padding: 0; height: 0; width: 0;\\\"><sc' + 'ript type=\\\"text/javascript\\\" async=\\\"true\\\" src=\\\"https://nym2-tr.adnxs.com?cb=1160687683&a=958&b=821438&c=10.1.13.0&d=&e=nym2&f=2291993975392492012&g=play.google.com%2Fstore%2Fapps%2Fdetails%3Fid%3Dcom.appnexus.opensdkapp&h=1529451130\\\"><\\\\/scr' + 'ipt><\\\\/div>');} catch (e) { }</script><div name=\\\"anxv\\\" lnttag=\\\"v;tv=view7-1h;st=2;d=300x250;vc=iab;vid_ccr=1;cid=1;tag_id=13457285;cb=http%3A%2F%2Fnym1-ib.adnxs.com%2Fvevent%3Fe%3DwqT_3QKSB2ySAwAAAwDWAAUBCPqkptkFEOyz69SIjLPnHxj_EQEwASo2CXsUrkfheoQ_EREJBBkADQEAIRESACkRCQAxDRqoADCFr7UGOL4HQL4HSAJQrfTVMFj34j9gAGiRQHjH0wSAAQGKAQNVU0SSAQEG9GgBmAGsAqAB-gGoAQGwAQC4AQHAAQPIAQLQAQDYAQDgAQDwAQCKAjx1ZignYScsIDExMDc2MTcsIDE1Mjk0NTExMzApO3VmKCdyJywgMTAyMDcwODI5LCAxNTI5NDUxMTMwKTuSAvkBIU5qdWd5Z2lmMnVrS0VLMzAxVEFZQUNEMzRqOHdBRGdBUUFSSXZnZFFoYS0xQmxnQVlQX19fXzhQYUFCd0FYZ0JnQUVCaUFFQmtBRUJtQUVCb0FFQnFBRURzQUVBdVFHUjd3cnc0WHFFUDhFQmtlOEs4T0Y2aERfSkFaZmk4WFhnd2UwXzJRRUFBQUFBQUFEd1AtQUJBUFVCQUFBQUFKZ0NBS0FDQUxVQ0FBQUFBTDBDQUFBQUFNQUNBY2dDQWRBQ0FkZ0NBZUFDQU9nQ0FQZ0NBSUFEQVpBREFKZ0RBYWdEbjlycENyb0RDVTVaVFRJNk16WXpOUS4umgItIUd3cFB0Zzb8APQYATktSV9JQVFvQURvSlRsbE5Nam96TmpNMdgC6AfgAsfTAeoCPXBsYXkuZ29vZ2xlLmNvbS9zdG9yZS9hcHBzL2RldGFpbHM_aWQ9Y29tLmFwcG5leHVzLm9wZW5zZGthcHDyAhEKBkFEVl9JRBIHMTEwNzYxN_ICEQoGQ1BHX0lEEgczOTc5Mjky8gIRCgVDUF9JRBIIMjI3MDMzOTGAAwGIAwGQAwCYAxegAwGqAwDAA6wCyAMA2AP84FngAwDoAwL4AwCABACSBAYvdXQvdjKYBACiBAoxMC4xLjEzLjU2qAQAsgQQCAAQARisAiD6ASgAMAA4ArgEAMAEAMgEANIEDTk1OCNOWU0yOjM2MzXaBAIIAeAEAfAEQbAM-gQSCUXOPEBKQBEAAADAzMwqQIIFF2NW6wAciAUBmAUAoAVxOhj_AcAFAMkFBUIUAPA_0gUJCU5sAAAA2AUB4AUB8AUB-gUECAAQAJAGAJgGALgGAQ..%26s%3Da4fa60c2fa9389a1d21c48300e1cad625c5dc713%26referrer%3Dplay.google.com%252Fstore%252Fapps%252Fdetails%253Fid%253Dcom.appnexus.opensdkapp;ts=1529451130;cet=0;cecb=\\\" width=\\\"0\\\" height=\\\"0\\\" style=\\\"display: block; margin: 0; padding: 0; height: 0; width: 0;\\\"><script type=\\\"text/javascript\\\" async=\\\"true\\\" src=\\\"http://cdn.adnxs.com/v/s/133/trk.js\\\"></script></div>\",\n" +
            "                     \"width\": 300,\n" +
            "                     \"height\": 250\n" +
            "                     },\n" +
            "                     \"trackers\": [\n" +
            "                                  {\n" +
            "                                  \"impression_urls\": [\n" +
            "                                                      \""+UTConstants.REQUEST_BASE_URL_UT+"/it?e=wqT_3QLWBmxWAwAAAwDWAAUBCPqkptkFEOyz69SIjLPnHxj_EQEwASo2CXsUrkfheoQ_EREJBBkADQEAIRESACkRCQAxDRqoADCFr7UGOL4HQL4HSAJQrfTVMFj34j9gAGiRQHjH0wSAAQGKAQNVU0SSAQEG9GgBmAGsAqAB-gGoAQGwAQC4AQHAAQTIAQLQAQDYAQDgAQDwAQCKAjx1ZignYScsIDExMDc2MTcsIDE1Mjk0NTExMzApO3VmKCdyJywgMTAyMDcwODI5LCAxNTI5NDUxMTMwKTuSAvkBIU5qdWd5Z2lmMnVrS0VLMzAxVEFZQUNEMzRqOHdBRGdBUUFSSXZnZFFoYS0xQmxnQVlQX19fXzhQYUFCd0FYZ0JnQUVCaUFFQmtBRUJtQUVCb0FFQnFBRURzQUVBdVFHUjd3cnc0WHFFUDhFQmtlOEs4T0Y2aERfSkFaZmk4WFhnd2UwXzJRRUFBQUFBQUFEd1AtQUJBUFVCQUFBQUFKZ0NBS0FDQUxVQ0FBQUFBTDBDQUFBQUFNQUNBY2dDQWRBQ0FkZ0NBZUFDQU9nQ0FQZ0NBSUFEQVpBREFKZ0RBYWdEbjlycENyb0RDVTVaVFRJNk16WXpOUS4umgItIUd3cFB0Zzb8APDcOS1JX0lBUW9BRG9KVGxsTk1qb3pOak0x2ALoB-ACx9MB6gI9cGxheS5nb29nbGUuY29tL3N0b3JlL2FwcHMvZGV0YWlscz9pZD1jb20uYXBwbmV4dXMub3BlbnNka2FwcIADAYgDAZADAJgDF6ADAaoDAMADrALIAwDYA_zgWeADAOgDAvgDAIAEAJIEBi91dC92MpgEAKIECjEwLjEuMTMuNTaoBACyBBAIABABGKwCIPoBKAAwADgCuAQAwAQAyAQA0gQNOTU4I05ZTTI6MzYzNdoEAggB4AQB8ARBdAz6BBIJRZJEQEpAEQAAAMDMzCpAggUXY29tTq8AHIgFAZgFAKAFUf4Y_wHABQDJBQVCFADwP9IFCQlObAAAANgFAeAFAfAFAfoFBAgAEACQBgCYBgC4BgE.&s=c60bf8485daaa47c88843086734eff52180ae5a5&referrer=play.google.com%2Fstore%2Fapps%2Fdetails%3Fid%3Dcom.appnexus.opensdkapp\"\n" +
            "                                                      ],\n" +
            "                                  \"video_events\": {}\n" +
            "                                  }\n" +
            "                                  ]\n" +
            "                     }\n" +
            "                     }\n" +
            "                     ]\n" +
            "             }\n" +
            "             ]\n" +
            "}\n";
    public static final String BANNER_AD = "{\n" +
            "  \"version\": \"3.0.0\",\n" +
            "  \"tags\": [\n" +
            "    {\n" +
            "      \"uuid\": \"45022FA4-FEE0-4F7B-8E4A-21C37C5CDD3F\",\n" +
            "      \"tag_id\": 20331545,\n" +
            "      \"auction_id\": \"3845066681594113162\",\n" +
            "      \"nobid\": false,\n" +
            "      \"no_ad_url\": \""+UTConstants.REQUEST_BASE_URL_UT+"/it?an_audit=0&test=1&e=wqT_3QLAA6DAAQAAAwDWAAUBCO63g4IGEIrZ7fDl-JquNRjIxcGZj4HNjnMqNgkAAAkCABEJBwgAABkRCQAhEQkAKREJADERCfD9MJn42Ak47k5A7k5IAFAAWABgAGgAeACAAQCKAQCYAQCgAQCoAQGwAQC4AQHAAQDIAQDQAQDYAQDgAQHwAQDYAgDgAgCAAwCIAwGQAwCYAxegAwCqAwDAA6wCyAMA2AMA4AMA6AMC-AMAgAQAkgQGL3V0L3YzmAQAqAQAsgQQCAAQARj3AiCsBigAMAA4ArgEAMAEAMgEANoEAggA4AQB8AQAggUbY29tLmFwcG5leHVzLkFwcE5leHVzU0RLQXBwiAUBmAUAoAUAwAUAyQUAAAAAAADwP9IFCQkAAAAAAAAAANgFAOAFAPAFAPoFBAgAEACQBgCYBgC4BgDBBgAlBhjwv9oGFgoQBQwdAVwQABgA4AYA8gYCCACABwGIBwCgBwC6Bw4BREAYACAAMAA4AEAAyAcA0gcNCRE4ATVA2gcGCAAQABgA4AcA6gcCCAA.&s=448fe5fc4acace6a6076ae0d814492bcdfd249b5\",\n" +
            "      \"timeout_ms\": 0,\n" +
            "      \"ad_profile_id\": 0,\n" +
            "      \"rtb_video_fallback\": false,\n" +
            "      \"ads\": [\n" +
            "        {\n" +
            "          \"content_source\": \"rtb\",\n" +
            "          \"ad_type\": \"banner\",\n" +
            "          \"buyer_member_id\": 10094,\n" +
            "          \"creative_id\": 223272198,\n" +
            "          \"media_type_id\": 1,\n" +
            "          \"media_subtype_id\": 1,\n" +
            "          \"brand_category_id\": 0,\n" +
            "          \"client_initiated_ad_counting\": true,\n" +
            "          \"rtb\": {\n" +
            "            \"banner\": {\n" +
            "              \"content\": \"<!-- Creative 223272198 served by Member 10094 via AppNexus --><html><body style=\\\"margin-left: 0%; margin-right: 0%; margin-top: 0%; margin-bottom: 0%\\\"><script type=\\\"text/javascript\\\">document.write('<script src=\\\\\\\"https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js\\\\\\\">\\\\n<\\\\/script>\\\\n<script src=\\\\\\\"mraid.js\\\\\\\"><\\\\/script>\\\\n<!-- Dummy Background used to control the size of the Banner !-->\\\\n<body onclick=\\\\\\\"clickThrough();\\\\\\\">\\\\n<div id=\\\\\\\"background\\\\\\\">\\\\n\\\\t<style>\\\\n\\\\t\\\\t#background {\\\\n\\\\t\\\\t\\\\tbackground-color: #FC5047;\\\\n\\\\t\\\\t\\\\twidth: 100%;\\\\n\\\\t\\\\t\\\\theight: 100%;\\\\n\\\\t\\\\t\\\\tposition: fixed;\\\\n\\\\t\\\\t\\\\tz-index: -1;\\\\n\\\\t\\\\t\\\\topacity: 0;\\\\n\\\\t\\\\t\\\\tmargin: auto;\\\\n\\\\t\\\\t\\\\tcursor: pointer;\\\\n\\\\t\\\\t}\\\\n\\\\t</style>\\\\n\\\\t<div id=\\\\\\\"wrapper\\\\\\\">\\\\n\\\\t\\\\t<style>\\\\n\\\\t\\\\t\\\\t#wrapper {\\\\n\\\\t\\\\t\\\\t\\\\tmargin: 0;\\\\n\\\\t\\\\t\\\\t\\\\tposition: absolute;\\\\n\\\\t\\\\t\\\\t\\\\ttop: 50%;\\\\n\\\\t\\\\t\\\\t\\\\tleft: 50%;\\\\n\\\\t\\\\t\\\\t\\\\t-ms-transform: translate(-50%, -50%);\\\\n\\\\t\\\\t\\\\t\\\\ttransform: translate(-50%, -50%);\\\\n\\\\t\\\\t\\\\t\\\\topacity: 0;\\\\n\\\\t\\\\t\\\\t}\\\\n\\\\t\\\\t</style>\\\\n\\\\t\\\\t<div id=\\\\\\\"content\\\\\\\">\\\\n\\\\t\\\\t\\\\t<style>\\\\n\\\\t\\\\t\\\\t\\\\t#content {\\\\n\\\\t\\\\t\\\\t\\\\t\\\\tborder: \\\\'1px\\\\';\\\\n\\\\t\\\\t\\\\t\\\\t\\\\ttext-align: center;\\\\n\\\\t\\\\t\\\\t\\\\t\\\\tcolor: #FCF7F7;\\\\n\\\\t\\\\t\\\\t\\\\t\\\\tfont-size: \\\\'15px\\\\';\\\\n\\\\t\\\\t\\\\t\\\\t\\\\topacity: 1;\\\\n\\\\t\\\\t\\\\t\\\\t\\\\tpadding: \\\\'2px\\\\';\\\\n\\\\t\\\\t\\\\t\\\\t}\\\\n\\\\t\\\\t\\\\t</style>\\\\n\\\\t\\\\t</div>\\\\n\\\\t\\\\t<!-- Dummy div to show the Width and Height in the Ad!-->\\\\n\\\\t\\\\t<div id=\\\\\\\"size\\\\\\\">\\\\n\\\\t\\\\t\\\\t<style>\\\\n\\\\t\\\\t\\\\t\\\\t#size {\\\\n\\\\t\\\\t\\\\t\\\\t\\\\tborder: \\\\'1px\\\\';\\\\n\\\\t\\\\t\\\\t\\\\t\\\\ttext-align: center;\\\\n\\\\t\\\\t\\\\t\\\\t\\\\tcolor: #FCF7F7;\\\\n\\\\t\\\\t\\\\t\\\\t\\\\tfont-size: \\\\'15px\\\\';\\\\n\\\\t\\\\t\\\\t\\\\t\\\\topacity: 1;\\\\n\\\\t\\\\t\\\\t\\\\t\\\\tpadding: \\\\'2px\\\\';\\\\n\\\\t\\\\t\\\\t\\\\t}\\\\n\\\\t\\\\t\\\\t</style>\\\\n\\\\t\\\\t</div>\\\\n\\\\t</div>\\\\n</div>\\\\n</body>\\\\n<script>\\\\n\\\\t\\$(document).ready(function() {\\\\n\\\\t\\\\tvar width = getWidth();\\\\n\\\\t\\\\tvar height = getHeight();\\\\n\\\\t\\\\t\\$(\\\\\\\"#background\\\\\\\")\\\\n\\\\t\\\\t.animate({\\\\n\\\\t\\\\t\\\\twidth: width,\\\\n\\\\t\\\\t\\\\theight: height,\\\\n\\\\t\\\\t\\\\topacity: 1\\\\n\\\\t\\\\t}, 200, function() {\\\\n\\\\t\\\\t\\\\tvar contentDiv = document.getElementById(\\\\\\\"content\\\\\\\");\\\\n\\\\t\\\\t\\\\tcontentDiv.innerHTML = getContent();\\\\n\\\\n            var pt1 = \\\\\\\"\\\\\\\";\\\\n            var pt2 = \\\\\\\"\\\\\\\";\\\\n\\\\n\\\\t\\\\t\\\\tvar sizeDiv = document.getElementById(\\\\\\\"size\\\\\\\");\\\\n\\\\t\\\\t\\\\tsizeDiv.innerHTML = \\\\'Size = \\\\' + width + \\\\' x \\\\' + height+ \\\\' pt1value= \\\\' + pt1 + \\\\' pt2value= \\\\' + pt2;\\\\n\\\\n\\\\t\\\\t\\\\t\\$(\\\\\\\"#wrapper\\\\\\\").animate({\\\\n\\\\t\\\\t\\\\t\\\\topacity: 1\\\\n\\\\t\\\\t\\\\t});\\\\n\\\\t\\\\t});\\\\n\\\\t})\\\\n\\\\n\\\\n\\\\tfunction getWidth() {\\\\n\\\\t\\\\t\\\\treturn 300;\\\\n\\\\t}\\\\n\\\\n\\\\tfunction getHeight() {\\\\n\\\\t\\\\t\\\\treturn 250;\\\\n\\\\t}\\\\n\\\\n\\\\tfunction getContent() {\\\\n\\\\t\\\\t\\\\treturn \\\\\\\"This is a RTB test Ad from Xandr. Your mediation setup didnot win this auction\\\\\\\";\\\\n\\\\t}\\\\n\\\\n\\\\tfunction clickThrough() {\\\\n      mraid.open(\\\\'"+UTConstants.REQUEST_BASE_URL_UT+"/click?AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIpsG17Ga1w1yGIw8wg0HXPu20BgAAAAABk8NgFuJwAAbicAAAAAAAAG3U4NAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAQMAAAAAAAEArgsySQAAAAA./bcr=AAAAAAAAAAA=/bn=0/test=1/clickenc=https://www.xandr.com\\\\');\\\\n\\\\t}\\\\n<\\\\/script>\\\\n');</script></body></html><script async=\\\"true\\\" src=\\\"https://acdn.adnxs.com/ij/static/34/disca.js#enc=Ce7bQGAAAAAAEYpsG17Ga1w1GO5OIO5OKJn42Akwhrq7ajhsQOkNSAFQAQ..\\\"></script><div name=\\\"anxv\\\" lnttag=\\\"app;tv=omd1-21hs;st=2;d=300x250;vc=iab;vid_ccr=1;tag_id=20331545;cb=https%3A%2F%2Fsin3-ib.adnxs.com%2Fvevent%3Fan_audit%3D0%26test%3D1%26e%3DwqT_3QLFA6DFAQAAAwDWAAUBCO63g4IGEIrZ7fDl-JquNRjIxcGZj4HNjnMqNgkAAAkCABEJBwgAABkRCQAhEQkAKREJADERCfDeMJn42Ak47k5A7k5IAFCGurtqWABgAGgAeACAAQCKAQCYAawCoAH6AagBAbABALgBAcABA8gBANABANgBAOABAfABANgCAOACAIADAIgDAZADAJgDF6ADAKoDAMADrALIAwDYAwDgAwDoAwL4AwCABACSBAYvdXQvdjOYBACoBACyBBAIABABGPcCIKwGKAAwADgCuAQAwAQAyAQA2gQCCAHgBAHwBACCBRtjb20uYXBwbmV4dXMuQXBwTmV4dXNTREtBcHCIBQGYBQCgBQDABQDJBQAAAAAAAPA_0gUJCRHnaNgFAOAFAPAFAPoFBAgAEACQBgCYBgC4BgDBBhEjENoGFgoQEQ0RAVwQABgA4AYA8gYCCACABwGIBwCgBwC6Bw4BREAYACAAMAA4AEAAyAcA0gcNCS41AEDaBwYIABAAGADgBwDqBwIIAA..%26s%3D43fe777de574d97b27f3546f595b79ed44c9285b;ts=1614863342;cet=0;cecb=\\\" width=\\\"0\\\" height=\\\"0\\\" style=\\\"display: block; margin: 0; padding: 0; height: 0; width: 0;\\\"><script type=\\\"text/javascript\\\" async=\\\"true\\\" src=\\\"https://cdn.adnxs.com/v/app/203/trk.js\\\"></script></div>\",\n" +
            "              \"width\": 300,\n" +
            "              \"height\": 250\n" +
            "            },\n" +
            "            \"trackers\": [\n" +
            "              {\n" +
            "                \"impression_urls\": [\n" +
            "                  \""+UTConstants.REQUEST_BASE_URL_UT+"/it?an_audit=0&test=1&e=wqT_3QLFA6DFAQAAAwDWAAUBCO63g4IGEIrZ7fDl-JquNRjIxcGZj4HNjnMqNgkAAAkCABEJBwgAABkRCQAhEQkAKREJADERCfDeMJn42Ak47k5A7k5IAFCGurtqWABgAGgAeACAAQCKAQCYAawCoAH6AagBAbABALgBAcABA8gBANABANgBAOABAfABANgCAOACAIADAIgDAZADAJgDF6ADAKoDAMADrALIAwDYAwDgAwDoAwL4AwCABACSBAYvdXQvdjOYBACoBACyBBAIABABGPcCIKwGKAAwADgCuAQAwAQAyAQA2gQCCAHgBAHwBACCBRtjb20uYXBwbmV4dXMuQXBwTmV4dXNTREtBcHCIBQGYBQCgBQDABQDJBQAAAAAAAPA_0gUJCRHnaNgFAOAFAPAFAPoFBAgAEACQBgCYBgC4BgDBBhEjENoGFgoQEQ0RAVwQABgA4AYA8gYCCACABwGIBwCgBwC6Bw4BREAYACAAMAA4AEAAyAcA0gcNCS41AEDaBwYIABAAGADgBwDqBwIIAA..&s=43fe777de574d97b27f3546f595b79ed44c9285b\"\n" +
            "                ],\n" +
            "                \"video_events\": {}\n" +
            "              }\n" +
            "            ]\n" +
            "          }\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}";
    public static final String NATIVE_AD = "{\n" +
            "    \"version\": \"3.0.0\",\n" +
            "    \"tags\": [\n" +
            "        {\n" +
            "            \"tag_id\": 15740033,\n" +
            "            \"auction_id\": \"3579836792830527402\",\n" +
            "            \"nobid\": false,\n" +
            "            \"no_ad_url\": \""+UTConstants.REQUEST_BASE_URL_UT+"/it?an_audit=0&referrer=itunes.apple.com%2Fus%2Fapp%2Fappnexus-sdk-app%2Fid736869833&e=wqT_3QKtCKAtBAAAAwDWAAUBCIG7rucFEKqPprSK04jXMRi1zICs5JfXxSYqNgkAAAkCABEJBywAABkAAABA4XqEPyEREgApEQkAMREb8JowgdnABzjuTkDuTkgAUABYzrNsYABopJOGAXgAgAEBigEAkgEDVVNEmAEBoAEBqAEBsAEAuAEBwAEAyAEC0AEA2AEA4AEA8AEAigJZdWYoJ2EnLCAzMDM4MzE0LCAxNTU4OTQ1MTUzKTt1ZignaScsIDEwMDg3NzgsIDE1NTg5NDUxNTMpO3VmKCdyJywgMTU0NDg1ODA3LCAxNRk88JqSApECIXJ6N1J3UWlIaS1FTkVLLUkxVWtZQUNET3Myd3dBRGdBUUFSSTdrNVFnZG5BQjFnQVlLVUZhQUJ3QUhnQWdBRUFpQUVBa0FFQm1BRUJvQUVCcUFFRHNBRUF1UUdSN3dydzRYcUVQOEVCa2U4SzhPRjZoRF9KQVNSRzBvREZWUUJBMlFFQUFBQUFBQUR3UC1BQmlzazk5UQkULG1BSUFvQUlBdFFJQQEBAHYNCJh3QUlBeUFJQTBBSUEyQUlBNEFJQTZBSUEtQUlBZ0FNQm1BTUJxQU8F1Oh1Z01KVTBsT01Ub3pOVGMxNEFQTURJQUU4OERrQVlnRTljRGtBWkFFQUpnRUFRLi6aAmEhT0JDSDVnaQVAMRRAenJOc0lBUW9BREY3Rks1SDQB2AR6bzJcABRRTXdNU1EBqhhBQUFQQV9VEQwMQUFBVx0M9DQB2AIA4ALKqE3qAjRpdHVuZXMuYXBwbGUuY29tL3VzL2FwcC9hcHBuZXh1cy1zZGstYXBwL2lkNzM2ODY5ODMzgAMAiAMBkAMAmAMXoAMBqgMAwAOsAsgDANIDKAgAEiQ1MzYzOTYzYi02MWVhLTRiZmUtYjczMS05ZGE1MGFhNTJhYmPSAygIChIkZWY1NDA0ZTQtM2NmOS00YzNiLTkzNTAtYjRhOWE5YzA0YjU12AP5o3rgAwDoAwL4AwCABACSBAYvdXQvdjOYBACiBAsxMC4xNC4xMi40NagEhOYisgQQCAAQARisAiD6ASgAMAA4ArgEAMAEAMgEANIEDzEwMDk0I1NJTjE6MzU3NdoEAggA4AQB8ASviNVJ-gQSCQAAAECW5EJAEQAAAMACml7AggUJNzM2CfwgiAUBmAUAoAX_EQEYAcAFAMkFAAUBFPA_0gUJCQULdAAAANgFAeAFAfAFAfoFBAgAEACQBgGYBgC4BgDBBgEfLAAA8L_IBgDaBhYKEAkQGQFEEAAYAOAGDPIGAggAgAcBiAcA&s=6f2fa33f3b83d94aacc2346e5345dead47934579\",\n" +
            "            \"timeout_ms\": 0,\n" +
            "            \"ad_profile_id\": 1266762,\n" +
            "            \"ads\": [\n" +
            "                {\n" +
            "                    \"content_source\": \"rtb\",\n" +
            "                    \"ad_type\": \"native\",\n" +
            "                    \"buyer_member_id\": 10094,\n" +
            "                    \"creative_id\": 154485807,\n" +
            "                    \"media_type_id\": 12,\n" +
            "                    \"media_subtype_id\": 65,\n" +
            "                    \"brand_category_id\": 0,\n" +
            "                    \"client_initiated_ad_counting\": true,\n" +
            "                    \"viewability\": {\n" +
            "                        \"config\": \"<script type=\\\"text/javascript\\\" async=\\\"true\\\" src=\\\"https://cdn.adnxs.com/v/app/166/trk.js#app;vk=appnexus.com-omid;tv=app-native-23h;dom_id=%native_dom_id%;st=2;d=1x1;vc=iab;vid_ccr=1;ab=10;tag_id=15740033;cb=https%3A%2F%2Fsin1-mobile.adnxs.com%2Fvevent%3Fan_audit%3D0%26referrer%3Ditunes.apple.com%252Fus%252Fapp%252Fappnexus-sdk-app%252Fid736869833%26e%3DwqT_3QKKCfA8igQAAAMA1gAFAQiBu67nBRCqj6a0itOI1zEYtcyArOSX18UmKjYJexSuR-F6hD8RexSuR-F6hD8ZAAAAQAESACERGwApEQkAMREbqDCB2cAHOO5OQO5OSAJQr4jVSVjOs2xgAGikk4YBeMukBYABAYoBA1VTRJIFBvBPmAEBoAEBqAEBsAEAuAEBwAEEyAEC0AEA2AEA4AEA8AEAigJZdWYoJ2EnLCAzMDM4MzE0LCAxNTU4OTQ1MTUzKTt1ZignaScsIDEwMDg3NzhGHQAEcicBFBg0NDg1ODA3AQsZPPCakgKRAiFyejdSd1FpSGktRU5FSy1JMVVrWUFDRE9zMnd3QURnQVFBUkk3azVRZ2RuQUIxZ0FZS1VGYUFCd0FIZ0FnQUVBaUFFQWtBRUJtQUVCb0FFQnFBRURzQUVBdVFHUjd3cnc0WHFFUDhFQmtlOEs4T0Y2aERfSkFTUkcwb0RGVlFCQTJRRUFBQUFBQUFEd1AtQUJpc2s5OVEJFCxtQUlBb0FJQXRRSUEBAQB2DQiYd0FJQXlBSUEwQUlBMkFJQTRBSUE2QUlBLUFJQWdBTUJtQU1CcUFPBdTodWdNSlUwbE9NVG96TlRjMTRBUE1ESUFFODhEa0FZZ0U5Y0RrQVpBRUFKZ0VBUS4umgJhIU9CQ0g1Z2kFQDEUQHpyTnNJQVFvQURGN0ZLNUg0AdgEem8yXAAUUU13TVNRAaoYQUFBUEFfVREMDEFBQVcdDPB52AIA4ALKqE3qAjRpdHVuZXMuYXBwbGUuY29tL3VzL2FwcC9hcHBuZXh1cy1zZGstYXBwL2lkNzM2ODY5ODMz8gITCg9DVVNUT01fTU9ERUxfSUQSAPICGgoWQ1VTVE9NX01PREVMX0xFQUZfTkFNRRIA8gIeChpDVVMZMwxMQVNUAT7w3klGSUVEEgCAAwCIAwGQAwCYAxegAwGqAwDAA-CoAcgDANIDKAgAEiQ1MzYzOTYzYi02MWVhLTRiZmUtYjczMS05ZGE1MGFhNTJhYmPSAygIChIkZWY1NDA0ZTQtM2NmOS00YzNiLTkzNTAtYjRhOWE5YzA0YjU12AP5o3rgAwDoAwL4AwCABACSBAYvdXQvdjOYBACiBAsxMC4xNC4xMi40NagEhOYisgQQCAAQARisAiD6ASgAMAA4ArgEAMAEAMgEANIEDzEwMDk0I1NJTjE6MzU3NdoEAggB4AQB8ARhgwz6BBIJYaFAluRCQBEAAADAAppewIIFCTcxUSCIBQGYBQCgBf8RARgBwAUAyQUABQEQ8D_SBQkBQAUBaNgFAeAFAfAFAfoFBAgAEACQBgGYBgC4BgDBBgUgKADwP8gGANoGFgoQCRAZAUQQABgA4AYM8gYCCACABwGIBwA.%26s%3D7cb63079a287dbd6550ba2e9c304839b9e8d479f;ts=1558945153;cet=0;cecb=\\\"></script>\"\n" +
            "                    },\n" +
            "                    \"rtb\": {\n" +
            "                        \"native\": {\n" +
            "                            \"title\": \"Native Renderer Title\",\n" +
            "                            \"desc\": \"Native Renderer Desc\",\n" +
            "                            \"sponsored\": \"Abhishek Sharma\",\n" +
            "                            \"ctatext\": \"NativeRendererCampaign\",\n" +
            "                            \"icon\": {\n" +
            "                                \"url\": \"https://www.keralagiftdelivery.com/images/gifts/G20101.jpg\",\n" +
            "                                \"width\": 868,\n" +
            "                                \"height\": 996,\n" +
            "                                \"prevent_crop\": false\n" +
            "                            },\n" +
            "                            \"main_img\": {\n" +
            "                                \"url\": \"https://www.keralagiftdelivery.com/images/gifts/G20101.jpg\",\n" +
            "                                \"width\": 868,\n" +
            "                                \"height\": 996,\n" +
            "                                \"prevent_crop\": false\n" +
            "                            },\n" +
            "                            \"link\": {\n" +
            "                                \"url\": \"https://appnexus.com\",\n" +
            "                                \"click_trackers\": [\n" +
            "                                    \""+UTConstants.REQUEST_BASE_URL_UT+"/click?exSuR-F6hD97FK5H4XqEPwAAAEDheoQ_exSuR-F6hD97FK5H4XqEP6qHiaaYIq4xNSaARb5ciyaBnetcAAAAAIEs8ABuJwAAbicAAAIAAAAvRDUJzhkbAAAAAABVU0QAVVNEAAEAAQCkiQAAAAABAQQCAAAAAMIAfSJ4VQAAAAA./cpcpm=AAAAAAAAAAA=/bcr=AAAAAAAA8D8=/cnd=%21OBCH5giHi-ENEK-I1UkYzrNsIAQoADF7FK5H4XqEPzoJU0lOMTozNTc1QMwMSQAAAAAAAPA_UQAAAAAAAAAAWQAAAAAAAAAA/cca=MTAwOTQjU0lOMTozNTc1/bn=86603/\"\n" +
            "                                ]\n" +
            "                            },\n" +
            "                            \"impression_trackers\": [\n" +
            "                                \""+UTConstants.REQUEST_BASE_URL_UT+"/it?an_audit=0&referrer=itunes.apple.com%2Fus%2Fapp%2Fappnexus-sdk-app%2Fid736869833&e=wqT_3QK2CPA8NgQAAAMA1gAFAQiBu67nBRCqj6a0itOI1zEYtcyArOSX18UmKjYJexSuR-F6hD8RexSuR-F6hD8ZAAAAQAESACERGwApEQkAMREbqDCB2cAHOO5OQO5OSAJQr4jVSVjOs2xgAGikk4YBeMukBYABAYoBA1VTRJIFBvBPmAEBoAEBqAEBsAEAuAEBwAEEyAEC0AEA2AEA4AEA8AEAigJZdWYoJ2EnLCAzMDM4MzE0LCAxNTU4OTQ1MTUzKTt1ZignaScsIDEwMDg3NzhGHQAEcicBFBg0NDg1ODA3AQsZPPCakgKRAiFyejdSd1FpSGktRU5FSy1JMVVrWUFDRE9zMnd3QURnQVFBUkk3azVRZ2RuQUIxZ0FZS1VGYUFCd0FIZ0FnQUVBaUFFQWtBRUJtQUVCb0FFQnFBRURzQUVBdVFHUjd3cnc0WHFFUDhFQmtlOEs4T0Y2aERfSkFTUkcwb0RGVlFCQTJRRUFBQUFBQUFEd1AtQUJpc2s5OVEJFCxtQUlBb0FJQXRRSUEBAQB2DQiYd0FJQXlBSUEwQUlBMkFJQTRBSUE2QUlBLUFJQWdBTUJtQU1CcUFPBdTodWdNSlUwbE9NVG96TlRjMTRBUE1ESUFFODhEa0FZZ0U5Y0RrQVpBRUFKZ0VBUS4umgJhIU9CQ0g1Z2kFQDEUQHpyTnNJQVFvQURGN0ZLNUg0AdgEem8yXAAUUU13TVNRAaoYQUFBUEFfVREMDEFBQVcdDPReAdgCAOACyqhN6gI0aXR1bmVzLmFwcGxlLmNvbS91cy9hcHAvYXBwbmV4dXMtc2RrLWFwcC9pZDczNjg2OTgzM4ADAIgDAZADAJgDF6ADAaoDAMAD4KgByAMA0gMoCAASJDUzNjM5NjNiLTYxZWEtNGJmZS1iNzMxLTlkYTUwYWE1MmFiY9IDKAgKEiRlZjU0MDRlNC0zY2Y5LTRjM2ItOTM1MC1iNGE5YTljMDRiNTXYA_mjeuADAOgDAvgDAIAEAJIEBi91dC92M5gEAKIECzEwLjE0LjEyLjQ1qASE5iKyBBAIABABGKwCIPoBKAAwADgCuAQAwAQAyAQA0gQPMTAwOTQjU0lOMTozNTc12gQCCAHgBAHwBK-I1Un6BBIJAAAAQJbkQkARAAAAwAKaXsCCBQk3MzY4Njk4MzOIBQGYBQCgBf___________wHABQDJBQAAAAAAAPA_0gUJCQULdAAAANgFAeAFAfAFAfoFBAgAEACQBgGYBgC4BgDBBgEfLAAA8D_IBgDaBhYKEAkQGQFEEAAYAOAGDPIGAggAgAcBiAcA&s=91d3af1f75b86ccc77cdf065374aa86b1385b11f\"\n" +
            "                            ],\n" +
            "                            \"id\": 154485807\n" +
            "                        }\n" +
            "                    }\n" +
            "                }\n" +
            "            ]\n" +
            "        }\n" +
            "    ]\n" +
            "}\n";

            
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
