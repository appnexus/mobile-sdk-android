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

import com.appnexus.opensdk.shadows.ShadowAsyncTaskNoExecutor;
import com.appnexus.opensdk.shadows.ShadowWebSettings;
import com.appnexus.opensdk.shadows.ShadowWebView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertTrue;

@Config(shadows = {ShadowAsyncTaskNoExecutor.class,
        ShadowWebView.class, ShadowWebSettings.class},
        emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class NativeRequestTest extends BaseNativeTest{

    public void assertAdLoaded(Boolean loaded) {
        assertTrue(adLoaded || adFailed);
        assertTrue(loaded | !adFailed);
        assertTrue(loaded | adLoaded);
    }

    @Test
    public void requestNative() {
        adRequest.loadAd();
        Robolectric.addPendingHttpResponse(200, TestResponses.anNative());
        Robolectric.runUiThreadTasks();
        waitForTasks();
        Robolectric.runBackgroundTasks();
        Robolectric.runUiThreadTasks();
        assertAdLoaded(true);
    }

    @Override
    public void tearDown() {
        super.tearDown();
        if (response != null) {
            response.destroy();
        }
    }
}
