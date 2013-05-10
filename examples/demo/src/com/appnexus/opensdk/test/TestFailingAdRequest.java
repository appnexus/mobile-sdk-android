package com.appnexus.opensdk.test;

import com.appnexus.opensdk.AdListener;
import com.appnexus.opensdk.AdRequest;
import com.appnexus.opensdk.AdRequester;
import com.appnexus.opensdk.AdResponse;
import com.appnexus.opensdk.AdView;

import junit.framework.TestCase;

public class TestFailingAdRequest extends TestCase implements AdRequester, AdListener{
	int notifyCount=0;
	AdRequest shouldNotWork;
	AdRequest shouldNotWork2;
	boolean shouldPass=false;
	boolean shouldPass2=false;
	protected void setUp(){
		shouldNotWork=new AdRequest(this, "123456", null, null, null, "portrait", "AT&T", 320, 50, 320, 50, null, null, "wifi", false, null);
		shouldNotWork2=new AdRequest(null, "123456", null, null, null, "portrait", "AT&T", 320, 50, 320, 50, null, null, "wifi", false, this);
	}

	public void testFailingRequest(){
		shouldNotWork.execute();
		pause();
		shouldNotWork.cancel(true);
		assertEquals(true, shouldPass);
	}
	
	public void testFailingRequestListener(){
		shouldNotWork2.execute();
		pause();
		shouldNotWork2.cancel(true);
		assertEquals(true, shouldPass2);
	}
	
	synchronized void pause(){
		try {
			wait(30*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			shouldNotWork.cancel(true);
			shouldNotWork2.cancel(true);
			return;
		}
	}

	@Override
	synchronized public void onReceiveResponse(AdResponse response) {
			shouldPass=response.getBody()!=null?false:true;
			if(response.getBody()!=null)
				shouldPass=response.getBody().length()>0?false:true;
			notify();
	}

	@Override
	synchronized public void failed(AdRequest request) {
		shouldPass=true;
		notify();
	}

	@Override
	synchronized public void onAdLoaded(AdView adView) {
		shouldPass2=false;
		notify();
	}

	@Override
	synchronized public void onAdRequestFailed(AdView adView) {
		shouldPass2=true;
		notify();
	}

}
