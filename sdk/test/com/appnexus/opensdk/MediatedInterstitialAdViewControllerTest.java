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

import android.net.UrlQuerySanitizer;

import com.appnexus.opensdk.shadows.ShadowAsyncTaskNoExecutor;
import com.appnexus.opensdk.shadows.ShadowCustomWebView;
import com.appnexus.opensdk.shadows.ShadowSettings;
import com.appnexus.opensdk.testviews.MediatedInterstitialNoFillView;
import com.appnexus.opensdk.testviews.MediatedInterstitialNoRequest;
import com.appnexus.opensdk.testviews.MediatedInterstitialSuccessful;
import com.appnexus.opensdk.testviews.MediatedInterstitialSuccessful2;
import com.appnexus.opensdk.util.Lock;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.appnexus.opensdk.ResultCode.INTERNAL_ERROR;
import static com.appnexus.opensdk.ResultCode.MEDIATED_SDK_UNAVAILABLE;
import static com.appnexus.opensdk.ResultCode.SUCCESS;
import static com.appnexus.opensdk.ResultCode.UNABLE_TO_FILL;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * This Tests only the Interstitial SDK mediation part. - On a general note the test try to cover the below items. Although individual tests might do more.
 * 1) Checks if ResponseURL is fired as GET.
 * 2) Checks if ResponseURL is fired with correct response code.
 * 3) Checks if ResponseURL is fired with latency populated.
 * 4) Also tries to assert if the SDK get the correct onAdLoaded/onAdFailed callback.
 * 5) Confirms isReady() and showAd() for Interstitial are fired
 */


@Config(sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class,
                ShadowCustomWebView.class, ShadowSettings.class})
@RunWith(RobolectricTestRunner.class)
public class MediatedInterstitialAdViewControllerTest extends BaseViewAdTest {
    boolean requestQueued = false;
    private static final boolean ASSERT_AD_LOAD_SUCESS = true;
    private static final boolean ASSERT_AD_LOAD_FAIL = false;
    private static final boolean CHECK_LATENCY_TRUE = true; // This is to be used where ever the test response mediated to a testview
    private static final boolean CHECK_LATENCY_FALSE = false; // This should be used in cases where the TestView is not available and we are not able to inject delay to populate latencyValue

    @Override
    public void setup() {
        super.setup();
        requestManager = new AdViewRequestManager(interstitialAdView);
        MediatedInterstitialSuccessful.didPass = false;
        MediatedInterstitialSuccessful2.didPass = false;
        MediatedInterstitialSuccessful.showCalled = false;
        MediatedInterstitialSuccessful2.showCalled = false;

    }

    // checks that the responseURL appends the reason code correctly
    private void assertResponseURL(int curRequestPositionInQueue, ResultCode errorCode, boolean checkLatency) {
        String response_url = "";
        try {
            for (int i = 1; i <= curRequestPositionInQueue; i++) {
                RecordedRequest request = server.takeRequest();
                if (i == curRequestPositionInQueue) {
                    response_url = request.getRequestLine();
                    System.out.print("response_URL::" + response_url + "\n");
                    assertTrue(response_url.startsWith("GET"));
                    System.out.print("/response_url?&reason=" + errorCode.getCode() + "\n");
                    assertTrue(response_url.contains("/response_url?&reason=" + errorCode.getCode()));

                    UrlQuerySanitizer sanitizer = new UrlQuerySanitizer();
                    sanitizer.setAllowUnregisteredParamaters(true);
                    sanitizer.parseUrl(response_url);

                    int reasonVal = Integer.parseInt(sanitizer.getValue("reason").replace("_HTTP/1.1", ""));

                    assertEquals(reasonVal, errorCode.getCode());

                    if(checkLatency) {
                        String str_latencyVal = sanitizer.getValue("latency");
                        int latencyVal = Integer.parseInt(str_latencyVal.replace("_HTTP/1.1", ""));
                        assertTrue(latencyVal > 0); // should be greater than 0 at the minimum and should be present in the response
                    }


                }
            }
        } catch (InterruptedException e) {
            System.out.print("/InterruptedException" + errorCode.getCode());
            e.printStackTrace();
        } catch (Exception e) {
            fail();
        }

    }

