/*
 *    Copyright 2014 APPNEXUS INC
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
import com.facebook.ads.*;

import java.lang.ref.WeakReference;


public class FacebookNativeAdListener implements NativeAdListener {

    private final MediatedNativeAdController controller;
    private WeakReference<FBNativeAdResponse> response;

    public FacebookNativeAdListener(MediatedNativeAdController mBC, FBNativeAdResponse response) {
        this.controller = mBC;
        this.response = new WeakReference<FBNativeAdResponse>(response);
    }

    @Override
    public void onError(Ad ad, AdError adError) {
        Clog.d(Clog.mediationLogTag, "Facebook: " + adError.getErrorMessage());

        ResultCode code;

        if (adError.getErrorCode() == AdError.NO_FILL.getErrorCode()) {
            code = ResultCode.getNewInstance(ResultCode.UNABLE_TO_FILL);
        } else if (adError.getErrorCode() == AdError.LOAD_TOO_FREQUENTLY.getErrorCode()) {
            code = ResultCode.getNewInstance(ResultCode.REQUEST_TOO_FREQUENT);
        } else if (adError.getErrorCode() == AdError.INTERNAL_ERROR.getErrorCode()) {
            code = ResultCode.getNewInstance(ResultCode.INTERNAL_ERROR);
        } else if (adError.getErrorCode() == AdError.MISSING_PROPERTIES.getErrorCode()) {
            code = ResultCode.getNewInstance(ResultCode.INVALID_REQUEST);
        } else {
            code = ResultCode.getNewInstance(ResultCode.INTERNAL_ERROR);
        }

        controller.onAdFailed(code);
    }

    @Override
    public void onAdLoaded(Ad ad) {
        FBNativeAdResponse response = this.response.get();
        if (response != null) {
            response.setResources();
            controller.onAdLoaded(response);
        } else {
            controller.onAdFailed(ResultCode.getNewInstance(ResultCode.UNABLE_TO_FILL));
        }

    }

    @Override
    public void onAdClicked(Ad ad) {
        FBNativeAdResponse response = this.response.get();
        if (response != null) {
            NativeAdEventListener listener = response.getListener();
            if (listener != null) {
                listener.onAdWasClicked();
            }
        }
    }

    @Override
    public void onLoggingImpression(Ad ad) {
        Clog.e(Clog.mediationLogTag, "Facebook - onLoggingImpression");
        NativeAdEventListener listener = null;
        FBNativeAdResponse fbNativeAdResponse = this.response.get();
        if (fbNativeAdResponse != null && fbNativeAdResponse.getListener() != null) {
            listener = fbNativeAdResponse.getListener();
        }
        if(controller!=null) {
            controller.onAdImpression(listener);
        }
    }

    @Override
    public void onMediaDownloaded(Ad ad) {
        Clog.d(Clog.mediationLogTag, "Facebook -  onMediaDownloaded");
    }
}
