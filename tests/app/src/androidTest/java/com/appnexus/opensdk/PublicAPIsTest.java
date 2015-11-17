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

import android.content.Intent;
import android.webkit.WebView;

import com.appnexus.opensdk.AdActivity;
import com.appnexus.opensdk.AdViewRequestManager;
import com.appnexus.opensdk.BannerAdView;
import com.appnexus.opensdk.InterstitialAdView;
import com.appnexus.opensdk.NativeAdRequest;
import com.appnexus.opensdk.NativeAdRequestListener;
import com.appnexus.opensdk.NativeAdRequestManager;
import com.appnexus.opensdk.NativeAdResponse;
import com.appnexus.opensdk.OpenSDKUnitTestsActivity;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.shadows.ShadowAsyncTaskNoExecutor;
import com.appnexus.opensdk.shadows.ShadowWebSettings;
import com.appnexus.opensdk.shadows.ShadowWebView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import dalvik.annotation.TestTargetClass;

import org.apache.http.client.methods.HttpUriRequest;

import java.lang.Override;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

@Config(shadows = {ShadowAsyncTaskNoExecutor.class,
        ShadowWebView.class, ShadowWebSettings.class},
        emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class PublicAPIsTest extends BaseRoboTest {

    @Override
    public void setup() {
        super.setup();
        Robolectric.getFakeHttpLayer().interceptHttpRequests(true);
    }

    /**
     * Test that when both member ID, inventory code & placement ID are passed in,
     * ad call should happen and only member ID, inventory code should be used
     */
    @Test
    public void testSetMemberIdAndInventoryCodeAndPlacementIDOnBanner() {
        BannerAdView adView = new BannerAdView(activity);
        adView.setAdSize(320, 50);
        adView.setPlacementID("1");
        adView.setInventoryCodeAndMemberID(2, "test");
        Robolectric.addPendingHttpResponse(200, TestResponses.banner());
        // Since banner is not attached to screen and created programmatically,
        // loadAd() would return false in this case,
        // calling request manager execute() here to make an ad request
        AdViewRequestManager requestManager = new AdViewRequestManager(adView);
        requestManager.execute();
        assertTrue("Request param not ready", requestManager.getRequestParams().isReadyForRequest());
        // Run async task to request an ad
        waitForTasks();
        while (Robolectric.getBackgroundScheduler().areAnyRunnable()) {
            Robolectric.getBackgroundScheduler().runOneTask();
        }
        Robolectric.runUiThreadTasks();
        // Get the sent request
        HttpUriRequest sentRequest = (HttpUriRequest) Robolectric.getLatestSentHttpRequest();
        String resultUri = sentRequest.getURI().toString();

        assertTrue(sentRequest.getMethod().equals("GET"));
        assertTrue("Actual request: " + resultUri, resultUri.contains("?member=2&inv_code=test"));
        assertTrue("Actual request: " + resultUri, !resultUri.contains("?id=1"));
    }

    /**
     * Test that only member ID and inventory code passed in, ad call will be made
     */
    @Test
    public void testSetOnlyMemberIdAndInventoryCodeOnBanner() {
        BannerAdView adView = new BannerAdView(activity);
        adView.setAdSize(320, 50);
        adView.setInventoryCodeAndMemberID(2, "test");
        Robolectric.addPendingHttpResponse(200, TestResponses.banner());
        // Since banner is not attached to screen and created programmatically,
        // loadAd() would return false in this case,
        // calling request manager execute() here to make an ad request
        AdViewRequestManager requestManager = new AdViewRequestManager(adView);
        requestManager.execute();
        assertTrue("Request param not ready", requestManager.getRequestParams().isReadyForRequest());
        // Run async task to request an ad
        waitForTasks();
        while (Robolectric.getBackgroundScheduler().areAnyRunnable()) {
            Robolectric.getBackgroundScheduler().runOneTask();
        }
        Robolectric.runUiThreadTasks();
        // Get the sent request
        HttpUriRequest sentRequest = (HttpUriRequest) Robolectric.getLatestSentHttpRequest();
        String resultUri = sentRequest.getURI().toString();

        assertTrue(sentRequest.getMethod().equals("GET"));
        assertTrue("Actual request: " + resultUri, resultUri.contains("?member=2&inv_code=test"));
        assertTrue("Actual request: " + resultUri, !resultUri.contains("?id="));
    }

    /**
     * Test that only placement id passed in, ad call will be made
     */
    @Test
    public void testSetOnlyPlacementIdOnBanner() {
        BannerAdView adView = new BannerAdView(activity);
        adView.setAdSize(320, 50);
        adView.setPlacementID("1");
        Robolectric.addPendingHttpResponse(200, TestResponses.banner());
        // Since banner is not attached to screen and created programmatically,
        // loadAd() would return false in this case,
        // calling request manager execute() here to make an ad request
        AdViewRequestManager requestManager = new AdViewRequestManager(adView);
        requestManager.execute();
        assertTrue("Request param not ready", requestManager.getRequestParams().isReadyForRequest());
        // Run async task to request an ad
        waitForTasks();
        while (Robolectric.getBackgroundScheduler().areAnyRunnable()) {
            Robolectric.getBackgroundScheduler().runOneTask();
        }
        Robolectric.runUiThreadTasks();
        // Get the sent request
        HttpUriRequest sentRequest = (HttpUriRequest) Robolectric.getLatestSentHttpRequest();
        String resultUri = sentRequest.getURI().toString();

        assertTrue(sentRequest.getMethod().equals("GET"));
        assertTrue("Actual request: " + resultUri, !resultUri.contains("member="));
        assertTrue("Actual request: " + resultUri, !resultUri.contains("inv_code="));
        assertTrue("Actual request: " + resultUri, resultUri.contains("id=1"));
    }

    /**
     * Test that no member id and inventory code or placement id passed in, ad call will be not be made
     */
    @Test
    public void testSetNoIdentifierOnBanner() {
        BannerAdView adView = new BannerAdView(activity);
        adView.setAdSize(320, 50);
        Robolectric.addPendingHttpResponse(200, TestResponses.banner());
        // Since banner is not attached to screen and created programmatically,
        // loadAd() would return false in this case,
        // calling request manager execute() here to make an ad request
        AdViewRequestManager requestManager = new AdViewRequestManager(adView);
        assertTrue("Request param should not be ready.", !requestManager.getRequestParams().isReadyForRequest());
    }

    /**
     * Test that when both member ID, inventory code & placement ID are passed in,
     * ad call should happen and only member ID, inventory code should be used
     */
    @Test
    public void testSetMemberIdAndInventoryCodeAndPlacementIDOnInterstitial() {
        InterstitialAdView iad = new InterstitialAdView(activity);
        iad.setPlacementID("1");
        iad.setInventoryCodeAndMemberID(2, "test");
        Robolectric.addPendingHttpResponse(200, TestResponses.banner());
        // Since window is not visible,
        // loadAd() would return false in this case,
        // calling request manager execute() here to make an ad request
        AdViewRequestManager requestManager = new AdViewRequestManager(iad);
        requestManager.execute();
        assertTrue("Request param not ready", requestManager.getRequestParams().isReadyForRequest());
        // Run async task to request an ad
        waitForTasks();
        while (Robolectric.getBackgroundScheduler().areAnyRunnable()) {
            Robolectric.getBackgroundScheduler().runOneTask();
        }
        Robolectric.runUiThreadTasks();
        // Get the sent request
        HttpUriRequest sentRequest = (HttpUriRequest) Robolectric.getLatestSentHttpRequest();
        String resultUri = sentRequest.getURI().toString();

        assertTrue(sentRequest.getMethod().equals("GET"));
        assertTrue("Actual request: " + resultUri, resultUri.contains("?member=2&inv_code=test"));
        assertTrue("Actual request: " + resultUri, !resultUri.contains("?id=1"));
    }

    /**
     * Test that only member ID and inventory code passed in, ad call will be made
     */
    @Test
    public void testSetOnlyMemberIdAndInventoryCodeOnInterstitial() {
        InterstitialAdView iad = new InterstitialAdView(activity);
        iad.setInventoryCodeAndMemberID(2, "test");
        Robolectric.addPendingHttpResponse(200, TestResponses.banner());
        // Since window is not visible,
        // loadAd() would return false in this case,
        // calling request manager execute() here to make an ad request
        AdViewRequestManager requestManager = new AdViewRequestManager(iad);
        requestManager.execute();
        assertTrue("Request param not ready", requestManager.getRequestParams().isReadyForRequest());
        // Run async task to request an ad
        waitForTasks();
        while (Robolectric.getBackgroundScheduler().areAnyRunnable()) {
            Robolectric.getBackgroundScheduler().runOneTask();
        }
        Robolectric.runUiThreadTasks();
        // Get the sent request
        HttpUriRequest sentRequest = (HttpUriRequest) Robolectric.getLatestSentHttpRequest();
        String resultUri = sentRequest.getURI().toString();

        assertTrue(sentRequest.getMethod().equals("GET"));
        assertTrue("Actual request: " + resultUri, resultUri.contains("?member=2&inv_code=test"));
        assertTrue("Actual request: " + resultUri, !resultUri.contains("?id="));
    }

    /**
     * Test that only placement id passed in, ad call will be made
     */
    @Test
    public void testSetOnlyPlacementIdOnInterstitial() {
        InterstitialAdView iad = new InterstitialAdView(activity);
        iad.setPlacementID("1");
        Robolectric.addPendingHttpResponse(200, TestResponses.banner());
        // Since window is not visible,
        // loadAd() would return false in this case,
        // calling request manager execute() here to make an ad request
        AdViewRequestManager requestManager = new AdViewRequestManager(iad);
        requestManager.execute();
        assertTrue("Request param not ready", requestManager.getRequestParams().isReadyForRequest());
        // Run async task to request an ad
        waitForTasks();
        while (Robolectric.getBackgroundScheduler().areAnyRunnable()) {
            Robolectric.getBackgroundScheduler().runOneTask();
        }
        Robolectric.runUiThreadTasks();
        // Get the sent request
        HttpUriRequest sentRequest = (HttpUriRequest) Robolectric.getLatestSentHttpRequest();
        String resultUri = sentRequest.getURI().toString();

        assertTrue(sentRequest.getMethod().equals("GET"));
        assertTrue("Actual request: " + resultUri, !resultUri.contains("member="));
        assertTrue("Actual request: " + resultUri, !resultUri.contains("inv_code="));
        assertTrue("Actual request: " + resultUri, resultUri.contains("id=1"));
    }

    /**
     * Test that no member id and inventory code or placement id passed in, ad call will be not be made
     */
    @Test
    public void testSetNoIdentifierOnInterstitial() {
        InterstitialAdView iad = new InterstitialAdView(activity);
        Robolectric.addPendingHttpResponse(200, TestResponses.banner());
        // Since window is not visible,
        // loadAd() would return false in this case,
        // calling request manager execute() here to make an ad request
        AdViewRequestManager requestManager = new AdViewRequestManager(iad);
        assertTrue("Request param should not be ready.", !requestManager.getRequestParams().isReadyForRequest());
    }

    /**
     * Test that when both member ID, inventory code & placement ID are passed in,
     * ad call should happen and only member ID, inventory code should be used
     */
    @Test
    public void testSetMemberIdAndInventoryCodeAndPlacementIDOnNative() {
        NativeAdRequest adRequest = new NativeAdRequest(activity, "1");
        NativeAdRequestListener listener = new NativeAdRequestListener() {
            @Override
            public void onAdLoaded(NativeAdResponse response) {
                // do nothing
            }

            @Override
            public void onAdFailed(ResultCode errorcode) {
                // do nothing
            }
        };
        adRequest.setListener(listener);
        adRequest.setInventoryCodeAndMemberID(2, "test");
        Robolectric.addPendingHttpResponse(200, TestResponses.anNative());
        NativeAdRequestManager requestManager = new NativeAdRequestManager(adRequest);
        requestManager.execute();
        assertTrue("Request param not ready", requestManager.getRequestParams().isReadyForRequest());
        waitForTasks();
        while (Robolectric.getBackgroundScheduler().areAnyRunnable()) {
            Robolectric.getBackgroundScheduler().runOneTask();
        }
        Robolectric.runUiThreadTasks();
        // Get the sent request
        HttpUriRequest sentRequest = (HttpUriRequest) Robolectric.getLatestSentHttpRequest();
        String resultUri = sentRequest.getURI().toString();

        assertTrue(sentRequest.getMethod().equals("GET"));
        assertTrue("Actual request: " + resultUri, resultUri.contains("?member=2&inv_code=test"));
        assertTrue("Actual request: " + resultUri, !resultUri.contains("?id=1"));
    }

    /**
     * Test that only member ID and inventory code passed in, ad call will be made
     */
    @Test
    public void testSetOnlyMemberIdAndInventoryCodeOnNative() {
        NativeAdRequest adRequest = new NativeAdRequest(activity, "test", 2);
        NativeAdRequestListener listener = new NativeAdRequestListener() {
            @Override
            public void onAdLoaded(NativeAdResponse response) {
                // do nothing
            }

            @Override
            public void onAdFailed(ResultCode errorcode) {
                // do nothing
            }
        };
        adRequest.setListener(listener);
        Robolectric.addPendingHttpResponse(200, TestResponses.anNative());
        NativeAdRequestManager requestManager = new NativeAdRequestManager(adRequest);
        requestManager.execute();
        assertTrue("Request param not ready", requestManager.getRequestParams().isReadyForRequest());
        // Run async task to request an ad
        waitForTasks();
        while (Robolectric.getBackgroundScheduler().areAnyRunnable()) {
            Robolectric.getBackgroundScheduler().runOneTask();
        }
        Robolectric.runUiThreadTasks();
        // Get the sent request
        HttpUriRequest sentRequest = (HttpUriRequest) Robolectric.getLatestSentHttpRequest();
        String resultUri = sentRequest.getURI().toString();

        assertTrue(sentRequest.getMethod().equals("GET"));
        assertTrue("Actual request: " + resultUri, resultUri.contains("?member=2&inv_code=test"));
        assertTrue("Actual request: " + resultUri, !resultUri.contains("?id="));
        // change inventory code and params on ad request also changes
        adRequest.setInventoryCodeAndMemberID(3, "test_2");
        Robolectric.addPendingHttpResponse(200, TestResponses.anNative());
        requestManager = new NativeAdRequestManager(adRequest);
        requestManager.execute();
        assertTrue("Request param not ready", requestManager.getRequestParams().isReadyForRequest());
        // Run async task to request an ad
        waitForTasks();
        while (Robolectric.getBackgroundScheduler().areAnyRunnable()) {
            Robolectric.getBackgroundScheduler().runOneTask();
        }
        Robolectric.runUiThreadTasks();
        // Get the sent request
        sentRequest = (HttpUriRequest) Robolectric.getLatestSentHttpRequest();
        resultUri = sentRequest.getURI().toString();

        assertTrue(sentRequest.getMethod().equals("GET"));
        assertTrue("Actual request: " + resultUri, resultUri.contains("?member=3&inv_code=test_2"));
        assertTrue("Actual request: " + resultUri, !resultUri.contains("?id="));
    }

    /**
     * Test that only placement id passed in, ad call will be made
     */
    @Test
    public void testSetOnlyPlacementIdOnNative() {
        NativeAdRequest adRequest = new NativeAdRequest(activity, "1");
        NativeAdRequestListener listener = new NativeAdRequestListener() {
            @Override
            public void onAdLoaded(NativeAdResponse response) {
                // do nothing
            }

            @Override
            public void onAdFailed(ResultCode errorcode) {
                // do nothing
            }
        };
        adRequest.setListener(listener);
        Robolectric.addPendingHttpResponse(200, TestResponses.anNative());
        NativeAdRequestManager requestManager = new NativeAdRequestManager(adRequest);
        requestManager.execute();
        assertTrue("Request param not ready", requestManager.getRequestParams().isReadyForRequest());
        // Run async task to request an ad
        waitForTasks();
        while (Robolectric.getBackgroundScheduler().areAnyRunnable()) {
            Robolectric.getBackgroundScheduler().runOneTask();
        }
        Robolectric.runUiThreadTasks();
        // Get the sent request
        HttpUriRequest sentRequest = (HttpUriRequest) Robolectric.getLatestSentHttpRequest();
        String resultUri = sentRequest.getURI().toString();

        assertTrue(sentRequest.getMethod().equals("GET"));
        assertTrue("Actual request: " + resultUri, !resultUri.contains("member="));
        assertTrue("Actual request: " + resultUri, !resultUri.contains("inv_code="));
        assertTrue("Actual request: " + resultUri, resultUri.contains("id=1"));
    }

}