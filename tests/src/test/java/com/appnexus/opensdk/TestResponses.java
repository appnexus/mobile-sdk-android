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

import com.appnexus.opensdk.util.TestUtil;

import java.util.ArrayList;

/**
 * Used for mock responses
 */
public class TestResponses {

    public static final String RESULTCB = "http://result.com/";

    // template strings
    private static final String CLASSNAME = "com.appnexus.opensdk.testviews.%s";

    // impbus response

    private static final String RESPONSE = "{\"status\":\"%s\",\"ads\":%s,\"mediated\":%s,\"native\":%s,\"version\":%d}";
    private static final String ADS = "[{ \"type\": \"%s\", \"width\": %d, \"height\": %d, \"content\": \"%s\" }]";
    private static final String MEDIATED_AD = "{\"type\":\"%s\",\"class\":\"%s\",\"param\":\"%s\",\"width\":\"%d\",\"height\":\"%d\",\"id\":\"%s\"}";
    private static final String MEDIATED_ARRAY_SINGLE_AD = "[{ \"handler\": [{\"type\":\"%s\",\"class\":\"%s\",\"param\":\"%s\",\"width\":\"%d\",\"height\":\"%d\",\"id\":\"%s\"}],\"result_cb\":\"%s\"}]";
    private static final String HANDLER = "{ \"handler\": [%s],\"result_cb\":\"%s\"}";
    private static final String HANDLER_NO_RESULTCB = "{ \"handler\": [%s]}";
    private static final String MEDIATED_ARRAY = "[%s]";

    private static final String MRAID_CONTENT = "<script type=\\\"text/javascript\\\" src=\\\"mraid.js\\\"></script><script type=\\\"text/javascript\\\">document.write('<div style=\\\"background-color:#EF8200;height:1000px;width:1000px;\\\"><p>%s</p></div>');</script>";

    private static final String AN_NATIVE_RESPONSE = "[{\"type\":\"%s\",\"title\":\"%s\",\"description\":\"%s\",\"full_text\":\"%s\",\"context\":\"%s\",\"icon_img_url\":\"%s\",\"main_media\":%s,\"cta\":\"%s\",\"click_trackers\":[%s],\"impression_trackers\":[%s],\"rating\":%s,\"click_url\":\"%s\",\"click_fallback_url\":\"%s\",\"custom\":%s}]";
    private static final String NATIVE_MAIN_MEDIA = "[{\"url\":\"%s\",\"width\":%d,\"height\":%d,\"label\":\"default\"},{\"url\":\"%s\",\"width\":%d,\"height\":%d},{\"url\":\"%s\",\"width\":%d,\"height\":%d}]";
    private static final String NATIVE_RATING = "{\"value\":%.2f,\"scale\":%.2f}";
    private static final String EMPTY_ARRAY = "[]";
    private static final String STATUS_OK = "ok";
    private static final String STATUS_NO_BID = "no_bid";

    public static String blank() {
        return "";
    }

    public static String banner() {
        return templateAdsResponse("banner", 320, 50, "content");
    }

    public static String blankBanner() {
        return templateAdsResponse("banner", 320, 50, "");
    }

    public static String mraidBanner(String name) {
        return mraidBanner(name, 320, 50);
    }

    public static String mraidBanner(String name, int width, int height) {
        String content = String.format(MRAID_CONTENT, name);
        return templateAdsResponse("banner", width, height, content);
    }

    public static String mediatedSuccessfulBanner() {
        return templateSingleMediatedAdResponse(createClassName("SuccessfulBanner"), RESULTCB);
    }

    public static String mediatedFakeClass() {
        return templateSingleMediatedAdResponse(createClassName("FakeClass"), RESULTCB);
    }

    public static String mediatedDummyClass() {
        return templateSingleMediatedAdResponse(createClassName("DummyClass"), RESULTCB);
    }

    public static String mediatedNoRequest() {
        return templateSingleMediatedAdResponse(createClassName("NoRequestBannerView"), RESULTCB);
    }

    public static String mediatedOutOfMemory() {
        return templateSingleMediatedAdResponse(createClassName("OOMBannerView"), RESULTCB);
    }

