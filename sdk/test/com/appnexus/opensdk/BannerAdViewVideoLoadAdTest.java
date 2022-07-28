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
import com.appnexus.opensdk.shadows.ShadowCustomVideoWebView;
import com.appnexus.opensdk.shadows.ShadowSettings;
import com.appnexus.opensdk.shadows.ShadowWebSettings;
import com.squareup.okhttp.mockwebserver.MockResponse;

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

@Config(sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class,
                ShadowCustomVideoWebView.class, ShadowWebSettings.class, ShadowSettings.class, ShadowLog.class})
@RunWith(RobolectricTestRunner.class)
public class BannerAdViewVideoLoadAdTest extends BaseViewAdTest {

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
    public void testgetAdTypeVideo() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.rtbVASTVideo())); // First queue a regular HTML banner response
        assertTrue(bannerAdView.getAdType() == AdType.UNKNOWN); // First tests if ad_type is UNKNOW initially
        executeBannerRequest();
        assertTrue(bannerAdView.getAdType() == AdType.VIDEO); // If a VAST Video is served then VIDEO
    }

    @Test
    public void testLazyBannerVideoAdLoaded() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.rtbVASTVideo()));
        bannerAdView.enableLazyLoad();
        bannerAdView.setAllowVideoDemand(true);
        executeBannerRequest();
        assertCallbacks(true);
        assertFalse(bannerAdView.loadLazyAd());
        assertCallbacks(true);
    }

    @Test
    public void testgetCreativeIdVideoCreativeId() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.rtbVASTVideo())); // First queue a regular HTML banner response
        executeBannerRequest();
        assertEquals("6332753", bannerAdView.getAdResponseInfo().getCreativeId());
    }




    private void executeBannerRequest() {
        bannerAdView.setAutoRefreshInterval(15);
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
