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

public class Settings {
    public String hidmd5 = null;
    public String hidsha1 = null;
    public String carrierName = null;
    public String aaid = null;
    public boolean limitTrackingEnabled = false;
    @Deprecated
    public boolean useHttps=true;

    public final String deviceMake = Build.MANUFACTURER;
    public final String deviceModel = Build.MODEL;

    public String app_id = null;

    public boolean test_mode = false;

    public boolean debug_mode = false; // This should always be false here.
    public String ua = null;

    public final String sdkVersion = "7.8.1";

    public String mcc;
    public String mnc;
    public final String language = Locale.getDefault().getLanguage();

    public boolean omEnabled = true;

    public boolean locationEnabled = true;
    public long auctionTimeout = 0;
    public Location location = null;
    public int locationDecimalDigits = -1;

    public HashMap<String, String> externalMediationClasses = new HashMap<String, String>();
    public String countryCode;
    public String zip;
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

    public static final long NATIVE_AD_RESPONSE_EXPIRATION_TIME = 6 * 60 * 60 * 1000; // 6 hours
    public static final long NATIVE_AD_RESPONSE_EXPIRATION_TIME_CSM_CSR = 60 * 60 * 1000; // an hour
    public static final long NATIVE_AD_RESPONSE_EXPIRATION_TIME_TRIPLELIFT = 5 * 60 * 1000; // 5 minutes

    public static final long NATIVE_AD_ABOUT_TO_EXPIRE_INTERVAL_DEFAULT = 60 * 1000; // 1 minute

    /**
     * This variable can be modified to alter the interval in ms,
     * which will denote the time (in ms) that the user will be notified (with onAdAboutToExpire()) before the Ad is expired.
     * Default value is set to 60 seconds.
     */
    public static long NATIVE_AD_ABOUT_TO_EXPIRE_INTERVAL = NATIVE_AD_ABOUT_TO_EXPIRE_INTERVAL_DEFAULT;

    public static final int NATIVE_AD_VISIBLE_PERIOD_MILLIS = 1000;

    public static final int MIN_PERCENTAGE_VIEWED = 50;

    public static final int VIDEO_AUTOPLAY_PERCENTAGE = 50;

    private static String COOKIE_DOMAIN = "https://mediation.adnxs.com";
    public static final String AN_UUID = "uuid2";
    private static String BASE_URL = "https://mediation.adnxs.com/";
    private static String REQUEST_BASE_URL = "https://mediation.adnxs.com/mob?";
    private static String INSTALL_BASE_URL = "https://mediation.adnxs.com/install?";

    private static String VIDEO_HTML = "file:///android_asset/apn_vastvideo.html";

    private static Settings settings_instance = null;
    public boolean locationEnabledForCreative = true;

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
        return BASE_URL;
    }

    public static String getRequestBaseUrl() {
        return REQUEST_BASE_URL;
    }

    public static String getInstallBaseUrl() {
        return INSTALL_BASE_URL;
    }


    public static String getVideoHtmlPage() {
        return Settings.getSettings().debug_mode ? VIDEO_HTML.replace("apn_vastvideo.html", "apn_vastvideo.html?ast_debug=true") : VIDEO_HTML;
    }


    public static String getCookieDomain(){
        return COOKIE_DOMAIN;
    }

}
