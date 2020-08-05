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
import com.appnexus.opensdk.NativeAdEventListener;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.utils.Clog;
import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiNative;

import java.lang.ref.WeakReference;

public class InMobiNativeAdListener extends com.inmobi.ads.listeners.NativeAdEventListener {
    private final MediatedNativeAdController controller;
    private WeakReference<InMobiNativeAdResponse> weakReferenceInMobiNativeAdResponse;

    public InMobiNativeAdListener(MediatedNativeAdController controller) {
        this.controller = controller;
    }

    @Override
    public void onAdLoadSucceeded(InMobiNative inMobiNative) {
        if (inMobiNative != null) {
            if (controller != null) {
                InMobiNativeAdResponse response = new InMobiNativeAdResponse();
                if (response.setResources(inMobiNative)) {
                    weakReferenceInMobiNativeAdResponse = new WeakReference<InMobiNativeAdResponse>(response);
                    controller.onAdLoaded(response);
                } else {
                    controller.onAdFailed(ResultCode.getNewInstance(ResultCode.UNABLE_TO_FILL));
                }
            }

        }
    }

    @Override
    public void onAdLoadFailed(InMobiNative inMobiNative, InMobiAdRequestStatus inMobiAdRequestStatus) {
        Clog.e(Clog.mediationLogTag, "InMobiNative: " + inMobiAdRequestStatus.toString());
        if (controller != null) {
            controller.onAdFailed(InMobiSettings.getResultCode(inMobiAdRequestStatus));
        }
    }

    @Override
    public void onAdReceived(InMobiNative inMobiNative) {
        Clog.d(Clog.mediationLogTag, "InMobiNative - onAdReceived");
    }

    @Override
    public void onAdFullScreenDismissed(InMobiNative inMobiNative) {
        Clog.d(Clog.mediationLogTag, "InMobiNative - onAdFullScreenDismissed");
    }

    @Override
    public void onAdFullScreenWillDisplay(InMobiNative inMobiNative) {
        Clog.d(Clog.mediationLogTag, "InMobiNative - onAdFullScreenWillDisplay");
    }

    @Override
    public void onAdFullScreenDisplayed(InMobiNative inMobiNative) {
        Clog.d(Clog.mediationLogTag, "InMobiNative - onAdFullScreenDisplayed");
    }

    @Override
    public void onUserWillLeaveApplication(InMobiNative inMobiNative) {
        Clog.d(Clog.mediationLogTag, "InMobiNative - onUserWillLeaveApplication");
        InMobiNativeAdResponse response = this.weakReferenceInMobiNativeAdResponse.get();
        if (response != null) {
            NativeAdEventListener listener = response.getListener();
            if (listener != null) {
                listener.onAdWillLeaveApplication();
            }
        }
    }

    @Override
    public void onAdImpressed(InMobiNative inMobiNative) {
        Clog.d(Clog.mediationLogTag, "InMobiNative - onAdImpressed");
        if(controller!=null) {
            controller.onAdImpression();
        }
    }

    @Override
    public void onAdClicked(InMobiNative inMobiNative) {
        Clog.d(Clog.mediationLogTag, "InMobiNative - onAdClicked");
        InMobiNativeAdResponse response = this.weakReferenceInMobiNativeAdResponse.get();
        if (response != null) {
            NativeAdEventListener listener = response.getListener();
            if (listener != null) {
                listener.onAdWasClicked();
            }
        }
    }

    @Override
    public void onAdStatusChanged(InMobiNative inMobiNative) {
        Clog.d(Clog.mediationLogTag, "InMobiNative - onAdStatusChanged");
    }

}
