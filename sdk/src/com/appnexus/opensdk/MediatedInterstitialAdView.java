package com.appnexus.opensdk;

import android.app.Activity;

/**
 * This interface must be implemented by third-party SDKs whose
 * interstitial views will be mediated by the AppNexus SDK.  Implement
 * the required methods and configure them within the AppNexus Ad
 * Network Manager to be called whenever the targeting matches the
 * conditions defined in the Ad Network Manager.  (The Ad Network
 * Manager is a web application that AppNexus platform members can use
 * to work with ad networks that are not on the platform.)
 */

public interface MediatedInterstitialAdView extends MediatedAdView {

    /**
     * The AppNexus SDK will call this method in your class when
     * directed to do so from the Ad Network Manager, where an
     * application has instantiated an {@link InterstitialAdView}.
     *
     * @param mIC A controller through which the adaptor must send
     *            events to the AppNexus SDK.
     *
     * @param parameter An optional opaque string passed from the Ad
     *                  Network Manager, this can be used to define
     *                  SDK-specific parameters such as additional
     *                  targeting information.  The encoding of the
     *                  contents of this string are entirely up to the
     *                  implementation of the third-party SDK adaptor.
     *
     * @param uid The network ID for this ad call.  This ID is opaque
     *            to the AppNexus SDK; the ID's contents and their
     *            encoding are up to the implementation of the
     *            third-party SDK.
     */

    public void requestAd(MediatedInterstitialAdViewController mIC, Activity activity, String parameter, String uid, TargetingParameters tp);

    /**
     * The AppNexus SDK will call this method to show the interstitial
     * view when the user has called
     * <code>InterstitialAdView.show()</code>. (See {@link
     * InterstitialAdView}.)
     */

    public void show();

    /**
     * The AppNexus SDK will call this method to check if the
     * interstitial view is ready when the user calls
     * <code>InterstitialAdView.isReady()</code>. (See {@link
     * InterstitialAdView}.)
     *
     * @return <code>true</code> if the interstitial ad is ready to
     * show, <code>false</code> otherwise.
     */

    public boolean isReady();

}
