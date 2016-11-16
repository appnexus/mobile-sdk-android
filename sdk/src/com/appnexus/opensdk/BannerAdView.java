/*
 *    Copyright 2013 - 2014 APPNEXUS INC
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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.appnexus.opensdk.transitionanimation.Animator;
import com.appnexus.opensdk.transitionanimation.TransitionDirection;
import com.appnexus.opensdk.transitionanimation.TransitionType;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.WebviewUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


/**
 * This view is added to an existing layout in order to display banner
 * ads.  It may be added via XML or code.
 * <p/>
 * <p>
 * Note that you need a placement ID in order to show ads.  If you
 * don't have a placement ID, you'll need to get one from your
 * AppNexus representative or your ad network.
 * </p>
 * Using XML, you might add it like this:
 * <p/>
 * <pre>
 * {@code
 *
 * <com.appnexus.opensdk.BannerAdView
 *           android:id="@+id/banner"
 *           android:layout_width="wrap_content"
 *           android:layout_height="wrap_content"
 *           android:placement_id="YOUR PLACEMENT ID"
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
 * <p/>
 * In code you can do the following:
 * <p/>
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
 */
public class BannerAdView extends AdView {

    private int period;
    private boolean loadAdHasBeenCalled;
    private boolean shouldReloadOnResume;
    private BroadcastReceiver receiver;
    protected boolean shouldResetContainer;
    private boolean expandsToFitScreenWidth;
    private boolean resizeToFitContainer;
    private boolean measured;
    private Animator animator;
    private boolean autoRefreshOffInXML;

    private void setDefaultsBeforeXML() {
        loadAdHasBeenCalled = false;
        period = Settings.DEFAULT_REFRESH;
        shouldReloadOnResume = false;
        autoRefreshOffInXML = false;
    }

    /**
     * Create a new BannerAdView in which to load and show ads.
     *
     * @param context The context of the {@link ViewGroup} to which
     *                the BannerAdView is being added.
     */
    public BannerAdView(Context context) {
        super(context);
    }

    /**
     * Create a new BannerAdView in which to load and show ads.
     *
     * @param context The context of the {@link ViewGroup} to which
     *                the BannerAdView is being added.
     * @param attrs   The {@link AttributeSet} to use when creating the
     *                BannerAdView.
     */
    public BannerAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Create a new BannerAdView in which to load and show ads.
     *
     * @param context  The context of the {@link ViewGroup} to which
     *                 the BannerAdView is being added.
     * @param attrs    The {@link AttributeSet} to use when creating the
     *                 BannerAdView.
     * @param defStyle The default style to apply to this view.  If 0,
     *                 no style will be applied (beyond what is
     *                 included in the theme).  This may be either an
     *                 attribute resource, whose value will be
     *                 retrieved from the current theme, or an
     *                 explicit style resource.
     */
    public BannerAdView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Creates a new BannerAdView in which to load and show ads.
     *
     * @param context          The context of the {@link ViewGroup} to which
     *                         the BannerAdView is being added.
     * @param refresh_interval The desired refresh rate, in
     *                         milliseconds.  The default value is 30
     *                         seconds; minimum is 15.  A value of 0
     *                         turns auto-refreshing off.
     */
    public BannerAdView(Context context, int refresh_interval) {
        super(context);
        this.setAutoRefreshInterval(refresh_interval);
    }

    @Override
    protected void setup(Context context, AttributeSet attrs) {
        period = Settings.DEFAULT_REFRESH;
        shouldResetContainer = false;
        expandsToFitScreenWidth = false;
        resizeToFitContainer = false;
        measured = false;
        animator = new Animator(getContext(), TransitionType.NONE, TransitionDirection.UP, 1000);

        super.setup(context, attrs);
        onFirstLayout();
        requestParameters.setMediaType(MediaType.BANNER);
        mAdFetcher.setPeriod(period);
        if (autoRefreshOffInXML) {
            mAdFetcher.start();
        }
    }