    public static String mediatedNoFill() {
        return templateSingleMediatedAdResponse(createClassName("NoFillView"), RESULTCB);
    }

    public static String createClassName(String className) {
        return String.format(CLASSNAME, className);
    }

    public static String resultCB(int code) {
        return String.format(RESULTCB + "&reason=%d", code);
    }

    public static String waterfall(String[] classNames, String[] resultCBs) {
        if (classNames.length != resultCBs.length) {
            System.err.println("different numbers of class names and resultCBs");
            return "";
        }

        ArrayList<String> handlers = new ArrayList<String>(classNames.length);

        for (int i = 0; i < classNames.length; i++) {
            String[] mediatedAds = {templateMediatedAd(createClassName(classNames[i]))};
            String handler = templateHandlerFromMediatedAds(mediatedAds, resultCBs[i]);
            handlers.add(handler);
        }

        return templateMediatedResponseFromHandlers(handlers.toArray(new String[handlers.size()]));
    }

    public static String callbacks(int testNumber) {
        return templateSingleMediatedAdResponse("android", createClassName("CallbacksTestView"), "",
                320, 50, String.valueOf(testNumber), RESULTCB);
    }

    // templates

    public static String templateResponse(String status, String ads, String mediated, String nativeResponse, int version) {
        return String.format(RESPONSE, status, ads, mediated, nativeResponse, version);
    }

    public static String templateAdsResponse(String type, int width, int height, String content) {
        String ads = String.format(ADS, type, width, height, content);
        return templateResponse(STATUS_OK, ads, EMPTY_ARRAY, EMPTY_ARRAY, TestUtil.VERSION);
    }

    public static String templateSingleMediatedAdResponse(String className, String resultCB) {
        return templateSingleMediatedAdResponse("android", className, "", 320, 50, "", resultCB);
    }

    public static String templateSingleMediatedAdResponse(String type, String className, String param, int width, int height, String id, String resultCB) {
        String mediatedAd = String.format(MEDIATED_ARRAY_SINGLE_AD, type, className, param, width, height, id, resultCB);
        return templateResponse(STATUS_OK, EMPTY_ARRAY, mediatedAd, EMPTY_ARRAY, TestUtil.VERSION);
    }

    public static String templateMediatedAd(String className) {
        return templateMediatedAd("android", className, "", 320, 50, "");
    }

    public static String templateMediatedAd(String type, String className, String param, int width, int height, String id) {
        return String.format(MEDIATED_AD, type, className, param, width, height, id);
    }

    public static String templateHandlerFromMediatedAds(String[] mediatedAds, String resultCB) {
        StringBuilder sb = new StringBuilder();
        for (String mediatedAd : mediatedAds) {
            sb.append(mediatedAd).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);

        if (resultCB == null) return String.format(HANDLER_NO_RESULTCB, sb.toString());
        return String.format(HANDLER, sb.toString(), resultCB);
    }

