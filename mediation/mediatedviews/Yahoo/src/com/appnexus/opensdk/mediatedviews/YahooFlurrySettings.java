package com.appnexus.opensdk.mediatedviews;

import android.content.Context;
import android.os.Build;
import android.util.Pair;

import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.StringUtil;
import com.flurry.android.FlurryAgent;
import com.flurry.android.ads.FlurryAdErrorType;
import com.flurry.android.ads.FlurryAdTargeting;
import com.flurry.android.ads.FlurryGender;

import java.util.HashMap;

public class YahooFlurrySettings {

    public static boolean test_mode = false;

    // For native asset retrievement
    public static String ADVERTISER_NAME = "source";
    public static String SECURE_BRANDING_LOGO = "secBrandingLogo";
    public static String SECURE_HQ_BRANDING_LOGO = "secHqBrandingLogo";
    public static String SECURE_ORIGINAL_IMAGE = "secOrigImg";
    public static String SECURE_HQ_IMAGE = "secHqImage";
    public static String SECURE_IMAGE = "secImage";
    public static String APP_CATEGORY = "appCategory";
    public static String SECURE_RATING_IMG = "secRatingImg";
    public static String SECURE_HQ_RATING_IMG = "secHqRatingImg";
    public static String SHOW_RATING = "showRating";

    public static void init(Context context, String appKey) {
        FlurryAgent.init(context, appKey);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            Clog.i(Clog.mediationLogTag,
                    "Since the SDK target is lower than Ice Cream Sandwich, " +
                            "if you haven't configured session control of flurry yet, " +
                            "please refer to https://developer.yahoo.com/flurry/docs/publisher/code/android/ " +
                            "to add onStartSession, onEndSession in your activity's lifecycle.");
        }
    }

    public static FlurryAdTargeting getFlurryAdTargeting(TargetingParameters tp) {
        if (tp != null) {
            FlurryAdTargeting targeting = new FlurryAdTargeting();
            if (!StringUtil.isEmpty(tp.getAge())) {
                Integer age = Integer.getInteger(tp.getAge());
                if (age != null) {
                    targeting.setAge(age);
                }
            }
            if (tp.getLocation() != null) {
                float lat = (float) tp.getLocation().getLatitude();
                float lon = (float) tp.getLocation().getLongitude();
                targeting.setLocation(lat, lon);
            }
            if (tp.getGender() != null) {
                switch (tp.getGender()) {
                    case UNKNOWN:
                        targeting.setGender(FlurryGender.UNKNOWN);
                        break;
                    case MALE:
                        targeting.setGender(FlurryGender.MALE);
                        break;
                    case FEMALE:
                        targeting.setGender(FlurryGender.FEMALE);
                        break;
                }
            }
            if (tp.getCustomKeywords() != null && !tp.getCustomKeywords().isEmpty()) {
                HashMap<String, String> keywords = new HashMap<String, String>();
                for (Pair<String, String> p : tp.getCustomKeywords()) {
                    if (p != null) {
                        keywords.put(p.first, p.second);
                    }
                }
                if (!keywords.isEmpty()) {
                    targeting.setKeywords(keywords);
                }
            }
            if (test_mode) {
                targeting.setEnableTestAds(true);
            }
            return targeting;
        }
        return null;
    }

    public static ResultCode errorCodeMapping(FlurryAdErrorType flurryAdErrorType, int i) {
        // See details here: https://gist.github.com/flurrydev/92d14e136403c2ea35b4
        ResultCode error = ResultCode.INTERNAL_ERROR;
        if (i == 1) {
            error = ResultCode.NETWORK_ERROR;
        } else if (i == 4 || i == 21) {
            error = ResultCode.INVALID_REQUEST;
        } else if (i == 20) {
            error = ResultCode.UNABLE_TO_FILL;
        }
        return error;
    }

}
