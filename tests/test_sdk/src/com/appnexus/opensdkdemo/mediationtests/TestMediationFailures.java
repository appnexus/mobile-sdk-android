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

import android.util.Log;
import com.appnexus.opensdk.*;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdkdemo.testviews.NoSDK;
import com.appnexus.opensdkdemo.testviews.ThirdSuccessfulMediationView;
import com.appnexus.opensdkdemo.util.TestUtil;
import junit.framework.TestCase;

public class TestMediationFailures extends TestCase implements AdRequester {
	String old_base_url;
	AdRequest shouldFail;
	String shouldFailPlacement;

	@Override
	protected void setUp() {
		Clog.w(TestUtil.testLogTag, "NEW TEST");
		old_base_url = Settings.getSettings().BASE_URL;
		Settings.getSettings().BASE_URL = TestUtil.MEDIATION_TEST_URL;
		Clog.d(TestUtil.testLogTag, "BASE_URL set to " + Settings.getSettings().BASE_URL);
		NoSDK.didPass = false;
		ThirdSuccessfulMediationView.didPass = false;
	}

	@Override
	synchronized protected void tearDown() {
		try {
			wait(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (NoSDK.didPass && ThirdSuccessfulMediationView.didPass)
			Clog.w(TestUtil.testLogTag, "TEST PASSED #" + shouldFailPlacement);
		Settings.getSettings().BASE_URL = old_base_url;
	}

	public void test2NoClassMediationCall() {
		// Create an AdRequest which will request a mediated response
		// that returns a non-exist class which cannot be instantiated
		// then verify that the correct fail URL request was made

		shouldFailPlacement = "2";
		shouldFail = new AdRequest(this, null, null, null, shouldFailPlacement, null, null, 320, 50, -1, -1, null, null, null, true, null, false, false);

		shouldFail.execute();
		pause();
		shouldFail.cancel(true);
		assertEquals(true, NoSDK.didPass);
		assertEquals(true, ThirdSuccessfulMediationView.didPass);
	}

	public void test3BadClassMediationCall() {
		// Create an AdRequest which will request a mediated response
		// that returns an existing but invalid class which cannot be instantiated
		// then verify that the correct fail URL request was made

		shouldFailPlacement = "3";
		shouldFail = new AdRequest(this, null, null, null, shouldFailPlacement, null, null, 320, 50, -1, -1, null, null, null, true, null, false, false);

		shouldFail.execute();
		pause();
		shouldFail.cancel(true);
		assertEquals(true, NoSDK.didPass);
		assertEquals(true, ThirdSuccessfulMediationView.didPass);
	}

	public void test4NoRequestMediationCall() {
		// Create an AdRequest which will request a mediated response
		// that returns an class which does not make an ad request
		// then verify that the correct fail URL request was made

		shouldFailPlacement = "4";
		shouldFail = new AdRequest(this, null, null, null, shouldFailPlacement, null, null, 320, 50, -1, -1, null, null, null, true, null, false, false);

		shouldFail.execute();
		pause();
		shouldFail.cancel(true);
		assertEquals(true, NoSDK.didPass);
		assertEquals(true, ThirdSuccessfulMediationView.didPass);
	}

	public void test5ErrorThrownMediationCall() {
		// Create an AdRequest which will request a mediated response
		// that returns an class which throws an exception
		// then verify that the correct fail URL request was made
		shouldFailPlacement = "5";
		shouldFail = new AdRequest(this, null, null, null, shouldFailPlacement, null, null, 320, 50, -1, -1, null, null, null, true, null, false, false);

		shouldFail.execute();
		pause();
		shouldFail.cancel(true);
		assertEquals(true, NoSDK.didPass);
		assertEquals(true, ThirdSuccessfulMediationView.didPass);
	}

	public void test6NoFillMediationCall() {
		// Create an AdRequest which will request a mediated response
		// that succeeds in instantiation but fails to return an ad
		// verify that the correct fail URL request was made
		shouldFailPlacement = "6";
		shouldFail = new AdRequest(this, null, null, null, shouldFailPlacement, null, null, 320, 50, -1, -1, null, null, null, true, null, false, false);

		shouldFail.execute();
		pause();
		shouldFail.cancel(true);
		assertEquals(true, NoSDK.didPass);
		assertEquals(true, ThirdSuccessfulMediationView.didPass);
	}

	@Override
	public void failed(AdRequest request) {
		Log.d(TestUtil.testLogTag, "request failed");
		synchronized (ThirdSuccessfulMediationView.lock) {
			ThirdSuccessfulMediationView.lock.notify();
		}
	}

	@Override
	public void onReceiveResponse(AdResponse response) {
		Log.d(TestUtil.testLogTag, "received response: " + response.getMediatedResultCB());
		MediatedBannerAdViewController output = MediatedBannerAdViewController.create(
				null, response);
	}

	@Override
	public AdView getOwner() {
		return null;
	}

	void pause() {
		Log.d(TestUtil.testLogTag, "pausing");
		try {
			synchronized (ThirdSuccessfulMediationView.lock) {
				ThirdSuccessfulMediationView.lock.wait(15 * 1000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			shouldFail.cancel(true);
			return;
		}
		Log.d(TestUtil.testLogTag, "unpausing");
	}

	@Override
	public void dispatchResponse(final AdResponse response) {
		if (response.getMediatedResultCB() == null) {
			Log.d(TestUtil.testLogTag, "dispatching null result, return");
			return;
		}
		Log.d(TestUtil.testLogTag, "dispatch: " + response.toString());
		MediatedBannerAdViewController output = MediatedBannerAdViewController.create(
				null, response);

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