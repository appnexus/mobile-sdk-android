/*
 *    Copyright 2018 APPNEXUS INC
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
import com.appnexus.opensdk.shadows.ShadowCustomWebView;
import com.appnexus.opensdk.shadows.ShadowSettings;
import com.appnexus.opensdk.shadows.ShadowWebSettings;
import com.appnexus.opensdk.util.RoboelectricTestRunnerWithResources;
import com.appnexus.opensdk.util.TestUtil;
import com.appnexus.opensdk.utils.Clog;
import com.squareup.okhttp.mockwebserver.MockResponse;

import org.bouncycastle.jcajce.provider.symmetric.ARC4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowWebView;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@Config(constants = BuildConfig.class, sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class,
                ShadowCustomWebView.class, ShadowWebSettings.class, ShadowSettings.class, ShadowLog.class})
@RunWith(RoboelectricTestRunnerWithResources.class)
public class BannerAdViewLoadAdTests extends BaseViewAdTest {

    @Override
    public void setup() {
        super.setup();
    }

    @Override
    public void tearDown() {
        super.tearDown();
    }

    @Test
    public void testgetAdTypeBanner() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner())); // First queue a regular HTML banner response
        assertTrue(bannerAdView.getAdType() == AdType.UNKNOWN); // First tests if ad_type is UNKNOW initially
        executeBannerRequest();
        assertTrue(bannerAdView.getAdType() == AdType.BANNER); // If a HTML banner is served then BANNER
    }


    @Test
    public void testgetAdTypeVideo() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.rtbVASTVideo())); // First queue a regular HTML banner response
        assertTrue(bannerAdView.getAdType() == AdType.UNKNOWN); // First tests if ad_type is UNKNOW initially
        executeBannerRequest();
        assertTrue(bannerAdView.getAdType() == AdType.VIDEO); // If a VAST Video is served then VIDEO
    }

    @Test
    public void testgetAdTypeUnKnown() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blankBanner())); // First queue a regular HTML banner response
        assertTrue(bannerAdView.getAdType() == AdType.UNKNOWN); // First tests if ad_type is UNKNOW initially
        executeBannerRequest();
        assertTrue(bannerAdView.getAdType() == AdType.UNKNOWN); // If a HTML banner is served then BANNER
    }

    private void executeBannerRequest() {
        bannerAdView.setAutoRefreshInterval(15);
        bannerAdView.loadAdOffscreen();

        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
    }


    @Test
    public void testgetCreativeIdBanner() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner())); // First queue a regular HTML banner response
        executeBannerRequest();
        assertEquals("6332753",bannerAdView.getCreativeId());
    }

    @Test
    public void testgetCreativeIdVideoCreativeId() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.rtbVASTVideo())); // First queue a regular HTML banner response
        executeBannerRequest();
        assertEquals("6332753",bannerAdView.getCreativeId());
    }

    @Test
    public void testgetCreativeIdUnKnownCreativeId() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blankBanner())); // First queue a regular HTML banner response
        executeBannerRequest();
        assertEquals("",bannerAdView.getCreativeId());

    }
}
