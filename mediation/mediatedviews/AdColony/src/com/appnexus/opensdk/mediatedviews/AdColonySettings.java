/*
 *    Copyright 2015 APPNEXUS INC
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


import android.app.Activity;
import android.util.Pair;

import com.adcolony.sdk.AdColony;
import com.adcolony.sdk.AdColonyAppOptions;
import com.adcolony.sdk.AdColonyUserMetadata;
import com.adcolony.sdk.AdColonyZone;
import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.StringUtil;

public class AdColonySettings {

    static String appID = "";
    static String[] zoneIds;
    static String version;
    static String store;

    /**
     * Convenience method to initialize AdColony and cache video ads for zones. We recommend calling
     * this method in the onCreate() of the main activity of your application.
     *
     * @param activity the activity that this method is called from, suggesting the main activity
     * @param version  the version of the app
     * @param store    the store that the app is published on, currently AdColony supports 'google' and 'amazon'
     * @param appID    appID for the app from AdColony
     * @param zoneIds  zoneIds for the app, at least one should be passed in
     */
    public static void configure(Activity activity,String version, String store, String appID, String... zoneIds) {
        AdColonySettings.appID = appID;
        AdColonySettings.zoneIds = zoneIds;
        AdColonySettings.version = version;
        AdColonySettings.store = store;
    }


    static AdColonyAppOptions getAdColonyAppOptions(TargetingParameters tp) {
        AdColonyAppOptions adColonyAppOptions = new AdColonyAppOptions();
        adColonyAppOptions.setOriginStore(AdColonySettings.store);
        adColonyAppOptions.setAppVersion(AdColonySettings.version);

        AdColonyUserMetadata userMetadata = new AdColonyUserMetadata();
        switch(tp.getGender()){
            case FEMALE:
                userMetadata.setUserGender(AdColonyUserMetadata.USER_FEMALE);
                break;
            case MALE:
                userMetadata.setUserGender(AdColonyUserMetadata.USER_MALE);
                break;
            default:
                break;
        }

        if (!StringUtil.isEmpty(tp.getAge())) {
            userMetadata.setUserAge(Integer.parseInt(tp.getAge()));
        }

        if (tp.getLocation() != null) {
            userMetadata.setUserLocation(tp.getLocation());
        }

        adColonyAppOptions.setUserMetadata(userMetadata);


        for (Pair<String, String> p : tp.getCustomKeywords()) {
            // userMetadata.setMetadata(String key,String Value); // Not using User Meta data
            adColonyAppOptions.setOption(p.first,p.second);
        }
        return adColonyAppOptions;
    }

    static boolean isConfigured() {
        return !AdColony.getSDKVersion().isEmpty();
    }


    static boolean isAdColonyZoneValid(String zoneId) {
        AdColonyZone zone = AdColony.getZone(zoneId);
        if(zone != null && zone.isValid()){
            return true;
        }else{
            Clog.e(Clog.mediationLogTag, "Invalid AdColony Zone id:"+ zoneId);
            return false;
        }
    }


}
