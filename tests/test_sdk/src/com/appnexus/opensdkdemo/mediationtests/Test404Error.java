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
import com.appnexus.opensdkdemo.testviews.SuccessfulMediationView;
import com.appnexus.opensdkdemo.util.InstanceLock;
import com.appnexus.opensdkdemo.util.TestUtil;

public class Test404Error extends AndroidTestCase implements AdRequester {
    /**
     * NOTE: requires commenting out return code in MAVC's resultCB handler
     * to allow for multiple successes.
     */
    String old_base_url;
    AdRequest shouldWork;
    String shouldWorkPlacement;
    InstanceLock lock;
    boolean responseWasNull;
    boolean receivedResponse;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        old_base_url = Settings.getSettings().BASE_URL;
        Settings.getSettings().BASE_URL = TestUtil.MEDIATION_TEST_URL;
        Clog.d(TestUtil.testLogTag, "BASE_URL set to " + Settings.getSettings().BASE_URL);
        SuccessfulMediationView.didPass = false;
        DummyView.createView(getContext());
        lock = new InstanceLock();
        responseWasNull = false;
        receivedResponse = false;
    }

    @Override
    protected void tearDown() throws Exception {
        Clog.d(TestUtil.testLogTag, "tear down");
        Settings.getSettings().BASE_URL = old_base_url;
        super.tearDown();
    }

    public void testSuccessWithResult404() {
        // Create a AdRequest which will request a mediated response to
        // instantiate the SuccessfulMediationView. The (success) result_cb
        // should return a 404 error, which should fail instantiation

        shouldWorkPlacement = "9a";
        shouldWork = new AdRequest(this, null, null, null, shouldWorkPlacement, null, null, 320, 50, -1, -1, null, null, null, true, null, false, false);
        shouldWork.execute();
        lock.pause(10000);
        shouldWork.cancel(true);

        assertTrue(receivedResponse);
        assertEquals(true, SuccessfulMediationView.didPass);
        assertTrue(responseWasNull);
    }

    public void testFailWithResult404() {
        // Create a AdRequest which will request a mediated response to
        // instantiate the a non-existent view. The (failure) result_cb
        // should return a 404 error, which should fail instantiation

        shouldWorkPlacement = "9b";
        shouldWork = new AdRequest(this, null, null, null, shouldWorkPlacement, null, null, 320, 50, -1, -1, null, null, null, true, null, false, false);
        shouldWork.execute();
        lock.pause(10000);
        shouldWork.cancel(true);

        assertTrue(receivedResponse);
        assertTrue(responseWasNull);
    }

    @Override
    public void failed(AdRequest request) {
        Clog.d(TestUtil.testLogTag, "request failed: " + request);
        SuccessfulMediationView.didPass = false;
        lock.unpause();
    }

    @Override
    public void onReceiveResponse(AdResponse response) {
        // response should be a regular valid mediatied ad
        Clog.d(TestUtil.testLogTag, "received response: " + response);
        if (response != null && response.getMediatedAds() != null) {
            MediatedBannerAdViewController output = MediatedBannerAdViewController.create(
                    null, this, response.getMediatedAds().pop(), null);
            receivedResponse = true;
            return;
        } else if (response == null) {
            // resultCB response should have be a 404, so null
            responseWasNull = true;
        }
        lock.unpause();
    }

    @Override
    public AdView getOwner() {
        return null;
    }
}
