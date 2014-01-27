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

public class TestResponses {

    public static final String RESULTCB = "http://result.com/";

    // template strings
    private static final String CLASSNAME = "com.appnexus.opensdk.testviews.%s";

    private static final String RESPONSE = "{\"status\":\"%s\",\"ads\": %s,\"mediated\": %s}";
    private static final String ADS = "[{ \"type\": \"%s\", \"width\": %d, \"height\": %d, \"content\": \"%s\" }]";
    private static final String MEDIATED = "[{ \"type\": \"%s\", \"width\": %d, \"height\": %d, \"content\": \"%s\" }]";
    private static final String MEDIATED_AD = "{\"type\":\"%s\",\"class\":\"%s\",\"param\":\"%s\",\"width\":\"%d\",\"height\":\"%d\",\"id\":\"%s\"}";
    private static final String MEDIATED_ARRAY_SINGLE_AD = "[{ \"handler\": [{\"type\":\"%s\",\"class\":\"%s\",\"param\":\"%s\",\"width\":\"%d\",\"height\":\"%d\",\"id\":\"%s\"}],\"result_cb\":\"%s\"}]";

    public static String blank() {
        return "";
    }

    public static String banner() {
        return templateAdsResponse("banner", 320, 50, "content");
    }

    public static String blankBanner() {
        return templateAdsResponse("banner", 320, 50, "");
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

    // templates

    public static String templateResponse(String status, String ads, String mediated) {
        return String.format(RESPONSE, status, ads, mediated);
    }

    public static String templateAdsResponse(String type, int width, int height, String content) {
        String ads = String.format(ADS, type, width, height, content);
        return templateResponse("ok", ads, null);
    }

    public static String templateSingleMediatedAdResponse(String className, String resultCB) {
        String mediatedAd = String.format(MEDIATED_ARRAY_SINGLE_AD, "android", className, "", 320, 50, "", resultCB);
        return templateResponse("ok", "[]", mediatedAd);
    }

    public static String templateSingleMediatedAdResponse(String type, String className, String param, int width, int height, String id, String resultCB) {
        String mediatedAd = String.format(MEDIATED_ARRAY_SINGLE_AD, type, className, param, width, height, id, resultCB);
        return templateResponse("ok", "[]", mediatedAd);
    }
}
