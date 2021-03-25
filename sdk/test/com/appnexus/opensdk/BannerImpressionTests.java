/*
 *    Copyright 2017 APPNEXUS INC
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

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.appnexus.opensdk.shadows.ShadowAsyncTaskNoExecutor;
import com.appnexus.opensdk.shadows.ShadowCustomWebView;
import com.appnexus.opensdk.shadows.ShadowSettings;
import com.appnexus.opensdk.shadows.ShadowWebSettings;
import com.appnexus.opensdk.util.Lock;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadow.api.Shadow;
import org.robolectric.shadows.ShadowConnectivityManager;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowNetworkInfo;

import static junit.framework.Assert.assertTrue;


/**
 *This class covers the cases for BannerAdView impression tracker firing.
 *
 * Currently its possible to write these cases only with loadAdOffScreen case's
 * loadAd is not possible because of limitations in roboelectric need to figure out a workaround in future.
 *
 */

@Config(sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class,
                ShadowCustomWebView.class, ShadowWebSettings.class, ShadowSettings.class, ShadowLog.class, ShadowConnectivityManager.class})
@RunWith(RobolectricTestRunner.class)
public class BannerImpressionTests extends BaseViewAdTest {

    private ConnectivityManager connectivityManager;
    private ShadowConnectivityManager shadowConnectivityManager;

    @Override
    public void setup() {
        super.setup();
        connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        // Not using Shadows.shadowOf(connectivityManager) because of Robolectric bug when using API23+
        // See: https://github.com/robolectric/robolectric/issues/1862
        shadowConnectivityManager = (ShadowConnectivityManager) Shadow.extract(connectivityManager);
    }

    @Override
    public void tearDown() {
        super.tearDown();
        SDKSettings.setCountImpressionOn1pxRendering(false);
    }

    // 1. Loads the banner off screen
    // 2. Attaches banner to a screen
    // 3. Checks if impression_url is fired succesfully
    @Test
    public void test_1BannerImpressionFiring() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner())); // This is for UT Request
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank())); // This is for Impression

        runBasicBannerTest();

        attachBannerToView();

        assertImpressionURL(2);
    }


    //1. Loads banner off screen.
    //2. Attaches banner to a screen.
    //2. Goes offline forcing the impression url to be added to shared network manager.
    //3. Goes back online
    //4. Confirms impression URL is fired once back online.
    @Test
    public void test_2BannerImpressionFiringNetworkRetry() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner())); // This is for UT Request
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank())); // This is for Impression

        runBasicBannerTest();

        goOffline();

        attachBannerToView();

        confirmNoImpressionULRinQueue();

        goOnline();


        // We need to wait for 10 Seconds here for the NetWork Retry timer to get fired and to test.
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertImpressionURL(2);
    }

    // 1. Loads the banner off screen with setCountImpressionOnAdLoad(true)
    // 2. Checks if impression_url is fired succesfully
    @Test
    public void test_3BannerImpressionFiringonAdLoad() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner())); // This is for UT Request
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank())); // This is for Impression
        bannerAdView.setCountImpressionOnAdLoad(true);

        //runBasicBannerTest();

        executeBannerRequest();

       // attachBannerToView();

        assertImpressionURL(2);
    }

    // ============= ONE PX test cases ==============

    // 1. Loads the banner off screen
    // 2. Attaches banner to a screen
    // 3. Checks if impression_url is fired succesfully
    @Test
    public void test_1BannerImpressionFiringOnePx() {
        SDKSettings.setCountImpressionOn1pxRendering(true);

        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner())); // This is for UT Request
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank())); // This is for Impression

        runBasicBannerTest();

        attachBannerToView();

        confirmNoImpressionULRinQueue();
    }

    // 1. Loads the banner off screen
    // 2. Attaches banner to a screen
    // 3. Checks if impression_url is fired succesfully
    @Test
    public void test_1BannerImpressionFiringPreferCountImpressionOnAdLoadOverOnePx() {
        bannerAdView.setCountImpressionOnAdLoad(true);
        SDKSettings.setCountImpressionOn1pxRendering(true);

        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner())); // This is for UT Request
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank())); // This is for Impression

        runBasicBannerTest();

        attachBannerToView();

        assertImpressionURL(2);
    }

    //////////////// END TESTS ////////////////////////



    //Helper Methods

    private void assertImpressionURL(int positionInQueue) {

        // Wait for Impression URL to Fire succesfully
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        RecordedRequest request = null;
        try {
            for (int i = 1; i <= positionInQueue; i++) {
                request = server.takeRequest();
                if (i == positionInQueue) {
                    String impression_url = request.getRequestLine();
                    System.out.print("impression_url::" + impression_url + "\n");
                    assertTrue(impression_url.startsWith("GET /impression_url? HTTP/1.1"));
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void confirmNoImpressionULRinQueue() {
        // Wait for Any tasks and flush them.
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
       // waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertTrue(server.getRequestCount() == 1);
    }


    private void executeBannerRequest() {


        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();


        requestManager = new AdViewRequestManager(bannerAdView);
        requestManager.execute();

        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();

        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

       // waitForTasks();
       // Robolectric.flushBackgroundThreadScheduler();
       // Robolectric.flushForegroundThreadScheduler();

//        Robolectric.getBackgroundThreadScheduler().advanceToNextPostedRunnable();
//        Robolectric.getForegroundThreadScheduler().advanceToNextPostedRunnable();
    }


    // common format for several of the basic banner tests execute UT request and attach the banner to a View
    public void runBasicBannerTest() {
        executeBannerRequest();
        assertCallbacks(true);
    }

    private void attachBannerToView() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(320, 50);
        bannerAdView.setLayoutParams(layoutParams);

        LinearLayout container = new LinearLayout(activity);
        container.addView(bannerAdView);

        final ViewGroup viewGroup = ((ViewGroup) activity.getWindow().getDecorView().getRootView());
        viewGroup.addView(container);
        bannerAdView.setVisibility(View.VISIBLE);
    }


    private void goOffline() {
        NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();
        assertTrue(activeInfo != null && activeInfo.isConnected());

        shadowConnectivityManager.setActiveNetworkInfo(
                ShadowNetworkInfo.newInstance(NetworkInfo.DetailedState.DISCONNECTED, ConnectivityManager.TYPE_MOBILE, 0, true, false)
        );
        NetworkInfo activeInfo2 = connectivityManager.getActiveNetworkInfo();
        assertTrue(activeInfo2 != null && !activeInfo2.isConnected());
    }


    private void goOnline() {
        NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();
        assertTrue(activeInfo != null && !activeInfo.isConnected());

        shadowConnectivityManager.setActiveNetworkInfo(
                ShadowNetworkInfo.newInstance(NetworkInfo.DetailedState.DISCONNECTED, ConnectivityManager.TYPE_MOBILE, 0, true, true)
        );
        NetworkInfo activeInfo2 = connectivityManager.getActiveNetworkInfo();
        assertTrue(activeInfo2 != null && activeInfo2.isConnected());
    }


    @Override
    public void onAdLoaded(AdView adView) {
        super.onAdLoaded(adView);
        Lock.unpause();
    }

    @Override
    public void onAdRequestFailed(AdView adView, ResultCode resultCode) {
        super.onAdRequestFailed(adView, resultCode);
        Lock.unpause();
    }

}
