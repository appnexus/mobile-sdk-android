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

    @Override
    public void setup() {
        super.setup();
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

    @Test
    public void testInterstitialAdLoaded() {
        FakeHttp.addPendingHttpResponse(200, TestResponses.banner());
        requestManager = new AdViewRequestManager(interstitialAdView);
        requestManager.execute();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertCallbacks(true);
    }

    @Test
    public void testInterstitialVideoAdLoaded() {
        try {
            setupMockServer(TestUTResponses.videoUT());
        } catch (IOException e) {
            e.printStackTrace();
        }
        requestManager = new InterstitialAdRequestManager(interstitialAdView);
        requestManager.execute();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        shutdownServer();

        FakeHttp.addPendingHttpResponse(200, TestUTResponses.getVastInlineResponse());
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertCallbacks(true);
    }

    @Test
    public void testUTRTBAdLoaded() {
        try {
            // TODO /mob should be using FakeHttp to add response
            setupMockServer(TestUTResponses.utHTMLBanner());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // TODO this is /mob why checking against UT RTB response?
        requestManager = new AdViewRequestManager(bannerAdView);
        requestManager.execute();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        shutdownServer();

        FakeHttp.addPendingHttpResponse(200, TestUTResponses.getUTHTMLResponse());
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertCallbacks(true);
    }

    private void setupMockServer(String response) throws IOException {
        server = new MockWebServer();
        server.start();

        HttpUrl url = server.url("/");
        Settings.BASE_URL_UT_V2 = url.toString();
        server.enqueue(new MockResponse().setResponseCode(200).setBody(response));
    }

    @Test
    public void testInterstitialAdFailed() {
        // TODO should be using mock web server here
        FakeHttp.addPendingHttpResponse(200, TestResponses.blank());
        requestManager = new AdViewRequestManager(interstitialAdView);
        requestManager.execute();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertCallbacks(false);
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
