package com.appnexus.opensdk;


import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

@SuppressLint("ViewConstructor")
public class MRAIDWebView extends WebView implements Displayable {
	private MRAIDImplementation implementation;
	private boolean failed=false;
	private AdView owner;
	public MRAIDWebView(AdView owner) {
		super(owner.getContext());
		this.owner=owner;
		setup();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void setup(){
		this.getSettings().setJavaScriptEnabled(true);
		this.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		this.getSettings().setPluginState(WebSettings.PluginState.ON);
		this.getSettings().setBuiltInZoomControls(false);
		this.getSettings().setLightTouchEnabled(false);
		this.getSettings().setLoadsImagesAutomatically(true);
		this.getSettings().setSupportZoom(false);
		this.getSettings().setUseWideViewPort(true);
		this.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

	}
	
	protected void setImplementation(MRAIDImplementation imp){
		implementation=imp;
		this.setWebViewClient(imp.getWebViewClient());
		this.setWebChromeClient(imp.getWebChromeClient());
	}
	
	protected MRAIDImplementation getImplementation(){
		return implementation;
	}

	public void loadAd(AdResponse ar){	
		Log.d("MRAID", "Loading an MRAID ad");
		String html = ar.getBody();
		
		if(html.contains("mraid.js")){
			setImplementation(new MRAIDImplementation(this));
		}
		
		if(implementation!=null){
			html=implementation.onPreLoadContent(this, html);
		}
		
		final float scale = owner.getContext().getResources().getDisplayMetrics().density;
		int rheight = (int)(ar.getHeight()*scale+0.5f);
		int rwidth = (int)(ar.getWidth()*scale+0.5f);
		int rgravity=Gravity.CENTER;
		AdView.LayoutParams resize = new AdView.LayoutParams(rwidth, rheight, rgravity);
		this.setLayoutParams(resize);
		
		// Save the html to a file for debugging TODO remove this
		try {
			DataOutputStream out = new DataOutputStream(getContext().openFileOutput("last_mraid_ad.html", Context.MODE_PRIVATE));
			out.writeUTF(html);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
	}
	
	@Override
	public void onVisibilityChanged(View view, int visibility){
		switch(visibility){
			case View.VISIBLE:
				if(implementation!=null) implementation.onVisible();
				break;
			default:
				if(implementation!=null) implementation.onInvisible();
				break;
		}
	}
	
	//w,h in dips. this function converts to pixels
	protected void expand(int w, int h){
		//TODO change these to FrameLayout.LayoutParams, since this gets added to an AdView
		DisplayMetrics metrics = new DisplayMetrics();
		((WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
		h = (int) (h*metrics.density+0.5);
		w = (int) (w*metrics.density+0.5);
		
		AdView.LayoutParams lp = new AdView.LayoutParams(this.getLayoutParams());
		lp.height=h;
		lp.width=w;
		lp.gravity=Gravity.CENTER;
		
		if(owner!=null){
			owner.expand(w, h);
		}
		
		this.setLayoutParams(lp);
	}

	@Override
	public View getView() {
		return this;
	}

	@Override
	public boolean failed() {
		return failed;
	}
}
