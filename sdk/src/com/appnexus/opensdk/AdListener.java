package com.appnexus.opensdk;

/**
 * Implement this interface to create a class which can react to ad events.
 * @author Jacob Shufro
 *
 */
public interface AdListener{
	/**
	 * Called when an ad has successfully been loaded from the server.
	 * @param iAdView	The {@link AdView} that loaded the ad.
	 */
	public void onAdLoaded(AdView adView);
	/**
	 * Called when ad ad request fails.
	 * @param iAdView The {@link AdView} that loaded the ad.
	 */
	public void onAdRequestFailed(AdView adView);
}
