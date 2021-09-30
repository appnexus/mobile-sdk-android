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
import com.appnexus.opensdk.ut.UTConstants;
import com.squareup.okhttp.mockwebserver.MockResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.robolectric.Shadows.shadowOf;

@Config(sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class,
                ShadowCustomWebView.class, ShadowWebSettings.class, ShadowSettings.class, ShadowLog.class})
@RunWith(RobolectricTestRunner.class)
public class ANAdResponseInfoInterstitialTests extends BaseViewAdTest {

    @Override
    public void setup() {
        super.setup();
    }

    @Override
    public void tearDown() {
        super.tearDown();
    }

    @Test
    public void testAdResponseInfoRTBInterstitial() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner())); // First queue a regular HTML banner response
        assertNull(interstitialAdView.getAdResponseInfo());
        assertTrue(interstitialAdView.getAdType() == AdType.UNKNOWN); // First tests if ad_type is UNKNOWN initially
        executeBannerRequest();
        assertTrue(interstitialAdView.getAdType() == AdType.BANNER); // If a HTML banner is served then BANNER
        assertNotNull(interstitialAdView.getAdResponseInfo());
        assertEquals(interstitialAdView.getAdResponseInfo().getAdType(), AdType.BANNER);
        assertEquals(interstitialAdView.getAdResponseInfo().getCreativeId(), "6332753");
        assertEquals(interstitialAdView.getAdResponseInfo().getTagId(), "123456");
        assertEquals(interstitialAdView.getAdResponseInfo().getBuyMemberId(), 123);
        assertEquals(interstitialAdView.getAdResponseInfo().getContentSource(), UTConstants.RTB);
        assertEquals(interstitialAdView.getAdResponseInfo().getNetworkName(), "");
        assertEquals(interstitialAdView.getAdResponseInfo().getCpm(), 0.000010);
        assertEquals(interstitialAdView.getAdResponseInfo().getCpmPublisherCurrency(), 0.000010);
        assertEquals(interstitialAdView.getAdResponseInfo().getPublisherCurrencyCode(), "$");
    }

    @Test
    public void testAdResponseInfoRTBInterstitialNoBid() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.NO_BID));
        assertNull(interstitialAdView.getAdResponseInfo());
        executeBannerRequest();
        assertNotNull(interstitialAdView.getAdResponseInfo());
        assertEquals(interstitialAdView.getAdResponseInfo().getAdType(), null);
        assertEquals(interstitialAdView.getAdResponseInfo().getCreativeId(), "");
        assertEquals(interstitialAdView.getAdResponseInfo().getTagId(), "123456789");
        assertEquals(interstitialAdView.getAdResponseInfo().getBuyMemberId(), 0);
        assertEquals(interstitialAdView.getAdResponseInfo().getContentSource(), "");
        assertEquals(interstitialAdView.getAdResponseInfo().getNetworkName(), "");
        assertEquals(interstitialAdView.getAdResponseInfo().getCpm(), 0d);
        assertEquals(interstitialAdView.getAdResponseInfo().getCpmPublisherCurrency(), 0d);
        assertEquals(interstitialAdView.getAdResponseInfo().getPublisherCurrencyCode(), "");
    }

    @Test
    public void testAdResponseInfoRTBInterstitialBlank() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));
        assertNull(interstitialAdView.getAdResponseInfo());
        executeBannerRequest();
        assertNull(interstitialAdView.getAdResponseInfo());
    }

    @Test
    public void testgetCreativeIdInterstitial() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner())); // First queue a regular HTML banner response
        assertNull(interstitialAdView.getAdResponseInfo());
        executeBannerRequest();
        assertEquals("6332753", interstitialAdView.getAdResponseInfo().getCreativeId());
    }

    @Test
    public void testGetAdResponseInfoNullForBlankInterstitialResponse() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blankBanner())); // First queue a regular HTML banner response
        assertNull(interstitialAdView.getAdResponseInfo());
        executeBannerRequest();
        assertNull(interstitialAdView.getAdResponseInfo());
    }

    private void executeBannerRequest() {
        interstitialAdView.loadAd();

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
