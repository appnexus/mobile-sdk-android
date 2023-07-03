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
import com.appnexus.opensdk.shadows.ShadowSettings;
import com.appnexus.opensdk.shadows.ShadowWebSettings;
import com.appnexus.opensdk.shadows.ShadowCustomVideoWebView;
import com.appnexus.opensdk.util.TestUtil;
import com.appnexus.opensdk.utils.Clog;
import com.squareup.okhttp.mockwebserver.MockResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import static junit.framework.Assert.assertTrue;

@Config(sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class,
                ShadowCustomVideoWebView.class, ShadowWebSettings.class, ShadowSettings.class, ShadowLog.class})
@RunWith(RobolectricTestRunner.class)
public class VideoImplementationTest extends BaseViewAdTest {

    @Override
    public void setup() {
        super.setup();
    }

    @Override
    public void tearDown() {
        super.tearDown();
        ShadowCustomVideoWebView.simulateVideoError = false; // Reset the value if not next test case will fail Global instance.
        ShadowCustomVideoWebView.simulateDelayedVideoError = false; // Reset the value if not next test case will fail Global instance.
        ShadowCustomVideoWebView.aspectRatio = "";
    }

     //This test Succes onAdLoaded for Video
     //Also test the Adfetcher state is Stopped for Video on Succesful AdLoad
    @Test
    public void testVideoImplementationAdLoadSuccess() throws Exception {
        server.setDispatcher(getDispatcher(TestResponsesUT.rtbVASTVideo()));
        executeBannerRequest();
        assertCallbacks(true);
        assertTrue(bannerAdView.mAdFetcher.getState() == AdFetcher.STATE.STOPPED);
    }




    // This Tests onAdFailed is called succesfully
    // Also tests AutoRefresh is on for onAdFailed
    @Test
    public void testVideoImplementationAdLoadFailure() throws Exception {
        server.setDispatcher(getDispatcher(TestResponsesUT.rtbVASTVideo()));
        ShadowCustomVideoWebView.simulateVideoError = true;

        executeBannerRequest();
        assertCallbacks(false);
        assertTrue(bannerAdView.mAdFetcher.getState() == AdFetcher.STATE.AUTO_REFRESH);
    }


    // After we receive adReady we fire AdLoaded and stop autorefresh
    // If we receive video-error after adReady but before video-complete then we should turn on Autorefresh again
    // This tests that scenario
    @Test
    public void testToggleAutoRefresh() throws Exception {
        server.setDispatcher(getDispatcher(TestResponsesUT.rtbVASTVideo()));
        ShadowCustomVideoWebView.simulateDelayedVideoError = true;
        executeBannerRequest();
        assertCallbacks(true);
        assertTrue(bannerAdView.mAdFetcher.getState() == AdFetcher.STATE.STOPPED);

        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertTrue(bannerAdView.mAdFetcher.getState() == AdFetcher.STATE.AUTO_REFRESH);

    }

    @Test
    public void testGetVideoOrientationPortrait() throws Exception {
        server.setDispatcher(getDispatcher(TestResponsesUT.rtbVASTVideo()));
        ShadowCustomVideoWebView.aspectRatio = "0.5625"; // 9:16
        bannerAdView.setPortraitBannerVideoPlayerSize(new AdSize(300,400));
        bannerAdView.setLandscapeBannerVideoPlayerSize(new AdSize(320,250));
        bannerAdView.setSquareBannerVideoPlayerSize(new AdSize(200,200));
        executeBannerRequest();
        assertCallbacks(true);
        assertTrue(bannerAdView.mAdFetcher.getState() == AdFetcher.STATE.STOPPED);

        assertTrue(bannerAdView.getVideoOrientation().equals(VideoOrientation.PORTRAIT));

        Clog.d(TestUtil.testLogTag, "VideoImplementaitonTest::bannerAdView.getCreativeWidth()::"+bannerAdView.getCreativeWidth());
        Clog.d(TestUtil.testLogTag, "VideoImplementaitonTest::bannerAdView.getCreativeHeight()::"+bannerAdView.getCreativeHeight());
        assertTrue(bannerAdView.getCreativeWidth() == 300);
        assertTrue(bannerAdView.getCreativeHeight() == 400);
    }

