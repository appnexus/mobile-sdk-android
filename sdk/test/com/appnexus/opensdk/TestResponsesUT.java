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

import java.util.ArrayList;


public class TestResponsesUT {

    public static final String RESPONSE_URL_PATH = "response_url?";
    public static final String NO_AD_URL_PATH = "no_ad?";
    public static final String SSM_URL_PATH = "ssm?";
    public static final String REQUEST_URL = "http://mobile.devnxs.net/request_url?";
    public static String NO_AD_URL = "http://mobile.devnxs.net/no_ad_url?";
    public static final String IMPRESSION_URL = "http://mobile.devnxs.net/impression_url?";
    public static String RESPONSE_URL = "";
    public static final String NO_BID_TRUE = "true";
    public static final String NO_BID_FALSE = "false";
    public static String SSM_URL = "http://nym1-mobile.adnxs.com/ssm";
    public static final String SSM_NO_URL = "";
    public static void setTestURL(String url){
        RESPONSE_URL = url+RESPONSE_URL_PATH;
        NO_AD_URL = url+NO_AD_URL_PATH;
        SSM_URL= url+SSM_URL_PATH;
    }

    public static final String DUMMY_BANNER_CONTENT = "<script type=\\\"text/javascript\\\">document.write('<div style=\\\"background-color:#EF8200;height:1000px;width:1000px;\\\"><p>%s</p></div>');</script>";
    private static final String AN_NATIVE_RESPONSE = "[{\"type\":\"%s\",\"title\":\"%s\",\"description\":\"%s\",\"full_text\":\"%s\",\"context\":\"%s\",\"icon_img_url\":\"%s\",\"main_media\":%s,\"cta\":\"%s\",\"click_trackers\":[%s],\"impression_trackers\":[%s],\"rating\":%s,\"click_url\":\"%s\",\"click_fallback_url\":\"%s\",\"sponsored_by\":\"%s\",\"custom\":%s}]";
    private static final String MRAID_CONTENT = "<script type=\\\"text/javascript\\\" src=\\\"mraid.js\\\"></script><script type=\\\"text/javascript\\\">document.write('<div style=\\\"background-color:#EF8200;height:1000px;width:1000px;\\\"><p>%s</p></div>');</script>";
    private static final String NATIVE_MAIN_MEDIA = "[{\"url\":\"%s\",\"width\":%d,\"height\":%d,\"label\":\"default\"},{\"url\":\"%s\",\"width\":%d,\"height\":%d},{\"url\":\"%s\",\"width\":%d,\"height\":%d}]";
    private static final String NATIVE_RATING = "{\"value\":%.2f,\"scale\":%.2f}";

    // template strings
    private static final String CLASSNAME = "com.appnexus.opensdk.testviews.%s";

    //Cookie Strings
    public static final String UUID_COOKIE_1 = "uuid2=1263546692102051030; Path=/; Max-Age=7776000; Expires=Wed, 07-Dec-2025 16:23:26 GMT; Domain=.adnxs.com; HttpOnly";
    public static final String UUID_COOKIE_RESET = "uuid2=-1; Path=/; Max-Age=314496000; Expires=Thu, 27-Aug-2026 18:28:50 GMT; Domain=.adnxs.com; HttpOnly";

    // UT Response - Template String
    public static final String RESPONSE = "{\"version\":\"0.0.1\",\"tags\":[{\"tag_id\":123456,\"auction_id\":\"123456789\",\"nobid\":\"%s\",\"no_ad_url\":\"%s\",\"timeout_ms\":10000,\"ad_profile_id\":98765,%s}]}";

    public static final String ADS = "\"ads\":[%s]";

