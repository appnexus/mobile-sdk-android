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

package com.appnexus.opensdk.instreamvideo;

import com.appnexus.opensdk.ANAdResponseInfo;
import com.appnexus.opensdk.AdType;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.instreamvideo.shadows.ShadowAsyncTaskNoExecutor;
import com.appnexus.opensdk.instreamvideo.shadows.ShadowCustomWebView;
import com.appnexus.opensdk.instreamvideo.shadows.ShadowSettings;
import com.appnexus.opensdk.instreamvideo.shadows.ShadowWebSettings;
import com.appnexus.opensdk.ut.UTConstants;
import com.squareup.okhttp.mockwebserver.MockResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@Config(sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class,
                ShadowCustomWebView.class, ShadowWebSettings.class, ShadowSettings.class, ShadowLog.class})
@RunWith(RobolectricTestRunner.class)
public class ANAdResponseInfoVideoTests extends BaseRoboTest implements VideoAdLoadListener {

    VideoAd videoAd;
    ANAdResponseInfo adResponseInfo;

    @Override
    public void setup() {
        super.setup();
        videoAd = new VideoAd(activity,"12345");
        videoAd.setAdLoadListener(this);
    }

    @Override
    public void tearDown() {
        super.tearDown();
        videoAd = null;
        adResponseInfo = null;
    }

    @Test
    public void testAdResponseInfoRTBVideo() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestUTResponses.video())); // First queue a regular HTML banner response
        assertNull(videoAd.getAdResponseInfo());
        executeVideoRequest();
        assertNotNull(videoAd.getAdResponseInfo());
        assertEquals(videoAd.getAdResponseInfo().getAdType(), AdType.VIDEO);
        assertEquals(videoAd.getAdResponseInfo().getCreativeId(), "6332753");
        assertEquals(videoAd.getAdResponseInfo().getTagId(), "123456");
        assertEquals(videoAd.getAdResponseInfo().getBuyMemberId(), 123);
        assertEquals(videoAd.getAdResponseInfo().getContentSource(), UTConstants.RTB);
        assertEquals(videoAd.getAdResponseInfo().getNetworkName(), "");
        assertEquals(videoAd.getAdResponseInfo().getAuctionId(), "123456789");
        assertEquals(videoAd.getAdResponseInfo().getCpm(), 0.000010);
        assertEquals(videoAd.getAdResponseInfo().getCpmPublisherCurrency(), 0.000010);
        assertEquals(videoAd.getAdResponseInfo().getPublisherCurrencyCode(), "$");
    }

    @Test
    public void testAdResponseInfoRTBVideoNoBid() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestUTResponses.NO_BID));
        assertNull(videoAd.getAdResponseInfo());
        executeVideoRequest();
        assertNotNull(videoAd.getAdResponseInfo());
        assertEquals(videoAd.getAdResponseInfo().getAdType(), null);
        assertEquals(videoAd.getAdResponseInfo().getCreativeId(), "");
        assertEquals(videoAd.getAdResponseInfo().getTagId(), "123456789");
        assertEquals(videoAd.getAdResponseInfo().getBuyMemberId(), 0);
        assertEquals(videoAd.getAdResponseInfo().getContentSource(), "");
        assertEquals(videoAd.getAdResponseInfo().getNetworkName(), "");
        assertEquals(videoAd.getAdResponseInfo().getAuctionId(), "3552547938089377051000000");
        assertEquals(videoAd.getAdResponseInfo().getCpm(), 0d);
        assertEquals(videoAd.getAdResponseInfo().getCpmPublisherCurrency(), 0d);
        assertEquals(videoAd.getAdResponseInfo().getPublisherCurrencyCode(), "");

    }

    @Test
    public void testAdResponseInfoRTBVideoBlank() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestUTResponses.blank()));
        assertNull(videoAd.getAdResponseInfo());
        executeVideoRequest();
        assertNull(videoAd.getAdResponseInfo());
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

    }

    @Override
    public void onAdRequestFailed(VideoAd videoAd, ResultCode errorCode) {
    }
}
