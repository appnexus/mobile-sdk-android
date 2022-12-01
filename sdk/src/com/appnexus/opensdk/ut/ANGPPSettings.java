/*
 *    Copyright 2018 APPNEXUS INC
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

package com.appnexus.opensdk.ut;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.appnexus.opensdk.utils.Clog;

import org.json.JSONArray;

/**
 * GPP Settings class used for UT request formation.
 */
class ANGPPSettings {

    // GPP consent parameters
    private static final String IABGPP_HDR_GppString = "IABGPP_HDR_GppString";
    private static final String IABGPP_GppSID = "IABGPP_GppSID";

    /**
     * @return SharedPreference value for IABGPP_HDR_GppString (if any) in the String format
     * */
    public static String getIabGppString(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(IABGPP_HDR_GppString, null);
    }

    /**
     * @return SharedPreference value for IABGPP_GppSID (if any) in the String format
     * */
    public static JSONArray getIabGppSIDArray(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String sIds = pref.getString(IABGPP_GppSID, null);
        if (sIds != null && sIds.length() > 0) {
            String[] sIdStringArray = sIds.split("_");
            if (sIdStringArray.length > 0) {
                try {
                    JSONArray sIdIntArray = new JSONArray();
                    for (String sId: sIdStringArray) {
                        sIdIntArray.put(Integer.valueOf(sId));
                    }
                    return sIdIntArray;
                } catch (NumberFormatException e) {
                    Clog.e(Clog.baseLogTag, "GPP SIDs should be comma separated integers");
                }
            }
        }
        return null;
    }
}
