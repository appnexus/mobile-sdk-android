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

import android.test.ActivityInstrumentationTestCase2;
import com.appnexus.opensdk.AdListener;
import com.appnexus.opensdk.AdView;
import com.appnexus.opensdk.BannerAdView;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.TestActivity;
import com.appnexus.opensdk.testviews.DummyView;
import com.appnexus.opensdk.testviews.SecondSuccessfulMediationView;
import com.appnexus.opensdk.testviews.SuccessfulMediationView;
import com.appnexus.opensdk.testviews.ThirdSuccessfulMediationView;
import com.appnexus.opensdk.util.InstanceLock;
import com.appnexus.opensdk.util.Lock;
import com.appnexus.opensdk.util.TestUtil;

public class TestActivityMediationCallbacks extends ActivityInstrumentationTestCase2<TestActivity> implements AdListener {

    TestActivity activity;
    BannerAdView bav;
    InstanceLock lock;
    boolean didLoad, didFail;
    boolean didLoadMultiple, didFailMultiple;
    boolean didExpand, didCollapse, didClick;
    String oldUrl;

    private static final long WAIT_TIME = 10000;

    public TestActivityMediationCallbacks() {
        super(TestActivity.class);
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
        didExpand = false;
        didCollapse = false;
        didClick = false;
        lock = new InstanceLock();

        setActivityInitialTouchMode(false);

        activity = getActivity();

        DummyView.createView(activity);

        bav = (BannerAdView) activity.findViewById(com.appnexus.opensdk.R.id.banner);
        bav.setAdListener(this);
    }

    @Override
    protected void tearDown() throws Exception {
        Clog.w(TestUtil.testLogTag, "tear down");
        Settings.getSettings().BASE_URL = oldUrl;

        Thread.sleep(2500);

        super.tearDown();
    }

    private void runBasicTest(String placementId, boolean didLoadValue, long waitPeriod) {
        bav.setPlacementID(placementId);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bav.loadAd();
            }
        });

        lock.pause(waitPeriod);

        // wait for the view to notify after completing all of its calls
        Lock.pause();

        assertEquals(didLoadValue, didLoad);
        assertEquals(!didLoadValue, didFail);
        assertFalse(didLoadMultiple);
        assertFalse(didFailMultiple);
    }

    public void test18AdLoadedMultiple() throws Exception {
        runBasicTest("18", true, WAIT_TIME);
    }

    public void test19Timeout() {
        runBasicTest("19", false, WAIT_TIME + Settings.getSettings().MEDIATED_NETWORK_TIMEOUT);
    }

    public void test20LoadThenFail() {
        runBasicTest("20", true, WAIT_TIME);
    }

    public void test21FailThenLoad() {
        runBasicTest("21", false, WAIT_TIME);
    }

    public void test22LoadAndHitOtherCallbacks() {
        runBasicTest("22", true, WAIT_TIME + Settings.getSettings().MEDIATED_NETWORK_TIMEOUT);

        assertTrue(didExpand);
        assertTrue(didCollapse);
        assertTrue(didClick);
    }

    public void test23FailAndHitOtherCallbacks() {
        runBasicTest("23", false, WAIT_TIME + Settings.getSettings().MEDIATED_NETWORK_TIMEOUT);

        assertFalse(didExpand);
        assertFalse(didCollapse);
        assertFalse(didClick);
    }

    public void test24AdFailedMultiple() throws Exception {
        runBasicTest("24", false, WAIT_TIME);
    }

    public void test25LoadThenLoadNewAndHitOtherCallbacks() throws Exception {
        bav.setPlacementID("22");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bav.loadAd();
            }
        });

        lock.pause(WAIT_TIME);

        assertTrue(didLoad);
        assertFalse(didFail);
        assertFalse(didLoadMultiple);
        assertFalse(didFailMultiple);

        // reset the variable since we will load a new ad
        didLoad = false;

        bav.setPlacementID("18");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bav.loadAd();
            }
        });

        lock.pause(WAIT_TIME);

        // wait for the view to notify after completing all of its calls
        Lock.pause();

        assertTrue(didLoad);
        assertFalse(didFail);
        assertFalse(didLoadMultiple);
        assertFalse(didFailMultiple);

        Lock.pause();

        assertFalse(didExpand);
        assertFalse(didCollapse);
        assertFalse(didClick);
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
        Clog.d(TestUtil.testLogTag, "onAdExpanded");
        didExpand = true;
    }

    @Override
    public void onAdCollapsed(AdView adView) {
        Clog.d(TestUtil.testLogTag, "onAdCollapsed");
        didCollapse = true;
    }

    @Override
    public void onAdClicked(AdView adView) {
        Clog.d(TestUtil.testLogTag, "onAdClicked");
        didClick = true;
    }
}