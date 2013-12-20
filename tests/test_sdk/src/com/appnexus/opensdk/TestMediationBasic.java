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

import android.test.AndroidTestCase;
import com.appnexus.opensdk.*;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.testviews.*;
import com.appnexus.opensdk.util.Lock;
import com.appnexus.opensdk.util.TestUtil;

// many of the old mediation tests (1-10) are broken because we don't allow more than one successful adview
// comment out the return code in MAVC to allow multiple successes and make the test pass
public class TestMediationBasic extends AndroidTestCase implements AdRequester {
    String oldUrl;
    AdRequest request;
    String placementId;
    boolean didFail;
    private static final long WAIT_TIME = 10000;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Clog.w(TestUtil.testLogTag, "Setting up new test");
        oldUrl = Settings.getSettings().BASE_URL;
        Settings.getSettings().BASE_URL = TestUtil.MEDIATION_TEST_URL;
        Clog.w(TestUtil.testLogTag, "BASE_URL set to " + Settings.getSettings().BASE_URL);
        didFail = false;
        SuccessfulMediationView.didPass = false;
        SecondSuccessfulMediationView.didPass = false;
        ThirdSuccessfulMediationView.didPass = false;
        NoSDK.didPass = false;
        ThirdSuccessfulMediationView.didPass = false;
        DummyView.createView(getContext());
    }

    @Override
    synchronized protected void tearDown() throws Exception {
        wait(1000);
        if ((SuccessfulMediationView.didPass && SecondSuccessfulMediationView.didPass && ThirdSuccessfulMediationView.didPass)
                || (NoSDK.didPass && ThirdSuccessfulMediationView.didPass))
            Clog.w(TestUtil.testLogTag, "TEST PASSED #" + placementId);
        Settings.getSettings().BASE_URL = oldUrl;
        super.tearDown();
    }

    /**
     * Create a AdRequest which will request a mediated response to
     * instantiate the SuccessfulMediationView
     */
    public void test1SucceedingMediationCall() {
        placementId = "1";
        request = new AdRequest(this, null, null, null, placementId, null, null, 320, 50, -1, -1, null, null, null, true, null, false, false);

        request.execute();
        Lock.pause(WAIT_TIME);
        request.cancel(true);

        assertTrue(SuccessfulMediationView.didPass);
        assertTrue(SecondSuccessfulMediationView.didPass);
        assertTrue(ThirdSuccessfulMediationView.didPass);
        assertFalse(didFail);
    }

    /*
    * All of these tests follow the same pattern:
    * Initial call directs them to a MediatedBannerView class unique to the test case
    * the following call will go to NoSDK.
    * the following call will go to ThirdSuccessfulMediationView to confirm success
     */
    private void runStandardTest() {
        request = new AdRequest(this, null, null, null, placementId, null, null, 320, 50, -1, -1, null, null, null, true, null, false, false);

        request.execute();
        Lock.pause(WAIT_TIME);
        request.cancel(true);

        assertTrue(NoSDK.didPass);
        assertTrue(ThirdSuccessfulMediationView.didPass);
        assertFalse(didFail);
    }

    public void test2NoClassMediationCall() {
        // Create an AdRequest which will request a mediated response
        // that returns a non-exist class which cannot be instantiated
        // then verify that the correct fail URL request was made

        placementId = "2";
        runStandardTest();
    }

    public void test3BadClassMediationCall() {
        // Create an AdRequest which will request a mediated response
        // that returns an existing but invalid class which cannot be instantiated
        // then verify that the correct fail URL request was made

        placementId = "3";
        runStandardTest();
    }

    public void test4NoRequestMediationCall() {
        // Create an AdRequest which will request a mediated response
        // that returns an class which does not make an ad request
        // then verify that the correct fail URL request was made

        placementId = "4";
        runStandardTest();
    }

    public void test5ErrorThrownMediationCall() {
        // Create an AdRequest which will request a mediated response
        // that returns an class which throws an exception
        // then verify that the correct fail URL request was made
        placementId = "5";
        runStandardTest();
    }

    public void test6NoFillMediationCall() {
        // Create an AdRequest which will request a mediated response
        // that succeeds in instantiation but fails to return an ad
        // verify that the correct fail URL request was made
        placementId = "6";
        runStandardTest();
    }

    @Override
    public void failed(AdRequest request) {
        Clog.d(TestUtil.testLogTag, "request failed");
        didFail = true;
        Lock.unpause();
    }

    @Override
    public void onReceiveResponse(AdResponse response) {
        if (response == null) return;
        Clog.d(TestUtil.testLogTag, "received response: " + response.getContent());
        MediatedBannerAdViewController output = MediatedBannerAdViewController.create(
                null, this, response.getMediatedAds().pop(), null);
    }

    @Override
    public AdView getOwner() {
        return null;
    }
}