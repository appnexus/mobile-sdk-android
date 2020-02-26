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


import com.appnexus.opensdk.mocks.MockFBNativeBannerAdResponse;
import com.appnexus.opensdk.shadows.ShadowAsyncTaskNoExecutor;
import com.appnexus.opensdk.shadows.ShadowSettings;
import com.appnexus.opensdk.shadows.ShadowWebSettings;
import com.appnexus.opensdk.ut.UTConstants;
import com.appnexus.opensdk.util.Lock;
import com.appnexus.opensdk.utils.Settings;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.mockwebserver.Dispatcher;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowWebView;

import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

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
        NativeAdRequestListener listener = new NativeAdRequestListener() {
            @Override
            public void onAdLoaded(NativeAdResponse response) {
                ((MockFBNativeBannerAdResponse) response).logImpression();
                ((MockFBNativeBannerAdResponse) response).clickAd();
            }

            @Override
            public void onAdFailed(ResultCode errorcode) {
            }
        };
        NativeAdRequestListener listenerSpy = spy(listener);
        adRequest.setListener(listenerSpy);
        adRequest.loadAd();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        verify(listenerSpy).onAdLoaded(any(MockFBNativeBannerAdResponse.class));
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
        NativeAdRequestListener adListener = mock(NativeAdRequestListener.class);
        adRequest.setListener(adListener);
        adRequest.loadAd();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        verify(adListener).onAdLoaded(any(MockFBNativeBannerAdResponse.class));
    }

    @Test
    public void requestNativeGetsCSRNofillThenMediation() {
        server.enqueue(new MockResponse().setBody(TestResponsesUT.csrNoFillThenMediationSuccessfull()).setResponseCode(200));
        NativeAdRequestListener adListener = mock(NativeAdRequestListener.class);
        adRequest.setListener(adListener);
        adRequest.loadAd();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        verify(adListener).onAdLoaded(any(BaseNativeAdResponse.class));
    }

    @Test
    public void requestNativeCSRNofill() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.csrNativeNofill()));
        NativeAdRequestListener adListener = mock(NativeAdRequestListener.class);
        adRequest.setListener(adListener);
        adRequest.loadAd();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        verify(adListener).onAdFailed(ResultCode.UNABLE_TO_FILL);
    }

    @Test
    public void requestNativeCSRSuccessful() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.csrNativeSuccessful()));
        NativeAdRequestListener adListener = mock(NativeAdRequestListener.class);
        adRequest.setListener(adListener);
        adRequest.loadAd();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        verify(adListener).onAdLoaded(any(MockFBNativeBannerAdResponse.class));
    }

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
