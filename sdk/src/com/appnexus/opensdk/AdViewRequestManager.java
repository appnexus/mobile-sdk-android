/*
 *    Copyright 2017 APPNEXUS INC
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

import com.appnexus.opensdk.tasksmanager.TasksManager;
import com.appnexus.opensdk.ut.UTAdResponse;
import com.appnexus.opensdk.ut.UTConstants;
import com.appnexus.opensdk.ut.UTRequestParameters;
import com.appnexus.opensdk.ut.adresponse.BaseAdResponse;
import com.appnexus.opensdk.ut.adresponse.CSMSDKAdResponse;
import com.appnexus.opensdk.ut.adresponse.CSMVASTAdResponse;
import com.appnexus.opensdk.ut.adresponse.CSRAdResponse;
import com.appnexus.opensdk.ut.adresponse.RTBNativeAdResponse;
import com.appnexus.opensdk.ut.adresponse.RTBVASTAdResponse;
import com.appnexus.opensdk.ut.adresponse.SSMHTMLAdResponse;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings.CountImpression;
import com.appnexus.opensdk.utils.StringUtil;

import java.lang.ref.WeakReference;

public class AdViewRequestManager extends RequestManager {

    private final ANMultiAdRequest anMultiAdRequest;
    private MediatedAdViewController controller;
    private MediatedSSMAdViewController ssmAdViewController;
    private MediatedNativeAdController mediatedNativeAdController;
    private CSRNativeBannerController csrNativeBannerController;
    private AdWebView adWebview;
    private final WeakReference<Ad> owner;
    private BaseAdResponse currentAd;

    public AdViewRequestManager(Ad owner) {
        super();
        this.owner = new WeakReference<Ad>(owner);
        anMultiAdRequest = null;
    }

    public AdViewRequestManager(ANMultiAdRequest anMultiAdRequest) {
        super();
        this.owner = new WeakReference<>(null);
        this.anMultiAdRequest = anMultiAdRequest;
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

        if (mediatedNativeAdController != null) {
            mediatedNativeAdController.cancel(true);
            mediatedNativeAdController = null;
        }

        if (csrNativeBannerController != null) {
            csrNativeBannerController.cancel(true);
            csrNativeBannerController = null;
        }

        if (ssmAdViewController != null) {
            ssmAdViewController = null;
        }
        if (owner != null) {
            owner.clear();
        }
    }

    @Override
    public UTRequestParameters getRequestParams() {
        if (owner != null) {
            Ad owner = this.owner.get();
            if (owner != null) {
                return owner.getRequestParameters();
            }
        }
        return null;
    }

    @Override
    public void failed(final ResultCode code, final ANAdResponseInfo responseInfo) {
        processFailure(code, responseInfo);
    }

    private void processFailure(ResultCode code, ANAdResponseInfo responseInfo) {
        printMediatedClasses();
        Clog.e(Clog.baseLogTag, code.getMessage());

        Ad owner = this.owner.get();
        fireTracker(noAdUrl, Clog.getString(R.string.no_ad_url));
        if (owner != null) {
            owner.getAdDispatcher().onAdFailed(code, responseInfo);
        }
    }

    @Override
    public void continueWaterfall(ResultCode reason) {
        Clog.d(Clog.baseLogTag, "Waterfall continueWaterfall");
        if (getAdList() == null || getAdList().isEmpty()) {
            failed(reason, currentAd != null ? currentAd.getAdResponseInfo() : null);
        } else {
            // Process next available ad response
            processNextAd();
        }
    }

    @Override
    public void nativeRenderingFailed() {
        if (currentAd != null && currentAd instanceof RTBNativeAdResponse) {
            RTBNativeAdResponse response = (RTBNativeAdResponse) currentAd;
            processNativeAd(response.getNativeAdResponse(), currentAd);
        }
    }

    @Override
    public void onReceiveAd(AdResponse ad) {
        printMediatedClasses();
        if (controller != null) {
            // do not hold a reference of current mediated ad controller after ad is loaded
            controller = null;
        }
        if (mediatedNativeAdController != null) {
            // do not hold a reference of current mediated ad controller after ad is loaded
            mediatedNativeAdController = null;
        }
        if (ssmAdViewController != null) {
            // do not hold a reference of current ssm mediated ad controller after ad is loaded
            ssmAdViewController = null;
        }
        if (csrNativeBannerController != null) {
            csrNativeBannerController = null;
        }
        Ad owner = this.owner.get();
        if (owner != null) {
            if (ad.getMediaType().equals(MediaType.BANNER)) {
                BannerAdView bav = (BannerAdView) owner;
                if (bav.getExpandsToFitScreenWidth() || bav.getResizeAdToFitContainer()) {
                    int width = ad.getResponseData().getWidth() <= 1 ? bav.getRequestParameters().getPrimarySize().width() : ad.getResponseData().getWidth();
                    int height = ad.getResponseData().getHeight() <= 1 ? bav.getRequestParameters().getPrimarySize().height() : ad.getResponseData().getHeight();
                    if (bav.getExpandsToFitScreenWidth()) {
                        bav.expandToFitScreenWidth(width, height, ad.getDisplayable().getView());
                    }
                    if (bav.getResizeAdToFitContainer()) {
                        bav.resizeViewToFitContainer(width, height, ad.getDisplayable().getView());
                    }
                }
                fireImpressionTrackerEarly (bav, ad.getResponseData());
            }
            ((AdDispatcher) owner.getAdDispatcher()).onAdLoaded(ad);
        } else {
            ad.destroy();
        }
    }

    @Override
    public void onReceiveUTResponse(UTAdResponse response) {
        super.onReceiveUTResponse(response);
        Clog.d(Clog.baseLogTag, "onReceiveUTResponse");
        processUTResponse(response);
    }

    private void fireImpressionTrackerEarly (AdView adView, BaseAdResponse response) {
        if(adView.getEffectiveImpressionCountingMethod() == CountImpression.ON_LOAD ||
                (adView.getEffectiveImpressionCountingMethod() == CountImpression.LAZY_LOAD &&
                        adView.isWebviewActivated() && response.getAdType().equalsIgnoreCase(UTConstants.AD_TYPE_BANNER))){
            if(response.getImpressionURLs() != null && response.getImpressionURLs().size() > 0){
                adView.impressionTrackers = response.getImpressionURLs();
                adView.fireImpressionTracker();
                Clog.e(Clog.httpRespLogTag, "Impression URL fired when we have a valid ad & the view is created");
                //remove the impression trackers else will fire again in the AdView logic
                response.setImpressionURLs(null);
            }
        }
    }

    private void processUTResponse(UTAdResponse response) {
        final Ad owner = this.owner.get();
        if ((owner != null) && doesAdListExists(response)) {
            setAdList(response.getAdList());
            processNextAd();
        } else {
            Clog.w(Clog.httpRespLogTag, Clog.getString(R.string.response_no_ads));
            failed(ResultCode.getNewInstance(ResultCode.UNABLE_TO_FILL), response.getAdResponseInfo());
        }
    }

    private boolean doesAdListExists(UTAdResponse response) {
        return ((response != null) && (response.getAdList() != null) && (!response.getAdList().isEmpty()));
    }

    private void processNextAd() {
        // If we're about to dispatch a creative to a banner
        // that has been resized by ad stretching, reset its size
        final Ad owner = this.owner.get();
        if ((owner != null) && getAdList() != null && !getAdList().isEmpty()) {
            final BaseAdResponse baseAdResponse = popAd();

            if (baseAdResponse == null) {
                Clog.e(Clog.baseLogTag, "processNextAd failed:: invalid Ad response:: " + baseAdResponse);
                continueWaterfall(ResultCode.getNewInstance(ResultCode.INTERNAL_ERROR));
                return;
            }

            this.currentAd = baseAdResponse;

            if (UTConstants.RTB.equalsIgnoreCase(baseAdResponse.getContentSource())) {
                handleRTBResponse(owner, baseAdResponse);
            } else if (UTConstants.CSM.equalsIgnoreCase(baseAdResponse.getContentSource())) {
                if (SDKSettings.isBackgroundThreadingEnabled()) {
                    TasksManager.getInstance().executeOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            handleCSMResponse(owner, (CSMSDKAdResponse) baseAdResponse);
                        }
                    });
                } else {
                    handleCSMResponse(owner, (CSMSDKAdResponse) baseAdResponse);
                }
            } else if (UTConstants.SSM.equalsIgnoreCase(baseAdResponse.getContentSource())) {
                handleSSMResponse((AdView) owner, (SSMHTMLAdResponse) baseAdResponse);
            } else if (UTConstants.CSR.equalsIgnoreCase(baseAdResponse.getContentSource())) {
                handleCSRResponse(owner, (CSRAdResponse) baseAdResponse);
            } else if (UTConstants.CSM_VIDEO.equalsIgnoreCase(baseAdResponse.getContentSource())) {
                handleCSMVASTAdResponse(owner, (CSMVASTAdResponse) baseAdResponse);
            } else {
                Clog.e(Clog.baseLogTag, "processNextAd failed:: invalid content source:: " + baseAdResponse.getContentSource());
                continueWaterfall(ResultCode.getNewInstance(ResultCode.INTERNAL_ERROR));
            }
        }
    }

    private void handleNativeResponse(final Ad owner, final BaseAdResponse baseAdResponse) {
        final ANNativeAdResponse nativeAdResponse = ((RTBNativeAdResponse) baseAdResponse).getNativeAdResponse();

        if (owner != null) {
            nativeAdResponse.setLoadsInBackground(owner.getRequestParameters().getLoadsInBackground());
            nativeAdResponse.setClickThroughAction(owner.getRequestParameters().getClickThroughAction());
        }

        if (owner instanceof BannerAdView && ((BannerAdView) owner).isNativeRenderingEnabled() && nativeAdResponse.getRendererUrl().length() > 0) {
            initiateWebview(owner, baseAdResponse);
        } else {
            processNativeAd(nativeAdResponse, baseAdResponse);
        }
    }

    protected void processNativeAd(final ANNativeAdResponse nativeAdResponse, final BaseAdResponse baseAdResponse) {

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
                return baseAdResponse;
            }

            @Override
            public void destroy() {
                nativeAdResponse.destroy();
            }
        });
    }


    private void handleRTBResponse(Ad ownerAd, BaseAdResponse rtbAdResponse) {

        if (rtbAdResponse instanceof RTBVASTAdResponse && !(ownerAd instanceof BannerAdView)) {
            if (rtbAdResponse.getAdContent() != null) {
                if (UTConstants.AD_TYPE_VIDEO.equalsIgnoreCase(rtbAdResponse.getAdType())) {
                    // Vast ads
                    handleRTBVASTResponse(ownerAd, (RTBVASTAdResponse) rtbAdResponse);
                } else {
                    continueWaterfall(ResultCode.getNewInstance(ResultCode.UNABLE_TO_FILL));
                }
            } else {
                continueWaterfall(ResultCode.getNewInstance(ResultCode.UNABLE_TO_FILL));
            }
        } else if (rtbAdResponse instanceof RTBNativeAdResponse) {
            handleNativeResponse(ownerAd, rtbAdResponse);
        } else {
            if (rtbAdResponse.getAdContent() != null) {

                if (UTConstants.AD_TYPE_BANNER.equalsIgnoreCase(rtbAdResponse.getAdType()) ||
                        UTConstants.AD_TYPE_VIDEO.equalsIgnoreCase(rtbAdResponse.getAdType())) {


                    // Fire Notify URL - Currently only for Video Ad's
                    if (UTConstants.AD_TYPE_VIDEO.equalsIgnoreCase(rtbAdResponse.getAdType())) {
                        fireTracker(((RTBVASTAdResponse) rtbAdResponse).getNotifyUrl(), Clog.getString(R.string.notify_url));
                    }

                    // Standard ads or Video Ads
                    if (ownerAd instanceof BannerAdView && ((BannerAdView)ownerAd).isLazyWebviewInactive() && UTConstants.AD_TYPE_BANNER.equalsIgnoreCase(rtbAdResponse.getAdType())) {
                        ((AdDispatcher)ownerAd.getAdDispatcher()).onLazyAdLoaded(currentAd.getAdResponseInfo());
                    } else {
                        if (ownerAd instanceof AdView) {
                            initiateWebview(ownerAd, rtbAdResponse);
                            AdView owner = (AdView) ownerAd;
                            fireImpressionTrackerEarly(owner, rtbAdResponse);
                        } else {
                            Clog.e(Clog.baseLogTag, "AdType can not be identified.");
                            continueWaterfall(ResultCode.getNewInstance(ResultCode.INVALID_REQUEST));
                        }
                    }
                } else {
                    Clog.e(Clog.baseLogTag, "handleRTBResponse failed:: invalid adType::" + rtbAdResponse.getAdType());
                    continueWaterfall(ResultCode.getNewInstance(ResultCode.INTERNAL_ERROR));
                }
            } else {
                continueWaterfall(ResultCode.getNewInstance(ResultCode.UNABLE_TO_FILL));
            }
        }
    }

    private void handleRTBVASTResponse(final Ad owner, final RTBVASTAdResponse rtbAdResponse) {

        if (!StringUtil.isEmpty(rtbAdResponse.getAdContent())) {
            fireNotifyUrlForVideo(rtbAdResponse);
            if (rtbAdResponse != null && rtbAdResponse.getAdContent() != null) {
                owner.getMultiAd().initiateVastAdView(rtbAdResponse, this);
            } else {
                continueWaterfall(ResultCode.getNewInstance(ResultCode.UNABLE_TO_FILL));
            }
        }
    }

    private void handleCSMVASTAdResponse(Ad owner, CSMVASTAdResponse csmvastAdResponse) {
        if (csmvastAdResponse != null && csmvastAdResponse.getAdJSONContent() != null) {
            if (UTConstants.AD_TYPE_VIDEO.equalsIgnoreCase(csmvastAdResponse.getAdType())) {
                // @NOTE no need to fire notify URL here it is taken care by ASTMediationManager.js
                owner.getMultiAd().initiateVastAdView(csmvastAdResponse, this);
            } else {
                continueWaterfall(ResultCode.getNewInstance(ResultCode.UNABLE_TO_FILL));
            }
        } else {
            continueWaterfall(ResultCode.getNewInstance(ResultCode.UNABLE_TO_FILL));
        }
    }


    private void handleCSMResponse(Ad ownerAd, final CSMSDKAdResponse csmSdkAdResponse) {
        Clog.i(Clog.baseLogTag, "Mediation type is CSM, passing it to MediatedAdViewController.");
        if (csmSdkAdResponse.getAdType().equals(UTConstants.AD_TYPE_NATIVE)) {
            mediatedNativeAdController = MediatedNativeAdController.create(csmSdkAdResponse,
                    AdViewRequestManager.this);

        } else {
            AdView owner = (AdView) ownerAd;
            if (owner.getMediaType().equals(MediaType.BANNER)) {
                controller = MediatedBannerAdViewController.create(
                        (Activity) owner.getContext(),
                        AdViewRequestManager.this,
                        csmSdkAdResponse,
                        owner.getAdDispatcher());

            } else if (owner.getMediaType().equals(MediaType.INTERSTITIAL)) {
                controller = MediatedInterstitialAdViewController.create(
                        (Activity) owner.getContext(),
                        AdViewRequestManager.this,
                        csmSdkAdResponse,
                        owner.getAdDispatcher());
            } else {
                Clog.e(Clog.baseLogTag, "MediaType type can not be identified.");
                continueWaterfall(ResultCode.getNewInstance(ResultCode.INVALID_REQUEST));
            }
        }
    }

    private void handleCSRResponse(Ad ownerAd, CSRAdResponse csrAdResponse) {
        Clog.i(Clog.baseLogTag, "Content source type is CSR, passing it to CSRHandler.");
        csrNativeBannerController = new CSRNativeBannerController(csrAdResponse, AdViewRequestManager.this);
    }

    private void handleSSMResponse(final AdView owner, final SSMHTMLAdResponse ssmHtmlAdResponse) {
        Clog.i(Clog.baseLogTag, "Mediation type is SSM, passing it to SSMAdViewController.");
        ssmAdViewController = MediatedSSMAdViewController.create(owner, AdViewRequestManager.this, ssmHtmlAdResponse);
    }

    private void initiateWebview(final Ad owner, final BaseAdResponse response) {
        if (SDKSettings.isBackgroundThreadingEnabled()) {
            TasksManager.getInstance().executeOnMainThread(new Runnable() {
                @Override
                public void run() {
                    adWebview = new AdWebView((AdView) owner, AdViewRequestManager.this);
                    adWebview.loadAd(response);
                }
            });
        } else {
            adWebview = new AdWebView((AdView) owner, AdViewRequestManager.this);
            adWebview.loadAd(response);

        }
    }

    public ANMultiAdRequest getMultiAdRequest() {
        return anMultiAdRequest;
    }

    protected void loadLazyAd() {
        final Ad adOwner = owner.get();

        if (adOwner == null || currentAd == null) {
            failed(ResultCode.getNewInstance(ResultCode.INTERNAL_ERROR), null);
            return;
        }

        initiateWebview(adOwner, currentAd);
        if (adOwner != null && adOwner instanceof AdView) {
            TasksManager.getInstance().executeOnMainThread(new Runnable() {
                @Override
                public void run() {
                    fireImpressionTrackerEarly((AdView) adOwner, currentAd);
                }
            });
        }
    }

}