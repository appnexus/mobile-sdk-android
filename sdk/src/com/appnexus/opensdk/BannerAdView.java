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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;

/**
 * This view is added to an existing layout in order to display ads.
 *
 * @author Jacob Shufro
 */
public class BannerAdView extends AdView {

    private int period;
    private boolean auto_refresh;
    private boolean running;
    private boolean shouldReloadOnResume;
    private BroadcastReceiver receiver;
    private boolean receiversRegistered;

    private void setDefaultsBeforeXML() {
        running = false;
        auto_refresh = true;
        shouldReloadOnResume = false;
        receiversRegistered = false;
    }

    /**
     * Creates a new BannerAdView
     *
     * @param context The context of the ViewGroup to which the BannerAdView is
     *                being added.
     */
    public BannerAdView(Context context) {
        super(context);
    }

    /**
     * Creates a new BannerAdView
     *
     * @param context The context of the ViewGroup to which the BannerAdView is
     *                being added.
     * @param attrs   The AttributeSet to use when creating the BannerAdView.
     */
    public BannerAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Creates a new BannerAdView
     *
     * @param context  The context of the ViewGroup to which the BannerAdView is
     *                 being added.
     * @param attrs    The AttributeSet to use when creating the BannerAdView.
     * @param defStyle The default style to apply to this view. If 0, no style will
     *                 be applied (beyond what is included in the theme). This may
     *                 either be an attribute resource, whose value will be retrieved
     *                 from the current theme, or an explicit style resource.
     */
    public BannerAdView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Creates a new BannerAdView
     *
     * @param context          The context of the ViewGroup to which the BannerAdView is
     *                         being added.
     * @param refresh_interval The desired refresh rate, in milliseconds. By default, 30
     *                         seconds. Minimum is 15 seconds. 0 turns auto-refresh off.
     */
    public BannerAdView(Context context, int refresh_interval) {
        super(context);
        if (refresh_interval == 0) {
            this.setAutoRefresh(false);
        } else {
            this.setAutoRefresh(true);
            this.setAutoRefreshInterval(refresh_interval);
        }
    }

    @Override
    protected void setup(Context context, AttributeSet attrs) {
        super.setup(context, attrs);
        onFirstLayout();
        mAdFetcher.setPeriod(period);
        mAdFetcher.setAutoRefresh(getAutoRefresh());
    }

