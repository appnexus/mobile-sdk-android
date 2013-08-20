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

import com.appnexus.opensdk.AdListener;
import com.appnexus.opensdk.AdRequest;
import com.appnexus.opensdk.AdRequester;
import com.appnexus.opensdk.AdResponse;
import com.appnexus.opensdk.AdView;

import junit.framework.TestCase;

public class TestShortestSuccessfulAdRequest extends TestCase implements AdRequester, AdListener {
	AdRequest shouldWork;
	AdRequest shouldWork2;
	boolean shouldWorkDidWork = false;
	boolean shouldWorkDidWork2 = false;
	
	protected void setUp() {
		shouldWork = new AdRequest(this, null, null, null, "1281482",
				null, null, 320, 50, -1, -1, null, null, null, false, null, false);
		shouldWork2 = new AdRequest(null, null, null, null, "1281482",
				null, null, 320, 50, -1, -1, null, null, null, false, this, false);
	}

	public void testSucceedingRequest() {
		shouldWork.execute();
		pause();
		shouldWork.cancel(true);
		assertEquals(true, shouldWorkDidWork);
	}
	
	public void testSucceedingRequestListener() {
		shouldWork2.execute();
		pause();
		shouldWork2.cancel(true);
		assertEquals(true, shouldWorkDidWork2);
	}

	@Override
	synchronized public void onReceiveResponse(AdResponse response) {
		assertEquals(true, response.getBody().length() > 0);
		shouldWorkDidWork = true;
		notify();
	}

	@Override
	synchronized public void failed(AdRequest request) {
		shouldWorkDidWork = false;
		notify();
	}
	
	synchronized void pause(){
		try {
			wait(10*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			shouldWork.cancel(true);
			shouldWork2.cancel(true);
			return;
		}
	}

	@Override
	synchronized public void onAdLoaded(AdView adView) {
		shouldWorkDidWork2=true;
		notify();
		
	}

	@Override
	synchronized public void onAdRequestFailed(AdView adView) {
		shouldWorkDidWork2=false;
		notify();		
	}

	@Override
	public void onAdExpanded(AdView adView) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAdCollapsed(AdView adView) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAdClicked(AdView adView) {
		// TODO Auto-generated method stub
		
	}

}
