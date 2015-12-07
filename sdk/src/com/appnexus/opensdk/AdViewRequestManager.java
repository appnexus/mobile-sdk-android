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

import com.appnexus.opensdk.adresponsedata.BaseAdResponse;
import com.appnexus.opensdk.adresponsedata.CSMAdResponse;
import com.appnexus.opensdk.adresponsedata.RTBAdResponse;
import com.appnexus.opensdk.adresponsedata.SSMAdResponse;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.HTTPGet;
import com.appnexus.opensdk.utils.HTTPResponse;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.vastdata.AdModel;

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
            if(Settings.useUniversalTagV2) {
                handleUTV2Response(response, owner);
            }else{
                handleUTResponse(response, owner);
            }
        }
    }

    private void handleUTResponse(final UTAdResponse response, final AdView owner) {
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
            if (response.getVastAdResponse() != null) {
                // Vast ads
                initiateVastAdView(owner, response.getVastAdResponse());
            } else {
                // Standard ads
                initiateWebview(owner, response);
            }
        }
    }


    private void handleUTV2Response(UTAdResponse response, AdView owner) {
        boolean responseHasAds = (response != null) && response.containsAds();
        boolean ownerHasAds = (getAdList() != null) && !getAdList().isEmpty();

        // no ads in the response and no old ads means no fill
        if (!responseHasAds && !ownerHasAds) {
            Clog.w(Clog.httpRespLogTag, Clog.getString(R.string.response_no_ads));
            owner.getAdDispatcher().onAdFailed(ResultCode.UNABLE_TO_FILL);
            return;
        }

        if (responseHasAds) {
            // if non-mediated ad is overriding the list,
            // this will be null and skip the loop for mediation
            setAdList(response.getAdList());
        }
        // If we're about to dispatch a creative to a banner
        // that has been resized by ad stretching, reset its size
        if (owner.getMediaType().equals(MediaType.BANNER)) {
            BannerAdView bav = (BannerAdView) owner;
            bav.resetContainerIfNeeded();
        }

        if (getAdList() != null && !getAdList().isEmpty()) {
            BaseAdResponse baseAdResponse = popAd();

            if (baseAdResponse.getContentSource().equalsIgnoreCase(UTAdResponse.RTB)) {
                handleRTBResponse(owner, (RTBAdResponse) baseAdResponse);
            } else if (baseAdResponse.getContentSource().equalsIgnoreCase(UTAdResponse.CSM)) {
                handleCSMResponse(owner, (CSMAdResponse) baseAdResponse);
            } else if (baseAdResponse.getContentSource().equalsIgnoreCase(UTAdResponse.SSM)) {
                handleSSMResponse(owner, (SSMAdResponse) baseAdResponse);
            }

        }
    }

    private void handleSSMResponse(final AdView owner, final SSMAdResponse ssmAdResponse) {
        new HTTPGet() {
            @Override
            protected void onPostExecute(HTTPResponse response) {
                if(response != null && response.getSucceeded()) {
                    ssmAdResponse.setAdContent(response.getResponseBody());
                    initiateWebview(owner, ssmAdResponse);
                }else {
                    // Process next available ad response
                    onReceiveUTResponse(null);
                }
            }

            @Override
            protected String getUrl() {
                return ssmAdResponse.getAdUrl();
            }
        }.execute();
    }

    private void handleCSMResponse(AdView owner, CSMAdResponse csmAdResponse) {
        if (owner.getMediaType().equals(MediaType.BANNER)) {
            controller = MediatedBannerAdViewController.create(
                    (Activity) owner.getContext(),
                    AdViewRequestManager.this,
                    csmAdResponse,
                    owner.getAdDispatcher());
        } else if (owner.getMediaType().equals(MediaType.INTERSTITIAL)) {
            controller = MediatedInterstitialAdViewController.create(
                    (Activity) owner.getContext(),
                    AdViewRequestManager.this,
                    csmAdResponse,
                    owner.getAdDispatcher());
        } else {
            Clog.e(Clog.baseLogTag, "Request type can not be identified.");
            owner.getAdDispatcher().onAdFailed(ResultCode.INVALID_REQUEST);
        }
    }

    private void handleRTBResponse(AdView owner, RTBAdResponse rtbAdResponse) {
        if (rtbAdResponse.getVastAdResponse() != null) {
            // Vast ads
            initiateVastAdView(owner, rtbAdResponse.getVastAdResponse());
        } else if(rtbAdResponse.getAdContent() != null){
            // Standard ads
            initiateWebview(owner, rtbAdResponse);
        }else{
            // Process next available ad response
            onReceiveUTResponse(null);
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

    private void initiateWebview(final AdView owner, final BaseAdResponse response) {
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
            public BaseAdResponse getResponseData() {
                return response;
            }

            @Override
            public void destroy() {
                output.destroy();
            }
        });
    }


    private void initiateVastAdView(final AdView owner, final BaseAdResponse response) {
        RTBAdResponse rtbAdResponse = (RTBAdResponse)response;
        final VastVideoView adVideoView = new VastVideoView(owner.getContext(), rtbAdResponse.getVastAdResponse());

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
            public BaseAdResponse getResponseData() {
                return response;
            }

            @Override
            public void destroy() {
                adVideoView.destroy();
            }
        });
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
            public BaseAdResponse getResponseData() {
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
            public BaseAdResponse getResponseData() {
                return null;
            }

            @Override
            public void destroy() {
                output.destroy();
            }
        });
    }



    private void initiateVastAdView(final AdView owner, AdModel vastAdResponse) {
        final VastVideoView adVideoView = new VastVideoView(owner.getContext(), vastAdResponse);

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
            public BaseAdResponse getResponseData() {
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
