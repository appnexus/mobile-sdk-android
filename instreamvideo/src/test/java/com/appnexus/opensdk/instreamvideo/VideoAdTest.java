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

import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.VideoOrientation;
import com.appnexus.opensdk.instreamvideo.shadows.ShadowAsyncTaskNoExecutor;
import com.appnexus.opensdk.instreamvideo.shadows.ShadowCustomWebView;
import com.appnexus.opensdk.instreamvideo.shadows.ShadowSettings;
import com.appnexus.opensdk.instreamvideo.shadows.ShadowWebSettings;
import com.appnexus.opensdk.instreamvideo.util.Lock;
import com.appnexus.opensdk.instreamvideo.util.TestUtil;
import com.appnexus.opensdk.utils.Clog;
import com.squareup.okhttp.mockwebserver.MockResponse;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * This tests if the API's in VideoAd are functioning as expected.
 */
@Config(sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class, ShadowWebSettings.class, ShadowCustomWebView.class, ShadowSettings.class, ShadowLog.class})
@RunWith(RobolectricTestRunner.class)
public class VideoAdTest extends BaseRoboTest implements VideoAdLoadListener, VideoAdPlaybackListener {

    VideoAd videoAd;
    boolean adLoaded, adFailed, adPlaying;

    @Override
    public void setup() {
        super.setup();
        videoAd = new VideoAd(activity,"12345");
        videoAd.setAdLoadListener(this);
        videoAd.setVideoPlaybackListener(this);
    }

    @Override
    public void tearDown(){
        super.tearDown();
        videoAd = null;
    }

    @Test
    public void testVideoDuration() throws Exception {
        int minDuration = 10;
        int maxDuration = 100;
        videoAd.setAdMinDuration(minDuration);
        videoAd.setAdMaxDuration(maxDuration);
        inspectVideoDuration(minDuration, maxDuration);
    }

