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

import com.appnexus.opensdk.util.RoboelectricTestRunnerWithResources;
import com.appnexus.opensdk.utils.Settings;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;

@Config(constants = BuildConfig.class, sdk = 21)
@RunWith(RoboelectricTestRunnerWithResources.class)
public class DefaultSettingsTest {

    @Before
    public void setup() {
    }

    @After
    public void tearDown() {
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
        assertEquals(3600000, Settings.NATIVE_AD_RESPONSE_EXPIRATION_TIME);
        assertEquals(1000, Settings.NATIVE_AD_VISIBLE_PERIOD_MILLIS);
        assertEquals(50, Settings.MIN_PERCENTAGE_VIEWED);
        assertEquals("http://mediation.adnxs.com", Settings.getCookieDomain());
        assertEquals("uuid2", Settings.AN_UUID);
        assertEquals("http://mediation.adnxs.com/", Settings.getBaseUrl());
        assertEquals("http://mediation.adnxs.com/mob?", Settings.getRequestBaseUrl());
        assertEquals("http://mediation.adnxs.com/install?", Settings.getInstallBaseUrl());
    }
}
