package com.appnexus.opensdk;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class AdActivity extends Activity {
	
	FrameLayout layout;
	
	@Override
	public void onCreate(Bundle b){
		super.onCreate(b);
		layout = new FrameLayout(this);
		setContentView(layout);
		setIAdView(InterstitialAdView.INTERSTITIALADVIEW_TO_USE);
	}
	
	protected void setIAdView(InterstitialAdView av){
		if(layout!=null){ 
			layout.setBackgroundColor(av.getBackgroundColor());
			layout.removeAllViews();
			if(((ViewGroup)av.getParent())!=null){
				((ViewGroup)av.getParent()).removeAllViews();
			}
			layout.addView(InterstitialAdView.q.poll().getView());
		}
	}

}
