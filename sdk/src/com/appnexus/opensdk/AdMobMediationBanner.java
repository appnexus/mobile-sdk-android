package com.appnexus.opensdk;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.TextView;

import com.appnexus.opensdk.utils.Clog;
import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdSize;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.mediation.MediationAdRequest;
import com.google.ads.mediation.customevent.CustomEventBanner;
import com.google.ads.mediation.customevent.CustomEventBannerListener;

public class AdMobMediationBanner implements CustomEventBanner, AdListener {
	AdRequest ar;
	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDismissScreen(Ad arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLeaveApplication(Ad arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPresentScreen(Ad arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReceiveAd(Ad arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void requestBannerAd(CustomEventBannerListener listener, final Activity activity,
			String label, String serverParameter, AdSize adSize, MediationAdRequest mediationAdRequest,
			Object extra) {
		
		BannerAdView appNexusAdView = new BannerAdView(activity, serverParameter);
		appNexusAdView.setAdHeight(adSize.getHeight());
		appNexusAdView.setAdWidth(adSize.getWidth());

		listener.onReceivedAd(appNexusAdView);

	}

}
