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

package com.appnexus.opensdkdemo.stdtests;

import android.test.AndroidTestCase;
import com.appnexus.opensdk.*;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdkdemo.util.InstanceLock;
import com.appnexus.opensdkdemo.util.TestUtil;

public class TestRetryAdRequest extends AndroidTestCase implements AdRequester {
    /**
     * Make sure permissions are set up in demo's AndroidManifest.xml file for wifi+data access and change
     *
     * Also make sure AdRequest has setContext() uncommented, and uncomment the code here
     */

    String settingsURL;
    long settingsInterval;
    AdRequest shouldFailAndRetry;
    boolean didFail = false;
    boolean didSucceed = false;
    InstanceLock lock;
    int timesRetried = 0;
    AdView adView;

    String placementId = "17";

    protected void setUp() throws Exception {
        super.setUp();
        Clog.w(TestUtil.testLogTag, "Testing Retries");
        settingsURL = Settings.getSettings().BASE_URL;
        settingsInterval = Settings.getSettings().HTTP_RETRY_INTERVAL;
        Settings.getSettings().BASE_URL = TestUtil.MEDIATION_TEST_URL;
        Settings.getSettings().HTTP_RETRY_INTERVAL = TestUtil.SHORT_RETRY_INTERVAL;
        shouldFailAndRetry = new AdRequest(this, "123456", null, null, placementId, "portrait", "AT&T",
                320, 50, 320, 50, null, null, "wifi", false, null, true, true);
        lock = new InstanceLock();
        timesRetried = 0;
        adView = new BannerAdView(getContext());
        adView.setPlacementID(placementId);

//        shouldFailAndRetry.setContext(getContext());
        TestUtil.setWifi(true, getContext());
        TestUtil.setData(true, getContext());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        TestUtil.setWifi(true, getContext());
        TestUtil.setData(true, getContext());
    }

    public void testBlankRetries() {
        shouldFailAndRetry.execute();
        long retryTime = Settings.getSettings().HTTP_RETRY_INTERVAL * Settings.getSettings().MAX_BLANK_RETRIES;
        lock.pause(10000 + retryTime);
        shouldFailAndRetry.cancel(true);

        assertFalse(didSucceed);
        assertTrue(didFail);
        assertEquals(Settings.getSettings().MAX_BLANK_RETRIES, timesRetried);
    }

    public void testConnectivityRetries() {
        TestUtil.setWifi(false, getContext());
        TestUtil.setData(false, getContext());

        shouldFailAndRetry.execute();
        long retryTime = Settings.getSettings().HTTP_RETRY_INTERVAL * Settings.getSettings().MAX_CONNECTIVITY_RETRIES;
        lock.pause(10000 + retryTime);
        shouldFailAndRetry.cancel(true);

        assertFalse(didSucceed);
        assertTrue(didFail);
        assertEquals(Settings.getSettings().MAX_CONNECTIVITY_RETRIES, timesRetried);
    }

    @Override
    public void onReceiveResponse(AdResponse response) {
        didSucceed = true;
        if (lock != null)
            lock.unpause();
    }

    @Override
    public AdView getOwner() {
        return adView;
    }

    @Override
    public void setAdRequest(AdRequest adRequest) {
        // ignore deallocation calls
        if (adRequest != null)
            timesRetried++;
    }

    @Override
    public void failed(AdRequest request) {
        didFail = true;
        if (lock != null)
            lock.unpause();
    }
}
