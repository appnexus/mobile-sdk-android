/*
 *    Copyright 2020 APPNEXUS INC
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

package com.appnexus.opensdk.csr;

import android.content.Context;
import android.text.TextUtils;

import com.appnexus.opensdk.CSRAd;
import com.appnexus.opensdk.CSRController;
import com.appnexus.opensdk.NativeAdEventListener;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.utils.Clog;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class FBNativeBanner implements CSRAd {
    @Override
    public void requestAd(Context context, String payload, final CSRController mBC, TargetingParameters tp) {
        Clog.d(Clog.csrLogTag,"FBNativeBanner - requestAd called.");
        if (!TextUtils.isEmpty(payload)) {
            String placementID = null;
            try {
                JSONObject payloadJSON = new JSONObject(payload);
                placementID = payloadJSON.getString("placement_id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (!TextUtils.isEmpty(placementID)) {
                final NativeBannerAd nativeBannerAd = new NativeBannerAd(context, placementID);
                nativeBannerAd.loadAd(nativeBannerAd.buildLoadAdConfig().withBid(payload).withAdListener(new NativeAdListener() {
                    WeakReference<FBNativeBannerAdResponse> responseWeakReference = new WeakReference<>(null);

                    @Override
                    public void onMediaDownloaded(Ad ad) {

                    }

                    @Override
                    public void onError(Ad ad, AdError adError) {
                        ResultCode code;
                        Clog.e(Clog.csrLogTag, "FBNativeBanner - received adError " + adError.getErrorCode() + " with message: " + adError.getErrorMessage());

                        if (adError.getErrorCode() == AdError.NO_FILL.getErrorCode()) {
                            code = ResultCode.getNewInstance(ResultCode.UNABLE_TO_FILL);
                        } else if (adError.getErrorCode() == AdError.LOAD_TOO_FREQUENTLY.getErrorCode()) {
                            code = ResultCode.getNewInstance(ResultCode.REQUEST_TOO_FREQUENT);
                        } else if (adError.getErrorCode() == AdError.INTERNAL_ERROR.getErrorCode()) {
                            code = ResultCode.getNewInstance(ResultCode.INTERNAL_ERROR);
                        } else {
                            code = ResultCode.getNewInstance(ResultCode.CUSTOM_ADAPTER_ERROR);
                            code.setMessage("AdError: " + adError.getErrorCode() + " Message: " + adError.getErrorMessage());
                        }

                        mBC.onAdFailed(code);
                    }

                    @Override
                    public void onAdLoaded(Ad ad) {
                        FBNativeBannerAdResponse response = FBNativeBannerAdResponse.createResponse(nativeBannerAd);
                        if (response == null) {
                            Clog.e(Clog.csrLogTag, "FBNativeBanner - Unable to create native response.");
                            mBC.onAdFailed(ResultCode.getNewInstance(ResultCode.INTERNAL_ERROR));
                        } else {
                            Clog.d(Clog.csrLogTag, "onAdLoaded called.");
                            mBC.onAdLoaded(response);
                            responseWeakReference = new WeakReference<>(response);
                        }

                    }

                    @Override
                    public void onAdClicked(Ad ad) {
                        Clog.d(Clog.csrLogTag, "FBNativeBanner - onAdClicked called.");
                        mBC.onAdClicked();
                        if (responseWeakReference.get() != null) {
                            FBNativeBannerAdResponse response = responseWeakReference.get();
                            if (response.nativeAdEventListener != null) {
                                response.nativeAdEventListener.onAdWasClicked();
                            }
                        }
                    }

                    @Override
                    public void onLoggingImpression(Ad ad) {
                        Clog.d(Clog.csrLogTag, "FBNativeBanner - onLoggingImpression called.");
                        NativeAdEventListener listener = null;
                        if (responseWeakReference.get() != null) {
                            FBNativeBannerAdResponse fbNativeBannerAdResponse = responseWeakReference.get();
                            if (fbNativeBannerAdResponse != null) {
                                fbNativeBannerAdResponse.removeExpiryCallbacks();
                                if (fbNativeBannerAdResponse.nativeAdEventListener != null) {
                                    listener = fbNativeBannerAdResponse.nativeAdEventListener;
                                }
                            }
                        }
                        mBC.onAdImpression(listener);
                    }
                }).build());
            } else {
                Clog.e(Clog.csrLogTag, "FBNativeBanner - Unable to retrieve placement id.");
                mBC.onAdFailed(ResultCode.getNewInstance(ResultCode.INVALID_REQUEST));
            }

        } else {
            Clog.e(Clog.csrLogTag, "FBNativeBanner - Payload is empty.");
            mBC.onAdFailed(ResultCode.getNewInstance(ResultCode.INVALID_REQUEST));
        }

    }
}
