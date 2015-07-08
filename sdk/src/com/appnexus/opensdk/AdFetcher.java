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
import android.os.Handler;
import android.os.Message;

import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * AdFetcher class that schedules requests based on auto refresh settings
 */
class AdFetcher {
    private ScheduledExecutorService tasker;
    private int period = -1;
    private final RequestHandler handler;
    private long lastFetchTime = -1;
    private long timePausedAt = -1;
    private final Ad owner;
    private RequestManager requestManager;
    private STATE state = STATE.STOPPED;

    enum STATE {
        STOPPED,    // AdFetcher is Stopped
        SINGLE_REQUEST, // Request on demand mode
        AUTO_REFRESH // Auto refresh mode
    }

    // Fires requests whenever it receives a message
    public AdFetcher(Ad owner) {
        this.owner = owner;
        handler = new RequestHandler(this);
    }

    void setPeriod(int period) {
        boolean periodChanged = this.period != period;
        this.period = period;
        if ((periodChanged) && !state.equals(STATE.STOPPED)) {
            // We should reset.
            Clog.d(Clog.baseLogTag, "AdFetcher refresh period changed to " + this.period);
            Clog.d(Clog.baseLogTag, "Resetting AdFetcher");
            stop();
            start();
        }
    }

    void stop() {
        if (requestManager != null) {
            requestManager.cancel();
            requestManager = null;
        }

        clearTasker();

        Clog.d(Clog.baseLogTag, Clog.getString(R.string.stop));
        timePausedAt = System.currentTimeMillis();
        state = STATE.STOPPED;
    }

    void start() {
        Clog.d(Clog.baseLogTag, Clog.getString(R.string.start));
        createTasker();
        switch (state) {
            case STOPPED:
                if (this.period <= 0) {
                    Clog.v(Clog.baseLogTag,
                            Clog.getString(R.string.fetcher_start_single));
                    // Request an ad once
                    tasker.schedule(new MessageRunnable(), 0, TimeUnit.SECONDS);
                    state = STATE.SINGLE_REQUEST;
                } else {
                    Clog.v(Clog.baseLogTag, Clog.getString(R.string.fetcher_start_auto));
                    // Start recurring ad requests
                    final int msPeriod = period; // refresh period
                    final long stall; // delay millis for the initial request
                    if (timePausedAt != -1 && lastFetchTime != -1) {
                        //Clamp the stall between 0 and the period. Ads should never be requested on
                        //a delay longer than the period
                        stall = Math.min(msPeriod, Math.max(0, msPeriod - (timePausedAt - lastFetchTime)));
                    } else {
                        stall = 0;
                    }

                    Clog.v(Clog.baseLogTag, Clog.getString(
                            R.string.request_delayed_by_x_ms, stall));
                    tasker.scheduleAtFixedRate(new MessageRunnable(), stall,
                            msPeriod, TimeUnit.MILLISECONDS);

                    state = STATE.AUTO_REFRESH;
                }
                break;
            case SINGLE_REQUEST:
                Clog.v(Clog.baseLogTag,
                        Clog.getString(R.string.fetcher_start_single));
                // Request an ad once
                tasker.schedule(new MessageRunnable(), 0, TimeUnit.SECONDS);
                break;
            case AUTO_REFRESH:
                // if auto refresh has already started
                // prevent loading again if start() gets called twice in a row
                break;
        }
    }

    private void createTasker() {
        if (tasker == null) {
            // Start a Scheduler to execute recurring tasks
            tasker = Executors
                    .newScheduledThreadPool(Settings.FETCH_THREAD_COUNT);
        }
    }

    private void clearTasker() {
        if (tasker == null)
            return;
        tasker.shutdownNow();
        try {
            tasker.awaitTermination(period, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            // ignored
        } finally {
            tasker = null;
        }

    }

    void clearDurations() {
        lastFetchTime = -1;
        timePausedAt = -1;
    }

    private class MessageRunnable implements Runnable {

        @Override
        public void run() {
            Clog.v(Clog.baseLogTag,
                    Clog.getString(R.string.handler_message_pass));
            handler.sendEmptyMessage(0);

        }

    }

    // Create a handler which will receive the AsyncTasks and spawn them from
    // the main thread.
    private static class RequestHandler extends Handler {
        private final WeakReference<AdFetcher> mFetcher;

        RequestHandler(AdFetcher f) {
            mFetcher = new WeakReference<AdFetcher>(f);
        }

        @SuppressLint("NewApi")
        @Override
        synchronized public void handleMessage(Message msg) {
            // If the adfetcher, for some reason, has vanished, do nothing with
            // this message
            // If the owner is not ready for a new ad, do nothing with
            // this message
            AdFetcher fetcher = mFetcher.get();
            if (fetcher == null
                    || !fetcher.owner.isReadyToStart())
                return;

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
            switch (fetcher.owner.getMediaType()) {
                case BANNER:
                    fetcher.requestManager = new AdViewRequestManager((BannerAdView) fetcher.owner);
                    break;
                case INTERSTITIAL:
                    fetcher.requestManager = new AdViewRequestManager((InterstitialAdView) fetcher.owner);
                    break;
                case NATIVE:
                    fetcher.requestManager = new NativeAdRequestManager((NativeAdRequest) fetcher.owner);
                    break;
            }

            fetcher.requestManager.execute();
        }
    }

    int getPeriod() {
        return this.period;
    }

    STATE getState() {
        return this.state;
    }

}
