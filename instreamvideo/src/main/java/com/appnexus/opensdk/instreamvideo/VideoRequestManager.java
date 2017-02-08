/*
 *    Copyright 2016 APPNEXUS INC
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
package com.appnexus.opensdk.instreamvideo;

import android.os.AsyncTask;
import android.os.Build;

import com.appnexus.opensdk.AdResponse;
import com.appnexus.opensdk.MediatedAd;
import com.appnexus.opensdk.RequestParameters;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.ServerResponse;
import com.appnexus.opensdk.instreamvideo.adresponsedata.BaseAdResponse;
import com.appnexus.opensdk.instreamvideo.adresponsedata.CSMAdResponse;
import com.appnexus.opensdk.instreamvideo.adresponsedata.CSMVideoAdResponse;
import com.appnexus.opensdk.instreamvideo.adresponsedata.RTBAdResponse;
import com.appnexus.opensdk.instreamvideo.ut.UTAdRequest;
import com.appnexus.opensdk.instreamvideo.ut.UTAdResponse;
import com.appnexus.opensdk.instreamvideo.utils.ANConstants;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.HTTPGet;
import com.appnexus.opensdk.utils.HTTPResponse;
import com.appnexus.opensdk.utils.StringUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;


class VideoRequestManager implements UTAdRequester {
    protected UTAdRequest utAdRequest;
    private LinkedList<MediatedAd> mediatedAds;
    private LinkedList<BaseAdResponse> adList;
    private final WeakReference<VideoAd> owner;
    private String noAdUrl;

    VideoRequestManager(VideoAd owner) {
        super();
        this.owner = new WeakReference<VideoAd>(owner);
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
    }

    @Override
    public RequestParameters getRequestParams() {
        VideoAd owner = this.owner.get();
        if (owner != null) {
            return owner.getRequestParameters();
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
            final VideoAd owner = this.owner.get();
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
                    noAdUrl = response.getNoAdUrl();
                }
                processNextAd();
            }
        }else{
            handleResponseFailure(resultCode, null);
        }
    }

    private void handleResponseFailure(ResultCode reason, String noAdUrl) {
        fireTracker(noAdUrl);
        printMediatedClasses();
        VideoAd owner = this.owner.get();
        if (owner != null) {
            owner.getAdDispatcher().onAdFailed(reason);
        }
    }


    private void processNextAd() {
        // If we're about to dispatch a creative to a banner
        // that has been resized by ad stretching, reset its size
        VideoAd owner = this.owner.get();
        if (getAdList() != null && !getAdList().isEmpty()) {
            BaseAdResponse baseAdResponse = popAd();
            if (baseAdResponse.getContentSource().equalsIgnoreCase(ANConstants.RTB)) {
                handleRTBResponse(owner, (RTBAdResponse) baseAdResponse);
            } else if (baseAdResponse.getContentSource().equalsIgnoreCase(ANConstants.CSM_VIDEO)) {
                handleCSMVideoAdResponse(owner, (CSMVideoAdResponse) baseAdResponse);
            }
        }
    }
    @Override
    public void currentAdFailed(ResultCode reason) {
        Clog.d(ANConstants.videoLogTag,"Waterfall currentAdFailed");
        if(getAdList() == null || getAdList().isEmpty()){
            handleResponseFailure(reason, noAdUrl);
        }else {
            // Process next available ad response
            processNextAd();
        }
    }




    private void handleRTBResponse(VideoAd owner, RTBAdResponse rtbAdResponse) {
        if(rtbAdResponse.getAdContent() != null) {
            if (ANConstants.AD_TYPE_VIDEO.equalsIgnoreCase(rtbAdResponse.getAdType())) {
                // Vast ads
                handleVASTResponse(owner, rtbAdResponse);

            } else {
                currentAdFailed(ResultCode.UNABLE_TO_FILL);
            }
        }else{
            currentAdFailed(ResultCode.UNABLE_TO_FILL);
        }
    }

    private void handleVASTResponse(final VideoAd owner, final RTBAdResponse rtbAdResponse) {

        if (!StringUtil.isEmpty(rtbAdResponse.getAdContent())) {
            fireNotifyUrlForVideo(rtbAdResponse);

            if(rtbAdResponse !=null && rtbAdResponse.getAdContent()!=null) {
                initiateVastAdView(owner, rtbAdResponse);
            }else{
                currentAdFailed(ResultCode.UNABLE_TO_FILL);
            }
        }
    }

    private void handleCSMVideoAdResponse(VideoAd owner, CSMVideoAdResponse csmVideoAdResponse) {
        if(csmVideoAdResponse !=null && csmVideoAdResponse.getAdJSONContent() != null) {
            if (ANConstants.AD_TYPE_VIDEO.equalsIgnoreCase(csmVideoAdResponse.getAdType())) {
                // @NOTE no need to fire notify URL here it is taken care by ASTMediationManager.js
                //fireNotifyUrlForVideo(csmVideoAdResponse);

                initiateVastAdView(owner, csmVideoAdResponse);
            } else {
                currentAdFailed(ResultCode.UNABLE_TO_FILL);
            }
        }else{
            currentAdFailed(ResultCode.UNABLE_TO_FILL);
        }
    }


    private void fireNotifyUrlForVideo(BaseAdResponse adResponse) {
        if(ANConstants.AD_TYPE_VIDEO.equalsIgnoreCase(adResponse.getAdType())){
            fireTracker(adResponse.getNotifyUrl());
        }
    }

    private void fireTracker(final String trackerUrl) {
        if(trackerUrl == null) return;

        new HTTPGet() {
            @Override
            protected void onPostExecute(HTTPResponse response) {
                if (response != null && response.getSucceeded()) {
                    Clog.i(Clog.baseLogTag, "Tracker fired successfully!");
                }
            }

            @Override
            protected String getUrl() {
                return trackerUrl;
            }
        }.execute();
    }


    @Override
    public void onReceiveServerResponse(final ServerResponse response) {
    }

    private void initiateVastAdView(final VideoAd owner, final BaseAdResponse response) {
        Clog.d(ANConstants.videoLogTag,"Creating WebView for::"+response.getContentSource());
        VideoWebView adVideoView = new VideoWebView(owner.getContext(),owner,this);
        owner.getVideoAdView().setVideoWebView(adVideoView);
        adVideoView.loadAd(response);
    }

    @Override
    public void onReceiveAd(AdResponse ad) {}

    @Override
    public void markLatencyStart() {

    }

    @Override
    public long getLatency(long now) {
        return 0;
    }



        /*
     * Meditated Ads
     */

    // For logging mediated classes
    private ArrayList<String> mediatedClasses = new ArrayList<String>();

    protected void printMediatedClasses() {
        if (mediatedClasses.isEmpty()) return;
        StringBuilder sb = new StringBuilder("Mediated Classes: \n");
        for (int i = mediatedClasses.size(); i > 0; i--) {
            sb.append(String.format("%d: %s\n", i, mediatedClasses.get(i - 1)));
        }
        Clog.i(Clog.mediationLogTag, sb.toString());
        mediatedClasses.clear();
    }


    @Override
    public LinkedList<MediatedAd> getMediatedAds() {
        return mediatedAds;
    }


    public LinkedList<BaseAdResponse> getAdList() {
        return adList;
    }

    protected void setMediatedAds(LinkedList<MediatedAd> mediatedAds) {
        this.mediatedAds = mediatedAds;
    }

    protected void setAdList(LinkedList<BaseAdResponse> adList) {
        this.adList = adList;
    }

    // returns the first mediated ad if available
    protected MediatedAd popMediatedAd() {
        if ((mediatedAds != null) && (mediatedAds.getFirst() != null)) {
            mediatedClasses.add(mediatedAds.getFirst().getClassName());
            return mediatedAds.removeFirst();
        }
        return null;
    }

    // returns the first mediated ad if available
    protected BaseAdResponse popAd() {
        if ((adList != null) && (adList.getFirst() != null)) {
            if (adList.getFirst().getContentSource() != null && adList.getFirst().getContentSource().equalsIgnoreCase("csm")){
                CSMAdResponse csmAdResponse = (CSMAdResponse)adList.getFirst();
                mediatedClasses.add(csmAdResponse.getClassName());
            }

            return adList.removeFirst();
        }
        return null;
    }
}
