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

import android.arch.core.BuildConfig;
import android.content.Context;

import com.appnexus.opensdk.shadows.ShadowAsyncTaskNoExecutor;
import com.appnexus.opensdk.shadows.ShadowCustomWebView;
import com.appnexus.opensdk.shadows.ShadowSettings;
import com.appnexus.opensdk.shadows.ShadowWebSettings;
import com.appnexus.opensdk.util.Lock;
import com.squareup.okhttp.mockwebserver.MockResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;

@Config(constants = BuildConfig.class, sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class,
                ShadowCustomWebView.class, ShadowWebSettings.class, ShadowSettings.class, ShadowLog.class})
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
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
    }


    private void assertExpectedBGTasksAfterOneAdRequest(int expectedBgTaskCount) {
        // pause until a scheduler has a task in queue
        waitForTasks();

        // AdFetcher posts to a Handler which executes (queues) an AdRequest -- run the handler message
        Robolectric.getForegroundThreadScheduler().advanceToLastPostedRunnable();

        // Check that the AdRequest was queued
        int bgTaskCount = Robolectric.getBackgroundThreadScheduler().size();
        assertEquals("Expected: " + expectedBgTaskCount + ", actual: " + bgTaskCount, expectedBgTaskCount, bgTaskCount);
    }

    @Test
    public void testDefaultState() {
        assertEquals(-1, adFetcher.getPeriod());
        assertEquals(AdFetcher.STATE.STOPPED, adFetcher.getState());
    }

    @Test
    public void testStartWithRefreshOff() {
        // Default state is auto refresh off, just start the ad fetcher
        assertEquals("Default state should be stopped", AdFetcher.STATE.STOPPED, adFetcher.getState());
        adFetcher.start();
        Lock.pause(1000); // added this so jenkins can have enough time to process
        assertEquals("Single request should be used.", AdFetcher.STATE.SINGLE_REQUEST, adFetcher.getState());
        assertExpectedBGTasksAfterOneAdRequest(2);
        assertEquals("State should not be changed after request.", AdFetcher.STATE.SINGLE_REQUEST, adFetcher.getState());
    }

    @Test
    public void testStartWithRefreshOn() {
        // default state was stopped
        adFetcher.setPeriod(30000);
        adFetcher.start();
        Lock.pause(1000); // added this so jenkins can have enough time to process
        // assert 2 here because a AAID async task is executed for each AdRequest
        assertExpectedBGTasksAfterOneAdRequest(2);

        assertEquals(AdFetcher.STATE.AUTO_REFRESH, adFetcher.getState());

        // reset background scheduler, clear tasks for the refresh
        Lock.pause(30000 + 1000); // We wait for till autorefresh is triggered

        // in the following method, wait until next ad request is enqueued
        assertExpectedBGTasksAfterOneAdRequest(3);
        assertEquals(AdFetcher.STATE.AUTO_REFRESH, adFetcher.getState());
    }

    @Test
    public void testStop() {
        if (adFetcher != null) {
            // not needed, but in case AdRequest is run
            server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));
            clearAAIDAsyncTasks();
            // start an AdFetcher normally, until an AdRequest is queued
            adFetcher.start();
            Lock.pause(1000); // added this so jenkins can have enough time to process
            assertExpectedBGTasksAfterOneAdRequest(1);
            assertNotSame(AdFetcher.STATE.STOPPED, adFetcher.getState());

            adFetcher.stop();

            // pause until a scheduler has a task in queue
            waitForTasks();
            // Run the cancel command on AdRequest
            Robolectric.flushForegroundThreadScheduler();
            // Run the pending AdRequest from start() -- should have been canceled
            while (Robolectric.getBackgroundThreadScheduler().areAnyRunnable()) {
                Robolectric.getBackgroundThreadScheduler().runOneTask();
            }

            // A normally executed AdRequest will queue onPostExecute call to the UI thread,
            // but it should be canceled, and queue nothing
            int uiTaskCount = Robolectric.getForegroundThreadScheduler().size();
            assertEquals(0, uiTaskCount);
            assertEquals(AdFetcher.STATE.STOPPED, adFetcher.getState());
        }
    }

    @Test
    public void testSetPeriod() {
        int period = 30000;
        adFetcher.setPeriod(period);
        assertEquals(period, adFetcher.getPeriod());
    }

    @Test
    public void testResetRefreshOff() {
        adFetcher.setPeriod(5000);
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
