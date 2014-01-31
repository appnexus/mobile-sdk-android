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
import android.view.*;
import android.webkit.WebView;
import android.widget.FrameLayout;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.WebviewUtil;

/**
 * This view is added to an existing layout in order to display banner
 * ads.  It may be added via XML or code.
 *
 * <p>
 * Note that you need a placement ID in order to show ads.  If you
 * don't have a placement ID, you'll need to get one from your
 * AppNexus representative or your ad network.
 * </p>
 * Using XML, you might add it like this:
 *
 * <pre>
 * {@code
 *
 * <com.appnexus.opensdk.BannerAdView
 *           android:id="@+id/banner"
 *           android:layout_width="wrap_content"
 *           android:layout_height="wrap_content"
 *           android:placement_id="YOUR PLACEMENT ID"
 *           android:auto_refresh="true"
 *           android:auto_refresh_interval="30"
 *           android:opens_native_browser="true"
 *           android:adWidth="320"
 *           android:adHeight="50"
 *           android:should_reload_on_resume="true"
 *           android:opens_native_browser="true"
 *           android:expands_to_fit_screen_width="false"
 *           />
 * }
 * </pre>
 *
 * In code you can do the following:
 *
 * <pre>
 * {@code
 * RelativeLayout rl = (RelativeLayout)(findViewById(R.id.mainview));
 * AdView av = new BannerAdView(this);
 * LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, 100);
 * av.setAdHeight(50);
 * av.setAdWidth(320);
 * av.setLayoutParams(lp);
 * av.setPlacementID("12345");
 * rl.addView(av);
 * av.loadAd();
 * }
 * </pre>
 *
 *
 */
public class BannerAdView extends AdView {

    private int period;
    private boolean auto_refresh = true;
    private boolean loadAdHasBeenCalled;
    private boolean shouldReloadOnResume;
    private BroadcastReceiver receiver;
    private boolean receiversRegistered;
    protected boolean shouldResetContainer = false;
    private boolean expandsToFitScreenWidth = false;
    private int width = -1;
    private int height = -1;

    private void setDefaultsBeforeXML() {
        loadAdHasBeenCalled = false;
        auto_refresh = true;
        shouldReloadOnResume = false;
        receiversRegistered = false;
    }

    /**
     * Create a new BannerAdView in which to load and show ads.
     *
     * @param context The context of the {@link ViewGroup} to which
     *                the BannerAdView is being added.
     */
    public BannerAdView(Context context) {
        super(context);
        setup(context, null);
    }

