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

import android.os.Build;
import com.appnexus.opensdk.R;

import java.util.Locale;
import java.util.TimeZone;

public class Settings {
    public String hidmd5 = null;
    public String hidsha1 = null;
    public String carrierName = null;
    public String aaid = null;
    public boolean limitTrackingEnabled = false;

    public final String deviceMake = Build.MANUFACTURER;
    public final String deviceModel = Build.MODEL;

    public String app_id = null;

    public boolean test_mode = false;
    public String ua = null;
    public boolean first_launch;
    public final String sdkVersion = "1.14";

    public String mcc;
    public String mnc;
    public final String dev_timezone = TimeZone.getDefault().getID();
    public final String language = Locale.getDefault().getLanguage();
    public boolean locationEnabled = true;

    public final int HTTP_CONNECTION_TIMEOUT = 15000;
    public final int HTTP_SOCKET_TIMEOUT = 20000;

    public final int FETCH_THREAD_COUNT = 4;

    public final int MIN_REFRESH_MILLISECONDS = 15000;
    public final int DEFAULT_INTERSTITIAL_CLOSE_BUTTON_DELAY = 10000;

    public final long MEDIATED_NETWORK_TIMEOUT = 15000;

    public final String COOKIE_DOMAIN = "http://mediation.adnxs.com";
    public final String AN_UUID = "uuid2";
    public final String BASE_URL = "http://mediation.adnxs.com/";
    public final String REQUEST_BASE_URL = "http://mediation.adnxs.com/mob?";
    public final String INSTALL_BASE_URL = "http://mediation.adnxs.com/install?";

    // STATICS
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

    /**
     * Sets the Android Advertising ID to be passed in the ad request.
     *
     * @param aaid the android advertising id value.
     * @param limitTrackingEnabled whether limitTracking is enabled or not.
     */
    public static void setAAID(String aaid, boolean limitTrackingEnabled) {
        getSettings().aaid = aaid;
        getSettings().limitTrackingEnabled = limitTrackingEnabled;
    }

    /**
     * Sets whether or not location (latitude, longitude) is retrieved and
     * passed in the ad request. This includes whether location is sent to any
     * 3rd party networks.
     *
     * @param enabled whether to enable location or not. default is true
     */
    public static void setLocationEnabled(boolean enabled) {
        getSettings().locationEnabled = enabled;
    }

    /**
     * Returns true if the ad server calls will include location information
     * or false otherwise. The
     */
    public static boolean getLocationEnabled() {
        return getSettings().locationEnabled;
    }
}
