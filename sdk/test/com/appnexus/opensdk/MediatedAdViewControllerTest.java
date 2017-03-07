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

import android.view.View;

import com.appnexus.opensdk.shadows.ShadowAsyncTaskNoExecutor;
import com.appnexus.opensdk.shadows.ShadowSettings;
import com.appnexus.opensdk.shadows.ShadowWebSettings;
import com.appnexus.opensdk.testviews.DummyView;
import com.appnexus.opensdk.testviews.NoFillView;
import com.appnexus.opensdk.testviews.NoRequestBannerView;
import com.appnexus.opensdk.testviews.SuccessfulBanner;
import com.appnexus.opensdk.testviews.SuccessfulBanner2;
import com.appnexus.opensdk.util.Lock;
import com.appnexus.opensdk.util.RoboelectricTestRunnerWithResources;
import com.appnexus.opensdk.utils.Settings;
import com.squareup.okhttp.mockwebserver.MockResponse;

import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowWebView;
import org.robolectric.shadows.httpclient.FakeHttp;

import static com.appnexus.opensdk.ResultCode.INTERNAL_ERROR;
import static com.appnexus.opensdk.ResultCode.MEDIATED_SDK_UNAVAILABLE;
import static com.appnexus.opensdk.ResultCode.SUCCESS;
import static com.appnexus.opensdk.ResultCode.UNABLE_TO_FILL;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@Config(constants = BuildConfig.class, sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class,
                ShadowWebView.class, ShadowWebSettings.class, ShadowSettings.class})
@RunWith(RoboelectricTestRunnerWithResources.class)
public class MediatedAdViewControllerTest extends BaseViewAdTest {
    boolean requestQueued = false;

    @Override
    public void setup() {
        super.setup();
        requestManager = new AdViewRequestManager(bannerAdView);
        SuccessfulBanner.didPass = false;
        SuccessfulBanner2.didPass = false;
    }

    // checks that the resultCB appends the reason code correctly
    private void assertResultCB(int requestNumber, ResultCode errorCode) {
        //@TODO need to fix this. Take and verify the GET result from Mockserver.
/*        HttpUriRequest sentResultCBRequest = (HttpUriRequest) FakeHttp.getSentHttpRequest(requestNumber);
        String resultCBUri = sentResultCBRequest.getURI().toString();
        String result = TestResponses.resultCB(errorCode.ordinal());

        assertTrue(sentResultCBRequest.getMethod().equals("GET"));
        assertTrue(resultCBUri.startsWith(result));*/
    }

    private void executeMediationAdRequest() {
        requestManager.execute();
        // execute main ad request
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
    }

    private void executeResultCBRequest() {
        Robolectric.getForegroundThreadScheduler().advanceToLastPostedRunnable();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
    }

    // common format for several of the basic mediation tests
    public void runBasicResultCBTest(ResultCode errorCode, boolean success) {
        executeMediationAdRequest();
        executeResultCBRequest();
        Lock.pause(Settings.MEDIATED_NETWORK_TIMEOUT + 1000);

        assertResultCB(1, errorCode);
        assertCallbacks(success);
    }

    /**
     * Basic Mediation tests, in particular, testing the resultCB
     */