    /**
     * Create a new BannerAdView in which to load and show ads.
     *
     * @param context The context of the {@link ViewGroup} to which
     *                the BannerAdView is being added.
     *
     * @param attrs The {@link AttributeSet} to use when creating the
     *              BannerAdView.
     */
    public BannerAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs);
    }

    /**
     * Create a new BannerAdView in which to load and show ads.
     *
     * @param context The context of the {@link ViewGroup} to which
     *                the BannerAdView is being added.
     *
     * @param attrs The {@link AttributeSet} to use when creating the
     *              BannerAdView.

     * @param defStyle The default style to apply to this view.  If 0,
     *                 no style will be applied (beyond what is
     *                 included in the theme).  This may be either an
     *                 attribute resource, whose value will be
     *                 retrieved from the current theme, or an
     *                 explicit style resource.
     */
    public BannerAdView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup(context, attrs);
    }

    /**
     * Creates a new BannerAdView in which to load and show ads.
     *
     * @param context The context of the {@link ViewGroup} to which
     *                the BannerAdView is being added.
     *
     * @param refresh_interval The desired refresh rate, in
     *                         milliseconds.  The default value is 30
     *                         seconds; minimum is 15.  A value of 0
     *                         turns auto-refreshing off.
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
        mAdFetcher.setAutoRefresh(auto_refresh);
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
        if (loadAdHasBeenCalled) {
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
     * Call this method to start loading an ad into this view
     * asynchronously.  This will request an ad from the server.  If
     * you wish to know whether the ad succeeded or failed to load,
     * use the {@link AdListener} object to receive the corresponding
     * events.
     *
     * @return <code>true</code> if the ad load was successfully
     *         dispatched, false otherwise.
     */
    @Override
    public boolean loadAd() {
        loadAdHasBeenCalled = true;
        if(super.loadAd())
            return true;
        else{
            loadAdHasBeenCalled = false;
            return false;
        }
    }

    /**
     * Loads a new ad, if the ad space is visible, and sets the
     * placement ID, ad width, and ad height attributes of the AdView.
     *
     * @param placementID
     *        The placement ID to use in this view.
     * @param width
     *        The width of the ad.
     * @param height
     *        The height of the ad.
     *
     * @return <code>true</code> if the ad will begin loading,
     *         <code>false</code> otherwise.
     */
    public boolean loadAd(String placementID, int width, int height) {
        setAdSize(width, height);
        this.setPlacementID(placementID);
        return loadAd();
    }

    @Override
    void display(Displayable d) {
        super.display(d);

        WebView webView = null;
        if (getChildAt(0) instanceof WebView) {
            webView = (WebView) getChildAt(0);
        }

        this.removeAllViews();
        if (webView != null)
            webView.destroy();

        View displayableView = d.getView();
        this.addView(displayableView);

        // center the displayable view in AdView
        ((LayoutParams) displayableView.getLayoutParams()).gravity = Gravity.CENTER;

        unhide();
    }

    void start() {
        Clog.d(Clog.publicFunctionsLogTag, Clog.getString(R.string.start));
        mAdFetcher.start();
        loadAdHasBeenCalled = true;
    }

    void stop() {
        Clog.d(Clog.publicFunctionsLogTag, Clog.getString(R.string.stop));
        mAdFetcher.stop();
        loadAdHasBeenCalled = false;
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
     * Retrieve the currently set auto-refresh interval.
     *
     * @return The interval, in milliseconds, at which the
     *         BannerAdView will request new ads, if auto-refresh is
     *         enabled.
     */
    public int getAutoRefreshInterval() {
        Clog.d(Clog.publicFunctionsLogTag,
                Clog.getString(R.string.get_period, period));
        return period;
    }

    /**
     * Set the height of the ad to request.
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
     * Set the width of the ad to request.
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
     * Set the size of the ad to request.
     *
     * @param w The width of the ad, in pixels.
     * @param h The height of the ad, in pixels.
     */
    public void setAdSize(int w, int h){
        Clog.d(Clog.baseLogTag, Clog.getString(R.string.set_size, w, h));
        width=w;
        height=h;
    }

    /**
     * Check the height of the ad to be requested for this view.
     *
     * @return The height of the ad to request.
     */
    public int getAdHeight() {
        Clog.d(Clog.baseLogTag, Clog.getString(R.string.get_height, height));
        return height;
    }

    /**
     * Check the width of the ad to be requested for this view.
     *
     * @return The width of the ad to request.
     */
    public int getAdWidth() {
        Clog.d(Clog.baseLogTag, Clog.getString(R.string.get_width, width));
        return width;
    }

    /**
     * Set the auto-refresh interval.  This is the interval, in
     * milliseconds, at which the BannerAdView will request new ads,
     * if auto-refresh is enabled.  The default period is 30 seconds;
     * the minimum is 15.  You can enable or disable auto-refresh
     * using the setAutoRefresh method.
     *
     * @param period The auto-refresh interval, in milliseconds.
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
     * Check whether auto-refresh is currently enabled for this ad
     * view.
     *
     * @return If true, this view will periodically request new ads.
     */
    private boolean getAutoRefresh() {
        Clog.d(Clog.publicFunctionsLogTag,
                Clog.getString(R.string.get_auto_refresh, auto_refresh));
        return auto_refresh;
    }

    /**
     * Turn the auto-refresh setting for this ad view on or off.
     *
     * @param auto_refresh If set to true, this view will periodically
     *                     request new ads.
     */
    private void setAutoRefresh(boolean auto_refresh) {
        Clog.d(Clog.publicFunctionsLogTag,
                Clog.getString(R.string.set_auto_refresh, auto_refresh));
        this.auto_refresh = auto_refresh;
        if (mAdFetcher != null) {
            mAdFetcher.setAutoRefresh(auto_refresh);
            mAdFetcher.clearDurations();
        }
        if (this.auto_refresh && !loadAdHasBeenCalled && mAdFetcher != null) {
            start();
        }
    }

    /**
     * Check whether the ad view will load a new ad if the user
     * resumes use of the app from a screenlock or multitask.
     *
     * @return If true, the ad will reload on resume.
     */
    public boolean getShouldReloadOnResume() {
        Clog.d(Clog.publicFunctionsLogTag, Clog.getString(
                R.string.get_should_resume, shouldReloadOnResume));
        return shouldReloadOnResume;
    }

    /**
     * Set whether or not this view should load a new ad if the user
     * resumes use of the app from a screenlock or multitask.
     *
     * @param shouldReloadOnResume Set this to true to reload the ad
     *                             on resume.
     */
    void setShouldReloadOnResume(boolean shouldReloadOnResume) {
        Clog.d(Clog.publicFunctionsLogTag, Clog.getString(
                R.string.set_should_resume, shouldReloadOnResume));
        this.shouldReloadOnResume = shouldReloadOnResume;
    }


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
            //The only time we want to request on visibility changes is if an ad hasn't been loaded yet (loadAdHasBeenCalled)
            // shouldReloadOnResume is true
            // OR auto_refresh is enabled
            if(loadAdHasBeenCalled || shouldReloadOnResume || auto_refresh){

                //If we're MRAID mraid_is_closing or expanding, don't load.
                if (!mraid_is_closing && !mraid_changing_size_or_visibility && !isMRAIDExpanded() && mAdFetcher != null){
                    start();
                }
            }
            mraid_is_closing = false;

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
            if (mAdFetcher != null && loadAdHasBeenCalled) {
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
     * Check whether the ad will expand to fit the screen width.  This
     * feature is disabled by default.
     *
     * @return If true, the ad will expand to fit screen width.
     */
    public boolean getExpandsToFitScreenWidth() {
        return expandsToFitScreenWidth;
    }

    /**
     * Set whether ads will expand to fit the screen width.  This
     * feature will cause ad creatives that are smaller than the view
     * size to 'stretch' to the current size.  This may cause image
     * quality degradation for the benefit of having an ad occupy the
     * entire ad view.  This feature is disabled by default.
     *
     * @param expandsToFitScreenWidth If true, automatic expansion is
     * enabled.
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
