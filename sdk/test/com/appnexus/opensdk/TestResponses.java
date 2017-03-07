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

import com.appnexus.opensdk.shadows.ShadowSettings;
import com.appnexus.opensdk.util.TestUtil;

import java.util.ArrayList;

public class TestResponses {

    public static final String RESULTCB = "http://result.com/";

    // template strings
    private static final String CLASSNAME = "com.appnexus.opensdk.testviews.%s";

    //Cookie Strings
    public static final String UUID_COOKIE_1="uuid2=1263546692102051030; Path=/; Max-Age=7776000; Expires=Wed, 07-Dec-2025 16:23:26 GMT; Domain=.adnxs.com; HttpOnly";
    public static final String UUID_COOKIE_RESET="uuid2=-1; Path=/; Max-Age=314496000; Expires=Thu, 27-Aug-2026 18:28:50 GMT; Domain=.adnxs.com; HttpOnly";

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
        return templateSingleMediatedAdResponse(createClassName("NoFillView"), ShadowSettings.getBaseUrl());
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

}
