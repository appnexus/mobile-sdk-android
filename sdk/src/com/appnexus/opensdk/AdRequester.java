package com.appnexus.opensdk;

public interface AdRequester {
	public void failed(AdRequest request);
	public void onReceiveResponse(AdResponse response);
}
