package com.appnexus.opensdk;

import android.app.Activity;

/**
 * This interface must be implemented by 3rd Party SDK's that which to have Interstitial views that are 
 * mediated by the AppNexus SDK.  To integrate a 3rd party SDK create a class that implements
 * MediatedInterstitialAdView. Implement the required methods and configure it within 
 * the AppNexus Network Manager to be called whenever the network targeting 
 * matches the conditions that are defined in Network Manager.
 *
 */
public interface MediatedInterstitialAdView extends MediatedAdView {
	/**
	 * The AppNexus SDK will call this method in your class when directed to do so from Network Manager. 
	 * Where an application has instantiated an InterstitialAdView.
	 * @param mIC A controller through which the adapter must send events to the AppNexus SDK
	 * @param parameter An optional opaque string passed from Network Manager , this can be used to defined
	 * SDK specific parameters such as additional targeting information. The encoding of the contents of this 
	 * string are entirely up to the implementation of the 3rd party SDK adaptor. 
	 * @param uid The network ID for this ad call. This ID is opaque to the AppNexus SDK and its contents and their encoding
	 * are up to the implementation of the 3rd party SDK. 	 */
    public void requestAd(MediatedInterstitialAdViewController mIC, Activity activity, String parameter, String uid);

    /**
     * The AppNexus SDK will call this method to show the interstitial view when the user has call
     * InterstitialAdView.show() {@link InterstitialAdView} 
     */
    public void show();

    /**
     * The AppNexus SDK will call this method to check if the interstitial view is ready when the user
     * calls InterstitialAdView.isReady() {@link InterstitialAdView}
     */
    public boolean isReady();
}
