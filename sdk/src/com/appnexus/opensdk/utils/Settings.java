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
import android.webkit.WebView;
import com.appnexus.opensdk.ANUserId;
import com.appnexus.opensdk.BuildConfig;
import com.appnexus.opensdk.MediaType;
import com.appnexus.opensdk.R;
import com.appnexus.opensdk.ut.UTConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class Settings {

    /**
     * Fire Impression
     * DEFAULT - When the Ad is displayed
     * ON_LOAD - When content is loaded on the WebView
     * ONE_PX - When 1px of content is displayed
     * LAZY_LOAD - When the content is loaded on the Webview (LazyLoad is enabled)
     * */
    public enum ImpressionType {
        BEGIN_TO_RENDER, // When the Ad begin to render
        VIEWABLE_IMPRESSION,  // When 1px of Ad content is visible on screen
    }

    public String hidmd5 = null;
    public String hidsha1 = null;
    public String carrierName = null;
    public String aaid = null;
    public boolean limitTrackingEnabled = false;
    // Caches the value of DeviceAccessConsent for each request and is updated every time a request is made.
    // Use this value instead of calling and fetching from shared preference each time.
    public boolean deviceAccessAllowed = true;

    @Deprecated
    public boolean useHttps=true;

    public final String deviceMake = Build.MANUFACTURER;
    public final String deviceModel = Build.MODEL;

    public String app_id = null;

    public boolean test_mode = false;

    public boolean debug_mode = false; // This should always be false here.
    public String ua = null;

    public final String sdkVersion = "8.6";
//            BuildConfig.VERSION_NAME;


    public String mcc;
    public String mnc;
    public final String language = Locale.getDefault().getLanguage();

    public boolean omEnabled = true;

    public boolean locationEnabled = true;
    public long auctionTimeout = 0;
    public Location location = null;
    public int locationDecimalDigits = -1;

    public boolean preventWebViewScrolling = true;

    public boolean disableAAIDUsage = false;

    public boolean doNotTrack = false;

    public HashMap<String, String> externalMediationClasses = new HashMap<String, String>();
    public String countryCode;
    public String zip;
    private HashSet<String> invalidBannerNetworks = new HashSet<String>();
    private HashSet<String> invalidInterstitialNetworks = new HashSet<String>();
    private HashSet<String> invalidNativeNetworks = new HashSet<String>();

    public String publisherUserId = "";
    public List<ANUserId> userIds = new ArrayList<>();

    /**
     * @deprecated
     * This feature flag is responsible for turning on/off  ib.adnxs-simple.com domain usage.
     * By default this is true. This feature flag will be removed in future releases. This is introduced just as a failsafe kill switch.
     * */
    public static boolean simpleDomainUsageAllowed = true;

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
    public static final long NATIVE_AD_RESPONSE_EXPIRATION_TIME_MSAN = 10 * 60 * 1000; // 10 minutes
    public static final long NATIVE_AD_RESPONSE_EXPIRATION_TIME_INDEX = 5 * 60 * 1000; // 5 minutes
    public static final long NATIVE_AD_RESPONSE_EXPIRATION_TIME_INMOBI = 55 * 60 * 1000; // 55 minutes

    public static final long NATIVE_AD_ABOUT_TO_EXPIRE_INTERVAL_DEFAULT = 60 * 1000; // 1 minute

    /**
     * This variable can be modified to alter the interval in ms,
     * which will denote the time (in ms) that the user will be notified (with onAdAboutToExpire()) before the Ad is expired.
     * Default value is set to 60 seconds.
     */
    public static long NATIVE_AD_ABOUT_TO_EXPIRE_INTERVAL = NATIVE_AD_ABOUT_TO_EXPIRE_INTERVAL_DEFAULT;

    public static final int NATIVE_AD_VISIBLE_PERIOD_MILLIS = 1000;

    public static final int MIN_PERCENTAGE_VIEWED = 50;

    public static final int MIN_AREA_VIEWED_FOR_1PX = 1;

    public static final int VIDEO_AUTOPLAY_PERCENTAGE = 50;

    public static final String AN_UUID = "uuid2";

    private static String VIDEO_HTML = "file:///android_asset/apn_vastvideo.html";

    private static Settings settings_instance = null;
    public boolean locationEnabledForCreative = true;

    /**
     * This map is responsible for storing the installed packages for given Action,
     */
    private static Map<String, Boolean> hasIntentMap = new HashMap();

    private static List<WebView> cachedAdWebView = new CopyOnWriteArrayList();

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

    // Returns ib.adnxs-simple.com if we do not have deviceAccessConsent as per GDPR or when doNotTrack is turned on by publisher
    public static String getWebViewBaseUrl() {
        if ((!Settings.getSettings().deviceAccessAllowed || Settings.getSettings().doNotTrack) && Settings.getSettings().simpleDomainUsageAllowed) {
            return UTConstants.WEBVIEW_BASE_URL_SIMPLE;
        }
        return UTConstants.WEBVIEW_BASE_URL_UT;
    }

    // Returns ib.adnxs-simple.com if we do not have deviceAccessConsent as per GDPR or when doNotTrack is turned on by publisher
    public static String getAdRequestUrl() {
        if ((!Settings.getSettings().deviceAccessAllowed || Settings.getSettings().doNotTrack) && Settings.getSettings().simpleDomainUsageAllowed) {
            return UTConstants.REQUEST_BASE_URL_SIMPLE;
        }
        return UTConstants.REQUEST_BASE_URL_UT;
    }

    public static String getVideoHtmlPage() {
        return Settings.getSettings().debug_mode ? VIDEO_HTML.replace("apn_vastvideo.html", "apn_vastvideo.html?ast_debug=true") : VIDEO_HTML;
    }

    // There is only one cookie domain
    public static String getCookieDomain(){
        return UTConstants.COOKIE_DOMAIN;
    }


    /**
     * To cache this intent action .
     * @param hasIntent
     * @param action
     */
    public static void cacheIntentForAction(boolean hasIntent, String action) {
        hasIntentMap.put(action,hasIntent);
    }

    /**
     * To check the intent action is cached or not.
     * @param action
     */
    public static Boolean getCachedIntentForAction(String action) {
        return hasIntentMap.containsKey(action)? hasIntentMap.get(action):null;
    }

    /**
     * To check the intent hashmap is empty or not.
     */
    public static Boolean isIntentMapAlreadyCached() {
        return hasIntentMap.isEmpty();
    }

    public List<WebView> getCachedAdWebView() {
        return cachedAdWebView;
    }

    public void setCachedAdWebView(List<WebView> cachedAdWebView) {
        Settings.cachedAdWebView = cachedAdWebView;
    }
}
