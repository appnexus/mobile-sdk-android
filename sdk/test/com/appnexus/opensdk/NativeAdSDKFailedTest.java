/*
 *    Copyright 2015 APPNEXUS INC
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


import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.appnexus.opensdk.shadows.ShadowAsyncTaskNoExecutor;
import com.appnexus.opensdk.shadows.ShadowCustomWebView;
import com.appnexus.opensdk.shadows.ShadowSettings;
import com.appnexus.opensdk.shadows.ShadowWebSettings;
import com.appnexus.opensdk.testviews.DummyView;
import com.appnexus.opensdk.ut.UTConstants;
import com.appnexus.opensdk.util.Lock;
import com.appnexus.opensdk.utils.Settings;
import com.squareup.okhttp.mockwebserver.MockResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowLooper;

import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;


@Config(sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class,
                ShadowCustomWebView.class, ShadowWebSettings.class, ShadowSettings.class, ShadowLog.class})
@RunWith(RobolectricTestRunner.class)
public class NativeAdSDKFailedTest extends BaseNativeTest implements NativeAdEventListener {

    boolean adWasClicked, adWillLeaveApplication;
    View nativeAdView;

    public void assertAdLoaded(Boolean loaded) {
        assertTrue(adLoaded || adFailed);
        assertTrue(loaded | !adFailed);
        assertTrue(loaded | adLoaded);
    }

    public void assertAdFailed(Boolean loadFailed) {
        assertTrue(adLoaded || adFailed);
        assertTrue(loadFailed | !adLoaded);
        assertTrue(loadFailed | adFailed);
    }

    @Override
    public void setup() {
        super.setup();
        Settings.getSettings().ua = "";
        adWasClicked = false;
        adWillLeaveApplication = false;
        Settings.NATIVE_AD_ABOUT_TO_EXPIRE_INTERVAL = Settings.NATIVE_AD_ABOUT_TO_EXPIRE_INTERVAL_DEFAULT;
    }





    @Test
    public void requestNativeSuccessTestExpiryModifiedValidInterval() {
        Settings.NATIVE_AD_ABOUT_TO_EXPIRE_INTERVAL = 120 * 1000;
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.anNative()));
        adRequest.loadAd();
        Lock.pause(1000);
        waitForTasks();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();

        waitForTasks();
        Robolectric.getBackgroundThreadScheduler().advanceToNextPostedRunnable();
        Robolectric.getForegroundThreadScheduler().advanceToNextPostedRunnable();
        assertAdLoaded(true);
        nativeAdView = DummyView.getDummyView(activity);

        NativeAdSDK.registerTracking(response, nativeAdView, this);
        long aboutToExpireTime = getAboutToExpireTime(UTConstants.RTB, 0);
        long expiryInterval = getExpiryInterval(UTConstants.RTB, 0);
        Log.e("INTERVALS", "About To Expire Interval: " + aboutToExpireTime + ", ExpiryInterval: " + expiryInterval);
        assertEquals(aboutToExpireTime, Settings.NATIVE_AD_RESPONSE_EXPIRATION_TIME - Settings.NATIVE_AD_ABOUT_TO_EXPIRE_INTERVAL);
        assertEquals(expiryInterval, Settings.NATIVE_AD_ABOUT_TO_EXPIRE_INTERVAL);

        ShadowLooper.pauseMainLooper();
        Robolectric.getForegroundThreadScheduler().advanceBy(aboutToExpireTime, TimeUnit.MILLISECONDS);
        ShadowLooper.unPauseMainLooper();
        assertTrue(aboutToExpire);

        ShadowLooper.pauseMainLooper();
        Robolectric.getForegroundThreadScheduler().advanceBy(expiryInterval, TimeUnit.MILLISECONDS);
        ShadowLooper.unPauseMainLooper();
        assertTrue(expired);

    }

    @Test
    public void requestNativeSuccessTestExpiryModifiedInvalidInterval() {
        Settings.NATIVE_AD_ABOUT_TO_EXPIRE_INTERVAL = Settings.NATIVE_AD_RESPONSE_EXPIRATION_TIME;
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.anNative()));
        adRequest.loadAd();
        Lock.pause(1000);
        waitForTasks();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();

        waitForTasks();
        Robolectric.getBackgroundThreadScheduler().advanceToNextPostedRunnable();
        Robolectric.getForegroundThreadScheduler().advanceToNextPostedRunnable();
        assertAdLoaded(true);
        nativeAdView = DummyView.getDummyView(activity);

        NativeAdSDK.registerTracking(response, nativeAdView, this);
        long aboutToExpireTime = getAboutToExpireTime(UTConstants.RTB, 0);
        long expiryInterval = getExpiryInterval(UTConstants.RTB, 0);
        Log.e("INTERVALS", "About To Expire Interval: " + aboutToExpireTime + ", ExpiryInterval: " + expiryInterval);
        assertEquals(aboutToExpireTime, Settings.NATIVE_AD_RESPONSE_EXPIRATION_TIME - Settings.NATIVE_AD_ABOUT_TO_EXPIRE_INTERVAL_DEFAULT);
        assertEquals(expiryInterval, Settings.NATIVE_AD_ABOUT_TO_EXPIRE_INTERVAL_DEFAULT);

        ShadowLooper.pauseMainLooper();
        Robolectric.getForegroundThreadScheduler().advanceBy(aboutToExpireTime, TimeUnit.MILLISECONDS);
        ShadowLooper.unPauseMainLooper();
        assertTrue(aboutToExpire);

        ShadowLooper.pauseMainLooper();
        Robolectric.getForegroundThreadScheduler().advanceBy(expiryInterval, TimeUnit.MILLISECONDS);
        ShadowLooper.unPauseMainLooper();
        assertTrue(expired);

    }



//    @Test
//    public void requestNativeSuccessTestExpiryNotCalledAfterOnAdAboutToExpiryAlreadyCalledWhenViewAttachedToWindow() {
//        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.anNative()));
//        adRequest.loadAd();
//        Lock.pause(1000);
//        waitForTasks();
//        Robolectric.flushForegroundThreadScheduler();
//        Robolectric.flushBackgroundThreadScheduler();
//
//        waitForTasks();
//        Robolectric.getBackgroundThreadScheduler().advanceToNextPostedRunnable();
//        Robolectric.getForegroundThreadScheduler().advanceToNextPostedRunnable();
//        assertAdLoaded(true);
//        nativeAdView = DummyView.getDummyView(activity);
//
//        NativeAdSDK.registerTracking(response, nativeAdView, this);
//
//        long aboutToExpireTime = getAboutToExpireTime(UTConstants.RTB, 0);
//        long expiryInterval = getExpiryInterval(UTConstants.RTB, 0);
//        Log.e("INTERVALS", "About To Expire Interval: " + aboutToExpireTime + ", ExpiryInterval: " + expiryInterval);
//        assertEquals(aboutToExpireTime, Settings.NATIVE_AD_RESPONSE_EXPIRATION_TIME - Settings.NATIVE_AD_ABOUT_TO_EXPIRE_INTERVAL_DEFAULT);
//        assertEquals(expiryInterval, Settings.NATIVE_AD_ABOUT_TO_EXPIRE_INTERVAL_DEFAULT);
//
//        ShadowLooper.pauseMainLooper();
//        Robolectric.getForegroundThreadScheduler().advanceBy(aboutToExpireTime, TimeUnit.MILLISECONDS);
//        ShadowLooper.unPauseMainLooper();
//
//        assertTrue(aboutToExpire);
//
//        attachToWindow(nativeAdView);
//        nativeAdView.setVisibility(View.VISIBLE);
//
//        while (!impressionLogged) {
//            waitForTasks();
//            Robolectric.getBackgroundThreadScheduler().advanceToNextPostedRunnable();
//            Robolectric.getForegroundThreadScheduler().advanceToNextPostedRunnable();
//        }
//        assertTrue(impressionLogged);
//
//        ShadowLooper.pauseMainLooper();
//        Robolectric.getForegroundThreadScheduler().advanceBy(expiryInterval, TimeUnit.MILLISECONDS);
//        ShadowLooper.unPauseMainLooper();
//        assertFalse(expired);
//
//    }



    // ========= INMOBI ====== //

    @Test
    public void requestNativeSuccessTestExpiryInmobi() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.anNativeInMobi()));
        adRequest.loadAd();
        Lock.pause(1000);
        waitForTasks();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();

        waitForTasks();
        Robolectric.getBackgroundThreadScheduler().advanceToNextPostedRunnable();
        Robolectric.getForegroundThreadScheduler().advanceToNextPostedRunnable();
        assertAdLoaded(true);
        nativeAdView = DummyView.getDummyView(activity);

        NativeAdSDK.registerTracking(response, nativeAdView, this);
        long aboutToExpireTime = getAboutToExpireTime(UTConstants.RTB, 12317);
        long expiryInterval = getExpiryInterval(UTConstants.RTB, 12317);
        Log.e("INTERVALS", "About To Expire Interval: " + aboutToExpireTime + ", ExpiryInterval: " + expiryInterval);
        assertEquals(aboutToExpireTime, Settings.NATIVE_AD_RESPONSE_EXPIRATION_TIME_INMOBI - Settings.NATIVE_AD_ABOUT_TO_EXPIRE_INTERVAL_DEFAULT);
        assertEquals(expiryInterval, Settings.NATIVE_AD_ABOUT_TO_EXPIRE_INTERVAL_DEFAULT);

        ShadowLooper.pauseMainLooper();
        Robolectric.getForegroundThreadScheduler().advanceBy(aboutToExpireTime, TimeUnit.MILLISECONDS);
        ShadowLooper.unPauseMainLooper();

        assertTrue(aboutToExpire);
        ShadowLooper.pauseMainLooper();
        Robolectric.getForegroundThreadScheduler().advanceBy(expiryInterval, TimeUnit.MILLISECONDS);
        ShadowLooper.unPauseMainLooper();
        assertTrue(expired);

    }

    @Test
    public void requestNativeSuccessTestExpiryInMobiModifiedValidInterval() {
        Settings.NATIVE_AD_ABOUT_TO_EXPIRE_INTERVAL = 120 * 1000;
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.anNativeInMobi()));
        adRequest.loadAd();
        Lock.pause(1000);
        waitForTasks();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();

        waitForTasks();
        Robolectric.getBackgroundThreadScheduler().advanceToNextPostedRunnable();
        Robolectric.getForegroundThreadScheduler().advanceToNextPostedRunnable();
        assertAdLoaded(true);
        nativeAdView = DummyView.getDummyView(activity);

        NativeAdSDK.registerTracking(response, nativeAdView, this);
        long aboutToExpireTime = getAboutToExpireTime(UTConstants.RTB, 12317);
        long expiryInterval = getExpiryInterval(UTConstants.RTB, 12317);
        Log.e("INTERVALS", "About To Expire Interval: " + aboutToExpireTime + ", ExpiryInterval: " + expiryInterval);
        assertEquals(aboutToExpireTime, Settings.NATIVE_AD_RESPONSE_EXPIRATION_TIME_INMOBI - Settings.NATIVE_AD_ABOUT_TO_EXPIRE_INTERVAL);
        assertEquals(expiryInterval, Settings.NATIVE_AD_ABOUT_TO_EXPIRE_INTERVAL);

        ShadowLooper.pauseMainLooper();
        Robolectric.getForegroundThreadScheduler().advanceBy(aboutToExpireTime, TimeUnit.MILLISECONDS);
        ShadowLooper.unPauseMainLooper();

        assertTrue(aboutToExpire);
        ShadowLooper.pauseMainLooper();
        Robolectric.getForegroundThreadScheduler().advanceBy(expiryInterval, TimeUnit.MILLISECONDS);
        ShadowLooper.unPauseMainLooper();
        assertTrue(expired);

    }


    @Test
    public void requestNativeSuccessInMobiTestExpiryNotCalledWhenViewAttachedToWindow() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.anNativeInMobi()));
        adRequest.loadAd();
        Lock.pause(1000);
        waitForTasks();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();

        waitForTasks();
        Robolectric.getBackgroundThreadScheduler().advanceToNextPostedRunnable();
        Robolectric.getForegroundThreadScheduler().advanceToNextPostedRunnable();
        assertAdLoaded(true);
        nativeAdView = DummyView.getDummyView(activity);
        attachToWindow(nativeAdView);
        nativeAdView.setVisibility(View.VISIBLE);

        NativeAdSDK.registerTracking(response, nativeAdView, this);

        long aboutToExpireTime = getAboutToExpireTime(UTConstants.RTB, 12317);
        long expiryInterval = getExpiryInterval(UTConstants.RTB, 12317);
        Log.e("INTERVALS", "About To Expire Interval: " + aboutToExpireTime + ", ExpiryInterval: " + expiryInterval);
        assertEquals(aboutToExpireTime, Settings.NATIVE_AD_RESPONSE_EXPIRATION_TIME_INMOBI - Settings.NATIVE_AD_ABOUT_TO_EXPIRE_INTERVAL_DEFAULT);
        assertEquals(expiryInterval, Settings.NATIVE_AD_ABOUT_TO_EXPIRE_INTERVAL_DEFAULT);

        while (!impressionLogged) {
            waitForTasks();
            Robolectric.getBackgroundThreadScheduler().advanceToNextPostedRunnable();
            Robolectric.getForegroundThreadScheduler().advanceToNextPostedRunnable();
        }

        assertTrue(impressionLogged);

        ShadowLooper.pauseMainLooper();
        Robolectric.getForegroundThreadScheduler().advanceBy(aboutToExpireTime, TimeUnit.MILLISECONDS);
        ShadowLooper.unPauseMainLooper();

        assertFalse(aboutToExpire);
        ShadowLooper.pauseMainLooper();
        Robolectric.getForegroundThreadScheduler().advanceBy(expiryInterval, TimeUnit.MILLISECONDS);
        ShadowLooper.unPauseMainLooper();
        assertFalse(expired);

    }

    // ========= End testing with registerExpiryListener ========

    private void attachToWindow(View nativeAdView) {
        // Create a container (a parent view that holds all the
        // views for native ads)


        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(10, 10);
        nativeAdView.setLayoutParams(layoutParams);


        LinearLayout container = new LinearLayout(activity);
        container.addView(nativeAdView);


        final ViewGroup viewGroup = ((ViewGroup) activity.getWindow().getDecorView().getRootView());
        viewGroup.addView(container);
        nativeAdView.setVisibility(View.VISIBLE);
    }

    @Override
    public void tearDown() {
        super.tearDown();
        if (response != null) {
            response.destroy();
        }
    }

    @Override
    public void onAdWasClicked() {
        adWasClicked = true;
    }

    @Override
    public void onAdWillLeaveApplication() {
        adWillLeaveApplication = true;
    }

    @Override
    public void onAdWasClicked(String clickUrl, String fallbackURL) {
        adWasClicked = true;
    }
}
