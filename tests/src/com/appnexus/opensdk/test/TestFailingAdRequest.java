package com.appnexus.opensdk.test;

import android.util.Log;

import com.appnexus.opensdk.AdRequest;
import com.appnexus.opensdk.AdRequester;
import com.appnexus.opensdk.AdResponse;

import junit.framework.TestCase;

public class TestFailingAdRequest extends TestCase implements AdRequester{
	AdRequest shouldNotWork;
	boolean shouldPass=true;
	protected void setUp(){
		shouldNotWork=new AdRequest(this, "123456", null, null, null, "portrait", "AT&T", 320, 50, 320, 50, null, null, "wifi");
	}

	public void testFailingRequest(){
		shouldNotWork.execute();
		pause();
		assertEquals(true, shouldPass);
	}
	
	synchronized void pause(){
		try {
			wait(60*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return;
		}
	}

	@Override
	synchronized public void onReceiveResponse(AdResponse response) {
			shouldPass=response.getBody()!=null?false:true;
			if(response.getBody()!=null)
				shouldPass=response.getBody().length()>0?false:true;
	}

	@Override
	synchronized public void failed(AdRequest request) {
		shouldPass=true;
	}

}
