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
package com.appnexus.opensdk.instreamvideo;


public class TestUTResponses {

    public static String NO_AD_URL = "http://mobile.devnxs.net/no_ad_url?";
    public static final String NO_BID_FALSE = "false";
    public static final String NO_BID = "{\"version\":\"3.0.0\",\"tags\":[{\"tag_id\":123456789,\"auction_id\":\"3552547938089377051000000\",\"nobid\":true,\"ad_profile_id\":2707239}]}";

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



    // UT Response - Template String
    public static final String RESPONSE = "{\"version\":\"0.0.1\",\"tags\":[{\"tag_id\":123456,\"auction_id\":\"123456789\",\"nobid\":\"%s\",\"no_ad_url\":\"%s\",\"timeout_ms\":10000,\"ad_profile_id\":98765,%s}]}";

    public static final String ADS = "\"ads\":[%s]";


    public static final String RTB_VIDEO = "{\"cpm\":0.000010,\"cpm_publisher_currency\":0.000010,\"publisher_currency_code\":\"$\",\"content_source\":\"rtb\",\"ad_type\":\"video\",\"buyer_member_id\":123,\"creative_id\":6332753,\"media_type_id\":4,\"media_subtype_id\":64,\"client_initiated_ad_counting\":true,\"rtb\":{\"video\":{\"content\":\"%s\",\"duration_ms\":100}}}";

    public static String blank() {
        return "";
    }


    public static String video() {
        return templateVideoRTBAdsResponse(DUMMY_VIDEO_CONTENT);
    }


    private static String templateVideoRTBAdsResponse(String content) {
        String rtbVideo = singleRTBVideo(content);
        String ads = String.format(ADS, rtbVideo);
        return templateResponse(NO_BID_FALSE, NO_AD_URL, ads);
    }




    private static String singleRTBVideo(String content){
        return (String.format(RTB_VIDEO, content));
    }


    private static String templateResponse(String noBid, String noAdURL, String ads) {
        System.out.println(String.format(RESPONSE, noBid, noAdURL, ads));
        return String.format(RESPONSE, noBid, noAdURL, ads);
    }



}
