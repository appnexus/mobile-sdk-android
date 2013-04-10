package com.appnexus.opensdk;

import android.app.Activity;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.mediation.MediationAdRequest;
import com.google.ads.mediation.customevent.CustomEventInterstitial;
import com.google.ads.mediation.customevent.CustomEventInterstitialListener;

public class AdMobMediationInterstitial implements CustomEventInterstitial,
		AdListener, com.appnexus.opensdk.AdListener {
	InterstitialAdView iav=null;
	CustomEventInterstitialListener listener;

	@Override
	public void destroy() {
		if(iav!=null) iav.destroy();
	}

	@Override
	public void onDismissScreen(Ad arg0) {
		if(listener!=null) listener.onDismissScreen();
	}

	@Override
	public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
		if(listener!=null) listener.onFailedToReceiveAd();
	}

	@Override
	public void onLeaveApplication(Ad arg0) {
		if(listener!=null) listener.onLeaveApplication();
	}

	@Override
	public void onPresentScreen(Ad arg0) {
		if(listener!=null) listener.onPresentScreen();
	}

	@Override
	public void onReceiveAd(Ad arg0) {
		if(listener!=null) listener.onReceivedAd();
	}

	@Override
	public void requestInterstitialAd(CustomEventInterstitialListener listener,
			Activity activity, String label, String placement_id, MediationAdRequest adRequest,
			Object extra) {
		iav=new InterstitialAdView(activity, placement_id);
		this.listener=listener;
		iav.loadAd();
	}

	@Override
	public void showInterstitial() {
		if(iav!=null) iav.show();
	}

	@Override
	public void onAdLoaded(InterstitialAdView iAdView) {
		onReceiveAd(null);
	}

	@Override
	public void onAdRequestFailed(InterstitialAdView iAdView) {
		onFailedToReceiveAd(null, null);
	}

}
