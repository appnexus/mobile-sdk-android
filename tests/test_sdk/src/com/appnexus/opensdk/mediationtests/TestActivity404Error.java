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

package com.appnexus.opensdk.mediationtests;

import android.test.ActivityInstrumentationTestCase2;
import com.appnexus.opensdk.AdListener;
import com.appnexus.opensdk.AdView;
import com.appnexus.opensdk.BannerAdView;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.TestActivity;
import com.appnexus.opensdk.testviews.DummyView;
import com.appnexus.opensdk.testviews.SuccessfulMediationView;
import com.appnexus.opensdk.util.InstanceLock;
import com.appnexus.opensdk.util.TestUtil;

public class TestActivity404Error extends ActivityInstrumentationTestCase2<TestActivity> implements AdListener {
    /**
     * NOTE: requires commenting out return code in MAVC's resultCB handler
     * to allow for multiple successes.
     */
    TestActivity activity;
    BannerAdView bav;
    InstanceLock lock;
    boolean didLoad = false, didFail = false;
    String old_base_url;

    public TestActivity404Error() {
        super(TestActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        old_base_url = Settings.getSettings().BASE_URL;
        Settings.getSettings().BASE_URL = TestUtil.MEDIATION_TEST_URL;
        Clog.d(TestUtil.testLogTag, "BASE_URL set to " + Settings.getSettings().BASE_URL);
        SuccessfulMediationView.didPass = false;
        didLoad = false;
        didFail = false;
        lock = new InstanceLock();

        setActivityInitialTouchMode(false);

        activity = getActivity();

        DummyView.createView(activity);

        bav = (BannerAdView) activity.findViewById(com.appnexus.opensdk.R.id.banner);
        bav.setAdListener(this);
    }


    @Override
    protected void tearDown() throws Exception {
        Clog.d(TestUtil.testLogTag, "tear down");
        Settings.getSettings().BASE_URL = old_base_url;

        super.tearDown();
    }

    public void testSuccessThen404() {
        // Create a AdRequest which will request a mediated response to
        // instantiate the a successful view. The (failure) result_cb
        // should return a 404 error, which should fail instantiation

        bav.setPlacementID("9a");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bav.loadAd();
            }
        });

        lock.pause(10000);

        assertTrue(SuccessfulMediationView.didPass);
        assertTrue(didLoad);
        assertFalse(didFail);
        // no way to verify the response from the resultCB here
        // use LogCat / Test404Error tests it
    }

    public void testFailThen404() {
        // Create a AdRequest which will request a mediated response to
        // instantiate the fake view. The (failure) result_cb
        // should return a 404 error, which should fail instantiation

        bav.setPlacementID("9b");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bav.loadAd();
            }
        });

        lock.pause(10000);

        assertFalse(didLoad);
        assertTrue(didFail);
        // no way to verify the response from the resultCB here
        // use LogCat / Test404Error tests it
    }

    @Override
    public void onAdLoaded(AdView adView) {
        didLoad = true;
        lock.unpause();
    }

    @Override
    public void onAdRequestFailed(AdView adView) {
        if (lock == null) return;
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

}