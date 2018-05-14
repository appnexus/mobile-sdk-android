
package com.appnexus.opensdk.mediatednativead;

import android.content.Context;

import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.utils.StringUtil;
import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.sdk.InMobiSdk;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class InMobiSettings {
    static String KEY_TITLE = "title";
    static String KEY_ICON = "icon";
    static String KEY_URL = "url";
    static String KEY_IMAGE = "screenshots";
    static String KEY_CALL_TO_ACTION = "cta";
    static String KEY_RATING = "rating";
    static String KEY_LANDING_URL = "landingURL";
    static String KEY_DESCRIPTION = "description";
    public static String INMOBI_APP_ID = "";
    public static String NATIVE_ELEMENT_OBJECT = "element";
    public static String IMPRESSION_TRACKERS = "impressionTrackers";

    public static void setInMobiAppId(String key, Context context) {
        INMOBI_APP_ID = key;
        if (!StringUtil.isEmpty(INMOBI_APP_ID)) {
            InMobiSdk.init(context, INMOBI_APP_ID);
        }
    }

    public static void setCustomizedKeyForTitle(String key) {
        KEY_TITLE = key;
    }

    public static void setCustomizedKeyForIcon(String key) {
        KEY_ICON = key;
    }

    public static void setCustomizedKeyForUrl(String key) {
        KEY_URL = key;
    }

    public static void setCustomizedKeyForScreenshots(String key) {
        KEY_IMAGE = key;
    }

    public static void setCustomizeKeyForCTA(String key) {
        KEY_CALL_TO_ACTION = key;
    }

    public static void setCustomizeKeyForLandingUrl(String key) {
        KEY_LANDING_URL = key;
    }

    public static void setCustomizeKeyForDescription(String key) {
        KEY_DESCRIPTION = key;
    }

    public static void setCustomizeKeyForRating(String key) {
        KEY_RATING = key;
    }

    public static ResultCode getResultCode(InMobiAdRequestStatus status) {
        ResultCode code = ResultCode.INTERNAL_ERROR;
        switch (status.getStatusCode()) {
            case NETWORK_UNREACHABLE:
                code = ResultCode.NETWORK_ERROR;
                break;
            case NO_FILL:
                code = ResultCode.UNABLE_TO_FILL;
                break;
            case REQUEST_INVALID:
                code = ResultCode.INVALID_REQUEST;
                break;
            case REQUEST_PENDING:
                break;
            case REQUEST_TIMED_OUT:
                code = ResultCode.NETWORK_ERROR;
                break;
            case INTERNAL_ERROR:
                break;
            case SERVER_ERROR:
                code = ResultCode.UNABLE_TO_FILL;
                break;
            case AD_ACTIVE:
                code = ResultCode.INVALID_REQUEST;
                break;
            case EARLY_REFRESH_REQUEST:
                code = ResultCode.INVALID_REQUEST;
                break;
        }
        return code;
    }

    public static void setTargetingParams(TargetingParameters tp) {
        if (tp == null) return;
        switch (tp.getGender()) {
            case UNKNOWN:
                break;
            case MALE:
                InMobiSdk.setGender(InMobiSdk.Gender.MALE);
                break;
            case FEMALE:
                InMobiSdk.setGender(InMobiSdk.Gender.FEMALE);
                break;
        }
        if (tp.getAge() != null) {
            int age = 0;
            try {
                String age_string = tp.getAge();
                if (age_string.contains("-")) {
                    int dash = age_string.indexOf("-");
                    int age1 = Integer.parseInt(age_string.substring(0, dash));
                    int age2 = Integer.parseInt(age_string.substring(dash + 1));
                    age = (age1 + age2) / 2;
                } else {
                    age = Integer.parseInt(tp.getAge());
                    if (age > 1900) {
                        GregorianCalendar calendar = new GregorianCalendar();
                        Date date = new Date();
                        calendar.setTime(date);
                        int year = calendar.get(Calendar.YEAR);
                        age = year - age;
                    }
                }
            } catch (NumberFormatException e) {
            } catch (IllegalArgumentException e1) {
            } catch (ArrayIndexOutOfBoundsException e2) {
            }
            if (age > 0) {
                InMobiSdk.setAge(age);
                if (age < 18) {
                    InMobiSdk.setAgeGroup(InMobiSdk.AgeGroup.BELOW_18);
                } else if (age <= 24) {
                    InMobiSdk.setAgeGroup(InMobiSdk.AgeGroup.BETWEEN_18_AND_24);
                } else if (age <= 29) {
                    InMobiSdk.setAgeGroup(InMobiSdk.AgeGroup.BETWEEN_25_AND_29);
                } else if (age <= 34) {
                    InMobiSdk.setAgeGroup(InMobiSdk.AgeGroup.BETWEEN_30_AND_34);
                } else if (age <= 44) {
                    InMobiSdk.setAgeGroup(InMobiSdk.AgeGroup.BETWEEN_35_AND_44);
                }
                else if (age <= 54) {
                    InMobiSdk.setAgeGroup(InMobiSdk.AgeGroup.BETWEEN_45_AND_54);
                }
                else if (age <= 64) {
                    InMobiSdk.setAgeGroup(InMobiSdk.AgeGroup.BETWEEN_55_AND_65);
                }
                else{
                    InMobiSdk.setAgeGroup(InMobiSdk.AgeGroup.ABOVE_65);
                }
            }
        }

        if (tp.getLocation() != null) {
            InMobiSdk.setLocation(tp.getLocation());
        }
    }
}
