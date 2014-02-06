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
import com.appnexus.opensdk.shadows.ShadowAsyncTaskNoExecutor;
import com.appnexus.opensdk.shadows.ShadowWebSettings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertTrue;

@Config(shadows = {ShadowAsyncTaskNoExecutor.class, ShadowWebSettings.class},
        manifest = "../sdk/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class TestMRAID extends BaseRoboTest {

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
        Robolectric.addPendingHttpResponse(200, TestResponses.mraidBanner(testName));
        bannerAdView.setAdSize(320, 50);
        bannerAdView.loadAdOffscreen();

        // let AdFetcher queue AdRequest
        waitForTasks();
        Robolectric.runUiThreadTasks();
        // run AdRequest
        Robolectric.getBackgroundScheduler().runOneTask();
        // runs all of the UI events until webview is loaded
        Robolectric.runUiThreadTasks();

        assertTrue(bannerAdView.getChildAt(0) instanceof WebView);
        webView = (WebView) bannerAdView.getChildAt(0);
    }

    @Test
    public void testSuccessfulBannerLoaded() {
        loadMraidBanner("testSuccessfulBannerLoaded");
    }

}
