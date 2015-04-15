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

import com.appnexus.opensdk.MediatedAdViewController;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.utils.Clog;
import com.jirbo.adcolony.AdColonyAd;
import com.jirbo.adcolony.AdColonyAdListener;

public class AdColonyListener implements AdColonyAdListener {
    final MediatedAdViewController mAC;
    final String className;

    public AdColonyListener(MediatedAdViewController mAC, String className) {
        this.mAC = mAC;
        this.className = className;
    }

    @Override
    public void onAdColonyAdAttemptFinished(AdColonyAd adColonyAd) {
        Clog.d(Clog.mediationLogTag, "Attempt to finish ad from " + className);
        if (mAC != null) {
            mAC.onAdCollapsed();
        }
    }

    @Override
    public void onAdColonyAdStarted(AdColonyAd adColonyAd) {
        Clog.d(Clog.mediationLogTag, className + "ad started");
        if (mAC != null) {
            mAC.onAdExpanded();
        }
    }

    public void onZoneStatusNotActive(String zoneStatus, String zoneId) {
        ResultCode code = ResultCode.INTERNAL_ERROR;

        if (zoneStatus.equals(AdColonySettings.INVALID)) {
            code = ResultCode.INVALID_REQUEST;
            printToClogError("Invalid zone id.");
        } else if (zoneStatus.equals(AdColonySettings.UNKNOWN)) {
            code = ResultCode.INVALID_REQUEST;
            printToClogError("You haven't configured AdColony yet, call AdColonySetting.configure() in activity onCreate()");
        } else if (zoneStatus.equals(AdColonySettings.LOADING)) {
            code = ResultCode.UNABLE_TO_FILL;
            printToClogError("No available ads yet.");
        } else if (zoneStatus.equals(AdColonySettings.OFF)) {
            code = ResultCode.INVALID_REQUEST;
            printToClogError("Zone id " + zoneId + " is turned off.");
        }
        if (mAC != null) {
            mAC.onAdFailed(code);
        }
    }

    void printToClogError(String s) {
        Clog.e(Clog.mediationLogTag, className + "-" + s);
    }
}