    // Ad objects
    public static final String RTB_BANNER = "{\"content_source\":\"rtb\",\"ad_type\":\"banner\",\"buyer_member_id\":123,\"creative_id\":6332753,\"media_type_id\":1,\"media_subtype_id\":1,\"client_initiated_ad_counting\":true,\"rtb\":{\"banner\":{\"content\":\"%s\",\"width\":%d,\"height\":%d},\"trackers\":[{\"impression_urls\":[\"%s\"],\"video_events\":{}}]}}";
    public static final String CSM_BANNER = "{\"content_source\":\"csm\",\"ad_type\":\"banner\",\"buyer_member_id\":123,\"creative_id\":44863345,\"media_type_id\":1,\"media_subtype_id\":1,\"client_initiated_ad_counting\":false,\"csm\":{\"banner\":{\"content\":\"%s\",\"width\":10,\"height\":10},\"timeout_ms\":500,\"handler\":[{\"param\":\"%s\",\"height\":\"%d\",\"width\":\"%d\",\"id\":\"%s\",\"type\":\"%s\",\"class\":\"%s\"},{\"param\":\"#{PARAM}\",\"height\":\"50\",\"width\":\"320\",\"id\":\"163441140754789_163441480754755\",\"type\":\"ios\",\"class\":\"DummyIOSClass\"}],\"trackers\":[{\"impression_urls\":[\"%s\"],\"video_events\":{}}],\"request_url\":\"%s\",\"response_url\":\"%s\"}}";
    public static final String SSM_BANNER = "{\"content_source\":\"ssm\",\"ad_type\":\"banner\",\"buyer_member_id\":123,\"creative_id\":44863345,\"media_type_id\":1,\"media_subtype_id\":1,\"client_initiated_ad_counting\":false,\"ssm\":{\"banner\":{\"content\":\"%s\",\"width\":10,\"height\":10},\"timeout_ms\":500,\"handler\":[{\"url\":\"%s\"}],\"trackers\":[{\"impression_urls\":[\"%s\"],\"video_events\":{}}],\"request_url\":\"%s\",\"response_url\":\"%s\"}}";
    public static final String RTB_NATIVE = "{\"content_source\":\"rtb\",\"ad_type\":\"native\",\"buyer_member_id\":958,\"creative_id\":47772560,\"media_type_id\":12,\"media_subtype_id\":65,\"client_initiated_ad_counting\":true,\"rtb\":{\"native\":{\"status\":\"ok\",\"version\":1,\"ads\":[],\"mediated\":[],\"native\":%s}}}";
    public static final String CSM_NATIVE = "{\"content_source\":\"csm\",\"ad_type\":\"native\",\"buyer_member_id\":958,\"creative_id\":44863492,\"media_type_id\":12,\"media_subtype_id\":65,\"client_initiated_ad_counting\":true,\"csm\": {\"timeout_ms\":500,\"handler\": [{\"type\": \"android\",\"class\": \"%s\",\"param\": \"%s\",\"id\": \"%s\"},{\"type\": \"ios\",\"class\": \"DummyIOSClass\",\"param\": \"#{PARAM}\",\"id\": \"210827375150_10154672419150151\"}],\"request_url\": \"%s\",\"response_url\": \"%s\"}}";
    public static final String NO_BID = "{\"version\":\"0.0.1\",\"tags\":[{\"tag_id\":123456789,\"auction_id\":\"3552547938089377051000000\",\"nobid\":true,\"ad_profile_id\":2707239}]}";

    public static String blank() {
        return "";
    }

    public static String noResponse() {
        return "{\"version\":\"0.0.1\",\"tags\":[{\"tag_id\":123456,\"auction_id\":\"1234567890\",\"nobid\":true,\"ad_profile_id\":98765}]}";

    }

    public static String banner() {
        String bannerContent = String.format(DUMMY_BANNER_CONTENT, "Test Banner Content");
        return templateBannerRTBAdsResponse(bannerContent, 320, 50, IMPRESSION_URL);
    }

