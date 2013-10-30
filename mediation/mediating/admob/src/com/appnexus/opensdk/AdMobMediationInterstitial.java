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
		iav=new InterstitialAdView(activity);
		iav.setPlacementID(placement_id);
		iav.setAdListener(this);
		iav.setShouldServePSAs(false);
		this.listener=listener;
		iav.loadAd();
	}

	@Override
	public void showInterstitial() {
		if(iav!=null) iav.show();
	}

	@Override
	public void onAdLoaded(AdView adView) {
		onReceiveAd(null);
	}

	@Override
	public void onAdRequestFailed(AdView adView) {
		onFailedToReceiveAd(null, null);
	}

	@Override
	public void onAdExpanded(AdView adView) {
		onPresentScreen(null);
		
	}

	@Override
	public void onAdCollapsed(AdView adView) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAdClicked(AdView adView) {
		onLeaveApplication(null);
		
	}

}