    // Verify that a successful mediation response,
    // makes the resultCB call with SUCCESS code
    @Test
    public void test1SucceedingMediationCall() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.mediatedSuccessfulBanner()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.blank()));
        runBasicResultCBTest(SUCCESS, true);
    }

    // Verify that a response with a class that cannot be found,
    // makes the resultCB call with MEDIATED_SDK_UNAVAILABLE code
    @Test
    public void test2NoClassMediationCall() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.mediatedFakeClass()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.blank()));
        runBasicResultCBTest(MEDIATED_SDK_UNAVAILABLE, false);
    }

    // Verify that a response with a class that does not implement the mediation interface,
    // makes the resultCB call with MEDIATED_SDK_UNAVAILABLE code
    @Test
    public void test3BadClassMediationCall() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.mediatedDummyClass()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.blank()));
        runBasicResultCBTest(MEDIATED_SDK_UNAVAILABLE, false);
    }

    // Verify that a response with a class that does not make a request (times out),
    // makes the resultCB call with INTERNAL_ERROR code
    @Test
    public void test4NoRequestMediationCall() {
        // Create an AdRequest which will request a mediated response
        // that returns an class which does not make an ad request
        // then verify that the correct fail URL request was made
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.mediatedNoRequest()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.blank()));
        runBasicResultCBTest(INTERNAL_ERROR, false);
        assertTrue(NoRequestBannerView.didInstantiate);
    }

    // Verify that a response with a class that throws an error,
    // makes the resultCB call with MEDIATED_SDK_UNAVAILABLE code
    @Test
    public void test5ErrorThrownMediationCall() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.mediatedOutOfMemory()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.blank()));
        runBasicResultCBTest(INTERNAL_ERROR, false);
    }

    // Verify that a response with a class that hits callback UNABLE_TO_FILL,
    // makes the resultCB call with UNABLE_TO_FILL code
    @Test
    public void test6NoFillMediationCall() {
        // Create an AdRequest which will request a mediated response
        // that succeeds in instantiation but fails to return an ad
        // verify that the correct fail URL request was made
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.mediatedNoFill()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.blank()));
        runBasicResultCBTest(UNABLE_TO_FILL, false);
    }

    // Verify that a no_fill mediation response with a resultCB standard ad response,
    // transitions to the standard ad successfully
    @Test
    public void test7NoFillMediationWithStandardResultCB() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.mediatedNoFill()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.banner()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.blank()));
        runBasicResultCBTest(UNABLE_TO_FILL, true);
        // check that the standard ad was loaded
        View view = bannerAdView.getChildAt(0);
        assertTrue(view instanceof AdWebView);
    }

    // Verify that a standard ad can transition to a mediated ad successfully
    @Test
    public void test8StandardThenMediated() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.banner()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.mediatedSuccessfulBanner()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.blank()));

        // load a standard ad
        requestManager.execute();

        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        Lock.pause(Settings.MEDIATED_NETWORK_TIMEOUT);

        View view = bannerAdView.getChildAt(0);
        assertTrue(view instanceof AdWebView);
        assertCallbacks(true);

        adLoaded = false;

        // load a mediated ad
        requestManager = new AdViewRequestManager(bannerAdView);
        requestManager.execute();

        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        Robolectric.getBackgroundThreadScheduler().advanceToLastPostedRunnable();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        Lock.pause(Settings.MEDIATED_NETWORK_TIMEOUT);

        View mediatedView = bannerAdView.getChildAt(0);
        assertNotNull(mediatedView);
        assertEquals(DummyView.dummyView, mediatedView);
        assertCallbacks(true);
    }

    // Verify that a 404 resultCB is handled properly
    @Test
    public void test9Http404ErrorResponseFromSuccess() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.mediatedSuccessfulBanner()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.blank()));

        executeMediationAdRequest();
        executeResultCBRequest();

        assertResultCB(1, SUCCESS);
        assertCallbacks(true);
    }

    // Verify that a 404 resultCB is handled properly
    @Test
    public void test9Http404ErrorResponseFromFailure() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.mediatedNoFill()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.blank()));

        executeMediationAdRequest();
        executeResultCBRequest();

        assertResultCB(1, UNABLE_TO_FILL);
        assertCallbacks(false);
    }

    /**
     * Mediation Waterfall tests
     */

    // Verify that a response with 2 mediated ads stops after the first (successful) ad
    @Test
    public void test11FirstSuccessfulSkipSecond() {
        String[] classNames = {"SuccessfulBanner", "SuccessfulBanner2"};
        String[] resultCBs = {TestResponses.RESULTCB, TestResponses.RESULTCB};
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.waterfall(classNames, resultCBs)));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.blank()));

        runBasicResultCBTest(SUCCESS, true);
        assertTrue("Banner " + SuccessfulBanner.didPass, SuccessfulBanner.didPass);
        assertFalse("Banner2 " + SuccessfulBanner2.didPass, SuccessfulBanner2.didPass);
    }

    // Verify that a response with 2 mediated ads continues after the first (failure) ad
    // to succeeds on the second ad
    @Test
    public void test12SkipFirstSuccessfulSecond() {
        String[] classNames = {"NoFillView", "SuccessfulBanner2"};
        String[] resultCBs = {TestResponses.RESULTCB, TestResponses.RESULTCB};
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.waterfall(classNames, resultCBs)));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.blank()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.blank()));

        executeMediationAdRequest();
        executeResultCBRequest();
        executeResultCBRequest();

        Lock.pause(Settings.MEDIATED_NETWORK_TIMEOUT + 1000);

        assertResultCB(1, UNABLE_TO_FILL);
        assertResultCB(2, SUCCESS);
        assertCallbacks(true);

        assertTrue(SuccessfulBanner2.didPass);
    }

