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

import com.appnexus.opensdk.utils.Clog;
import com.chartboost.sdk.Chartboost;

public class ChartboostSettings {
    public static final String KEY_CHARTBOOST_LOCATION = "Chartboost location";

    /**
     * Convenience method to initialize Chartboost. We recommend calling this method in the onCreate()
     * of your activity.
     * @param activity The activity that Chartboost is launched from.
     * @param appId Your appId from Chartboost.
     * @param appSignature Your app signature.
     * @return True if initialization is successful.
     */

    public static boolean initialize(Activity activity, String appId, String appSignature) {
        try {
            Chartboost.startWithAppId(activity, appId, appSignature);
            Chartboost.setDelegate(ChartboostDelegateBridge.getInstance());
            Chartboost.onCreate(activity);
            Chartboost.onStart(activity);
            return true;
        } catch (Exception initializationError) {
            Clog.d(Clog.mediationLogTag, "Chartboost initialization failed because " + initializationError.getMessage());
            return false;
        }
    }

}
