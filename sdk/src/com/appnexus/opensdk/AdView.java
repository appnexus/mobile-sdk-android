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
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.MutableContextWrapper;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.appnexus.opensdk.VisibilityDetector.VisibilityListener;
import com.appnexus.opensdk.tasksmanager.TasksManager;
import com.appnexus.opensdk.ut.UTAdRequester;
import com.appnexus.opensdk.ut.UTConstants;
import com.appnexus.opensdk.ut.UTRequestParameters;
import com.appnexus.opensdk.ut.adresponse.BaseAdResponse;
import com.appnexus.opensdk.ut.adresponse.RTBHTMLAdResponse;
import com.appnexus.opensdk.ut.adresponse.RTBVASTAdResponse;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.HTTPGet;
import com.appnexus.opensdk.utils.HTTPResponse;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.Settings.CountImpression;
import com.appnexus.opensdk.utils.ViewUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * The parent class of {@link InterstitialAdView} and {@link
 * BannerAdView}.  This may not be instantiated directly.  Its public
 * methods are accessed through one of its sub classes.
 */
public abstract class AdView extends FrameLayout implements Ad, MultiAd, VisibilityListener {

    AdFetcher mAdFetcher;
    private AdResponse ad = null;
    boolean mraid_changing_size_or_visibility = false;
    int creativeWidth;
    int creativeHeight;
    private AdType adType;
    String creativeId = "";
    private AdListener adListener;
    private AppEventListener appEventListener;

    final Handler handler = new Handler(Looper.getMainLooper());
    protected Displayable lastDisplayable;
    private AdViewDispatcher dispatcher;
    boolean loadedOffscreen = false;
    boolean isMRAIDExpanded = false;
    boolean countBannerImpressionOnAdLoad = false;

    private boolean shouldResizeParent = false;
    private boolean showLoadingIndicator = true;
    private boolean isAttachedToWindow = false;
    // This is to keep track if the lazy Load has been enabled for the current Ad Request (applies only to BannerAdView)
    private boolean enableLazyLoad = false;
    // This is to keep track if the loadLazyAd has been called or not
    private boolean activateWebview = false;

    UTRequestParameters requestParameters;

    protected ArrayList<String> impressionTrackers;
    private ANAdResponseInfo adResponseInfo;
    /**
     * This variable keeps track of the Complete AdRequest
     * starting from loadAd() and ending at onAdLoaded(), onAdFailed() or onLazyAdLoaded()
     * initial value is false
     * the value is set to true while initialising and AdRequest (eg banner.loadAd() / mar.load())
     * the value is set to false while sending back these callbacks: onAdLoaded(), onAdFailed() or onLazyAdLoaded()
     */
    private boolean isFetching = false;

    private ArrayList<WeakReference<View>> friendlyObstructionList = new ArrayList<>();

    /**
     * Begin Construction
     */
    AdView(Context context) {
        this(context, null);
    }

