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
import android.view.View;

/**
 * The interface a mediation adaptor must implement for requesting banners. 
 * The mediation interface allows 3rd party SDK's to be called via the 
 * AppNexus SDK. To integrate a 3rd party SDK create a class that implements
 * MediatedBannerAdView. Implement the required method and configure it within 
 * the AppNexus Network Manager to be called whenever the network targeting 
 * matches the conditions that are defined in Network Manager.
 * 
 *
 */
public interface MediatedBannerAdView extends MediatedAdView {
	/**
	 * The AppNexus SDK will call this method to ask the 3rd party SDK to request an Ad 
	 * from its network. The  AppNexus SDK expects to be notified of events through the 
	 * {@link MediatedBannerAdViewController} Note that once a requestAd call has been made 
	 * the AppNexus SDK expects a onAdLoaded or onAdFailed called through the MediatedBannerAdViewController
	 * within 15 seconds or the mediation call is considered failed. 
	 * 
	 * @param mBC The controller to notify on load, failure etc.
	 * @param activity The activity from which this call was made. 
	 * @param parameter An optional opaque string passed from Network Manager , this can be used to defined
	 * SDK specific parameters such as additional targeting information. The encoding of the contents of this 
	 * string are entirely up to the implementation of the 3rd party SDK adaptor. 
	 * @param uid The network ID for this ad call. This ID is opaque to the AppNexus SDK and its contents and their encoding
	 * are up to the implementation of the 3rd party SDK.
	 * @param width The width of the advertisement as defined in the {@link BannerAdView} object that initiated this call.
	 * @param height The height of the advertisement as defined in the {@link BannerAdView} object that initiated this call.
	 * @return A view that will be inserted into the BannerAdView that will hold the ad from the 3rd party SDK
	 */
    public View requestAd(MediatedBannerAdViewController mBC, Activity activity, String parameter, String uid, int width, int height);
}
