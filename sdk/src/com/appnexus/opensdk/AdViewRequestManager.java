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

import com.appnexus.opensdk.ut.UTAdResponse;
import com.appnexus.opensdk.ut.UTConstants;
import com.appnexus.opensdk.ut.UTRequestParameters;
import com.appnexus.opensdk.ut.adresponse.BaseAdResponse;
import com.appnexus.opensdk.ut.adresponse.CSMSDKAdResponse;
import com.appnexus.opensdk.ut.adresponse.RTBVASTAdResponse;
import com.appnexus.opensdk.ut.adresponse.SSMHTMLAdResponse;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.HTTPGet;
import com.appnexus.opensdk.utils.HTTPResponse;

import java.lang.ref.WeakReference;

class AdViewRequestManager extends RequestManager {

    private MediatedAdViewController controller;
    private MediatedSSMAdViewController ssmAdViewController;
    private final WeakReference<AdView> owner;

    public AdViewRequestManager(AdView owner) {
        super();
        this.owner = new WeakReference<AdView>(owner);
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

        if (ssmAdViewController != null) {
            ssmAdViewController = null;
        }
        owner.clear();
    }

    @Override
    public UTRequestParameters getRequestParams() {
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
        fireTracker(noAdUrl, Clog.getString(R.string.no_ad_url));
        if (owner != null) {
            owner.getAdDispatcher().onAdFailed(code);
        }
    }

    @Override
    public void continueWaterfall(ResultCode reason) {
        Clog.d(Clog.baseLogTag, "Waterfall continueWaterfall");
        if (getAdList() == null || getAdList().isEmpty()) {
            failed(reason);
        } else {
            // Process next available ad response
            processNextAd();
        }
    }

    @Override
    public void onReceiveAd(AdResponse ad) {
        printMediatedClasses();
        if (controller != null) {
            // do not hold a reference of current mediated ad controller after ad is loaded
            controller = null;
        }
        if (ssmAdViewController != null) {
            // do not hold a reference of current ssm mediated ad controller after ad is loaded
            ssmAdViewController = null;
        }
        AdView owner = this.owner.get();
        if (owner != null) {
            owner.getAdDispatcher().onAdLoaded(ad);
        } else {
            ad.destroy();
        }
    }

    @Override
    public void onReceiveUTResponse(UTAdResponse response) {
        super.onReceiveUTResponse(response);
        final AdView owner = this.owner.get();
        if (owner != null && response != null && response.getAdList() != null && !response.getAdList().isEmpty()) {
            setAdList(response.getAdList());
            processNextAd();
        } else {
            Clog.w(Clog.httpRespLogTag, Clog.getString(R.string.response_no_ads));
            failed(ResultCode.UNABLE_TO_FILL);
        }
    }

    private void processNextAd() {
        // If we're about to dispatch a creative to a banner
        // that has been resized by ad stretching, reset its size
        AdView owner = this.owner.get();
        if (owner != null && getAdList() != null && !getAdList().isEmpty()) {
            BaseAdResponse baseAdResponse = popAd();
            if (baseAdResponse.getContentSource().equalsIgnoreCase(UTConstants.RTB)) {
                handleRTBResponse(owner, baseAdResponse);
            } else if (baseAdResponse.getContentSource().equalsIgnoreCase(UTConstants.CSM)) {
                handleCSMResponse(owner, (CSMSDKAdResponse) baseAdResponse);
            } else if (baseAdResponse.getContentSource().equalsIgnoreCase(UTConstants.SSM)) {
                handleSSMResponse(owner, (SSMHTMLAdResponse) baseAdResponse);
            } else {
                Clog.e(Clog.baseLogTag, "processNextAd failed:: invalid content source:: " + baseAdResponse.getContentSource());
                continueWaterfall(ResultCode.INTERNAL_ERROR);
            }
        }
    }


    private void handleRTBResponse(AdView owner, BaseAdResponse rtbAdResponse) {
        if (rtbAdResponse.getAdContent() != null) {
            if (UTConstants.AD_TYPE_BANNER.equalsIgnoreCase(rtbAdResponse.getAdType()) ||
                    UTConstants.AD_TYPE_VIDEO.equalsIgnoreCase(rtbAdResponse.getAdType())) {


                // Fire Notify URL - Currently only for Video Ad's
                if (UTConstants.AD_TYPE_VIDEO.equalsIgnoreCase(rtbAdResponse.getAdType())) {
                    fireTracker(((RTBVASTAdResponse)rtbAdResponse).getNotifyUrl(), Clog.getString(R.string.notify_url));
                }

                // Standard ads or Video Ads
                initiateWebview(owner, rtbAdResponse);
            } else {
                Clog.e(Clog.baseLogTag, "handleRTBResponse failed:: invalid adType::" + rtbAdResponse.getAdType());
                continueWaterfall(ResultCode.INTERNAL_ERROR);
            }
        } else {
            continueWaterfall(ResultCode.UNABLE_TO_FILL);
        }
    }


    private void handleCSMResponse(AdView owner, CSMSDKAdResponse csmSdkAdResponse) {
        Clog.i(Clog.baseLogTag, "Mediation type is CSM, passing it to MediatedAdViewController.");
        if (owner.getMediaType().equals(MediaType.INTERSTITIAL)) {
            controller = MediatedInterstitialAdViewController.create(
                    (Activity) owner.getContext(),
                    AdViewRequestManager.this,
                    csmSdkAdResponse,
                    owner.getAdDispatcher());
        } else if (owner.getMediaType().equals(MediaType.BANNER)) {
            controller = MediatedBannerAdViewController.create(
                    (Activity) owner.getContext(),
                    AdViewRequestManager.this,
                    csmSdkAdResponse,
                    owner.getAdDispatcher());

        } else {
            Clog.e(Clog.baseLogTag, "MediaType type can not be identified.");
            continueWaterfall(ResultCode.INVALID_REQUEST);
        }
    }

    private void handleSSMResponse(final AdView owner, final SSMHTMLAdResponse ssmHtmlAdResponse) {
        Clog.i(Clog.baseLogTag, "Mediation type is SSM, passing it to SSMAdViewController.");
        ssmAdViewController = MediatedSSMAdViewController.create(owner, AdViewRequestManager.this, ssmHtmlAdResponse);
    }

    private void initiateWebview(final AdView owner, final BaseAdResponse response) {
        final AdWebView output = new AdWebView(owner, AdViewRequestManager.this);
        output.loadAd(response);

        if (owner.getMediaType().equals(MediaType.BANNER)) {
            BannerAdView bav = (BannerAdView) owner;
            if (bav.getExpandsToFitScreenWidth()) {
                bav.expandToFitScreenWidth(response.getWidth(), response.getHeight(), output);
            }
            if (bav.getResizeAdToFitContainer()) {
                bav.resizeWebViewToFitContainer(response.getWidth(), response.getHeight(), output);
            }
        }
    }

}