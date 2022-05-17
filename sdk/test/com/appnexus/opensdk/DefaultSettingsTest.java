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

import com.appnexus.opensdk.utils.Settings;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertNull;

@Config(sdk = 21)
@RunWith(RobolectricTestRunner.class)
public class DefaultSettingsTest extends BaseRoboTest {

    @Before
    public void setup() {
        SDKSettings.setExternalExecutor(null);
    }

    @After
    public void tearDown() {
        Settings.getSettings().deviceAccessAllowed = true; // Reset to defaultduring setup
    }

    @Test
    public void testDefaultStaticValues() {
        assertEquals(15000, Settings.HTTP_CONNECTION_TIMEOUT);
        assertEquals(20000, Settings.HTTP_SOCKET_TIMEOUT);
        assertEquals(4, Settings.FETCH_THREAD_COUNT);
        assertEquals(30000, Settings.DEFAULT_REFRESH);
        assertEquals(15000, Settings.MIN_REFRESH_MILLISECONDS);
        assertEquals(10000, Settings.DEFAULT_INTERSTITIAL_CLOSE_BUTTON_DELAY);
        assertEquals(15000, Settings.MEDIATED_NETWORK_TIMEOUT);
        assertEquals(21600000, Settings.NATIVE_AD_RESPONSE_EXPIRATION_TIME);
        assertEquals(1000, Settings.NATIVE_AD_VISIBLE_PERIOD_MILLIS);
        assertEquals(50, Settings.MIN_PERCENTAGE_VIEWED);
        assertEquals("https://mediation.adnxs.com", Settings.getCookieDomain());
        assertEquals("uuid2", Settings.AN_UUID);
        assertEquals("https://mediation.adnxs.com/", Settings.getWebViewBaseUrl());
        assertEquals("https://mediation.adnxs.com/ut/v3", Settings.getAdRequestUrl());
    }

    @Test
    public void testDeviceAccessConsentFalseStaticValues() {
        Settings.getSettings().deviceAccessAllowed = false;
        assertEquals("https://mediation.adnxs.com", Settings.getCookieDomain()); // No seperate cookie domain
        assertEquals("https://ib.adnxs-simple.com/", Settings.getWebViewBaseUrl());
        System.out.println("Setting WebView Base URL"+Settings.getWebViewBaseUrl());
        assertEquals("https://ib.adnxs-simple.com/ut/v3", Settings.getAdRequestUrl());
        System.out.println("REQUEST Request URL"+Settings.getAdRequestUrl());
    }

    @Test
    public void testCheckIntentIsCachedOrNot(){
        assertNull(Settings.getCachedIntentForAction("ACTION_VIEW"));
        Settings.cacheIntentForAction(true, "ACTION_VIEW");
        assertEquals( Settings.getCachedIntentForAction("ACTION_VIEW"),  Boolean.TRUE);
        assertNull(Settings.getCachedIntentForAction("ACTION_LAUNCH"));
        Settings.cacheIntentForAction(false, "ACTION_DIAL");
        assertEquals( Settings.getCachedIntentForAction("ACTION_DIAL"),  Boolean.FALSE);
    }
}
