/*
 *    Copyright 2018 APPNEXUS INC
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

import com.appnexus.opensdk.mar.MultiAdRequestListener;
import com.appnexus.opensdk.mocks.MockDefaultExecutorSupplier;
import com.appnexus.opensdk.shadows.ShadowAsyncTaskNoExecutor;
import com.appnexus.opensdk.shadows.ShadowCustomWebView;
import com.appnexus.opensdk.shadows.ShadowSettings;
import com.appnexus.opensdk.shadows.ShadowWebSettings;
import com.appnexus.opensdk.util.MockServerResponses;
import com.squareup.okhttp.mockwebserver.MockResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static android.os.Looper.getMainLooper;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.robolectric.Shadows.shadowOf;

@Config(sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class,
                ShadowCustomWebView.class, ShadowWebSettings.class, ShadowSettings.class})
@RunWith(RobolectricTestRunner.class)
public class ANMultiAdRequestLoadTests extends BaseViewAdTest {

    ANMultiAdRequest anMultiAdRequest;
    private boolean secondMarCompleted, secondMarFailed;

    @Override
    public void setup() {
        super.setup();
        anMultiAdRequest = new ANMultiAdRequest(activity, 0, 1234, this);
        anMultiAdRequest.addAdUnit(bannerAdView);
        anMultiAdRequest.addAdUnit(interstitialAdView);
    }

    @Override
    public void tearDown() {
        anMultiAdRequest = null;
        shadowOf(getMainLooper()).quitUnchecked();
        super.tearDown();
    }

    //This verifies that the AsyncTask for Request is being executed on the Correct Executor.
    @Test
    public void testRequestExecutorForBackgroundTasks() {
        SDKSettings.setExternalExecutor(MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
        assertNotSame(ShadowAsyncTaskNoExecutor.getExecutor(), MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
        anMultiAdRequest.load();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertEquals(ShadowAsyncTaskNoExecutor.getExecutor(), MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
    }

    //MAR Success
    @Test
    public void testMARSuccessWithSingleInitializationMethod() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(MockServerResponses.marSuccess()));
        assertFalse(marCompleted);
        executeMARRequest();
        assertTrue(marCompleted);
    }

    @Test
    public void testMARSuccessWithConvenience() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(MockServerResponses.marSuccess()));
        assertFalse(marCompleted);
        initMARWithConvenience();
        assertTrue(marCompleted);
    }

    @Test
    public void testMARWithSingleInitializationMethodAndStop() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(MockServerResponses.marSuccess()));
        assertFalse(marCompleted);
        reset();

        anMultiAdRequest.load();

        anMultiAdRequest.stop();

        assertTrue(marFailed);
    }

    @Test
    public void testMARWithSingleInitializationMethodAndStopRemoveAddAndLoad() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(MockServerResponses.marSuccess()));
        assertFalse(marCompleted);
        reset();

        anMultiAdRequest.load();

        anMultiAdRequest.stop();

        assertTrue(marFailed);

        anMultiAdRequest.removeAdUnit(interstitialAdView);

        InterstitialAdView interstitialAdView2 = new InterstitialAdView(activity);
        interstitialAdView2.setPlacementID("0");
        interstitialAdView2.setAdListener(this);

        anMultiAdRequest.addAdUnit(interstitialAdView2);

        anMultiAdRequest.load();

        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        assertTrue(marCompleted);
    }

    @Test
    public void testMARWithConvenienceAndStop() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(MockServerResponses.marSuccess()));
        assertFalse(marCompleted);
        reset();

        BannerAdView bannerAdView = new BannerAdView(activity);
        bannerAdView.setPlacementID("0");
        bannerAdView.setAdListener(this);
        bannerAdView.setAdSize(320, 50);
        bannerAdView.setAutoRefreshInterval(-1);

        InterstitialAdView interstitialAdView = new InterstitialAdView(activity);
        interstitialAdView.setPlacementID("0");
        interstitialAdView.setAdListener(this);

        ANMultiAdRequest anMultiAdRequestLocal = new ANMultiAdRequest(activity, 0, 1234, this, true, bannerAdView, interstitialAdView);

        anMultiAdRequestLocal.stop();

        assertTrue(marFailed);

    }

    @Test
    public void testMARWithConvenienceAndStopRemoveAddAndLoad() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(MockServerResponses.marSuccess()));
        assertFalse(marCompleted);
        reset();

        BannerAdView bannerAdView = new BannerAdView(activity);
        bannerAdView.setPlacementID("0");
        bannerAdView.setAdListener(this);
        bannerAdView.setAdSize(320, 50);
        bannerAdView.setAutoRefreshInterval(-1);

        InterstitialAdView interstitialAdView = new InterstitialAdView(activity);
        interstitialAdView.setPlacementID("0");
        interstitialAdView.setAdListener(this);

        ANMultiAdRequest anMultiAdRequestLocal = new ANMultiAdRequest(activity, 0, 1234, this, true, bannerAdView, interstitialAdView);

        anMultiAdRequestLocal.stop();

        assertTrue(marFailed);

        anMultiAdRequest.removeAdUnit(interstitialAdView);

        InterstitialAdView interstitialAdView2 = new InterstitialAdView(activity);
        interstitialAdView2.setPlacementID("0");
        interstitialAdView2.setAdListener(this);

        anMultiAdRequest.addAdUnit(interstitialAdView2);

        anMultiAdRequest.load();

        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        assertTrue(marCompleted);

    }

    //MAR Success With NoBid Response for AdUnits
    @Test
    public void testMARSuccessAdUnitNoBid() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(MockServerResponses.marSuccessAdUnitNoBid()));
        assertFalse(marCompleted);
        executeMARRequest();
        assertTrue(marCompleted);
    }

    //MAR Failure with empty response
    @Test
    public void testMARFailure() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(""));
        assertFalse(marFailed);
        executeMARRequest();
        assertTrue(marFailed);
    }

    //MAR Success with the AdListener success calls for the AdUnits
    @Test
    public void testMARSuccessWithAdListenerLoad() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(MockServerResponses.marSuccess()));
        assertFalse(marCompleted);
        executeMARRequest();
        assertTrue(marCompleted);
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertCallbacks(true);
        assertBannerAdResponse(true);
        assertInterstitialAdResponse(true);
    }

    //MAR Success with the AdListener failure calls for the AdUnits
    @Test
    public void testMARSuccessAdUnitNoBidWithAdListenerFailure() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(MockServerResponses.marSuccessAdUnitNoBid()));
        assertFalse(marFailed);
        assertFalse(marCompleted);
        executeMARRequest();
        assertTrue(marCompleted);
        assertFalse(marFailed);
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertCallbacks(false);
        assertBannerAdResponse(false);
        assertInterstitialAdResponse(false);
    }

    //MAR Success with the AdListener success calls for the AdUnits
    @Test
    public void testMARSuccessWithAdListenerLazyLoad() {
        bannerAdView.enableLazyLoad();
        server.enqueue(new MockResponse().setResponseCode(200).setBody(MockServerResponses.marSuccess()));
        assertFalse(marCompleted);
        executeMARRequest();
        assertTrue(marCompleted);
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertTrue(adLazyLoaded);
        assertInterstitialAdResponse(true);
        assertBannerAdResponse(false);
        bannerAdView.loadLazyAd();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertBannerAdResponse(true);
    }

    //MAR Success with the AdListener success calls for the AdUnits
    @Test
    public void testMARSuccessWithAdListenerLazyLoadBeforeAndAfterInititializingMARRequest() {
        bannerAdView.enableLazyLoad();
        server.enqueue(new MockResponse().setResponseCode(200).setBody(MockServerResponses.marSuccess()));
        assertFalse(marCompleted);
        assertFalse(bannerAdView.loadLazyAd());
        executeMARRequest();
        assertTrue(marCompleted);
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertTrue(adLazyLoaded);
        assertInterstitialAdResponse(true);
        assertBannerAdResponse(false);
        assertTrue(bannerAdView.loadLazyAd());
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertBannerAdResponse(true);
    }

    //MAR Success with Success Reload
    @Test
    public void testMARSuccessAndReloadSuccess() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(MockServerResponses.marSuccess()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(MockServerResponses.marSuccess()));
        assertFalse(marFailed);
        assertFalse(marCompleted);
        executeMARRequest();
        assertTrue(marCompleted);
        assertFalse(marFailed);
        executeMARRequest();
        assertTrue(marCompleted);
        assertFalse(marFailed);
    }

    //MAR Success with Success Reload
    @Test
    public void testMARSuccessAndReloadSuccessWithBannerPlacementChanges() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(MockServerResponses.marSuccess()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(MockServerResponses.marSuccess()));
        assertFalse(marFailed);
        assertFalse(marCompleted);
        executeMARRequest();
        assertTrue(marCompleted);
        assertFalse(marFailed);
        bannerAdView.setPlacementID("123456");
        executeMARRequest();
        assertTrue(marCompleted);
        assertFalse(marFailed);
    }

    //MAR Success with Failure Reload
    @Test
    public void testMARSuccessAndReloadFailure() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(MockServerResponses.marSuccess()));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(""));
        assertFalse(marFailed);
        assertFalse(marCompleted);
        executeMARRequest();
        assertTrue(marCompleted);
        assertFalse(marFailed);
        //Reload
        executeMARRequest();
        assertTrue(marFailed);
        assertFalse(marCompleted);
    }

    //MAR Failure with Success Reload
    @Test
    public void testMARFailureAndReloadSuccess() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(""));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(MockServerResponses.marSuccess()));
        assertFalse(marFailed);
        assertFalse(marCompleted);
        executeMARRequest();
        assertTrue(marFailed);
        assertFalse(marCompleted);
        //Reload
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        executeMARRequest();
        assertTrue(marCompleted);
        assertFalse(marFailed);
    }

    //MAR Success with No bid response for AdUnits with AdUnits Success Reload
    @Test
    public void testMARSuccessAdUnitNoBidWithAdListenerFailureReloadSuccess() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(MockServerResponses.marSuccessAdUnitNoBid()));
        assertFalse(marCompleted);
        executeMARRequest();
        assertTrue(marCompleted);
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertCallbacks(false);
        assertBannerAdResponse(false);
        assertInterstitialAdResponse(false);
        //Reload
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        server.enqueue(new MockResponse().setResponseCode(200).setBody(MockServerResponses.marSuccess()));
        executeMARRequest();
        assertTrue(marCompleted);
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertCallbacks(true);
        assertBannerAdResponse(true);
        assertInterstitialAdResponse(true);
    }

    //MAR Success with an attached BannerAdView request
    @Test
    public void testMARSuccessAdUnitNoBidWithBannerRequest() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(MockServerResponses.marSuccessAdUnitNoBid()));
        assertFalse(marCompleted);
        executeMARRequest();
        assertTrue(marCompleted);
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertCallbacks(false);
        assertBannerAdResponse(false);
        assertInterstitialAdResponse(false);
        //Load Banner
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner()));
        executeBannerRequest();
        assertFalse(marCompleted);
        assertFalse(marFailed);
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertCallbacks(true);
        assertBannerAdResponse(true);
        assertInterstitialAdResponse(false);
    }

    //MAR Success with an attached Interstitial request
    @Test
    public void testMARSuccessAdUnitNoBidWithInterstitialRequest() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(MockServerResponses.marSuccessAdUnitNoBid()));
        assertFalse(marCompleted);
        executeMARRequest();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertTrue(marCompleted);
        assertCallbacks(false);
        assertBannerAdResponse(false);
        assertInterstitialAdResponse(false);
        //Load Banner
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner()));
        executeInterstitialRequest();
        assertFalse(marCompleted);
        assertFalse(marFailed);
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertCallbacks(true);
        assertInterstitialAdResponse(true);
        assertBannerAdResponse(false);
    }

    //Concurrent MAR Success
    @Test
    public void testConcurrentMARSuccess() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(MockServerResponses.marSuccess()));
        assertFalse(marCompleted);
        executeMARRequest();
        server.enqueue(new MockResponse().setResponseCode(200).setBody(MockServerResponses.marSuccess()));
        executeSecondMARRequest();
        assertTrue(marCompleted);
        assertTrue(secondMarCompleted);
    }

    @Test
    public void testConcurrentMARSuccessWithConvenience() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(MockServerResponses.marSuccess()));
        assertFalse(marCompleted);
        assertFalse(secondMarCompleted);
        initMARWithConvenience();
        server.enqueue(new MockResponse().setResponseCode(200).setBody(MockServerResponses.marSuccess()));
        executeSecondMARRequest();
        assertTrue(marCompleted);
        assertTrue(secondMarCompleted);
    }

    private void initMARWithConvenience() {
        BannerAdView bannerAdView = new BannerAdView(activity);
        bannerAdView.setPlacementID("0");
        bannerAdView.setAdListener(this);
        bannerAdView.setAdSize(320, 50);
        bannerAdView.setAutoRefreshInterval(-1);

        InterstitialAdView interstitialAdView = new InterstitialAdView(activity);
        interstitialAdView.setPlacementID("0");
        interstitialAdView.setAdListener(this);

        new ANMultiAdRequest(activity, 0, 1234, this, true, bannerAdView, interstitialAdView);

        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

//        ShadowLooper shadowLooper = shadowOf(getMainLooper());
//        if (!shadowLooper.isIdle()) {
//            shadowLooper.idle();
//        }
//        RuntimeEnvironment.getMasterScheduler().advanceToNextPostedRunnable();
    }

    //Concurrent MAR Success
    @Test
    public void testReloadMARSuccess() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(MockServerResponses.marSuccess()));
        assertFalse(marCompleted);
        executeMARRequest();
        server.enqueue(new MockResponse().setResponseCode(200).setBody(MockServerResponses.marSuccess()));
        executeSecondMARRequest();
        assertTrue(marCompleted);
        assertTrue(secondMarCompleted);
    }

    private void executeSecondMARRequest() {
        ANMultiAdRequest anMultiAdRequest = new ANMultiAdRequest(activity, 123, 1234, new MultiAdRequestListener() {
            @Override
            public void onMultiAdRequestCompleted() {
                secondMarCompleted = true;
                secondMarFailed = false;
            }

            @Override
            public void onMultiAdRequestFailed(ResultCode code) {
                secondMarCompleted = false;
                secondMarFailed = true;
            }
        });

        BannerAdView bannerAdView = new BannerAdView(activity);
        bannerAdView.setPlacementID("0");
        bannerAdView.setAdListener(this);
        bannerAdView.setAdSize(320, 50);
        bannerAdView.setAutoRefreshInterval(-1);

        InterstitialAdView interstitialAdView = new InterstitialAdView(activity);
        interstitialAdView.setPlacementID("0");
        interstitialAdView.setAdListener(this);
        anMultiAdRequest.addAdUnit(bannerAdView);
        anMultiAdRequest.addAdUnit(interstitialAdView);

        executeMARRequest(anMultiAdRequest);
    }

    private void executeBannerRequest() {
        reset();

        bannerAdView.setAutoRefreshInterval(15000);
        bannerAdView.loadAd();

        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

//        Robolectric.getBackgroundThreadScheduler().advanceToNextPostedRunnable();
//        Robolectric.getForegroundThreadScheduler().advanceToNextPostedRunnable();
    }

    private void executeInterstitialRequest() {
        reset();

        interstitialAdView.loadAd();

        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
    }

    private void executeMARRequest() {
        reset();
        executeMARRequest(anMultiAdRequest);
    }

    private void executeMARRequest(ANMultiAdRequest anMultiAdRequest) {
//        AdViewRequestManager requestManager = new AdViewRequestManager(anMultiAdRequest);
//        requestManager.execute();
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        anMultiAdRequest.load();

        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();


//        ShadowLooper.runUiThreadTasks();
//        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
//        ShadowLooper.idleMainLooper();
//        ShadowLooper.idleMainLooperConstantly(true);
//        ShadowLooper.shadowMainLooper().quitUnchecked();
//        ShadowLooper shadowLooper = shadowOf(getMainLooper());
//        if (!shadowLooper.isIdle()) {
//            shadowLooper.idle();
//        }
//        RuntimeEnvironment.getMasterScheduler().advanceToNextPostedRunnable();
    }

    private void reset() {
        adLoaded = false;
        adFailed = false;
        adExpanded = false;
        adCollapsed = false;
        adClicked = false;
        adClickedWithUrl = false;

        isBannerLoaded = false;
        isInterstitialLoaded = false;

        marCompleted = false;
        marFailed = false;
        secondMarCompleted = false;
        secondMarFailed = false;
    }
}
