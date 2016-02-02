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

import com.appnexus.opensdk.utils.Clog;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.RejectedExecutionException;

abstract class RequestManager implements AdRequester {
    private LinkedList<MediatedAd> mediatedAds;
    protected AdRequest adRequest;
    private long totalLatencyStart = -1;

    @Override
    public abstract void cancel();

    @Override
    public void execute() {
        adRequest = new AdRequest(this);
        markLatencyStart();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                adRequest.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                adRequest.execute();
            }
        } catch (RejectedExecutionException rejectedExecutionException) {
            Clog.e(Clog.baseLogTag, "Concurrent Thread Exception while firing new ad request: "
                    + rejectedExecutionException.getMessage());
        } catch (Exception exception) {
            Clog.e(Clog.baseLogTag, "Exception while firing new ad request: " + exception.getMessage());
        }
    }

    @Override
    public abstract RequestParameters getRequestParams();

    @Override
    public abstract void failed(ResultCode code);

    @Override
    public abstract void onReceiveServerResponse(ServerResponse response);

    @Override
    public abstract void onReceiveAd(AdResponse ad);

    @Override
    public void markLatencyStart() {
        totalLatencyStart = System.currentTimeMillis();
    }

    @Override
    public long getLatency(long now) {
        if (totalLatencyStart > 0) {
            return (now - totalLatencyStart);
        }
        // return -1 if `totalLatencyStart` was not set.
        return -1;
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

    protected void setMediatedAds(LinkedList<MediatedAd> mediatedAds) {
        this.mediatedAds = mediatedAds;
    }

    // returns the first mediated ad if available
    protected MediatedAd popMediatedAd() {
        if ((mediatedAds != null) && (mediatedAds.getFirst() != null)) {
            mediatedClasses.add(mediatedAds.getFirst().getClassName());
            return mediatedAds.removeFirst();
        }
        return null;
    }
}
