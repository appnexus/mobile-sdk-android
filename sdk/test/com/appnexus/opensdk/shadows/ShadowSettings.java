/*
 *    Copyright 2016 APPNEXUS INC
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
package com.appnexus.opensdk.shadows;

import com.appnexus.opensdk.utils.Settings;

import org.robolectric.annotation.Implements;

@Implements(value = Settings.class, callThroughByDefault = true)
public class ShadowSettings {

    private static String COOKIE_DOMAIN = "https://mediation.adnxs.com";
    private static String BASE_URL = "https://mediation.adnxs.com/";
    private static String REQUEST_BASE_URL = "https://mediation.adnxs.com/ut/v3";
    private static String INSTALL_BASE_URL = "https://mediation.adnxs.com/install?";

    public static final long MEDIATED_NETWORK_TIMEOUT = 1000;

    public boolean test_mode = true;

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static String getRequestBaseUrl() {
        return REQUEST_BASE_URL;
    }

    public static String getInstallBaseUrl() {
        return INSTALL_BASE_URL;
    }

    public static String getCookieDomain() {
        return COOKIE_DOMAIN;
    }

    public static void setTestURL(String url) {
        BASE_URL = url;
        REQUEST_BASE_URL = url;
        COOKIE_DOMAIN = url;
    }


}
