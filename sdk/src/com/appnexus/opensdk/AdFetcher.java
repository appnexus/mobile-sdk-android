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

public class AdFetcher implements AdRequester {
    private ScheduledExecutorService tasker;
    protected AdView owner;
    private int period = -1;
    private boolean autoRefresh;
    private RequestHandler handler;
    private boolean shouldReset = false;
    private long lastFetchTime = -1;
    private long timePausedAt = -1;
    private boolean shouldShowTrueTime = false;

    // Fires requests whenever it receives a message
    public AdFetcher(AdView owner) {
        this.owner = owner;
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

    protected void start() {
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

        if (!getAutoRefresh()) {
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
            if (!shouldShowTrueTime) {
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

    class MessageRunnable implements Runnable {

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
            if (mFetcher.get() == null
                    || mFetcher.get().owner.isMRAIDExpanded())
                return;

            // If we need to reset, reset.
            if (mFetcher.get().shouldReset) {
                mFetcher.get().shouldReset = false;
                mFetcher.get().stop();
                mFetcher.get().start();
                return;
            }

            // Update last fetch time once
            Clog.d(Clog.baseLogTag,
                    Clog.getString(
                            R.string.new_ad_since,
                            (int) (System.currentTimeMillis() - mFetcher.get().lastFetchTime)));
            mFetcher.get().lastFetchTime = System.currentTimeMillis();

            // Spawn an AdRequest
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new AdRequest(mFetcher.get())
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                new AdRequest(mFetcher.get()).execute();
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

    @Override
    public void failed(AdRequest request) {
        owner.fail();
    }

    @Override
    public void onReceiveResponse(final AdResponse response) {
        this.owner.post(new Runnable() {
            public void run() {

                boolean responseHasAds = (response != null) && response.containsAds();
                boolean ownerHasAds = (owner.getMediatedAds() != null) && !owner.getMediatedAds().isEmpty();

                // no ads in the response and no old ads means no fill
                if (!responseHasAds && !ownerHasAds) {
                    Clog.w(Clog.httpRespLogTag, Clog.getString(R.string.response_no_ads));
                    requestFailed();
                    return;
                }

                if (responseHasAds) {
                    // if non-mediated ad is overriding the list,
                    // this will be null and skip the loop for mediation
                    owner.setMediatedAds(response.getMediatedAds());
                }

                if ((owner.getMediatedAds() != null) && !owner.getMediatedAds().isEmpty()) {
                    // mediated
                    if (owner.isBanner()) {
                        MediatedBannerAdViewController output = MediatedBannerAdViewController.create(
                                (Activity) owner.getContext(),
                                owner.mAdFetcher,
                                owner.popMediatedAd(),
                                owner);
                    }
                    else if (owner.isInterstitial()) {
                        MediatedInterstitialAdViewController output = MediatedInterstitialAdViewController.create(
                                (Activity) owner.getContext(),
                                owner.mAdFetcher,
                                owner.popMediatedAd(),
                                owner);
                        if (output != null)
                            output.getView();
                    }
                } else if ((response != null)
                        && response.isMraid) {
                    // mraid
                    MRAIDWebView output = new MRAIDWebView(owner);
                    output.loadAd(response);
                    owner.onAdLoaded(output);
                } else {
                    // standard
                    AdWebView output = new AdWebView(owner);
                    output.loadAd(response);
                    owner.onAdLoaded(output);
                }
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
