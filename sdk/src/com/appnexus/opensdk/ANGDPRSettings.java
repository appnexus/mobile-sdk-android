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
import com.appnexus.opensdk.utils.StringUtil;

/**
 * Global GDPR Settings class.
 */
public class ANGDPRSettings {

    private static final String IAB_CONSENT_STRING = "IABConsent_ConsentString";
    private static final String IAB_SUBJECT_TO_GDPR = "IABConsent_SubjectToGDPR";

    private static final String ANGDPR_CONSENT_STRING = "ANGDPR_ConsentString";
    private static final String ANGDPR_CONSENT_REQUIRED = "ANGDPR_ConsentRequired";
    private static final String ANGDPR_PurposeConsents = "ANGDPR_PurposeConsents";

    //TCF 2.0 consent parameters
    private static final String IABTCF_CONSENT_STRING = "IABTCF_TCString";
    private static final String IABTCF_SUBJECT_TO_GDPR = "IABTCF_gdprApplies";
    private static final String IABTCF_PurposeConsents = "IABTCF_PurposeConsents";


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

            if (pref.contains(ANGDPR_PurposeConsents)) {
                pref.edit().remove(ANGDPR_PurposeConsents).apply();
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
        String subjectToGdprValue = "";
        if (context != null) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            if (pref.contains(ANGDPR_CONSENT_REQUIRED)) {
                subjectToGdprValue = pref.getString(ANGDPR_CONSENT_REQUIRED, "");
            } else if (pref.contains(IABTCF_SUBJECT_TO_GDPR)) {
                int iabTcfSubjectToGdpr = pref.getInt(IABTCF_SUBJECT_TO_GDPR, -1);
                subjectToGdprValue = iabTcfSubjectToGdpr == -1 ? "" : String.valueOf(iabTcfSubjectToGdpr);
            } else if (pref.contains(IAB_SUBJECT_TO_GDPR)) {
                subjectToGdprValue = pref.getString(IAB_SUBJECT_TO_GDPR, "");
            }
        }

        return TextUtils.isEmpty(subjectToGdprValue) ? null : subjectToGdprValue.equals("1");
    }

    /**
     * Set the device access Consent by the publisher.
     *
     * @param purposeConsents A consent set by the publisher to access the device data as per https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework
     */
    public static void setPurposeConsents(Context context, String purposeConsents) {
        if (context != null && !purposeConsents.isEmpty()) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(ANGDPR_PurposeConsents, purposeConsents).apply();
        }
    }

    /**
     * Get the device access Consent set by the publisher.
     *
     * @param context
     * @return A valid Base64 encode consent string as per https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework
     * or "" if not set
     */
    public static String getDeviceAccessConsent(Context context) {

        if(context == null)
            return null;

        String deviceConsent = null;

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        if (pref.contains(ANGDPR_PurposeConsents)) {
                deviceConsent = pref.getString(ANGDPR_PurposeConsents, null);
        } else if (pref.contains(IABTCF_PurposeConsents)){
                deviceConsent = pref.getString(IABTCF_PurposeConsents, null);
        }

        return !StringUtil.isEmpty(deviceConsent) ? deviceConsent.substring(0, 1) : null;
    }

    public static Boolean canIAccessDeviceData(Context context) {
        //fetch advertising identifier based TCF 2.0 Purpose1 value
        //truth table
            /*
                                    deviceAccessConsent=true   deviceAccessConsent=false  deviceAccessConsent undefined
            consentRequired=false        Yes, read IDFA             No, don’t read IDFA           Yes, read IDFA
            consentRequired=true         Yes, read IDFA             No, don’t read IDFA           No, don’t read IDFA
            consentRequired=undefined    Yes, read IDFA             No, don’t read IDFA           Yes, read IDFA
            */

        if(((ANGDPRSettings.getDeviceAccessConsent(context) == null) && (ANGDPRSettings.getConsentRequired(context) == null || ANGDPRSettings.getConsentRequired(context) == false)) ||
                (ANGDPRSettings.getDeviceAccessConsent(context) != null && ANGDPRSettings.getDeviceAccessConsent(context).equals("1"))){
            return true;
        }

        return false;
    }


}