    public static String blankBanner() {
        return templateBannerRTBAdsResponse("", 320, 50, IMPRESSION_URL);
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

    public static String mediatedSSMBanner(){
        return templateSingleSSMAdResponse();
    }

    public static String mediatedNoSSMBanner(){
        return templateNoURLSSMResponse();
    }

    public static String noFillCSM_RTBBanner(){
        //Create a CSM - Ad
        String csmAd = templateSingleCSMAdResponseBannerInterstitial(createClassName("MediatedBannerNoFillView"),320,50,IMPRESSION_URL,REQUEST_URL,RESPONSE_URL,"","","android");

        // Create a RTB Banner Ad
        String bannerContent = String.format(DUMMY_BANNER_CONTENT, "Test Banner Content");
        String bannerAd = singleRTBBanner(bannerContent, 320, 50, IMPRESSION_URL);

        ArrayList<String> adsArray = new ArrayList<String>(2);
        adsArray.add(csmAd);
        adsArray.add(bannerAd);

        //Return a WaterFall response
        return templateMediatedWaterFallResponses(adsArray.toArray(new String[adsArray.size()]));
    }

    public static String noFillCSM_RTBInterstitial(){
        //Create a CSM - Ad
        String csmAd = templateSingleCSMAdResponseBannerInterstitial(createClassName("MediatedInterstitialNoFillView"),320,480,IMPRESSION_URL,REQUEST_URL,RESPONSE_URL,"","","android");

        // Create a RTB Banner Ad
        String bannerConetent = String.format(DUMMY_BANNER_CONTENT, "Test Banner Content");
        String bannerAd = singleRTBBanner(bannerConetent, 320, 480, IMPRESSION_URL);

        ArrayList<String> adsArray = new ArrayList<String>(2);
        adsArray.add(csmAd);
        adsArray.add(bannerAd);

        //Return a WaterFall response
        return templateMediatedWaterFallResponses(adsArray.toArray(new String[adsArray.size()]));
    }


    public static String noFillCSM_RTBNative(){
        //Create a CSM - Ad
        String csmAd = templateSingleCSMAdResponseNative(createClassName("MediatedNativeNoFill"),"","",REQUEST_URL,RESPONSE_URL);

        // Create a RTB Banner Ad
        String nativeResponse = templateNativeResponse("native", "test title", "test description", "full text", "newsfeed",
                "http://path_to_icon.com", templateNativeMainMedia("http://path_to_main.com", 300, 200, "http://path_to_main2.com", 50, 50, "http://path_to_main3.com", 250, 250),
                "install", "\"http://ib.adnxs.com/click...\"", "\"http://ib.adnxs.com/it...\"", templateNativeRating(4f, 5f), "http://www.appnexus.com", "http://www.google.com", "test sponsored by","{\"key\":\"value\"}"
        );
        System.out.println(nativeResponse+"\n");
        String nativeRTB= String.format(RTB_NATIVE,nativeResponse);

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
            String singleCSMAd = templateSingleCSMAdResponseBannerInterstitial(createClassName(classNames[i]),320,50,IMPRESSION_URL,REQUEST_URL,responseURLS[i],"","","android");
            adsArray.add(singleCSMAd);
        }

        return templateMediatedWaterFallResponses(adsArray.toArray(new String[adsArray.size()]));
    }


