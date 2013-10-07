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

import com.appnexus.opensdk.*;
import junit.framework.TestCase;

public class TestFailingAdRequest extends TestCase implements AdRequester, AdListener {
    int notifyCount = 0;
    AdRequest shouldNotWork;
    AdRequest shouldNotWork2;
    boolean shouldPass = false;
    boolean shouldPass2 = false;

    protected void setUp() {
        shouldNotWork = new AdRequest(this, "123456", null, null, null, "portrait", "AT&T", 320, 50, 320, 50, null, null, "wifi", false, null, true, false);
        shouldNotWork2 = new AdRequest(null, "123456", null, null, null, "portrait", "AT&T", 320, 50, 320, 50, null, null, "wifi", false, this, true, false);
    }

    public void testFailingRequest() {
        shouldNotWork.execute();
        pause();
        shouldNotWork.cancel(true);
        assertEquals(true, shouldPass);
    }

    public void testFailingRequestListener() {
        shouldNotWork2.execute();
        pause();
        shouldNotWork2.cancel(true);
        assertEquals(true, shouldPass2);
    }

    synchronized void pause() {
        try {
            wait(30 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            shouldNotWork.cancel(true);
            shouldNotWork2.cancel(true);
            return;
        }
    }

    @Override
    synchronized public void onReceiveResponse(AdResponse response) {
        shouldPass = response.getBody() != null ? false : true;
        if (response.getBody() != null)
            shouldPass = response.getBody().length() > 0 ? false : true;
        notify();
    }

    @Override
    public AdView getOwner() {
        return null;
    }

    @Override
    synchronized public void failed(AdRequest request) {
        shouldPass = true;
        notify();
    }

    @Override
    synchronized public void onAdLoaded(AdView adView) {
        shouldPass2 = false;
        notify();
    }

    @Override
    synchronized public void onAdRequestFailed(AdView adView) {
        shouldPass2 = true;
        notify();
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
