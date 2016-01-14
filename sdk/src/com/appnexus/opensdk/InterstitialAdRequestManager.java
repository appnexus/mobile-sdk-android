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
import com.appnexus.opensdk.utils.ANConstants;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.HTTPGet;
import com.appnexus.opensdk.utils.HTTPResponse;
import com.appnexus.opensdk.utils.StringUtil;
import com.appnexus.opensdk.vastdata.AdModel;
import com.appnexus.opensdk.vastdata.VastResponseParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;

class InterstitialAdRequestManager extends RequestManager {
    private final WeakReference<AdView> owner;
    private MediatedAdViewController controller;

    InterstitialAdRequestManager(AdView owner) {
        super();
        this.owner = new WeakReference<AdView>(owner);
    }

    @Override
    public void execute() {
        utAdRequest = new UTAdRequest(this);
        markLatencyStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            utAdRequest.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            utAdRequest.execute();
        }
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
    }

    @Override
    public void onReceiveUTResponse(UTAdResponse response, ResultCode resultCode) {
        if(resultCode == ResultCode.SUCCESS) {
            final AdView owner = this.owner.get();
            if (owner != null) {
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
                processNextAd();
            }
        }else{
            handleResponseFailure(resultCode);
        }
    }

    private void handleResponseFailure(ResultCode reason) {
        printMediatedClasses();
        AdView owner = this.owner.get();
        if (owner != null) {
            owner.getAdDispatcher().onAdFailed(reason);
        }
    }


    private void processNextAd() {
        // If we're about to dispatch a creative to a banner
        // that has been resized by ad stretching, reset its size
        AdView owner = this.owner.get();
        if (getAdList() != null && !getAdList().isEmpty()) {
            BaseAdResponse baseAdResponse = popAd();
            if (baseAdResponse.getContentSource().equalsIgnoreCase(ANConstants.RTB)) {
                handleRTBResponse(owner, (RTBAdResponse) baseAdResponse);
            } else if (baseAdResponse.getContentSource().equalsIgnoreCase(ANConstants.CSM)) {
                handleCSMResponse(owner, (CSMAdResponse) baseAdResponse);
            } else if (baseAdResponse.getContentSource().equalsIgnoreCase(ANConstants.SSM)) {
                handleSSMResponse(owner, (SSMAdResponse) baseAdResponse);
            }
        }
    }

    @Override
    public void currentAdFailed(ResultCode reason) {

        if(getAdList() == null || getAdList().isEmpty()){
            handleResponseFailure(reason);
        }else {
            // Process next available ad response
            processNextAd();
        }
    }

    @Override
    public void currentAdLoaded(AdResponse ad) {
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

    private void handleSSMResponse(final AdView owner, final SSMAdResponse ssmAdResponse) {
        new HTTPGet() {
            @Override
            protected HTTPResponse doInBackground(Void... params) {
                HTTPResponse response = super.doInBackground(params);
                String vastResponse = response.getResponseBody();
                ssmAdResponse.setAdContent(response.getResponseBody());
                
                if(!StringUtil.isEmpty(vastResponse) && ANConstants.AD_TYPE_VIDEO.equalsIgnoreCase(ssmAdResponse.getAdType())) {
                    try {
                        InputStream stream = new ByteArrayInputStream(vastResponse.getBytes(Charset.forName(ANConstants.UTF_8)));
                        VastResponseParser vastResponseParser = new VastResponseParser();
                        AdModel vastAdResponse = vastResponseParser.readVAST(stream);
                        VastVideoUtil.addANTrackers(vastAdResponse, ssmAdResponse);
                        ssmAdResponse.setVastAdResponse(vastAdResponse);
                    } catch (Exception e) {
                        Clog.e(Clog.httpRespLogTag, "Exception processing vast response: "+e.getMessage());
                    }
                }
                return response;
            }

            @Override
            protected void onPostExecute(HTTPResponse response) {
                if(response != null && response.getSucceeded()) {
                    if(ANConstants.AD_TYPE_HTML.equalsIgnoreCase(ssmAdResponse.getAdType()) && !StringUtil.isEmpty(ssmAdResponse.getAdContent())){
                        initiateWebview(owner, ssmAdResponse);
                    }else if(ANConstants.AD_TYPE_VIDEO.equalsIgnoreCase(ssmAdResponse.getAdType())){
                        if(ssmAdResponse.getVastAdResponse() != null && ssmAdResponse.getVastAdResponse().containsMediaUrl()) {
                            initiateVastAdView(owner, ssmAdResponse);
                        }else{
                            Clog.e(Clog.httpRespLogTag, "Vast ad is not available");
                            currentAdFailed(ResultCode.UNABLE_TO_FILL);
                        }
                    }else{
                        currentAdFailed(ResultCode.UNABLE_TO_FILL);
                    }
                }else {
                    currentAdFailed(ResultCode.UNABLE_TO_FILL);
                }
            }

            @Override
            protected String getUrl() {
                return ssmAdResponse.getAdUrl();
            }
        }.execute();
    }



    private void handleCSMResponse(AdView owner, CSMAdResponse csmAdResponse) {
        if (owner.getMediaType().equals(MediaType.INTERSTITIAL)) {
            controller = MediatedInterstitialAdViewController.create(
                    (Activity) owner.getContext(),
                    InterstitialAdRequestManager.this,
                    csmAdResponse,
                    owner.getAdDispatcher());
        } else {
            Clog.e(Clog.baseLogTag, "Request type can not be identified.");
            owner.getAdDispatcher().onAdFailed(ResultCode.INVALID_REQUEST);
        }
    }

    private void handleRTBResponse(AdView owner, RTBAdResponse rtbAdResponse) {
        if(rtbAdResponse.getAdContent() != null) {
            if (ANConstants.AD_TYPE_VIDEO.equalsIgnoreCase(rtbAdResponse.getAdType())) {
                // Vast ads
                handleVASTResponse(owner, rtbAdResponse);

            } else if (ANConstants.AD_TYPE_HTML.equalsIgnoreCase(rtbAdResponse.getAdType())) {
                // Standard ads
                initiateWebview(owner, rtbAdResponse);
            } else {
                currentAdFailed(ResultCode.UNABLE_TO_FILL);
            }
        }else{
            currentAdFailed(ResultCode.UNABLE_TO_FILL);
        }
    }

    private void handleVASTResponse(final AdView owner, final RTBAdResponse rtbAdResponse) {

        if (!StringUtil.isEmpty(rtbAdResponse.getAdContent())) {
            new AsyncTask<String, Void, AdModel>() {
                @Override
                protected AdModel doInBackground(String... params) {
                    try {
                        InputStream stream = new ByteArrayInputStream(params[0].getBytes(Charset.forName(ANConstants.UTF_8)));
                        VastResponseParser vastResponseParser = new VastResponseParser();
                        return vastResponseParser.readVAST(stream);
                    }catch(Exception e){
                        Clog.e(Clog.vastLogTag, "Exception processing the VAST response: "+e.getMessage());
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(AdModel vastAdResponse) {
                    if (vastAdResponse != null && vastAdResponse.containsMediaUrl()) {
                        rtbAdResponse.setVastAdResponse(vastAdResponse);
                        Clog.d(Clog.httpRespLogTag, "Vast response parsed");
                        initiateVastAdView(owner, rtbAdResponse);
                    }else{
                        currentAdFailed(ResultCode.UNABLE_TO_FILL);
                    }
                }
            }.execute(rtbAdResponse.getAdContent());
        }

    }


    @Override
    public void onReceiveServerResponse(final ServerResponse response) {
    }

    private void initiateWebview(final AdView owner, final BaseAdResponse response) {
        final AdWebView output = new AdWebView(owner);
        output.loadAd(response);

        currentAdLoaded(new AdResponse() {
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
        final VastVideoView adVideoView = new VastVideoView(owner.getContext(), response.getVastAdResponse());

        currentAdLoaded(new AdResponse() {
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

    @Override
    public void onReceiveAd(AdResponse ad) {}
}
