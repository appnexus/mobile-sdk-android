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

import java.util.LinkedList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@Config(emulateSdk=18)
@RunWith(RobolectricTestRunner.class)
public class AdRequestToAdRequesterTest extends BaseRoboTest implements AdRequester {
    boolean requesterFailed, requesterReceivedResponse;
    AdRequest adRequest;
    AdResponse response;
    RequestParameters requestParameters;

    @Override
    public void setup() {
        super.setup();
        requesterFailed = false;
        requesterReceivedResponse = false;
        requestParameters = new RequestParameters(activity);
    }

    public void assertCallbacks(boolean success) {
        assertTrue(requesterReceivedResponse || requesterFailed);
        assertEquals(success, requesterReceivedResponse);
        assertEquals(!success, requesterFailed);
    }

    public void setBannerRequestParams() {
        requestParameters.setPlacementID("0");
        requestParameters.setAdWidth(320);
        requestParameters.setAdHeight(50);
        requestParameters.setMediaType(MediaType.BANNER);
    }

    public void setNativeRequestParams() {
        requestParameters.setPlacementID("0");
        requestParameters.setAdWidth(1);
        requestParameters.setAdHeight(1);
        requestParameters.setMediaType(MediaType.NATIVE);
    }

    @Test
    public void testRequestBannerSucceeded() {
        setBannerRequestParams();
        // adRequest initialization goes here because getOwner is called in the constructor
        adRequest = new AdRequest(this);

        Robolectric.addPendingHttpResponse(200, TestResponses.banner());
        adRequest.execute();
        Robolectric.runBackgroundTasks();
        Robolectric.runUiThreadTasks();
        assertCallbacks(true);
        assertEquals(MediaType.BANNER, response.getMediaType());

    }

    @Test
    public void testRequestBlank() {
        setBannerRequestParams();
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
        setBannerRequestParams();
        adRequest = new AdRequest(this);
        Robolectric.addPendingHttpResponse(404, TestResponses.banner());
        adRequest.execute();
        Robolectric.runBackgroundTasks();
        Robolectric.runUiThreadTasks();
        assertCallbacks(false);
    }

    @Test
    public void testRequestNativeSucceeded() {
        setNativeRequestParams();
        adRequest = new AdRequest(this);

        Robolectric.addPendingHttpResponse(200, TestResponses.anNative());
        adRequest.execute();
        Robolectric.runBackgroundTasks();
        Robolectric.runUiThreadTasks();
        assertCallbacks(true);
        assertEquals(MediaType.NATIVE, response.getMediaType());
    }

    @Override
    public void failed(AdRequest request) {
        requesterFailed = true;
    }

    @Override
    public void onReceiveResponse(AdResponse response) {
        requesterReceivedResponse = true;
        this.response = response;
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

    @Override
    public LinkedList<MediatedAd> getMediatedAds() {
        return null;
    }

    @Override
    public RequestParameters getRequestParams() {
        return requestParameters;
    }
}
