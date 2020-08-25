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


import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.appnexus.opensdk.shadows.ShadowAsyncTaskNoExecutor;
import com.appnexus.opensdk.shadows.ShadowCustomWebView;
import com.appnexus.opensdk.shadows.ShadowSettings;
import com.appnexus.opensdk.shadows.ShadowWebSettings;
import com.appnexus.opensdk.testviews.DummyView;
import com.appnexus.opensdk.util.Lock;
import com.appnexus.opensdk.utils.Settings;
import com.squareup.okhttp.mockwebserver.MockResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;


@Config(sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class,
                ShadowCustomWebView.class, ShadowWebSettings.class, ShadowSettings.class, ShadowLog.class})
@RunWith(RobolectricTestRunner.class)
public class NativeAdSDKTest extends BaseNativeTest implements NativeAdEventListener {

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
    }

    @Test
    public void requestNativeSuccess() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.anNative()));
        adRequest.loadAd();
        Lock.pause(1000);
        waitForTasks();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();

        waitForTasks();
        Robolectric.getBackgroundThreadScheduler().advanceToNextPostedRunnable();
        Robolectric.getForegroundThreadScheduler().advanceToNextPostedRunnable();
//        Robolectric.flushForegroundThreadScheduler();
//        Lock.pause(2000);
        assertAdLoaded(true);
        nativeAdView = DummyView.getDummyView(activity);
        attachToWindow(nativeAdView);

        NativeAdSDK.registerTracking(response, nativeAdView, this);

        //@TODO can do perform Click and Test but issue with roboelectric https://github.com/robolectric/robolectric/issues/2372
        //Shadows.shadowOf(nativeAdView).checkedPerformClick();
        //Robolectric.getForegroundThreadScheduler().advanceToNextPostedRunnable();
        //assertTrue(adWasClicked);

        assertFalse(response.hasExpired());

    }

//    @Test
//    public void requestOnAdDidLogImpression() {
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
//        assertFalse(nativeAdView.isShown());
//        NativeAdSDK.registerTracking(nativeAdResponse, nativeAdView, this);
//        attachToWindow(nativeAdView);
//        assertTrue(nativeAdView.isShown());
//
//        Shadows.shadowOf(nativeAdView).setViewFocus(true);
//        assertTrue(nativeAdView.isShown());
//        Robolectric.getForegroundThreadScheduler().advanceToNextPostedRunnable();
//        Lock.pause(2000);
//        assertTrue(nativeAdView.getVisibility() == View.VISIBLE);
//        assertTrue(impressionLogged);
//        assertFalse(response.hasExpired());
//
//    }


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
