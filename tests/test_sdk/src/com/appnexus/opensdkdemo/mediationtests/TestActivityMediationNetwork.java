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
import com.appnexus.opensdk.*;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdkdemo.DemoMainActivity;
import com.appnexus.opensdkdemo.testviews.DummyView;
import com.appnexus.opensdkdemo.util.InstanceLock;
import com.appnexus.opensdkdemo.util.TestUtil;

public class TestActivityMediationNetwork extends ActivityInstrumentationTestCase2<DemoMainActivity> implements AdRequester, AdListener {

    DemoMainActivity activity;
    String old_base_url;
    AdRequest shouldWork;
    String AdMobId = "10am";
    String MMId = "10mm";
    boolean didPass = true;
    InstanceLock lock;

    /**
     * Make sure permissions are set up in demo's AndroidManifest.xml file for wifi+data access and change
     */

    public TestActivityMediationNetwork() {
        super(DemoMainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Clog.w(TestUtil.testLogTag, "Setup");
        old_base_url = Settings.getSettings().BASE_URL;
        Settings.getSettings().BASE_URL = TestUtil.MEDIATION_TEST_URL;
        Clog.d(TestUtil.testLogTag, "BASE_URL set to " + Settings.getSettings().BASE_URL);

        didPass = false;
        lock = new InstanceLock();
        setActivityInitialTouchMode(false);

        activity = getActivity();
        DummyView.createView(activity);

        TestUtil.setWifi(true, activity);
        TestUtil.setData(false, activity);
    }

    @Override
    protected void tearDown() throws Exception {
        Clog.d(TestUtil.testLogTag, "tear down");
        Settings.getSettings().BASE_URL = old_base_url;
        TestUtil.setWifi(true, activity);
        TestUtil.setData(true, activity);

        super.tearDown();
    }

    public void testAdMobNetworkInterruption() {
        // Create a AdRequest which will request a mediated response to
        // instantiate an AdMob view
        // Shut off the network before AdMob can make a call

        runBasicTest(AdMobId);
    }

    public void testMMNetworkInterruption() {
        // same for MM
        runBasicTest(MMId);
    }

    private void runBasicTest(String placementId) {
        Clog.w(TestUtil.testLogTag, "Start test");

        shouldWork = new AdRequest(this, null, null, null, placementId, null, null, 320, 50, -1, -1, null, null, null, true, this, false, false);
        // change AdRequest.java to have setter for this unit test
//        shouldWork.setContext(activity);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                shouldWork.execute();
            }
        });
        lock.pause(15000);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                shouldWork.cancel(true);
            }
        });

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(false, didPass);
    }

    @Override
    public void failed(AdRequest request) {
        Clog.d(TestUtil.testLogTag, "request failed: " + request);
        // initial call for mediated ad failed. abort test
        assertEquals(true, false);
    }

    @Override
    public void onReceiveResponse(AdResponse response) {
        if (response == null) return;
        Clog.d(TestUtil.testLogTag, "received response: " + response.toString());

        Clog.w(TestUtil.testLogTag, "disabling wifi");
        TestUtil.setWifi(false, activity);
        TestUtil.setData(false, activity);

        if (response.getMediatedAds() != null) {
            MediatedBannerAdViewController output = MediatedBannerAdViewController.create(
                    activity, this, response.getMediatedAds().pop(), null);
        }
        else
            didPass = true;

    }

    @Override
    public AdView getOwner() {
        return null;
    }

    @Override
    public void setAdRequest(AdRequest adRequest) {
    }

    @Override
    public void onAdLoaded(AdView adView) {
        if (lock == null) return;
        Clog.d(TestUtil.testLogTag, "loaded: " + adView);
        didPass = true;
        lock.unpause();
    }

    @Override
    public void onAdRequestFailed(AdView adView) {
        if (lock == null) return;
        Clog.d(TestUtil.testLogTag, "request failed");
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
