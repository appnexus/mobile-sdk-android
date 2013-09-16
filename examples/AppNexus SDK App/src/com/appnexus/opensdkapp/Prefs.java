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

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {

    public static final String KEY_PLACEMENT = "PLACEMENT";
    public static final String KEY_COLOR_HEX = "COLOR";
    public static final String KEY_MEMBERID = "MEMBERID";
    public static final String KEY_DONGLE = "DONGLE";

    public static final String DEF_PLACEMENT = "000000";
    public static final String DEF_COLOR_HEX = "FF000000";
    public static final String DEF_MEMBERID = "";
    public static final String DEF_DONGLE = "";

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(
                Constants.PREFERENCES, Context.MODE_PRIVATE);
    }

    public static String getString(Context context, String key, String def) {
        SharedPreferences prefs = getPreferences(context);
        return prefs.getString(key, def);
    }

    public static boolean writeString(Context context, String key, String value) {
        SharedPreferences prefs = getPreferences(context);
        return prefs.edit().putString(key, value).commit();
    }
}