    public static String templateMediatedResponseFromHandlers(String[] handlers) {
        StringBuilder sb = new StringBuilder();
        for (String handler : handlers) {
            sb.append(handler).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return templateResponse(STATUS_OK, EMPTY_ARRAY, String.format(MEDIATED_ARRAY, sb.toString()), null, TestUtil.VERSION);
    }

    public static String templateNativeResponse(String type, String title, String description, String full_text, String context,
                                                String icon, String main_media, String cta, String click_trackers,
                                                String imp_trackers, String rating, String click_url,
                                                String click_fallback_url, String custom) {
        return String.format(AN_NATIVE_RESPONSE, type, title, description, full_text, context, icon, main_media, cta, click_trackers, imp_trackers, rating, click_url, click_fallback_url, custom);


    }

    public static String templateNativeMainMedia(String url, int width, int height, String url2, int width2, int height2, String url3, int width3, int height3) {
        return String.format(NATIVE_MAIN_MEDIA, url, width, height, url2, width2, height2, url3, width3, height3);
    }

    public static String templateNativeRating(float value, float scale) {
        return String.format(NATIVE_RATING, value, scale);
    }

    public static String anNative() {
        String nativeResponse = templateNativeResponse("native", "test title", "test description", "full text", "newsfeed",
                "http://path_to_icon.com", templateNativeMainMedia("http://path_to_main.com", 300, 200, "http://path_to_main2.com", 50, 50, "http://path_to_main3.com", 250, 250),
                "install", "\"http://ib.adnxs.com/click...\"", "\"http://ib.adnxs.com/it...\"", templateNativeRating(4f, 5f), "http://www.appnexus.com", "http://www.google.com", "{\"key\":\"value\"}"
        );
        return templateResponse(STATUS_OK, EMPTY_ARRAY, EMPTY_ARRAY, nativeResponse, TestUtil.VERSION);
    }

    public static String UTV2() {
        return "{\"version\":\"0.0.1\",\"tags\":[{\"uuid\":\"null\",\"tag_id\":6061613,\"auction_id\":\"4029565918821536696\",\"no_ad_url\":\"http://nym1.ib.adnxs.com/it?e=wqT_3QKrBKgiAgAAAgDWAAUIhNbPtAUQuKeDs6ib-fU3GMvdqvrN3c_mVSABKi0JAAAABQIAEQUGDAAAABkBBRAAAPA_IQkJCAAAKREJ8HgwrfzxAji-B0C-B0gAUABY6og1YABo7KoDeACAAQGSAQNVU0SYAQGgAQGoAQGwAQC4AQDAAQDIAQDQAQLYARngAQDwAQCKAjp1ZignYScsIDY0NzcyNiwgMTQ1MjUzNDUzMik7dWYoJ3InLCAzNjc1MjgzMCwgMTQ1FR7wY5ICpQEhS3lMVkhRalEyTDBGRUw2Ynd4RVlBQ0RxaURVd0FEZ0FRQVJJdmdkUXJmenhBbGdBWU1zRWFBQndBSGdBZ0FFQWlBRUFrQUVCbUFFQm9BRUJxQUVEc0FFQXVRRUFBQUEBAxAwUU1FQgEJAQFETkVESkFmYWRvZUJqenYwXzJRESggRHdQLUFCQVBVCSxQSmdDaW9iY3lndy6aAh0ha3dhb1BnNqgA8JM2b2cxSUFRLtgC6AfgAs2NLYADAIgDAZADAJgDF6ADAaoDALADALgDAMADrALIAwDSAygIChIkYWMzOTQzMmMtNGFlZi00Y2E3LTg1M2YtMzU0NGVhYjA5ZGVl2AMA4AMA6AMA8AMA-AMAgAQAkgQGL3V0L3YymAQAogQKMTAuMS4xMi4xNagEp-wRsgQGCAAQBBgB&s=e80e5260b3cc1da2a9d26c3cdb32732bce7a3b3f\",\"timeout_ms\":2500,\"ads\":[{\"content_source\":\"rtb\",\"ad_type\":\"video\",\"notify_url\":\"http://nym1.ib.adnxs.com/vast_track/v2?info=SgAAAAIArgAFCQTrk1YAAAAAEbjTYIba5Os3GQTrk1YAAAAAIL6bwxEoADC-Bzi-B0CevSpI6opnUK388QJYAWICLS1oAXABeACAAQKIAQCQAQE.&event_type=1\",\"buyer_member_id\":958,\"creative_id\":36752830,\"media_type_id\":4,\"media_subtype_id\":64,\"renderer_url\":\"http://cdn.adnxs.com/renderer/video/ANOutstreamVideo.js\",\"renderer_id\":2,\"client_initiated_ad_counting\":true,\"rtb\":{\"video\":{\"duration_ms\":30000,\"playback_methods\":[\"auto_play_sound_off\"],\"frameworks\":[],\"content\":\"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\" standalone=\\\"yes\\\"?><VAST version=\\\"2.0\\\"><Ad id=\\\"36752830\\\"><Wrapper><AdSystem version=\\\"2.0\\\">adnxs</AdSystem><VASTAdTagURI><![CDATA[http://openad.tf1.fr/2/vast_preroll/@Bottom]]></VASTAdTagURI><Error><![CDATA[http://nym1.ib.adnxs.com/vast_track?e=wqT_3QK2BMAtAgAAAgDWAAUIhNbPtAUQuKeDs6ib-fU3GMvdqvrN3c_mVSABKi0JAAAAAAAANEARBQgMADRAGQkJCPA_IQkJCDRAKREJqDCt_PECOL4HQL4HSAJQvpvDEVjqiDVgAGjsqgN49tQDgAEBigEDVVNEkgEBBvBQmAEBoAEBqAEBsAEAuAEAwAEDyAEA0AEC2AEZ4AEA8AEAigI6dWYoJ2EnLCA2NDc3MjYsIDE0NTI1MzQ1MzIpO3VmKCdyJywgMzY3NTI4MzAsMh4A8GOSAqUBIUt5TFZIUWpRMkwwRkVMNmJ3eEVZQUNEcWlEVXdBRGdBUUFSSXZnZFFyZnp4QWxnQVlNc0VhQUJ3QUhnQWdBRUFpQUVBa0FFQm1BRUJvQUVCcUFFRHNBRUF1UUVBQUFBAQMQMFFNRUIBCQEBRE5FREpBZmFkb2VCanp2MF8yUREoIER3UC1BQkFQVQksUEpnQ2lvYmN5Z3cumgIdIWt3YW9QZzaoAPCTNm9nMUlBUS7YAugH4ALNjS2AAwCIAwGQAwCYAxegAwGqAwCwAwC4AwDAA5AcyAMA0gMoCAoSJGFjMzk0MzJjLTRhZWYtNGNhNy04NTNmLTM1NDRlYWIwOWRlZdgDAOADAOgDAPADAPgDAIAEAJIEBi91dC92MpgEAKIECjEwLjEuMTIuMTWoBKfsEbIEBggAEAQYAQ..&event=4&s=bd855d23ceecc8ceea4e900f5c0615482e15f86b]]></Error><Impression id=\\\"adnxs\\\"><![CDATA[http://nym1.ib.adnxs.com/vast_track?e=wqT_3QK2BMAtAgAAAgDWAAUIhNbPtAUQuKeDs6ib-fU3GMvdqvrN3c_mVSABKi0JAAAAAAAANEARBQgMADRAGQkJCPA_IQkJCDRAKREJqDCt_PECOL4HQL4HSAJQvpvDEVjqiDVgAGjsqgN49tQDgAEBigEDVVNEkgEBBvBQmAEBoAEBqAEBsAEAuAEAwAEDyAEA0AEC2AEZ4AEA8AEAigI6dWYoJ2EnLCA2NDc3MjYsIDE0NTI1MzQ1MzIpO3VmKCdyJywgMzY3NTI4MzAsMh4A8GOSAqUBIUt5TFZIUWpRMkwwRkVMNmJ3eEVZQUNEcWlEVXdBRGdBUUFSSXZnZFFyZnp4QWxnQVlNc0VhQUJ3QUhnQWdBRUFpQUVBa0FFQm1BRUJvQUVCcUFFRHNBRUF1UUVBQUFBAQMQMFFNRUIBCQEBRE5FREpBZmFkb2VCanp2MF8yUREoIER3UC1BQkFQVQksUEpnQ2lvYmN5Z3cumgIdIWt3YW9QZzaoAPCTNm9nMUlBUS7YAugH4ALNjS2AAwCIAwGQAwCYAxegAwGqAwCwAwC4AwDAA5AcyAMA0gMoCAoSJGFjMzk0MzJjLTRhZWYtNGNhNy04NTNmLTM1NDRlYWIwOWRlZdgDAOADAOgDAPADAPgDAIAEAJIEBi91dC92MpgEAKIECjEwLjEuMTIuMTWoBKfsEbIEBggAEAQYAQ..&event=9&s=bd855d23ceecc8ceea4e900f5c0615482e15f86b]]></Impression><Creatives><Creative id=\\\"8047\\\"><Linear><TrackingEvents><Tracking event=\\\"start\\\"><![CDATA[http://nym1.ib.adnxs.com/vast_track?e=wqT_3QK2BMAtAgAAAgDWAAUIhNbPtAUQuKeDs6ib-fU3GMvdqvrN3c_mVSABKi0JAAAAAAAANEARBQgMADRAGQkJCPA_IQkJCDRAKREJqDCt_PECOL4HQL4HSAJQvpvDEVjqiDVgAGjsqgN49tQDgAEBigEDVVNEkgEBBvBQmAEBoAEBqAEBsAEAuAEAwAEDyAEA0AEC2AEZ4AEA8AEAigI6dWYoJ2EnLCA2NDc3MjYsIDE0NTI1MzQ1MzIpO3VmKCdyJywgMzY3NTI4MzAsMh4A8GOSAqUBIUt5TFZIUWpRMkwwRkVMNmJ3eEVZQUNEcWlEVXdBRGdBUUFSSXZnZFFyZnp4QWxnQVlNc0VhQUJ3QUhnQWdBRUFpQUVBa0FFQm1BRUJvQUVCcUFFRHNBRUF1UUVBQUFBAQMQMFFNRUIBCQEBRE5FREpBZmFkb2VCanp2MF8yUREoIER3UC1BQkFQVQksUEpnQ2lvYmN5Z3cumgIdIWt3YW9QZzaoAPCTNm9nMUlBUS7YAugH4ALNjS2AAwCIAwGQAwCYAxegAwGqAwCwAwC4AwDAA5AcyAMA0gMoCAoSJGFjMzk0MzJjLTRhZWYtNGNhNy04NTNmLTM1NDRlYWIwOWRlZdgDAOADAOgDAPADAPgDAIAEAJIEBi91dC92MpgEAKIECjEwLjEuMTIuMTWoBKfsEbIEBggAEAQYAQ..&event=2&s=bd855d23ceecc8ceea4e900f5c0615482e15f86b]]></Tracking><Tracking event=\\\"firstQuartile\\\"><![CDATA[http://nym1.ib.adnxs.com/vast_track?e=wqT_3QK2BMAtAgAAAgDWAAUIhNbPtAUQuKeDs6ib-fU3GMvdqvrN3c_mVSABKi0JAAAAAAAANEARBQgMADRAGQkJCPA_IQkJCDRAKREJqDCt_PECOL4HQL4HSAJQvpvDEVjqiDVgAGjsqgN49tQDgAEBigEDVVNEkgEBBvBQmAEBoAEBqAEBsAEAuAEAwAEDyAEA0AEC2AEZ4AEA8AEAigI6dWYoJ2EnLCA2NDc3MjYsIDE0NTI1MzQ1MzIpO3VmKCdyJywgMzY3NTI4MzAsMh4A8GOSAqUBIUt5TFZIUWpRMkwwRkVMNmJ3eEVZQUNEcWlEVXdBRGdBUUFSSXZnZFFyZnp4QWxnQVlNc0VhQUJ3QUhnQWdBRUFpQUVBa0FFQm1BRUJvQUVCcUFFRHNBRUF1UUVBQUFBAQMQMFFNRUIBCQEBRE5FREpBZmFkb2VCanp2MF8yUREoIER3UC1BQkFQVQksUEpnQ2lvYmN5Z3cumgIdIWt3YW9QZzaoAPCTNm9nMUlBUS7YAugH4ALNjS2AAwCIAwGQAwCYAxegAwGqAwCwAwC4AwDAA5AcyAMA0gMoCAoSJGFjMzk0MzJjLTRhZWYtNGNhNy04NTNmLTM1NDRlYWIwOWRlZdgDAOADAOgDAPADAPgDAIAEAJIEBi91dC92MpgEAKIECjEwLjEuMTIuMTWoBKfsEbIEBggAEAQYAQ..&event=5&s=bd855d23ceecc8ceea4e900f5c0615482e15f86b]]></Tracking><Tracking event=\\\"midpoint\\\"><![CDATA[http://nym1.ib.adnxs.com/vast_track?e=wqT_3QK2BMAtAgAAAgDWAAUIhNbPtAUQuKeDs6ib-fU3GMvdqvrN3c_mVSABKi0JAAAAAAAANEARBQgMADRAGQkJCPA_IQkJCDRAKREJqDCt_PECOL4HQL4HSAJQvpvDEVjqiDVgAGjsqgN49tQDgAEBigEDVVNEkgEBBvBQmAEBoAEBqAEBsAEAuAEAwAEDyAEA0AEC2AEZ4AEA8AEAigI6dWYoJ2EnLCA2NDc3MjYsIDE0NTI1MzQ1MzIpO3VmKCdyJywgMzY3NTI4MzAsMh4A8GOSAqUBIUt5TFZIUWpRMkwwRkVMNmJ3eEVZQUNEcWlEVXdBRGdBUUFSSXZnZFFyZnp4QWxnQVlNc0VhQUJ3QUhnQWdBRUFpQUVBa0FFQm1BRUJvQUVCcUFFRHNBRUF1UUVBQUFBAQMQMFFNRUIBCQEBRE5FREpBZmFkb2VCanp2MF8yUREoIER3UC1BQkFQVQksUEpnQ2lvYmN5Z3cumgIdIWt3YW9QZzaoAPCTNm9nMUlBUS7YAugH4ALNjS2AAwCIAwGQAwCYAxegAwGqAwCwAwC4AwDAA5AcyAMA0gMoCAoSJGFjMzk0MzJjLTRhZWYtNGNhNy04NTNmLTM1NDRlYWIwOWRlZdgDAOADAOgDAPADAPgDAIAEAJIEBi91dC92MpgEAKIECjEwLjEuMTIuMTWoBKfsEbIEBggAEAQYAQ..&event=6&s=bd855d23ceecc8ceea4e900f5c0615482e15f86b]]></Tracking><Tracking event=\\\"thirdQuartile\\\"><![CDATA[http://nym1.ib.adnxs.com/vast_track?e=wqT_3QK2BMAtAgAAAgDWAAUIhNbPtAUQuKeDs6ib-fU3GMvdqvrN3c_mVSABKi0JAAAAAAAANEARBQgMADRAGQkJCPA_IQkJCDRAKREJqDCt_PECOL4HQL4HSAJQvpvDEVjqiDVgAGjsqgN49tQDgAEBigEDVVNEkgEBBvBQmAEBoAEBqAEBsAEAuAEAwAEDyAEA0AEC2AEZ4AEA8AEAigI6dWYoJ2EnLCA2NDc3MjYsIDE0NTI1MzQ1MzIpO3VmKCdyJywgMzY3NTI4MzAsMh4A8GOSAqUBIUt5TFZIUWpRMkwwRkVMNmJ3eEVZQUNEcWlEVXdBRGdBUUFSSXZnZFFyZnp4QWxnQVlNc0VhQUJ3QUhnQWdBRUFpQUVBa0FFQm1BRUJvQUVCcUFFRHNBRUF1UUVBQUFBAQMQMFFNRUIBCQEBRE5FREpBZmFkb2VCanp2MF8yUREoIER3UC1BQkFQVQksUEpnQ2lvYmN5Z3cumgIdIWt3YW9QZzaoAPCTNm9nMUlBUS7YAugH4ALNjS2AAwCIAwGQAwCYAxegAwGqAwCwAwC4AwDAA5AcyAMA0gMoCAoSJGFjMzk0MzJjLTRhZWYtNGNhNy04NTNmLTM1NDRlYWIwOWRlZdgDAOADAOgDAPADAPgDAIAEAJIEBi91dC92MpgEAKIECjEwLjEuMTIuMTWoBKfsEbIEBggAEAQYAQ..&event=7&s=bd855d23ceecc8ceea4e900f5c0615482e15f86b]]></Tracking><Tracking event=\\\"complete\\\"><![CDATA[http://nym1.ib.adnxs.com/vast_track?e=wqT_3QK2BMAtAgAAAgDWAAUIhNbPtAUQuKeDs6ib-fU3GMvdqvrN3c_mVSABKi0JAAAAAAAANEARBQgMADRAGQkJCPA_IQkJCDRAKREJqDCt_PECOL4HQL4HSAJQvpvDEVjqiDVgAGjsqgN49tQDgAEBigEDVVNEkgEBBvBQmAEBoAEBqAEBsAEAuAEAwAEDyAEA0AEC2AEZ4AEA8AEAigI6dWYoJ2EnLCA2NDc3MjYsIDE0NTI1MzQ1MzIpO3VmKCdyJywgMzY3NTI4MzAsMh4A8GOSAqUBIUt5TFZIUWpRMkwwRkVMNmJ3eEVZQUNEcWlEVXdBRGdBUUFSSXZnZFFyZnp4QWxnQVlNc0VhQUJ3QUhnQWdBRUFpQUVBa0FFQm1BRUJvQUVCcUFFRHNBRUF1UUVBQUFBAQMQMFFNRUIBCQEBRE5FREpBZmFkb2VCanp2MF8yUREoIER3UC1BQkFQVQksUEpnQ2lvYmN5Z3cumgIdIWt3YW9QZzaoAPCTNm9nMUlBUS7YAugH4ALNjS2AAwCIAwGQAwCYAxegAwGqAwCwAwC4AwDAA5AcyAMA0gMoCAoSJGFjMzk0MzJjLTRhZWYtNGNhNy04NTNmLTM1NDRlYWIwOWRlZdgDAOADAOgDAPADAPgDAIAEAJIEBi91dC92MpgEAKIECjEwLjEuMTIuMTWoBKfsEbIEBggAEAQYAQ..&event=8&s=bd855d23ceecc8ceea4e900f5c0615482e15f86b]]></Tracking><Tracking event=\\\"skip\\\"><![CDATA[http://nym1.ib.adnxs.com/vast_track?e=wqT_3QK2BMAtAgAAAgDWAAUIhNbPtAUQuKeDs6ib-fU3GMvdqvrN3c_mVSABKi0JAAAAAAAANEARBQgMADRAGQkJCPA_IQkJCDRAKREJqDCt_PECOL4HQL4HSAJQvpvDEVjqiDVgAGjsqgN49tQDgAEBigEDVVNEkgEBBvBQmAEBoAEBqAEBsAEAuAEAwAEDyAEA0AEC2AEZ4AEA8AEAigI6dWYoJ2EnLCA2NDc3MjYsIDE0NTI1MzQ1MzIpO3VmKCdyJywgMzY3NTI4MzAsMh4A8GOSAqUBIUt5TFZIUWpRMkwwRkVMNmJ3eEVZQUNEcWlEVXdBRGdBUUFSSXZnZFFyZnp4QWxnQVlNc0VhQUJ3QUhnQWdBRUFpQUVBa0FFQm1BRUJvQUVCcUFFRHNBRUF1UUVBQUFBAQMQMFFNRUIBCQEBRE5FREpBZmFkb2VCanp2MF8yUREoIER3UC1BQkFQVQksUEpnQ2lvYmN5Z3cumgIdIWt3YW9QZzaoAPCTNm9nMUlBUS7YAugH4ALNjS2AAwCIAwGQAwCYAxegAwGqAwCwAwC4AwDAA5AcyAMA0gMoCAoSJGFjMzk0MzJjLTRhZWYtNGNhNy04NTNmLTM1NDRlYWIwOWRlZdgDAOADAOgDAPADAPgDAIAEAJIEBi91dC92MpgEAKIECjEwLjEuMTIuMTWoBKfsEbIEBggAEAQYAQ..&event=3&s=bd855d23ceecc8ceea4e900f5c0615482e15f86b]]></Tracking></TrackingEvents><VideoClicks><ClickTracking id=\\\"adnxs\\\"><![CDATA[http://nym1.ib.adnxs.com/click?AAAAAAAANEAAAAAAAAA0QAAAAAAAAPA_AAAAAAAANEAAAAAAAAA0QLjTYIba5Os3y65K3-w-zVUE65NWAAAAAC1-XAC-AwAAvgMAAAIAAAC-zTACakQNAAAAAQBVU0QAVVNEAAEAAQBs1QAAduoBAAMAAQACGaYAXBm1ggAAAAA./cnd=%21kwaoPgjQ2L0FEL6bwxEY6og1IAQ./]]></ClickTracking></VideoClicks></Linear></Creative></Creatives></Wrapper></Ad></VAST>\"}}}]}]}";
    }

}
