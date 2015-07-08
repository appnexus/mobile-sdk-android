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

import android.content.Context;

import com.appnexus.opensdk.shadows.ShadowAsyncTaskNoExecutor;
import com.appnexus.opensdk.shadows.ShadowWebSettings;
import com.appnexus.opensdk.shadows.ShadowWebView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;

@Config(shadows = {ShadowAsyncTaskNoExecutor.class,
        ShadowWebView.class, ShadowWebSettings.class},
        emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class AdFetcherTest extends BaseRoboTest {
    private AdFetcher adFetcher;

    @Override
    public void setup() {
        super.setup();
        // Since ad type is not a key factor that affects ad fetcher
        // Using BannerAdView as the owner ad of AdFetcher here
        MockAdOwner owner = new MockAdOwner(activity);
        owner.setPlacementID("0");
        owner.setAdSize(320, 50);
        clearAAIDAsyncTasks();
        adFetcher = new AdFetcher(owner);
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

    /**
     * When instantiating an AdView instance, AAID retrieval async tasks are scheduled
     * This method clears all the background and UI thread tasks before running actual tests
     */
    private void clearAAIDAsyncTasks() {
        Robolectric.runBackgroundTasks();
        Robolectric.runUiThreadTasks();
    }


    private void assertExpectedBGTasksAfterOneAdRequest(int expectedBgTaskCount) {
        // pause until a scheduler has a task in queue
        waitForTasks();

        // AdFetcher posts to a Handler which executes (queues) an AdRequest -- run the handler message
        Robolectric.runUiThreadTasks();

        // Check that the AdRequest was queued
        int bgTaskCount = Robolectric.getBackgroundScheduler().enqueuedTaskCount();

        assertEquals(expectedBgTaskCount, bgTaskCount);
    }

    @Test
    public void testDefaultState() {
        assertEquals(-1, adFetcher.getPeriod());
        assertEquals(AdFetcher.STATE.STOPPED, adFetcher.getState());
    }

    @Test
    public void testStartWithRefreshOff() {
        // Default state is auto refresh off, just start the ad fetcher
        adFetcher.start();
        assertExpectedBGTasksAfterOneAdRequest(2);
        assertEquals(AdFetcher.STATE.SINGLE_REQUEST, adFetcher.getState());
    }

    @Test
    public void testStartWithRefreshOn() {
        // default state was stopped
        // setting period to 10000 won't start the ad fetcher
        adFetcher.setPeriod(10000);
        adFetcher.start();

        // assert 2 here because a AAID async task is executed for each AdRequest
        assertExpectedBGTasksAfterOneAdRequest(2);

        assertEquals(AdFetcher.STATE.AUTO_REFRESH, adFetcher.getState());

        // reset background scheduler, clear tasks for the refresh
        Robolectric.getBackgroundScheduler().reset();
        Robolectric.getBackgroundScheduler().pause();

        // in the following method, wait until next ad request is enqueued
        assertExpectedBGTasksAfterOneAdRequest(2);
        assertEquals(AdFetcher.STATE.AUTO_REFRESH, adFetcher.getState());
    }

    @Test
    public void testStop() {
        // not needed, but in case AdRequest is run
        Robolectric.addPendingHttpResponse(200, TestResponses.blank());

        // start an AdFetcher normally, until an AdRequest is queued
        adFetcher.start();
        assertExpectedBGTasksAfterOneAdRequest(2);
        assertNotSame(AdFetcher.STATE.STOPPED, adFetcher.getState());

        adFetcher.stop();

        // pause until a scheduler has a task in queue
        waitForTasks();
        // Run the cancel command on AdRequest
        Robolectric.runUiThreadTasks();
        // Run the pending AdRequest from start() -- should have been canceled
        while (Robolectric.getBackgroundScheduler().areAnyRunnable()) {
            Robolectric.getBackgroundScheduler().runOneTask();
        }

        // A normally executed AdRequest will queue onPostExecute call to the UI thread,
        // but it should be canceled, and queue nothing
        int uiTaskCount = Robolectric.getUiThreadScheduler().enqueuedTaskCount();
        assertEquals(1, uiTaskCount);
        assertEquals(AdFetcher.STATE.STOPPED, adFetcher.getState());
    }

    @Test
    public void testSetPeriod() {
        int period = 30000;
        adFetcher.setPeriod(period);
        assertEquals(period, adFetcher.getPeriod());
    }

    @Test
    public void testResetRefreshOff() {
        adFetcher.setPeriod(10000);
        adFetcher.start();
        assertEquals(AdFetcher.STATE.AUTO_REFRESH, adFetcher.getState());
        adFetcher.setPeriod(-1);
        assertEquals(AdFetcher.STATE.SINGLE_REQUEST, adFetcher.getState());
    }

    class MockAdOwner extends BannerAdView {

        public MockAdOwner(Context context) {
            super(context);
        }

        @Override
        public boolean isReadyToStart() {
            return true;
        }

    }
}