    private void assertNoAdURL() {
        RecordedRequest request = null;
        try {
            request = server.takeRequest();
            String no_AdURL = request.getRequestLine();
            System.out.print("no_ad_URL::" + no_AdURL + "\n");
            assertTrue(no_AdURL.startsWith("GET /no_ad? HTTP/1.1"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void executeUTRequest() {
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();
        requestManager.execute();
        // execute main ad request
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
    }

    private void executeAndAssertResponseURL(int positionInQueue, ResultCode errorCode, boolean checkLatency) {
//        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertResponseURL(positionInQueue, errorCode, checkLatency);
    }

    // common format for several of the basic mediation tests
    public void runBasicMediationTest(ResultCode errorCode, boolean success, boolean checkLatency) {
        executeUTRequest();

        executeAndAssertResponseURL(2, errorCode, checkLatency);
        assertCallbacks(success);
        assertEquals(success,interstitialAdView.isReady());
        if(ResultCode.SUCCESS == errorCode.getCode()){
            interstitialAdView.show();
            assertTrue(MediatedInterstitialSuccessful.showCalled);
        }
    }

    /**
     * Basic Mediation tests, in particular, testing the responseURL
     */

    // Verify that a successful mediation response,
    // makes the responseURL call with SUCCESS code
    @Test
    public void testSucceedingMediationCall() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.mediatedSuccessfulInterstitial()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));
        runBasicMediationTest(ResultCode.getNewInstance(SUCCESS), ASSERT_AD_LOAD_SUCESS, CHECK_LATENCY_TRUE);
    }

    // Verify that a response with a class that cannot be found,
    // makes the responseURL call with MEDIATED_SDK_UNAVAILABLE code
    @Test
    public void testNoClassMediationCall() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.mediatedFakeClassBannerInterstitial()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank())); // This is for NO_AD url
        runBasicMediationTest(ResultCode.getNewInstance(MEDIATED_SDK_UNAVAILABLE), ASSERT_AD_LOAD_FAIL, CHECK_LATENCY_FALSE);
        assertNoAdURL();
    }

    // Verify that a response with a class that does not implement the mediation interface,
    // makes the responseURL call with MEDIATED_SDK_UNAVAILABLE code
    @Test
    public void testBadClassMediationCall() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.mediatedDummyClassBannerInterstitial()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank())); // This is for NO_AD url
        runBasicMediationTest(ResultCode.getNewInstance(MEDIATED_SDK_UNAVAILABLE), ASSERT_AD_LOAD_FAIL, CHECK_LATENCY_FALSE);
        assertNoAdURL();
    }

    // Verify that a response with a class that does not make a request (times out),
    // makes the responseURL call with INTERNAL_ERROR code
    @Test
    public void testNoRequestMediationCall() {
        // Create an AdRequest which will request a mediated response
        // that returns an class which does not make an ad request
        // then verify that the correct fail URL request was made
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.mediatedNoRequestInterstitial()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank())); // This is for NO_AD url
        requestManager.execute();

        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.toString();
            //fail(e.toString());
        }
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        executeAndAssertResponseURL(2, ResultCode.getNewInstance(INTERNAL_ERROR), CHECK_LATENCY_TRUE);
        if (Robolectric.getBackgroundThreadScheduler().areAnyRunnable()) {
            Robolectric.flushBackgroundThreadScheduler();
        }
        if (Robolectric.getForegroundThreadScheduler().areAnyRunnable()) {
            Robolectric.flushForegroundThreadScheduler();
        }


        assertCallbacks(false);
        assertTrue(MediatedInterstitialNoRequest.didInstantiate);
        assertFalse(interstitialAdView.isReady());
        assertNoAdURL();
    }

    // Verify that a response with a class that throws an error,
    // makes the responseURL call with MEDIATED_SDK_UNAVAILABLE code
    @Test
    public void testErrorThrownMediationCall() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.mediatedOutOfMemoryInterstitial()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank())); // This is for NO_AD url
        runBasicMediationTest(ResultCode.getNewInstance(INTERNAL_ERROR), ASSERT_AD_LOAD_FAIL, CHECK_LATENCY_TRUE);
        assertNoAdURL();
    }

    // Verify that a response with a class that hits callback UNABLE_TO_FILL,
    // makes the responseURL call with UNABLE_TO_FILL code
    @Test
    public void testNoFillMediationCall() {
        // Create an AdRequest which will request a mediated response
        // that succeeds in instantiation but fails to return an ad
        // verify that the correct fail URL request was made
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.mediatedNoFillInterstitial()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank())); // This is for NO_AD url
        runBasicMediationTest(ResultCode.getNewInstance(UNABLE_TO_FILL), ASSERT_AD_LOAD_FAIL, CHECK_LATENCY_TRUE);
        assertNoAdURL();
    }

    // Verify that a no_fill mediation response with a responseURL standard ad response,
    // transitions to the standard ad successfully
    @Test
    public void testNoFillMediationWithRTB() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.noFillCSM_RTBInterstitial()));
        //server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));
        runBasicMediationTest(ResultCode.getNewInstance(UNABLE_TO_FILL), ASSERT_AD_LOAD_SUCESS, CHECK_LATENCY_TRUE);
    }

    // Verify that a 404 responseURL is handled properly
    @Test
    public void testHttp404ErrorResponseFromSuccess() {
        String[] classNames = {"MediatedInterstitialSuccessful"};
        String[] responseURLs = {"http://wiki221random.devnxs.net/"};
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.waterfall_CSM_Banner_Interstitial(classNames, responseURLs)));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));

        executeUTRequest();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertCallbacks(true);
        assertTrue(interstitialAdView.isReady());
    }

    // Verify that a 404 responseURL is handled properly
    @Test
    public void testHttp404ErrorResponseFromFailure() {
        String[] classNames = {"MediatedInterstitialNoFillView"};
        String[] responseURLs = {"http://wiki221random.devnxs.net/"};
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.waterfall_CSM_Banner_Interstitial(classNames, responseURLs)));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));

        executeUTRequest();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertCallbacks(false);
        assertFalse(interstitialAdView.isReady());
    }

    /**
     * Mediation Waterfall tests
     */

    // Verify that a response with 2 mediated ads stops after the first (successful) ad
    @Test
    public void testFirstSuccessfulSkipSecond() {
        String[] classNames = {"MediatedInterstitialSuccessful", "MediatedInterstitialSuccessful2"};
        String[] responseURLs = {TestResponsesUT.RESPONSE_URL, TestResponsesUT.RESPONSE_URL};
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.waterfall_CSM_Banner_Interstitial(classNames, responseURLs)));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));

        runBasicMediationTest(ResultCode.getNewInstance(SUCCESS), ASSERT_AD_LOAD_SUCESS, CHECK_LATENCY_TRUE);
        assertTrue("Interstitial " + MediatedInterstitialSuccessful.didPass, MediatedInterstitialSuccessful.didPass);
        assertFalse("Interstitial2 " + MediatedInterstitialSuccessful2.didPass, MediatedInterstitialSuccessful2.didPass);
    }

    // Verify that a response with 2 mediated ads continues after the first (failure) ad
    // to succeeds on the second ad
    @Test
    public void testSkipFirstSuccessfulSecond() {
        String[] classNames = {"MediatedInterstitialNoFillView", "MediatedInterstitialSuccessful2"};
        String[] responseURLs = {TestResponsesUT.RESPONSE_URL, TestResponsesUT.RESPONSE_URL};
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.waterfall_CSM_Banner_Interstitial(classNames, responseURLs)));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));

        executeUTRequest();
        executeAndAssertResponseURL(2, ResultCode.getNewInstance(UNABLE_TO_FILL), CHECK_LATENCY_TRUE);
        //2 request are already taken out of queue current position of ResponseURL in queue is 1
        executeAndAssertResponseURL(1, ResultCode.getNewInstance(SUCCESS), CHECK_LATENCY_TRUE);


        assertCallbacks(true);
        assertTrue(interstitialAdView.isReady());

        assertTrue(MediatedInterstitialSuccessful2.didPass);
        interstitialAdView.show();
        assertTrue(MediatedInterstitialSuccessful2.showCalled);
    }


    // Verify that a response with 1 invalid mediated ads with a resultCB that
    // also responds with 1 invalid ad returns failure (UNABLE_TO_FILL)
    @Test
    public void testTestNoFill() {

        String[] classNames = {"MediatedInterstitialNoFillView", "MediatedInterstitialNoFillView"};
        String[] responseURLs = {TestResponsesUT.RESPONSE_URL, TestResponsesUT.RESPONSE_URL};
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.waterfall_CSM_Banner_Interstitial(classNames, responseURLs)));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank())); // This is for NO_AD url

        executeUTRequest();
        executeAndAssertResponseURL(2, ResultCode.getNewInstance(UNABLE_TO_FILL), CHECK_LATENCY_TRUE);
        //2 request are already taken out of queue current position of ResponseURL in queue is 1
        executeAndAssertResponseURL(1, ResultCode.getNewInstance(UNABLE_TO_FILL), CHECK_LATENCY_TRUE);

        Lock.pause(ShadowSettings.MEDIATED_NETWORK_TIMEOUT + 1000);

        assertCallbacks(false);
        assertFalse(interstitialAdView.isReady());
        assertNoAdURL();
    }

    // Verify that the waterfall_CSM_Banner_Interstitial continues normally if the responseURL is empty
    // or non-existent.
    @Test
    public void testNoResponseURL() {
        String[] classNames = {"FakeClass", "MediatedInterstitialNoFillView", "MediatedInterstitialSuccessful"};
        String[] responseURLs = {"", null, TestResponsesUT.RESPONSE_URL};
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.waterfall_CSM_Banner_Interstitial(classNames, responseURLs)));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));
        runBasicMediationTest(ResultCode.getNewInstance(SUCCESS), ASSERT_AD_LOAD_SUCESS, CHECK_LATENCY_TRUE);

        assertTrue(MediatedInterstitialSuccessful.didPass);
    }

    // Verify that the destroy() function gets called on a mediatedInterstitial
    // when a new Interstitial is being created
    @Test
    public void testDestroy() {
        String[] classNames = {"MediatedInterstitialNoFillView", "MediatedInterstitialSuccessful2"};
        String[] responseURLs = {TestResponsesUT.RESPONSE_URL, TestResponsesUT.RESPONSE_URL};
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.waterfall_CSM_Banner_Interstitial(classNames, responseURLs)));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));

        executeUTRequest();
        executeAndAssertResponseURL(2, ResultCode.getNewInstance(UNABLE_TO_FILL), CHECK_LATENCY_TRUE);
        //2 request are already taken out of queue current position of ResponseURL in queue is 1
        executeAndAssertResponseURL(1, ResultCode.getNewInstance(SUCCESS), CHECK_LATENCY_TRUE);

        Lock.pause(ShadowSettings.MEDIATED_NETWORK_TIMEOUT + 1000);

        assertCallbacks(true);

        assertTrue(MediatedInterstitialSuccessful2.didPass);
        assertTrue(MediatedInterstitialNoFillView.didDestroy);
        assertTrue(interstitialAdView.isReady());
        interstitialAdView.show();
        assertTrue(MediatedInterstitialSuccessful2.showCalled);
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
