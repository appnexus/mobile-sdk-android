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

import android.webkit.WebView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.httpclient.FakeHttp;

import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class MRAIDTest extends BaseViewAdTest {

    WebView webView;

    @Override
    public void setup() {
        super.setup();
    }

    @Override
    public void tearDown() {
        super.tearDown();
        if (webView != null) webView.destroy();
        webView = null;
    }

    private void loadMraidBanner(String testName) {
        FakeHttp.addPendingHttpResponse(200, TestResponses.mraidBanner(testName));
        bannerAdView.setAdSize(320, 50);
        bannerAdView.loadAdOffscreen();

        // let AdFetcher queue AdRequest
        waitForTasks();
        // Flush AAID tasks before AdRequest tasks twice to make sure AdRequest gets executed
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        assertTrue(bannerAdView.getChildAt(0) instanceof WebView);
        webView = (WebView) bannerAdView.getChildAt(0);
    }

    @Test
    public void testSuccessfulBannerLoaded() {
        loadMraidBanner("testSuccessfulBannerLoaded");
    }

}
