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

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;

import com.appnexus.opensdk.utils.Clog;

import java.lang.ref.WeakReference;

class AdViewRequestManager extends RequestManager {
    private final WeakReference<AdView> owner;
    private MediatedAdViewController controller;

    AdViewRequestManager(AdView owner) {
        super();
        this.owner = new WeakReference<AdView>(owner);
    }

    @Override
    public void execute() {
        if(owner.get() instanceof InterstitialAdView){
            utAdRequest = new UTAdRequest(this);
            markLatencyStart();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                utAdRequest.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                utAdRequest.execute();
            }
        }else {
            super.execute();
        }
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
    }

    @Override
    public RequestParameters getRequestParams() {
        AdView owner = this.owner.get();
        if (owner != null) {
            return owner.requestParameters;
        } else {
            return null;
        }

    }

    @Override
    public void failed(ResultCode code) {
        printMediatedClasses();
        AdView owner = this.owner.get();
        if (owner != null) {
            owner.getAdDispatcher().onAdFailed(code);
        }
    }

    @Override
    public void onReceiveUTResponse(final UTAdResponse response) {
        final AdView owner = this.owner.get();
        if (owner != null) {
            owner.handler.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            boolean responseHasAds = (response != null) && response.containsAds();
                            boolean ownerHasAds = (getMediatedAds() != null) && !getMediatedAds().isEmpty();

                            // no ads in the response and no old ads means no fill
                            if (!responseHasAds && !ownerHasAds) {
                                Clog.w(Clog.httpRespLogTag, Clog.getString(R.string.response_no_ads));
                                owner.getAdDispatcher().onAdFailed(ResultCode.UNABLE_TO_FILL);
                                return;
                            }

                            // If we're about to dispatch a creative to a banner
                            // that has been resized by ad stretching, reset its size
                            if (owner.getMediaType().equals(MediaType.BANNER)) {
                                BannerAdView bav = (BannerAdView) owner;
                                bav.resetContainerIfNeeded();
                            }

                            if (response != null) { // null-check response

                                /**
                                 * TODO: Need to revisit this condition
                                 */
                                if (response.getVastAdResponse() != null) {
                                    // Vast ads
                                    Clog.i(Clog.baseLogTag, "initiate VideoView");
                                    initiateVastAdView(owner, response);
                                } else {
                                    // Standard ads
                                    Clog.i(Clog.baseLogTag, "initiate Webview");
                                    initiateWebview(owner, response);
                                }
                            }
                        }
                    }
            );
        }
    }

    @Override
    public void onReceiveServerResponse(final ServerResponse response) {
        final AdView owner = this.owner.get();
        if (owner != null) {
            owner.handler.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            boolean responseHasAds = (response != null) && response.containsAds();
                            boolean ownerHasAds = (getMediatedAds() != null) && !getMediatedAds().isEmpty();

                            // no ads in the response and no old ads means no fill
                            if (!responseHasAds && !ownerHasAds) {
                                Clog.w(Clog.httpRespLogTag, Clog.getString(R.string.response_no_ads));
                                owner.getAdDispatcher().onAdFailed(ResultCode.UNABLE_TO_FILL);
                                return;
                            }

                            // If we're about to dispatch a creative to a banner
                            // that has been resized by ad stretching, reset its size
                            if (owner.getMediaType().equals(MediaType.BANNER)) {
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
                                if (owner.getMediaType().equals(MediaType.BANNER)) {
                                    controller = MediatedBannerAdViewController.create(
                                            (Activity) owner.getContext(),
                                            AdViewRequestManager.this,
                                            mediatedAd,
                                            owner.getAdDispatcher());
                                } else if (owner.getMediaType().equals(MediaType.INTERSTITIAL)) {
                                    controller = MediatedInterstitialAdViewController.create(
                                            (Activity) owner.getContext(),
                                            AdViewRequestManager.this,
                                            mediatedAd,
                                            owner.getAdDispatcher());
                                } else {
                                    Clog.e(Clog.baseLogTag, "Request type can not be identified.");
                                    owner.getAdDispatcher().onAdFailed(ResultCode.INVALID_REQUEST);
                                }
                            } else if (response != null) { // null-check response

                                initiateWebview(owner, response);
                            }
                        }
                    }
            );
        }
    }

    private void initiateWebview(final AdView owner, UTAdResponse response) {
        final AdWebView output = new AdWebView(owner);
        output.loadAd(response);

        if (owner.getMediaType().equals(MediaType.BANNER)) {
            BannerAdView bav = (BannerAdView) owner;
            if (bav.getExpandsToFitScreenWidth()) {
                bav.expandToFitScreenWidth(response.getWidth(), response.getHeight(), output);
            }
        }

        onReceiveAd(new AdResponse() {
            @Override
            public MediaType getMediaType() {
                return owner.getMediaType();
            }

            @Override
            public boolean isMediated() {
                return false;
            }

            @Override
            public Displayable getDisplayable() {
                return output;
            }

            @Override
            public NativeAdResponse getNativeAdResponse() {
                return null;
            }

            @Override
            public void destroy() {
                output.destroy();
            }
        });
    }


    private void initiateWebview(final AdView owner, ServerResponse response) {
        final AdWebView output = new AdWebView(owner);
        output.loadAd(response);

        if (owner.getMediaType().equals(MediaType.BANNER)) {
            BannerAdView bav = (BannerAdView) owner;
            if (bav.getExpandsToFitScreenWidth()) {
                bav.expandToFitScreenWidth(response.getWidth(), response.getHeight(), output);
            }
        }

        onReceiveAd(new AdResponse() {
            @Override
            public MediaType getMediaType() {
                return owner.getMediaType();
            }

            @Override
            public boolean isMediated() {
                return false;
            }

            @Override
            public Displayable getDisplayable() {
                return output;
            }

            @Override
            public NativeAdResponse getNativeAdResponse() {
                return null;
            }

            @Override
            public void destroy() {
                output.destroy();
            }
        });
    }



    private void initiateVastAdView(final AdView owner, UTAdResponse response) {
        final VastVideoView adVideoView = new VastVideoView(owner.getContext(), response.getVastAdResponse());

        onReceiveAd(new AdResponse() {
            @Override
            public MediaType getMediaType() {
                return owner.getMediaType();
            }

            @Override
            public boolean isMediated() {
                return false;
            }

            @Override
            public Displayable getDisplayable() {
                return adVideoView;
            }

            @Override
            public NativeAdResponse getNativeAdResponse() {
                return null;
            }

            @Override
            public void destroy() {
                adVideoView.destroy();
            }
        });
    }

    @Override
    public void onReceiveAd(AdResponse ad) {
        printMediatedClasses();
        if (controller != null) {
            // do not hold a reference of current mediated ad controller after ad is loaded
            controller = null;
        }
        AdView owner = this.owner.get();
        if (owner != null) {
            owner.getAdDispatcher().onAdLoaded(ad);
        } else {
            ad.destroy();
        }
    }
}