    private void setupBroadcast() {
        if (receiver != null) return;

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
                    boolean ad_started = false;
                    if (period > 0) {
                        start();
                        ad_started = true;
                    } else if (shouldReloadOnResume) {
                        stop();
                        start();
                        ad_started = true;
                    }
                    if (ad_started) {
                        Clog.d(Clog.baseLogTag,
                                Clog.getString(R.string.screen_on_start));
                    }
                }
            }
        };
        // for non-sticky filters, registerReceiver always returns null.
        getContext().registerReceiver(receiver, filter);
    }

    @Override
    public final void onLayout(boolean changed, int left, int top, int right,
                               int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (mraid_changing_size_or_visibility) {
            mraid_changing_size_or_visibility = false;
            return;
        }
        if (!measured || changed) {
            // Convert to dips
            float density = getContext().getResources().getDisplayMetrics().density;
            int measuredWidth = (int) ((right - left) / density + 0.5f);
            int measuredHeight = (int) ((bottom - top) / density + 0.5f);
            if (requestParameters.getAllowedSizes() == null
                    && (measuredWidth < requestParameters.getAdWidth() || measuredHeight < requestParameters.getAdHeight())
                    && measuredWidth > 0 && measuredHeight > 0) {
                Clog.e(Clog.baseLogTag, Clog.getString(R.string.adsize_too_big,
                        measuredWidth, measuredHeight, requestParameters.getAdWidth(), requestParameters.getAdHeight()));
                // Hide the space, since no ad will be loaded due to error
                hide();
                // Stop any request in progress
                if (mAdFetcher != null)
                    mAdFetcher.stop();
                // Returning here allows the SDK to re-request when the layout
                // next changes, and maybe the error will be amended.
                return;
            }
            requestParameters.setContainerWidth(measuredWidth);
            requestParameters.setContainerHeight(measuredHeight);
            // Hide the adview
            if (!measured && !loadedOffscreen) {
                hide();
            }

            loadedOffscreen = false;
            measured = true;
        }

        // Are we coming back from a screen/user presence change?
        if (loadAdHasBeenCalled) {
            setupBroadcast();
            if (shouldReloadOnResume) {
                start();
            }
        }

    }

    // Make sure receiver is registered.
    private void onFirstLayout() {
        if (period > 0) {
            setupBroadcast();
        }
    }

    @Override
    public MediaType getMediaType() {
        return MediaType.BANNER;
    }

    /**
     * Call this method to start loading an ad into this view
     * asynchronously.  This will request an ad from the server.  If
     * you wish to know whether the ad succeeded or failed to load,
     * use the {@link AdListener} object to receive the corresponding
     * events.
     *
     * @return <code>true</code> if the ad load was successfully
     * dispatched, false otherwise.
     */
    @Override
    public boolean loadAd() {
        loadAdHasBeenCalled = true;
        if (super.loadAd())
            return true;
        else {
            loadAdHasBeenCalled = false;
            return false;
        }
    }

    /**
     * Loads a new ad, if the ad space is visible, and sets the
     * placement ID, ad width, and ad height attributes of the AdView.
     *
     * @param placementID The placement ID to use in this view.
     * @param width       The width of the ad.
     * @param height      The height of the ad.
     * @return <code>true</code> if the ad will begin loading,
     * <code>false</code> otherwise.
     */
    public boolean loadAd(String placementID, int width, int height) {
        setAdSize(width, height);
        this.setPlacementID(placementID);
        return loadAd();
    }

    private Displayable currentDisplayable;

    @Override
    void interacted(){
        // do nothing
    }


    @Override
    protected void display(Displayable d) {
        // safety check: this should never evaluate to true
        if ((d == null) || d.failed() || (d.getView() == null)) {
            // The displayable has failed to be parsed or turned into a View.
            // We're already calling onAdLoaded, so don't call onAdFailed; just log
            Clog.e(Clog.baseLogTag, "Loaded an ad with an invalid displayable");
            return;
        }

        this.currentDisplayable = d;
        if (getTransitionType() == TransitionType.NONE) {
            // default to show ads without animation
            // call destroy on any old views
            this.removeAllViews();

            if (lastDisplayable != null) {
                lastDisplayable.destroy();
            }

            View displayableView = d.getView();

            this.addView(displayableView);

            // set the displayable view's gravity inside AdView
            if ((displayableView.getLayoutParams()) != null) {
                ((LayoutParams) displayableView.getLayoutParams()).gravity = getAdAlignment().getGravity();
            }

        } else {
            // first time showing animator
            // which means there's no previous ad or previous ad does not show animation
            if (this.getChildCount() == 0 || this.indexOfChild(animator) > -1) {
                this.removeAllViews();
                this.addView(animator);
            }

            // add the new ad to animator to be displayed with animation
            animator.addView(d.getView());

            if (d.getView().getLayoutParams() != null) {
                ((LayoutParams) d.getView().getLayoutParams()).gravity = getAdAlignment().getGravity();
                animator.setLayoutParams(d.getView().getLayoutParams());
            }

            // show animation
            animator.showNext();

            final Displayable toBeDestroyed = lastDisplayable;

            if (toBeDestroyed != null) {
                if (toBeDestroyed.getView().getAnimation() != null) {
                    toBeDestroyed.getView().getAnimation().setAnimationListener(
                            new AnimatorListener(toBeDestroyed, animator)
                    );
                } else {
                    toBeDestroyed.destroy();
                }
            }

        }
        unhide();

        lastDisplayable = d;

    }

    //Mediated banners behave the same as non-mediated.
    @Override
    protected void displayMediated(MediatedDisplayable d) {
        display(d);
    }

    class AnimatorListener implements Animation.AnimationListener {
        private final WeakReference<Displayable> oldView;
        private final WeakReference<Animator> animator;

        AnimatorListener(Displayable view, Animator animator) {
            this.oldView = new WeakReference<Displayable>(view);
            this.animator = new WeakReference<Animator>(animator);
        }

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            animation.setAnimationListener(null);
            final Displayable oldView = this.oldView.get();
            final Animator animator = this.animator.get();

            if (oldView != null && animator != null) {
                // Make sure to post actions on UI thread
                oldView.getView().getHandler().post(new Runnable() {
                    public void run() {
                        animator.clearAnimation();
                        oldView.destroy();
                        animator.setAnimation();
                    }
                });
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    private AdAlignment adAlignment;

    public enum AdAlignment {
        TOP_LEFT,
        TOP_CENTER,
        TOP_RIGHT,
        CENTER_LEFT,
        CENTER,
        CENTER_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_CENTER,
        BOTTOM_RIGHT;

        int getGravity() {
            switch (this) {
                case TOP_LEFT:
                    return Gravity.TOP | Gravity.LEFT;
                case TOP_CENTER:
                    return Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                case TOP_RIGHT:
                    return Gravity.TOP | Gravity.RIGHT;
                case CENTER_LEFT:
                    return Gravity.LEFT | Gravity.CENTER_VERTICAL;
                case CENTER:
                    return Gravity.CENTER;
                case CENTER_RIGHT:
                    return Gravity.RIGHT | Gravity.CENTER_VERTICAL;
                case BOTTOM_LEFT:
                    return Gravity.BOTTOM | Gravity.LEFT;
                case BOTTOM_CENTER:
                    return Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                case BOTTOM_RIGHT:
                    return Gravity.BOTTOM | Gravity.RIGHT;
            }
            return Gravity.CENTER;
        }
    }

    /**
     * Sets the alignment of ads inside the BannerAdView,
     * which can be set to 9 different positions.
     * It will be applied to next ad after setting the alignment.
     *
     * @param layout The alignment
     */
    public void setAdAlignment(AdAlignment layout) {
        this.adAlignment = layout;
    }

    /**
     * Returns the alignment of ads inside the BannerAdView.
     * Default is center in the BannerAdView.
     *
     * @return The alignment
     */
    public AdAlignment getAdAlignment() {
        if (this.adAlignment == null) {
            this.adAlignment = AdAlignment.CENTER;
        }
        return this.adAlignment;
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

        int width = -1;
        int height = -1;

        final int N = a.getIndexCount();
        Clog.v(Clog.xmlLogTag, Clog.getString(R.string.found_n_in_xml, N));
        for (int i = 0; i < N; ++i) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.BannerAdView_placement_id) {
                setPlacementID(a.getString(attr));
                Clog.d(Clog.xmlLogTag, Clog.getString(R.string.placement_id,
                        a.getString(attr)));
            } else if (attr == R.styleable.BannerAdView_auto_refresh_interval) {
                int period = a.getInt(attr, Settings.DEFAULT_REFRESH);
                setAutoRefreshInterval(period);
                if (period <= 0) {
                    autoRefreshOffInXML = true;
                }
                Clog.d(Clog.xmlLogTag,
                        Clog.getString(R.string.xml_set_period, period));
            } else if (attr == R.styleable.BannerAdView_test) {
                Settings.getSettings().test_mode = a.getBoolean(attr, false);
                Clog.d(Clog.xmlLogTag,
                        Clog.getString(R.string.xml_set_test,
                                Settings.getSettings().test_mode));
            } else if (attr == R.styleable.BannerAdView_adWidth) {
                width = a.getInt(attr, -1);
                Clog.d(Clog.xmlLogTag,
                        Clog.getString(R.string.xml_ad_width,
                                a.getInt(attr, -1)));
            } else if (attr == R.styleable.BannerAdView_adHeight) {
                height = a.getInt(attr, -1);
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
                        getOpensNativeBrowser()));
            } else if (attr == R.styleable.BannerAdView_expands_to_fit_screen_width) {
                setExpandsToFitScreenWidth(a.getBoolean(attr, false));
                Clog.d(Clog.xmlLogTag, Clog.getString(
                        R.string.xml_set_expands_to_full_screen_width,
                        expandsToFitScreenWidth
                ));
            } else if (attr == R.styleable.BannerAdView_resize_ad_to_fit_container) {
                setResizeAdToFitContainer(a.getBoolean(attr, false));
                Clog.d(Clog.xmlLogTag, Clog.getString(
                        R.string.xml_resize_ad_to_fit_container,
                        resizeToFitContainer
                ));
            } else if (attr == R.styleable.BannerAdView_show_loading_indicator) {
                Clog.d(Clog.xmlLogTag,
                        Clog.getString(R.string.show_loading_indicator_xml));
                setShowLoadingIndicator(a.getBoolean(attr, true));
            } else if (attr == R.styleable.BannerAdView_transition_type) {
                Clog.d(Clog.xmlLogTag,
                        Clog.getString(R.string.transition_type));
                int transitionTypeFromXML = a.getInt(attr, 0);
                setTransitionType(TransitionType.getTypeForInt(transitionTypeFromXML));
            } else if (attr == R.styleable.BannerAdView_transition_direction) {
                Clog.d(Clog.xmlLogTag,
                        Clog.getString(R.string.transition_direction));
                setTransitionDirection(TransitionDirection.getDirectionForInt(a.getInt(attr, 0)));

            } else if (attr == R.styleable.BannerAdView_transition_duration) {
                Clog.d(Clog.xmlLogTag,
                        Clog.getString(R.string.transition_duration));
                setTransitionDuration((long) a.getInt(attr, 1000));
            } else if (attr == R.styleable.BannerAdView_load_landing_page_in_background) {
                setLoadsInBackground(a.getBoolean(attr, true));
                Clog.d(Clog.xmlLogTag, Clog.getString(R.string.xml_load_landing_page_in_background, doesLoadingInBackground));
            }
        }

        if ((width != -1) && (height != -1)) {
            setAdSize(width, height);
        }

        a.recycle();
    }

    /**
     * Retrieve the currently set auto-refresh interval.
     *
     * @return The interval, in milliseconds, at which the
     * BannerAdView will request new ads, if auto-refresh is
     * enabled.
     */
    public int getAutoRefreshInterval() {
        Clog.d(Clog.publicFunctionsLogTag,
                Clog.getString(R.string.get_period, period));
        return period;
    }

    /**
     * Set the size of the ad to request.
     *
     * @param w The width of the ad, in pixels.
     * @param h The height of the ad, in pixels.
     */
    public void setAdSize(int w, int h) {
        Clog.d(Clog.baseLogTag, Clog.getString(R.string.set_size, w, h));
        requestParameters.setAdWidth(w);
        requestParameters.setAdHeight(h);
    }

    /**
     * Set the {@link AdSize}s which are allowed to be displayed.
     * This is a list of the platform ad sizes that may be inserted
     * into a banner ad view.
     *
     *
     * @param adSizes The {@link ArrayList} of {@link AdSize}s
     *                which are allowed to be displayed.
     */
    public void setAdSizes(ArrayList<AdSize> adSizes) {
        Clog.d(Clog.baseLogTag, Clog.getString(R.string.set_ad_sizes));

        if (adSizes == null) {
            Clog.e(Clog.baseLogTag, Clog.getString(R.string.set_ad_sizes_null));
            return;
        }

        if (adSizes.size() == 0) {
            Clog.e(Clog.baseLogTag, Clog.getString(R.string.set_ad_sizes_no_elements));
            return;
        }

        requestParameters.setAdWidth(-1);
        requestParameters.setAdHeight(-1);
        requestParameters.setOverrideMaxSize(false);
        requestParameters.setMaxSize(-1, -1);
        requestParameters.setAllowedSizes(null);

        AdSize primarySize = adSizes.get(0);
        this.setAdSize(primarySize.width(), primarySize.height());

        if (adSizes.size() > 1) {
            ArrayList<AdSize> promoSizes = new ArrayList<>(adSizes.subList(1, adSizes.size()));
            requestParameters.setAllowedSizes(promoSizes);
        }
    }

    /**
     * Set the maximum size of the desired ad. Used in place of AdSize if
     * setOverrideMaxSize() has been set to true.
     *
     * @param maxW The maximum width in pixels.
     * @param maxH The maximum height in pixels.
     */
    public void setMaxSize(int maxW, int maxH) {
        Clog.d(Clog.baseLogTag, Clog.getString(R.string.set_max_size, maxW, maxH));
        requestParameters.setMaxSize(maxW, maxH);
    }

    /**
     * Sets whether to use the size value from {@link #setMaxSize} instead of
     * values from {@link #setAdSize}. Call with value true in order override
     * the value from {@link #setAdSize}.
     *
     * @param shouldOverrideMaxSize Whether the ad will request an ad
     *                              for a maximum size. Default is false.
     */
    public void setOverrideMaxSize(boolean shouldOverrideMaxSize) {
        Clog.d(Clog.baseLogTag, Clog.getString(R.string.set_override_max_size, shouldOverrideMaxSize));
        requestParameters.setOverrideMaxSize(shouldOverrideMaxSize);
    }

    /**
     * Check the maximum height of the ad to be requested for this view.
     * Only used if {@link #setOverrideMaxSize} has been set to true.
     *
     * @return The maximum height of the ad to be requested.
     */
    public int getMaxHeight() {
        Clog.d(Clog.baseLogTag, Clog.getString(R.string.get_max_height, requestParameters.getMaxHeight()));
        return requestParameters.getMaxHeight();
    }

    /**
     * Check the maximum width of the ad to be requested for this view.
     * Only used if {@link #setOverrideMaxSize} has been set to true.
     *
     * @return The maximum width of the ad to be requested.
     */
    public int getMaxWidth() {
        Clog.d(Clog.baseLogTag, Clog.getString(R.string.get_max_width, requestParameters.getMaxWidth()));
        return requestParameters.getMaxWidth();
    }

    /**
     * Check whether the ad request will pass the value from
     * {@link #setAdSize} or from {@link #setMaxSize}.
     *
     * @return If the maximum size will be passed instead of the ad size.
     */
    public boolean getOverrideMaxSize() {
        Clog.d(Clog.baseLogTag, Clog.getString(R.string.get_override_max_size, requestParameters.getOverrideMaxSize()));
        return requestParameters.getOverrideMaxSize();
    }

    /**
     * Check the height of the ad to be requested for this view.
     *
     * @return The height of the ad to request.
     */
    public int getAdHeight() {
        Clog.d(Clog.baseLogTag, Clog.getString(R.string.get_height, requestParameters.getAdHeight()));
        return requestParameters.getAdHeight();
    }

    /**
     * Check the width of the ad to be requested for this view.
     *
     * @return The width of the ad to request.
     */
    public int getAdWidth() {
        Clog.d(Clog.baseLogTag, Clog.getString(R.string.get_width, requestParameters.getAdWidth()));
        return requestParameters.getAdWidth();
    }

    /**
     * Check the ad sizes which will be requested for this view.
     *
     * @return The sizes allowed to be displayed in this view.
     */
    public ArrayList<AdSize> getAdSizes() {
        Clog.d(Clog.baseLogTag, Clog.getString(R.string.get_ad_sizes));
        ArrayList<AdSize> adSizes = new ArrayList<>();
        adSizes.add(new AdSize(getAdWidth(), getAdHeight()));
        if (requestParameters.getAllowedSizes() != null) {
            adSizes.addAll(requestParameters.getAllowedSizes());
        }
        return adSizes;
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
        if (period > 0) {
            this.period = Math.max(Settings.MIN_REFRESH_MILLISECONDS,
                    period);
        } else {
            this.period = period;
        }
        Clog.d(Clog.publicFunctionsLogTag,
                Clog.getString(R.string.set_period, this.period));
        if (mAdFetcher != null)
            mAdFetcher.setPeriod(this.period);
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
    public void setShouldReloadOnResume(boolean shouldReloadOnResume) {
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
            setupBroadcast();
            Clog.d(Clog.baseLogTag, Clog.getString(R.string.unhidden));
            //The only time we want to request on visibility changes is if an ad hasn't been loaded yet (loadAdHasBeenCalled)
            // shouldReloadOnResume is true
            // OR auto_refresh is enabled
            if (loadAdHasBeenCalled || shouldReloadOnResume || (period > 0)) {

                //If we're MRAID mraid_is_closing or expanding, don't load.
                if (!mraid_is_closing && !mraid_changing_size_or_visibility
                        && !isMRAIDExpanded() && (mAdFetcher != null)
                        && !loadedOffscreen) {
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
            dismantleBroadcast();
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
        if (receiver == null) return;
        // Catch exception to protect against receiver failing to be registered.
        try {
            getContext().unregisterReceiver(receiver);
        } catch (IllegalArgumentException ignored) {
        }
        receiver = null;
    }

    @Override
    protected void unhide() {
        super.unhide();
    }


    @Override
    public void destroy() {
        super.destroy();
    }


    @Override
    boolean isBanner() {
        return true;
    }

    @Override
    boolean isInterstitial() {
        return false;
    }

    @Override
    public void activityOnDestroy() {
        if (this.currentDisplayable != null) {
            this.currentDisplayable.onDestroy();
            this.currentDisplayable = null;
        }

        dismantleBroadcast();
        if (mAdFetcher != null) {
            stop();
        }
    }

    @Override
    public void activityOnPause() {
        if (this.currentDisplayable != null) {
            this.currentDisplayable.onPause();
        }
    }

    @Override
    public void activityOnResume() {
        if (this.currentDisplayable != null) {
            this.currentDisplayable.onResume();
        }
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
     *                                enabled.
     */
    public void setExpandsToFitScreenWidth(boolean expandsToFitScreenWidth) {
        this.expandsToFitScreenWidth = expandsToFitScreenWidth;
    }

    /**
     * Set whether ads will expand to fit the BannerAdView.  This
     * feature will cause ad creatives that are smaller than the BannerAdView
     * size to 'stretch' to the BannerAdView size.  This may cause image
     * quality degradation for the benefit of having an ad occupy the
     * entire BannerAdView.  This feature is disabled by default.
     *
     * @param resizeAdToFitContainer If true, automatic expansion is
     *                               enabled.
     */
    public void setResizeAdToFitContainer(boolean resizeAdToFitContainer) {
        this.resizeToFitContainer = resizeAdToFitContainer;
    }


    /**
     * Check whether the ad will expand to fit the BannerAdView.  This
     * feature is disabled by default.
     *
     * @return If true, the ad will expand to fit the BannerAdView.
     */
    public boolean getResizeAdToFitContainer() {
        return resizeToFitContainer;
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    protected void resizeWebViewToFitContainer(int adWidth, int adHeight, AdWebView webview) {
        int containerWidth;
        int containerHeight;
        if (getWidth() <= 0) {
            containerWidth = getMeasuredWidth();
        } else {
            containerWidth = getWidth();
        }
        if (getHeight() <= 0) {
            containerHeight = getMeasuredHeight();
        } else {
            containerHeight = getHeight();
        }

        if (containerHeight <= 0 || containerWidth <= 0) {
            Clog.w(Clog.baseLogTag, "Unable to resize ad to fit container because of failure to obtain the container size.");
            return;
        }

        int webViewWidth;
        int webViewHeight;

        float widthRatio = ((float) adWidth) / ((float)containerWidth);
        float heightRatio = ((float) adHeight) / ((float)containerHeight);

        if (widthRatio < heightRatio) {
            //expand to full container height
            webViewHeight = containerHeight;
            webViewWidth = (adWidth * containerHeight / adHeight);
            webview.setInitialScale((int) Math.ceil(100 * containerHeight / adHeight));

        } else {
            //expand to full container width
            webViewWidth = containerWidth;
            webViewHeight = (adHeight * containerWidth / adWidth);
            webview.setInitialScale((int) Math.ceil(100 * containerWidth / adWidth));
        }

        // Adjust width or height of webview to fit container
        if (webview.getLayoutParams() == null) {
            LayoutParams layoutParams = new LayoutParams(webViewWidth, webViewHeight);
            layoutParams.gravity = Gravity.CENTER;
            webview.setLayoutParams(layoutParams);

        } else {
            webview.getLayoutParams().width = webViewWidth;
            webview.getLayoutParams().height = webViewHeight;
            ((LayoutParams) webview.getLayoutParams()).gravity = Gravity.CENTER;
        }
        webview.invalidate();
    }

    protected int oldH;
    protected int oldW;

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    protected void expandToFitScreenWidth(int adWidth, int adHeight, AdWebView webview) {
        //Determine the width of the screen
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        int width = -1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point p = new Point();
            display.getSize(p);
            width = p.x;
        } else {
            width = display.getWidth();
        }

        float ratio_delta = ((float) width) / ((float) adWidth);
        int new_height = (int) Math.floor(adHeight * ratio_delta);
        oldH = getLayoutParams().height;
        oldW = getLayoutParams().width;

        //Adjust width of container
        if (getLayoutParams().width > 0 || getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            getLayoutParams().width = width;
        }

        //Adjust height of container
        getLayoutParams().height = new_height;


        //Adjust height of webview
        if (webview.getLayoutParams() == null) {
            webview.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        } else {
            webview.getLayoutParams().width = FrameLayout.LayoutParams.MATCH_PARENT;
            webview.getLayoutParams().height = FrameLayout.LayoutParams.MATCH_PARENT;
        }
        webview.setInitialScale((int) Math.ceil(ratio_delta * 100));
        webview.invalidate();

        shouldResetContainer = true;

    }

    protected void resetContainer() {
        shouldResetContainer = false;
        if (getLayoutParams() != null) {
            getLayoutParams().height = oldH;
            getLayoutParams().width = oldW;
        }
    }

    void resetContainerIfNeeded() {
        if (this.shouldResetContainer) {
            resetContainer();
        }
    }

    /**
     * Set the transition animation's type
     *
     * @param transitionType transition animation's type
     */

    public void setTransitionType(TransitionType transitionType) {
        animator.setTransitionType(transitionType);
    }

    /**
     * Get the type of the transition animation
     *
     * @return TransitionType
     */

    public TransitionType getTransitionType() {
        return animator.getTransitionType();
    }

    /**
     * Set the transition animation's direction
     *
     * @param direction transition animation's direction
     */
    public void setTransitionDirection(TransitionDirection direction) {
        animator.setTransitionDirection(direction);
    }

    /**
     * Get the direction of the transition animation
     *
     * @return TransionDirection
     */

    public TransitionDirection getTransitionDirection() {
        return animator.getTransitionDirection();
    }

    /**
     * Set the transition animation's duration
     *
     * @param duration in milliseconds
     */
    public void setTransitionDuration(long duration) {
        animator.setTransitionDuration(duration);
    }

    /**
     * Get the duration for the transition animation
     *
     * @return duration in milliseconds
     */
    public long getTransitionDuration() {
        return animator.getTransitionDuration();
    }
}
