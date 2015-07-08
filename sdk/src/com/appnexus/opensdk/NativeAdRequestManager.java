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
        if (adRequest != null) {
            adRequest.cancel(true);
            adRequest = null;
        }
        setMediatedAds(null);
        if (controller != null) {
            controller.cancel(true);
            controller = null;
        }
        owner.clear();
    }

    @Override
    public RequestParameters getRequestParams() {
        NativeAdRequest owner = this.owner.get();
        if (owner != null) {
            return owner.getRequestParameters();
        } else {
            return null;
        }
    }

    @Override
    public void failed(ResultCode code) {
        printMediatedClasses();
        NativeAdRequest owner = this.owner.get();
        if (owner != null) {
            owner.getAdDispatcher().onAdFailed(code);
        }
    }

    @Override
    public void onReceiveServerResponse(ServerResponse response) {
        final NativeAdRequest owner = this.owner.get();
        if (owner != null) {
            boolean responseHasAds = (response != null) && response.containsAds();
            boolean ownerHasAds = (getMediatedAds() != null) && !getMediatedAds().isEmpty();

            // no ads in the response and no old ads means no fill
            if (!responseHasAds && !ownerHasAds) {
                Clog.w(Clog.httpRespLogTag, Clog.getString(R.string.response_no_ads));

                owner.getAdDispatcher().onAdFailed(ResultCode.UNABLE_TO_FILL);
                return;
            }

            if (responseHasAds) {
                // if non-mediated ad is overriding the list,
                // this will be null and skip the loop for mediation
                setMediatedAds(response.getMediatedAds());
            }

            // check the latest mediated ads, may have updates from new response
            if ((getMediatedAds() != null) && !getMediatedAds().isEmpty()) {
                MediatedAd mediatedAd = popMediatedAd();
                if ((mediatedAd != null) && (response != null)) {
                    mediatedAd.setExtras(response.getExtras());
                }
                // mediated
                controller = MediatedNativeAdController.create(mediatedAd, NativeAdRequestManager.this);
            } else {
                final ANNativeAdResponse nativeAdResponse = (ANNativeAdResponse) response.getNativeAdResponse();
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
                    public void destroy() {
                        nativeAdResponse.destroy();
                    }
                });

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
}
