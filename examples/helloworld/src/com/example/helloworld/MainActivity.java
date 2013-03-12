package com.example.helloworld;

import com.appnexus.opensdk.BannerAdView;
import com.appnexus.opensdk.InterstitialAdView;
import com.appnexus.opensdk.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import android.os.Bundle;
import android.app.Activity;
import android.gesture.GestureOverlayView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class MainActivity extends Activity implements AdListener{
	private BannerAdView av;
	InterstitialAdView iav;
	RelativeLayout layout;
	private int interstitials;
	Button showButton;
	private AdView adview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Interstitial
		showButton = (Button) findViewById(R.id.showbutton);
		showButton.setClickable(false);
		showButton.setEnabled(false);
		iav = new InterstitialAdView(this);
		iav.setPlacementID("656561");
		iav.setAdListener(this);
		
		//admob
		if(true) return;
		layout=(RelativeLayout) findViewById(R.id.mainview);
		AdView av = new AdView(this, AdSize.BANNER, "a1512e787095f19");
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 50);
		lp.addRule(RelativeLayout.ABOVE, R.id.ll);
		lp.alignWithParent=true;
		av.setLayoutParams(lp);
		
		layout.addView(av);
		AdRequest ar = new AdRequest();
		ar.addTestDevice("AE736B8A6A42CDC5B796A8A2BAB34524");
		av.loadAd(ar);
		GestureOverlayView gov = (GestureOverlayView)av.getChildAt(0);
		FrameLayout fl = (FrameLayout) gov.getChildAt(0);
		for(int i=0;i<fl.getChildCount();i++){
			Log.d("ADMOB", fl.getChildAt(i).toString());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public boolean loadAd(MenuItem mi){
		av.loadAd();
		return true;
	}
	
	public void loadIA(View view){
		iav.loadAd();
	}
	
	public void showIA(View view){
		interstitials=iav.show();
		if(interstitials<1){
			showButton.setClickable(false);
			showButton.setEnabled(false);
		}
	}

	@Override
	public void onAdLoaded(InterstitialAdView iAdView) {
		interstitials++;
		if(interstitials>0){
			showButton.setClickable(true);
			showButton.setEnabled(true);
		}
		
	}

	@Override
	public void onAdRequestFailed(InterstitialAdView iAdView) {
		Log.e("HelloWorld", "Ad request failed");
		
	}
}
