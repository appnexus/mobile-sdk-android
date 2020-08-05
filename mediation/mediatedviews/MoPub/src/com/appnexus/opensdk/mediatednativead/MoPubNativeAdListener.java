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

import android.view.View;

import com.appnexus.opensdk.MediatedNativeAdController;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.utils.Clog;
import com.mopub.nativeads.MoPubNative;
import com.mopub.nativeads.NativeAd;
import com.mopub.nativeads.NativeErrorCode;


import java.lang.ref.WeakReference;


public class MoPubNativeAdListener implements MoPubNative.MoPubNativeNetworkListener, NativeAd.MoPubNativeEventListener {
    private WeakReference<MoPubNativeAdResponse> response;
    private final MediatedNativeAdController controller;

    public MoPubNativeAdListener(MediatedNativeAdController mBC) {
        this.controller = mBC;
    }

    @Override
    public void onNativeLoad(NativeAd nativeResponse) {
        MoPubNativeAdResponse response = new MoPubNativeAdResponse();
        nativeResponse.setMoPubNativeEventListener(this);
        if (response.setResources(nativeResponse)) {
            this.response = new WeakReference<MoPubNativeAdResponse>(response);
            controller.onAdLoaded(response);
        } else {
            controller.onAdFailed(ResultCode.getNewInstance(ResultCode.INTERNAL_ERROR));
        }
    }

    @Override
    public void onNativeFail(NativeErrorCode nativeErrorCode) {
        Clog.d(Clog.mediationLogTag, "MoPub: " + nativeErrorCode.toString());

        ResultCode code = ResultCode.getNewInstance(ResultCode.INTERNAL_ERROR);

        switch (nativeErrorCode) {
            case EMPTY_AD_RESPONSE:
                code = ResultCode.getNewInstance(ResultCode.UNABLE_TO_FILL);
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
                code = ResultCode.getNewInstance(ResultCode.NETWORK_ERROR);
                break;
            case UNSPECIFIED:
                break;
            case NETWORK_INVALID_REQUEST:
                code = ResultCode.getNewInstance(ResultCode.NETWORK_ERROR);
                break;
            case NETWORK_TIMEOUT:
                code = ResultCode.getNewInstance(ResultCode.NETWORK_ERROR);
                break;
            case NETWORK_NO_FILL:
                code = ResultCode.getNewInstance(ResultCode.UNABLE_TO_FILL);
                break;
            case NETWORK_INVALID_STATE:
                code = ResultCode.getNewInstance(ResultCode.NETWORK_ERROR);
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
    public void onImpression(View view) {
        if(controller!=null) {
            controller.onAdImpression();
        }
    }

    @Override
    public void onClick(View view) {
        if (this.response != null) {
            MoPubNativeAdResponse response = this.response.get();
            if (response != null) {
                response.onAdClicked();
            }
        }
    }
}