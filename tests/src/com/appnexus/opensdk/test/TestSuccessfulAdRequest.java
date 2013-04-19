package com.appnexus.opensdk.test;

import com.appnexus.opensdk.AdRequest;
import com.appnexus.opensdk.AdRequester;
import com.appnexus.opensdk.AdResponse;

import junit.framework.TestCase;

public class TestSuccessfulAdRequest extends TestCase implements AdRequester {
	AdRequest shouldWork;
	boolean shouldWorkDidWork = false;

	protected void setUp() {
		shouldWork = new AdRequest(this, "123456", null, null, "1281482",
				"portrait", "AT&T", 320, 50, 320, 50, null, null, "wifi");
	}

	public void testSucceedingRequest() {
		shouldWork.execute();
	}

	@Override
	public void onReceiveResponse(AdResponse response) {
		assertEquals(true, response.getBody().length() > 0);
	}

	@Override
	public void failed(AdRequest request) {
		fail("A valid ad request failed");
	}

}
