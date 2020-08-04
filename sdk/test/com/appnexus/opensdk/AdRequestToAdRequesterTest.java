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

import com.appnexus.opensdk.mocks.MockDefaultExecutorSupplier;
import com.appnexus.opensdk.shadows.ShadowAsyncTaskNoExecutor;
import com.appnexus.opensdk.shadows.ShadowSettings;
import com.appnexus.opensdk.shadows.ShadowWebSettings;
import com.appnexus.opensdk.ut.UTAdRequest;
import com.appnexus.opensdk.ut.UTAdRequester;
import com.appnexus.opensdk.ut.UTAdResponse;
import com.appnexus.opensdk.ut.UTRequestParameters;
import com.appnexus.opensdk.ut.adresponse.BaseAdResponse;
import com.appnexus.opensdk.utils.Settings;
import com.squareup.okhttp.mockwebserver.MockResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowWebView;

import java.util.ArrayList;
import java.util.LinkedList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

@Config(sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class,
                ShadowWebView.class, ShadowWebSettings.class, ShadowSettings.class, ShadowLog.class})
@RunWith(RobolectricTestRunner.class)
public class AdRequestToAdRequesterTest extends BaseRoboTest implements UTAdRequester {
    boolean requesterFailed, requesterReceivedServerResponse, requesterReceivedAd;
    UTAdRequest adRequest;
    UTAdResponse response;
    UTRequestParameters requestParameters;

    @Override
    public void setup() {
        super.setup();
        requesterFailed = false;
        requesterReceivedServerResponse = false;
        requesterReceivedAd = false;
        requestParameters = new UTRequestParameters(activity);
        Settings.getSettings().ua = "";
    }

    public void assertReceiveServerResponseSuccessful(boolean success) {
        assertTrue(requesterReceivedServerResponse || requesterFailed);
        assertEquals(success, requesterReceivedServerResponse);
        assertEquals(!success, requesterFailed);
    }

    public void assertServerResponseHasAds(boolean hasAds) {
        if (response != null && response.getAdList()!=null) {
            assertEquals(hasAds, response.getAdList().size()>0);
        }
    }

    public void setBannerRequestParams() {
        requestParameters.setPlacementID("0");
        requestParameters.setPrimarySize(new AdSize(320,50));
        requestParameters.setMediaType(MediaType.BANNER);
    }

    public void setInterstitialRequestParams() {
        requestParameters.setPlacementID("0");
        ArrayList<AdSize> allowedSizes = new ArrayList<AdSize>();
        allowedSizes.add(new AdSize(300, 250));
        requestParameters.setSizes(allowedSizes);
        requestParameters.setPrimarySize(new AdSize(1,1));
        requestParameters.setMediaType(MediaType.INTERSTITIAL);
    }

    public void setNativeRequestParams() {
        requestParameters.setPlacementID("0");
        requestParameters.setPrimarySize(new AdSize(1,1));
        requestParameters.setMediaType(MediaType.NATIVE);
    }

    //This verifies that the AsyncTask for Request is being executed on the Correct Executor.
    @Test
    public void testRequestExecutorForBackgroundTasks() {
        SDKSettings.setExternalExecutor(MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
        assertNotSame(ShadowAsyncTaskNoExecutor.getExecutor(), MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
        adRequest = new UTAdRequest(this);
        adRequest.execute();
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertEquals(ShadowAsyncTaskNoExecutor.getExecutor(), MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
    }

    @Test
    public void testRequestBannerSucceeded() {
        setBannerRequestParams();
        // adRequest initialization goes here because getOwner is called in the constructor
        adRequest = new UTAdRequest(this);
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner()));
        adRequest.execute();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        assertReceiveServerResponseSuccessful(true);
        assertServerResponseHasAds(true);
        assertEquals(MediaType.BANNER, response.getMediaType());
    }

    @Test
    public void testRequestBannerNativeSucceeded() {
        setBannerRequestParams();
        // adRequest initialization goes here because getOwner is called in the constructor
        adRequest = new UTAdRequest(this);
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.anNative()));
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
        adRequest = new UTAdRequest(this);
        // blanks are handled by requester
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));
        adRequest.execute();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertReceiveServerResponseSuccessful(false);
        assertNull(response.getAdList());
    }

    @Test
    public void testRequestStatusError() {
        setBannerRequestParams();
        adRequest = new UTAdRequest(this);
        server.enqueue(new MockResponse().setResponseCode(404).setBody(TestResponsesUT.banner()));
        adRequest.execute();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertReceiveServerResponseSuccessful(false);
        assertServerResponseHasAds(false);
    }

    @Test
    public void testRequestNativeSucceeded() {
        setNativeRequestParams();
        adRequest = new UTAdRequest(this);
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.anNative()));
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
        adRequest = new UTAdRequest(this);

        // Server response for banner and interstitial is the same
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner()));
        adRequest.execute();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertReceiveServerResponseSuccessful(true);
        assertServerResponseHasAds(true);
        assertEquals(MediaType.INTERSTITIAL, response.getMediaType());
    }

    long time;

    @Override
    public void continueWaterfall(ResultCode code) {

    }

    @Override
    public void nativeRenderingFailed() {
    }

    @Override
    public void onReceiveUTResponse(UTAdResponse response){
        if(response!=null && response.getAdList() != null && !response.getAdList().isEmpty()) {
            requesterReceivedServerResponse = true;
        }else{
            failed(ResultCode.getNewInstance(ResultCode.UNABLE_TO_FILL), response.getAdResponseInfo());
        }
        this.response = response;
    }

    @Override
    public void failed(ResultCode code, ANAdResponseInfo adResponseInfo) {
        requesterFailed = true;
    }

    @Override
    public void onReceiveAd(AdResponse ad) {
        requesterReceivedAd = true;
    }

    @Override
    public void cancel() {

    }

    @Override
    public void execute() {

    }

    @Override
    public LinkedList<BaseAdResponse> getAdList() {
        return null;
    }

    @Override
    public UTRequestParameters getRequestParams() {
        return requestParameters;
    }
}
