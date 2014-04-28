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
import com.appnexus.opensdk.shadows.ShadowWebSettings;
import com.appnexus.opensdk.util.Lock;
import com.appnexus.opensdk.utils.Settings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@Config(shadows = {ShadowAsyncTaskNoExecutor.class, ShadowWebSettings.class},
        manifest = "../sdk/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class TestMediationCallbacks extends BaseRoboTest implements AdListener {

    boolean adLoadedMultiple, adFailedMultiple;

    @Override
    public void setup() {
        super.setup();
        adRequest = new AdRequest(bannerAdView.mAdFetcher);

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
        Robolectric.addPendingHttpResponse(200, TestResponses.callbacks(testNumber));
        Robolectric.addPendingHttpResponse(200, TestResponses.blank());

        adRequest.execute();
        // execute main ad request
        Robolectric.getBackgroundScheduler().runOneTask();
        Robolectric.runUiThreadTasks();

        // fast-forward any timers (for mediation network timeouts)
        Robolectric.getUiThreadScheduler().advanceToLastPostedRunnable();

        // execute resultCB
        Robolectric.getBackgroundScheduler().runOneTask();
        Robolectric.runUiThreadTasksIncludingDelayedTasks();

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

    // Verifies that a timed-out network call to onAdLoaded fails
    @Test
    public void test19Timeout() {
        runCallbacksTest(19, false);
    }

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
        runCallbacksTest(23, false);
        assertFalse(adExpanded);
        assertFalse(adCollapsed);
        assertFalse(adClicked);
    }

    // Verifies that multiple onAdFailed calls only call AdListener.onAdFailed once
    @Test
    public void test24AdFailedMultiple() {
        runCallbacksTest(24, false);
    }

    // Verifies that the SDK ignores any extra callbacks called by
    // a network from a previous ad
    @Test
    public void test25LoadThenLoadNewAndHitOtherCallbacks() {
        Robolectric.addPendingHttpResponse(200, TestResponses.callbacks(22));
        Robolectric.addPendingHttpResponse(200, TestResponses.blank());
        Robolectric.addPendingHttpResponse(200, TestResponses.banner());
        Robolectric.addPendingHttpResponse(200, TestResponses.blank());

        // load first successful view, which calls extra callbacks after a delay
        adRequest.execute();
        Robolectric.getBackgroundScheduler().runOneTask();
        Robolectric.runUiThreadTasks();

        // resultCB for first view
        Robolectric.getBackgroundScheduler().runOneTask();
        Robolectric.runUiThreadTasks();

        assertCallbacks(true);

        // reset for new ad request
        adLoaded = false;

        // load second view to replace first view
        adRequest = new AdRequest(bannerAdView.mAdFetcher);
        adRequest.execute();
        Robolectric.getBackgroundScheduler().runOneTask();
        Robolectric.runUiThreadTasks();

        // run delayed runnables
        Robolectric.getUiThreadScheduler().advanceToLastPostedRunnable();
        Robolectric.getBackgroundScheduler().runOneTask();
        Robolectric.runUiThreadTasksIncludingDelayedTasks();

        Lock.pause(Settings.MEDIATED_NETWORK_TIMEOUT + 1000);

        assertCallbacks(true);

        assertFalse(adExpanded);
        assertFalse(adCollapsed);
        assertFalse(adClicked);
    }
}
