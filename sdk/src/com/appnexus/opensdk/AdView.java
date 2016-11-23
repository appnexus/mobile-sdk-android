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
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.appnexus.opensdk.utils.AdvertistingIDUtil;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.ViewUtil;

import java.util.ArrayList;

/**
 * The parent class of {@link InterstitialAdView} and {@link
 * BannerAdView}.  This may not be instantiated directly.  Its public
 * methods are accessed through one of its sub classes.
 */
public abstract class AdView extends FrameLayout implements Ad {

    AdFetcher mAdFetcher;
    boolean mraid_changing_size_or_visibility = false;
    int creativeWidth;
    int creativeHeight;
    private AdListener adListener;
    private AppEventListener appEventListener;
    private BrowserStyle browserStyle;

    final Handler handler = new Handler(Looper.getMainLooper());
    protected Displayable lastDisplayable;
    private AdViewDispatcher dispatcher;
    boolean loadedOffscreen = false;
    boolean isMRAIDExpanded = false;
    boolean doesLoadingInBackground = true;

    private boolean shouldResizeParent = false;
    private boolean showLoadingIndicator = true;

    RequestParameters requestParameters;
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
        requestParameters = new RequestParameters(context);

        AdvertistingIDUtil.retrieveAndSetAAID(context);

        // Store self.context in the settings for errors
        Clog.setErrorContext(this.getContext());

        Clog.d(Clog.publicFunctionsLogTag, Clog.getString(R.string.new_adview));

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        if (prefs.getBoolean("opensdk_first_launch", true)) {
            // This is the first launch, store a value to remember
            Clog.v(Clog.baseLogTag,
                    Clog.getString(R.string.first_opensdk_launch));
            Settings.getSettings().first_launch = true;
            prefs.edit().putBoolean("opensdk_first_launch", false).commit();
        } else {
            // Found the stored value, this is NOT the first launch
            Clog.v(Clog.baseLogTag,
                    Clog.getString(R.string.not_first_opensdk_launch));
            Settings.getSettings().first_launch = false;
        }

