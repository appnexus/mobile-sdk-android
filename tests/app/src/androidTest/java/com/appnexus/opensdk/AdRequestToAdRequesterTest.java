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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@Config(emulateSdk=18)
@RunWith(RobolectricTestRunner.class)
public class AdRequestToAdRequesterTest extends BaseRoboTest implements AdRequester {
    boolean requesterFailed, requesterReceivedResponse, requesterReturnedOwner;

    @Override
    public void setup() {
        super.setup();
        requesterFailed = false;
        requesterReceivedResponse = false;
        requesterReturnedOwner = false;
    }

    @Override
    public void assertCallbacks(boolean success) {
        assertTrue(requesterReturnedOwner);
        assertEquals(success, requesterReceivedResponse);
        assertEquals(!success, requesterFailed);
    }

    @Test
    public void testRequestSucceeded() {
        // adRequest initialization goes here because getOwner is called in the constructor
        adRequest = new AdRequest(this);

        Robolectric.addPendingHttpResponse(200, TestResponses.banner());
        adRequest.execute();
        Robolectric.runBackgroundTasks();
        Robolectric.runUiThreadTasks();
        assertCallbacks(true);
    }

    @Test
    public void testRequestBlank() {
        adRequest = new AdRequest(this);

        // blanks are handled by requester
        Robolectric.addPendingHttpResponse(200, TestResponses.blank());
        adRequest.execute();
        Robolectric.runBackgroundTasks();
        Robolectric.runUiThreadTasks();
        assertCallbacks(true);
    }

    @Test
    public void testRequestStatusError() {
        adRequest = new AdRequest(this);

        Robolectric.addPendingHttpResponse(404, TestResponses.banner());
        adRequest.execute();
        Robolectric.runBackgroundTasks();
        Robolectric.runUiThreadTasks();
        assertCallbacks(false);
    }

    @Override
    public void failed(AdRequest request) {
        requesterFailed = true;
    }

    @Override
    public void onReceiveResponse(AdResponse response) {
        requesterReceivedResponse = true;
    }

    @Override
    public AdView getOwner() {
        requesterReturnedOwner = true;
        return bannerAdView;
    }

    long time;
    @Override
    public void markLatencyStart() {
        time = System.currentTimeMillis();
    }

    @Override
    public long getLatency(long now) {
        return System.currentTimeMillis()-time;
    }
}
