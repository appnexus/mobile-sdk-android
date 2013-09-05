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
import android.util.Log;
import com.appnexus.opensdk.*;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdkdemo.testviews.SuccessfulMediationView;
import com.appnexus.opensdkdemo.util.TestUtil;

public class TestMediationSuccessThenStd extends AndroidTestCase implements AdRequester {
	String old_base_url;
	AdRequest shouldWork;
	boolean didPass = false;
	String shouldWorkPlacement = "7";

	@Override
	protected void setUp() {
		old_base_url = Settings.getSettings().BASE_URL;
		Settings.getSettings().BASE_URL = TestUtil.MEDIATION_TEST_URL;
		Clog.d(TestUtil.testLogTag, "BASE_URL set to " + Settings.getSettings().BASE_URL);
		shouldWork = new AdRequest(this, null, null, null, shouldWorkPlacement, null, null, 320, 50, -1, -1, null, null, null, true, null, false, false);
		SuccessfulMediationView.didPass = false;
		didPass = false;
	}

	@Override
	protected void tearDown() {
		Clog.d(TestUtil.testLogTag, "tear down");
		Settings.getSettings().BASE_URL = old_base_url;
	}

	public void testSucceedingMediationAndStdCall() {
		// Create a AdRequest which will request a mediated response to
		// instantiate the SuccessfulMediationView
		// Since we're just testing to see successful instantiation, interrupt
		// the sleeping thread from the requestAd function

		shouldWork.execute();
		pause();
		shouldWork.cancel(true);

		assertEquals(true, SuccessfulMediationView.didPass);
		assertEquals(true, didPass);
	}

	@Override
	synchronized public void failed(AdRequest request) {
		Log.d(TestUtil.testLogTag, "request failed: " + request);
		SuccessfulMediationView.didPass = false;
		notify();
	}

	@Override
	public void onReceiveResponse(AdResponse response) {
		Log.d(TestUtil.testLogTag, "received first response");
		MediatedBannerAdViewController output = MediatedBannerAdViewController.create(
				null, response);
	}

	@Override
	public AdView getOwner() {
		return null;
	}

	synchronized private void pause() {
		Log.d(TestUtil.testLogTag, "pausing");
		try {
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
			shouldWork.cancel(true);
			return;
		}
		Log.d(TestUtil.testLogTag, "unpausing");
	}

	@Override
	synchronized public void dispatchResponse(final AdResponse response) {
		Clog.d(TestUtil.testLogTag, "dispatch " + response.toString());
		if (response.getType() == null) {
			Clog.d(TestUtil.testLogTag, "null type");
			return;
		}
		if (response.getType().equals("banner"))
			didPass = true;
		notify();
	}
}
