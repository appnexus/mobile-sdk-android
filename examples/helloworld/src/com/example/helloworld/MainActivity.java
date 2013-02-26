package com.example.helloworld;

import com.appnexus.mraid.MRAIDWebView;
import com.appnexus.opensdk.BannerAdView;
import com.appnexus.opensdk.InterstitialAdView;
import com.appnexus.opensdk.AdListener;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements AdListener{
	private BannerAdView av;
	InterstitialAdView iav;
	private int interstitials;
	Button showButton;

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
		
		//mraid
		MRAIDWebView mraid_space = (MRAIDWebView) findViewById(R.id.mraid_container);
		mraid_space.load(mraid_space.getAdMobTestBannerDotJs());
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
