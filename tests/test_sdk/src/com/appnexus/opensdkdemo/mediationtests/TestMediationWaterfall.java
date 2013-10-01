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

import android.test.AndroidTestCase;
import com.appnexus.opensdk.*;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdkdemo.testviews.DummyView;
import com.appnexus.opensdkdemo.testviews.SecondSuccessfulMediationView;
import com.appnexus.opensdkdemo.testviews.SuccessfulMediationView;
import com.appnexus.opensdkdemo.util.Lock;
import com.appnexus.opensdkdemo.util.TestUtil;

public class TestMediationWaterfall extends AndroidTestCase implements AdRequester, MediatedAdViewControllerListener {
    String old_base_url;
    AdRequest shouldWork;
    String testId = "11";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        old_base_url = Settings.getSettings().BASE_URL;
        Settings.getSettings().BASE_URL = TestUtil.MEDIATION_TEST_URL;
        Clog.d(TestUtil.testLogTag, "BASE_URL set to " + Settings.getSettings().BASE_URL);
        shouldWork = new AdRequest(this, null, null, null, testId, null, null, 320, 50, -1, -1, null, null, null, true, null, false, false);
        SuccessfulMediationView.didPass = false;
        SecondSuccessfulMediationView.didPass = false;
        DummyView.createView(getContext());
    }

    @Override
    protected void tearDown() throws Exception {
        Clog.d(TestUtil.testLogTag, "tear down");
        Settings.getSettings().BASE_URL = old_base_url;
        super.tearDown();
    }

    public void test1FirstSuccessfulSkipSecond() {
        // Create a AdRequest which will request a mediated response to
        // instantiate the SuccessfulMediationView
        // Since we're just testing to see successful instantiation, interrupt
        // the sleeping thread from the requestAd function

        shouldWork.execute();
        Lock.pause(10000);
        shouldWork.cancel(true);

        assertTrue(SuccessfulMediationView.didPass);
        assertFalse(SecondSuccessfulMediationView.didPass);
    }

    @Override
    public void failed(AdRequest request) {
        Clog.d(TestUtil.testLogTag, "request failed: " + request);
        SuccessfulMediationView.didPass = false;
        Lock.unpause();
    }

    @Override
    public void onReceiveResponse(AdResponse response) {
        Clog.d(TestUtil.testLogTag, "received first response");
        MediatedBannerAdViewController output = MediatedBannerAdViewController.create(
                null, this, response.getMediatedAds().pop(), this);
    }

    @Override
    public AdView getOwner() {
        return null;
    }

    @Override
    public void dispatchResponse(AdResponse response) {
        Clog.d(TestUtil.testLogTag, "dispatch (for second and third responses)");
        MediatedBannerAdViewController output = MediatedBannerAdViewController.create(
                null, this, response.getMediatedAds().pop(), null);
    }

    @Override
    public void onAdLoaded() {
    }

    @Override
    public void onAdFailed(boolean noMoreAds) {
    }

    @Override
    public void onAdExpanded() {
    }

    @Override
    public void onAdCollapsed() {
    }

    @Override
    public void onAdClicked() {
    }
}
