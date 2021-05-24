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


import android.util.Log;
import android.view.View;

import com.appnexus.opensdk.mocks.MockFBNativeBannerAdResponse;
import com.appnexus.opensdk.shadows.ShadowAsyncTaskNoExecutor;
import com.appnexus.opensdk.shadows.ShadowSettings;
import com.appnexus.opensdk.shadows.ShadowWebSettings;
import com.appnexus.opensdk.testviews.DummyView;
import com.appnexus.opensdk.ut.UTConstants;
import com.appnexus.opensdk.util.Lock;
import com.appnexus.opensdk.utils.Settings;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.mockwebserver.Dispatcher;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.shadows.ShadowWebView;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNull;

@Config(sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class,
                ShadowWebView.class, ShadowWebSettings.class, ShadowSettings.class, ShadowLog.class})
@RunWith(RobolectricTestRunner.class)
public class NativeRequestTest extends BaseNativeTest {

    public void assertAdLoaded(Boolean loaded) {
        assertTrue(adLoaded || adFailed);
        assertTrue(loaded | !adFailed);
        assertTrue(loaded | adLoaded);
    }

    public void assertAdFailed(Boolean loadFailed) {
        assertTrue(adLoaded || adFailed);
        assertTrue(loadFailed | !adLoaded);
        assertTrue(loadFailed | adFailed);
    }

    @Override
    public void setup() {
        super.setup();
        Settings.getSettings().ua = "";
    }

    @Test
    public void testNativeCSRResponseLogImpresionsClicksProperly() {
        final HashMap<String, Boolean> logs = new HashMap<>();
        logs.put("impression", false);
        logs.put("click", false);
        logs.put("request_url", false);
        logs.put("response_url", false);
        HttpUrl impression = server.url("/impression");
        HttpUrl click = server.url("/click");
        final HttpUrl request_url = server.url("/request_url");
        final HttpUrl response_url = server.url("/response_url");
        final MockResponse impbusResponse = new MockResponse().setResponseCode(200).setBody(TestResponsesUT.csrNativeSuccesfulWithMockTrackers(impression.toString(), click.toString(), request_url.toString(), response_url.toString()));
        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                String path = request.getPath();
                if ("/".equals(path)) {
                    return impbusResponse;
                } else if ("/impression".equals(path)) {
                    logs.put("impresssion", true);
                    return new MockResponse().setResponseCode(200);
                } else if ("/click".equals(path)) {
                    logs.put("click", true);
                    return new MockResponse().setResponseCode(200);
                } else if ("/request_url".equals(path)) {
                    logs.put("request_url", true);
                    return new MockResponse().setResponseCode(200);
                } else if (path != null && path.startsWith("/response_url")) {
                    logs.put("response_url", true);
                    return new MockResponse().setResponseCode(200);
                }
                return new MockResponse().setResponseCode(404);
            }
        };
        server.setDispatcher(dispatcher);
        HttpUrl impbus = server.url("/");
        UTConstants.REQUEST_BASE_URL_UT = impbus.toString();
        UTConstants.REQUEST_BASE_URL_SIMPLE = impbus.toString();
        NativeAdRequestListener listener = new NativeAdRequestListener() {

            @Override
            public void onAdLoaded(NativeAdResponse response) {
                nativeAdResponse = response;
                ((MockFBNativeBannerAdResponse) response).logImpression();
                ((MockFBNativeBannerAdResponse) response).clickAd();
            }

            @Override
            public void onAdFailed(ResultCode errorcode, ANAdResponseInfo adResponseInfo) {
            }
        };
        adRequest.setListener(listener);
        adRequest.loadAd();
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertNotNull(nativeAdResponse);
        assertTrue(nativeAdResponse instanceof MockFBNativeBannerAdResponse);
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertTrue(logs.get("impresssion"));
        assertTrue(logs.get("click"));
        assertTrue(logs.get("response_url"));
    }

    @Test
    public void requestNativeGetsMediationNoFillThenCSR() {
        server.enqueue(new MockResponse().setBody(TestResponsesUT.mediationNoFillThenCSRSuccessfull()).setResponseCode(200));
//        NativeAdRequestListener adListener = mock(NativeAdRequestListener.class);
        adRequest.setListener(this);
        adRequest.loadAd();
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertNotNull(nativeAdResponse);
        assertTrue(nativeAdResponse instanceof MockFBNativeBannerAdResponse);
//        verify(adListener).onAdLoaded(any(MockFBNativeBannerAdResponse.class));
    }

    @Test
    public void requestNativeGetsCSRNofillThenMediation() {
        server.enqueue(new MockResponse().setBody(TestResponsesUT.csrNoFillThenMediationSuccessfull()).setResponseCode(200));
//        NativeAdRequestListener adListener = mock(NativeAdRequestListener.class);
        adRequest.setListener(this);
        assertNull(nativeAdResponse);
        adRequest.loadAd();
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertNotNull(nativeAdResponse);
        assertTrue(nativeAdResponse instanceof BaseNativeAdResponse);
//        verify(adListener).onAdLoaded(any(BaseNativeAdResponse.class));
    }

    @Test
    public void requestNativeCSRNofill() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.csrNativeNofill()));