    protected void setupBroadcast(Context context) {
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                    stop();
                    Clog.d(Clog.baseLogTag,
                            Clog.getString(R.string.screen_off_stop));
                } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                    if (auto_refresh)
                        start();
                    else if (shouldReloadOnResume)
                        stop();
                    start();
                    Clog.d(Clog.baseLogTag,
                            Clog.getString(R.string.screen_on_start));
                }

            }

        };
        context.registerReceiver(receiver, filter);
    }

    @Override
    public final void onLayout(boolean changed, int left, int top, int right,
                               int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // Are we coming back from a screen/user presence change?
        if (running) {
            if (!receiversRegistered) {
                setupBroadcast(getContext());
                receiversRegistered = true;
            }
            if (shouldReloadOnResume) {
                start();
            }
        }

    }

    // Make sure receiver is registered.
    private void onFirstLayout() {
        if (this.auto_refresh) {
            if (!receiversRegistered) {
                setupBroadcast(getContext());
                receiversRegistered = true;
            }
        }
    }

    protected void start() {
        Clog.d(Clog.publicFunctionsLogTag, Clog.getString(R.string.start));
        mAdFetcher.start();
        running = true;
    }

    protected void stop() {
        Clog.d(Clog.publicFunctionsLogTag, Clog.getString(R.string.stop));
        mAdFetcher.stop();
        running = false;
    }

    @Override
    protected void loadVariablesFromXML(Context context, AttributeSet attrs) {
        // Defaults
        setDefaultsBeforeXML();

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.BannerAdView);

        final int N = a.getIndexCount();
        Clog.v(Clog.xmlLogTag, Clog.getString(R.string.found_n_in_xml, N));
        for (int i = 0; i < N; ++i) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.BannerAdView_placement_id) {
                setPlacementID(a.getString(attr));
                Clog.d(Clog.xmlLogTag, Clog.getString(R.string.placement_id,
                        a.getString(attr)));
            } else if (attr == R.styleable.BannerAdView_auto_refresh_interval) {
                setAutoRefreshInterval(a.getInt(attr, 30 * 1000));
                Clog.d(Clog.xmlLogTag,
                        Clog.getString(R.string.xml_set_period, period));
            } else if (attr == R.styleable.BannerAdView_test) {
                Settings.getSettings().test_mode = a.getBoolean(attr, false);
                Clog.d(Clog.xmlLogTag,
                        Clog.getString(R.string.xml_set_test,
                                Settings.getSettings().test_mode));
            } else if (attr == R.styleable.BannerAdView_auto_refresh) {
                setAutoRefresh(a.getBoolean(attr, false));
                Clog.d(Clog.xmlLogTag, Clog.getString(
                        R.string.xml_set_auto_refresh, auto_refresh));
            } else if (attr == R.styleable.BannerAdView_width) {
                setAdWidth(a.getInt(attr, -1));
                Clog.d(Clog.xmlLogTag,
                        Clog.getString(R.string.xml_ad_width,
                                a.getInt(attr, -1)));
            } else if (attr == R.styleable.BannerAdView_height) {
                setAdHeight(a.getInt(attr, -1));
                Clog.d(Clog.xmlLogTag,
                        Clog.getString(R.string.xml_ad_height,
                                a.getInt(attr, -1)));
            } else if (attr == R.styleable.BannerAdView_should_reload_on_resume) {
                setShouldReloadOnResume(a.getBoolean(attr, false));
                Clog.d(Clog.xmlLogTag, Clog.getString(
                        R.string.xml_set_should_reload, shouldReloadOnResume));
            } else if (attr == R.styleable.BannerAdView_opens_native_browser) {
                setOpensNativeBrowser(a.getBoolean(attr, false));
                Clog.d(Clog.xmlLogTag, Clog.getString(
                        R.string.xml_set_opens_native_browser,
                        opensNativeBrowser));
            }
        }
        a.recycle();
    }

    /**
     * @return The interval, in milliseconds, at which the BannerAdView will
     *         request new ads, if autorefresh is enabled.
     */
    public int getAutoRefreshInterval() {
        Clog.d(Clog.publicFunctionsLogTag,
                Clog.getString(R.string.get_period, period));
        return period;
    }

    /**
     * @param period The interval, in milliseconds, at which the BannerAdView will
     *               request new ads, if autorefresh is enabled. The minimum period
     *               is 15 seconds. The default period is 30 seconds.
     */
    public void setAutoRefreshInterval(int period) {
        Clog.d(Clog.publicFunctionsLogTag,
                Clog.getString(R.string.set_period, period));
        this.period = Math.max(Settings.getSettings().MIN_REFRESH_MILLISECONDS,
                period);
        if (period > 0) {
            setAutoRefresh(true);
        } else {
            setAutoRefresh(false);
        }
        if (mAdFetcher != null)
            mAdFetcher.setPeriod(this.period);
    }

    /**
     * @return Whether this view should periodically request new ads.
     */
    private boolean getAutoRefresh() {
        Clog.d(Clog.publicFunctionsLogTag,
                Clog.getString(R.string.get_auto_refresh, auto_refresh));
        return auto_refresh;
    }

    /**
     * @param auto_refresh Whether this view should periodically request new ads.
     */
    private void setAutoRefresh(boolean auto_refresh) {
        Clog.d(Clog.publicFunctionsLogTag,
                Clog.getString(R.string.set_auto_refresh, auto_refresh));
        this.auto_refresh = auto_refresh;
        if (mAdFetcher != null) {
            mAdFetcher.setAutoRefresh(auto_refresh);
            mAdFetcher.clearDurations();
        }
        if (!running && mAdFetcher != null) {
            start();
        }
    }

    /**
     * @return Whether or not this view should load a new ad if the user resumes
     *         use of the app from a screenlock or multitask.
     */
    public boolean getShouldReloadOnResume() {
        Clog.d(Clog.publicFunctionsLogTag, Clog.getString(
                R.string.get_should_resume, shouldReloadOnResume));
        return shouldReloadOnResume;
    }

    /**
     * @param shouldReloadOnResume Whether or not this view should load a new ad if the user
     *                             resumes use of the app from a screenlock or multitask.
     */
    public void setShouldReloadOnResume(boolean shouldReloadOnResume) {
        Clog.d(Clog.publicFunctionsLogTag, Clog.getString(
                R.string.set_should_resume, shouldReloadOnResume));
        this.shouldReloadOnResume = shouldReloadOnResume;
    }

    private boolean requesting_visible = false;

    @Override
    public void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            // Register a broadcast receiver to pause and refresh when the phone
            // is
            // locked
            if (!receiversRegistered) {
                setupBroadcast(getContext());
                receiversRegistered = true;
            }
            Clog.d(Clog.baseLogTag, Clog.getString(R.string.unhidden));
            if (mAdFetcher != null
                    && (!requesting_visible || running || shouldReloadOnResume || auto_refresh))
                start();
            else {
                // Were' not displaying the adview, the system is
                requesting_visible = false;
            }
        } else {
            // Unregister the receiver to prevent a leak.
            if (receiversRegistered) {
                dismantleBroadcast();
                receiversRegistered = false;
            }
            Clog.d(Clog.baseLogTag, Clog.getString(R.string.hidden));
            if (mAdFetcher != null && running) {
                stop();
            }
        }
    }

    private void dismantleBroadcast() {
        getContext().unregisterReceiver(receiver);
    }

    @Override
    protected void unhide() {
        super.unhide();
        this.requesting_visible = true;
    }

    @Override
    boolean isBanner() {
        return true;
    }

    @Override
    boolean isInterstitial() {
        return false;
    }
}
