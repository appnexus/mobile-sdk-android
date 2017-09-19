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

import android.os.AsyncTask;
import android.os.Build;

import com.appnexus.opensdk.ut.UTAdRequest;
import com.appnexus.opensdk.ut.UTAdRequester;
import com.appnexus.opensdk.ut.UTAdResponse;
import com.appnexus.opensdk.ut.UTRequestParameters;
import com.appnexus.opensdk.ut.adresponse.BaseAdResponse;
import com.appnexus.opensdk.ut.adresponse.CSMSDKAdResponse;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.HTTPGet;
import com.appnexus.opensdk.utils.HTTPResponse;
import com.appnexus.opensdk.utils.Settings;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.RejectedExecutionException;

public abstract class RequestManager implements UTAdRequester{
    UTAdRequest utAdRequest;
    private LinkedList<BaseAdResponse> adList;
    String noAdUrl;

    private long time;

    /**
     * Called when the request made by the requester fails.
     *
     * @param code reason why the request fails.
     */
    public abstract void failed(ResultCode code);

    public abstract void onReceiveAd(AdResponse ad);

    @Override
    public boolean isHttpsEnabled(){
        return Settings.getSettings().isHttpsEnabled();
    }

    @Override
    public void onReceiveUTResponse(UTAdResponse response){
        //First set the NoAdUrl from response. This will be used to fire tracked for failed case.
        if(response != null){
            noAdUrl = response.getNoAdUrl();
        }
    }

    public abstract void continueWaterfall(ResultCode reason);

    public abstract void cancel();

    @Override
    public void execute() {
        utAdRequest = new UTAdRequest(this);
        markLatencyStart();

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                utAdRequest.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                utAdRequest.execute();
            }
        } catch (RejectedExecutionException rejectedExecutionException) {
            Clog.e(Clog.baseLogTag, "Concurrent Thread Exception while firing new ad request: "
                    + rejectedExecutionException.getMessage());
        } catch (Exception exception) {
            Clog.e(Clog.baseLogTag, "Exception while firing new ad request: " + exception.getMessage());
        }

    }

    public abstract UTRequestParameters getRequestParams();

    @Override
    public void markLatencyStart() {

        time = System.currentTimeMillis();
    }

    @Override
    public long getLatency(long now) {
        return System.currentTimeMillis() - time;
    }

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

    protected void fireNoAdTracker(final String trackerUrl, final String trackerType) {
        if((trackerUrl == null) || trackerUrl == "") return;

        new HTTPGet() {
            @Override
            protected void onPostExecute(HTTPResponse response) {
                if (response != null && response.getSucceeded()) {
                    //noinspection ConstantConditions
                    Clog.i(Clog.baseLogTag, trackerType.concat( Clog.getString(R.string.fire_tracker_succesfully_message)));
                }
            }

            @Override
            protected String getUrl() {
                return trackerUrl;
            }
        }.execute();
    }

    // returns the first mediated ad if available
    protected BaseAdResponse popAd() {
        if ((adList != null) && (adList.getFirst() != null)) {
            if (adList.getFirst().getContentSource() != null && adList.getFirst().getContentSource().equalsIgnoreCase("csm")){
                CSMSDKAdResponse CSMSDKAdResponse = (CSMSDKAdResponse)adList.getFirst();
                mediatedClasses.add(CSMSDKAdResponse.getClassName());
            }

            return adList.removeFirst();
        }
        return null;
    }


    @Override
    public LinkedList<BaseAdResponse> getAdList() {
        return adList;
    }

    protected void setAdList(LinkedList<BaseAdResponse> adList) {
        this.adList = adList;
    }

}
