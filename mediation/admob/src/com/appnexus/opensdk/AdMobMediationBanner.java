/*
 *    Copyright 2013 APPNEXUS INC
 *    
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *    
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
*/

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
		
		BannerAdView appNexusAdView = new BannerAdView(activity);
		appNexusAdView.setPlacementID(serverParameter);
		appNexusAdView.setAdHeight(adSize.getHeight());
		appNexusAdView.setAdWidth(adSize.getWidth());
		appNexusAdView.setShouldServePSAs(false);
		listener.onReceivedAd(appNexusAdView);

	}

}
