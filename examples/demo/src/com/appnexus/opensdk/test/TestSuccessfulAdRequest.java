package com.appnexus.opensdk.test;

import com.appnexus.opensdk.AdListener;
import com.appnexus.opensdk.AdRequest;
import com.appnexus.opensdk.AdRequester;
import com.appnexus.opensdk.AdResponse;
import com.appnexus.opensdk.AdView;

import junit.framework.TestCase;

public class TestSuccessfulAdRequest extends TestCase implements AdRequester, AdListener{
	AdRequest shouldWork;
	AdRequest shouldWork2;
	boolean shouldWorkDidWork = false;
	boolean shouldWorkDidWork2 = false;

	protected void setUp() {
		shouldWork = new AdRequest(this, "123456", null, null, "1281482",
				"portrait", "AT&T", 320, 50, 320, 50, null, null, "wifi", false, null);
		shouldWork2 = new AdRequest(null, "123456", null, null, "1281482",
				"portrait", "AT&T", 320, 50, 320, 50, null, null, "wifi", false, this);
	}

	public void testSucceedingRequest() {
		shouldWork.execute();
		pause();
		assertEquals(true, shouldWorkDidWork);
	}
	
	public void testSucceedingRequest2() {
		shouldWork2.execute();
		pause();
		assertEquals(true, shouldWorkDidWork2);
	}

	@Override
	synchronized public void onReceiveResponse(AdResponse response) {
		shouldWorkDidWork = true;
		assertEquals(true, response.getBody().length() > 0);
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

}
