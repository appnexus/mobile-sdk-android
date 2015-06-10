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

import com.appnexus.opensdk.ResultCode;
import com.jirbo.adcolony.AdColony;

public class AdColonySettings {
    static String appID = "";
    static String client_options = "";

    // key to retrieve video native ad view from native elements
    public static final String KEY_NATIVE_AD_VIEW = "nativeAdView";
    public static final String KEY_NATIVE_AD_WIDTH = "ad_width";

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
    public static void configure(Activity activity, String version, String store, String appID, String... zoneIds) {
        AdColonySettings.appID = appID;
        client_options = "version:" + version + ",store:" + store;
        AdColony.configure(activity, client_options, appID, zoneIds);
    }

    public static enum AdColonyStatus {
        INVALID ("invalid", "Invalid zone id."),
        UNKNOWN ("unknown", "You haven't configured AdColony yet, call AdColonySetting.configure() in activity onCreate()"),
        OFF ("off", "Zone id is turned off."),
        LOADING ("loading", "No available ads yet."),
        ACTIVE ("active", "Ad is ready.");

        private final String status;
        private final String errorMessage;

        private AdColonyStatus(String s, String e) {
            status = s;
            errorMessage = e;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public String getStatus() {
            return status;
        }

        public static AdColonyStatus getStatus(String s) {
            for (AdColonyStatus status : AdColonyStatus.values()) {
                if (status.getStatus().equals(s)) {
                    return status;
                }
            }
            return AdColonyStatus.INVALID;
        }

    }

    public static ResultCode errorCodeForStatus(AdColonyStatus status) {
        ResultCode code = ResultCode.INTERNAL_ERROR;

        switch (status) {
            case INVALID:
                code = ResultCode.INVALID_REQUEST;
                break;
            case UNKNOWN:
                code = ResultCode.INVALID_REQUEST;
                break;
            case OFF:
                code = ResultCode.INVALID_REQUEST;
                break;
            case LOADING:
                code = ResultCode.UNABLE_TO_FILL;
                break;
            case ACTIVE:
                code = ResultCode.SUCCESS;
                break;
        }
        return code;
    }

    public static boolean isActive(String status) {
        return status.equals(AdColonyStatus.ACTIVE.getStatus());
    }
}
