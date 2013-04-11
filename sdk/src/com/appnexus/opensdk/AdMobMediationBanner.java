package com.appnexus.opensdk;

import android.app.Activity;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdSize;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.mediation.MediationAdRequest;
import com.google.ads.mediation.customevent.CustomEventBanner;
import com.google.ads.mediation.customevent.CustomEventBannerListener;

public class AdMobMediationBanner implements CustomEventBanner, AdListener {
	AdRequest ar;
	CustomEventBannerListener listener;
	@Override
	public void destroy() {
		
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
		if(listener!=null)	listener.onReceivedAd(null);

	}

	@Override
	public void requestBannerAd(CustomEventBannerListener listener, final Activity activity,
			String label, String serverParameter, AdSize adSize, MediationAdRequest mediationAdRequest,
			Object extra) {
		
		this.listener=listener;
		
		BannerAdView appNexusAdView = new BannerAdView(activity, serverParameter);
		appNexusAdView.setAdHeight(adSize.getHeight());
		appNexusAdView.setAdWidth(adSize.getWidth());

		listener.onReceivedAd(appNexusAdView);

	}

}