    // Just take in count here since SSM waterfall can be controlled by altering the response for  handler URL response.
    public static String waterfall_SSM_Banner_Interstitial(int count) {

        ArrayList<String> adsArray = new ArrayList<String>(count);

        for (int i = 0; i < count; i++) {
            String ssmAdTag = String.format(SSM_BANNER, DUMMY_BANNER_CONTENT, SSM_URL, IMPRESSION_URL,REQUEST_URL,RESPONSE_URL);
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
            String singleCSMAd = templateSingleCSMAdResponseNative(createClassName(classNames[i]),"","",REQUEST_URL,responseURLS[i]);
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
        return templateSingleCSMAdResponseBannerInterstitial(createClassName("MediatedBannerCallbacksTestView"),RESPONSE_URL,String.valueOf(testNumber));
    }


    public static String anNative() {
        String nativeResponse = templateNativeResponse("native", "test title", "test description", "full text", "newsfeed",
                "http://path_to_icon.com", templateNativeMainMedia("http://path_to_main.com", 300, 200, "http://path_to_main2.com", 50, 50, "http://path_to_main3.com", 250, 250),
                "install", "\"http://ib.adnxs.com/click...\"", "\"http://ib.adnxs.com/it...\"", templateNativeRating(4f, 5f), "http://www.appnexus.com", "http://www.google.com", "test sponsored by","{\"key\":\"value\"}"
        );
        System.out.println(nativeResponse+"\n");
        String nativeRTB= String.format(RTB_NATIVE,nativeResponse);
        System.out.println(nativeRTB+"\n");
        String ads = String.format(ADS, nativeRTB);
        System.out.println(ads+"\n");
        return templateResponse(NO_BID_FALSE, NO_AD_URL, ads);
    }


    // templates

    private static String templateBannerRTBAdsResponse(String content, int width, int height, String impressionURL) {
        String rtbBanner = singleRTBBanner(content, width, height, impressionURL);
        String ads = String.format(ADS, rtbBanner);
        return templateResponse(NO_BID_FALSE, NO_AD_URL, ads);
    }


    private static String singleRTBBanner(String content, int width, int height, String impressionURL){
        return (String.format(RTB_BANNER, content, width, height, impressionURL));
    }


    private static String templateResponse(String noBid, String noAdURL, String ads) {
        System.out.println(String.format(RESPONSE, noBid, noAdURL, ads));
        return String.format(RESPONSE, noBid, noAdURL, ads);
    }

    private static String templateSingleCSMAdResponseBannerInterstitial(String className, String response_url) {
        String csmBanner = templateSingleCSMAdResponseBannerInterstitial(className,320,50,IMPRESSION_URL,REQUEST_URL,response_url,"","","android");
        return templateMediatedAdResponse(csmBanner);
    }

    public static String templateSingleCSMAdResponseBannerInterstitial(String className, String response_url, String id) {
        String csmBanner = templateSingleCSMAdResponseBannerInterstitial(className,320,50,IMPRESSION_URL,REQUEST_URL,response_url,"",id,"android");
        return templateMediatedAdResponse(csmBanner);
    }

    private static String templateSingleCSMAdResponseNative(String className, String response_url) {
        String csmNative = templateSingleCSMAdResponseNative(className,"abc","1234",REQUEST_URL,response_url);
        return templateMediatedAdResponse(csmNative);
    }

    private static String templateSingleSSMAdResponse() {
        String ssmAdTag = String.format(SSM_BANNER, DUMMY_BANNER_CONTENT, SSM_URL, IMPRESSION_URL,REQUEST_URL,RESPONSE_URL);
        String ads = String.format(ADS, ssmAdTag);
        return String.format(RESPONSE, NO_BID_FALSE, NO_AD_URL, ads);
    }

    private static String templateNoURLSSMResponse() {
        String ssmAdTag = String.format(SSM_BANNER, DUMMY_BANNER_CONTENT, SSM_NO_URL, IMPRESSION_URL,REQUEST_URL,RESPONSE_URL);
        String ads = String.format(ADS, ssmAdTag);
        return String.format(RESPONSE, NO_BID_FALSE, NO_AD_URL, ads);
    }


    private static String templateSingleCSMAdResponseBannerInterstitial(String className, int width, int height, String impressionURL, String request_url, String response_url, String params, String id, String type){
        return String.format(CSM_BANNER, DUMMY_BANNER_CONTENT, params,height, width,id,type, className, impressionURL, request_url, response_url);
    }

    private static String templateSingleCSMAdResponseNative(String className, String params, String id, String request_url, String response_url){
        return String.format(CSM_NATIVE, className, params,id,request_url, response_url);
    }


    private static String templateMediatedAdResponse(String adsArray) {
        String ads = String.format(ADS, adsArray);
        return templateResponse(NO_BID_FALSE, NO_AD_URL, ads);
    }


    private static String templateNativeResponse(String type, String title, String description, String full_text, String context,
                                                String icon, String main_media, String cta, String click_trackers,
                                                String imp_trackers, String rating, String click_url,
                                                String click_fallback_url, String sponsored_by, String custom) {
        return String.format(AN_NATIVE_RESPONSE, type, title, description, full_text, context, icon, main_media, cta, click_trackers, imp_trackers, rating, click_url, click_fallback_url, sponsored_by, custom);


    }

    private static String templateNativeMainMedia(String url, int width, int height, String url2, int width2, int height2, String url3, int width3, int height3) {
        return String.format(NATIVE_MAIN_MEDIA, url, width, height, url2, width2, height2, url3, width3, height3);
    }

    private static String templateNativeRating(float value, float scale) {
        return String.format(NATIVE_RATING, value, scale);
    }


}
