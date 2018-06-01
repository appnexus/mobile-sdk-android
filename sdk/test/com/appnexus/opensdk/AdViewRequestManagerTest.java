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
import com.appnexus.opensdk.shadows.ShadowCustomWebView;
import com.appnexus.opensdk.shadows.ShadowSettings;
import com.appnexus.opensdk.shadows.ShadowWebSettings;
import com.appnexus.opensdk.util.Lock;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import static junit.framework.Assert.assertTrue;

/**
 * This tests AdViewRequestManager
 */


@Config(constants = BuildConfig.class, sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class,
                ShadowCustomWebView.class, ShadowWebSettings.class, ShadowSettings.class, ShadowLog.class})
@RunWith(RobolectricTestRunner.class)
public class AdViewRequestManagerTest extends BaseViewAdTest {

    RecordedRequest request = null;

    @Override
    public void setup() {
        super.setup();
        requestManager = new AdViewRequestManager(bannerAdView);
        request = null;
    }


    // Given a RecordedRequest verifies if it is notify URL.
    private void assertNotifyURL(RecordedRequest request) {
        String no_AdURL = request.getRequestLine();
        System.out.print("notify_URL::" + no_AdURL + "\n");
        assertTrue(no_AdURL.startsWith("GET /vast_track/v2?info&notifyURL HTTP/1.1"));
    }



    // After we receive adReady we fire AdLoaded and stop autorefresh
    // If we receive video-error after adReady but before video-complete then we should turn on Autorefresh again
    // This tests that scenario
    @Test
    public void testNotifyURLForVideo() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.rtbVASTVideo()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));

        executeUTRequest();
        assertCallbacks(true);
        assertTrue(bannerAdView.mAdFetcher.getState() == AdFetcher.STATE.STOPPED);

        request = server.takeRequest(); // Discard the first request since its a HTTP Post for /ut/v2 ad request call
        request = server.takeRequest();
        assertNotifyURL(request);
    }


    private void executeUTRequest() {
        requestManager.execute();
        waitForTasks();
        // execute main ad request
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        waitForTasks();
        // execute main ad request
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
    }


    @Override
    public void onAdLoaded(AdView adView) {
        super.onAdLoaded(adView);
        Lock.unpause();
    }

    @Override
    public void onAdLoaded(NativeAdResponse nativeAdResponse) {
        super.onAdLoaded(nativeAdResponse);
        Lock.unpause();
    }

    @Override
    public void onAdRequestFailed(AdView adView, ResultCode resultCode) {
        super.onAdRequestFailed(adView, resultCode);
        Lock.unpause();
    }

}
