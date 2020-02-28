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

package com.appnexus.opensdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;

/**
 * Global GDPR Settings class.
 */
public class ANGDPRSettings {

    private static final String IAB_CONSENT_STRING = "IABConsent_ConsentString";
    private static final String IAB_SUBJECT_TO_GDPR = "IABConsent_SubjectToGDPR";

    private static final String ANGDPR_CONSENT_STRING = "ANGDPR_ConsentString";
    private static final String ANGDPR_CONSENT_REQUIRED = "ANGDPR_ConsentRequired";


    //TCF 2.0 consent parameters
    private static final String IABTCF_CONSENT_STRING = "IABTCF_TCString";
    private static final String IABTCF_SUBJECT_TO_GDPR = "IABTCF_gdprApplies";

    private static final String  ANGDPR_DeviceAccessConsent = "ANGDPR_DeviceAccessConsent";

    /**
     * Set the consent string in the SDK
     *
     * @param consentString A valid Base64 encode consent string as per https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework
     */
    public static void setConsentString(Context context, String consentString) {
        if(!TextUtils.isEmpty(consentString) && context != null) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(ANGDPR_CONSENT_STRING, consentString).apply();
        }
    }


    /**
     * Set the consentRequired value in the SDK
     *
     * @param subjectToGDPR true if subject to GDPR regulations, false otherwise
     */
    public static void setConsentRequired(Context context, boolean subjectToGDPR) {
        if (context != null) {
            if (subjectToGDPR) {
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString(ANGDPR_CONSENT_REQUIRED, "1").apply();
            } else {
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString(ANGDPR_CONSENT_REQUIRED, "0").apply();
            }
        }
    }


    /**
     * Clears the value that were previously set using ANGDPRSettings.setConsentString and ANGDPRSettings.setConsentRequired
     *
     * @param context
     */
    public static void reset(Context context) {
        if (context != null) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            if (pref.contains(ANGDPR_CONSENT_STRING)) {
                pref.edit().remove(ANGDPR_CONSENT_STRING).apply();
            }

            if (pref.contains(ANGDPR_CONSENT_REQUIRED)) {
                pref.edit().remove(ANGDPR_CONSENT_REQUIRED).apply();
            }
        }
    }

    /**
     * Get the Consent String  that will be sent in the request.
     *
     * @param context
     * @return A valid Base64 encode consent string as per https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework
     * or "" if not set
     */
    public static String getConsentString(Context context) {
        if (context != null) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            if (pref.contains(ANGDPR_CONSENT_STRING)) {
                return pref.getString(ANGDPR_CONSENT_STRING, "");
            } else if (pref.contains(IABTCF_CONSENT_STRING)) {
                return pref.getString(IABTCF_CONSENT_STRING, "");
            } else if (pref.contains(IAB_CONSENT_STRING)) {
                return pref.getString(IAB_CONSENT_STRING, "");
            }
        }
        return "";
    }


    /**
     * Get the Consent Required value that will be sent in the request.
     *
     * @param context
     * @return true if subject to GDPR regulations, false if not subject to regulations.
     * null undetermined.
     */
    public static Boolean getConsentRequired(Context context) {
        String subjectToGdprValue = "Nil";
        if (context != null) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            if (pref.contains(ANGDPR_CONSENT_REQUIRED)) {
                subjectToGdprValue = pref.getString(ANGDPR_CONSENT_REQUIRED, "Nil");
            } else if (pref.contains(IABTCF_SUBJECT_TO_GDPR)) {
                subjectToGdprValue = pref.getString(IABTCF_SUBJECT_TO_GDPR, "Nil");
            } else if (pref.contains(IAB_SUBJECT_TO_GDPR)) {
                subjectToGdprValue = pref.getString(IAB_SUBJECT_TO_GDPR, "Nil");
            }
        }

        if (subjectToGdprValue.equalsIgnoreCase("1")) {
            return true;
        } else if (subjectToGdprValue.equalsIgnoreCase("0")) {
            return false;
        }
        return null;
    }

    /**
     * Set the device access Consent by the publisher.
     *
     * @param context
     * @param A consent set by the publisher to access the device data as per https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework
     */
    public static void setDeviceAccessConsent(Context context, boolean deviceConsent) {
        if (context != null) {
            if (deviceConsent) {
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString(ANGDPR_DeviceAccessConsent, "1").apply();
            } else {
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString(ANGDPR_DeviceAccessConsent, "0").apply();
            }
        }
    }

    /**
     * Get the device access Consent set by the publisher.
     *
     * @param context
     * @return A valid Base64 encode consent string as per https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework
     * or "" if not set
     */
    public static Boolean getDeviceAccessConsent(Context context) {

        String deviceConsent = "Nil";

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            if (pref.contains(ANGDPR_DeviceAccessConsent)) {
                deviceConsent = pref.getString(ANGDPR_DeviceAccessConsent, "Nil");
            }


        if (deviceConsent.equalsIgnoreCase("1")) {
            return true;
        } else if (deviceConsent.equalsIgnoreCase("0")) {
            return false;
        }
        return null;
    }


}
