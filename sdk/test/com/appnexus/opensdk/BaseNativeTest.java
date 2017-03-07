/*
 *    Copyright 2015 APPNEXUS INC
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

import org.robolectric.Robolectric;

public class BaseNativeTest extends BaseRoboTest implements NativeAdRequestListener {
    NativeAdRequest adRequest;
    NativeAdResponse response;

    boolean adLoaded, adFailed;


    @Override
    public void setup() {
        super.setup();

        adLoaded = false;
        adFailed = false;

        adRequest = new NativeAdRequest(activity, "0");
        // clear AAID async task
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        adRequest.setListener(this);
    }

    @Override
    public void tearDown() {
        super.tearDown();
        if (response != null) {
            response.destroy();
        }
    }

    @Override
    public void onAdLoaded(NativeAdResponse response) {
        adLoaded = true;
        this.response = response;
    }

    @Override
    public void onAdFailed(ResultCode errorcode) {
        adFailed = true;

    }

}
