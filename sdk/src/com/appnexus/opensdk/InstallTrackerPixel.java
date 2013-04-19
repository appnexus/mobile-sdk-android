package com.appnexus.opensdk;

import java.util.ArrayList;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class InstallTrackerPixel extends BroadcastReceiver{
	BroadcastReceiver receiver_install;
	Context context;
	
	
	//Test with am broadcast -a com.android.vending.INSTALL_REFERRER --es "referrer" "utm_source=test_source&utm_medium=test_medium&utm_term=test_term&utm_content=test_content&utm_campaign=test_name"
	//in adb
	public InstallTrackerPixel(){
		super();
	}

	@Override
	public void onReceive(Context context, final Intent intent) {
		this.context=context;
		new Thread(new Runnable(){

			@Override
			public void run() {
				Bundle extras = intent.getExtras();
				String referralString = extras.getString("referrer");

				ArrayList<BasicNameValuePair> parameters = getParameters(referralString);
				String url = getPixelUrl(parameters);
				
				Clog.d(Clog.baseLogTag, Clog.getString(R.string.conversion_pixel, url));
				
				try{
					HttpClient client = new DefaultHttpClient();
					HttpGet get = new HttpGet(url);
					client.execute(get);
					// TODO: what happens if it fails? what is the server response if it succeeds?
				}catch(Exception e){
					e.printStackTrace();
					// TODO: repeat the request later?
				}
				
			}
		}).start();

	}
	
	private ArrayList<BasicNameValuePair> getParameters(String s){
		ArrayList<BasicNameValuePair> output = new ArrayList<BasicNameValuePair>();
		String[] fields = s.split("&");
		for(String pair : fields){
			output.add(new BasicNameValuePair(pair.split("=")[0], pair.split("=")[1]));
		}
		return output;
	}
	
	private String getPixelUrl(ArrayList<BasicNameValuePair> input){
		String source = null;
		String medium = null;
		String term = null;
		String content = null;
		String campaign = null;
		String appid = null;
		
		for(BasicNameValuePair p : input){
			if(p.getName().equals("utm_source")){
				source=p.getValue();
			}else if(p.getName().equals("utm_medium")){
				medium=p.getValue();
			}else if(p.getName().equals("utm_term")){
				term=p.getValue();
			}else if(p.getName().equals("utm_content")){
				content=p.getValue();
			}else if(p.getName().equals("utm_campaign")){
				campaign=p.getValue();
			}
		}
		
		if(context!=null) appid = context.getApplicationContext()
				.getPackageName();
		
		StringBuilder urlBuilder = new StringBuilder(Settings.getSettings().PIXEL_BASE_URL);
		urlBuilder.append(source!=null?"source="+Uri.encode(source):"");
		urlBuilder.append(medium!=null?"&medium="+Uri.encode(medium):"");
		urlBuilder.append(term!=null?"&term="+Uri.encode(term):"");
		urlBuilder.append(content!=null?"&content="+Uri.encode(content):"");
		urlBuilder.append(campaign!=null?"&campaign="+Uri.encode(campaign):"");
		urlBuilder.append(appid!=null?"&appid="+Uri.encode(appid):"");
		
		return urlBuilder.toString();
	}

}