/*    // Verify that a response with 2 mediated ads with an overriding resultCB
    // skips the second ad and follows the result (standard ad)
    @Test
    public void test13FirstFailsIntoOverrideStd() {
        String[] classNames = {"NoFillView", "SuccessfulBanner2"};
        String[] resultCBs = {TestResponses.RESULTCB, TestResponses.RESULTCB};
        Robolectric.addPendingHttpResponse(200, TestResponses.waterfall(classNames, resultCBs));
        Robolectric.addPendingHttpResponse(200, TestResponses.banner());

        runBasicResultCBTest(UNABLE_TO_FILL, true);

        assertFalse(SuccessfulBanner2.didPass);
    }*/

/*    // Verify that a response with 2 mediated ads with an overriding resultCB
    // skips the second ad and follows the result (mediated ad)
    @Test
    public void test14FirstFailsIntoOverrideMediated() {
        String[] classNames = {"NoFillView", "SuccessfulBanner2"};
        String[] resultCBs = {TestResponses.RESULTCB, TestResponses.RESULTCB};
        Robolectric.addPendingHttpResponse(200, TestResponses.waterfall(classNames, resultCBs));
        Robolectric.addPendingHttpResponse(200, TestResponses.mediatedSuccessfulBanner());
        Robolectric.addPendingHttpResponse(200, TestResponses.blank());

        executeMediationAdRequest();
        executeResultCBRequest();
        executeResultCBRequest();

        Lock.pause(Settings.MEDIATED_NETWORK_TIMEOUT + 1000);

        assertResultCB(1, UNABLE_TO_FILL);
        assertResultCB(2, SUCCESS);
        assertCallbacks(true);

        assertFalse(SuccessfulBanner2.didPass);
        assertTrue(SuccessfulBanner.didPass);
    }*/

    // Verify that a response with 1 invalid mediated ads with a resultCB that
    // also responds with 1 invalid ad returns failure (UNABLE_TO_FILL)
    @Test
    public void test15TestNoFill() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.mediatedNoFill()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.mediatedNoFill()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.blank()));

        executeMediationAdRequest();
        executeResultCBRequest();
        executeResultCBRequest();

        Lock.pause(Settings.MEDIATED_NETWORK_TIMEOUT + 1000);

        assertResultCB(1, UNABLE_TO_FILL);
        assertResultCB(2, UNABLE_TO_FILL);
        assertCallbacks(false);
    }

    // Verify that the waterfall continues normally if the resultCB is empty
    // or non-existent.
    @Test
    public void test16NoResultCB() {
        String[] classNames = {"FakeClass", "NoFillView", "SuccessfulBanner"};
        String[] resultCBs = {"", null, TestResponses.RESULTCB};
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.waterfall(classNames, resultCBs)));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.blank()));
        runBasicResultCBTest(SUCCESS, true);

        assertTrue(SuccessfulBanner.didPass);
    }

    // Verify that the destroy() function gets called on a mediatedBanner
    // when a new banner is being created
    @Test
    public void testDestroy() {
        String[] classNames = {"NoFillView", "SuccessfulBanner2"};
        String[] resultCBs = {TestResponses.RESULTCB, TestResponses.RESULTCB};
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.waterfall(classNames, resultCBs)));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.blank()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.blank()));

        executeMediationAdRequest();
        executeResultCBRequest();
        executeResultCBRequest();

        Lock.pause(Settings.MEDIATED_NETWORK_TIMEOUT + 1000);

        assertResultCB(1, UNABLE_TO_FILL);
        assertResultCB(2, SUCCESS);
        assertCallbacks(true);

        assertTrue(SuccessfulBanner2.didPass);
        assertTrue(NoFillView.didDestroy);
    }

    @Override
    public void onAdLoaded(AdView adView) {
        super.onAdLoaded(adView);
        Lock.unpause();
    }

    @Override
    public void onAdRequestFailed(AdView adView, ResultCode resultCode) {
        super.onAdRequestFailed(adView, resultCode);
        Lock.unpause();
    }

}
