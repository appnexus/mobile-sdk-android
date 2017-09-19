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
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.ut.UTAdRequest;
import com.appnexus.opensdk.ut.UTAdRequester;
import com.appnexus.opensdk.ut.UTAdResponse;
import com.appnexus.opensdk.ut.UTConstants;
import com.appnexus.opensdk.ut.adresponse.CSMVASTAdResponse;
import com.appnexus.opensdk.ut.adresponse.RTBVASTAdResponse;
import com.appnexus.opensdk.ut.UTRequestParameters;
import com.appnexus.opensdk.ut.adresponse.BaseAdResponse;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.HTTPGet;
import com.appnexus.opensdk.utils.HTTPResponse;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.StringUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;


class VideoRequestManager implements UTAdRequester {
    protected UTAdRequest utAdRequest;
    //private LinkedList<MediatedAd> mediatedAds;
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
    public UTRequestParameters getRequestParams() {
        VideoAd owner = this.owner.get();
        if (owner != null) {
            return owner.getRequestParameters();
        } else {
            return null;
        }
    }

    @Override
    public boolean isHttpsEnabled() {
        return Settings.getSettings().isHttpsEnabled();
    }

    @Override
    public void failed(ResultCode code) {
        handleResponseFailure(code, null);
    }

    @Override
    public void onReceiveUTResponse(UTAdResponse response) {
        final VideoAd owner = this.owner.get();
        if (owner != null && response != null && response.getAdList() != null && !response.getAdList().isEmpty()) {
            setAdList(response.getAdList());
            noAdUrl = response.getNoAdUrl();
            processNextAd();
        } else {
            Clog.w(Clog.httpRespLogTag, Clog.getString(com.appnexus.opensdk.R.string.response_no_ads));
            handleResponseFailure(ResultCode.UNABLE_TO_FILL, null);
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
            if (baseAdResponse.getContentSource().equalsIgnoreCase(UTConstants.RTB)) {
                handleRTBResponse(owner, (RTBVASTAdResponse) baseAdResponse);
            } else if (baseAdResponse.getContentSource().equalsIgnoreCase(UTConstants.CSM_VIDEO)) {
                handleCSMVASTAdResponse(owner, (CSMVASTAdResponse) baseAdResponse);
            }
        }
    }

    @Override
    public void continueWaterfall(ResultCode reason) {
        Clog.d(Clog.videoLogTag, "Waterfall continueWaterfall");
        if (getAdList() == null || getAdList().isEmpty()) {
            handleResponseFailure(reason, noAdUrl);
        } else {
            // Process next available ad response
            processNextAd();
        }
    }


    private void handleRTBResponse(VideoAd owner, RTBVASTAdResponse rtbAdResponse) {

        if (rtbAdResponse.getAdContent() != null) {
            if (UTConstants.AD_TYPE_VIDEO.equalsIgnoreCase(rtbAdResponse.getAdType())) {
                // Vast ads
                handleRTBVASTResponse(owner, rtbAdResponse);

            } else {
                continueWaterfall(ResultCode.UNABLE_TO_FILL);
            }
        } else {
            continueWaterfall(ResultCode.UNABLE_TO_FILL);
        }
    }

    private void handleRTBVASTResponse(final VideoAd owner, final RTBVASTAdResponse rtbAdResponse) {

        if (!StringUtil.isEmpty(rtbAdResponse.getAdContent())) {
            fireNotifyUrlForVideo(rtbAdResponse);
            if (rtbAdResponse != null && rtbAdResponse.getAdContent() != null) {
                initiateVastAdView(owner, rtbAdResponse);
            } else {
                continueWaterfall(ResultCode.UNABLE_TO_FILL);
            }
        }
    }

    private void handleCSMVASTAdResponse(VideoAd owner, CSMVASTAdResponse csmvastAdResponse) {
        if (csmvastAdResponse != null && csmvastAdResponse.getAdJSONContent() != null) {
            if (UTConstants.AD_TYPE_VIDEO.equalsIgnoreCase(csmvastAdResponse.getAdType())) {
                // @NOTE no need to fire notify URL here it is taken care by ASTMediationManager.js

                initiateVastAdView(owner, csmvastAdResponse);
            } else {
                continueWaterfall(ResultCode.UNABLE_TO_FILL);
            }
        } else {
            continueWaterfall(ResultCode.UNABLE_TO_FILL);
        }
    }


    private void fireNotifyUrlForVideo(RTBVASTAdResponse adResponse) {
        if (UTConstants.AD_TYPE_VIDEO.equalsIgnoreCase(adResponse.getAdType())) {
            fireTracker(adResponse.getNotifyUrl());
        }
    }

    private void fireTracker(final String trackerUrl) {
        if (trackerUrl == null) return;

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


    private void initiateVastAdView(final VideoAd owner, final BaseAdResponse response) {
        Clog.d(Clog.videoLogTag, "Creating WebView for::" + response.getContentSource());
        VideoWebView adVideoView = new VideoWebView(owner.getContext(), owner, this);
        owner.getVideoAdView().setVideoWebView(adVideoView);
        adVideoView.loadAd(response);
    }

    @Override
    public void onReceiveAd(AdResponse ad) {
    }

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


    public LinkedList<BaseAdResponse> getAdList() {
        return adList;
    }

    protected void setAdList(LinkedList<BaseAdResponse> adList) {
        this.adList = adList;
    }


    // returns the first mediated ad if available
    protected BaseAdResponse popAd() {
        if ((adList != null) && (adList.getFirst() != null)) {
            return adList.removeFirst();
        }
        return null;
    }
}
