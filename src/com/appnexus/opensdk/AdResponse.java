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
public class AdResponse {
	private String mBody;
	/**
	 * 
	 */
	public AdResponse(String body, Header[] headers) {
		// TODO Auto-generated constructor stub
		mBody=body;
		Log.d("OPENSDKHTTP", "RESPONSE BODY: "+body);
		for(Header h : headers){
			Log.d("OPENSDKHTTP", "HEADER: "+h.getName()+" Value: "+h.getValue());
		}
	}
	
	public String getBody(){
		return mBody==null?"":mBody;
	}
}
