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
import com.appnexus.opensdk.shadows.ShadowSettings;
import com.appnexus.opensdk.shadows.ShadowWebSettings;
import com.appnexus.opensdk.ut.UTConstants;
import com.appnexus.opensdk.utils.Settings;
import com.squareup.okhttp.mockwebserver.MockResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowWebView;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@Config(sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class,
                ShadowWebView.class, ShadowWebSettings.class, ShadowSettings.class, ShadowLog.class})
@RunWith(RobolectricTestRunner.class)
public class ANAdResponseInfoNativeTest extends BaseNativeTest {

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
    public void requestNativeSuccessAdResponseInfo() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.anNative()));
        assertNull(response);
        executeNativeRequest();
        assertNotNull(response.getAdResponseInfo());
        assertEquals(response.getAdResponseInfo().getAdType(), AdType.NATIVE);
        assertEquals(response.getAdResponseInfo().getCreativeId(), "47772560");
        assertEquals(response.getAdResponseInfo().getTagId(), "123456");
        assertEquals(response.getAdResponseInfo().getBuyMemberId(), 958);
        assertEquals(response.getAdResponseInfo().getContentSource(), UTConstants.RTB);
        assertEquals(response.getAdResponseInfo().getNetworkName(), "");
        assertEquals(response.getAdResponseInfo().getAuctionId(), "123456789");
        assertEquals(response.getAdResponseInfo().getCpm(), 0.000010);
        assertEquals(response.getAdResponseInfo().getCpmPublisherCurrency(), 0.000010);
        assertEquals(response.getAdResponseInfo().getPublisherCurrencyCode(), "$");

    }

    @Test
    public void requestNativeNoBidAdResponseInfo() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.NO_BID));
        assertNull(adResponseInfo);
        executeNativeRequest();
        assertNotNull(adResponseInfo);
        assertEquals(adResponseInfo.getAdType(), null);
        assertEquals(adResponseInfo.getCreativeId(), "");
        assertEquals(adResponseInfo.getTagId(), "123456789");
        assertEquals(adResponseInfo.getBuyMemberId(), 0);
        assertEquals(adResponseInfo.getContentSource(), "");
        assertEquals(adResponseInfo.getNetworkName(), "");
        assertEquals(adResponseInfo.getAuctionId(), "3552547938089377051000000");
        assertEquals(adResponseInfo.getCpm(), 0d);
        assertEquals(adResponseInfo.getCpmPublisherCurrency(), 0d);
        assertEquals(adResponseInfo.getPublisherCurrencyCode(), "");
    }

    @Test
    public void requestNativeBlankAdResponseInfo() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));
        assertNull(adResponseInfo);
        executeNativeRequest();
        assertNull(adResponseInfo);
    }

    public void executeNativeRequest() {
        adRequest.loadAd();
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
    }


    @Override
    public void tearDown() {
        super.tearDown();
        if (response != null) {
            response.destroy();
        }
    }
}
