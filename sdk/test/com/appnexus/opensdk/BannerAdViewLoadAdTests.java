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

import com.appnexus.opensdk.mocks.MockDefaultExecutorSupplier;
import com.appnexus.opensdk.shadows.ShadowAsyncTaskNoExecutor;
import com.appnexus.opensdk.shadows.ShadowCustomWebView;
import com.appnexus.opensdk.shadows.ShadowSettings;
import com.appnexus.opensdk.shadows.ShadowWebSettings;
import com.squareup.okhttp.mockwebserver.MockResponse;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@Config(sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class,
                ShadowCustomWebView.class, ShadowWebSettings.class, ShadowSettings.class, ShadowLog.class})
@RunWith(RobolectricTestRunner.class)
public class BannerAdViewLoadAdTests extends BaseViewAdTest {

    @Override
    public void setup() {
        super.setup();
    }

    @Override
    public void tearDown() {
        super.tearDown();
    }

    //This verifies that the AsyncTask for Request is being executed on the Correct Executor.
    @Test
    public void testRequestExecutorForBackgroundTasks() {
        SDKSettings.setExternalExecutor(MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
        assertNotSame(ShadowAsyncTaskNoExecutor.getExecutor(), MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
        bannerAdView.loadAd();
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertEquals(ShadowAsyncTaskNoExecutor.getExecutor(), MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
    }

    @Test
    public void testgetAdTypeBanner() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner())); // First queue a regular HTML banner response
        assertTrue(bannerAdView.getAdType() == AdType.UNKNOWN); // First tests if ad_type is UNKNOW initially
        executeBannerRequest();
        assertTrue(bannerAdView.getAdType() == AdType.BANNER); // If a HTML banner is served then BANNER
    }


    @Test
    public void testgetAdTypeBannerNative() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.anNativeVideo())); // First queue a regular HTML banner response
        assertTrue(bannerAdView.getAdType() == AdType.UNKNOWN); // First tests if ad_type is UNKNOWN initially
        executeBannerRequest();
        assertTrue(bannerAdView.getAdType() == AdType.NATIVE); // If a Native Ad is served then NATIVE

        assertTrue(nativeAdResponse.getCreativeId().equalsIgnoreCase("47772560"));
        assertTrue(nativeAdResponse.getIconUrl().equalsIgnoreCase("http://path_to_icon.com"));
        assertTrue(nativeAdResponse.getIcon() == null);
        assertTrue(nativeAdResponse.getImage() == null);
        assertTrue(nativeAdResponse.getImageUrl().equalsIgnoreCase("http://path_to_main.com"));
        assertTrue(nativeAdResponse.getTitle().equalsIgnoreCase("test title"));
        assertTrue(nativeAdResponse.getDescription().equalsIgnoreCase("test description"));
        assertTrue(nativeAdResponse.getAdditionalDescription().equalsIgnoreCase("additional test description"));
        assertTrue(nativeAdResponse.getImageSize().getHeight() == 200);
        assertTrue(nativeAdResponse.getImageSize().getWidth() == 300);
        assertTrue(nativeAdResponse.getIconSize().getHeight() == 150);
        assertTrue(nativeAdResponse.getIconSize().getWidth() == 100);
        assertEquals("<VAST>content</VAST>",nativeAdResponse.getVastXml());
        assertEquals("http://ib.adnxs.com/privacy...",nativeAdResponse.getPrivacyLink());

    }

    @Test
    public void testBannerNativeSwitchingAdTypes() {
        bannerAdView.setAutoRefreshInterval(15000);
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner()));
        Assert.assertEquals(AdType.UNKNOWN, bannerAdView.getAdType());
        requestManager = new AdViewRequestManager(bannerAdView);
        requestManager.execute();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        Assert.assertEquals(15000, bannerAdView.getAutoRefreshInterval());
        Assert.assertEquals(AdType.BANNER, bannerAdView.getAdType());
        assertCallbacks(true);
        assertBannerAdResponse(true);
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.anNativeWithoutImages()));
        bannerAdView.enableNativeRendering(false);
        requestManager.execute();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        Assert.assertEquals(15000, bannerAdView.getAutoRefreshInterval());
        Assert.assertEquals(AdType.NATIVE, bannerAdView.getAdType());
        Assert.assertEquals(false, bannerAdView.isNativeRenderingEnabled());
        assertCallbacks(true);
        assertBannerAdResponse(false);
    }

    @Test
    public void testgetAdTypeUnKnown() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blankBanner())); // First queue a regular HTML banner response
        assertTrue(bannerAdView.getAdType() == AdType.UNKNOWN); // First tests if ad_type is UNKNOWN initially
        executeBannerRequest();
        assertTrue(bannerAdView.getAdType() == AdType.UNKNOWN); // If a HTML banner is served then BANNER
    }

    private void executeBannerRequest() {
        bannerAdView.setAutoRefreshInterval(15000);
        bannerAdView.loadAdOffscreen();

        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

//        Robolectric.getBackgroundThreadScheduler().advanceToNextPostedRunnable();
//        Robolectric.getForegroundThreadScheduler().advanceToNextPostedRunnable();
    }


    @Test
    public void testgetCreativeIdBanner() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner())); // First queue a regular HTML banner response
        executeBannerRequest();
        assertEquals("6332753", bannerAdView.getCreativeId());
    }


    @Test
    public void testgetCreativeIdBannerNativeCreativeId() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.anNative())); // First queue a banner Native response
        executeBannerRequest();
        assertEquals("47772560", bannerAdView.getCreativeId());
    }

    @Test
    public void testgetCreativeIdUnKnownCreativeId() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blankBanner())); // First queue a regular HTML banner response
        executeBannerRequest();
        assertEquals("", bannerAdView.getCreativeId());

    }

    @Test
    public void testAutoRefreshBannerNativeRenderer() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.anNativeRenderer())); // First queue a banner Native response
        bannerAdView.enableNativeRendering(true);
        executeBannerRequest();
        assertTrue(bannerAdView.isNativeRenderingEnabled());
        assertEquals(15000, bannerAdView.getAutoRefreshInterval());
    }

    @Test
    public void testUseNativeAssemblyRendererTrue() {
        ShadowCustomWebView.simulateRendererScriptSuccess = true;
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.anNativeRenderer())); // First queue a banner Native response
        bannerAdView.enableNativeRendering(true);
        executeBannerRequest();
        assertTrue(bannerAdView.isNativeRenderingEnabled());
        // Asserting that the AdLoaded for NativeAdResponse is not triggered
        assertNull(nativeAdResponse);
    }

    @Test
    public void testUseNativeAssemblyRendererFalse() {
        ShadowCustomWebView.simulateRendererScriptSuccess = false;
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.anNative())); // First queue a banner Native response
        bannerAdView.enableNativeRendering(false);
        executeBannerRequest();
        assertFalse(bannerAdView.isNativeRenderingEnabled());
        assertNotNull(nativeAdResponse);
        assertBannerAdResponse(false);
    }
}
