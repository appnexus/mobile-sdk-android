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
import com.appnexus.opensdkdemo.testviews.SuccessfulMediationView;
import com.appnexus.opensdkdemo.util.InstanceLock;
import com.appnexus.opensdkdemo.util.TestUtil;

public class TestActivity404Error extends ActivityInstrumentationTestCase2<DemoMainActivity> implements AdListener {

    DemoMainActivity activity;
    BannerAdView bav;
    InstanceLock lock;
    boolean didPass = false;
    String old_base_url;

    public TestActivity404Error() {
        super(DemoMainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        old_base_url = Settings.getSettings().BASE_URL;
        Settings.getSettings().BASE_URL = TestUtil.MEDIATION_TEST_URL;
        Clog.d(TestUtil.testLogTag, "BASE_URL set to " + Settings.getSettings().BASE_URL);
        SuccessfulMediationView.didPass = false;
        didPass = false;
        lock = new InstanceLock();

        setActivityInitialTouchMode(false);

        activity = getActivity();

        DummyView.createView(activity);

        bav = (BannerAdView) activity.findViewById(com.appnexus.opensdkdemo.R.id.banner);
        bav.setPlacementID("9b");
        bav.setAdListener(this);
    }


    @Override
    protected void tearDown() throws Exception {
        Clog.d(TestUtil.testLogTag, "tear down");
        Settings.getSettings().BASE_URL = old_base_url;

        super.tearDown();
    }

    public void test404() {
        // Create a AdRequest which will request a mediated response to
        // instantiate the fake view. The (failure) result_cb
        // should return a 404 error, which should fail instantiation

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bav.loadAd();
            }
        });

        lock.pause(10000);

        // give time for the result cb to fire
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(true, didPass);
        // currently no way to verify the response from the resultCB
        // use LogCat
    }

    @Override
    public void onAdLoaded(AdView adView) {
        didPass = true;
        lock.unpause();
    }

    @Override
    public void onAdRequestFailed(AdView adView) {
        didPass = false;
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