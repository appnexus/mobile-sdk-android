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

package com.appnexus.opensdk.test;

import android.app.Activity;
import android.view.View;

import com.appnexus.opensdk.AdResponse;
import com.appnexus.opensdk.MediatedBannerAdView;
import com.appnexus.opensdk.MediatedBannerAdViewController;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.AdRequest;
import com.appnexus.opensdk.AdRequester;

import junit.framework.TestCase;

public class TestSuccessfulMediation extends TestCase implements AdRequester {
	String old_base_url;
	boolean shouldPass=false;

	@Override
	protected void setUp() {
		old_base_url = Settings.getSettings().BASE_URL;
		Settings.getSettings().BASE_URL = "http://rlissack.adnxs.com:8080/mobile/utest?";
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
		
		AdRequest ar = new AdRequest(this, old_base_url, old_base_url, old_base_url, old_base_url, old_base_url, old_base_url, 0, 0, 0, 0, old_base_url, old_base_url, old_base_url, false, null, false)
		

	}

	class SuccessfulMediationView implements MediatedBannerAdView {
		public View requestAd(MediatedBannerAdViewController mBC,
				Activity activity, String parameter, String uid, int width,
				int height, View adSpace) {

			return null;
		}
	}

	@Override
	public void failed(AdRequest request) {
		shouldPass=false;
		finish();
		
	}

	@Override
	public void onReceiveResponse(AdResponse response) {
		shouldPass=true;
		finish();
		
	}
	
	public synchronized void finish(){
	}
	}

}
