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
package com.appnexus.opensdk.ut;

public class UTConstants {
    public static final String CSM = "csm";
    public static final String SSM = "ssm";
    public static final String RTB = "rtb";
    public static final String CSR = "csr";
    public static final String CSM_VIDEO = "csmvideo";
    public static final String UTF_8 = "UTF-8";
    public static final String AD_TYPE_BANNER = "banner";
    public static final String AD_TYPE_VIDEO = "video";
    public static final String AD_TYPE_NATIVE = "native";
    public static final String MRAID_JS_FILENAME = "mraid.js";
    public static final String EXTRAS_KEY_MRAID = "MRAID";
    public static final String EXTRAS_KEY_ORIENTATION = "ORIENTATION";

    // URL Constants below should never be directly accessed inside SDK.
    // Always use Settings.getWebViewBaseUrl() / Settings.getAdRequestUrl() / Settings.getCookieDomain()
    public static String REQUEST_BASE_URL_UT = "https://mediation.adnxs.com/ut/v3";
    public static String WEBVIEW_BASE_URL_UT = "https://mediation.adnxs.com/";
    public static String COOKIE_DOMAIN = "https://mediation.adnxs.com"; // There is only one cookie domain. No cookies for ib.adnxs-simple

    public static String REQUEST_BASE_URL_SIMPLE = "https://ib.adnxs-simple.com/ut/v3";
    public static String WEBVIEW_BASE_URL_SIMPLE = "https://ib.adnxs-simple.com/";

}
