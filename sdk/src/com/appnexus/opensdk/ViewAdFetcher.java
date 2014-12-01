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

import android.app.Activity;

import com.appnexus.opensdk.utils.Clog;

class ViewAdFetcher extends AdFetcher {
    private final AdView owner; // assume not null

    ViewAdFetcher(AdView owner) {
        super(owner.requestParameters);
        this.owner = owner;
    }


    @Override
    protected boolean isReadyToStart() {
        return owner.isReadyToStart();
    }

    @Override
    public void failed(AdRequest request) {
        owner.fail(ResultCode.NETWORK_ERROR);
    }

    @Override
    public void onReceiveResponse(final AdResponse response) {
        this.owner.handler.post(new Runnable() {
            @Override
            public void run() {
                boolean responseHasAds = (response != null) && response.containsAds();
                boolean ownerHasAds = (getMediatedAds() != null) && !getMediatedAds().isEmpty();

                // no ads in the response and no old ads means no fill
                if (!responseHasAds && !ownerHasAds) {
                    Clog.w(Clog.httpRespLogTag, Clog.getString(R.string.response_no_ads));
                    owner.fail(ResultCode.UNABLE_TO_FILL);
                    return;
                }

                // If we're about to dispatch a creative to a banner
                // that has been resized by ad stretching, reset its size
                if (owner.isBanner()) {
                    BannerAdView bav = (BannerAdView) owner;
                    bav.resetContainerIfNeeded();
                }

                if (responseHasAds) {
                    // if non-mediated ad is overriding the list,
                    // this will be null and skip the loop for mediation
                    setMediatedAds(response.getMediatedAds());
                }

                // create output - either mediated or AdWebView

                // check if most recent `mediatedAds` is non-empty
                if ((getMediatedAds() != null) && !getMediatedAds().isEmpty()) {
                    MediatedAd mediatedAd = popMediatedAd();
                    if ((mediatedAd != null) && (response != null)) {
                        mediatedAd.setExtras(response.getExtras());
                    }
                    // mediated
                    if (owner.isBanner()) {
                        MediatedBannerAdViewController.create(
                                (Activity) owner.getContext(),
                                owner.mAdFetcher,
                                mediatedAd,
                                owner.getAdDispatcher());
                    } else if (owner.isInterstitial()) {
                        MediatedInterstitialAdViewController.create(
                                (Activity) owner.getContext(),
                                owner.mAdFetcher,
                                mediatedAd,
                                owner.getAdDispatcher());
                    } else {
                        Clog.e(Clog.baseLogTag, "Request type can not be identified.");
                        owner.getAdDispatcher().onAdFailed(ResultCode.INVALID_REQUEST);
                    }
                } else if (response != null) { // null-check response in case
                    // standard ads
                    AdWebView output = new AdWebView(owner);
                    output.loadAd(response);

                    if (owner.isBanner()) {
                        BannerAdView bav = (BannerAdView) owner;
                        if (bav.getExpandsToFitScreenWidth()) {
                            bav.expandToFitScreenWidth(response.getWidth(), response.getHeight(), output);
                        }
                    }
                    owner.getAdDispatcher().onAdLoaded(output, false);
                }
            }
        }
        );
    }
}
