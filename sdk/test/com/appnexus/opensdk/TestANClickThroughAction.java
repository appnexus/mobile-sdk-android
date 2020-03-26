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

import com.appnexus.opensdk.shadows.ShadowAsyncTaskNoExecutor;
import com.appnexus.opensdk.shadows.ShadowCustomClickThroughWebView;
import com.squareup.okhttp.mockwebserver.MockResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLooper;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@Config(sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class,
                ShadowCustomClickThroughWebView.class})
@RunWith(RobolectricTestRunner.class)
public class TestANClickThroughAction extends BaseViewAdTest {
    @Override
    public void setup() {
        super.setup();
    }

    @Override
    public void tearDown() {
        super.tearDown();
    }

    @Test
    public void testBannerANClickThroughActionReturnURL() {
        bannerAdView.setClickThroughAction(ANClickThroughAction.RETURN_URL);
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner())); // First queue a regular HTML banner response
        executeBannerRequest();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        waitUntilExecuted();

        assertTrue(adClickedWithUrl);
        assertFalse(adClicked);
    }

    @Test
    public void testBannerANClickThroughActionSDKBrowser() {
        bannerAdView.setClickThroughAction(ANClickThroughAction.OPEN_SDK_BROWSER);
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner())); // First queue a regular HTML banner response
        executeBannerRequest();

        waitUntilExecuted();

        assertTrue(adClicked);
        assertFalse(adClickedWithUrl);
    }

    @Test
    public void testBannerANClickThroughActionDeviceBrowser() {
        bannerAdView.setClickThroughAction(ANClickThroughAction.OPEN_DEVICE_BROWSER);
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner())); // First queue a regular HTML banner response
        executeBannerRequest();

        waitUntilExecuted();

        assertTrue(adClicked);
        assertFalse(adClickedWithUrl);
    }

    @Test
    public void testInterstitialANClickThroughActionReturnURL() {
        interstitialAdView.setClickThroughAction(ANClickThroughAction.RETURN_URL);
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.noFillCSM_RTBInterstitial()));
        executeInterstitialRequest();

        waitUntilExecuted();

        assertTrue(adClickedWithUrl);
        assertFalse(adClicked);
    }

    @Test
    public void testInterstitialANClickThroughActionSDKBrowser() {
        interstitialAdView.setClickThroughAction(ANClickThroughAction.OPEN_SDK_BROWSER);
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.noFillCSM_RTBInterstitial()));

        executeInterstitialRequest();

        waitUntilExecuted();

        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        assertTrue(adClicked);
        assertFalse(adClickedWithUrl);
    }

    @Test
    public void testInterstitialANClickThroughActionDeviceBrowser() {
        interstitialAdView.setClickThroughAction(ANClickThroughAction.OPEN_DEVICE_BROWSER);
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.noFillCSM_RTBInterstitial()));
        executeInterstitialRequest();

        waitUntilExecuted();

        assertTrue(adClicked);
        assertFalse(adClickedWithUrl);
    }

    private void executeBannerRequest() {
        bannerAdView.loadAd();

        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

//        Robolectric.getBackgroundThreadScheduler().advanceToNextPostedRunnable();
//        Robolectric.getForegroundThreadScheduler().advanceToNextPostedRunnable();
    }

    private void executeInterstitialRequest() {
        interstitialAdView.loadAd();

        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
    }

    private void waitUntilExecuted() {
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
    }
}
