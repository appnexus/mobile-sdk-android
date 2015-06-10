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
import com.inmobi.monetization.IMErrorCode;
import com.inmobi.monetization.IMNative;
import com.inmobi.monetization.IMNativeListener;

import java.lang.ref.WeakReference;

public class InMobiNativeAdListener implements IMNativeListener {
    private final MediatedNativeAdController controller;

    public InMobiNativeAdListener(MediatedNativeAdController controller) {
        this.controller = controller;
    }

    @Override
    public void onNativeRequestFailed(IMErrorCode imErrorCode) {
        Clog.d(Clog.mediationLogTag, "InMobi: " + imErrorCode.toString());

        ResultCode code = ResultCode.INTERNAL_ERROR;

        switch (imErrorCode) {

            case INVALID_REQUEST:
                code = ResultCode.INVALID_REQUEST;
                break;
            case INTERNAL_ERROR:
                break;
            case NO_FILL:
                code = ResultCode.UNABLE_TO_FILL;
                break;
            case DO_MONETIZE:
                break;
            case DO_NOTHING:
                break;
            case NETWORK_ERROR:
                code = ResultCode.NETWORK_ERROR;
                break;
        }

        if (controller != null) {
            controller.onAdFailed(code);
        }

    }

    @Override
    public void onNativeRequestSucceeded(IMNative imNative) {
        if (imNative != null) {
            InMobiNativeAdResponse response = new InMobiNativeAdResponse();
            if (response.setResources(imNative)) {
                controller.onAdLoaded(response);
                return;
            }
        }
        if (controller != null) {
            controller.onAdFailed(ResultCode.UNABLE_TO_FILL);
        }

    }
}
