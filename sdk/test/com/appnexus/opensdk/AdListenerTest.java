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

import android.webkit.WebView;

import com.appnexus.opensdk.mocks.MockDefaultExecutorSupplier;
import com.appnexus.opensdk.shadows.ShadowAsyncTaskNoExecutor;
import com.appnexus.opensdk.shadows.ShadowCustomWebView;
import com.appnexus.opensdk.shadows.ShadowSettings;
import com.appnexus.opensdk.shadows.ShadowWebSettings;
import com.appnexus.opensdk.util.Lock;
import com.squareup.okhttp.mockwebserver.MockResponse;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertTrue;

@Config(sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class,
                ShadowCustomWebView.class, ShadowWebSettings.class, ShadowSettings.class, ShadowLog.class})
@RunWith(RobolectricTestRunner.class)
public class AdListenerTest extends BaseViewAdTest {

    @Override
    public void setup() {
        super.setup();
    }

    //This verifies that the AsyncTask for Request is being executed on the Correct Executor.
    @Test
    public void testRequestExecutorForBackgroundTasks() {
        SDKSettings.setExternalExecutor(MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
        assertNotSame(ShadowAsyncTaskNoExecutor.getExecutor(), MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
        requestManager = new AdViewRequestManager(bannerAdView);
        requestManager.execute();
        assertEquals(ShadowAsyncTaskNoExecutor.getExecutor(), MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
    }

    // Banner Testing

    @Test
    public void testBannerAdLoaded() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner()));
        requestManager = new AdViewRequestManager(bannerAdView);
        requestManager.execute();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertCallbacks(true);
    }

    @Test
    public void testLazyBannerAdLoaded() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner()));
        bannerAdView.enableLazyLoad();
        executeBannerRequest();
        assertLazyLoadCallbackInProgress();
        bannerAdView.loadLazyAd();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertLazyLoadCallbackSuccess();
    }

    // This proves that the second loadAd() behaves as a Lazy load even after the Lazy Ad has already been loaded once (after calling loadLazyAd())
    @Test
    public void testLazyBannerAdLoadedSuccessAndLoadAgain() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner()));
        bannerAdView.enableLazyLoad();
        executeBannerRequest();
        assertFalse(bannerAdView.getChildAt(0) instanceof WebView);
        assertLazyLoadCallbackInProgress();
        bannerAdView.loadLazyAd();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertLazyLoadCallbackSuccess();
        assertTrue(bannerAdView.getChildAt(0) instanceof WebView);
        adLoaded = false;
        adLazyLoaded = false;
        adFailed = false;
        restartServer();
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner()));
        executeBannerRequest();
        assertLazyLoadCallbackInProgress();
        bannerAdView.loadLazyAd();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertLazyLoadCallbackSuccess();
        assertTrue(bannerAdView.getChildAt(0) instanceof WebView);
    }

    // This proves that the second loadAd() also behaves as a Lazy load if the loadLazyAd() has not already been called before
    @Test
    public void testLazyBannerLazyAdLoadedAndLoadAgain() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner()));
        bannerAdView.enableLazyLoad();
        executeBannerRequest();
        assertFalse(bannerAdView.getChildAt(0) instanceof WebView);
        assertLazyLoadCallbackInProgress();
        adLoaded = false;
        adLazyLoaded = false;
        adFailed = false;
        restartServer();
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner()));
        executeBannerRequest();
        assertFalse(bannerAdView.getChildAt(0) instanceof WebView);
        assertLazyLoadCallbackInProgress();
        bannerAdView.loadLazyAd();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertLazyLoadCallbackSuccess();
        assertTrue(bannerAdView.getChildAt(0) instanceof WebView);
    }

    @Test
    public void testEnableLazyLoadRTBBannerNoBidLoadLazyAd() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.NO_BID));
        bannerAdView.enableLazyLoad();
        executeBannerRequest();
        assertCallbacks(false);
        assertFalse(bannerAdView.loadLazyAd());
        assertFalse(bannerAdView.getChildAt(0) instanceof WebView);
    }

    @Test
    public void testEnableLazyLoadRTBBannerNoBidLoadLazyAdLoadAdAgain() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.NO_BID));
        bannerAdView.enableLazyLoad();
        executeBannerRequest();
        assertCallbacks(false);
        assertFalse(bannerAdView.loadLazyAd());
        assertFalse(bannerAdView.getChildAt(0) instanceof WebView);
        adLoaded = false;
        adLazyLoaded = false;
        adFailed = false;
        restartServer();
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner()));
        executeBannerRequest();
        assertFalse(bannerAdView.getChildAt(0) instanceof WebView);
        assertLazyLoadCallbackInProgress();
        bannerAdView.loadLazyAd();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertLazyLoadCallbackSuccess();
        assertTrue(bannerAdView.getChildAt(0) instanceof WebView);
    }

    @Test
    public void testLazyBannerAdLoadWithloadLazyAdAlreadyCalled() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner()));
        bannerAdView.enableLazyLoad();
        assertFalse(bannerAdView.loadLazyAd());
        executeBannerRequest();
        assertLazyLoadCallbackInProgress();
        assertTrue(bannerAdView.loadLazyAd());
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertLazyLoadCallbackSuccess();
    }

    @Test
    public void testLazyBannerNativeAdLoaded() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.anNativeWithoutImages()));
        bannerAdView.enableLazyLoad();
        executeBannerRequest();
        assertCallbacks(true);
        assertNotNull(nativeAdResponse);
    }

    @Test
    public void testLazyBannerAdFailed() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.invalidBanner()));
        bannerAdView.enableLazyLoad();
        executeBannerRequest();
        assertLazyLoadCallbackInProgress();
        bannerAdView.loadLazyAd();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertLazyLoadCallbackFailure();

    }

    // This proves that the second loadAd() behaves as a Lazy load after the Lazy Ad has failed in the first attempt (after calling loadLazyAd())
    @Test
    public void testLazyBannerAdLoadedFailureAndLoadAgainSuccess() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.invalidBanner()));
        bannerAdView.enableLazyLoad();
        assertFalse(bannerAdView.getChildAt(0) instanceof WebView);
        executeBannerRequest();
        assertLazyLoadCallbackInProgress();
        bannerAdView.loadLazyAd();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertLazyLoadCallbackFailure();
        adLoaded = false;
        adFailed = false;
        adLazyLoaded = false;
        restartServer();
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner()));
        executeBannerRequest();
        assertLazyLoadCallbackInProgress();
        assertFalse(bannerAdView.getChildAt(0) instanceof WebView);
        bannerAdView.loadLazyAd();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertLazyLoadCallbackSuccess();
    }

    @Test
    public void testloadLazyAdAfterAdLoad() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner()));
        assertTrue(bannerAdView.enableLazyLoad());
        executeBannerRequest();
        assertLazyLoadCallbackInProgress();
        assertTrue(bannerAdView.loadLazyAd());
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertLazyLoadCallbackSuccess();
        adLoaded = false;
        adFailed = false;
        assertFalse(bannerAdView.loadLazyAd());
        assertFalse(adLoaded);
        assertFalse(adFailed);
    }

