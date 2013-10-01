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

package com.appnexus.opensdkdemo.mediationtests;

import android.test.ActivityInstrumentationTestCase2;
import com.appnexus.opensdk.AdListener;
import com.appnexus.opensdk.AdView;
import com.appnexus.opensdk.BannerAdView;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdkdemo.DemoMainActivity;
import com.appnexus.opensdkdemo.testviews.DummyView;
import com.appnexus.opensdkdemo.testviews.SecondSuccessfulMediationView;
import com.appnexus.opensdkdemo.testviews.SuccessfulMediationView;
import com.appnexus.opensdkdemo.testviews.ThirdSuccessfulMediationView;
import com.appnexus.opensdkdemo.util.InstanceLock;
import com.appnexus.opensdkdemo.util.TestUtil;

public class TestActivityMediationWaterfall extends ActivityInstrumentationTestCase2<DemoMainActivity> implements AdListener {

    DemoMainActivity activity;
    BannerAdView bav;
    InstanceLock lock;
    boolean didLoad = false, didFailToLoad = false;
    String old_base_url;

    public TestActivityMediationWaterfall() {
        super(DemoMainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        old_base_url = Settings.getSettings().BASE_URL;
        Settings.getSettings().BASE_URL = TestUtil.MEDIATION_TEST_URL;
        Clog.d(TestUtil.testLogTag, "BASE_URL set to " + Settings.getSettings().BASE_URL);
        SuccessfulMediationView.didPass = false;
        SecondSuccessfulMediationView.didPass = false;
        ThirdSuccessfulMediationView.didPass = false;
        didLoad = false;
        didFailToLoad = false;
        lock = new InstanceLock();

        setActivityInitialTouchMode(false);

        activity = getActivity();

        DummyView.createView(activity);

        bav = (BannerAdView) activity.findViewById(com.appnexus.opensdkdemo.R.id.banner);
        bav.setAdListener(this);
    }

    @Override
    protected void tearDown() throws Exception {
        Clog.d(TestUtil.testLogTag, "tear down");
        Settings.getSettings().BASE_URL = old_base_url;

        super.tearDown();
    }

    public void test1FirstSuccessfulSkipSecond() {
        bav.setPlacementID("11");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bav.loadAd();
            }
        });

        lock.pause(10000);

        assertEquals(true, didLoad);
        assertEquals(false, didFailToLoad);
        assertTrue(SuccessfulMediationView.didPass);
        assertFalse(SecondSuccessfulMediationView.didPass);
    }

    public void test2SkipFirstSuccessfulSecond() {
        bav.setPlacementID("12");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bav.loadAd();
            }
        });

        lock.pause(10000);

        assertEquals(true, didLoad);
        assertEquals(false, didFailToLoad);
        assertTrue(SecondSuccessfulMediationView.didPass);
    }

    public void test3FirstFailsIntoOverrideStd() {
        bav.setPlacementID("13");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bav.loadAd();
            }
        });

        lock.pause(10000);

        assertEquals(true, didLoad);
        assertEquals(false, didFailToLoad);
        assertFalse(SuccessfulMediationView.didPass);
    }

    public void test4FirstFailsIntoOverrideMediated() {
        bav.setPlacementID("14");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bav.loadAd();
            }
        });

        lock.pause(10000);

        assertEquals(true, didLoad);
        assertEquals(false, didFailToLoad);
        assertFalse(SuccessfulMediationView.didPass);
        assertTrue(ThirdSuccessfulMediationView.didPass);
    }

    public void test5TestNoFill() {
        bav.setPlacementID("15");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bav.loadAd();
            }
        });

        lock.pause(10000);

        // onAdRequestFailed should have been called
        assertFalse(didLoad);
        assertTrue(didFailToLoad);
    }

    public void test6NoResultCB() {
        bav.setPlacementID("16");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bav.loadAd();
            }
        });

        lock.pause(10000);

        // onAdLoaded should have been called
        assertTrue(didLoad);
        assertFalse(didFailToLoad);
        assertTrue(SuccessfulMediationView.didPass);
    }

    @Override
    public void onAdLoaded(AdView adView) {
        didLoad = true;
        lock.unpause();
    }

    @Override
    public void onAdRequestFailed(AdView adView) {
        didFailToLoad = true;
        lock.unpause();
    }

    @Override
    public void onAdExpanded(AdView adView) {
    }

    @Override
    public void onAdCollapsed(AdView adView) {
    }

    @Override
    public void onAdClicked(AdView adView) {
    }
}