package com.appnexus.opensdk;

import com.appnexus.opensdk.util.TestUtil;
import com.appnexus.opensdk.utils.Clog;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@Config(sdk = 21)
@RunWith(RobolectricTestRunner.class)
public class BaseViewAdTest extends BaseRoboTest implements AdListener {

    BannerAdView bannerAdView;
    InterstitialAdView interstitialAdView;
    AdViewRequestManager requestManager;

    boolean adLoaded, adFailed, adExpanded, adCollapsed, adClicked, adClickedWithUrl;
    boolean isAutoDismissDelay, enableInterstitialShowonLoad;
    NativeAdResponse nativeAdResponse;
    private boolean isBannerLoaded;

    public void setAutoDismissDelay(boolean autoDismissDelay) {
        isAutoDismissDelay = autoDismissDelay;
    }

    public void setInterstitialShowonLoad(boolean interstitialShowonLoad) {
        enableInterstitialShowonLoad = interstitialShowonLoad;
    }


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
        adClickedWithUrl = false;

    }

    public void assertCallbacks(boolean success) {
        assertEquals(success, adLoaded);
        assertEquals(!success, adFailed);
    }

    public void assertOpensInNativeBrowser() {
        assertEquals(bannerAdView.getOpensNativeBrowser(), ((ANNativeAdResponse) nativeAdResponse).isOpenNativeBrowser());
    }

    public void assertClickThroughAction() {
        System.out.println("BANNER CLICKTHROUGH: " + bannerAdView.getClickThroughAction() + ", NATIVEADRESPONSE CLICKTHROUGH: " + ((ANNativeAdResponse) nativeAdResponse).getClickThroughAction());
        assertEquals(bannerAdView.getClickThroughAction(), ((ANNativeAdResponse) nativeAdResponse).getClickThroughAction());
    }

    public void assertClickThroughAction(ANClickThroughAction clickThroughAction) {
        assertEquals(clickThroughAction, ((ANNativeAdResponse) nativeAdResponse).getClickThroughAction());
    }

    public void assertLoadsInBackground() {
        assertEquals(bannerAdView.getLoadsInBackground(), ((ANNativeAdResponse) nativeAdResponse).getLoadsInBackground());
    }

    public void assertBannerAdResponse(boolean isBannerLoaded) {
        assertEquals(isBannerLoaded, this.isBannerLoaded);
    }

    @Test
    public void testDummy() {
        assertTrue(true);
    }

    @Override
    public void onAdLoaded(AdView adView) {
        Clog.w(TestUtil.testLogTag, "BaseViewAdTest onAdLoaded");
        adLoaded = true;
        if (adView.getMediaType() == MediaType.BANNER) {
            isBannerLoaded = true;
        }
        if (adView.getMediaType() == MediaType.INTERSTITIAL) {
            if (enableInterstitialShowonLoad) {
                if (isAutoDismissDelay) {
                    interstitialAdView.showWithAutoDismissDelay(5);
                } else {
                    interstitialAdView.show();
                }
            }
        }
    }

    @Override
    public void onAdLoaded(NativeAdResponse nativeAdResponse) {
        Clog.w(TestUtil.testLogTag, "BaseViewAdTest onAdLoaded(NativeAdResponse nativeAdResponse)");
        adLoaded = true;
        this.nativeAdResponse = nativeAdResponse;
        isBannerLoaded = false;
    }

    @Override
    public void onAdRequestFailed(AdView adView, ResultCode resultCode) {
        Clog.w(TestUtil.testLogTag, "BaseViewAdTest onAdRequestFailed");
        adFailed = true;
    }

    @Override
    public void onAdExpanded(AdView adView) {
        Clog.w(TestUtil.testLogTag, "BaseViewAdTest onAdExpanded");
        adExpanded = true;
    }

    @Override
    public void onAdCollapsed(AdView adView) {
        Clog.w(TestUtil.testLogTag, "BaseViewAdTest onAdCollapsed");
        adCollapsed = true;
    }

    @Override
    public void onAdClicked(AdView adView) {
        Clog.w(TestUtil.testLogTag, "BaseViewAdTest onAdClicked");
        adClicked = true;
    }

    @Override
    public void onAdClicked(AdView adView, String clickUrl) {
        Clog.w(TestUtil.testLogTag, "BaseViewAdTest onAdClickedWithUrl");
        adClickedWithUrl = true;
    }

}

