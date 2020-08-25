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
import com.appnexus.opensdk.shadows.ShadowSettings;
import com.appnexus.opensdk.testviews.MediatedNativeSuccessful;
import com.appnexus.opensdk.testviews.MediatedNativeSuccessful2;
import com.appnexus.opensdk.util.Lock;
import com.appnexus.opensdk.utils.Settings;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowWebView;

import static com.appnexus.opensdk.ResultCode.INTERNAL_ERROR;
import static com.appnexus.opensdk.ResultCode.MEDIATED_SDK_UNAVAILABLE;
import static com.appnexus.opensdk.ResultCode.SUCCESS;
import static com.appnexus.opensdk.ResultCode.UNABLE_TO_FILL;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * This Tests only the Native SDK mediation part. - On a general note the test try to cover the below items. Although individual tests might do more.
 * 1) Checks if ResponseURL is fired as GET.
 * 2) Checks if ResponseURL is fired with correct response code.
 * 3) Checks if ResponseURL is fired with latency populated.
 * 4) Also tries to assert if the SDK get the correct onAdLoaded/onAdFailed callback.
 * 5) Confirms if the response contains the set values for Mediated Native.
 */


@Config(sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class,
                ShadowWebView.class, ShadowSettings.class})
@RunWith(RobolectricTestRunner.class)

public class MediatedNativeAdViewControllerTest extends BaseNativeTest {
    boolean requestQueued = false;
    private static final boolean ASSERT_AD_LOAD_SUCESS = true;
    private static final boolean ASSERT_AD_LOAD_FAIL = false;
    private static final boolean CHECK_LATENCY_TRUE = true; // This is to be used where ever the test response mediated to a testview
    private static final boolean CHECK_LATENCY_FALSE = false; // This should be used in cases where the TestView is not available and we are not able to inject delay to populate latencyValue

    @Override
    public void setup() {
        super.setup();
        Settings.getSettings().ua = "";
        MediatedNativeSuccessful.didPass = false;
        MediatedNativeSuccessful2.didPass = false;
        Robolectric.getBackgroundThreadScheduler().reset();
        Robolectric.getForegroundThreadScheduler().reset();

    }