    @Test
    public void testGetVideoOrientationLandscape() throws Exception {
        server.setDispatcher(getDispatcher(TestResponsesUT.rtbVASTVideo()));
        ShadowCustomVideoWebView.aspectRatio = "1.7778"; // 16:9
        bannerAdView.setPortraitBannerVideoPlayerSize(new AdSize(300,400));
        bannerAdView.setLandscapeBannerVideoPlayerSize(new AdSize(620,500));
        bannerAdView.setSquareBannerVideoPlayerSize(new AdSize(200,200));
        executeBannerRequest();
        assertCallbacks(true);
        assertTrue(bannerAdView.mAdFetcher.getState() == AdFetcher.STATE.STOPPED);

        assertTrue(bannerAdView.getVideoOrientation().equals(VideoOrientation.LANDSCAPE));

        Clog.d(TestUtil.testLogTag, "VideoImplementaitonTest::bannerAdView.getCreativeWidth()::"+bannerAdView.getCreativeWidth());
        Clog.d(TestUtil.testLogTag, "VideoImplementaitonTest::bannerAdView.getCreativeHeight()::"+bannerAdView.getCreativeHeight());
        assertTrue(bannerAdView.getCreativeWidth() == 620);
        assertTrue(bannerAdView.getCreativeHeight() == 500);
    }

    @Test
    public void testGetVideoOrientationSquare() throws Exception {
        server.setDispatcher(getDispatcher(TestResponsesUT.rtbVASTVideo()));
        ShadowCustomVideoWebView.aspectRatio = "1";
        bannerAdView.setPortraitBannerVideoPlayerSize(new AdSize(300,400));
        bannerAdView.setLandscapeBannerVideoPlayerSize(new AdSize(620,500));
        bannerAdView.setSquareBannerVideoPlayerSize(new AdSize(200,200));
        executeBannerRequest();
        //assertCallbacks(true);
        assertTrue(bannerAdView.mAdFetcher.getState() == AdFetcher.STATE.STOPPED);

        assertTrue(bannerAdView.getVideoOrientation().equals(VideoOrientation.SQUARE));

        Clog.d(TestUtil.testLogTag, "VideoImplementaitonTest::bannerAdView.getCreativeWidth()::"+bannerAdView.getCreativeWidth());
        Clog.d(TestUtil.testLogTag, "VideoImplementaitonTest::bannerAdView.getCreativeHeight()::"+bannerAdView.getCreativeHeight());
        assertTrue(bannerAdView.getCreativeWidth() == 200);
        assertTrue(bannerAdView.getCreativeHeight() == 200);
    }

    @Test
    public void testGetVideoOrientationUnknown() throws Exception {
        server.setDispatcher(getDispatcher(TestResponsesUT.rtbVASTVideo()));
        ShadowCustomVideoWebView.aspectRatio = "0"; // Unknown Case
        bannerAdView.setPortraitBannerVideoPlayerSize(new AdSize(300,400));
        bannerAdView.setLandscapeBannerVideoPlayerSize(new AdSize(620,500));
        bannerAdView.setSquareBannerVideoPlayerSize(new AdSize(200,200));
        executeBannerRequest();
        assertCallbacks(true);
        assertTrue(bannerAdView.mAdFetcher.getState() == AdFetcher.STATE.STOPPED);

        assertTrue(bannerAdView.getVideoOrientation().equals(VideoOrientation.UNKNOWN));


        Clog.d(TestUtil.testLogTag, "VideoImplementaitonTest::bannerAdView.getCreativeWidth()::"+bannerAdView.getCreativeWidth());
        Clog.d(TestUtil.testLogTag, "VideoImplementaitonTest::bannerAdView.getCreativeHeight()::"+bannerAdView.getCreativeHeight());
        // If Orientation is unknown then by default Landscape sizes should be used
        assertTrue(bannerAdView.getCreativeWidth() == 620);
        assertTrue(bannerAdView.getCreativeHeight() == 500);
    }

    @Test
    public void testGetVideoWidthAndHeight() throws Exception {
        server.setDispatcher(getDispatcher(TestResponsesUT.rtbVASTVideo()));

        executeBannerRequest();
        assertCallbacks(true);
        assertTrue(bannerAdView.mAdFetcher.getState() == AdFetcher.STATE.STOPPED);

        assertTrue(bannerAdView.getBannerVideoCreativeWidth() == 243);
        assertTrue(bannerAdView.getBannerVideoCreativeHeight() == 432);
    }




    private void executeBannerRequest(){
        bannerAdView.setAutoRefreshInterval(30);
        bannerAdView.loadAd();

        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
    }


}
