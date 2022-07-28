/*
 *    Copyright 2020 APPNEXUS INC
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

import com.appnexus.opensdk.shadows.ShadowAsyncTaskNoExecutor;
import com.appnexus.opensdk.shadows.ShadowCustomWebView;
import com.appnexus.opensdk.shadows.ShadowSettings;
import com.appnexus.opensdk.shadows.ShadowWebSettings;
import com.squareup.okhttp.mockwebserver.MockResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@Config(sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class,
                ShadowCustomWebView.class, ShadowWebSettings.class, ShadowSettings.class, ShadowLog.class})
@RunWith(RobolectricTestRunner.class)
public class AdViewFriendlyObstructionTests extends BaseViewAdTest {

    @Override
    public void setup() {
        super.setup();
//        NativeAdSDK.count = 0;
    }

    @Override
    public void tearDown() {
        super.tearDown();
    }

    @Test
    public void testBannerAddFriendlyObstruction() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner())); // First queue a regular HTML banner response
        assertFriendlyObstruction(bannerAdView, 0);
        SDKSettings.setOMEnabled(true);
        View v1 = new View(activity);
        View v2 = new View(activity);
        View v3 = new View(activity);
        v1.setAlpha(0f);
        v2.setAlpha(0f);
        v3.setAlpha(0f);
        bannerAdView.addFriendlyObstruction(v1);
        bannerAdView.addFriendlyObstruction(v2);
        bannerAdView.addFriendlyObstruction(v3);
        executeBannerRequest();
        assertFriendlyObstruction(bannerAdView, 3);
    }

    @Test
    public void testBannerAddAndRemoveFriendlyObstruction() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner())); // First queue a regular HTML banner response
        assertFriendlyObstruction(bannerAdView, 0);
        SDKSettings.setOMEnabled(true);
        View v1 = new View(activity);
        View v2 = new View(activity);
        View v3 = new View(activity);
        View v4 = new View(activity);
        v1.setAlpha(0f);
        v2.setAlpha(0f);
        v3.setAlpha(0f);
        v4.setAlpha(0f);
        bannerAdView.addFriendlyObstruction(v1);
        bannerAdView.addFriendlyObstruction(v2);
        bannerAdView.addFriendlyObstruction(v3);
        bannerAdView.addFriendlyObstruction(v4);
        executeBannerRequest();
        assertFriendlyObstruction(bannerAdView, 4);
        bannerAdView.removeFriendlyObstruction(v1);
        assertFriendlyObstruction(bannerAdView, 3);
    }

    @Test
    public void testBannerAddAndRemoveAllFriendlyObstruction() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner())); // First queue a regular HTML banner response
        assertFriendlyObstruction(bannerAdView, 0);
        SDKSettings.setOMEnabled(true);
        View v1 = new View(activity);
        View v2 = new View(activity);
        View v3 = new View(activity);
        View v4 = new View(activity);
        v1.setAlpha(0f);
        v2.setAlpha(0f);
        v3.setAlpha(0f);
        v4.setAlpha(0f);
        bannerAdView.addFriendlyObstruction(v1);
        bannerAdView.addFriendlyObstruction(v2);
        bannerAdView.addFriendlyObstruction(v3);
        bannerAdView.addFriendlyObstruction(v4);
        executeBannerRequest();
        assertFriendlyObstruction(bannerAdView, 4);
        bannerAdView.removeAllFriendlyObstructions();
        assertFriendlyObstruction(bannerAdView, 0);
    }

    @Test
    public void testInterstitialAddFriendlyObstruction() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner())); // First queue a regular HTML banner response
        assertFriendlyObstruction(interstitialAdView, 0);
        SDKSettings.setOMEnabled(true);
        View v1 = new View(activity);
        View v2 = new View(activity);
        View v3 = new View(activity);
        v1.setAlpha(0f);
        v2.setAlpha(0f);
        v3.setAlpha(0f);
        interstitialAdView.addFriendlyObstruction(v1);
        interstitialAdView.addFriendlyObstruction(v2);
        interstitialAdView.addFriendlyObstruction(v3);
        executeBannerRequest();
        assertFriendlyObstruction(interstitialAdView, 3);
    }

    @Test
    public void testInterstitialAddAndRemoveFriendlyObstruction() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner())); // First queue a regular HTML banner response
        assertFriendlyObstruction(interstitialAdView, 0);
        SDKSettings.setOMEnabled(true);
        View v1 = new View(activity);
        View v2 = new View(activity);
        View v3 = new View(activity);
        View v4 = new View(activity);
        v1.setAlpha(0f);
        v2.setAlpha(0f);
        v3.setAlpha(0f);
        v4.setAlpha(0f);
        interstitialAdView.addFriendlyObstruction(v1);
        interstitialAdView.addFriendlyObstruction(v2);
        interstitialAdView.addFriendlyObstruction(v3);
        interstitialAdView.addFriendlyObstruction(v4);
        executeBannerRequest();
        assertFriendlyObstruction(interstitialAdView, 4);
        interstitialAdView.removeFriendlyObstruction(v1);
        assertFriendlyObstruction(interstitialAdView, 3);
    }

    @Test
    public void testInterstitialAddAndRemoveAllFriendlyObstruction() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner())); // First queue a regular HTML banner response
        assertFriendlyObstruction(interstitialAdView, 0);
        SDKSettings.setOMEnabled(true);
        View v1 = new View(activity);
        View v2 = new View(activity);
        View v3 = new View(activity);
        View v4 = new View(activity);
        v1.setAlpha(0f);
        v2.setAlpha(0f);
        v3.setAlpha(0f);
        v4.setAlpha(0f);
        interstitialAdView.addFriendlyObstruction(v1);
        interstitialAdView.addFriendlyObstruction(v2);
        interstitialAdView.addFriendlyObstruction(v3);
        interstitialAdView.addFriendlyObstruction(v4);
        executeBannerRequest();
        assertFriendlyObstruction(interstitialAdView, 4);
        interstitialAdView.removeAllFriendlyObstructions();
        assertFriendlyObstruction(interstitialAdView, 0);
    }

//    @Test
//    public void testBannerNativeAddFriendlyObstruction() {
//        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.anNative())); // First queue a regular HTML banner response
//        SDKSettings.setOMEnabled(true);
//        executeBannerRequest();
//        assertFriendlyObstruction(0);
//        View v1 = new View(activity);
//        View v2 = new View(activity);
//        View v3 = new View(activity);
//        v1.setAlpha(0f);
//        v2.setAlpha(0f);
//        v3.setAlpha(0f);
//        List<View> views = new ArrayList<>();
//        views.add(v1);
//        views.add(v2);
//        views.add(v3);
//        NativeAdSDK.registerTracking(nativeAdResponse, new View(activity), null, views);
//        assertFriendlyObstruction(3);
//    }

//    @Test
//    public void testBannerNativeRendererAddFriendlyObstruction() {
//        bannerAdView.enableNativeRendering(true);
//        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.anNativeRenderer())); // First queue a regular HTML banner response
//        assertFriendlyObstruction(bannerAdView, 0);
//        SDKSettings.setOMEnabled(true);
//        View v1 = new View(activity);
//        View v2 = new View(activity);
//        View v3 = new View(activity);
//        View v4 = new View(activity);
//        bannerAdView.addFriendlyObstruction(v1);
//        bannerAdView.addFriendlyObstruction(v2);
//        bannerAdView.addFriendlyObstruction(v3);
//        bannerAdView.addFriendlyObstruction(v4);
//        executeBannerRequest();
//        assertFriendlyObstruction(4);
//    }

//    @Test
//    public void testBannerNativeRendererAddAndRemoveFriendlyObstruction() {
//        bannerAdView.enableNativeRendering(true);
//        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.anNativeRenderer())); // First queue a regular HTML banner response
//        assertFriendlyObstruction(bannerAdView, 0);
//        SDKSettings.setOMEnabled(true);
//        View v1 = new View(activity);
//        View v2 = new View(activity);
//        View v3 = new View(activity);
//        View v4 = new View(activity);
//        bannerAdView.addFriendlyObstruction(v1);
//        bannerAdView.addFriendlyObstruction(v2);
//        bannerAdView.addFriendlyObstruction(v3);
//        bannerAdView.addFriendlyObstruction(v4);
//        executeBannerRequest();
//        assertFriendlyObstruction(4);
//        bannerAdView.removeFriendlyObstruction(v1);
//        assertFriendlyObstruction(4);
//    }

//    @Test
//    public void testBannerNativeRendererAddAndRemoveAllFriendlyObstruction() {
//        bannerAdView.enableNativeRendering(true);
//        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.anNativeRenderer())); // First queue a regular HTML banner response
//        assertFriendlyObstruction(bannerAdView, 0);
//        SDKSettings.setOMEnabled(true);
//        View v1 = new View(activity);
//        View v2 = new View(activity);
//        View v3 = new View(activity);
//        View v4 = new View(activity);
//        bannerAdView.addFriendlyObstruction(v1);
//        bannerAdView.addFriendlyObstruction(v2);
//        bannerAdView.addFriendlyObstruction(v3);
//        bannerAdView.addFriendlyObstruction(v4);
//        executeBannerRequest();
//        assertFriendlyObstruction(4);
//        bannerAdView.removeAllFriendlyObstructions();
//        assertFriendlyObstruction(4);
//    }

    private void assertFriendlyObstruction(AdView adView, int count) {
        try {
            ArrayList<WeakReference<View>> viewList = adView.getFriendlyObstructionList();
            assertEquals(count, viewList.size());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

//    private void assertFriendlyObstruction(int count) throws NullPointerException {
//        int size = NativeAdSDK.count;
//        assertEquals(count, size);
//    }

    private void executeBannerRequest() {
        bannerAdView.setAutoRefreshInterval(15000);
        bannerAdView.loadAd();

        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
    }

}
