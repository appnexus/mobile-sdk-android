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

package com.appnexus.opensdk.instreamvideo.shadows;

import android.os.Build;
import android.webkit.WebView;

import com.appnexus.opensdk.instreamvideo.util.TestUtil;
import com.appnexus.opensdk.utils.Clog;

import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowWebView;

@Implements(value = WebView.class, callThroughByDefault = true)
public class ShadowCustomWebView extends ShadowWebView {

    public static String aspectRatio = "";
    private WebView webView;
    private static final String AD_READY_CONSTANT = "{\"event\":\"adReady\",\"params\":{\"aspectRatio\":\"aspect_ratio\",\"creativeUrl\":\"http://vcdn.adnxs.com/p/creative-video/ef/a6/d0/bb/efa6d0bb-8c19-44a8-b140-4b0bc2e02087/efa6d0bb-8c19-44a8-b140-4b0bc2e02087_768_432_500k.mp4\",\"duration\":145000,\"vastXML\":\"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\" standalone=\\\"yes\\\"?><VAST version=\\\"2.0\\\"><Ad id=\\\"85346399\\\"><InLine><AdSystem>adnxs</AdSystem><AdTitle><![CDATA[KungfuPandaWithAudio.mp4]]></AdTitle><Error><![CDATA[http://nym1-mobile.adnxs.com/vast_track/v2?info=YQAAAAMArgAFAQmbpXNaAAAAABEs1CgjIW1nQRmbpXNaAAAAACDfkNkoKAAwvgc4vgdAyOc9SIzyyQFQwsv8BVgBYgItLWgBcAF4AIABAIgBAJABAJgBAKABAKgB35DZKA..&event_type=4&error_code=[ERRORCODE]]]></Error><Impression id=\\\"adnxs\\\"><![CDATA[http://nym1-mobile.adnxs.com/it?e=wqT_3QLCBuhCAwAAAwDWAAUBCJvLztMFEKyoo5mSpNuzQRiW-5rG4aOM2yYqNgl7FK5H4XqEPxF7FK5H4XqEPxkAAAECCPA_IREbACkRCQAxARmwAADgPzDCy_wFOL4HQL4HSAJQ35DZKFjLu05gAGiRQHjU3ASAAQGKAQNVU0SSBQbwUJgBAaABAagBAbABALgBA8ABBMgBAtABANgBAOABAPABAIoCO3VmKCdhJywgMTc5Nzg2NSwgMTUxNzUyODQ3NSk7dWYoJ3InLCA4NTM0NjM5OTYeAPCKkgL5ASFFRG41VndpNDE3WUpFTi1RMlNnWUFDREx1MDR3QURnQVFBUkl2Z2RRd3N2OEJWZ0FZTVlGYUFCd0huZ0FnQUZraUFFQWtBRUJtQUVCb0FFQnFBRURzQUVBdVFHUjd3cnc0WHFFUDhFQmtlOEs4T0Y2aERfSkFkdzZFXzYtaTlNXzJRRUFBQQEDJER3UC1BQkFQVUIBDixBSmdDQUtBQ0FMVUMFEARMMAkI8FBNQUNBTWdDQU9BQ0FPZ0NBUGdDQUlBREFaQURBSmdEQWFnRHVOZTJDYm9ERW5GMVlXNTBhV3hsSTA1WlRUSTZOREEwTncuLpoCOSFFQTJMSHc2_AD0HAF5N3RPSUFRb0FEb1NjWFZoYm5ScGJHVWpUbGxOTWpvME1EUTPSAgc4NjI5MDA12ALoB-ACx9MBgAMBiAMBkAMAmAMXoAMBqgMAwAOQHMgDANIDKAgKEiQ2MzFiNjg5Ni0xOGM0LTRlYjktYTgzNC02NzVkYmE4NjQ4OGLYAwDgAwDoAwL4AwCABACSBAYvdXQvdjKYBACiBAsxMC4xLjEzLjE2MqgEiE-yBAwIABABGAAgADAAOAK4BADABADIBADSBBJxdWFudGlsZSNOWU0yOjQwNDfaBAIIAOAEAPAE35DZKIIFIGNvbS5hcHBuZXh1cy5leGFtcGxlLnNpbXBsZXZpZGVviAUBmAUAoAX___________8BwAUAyQVJxxTwP9IFCQkJDFAAANgFAeAFAfAFAfoFBAgAEACQBgA.&s=c4b9cbcd5ec79f8d24a5a1f9aba4e278dbe4c671]]></Impression><Creatives><Creative id=\\\"49362\\\" AdID=\\\"85346399\\\"><Linear><Duration>00:02:25</Duration><TrackingEvents><Tracking event=\\\"start\\\"><![CDATA[http://nym1-mobile.adnxs.com/vast_track/v2?info=YQAAAAMArgAFAQmbpXNaAAAAABEs1CgjIW1nQRmbpXNaAAAAACDfkNkoKAAwvgc4vgdAyOc9SIzyyQFQwsv8BVgBYgItLWgBcAF4AIABAIgBAJABAJgBAKABAKgB35DZKA..&event_type=2]]></Tracking><Tracking event=\\\"skip\\\"><![CDATA[http://nym1-mobile.adnxs.com/vast_track/v2?info=YQAAAAMArgAFAQmbpXNaAAAAABEs1CgjIW1nQRmbpXNaAAAAACDfkNkoKAAwvgc4vgdAyOc9SIzyyQFQwsv8BVgBYgItLWgBcAF4AIABAIgBAJABAJgBAKABAKgB35DZKA..&event_type=3]]></Tracking><Tracking event=\\\"firstQuartile\\\"><![CDATA[http://nym1-mobile.adnxs.com/vast_track/v2?info=YQAAAAMArgAFAQmbpXNaAAAAABEs1CgjIW1nQRmbpXNaAAAAACDfkNkoKAAwvgc4vgdAyOc9SIzyyQFQwsv8BVgBYgItLWgBcAF4AIABAIgBAJABAJgBAKABAKgB35DZKA..&event_type=5]]></Tracking><Tracking event=\\\"midpoint\\\"><![CDATA[http://nym1-mobile.adnxs.com/vast_track/v2?info=YQAAAAMArgAFAQmbpXNaAAAAABEs1CgjIW1nQRmbpXNaAAAAACDfkNkoKAAwvgc4vgdAyOc9SIzyyQFQwsv8BVgBYgItLWgBcAF4AIABAIgBAJABAJgBAKABAKgB35DZKA..&event_type=6]]></Tracking><Tracking event=\\\"thirdQuartile\\\"><![CDATA[http://nym1-mobile.adnxs.com/vast_track/v2?info=YQAAAAMArgAFAQmbpXNaAAAAABEs1CgjIW1nQRmbpXNaAAAAACDfkNkoKAAwvgc4vgdAyOc9SIzyyQFQwsv8BVgBYgItLWgBcAF4AIABAIgBAJABAJgBAKABAKgB35DZKA..&event_type=7]]></Tracking><Tracking event=\\\"complete\\\"><![CDATA[http://nym1-mobile.adnxs.com/vast_track/v2?info=YQAAAAMArgAFAQmbpXNaAAAAABEs1CgjIW1nQRmbpXNaAAAAACDfkNkoKAAwvgc4vgdAyOc9SIzyyQFQwsv8BVgBYgItLWgBcAF4AIABAIgBAJABAJgBAKABAKgB35DZKA..&event_type=8]]></Tracking></TrackingEvents><VideoClicks><ClickThrough><![CDATA[https://www.appnexus.com]]></ClickThrough><ClickTracking id=\\\"adnxs\\\"><![CDATA[http://nym1-mobile.adnxs.com/click?exSuR-F6hD97FK5H4XqEPwAAAAAAAPA_exSuR-F6hD97FK5H4XqEPyzUKCMhbWdBlr3GGB4xtiabpXNaAAAAAMIlvwC-AwAAvgMAAAIAAABfSBYFy50TAAAAAABVU0QAVVNEAAEAAQARIAAAAAABAwQCAAAAAAAAHh-RdAAAAAA./cnd=%21EA2LHwi417YJEN-Q2SgYy7tOIAQoADoScXVhbnRpbGUjTllNMjo0MDQ3/bn=77396/]]></ClickTracking></VideoClicks><MediaFiles><MediaFile id=\\\"612525\\\" delivery=\\\"progressive\\\" type=\\\"video/x-flv\\\" width=\\\"768\\\" height=\\\"432\\\" scalable=\\\"true\\\" bitrate=\\\"500\\\" maintainAspectRatio=\\\"true\\\"><![CDATA[http://vcdn.adnxs.com/p/creative-video/ef/a6/d0/bb/efa6d0bb-8c19-44a8-b140-4b0bc2e02087/efa6d0bb-8c19-44a8-b140-4b0bc2e02087_768_432_500k.flv]]></MediaFile><MediaFile id=\\\"612526\\\" delivery=\\\"progressive\\\" type=\\\"video/mp4\\\" width=\\\"768\\\" height=\\\"432\\\" scalable=\\\"true\\\" bitrate=\\\"1700\\\" maintainAspectRatio=\\\"true\\\"><![CDATA[http://vcdn.adnxs.com/p/creative-video/ef/a6/d0/bb/efa6d0bb-8c19-44a8-b140-4b0bc2e02087/efa6d0bb-8c19-44a8-b140-4b0bc2e02087_768_432_1700k.mp4]]></MediaFile><MediaFile id=\\\"612527\\\" delivery=\\\"progressive\\\" type=\\\"video/mp4\\\" width=\\\"768\\\" height=\\\"432\\\" scalable=\\\"true\\\" bitrate=\\\"500\\\" maintainAspectRatio=\\\"true\\\"><![CDATA[http://vcdn.adnxs.com/p/creative-video/ef/a6/d0/bb/efa6d0bb-8c19-44a8-b140-4b0bc2e02087/efa6d0bb-8c19-44a8-b140-4b0bc2e02087_768_432_500k.mp4]]></MediaFile><MediaFile id=\\\"612528\\\" delivery=\\\"progressive\\\" type=\\\"video/webm\\\" width=\\\"1280\\\" height=\\\"720\\\" scalable=\\\"true\\\" bitrate=\\\"2000\\\" maintainAspectRatio=\\\"true\\\"><![CDATA[http://vcdn.adnxs.com/p/creative-video/ef/a6/d0/bb/efa6d0bb-8c19-44a8-b140-4b0bc2e02087/efa6d0bb-8c19-44a8-b140-4b0bc2e02087_1280_720_2000k.webm]]></MediaFile><MediaFile id=\\\"612529\\\" delivery=\\\"progressive\\\" type=\\\"video/x-flv\\\" width=\\\"768\\\" height=\\\"432\\\" scalable=\\\"true\\\" bitrate=\\\"1100\\\" maintainAspectRatio=\\\"true\\\"><![CDATA[http://vcdn.adnxs.com/p/creative-video/ef/a6/d0/bb/efa6d0bb-8c19-44a8-b140-4b0bc2e02087/efa6d0bb-8c19-44a8-b140-4b0bc2e02087_768_432_1100k.flv]]></MediaFile><MediaFile id=\\\"612530\\\" delivery=\\\"progressive\\\" type=\\\"video/webm\\\" width=\\\"1280\\\" height=\\\"720\\\" scalable=\\\"true\\\" bitrate=\\\"600\\\" maintainAspectRatio=\\\"true\\\"><![CDATA[http://vcdn.adnxs.com/p/creative-video/ef/a6/d0/bb/efa6d0bb-8c19-44a8-b140-4b0bc2e02087/efa6d0bb-8c19-44a8-b140-4b0bc2e02087_1280_720_600k.webm]]></MediaFile><MediaFile id=\\\"612531\\\" delivery=\\\"progressive\\\" type=\\\"video/webm\\\" width=\\\"768\\\" height=\\\"432\\\" scalable=\\\"true\\\" bitrate=\\\"500\\\" maintainAspectRatio=\\\"true\\\"><![CDATA[http://vcdn.adnxs.com/p/creative-video/ef/a6/d0/bb/efa6d0bb-8c19-44a8-b140-4b0bc2e02087/efa6d0bb-8c19-44a8-b140-4b0bc2e02087_768_432_500k.webm]]></MediaFile><MediaFile id=\\\"612532\\\" delivery=\\\"progressive\\\" type=\\\"video/mp4\\\" width=\\\"1280\\\" height=\\\"720\\\" scalable=\\\"true\\\" bitrate=\\\"500\\\" maintainAspectRatio=\\\"true\\\"><![CDATA[http://vcdn.adnxs.com/p/creative-video/ef/a6/d0/bb/efa6d0bb-8c19-44a8-b140-4b0bc2e02087/efa6d0bb-8c19-44a8-b140-4b0bc2e02087_1280_720_500k.mp4]]></MediaFile><MediaFile id=\\\"612533\\\" delivery=\\\"progressive\\\" type=\\\"video/mp4\\\" width=\\\"768\\\" height=\\\"432\\\" scalable=\\\"true\\\" bitrate=\\\"1100\\\" maintainAspectRatio=\\\"true\\\"><![CDATA[http://vcdn.adnxs.com/p/creative-video/ef/a6/d0/bb/efa6d0bb-8c19-44a8-b140-4b0bc2e02087/efa6d0bb-8c19-44a8-b140-4b0bc2e02087_768_432_1100k.mp4]]></MediaFile><MediaFile id=\\\"612534\\\" delivery=\\\"progressive\\\" type=\\\"video/webm\\\" width=\\\"768\\\" height=\\\"432\\\" scalable=\\\"true\\\" bitrate=\\\"1500\\\" maintainAspectRatio=\\\"true\\\"><![CDATA[http://vcdn.adnxs.com/p/creative-video/ef/a6/d0/bb/efa6d0bb-8c19-44a8-b140-4b0bc2e02087/efa6d0bb-8c19-44a8-b140-4b0bc2e02087_768_432_1500k.webm]]></MediaFile><MediaFile id=\\\"612535\\\" delivery=\\\"progressive\\\" type=\\\"video/mp4\\\" width=\\\"1280\\\" height=\\\"720\\\" scalable=\\\"true\\\" bitrate=\\\"1100\\\" maintainAspectRatio=\\\"true\\\"><![CDATA[http://vcdn.adnxs.com/p/creative-video/ef/a6/d0/bb/efa6d0bb-8c19-44a8-b140-4b0bc2e02087/efa6d0bb-8c19-44a8-b140-4b0bc2e02087_1280_720_1100k.mp4]]></MediaFile></MediaFiles></Linear></Creative></Creatives></InLine></Ad></VAST>\",\"vastCreativeUrl\":\"\"}}";

    @Override
    public void loadUrl(String url) {
        super.loadUrl(url);
        if (url.contains("file:///android_asset/apn_vastvideo.html")) {
            webView = new WebView(RuntimeEnvironment.application);
            Clog.w(TestUtil.testLogTag, "ShadowCustomWebView loadUrl");
            String adReady = AD_READY_CONSTANT.replace("aspect_ratio", aspectRatio);
            // Just send back adReady notification from here since this is unit tests webview is not loading complete.
            this.getWebViewClient().shouldOverrideUrlLoading(webView, String.format("video://%s", adReady));
        }

    }

    protected void injectJavaScript(String url) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript(url, null);
        } else {
            loadUrl(url);
        }
    }
}
