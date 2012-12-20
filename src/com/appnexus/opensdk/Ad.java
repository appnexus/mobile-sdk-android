/**
 * 
 */
package com.appnexus.opensdk;

import org.apache.http.Header;

import android.util.Log;

/**
 * @author jacob
 *
 */
public class Ad {
	private String mBody;
	/**
	 * 
	 */
	public Ad(String body, Header[] headers) {
		// TODO Auto-generated constructor stub
		mBody=body;
		Log.d("OPENSDKHTTP", "RESPONSE BODY: "+body);
		Log.d("OPENSDKHTTP", "RESPONSE HEADERS: ");
		for(Header h : headers){
			Log.d("OPENSDKHTTP", "HeADER: "+h.getName()+" Value: "+h.getValue());
		}
	}
	
	public String getBody(){
		return mBody==null?"TEST":mBody;
	}
}