        // Store the UA in the settings
        try {
            Settings.getSettings().ua = new WebView(context).getSettings()
                    .getUserAgentString();
            Clog.v(Clog.baseLogTag,
                    Clog.getString(R.string.ua, Settings.getSettings().ua));
        }catch (Exception e){
            // Catches PackageManager$NameNotFoundException for webview
            Settings.getSettings().ua = "";
            Clog.e(Clog.baseLogTag, " Exception: "+e.getMessage());
        }

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
        if (isMRAIDExpanded()) {
            Clog.e(Clog.baseLogTag, Clog.getString(R.string.already_expanded));
            return false;
        }
        return requestParameters.isReadyForRequest();
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
        if (this.getWindowVisibility() == VISIBLE && mAdFetcher != null) {
            // Reload Ad Fetcher to get new ad at user's request
            mAdFetcher.stop();
            mAdFetcher.clearDurations();
            mAdFetcher.start();
            return true;
        }
        return false;
    }

    public void loadAdOffscreen() {
        if (!isReadyToStart())
            return;
        if (mAdFetcher != null) {
            // Reload Ad Fetcher to get new ad at user's request
            mAdFetcher.stop();
            mAdFetcher.clearDurations();
            mAdFetcher.start();
            loadedOffscreen = true;
        }
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
        AdWebView output = new AdWebView(this);
        ServerResponse response = new ServerResponse(html, width, height);
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

    /**
     * Retrieve the member ID.
     *
     * @return the member id that this AdView belongs to.
     */
    public int getMemberID() {
        return requestParameters.getMemberID();
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
        Clog.d(Clog.baseLogTag, "called destroy() on AdView");
        if (this.lastDisplayable != null) {
            this.lastDisplayable.destroy();
            this.lastDisplayable = null;
        }
        // Just in case, kill the adfetcher's service
        if (mAdFetcher != null) {
            mAdFetcher.stop();
        }
    }

    int getContainerWidth() {
        return requestParameters.getContainerWidth();
    }

    int getContainerHeight() {
        return requestParameters.getContainerHeight();
    }

    protected void setShouldResizeParent(boolean shouldResizeParent) {
        this.shouldResizeParent = shouldResizeParent;
    }

    /**
     * MRAID functions and variables
     */
    boolean mraid_is_closing = false;
    ImageButton close_button;
    static FrameLayout mraidFullscreenContainer;
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
                ((MutableContextWrapper)caller.owner.getContext()).setBaseContext(getContext());
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
            close_button = ViewUtil.createCloseButton(this.getContext(), use_custom_close);
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
        close_button = ViewUtil.createCloseButton(this.getContext(), custom_close);
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

    void resize(int w, int h, int offset_x, int offset_y, MRAIDImplementation.CUSTOM_CLOSE_POSITION custom_close_position, boolean allow_offscrean,
                final MRAIDImplementation caller) {
        MRAIDChangeSize(w, h);

        // Add a stock close_button button to the top right corner
        ViewUtil.removeChildFromParent(close_button);

        if (!(buttonPxSideLength > 0)) {
            final float scale = caller.owner.getContext().getResources().getDisplayMetrics().density;
            buttonPxSideLength = (int) (50 * scale);
        }

        close_button = new ImageButton(this.getContext()) {

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

                    close_button.setImageDrawable(getResources().getDrawable(
                            android.R.drawable.ic_menu_close_clear_cancel));
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
     */
    public void setOpensNativeBrowser(boolean opensNativeBrowser) {
        Clog.d(Clog.publicFunctionsLogTag, Clog.getString(
                R.string.set_opens_native_browser, opensNativeBrowser));
        requestParameters.setOpensNativeBrowser(opensNativeBrowser);
    }

    BrowserStyle getBrowserStyle() {
        return browserStyle;
    }

    protected void setBrowserStyle(BrowserStyle browserStyle) {
        this.browserStyle = browserStyle;
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

    static class BrowserStyle {

        public BrowserStyle(Drawable forwardButton, Drawable backButton,
                            Drawable refreshButton) {
            this.forwardButton = forwardButton;
            this.backButton = backButton;
            this.refreshButton = refreshButton;
        }

        final Drawable forwardButton;
        final Drawable backButton;
        final Drawable refreshButton;

        static final ArrayList<Pair<String, BrowserStyle>> bridge = new ArrayList<Pair<String, BrowserStyle>>();
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

    /**
     * Sets whether or not to load landing pages in the background before displaying them.
     * This feature is on by default, but only works with the in-app browser (which is also enabled by default).
     * Disabling this feature may cause redirects, such as to the app store, to first open a blank web page.
     *
     * @param doesLoadingInBackground Whether or not to load landing pages in background.
     */
    public void setLoadsInBackground(boolean doesLoadingInBackground) {
        this.doesLoadingInBackground = doesLoadingInBackground;
    }

    /**
     * Gets whether or not this AdView will load landing pages in the background before displaying them.
     * This feature is on by default, but only works with the in-app browser (which is also enabled by default).
     * Disabling this feature may cause redirects, such as to the app store, to first open a blank web page.
     *
     * @return Whether or not redirects and landing pages are loaded/processed in the background before being displayed.
     */
    public boolean getLoadsInBackground() {
        return this.doesLoadingInBackground;
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
            if (ad.getMediaType().equals(MediaType.BANNER) || ad.getMediaType().equals(MediaType.INTERSTITIAL)) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        setCreativeWidth(ad.getDisplayable().getCreativeWidth());
                        setCreativeHeight(ad.getDisplayable().getCreativeHeight());
                        if (ad.isMediated()) {
                            try {
                                displayMediated((MediatedDisplayable) ad.getDisplayable());
                            } catch (ClassCastException cce) {
                                Clog.e(Clog.baseLogTag, "The SDK shouldn't fail downcasts to MediatedDisplayable in AdView");
                            }
                        } else {
                            display(ad.getDisplayable());
                        }
                        if (adListener != null)
                            adListener.onAdLoaded(AdView.this);
                    }
                });
            } else {
                onAdFailed(ResultCode.INTERNAL_ERROR);
            }

        }

        @Override
        public void onAdFailed(final ResultCode code) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (adListener != null)
                        adListener.onAdRequestFailed(AdView.this, code);
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
                    if (adListener != null)
                        adListener.onAdClicked(AdView.this);
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

}
