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

package com.appnexus.opensdk.instreamvideo;

import com.appnexus.opensdk.ANClickThroughAction;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.instreamvideo.shadows.ShadowAsyncTaskNoExecutor;
import com.appnexus.opensdk.instreamvideo.shadows.ShadowCustomClickThroughWebView;
import com.appnexus.opensdk.instreamvideo.shadows.ShadowSettings;
import com.appnexus.opensdk.instreamvideo.shadows.ShadowWebSettings;
import com.squareup.okhttp.mockwebserver.MockResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@Config(sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class,
                ShadowCustomClickThroughWebView.class, ShadowWebSettings.class, ShadowSettings.class, ShadowLog.class})
@RunWith(RobolectricTestRunner.class)
public class TestANClickThroughAction extends BaseRoboTest implements VideoAdLoadListener, VideoAdPlaybackListener {
    VideoAd videoAd;
    boolean adLoaded, adFailed, adPlaying;
    boolean adClicked, adClickedWithUrl;

    @Override
    public void setup() {
        super.setup();
        videoAd = new VideoAd(activity, "12345");
        videoAd.setAdLoadListener(this);
        videoAd.setVideoPlaybackListener(this);
    }

    @Override
    public void tearDown() {
        super.tearDown();
        videoAd = null;
    }

    @Test
    public void testVideoANClickThroughActionReturnURL() {
        videoAd.setClickThroughAction(ANClickThroughAction.RETURN_URL);
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestUTResponses.video())); // First queue a regular HTML banner response
        executeVideoRequest();
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        assertTrue(adClickedWithUrl);
        assertFalse(adClicked);
    }

    @Test
    public void testVideoANClickThroughActionSDKBrowser() {
        videoAd.setClickThroughAction(ANClickThroughAction.OPEN_SDK_BROWSER);
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestUTResponses.video())); // First queue a regular HTML banner response
        executeVideoRequest();

        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        assertTrue(adClicked);
        assertFalse(adClickedWithUrl);
    }

    @Test
    public void testVideoANClickThroughActionDeviceBrowser() {
        videoAd.setClickThroughAction(ANClickThroughAction.OPEN_DEVICE_BROWSER);
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestUTResponses.video())); // First queue a regular HTML banner response
        executeVideoRequest();

        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        assertTrue(adClicked);
        assertFalse(adClickedWithUrl);
    }


    private void executeVideoRequest() {
        videoAd.loadAd();

        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
    }

    @Override
    public void onAdLoaded(VideoAd videoAd) {
        adLoaded = true;
        ShadowCustomClickThroughWebView.simulateVideoAdClick = true;
    }

    @Override
    public void onAdRequestFailed(VideoAd videoAd, ResultCode errorCode) {
        adFailed = true;
    }

    @Override
    public void onAdPlaying(VideoAd videoAd) {
        adPlaying = true;
    }

    @Override
    public void onQuartile(VideoAd videoAd, Quartile quartile) {

    }

    @Override
    public void onAdCompleted(VideoAd videoAd, PlaybackCompletionState playbackCompletionStateState) {

    }

    @Override
    public void onAdMuted(VideoAd ad, boolean isMuted) {

    }

    @Override
    public void onAdClicked(VideoAd videoAd) {
        adClicked = true;
    }

    @Override
    public void onAdClicked(VideoAd videoAd, String clickUrl) {
        adClickedWithUrl = true;
    }
}
