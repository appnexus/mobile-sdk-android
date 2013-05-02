package com.appnexus.opensdk;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.appnexus.opensdk.utils.Clog;



public class AdResponse {
	public AdRequester requester;
	private String body;
	private int height;
	private int width;
	private String type;
	boolean fail=false;
	final static String http_error="HTTP_ERROR";
	boolean isMraid=false;

	public AdResponse(AdRequester requester, String body, Header[] headers) {
		this.requester=requester;
		if(body==null){
			this.fail=true;
			Clog.clearLastResponse();
			return;
		}else if(body.equals(AdResponse.http_error)){
			this.fail=true;
			Clog.clearLastResponse();
			return;
		}else if(body.length()==0){
			Clog.w(Clog.httpRespLogTag, Clog.getString(R.string.response_blank));
			this.fail=true;
			Clog.clearLastResponse();
			return;
		}
		
		Clog.setLastResponse(body);
		
		Clog.d(Clog.httpRespLogTag, Clog.getString(R.string.response_body, body));
		if(headers!=null){
			for(Header h : headers){
				Clog.v(Clog.httpRespLogTag, Clog.getString(R.string.response_header, h.getName(), h.getValue()));
			}
		}
		
		parseResponse(body);
		
		isMraid=getBody().contains("mraid.js");
		
	}
	
	private void parseResponse(String body){
		JSONObject response;
		try {
			response = new JSONObject(body);
			String status = response.getString("status");
			if(status.equals("error")){
				String error = response.getString("errorMessage");
				Clog.e(Clog.httpRespLogTag, Clog.getString(R.string.response_error,error));
				return;
			}
			JSONArray ads = response.getJSONArray("ads");
			//is the array empty? if so, no ads were returned, and we need to fail gracefully
			if(ads.length()==0){
				Clog.w(Clog.httpRespLogTag, Clog.getString(R.string.response_no_ads));
				return;
			}
			//for now, just take the first ad
			JSONObject firstAd = ads.getJSONObject(0);
			//assume there's content
			height = firstAd.getInt("height");
			width = firstAd.getInt("width");
			this.body = firstAd.getString("content");
			type= firstAd.getString("type");
			if (this.body.equals("") || this.body==null)Clog.e(Clog.httpRespLogTag, Clog.getString(R.string.blank_ad));
		} catch (JSONException e) {
			Clog.e(Clog.httpRespLogTag, Clog.getString(R.string.response_json_error,body));
			e.printStackTrace();
			return;
		}

	}
	
	public String getBody(){
		if (body==null) return "";
		return body;
	}
	
	public int getHeight(){
		return height;
	}
	
	public int getWidth(){
		return width;
	}
	
	//banner, interstitial
	public String getType(){
		return type;
	}
}
