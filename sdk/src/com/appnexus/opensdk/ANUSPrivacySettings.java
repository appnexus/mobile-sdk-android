/*
 *    Copyright 2019 APPNEXUS INC
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
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;


/**
 *  USPrivacy Settings class.
 */
public class ANUSPrivacySettings {

    private static final String AN_USPRIVACY_STRING = "ANUSPrivacy_String";

    private static final String IAB_USPRIVACY_STRING = "IABUSPrivacy_String";




    /**
     * Set the IAB US Privacy String in the SDK
     *
     * @param privacyString will be set and pass string to Ad Server
     */
    public static void setUSPrivacyString(Context context, String privacyString) {
        if(!TextUtils.isEmpty(privacyString) && context != null) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(AN_USPRIVACY_STRING, privacyString).apply();
        }
    }

    /**
     * Get the IAB US Privacy String  that will be sent in the request.
     *
     * @param context
     * @return Return IAB US Privacy String set by Publisher
     * If IAB US Privacy String is not set , empty string value or reset , do not pass any us_privacy signal in request
     */
    public static String getUSPrivacyString(Context context) {
        if (context != null) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

            if (pref.contains(AN_USPRIVACY_STRING)) {
                return pref.getString(AN_USPRIVACY_STRING, "");
            } else if (pref.contains(IAB_USPRIVACY_STRING)) {
                return pref.getString(IAB_USPRIVACY_STRING, "");
            }


        }
        return "";
    }


    /**
     * Clear the value of IAB US Privacy String that was previously set using setUSPrivacyString
     *
     * @param context
     */

    public static void reset(Context context) {
        if (context != null) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            if (pref.contains(AN_USPRIVACY_STRING)) {
                pref.edit().remove(AN_USPRIVACY_STRING).apply();
            }
        }
    }


}
