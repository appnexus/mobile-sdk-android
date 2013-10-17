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

public class TestActivityMediationCallbacks extends ActivityInstrumentationTestCase2<DemoMainActivity> implements AdListener {

    DemoMainActivity activity;
    BannerAdView bav;
    InstanceLock lock;
    boolean didLoad, didFail;
    boolean didLoadMultiple, didFailMultiple;
    String oldUrl;

    private static final long WAIT_TIME = 10000;

    public TestActivityMediationCallbacks() {
        super(DemoMainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        oldUrl = Settings.getSettings().BASE_URL;
        Settings.getSettings().BASE_URL = TestUtil.MEDIATION_TEST_URL;
        Clog.w(TestUtil.testLogTag, "BASE_URL set to " + Settings.getSettings().BASE_URL);
        SuccessfulMediationView.didPass = false;
        SecondSuccessfulMediationView.didPass = false;
        ThirdSuccessfulMediationView.didPass = false;
        didLoad = false;
        didLoadMultiple = false;
        didFail = false;
        didFailMultiple = false;
        lock = new InstanceLock();

        setActivityInitialTouchMode(false);

        activity = getActivity();

        DummyView.createView(activity);

        bav = (BannerAdView) activity.findViewById(com.appnexus.opensdkdemo.R.id.banner);
        bav.setAdListener(this);
    }

    @Override
    protected void tearDown() throws Exception {
        Clog.w(TestUtil.testLogTag, "tear down");
        Settings.getSettings().BASE_URL = oldUrl;

        Thread.sleep(2500);

        super.tearDown();
    }

    public void test18AdLoadedMultiple() throws Exception {
        bav.setPlacementID("18");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bav.loadAd();
            }
        });

        lock.pause(WAIT_TIME);

        // wait a bit to see if any new callbacks come in
        pause();

        assertTrue(didLoad);
        assertFalse(didLoadMultiple);
        assertFalse(didFail);
        assertFalse(didFailMultiple);
    }

    public void test19Timeout() {
        bav.setPlacementID("19");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bav.loadAd();
            }
        });

        lock.pause(WAIT_TIME + Settings.getSettings().MEDIATED_NETWORK_TIMEOUT);

        // wait a bit to see if any new callbacks come in
        pause();

        assertFalse(didLoad);
        assertFalse(didLoadMultiple);
        assertTrue(didFail);
        assertFalse(didFailMultiple);
    }


    public void test20LoadThenFail() {
        bav.setPlacementID("20");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bav.loadAd();
            }
        });

        lock.pause(WAIT_TIME);

        // wait a bit to see if any new callbacks come in
        pause();

        assertTrue(didLoad);
        assertFalse(didLoadMultiple);
        assertFalse(didFail);
        assertFalse(didFailMultiple);
    }

    public void test21FailThenLoad() {
        bav.setPlacementID("21");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bav.loadAd();
            }
        });

        lock.pause(WAIT_TIME);

        // wait a bit to see if any new callbacks come in
        pause();

        assertFalse(didLoad);
        assertFalse(didLoadMultiple);
        assertTrue(didFail);
        assertFalse(didFailMultiple);
    }

    @Override
    public void onAdLoaded(AdView adView) {
        if (lock == null) return;
        Clog.d(TestUtil.testLogTag, "onAdLoaded");
        if (didLoad)
            didLoadMultiple = true;
        didLoad = true;
        lock.unpause();
    }

    @Override
    public void onAdRequestFailed(AdView adView) {
        if (lock == null) return;
        Clog.d(TestUtil.testLogTag, "onAdFailed");
        if (didFail)
            didFailMultiple = true;
        didFail = true;
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

    protected void pause() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}