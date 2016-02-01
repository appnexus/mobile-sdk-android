/*
 *    Copyright 2016 APPNEXUS INC
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
package com.appnexus.opensdk.mediatedviews;

import android.support.annotation.NonNull;
import android.util.Pair;

import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.utils.Clog;

import java.util.HashMap;


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
