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
				null, null, 320, 50, -1, -1, null, null, null, null);
		shouldWork2 = new AdRequest(null, null, null, null, "1281482",
				null, null, 320, 50, -1, -1, null, null, null, this);
	}

	public void testSucceedingRequest() {
		shouldWork.execute();
		pause();
		assertEquals(true, shouldWorkDidWork);
	}
	
	public void testSucceedingRequestListener() {
		shouldWork2.execute();
		pause();
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
		fail("A valid ad request failed");
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
