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

/**
 * Implement this interface to create a class which can react to ad events.
 * 
 * @author Jacob Shufro
 * 
 */
public interface AdListener {
	/**
	 * Called when an ad has successfully been loaded from the server.
	 * 
	 * @param adView
	 *            The {@link AdView} that loaded the ad.
	 */
	public void onAdLoaded(AdView adView);

	/**
	 * Called when an ad request fails.
	 * 
	 * @param adView
	 *            The {@link AdView} that loaded the ad.
	 */
	public void onAdRequestFailed(AdView adView);
	
	/**
	 * Called when an ad expands due to interaction.
	 * 
	 * @param adView
	 *            The {@link AdView} that loaded the ad.
	 */
	public void onAdExpanded(AdView adView);
	
	/**
	 * Called when an ad is closed/unexpanded.
	 * 
	 * @param adView
	 *            The {@link AdView} that loaded the ad.
	 */
	public void onAdCollapsed(AdView adView);
	
	/**
	 * Called when an ad is clicked, and the user is directed to the landing page.
	 * 
	 * @param adView
	 *            The {@link AdView} that loaded the ad.
	 */
	public void onAdClicked(AdView adView);
}
