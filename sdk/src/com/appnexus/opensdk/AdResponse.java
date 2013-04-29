package com.appnexus.opensdk;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.appnexus.opensdk.utils.Clog;



public class AdResponse {
	private String body;
	private int height;
	private int width;
	private String type;
	private AdView owner;
	boolean fail=false;
	final static String http_error="HTTP_ERROR";

	public AdResponse(AdView owner, String body, Header[] headers) {
		if(body==null){
			this.fail=true;
			Clog.setLastResponse("");
			return;
		}else if(body.equals(AdResponse.http_error)){
			this.fail=true;
			Clog.setLastResponse("");
			return;
		}else if(body.length()==0){
			Clog.w(Clog.httpRespLogTag, Clog.getString(R.string.response_blank));
			this.fail=true;
			Clog.setLastResponse("");
			return;
		}
		
		Clog.setLastResponse(body);
		
		Clog.d(Clog.httpRespLogTag, Clog.getString(R.string.response_body, body));
		if(headers!=null){
			for(Header h : headers){
				Clog.v(Clog.httpRespLogTag, Clog.getString(R.string.response_header, h.getName(), h.getValue()));
			}
		}
		
		this.owner=owner;
		
		parseResponse(body);
		
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

	protected Displayable getDisplayable(){
		if(this.fail) return null;
		if(!getBody().contains("mraid.js")){
			AdWebView out = new AdWebView(owner);
			out.loadAd(this);
			return out;
		}else{
			MRAIDWebView out = new MRAIDWebView(owner);
			out.loadAd(this);
			return out;
		}
	}
}
