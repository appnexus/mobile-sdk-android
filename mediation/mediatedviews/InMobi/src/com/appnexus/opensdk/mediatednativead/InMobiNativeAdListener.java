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

package com.appnexus.opensdk.mediatednativead;

import com.appnexus.opensdk.MediatedNativeAdController;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.utils.Clog;
import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiNative;

public class InMobiNativeAdListener implements InMobiNative.NativeAdListener {
    private final MediatedNativeAdController controller;

    public InMobiNativeAdListener(MediatedNativeAdController controller) {
        this.controller = controller;
    }

    @Override
    public void onAdLoadSucceeded(InMobiNative inMobiNative) {
        if (inMobiNative != null) {
            if (controller != null) {
                InMobiNativeAdResponse response = new InMobiNativeAdResponse();
                if (response.setResources(inMobiNative)) {
                    controller.onAdLoaded(response);
                } else {
                    controller.onAdFailed(ResultCode.UNABLE_TO_FILL);
                }
            }

        }
    }

    @Override
    public void onAdLoadFailed(InMobiNative inMobiNative, InMobiAdRequestStatus inMobiAdRequestStatus) {
        Clog.d(Clog.mediationLogTag, "InMobi: " + inMobiAdRequestStatus.toString());
        if (controller != null) {
            controller.onAdFailed(InMobiSettings.getResultCode(inMobiAdRequestStatus));
        }
    }

    @Override
    public void onAdDismissed(InMobiNative inMobiNative) {

    }

    @Override
    public void onAdDisplayed(InMobiNative inMobiNative) {

    }

    @Override
    public void onUserLeftApplication(InMobiNative inMobiNative) {

    }
}
