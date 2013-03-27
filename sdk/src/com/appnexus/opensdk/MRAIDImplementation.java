package com.appnexus.opensdk;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import com.appnexus.opensdk.MRAIDWebView;
import com.appnexus.opensdk.utils.Clog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.net.http.SslError;
import android.view.Gravity;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressLint("InlinedApi")
public class MRAIDImplementation {
	MRAIDWebView owner;
	boolean readyFired=false;
	boolean expanded=false;
	boolean hidden=false;
	int default_width, default_height;
	
	public MRAIDImplementation(MRAIDWebView owner){
		this.owner=owner;
	}
	
	//The webview about to load the ad, and the html ad content
	protected String onPreLoadContent(WebView wv, String html){
		//Check to ensure <html> tags are present
		if(!html.contains("<html>")){
			html="<html><head></head><body style='padding:0;margin:0;'>"+html+"</body></html>";
		} else if (!html.contains("<head>")) {
			// The <html> tags are present, but there is no <head> section to
			// inject the mraid js
			html = html.replace("<html>", "<html><head></head>");
		}
		
		//Insert mraid script source
		html=html.replace("<head>", "<head><script>"+getMraidDotJS(wv.getResources())+"</script>");
		
		return html;
	}
	
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
	
	protected void onReceivedError(WebView view, int errorCode, String desc,
			String failingUrl) {
		Clog.w(Clog.mraidLogTab, Clog.getString(R.string.webview_received_error, errorCode, desc, failingUrl));
	}
	
	protected WebViewClient getWebViewClient() {
		

		return new WebViewClient(){

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (!url.startsWith("mraid:") && !url.startsWith("javascript:")) {
					Intent intent = new Intent(Intent.ACTION_VIEW,
							Uri.parse(url));
					view.getContext().startActivity(intent);
					return true;
				}
				if(url.startsWith("mraid://")){
					MRAIDImplementation.this.dispatch_mraid_call(url);
					
					return true;
				}
				
				// See if any native activities can handle the Url
				try{
					owner.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
					return true;
				} catch(ActivityNotFoundException e){
					return false;
				}
			}
			
			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
				Clog.e(Clog.httpRespLogTag, Clog.getString(R.string.webclient_error, error.getPrimaryError(), error.toString()));
			}
			
			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingURL){
				Clog.e(Clog.httpRespLogTag, Clog.getString(R.string.webclient_error, errorCode, description));
			}
			
			@Override
			public void onPageFinished(WebView view, String url) {
				//Fire the ready event only once
				if(!readyFired){
					view.loadUrl("javascript:window.mraid.util.setPlacementType('"+owner.owner.getMRAIDAdType()+"')");
					view.loadUrl("javascript:window.mraid.util.setIsViewable(true)");
					view.loadUrl("javascript:window.mraid.util.stateChangeEvent('default')");
					view.loadUrl("javascript:window.mraid.util.readyEvent();");
					
					//Store width and height for close()
					default_width = owner.getLayoutParams().width;
					default_height = owner.getLayoutParams().height;
					
					readyFired = true;
				}
			}
		};
	}

	protected WebChromeClient getWebChromeClient() {
		return new WebChromeClient(){
			@Override
			public boolean onConsoleMessage(ConsoleMessage consoleMessage){
				//super.onConsoleMessage(consoleMessage);
				Clog.w(Clog.mraidLogTab, Clog.getString(R.string.console_message, consoleMessage.message(), consoleMessage.lineNumber(),consoleMessage.sourceId()));
				return true;
			}
			
			@Override
			public boolean onJsAlert(WebView view, String url, String message, JsResult result){
				///super.onJsAlert(view, url, message, result);
				Clog.w(Clog.mraidLogTab, Clog.getString(R.string.js_alert, message, url));
				result.confirm();
				return true;
			}
		};
	}

	protected void onVisible() {
		if(readyFired)
			owner.loadUrl("javascript:window.mraid.util.setIsViewable(true)");
		
	}

	protected void onInvisible() {
		if(readyFired)
			owner.loadUrl("javascript:window.mraid.util.setIsViewable(false)");
	}
	
	protected void close(){
		if(expanded){
			AdView.LayoutParams lp = new AdView.LayoutParams(owner.getLayoutParams());
			lp.height=default_height;
			lp.width=default_width;
			lp.gravity=Gravity.CENTER;
			owner.setLayoutParams(lp);
			owner.close();
			this.owner.loadUrl("javascript:window.mraid.util.stateChangeEvent('default');");
			
			//Allow orientation changes
			Activity a = ((Activity)this.owner.getContext());
			if(a!=null)
				a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
			expanded=false;
		}else{
			//state must be default
			owner.hide();
			hidden=true;
		}
	}
	
	protected void expand(ArrayList<BasicNameValuePair> parameters) {
		if(!hidden){
			int width=owner.getLayoutParams().width;//Use current height and width as expansion defaults.
			int height=owner.getLayoutParams().height;
			boolean useCustomClose=false;
			for(BasicNameValuePair bnvp : parameters){
				if(bnvp.getName().equals("w")) width = Integer.parseInt(bnvp.getValue());
				else if(bnvp.getName().equals("h")) height = Integer.parseInt(bnvp.getValue());
				else if(bnvp.getName().equals("useCustomClose")) useCustomClose = Boolean.parseBoolean(bnvp.getValue());
			}
		
			owner.expand(width, height, useCustomClose, this);
			//Fire the stateChange
			this.owner.loadUrl("javascript:window.mraid.util.stateChangeEvent('expanded');");
			expanded=true;
			//Lock the orientation
			AdActivity.lockOrientation((Activity)this.owner.getContext());

		}else{
			owner.show();
			hidden=false;
		}
	}
	
	protected void dispatch_mraid_call(String url) {
		//Remove the fake protocol
		url = url.replaceFirst("mraid://", "");
		
		//Separate the function from the parameters
		String func = url.split("\\?")[0].replaceAll("/", "");
		String params;
		ArrayList<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
		if(url.split("\\?").length>1){
			params = url.split("\\?")[1];
		
			for(String s : params.split("&")){
				parameters.add(new BasicNameValuePair(s.split("=")[0], s.split("=")[1]));
			}
		}
		
		if(func.equals("expand")){
			expand(parameters);
		}else if(func.equals("close")){
			close();
		}
	}
}
