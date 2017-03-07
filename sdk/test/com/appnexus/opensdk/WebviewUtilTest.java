/*
 *    Copyright 2016 APPNEXUS INC
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

import com.appnexus.opensdk.shadows.ShadowAsyncTaskNoExecutor;
import com.appnexus.opensdk.shadows.ShadowSettings;
import com.appnexus.opensdk.shadows.ShadowWebSettings;
import com.appnexus.opensdk.util.Lock;
import com.appnexus.opensdk.util.RoboelectricTestRunnerWithResources;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.WebviewUtil;
import com.squareup.okhttp.mockwebserver.MockResponse;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowWebView;

import static junit.framework.Assert.assertEquals;

@Config(constants = BuildConfig.class, sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class,
                ShadowWebView.class, ShadowWebSettings.class, ShadowSettings.class})
@RunWith(RoboelectricTestRunnerWithResources.class)
public class WebviewUtilTest extends BaseViewAdTest {

    RequestManager requestManager2;

    @Override
    public void setup() {
        super.setup();
        requestManager = new AdViewRequestManager(bannerAdView);
        requestManager2 = new AdViewRequestManager(interstitialAdView);
    }


    //Set the cookies to -1
    public void resetCookies() {

    }


    //This verifies that the cookies in response are synced correctly to the device.
    @Test
    public void test1CookiesSync() {
        server.enqueue(new MockResponse().setResponseCode(200).setHeader("Set-Cookie", TestResponses.UUID_COOKIE_1).setBody(TestResponses.banner()));
        requestManager.execute();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        String wvcookie = WebviewUtil.getCookie();
        //Asserts the Cookie stored in the device is the same as that of the one we sent back in the response.
        assertEquals(getUUId2(wvcookie), getUUId2(TestResponses.UUID_COOKIE_1));
    }

    //This verifies the Cookie is reset properly.
    @Test
    public void test2CookiesReset() {
        server.enqueue(new MockResponse().setResponseCode(200).setHeader("Set-Cookie", TestResponses.UUID_COOKIE_RESET).setBody(TestResponses.banner()));

        requestManager.execute();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        String wvcookie = WebviewUtil.getCookie();
        System.out.println(wvcookie);
        assertEquals(getUUId2(wvcookie), getUUId2(TestResponses.UUID_COOKIE_RESET));
    }

    private static String getUUId2(String wvcookie) {
        String[] existingCookies = wvcookie.split("; ");
        for (String cookie : existingCookies) {
            if (cookie != null && cookie.contains(Settings.AN_UUID)) {
                return cookie;
            }
        }
        return null;
    }


    @Override
    public void onAdLoaded(AdView adView) {
        super.onAdLoaded(adView);
        Lock.unpause();
        System.out.println("Ad loaded unpassed");
    }

    @Override
    public void onAdRequestFailed(AdView adView, ResultCode resultCode) {
        super.onAdRequestFailed(adView, resultCode);
        Lock.unpause();
    }

// @TODO RoboCookieManager adds cookies to the cookie store instead of replacing them need to figure out a way around that.
// @TODO also server.takerequest doesnot have the cookie in the header so unable to test cookie sent correctly in Unit test cases.


}