    // checks that the responseURL appends the reason code correctly
    private void assertResponseURL(int curRequestPositionInQueue, ResultCode errorCode,boolean checkLatency) {
        String response_url = "";
        try {
            for (int i = 1; i <= curRequestPositionInQueue; i++) {
                RecordedRequest request = server.takeRequest();
                if (i == curRequestPositionInQueue) {
                    response_url = request.getRequestLine();
                    System.out.print("response_URL::" + response_url + "\n");
                    assertTrue(response_url.startsWith("GET"));

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
                    Robolectric.getBackgroundThreadScheduler().advanceToNextPostedRunnable();
                    Robolectric.getForegroundThreadScheduler().advanceToNextPostedRunnable();
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



    //Helper Methods

    private void assertImpressionURL(int positionInQueue) {

        // Wait for Impression URL to Fire succesfully
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        RecordedRequest request = null;
        try {
            for (int i = 1; i <= positionInQueue; i++) {
                request = server.takeRequest();
                if (i == positionInQueue) {
                    String impression_url = request.getRequestLine();
                    System.out.print("impression_url::" + impression_url + "\n");
                    assertTrue(impression_url.startsWith("GET /impression_url? HTTP/1.1"));
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void executeUTRequest() {
        Robolectric.getBackgroundThreadScheduler().reset();
        Robolectric.getForegroundThreadScheduler().reset();
        adRequest.loadAd();
        waitForTasks();
        // execute main ad request
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        Robolectric.getBackgroundThreadScheduler().advanceToNextPostedRunnable();
        Robolectric.getForegroundThreadScheduler().advanceToNextPostedRunnable();
    }

    private void executeAndAssertResponseURL(int positionInQueue, ResultCode errorCode,boolean checkLatency) {
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertResponseURL(positionInQueue, errorCode,checkLatency);
    }

    // common format for several of the basic mediation tests
    public void runBasicMediationTest(ResultCode errorCode, boolean success, boolean checkLatency) {
        executeUTRequest();
        Lock.pause(ShadowSettings.MEDIATED_NETWORK_TIMEOUT + 1000);
        executeAndAssertResponseURL(2, errorCode,checkLatency);
        assertCallbacks(success);
        if(ResultCode.SUCCESS == errorCode.getCode()){
            assertEquals(MediatedNativeSuccessful.TITLE,response.getTitle());
            assertEquals(MediatedNativeSuccessful.DESCRIPTION,response.getDescription());
            assertEquals(MediatedNativeSuccessful.ADDITIONAL_DESCRIPTION,response.getAdditionalDescription());
            assertTrue(response.getImageSize().getHeight() == -1);
            assertTrue(response.getImageSize().getWidth() == -1);
            assertTrue(response.getIconSize().getHeight() == -1);
            assertTrue(response.getIconSize().getWidth() == -1);
            assertEquals(MediatedNativeSuccessful.ImageUrl,response.getImageUrl());
        }
    }

    /**
     * Basic Mediation tests, in particular, testing the responseURL
     */

    // Verify that a successful mediation response,
    // makes the responseURL call with SUCCESS code
    @Test
    public void testSucceedingMediationCall() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.mediatedSuccessfulNative()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));
        runBasicMediationTest(ResultCode.getNewInstance(SUCCESS), ASSERT_AD_LOAD_SUCESS, CHECK_LATENCY_TRUE);
        assertTrue(MediatedNativeSuccessful.params.equalsIgnoreCase("abc"));
        assertTrue(MediatedNativeSuccessful.uid.equalsIgnoreCase("1234"));

    }

    @Test
    public void testMediatedOnAdImpressionLogged() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.mediatedSuccessfulNative()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));
        runBasicMediationTest(ResultCode.getNewInstance(SUCCESS), ASSERT_AD_LOAD_SUCESS, CHECK_LATENCY_TRUE);
        assertTrue(MediatedNativeSuccessful.impressionLogged);
    }

    // Verify that a response with a class that cannot be found,
    // makes the responseURL call with MEDIATED_SDK_UNAVAILABLE code
    @Test
    public void testNoClassMediationCall() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.mediatedFakeClass_Native()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));// This is for No Ad URL
        runBasicMediationTest(ResultCode.getNewInstance(MEDIATED_SDK_UNAVAILABLE), ASSERT_AD_LOAD_FAIL,CHECK_LATENCY_FALSE);
        assertNoAdURL();
    }

    // Verify that a response with a class that does not implement the mediation interface,
    // makes the responseURL call with MEDIATED_SDK_UNAVAILABLE code
    @Test
    public void testBadClassMediationCall() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.mediatedDummyClass_Native()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));// This is for No Ad URL
        runBasicMediationTest(ResultCode.getNewInstance(MEDIATED_SDK_UNAVAILABLE), ASSERT_AD_LOAD_FAIL, CHECK_LATENCY_FALSE);
        assertNoAdURL();

        // This is to flush the No_Ad URL
        //waitForTasks();
        // execute main ad request
        //Robolectric.flushBackgroundThreadScheduler();
        //Robolectric.flushForegroundThreadScheduler();
    }

    // Verify that a response with a class that throws an error,
    // makes the responseURL call with MEDIATED_SDK_UNAVAILABLE code
    @Test
    public void testErrorThrownMediationCall() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.mediatedOutOfMemoryNative()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));// This is for No Ad URL
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
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.mediatedNoFillNative()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));// This is for No Ad URL
        runBasicMediationTest(ResultCode.getNewInstance(UNABLE_TO_FILL), ASSERT_AD_LOAD_FAIL, CHECK_LATENCY_TRUE);
        assertNoAdURL();
    }

    // Verify that a no_fill mediation response with a responseURL standard ad response,
    // transitions to the standard ad successfully
    @Test
    public void testNoFillMediationWithStandardResponseURL() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.noFillCSM_RTBNative()));
        //server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));
        runBasicMediationTest(ResultCode.getNewInstance(UNABLE_TO_FILL), ASSERT_AD_LOAD_SUCESS, CHECK_LATENCY_TRUE);

    }

    /**
     * Mediation Waterfall tests
     */

    // Verify that a response with 2 mediated ads stops after the first (successful) ad
    @Test
    public void testFirstSuccessfulSkipSecond() {
        String[] classNames = {"MediatedNativeSuccessful", "MediatedNativeSuccessful2"};
        String[] responseURLs = {TestResponsesUT.RESPONSE_URL, TestResponsesUT.RESPONSE_URL};
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.waterfall_CSM_Native(classNames, responseURLs)));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));

        runBasicMediationTest(ResultCode.getNewInstance(SUCCESS), ASSERT_AD_LOAD_SUCESS, CHECK_LATENCY_TRUE);
        assertTrue("Native " + MediatedNativeSuccessful.didPass, MediatedNativeSuccessful.didPass);
        assertFalse("Native2 " + MediatedNativeSuccessful2.didPass, MediatedNativeSuccessful2.didPass);
    }

    // Verify that a response with 2 mediated ads continues after the first (failure) ad
    // to succeeds on the second ad
    @Test
    public void testSkipFirstSuccessfulSecond() {
        String[] classNames = {"MediatedNativeNoFill", "MediatedNativeSuccessful2"};
        String[] responseURLs = {TestResponsesUT.RESPONSE_URL, TestResponsesUT.RESPONSE_URL};
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.waterfall_CSM_Native(classNames, responseURLs)));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));

        executeUTRequest();
        Lock.pause(ShadowSettings.MEDIATED_NETWORK_TIMEOUT + 1000);
        executeAndAssertResponseURL(2, ResultCode.getNewInstance(UNABLE_TO_FILL), CHECK_LATENCY_TRUE);
        //2 request are already taken out of queue current position of ResponseURL in queue is 1
        executeAndAssertResponseURL(1, ResultCode.getNewInstance(SUCCESS), CHECK_LATENCY_TRUE);
