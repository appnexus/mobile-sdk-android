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

import com.appnexus.opensdk.testviews.NoRequestBannerView;
import com.appnexus.opensdk.util.Lock;
import com.appnexus.opensdk.utils.Settings;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.Scheduler;

import static com.appnexus.opensdk.MediatedAdViewController.RESULT.*;
import static junit.framework.Assert.assertTrue;

@Config(shadows = {ShadowAsyncTaskNoExecutor.class})
@RunWith(RobolectricTestRunner.class)
public class TestMediatedAdViewController extends BaseRoboTest {

    @Override
    public void setup() {
        super.setup();
        bannerAdView.setAdListener(this);
        adRequest = new AdRequest(bannerAdView.mAdFetcher);
    }

    // checks that the resultCB appends the reason code correctly
    private void assertResultCB(int requestNumber, MediatedAdViewController.RESULT errorCode) {
        HttpUriRequest sentResultCBRequest = (HttpUriRequest) Robolectric.getSentHttpRequest(requestNumber);
        String resultCBUri = sentResultCBRequest.getURI().toString();
        String result = TestResponses.resultCB(errorCode.ordinal());

        assertTrue(sentResultCBRequest.getMethod().equals("GET"));
        assertTrue(resultCBUri.startsWith(result));
    }

    // common code between all tests
    public void runBasicResultCBTest(MediatedAdViewController.RESULT errorCode, boolean success) {
        // for the result cb call
        Robolectric.addPendingHttpResponse(200, TestResponses.blank());

        adRequest.execute();
        // execute main ad request
        Robolectric.getBackgroundScheduler().runOneTask();
        Robolectric.runUiThreadTasks();

        Robolectric.getUiThreadScheduler().advanceBy(Settings.getSettings().MEDIATED_NETWORK_TIMEOUT);
        // execute result cb request
        Robolectric.getBackgroundScheduler().runOneTask();
        Robolectric.runUiThreadTasks();

        Lock.pause(Settings.getSettings().MEDIATED_NETWORK_TIMEOUT + 1000);

        assertResultCB(1, errorCode);

        assertCallbacks(success);
    }

    // Verify that a successful mediation response,
    // makes the resultCB call with SUCCESS code
    @Test
    public void test1SucceedingMediationCall() {
        Robolectric.addPendingHttpResponse(200, TestResponses.mediatedSuccessfulBanner());
        runBasicResultCBTest(SUCCESS, true);
    }

    // Verify that a response with a class that cannot be found,
    // makes the resultCB call with MEDIATED_SDK_UNAVAILABLE code
    @Test
    public void test2NoClassMediationCall() {
        Robolectric.addPendingHttpResponse(200, TestResponses.mediatedFakeClass());
        runBasicResultCBTest(MEDIATED_SDK_UNAVAILABLE, false);
    }

    // Verify that a response with a class that does not implement the mediation interface,
    // makes the resultCB call with MEDIATED_SDK_UNAVAILABLE code
    @Test
    public void test3BadClassMediationCall() {
        Robolectric.addPendingHttpResponse(200, TestResponses.mediatedDummyClass());
        runBasicResultCBTest(MEDIATED_SDK_UNAVAILABLE, false);
    }

    // Verify that a response with a class that does not make a request (times out),
    // makes the resultCB call with INTERNAL_ERROR code
    @Test
    public void test4NoRequestMediationCall() {
        // Create an AdRequest which will request a mediated response
        // that returns an class which does not make an ad request
        // then verify that the correct fail URL request was made
        NoRequestBannerView.didInstantiate = false;

        Robolectric.addPendingHttpResponse(200, TestResponses.mediatedNoRequest());
        runBasicResultCBTest(INTERNAL_ERROR, false);
        assertTrue(NoRequestBannerView.didInstantiate);
    }

    // Verify that a response with a class that throws an error,
    // makes the resultCB call with MEDIATED_SDK_UNAVAILABLE code
    @Test
    public void test5ErrorThrownMediationCall() {
        Robolectric.addPendingHttpResponse(200, TestResponses.mediatedOutOfMemory());
        runBasicResultCBTest(MEDIATED_SDK_UNAVAILABLE, false);
    }

    // Verify that a response with a class that hits callback UNABLE_TO_FILL,
    // makes the resultCB call with UNABLE_TO_FILL code
    @Test
    public void test6NoFillMediationCall() {
        // Create an AdRequest which will request a mediated response
        // that succeeds in instantiation but fails to return an ad
        // verify that the correct fail URL request was made
        Robolectric.addPendingHttpResponse(200, TestResponses.mediatedNoFill());
        runBasicResultCBTest(UNABLE_TO_FILL, false);
    }

    @Override
    public void onAdLoaded(AdView adView) {
        super.onAdLoaded(adView);
        Lock.unpause();
    }

    @Override
    public void onAdRequestFailed(AdView adView) {
        super.onAdRequestFailed(adView);
        Lock.unpause();
    }
}
