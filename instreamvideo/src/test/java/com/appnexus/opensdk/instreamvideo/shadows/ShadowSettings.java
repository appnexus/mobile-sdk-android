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
package com.appnexus.opensdk.instreamvideo.shadows;

import com.appnexus.opensdk.utils.Settings;

import org.robolectric.annotation.Implements;

@Implements(value = Settings.class, callThroughByDefault = true)
public class ShadowSettings {

    private static String COOKIE_DOMAIN = "http://mediation.adnxs.com";
    private static String BASE_URL = "http://mediation.adnxs.com/";
    private static String REQUEST_BASE_URL = "http://mediation.adnxs.com/mob?";
    private static String INSTALL_BASE_URL = "http://mediation.adnxs.com/install?";
    private static String REQUEST_BASE_URL_UT_V2 = "http://mediation.adnxs.com/ut/v2";

    public static String getBaseUrl() {
        return Settings.getSettings().useHttps ? BASE_URL.replace("http:", "https:") : BASE_URL;
    }

    public static String getRequestBaseUrl() {
        return Settings.getSettings().useHttps ? REQUEST_BASE_URL.replace("http:", "https:") : REQUEST_BASE_URL;
    }

    public static String getRequestBaseUrlUTV2() {
        return Settings.getSettings().useHttps ? REQUEST_BASE_URL_UT_V2.replace("http:", "https:") : REQUEST_BASE_URL_UT_V2;
    }

    public static String getInstallBaseUrl() {
        return Settings.getSettings().useHttps ? INSTALL_BASE_URL.replace("http:", "https:") : INSTALL_BASE_URL;
    }

    public static String getCookieDomain() {
        return COOKIE_DOMAIN;
    }

    public static void setTestURL(String url) {
        BASE_URL = url;
        REQUEST_BASE_URL = url;
        COOKIE_DOMAIN = url;
        REQUEST_BASE_URL_UT_V2 = url;
    }


}
