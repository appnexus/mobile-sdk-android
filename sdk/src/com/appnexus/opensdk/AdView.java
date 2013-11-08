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

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * The parent class of InterstitialAdView and BannerAdView. This can not be
 * instantiated directly. It should be accessed through one of its child classes. 
 *
 */
public abstract class AdView extends FrameLayout implements AdViewListener {

    AdFetcher mAdFetcher;
    String placementID;
    boolean opensNativeBrowser = false;
    int measuredWidth;
    int measuredHeight;
    private boolean measured = false;
    private int width = -1;
    private int height = -1;
    boolean shouldServePSAs = true;
    private float reserve = 0.00f;
    String age;
    GENDER gender = GENDER.UNKNOWN;
    ArrayList<Pair<String, String>> customKeywords = new ArrayList<Pair<String, String>>();
    boolean mraid_expand = false;
    AdListener adListener;
    private BrowserStyle browserStyle;
    private LinkedList<MediatedAd> mediatedAds;
    final Handler handler = new Handler(Looper.getMainLooper());
    private Displayable lastDisplayable;

    /**
     * Begin Construction *
     */
    @SuppressWarnings("javadoc")
    AdView(Context context) {
        super(context, null);
        setup(context, null);
    }

    AdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs);

    }

    AdView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup(context, attrs);
    }

    void setup(Context context, AttributeSet attrs) {
        // Store self.context in the settings for errors
        Clog.error_context = this.getContext();

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
        Settings.getSettings().ua = new WebView(context).getSettings()
                .getUserAgentString();
        Clog.v(Clog.baseLogTag,
                Clog.getString(R.string.ua, Settings.getSettings().ua));

        // Store the AppID in the settings
        Settings.getSettings().app_id = context.getApplicationContext()
                .getPackageName();
        Clog.v(Clog.baseLogTag,
                Clog.getString(R.string.appid, Settings.getSettings().app_id));

        Clog.v(Clog.baseLogTag, Clog.getString(R.string.making_adman));
        // Make an AdFetcher - Continue the creation pass
        mAdFetcher = new AdFetcher(this);
        // Load user variables only if attrs isn't null
        if (attrs != null)
            loadVariablesFromXML(context, attrs);

        // We don't start the ad requesting here, since the view hasn't been
        // sized yet.
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right,
                         int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mraid_expand) {
            mraid_expand = false;
            return;
        }
        if (!measured || changed) {
            // Convert to dips
            float density = getContext().getResources().getDisplayMetrics().density;
            measuredWidth = (int) ((right - left) / density + 0.5f);
            measuredHeight = (int) ((bottom - top) / density + 0.5f);
            if ((measuredHeight < height || measuredWidth < width) && measuredHeight > 0 && measuredWidth > 0) {
                Clog.e(Clog.baseLogTag, Clog.getString(R.string.adsize_too_big,
                        measuredWidth, measuredHeight, width, height));
                // Hide the space, since no ad will be loaded due to error
                hide();
                // Stop any request in progress
                if (mAdFetcher != null)
                    mAdFetcher.stop();
                // Returning here allows the SDK to re-request when the layout
                // next changes, and maybe the error will be amended.
                return;
            }

            // Hide the adview
            if (!measured) {
                hide();
            }

            measured = true;


        }
    }

    boolean isMRAIDExpanded() {
        if (this.getChildCount() > 0
                && this.getChildAt(0) instanceof MRAIDWebView
                && ((MRAIDWebView) getChildAt(0)).getImplementation().expanded) {
            return true;
        }
        return false;
    }

    boolean isReadyToStart() {
        if (isMRAIDExpanded()) {
            Clog.e(Clog.baseLogTag, Clog.getString(R.string.already_expanded));
            return false;
        }
        if (placementID == null || placementID.isEmpty()) {
            Clog.e(Clog.baseLogTag, Clog.getString(R.string.no_placement_id));
            return false;
        }
        return true;
    }

    /**
     * Loads a new ad, if the ad space is visible. You should have called
     * setPlacementID() before invoking this method. 
     * @return true is ad will begin loading, false otherwise. 
     * 
     */
    protected boolean loadAd() {
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

    /**
     * Loads a new ad, if the ad space is visible, and sets the placement id
     * attribute of the AdView to the supplied parameter.
     *
     * @param placementID The new placement id to use.
     *
     * @return true is ad will begin loading, false if ad cannot be loaded
     * at this time given the current settings
     */
    public boolean loadAd(String placementID) {
        this.setPlacementID(placementID);
        return loadAd();
    }

    /**
     * Loads a new ad, if the ad space is visible, and sets the placement id, ad
     * width, and ad height attribute of the AdView to the supplied parameters.
     *
     * @param placementID The new placement id to use.
     * @param width       The new width to use.
     * @param height      The new height to use.
     *
     * @return true is ad will begin loading, false if ad cannot be loaded
     * at this time given the current settings
     */
    public boolean loadAd(String placementID, int width, int height) {
        this.setAdHeight(height);
        this.setAdWidth(width);
        this.setPlacementID(placementID);
        return loadAd();
    }

    void loadHtml(String content, int width, int height) {
        this.mAdFetcher.stop();

        AdWebView awv = new AdWebView(this);
        awv.loadData(content, "text/html", "UTF-8");
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width,
                height);
        awv.setLayoutParams(lp);
        this.display(awv);
    }

    protected abstract void loadVariablesFromXML(Context context,
                                                 AttributeSet attrs);

	/*
     * End Construction
	 */

    void display(Displayable d) {
        if ((d == null) || d.failed()) {
            // The displayable has failed to be parsed or turned into a View.
            fail();
            return;
        }
        if (lastDisplayable != null) {
            if(lastDisplayable instanceof MediatedAdViewController){
                lastDisplayable.destroy();
            }
            lastDisplayable = null;
        }

        WebView webView = null;
        if (getChildAt(0) instanceof WebView) {
            webView = (WebView) getChildAt(0);
        }

        this.removeAllViews();
        if (webView != null)
            webView.destroy();
        if (d.getView() == null) {
            return;
        }
        this.addView(d.getView());
        lastDisplayable = d;
        unhide();
    }

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
     * @return The current placement id.
     */
    public String getPlacementID() {
        Clog.d(Clog.publicFunctionsLogTag,
                Clog.getString(R.string.get_placement_id, placementID));
        return placementID;
    }

    /**
     * Sets the placement id of the AdView.
     *
     * @param placementID The placement id to use
     */
    public void setPlacementID(String placementID) {
        Clog.d(Clog.publicFunctionsLogTag,
                Clog.getString(R.string.set_placement_id, placementID));
        this.placementID = placementID;
    }

    @Override
    protected void finalize() {
        try {
            super.finalize();
        } catch (Throwable e) {
        }
        // Just in case, kill the adfetcher's service
        if (mAdFetcher != null)
            mAdFetcher.stop();
    }

    /**
     * Sets the height of the ad to request.
     *
     * @param h The height, in pixels, to use.
     */
    public void setAdHeight(int h) {
        Clog.d(Clog.baseLogTag, Clog.getString(R.string.set_height, h));
        height = h;
    }

    /**
     * Sets the width of the ad to request.
     *
     * @param w The width, in pixels, to use. 
     */
    public void setAdWidth(int w) {
        Clog.d(Clog.baseLogTag, Clog.getString(R.string.set_width, w));
        width = w;
    }

    /**
     * Retrieve the previously set height of the ad.
     * @return The height of the ad to be requested. A value of -1 indicates any size
     */
    public int getAdHeight() {
        Clog.d(Clog.baseLogTag, Clog.getString(R.string.get_height, height));
        return height;
    }

    /**
     * Retrieve the previously set width of the ad.
     * @return The width of the ad to be requested. A value of -1 indicates any size
     */
    public int getAdWidth() {
        Clog.d(Clog.baseLogTag, Clog.getString(R.string.get_width, width));
        return width;
    }

    int getContainerWidth() {
        return measuredWidth;
    }

    int getContainerHeight() {
        return measuredHeight;
    }

    // Used only by MRAID
    ImageButton close;

    void expand(int w, int h, boolean custom_close,
                final MRAIDImplementation caller) {
        // Only expand w and h if they are >0, otherwise they are match_parent
        // or something
        mraid_expand = true;
        if (getLayoutParams() != null) {
            if (getLayoutParams().width > 0)
                getLayoutParams().width = w;
            if (getLayoutParams().height > 0)
                getLayoutParams().height = h;
        }
        if (!custom_close && close == null) {
            // Add a stock close button to the top right corner
            close = new ImageButton(this.getContext());
            close.setImageDrawable(getResources().getDrawable(
                    android.R.drawable.ic_menu_close_clear_cancel));
            FrameLayout.LayoutParams blp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.RIGHT
                    | Gravity.TOP);
            if (this.getChildAt(0) != null)
                blp.rightMargin = (this.getMeasuredWidth() - this.getChildAt(0)
                        .getMeasuredWidth()) / 2;
            close.setLayoutParams(blp);
            close.setBackgroundColor(Color.TRANSPARENT);
            close.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    caller.close();

                }
            });
            this.addView(close);
        } else if (close != null) {
            if (custom_close) {
                close.setVisibility(GONE);
            } else {
                this.removeView(close);
                close.setVisibility(VISIBLE);
                this.addView(close);// Re-add to send to top
            }
        }
    }

    /**
     *
     * @return true if the AdView is a BannerAdView
     */
    abstract boolean isBanner();

    /**
     *
     * @return true if the AdView is an InterstitialAdView
     */
    abstract boolean isInterstitial();

    /**
     * Sets the listener that the InterstitialAdView will call events in.
     *
     * @param listener The {@link AdListener} object to use.
     */
    public void setAdListener(AdListener listener) {
        Clog.d(Clog.publicFunctionsLogTag,
                Clog.getString(R.string.set_ad_listener));
        adListener = listener;
    }

    /**
     * Gets the listener that the InterstitialAdView will call events in.
     *
     * @return The {@link AdListener} object in use.
     */
    public AdListener getAdListener() {
        Clog.d(Clog.publicFunctionsLogTag,
                Clog.getString(R.string.get_ad_listener));
        return adListener;
    }

    void fail() {
        onAdFailed(true);
    }

    /**
     * Retrieve the inapp or  native browser setting. 
     * @return whether or not the devices native browser is used instead of the in-app
     *         browser.
     */
    public boolean getOpensNativeBrowser() {
        Clog.d(Clog.publicFunctionsLogTag, Clog.getString(
                R.string.get_opens_native_browser, opensNativeBrowser));
        return opensNativeBrowser;
    }

    /**
     * Set this to true to disable the in-app browser. That will cause url's to open
     * in the device's native browser e.g. Chrome. If set to true when a user clicks 
     * on an ad your app will be paused and the native browser will open. 
     * Set this to false to enabled the in-app browser. Which is a lightweight browser that runs
     * within your app.
     * The default value is false.
     * @param opensNativeBrowser
     */
    public void setOpensNativeBrowser(boolean opensNativeBrowser) {
        Clog.d(Clog.publicFunctionsLogTag, Clog.getString(
                R.string.set_opens_native_browser, opensNativeBrowser));
        this.opensNativeBrowser = opensNativeBrowser;
    }

    BrowserStyle getBrowserStyle() {
        return browserStyle;
    }

    protected void setBrowserStyle(BrowserStyle browserStyle) {
        this.browserStyle = browserStyle;
    }

    /**
     * Retrieve the current PSA setting
     * @return Whether this placement accepts PSAs if no ad is served.
     */
    public boolean getShouldServePSAs() {
        return shouldServePSAs;
    }

    /**
     * Allows overriding the platform behavior if there is no ad available. If 
     * set to true the platform will retrieve a PSA ( Public Service Announcement).
     * set to false it will return no ad. If writing a custom mediation adaptor make sure 
     * that this is set to false.
     * @param shouldServePSAs Whether this placement accepts PSAs if no ad is served.
     */
    public void setShouldServePSAs(boolean shouldServePSAs) {
        this.shouldServePSAs = shouldServePSAs;
    }

    /**
     * Retrieve the minimum price. A value of zero indicates no minimum.
     * @return The minimum price zero indicates none.
     */
    public float getReserve() {
        return reserve;
    }

    /**
     * Set a minimum price. Note that setting a minimum may negatively affect monetization.
     * Setting this value to zero disables the minimum price. Default value is zero. 
     * @param reserve
     */
    public void setReserve(float reserve) {
        this.reserve = reserve;
    }

    
    /**
     * Retrieve the current users Age. Note this is a string as it may be an age, 
     * birth year or age range. The default value is an empty string.
     * @return The age 
     */
    public String getAge() {
        return age;
    }

    /**
     * Set's the user's age. should be used if age or age range is known. 
     * @param age should be a numerical age, birth year, or hyphenated age range.
     *            For example: "56", "1974", or "25-35"
     */
    public void setAge(String age) {
        this.age = age;
    }

    
    public enum GENDER {
    	UNKNOWN,
        MALE,
        FEMALE,

    }

    /**
     * Get the current user's gender. 
     * @return
     */
    public GENDER getGender() {
        return gender;
    }

    /**
     * Set the users gender if it is known. Default value is UNKNOWN
     * @param The users gender 
     */
    public void setGender(GENDER gender) {
        this.gender = gender;
    }

    /**
     * add a custom keyword to the request url for the ad. 
     * @param key keyword name to add, cannot be null or empty
     * @param value keyword value, cannot be null
     */
    public void addCustomKeywords(String key, String value) {
        if ((key == null) || (key.isEmpty()) || (value == null)) {
            return;
        }
        customKeywords.add(new Pair<String, String>(key, value));
    }

    /**
     * remove a custom keyword from the request url for the ad
     * @param key keyword name to remove, cannot be null or empty
     */
    public void removeCustomKeyword(String key) {
        if ((key == null) || (key.isEmpty()))
            return;

        for (int i = 0; i < customKeywords.size(); i++) {
            Pair<String, String> pair = customKeywords.get(i);
            if (pair.first.equals(key)) {
                customKeywords.remove(i);
                break;
            }
        }
    }

    /**
     * Retrieve the array of currently configured Custom Keywords
     * @return The array list
     */
    public ArrayList<Pair<String, String>> getCustomKeywords() {
        return customKeywords;
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

   
    @Override
    public void onAdLoaded(final Displayable d) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                display(d);
                if (adListener != null)
                    adListener.onAdLoaded(AdView.this);
            }
        });
    }

    @Override
    public void onAdFailed(boolean noMoreAds) {
        // wait until mediation waterfall is complete before calling adListener
        if (!noMoreAds)
            return;
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (adListener != null)
                    adListener.onAdRequestFailed(AdView.this);
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

    LinkedList<MediatedAd> getMediatedAds() {
        return mediatedAds;
    }

    void setMediatedAds(LinkedList<MediatedAd> mediatedAds) {
        this.mediatedAds = mediatedAds;
    }

    // returns the first mediated ad if available
    public MediatedAd popMediatedAd() {
        return mediatedAds != null ? mediatedAds.removeFirst() : null;
    }

}
