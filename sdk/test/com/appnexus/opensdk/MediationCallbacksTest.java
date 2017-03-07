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
import com.appnexus.opensdk.shadows.ShadowSettings;
import com.appnexus.opensdk.shadows.ShadowWebSettings;
import com.appnexus.opensdk.util.Lock;
import com.appnexus.opensdk.util.RoboelectricTestRunnerWithResources;
import com.appnexus.opensdk.utils.Settings;
import com.squareup.okhttp.mockwebserver.MockResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowWebView;
import org.robolectric.shadows.httpclient.FakeHttp;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@Config(constants = BuildConfig.class, sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class,
                ShadowWebView.class, ShadowWebSettings.class, ShadowSettings.class})
@RunWith(RoboelectricTestRunnerWithResources.class)
public class MediationCallbacksTest extends BaseViewAdTest {

    boolean adLoadedMultiple, adFailedMultiple;

    @Override
    public void setup() {
        super.setup();
        requestManager = new AdViewRequestManager(bannerAdView);

        adLoadedMultiple = false;
        adFailedMultiple = false;
    }

    @Override
    public void onAdLoaded(AdView adView) {
        if (adLoaded) adLoadedMultiple = true;
        super.onAdLoaded(adView);
    }

    @Override
    public void onAdRequestFailed(AdView adView, ResultCode resultCode) {
        if (adFailed) adFailedMultiple = true;
        super.onAdRequestFailed(adView, resultCode);
    }

    @Override
    public void assertCallbacks(boolean success) {
        super.assertCallbacks(success);
        assertFalse(adLoadedMultiple);
        assertFalse(adFailedMultiple);
    }

    public void runCallbacksTest(int testNumber, boolean success) {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.callbacks(testNumber)));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.blank()));
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();

        requestManager.execute();
        // loads server response
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();
        // wait for ResultCB response
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();

        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();

        Lock.pause(Settings.MEDIATED_NETWORK_TIMEOUT + 1000);

        assertCallbacks(success);
    }

    /**
     * These tests ensure that callbacks (app events) that are returned from mediation networks
     * at the wrong times do not make it to the app developer callback level (AdListener).
     */

    // Verifies that multiple onAdLoaded calls only call AdListener.onAdLoaded once
    @Test
    public void test18AdLoadedMultiple() {
        runCallbacksTest(18, true);
    }

    // TODO comment out this test temporarily because the deplayed runnables would all be executed if using Robolectric.flushForegroundTasks();
    // find a better solution to test this
/*    // Verifies that a timed-out network call to onAdLoaded fails
    @Test
    public void test19Timeout() {
        runCallbacksTest(19, false);
    } */

    // Verifies that the SDK ignores a call to onAdFailed if onAdLoaded
    // was already called by the network
    @Test
    public void test20LoadThenFail() {
        runCallbacksTest(20, true);
    }

    // Verifies that the SDK ignores a call to onAdLoaded if onAdFailed
    // was already called by the network
    @Test
    public void test21FailThenLoad() {
        runCallbacksTest(21, false);
    }

    // Verifies that a network that calls to onAdLoaded and then calls
    // the non-essential callbacks does so successfully
    @Test
    public void test22LoadAndHitOtherCallbacks() {
        runCallbacksTest(22, true);
        assertTrue(adExpanded);
        assertTrue(adCollapsed);
        assertTrue(adClicked);
    }

    // Verifies that the SDK ignores any extra callbacks onAdFailed
    // was already called by the network
    @Test
    public void test23FailAndHitOtherCallbacks() {
        runCallbacksTest(23, false); // unable to fill
        assertFalse(adExpanded);
        assertFalse(adCollapsed);
        assertFalse(adClicked);
    }

    // Verifies that multiple onAdFailed calls only call AdListener.onAdFailed once
    @Test
    public void test24AdFailedMultiple() {
        runCallbacksTest(24, false);
    }

    /* TODO find out if SDK fails this test
   // Verifies that the SDK ignores any extra callbacks called by
    // a network from a previous ad
    @Test
    public void test25LoadThenLoadNewAndHitOtherCallbacks() {
        FakeHttp.addPendingHttpResponse(200, TestResponses.callbacks(22));
        FakeHttp.addPendingHttpResponse(200, TestResponses.blank());
        FakeHttp.addPendingHttpResponse(200, TestResponses.banner());
        FakeHttp.addPendingHttpResponse(200, TestResponses.blank());

        // load first successful view, which calls extra callbacks after a delay
        requestManager.execute();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();

        // resultCB for first view
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();

        assertCallbacks(true);

        // reset for new ad request
        adLoaded = false;

        // load second view to replace first view
        requestManager = new AdViewRequestManager(bannerAdView);
        requestManager.execute();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();

        // run delayed runnables
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();

        Lock.pause(Settings.MEDIATED_NETWORK_TIMEOUT + 1000);

        assertCallbacks(true);

        assertFalse(adExpanded);
        assertFalse(adCollapsed);
        assertFalse(adClicked);
    }*/

}
