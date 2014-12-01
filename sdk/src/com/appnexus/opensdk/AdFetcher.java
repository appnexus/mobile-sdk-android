/*
 *    Copyright 2013 APPNEXUS INC
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

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

abstract class AdFetcher implements AdRequester {
    private ScheduledExecutorService tasker;
    private int period = -1;
    private boolean autoRefresh;
    private final RequestHandler handler;
    private boolean shouldReset = false;
    private long lastFetchTime = -1;
    private long timePausedAt = -1;
    private RequestParameters requestParameters;
    private AdRequest adRequest;
    private LinkedList<MediatedAd> mediatedAds;

    // Fires requests whenever it receives a message
    public AdFetcher(RequestParameters requestParams) {
        this.requestParameters = requestParams;
        handler = new RequestHandler(this);
    }

    protected void setPeriod(int period) {
        this.period = period;
        if (tasker != null)
            shouldReset = true;
    }

    protected int getPeriod() {
        return period;
    }

    protected void stop() {
        if (adRequest != null) {
            adRequest.cancel(true);
            adRequest = null;
        }

        if (tasker == null)
            return;
        tasker.shutdownNow();
        try {
            tasker.awaitTermination(period, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            tasker = null;
            return;
        }
        tasker = null;
        Clog.d(Clog.baseLogTag, Clog.getString(R.string.stop));
        timePausedAt = System.currentTimeMillis();

    }

    protected void start() {
        Clog.d(Clog.baseLogTag, Clog.getString(R.string.start));
        if (tasker != null) {
            // only print log, don't call onAdFailed callback
            Clog.d(Clog.baseLogTag, Clog.getString(R.string.moot_restart));
            return;
        }
        markLatencyStart();
        makeTasker();
    }

    protected void makeTasker() {
        // Start a Scheduler to execute recurring tasks
        tasker = Executors
                .newScheduledThreadPool(Settings.FETCH_THREAD_COUNT);

        // Get the period from the settings
        final int msPeriod = period <= 0 ? 30 * 1000 : period;

        if (!autoRefresh) {
            Clog.v(Clog.baseLogTag,
                    Clog.getString(R.string.fetcher_start_single));
            // Request an ad once
            tasker.schedule(new MessageRunnable(), 0, TimeUnit.SECONDS);
        } else {
            Clog.v(Clog.baseLogTag, Clog.getString(R.string.fetcher_start_auto));
            // Start recurring ad requests
            long stall_temp;
            if (timePausedAt != -1 && lastFetchTime != -1) {
                //Clamp the stall between 0 and the period. Ads should never be requested on
                //a delay longer than the period
                stall_temp = Math.min(msPeriod, Math.max(0, msPeriod - (timePausedAt - lastFetchTime)));
            } else {
                stall_temp = 0;
            }

            //To be safe, only stall with positive time values
            final long stall = Math.max(0, stall_temp);
            Clog.v(Clog.baseLogTag,
                    Clog.getString(R.string.request_delayed_by_x_ms, stall));
            tasker.schedule(new Runnable() {
                @Override
                public void run() {
                    Clog.v(Clog.baseLogTag, Clog.getString(
                            R.string.request_delayed_by_x_ms, stall));
                    tasker.scheduleAtFixedRate(new MessageRunnable(), 0,
                            msPeriod, TimeUnit.MILLISECONDS);
                }
            }, stall, TimeUnit.MILLISECONDS);
        }
    }

    protected void clearDurations() {
        lastFetchTime = -1;
        timePausedAt = -1;

    }

    protected class MessageRunnable implements Runnable {

        @Override
        public void run() {
            Clog.v(Clog.baseLogTag,
                    Clog.getString(R.string.handler_message_pass));
            handler.sendEmptyMessage(0);

        }

    }

    protected abstract boolean isReadyToStart();

    // Create a handler which will receive the AsyncTasks and spawn them from
    // the main thread.
    protected static class RequestHandler extends Handler {
        private final WeakReference<AdFetcher> mFetcher;

        RequestHandler(AdFetcher f) {
            mFetcher = new WeakReference<AdFetcher>(f);
        }

        @SuppressLint("NewApi")
        @Override
        synchronized public void handleMessage(Message msg) {
            // If the adfetcher, for some reason, has vanished, do nothing with
            // this message
            // If an MRAID ad is expanded in the owning view, do nothing with
            // this message
            AdFetcher fetcher = mFetcher.get();
            if (fetcher == null
                    || !fetcher.isReadyToStart())
                return;

            // If we need to reset, reset.
            if (fetcher.shouldReset) {
                fetcher.shouldReset = false;
                fetcher.stop();
                fetcher.start();
                return;
            }

            // Update last fetch time once
            // For sane logging, don't report negative times
            if (fetcher.lastFetchTime != -1) {
                Clog.d(Clog.baseLogTag,
                        Clog.getString(
                                R.string.new_ad_since,
                                Math.max(0, (int) (System.currentTimeMillis() - fetcher.lastFetchTime))));
            }
            fetcher.lastFetchTime = System.currentTimeMillis();

            // Spawn an AdRequest
            fetcher.adRequest = new AdRequest(fetcher);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                fetcher.adRequest.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                fetcher.adRequest.execute();
            }
        }
    }

    protected boolean getAutoRefresh() {
        return autoRefresh;
    }

    protected void setAutoRefresh(boolean autoRefresh) {
        this.autoRefresh = autoRefresh;
        // Restart with new autorefresh setting, but only if auto-refresh was
        // set to true
        if (tasker != null) {
            if (autoRefresh) {
                stop();
                start();
            }
        }
    }

    //
    // AdRequester implementation
    //

    @Override
    public abstract void failed(AdRequest request);

    @Override
    public abstract void onReceiveResponse(AdResponse response);

    /*
    Running Total Latency
     */

    private long totalLatencyStart = -1;

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

    void printMediatedClasses() {
        if (mediatedClasses.isEmpty()) return;
        StringBuilder sb = new StringBuilder("Mediated Classes: \n");
        for (int i = mediatedClasses.size(); i > 0; i--) {
            sb.append(String.format("%d: %s\n", i, mediatedClasses.get(i-1)));
        }
        Clog.i(Clog.mediationLogTag, sb.toString());
        mediatedClasses.clear();
    }

    @Override
    public LinkedList<MediatedAd> getMediatedAds() {
        return mediatedAds;
    }

    @Override
    public RequestParameters getRequestParams() {
        return requestParameters;
    }


    void setMediatedAds(LinkedList<MediatedAd> mediatedAds) {
        this.mediatedAds = mediatedAds;
    }

    // returns the first mediated ad if available
    MediatedAd popMediatedAd() {
        if ((mediatedAds != null) && (mediatedAds.getFirst() != null)) {
            mediatedClasses.add(mediatedAds.getFirst().getClassName());
            return mediatedAds.removeFirst();
        }
        return null;
    }
}
