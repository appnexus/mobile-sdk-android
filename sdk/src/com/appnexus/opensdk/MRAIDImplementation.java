package com.appnexus.opensdk;

import com.appnexus.opensdk.MRAIDWebView;

import android.content.res.Resources;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public abstract class MRAIDImplementation {
	MRAIDWebView owner;
	
	public MRAIDImplementation(MRAIDWebView owner){
		this.owner=owner;
	}
	
	//The webview about to load the ad, and the html ad content
	protected String onPreLoadContent(WebView wv, String html){
		//Check to ensure <html> tags are present
		if(!html.contains("<html>")){
			html="<html><head></head><body style='padding:0;margin:0;'>"+html+"</body></html>";
			Log.d("MRAID", "ADDING HTML TAGS");
		}
		
		//Insert mraid script source
		html=html.replace("<head>", "<head><script>"+getMraidDotJS(wv.getResources())+"</script>");
		
		return html;
	}
	
	protected abstract String getMraidDotJS(Resources r);
	
	protected void onReceivedError(WebView view, int errorCode, String desc,
			String failingUrl) {
		Log.w("MRAID", String.format("Error %n received, %s, while fetching url %s", errorCode, desc, failingUrl));
	}
	
	abstract class MRAIDWebViewClient extends WebViewClient{
		@Override
		public abstract void onPageFinished(WebView view, String url);
		
	}
	
	class MRAIDWebChromeClient extends WebChromeClient{
		@Override
		public boolean onConsoleMessage(ConsoleMessage consoleMessage){
			//super.onConsoleMessage(consoleMessage);
			Log.w("MRAID", "Received console message: "+consoleMessage.message()+" at line "+consoleMessage.lineNumber()+" sourceId "+consoleMessage.sourceId());
			return true;
		}
		
		@Override
		public boolean onJsAlert(WebView view, String url, String message, JsResult result){
			///super.onJsAlert(view, url, message, result);
			Log.w("MRAID", "Received JsAlert: "+message+" while loading "+url);
			result.confirm();
			return true;
		}
	}
	
	abstract protected MRAIDWebViewClient getWebViewClient();
	abstract protected MRAIDWebChromeClient getWebChromeClient();
	abstract protected void onVisible();
	abstract protected void onInvisible();
	abstract protected void close();

}