//        NativeAdRequestListener adListener = mock(NativeAdRequestListener.class);
        adRequest.setListener(this);
        adRequest.loadAd();
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertNull(nativeAdResponse);
        assertAdFailed(true);
        assertEquals(ResultCode.UNABLE_TO_FILL, failErrorCode.getCode());
//        verify(adListener).onAdFailed(ResultCode.UNABLE_TO_FILL);
    }

    @Test
    public void requestNativeCSRSuccessful() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.csrNativeSuccessful()));
//        NativeAdRequestListener adListener = mock(NativeAdRequestListener.class);
        adRequest.setListener(this);
        adRequest.loadAd();
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertNotNull(nativeAdResponse);
        assertTrue(nativeAdResponse instanceof MockFBNativeBannerAdResponse);
//        verify(adListener).onAdLoaded(any(MockFBNativeBannerAdResponse.class));
    }

//    @Test
//    public void requestNativeCSRSuccessfulAdExpiry() {
//        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.csrNativeSuccessful()));
////        NativeAdRequestListener adListener = mock(NativeAdRequestListener.class);
//        adRequest.setListener(this);
//        adRequest.loadAd();
//        waitForTasks();
//        Robolectric.flushBackgroundThreadScheduler();
//        Robolectric.flushForegroundThreadScheduler();
//        waitForTasks();
//        Robolectric.flushBackgroundThreadScheduler();
//        Robolectric.flushForegroundThreadScheduler();
//        assertNotNull(nativeAdResponse);
//        assertTrue(nativeAdResponse instanceof MockFBNativeBannerAdResponse);
//        View nativeAdView = DummyView.getDummyView(activity);
//
//        NativeAdSDK.registerTracking(response, nativeAdView, this);
//        long aboutToExpireTime = getAboutToExpireTime(UTConstants.CSR, 0);
//        long expiryInterval = getExpiryInterval(UTConstants.CSR, 0);
//        Log.e("INTERVALS", "About To Expire Interval: " + aboutToExpireTime + ", ExpiryInterval: " + expiryInterval);
//        assertEquals(aboutToExpireTime, Settings.NATIVE_AD_RESPONSE_EXPIRATION_TIME_FB - Settings.NATIVE_AD_RESPONSE_ON_AD_ABOUT_TO_EXPIRE_INTERVAL_DEFAULT);
//        assertEquals(expiryInterval, Settings.NATIVE_AD_RESPONSE_ON_AD_ABOUT_TO_EXPIRE_INTERVAL_DEFAULT);
//
//        ShadowLooper.pauseMainLooper();
//        Robolectric.getForegroundThreadScheduler().advanceBy(aboutToExpireTime, TimeUnit.MILLISECONDS);
//        ShadowLooper.unPauseMainLooper();
//
//        assertTrue(aboutToExpire);
//        ShadowLooper.pauseMainLooper();
//        Robolectric.getForegroundThreadScheduler().advanceBy(expiryInterval, TimeUnit.MILLISECONDS);
//        ShadowLooper.unPauseMainLooper();
//        assertTrue(expired);
//
//
////        verify(adListener).onAdLoaded(any(MockFBNativeBannerAdResponse.class));
//    }

    @Test
    public void requestNativeSuccess() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.anNative()));
        adRequest.loadAd();
        Lock.pause(1000);
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        assertAdLoaded(true);
    }

    @Test
    public void requestNativeSuccessNativeElement() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.anNativeVideo()));
        adRequest.loadAd();
        Lock.pause(1000);
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        assertAdLoaded(true);

        System.out.println("NATIVE: " + nativeAdResponse.getNativeElements());

        JSONObject json = (JSONObject) nativeAdResponse.getNativeElements().get(NativeAdResponse.NATIVE_ELEMENT_OBJECT);
        assertTrue(json.has("link"));
        try {
            JSONObject linkJson = json.getJSONObject("link");
            assertTrue(linkJson.has("url"));
            assertTrue(linkJson.getString("url").equals("http://www.appnexus.com"));
            assertTrue(linkJson.has("fallback_url"));
            assertTrue(linkJson.getString("fallback_url").equals("http://ib.adnxs.com/fallback"));
            assertNull(linkJson.getString("click_trackers"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void requestNativeFailure() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.NO_BID));
        adRequest.loadAd();
        Lock.pause(1000);
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertAdFailed(false);
    }


    @Test
    public void testCreativeId() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.anNative()));
        adRequest.loadAd();
        Lock.pause(1000);
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertEquals("47772560", response.getCreativeId());
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