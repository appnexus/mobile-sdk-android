package com.appnexus.opensdk;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;


public class BrowserActivity extends Activity {
	private WebView webview;
	private ImageButton back;
	private ImageButton forward;
	private ImageButton refresh;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstance){
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_in_app_browser);
		
		webview = (WebView) findViewById(R.id.web_view);
		back = (ImageButton) findViewById(R.id.browser_back);
		forward = (ImageButton) findViewById(R.id.browser_forward);
		refresh = (ImageButton) findViewById(R.id.browser_refresh);
		
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webview.getSettings().setDomStorageEnabled(true);
		webview.getSettings().setPluginState(PluginState.ON_DEMAND);
		
		
		back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(webview.canGoBack()){
					webview.goBack();
				}else{
					finish();
				}
			}
			
		});
		
		forward.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				webview.goForward();
			}
		});
		
		refresh.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				webview.reload();
			}
		});
		
		
		webview.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url){
				if(url.startsWith("http")){
					webview.loadUrl(url);
					return true;
				}
				return false;
			}
		});
		
		String url = (String) getIntent().getExtras().get("url");
		
		webview.loadUrl(url);
	}
}
