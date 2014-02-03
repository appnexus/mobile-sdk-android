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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Timer;
import java.util.TimerTask;

import static junit.framework.Assert.assertEquals;

@Config(shadows = {ShadowAsyncTaskNoExecutor.class, ShadowWebSettings.class})
@RunWith(RobolectricTestRunner.class)
public class TestAdFetcher extends BaseRoboTest {
    private AdFetcher adFetcher;
    private BannerAdView adView;

    @Override
    public void setup() {
        super.setup();
        adView = new BannerAdView(activity);
        adView.setPlacementID("0");
        adFetcher = new AdFetcher(adView);
    }

    private void schedulerTimerToCheckForTasks() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if ((looperScheduler.enqueuedTaskCount() > 0)
                        || (uiScheduler.enqueuedTaskCount() > 0)
                        || (bgScheduler.enqueuedTaskCount() > 0)) {
                    Lock.unpause();
                    this.cancel();
                }
            }
        }, 0, 100);
    }

    private void runStartTest(int expectedBgTaskCount) {
        // pause until a scheduler has a task in queue
        schedulerTimerToCheckForTasks();
        Lock.pause();

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
        adView.setPlacementID("");
        adFetcher.start();
        runStartTest(0);
    }

    @Test
    public void testStartWithShouldResetTrue() {
        adFetcher.start();
        adFetcher.setPeriod(0);
        runStartTest(0);
    }
}
