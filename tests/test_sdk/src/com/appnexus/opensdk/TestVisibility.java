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

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import com.appnexus.opensdk.AdListener;
import com.appnexus.opensdk.AdView;
import com.appnexus.opensdk.BannerAdView;
import com.appnexus.opensdk.TestActivity;
import com.appnexus.opensdk.R;
import com.appnexus.opensdk.util.InstanceLock;

public class TestVisibility extends
        ActivityInstrumentationTestCase2<TestActivity> implements AdListener {
    TestActivity activity;
    BannerAdView bav;
    int bavVisibility;
    InstanceLock lock1, lock2;

    public TestVisibility() {
        super(TestActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(false);
        activity = getActivity();
        bavVisibility = View.GONE;
        lock1 = new InstanceLock();
        lock2 = new InstanceLock();
    }

    public void testVisibility() {
        bav = (BannerAdView) activity.findViewById(R.id.banner);
        bav.setAutoRefreshInterval(0);
        bav.setPlacementID("1281482");
        bav.setAdListener(this);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bav.loadAd();
            }
        });

        lock1.pause(10000);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bavVisibility = bav.getVisibility();
                lock2.unpause();
            }
        });

        lock2.pause(10000);

        assertEquals(View.VISIBLE, bavVisibility);
    }

    @Override
    public void onAdLoaded(AdView adView) {
        lock1.unpause();
    }

    @Override
    public void onAdRequestFailed(AdView adView) {
        assertEquals(false, true);
        lock1.unpause();
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
