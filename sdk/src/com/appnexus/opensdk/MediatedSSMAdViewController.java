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

import android.os.Build;
import android.os.Handler;
import android.os.Message;

import com.appnexus.opensdk.ut.UTAdRequester;
import com.appnexus.opensdk.ut.UTConstants;
import com.appnexus.opensdk.ut.adresponse.SSMHTMLAdResponse;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.HTTPGet;
import com.appnexus.opensdk.utils.HTTPResponse;
import com.appnexus.opensdk.utils.StringUtil;

import java.lang.ref.WeakReference;


public class MediatedSSMAdViewController {
    protected MediaType mediaType;
    private WeakReference<UTAdRequester> caller_requester;
    protected SSMHTMLAdResponse ssmHtmlAdResponse;
    protected AdDispatcher listener;
    protected AdView owner;

    boolean hasFailed = false;
    boolean hasSucceeded = false;

    static MediatedSSMAdViewController create(
            AdView owner, UTAdRequester requester, SSMHTMLAdResponse currentAd) {
        MediatedSSMAdViewController out = new MediatedSSMAdViewController(owner, requester, currentAd);
        return out.hasFailed ? null : out;
    }

    private MediatedSSMAdViewController(AdView owner, UTAdRequester requester, SSMHTMLAdResponse currentAd) {
        this.caller_requester = new WeakReference<UTAdRequester>(requester);
        this.ssmHtmlAdResponse = currentAd;
        this.listener = owner.getAdDispatcher();
        this.owner = owner;

        if (ssmHtmlAdResponse == null || !UTConstants.AD_TYPE_BANNER.equalsIgnoreCase(ssmHtmlAdResponse.getAdType())) {
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.mediated_no_ads));
            onAdFailed(ResultCode.getNewInstance(ResultCode.UNABLE_TO_FILL));
        } else {
            startTimeout();
            markLatencyStart();
            instantiateNewMediatedSSMAd();
        }
    }

    private void instantiateNewMediatedSSMAd() {
        HTTPGet loadUrl = new HTTPGet() {
            @Override
            protected HTTPResponse doInBackground(Void... params) {
                return super.doInBackground(params);
            }

            @Override
            protected void onPostExecute(HTTPResponse response) {
                markLatencyStop();
                if (response != null && response.getSucceeded()) {
                    ssmHtmlAdResponse.setAdContent(response.getResponseBody());
                    if (!StringUtil.isEmpty(ssmHtmlAdResponse.getAdContent())) {
                        handleSSMServerResponse();
                    } else {
                        onAdFailed(ResultCode.getNewInstance(ResultCode.UNABLE_TO_FILL));
                    }
                } else {
                    onAdFailed(ResultCode.getNewInstance(ResultCode.UNABLE_TO_FILL));
                }

            }

            @Override
            protected String getUrl() {
                return ssmHtmlAdResponse.getAdUrl();
            }
        };

        loadUrl.execute();

    }

    public void onAdFailed(ResultCode reason) {
        if (hasSucceeded || hasFailed) return;
        markLatencyStop();
        cancelTimeout();
        hasFailed = true;
        fireResponseURL(ssmHtmlAdResponse, reason);
        UTAdRequester requester = this.caller_requester.get();
        if (requester != null) {
            requester.continueWaterfall(reason);
        }
    }


    private void handleSSMServerResponse() {
        if (hasSucceeded || hasFailed) return;
        cancelTimeout();
        hasSucceeded = true;
        fireResponseURL(ssmHtmlAdResponse, ResultCode.getNewInstance(ResultCode.SUCCESS));
        UTAdRequester requester = this.caller_requester.get();
        if (requester != null) {
            final AdWebView output = new AdWebView(owner, requester);
            output.loadAd(ssmHtmlAdResponse);
        }
    }

    private void fireResponseURL(SSMHTMLAdResponse ssmHtmlAdResponse, ResultCode result) {
        if ((ssmHtmlAdResponse == null) || (ssmHtmlAdResponse.getResponseURL() == null) || StringUtil.isEmpty(ssmHtmlAdResponse.getResponseURL())) {
            Clog.w(Clog.mediationLogTag, Clog.getString(R.string.fire_responseurl_null));
            return;
        }
        ResponseUrl responseUrl = new ResponseUrl.Builder(ssmHtmlAdResponse.getResponseURL(), result)
                .latency(getLatencyParam())
                .build();
        responseUrl.execute();
    }

    /*
     Timeout handler code
     */

    void startTimeout() {
        if (hasSucceeded || hasFailed) return;
        timeoutHandler.sendEmptyMessageDelayed(0, ssmHtmlAdResponse.getNetworkTimeout());
    }

    void cancelTimeout() {
        timeoutHandler.removeMessages(0);
    }

    static class TimeoutHandler extends Handler {
        WeakReference<MediatedSSMAdViewController> mavc;

        public TimeoutHandler(MediatedSSMAdViewController mavc) {
            this.mavc = new WeakReference<MediatedSSMAdViewController>(mavc);
        }

        @Override
        public void handleMessage(Message msg) {
            MediatedSSMAdViewController avc = mavc.get();

            if (avc == null || avc.hasFailed) return;
            Clog.w(Clog.mediationLogTag, Clog.getString(R.string.mediation_timeout));
            try {
                avc.onAdFailed(ResultCode.getNewInstance(ResultCode.INTERNAL_ERROR));
            } catch (IllegalArgumentException e) {
                // catch exception for unregisterReceiver() when destroying views
            } finally {
                avc.listener = null;

            }
        }
    }

    // if the mediated network fails to call us within the timeout period, fail
    private final Handler timeoutHandler = new TimeoutHandler(this);

    /*
    Measuring Latency functions
     */

    // variables for measuring latency.
    private long latencyStart = -1, latencyStop = -1;

    /**
     * Should be called immediately after mediated SDK returns
     * from `requestAd` call.
     */
    protected void markLatencyStart() {
        latencyStart = System.currentTimeMillis();
    }

    /**
     * Should be called immediately after mediated SDK
     * calls either of `onAdLoaded` or `onAdFailed`.
     */
    protected void markLatencyStop() {
        latencyStop = System.currentTimeMillis();
    }

    /**
     * The latency of the call to the mediated SDK.
     *
     * @return the mediated SDK latency, -1 if `latencyStart`
     * or `latencyStop` not set.
     */
    private long getLatencyParam() {
        if ((latencyStart > 0) && (latencyStop > 0)) {
            return (latencyStop - latencyStart);
        }
        // return -1 if invalid.
        return -1;
    }
}