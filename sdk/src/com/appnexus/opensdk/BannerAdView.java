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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.FrameLayout;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.WebviewUtil;

/**
 * This view is added to an existing layout in order to display banner ads.
 * It may be added via XML or via code
 *
 * Note that you must insert your Placement ID.
 * <pre>
 * {@code
 *
 * <com.appnexus.opensdk.BannerAdView
 *           android:id="@+id/banner"
 *           android:layout_width="wrap_content"
 *           android:layout_height="wrap_content"
 *           android:placement_id="YOUR PLACEMENT ID"
 *           android:auto_refresh=true
 *           android:auto_refresh_interval=30
 *           android:opens_native_browser=true
 *           android:adWidth=320
 *           android:adHeight=50
 *           android:should_reload_on_resume=true
 *           android:opens_native_browser=true
 *           android:expands_to_fit_screen_width=false
 *           />
 * }
 * </pre>
 *
 * In code you do the following
 * {@code
 *   blah
 *   blah
 *   blah
 * }
 *
 *
 *
 */
public class BannerAdView extends AdView {

    private int period;
    private boolean auto_refresh;
    private boolean running;
    private boolean shouldReloadOnResume;
    private BroadcastReceiver receiver;
    private boolean receiversRegistered;
    protected boolean shouldResetContainer = false;
    private boolean expandsToFitScreenWidth = false;
    private int width = -1;
    private int height = -1;

    private void setDefaultsBeforeXML() {
        running = false;
        auto_refresh = false;
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
        setup(context, null);
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
        setup(context, attrs);
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
        setup(context, attrs);
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

    void setupBroadcast(Context context) {
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

    /**
     * Call this method to start loading an ad into this view.
     * Ad loading is asynchronous, that is calling this method will request
     * an ad from the server. If you wish to know whether the ad failed to load or was successfully loaded
     *  use the AdListener object to receive the corresponding events.
     * @return true if the ad load was successfully dispatched, false otherwise
     */
    @Override
    public boolean loadAd() {
        running = true;
        return super.loadAd();
    }

    /**
     * Loads a new ad, if the ad space is visible, and sets the placement id, ad
     * width, and ad height attribute of the AdView to the supplied parameters.
     *
     * @param placementID
     *            The new placement id to use.
     * @param width
     *            The new width to use.
     * @param height
     *            The new height to use.
     *
     * @return true is ad will begin loading, false otherwise
     */
    public boolean loadAd(String placementID, int width, int height) {
        setAdSize(width, height);
        this.setPlacementID(placementID);
        return loadAd();
    }

    void start() {
        Clog.d(Clog.publicFunctionsLogTag, Clog.getString(R.string.start));
        mAdFetcher.start();
        running = true;
    }

    void stop() {
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
            } else if (attr == R.styleable.BannerAdView_adWidth) {
                setAdWidth(a.getInt(attr, -1));
                Clog.d(Clog.xmlLogTag,
                        Clog.getString(R.string.xml_ad_width,
                                a.getInt(attr, -1)));
            } else if (attr == R.styleable.BannerAdView_adHeight) {
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
            }else if (attr == R.styleable.BannerAdView_expands_to_fit_screen_width){
                setExpandsToFitScreenWidth(a.getBoolean(attr, false));
                Clog.d(Clog.xmlLogTag, Clog.getString(
                        R.string.xml_set_expands_to_full_screen_width,
                        expandsToFitScreenWidth
                ));
            }
        }
        a.recycle();
    }

    /**
     * Retrieve the current set auto refresh interval.
     * @return The interval, in milliseconds, at which the BannerAdView will
     *         request new ads, if autorefresh is enabled.
     */
    public int getAutoRefreshInterval() {
        Clog.d(Clog.publicFunctionsLogTag,
                Clog.getString(R.string.get_period, period));
        return period;
    }

    /**
     * Sets the height of the ad to request.
     *
     * @deprecated Favor setAdSize(int w, int h)
     * @param h The height, in pixels, to use.
     */
    @Deprecated
    public void setAdHeight(int h) {
        Clog.d(Clog.baseLogTag, Clog.getString(R.string.set_height, h));
        height = h;
    }

    /**
     * Sets the width of the ad to request.
     *
     * @deprecated Favor setAdSize(int w, int h)
     * @param w The width, in pixels, to use.
     */
    @Deprecated
    public void setAdWidth(int w) {
        Clog.d(Clog.baseLogTag, Clog.getString(R.string.set_width, w));
        width = w;
    }

    /**
     * Sets the size of the ad to request.
     *
     * @param w The width, in pixels, to use.
     * @param h The height, in pixels, to use.
     */
    public void setAdSize(int w, int h){
        Clog.d(Clog.baseLogTag, Clog.getString(R.string.set_size, w, h));
        width=w;
        height=h;
    }