    @Test
    public void testGetVideoOrientationPortrait() throws Exception {
        ShadowCustomWebView.aspectRatio = "0.5625"; // 9:16
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestUTResponses.video()));

        videoAd.loadAd();
        Lock.pause(1000);
        waitForTasks();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();

        waitForTasks();
        Robolectric.getBackgroundThreadScheduler().advanceToNextPostedRunnable();
        Robolectric.getForegroundThreadScheduler().advanceToNextPostedRunnable();
        assertAdLoaded(true);
        Clog.w(TestUtil.testLogTag, "VideoAdTest videoAd.getVideoOrientation()" +videoAd.getVideoOrientation());
        assertTrue(videoAd.getVideoOrientation().equals(VideoOrientation.PORTRAIT));
    }

    @Test
    public void testGetVideoOrientationLandscape() throws Exception {
        ShadowCustomWebView.aspectRatio = "1.7778"; // 16:9
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestUTResponses.video()));

        videoAd.loadAd();
        Lock.pause(1000);
        waitForTasks();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();

        waitForTasks();
        Robolectric.getBackgroundThreadScheduler().advanceToNextPostedRunnable();
        Robolectric.getForegroundThreadScheduler().advanceToNextPostedRunnable();
        assertAdLoaded(true);
        Clog.w(TestUtil.testLogTag, "VideoAdTest videoAd.getVideoOrientation()" +videoAd.getVideoOrientation());
        assertTrue(videoAd.getVideoOrientation().equals(VideoOrientation.LANDSCAPE));
    }

    @Test
    public void testGetVideoOrientationSquare() throws Exception {
        ShadowCustomWebView.aspectRatio = "1";
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestUTResponses.video()));

        videoAd.loadAd();
        Lock.pause(1000);
        waitForTasks();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();

        waitForTasks();
        Robolectric.getBackgroundThreadScheduler().advanceToNextPostedRunnable();
        Robolectric.getForegroundThreadScheduler().advanceToNextPostedRunnable();
        assertAdLoaded(true);
        Clog.w(TestUtil.testLogTag, "VideoAdTest videoAd.getVideoOrientation()" +videoAd.getVideoOrientation());
        assertTrue(videoAd.getVideoOrientation().equals(VideoOrientation.SQUARE));
    }

    @Test
    public void testGetVideoOrientationUnknown() throws Exception {
        ShadowCustomWebView.aspectRatio = "";
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestUTResponses.video()));

        videoAd.loadAd();
        Lock.pause(1000);
        waitForTasks();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();

        waitForTasks();
        Robolectric.getBackgroundThreadScheduler().advanceToNextPostedRunnable();
        Robolectric.getForegroundThreadScheduler().advanceToNextPostedRunnable();
        assertAdLoaded(true);
        Clog.w(TestUtil.testLogTag, "VideoAdTest videoAd.getVideoOrientation()" +videoAd.getVideoOrientation());
        assertTrue(videoAd.getVideoOrientation().equals(VideoOrientation.UNKNOWN));
    }


    @Test
    public void testGetCreativeURL() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestUTResponses.video()));

        videoAd.loadAd();
        Lock.pause(1000);
        waitForTasks();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();

        waitForTasks();
        Robolectric.getBackgroundThreadScheduler().advanceToNextPostedRunnable();
        Robolectric.getForegroundThreadScheduler().advanceToNextPostedRunnable();
        assertAdLoaded(true);
        Clog.w(TestUtil.testLogTag, "VideoAdTest videoAd.getCreativeURL()" +videoAd.getCreativeURL());
        assertTrue(videoAd.getCreativeURL().equalsIgnoreCase("http://vcdn.adnxs.com/p/creative-video/ef/a6/d0/bb/efa6d0bb-8c19-44a8-b140-4b0bc2e02087/efa6d0bb-8c19-44a8-b140-4b0bc2e02087_768_432_500k.mp4"));
    }


    @Test
    public void testGetVideoAdDuration() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestUTResponses.video()));

        videoAd.loadAd();
        Lock.pause(1000);
        waitForTasks();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();

        waitForTasks();
        Robolectric.getBackgroundThreadScheduler().advanceToNextPostedRunnable();
        Robolectric.getForegroundThreadScheduler().advanceToNextPostedRunnable();
        assertAdLoaded(true);
        Clog.w(TestUtil.testLogTag, "VideoAdTest videoAd.getCreativeURL()" +videoAd.getCreativeURL());
        assertTrue(videoAd.getVideoAdDuration() == 145000);
    }

    @Test
    public void testGetVastXML() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestUTResponses.video()));

        videoAd.loadAd();
        Lock.pause(1000);
        waitForTasks();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();

        waitForTasks();
        Robolectric.getBackgroundThreadScheduler().advanceToNextPostedRunnable();
        Robolectric.getForegroundThreadScheduler().advanceToNextPostedRunnable();
        assertAdLoaded(true);
        Clog.w(TestUtil.testLogTag, "VideoAdTest videoAd.getVastXML()" +videoAd.getVastXML());
        assertTrue(videoAd.getVastXML() != "");
    }

    @Test
    public void testGetVastURL() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestUTResponses.video()));

        videoAd.loadAd();
        Lock.pause(1000);
        waitForTasks();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();

        waitForTasks();
        Robolectric.getBackgroundThreadScheduler().advanceToNextPostedRunnable();
        Robolectric.getForegroundThreadScheduler().advanceToNextPostedRunnable();
        assertAdLoaded(true);
        Clog.w(TestUtil.testLogTag, "VideoAdTest videoAd.getVastURL()" +videoAd.getVastURL());
        assertTrue(videoAd.getVastURL() == "");
    }

    @Test
    public void testAdPlayStarted() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestUTResponses.video()));

        videoAd.loadAd();
        Lock.pause(1000);
        waitForTasks();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();

        waitForTasks();
        Robolectric.getBackgroundThreadScheduler().advanceToNextPostedRunnable();
        Robolectric.getForegroundThreadScheduler().advanceToNextPostedRunnable();
        assertAdLoaded(true);

        //@FIXME This test is not possible this case can only be tested in Integration Tests. We need to delete this note and test once we add integration tests
        // Leaving it here just to make sure that we donot ovelook it.
       /* RelativeLayout baseContainer = (RelativeLayout) activity.getWindow().getDecorView().getRootView();

        videoAd.playAd(baseContainer);

        Lock.pause(1000);
        waitForTasks();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();

        waitForTasks();
        Robolectric.getBackgroundThreadScheduler().advanceToNextPostedRunnable();
        Robolectric.getForegroundThreadScheduler().advanceToNextPostedRunnable();

        assertPlayAd(true);*/

    }

     // Tests the value of ExternalUid
    @Test
    public void testSetExternalUId(){
        setExternalUId();
        assertSetExternalUId();

    }

    /**
     * Validates the Traffic Source in the request
     *
     * @throws Exception
     */
    @Test
    public void testTrafficSourceCode() {
        assertNull(videoAd.getTrafficSourceCode());
        videoAd.setTrafficSourceCode("Xandr");
        assertEquals("Xandr", videoAd.getTrafficSourceCode());
    }

    /**
     * Validates the Ext Inv Code in the request
     *
     * @throws Exception
     */
    @Test
    public void testExtInvCode() {
        assertNull(videoAd.getExtInvCode());
        videoAd.setExtInvCode("Xandr");
        assertEquals("Xandr", videoAd.getExtInvCode());
    }

    private void setExternalUId(){
        videoAd.setExternalUid("AppNexus");
    }

    private void assertSetExternalUId(){
        assertNotNull(videoAd.getExternalUid());
        assertEquals(videoAd.getExternalUid(), "AppNexus");

    }


    @Override
    public void onAdLoaded(VideoAd videoAd) {
        adLoaded = true;
        Clog.w(TestUtil.testLogTag, "VideoAdTest onAdLoaded");
    }

    @Override
    public void onAdRequestFailed(VideoAd videoAd, ResultCode errorCode) {
        adFailed = true;
        Clog.w(TestUtil.testLogTag, "VideoAdTest onAdFailed");
    }


    public void assertAdLoaded(Boolean loaded) {
        assertTrue(adLoaded || adFailed);
        assertTrue(loaded | !adFailed);
        assertTrue(loaded | adLoaded);
    }

    public void assertPlayAd(Boolean playing){
        assertTrue(adPlaying);
        assertTrue(adPlaying | playing);
    }


    private void inspectVideoDuration(int minDuration, int maxDuration){
        Assert.assertEquals(videoAd.getAdMaxDuration(), maxDuration);
        Assert.assertEquals(videoAd.getAdMinDuration(), minDuration);
    }

    @Override
    public void onAdPlaying(VideoAd videoAd) {
        adPlaying = true;
        Clog.w(TestUtil.testLogTag, "VideoAdTest onAdPlaying");
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

    }

    @Override
    public void onAdClicked(VideoAd videoAd, String clickUrl) {

    }
}
