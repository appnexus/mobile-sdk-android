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

	/**
	 * 
	 */
	public Ad(String body, Header[] headers) {
		// TODO Auto-generated constructor stub
		Log.d("OPENSDKHTTP", "RESPONSE BODY: "+body);
		Log.d("OPENSDKHTTP", "RESPONSE HEADERS: ");
		for(Header h : headers){
			Log.d("OPENSDKHTTP", "HeADER: "+h.getName()+" Value: "+h.getValue());
		}
	}

}
