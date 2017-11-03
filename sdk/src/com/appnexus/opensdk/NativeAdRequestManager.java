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

package com.appnexus.opensdk;

import com.appnexus.opensdk.ut.UTAdResponse;
import com.appnexus.opensdk.ut.UTConstants;
import com.appnexus.opensdk.ut.UTRequestParameters;
import com.appnexus.opensdk.ut.adresponse.BaseAdResponse;
import com.appnexus.opensdk.ut.adresponse.CSMSDKAdResponse;
import com.appnexus.opensdk.ut.adresponse.RTBNativeAdResponse;
import com.appnexus.opensdk.utils.Clog;

import java.lang.ref.WeakReference;

class NativeAdRequestManager extends RequestManager {
    private final WeakReference<NativeAdRequest> owner;
    private MediatedNativeAdController controller;


    NativeAdRequestManager(NativeAdRequest owner) {
        super();
        this.owner = new WeakReference<NativeAdRequest>(owner);
    }

    @Override
    public void cancel() {
        if (utAdRequest != null) {
            utAdRequest.cancel(true);
            utAdRequest = null;
        }
        setAdList(null);

        if (controller != null) {
            controller.cancel(true);
            controller = null;
        }
        owner.clear();
    }

    @Override
    public UTRequestParameters getRequestParams() {
        NativeAdRequest owner = this.owner.get();
        if (owner != null) {
            return owner.getRequestParameters();
        } else {
            return null;
        }
    }

    @Override
    public void continueWaterfall(ResultCode reason) {
        Clog.d(Clog.baseLogTag,"Waterfall continueWaterfall");
        if(getAdList() == null || getAdList().isEmpty()){
            failed(reason);
        }else {
            // Process next available ad response
            processNextAd();
        }
    }

    @Override
    public void failed(ResultCode code) {
        printMediatedClasses();
        fireNoAdTracker(noAdUrl, Clog.getString(R.string.no_ad_url));
        NativeAdRequest owner = this.owner.get();
        if (owner != null) {
            owner.getAdDispatcher().onAdFailed(code);
        }
    }

    @Override
    public void onReceiveUTResponse(UTAdResponse response) {
        super.onReceiveUTResponse(response);
        final NativeAdRequest owner = this.owner.get();
        if (owner != null && response != null) {
            if(response.getAdList() != null && !response.getAdList().isEmpty()){
                setAdList(response.getAdList());
                processNextAd();
            } else {
                Clog.w(Clog.httpRespLogTag, Clog.getString(R.string.response_no_ads));
                failed(ResultCode.UNABLE_TO_FILL);
            }
        }
    }

    @Override
    public void onReceiveAd(AdResponse ad) {
        printMediatedClasses();
        if (controller != null) {
            // do not hold a reference of current mediated ad controller after ad is loaded
            controller = null;
        }
        NativeAdRequest owner = this.owner.get();
        if (owner != null) {
            owner.getAdDispatcher().onAdLoaded(ad);
        } else {
            ad.destroy();
        }
    }

    private void processNextAd() {
        if (getAdList() != null && !getAdList().isEmpty()) {
            BaseAdResponse baseAdResponse = popAd();
            final NativeAdRequest owner = this.owner.get();
            if (owner != null) {
                if (baseAdResponse instanceof RTBNativeAdResponse) {
                    final ANNativeAdResponse nativeAdResponse = ((RTBNativeAdResponse) baseAdResponse).getNativeAdResponse();
                    nativeAdResponse.openNativeBrowser(owner.getOpensNativeBrowser());
                    onReceiveAd(new AdResponse() {
                        @Override
                        public MediaType getMediaType() {
                            return MediaType.NATIVE;
                        }

                        @Override
                        public boolean isMediated() {
                            return false;
                        }

                        @Override
                        public Displayable getDisplayable() {
                            return null;
                        }

                        @Override
                        public NativeAdResponse getNativeAdResponse() {
                            return nativeAdResponse;
                        }

                        @Override
                        public BaseAdResponse getResponseData() {
                            return null;
                        }

                        @Override
                        public void destroy() {
                            nativeAdResponse.destroy();
                        }
                    });

                } else if (baseAdResponse.getContentSource().equalsIgnoreCase(UTConstants.CSM)) {
                    controller = MediatedNativeAdController.create((CSMSDKAdResponse) baseAdResponse, NativeAdRequestManager.this);
                } else {
                    Clog.e(Clog.baseLogTag, "processNextAd failed:: invalid content source::" + baseAdResponse.getContentSource());
                    continueWaterfall(ResultCode.INVALID_REQUEST);
                }
            }
        }

    }


}