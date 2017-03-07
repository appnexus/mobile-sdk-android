package com.appnexus.opensdk;

import com.appnexus.opensdk.util.RoboelectricTestRunnerWithResources;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(RoboelectricTestRunnerWithResources.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class BaseViewAdTest extends BaseRoboTest implements AdListener {

    BannerAdView bannerAdView;
    InterstitialAdView interstitialAdView;
    RequestManager requestManager;

    boolean adLoaded, adFailed, adExpanded, adCollapsed, adClicked;

    @Override
    public void setup() {
        super.setup();
        bannerAdView = new BannerAdView(activity);
        bannerAdView.setPlacementID("0");
        bannerAdView.setAdListener(this);
        bannerAdView.setAdSize(320, 50);
        bannerAdView.setAutoRefreshInterval(-1);

        interstitialAdView = new InterstitialAdView(activity);
        interstitialAdView.setPlacementID("0");
        interstitialAdView.setAdListener(this);

        adLoaded = false;
        adFailed = false;
        adExpanded = false;
        adCollapsed = false;
        adClicked = false;
    }

    public void assertCallbacks(boolean success) {
        assertEquals(success, adLoaded);
        assertEquals(!success, adFailed);
    }

    @Test
    public void testDummy() {
        assertTrue(true);
    }

    @Override
    public void onAdLoaded(AdView adView) {
        adLoaded = true;
    }

    @Override
    public void onAdRequestFailed(AdView adView, ResultCode resultCode) {
        adFailed = true;
    }

    @Override
    public void onAdExpanded(AdView adView) {
        adExpanded = true;
    }

    @Override
    public void onAdCollapsed(AdView adView) {
        adCollapsed = true;
    }

    @Override
    public void onAdClicked(AdView adView) {
        adClicked = true;
    }

}

