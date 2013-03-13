package com.appnexus.opensdk;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

public class AdActivity extends Activity {
	
	FrameLayout layout;
	long now;
	int orientation;
	
	@Override
	public void onCreate(Bundle b){
		super.onCreate(b);
		layout = new FrameLayout(this);
		
		orientation=getIntent().getIntExtra("Orientation", Configuration.ORIENTATION_LANDSCAPE);
		switch(orientation){
		case Configuration.ORIENTATION_LANDSCAPE:
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			break;
		case Configuration.ORIENTATION_PORTRAIT:
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			break;
		}
		setContentView(layout);
		
		setIAdView(InterstitialAdView.INTERSTITIALADVIEW_TO_USE);
		now = getIntent().getLongExtra("Time", System.currentTimeMillis());
		
		//Ads a close button.
		ImageButton close = new ImageButton(this);
		close.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_close_clear_cancel));
		FrameLayout.LayoutParams blp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.RIGHT | Gravity.TOP);
		close.setLayoutParams(blp);
		close.setBackgroundColor(Color.TRANSPARENT);
		close.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
		layout.addView(close);
		
	}
	
	protected void setIAdView(InterstitialAdView av){
		if(layout!=null){ 
			layout.setBackgroundColor(av.getBackgroundColor());
			layout.removeAllViews();
			if(((ViewGroup)av.getParent())!=null){
				((ViewGroup)av.getParent()).removeAllViews();
			}
			Pair<Long, Displayable> p = InterstitialAdView.q.poll();
			while(p!=null && p.second!=null && now-p.first > InterstitialAdView.MAX_AGE){
				Clog.w(Clog.baseLogTag, Clog.getString(R.string.too_old));
				p=InterstitialAdView.q.poll();
			}
			if(p==null) return;
			layout.addView(p.second.getView());
		}
	}

}