//        Lock.pause(Settings.MEDIATED_NETWORK_TIMEOUT + 1000);
        assertCallbacks(true);


        assertTrue(MediatedNativeSuccessful2.didPass);
        assertEquals(MediatedNativeSuccessful2.TITLE,response.getTitle());
        assertEquals(MediatedNativeSuccessful2.DESCRIPTION,response.getDescription());
        assertEquals(MediatedNativeSuccessful2.ADDITIONAL_DESCRIPTION,response.getAdditionalDescription());
        assertTrue(response.getImageSize().getHeight() == -1);
        assertTrue(response.getImageSize().getWidth() == -1);
        assertTrue(response.getIconSize().getHeight() == -1);
        assertTrue(response.getIconSize().getWidth() == -1);
        assertEquals(MediatedNativeSuccessful2.ImageUrl,response.getImageUrl());

    }


    // Verify that a response with 1 invalid mediated ads with a resultCB that
    // also responds with 1 invalid ad returns failure (UNABLE_TO_FILL)
    @Test
    public void testTestNoFill() {

        String[] classNames = {"MediatedNativeNoFill", "MediatedNativeNoFill"};
        String[] responseURLs = {TestResponsesUT.RESPONSE_URL, TestResponsesUT.RESPONSE_URL};
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.waterfall_CSM_Native(classNames, responseURLs)));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));// First ResponseURL
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));// Second Response URL
//        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));// This is for No Ad URL

        executeUTRequest();
        Lock.pause(ShadowSettings.MEDIATED_NETWORK_TIMEOUT + 1000);
        executeAndAssertResponseURL(2, ResultCode.getNewInstance(UNABLE_TO_FILL), CHECK_LATENCY_TRUE);
        //2 request are already taken out of queue current position of ResponseURL in queue is 1
        executeAndAssertResponseURL(1, ResultCode.getNewInstance(UNABLE_TO_FILL), CHECK_LATENCY_TRUE);

//        Lock.pause(Settings.MEDIATED_NETWORK_TIMEOUT + 1000);

        assertCallbacks(false);
        assertNoAdURL();
    }

    // Verify that the waterfall_CSM_Native continues normally if the responseURL is empty
    // or non-existent.
    @Test
    public void testNoResponseURL() {
        String[] classNames = {"FakeClass", "MediatedNativeNoFill", "MediatedNativeSuccessful"};
        String[] responseURLs = {"", null, TestResponsesUT.RESPONSE_URL};
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.waterfall_CSM_Native(classNames, responseURLs)));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank())); // For Response URL
        runBasicMediationTest(ResultCode.getNewInstance(SUCCESS), ASSERT_AD_LOAD_SUCESS, CHECK_LATENCY_TRUE);

        assertTrue(MediatedNativeSuccessful.didPass);
    }

    // Verify that the destroy() function gets called on a mediatedNative
    // when a new Native is being created
    @Test
    public void testDestroy() {
        String[] classNames = {"MediatedNativeNoFill", "MediatedNativeSuccessful2"};
        String[] responseURLs = {TestResponsesUT.RESPONSE_URL, TestResponsesUT.RESPONSE_URL};
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.waterfall_CSM_Native(classNames, responseURLs)));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));

        executeUTRequest();
        Lock.pause(ShadowSettings.MEDIATED_NETWORK_TIMEOUT + 1000);

        executeAndAssertResponseURL(2, ResultCode.getNewInstance(UNABLE_TO_FILL), CHECK_LATENCY_TRUE);
        //2 request are already taken out of queue current position of ResponseURL in queue is 1
        executeAndAssertResponseURL(1, ResultCode.getNewInstance(SUCCESS), CHECK_LATENCY_TRUE);

//        Lock.pause(Settings.MEDIATED_NETWORK_TIMEOUT + 1000);

        assertCallbacks(true);

        assertTrue(MediatedNativeSuccessful2.didPass);
    }



    // Verify that the Impression trackers are fired as expected.
    @Test
    public void testImpressionLogging() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.mediatedSuccessfulNative()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));// For response URL
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));// For Impression URL
        runBasicMediationTest(ResultCode.getNewInstance(SUCCESS), ASSERT_AD_LOAD_SUCESS, CHECK_LATENCY_TRUE);
        assertTrue(MediatedNativeSuccessful.params.equalsIgnoreCase("abc"));
        assertTrue(MediatedNativeSuccessful.uid.equalsIgnoreCase("1234"));

        assertImpressionURL(1);
    }

    @Override
    public void onAdLoaded(NativeAdResponse response) {
        super.onAdLoaded(response);
        Lock.unpause();
    }

    @Override
    public void onAdFailed(ResultCode errorcode, ANAdResponseInfo adResponseInfo) {
        super.onAdFailed(errorcode, adResponseInfo);
        Lock.unpause();
    }

}
