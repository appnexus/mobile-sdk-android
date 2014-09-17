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

import android.app.Activity;
import android.os.Looper;

import com.appnexus.opensdk.OpenSDKUnitTestsActivity;
import com.appnexus.opensdk.testviews.*;
import com.appnexus.opensdk.util.Lock;

import org.junit.After;
import org.junit.Before;
import org.robolectric.Robolectric;
import org.robolectric.util.Scheduler;

import java.util.Timer;
import java.util.TimerTask;

import static junit.framework.Assert.assertEquals;

public class BaseRoboTest implements AdListener {
    Activity activity;
    BannerAdView bannerAdView;
    InterstitialAdView interstitialAdView;
    AdRequest adRequest;
    boolean adLoaded, adFailed, adExpanded, adCollapsed, adClicked;

    Scheduler uiScheduler, bgScheduler, looperScheduler;

    @Before
    public void setup(){
        activity = Robolectric.buildActivity(OpenSDKUnitTestsActivity.class).create().get();
        //activity = Robolectric.buildActivity(Activity.class).create().visible().get();
        Robolectric.shadowOf(activity).grantPermissions("android.permission.ACCESS_NETWORK_STATE");
        Robolectric.shadowOf(activity).grantPermissions("android.permission.ACCESS_COARSE_LOCATION");
        Robolectric.shadowOf(activity).grantPermissions("android.permission.ACCESS_FINE_LOCATION");
        Robolectric.shadowOf(activity).grantPermissions("android.permission.INTERNET");

        bannerAdView = new BannerAdView(activity);
        bannerAdView.setPlacementID("0");
        bannerAdView.setAdListener(this);
        bannerAdView.setAutoRefreshInterval(0);

        interstitialAdView = new InterstitialAdView(activity);
        interstitialAdView.setPlacementID("0");
        interstitialAdView.setAdListener(this);

        looperScheduler = Robolectric.shadowOf(Looper.getMainLooper()).getScheduler();
        bgScheduler = Robolectric.getBackgroundScheduler();
        uiScheduler = Robolectric.getUiThreadScheduler();
        bgScheduler.pause();
        uiScheduler.pause();

        adLoaded = false;
        adFailed = false;
        adExpanded = false;
        adCollapsed = false;
        adClicked = false;

        SuccessfulBanner.didPass = false;
        SuccessfulBanner2.didPass = false;
        NoRequestBannerView.didInstantiate = false;
        NoFillView.didDestroy = false;
        DummyView.dummyView = null;
        System.out.println("Base setup complete.");
    }

    @After
    public void tearDown() {
        Robolectric.clearHttpResponseRules();
        Robolectric.clearPendingHttpResponses();

        looperScheduler.reset();
        bgScheduler.reset();
        uiScheduler.reset();

        bgScheduler.pause();
        uiScheduler.pause();
    }

    public void assertCallbacks(boolean success) {
        assertEquals(success, adLoaded);
        assertEquals(!success, adFailed);
    }

    public void scheduleTimerToCheckForTasks() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if ((looperScheduler.enqueuedTaskCount() > 0)
                        || (uiScheduler.enqueuedTaskCount() > 0)
                        || (bgScheduler.enqueuedTaskCount() > 0)) {
                    Lock.unpause();
                    this.cancel();
                }
            }
        }, 0, 100);
    }

    public void waitForTasks() {
        scheduleTimerToCheckForTasks();
        Lock.pause();
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