    AdView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    AdView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup(context, attrs);
    }

    void setup(Context context, AttributeSet attrs) {
        dispatcher = new AdViewDispatcher(handler);
        requestParameters = new UTRequestParameters(context);
        adType = AdType.UNKNOWN;

        SDKSettings.init(context, null);

        Clog.d(Clog.publicFunctionsLogTag, Clog.getString(R.string.new_adview));


        // Store the AppID in the settings
        Settings.getSettings().app_id = context.getApplicationContext()
                .getPackageName();
        Clog.v(Clog.baseLogTag,
                Clog.getString(R.string.appid, Settings.getSettings().app_id));

        Clog.v(Clog.baseLogTag, Clog.getString(R.string.making_adman));

        // Some AdMob creatives won't load unless we set their parent's viewgroup's padding to 0-0-0-0
        setPadding(0, 0, 0, 0);
        // Make an AdFetcher - Continue the creation pass
        mAdFetcher = new AdFetcher(this);

        // Load user variables only if attrs isn't null
        if (attrs != null)
            loadVariablesFromXML(context, attrs);

        // We don't start the ad requesting here, since the view hasn't been
        // sized yet.
    }

    @Override
    public void init() {
        if (this.getWindowVisibility() != VISIBLE) {
            loadedOffscreen = true;
        }
        isFetching = true;
        activateWebview = false;
        adResponseInfo = null;
    }

    /**
     * The view layout
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    boolean isMRAIDExpanded() {
        return isMRAIDExpanded;
    }

    @Override
    public boolean isReadyToStart() {
        if (!(getContext() instanceof Activity)) {
            Clog.e(Clog.baseLogTag, Clog.getString(R.string.passed_context_error));
            return false;
        }
        if (isMRAIDExpanded()) {
            Clog.e(Clog.baseLogTag, Clog.getString(R.string.already_expanded));
            return false;
        }
        return requestParameters.isReadyForRequest();
    }

    @Override
    public UTRequestParameters getRequestParameters() {
        return requestParameters;
    }

    /**
     * Passes the external inventory code to the Ad Request
     * @param extInvCode passed as String, specifies predefined value passed on the query string that can be used in reporting.
     * */
    public void setExtInvCode(String extInvCode) {
        requestParameters.setExtInvCode(extInvCode);
    }

    /**
     * Returns the external inventory code, initially added using {@link #setExtInvCode(String)}
     * @@return extInvCode as String, specifies predefined value passed on the query string that can be used in reporting.
     * */
    public String getExtInvCode() {
        return requestParameters.getExtInvCode();
    }

    /**
     * Passes the traffic source code to the Ad Request
     * @param trafficSourceCode passed as String, specifies the third-party source of this impression.
     * */
    public void setTrafficSourceCode(String trafficSourceCode) {
        requestParameters.setTrafficSourceCode(trafficSourceCode);
    }

    /**
     * Returns the traffic source code, initially added using {@link #setTrafficSourceCode(String)}
     * @return trafficSourceCode as String, specifies the third-party source of this impression.
     * */
    public String getTrafficSourceCode() {
        return requestParameters.getTrafficSourceCode();
    }

    /**
     * Loads a new ad, if the ad space is visible.  You should
     * have called setPlacementID before invoking this method.
     *
     * @return true means the ad will begin loading; false otherwise.
     */
    @Override
    public boolean loadAd() {
        if (!isReadyToStart())
            return false;
        if (mAdFetcher != null) {
            // Reload Ad Fetcher to get new ad at user's request
            mAdFetcher.stop();
            mAdFetcher.clearDurations();
            mAdFetcher.start();
            init();
            return true;
        }
        return false;
    }

    /**
     * @deprecated use {@link #loadAd()} instead.
     */
    @Deprecated
    public void loadAdOffscreen() {
        loadAd();
    }

    /**
     * Loads a new ad, if the ad space is visible, and sets the
     * AdView's placement ID.
     *
     * @param placementID The new placement ID to use.
     * @return true means the ad will begin loading; false otherwise.
     */
    public boolean loadAd(String placementID) {
        requestParameters.setPlacementID(placementID);
        return loadAd();
    }

    protected void loadAdFromHtml(String html, int width, int height) {
        // load an ad directly from html
        loadedOffscreen = true;
        AdWebView output = new AdWebView(this, null);
        RTBHTMLAdResponse response = new RTBHTMLAdResponse(width, height, getMediaType().toString(), null, getAdResponseInfo());
        response.setAdContent(html);
        output.loadAd(response);
        display(output);
    }


    protected void loadAdFromVAST(String VASTXML, int width, int height) {
        // load an ad directly from VASTXML
        loadedOffscreen = true;
        AdWebView output = new AdWebView(this, null);
        RTBVASTAdResponse response = new RTBVASTAdResponse(width, height, AdType.VIDEO.toString(), null, null, getAdResponseInfo());
        response.setAdContent(VASTXML);
        response.setContentSource(UTConstants.RTB);
        response.addToExtras(UTConstants.EXTRAS_KEY_MRAID, true);
        output.loadAd(response);
        display(output);
    }

    protected abstract void loadVariablesFromXML(Context context,
                                                 AttributeSet attrs);

    /*
     * End Construction
     */

    protected abstract void display(Displayable d);

    protected abstract void displayMediated(MediatedDisplayable d);

    void unhide() {
        if (getVisibility() != VISIBLE) {
            setVisibility(VISIBLE);
        }
    }

    void hide() {
        if (getVisibility() != GONE)
            setVisibility(GONE);
    }

    /**
     * Retrieve the current placement ID.
     *
     * @return The current placement id.
     */
    public String getPlacementID() {
        Clog.d(Clog.publicFunctionsLogTag,
                Clog.getString(R.string.get_placement_id, requestParameters.getPlacementID()));
        return requestParameters.getPlacementID();
    }

    /**
     * Sets the placement id of the AdView. The placement ID
     * identifies the location in your application where ads will
     * be shown.  You must have a valid, active placement ID to
     * monetize your application.
     *
     * @param placementID The placement ID to use.
     */
    public void setPlacementID(String placementID) {
        Clog.d(Clog.publicFunctionsLogTag,
                Clog.getString(R.string.set_placement_id, placementID));
        requestParameters.setPlacementID(placementID);
    }

    /**
     * Sets the inventory code and member id of the AdView. The inventory code
     * provides a more human readable way to identify the location in your
     * application where ads will be shown. Member id is required to for using
     * this feature. If both inventory code and placement id are presented,
     * inventory code will be used instead of placement id on the ad request.
     *
     * @param memberID      The member id that this AdView belongs to.
     * @param inventoryCode The inventory code of this AdView.
     */
    public void setInventoryCodeAndMemberID(int memberID, String inventoryCode) {
        requestParameters.setInventoryCodeAndMemberID(memberID, inventoryCode);
    }

    @Deprecated
    /**
     * Retrieve the member ID.
     *
     * @return the member id that this AdView belongs to.
     * @deprecated use {{@link ANAdResponseInfo}.getBuyMemberId}
     */
    public int getMemberID() {
        return requestParameters.getMemberID();
    }

    /**
     * Retrieve the Publisher ID.
     *
     * @return the Publisher id that this AdView belongs to.
     */
    public int getPublisherId() {
        return requestParameters.getPublisherId();
    }

    /**
     * Retrieve the Publisher ID.
     *
     * @@param publisherId the Publisher id that this AdView belongs to.
     */
    public void setPublisherId(int publisherId) {
        requestParameters.setPublisherId(publisherId);
    }

    /**
     * Retrieve the inventory code.
     *
     * @return the current inventory code.
     */
    public String getInventoryCode() {
        return requestParameters.getInvCode();
    }

    /**
     * This must be called from the UI thread,
     * when permanently remove the AdView from the view hierarchy.
     */
    public void destroy() {
        if (VisibilityDetector.getInstance() != null) {
            VisibilityDetector.getInstance().destroy(AdView.this);
        }

        Clog.d(Clog.baseLogTag, "called destroy() on AdView");
        if (this.lastDisplayable != null) {
            this.lastDisplayable.destroy();
            this.lastDisplayable = null;
        }

        // Just in case, kill the adfetcher's service
        if (mAdFetcher != null) {
            mAdFetcher.destroy();
        }
    }


    protected void setShouldResizeParent(boolean shouldResizeParent) {
        this.shouldResizeParent = shouldResizeParent;
    }

    /**
     * MRAID functions and variables
     */
    boolean mraid_is_closing = false;
    CircularProgressBar close_button;
    @SuppressLint("StaticFieldLeak")
    static FrameLayout mraidFullscreenContainer;
    @SuppressLint("StaticFieldLeak")
    static MRAIDImplementation mraidFullscreenImplementation;
    static AdWebView.MRAIDFullscreenListener mraidFullscreenListener;

    protected void close(int w, int h, MRAIDImplementation caller) {
        // Remove MRAID close button
        ViewUtil.removeChildFromParent(close_button);
        close_button = null;

        if (caller.owner.isFullScreen) {
            ViewUtil.removeChildFromParent(caller.owner);
            if (caller.getDefaultContainer() != null) {
                caller.getDefaultContainer().addView(caller.owner, 0);
            }

            if (caller.getFullscreenActivity() != null) {
                caller.getFullscreenActivity().finish();
            }

            // Reset the context of MutableContext wrapper for banner expand and close case.
            if (getMediaType().equals(MediaType.BANNER) && (caller.owner.getContext() instanceof MutableContextWrapper)) {
                ((MutableContextWrapper) caller.owner.getContext()).setBaseContext(getContext());
            }
        }
        // null these out for safety
        mraidFullscreenContainer = null;
        mraidFullscreenImplementation = null;
        mraidFullscreenListener = null;

        MRAIDChangeSize(w, h);
        mraid_is_closing = true;
        isMRAIDExpanded = false;
    }

    private void MRAIDChangeSize(int w, int h) {
        mraid_changing_size_or_visibility = true;

        if (getLayoutParams() != null) {
            if (getLayoutParams().width > 0)
                getLayoutParams().width = w;
            if (getLayoutParams().height > 0)
                getLayoutParams().height = h;
        }

        if (shouldResizeParent && (getParent() instanceof View)) {
            View parent = (View) getParent();
            if (parent.getLayoutParams() != null) {
                if (parent.getLayoutParams().width > 0)
                    parent.getLayoutParams().width = w;
                if (parent.getLayoutParams().height > 0)
                    parent.getLayoutParams().height = h;
            }
        }
    }

    protected void mraidFullscreenExpand(final MRAIDImplementation caller, boolean use_custom_close, AdWebView.MRAIDFullscreenListener listener) {
        caller.setDefaultContainer((ViewGroup) caller.owner.getParent());

        //Make a new framelayout to contain webview and button
        FrameLayout fslayout = new FrameLayout(this.getContext());

        // remove the webview from its parent and add it to the fullscreen container
        ViewUtil.removeChildFromParent(caller.owner);
        fslayout.addView(caller.owner);

        if (close_button == null) {
            close_button = ViewUtil.createCircularProgressBar(this.getContext());
            ViewUtil.showCloseButton(close_button, use_custom_close);
            close_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    caller.close();
                }
            });
        }
        fslayout.addView(close_button);

        mraidFullscreenContainer = fslayout;
        mraidFullscreenImplementation = caller;
        mraidFullscreenListener = listener;

        Class<?> activity_clz = AdActivity.getActivityClass();
        try {
            Intent i = new Intent(getContext(), activity_clz);
            i.putExtra(AdActivity.INTENT_KEY_ACTIVITY_TYPE,
                    AdActivity.ACTIVITY_TYPE_MRAID);
            getContext().startActivity(i);
        } catch (ActivityNotFoundException e) {
            Clog.e(Clog.baseLogTag, Clog.getString(R.string.adactivity_missing, activity_clz.getName()));
            mraidFullscreenContainer = null;
            mraidFullscreenImplementation = null;
            mraidFullscreenListener = null;
        }
    }

    void expand(int w, int h, boolean custom_close,
                final MRAIDImplementation caller,
                AdWebView.MRAIDFullscreenListener listener) {
        MRAIDChangeSize(w, h);

        // Add a stock close_button button to the top right corner
        close_button = ViewUtil.createCircularProgressBar(this.getContext());
        ViewUtil.showCloseButton(close_button, custom_close);
        FrameLayout.LayoutParams blp = (LayoutParams) close_button.getLayoutParams();

        // place the close button at the top right of the adview if it isn't fullscreen
        if (!caller.owner.isFullScreen) {
            if (getChildAt(0) != null) {
                blp.rightMargin = (this.getMeasuredWidth()
                        - this.getChildAt(0).getMeasuredWidth()) / 2;
            }
        }

        close_button.setLayoutParams(blp);
        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caller.close();
            }
        });

        if (caller.owner.isFullScreen) {
            mraidFullscreenExpand(caller, custom_close, listener);
        } else {
            // if not fullscreen, just add the close button
            this.addView(close_button);
        }

        isMRAIDExpanded = true;
    }


    int buttonPxSideLength = 0;

    void resize(int w, int h, int offset_x, int offset_y, MRAIDImplementation.CUSTOM_CLOSE_POSITION custom_close_position, boolean allow_offscreen,
                final MRAIDImplementation caller) {
        MRAIDChangeSize(w, h);

        // Add a stock close_button button to the top right corner
        ViewUtil.removeChildFromParent(close_button);

        if (!(buttonPxSideLength > 0)) {
            final float scale = caller.owner.getContext().getResources().getDisplayMetrics().density;
            buttonPxSideLength = (int) (50 * scale);
        }

        close_button = new CircularProgressBar(this.getContext(), null, android.R.attr.indeterminateOnly) {

            @SuppressWarnings("deprecation")
            @SuppressLint({"NewApi", "DrawAllocation"})
            @Override
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                int close_button_loc[] = new int[2];
                this.getLocationOnScreen(close_button_loc);

                //Determine container width and height
                Point container_size;
                Point screen_size = new Point(0, 0);
                Activity a = null;
                boolean useScreenSizeForAddedAccuracy = true;
                try {
                    a = (Activity) caller.owner.getContext();
                } catch (ClassCastException e) {
                    useScreenSizeForAddedAccuracy = false;
                }

                if (useScreenSizeForAddedAccuracy) {
                    if (Build.VERSION.SDK_INT >= 13) {
                        a.getWindowManager().getDefaultDisplay().getSize(screen_size);
                    } else {
                        screen_size.x = a.getWindowManager().getDefaultDisplay().getWidth();
                        screen_size.y = a.getWindowManager().getDefaultDisplay().getHeight();
                    }
                }

                int adviewLoc[] = new int[2];
                if (getMediaType().equals(MediaType.INTERSTITIAL)) {
                    InterstitialAdView.INTERSTITIALADVIEW_TO_USE.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
                    InterstitialAdView.INTERSTITIALADVIEW_TO_USE.getLocationOnScreen(adviewLoc);
                    container_size = new Point(InterstitialAdView.INTERSTITIALADVIEW_TO_USE.getMeasuredWidth(),
                            InterstitialAdView.INTERSTITIALADVIEW_TO_USE.getMeasuredHeight());
                } else {
                    AdView.this.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
                    AdView.this.getLocationOnScreen(adviewLoc);
                    container_size = new Point(AdView.this.getMeasuredWidth(),
                            AdView.this.getMeasuredHeight());
                }
                int max_x = (container_size.x - buttonPxSideLength);
                int max_y = (container_size.y - buttonPxSideLength);
                int min_x = 0;
                int min_y = 0;

                if (useScreenSizeForAddedAccuracy) {
                    max_x = adviewLoc[0] + Math.min(screen_size.x, container_size.x) - buttonPxSideLength;
                    max_y = adviewLoc[1] + Math.min(screen_size.y, container_size.y) - buttonPxSideLength;
                    min_x = adviewLoc[0];
                    min_y = adviewLoc[1];
                }

                if (close_button_loc[0] + 1 < min_x || close_button_loc[0] - 1 > max_x ||
                        close_button_loc[1] + 1 < min_y || close_button_loc[1] - 1 > max_y) {
                    //Button is off screen, and must be relocated on screen
                    final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(this.getLayoutParams());
                    lp.setMargins(0, 0, 0, 0);
                    lp.gravity = Gravity.TOP | Gravity.LEFT;
                    this.post(new Runnable() {
                        public void run() {
                            setLayoutParams(lp);
                        }
                    });

                    ViewUtil.showCloseButton(close_button, false);
                }
            }
        };

        FrameLayout.LayoutParams blp = new FrameLayout.LayoutParams(
                buttonPxSideLength,
                buttonPxSideLength, Gravity.CENTER);

        //Offsets from dead center
        int btn_offset_y = h / 2 - buttonPxSideLength / 2;
        int btn_offset_x = w / 2 - buttonPxSideLength / 2;
        switch (custom_close_position) {
            case bottom_center:
                blp.topMargin = btn_offset_y;
                break;
            case bottom_left:
                blp.rightMargin = btn_offset_x;
                blp.topMargin = btn_offset_y;
                break;
            case bottom_right:
                blp.leftMargin = btn_offset_x;
                blp.topMargin = btn_offset_y;
                break;
            case center:
                break;
            case top_center:
                blp.bottomMargin = btn_offset_y;
                break;
            case top_left:
                blp.rightMargin = btn_offset_x;
                blp.bottomMargin = btn_offset_y;
                break;
            case top_right:
                blp.leftMargin = btn_offset_x;
                blp.bottomMargin = btn_offset_y;
                break;

        }

        close_button.setLayoutParams(blp);
        close_button.setBackgroundColor(Color.TRANSPARENT);
        close_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                caller.close();

            }
        });

        if (caller.owner.getParent() != null) {
            ((ViewGroup) caller.owner.getParent()).addView(close_button);
        }
    }

    /**
     * @return true if the AdView is a {@link BannerAdView}.
     */
    abstract boolean isBanner();

    /**
     * @return true if the AdView is an {@link InterstitialAdView}.
     */
    abstract boolean isInterstitial();

    /**
     * Sets the currently installed listener that the SDK will send events to.
     *
     * @param listener The {@link AdListener} object to use.
     */
    public void setAdListener(AdListener listener) {
        Clog.d(Clog.publicFunctionsLogTag,
                Clog.getString(R.string.set_ad_listener));
        adListener = listener;
    }

    /**
     * Gets the currently installed listener that the SDK will send events to.
     *
     * @return The {@link AdListener} object in use.
     */
    public AdListener getAdListener() {
        Clog.d(Clog.publicFunctionsLogTag,
                Clog.getString(R.string.get_ad_listener));
        return adListener;
    }

    /**
     * Gets the currently installed app event listener that the SDK will send
     * custom events to.
     *
     * @return the {@link AppEventListener} object in use.
     */
    public AppEventListener getAppEventListener() {
        return appEventListener;
    }

    /**
     * Sets the currently installed app event listener that the SDK will send
     * custom events to.
     *
     * @param appEventListener The {@link AppEventListener} object to use.
     */
    public void setAppEventListener(AppEventListener appEventListener) {
        this.appEventListener = appEventListener;
    }

    /**
     * Retrieve the setting that determines whether or not the
     * device's native browser is used instead of the in-app
     * browser when the user clicks an ad.
     *
     * @return true if the device's native browser will be used; false otherwise.
     * @deprecated Use getClickThroughAction instead
     * Refer {@link ANClickThroughAction}
     */
    public boolean getOpensNativeBrowser() {
        Clog.d(Clog.publicFunctionsLogTag, Clog.getString(
                R.string.get_opens_native_browser, requestParameters.getOpensNativeBrowser()));
        return requestParameters.getOpensNativeBrowser();
    }

    /**
     * Set this to true to disable the in-app browser.  This will
     * cause URLs to open in a native browser such as Chrome so
     * that when the user clicks on an ad, your app will be paused
     * and the native browser will open.  Set this to false to
     * enable the in-app browser instead (a lightweight browser
     * that runs within your app).  The default value is false.
     *
     * @param opensNativeBrowser Whether or not the device's native browser should be used for
     *                           landing pages.
     * @deprecated Use setClickThroughAction instead
     * Refer {@link ANClickThroughAction}
     */
    public void setOpensNativeBrowser(boolean opensNativeBrowser) {
        Clog.d(Clog.publicFunctionsLogTag, Clog.getString(
                R.string.set_opens_native_browser, opensNativeBrowser));
        requestParameters.setOpensNativeBrowser(opensNativeBrowser);
    }

    /**
     * Returns the ANClickThroughAction that is used for this AdView.
     *
     * @return {@link ANClickThroughAction}
     */
    public ANClickThroughAction getClickThroughAction() {
        return requestParameters.getClickThroughAction();
    }


    /**
     * Determines what action to take when the user clicks on an ad.
     * If set to ANClickThroughAction.OPEN_DEVICE_BROWSER/ANClickThroughAction.OPEN_SDK_BROWSER then,
     * AdListener.onAdClicked(AdView adView) will be triggered and corresponding browser will load the click url.
     * If set to ANClickThroughAction.RETURN_URL then,
     * AdListener.onAdClicked(AdView adView, String clickUrl) will be triggered with clickUrl as its argument.
     * It is ASSUMED that the App will handle it appropriately.
     *
     * @param clickThroughAction ANClickThroughAction.OPEN_SDK_BROWSER which is default or
     *                           ANClickThroughAction.OPEN_DEVICE_BROWSER or
     *                           ANClickThroughAction.RETURN_URL
     */
    public void setClickThroughAction(ANClickThroughAction clickThroughAction) {
        requestParameters.setClickThroughAction(clickThroughAction);
    }

    /**
     * Retrieve the current PSA setting.  PSAs (Public Service
     * Announcements) are ads for various causes or nonprofit
     * organizations that can be served if there are no ads
     * available.  You can turn this on and off with
     * setShouldServePSAs.
     *
     * @return Whether this placement accepts PSAs if no ad is served.
     */
    public boolean getShouldServePSAs() {
        return requestParameters.getShouldServePSAs();
    }

    /**
     * Allows overriding the platform behavior in the case there is no ad
     * currently available. If set to true the platform will retrieve and
     * display a PSA (Public Service Announcement) . Set the value to false it
     * will return no ad.
     *
     * @param shouldServePSAs Whether this placement is willing to accept a PSA if no other ad is available.
     */
    public void setShouldServePSAs(boolean shouldServePSAs) {
        requestParameters.setShouldServePSAs(shouldServePSAs);
    }

    /**
     * Retrieve the reserve price.  The reserve price is the
     * minimum price you will accept in order to show an ad.  A
     * value of 0 indicates that there is no minimum.
     *
     * @return The reserve price.  A value of 0 indicates that no reserve is set.
     */
    public float getReserve() {
        return requestParameters.getReserve();
    }

    /**
     * Set a reserve price.  The reserve price is the minimum
     * price you will accept in order to show an ad.  Note that
     * setting a reserve price may negatively affect monetization,
     * since there may not be any buyers willing to pay more than
     * your reserve.  Setting this value to zero disables the
     * reserve price.  The default value is zero.
     *
     * @param reserve The reserve price expressed in CPM, e.g., 0.50f.
     */
    public void setReserve(float reserve) {
        requestParameters.setReserve(reserve);
    }

    /**
     * Retrieve the current user's age.  Note that this is a
     * string as it may come in one of several formats: age, birth
     * year, or age range.  The default value is an empty string.
     *
     * @return The current user's age.
     */
    public String getAge() {
        return requestParameters.getAge();
    }


    /**
     * Set the current user's age.  This should be set if the
     * user's age or age range is known, as it can help make
     * buying the ad space more attractive to advertisers.
     *
     * @param age A string containing a numeric age, birth year,
     *            or hyphenated age range.  For example: "56",
     *            "1974", or "25-35".
     */
    public void setAge(String age) {
        requestParameters.setAge(age);
    }

    @Deprecated
    /**
     * Set the current user's externalUID
     *
     * @param externalUid .
     * @deprecated  Use ({@link SDKSettings}.setPublisherUserId)
     */
    public void setExternalUid(String externalUid) {
        requestParameters.setExternalUid(externalUid);
    }

    @Deprecated
    /**
     * Retrieve the externalUID that was previously set.
     *
     * @return externalUID.
     */
    public String getExternalUid() {
        return requestParameters.getExternalUid();
    }


    /**
     * Get whether or not the banner or interstitial should show the loading indicator
     * after being pressed, but before able to launch the browser.
     * <p/>
     * Default is false
     *
     * @return true if the loading indicator will be displayed, else false
     */
    public boolean getShowLoadingIndicator() {
        return showLoadingIndicator;
    }

    /**
     * Set whether or not the banner or interstitial should show the loading indicator
     * after being pressed, but before able to launch the browser.
     * <p/>
     * Default is false
     *
     * @param show True if you desire the loading indicator to be displayed, else set to false
     */
    public void setShowLoadingIndicator(boolean show) {
        showLoadingIndicator = show;
    }

    /**
     * The user's gender.
     */
    public enum GENDER {
        UNKNOWN,
        MALE,
        FEMALE,

    }

    /**
     * Get the current user's gender, if it's available.  The
     * default value is UNKNOWN.
     *
     * @return The user's gender.
     */
    public GENDER getGender() {
        return requestParameters.getGender();
    }

    /**
     * Set the user's gender.  This should be set if the user's
     * gender is known, as it can help make buying the ad space
     * more attractive to advertisers.  The default value is
     * UNKNOWN.
     *
     * @param gender The user's gender.
     */
    public void setGender(GENDER gender) {
        requestParameters.setGender(gender);
    }

    /**
     * Add a custom keyword to the request URL for the ad.  This
     * is used to set custom targeting parameters within the
     * AppNexus platform.  You will be given the keys and values
     * to use by your AppNexus account representative or your ad
     * network.
     *
     * @param key   The key to add; this cannot be null or empty.
     * @param value The value to add; this cannot be null or empty.
     */
    public void addCustomKeywords(String key, String value) {
        requestParameters.addCustomKeywords(key, value);
    }

    /**
     * Remove a custom keyword from the request URL for the ad.
     * Use this to remove a keyword previously set using
     * addCustomKeywords.
     *
     * @param key The key to remove; this cannot be null or empty.
     */
    public void removeCustomKeyword(String key) {
        requestParameters.removeCustomKeyword(key);
    }

    /**
     * Clear all custom keywords from the request URL.
     */
    public void clearCustomKeywords() {
        requestParameters.clearCustomKeywords();
    }

    /**
     * Retrieve the array of custom keywords associated with the
     * current AdView.
     *
     * @return The current list of key-value pairs of custom
     * keywords.
     */
    public ArrayList<Pair<String, String>> getCustomKeywords() {
        return requestParameters.getCustomKeywords();
    }

    void setCreativeWidth(int w) {
        creativeWidth = w;
    }

    /**
     * Retrieve the 'unexpanded' size of the creative .
     * It does not change if the creative used the MRAID expand or resize calls.
     *
     * @return the width
     */

    public int getCreativeWidth() {
        return creativeWidth;
    }


    @Deprecated
    /**
     * Retrieve the Creative Id  of the creative .
     *
     * @return the creativeId
     * @deprecated see ({@link ANAdResponseInfo}.getCreativeId)
     */
    public String getCreativeId() {
        return creativeId;
    }

    void setCreativeId(String creativeId) {
        this.creativeId = creativeId;
    }

    /**
     * Set AppNexus CreativeId that you want to display on this AdUnit for debugging/testing purpose.
     *
     * @param forceCreativeId of the creative.
     */
    public void setForceCreativeId(int forceCreativeId) {
        requestParameters.setForceCreativeId(forceCreativeId);
    }

    void setCreativeHeight(int h) {
        creativeHeight = h;
    }

    /**
     * Retrieve the 'unexpanded' size of the creative .
     * It does not change if the creative used the MRAID expand or resize calls.
     *
     * @return the height
     */

    public int getCreativeHeight() {
        return creativeHeight;
    }


    void setAdType(AdType type) {
        adType = type;
    }

    @Deprecated
    /**
     * Retrieve the AdType being served on the AdView
     * AdType can be Banner/Video
     *
     * @return AdType of the Creative
     * @deprecated Use ({@link ANAdResponseInfo}.getAdType)
     */
    public AdType getAdType() {
        return adType;
    }

    /**
     * Sets whether or not to load landing pages in the background before displaying them.
     * This feature is on by default, but only works with the in-app browser (which is also enabled by default).
     * Disabling this feature may cause redirects, such as to the app store, to first open a blank web page.
     *
     * @param doesLoadingInBackground Whether or not to load landing pages in background.
     */
    public void setLoadsInBackground(boolean doesLoadingInBackground) {
        requestParameters.setLoadsInBackground(doesLoadingInBackground);
    }

    /**
     * Gets whether or not this AdView will load landing pages in the background before displaying them.
     * This feature is on by default, but only works with the in-app browser (which is also enabled by default).
     * Disabling this feature may cause redirects, such as to the app store, to first open a blank web page.
     *
     * @return Whether or not redirects and landing pages are loaded/processed in the background before being displayed.
     */
    public boolean getLoadsInBackground() {
        return requestParameters.getLoadsInBackground();
    }


    /**
     * Private class to bridge events from mediation to the user
     * AdListener class.
     */
    private class AdViewDispatcher implements AdDispatcher {

        Handler handler;

        public AdViewDispatcher(Handler h) {
            handler = h;
        }

        @Override
        public void onAdLoaded(final AdResponse ad) {
            if (SDKSettings.isBackgroundThreadingEnabled()) {
                TasksManager.getInstance().executeOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        processAdLoaded(ad);
                    }
                });
            } else {
                processAdLoaded(ad);
            }
        }

        private void processAdLoaded(AdResponse ad) {
            isFetching = false;
            if (ad.getMediaType().equals(MediaType.BANNER) || ad.getMediaType().equals(MediaType.INTERSTITIAL)) {
                handleBannerOrInterstitialAd(ad);
            } else if (ad.getMediaType().equals(MediaType.NATIVE)) {
                handleNativeAd(ad);
            } else {
                Clog.e(Clog.baseLogTag, "UNKNOWN media type::" + ad.getMediaType());
                onAdFailed(ResultCode.getNewInstance(ResultCode.INTERNAL_ERROR), null);
            }
        }

        @Override
        public void onAdLoaded() {
            isFetching = false;
        }

        @Override
        public void onAdFailed(final ResultCode code, final ANAdResponseInfo adResponseInfo) {
            if (SDKSettings.isBackgroundThreadingEnabled()) {
                TasksManager.getInstance().executeOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        processAdFailed(code, adResponseInfo);
                    }
                });
            } else {
                processAdFailed(code, adResponseInfo);
            }
        }

        private void processAdFailed(final ResultCode code, final ANAdResponseInfo adResponseInfo) {
            isFetching = false;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    setAdResponseInfo(adResponseInfo);
                    if (adListener != null) {
                        adListener.onAdRequestFailed(AdView.this, code);
                    }
                }
            });
        }

        @Override
        public void onAdExpanded() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (adListener != null)
                        adListener.onAdExpanded(AdView.this);
                }
            });
        }

        @Override
        public void onAdCollapsed() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (adListener != null)
                        adListener.onAdCollapsed(AdView.this);
                }
            });
        }

        @Override
        public void onAdClicked() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (adListener != null) {
                        Clog.d("ADVIEW", "onAdClicked");
                        adListener.onAdClicked(AdView.this);
                    }
                }
            });
        }

        @Override
        public void onAppEvent(final String name, final String data) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (appEventListener != null) {
                        appEventListener.onAppEvent(AdView.this, name, data);
                    }
                }
            });
        }

        @Override
        public void toggleAutoRefresh() {
            if (getMediaType().equals(MediaType.BANNER) && mAdFetcher.getState() == AdFetcher.STATE.STOPPED) {
                mAdFetcher.start();
            }
        }

        @Override
        public void onAdClicked(final String clickUrl) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (adListener != null) {
                        Clog.d(Clog.baseLogTag, "onAdClicked clickUrl");
                        adListener.onAdClicked(AdView.this, clickUrl);

                    }
                }
            });
        }

        private void handleNativeAd(AdResponse ad) {
            setAdType(AdType.NATIVE);

            setCreativeId(ad.getResponseData().getAdResponseInfo().getCreativeId());
            final NativeAdResponse response = ad.getNativeAdResponse();
            response.setAdResponseInfo(ad.getResponseData().getAdResponseInfo());
//            setAdResponseInfo(ad.getResponseData().getAdResponseInfo());
            response.setCreativeId(ad.getResponseData().getAdResponseInfo().getCreativeId());
            if (adListener != null) {
                adListener.onAdLoaded(response);
            }
        }

        private void handleBannerOrInterstitialAd(final AdResponse ad) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (ad.getResponseData() != null && ad.getResponseData().getImpressionURLs() != null && ad.getResponseData().getImpressionURLs().size() > 0) {
                        impressionTrackers = ad.getResponseData().getImpressionURLs();
                    }
                    if (ad.getDisplayable() != null && ad.getMediaType().equals(MediaType.BANNER) && ad.getResponseData().getAdType().equalsIgnoreCase(UTConstants.AD_TYPE_BANNER)) {
                        if (getEffectiveImpressionCountingMethod() == CountImpression.ONE_PX) {
                            VisibilityDetector visibilityDetector = VisibilityDetector.getInstance();
                            if (visibilityDetector != null) {
                                visibilityDetector.destroy(AdView.this);
                                visibilityDetector.addVisibilityListener(AdView.this);
                            }
                        }
                    }
                    setCreativeWidth(ad.getDisplayable().getCreativeWidth());
                    setCreativeHeight(ad.getDisplayable().getCreativeHeight());
                    setCreativeId(ad.getResponseData().getAdResponseInfo().getCreativeId());
                    setAdResponseInfo(ad.getResponseData().getAdResponseInfo());
                    if (ad.isMediated() && ad.getResponseData().getContentSource() == UTConstants.CSM) {
                        try {
                            displayMediated((MediatedDisplayable) ad.getDisplayable());
                        } catch (ClassCastException cce) {
                            Clog.e(Clog.baseLogTag, "The SDK shouldn't fail downcasts to MediatedDisplayable in AdView");
                        }
                    } else {
                        setFriendlyObstruction(ad.getDisplayable());
                        display(ad.getDisplayable());
                    }


                    // Banner OnAdLoaded and if View is attached to window, or if the LazyLoad is enabled Impression is counted.
                    if (getMediaType().equals(MediaType.BANNER)) {
                        if (getEffectiveImpressionCountingMethod() == CountImpression.ON_LOAD  ||
                                (getEffectiveImpressionCountingMethod() == CountImpression.LAZY_LOAD && isWebviewActivated() && ad.getResponseData().getAdType().equalsIgnoreCase(UTConstants.AD_TYPE_BANNER)) ||
                                (getEffectiveImpressionCountingMethod() == CountImpression.DEFAULT && isAdViewAttachedToWindow())) {
                            if (impressionTrackers != null && impressionTrackers.size() > 0) {
                                fireImpressionTracker();
                            }
                        }
                    }

                    if (ad.getResponseData().getAdType().equalsIgnoreCase(UTConstants.AD_TYPE_VIDEO)) {
                        setAdType(AdType.VIDEO);
                        if (mAdFetcher.getState() == AdFetcher.STATE.AUTO_REFRESH) {
                            mAdFetcher.stop();
                        }
                    } else if (ad.getResponseData().getAdType().equalsIgnoreCase(UTConstants.AD_TYPE_BANNER)) {
                        setAdType(AdType.BANNER);
                    }
                    if (adListener != null) {
                        adListener.onAdLoaded(AdView.this);
                    }
                    if (ad.getNativeAdResponse() != null) {
                        AdView.this.ad = ad;
                        NativeAdSDK.registerTracking(ad.getNativeAdResponse(), ad.getDisplayable().getView(), null, getFriendlyObstructionViewsList());
                    }
                }
            });
        }

        @Override
        public void onLazyAdLoaded(ANAdResponseInfo adResponseInfo) {
            isFetching = false;
            setAdResponseInfo(adResponseInfo);
            if (adListener != null) {
                adListener.onLazyAdLoaded(AdView.this);
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttachedToWindow = true;
        // OnAttaced to Window and Impresion tracker is non null then fire impression.
        if (getEffectiveImpressionCountingMethod() == CountImpression.DEFAULT && getMediaType().equals(MediaType.BANNER) && impressionTrackers != null && impressionTrackers.size() > 0) {
            fireImpressionTracker();
        }

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttachedToWindow = false;
    }

    void fireImpressionTracker() {
        try {
            // Just to be fail safe since we are making it to null below to mark it as being used.
            if (impressionTrackers != null && impressionTrackers.size() > 0) {
                ArrayList<String> impTrackers = new ArrayList<>(impressionTrackers);
                // Making it to null so that there is no duplicate firing. We fire exactly only once.
                impressionTrackers = null;
                SharedNetworkManager nm = SharedNetworkManager.getInstance(getContext());
                if (nm.isConnected(getContext())) {
                    for (String url : impTrackers) {
                        // Rare case: There can be a HTTP request for url, where nm.isConnected() is false, now
                        fireImpressionTracker(url);
                    }
                } else {
                    for (String url : impTrackers) {
                        nm.addURL(url, getContext());
                    }
                }
                impTrackers.clear();
            }

            if (lastDisplayable != null) {
                lastDisplayable.onAdImpression();
            }
        } catch (Exception e) { }
    }

    void fireImpressionTracker(final String trackerUrl) {
        Clog.d("FIRE_IMPRESSION", getEffectiveImpressionCountingMethod().name());
        HTTPGet impTracker = new HTTPGet() {
            @Override
            protected void onPostExecute(HTTPResponse response) {
                if (response != null && response.getSucceeded()) {
                    Clog.d(Clog.baseLogTag, "Impression Tracked successfully!");
                }
            }

            @Override
            protected String getUrl() {
                return trackerUrl;
            }
        };
        impTracker.execute();
    }

    boolean isAdViewAttachedToWindow() {

        // Use the framework API value if its KITKAT and Above.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return isAttachedToWindow();
        } else {
            return isAttachedToWindow;
        }
    }

    @Override
    public AdDispatcher getAdDispatcher() {
        return this.dispatcher;
    }

    /**
     * To be called by the developer when the fragment/activity's onDestroy() function is called.
     */
    abstract public void activityOnDestroy();

    /**
     * To be called by the developer when the fragment/activity's onPause() function is called.
     */
    abstract public void activityOnPause();

    /**
     * To be called by the developer when the fragment/activity's onResume() function is called.
     */
    abstract public void activityOnResume();

    abstract void interacted();

    @Override
    public ANMultiAdRequest getMultiAdRequest() {
        return requestParameters.getMultiAdRequest();
    }

    @Override
    public void associateWithMultiAdRequest(ANMultiAdRequest anMultiAdRequest) {
        requestParameters.associateWithMultiAdRequest(anMultiAdRequest);
    }

    @Override
    public void disassociateFromMultiAdRequest() {
        requestParameters.disassociateFromMultiAdRequest();
    }

    @Override
    public void initiateVastAdView(BaseAdResponse baseAdResponse, AdViewRequestManager adViewRequestManager) {
    }

    @Override
    public void setRequestManager(UTAdRequester requester) {
        mAdFetcher.setRequestManager(requester);
    }

    @Override
    public MultiAd getMultiAd() {
        return this;
    }

    public ANAdResponseInfo getAdResponseInfo() {
        return adResponseInfo;
    }

    private void setAdResponseInfo(ANAdResponseInfo adResponseInfo) {
        this.adResponseInfo = adResponseInfo;
    }

    /**
     * For adding Friendly Obstruction View
     *
     * @param view to be added
     */
    public void addFriendlyObstruction(View view) {
        if (view == null) {
            Clog.e(Clog.baseLogTag, "Invalid Friendly Obstruction View. The friendly obstruction view cannot be null.");
            return;
        }
        if (!alreadyAddedToFriendlyObstruction(view)) {
            friendlyObstructionList.add(new WeakReference<View>(view));
        }
        if (lastDisplayable != null) {
            lastDisplayable.addFriendlyObstruction(view);
        }
    }

    /**
     * For removing Friendly Obstruction View
     *
     * @param friendlyObstructionView to be removed
     */
    public void removeFriendlyObstruction(View friendlyObstructionView) {
        for (WeakReference<View> viewWeakReference : friendlyObstructionList) {
            if (viewWeakReference.get() != null && viewWeakReference.get() == friendlyObstructionView) {
                friendlyObstructionList.remove(viewWeakReference);
                break;
            }
        }
        if (lastDisplayable != null) {
            lastDisplayable.removeFriendlyObstruction(friendlyObstructionView);
        }
    }

    /**
     * For clearing the Friendly Obstruction Views
     */
    public void removeAllFriendlyObstructions() {
        friendlyObstructionList.clear();
        if (lastDisplayable != null) {
            lastDisplayable.removeAllFriendlyObstructions();
        }
    }

    protected ArrayList<WeakReference<View>> getFriendlyObstructionList() {
        return friendlyObstructionList;
    }

    private List<View> getFriendlyObstructionViewsList() {
        List<View> viewsList = new ArrayList<View>();
        for (WeakReference<View> view : friendlyObstructionList) {
            viewsList.add(view.get());
        }
        return viewsList;
    }

    private boolean alreadyAddedToFriendlyObstruction(View view) {
        for (WeakReference<View> viewWeakReference : friendlyObstructionList) {
            if (viewWeakReference.get() != null && viewWeakReference.get() == view) {
                return true;
            }
        }
        return false;
    }

    private void setFriendlyObstruction(Displayable displayable) {
        for (WeakReference<View> viewWeakReference : friendlyObstructionList) {
            if (viewWeakReference.get() != null) {
                displayable.addFriendlyObstruction(viewWeakReference.get());
            }
        }
    }

    /**
     * This is called to enable the Lazy Load
     *
     * @return true if the call to enableLazyLoad() is successful
     */
    protected boolean enableLazyLoad() {

        // enableLazyLoad() works only if the AdRequest is not already started.
        if (isFetching) {
            Clog.w(Clog.lazyLoadLogTag, getContext().getString(R.string.apn_enable_lazy_webview_failure_request_in_progress));
            return false;
        }

        // enableLazyLoad() works only if it is not already enabled.
        if (enableLazyLoad) {
            Clog.w(Clog.lazyLoadLogTag, getContext().getString(R.string.apn_enable_lazy_webview_failure_already_enabled));
            return false;
        }

        this.enableLazyLoad = true;
        return true;
    }

    /**
     * @return true if the user has enabled the Lazy Load, using enableLazyLoad()
     */
    protected boolean isLazyLoadEnabled() {
        return enableLazyLoad;
    }

    /**
     * Tells the status of the Lazy Load in conjunction with Webview activation
     *
     * @return true only if the lazy load enabled but the lazyLoadAd() has not been called yet.
     */
    protected boolean isLazyWebviewInactive() {
        return !isWebviewActivated() && enableLazyLoad;
    }

    /**
     * This method is called to load the content of a Lazy loaded Ad to the Webview (only if the Lazy Load is enabled)
     *
     * @return true if the call to loadLazyAd() is successful
     */
    protected boolean loadLazyAd() {

        // loadLazyAd() works only if the lazy load has been enabled (i.e enableLazyLoad is set to true)
        if (!enableLazyLoad) {
            Clog.w(Clog.lazyLoadLogTag, getContext().getString(R.string.apn_load_webview_failure_disabled_lazy_load));
            return false;
        }

        // loadLazyAd() works only if it isn't already called
        if (activateWebview) {
            Clog.w(Clog.lazyLoadLogTag, getContext().getString(R.string.apn_load_webview_failure_repeated_loadLazyAd));
            return false;
        }

        // loadLazyAd() only if the AdResponseInfo is already attached to the Banner instance
        if (getAdResponseInfo() == null) {
            Clog.w(Clog.lazyLoadLogTag, getContext().getString(R.string.apn_load_webview_failure_is_not_lazy_load));
            return false;
        }

        // loadLazyAd() only if the AdType is AdType.BANNER
        if (getAdResponseInfo().getAdType() != AdType.BANNER) {
            Clog.w(Clog.lazyLoadLogTag, getContext().getString(R.string.apn_enable_lazy_webview_failure_non_banner));
            return false;
        }

        activateWebview = true;
        if (mAdFetcher != null) {
            mAdFetcher.loadLazyAd();
        }
        return true;
    }

    /**
     * This returns if the Webview for the Lazy Load has been activated or not
     * The webview once activated is de-activated by calling the deactivateWebviewForNextCall() {basically for AutoRefresh }
     */
    protected boolean isWebviewActivated() {
        return activateWebview;
    }

    /**
     * This method deactivated the Webview - which means that, to load the Webview we need to call loadLazyAd()
     * This is make AutoRefresh work for Laay Load
     */
    protected void deactivateWebviewForNextCall() {
        activateWebview = false;
    }

    /**
     * This is used to know if the last response was successful, based on the content of the AdResponseInfo
     * see {@link ANAdResponseInfo}
     */
    protected boolean isLastResponseSuccessful() {
        return getAdResponseInfo() != null && getAdResponseInfo().getAdType() == AdType.BANNER;
    }

    /**
     * @return {@link CountImpression} Based on the boolean values set for the Impression Tracking
     * */
    public CountImpression getEffectiveImpressionCountingMethod() {
        if (countBannerImpressionOnAdLoad) {
            return CountImpression.ON_LOAD;
        } else if (SDKSettings.getCountImpressionOn1pxRendering()) {
            return CountImpression.ONE_PX;
        } else if (isLazyLoadEnabled()) {
            return CountImpression.LAZY_LOAD;
        } else {
            return CountImpression.DEFAULT;
        }
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        if (visible && impressionTrackers != null && impressionTrackers.size() > 0) {
            fireImpressionTracker();
            VisibilityDetector.getInstance().destroy(this);
        }
    }
}
