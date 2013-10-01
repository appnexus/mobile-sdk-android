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

package com.appnexus.opensdkdemo.mediationtests;

import android.test.AndroidTestCase;
import com.appnexus.opensdk.*;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdkdemo.testviews.DummyView;
import com.appnexus.opensdkdemo.testviews.NoSDK;
import com.appnexus.opensdkdemo.testviews.ThirdSuccessfulMediationView;
import com.appnexus.opensdkdemo.util.Lock;
import com.appnexus.opensdkdemo.util.TestUtil;

public class TestMediationFailures extends AndroidTestCase implements AdRequester {
    String old_base_url;
    AdRequest shouldFail;
    String shouldFailPlacement;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Clog.w(TestUtil.testLogTag, "NEW TEST");
        old_base_url = Settings.getSettings().BASE_URL;
        Settings.getSettings().BASE_URL = TestUtil.MEDIATION_TEST_URL;
        Clog.d(TestUtil.testLogTag, "BASE_URL set to " + Settings.getSettings().BASE_URL);
        NoSDK.didPass = false;
        ThirdSuccessfulMediationView.didPass = false;
        DummyView.createView(getContext());
    }

    @Override
    synchronized protected void tearDown() throws Exception {
        try {
            wait(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (NoSDK.didPass && ThirdSuccessfulMediationView.didPass)
            Clog.w(TestUtil.testLogTag, "TEST PASSED #" + shouldFailPlacement);
        Settings.getSettings().BASE_URL = old_base_url;
        super.tearDown();
    }

    /*
    * All of these tests follow the same pattern:
    * Initial call directs them to a MediatedBannerView class unique to the test case
    * the following call will go to NoSDK.
    * the following call will go to ThirdSuccessfulMediationView to confirm success
     */
    private void runStandardTest() {
        shouldFail = new AdRequest(this, null, null, null, shouldFailPlacement, null, null, 320, 50, -1, -1, null, null, null, true, null, false, false);

        shouldFail.execute();
        Lock.pause(10000);
        shouldFail.cancel(true);
        assertEquals(true, NoSDK.didPass);
        assertEquals(true, ThirdSuccessfulMediationView.didPass);
    }

    public void test2NoClassMediationCall() {
        // Create an AdRequest which will request a mediated response
        // that returns a non-exist class which cannot be instantiated
        // then verify that the correct fail URL request was made

        shouldFailPlacement = "2";
        runStandardTest();
    }

    public void test3BadClassMediationCall() {
        // Create an AdRequest which will request a mediated response
        // that returns an existing but invalid class which cannot be instantiated
        // then verify that the correct fail URL request was made

        shouldFailPlacement = "3";
        runStandardTest();
    }

    public void test4NoRequestMediationCall() {
        // Create an AdRequest which will request a mediated response
        // that returns an class which does not make an ad request
        // then verify that the correct fail URL request was made

        shouldFailPlacement = "4";
        runStandardTest();
    }

    public void test5ErrorThrownMediationCall() {
        // Create an AdRequest which will request a mediated response
        // that returns an class which throws an exception
        // then verify that the correct fail URL request was made
        shouldFailPlacement = "5";
        runStandardTest();
    }

    public void test6NoFillMediationCall() {
        // Create an AdRequest which will request a mediated response
        // that succeeds in instantiation but fails to return an ad
        // verify that the correct fail URL request was made
        shouldFailPlacement = "6";
        runStandardTest();
    }

    @Override
    public void failed(AdRequest request) {
        Clog.d(TestUtil.testLogTag, "request failed");
        Lock.unpause();
    }

    @Override
    public void onReceiveResponse(AdResponse response) {
        Clog.d(TestUtil.testLogTag, "received response: " + response.getBody());
        MediatedBannerAdViewController output = MediatedBannerAdViewController.create(
                null, this, response.getMediatedAds().pop(), null);
    }

    @Override
    public AdView getOwner() {
        return null;
    }

    @Override
    public void dispatchResponse(AdResponse response) {
        Clog.d(TestUtil.testLogTag, "dispatch: " + response.toString());
        MediatedBannerAdViewController output = MediatedBannerAdViewController.create(
                null, this, response.getMediatedAds().pop(), null);

        // verify fail result cb
//		Clog.d(TestUtil.testLogTag, response.getBody());
//		try {
//			JSONObject jsonObject = new JSONObject(response.getBody());
//			String code = jsonObject.getString("code");
//			if (Integer.valueOf(code) == (MediatedAdViewController.RESULT.MEDIATED_SDK_UNAVAILABLE.ordinal())) {
//				didFail = true;
//			} else {
//				didFail = false;
//				Clog.d(TestUtil.testLogTag, code);
//				Clog.d(TestUtil.testLogTag, String.valueOf(MediatedAdViewController.RESULT.MEDIATED_SDK_UNAVAILABLE.ordinal()));
//			}
//		} catch (JSONException e) {
//			Clog.d(TestUtil.testLogTag, "json parsing error", e);
//		} catch (NumberFormatException e) {
//			Clog.d(TestUtil.testLogTag, "error with response. make sure server implementation is correct", e);
//		}
//
//		synchronized (NoSDK.lock) {
//			NoSDK.lock.notify();
//		}
    }
}