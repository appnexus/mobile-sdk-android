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

package com.appnexus.opensdk;

import com.appnexus.opensdk.utils.Clog;


class NativeAdFetcher extends AdFetcher {
    private final NativeAdRequest request;

    NativeAdFetcher(NativeAdRequest request) {
        super(request.getRequestParameters());
        this.request = request;
    }

    @Override
    protected boolean isReadyToStart() {
        return request.getListener() != null;
    }

    @Override
    public void failed(AdRequest request) {
        this.request.getDispatcher().onAdFailed(ResultCode.NETWORK_ERROR);
    }

    @Override
    public void onReceiveResponse(AdResponse response) {
        boolean responseHasAds = (response != null) && response.containsAds();
        boolean ownerHasAds = (getMediatedAds() != null) && !getMediatedAds().isEmpty();

        // no ads in the response and no old ads means no fill
        if (!responseHasAds && !ownerHasAds) {
            Clog.w(Clog.httpRespLogTag, Clog.getString(R.string.response_no_ads));

            request.getDispatcher().onAdFailed(ResultCode.UNABLE_TO_FILL);
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
            MediatedNativeAdController.create(mediatedAd, this, request.getDispatcher());
        } else {
            ANNativeAdResponse nativeAdResponse = (ANNativeAdResponse) response.getNativeAdReponse();
            nativeAdResponse.openNativeBrowser(request.getOpensNativeBrowser());
            request.getDispatcher().onAdLoaded(response.getNativeAdReponse());
        }
    }

}