    /**
     * @return The height of the ad to be requested.
     */
    public int getAdHeight() {
        Clog.d(Clog.baseLogTag, Clog.getString(R.string.get_height, height));
        return height;
    }

    /**
     * @return The width of the ad to be requested.
     */
    public int getAdWidth() {
        Clog.d(Clog.baseLogTag, Clog.getString(R.string.get_width, width));
        return width;
    }

    /**
     * @param period The interval, in milliseconds, at which the BannerAdView will
     *               request new ads, if autorefresh is enabled. The minimum period
     *               is 15 seconds. The default period is 30 seconds.
     */
    public void setAutoRefreshInterval(int period) {
        this.period = Math.max(Settings.getSettings().MIN_REFRESH_MILLISECONDS,
                period);
        if (period > 0) {
            Clog.d(Clog.publicFunctionsLogTag,
                    Clog.getString(R.string.set_period, this.period));
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
        if (this.auto_refresh && !running && mAdFetcher != null) {
            start();
        }
    }

    /**
     * Retrieves the current shouldAutoReload on resume value
     * @return True add will reload on resume, false otherwise.
     */
    public boolean getShouldReloadOnResume() {
        Clog.d(Clog.publicFunctionsLogTag, Clog.getString(
                R.string.get_should_resume, shouldReloadOnResume));
        return shouldReloadOnResume;
    }

    /**
     *  Whether or not this view should load a new ad if the user
     *  resumes use of the app from a screenlock or multitask.
     * @param shouldReloadOnResume True to reload on resume, false otherwise
     */
    void setShouldReloadOnResume(boolean shouldReloadOnResume) {
        Clog.d(Clog.publicFunctionsLogTag, Clog.getString(
                R.string.set_should_resume, shouldReloadOnResume));
        this.shouldReloadOnResume = shouldReloadOnResume;
    }

    private boolean requesting_visible = true;


    @Override
    protected void onWindowVisibilityChanged(int visibility) {
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

            if (getChildAt(0) instanceof WebView) {
                WebView webView = (WebView) getChildAt(0);
                WebviewUtil.onResume(webView);
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

            if (getChildAt(0) instanceof WebView) {
                WebView webView = (WebView) getChildAt(0);
                WebviewUtil.onPause(webView);
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

    /**
     * Retrieves the current expandsToFitWindowWidth setting.
     *
     * @return true ad expands to fit screen width, false otherwise
     */
    public boolean getExpandsToFitScreenWidth() {
        return expandsToFitScreenWidth;
    }

    /**
     * Enable the expand ad to fit screen width. This feature will cause ad creatives
     * that are smaller than the view size to 'stretch' to the current size. This may cause
     * image quality degradation for the benefit of having an ad occupy the entire adview.
     * By default his feature is disabled
     * @param expandsToFitScreenWidth true to enable the automatic expansion, false otherwise
     */
    public void setExpandsToFitScreenWidth(boolean expandsToFitScreenWidth) {
        this.expandsToFitScreenWidth = expandsToFitScreenWidth;
    }

    protected int oldH;
    protected int oldW;

    @SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	protected void expandToFitScreenWidth(int adWidth, int adHeight, AdWebView webview) {
        //Determine the width of the screen
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        int width=-1;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2){
            Point p = new Point();
            display.getSize(p);
            width=p.x;
        }else{
            width=display.getWidth();
        }
        float ratio_delta = ((float) width)/((float) adWidth);
        int new_height = (int)(adHeight*ratio_delta);
        oldH = getLayoutParams().height;
        oldW = getLayoutParams().width;

        //Adjust width of container
        if(getLayoutParams().width>0){
            getLayoutParams().width=(int)(adWidth*ratio_delta);
        }else if(getLayoutParams().width==ViewGroup.LayoutParams.WRAP_CONTENT){
            getLayoutParams().width=(int)(adWidth*ratio_delta);
        }
        //Adjust height of container
        getLayoutParams().height=new_height;

        //Adjust height of webview
        if(webview.getLayoutParams()==null){
            webview.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        }else{
            webview.getLayoutParams().width = FrameLayout.LayoutParams.MATCH_PARENT;
            webview.getLayoutParams().height = FrameLayout.LayoutParams.MATCH_PARENT;
        }

        webview.setInitialScale((int)(ratio_delta*100));

        webview.invalidate();

        shouldResetContainer =true;

    }

    protected void resetContainer() {
        shouldResetContainer =false;
        if(getLayoutParams()!=null){
            getLayoutParams().height = oldH;
            getLayoutParams().width = oldW;
        }
    }

    void resetContainerIfNeeded() {
        if(this.shouldResetContainer){
            resetContainer();
        }
    }
}
