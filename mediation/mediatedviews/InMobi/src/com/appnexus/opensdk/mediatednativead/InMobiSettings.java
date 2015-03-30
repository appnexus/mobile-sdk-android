
package com.appnexus.opensdk.mediatednativead;

import android.content.Context;

import com.appnexus.opensdk.TargetingParameters;
import com.inmobi.commons.GenderType;
import com.inmobi.commons.InMobi;

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

    public static void setInMobiAppId(String key, Context context) {
        INMOBI_APP_ID = key;
        if (INMOBI_APP_ID != null && !INMOBI_APP_ID.isEmpty()) {
            InMobi.initialize(context, INMOBI_APP_ID);
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

    public static void setTargetingParams(TargetingParameters tp) {
        if (tp == null) return;
        switch (tp.getGender()) {
            case UNKNOWN:
                InMobi.setGender(GenderType.UNKNOWN);
                break;
            case MALE:
                InMobi.setGender(GenderType.MALE);
                break;
            case FEMALE:
                InMobi.setGender(GenderType.FEMALE);
                break;
        }
        if (Integer.getInteger(tp.getAge()) != null) {
            InMobi.setAge(Integer.getInteger(tp.getAge()));
        }
        if (tp.getLocation() != null) {
            InMobi.setCurrentLocation(tp.getLocation());
        }
    }
}
