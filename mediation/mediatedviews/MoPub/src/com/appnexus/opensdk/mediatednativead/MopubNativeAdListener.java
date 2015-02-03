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

import android.support.annotation.NonNull;
import android.view.View;

import com.appnexus.opensdk.MediatedNativeAdController;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.utils.Clog;
import com.mopub.nativeads.MoPubNative;
import com.mopub.nativeads.NativeErrorCode;
import com.mopub.nativeads.NativeResponse;

import java.lang.ref.WeakReference;

public class MoPubNativeAdListener implements MoPubNative.MoPubNativeNetworkListener, MoPubNative.MoPubNativeEventListener{
    private WeakReference<MoPubNativeAdResponse> response;
    private final MediatedNativeAdController controller;

    public MoPubNativeAdListener(@NonNull MoPubNativeAdResponse response, MediatedNativeAdController mBC) {
        this.response = new WeakReference<MoPubNativeAdResponse>(response);
        this.controller = mBC;
    }

    @Override
    public void onNativeLoad(NativeResponse nativeResponse) {
        MoPubNativeAdResponse response = this.response.get();
        if (response != null) {
            response.setResources(nativeResponse);
        } else {
            controller.onAdFailed(ResultCode.UNABLE_TO_FILL);
            return;
        }
        controller.onAdLoaded();

    }

    @Override
    public void onNativeFail(NativeErrorCode nativeErrorCode) {
        Clog.d(Clog.mediationLogTag, "MoPub: " + nativeErrorCode.toString());

        ResultCode code = ResultCode.INTERNAL_ERROR;

        switch (nativeErrorCode) {
            case EMPTY_AD_RESPONSE:
                code = ResultCode.UNABLE_TO_FILL;
                break;
            case INVALID_JSON:
                break;
            case IMAGE_DOWNLOAD_FAILURE:
                break;
            case INVALID_REQUEST_URL:
                break;
            case UNEXPECTED_RESPONSE_CODE:
                break;
            case SERVER_ERROR_RESPONSE_CODE:
                break;
            case CONNECTION_ERROR:
                code = ResultCode.NETWORK_ERROR;
                break;
            case UNSPECIFIED:
                break;
            case NETWORK_INVALID_REQUEST:
                code = ResultCode.NETWORK_ERROR;
                break;
            case NETWORK_TIMEOUT:
                code = ResultCode.NETWORK_ERROR;
                break;
            case NETWORK_NO_FILL:
                code = ResultCode.UNABLE_TO_FILL;
                break;
            case NETWORK_INVALID_STATE:
                code = ResultCode.NETWORK_ERROR;
                break;
            case NATIVE_ADAPTER_CONFIGURATION_ERROR:
                break;
            case NATIVE_ADAPTER_NOT_FOUND:
                break;
            default:
                Clog.w(Clog.mediationLogTag, "Unhandled Mopub error code: " + nativeErrorCode.toString());
                break;
        }

        controller.onAdFailed(code);
    }

    @Override
    public void onNativeImpression(View view) {
    }

    @Override
    public void onNativeClick(View view) {
        MoPubNativeAdResponse response = this.response.get();
        if (response != null) {
            response.onAdClicked();
        }
    }
}
