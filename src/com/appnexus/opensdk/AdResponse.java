/**
 * 
 */
package com.appnexus.opensdk;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
		if (mBody==null) return "";
		//TODO: test what happens when no ads are returned
		try {
			JSONObject response = new JSONObject(mBody);
			JSONArray ads = response.getJSONArray("ads");
			//is the array empty? if so, no ads were returned, and we need to fail gracefully
			if(ads.length()==0){
				return "";
			}
			//for now, just take the first ad
			JSONObject firstAd = ads.getJSONObject(0);
			//assume there's content
			//TODO handle the ad size
			return firstAd.getString("content");
		} catch (JSONException e) {
			// TODO Auto-generated catch block, BE VERY VERBOSE ABOUT WHAT WENT WRONG HERE
			e.printStackTrace();
			return "";
		}
	}
}
