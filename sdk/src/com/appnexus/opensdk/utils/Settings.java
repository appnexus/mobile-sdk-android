/*
 *    Copyright 2013 APPNEXUS INC
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

package com.appnexus.opensdk.utils;

import android.location.Location;
import android.os.Build;

import com.appnexus.opensdk.MediaType;
import com.appnexus.opensdk.R;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.TimeZone;

public class Settings {
    public String hidmd5 = null;
    public String hidsha1 = null;
    public String carrierName = null;
    public String aaid = null;
    public boolean limitTrackingEnabled = false;
    public boolean useHttps=false;

    public final String deviceMake = Build.MANUFACTURER;
    public final String deviceModel = Build.MODEL;

    public String app_id = null;

    public boolean test_mode = false;

    public boolean debug_mode = false; // This should always be false here.
    public String ua = null;
    public boolean first_launch;
    public final String sdkVersion = "3.4";

    public String mcc;
    public String mnc;
    public final String dev_timezone = TimeZone.getDefault().getDisplayName(true, TimeZone.SHORT);
    public final String language = Locale.getDefault().getLanguage();

    public boolean locationEnabled = true;
    public Location location = null;
    public int locationDecimalDigits = -1;

    public HashMap<String, String> externalMediationClasses = new HashMap<String, String>();
    private HashSet<String> invalidBannerNetworks = new HashSet<String>();
    private HashSet<String> invalidInterstitialNetworks = new HashSet<String>();
    private HashSet<String> invalidNativeNetworks = new HashSet<String>();

    // STATICS
    public static final int HTTP_CONNECTION_TIMEOUT = 15000;
    public static final int HTTP_SOCKET_TIMEOUT = 20000;

    public static final int FETCH_THREAD_COUNT = 4;

    public static final int DEFAULT_REFRESH = 30000; // Default banner refresh interval is 30 seconds
    public static final int MIN_REFRESH_MILLISECONDS = 15000;
    public static final int DEFAULT_INTERSTITIAL_CLOSE_BUTTON_DELAY = 10000;

    public static final long MEDIATED_NETWORK_TIMEOUT = 15000;

    public static final long NATIVE_AD_RESPONSE_EXPIRATION_TIME = 3600000; // an hour

    public static final int NATIVE_AD_VISIBLE_PERIOD_MILLIS = 1000;

    public static final int MIN_PERCENTAGE_VIEWED = 50;

    private static String COOKIE_DOMAIN = "http://mediation.adnxs.com";
    public static final String AN_UUID = "uuid2";
    private static String BASE_URL = "http://mediation.adnxs.com/";
    private static String REQUEST_BASE_URL = "http://mediation.adnxs.com/mob?";
    private static String INSTALL_BASE_URL = "http://mediation.adnxs.com/install?";
    private static String REQUEST_BASE_URL_UT_V2 = "http://mediation.adnxs.com/ut/v2";

    private static Settings settings_instance = null;

    public static Settings getSettings() {
        if (settings_instance == null) {
            settings_instance = new Settings();
            Clog.v(Clog.baseLogTag, Clog.getString(R.string.init));
        }
        return settings_instance;
    }

    private Settings() {

    }

    public void addInvalidNetwork(MediaType type, String network) {
        if (!StringUtil.isEmpty(network)) {
            switch (type) {
                case BANNER:
                    invalidBannerNetworks.add(network);
                    break;
                case INTERSTITIAL:
                    invalidInterstitialNetworks.add(network);
                    break;
                case NATIVE:
                    invalidNativeNetworks.add(network);
                    break;
            }
        }
    }

    public HashSet<String> getInvalidNetwork(MediaType type) {
        switch (type) {
            case BANNER:
                return invalidBannerNetworks;
            case INTERSTITIAL:
                return invalidInterstitialNetworks;
            case NATIVE:
                return invalidNativeNetworks;
        }
        return null;
    }

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

    public static String getCookieDomain(){
        return COOKIE_DOMAIN;
    }

}
