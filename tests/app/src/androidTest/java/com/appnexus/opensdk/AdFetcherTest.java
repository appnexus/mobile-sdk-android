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
import com.appnexus.opensdk.shadows.ShadowWebView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;

@Config(shadows = {ShadowAsyncTaskNoExecutor.class,
        ShadowWebView.class, ShadowWebSettings.class},
        emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class AdFetcherTest extends BaseRoboTest {
    private AdFetcher adFetcher;

    @Override
    public void setup() {
        super.setup();
        adFetcher = new AdFetcher(bannerAdView);
    }

    @Override
    public void tearDown() {
        super.tearDown();
        if (adFetcher != null) {
            adFetcher.stop();
            adFetcher.clearDurations();
        }
        adFetcher = null;
    }

    private void runStartTest(int expectedBgTaskCount) {
        // pause until a scheduler has a task in queue
        waitForTasks();

        // AdFetcher posts to a Handler which executes (queues) an AdRequest -- run the handler message
        Robolectric.runUiThreadTasks();
        // Check that the AdRequest was queued
        int bgTaskCount = Robolectric.getBackgroundScheduler().enqueuedTaskCount();
        assertEquals(expectedBgTaskCount, bgTaskCount);
    }

    @Test
    public void testStart() {
        adFetcher.start();
        runStartTest(1);
    }

    @Test
    public void testStartWithRefreshOn() {
        adFetcher.setAutoRefresh(true);
        adFetcher.start();
        runStartTest(1);
    }

    @Test
    public void testRefresh() {
        adFetcher.setAutoRefresh(true);
        adFetcher.setPeriod(1000);
        adFetcher.start();

        runStartTest(1);

        // reset for the refresh
        Robolectric.getBackgroundScheduler().reset();
        Robolectric.getBackgroundScheduler().pause();

        runStartTest(1);
    }

    @Test
    public void testStartWithBadPlacementId() {
        bannerAdView.setPlacementID("");
        adFetcher.start();
        runStartTest(0);
    }

    @Test
    public void testStartWithShouldResetTrue() {
        adFetcher.start();
        adFetcher.setPeriod(0);
        runStartTest(0);
    }

    /*
    Does not call UI thread anymore

    @Test
    public void testStartTwiceBeforeAdRequestQueued() {
        adFetcher.start();

        // wait for the first start to queue its UI thread task
        waitForTasks();

        // the second start should fail (and dispatch a UI thread call to adFailed)
        adFetcher.start();
        runStartTest(1);
    }

    @Test
    public void testStartTwiceAfterAdRequestQueued() {
        adFetcher.start();

        // wait for the first start to queue AdRequest task
        runStartTest(1);

        // the second start should fail (and dispatch a UI thread call to adFailed)
        adFetcher.start();
        runStartTest(1);
    }
    */

    @Test
    public void testStop() {
        // not needed, but in case AdRequest is run
        Robolectric.addPendingHttpResponse(200, TestResponses.blank());

        // start an AdFetcher normally, until an AdRequest is queued
        adFetcher.start();
        runStartTest(1);

        adFetcher.stop();

        // pause until a scheduler has a task in queue
        waitForTasks();
        // Run the cancel command on AdRequest
        Robolectric.runUiThreadTasks();
        // Run the pending AdRequest from start() -- should have been canceled
        Robolectric.getBackgroundScheduler().runOneTask();

        // A normally executed AdRequest will queue onPostExecute call to the UI thread,
        // but it should be canceled, and queue nothing
        int uiTaskCount = Robolectric.getUiThreadScheduler().enqueuedTaskCount();
        assertEquals(0, uiTaskCount);
    }

    @Test
    public void testPeriod() {
        int period = 30000;
        adFetcher.setPeriod(period);
        assertEquals(period, adFetcher.getPeriod());
    }
}
