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

package com.appnexus.opensdkdemo;

import android.test.AndroidTestCase;
import android.util.Log;
import com.appnexus.opensdk.*;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;

public class TestSuccessfulMediation extends AndroidTestCase implements AdRequester {
	String old_base_url;
	AdRequest shouldWork;

	@Override
	protected void setUp() {
		old_base_url = Settings.getSettings().BASE_URL;
		Settings.getSettings().BASE_URL = TestUtil.MEDIATION_TEST_URL;
		Clog.d(TestUtil.testLogTag, "BASE_URL set to " + Settings.getSettings().BASE_URL);
		shouldWork = new AdRequest(this, null, null, null, "1", null, null, 320, 50, -1, -1, null, null, null, true, null, false, false);
		SuccessfulMediationView.didPass = false;
	}

	@Override
	protected void tearDown() {
		Settings.getSettings().BASE_URL = old_base_url;
	}

	public void testSucceedingMediationCall() {
		// Create a AdRequest which will request a mediated response to
		// instantiate the SuccessfulMediationView
		// Since we're just testing to see successful instantiation, interrupt
		// the sleeping thread from the requestAd function

		shouldWork.execute();
		pause();
		shouldWork.cancel(true);
		assertEquals(true, SuccessfulMediationView.didPass);
	}

	@Override
	synchronized public void failed(AdRequest request) {
		Log.d(TestUtil.testLogTag, "request failed");
		SuccessfulMediationView.didPass = false;
		notify();
	}

	@Override
	synchronized public void onReceiveResponse(AdResponse response) {
		Log.d(TestUtil.testLogTag, "received response " + SuccessfulMediationView.class.toString());
		Log.d(TestUtil.testLogTag, "received response " + response.getBody());
		MediatedBannerAdViewController output = MediatedBannerAdViewController.create(
				null, response);
		Log.d(TestUtil.testLogTag, "passed instantiation: " + SuccessfulMediationView.didPass);
		notify();
	}

	@Override
	public AdView getOwner() {
		return null;
	}

	synchronized void pause() {
		Log.d(TestUtil.testLogTag, "pausing");
		try {
			wait(10 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			shouldWork.cancel(true);
			return;
		}
		Log.d(TestUtil.testLogTag, "call timed out");
	}
}
