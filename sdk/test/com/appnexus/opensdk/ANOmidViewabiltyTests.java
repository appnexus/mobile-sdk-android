/*
 *    Copyright 2018 APPNEXUS INC
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

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.appnexus.opensdk.shadows.ShadowOMIDBannerHTMLWebView;
import com.appnexus.opensdk.shadows.ShadowSettings;
import com.appnexus.opensdk.util.Lock;
import com.iab.omid.library.appnexus.Omid;
import com.squareup.okhttp.mockwebserver.MockResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import static junit.framework.Assert.assertTrue;


/**
 * This class covers the cases for BannerAdView impression tracker firing.
 * <p>
 * Currently its possible to write these cases only with loadAdOffScreen case's
 * loadAd is not possible because of limitations in roboelectric need to figure out a workaround in future.
 */

@Config(sdk = 21,
        shadows = {ShadowSettings.class, ShadowLog.class, ShadowOMIDBannerHTMLWebView.class})
@RunWith(RobolectricTestRunner.class)
public class ANOmidViewabiltyTests extends BaseViewAdTest {

    @Before
    public void setup() {
        super.setup();
        ShadowOMIDBannerHTMLWebView.omidImpressionString = "";
        ShadowOMIDBannerHTMLWebView.omidInitString = "";
        ShadowOMIDBannerHTMLWebView.omidStartSession = "";
    }

    @After
    public void tearDown() {
        super.tearDown();
        ShadowOMIDBannerHTMLWebView.omidImpressionString = "";
        ShadowOMIDBannerHTMLWebView.omidInitString = "";
        ShadowOMIDBannerHTMLWebView.omidStartSession = "";
    }

    // 1. Loads the banner
    // 2. Checks is OMID is activated or not.
    @Test
    public void testOMIDInitSuccess() {
        assertTrue(Omid.isActive());
    }


    // 1. Loads the banner
    // 2. Checks if omid init is fired to JS layer or not
    // 3. Checks if impression event is fired to JS layer or not.
    @Test
    public void testOmidSessionCreation() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner())); // This is for UT Request
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank())); // This is for Impression
        runBasicBannerTest();
        attachBannerToView();

        assertTrue(ShadowOMIDBannerHTMLWebView.omidInitString.contains("if(window.omidBridge!==undefined){omidBridge.init({\"impressionOwner\":\"native\",\"mediaEventsOwner\":\"none\",\"creativeType\":\"htmlDisplay\",\"impressionType\":\"viewable\",\"isolateVerificationScripts\":false})}"));
        int count = 0;
        while (count < 5 && ShadowOMIDBannerHTMLWebView.omidImpressionString.isEmpty()) {
            count++;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        assertTrue(ShadowOMIDBannerHTMLWebView.omidImpressionString.contains("if(window.omidBridge!==undefined){omidBridge.publishImpressionEvent()}"));
    }


    private void executeBannerRequest() {


        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();


        requestManager = new AdViewRequestManager(bannerAdView);
        requestManager.execute();

        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();

        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

//        Robolectric.getBackgroundThreadScheduler().advanceToNextPostedRunnable();
//        Robolectric.getForegroundThreadScheduler().advanceToNextPostedRunnable();
    }


    // common format for several of the basic banner tests execute UT request and attach the banner to a View
    public void runBasicBannerTest() {
        executeBannerRequest();
        assertCallbacks(true);
    }

    private void attachBannerToView() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(320, 50);
        bannerAdView.setLayoutParams(layoutParams);

        LinearLayout container = new LinearLayout(activity);
        container.addView(bannerAdView);

        final ViewGroup viewGroup = ((ViewGroup) activity.getWindow().getDecorView().getRootView());
        viewGroup.addView(container);
        bannerAdView.setVisibility(View.VISIBLE);
    }


    @Override
    public void onAdLoaded(AdView adView) {
        super.onAdLoaded(adView);
        Lock.unpause();
    }

    @Override
    public void onAdRequestFailed(AdView adView, ResultCode resultCode) {
        super.onAdRequestFailed(adView, resultCode);
        Lock.unpause();
    }


}
