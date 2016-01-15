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

import com.appnexus.opensdk.utils.Settings;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.httpclient.FakeHttp;

import java.io.IOException;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class AdListenerTest extends BaseViewAdTest {

    MockWebServer server;

    @Override
    public void setup() {
        super.setup();
        try {
            setupMockServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Banner Testing

    @Test
    public void testBannerAdLoaded() {
        FakeHttp.addPendingHttpResponse(200, TestResponses.banner());
        requestManager = new AdViewRequestManager(bannerAdView);
        requestManager.execute();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertCallbacks(true);
    }

    @Test
    public void testBannerAdFailed() {
        FakeHttp.addPendingHttpResponse(200, TestResponses.blank());
        requestManager = new AdViewRequestManager(bannerAdView);
        requestManager.execute();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertCallbacks(false);
    }

    // Interstitial Testing


    /**
     * Validates if success callback is firing correctly when HTML Interstitial has been successfully loaded.
     */
    @Test
    public void testInterstitialHtmlRTBAdLoaded() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestUTResponses.html()));
        requestManager = new InterstitialAdRequestManager(interstitialAdView);
        requestManager.execute();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertCallbacks(true);
    }

    /**
     * Validates if success callback is firing correctly when SSM Interstitial for HTML ad has been successfully loaded.
     */
    @Test
    public void testInterstitialHTMLSSMAdLoaded() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestUTResponses.ssmHtml()));
        requestManager = new InterstitialAdRequestManager(interstitialAdView);
        requestManager.execute();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        shutdownServer();
        FakeHttp.addPendingHttpResponse(200, TestUTResponses.htmlResponse());
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertCallbacks(true);
    }

    /**
     * Validates if failure callback is firing correctly when SSM Interstitial for HTML ad has been failed.
     */
    @Test
    public void testInterstitialHTMLSSMAdFailed() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestUTResponses.ssmHtml()));
        requestManager = new InterstitialAdRequestManager(interstitialAdView);
        requestManager.execute();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        shutdownServer();
        FakeHttp.addPendingHttpResponse(200, TestUTResponses.blank());
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertCallbacks(false);
    }

    /**
     * Validates if failure callback is firing correctly when SSM Interstitial for HTML ad has been failed.
     */
    @Test
    public void testInterstitialVideoSSMAdFailed() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestUTResponses.ssmVideo()));
        requestManager = new InterstitialAdRequestManager(interstitialAdView);
        requestManager.execute();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        shutdownServer();
        FakeHttp.addPendingHttpResponse(200, TestUTResponses.blank());
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertCallbacks(false);
    }

    /**
     * Validates if success callback is firing correctly when SSM Interstitial VAST Inline ad has been successfully loaded.
     * This includes going through multiple wrappers and getting the final inline response.
     */
    @Test
    public void testInterstitialVastSSMAdLoaded() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestUTResponses.ssmVideo()));
        requestManager = new InterstitialAdRequestManager(interstitialAdView);
        requestManager.execute();
        Robolectric.flushBackgroundThreadScheduler();
        shutdownServer();
        Robolectric.flushForegroundThreadScheduler();
        FakeHttp.addPendingHttpResponse(200, TestUTResponses.vastInline());
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertCallbacks(true);
    }

    /**
     * Validates if success callback is firing correctly when SSM Interstitial VAST has been successfully loaded.
     * This includes going through multiple wrappers and getting the final inline response.
     */
    @Test
    public void testInterstitialVastWrapperSSMAdLoaded() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestUTResponses.ssmVideo()));
        requestManager = new InterstitialAdRequestManager(interstitialAdView);
        requestManager.execute();
        Robolectric.flushBackgroundThreadScheduler();
        shutdownServer();
        FakeHttp.addPendingHttpResponse(200, TestUTResponses.vastXML());
        Robolectric.flushForegroundThreadScheduler();

        FakeHttp.addPendingHttpResponse(200, TestUTResponses.vastInline());
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertCallbacks(true);
    }


    /**
     * Validates if success callback is firing correctly when Interstitial VAST has been successfully loaded.
     * This includes going through multiple wrappers and getting the final inline response.
     */
    @Test
    public void testInterstitialVastRTBAdLoaded() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestUTResponses.video()));
        requestManager = new InterstitialAdRequestManager(interstitialAdView);
        requestManager.execute();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        FakeHttp.addPendingHttpResponse(200, TestUTResponses.vastInline());
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertCallbacks(true);
    }

    /**
     * Validates if failure callback is firing correctly if there is a blank response.
     */
    @Test
    public void testInterstitialFailedWithBlankResponse() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestUTResponses.blank()));
        requestManager = new InterstitialAdRequestManager(interstitialAdView);
        requestManager.execute();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertCallbacks(false);
    }

    /**
     * Validates if failure callback is firing correctly if there is a no bid response.
     */
    @Test
    public void testInterstitialFailedWithNoBidResponse() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestUTResponses.noBid()));
        requestManager = new InterstitialAdRequestManager(interstitialAdView);
        requestManager.execute();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertCallbacks(false);
    }

    /**
     * Validates if failure callback is firing correctly if VAST wrapper has failed to load.
     */
    @Test
    public void testInterstitialVastWrapperFailed() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestUTResponses.video()));
        requestManager = new InterstitialAdRequestManager(interstitialAdView);
        requestManager.execute();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        FakeHttp.addPendingHttpResponse(200, TestUTResponses.blank());
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertCallbacks(false);
    }


    private void setupMockServer() throws IOException {
        server = new MockWebServer();
        server.start();

        HttpUrl url = server.url("/");
        Settings.BASE_URL_UT = url.toString();
    }

    @Override
    public void tearDown() {
        super.tearDown();
        shutdownServer();
    }

    private void shutdownServer() {
        try {
            if (server != null) {
                server.shutdown();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
