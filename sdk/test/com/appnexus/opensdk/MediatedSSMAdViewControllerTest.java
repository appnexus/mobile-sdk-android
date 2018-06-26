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
import com.appnexus.opensdk.shadows.ShadowWebSettings;
import com.appnexus.opensdk.util.Lock;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.util.concurrent.TimeUnit;

import static com.appnexus.opensdk.ResultCode.SUCCESS;
import static com.appnexus.opensdk.ResultCode.UNABLE_TO_FILL;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * This Tests only the SSM mediation part. - On a general note the test try to cover the below items. Although individual tests might do more.
 * 1) Checks if ResponseURL is fired as GET.
 * 2) Checks if ResponseURL is fired with correct response code.
 * 3) Checks if ResponseURL is fired with latency populated.
 * 4) Also tries to assert if the SDK get the correct onAdLoaded/onAdFailed callback.
 */


@Config(constants = BuildConfig.class, sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class,
                ShadowCustomWebView.class})
@RunWith(RobolectricTestRunner.class)
public class MediatedSSMAdViewControllerTest extends BaseViewAdTest {
    boolean requestQueued = false;
    private static final boolean ASSERT_AD_LOAD_SUCESS = true;
    private static final boolean ASSERT_AD_LOAD_FAIL = false;
    private static final boolean CHECK_LATENCY_TRUE = true; // This is to be used where ever the test response mediated to a testview
    private static final boolean CHECK_LATENCY_FALSE = false; // This should be used in cases where the TestView is not available and we are not able to inject delay to populate latencyValue


    @Override
    public void setup() {
        super.setup();
        requestManager = new AdViewRequestManager(bannerAdView);

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

                    int reasonVal = Integer.parseInt(sanitizer.getValue("reason"));
                    String str_total_latencyVal = sanitizer.getValue("total_latency");
                    int totalLatencyVal = Integer.parseInt(str_total_latencyVal.replace("_HTTP/1.1", ""));

                    assertEquals(reasonVal, errorCode.ordinal());
                    assertTrue(totalLatencyVal >= 1); // should be greater than 0 at the minimum and should be present in the response

                    if(checkLatency) {
                        String str_latencyVal = sanitizer.getValue("latency");
                        int latencyVal = Integer.parseInt(str_latencyVal);
                        assertTrue(latencyVal > 0); // should be greater than 0 at the minimum and should be present in the response
                    }

                }
            }
        } catch (InterruptedException e) {
            System.out.print("/InterruptedException" + errorCode.ordinal());
            e.printStackTrace();
        } catch (Exception e) {
            fail();
        }
    }

    private void assertNoAdURL(RecordedRequest request) {
            String no_AdURL = request.getRequestLine();
            System.out.print("no_ad_URL::" + no_AdURL + "\n");
            assertTrue(no_AdURL.startsWith("GET /no_ad? HTTP/1.1"));
    }

    private RecordedRequest takeNoAdURLRequestFromQueue(int position){
        RecordedRequest request = null;
        for (int i = 1; i <= position; i++) {
            try {
                request  = server.takeRequest();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return request;
    }

    private void executeUTRequest() {
        requestManager.execute();
        // execute main ad request
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
    }

    private void executeAndAssertResponseURL(int positionInQueue, ResultCode errorCode,boolean checkLatency) {
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertResponseURL(positionInQueue, errorCode, checkLatency);
    }

    private void executeAndAssertNoAdURL(int positionInQueue) {
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertNoAdURL(takeNoAdURLRequestFromQueue(positionInQueue));
    }


    // common format for several of the basic mediation tests
    public void runBasicSSMMediationTest(ResultCode errorCode, boolean success, boolean checkLatency) {
        executeUTRequest();
        executeSSMRequest();

        if(!success){
            executeAndAssertNoAdURL(3);
            executeAndAssertResponseURL(1, errorCode, checkLatency);
        }else{
            executeAndAssertResponseURL(3, errorCode, checkLatency);
        }


        assertCallbacks(success);
    }


    private void executeSSMRequest(){
        // Execute the SSM Request
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();


    }

    // Verify that a successful mediation response,
    // makes the responseURL call with SUCCESS code
    @Test
    public void testSucceedingSSMMediationCall() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.mediatedSSMBanner()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.DUMMY_BANNER_CONTENT).setBodyDelay(2, TimeUnit.MILLISECONDS));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));
        runBasicSSMMediationTest(SUCCESS, ASSERT_AD_LOAD_SUCESS, CHECK_LATENCY_TRUE);
    }

    // Verify that a response with a class that cannot be found,
    // makes the responseURL call with MEDIATED_SDK_UNAVAILABLE code
    @Test
    public void testFailureSSMMediationCall() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.mediatedSSMBanner()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()).setBodyDelay(25,TimeUnit.MILLISECONDS)); // Status 200 but no Ad from SSM handler
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank())); // This is for Response URL
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));// This is for No Ad URL
        executeUTRequest();

        executeSSMRequest();

        executeAndAssertResponseURL(3, UNABLE_TO_FILL, CHECK_LATENCY_FALSE);
        //2 request are already taken out of queue current position of ResponseURL in queue is 1

        executeAndAssertNoAdURL(1);

        assertCallbacks(ASSERT_AD_LOAD_FAIL);

        //assertTrue(MediatedBannerSuccessful2.didPass);
    }



    // Verify that a response with a class that cannot be found,
    // makes the responseURL call with MEDIATED_SDK_UNAVAILABLE code
    @Test
    public void test404FailureSSMMediationCall() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.mediatedSSMBanner()));
        server.enqueue(new MockResponse().setResponseCode(404)); // Status 404
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank())); // This is for Response URL
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));// This is for No Ad URL
        //runBasicSSMMediationTest(SUCCESS, ASSERT_AD_LOAD_SUCESS, CHECK_LATENCY_TRUE);

        executeUTRequest();

        executeSSMRequest();

        executeAndAssertResponseURL(3, UNABLE_TO_FILL, CHECK_LATENCY_TRUE);

        executeAndAssertNoAdURL(1);

        assertCallbacks(ASSERT_AD_LOAD_FAIL);

        //assertTrue(MediatedBannerSuccessful2.didPass);
    }



    // Verify that a response with 2 mediated ads stops after the first (successful) ad
    @Test
    public void testFirstSuccessfulSkipSecond() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.waterfall_SSM_Banner_Interstitial(2)));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.DUMMY_BANNER_CONTENT).setBodyDelay(2, TimeUnit.MILLISECONDS)); // SSM Response
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank())); // Response URL
        runBasicSSMMediationTest(SUCCESS, ASSERT_AD_LOAD_SUCESS, CHECK_LATENCY_TRUE);

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
