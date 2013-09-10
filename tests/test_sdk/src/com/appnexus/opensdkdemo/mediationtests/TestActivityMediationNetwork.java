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

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import com.appnexus.opensdk.*;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdkdemo.DemoMainActivity;
import com.appnexus.opensdkdemo.testviews.DummyView;
import com.appnexus.opensdkdemo.util.InstanceLock;
import com.appnexus.opensdkdemo.util.TestUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestActivityMediationNetwork extends ActivityInstrumentationTestCase2<DemoMainActivity> implements AdRequester, AdListener {

    DemoMainActivity activity;
    String old_base_url;
    AdRequest shouldWork;
    String AdMobId = "10am";
    String MMId = "10mm";
    boolean didPass = true;
    BannerAdView bav;
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

        setWifi(true);
        setData(false);

        bav = (BannerAdView) activity.findViewById(com.appnexus.opensdkdemo.R.id.banner);
        bav.setAdListener(this);
    }

    @Override
    protected void tearDown() throws Exception {
        Clog.d(TestUtil.testLogTag, "tear down");
        Settings.getSettings().BASE_URL = old_base_url;
        setWifi(true);
        setData(true);

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
//		shouldWork.setContext(activity);
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
        Log.d(TestUtil.testLogTag, "request failed: " + request);
        // initial call for mediated ad failed. abort test
        assertEquals(true, false);
    }

    @Override
    public void onReceiveResponse(AdResponse response) {
        Log.d(TestUtil.testLogTag, "received response: " + response.toString());

        Clog.w(TestUtil.testLogTag, "disabling wifi");
        setWifi(false);
        setData(false);

        MediatedBannerAdViewController output = MediatedBannerAdViewController.create(
                bav, response);
    }

    @Override
    public AdView getOwner() {
        return null;
    }

    @Override
    synchronized public void dispatchResponse(final AdResponse response) {
        Clog.d(TestUtil.testLogTag, "dispatch: " + response.toString());
        didPass = true;
    }

    @Override
    public void onAdLoaded(AdView adView) {
        Log.d(TestUtil.testLogTag, "loaded");
        didPass = true;
    }

    @Override
    public void onAdRequestFailed(AdView adView) {
        Log.d(TestUtil.testLogTag, "request failed");
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

    private void setWifi(boolean state) {
        WifiManager wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(state);

        // let the change process
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setData(boolean state) {
        ConnectivityManager dataManager;
        dataManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        Method dataMtd = null;
        try {
            dataMtd = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", boolean.class);
            dataMtd.setAccessible(true);
            dataMtd.invoke(dataManager, state);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        // let the change process
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