//    @Test
//    public void testloadLazyAdAfterAdLoadAutoRefreshSuccessAndTwooFailures() {
//        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner()));
//        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner()));
//        assertTrue(bannerAdView.enableLazyLoad());
//        bannerAdView.setAutoRefreshInterval(15000);
//        executeBannerRequest();
//        System.out.println("REQUEST COUNT: " + server.getRequestCount());
//        assertLazyLoadCallbackInProgress();
//        assertTrue(bannerAdView.loadLazyAd());
//        Robolectric.flushBackgroundThreadScheduler();
//        Robolectric.flushForegroundThreadScheduler();
//        assertLazyLoadCallbackSuccess();
//        adLoaded = false;
//        adFailed = false;
//        assertFalse(bannerAdView.loadLazyAd());
//        assertFalse(adLoaded);
//        assertFalse(adFailed);
//
//        // First AutoRefresh Failure case
//        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.noResponse()));
//        adLoaded = false;
//        adFailed = false;
//        adLazyLoaded = false;
//        Lock.pause(15000);
//        waitForTasks();
//        Robolectric.flushBackgroundThreadScheduler();
//        Robolectric.flushForegroundThreadScheduler();
//
//        waitForTasks();
//        Robolectric.flushBackgroundThreadScheduler();
//        Robolectric.flushForegroundThreadScheduler();
//        System.out.println("REQUEST COUNT: " + server.getRequestCount());
//        assertFalse(adLoaded);
//        assertTrue(adFailed);
//
//        // Second AutoRefresh Failure Case
//        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.noResponse()));
//        adLoaded = false;
//        adFailed = false;
//        adLazyLoaded = false;
//        Lock.pause(15000);
//        waitForTasks();
//        Robolectric.flushBackgroundThreadScheduler();
//        Robolectric.flushForegroundThreadScheduler();
//
//        waitForTasks();
//        Robolectric.flushBackgroundThreadScheduler();
//        Robolectric.flushForegroundThreadScheduler();
//        System.out.println("REQUEST COUNT: " + server.getRequestCount());
//        assertFalse(adLoaded);
//        assertTrue(adFailed);
//
//        // Third AutoRefresh: Success Case
//        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner()));
//        adLoaded = false;
//        adFailed = false;
//        adLazyLoaded = false;
//        Lock.pause(15000);
//        waitForTasks();
//        Robolectric.flushBackgroundThreadScheduler();
//        Robolectric.flushForegroundThreadScheduler();
//
//        waitForTasks();
//        Robolectric.flushBackgroundThreadScheduler();
//        Robolectric.flushForegroundThreadScheduler();
//        System.out.println("REQUEST COUNT: " + server.getRequestCount());
//        assertLazyLoadCallbackInProgress();
//    }

    @Test
    public void testloadLazyAdForDisabledLazyLoad() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner()));
        assertFalse(bannerAdView.isLazyLoadEnabled());
        executeBannerRequest();
        assertCallbacks(true);
        adLoaded = false;
        adFailed = false;
        assertFalse(bannerAdView.loadLazyAd());
        assertFalse(adLoaded);
        assertFalse(adFailed);
    }



    @Test
    public void testBannerAdFailed() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));
        requestManager = new AdViewRequestManager(bannerAdView);
        requestManager.execute();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertCallbacks(false);
    }

    // Interstitial Testing

    @Test
    public void testInterstitialAdLoaded() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner()));
        requestManager = new AdViewRequestManager(interstitialAdView);
        requestManager.execute();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertCallbacks(true);
    }

    @Test
    public void testInterstitialAdFailed() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));
        requestManager = new AdViewRequestManager(interstitialAdView);
        requestManager.execute();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertCallbacks(false);
    }

    //Banner-Native test
    @Test
    public void testBannerNativeAdLoaded() {
        bannerAdView.setAutoRefreshInterval(30000);
        bannerAdView.setLoadsInBackground(false);
        bannerAdView.setOpensNativeBrowser(false);
        bannerAdView.setClickThroughAction(ANClickThroughAction.RETURN_URL);
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.anNativeWithoutImages()));
        Assert.assertEquals(AdType.UNKNOWN, bannerAdView.getAdType());
        requestManager = new AdViewRequestManager(bannerAdView);
        requestManager.execute();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        Assert.assertEquals(30000, bannerAdView.getAutoRefreshInterval());
        Assert.assertEquals(AdType.NATIVE, bannerAdView.getAdType());
        assertCallbacks(true);
        assertOpensInNativeBrowser();
        assertLoadsInBackground();
        assertClickThroughAction();
        assertClickThroughAction(ANClickThroughAction.RETURN_URL);
    }

    @Test
    public void testClickThroughDependencyOnOpensNativeFalse() {
        bannerAdView.setClickThroughAction(ANClickThroughAction.RETURN_URL);
        bannerAdView.setOpensNativeBrowser(false);
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.anNativeWithoutImages()));
        requestManager = new AdViewRequestManager(bannerAdView);
        requestManager.execute();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertClickThroughAction();
        assertClickThroughAction(ANClickThroughAction.OPEN_SDK_BROWSER);
    }

    @Test
    public void testClickThroughDependencyOnOpensNativeTrue() {
        bannerAdView.setClickThroughAction(ANClickThroughAction.RETURN_URL);
        bannerAdView.setOpensNativeBrowser(true);
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.anNativeWithoutImages()));
        requestManager = new AdViewRequestManager(bannerAdView);
        requestManager.execute();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertClickThroughAction();
        assertClickThroughAction(ANClickThroughAction.OPEN_DEVICE_BROWSER);
    }

    @Test
    public void testBannerNativeAdFailed() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));
        requestManager = new AdViewRequestManager(bannerAdView);
        requestManager.execute();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertCallbacks(false);
    }

    private void executeBannerRequest() {
        bannerAdView.setAutoRefreshInterval(0);
        bannerAdView.loadAdOffscreen();

        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

//        Robolectric.getBackgroundThreadScheduler().advanceToNextPostedRunnable();
//        Robolectric.getForegroundThreadScheduler().advanceToNextPostedRunnable();
    }
}
