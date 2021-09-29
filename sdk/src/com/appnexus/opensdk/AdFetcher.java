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

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.appnexus.opensdk.ut.UTAdRequester;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * AdFetcher class that schedules requests based on auto refresh settings
 */
public class AdFetcher {

    private final ANMultiAdRequest anMultiAdRequest;
    private ScheduledExecutorService tasker;
    private int period = -1;
    private RequestHandler handler;
    private long lastFetchTime = -1;
    private long timePausedAt = -1;
    private final Ad owner;
    private UTAdRequester requestManager;
    private STATE state = STATE.STOPPED;
    private static final String ANBACKGROUND = "ANBackgroundThread";

    public void setRequestManager(UTAdRequester requester) {
        requestManager = requester;
    }

    enum STATE {
        STOPPED,    // AdFetcher is Stopped
        SINGLE_REQUEST, // Request on demand mode
        AUTO_REFRESH // Auto refresh mode
    }

    // Fires requests whenever it receives a message
    public AdFetcher(Ad owner) {
        this.owner = owner;

        requestManager = new AdViewRequestManager(owner);
        anMultiAdRequest = null;
    }

    public AdFetcher(ANMultiAdRequest anMultiAdRequest) {
        this.owner = null;
        this.anMultiAdRequest = anMultiAdRequest;
    }

    public void destroy() {
        if (handler != null) {
            String threadName = handler.getLooper().getThread().getName();
            if (threadName.contains(ANBACKGROUND)) {
                Clog.i(Clog.baseLogTag, "Quitting background " + threadName);
                handler.getLooper().quit();
                handler = null;
            }
        }
        if (requestManager != null) {
            requestManager.cancel();
            requestManager = null;
        }

        clearTasker();
    }

    public void setPeriod(int period) {
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

    public void stop() {

        destroy();
        Clog.d(Clog.baseLogTag, Clog.getString(R.string.stop));
        timePausedAt = System.currentTimeMillis();
        state = STATE.STOPPED;
    }

    public void start() {
        initHandler();
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

    private void initHandler() {
        if (handler == null) {
            if (SDKSettings.isBackgroundThreadingEnabled()) {
                HandlerThread backgroundThread = new HandlerThread(ANBACKGROUND);
                backgroundThread.start();
                handler = new RequestHandler(this, backgroundThread.getLooper());
            } else {
                handler = new RequestHandler(this, Looper.myLooper());
            }
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

    public void clearDurations() {
        lastFetchTime = -1;
        timePausedAt = -1;
    }

    private class MessageRunnable implements Runnable {

        @Override
        public void run() {
            Clog.v(Clog.baseLogTag,
                    Clog.getString(R.string.handler_message_pass));
            if(handler != null) {
                handler.sendEmptyMessage(0);
            }

        }

    }

    // Create a handler which will receive the AsyncTasks and spawn them from
    // the main thread.
    private static class RequestHandler extends Handler {
        private final WeakReference<AdFetcher> mFetcher;

        RequestHandler(AdFetcher f, Looper looper) {
            super(looper);
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
                    || (fetcher.owner != null && !fetcher.owner.isReadyToStart()))
                return;

            // Update last fetch time once
            // For sane logging, don't report negative times
            if (fetcher.lastFetchTime != -1) {
                Clog.d(Clog.baseLogTag,
                        Clog.getString(
                                R.string.new_ad_since,
                                Math.max(0, (int) (System.currentTimeMillis() - fetcher.lastFetchTime))));
                // Condition to restrict the Auto Refresh for the Lazy Loaded Ad.
                if (fetcher.owner != null && fetcher.owner instanceof BannerAdView && ((BannerAdView)fetcher.owner).isLazyWebviewInactive() && ((BannerAdView)fetcher.owner).isLastResponseSuccessful()) {
                    Clog.w(Clog.lazyLoadLogTag, "Not Fetching due to Lazy Load");
                    return;
                }
            }
            // Checks if the lazy load is enabled and de activates the Webview (activateWebview - boolean in the AdView), so that the AutoRefresh for Lazy Load can work.
            // Doing this will deActivate the Webview, which will be required to be activated by calling the loadLazyAd() later.
            // The deActivated webview means the AutoRefresh needs to pause.
            if (fetcher.owner != null && fetcher.owner instanceof BannerAdView && ((BannerAdView)fetcher.owner).isLazyLoadEnabled()) {
                Clog.e(Clog.lazyLoadLogTag, "Lazy Load Fetching");
                ((AdView)fetcher.owner).deactivateWebviewForNextCall();
            }
            fetcher.lastFetchTime = System.currentTimeMillis();

            // Spawn an AdRequest
            if (fetcher.owner == null && fetcher.anMultiAdRequest != null && fetcher.anMultiAdRequest.isMARRequestInProgress()) {
                fetcher.requestManager = new AdViewRequestManager(fetcher.anMultiAdRequest);
                fetcher.requestManager.execute();
            } else {
                // Sanity null check
                if (fetcher.owner != null) {
                    MediaType mediaType = fetcher.owner.getMediaType();
                    if (mediaType.equals(MediaType.NATIVE) || mediaType.equals(MediaType.INTERSTITIAL) || mediaType.equals(MediaType.BANNER) || mediaType.equals(MediaType.INSTREAM_VIDEO)) {
                        fetcher.requestManager = new AdViewRequestManager(fetcher.owner);
                        fetcher.requestManager.execute();
                    } else {
                        fetcher.owner.getAdDispatcher().onAdFailed(ResultCode.getNewInstance(ResultCode.INVALID_REQUEST), null);
                    }
                }
            }
        }
    }

    int getPeriod() {
        return this.period;
    }

    STATE getState() {
        return this.state;
    }

    protected void loadLazyAd() {
        if (requestManager != null && requestManager instanceof AdViewRequestManager) {
            ((AdViewRequestManager)requestManager).loadLazyAd();
        }
    }
}
