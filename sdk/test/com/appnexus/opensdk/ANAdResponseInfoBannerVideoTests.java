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
import com.appnexus.opensdk.shadows.ShadowCustomVideoWebView;
import com.appnexus.opensdk.shadows.ShadowSettings;
import com.appnexus.opensdk.shadows.ShadowWebSettings;
import com.appnexus.opensdk.ut.UTConstants;
import com.squareup.okhttp.mockwebserver.MockResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.robolectric.Shadows.shadowOf;

@Config(sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class,
                ShadowCustomVideoWebView.class, ShadowWebSettings.class, ShadowSettings.class, ShadowLog.class})
@RunWith(RobolectricTestRunner.class)
public class ANAdResponseInfoBannerVideoTests extends BaseViewAdTest {

    @Override
    public void setup() {
        super.setup();
    }

    @Override
    public void tearDown() {
        super.tearDown();
    }

    @Test
    public void testAdResponseInfoRTBBannerVideo() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.rtbVASTVideo())); // First queue a regular HTML banner response
        assertNull(bannerAdView.getAdResponseInfo());
        assertSame(bannerAdView.getAdType(), AdType.UNKNOWN); // First tests if ad_type is UNKNOWN initially
        executeBannerRequest();
        assertCallbacks(true);
        assertSame(bannerAdView.getAdType(), AdType.VIDEO); // If a HTML banner is served then BANNER
        assertNotNull(bannerAdView.getAdResponseInfo());
        assertEquals(bannerAdView.getAdResponseInfo().getAdType(), AdType.VIDEO);
        assertEquals(bannerAdView.getAdResponseInfo().getCreativeId(), "6332753");
        assertEquals(bannerAdView.getAdResponseInfo().getTagId(), "123456");
        assertEquals(bannerAdView.getAdResponseInfo().getBuyMemberId(), 123);
        assertEquals(bannerAdView.getAdResponseInfo().getContentSource(), UTConstants.RTB);
        assertEquals(bannerAdView.getAdResponseInfo().getNetworkName(), "");
        assertEquals(bannerAdView.getAdResponseInfo().getCpm(), 0.000010);
        assertEquals(bannerAdView.getAdResponseInfo().getCpmPublisherCurrency(), 0.000010);
        assertEquals(bannerAdView.getAdResponseInfo().getPublisherCurrencyCode(), "$");
    }

    @Test
    public void testgetCreativeIdBannerVideoCreativeId() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.rtbVASTVideo())); // First queue a banner Native response
        assertNull(bannerAdView.getAdResponseInfo());
        executeBannerRequest();
        assertEquals("6332753", bannerAdView.getAdResponseInfo().getCreativeId());
    }

    private void executeBannerRequest() {
        bannerAdView.setAutoRefreshInterval(15000);
        bannerAdView.loadAd();

        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

//        Robolectric.getBackgroundThreadScheduler().advanceToNextPostedRunnable();
//        Robolectric.getForegroundThreadScheduler().advanceToNextPostedRunnable();

//        ShadowLooper shadowLooper = shadowOf(getMainLooper());
//        if (!shadowLooper.isIdle()) {
//            shadowLooper.idle();
//        }
//        RuntimeEnvironment.getMasterScheduler().advanceToNextPostedRunnable();
    }
}
