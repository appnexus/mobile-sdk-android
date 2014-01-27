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

package com.appnexus.opensdk;

import android.app.Activity;
import com.appnexus.opensdk.utils.Clog;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static junit.framework.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class BaseRoboTest implements AdListener {
    Activity activity;
    BannerAdView bannerAdView;
    AdRequest adRequest;
    boolean adLoaded, adFailed, adExpanded, adCollapsed, adClicked;

    @Before
    public void setup() {
        Clog.clogged = true;
        activity = Robolectric.buildActivity(Activity.class).create().get();
        Robolectric.shadowOf(activity).grantPermissions("android.permission.ACCESS_NETWORK_STATE");
        bannerAdView = new BannerAdView(activity);
        bannerAdView.setPlacementID("0");

        Robolectric.getBackgroundScheduler().pause();
        Robolectric.getUiThreadScheduler().pause();

        adLoaded = false;
        adFailed = false;
        adExpanded = false;
        adCollapsed = false;
        adClicked = false;
    }

    @After
    public void tearDown() {
        Robolectric.clearHttpResponseRules();
        Robolectric.clearPendingHttpResponses();
    }

    public void assertCallbacks(boolean success) {
        assertEquals(success, adLoaded);
        assertEquals(!success, adFailed);
    }

    @Override
    public void onAdLoaded(AdView adView) {
        adLoaded = true;
    }

    @Override
    public void onAdRequestFailed(AdView adView) {
        adFailed = true;
    }

    @Override
    public void onAdExpanded(AdView adView) {
        adExpanded = true;
    }

    @Override
    public void onAdCollapsed(AdView adView) {
        adCollapsed = true;
    }

    @Override
    public void onAdClicked(AdView adView) {
        adClicked = true;
    }
}
