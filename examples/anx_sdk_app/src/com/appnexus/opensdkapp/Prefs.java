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

package com.appnexus.opensdkapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.appnexus.opensdk.AdView;
import com.appnexus.opensdk.utils.Clog;

public class Prefs {

    public static final String KEY_ADTYPE_IS_BANNER = "ADTYPE";
    public static final String KEY_ALLOW_PSAS = "ALLOW_PSAS";
    public static final String KEY_BROWSER_IS_INAPP = "BROWSER";
    public static final String KEY_PLACEMENT = "PLACEMENT";
    public static final String KEY_SIZE = "SIZE";
    public static final String KEY_REFRESH = "REFRESH";
    public static final String KEY_COLOR_HEX = "COLOR";
    public static final String KEY_MEMBERID = "MEMBERID";
    public static final String KEY_DONGLE = "DONGLE";
    public static final String KEY_GENDER = "GENDER";
    public static final String KEY_AGE = "AGE";
    public static final String KEY_ZIP = "ZIP";

    public static final String KEY_LAST_LOG_UPLOAD = "LAST_LOG_UPLOAD";

    // default values for all the settings
    public static final boolean DEF_ADTYPE_IS_BANNER = true;
    public static final boolean DEF_ALLOW_PSAS = true;
    public static final boolean DEF_BROWSER_IS_INAPP = true;
    public static final String DEF_PLACEMENT = "1326299";
    public static final String DEF_SIZE = "320x480";
    public static final String DEF_REFRESH = "Off";
    public static final String DEF_COLOR_HEX = "FF000000";
    public static final String DEF_MEMBERID = "";
    public static final String DEF_DONGLE = "";
    public static final String DEF_AGE = "";
    public static final String DEF_ZIP = "";
    public static final AdView.GENDER DEF_GENDER = AdView.GENDER.UNKNOWN;

    public static final long DEF_LAST_LOG_UPLOAD = 0;

    private SharedPreferences.Editor editor;

    public Prefs(Context context) {
        editor = getPreferences(context).edit();
    }

    private static SharedPreferences getPreferences(Context context) {
        return context.getApplicationContext().getSharedPreferences(
                Constants.PREFERENCES, Context.MODE_PRIVATE);
    }

    public static String getString(Context context, String key, String def) {
        try {
            return getPreferences(context).getString(key, def);
        } catch (ClassCastException e) {
            Clog.e(Constants.PREFS_TAG, "Prefs failed to getString", e);
            return def;
        }
    }

    public void writeString(String key, String value) {
        Clog.d(Constants.PREFS_TAG, key + ", " + value);
        editor.putString(key, value);
    }

    public static int getInt(Context context, String key, int def) {
        try {
            return getPreferences(context).getInt(key, def);
        } catch (ClassCastException e) {
            Clog.e(Constants.PREFS_TAG, "Prefs failed to getInt", e);
            return def;
        }
    }

    public void writeInt(String key, int value) {
        Clog.d(Constants.PREFS_TAG, key + ", " + value);
        editor.putInt(key, value);
    }

    public static boolean getBoolean(Context context, String key, boolean def) {
        try {
            return getPreferences(context).getBoolean(key, def);
        } catch (ClassCastException e) {
            Clog.e(Constants.PREFS_TAG, "Prefs failed to getBoolean", e);
            return def;
        }
    }

    public void writeBoolean(String key, boolean value) {
        Clog.d(Constants.PREFS_TAG, key + ", " + value);
        editor.putBoolean(key, value);
    }

    public static long getLong(Context context, String key, long def) {
        try {
            return getPreferences(context).getLong(key, def);
        } catch (ClassCastException e) {
            Clog.e(Constants.PREFS_TAG, "Prefs failed to getLong", e);
            return def;
        }
    }

    public void writeLong(String key, long value) {
        Clog.d(Constants.PREFS_TAG, key + ", " + value);
        editor.putLong(key, value);
    }

    @SuppressLint("NewApi")
    public void applyChanges() {
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            editor.apply();
        } else {
            editor.commit();
        }
    }

    /**
     * Convenience methods
     */

    public static String getAge(Context context){
        return getString(context, Prefs.KEY_AGE, Prefs.DEF_AGE);
    }

    public static String getZip(Context context){
        return getString(context, Prefs.KEY_ZIP, Prefs.DEF_ZIP);
    }

    public static int getGender(Context context){
        return getInt(context, Prefs.KEY_GENDER, Prefs.DEF_GENDER.ordinal());
    }

    public static boolean getAdType(Context context) {
        return getBoolean(context, Prefs.KEY_ADTYPE_IS_BANNER, Prefs.DEF_ADTYPE_IS_BANNER);
    }

    public static boolean getAllowPSAs(Context context) {
        return getBoolean(context, Prefs.KEY_ALLOW_PSAS, Prefs.DEF_ALLOW_PSAS);
    }

    public static boolean getBrowserInApp(Context context) {
        return getBoolean(context, Prefs.KEY_BROWSER_IS_INAPP, Prefs.DEF_BROWSER_IS_INAPP);
    }

    public static String getPlacementId(Context context) {
        return getString(context, Prefs.KEY_PLACEMENT, Prefs.DEF_PLACEMENT);
    }

    public static String getSize(Context context) {
        return getString(context, Prefs.KEY_SIZE, Prefs.DEF_SIZE);
    }

    public static String getRefresh(Context context) {
        return getString(context, Prefs.KEY_REFRESH, Prefs.DEF_REFRESH);
    }

    public static String getColor(Context context) {
        return getString(context, Prefs.KEY_COLOR_HEX, Prefs.DEF_COLOR_HEX);
    }

    public static String getMemberId(Context context) {
        return getString(context, Prefs.KEY_MEMBERID, Prefs.DEF_MEMBERID);
    }

    public static String getDongle(Context context) {
        return getString(context, Prefs.KEY_DONGLE, Prefs.DEF_DONGLE);
    }

    public static long getLastLogUpload(Context context) {
        return getLong(context, Prefs.KEY_LAST_LOG_UPLOAD, Prefs.DEF_LAST_LOG_UPLOAD);
    }
}
