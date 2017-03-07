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
import com.appnexus.opensdk.util.RoboelectricTestRunnerWithResources;
import com.appnexus.opensdk.utils.Settings;
import com.squareup.okhttp.mockwebserver.MockResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowWebView;
import org.robolectric.shadows.httpclient.FakeHttp;

import java.util.ArrayList;
import java.util.LinkedList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@Config(constants = BuildConfig.class, sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class,
                ShadowWebView.class, ShadowWebSettings.class, ShadowSettings.class})
@RunWith(RoboelectricTestRunnerWithResources.class)
public class AdRequestToAdRequesterTest extends BaseRoboTest implements AdRequester {
    boolean requesterFailed, requesterReceivedServerResponse, requesterReceivedAd;
    AdRequest adRequest;
    ServerResponse response;
    RequestParameters requestParameters;

    @Override
    public void setup() {
        super.setup();
        requesterFailed = false;
        requesterReceivedServerResponse = false;
        requesterReceivedAd = false;
        requestParameters = new RequestParameters(activity);
        Settings.getSettings().ua = "";
    }

    public void assertReceiveServerResponseSuccessful(boolean success) {
        assertTrue(requesterReceivedServerResponse || requesterFailed);
        assertEquals(success, requesterReceivedServerResponse);
        assertEquals(!success, requesterFailed);
    }

    public void assertServerResponseHasAds(boolean hasAds) {
        if (response != null) {
            assertEquals(hasAds, response.containsAds());
        }
    }

    public void setBannerRequestParams() {
        requestParameters.setPlacementID("0");
        requestParameters.setAdWidth(320);
        requestParameters.setAdHeight(50);
        requestParameters.setMediaType(MediaType.BANNER);
    }

    public void setInterstitialRequestParams() {
        requestParameters.setPlacementID("0");
        ArrayList<AdSize> allowedSizes = new ArrayList<AdSize>();
        allowedSizes.add(new AdSize(300, 250));
        requestParameters.setAllowedSizes(allowedSizes);
        requestParameters.setMediaType(MediaType.INTERSTITIAL);
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
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.banner()));
        adRequest.execute();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        assertReceiveServerResponseSuccessful(true);
        assertServerResponseHasAds(true);
        assertEquals(MediaType.BANNER, response.getMediaType());
    }

    @Test
    public void testRequestBlank() {
        setBannerRequestParams();
        adRequest = new AdRequest(this);
        // blanks are handled by requester
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.blank()));
        adRequest.execute();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertReceiveServerResponseSuccessful(true);
        assertServerResponseHasAds(false);
    }

    @Test
    public void testRequestStatusError() {
        setBannerRequestParams();
        adRequest = new AdRequest(this);
        server.enqueue(new MockResponse().setResponseCode(404).setBody(TestResponses.banner()));
        adRequest.execute();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertReceiveServerResponseSuccessful(false);
        assertServerResponseHasAds(false);
    }

    @Test
    public void testRequestNativeSucceeded() {
        setNativeRequestParams();
        adRequest = new AdRequest(this);
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.anNative()));
        adRequest.execute();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertReceiveServerResponseSuccessful(true);
        assertServerResponseHasAds(true);
        assertEquals(MediaType.NATIVE, response.getMediaType());
    }

    @Test
    public void testRequestInterstitialSucceeded() {
        setInterstitialRequestParams();
        // adRequest initialization goes here because getOwner is called in the constructor
        adRequest = new AdRequest(this);

        // Server response for banner and interstitial is the same
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.banner()));
        adRequest.execute();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertReceiveServerResponseSuccessful(true);
        assertServerResponseHasAds(true);
        assertEquals(MediaType.INTERSTITIAL, response.getMediaType());
    }

    long time;

    @Override
    public void failed(ResultCode code) {
        requesterFailed = true;
    }

    @Override
    public void onReceiveServerResponse(ServerResponse response) {
        requesterReceivedServerResponse = true;
        this.response = response;
    }

    @Override
    public void onReceiveAd(AdResponse ad) {
        requesterReceivedAd = true;
    }

    @Override
    public void markLatencyStart() {
        time = System.currentTimeMillis();
    }

    @Override
    public long getLatency(long now) {
        return System.currentTimeMillis() - time;
    }

    @Override
    public void cancel() {

    }

    @Override
    public void execute() {

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
