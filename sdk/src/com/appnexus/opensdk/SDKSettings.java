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

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.appnexus.opensdk.tasksmanager.TasksManager;
import com.appnexus.opensdk.utils.AdvertisingIDUtil;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.StringUtil;
import com.appnexus.opensdk.viewability.ANOmidViewabilty;
import com.iab.omid.library.appnexus.Omid;

import java.util.Map;
import java.util.concurrent.Executor;

import static com.appnexus.opensdk.utils.Settings.countImpressionOn1pxRendering;

/**
 * Global static functions that apply to all SDK views and calls.
 */
public class SDKSettings {

    /**
     * This boolean is responsible for the execution of Ad request on the BGThread,
     * based on its value,
     * true - AdRequest is processed on BGThread
     * false - AdRequest is processed using AsyncTask
     * */
    private static Boolean useBackgroundThreads = false;

    /**
     * For internal use only
     * */
    private static boolean isOmidActivationDone = false, isUserAgentFetched = false, isAAIDFetched = false;

    // hide the constructor from javadocs
    private SDKSettings() {

    }

    /**
     * Sets the Android Advertising ID to be passed in the ad request.
     *
     * @param aaid                 the android advertising id value.
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
     *  An AppNexus disableAAIDUsage is a boolean value, which exclude the AAID field in ad request.
     *  Default value of disableAAIDUsage is set to false
     */
    public static boolean isAAIDUsageDisabled() {
        return Settings.getSettings().disableAAIDUsage;
    }

    /**
     * Sets true to exclude the AAID field in ad request else false.
     * @param disable whether to disable AAIDUsage or not. default is false
     */
    public static void disableAAIDUsage(boolean disable) {
        Settings.getSettings().disableAAIDUsage = disable;
    }

    /**
     Do not track flag. Set this to true/false if you have information in the app about user opt-out.
     If set to true, tracking cookies and AAID will be disabled for all future auctions.
     Default value is false.
     */
    public static void setDoNotTrack(boolean dnt) {
        Settings.getSettings().doNotTrack = dnt;
    }

    /**
     * Returns true if Do not track is enabled.
     * False otherwise.
     */
    public static boolean getDoNotTrack() {
        return Settings.getSettings().doNotTrack;
    }


    /**
     * Returns true if the ad server calls will include location information
     * or false otherwise.
     */
    public static boolean getLocationEnabled() {
        return Settings.getSettings().locationEnabled;
    }

    /**
     * Sets whether or not AdRequests should be executed in Test Mode.
     * Setting this to true will execute AdRequests in Test Mode.
     * This should be set to true only during development/testing.
     * Enabling Test Mode in production will result in unintended consequences and will impact Monetization of your app. Use with caution.
     *
     * default is false.
     * */
    public static void enableTestMode(boolean enabled) {
        Settings.getSettings().test_mode = enabled;
    }

    /**
     * Returns true if Test Mode is enabled
     * or false otherwise.
     */
    public static boolean isTestModeEnabled() {
        return Settings.getSettings().test_mode;
    }


    /**
     * The amount of time, in milliseconds, to wait for a bidder to respond to a bid request.
     *
     * @param auctionTimeout in milliseconds,  default is zero
     */
    public static void setAuctionTimeout(long auctionTimeout) {
        Settings.getSettings().auctionTimeout = auctionTimeout;
    }

    /**
     * Returns auctionTimeout in milliseconds
     */
    public static long getAuctionTimeout() {
        return Settings.getSettings().auctionTimeout;
    }

    /**
     * Set true to allow Open-Measurement for viewability and verification measurement for ads served
     * The Android version needs to be KITKAT(4.4) or above for enabling the Open Measurement
     *
     * @param enabled to enable OMSDK. default is true for Android KITKAT(4.4) and above
     */
    public static void setOMEnabled(boolean enabled) {
        Settings.getSettings().omEnabled = enabled;
    }

