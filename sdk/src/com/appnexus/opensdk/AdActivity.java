package com.appnexus.opensdk;

import com.appnexus.opensdk.utils.Clog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;

public class AdActivity extends Activity {
	
	FrameLayout layout;
	long now;
	int orientation;
	
	@SuppressLint("InlinedApi")
	@Override
	public void onCreate(Bundle b){
		super.onCreate(b);
		layout = new FrameLayout(this);
		
		Activity a = this;
		if(a!=null){
			Display d = ((WindowManager)a.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			switch (this.getResources().getConfiguration().orientation){
		        case Configuration.ORIENTATION_PORTRAIT:
		            if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.FROYO){
		            	a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		            } else {
		                int rotation = d.getRotation();
		            if(rotation == android.view.Surface.ROTATION_90|| rotation == android.view.Surface.ROTATION_180){
		                    a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
		                } else {
		                    a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		                }
		            }   
		        break;

		        case Configuration.ORIENTATION_LANDSCAPE:
		            if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.FROYO){
		                a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		            } else {
		                int rotation = d.getRotation();
		                if(rotation == android.view.Surface.ROTATION_0 || rotation == android.view.Surface.ROTATION_90){
		                    a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		                } else {
		                    a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
		                }
		            }
		        break;
			}
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
