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

package com.appnexus.opensdk.utils;

import android.content.Context;
import android.os.Build;
import android.os.Looper;
import android.webkit.WebView;

import java.lang.ref.WeakReference;
import java.util.Locale;

public class NewSettings {
    /**
     * Constants for pre-bid custom keywords
     */
    public static final String PREBID_KEY = "pb_prebid";
    public static final String ENCODED_URL_KEY = "pb_adurl_enc";
    public static final String PRICE_ONE_CENT_KEY = "pb_1c";
    public static final String PRICE_FIVE_CENTS_KEY = "pb_5c";
    public static final float FIVE_CENTS_RANGE = 0.05f;
    public static final String PRICE_TEN_CENTS_KEY = "pb_10c";
    public static final float TEN_CENTS_RANGE = 0.1f;
    public static final String PRICE_TWENTY_FIVE_CENTS_KEY = "pb_25c";
    public static final float TWENTY_FIVE_CENTS_RANGE = 0.25f;
    public static final String PRICE_ONE_DOLLAR_KEY = "pb_1d";
    public static final float DOLLAR_RANGE = 1f;
    public static final String PRICE_FIVE_DOLLARS_KEY = "pb_5d";
    public static final float FIVE_DOLLAR_RANGE = 5f;
    public static final String PRICE_TEN_DOLLARS_KEY = "pb_10d";
    public static final float TEN_DOLLAR_RANGE = 10f;

    /**
     * Constants for Universal Tag Request keys
     */
    public static final String BASEURL = "http://ib.adnxs.com/ut/v1";
    public static final String SIZE_WIDTH = "width";
    public static final String SIZE_HEIGHT = "height";
    public static final String TAGS = "tags";
    public static final String TAG_ID = "id";
    public static final String TAG_SIZES = "sizes";
    public static final String TAG_ALLOW_SMALLER_SIZES = "allow_smaller_sizes";
    public static final String TAG_ALLOWED_MEDIA_AD_TYPES = "ad_types";
    public static final String TAG_DISABLE_PSA = "disable_psa";
    public static final String TAG_PREBID = "prebid";
    public static final String USER = "user";
    public static final String USER_AGE = "age";
    public static final String USER_GENDER = "gender";
    public static final String USER_LANGUAGE = "language";
    public static final String DEVICE = "device";
    public static final String DEVICE_USERAGENT = "useragent";
    public static final String DEVICE_GEO = "geo";
    public static final String GEO_LAT = "lat";
    public static final String GEO_LON = "lng";
    public static final String GEO_AGE = "loc_age";
    public static final String GEO_PREC = "loc_precision";
    public static final String DEVICE_MAKE = "make";
    public static final String DEVICE_MODEL = "model";
    public static final String DEVICE_OS = "os";
    public static final String DEVICE_CARRIER = "carrier";
    public static final String DEVICE_CONNECTIONTYPE = "connectiontype";
    public static final String DEVICE_MCC = "mcc";
    public static final String DEVICE_MNC = "mnc";
    public static final String DEVICE_LMT = "limit_ad_tracking";
    public static final String DEVICE_DEVICE_ID = "device_id";
    public static final String DEVICE_DEVTIME = "devtime";
    public static final String DEVICE_ID_AAID = "aaid";
    public static final String APP = "app";
    public static final String APP_ID = "appid";
    public static final String KEYWORDS = "keywords";
    public static final String KEYVAL_KEY = "key";
    public static final String KEYVAL_VALUE = "value";

    /**
     * Constants for universal tag response parsing
     */
    public static final String BANNER = "banner";
    public static final String VAST = "video";
    public static final String CPM = "cpm";
    public static final String UT_URL = "ut_url";
    public static final String RESPONSE_TAG_ID = "tag_id";
    public static final String NO_BID = "nobid";
    public static final String AD_TYPE = "ad_type";
    public static final String AD = "ad";
    public static final long RESPONSE_EXPIRATION_TIME_MILLIS = 3600000; // one hour

    /**
     * Constants for device information
     */
    public static final String deviceMake = Build.MANUFACTURER;
    public static final String deviceModel = Build.MODEL;
    public static final String os = "android";
    public static final String version = "0.0";
    public static final String language = Locale.getDefault().getLanguage();
    public static final int HTTP_CONNECTION_TIMEOUT = 15000;

    static WeakReference<Context> weakContext;
    static String aaid = null;
    static String carrierName = null;
    static String app_id = null;
    static boolean limitAdTracking = false;
    static String useragent = null;
    static int mnc = -1;
    static int mcc = -1;

    static int locationDecimalDigits = -1;

    private static boolean initialzed = false;

    public static void init(final Context context) {
        if (!initialzed) {
            setContext(context);
            AdvertistingIDUtil.retrieveAndSetAAID(context);
            android.os.Handler mainThread = new android.os.Handler(Looper.getMainLooper());
            mainThread.post(new Runnable() {
                @Override
                public void run() {
                    WebView wv = new WebView(context);
                    setUseragent(wv.getSettings().getUserAgentString());
                }
            });
            initialzed = true;
        }
    }


    public static synchronized void setContext(Context context) {
        weakContext = new WeakReference<Context>(context);
    }

    public static synchronized Context getContext() {
        if (weakContext != null) {
            return weakContext.get();
        }
        return null;
    }

    public static synchronized String getAAID() {
        return aaid;
    }

    public static synchronized void setAAID(String aaid) {
        NewSettings.aaid = aaid;
    }

    public static synchronized String getCarrierName() {
        return carrierName;
    }

    public static synchronized void setCarrierName(String carrierName) {
        NewSettings.carrierName = carrierName;
    }

    public static synchronized String getAppID() {
        return app_id;
    }

    public static synchronized void setAppID(String app_id) {
        NewSettings.app_id = app_id;
    }

    public static synchronized boolean isLimitAdTracking() {
        return limitAdTracking;
    }

    public static synchronized void setLimitAdTracking(boolean limitAdTracking) {
        NewSettings.limitAdTracking = limitAdTracking;
    }

    public static synchronized String getUseragent() {
        return useragent;
    }

    public static synchronized void setUseragent(String useragent) {
        NewSettings.useragent = useragent;
    }

    public static synchronized int getMNC() {
        return mnc;
    }

    public static synchronized void setMNC(int mnc) {
        NewSettings.mnc = mnc;
    }

    public static synchronized int getMCC() {
        return mcc;
    }

    public static synchronized void setMCC(int mcc) {
        NewSettings.mcc = mcc;
    }

    public static synchronized int getLocationDecimalDigits() {
        return locationDecimalDigits;
    }

    public static synchronized void setLocationDecimalDigits(int locationDecimalDigits) {
        NewSettings.locationDecimalDigits = locationDecimalDigits;
    }

}
