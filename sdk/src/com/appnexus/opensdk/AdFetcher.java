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
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class AdFetcher implements AdRequester {
    private ScheduledExecutorService tasker;
    private final AdView owner;
    private int period = -1;
    private boolean autoRefresh;
    private final RequestHandler handler;
    private boolean shouldReset = false;
    private long lastFetchTime = -1;
    private long timePausedAt = -1;
    private AdRequest adRequest;

    // Fires requests whenever it receives a message
    public AdFetcher(AdView owner) {
        this.owner = owner;
        handler = new RequestHandler(this);
    }

    void setPeriod(int period) {
        this.period = period;
        if (tasker != null)
            shouldReset = true;
    }

    protected int getPeriod() {
        return period;
    }

    void stop() {
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

    private void requestFailed() {
        owner.fail();
    }

    void start() {
        Clog.d(Clog.baseLogTag, Clog.getString(R.string.start));
        if (tasker != null) {
            Clog.d(Clog.baseLogTag, Clog.getString(R.string.moot_restart));
            requestFailed();
            return;
        }
        makeTasker();
    }

    private void makeTasker() {
        // Start a Scheduler to execute recurring tasks
        tasker = Executors
                .newScheduledThreadPool(Settings.getSettings().FETCH_THREAD_COUNT);

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
                stall_temp = msPeriod - (timePausedAt - lastFetchTime);
            } else {
                stall_temp = 0;
            }

            final long stall = stall_temp;
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
    static class RequestHandler extends Handler {
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
                    || !fetcher.owner.isReadyToStart())
                return;

            // If we need to reset, reset.
            if (fetcher.shouldReset) {
                fetcher.shouldReset = false;
                fetcher.stop();
                fetcher.start();
                return;
            }

            // Update last fetch time once
            if (fetcher.lastFetchTime != -1) {
                Clog.d(Clog.baseLogTag,
                        Clog.getString(
                                R.string.new_ad_since,
                                (int) (System.currentTimeMillis() - fetcher.lastFetchTime)));
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

    boolean getAutoRefresh() {
        return autoRefresh;
    }

    void setAutoRefresh(boolean autoRefresh) {
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

    @Override
    public void failed(AdRequest request) {
        owner.fail();
    }

    public void dispatchResponse(final AdResponse response) {

        if ((owner.getMediatedAds() != null) && !owner.getMediatedAds().isEmpty()) {
            MediatedAd mediatedAd = owner.popMediatedAd();
            if ((mediatedAd != null) && (response != null)) {
                mediatedAd.setExtras(response.getExtras());
            }
            // mediated
            if (owner.isBanner()) {
                MediatedBannerAdViewController.create(
                        (Activity) owner.getContext(),
                        owner.mAdFetcher,
                        mediatedAd,
                        owner.getAdDispatcher());
            } else if (owner.isInterstitial()) {
                MediatedInterstitialAdViewController.create(
                        (Activity) owner.getContext(),
                        owner.mAdFetcher,
                        mediatedAd,
                        owner.getAdDispatcher());
            }
        } else {
            AdWebView output = new AdWebView(owner);
            output.loadAd(response);
            // standard
            if (owner.isBanner()) {
                BannerAdView bav = (BannerAdView) owner;
                if (bav.getExpandsToFitScreenWidth() && (response != null)) {
                    bav.expandToFitScreenWidth(response.getWidth(), response.getHeight(), output);
                }
            }
            owner.getAdDispatcher().onAdLoaded(output);
        }
    }

    public void onReceiveResponse(final AdResponse response) {
        boolean responseHasAds = (response != null) && response.containsAds();
        boolean ownerHasAds = (owner.getMediatedAds() != null) && !owner.getMediatedAds().isEmpty();

        // no ads in the response and no old ads means no fill
        if (!responseHasAds && !ownerHasAds) {
            Clog.w(Clog.httpRespLogTag, Clog.getString(R.string.response_no_ads));
            requestFailed();
            return;
        }

        //If we're about to dispatch a creative to a banneradview that has been resized by ad stretching, reset it's size
        if (owner.isBanner()) {
            BannerAdView bav = (BannerAdView) owner;
            bav.resetContainerIfNeeded();
        }

        if (responseHasAds) {
            // if non-mediated ad is overriding the list,
            // this will be null and skip the loop for mediation
            owner.setMediatedAds(response.getMediatedAds());
        }

        this.owner.handler.post(new Runnable() {
            @Override
            public void run() {
                AdFetcher.this.dispatchResponse(response);
            }
        });

    }

    @Override
    public AdView getOwner() {
        return owner;
    }

    public void clearDurations() {
        lastFetchTime = -1;
        timePausedAt = -1;

    }
}
