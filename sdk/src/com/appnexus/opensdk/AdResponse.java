package com.appnexus.opensdk;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class AdResponse {
	private String body;
	private int height;
	private int width;
	private String type;
	private AdView owner;

	public AdResponse(AdView owner, String body, Header[] headers) {
		Clog.d(Settings.getSettings().baseLogTag+Settings.getSettings().httpRespLogTag, "Response body: "+body);
		for(Header h : headers){
			Clog.v(Settings.getSettings().baseLogTag+Settings.getSettings().httpRespLogTag, "Header: "+h.getName()+" Value: "+h.getValue());
		}
		this.owner=owner;
		if(body==null) return;
		if(body.equals("")){
			Clog.e(Settings.getSettings().baseLogTag+Settings.getSettings().httpRespLogTag, "The server returned a blank response.");
			return;
		}
		
		try {
			JSONObject response = new JSONObject(body);
			String status = response.getString("status");
			if(status.equals("error")){
				String error = response.getString("errorMessage");
				Clog.e(Settings.getSettings().baseLogTag+Settings.getSettings().httpRespLogTag, "The server replied with an error: "+error);
				return;
			}
			JSONArray ads = response.getJSONArray("ads");
			//is the array empty? if so, no ads were returned, and we need to fail gracefully
			if(ads.length()==0){
				Clog.w(Settings.getSettings().baseLogTag+Settings.getSettings().httpRespLogTag, "The server responded, but didn't return any ads.");
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
			Clog.e(Settings.getSettings().baseLogTag+Settings.getSettings().httpRespLogTag, "There was an error parsing the JSON response: "+body);
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
	
	@SuppressWarnings("unused")
	protected Displayable getDisplayable(){
		if(true){ //All displayables, for now, are webviews
			AdWebView out = new AdWebView(owner);
			out.loadAd(this);
			return out;
		}else{
			return null;
		}
	}
}
