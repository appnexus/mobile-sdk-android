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
import android.view.View;
import com.appnexus.opensdk.AdListener;
import com.appnexus.opensdk.AdView;
import com.appnexus.opensdk.BannerAdView;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.TestActivity;
import com.appnexus.opensdk.testviews.DummyView;
import com.appnexus.opensdk.testviews.ThirdSuccessfulMediationView;
import com.appnexus.opensdk.util.InstanceLock;
import com.appnexus.opensdk.util.Lock;
import com.appnexus.opensdk.util.TestUtil;

public class TestActivityRefreshStdThenMediated extends ActivityInstrumentationTestCase2<TestActivity> implements AdListener {

    TestActivity activity;
    BannerAdView bav;
    boolean isLoadingMediation;
    InstanceLock lock;
    String old_base_url;
    boolean didPassStd = false;


    public TestActivityRefreshStdThenMediated() {
        super(TestActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        old_base_url = Settings.getSettings().BASE_URL;
        Settings.getSettings().BASE_URL = TestUtil.MEDIATION_TEST_URL;
        Clog.d(TestUtil.testLogTag, "BASE_URL set to " + Settings.getSettings().BASE_URL);
        ThirdSuccessfulMediationView.didPass = false;
        didPassStd = false;
        isLoadingMediation = false;
        lock = new InstanceLock();

        setActivityInitialTouchMode(false);

        activity = getActivity();

        DummyView.createView(activity);

        bav = (BannerAdView) activity.findViewById(com.appnexus.opensdk.R.id.banner);
        bav.setPlacementID("8a");
        bav.setAutoRefreshInterval(15);
        bav.setAdListener(this);
    }


    @Override
    protected void tearDown() throws Exception {
        Clog.d(TestUtil.testLogTag, "tear down");
        Settings.getSettings().BASE_URL = old_base_url;

        super.tearDown();
    }

    public void testRefresh() {
        Clog.w(TestUtil.testLogTag, "TEST REFRESH");

        // get a std ad
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bav.loadAd();
            }
        });

        lock.pause(10000);

        // set up mediation properties
        isLoadingMediation = true;
        bav.setPlacementID("8b");

        Clog.w(TestUtil.testLogTag, "wait for refresh - 15s minimum; " + Thread.currentThread().getName());
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Clog.w(TestUtil.testLogTag, "wait for refresh done " + Thread.currentThread().getName());

        // wait for mediation response to process and complete
        Lock.pause(10000);

        assertEquals(true, didPassStd);
        assertEquals(true, ThirdSuccessfulMediationView.didPass);
        assertEquals(View.VISIBLE, bav.getVisibility());
    }

    @Override
    public void onAdLoaded(AdView adView) {
        didPassStd = true;
        if (!isLoadingMediation) {
            lock.unpause();
        }
    }

    @Override
    public void onAdRequestFailed(AdView adView) {
        if (lock == null) return;
        didPassStd = false;
        if (!isLoadingMediation) {
            lock.unpause();
        }
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