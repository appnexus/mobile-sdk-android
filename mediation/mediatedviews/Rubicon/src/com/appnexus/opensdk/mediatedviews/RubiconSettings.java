package com.appnexus.opensdk.mediatedviews;

import android.support.annotation.NonNull;
import android.util.Pair;

import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.utils.Clog;

import java.util.HashMap;

/**
 * Created by ramit on 01/02/16.
 */
public class RubiconSettings {

    public static final String AD_ID = "adId";
    public static final String SERVER_NAME = "serverName";
    public static final String PUB_ID = "pubId";

    @NonNull
    public static HashMap<String, String> getTargetingParams(TargetingParameters targetingParameters) {
        HashMap<String,String> mTargetingInfo = new HashMap<String, String>();

        mTargetingInfo.put("GENDER", targetingParameters.getGender().toString());
        if(targetingParameters.getAge() != null) {
            mTargetingInfo.put("AGE", targetingParameters.getAge());
        }
        if(targetingParameters.getCustomKeywords().size() > 0) {
            mTargetingInfo.put("NBA_KV", getCustomKeywords(targetingParameters));
        }
        return mTargetingInfo;
    }

    private static String getCustomKeywords(TargetingParameters targetingParameters){
        String customKeywordString = "";
        try {
            for (Pair<String, String> keyword : targetingParameters.getCustomKeywords()) {
                customKeywordString = customKeywordString + keyword.first + "=" + keyword.second + ",";
            }
            if (customKeywordString.trim().length() > 0) {
                customKeywordString = customKeywordString.substring(0, customKeywordString.length() - 1);
            }
        }catch (StringIndexOutOfBoundsException e){
            Clog.e(Clog.mediationLogTag, "Exception parsing the custom keywords: " + customKeywordString);
        }
        return customKeywordString;
    }
}
