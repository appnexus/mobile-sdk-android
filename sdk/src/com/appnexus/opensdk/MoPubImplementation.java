package com.appnexus.opensdk;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import com.appnexus.opensdk.MRAIDImplementation;
import com.appnexus.opensdk.MRAIDWebView;
import com.appnexus.opensdk.R;
import android.content.res.Resources;
import android.util.Log;
import android.view.Gravity;
import android.webkit.WebView;

public class MoPubImplementation extends MRAIDImplementation {
	public MoPubImplementation(MRAIDWebView owner) {
		super(owner);
	}

	boolean readyFired=false;
	boolean expanded=false;
	int default_width, default_height;
	
	@Override
	protected MRAIDWebViewClient getWebViewClient() {
		

		return new MRAIDWebViewClient(){

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if(url.startsWith("mraid://")){
					MoPubImplementation.this.dispatch_mraid_call(url);
					
					return true;
				}
				return false;
			}
			
			@Override
			public void onPageFinished(WebView view, String url) {
				//Fire the ready event only once
				//TODO ads not reloading when rotated
				if(!readyFired){
					//Set the placement type TODO 0 for banner 1 for interstitial? check mopub's sdk
					view.loadUrl("javascript:window.mraidbridge.fireChangeEvent({placementType:'0'});");
					view.loadUrl("javascript:window.mraidbridge.fireChangeEvent({state:'default'});");
					view.loadUrl("javascript:window.mraidbridge.fireReadyEvent();");
					readyFired = true;
				}
			}
		};
	}
	
	protected void dispatch_mraid_call(String url) {
		//Remove the fake protocol
		Log.d("MRAID", "Command url: "+url);
		url = url.replaceFirst("mraid://", "");
		
		//Separate the function from the parameters
		String func = url.split("\\?")[0];
		String params;
		ArrayList<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
		if(url.split("\\?").length>1){
			params = url.split("\\?")[1];
		
			for(String s : params.split("&")){
				Log.d("MRAID", "Parameter: "+s.split("=")[0]+" Value: "+s.split("=")[1]);
				parameters.add(new BasicNameValuePair(s.split("=")[0], s.split("=")[1]));
			}
		}
		
		if(func.equals("expand")){
			expand(parameters);
		}else if(func.equals("close")){
			close();
		}
		
		owner.loadUrl("javascript:window.mraidbridge.nativeCallComplete('expand');");
		
	}
	
	protected void close(){
		if(!expanded) return;
		AdView.LayoutParams lp = new AdView.LayoutParams(owner.getLayoutParams());
		lp.height=default_height;
		lp.width=default_width;
		lp.gravity=Gravity.CENTER;
		owner.setLayoutParams(lp);
		
		this.owner.loadUrl("javascript:window.mraidbridge.fireChangeEvent({state:'default'});");
		expanded=false;
	}

	protected void expand(ArrayList<BasicNameValuePair> parameters) {
		int width=owner.getLayoutParams().width;//Use current height and width as expansion defaults.
		int height=owner.getLayoutParams().height;
		for(BasicNameValuePair bnvp : parameters){
			if(bnvp.getName().equals("w")) width = Integer.parseInt(bnvp.getValue());
			else if(bnvp.getName().equals("h")) height = Integer.parseInt(bnvp.getValue());
		}
		//TODO: Use custom close
		//TODO: lockOrientation
		
		//Store width and height for close()
		default_width = owner.getLayoutParams().width;
		default_height = owner.getLayoutParams().height;
		
		owner.expand(width, height);
		//Fire the stateChange
		this.owner.loadUrl("javascript:window.mraidbridge.fireChangeEvent({state:'expanded'});");
		expanded=true;
	}

	@Override
	protected MRAIDWebChromeClient getWebChromeClient() {
		return new MRAIDWebChromeClient(){
			
		};
	}

	@Override
	protected String getMraidDotJS(Resources r) {
		InputStream ins = r.openRawResource(R.raw.mraid);
		try {
			byte[] buffer = new byte[ins.available()];
			ins.read(buffer);
			return new String(buffer, "UTF-8");
		} catch (IOException e) {
			return null;
		}	
	}

	@Override
	protected void onVisible() {
		if(readyFired)
			owner.loadUrl("javascript:window.mraidbridge.fireChangeEvent({state:'"+(expanded==true?"expanded":"default")+"'});");
		
	}

	@Override
	protected void onInvisible() {
		if(readyFired)
			owner.loadUrl("javascript:window.mraidbridge.fireChangeEvent({state:'hidden'});");
	}

}
