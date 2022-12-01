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
import com.appnexus.opensdk.utils.StringUtil;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

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

    // Google ACM consent parameter
    private static final String IABTCF_ADDTL_CONSENT = "IABTCF_AddtlConsent";

    // GPP consent parameters
    private static final String IABGPP_TCFEU2_SubjectToGDPR = "IABGPP_TCFEU2_gdprApplies";
    private static final String IABGPP_TCFEU2_PurposeConsents = "IABGPP_TCFEU2_PurposeConsents";

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
            if (pref.contains(IABTCF_CONSENT_STRING)) {
                return pref.getString(IABTCF_CONSENT_STRING, "");
            }else if (pref.contains(ANGDPR_CONSENT_STRING)) {
                return pref.getString(ANGDPR_CONSENT_STRING, "");
            } else if (pref.contains(IAB_CONSENT_STRING)) {
                return pref.getString(IAB_CONSENT_STRING, "");
            }
        }
        return "";
    }


    // pull Google Ad Tech Provider (ATP) IDs ids from the Addtional Consent(AC)string and convert them to JSONArray of integers.
    // for example if addtlConsentString = '1~7.12.35.62.66.70.89.93.108', then we need to return [7,12,35,62,66,70,89,93,108] this is the format impbus understands.
    public static JSONArray getGoogleACMConsentStringJSONArray(Context context) {
        if (context != null) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            if (pref.contains(IABTCF_ADDTL_CONSENT)) {
                // From https://support.google.com/admanager/answer/9681920
                // An AC string contains the following three components:
                // Part 1: A specification version number, such as "1"
                // Part 2: A separator symbol "~"
                // Part 3: A dot-separated list of user-consented Google Ad Tech Provider (ATP) IDs. Example: "1.35.41.101"
                // For example, the AC string 1~1.35.41.101 means that the user has consented to ATPs with IDs 1, 35, 41 and 101, and the string is created using the format defined in the v1.0 specification.
                String addtlConsentString = pref.getString(IABTCF_ADDTL_CONSENT, "");
                // Only if a valid Additional consent string is present proceed further.
                // The string has to start with 1~ (we support only version 1 of the ACM spec)
                if (!StringUtil.isEmpty(addtlConsentString) && addtlConsentString.length()>2 && addtlConsentString.startsWith("1~")) {
                    List<Integer> arrayListofATP = new ArrayList<>();
                    try {
                        String[] parsedAC = addtlConsentString.split("~", 2);
                        String[] consentedATP = parsedAC[1].split("\\.", -1);
                        for (int i = 0; i < consentedATP.length; i++) {
                            arrayListofATP.add(Integer.parseInt(consentedATP[i]));
                        }
                        return new JSONArray(arrayListofATP);
                    } catch (Exception e) {
                        Clog.e(Clog.baseLogTag, "Exception while processing Google addtlConsentString: " + e.getMessage());
                    }
                }
            }
        }
        return null;
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
            if (pref.contains(IABTCF_SUBJECT_TO_GDPR)) {
                int iabTcfSubjectToGdpr = pref.getInt(IABTCF_SUBJECT_TO_GDPR, -1);
                subjectToGdprValue = iabTcfSubjectToGdpr == -1 ? "" : String.valueOf(iabTcfSubjectToGdpr);
            }else if (pref.contains(ANGDPR_CONSENT_REQUIRED)) {
                subjectToGdprValue = pref.getString(ANGDPR_CONSENT_REQUIRED, "");
            } else if (pref.contains(IAB_SUBJECT_TO_GDPR)) {
                subjectToGdprValue = pref.getString(IAB_SUBJECT_TO_GDPR, "");
            }  else if (pref.contains(IABGPP_TCFEU2_SubjectToGDPR)) {
                subjectToGdprValue = pref.getString(IABGPP_TCFEU2_SubjectToGDPR, "");
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
        if (pref.contains(IABTCF_PurposeConsents)){
            deviceConsent = pref.getString(IABTCF_PurposeConsents, null);
        }else if (pref.contains(ANGDPR_PurposeConsents)) {
            deviceConsent = pref.getString(ANGDPR_PurposeConsents, null);
        } else if (pref.contains(IABGPP_TCFEU2_PurposeConsents)) {
            deviceConsent = pref.getString(IABGPP_TCFEU2_PurposeConsents, null);
        }

        return !StringUtil.isEmpty(deviceConsent) ? deviceConsent.substring(0, 1) : null;
    }

    public static Boolean canIAccessDeviceData(Context context) {
        //fetch advertising identifier based TCF 2.0 Purpose1 value
        //truth table
            /*
                                    deviceAccessConsent=true   deviceAccessConsent=false  deviceAccessConsent undefined
            consentRequired=false        Yes, read AAID             No, don’t read AAID           Yes, read AAID
            consentRequired=true         Yes, read AAID             No, don’t read AAID           No, don’t read AAID
            consentRequired=undefined    Yes, read AAID             No, don’t read AAID           Yes, read AAID
            */

        if(((ANGDPRSettings.getDeviceAccessConsent(context) == null) && (ANGDPRSettings.getConsentRequired(context) == null || ANGDPRSettings.getConsentRequired(context) == false)) ||
                (ANGDPRSettings.getDeviceAccessConsent(context) != null && ANGDPRSettings.getDeviceAccessConsent(context).equals("1"))){
            return true;
        }

        return false;
    }
}