    /**
     * Return true only if the Android Version is KITKAT(4.4) or above
     * and if the ad server calls allow to include Open-Measurement for viewability and verification information
     * or false otherwise.
     */
    public static boolean getOMEnabled() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Clog.e(Clog.omidLogTag, Clog.getString(R.string.apn_omid_enable_failure));
            return false;
        }
        return Settings.getSettings().omEnabled;
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
        } else {
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
    public static int getLocationDecimalDigits() {
        return Settings.getSettings().locationDecimalDigits;
    }

    /**
     * Overrides the SDKs automatic location retrieval.
     * The countryCode will be passed to request parameters only if it is 2 characters long
     *
     * @param countryCode The countryCode value to use in the ad call. To reset, pass null.
     */
    public static void setGeoOverrideCountryCode(String countryCode) {
        Settings.getSettings().countryCode = countryCode;
    }

    /**
     * Overrides the SDKs automatic location retrieval.
     * The zipCode will be passed to request parameters only if it isn't empty
     *
     * @param zipCode The zipCode value to use in the ad call. To reset, pass null.
     */
    public static void setGeoOverrideZipCode(String zipCode) {
        Settings.getSettings().zip = zipCode;
    }

    /**
     * Returns the countryCode value that is set using {setGeoOverrideCountryCode}
     * */
    public static String getGeoOverrideCountryCode() {
        return Settings.getSettings().countryCode;
    }

    /**
     * Returns the zipCode value that is set using {setGeoOverrideZipCode}
     * */
    public static String getGeoOverrideZipCode() {
        return Settings.getSettings().zip;
    }


    /**
     * Register a mapping for an external mediation class.
     *
     * @param className         the end class name.
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
     * @param useHttps whether to enable Https or not.
     * @deprecated The SDK uses Https by default.
     * This API does not bring any change.
     */
    @Deprecated
    public static void useHttps(boolean useHttps) {
        return;
    }

    /**
     * @deprecated The SDK uses Https by default.
     * This API always returns true.
     */
    @Deprecated
    public static boolean isHttpsEnabled() {
        return true;
    }

    /**
     * Sets whether or not location (latitude, longitude)
     * permission alert will be shown to the user when called by the Creative.
     *
     * @param enable whether to enable location permission alert or not. default is true
     */
    public static void setLocationEnabledForCreative(boolean enable) {
        Settings.getSettings().locationEnabledForCreative = enable;
    }

    /**
     * Returns false if the locationEnabledForCreative is set to false
     * or true otherwise
     */
    public static boolean isLocationEnabledForCreative() {
        return Settings.getSettings().locationEnabledForCreative;
    }


    /**
     * Specifies a string that corresponds to the Publisher(First Party) User ID for the current application user.
     * @param publisherUserId
     */
    public static void setPublisherUserId(String publisherUserId){
        Settings.getSettings().publisherUserId = publisherUserId;
    }

    /**
     * Returns the Publisher User Id for the current application user, initially added using {@link #setPublisherUserId(String)}
     * @@return publisherUserId as String.
     */
    public static String getPublisherUserId() {
        return Settings.getSettings().publisherUserId;
    }

    /**
     * A Map containing objects that hold External UserId parameters for the current application user.
     * @param externalUserIds
     */
    public static void setExternalUserIds(Map<ANExternalUserIdSource,String> externalUserIds){
         Settings.getSettings().externalUserIds = externalUserIds;
    }

    /**
     * Returns the Map that hold External UserId parameters for the current application user, initially added using {@link #setExternalUserIds(Map<ANExternalUserIdSource,String>)}
     * @@return externalUserIds as Map.
     */
    public static Map<ANExternalUserIdSource,String> getExternalUserIds() {
        return Settings.getSettings().externalUserIds;
    }


    /**
     * Returns AppNexus SDK Version
     */
    public static String getSDKVersion() {
        return Settings.getSettings().sdkVersion;
    }

    private static Executor clientExecutor = null;

    public static void setExternalExecutor(Executor clientExecutor) {
        SDKSettings.clientExecutor = clientExecutor;
    }

    public static Executor getExternalExecutor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && clientExecutor == null) {
            return AsyncTask.THREAD_POOL_EXECUTOR;
        }
        return clientExecutor;
    }

    /**
     * Enables or disables the scroll protection of the web view of the advertisement.
     *
     * It is enabled by default.
     *
     * @param preventWebViewScrolling
     * true - Default value, the web view will stay at the top of the page (0, 0) when the scroll position is set.
     * false - The web view will use the default behaviour and allow scrolling.
     */
    public static void setPreventWebViewScrolling(boolean preventWebViewScrolling) {
        Settings.getSettings().preventWebViewScrolling = preventWebViewScrolling;
    }

    /**
     * @return boolean which states if the scrolling of the advertisement web view is prevented or not.
     */
    public static boolean getPreventWebViewScrolling() {
        return Settings.getSettings().preventWebViewScrolling;
    }

    /**
     * @return boolean which states if the BackgroundThreading is enabled or not
     * */
    public static boolean isBackgroundThreadingEnabled() {
        return useBackgroundThreads;
    }

    /**
     * This API can be used to process Ad request on the BGThread,
     * @param enable
     * true - For processing the AdRequest on BGThread
     * false - For processing the AdRequest using AsyncTask
     * default is set to false.
     * */
    public static void enableBackgroundThreading(boolean enable) {
        useBackgroundThreads = enable;
    }

    public static void init(Context context, final InitListener listener) {
        init(context, listener, true, true);
    }

    /**
     * This is an overloaded init method which provides flexibility to enable/disable booleans for activateOmid, fetcbUserAgent and fetchAAID.
     * @param fetchUserAgent enable / disable fetching of User Agent.
     * @param fetchAAID enable / disable fetching of AAID.
     * */
    public static void init(final Context context, final InitListener listener, final boolean fetchUserAgent, final boolean fetchAAID) {
        // Store the UA in the settings
        isOmidActivationDone = Omid.isActive();
        isUserAgentFetched = !fetchUserAgent || !StringUtil.isEmpty(Settings.getSettings().ua);
        isAAIDFetched = !fetchAAID || !StringUtil.isEmpty(getAAID());
        if (!isOmidActivationDone) {
            ANOmidViewabilty.getInstance().activateOmidAndCreatePartner(context.getApplicationContext());
            isOmidActivationDone = true;
            onInitFinished(listener);
        }
        if (!isUserAgentFetched) {
            TasksManager.getInstance().executeOnMainThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Settings.getSettings().ua = new WebView(context).getSettings()
                                .getUserAgentString();
                        Clog.v(Clog.baseLogTag,
                                Clog.getString(R.string.ua, Settings.getSettings().ua));
                        isUserAgentFetched = true;
                    } catch (Exception e) {
                        // Catches PackageManager$NameNotFoundException for webview
                        Settings.getSettings().ua = "";
                        Clog.e(Clog.baseLogTag, " Exception: " + e.getMessage());
                    }
                    onInitFinished(listener);
                }
            });
        }
        Clog.setErrorContext(context.getApplicationContext());
        /*
         * We have a check where it executes the block only when getAAID() is empty,
         * thus if we setAAID() prior to calling SDKSettings.init() it won't run following block.
         */
        if (!isAAIDFetched) {
            AdvertisingIDUtil.retrieveAndSetAAID(context, new InitListener() {
                @Override
                public void onInitFinished() {
                    isAAIDFetched = true;
                    SDKSettings.onInitFinished(listener);
                }
            });
        }

        // For the cases where Omid is already activated and UserAgent & AAID are already fetched, onInitFinished must be triggered.
        onInitFinished(listener);
    }

    private static void onInitFinished(final InitListener listener) {
        if (listener != null && isOmidActivationDone && isUserAgentFetched && isAAIDFetched) {
            TasksManager.getInstance().executeOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onInitFinished();
                }
            });
        }
    }

    public interface InitListener {
        void onInitFinished();
    }

    /**
     * @return boolean that states the value of countImpressionOn1pxRendering
     * set by using {@link #setCountImpressionOn1pxRendering(boolean)}. Default is false.
     * */
    public static boolean getCountImpressionOn1pxRendering() {
        return countImpressionOn1pxRendering;
    }

    /**
     * To enable the Impression counting on 1px display
     * @param enable set true to enable, false to disable. Default is false.
     * */
    public static void setCountImpressionOn1pxRendering(boolean enable) {
        countImpressionOn1pxRendering = enable;
    }


    /**
     * @deprecated This will be removed in future releases. This is introduced just a fail safe kill switch for initial rollout. No Alternative.
     * To allow/disallow using ib.adnxs-simple.com domain for Ad Requests.
     * @param allow set true to enable, false to disable. Default is true.
     * */
    public static void setAllowUsingSimpleDomain(boolean allow) {
        Settings.getSettings().simpleDomainUsageAllowed = allow;
    }

}
