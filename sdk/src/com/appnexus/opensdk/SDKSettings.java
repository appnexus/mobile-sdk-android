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

package com.appnexus.opensdk;

import android.location.Location;

import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;

/**
 * Global static functions that apply to all SDK views and calls.
 */
public class SDKSettings {

    // hide the constructor from javadocs
    private SDKSettings() {

    }

    /**
     * Sets the Android Advertising ID to be passed in the ad request.
     *
     * @param aaid the android advertising id value.
     * @param limitTrackingEnabled whether limitTracking is enabled or not.
     */
    public static void setAAID(String aaid, boolean limitTrackingEnabled) {
        Settings.getSettings().aaid = aaid;
        Settings.getSettings().limitTrackingEnabled = limitTrackingEnabled;
    }

    /**
     * Retrieve the current Android Advertising ID to be reported to the ad
     * server. If the value is null, then either a non-null value has not
     * been passed to {@link #setAAID}
     *
     * @return The AAID to be passed in the ad request
     */
    public static String getAAID() {
        return Settings.getSettings().aaid;
    }

    /**
     * Sets whether or not location (latitude, longitude) is retrieved and
     * passed in the ad request. This includes whether location is sent to any
     * 3rd party networks.
     *
     * @param enabled whether to enable location or not. default is true
     */
    public static void setLocationEnabled(boolean enabled) {
        Settings.getSettings().locationEnabled = enabled;
    }

    /**
     * Returns true if the ad server calls will include location information
     * or false otherwise.
     */
    public static boolean getLocationEnabled() {
        return Settings.getSettings().locationEnabled;
    }

    /**
     * Retrieve the current location reported to the ad server.
     * If the value is null either location has not yet been retrieved
     * or location reporting has been disabled via
     * {@link #setLocationEnabled(boolean)}
     *
     * @return The location used in the last ad call.
     */
    public static Location getLocation() {
        return Settings.getSettings().location;
    }

    /**
     * Overrides the SDKs automatic location retrieval.
     * If the passed in location value is not null and location is enabled,
     * then the passed in location value will be sent to the ad server.
     * If the location parameter is null then the automatic location retrieval
     * will be used if and only if {@link #getLocationEnabled()}.
     *
     * @param location The location value to use in the ad call (may be null)
     */
    public static void setLocation(Location location) {
        if (getLocationEnabled()) {
            if (getLocationDecimalDigits() != -1 && location != null) {
                double power = Math.pow(10, getLocationDecimalDigits());
                location.setLatitude(Math.round(location.getLatitude() * power) / power);
                location.setLongitude(Math.round(location.getLongitude() * power) / power);
            }
            Settings.getSettings().location = location;
        }
        else {
            Settings.getSettings().location = null;
        }
    }

    /**
     * Sets the number of digits after the decimal of the latitude and longitude.
     * It will only be applied if {@link #getLocationEnabled()}.
     * Maximum of precision is 6, which means less than a foot.
     *
     * @param digitsAfterDecimal The digits
     */
    public static void setLocationDecimalDigits(int digitsAfterDecimal) {
        if (digitsAfterDecimal > 6) {
            Settings.getSettings().locationDecimalDigits = 6;
            Clog.w(Clog.baseLogTag, "Out of range input " + digitsAfterDecimal + ", set location digits after decimal to maximum 6");
        } else if (digitsAfterDecimal >= -1) {
            Settings.getSettings().locationDecimalDigits = digitsAfterDecimal;
        } else {
            Settings.getSettings().locationDecimalDigits = -1;
            Clog.w(Clog.baseLogTag, "Negative input " + digitsAfterDecimal + ", set location digits after decimal to default");
        }
    }

    /**
     * Returns the number of digits after decimal of latitude and longitude.
     * If returns -1, it indicates that full resolution is used.
     *
     * @return The digits after decimal of latitude and longitude
     */
    public static int getLocationDecimalDigits(){
        return Settings.getSettings().locationDecimalDigits;
    }

    /**
     * Register a mapping for an external mediation class.
     *
     * @param className the end class name.
     * @param customAdaptorName the intermediary class name. The intermediate
     *                          class needs to have a constructor that
     *                          takes a single String parameter.
     */
    static void registerExternalMediationClass(String className, String customAdaptorName) {
        Settings.getSettings().externalMediationClasses.put(className, customAdaptorName);
    }

    /**
     * Unregister a mapping for an external mediation class.
     *
     * @param className the end class name.
     */
    static void unregisterExternalMediationClass(String className) {
        Settings.getSettings().externalMediationClasses.put(className, null);
    }

    /**
     * Enable HTTPS in the SDK if set to true.
     *
     * @param useHttps whether to enable Https or not. default is false
     */

    public static void useHttps(boolean useHttps){
        Settings.getSettings().useHttps=useHttps;
    }

    /**
     * Returns true if the Https is enabled for the ad calls
     * or false otherwise.
     */
    public static boolean isHttpsEnabled() {
        return Settings.getSettings().useHttps;
    }
}
