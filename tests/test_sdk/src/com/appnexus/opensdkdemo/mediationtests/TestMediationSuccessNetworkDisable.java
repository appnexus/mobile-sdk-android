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

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.test.AndroidTestCase;
import android.util.Log;
import com.appnexus.opensdk.*;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdkdemo.testviews.SecondSuccessfulMediationView;
import com.appnexus.opensdkdemo.testviews.SuccessfulMediationView;
import com.appnexus.opensdkdemo.testviews.ThirdSuccessfulMediationView;
import com.appnexus.opensdkdemo.util.TestUtil;

public class TestMediationSuccessNetworkDisable extends AndroidTestCase implements AdRequester {
	String old_base_url;
	AdRequest shouldWork;
	String AdMobId = "10am";
	String MMId = "10mm";
	boolean didPass = true;

	@Override
	protected void setUp() {
		old_base_url = Settings.getSettings().BASE_URL;
		Settings.getSettings().BASE_URL = TestUtil.MEDIATION_TEST_URL;
		Clog.d(TestUtil.testLogTag, "BASE_URL set to " + Settings.getSettings().BASE_URL);
		didPass = false;
//		SuccessfulMediationView.didPass = false;
//		SecondSuccessfulMediationView.didPass = false;
//		ThirdSuccessfulMediationView.didPass = false;
	}

	@Override
	protected void tearDown() {
		Clog.d(TestUtil.testLogTag, "tear down");
		Settings.getSettings().BASE_URL = old_base_url;
	}

	public void testAdMobNetworkInterruption() {
		// Create a AdRequest which will request a mediated response to
		// instantiate an AdMob view
		// Shut off the network before AdMob can make a call

		shouldWork = new AdRequest(this, null, null, null, AdMobId, null, null, 320, 50, -1, -1, null, null, null, true, null, false, false);
		shouldWork.execute();
		pause();
		shouldWork.cancel(true);

		assertEquals(true, didPass);
//		assertEquals(true, SecondSuccessfulMediationView.didPass);
//		assertEquals(true, ThirdSuccessfulMediationView.didPass);
	}

	@Override
	public void failed(AdRequest request) {
		Log.d(TestUtil.testLogTag, "request failed: " + request);
//		SuccessfulMediationView.didPass = false;
//		synchronized (ThirdSuccessfulMediationView.lock) {
//			ThirdSuccessfulMediationView.lock.notify();
//		}
	}

	@Override
	public void onReceiveResponse(AdResponse response) {
		Log.d(TestUtil.testLogTag, "received response: " + response.toString());

//		Clog.d(TestUtil.testLogTag, "disabling wifi");
//		WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
//		wifiManager.setWifiEnabled(false);
//		ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

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
			wait(15000);
			synchronized (ThirdSuccessfulMediationView.lock) {
				ThirdSuccessfulMediationView.lock.wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			shouldWork.cancel(true);
			return;
		}
		Log.d(TestUtil.testLogTag, "call timed out");
	}

	@Override
	synchronized public void dispatchResponse(final AdResponse response) {
		Clog.d(TestUtil.testLogTag, "dispatch: " + response.toString());
		didPass = true;
		notify();
	}
}
