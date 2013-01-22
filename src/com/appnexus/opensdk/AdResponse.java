package com.appnexus.opensdk;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


public class AdResponse {
	private String body;
	private int height;
	private int width;
	private String type;

	public AdResponse(String body, Header[] headers) {
		Log.d("OPENSDKHTTP", "RESPONSE BODY: "+body);
		for(Header h : headers){
			Log.d("OPENSDKHTTP", "HEADER: "+h.getName()+" Value: "+h.getValue());
		}
		
		if(body==null) return;
		
		try {
			//TODO: test what happens when no ads are returned
			//TODO: test what happens when there is an error returned
			JSONObject response = new JSONObject(body);
			JSONArray ads = response.getJSONArray("ads");
			//is the array empty? if so, no ads were returned, and we need to fail gracefully
			if(ads.length()==0){
				return;
			}
			//for now, just take the first ad
			JSONObject firstAd = ads.getJSONObject(0);
			//assume there's content
			height = firstAd.getInt("height");
			width = firstAd.getInt("width");
			this.body = firstAd.getString("content");
			type= firstAd.getString("type");
		} catch (JSONException e) {
			// TODO Auto-generated catch block, BE VERY VERBOSE ABOUT WHAT WENT WRONG HERE
			e.printStackTrace();
			return;
		}
		
	}
	
	protected String getBody(){
		if (body==null) return "";
		return body;
	}
	
	protected int getHeight(){
		return height;
	}
	
	protected int getWidth(){
		return width;
	}
	
	//banner, interstitial
	protected String getType(){
		return type;
	}
}
