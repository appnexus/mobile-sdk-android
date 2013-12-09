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

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This class controls the loading and displaying of interstitial ads.
 * Interstitial ads are modal and take up the entire screen. The Interstitial Ad is tied
 * to an {@link AdActivity} which is lauched to show the ad.
 *
 */
public class InterstitialAdView extends AdView {
    static final long MAX_AGE = 60000;
    private ArrayList<Size> allowedSizes;
    private int backgroundColor = Color.BLACK;
    private int closeButtonDelay = Settings.getSettings().DEFAULT_INTERSTITIAL_CLOSE_BUTTON_DELAY;
    boolean interacted = false;
    static InterstitialAdView INTERSTITIALADVIEW_TO_USE;
    static final Queue<Pair<Long, Displayable>> q = new LinkedList<Pair<Long, Displayable>>();

    //Intent Keys
    static final String INTENT_KEY_TIME = "TIME";
    private static final String INTENT_KEY_ORIENTATION = "ORIENTATION";
    static final String INTENT_KEY_CLOSE_BUTTON_DELAY = "CLOSE_BUTTON_DELAY";

    //To let the activity show the button.
    private AdActivity adActivity = null;

    /**
     * Creates a new InterstitialAdView
     *
     * @param context The context of the ViewGroup to which the InterstitialAdView
     *                is being added.
     */
    public InterstitialAdView(Context context) {
        super(context);
    }

    /**
     * Creates a new InterstitialAdView
     *
     * @param context The context of the ViewGroup to which the InterstitialAdView
     *                is being added.
     * @param attrs   The {@link AttributeSet} to use when creating the
     *                InterstitialAdView.
     */
    public InterstitialAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Creates a new InterstitialAdView
     *
     * @param context  The context of the ViewGroup to which the InterstitialAdView
     *                 is being added.
     * @param attrs    The AttributeSet to use when creating the
     *                 InterstitialAdView.rs
     * @param defStyle The default style to apply to this view. If 0, no style will
     *                 be applied (beyond what is included in the theme). This may
     *                 either be an attribute resource, whose value will be retrieved
     *                 from the current theme, or an explicit style resource.
     */
    public InterstitialAdView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void setup(Context context, AttributeSet attrs) {
        super.setup(context, attrs);
        INTERSTITIALADVIEW_TO_USE = this;
        mAdFetcher.setAutoRefresh(false);

        // Get the screen size
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        measuredHeight = dm.heightPixels;
        measuredWidth = dm.widthPixels;
        int h_adjust = 0;

        Activity a = (Activity) context;
        if (a != null) {
            Rect r = new Rect();
            a.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
            h_adjust += a.getWindow().findViewById(Window.ID_ANDROID_CONTENT)
                    .getTop();
            measuredHeight -= h_adjust;
        }

        float scale = dm.density;
        measuredHeight = (int) (measuredHeight / scale + 0.5f);
        measuredWidth = (int) (measuredWidth / scale + 0.5f);

        allowedSizes = new ArrayList<Size>();


        if (new Size(300, 250).fitsIn(measuredWidth, measuredHeight))
            allowedSizes.add(new Size(300, 250));
        if (new Size(320, 480).fitsIn(measuredWidth, measuredHeight))
            allowedSizes.add(new Size(320, 480));
        if (new Size(900, 500).fitsIn(measuredWidth, measuredHeight))
            allowedSizes.add(new Size(900, 500));
        if (new Size(1024, 1024).fitsIn(measuredWidth, measuredHeight))
            allowedSizes.add(new Size(1024, 1024));

    }

    @Override
    protected void loadVariablesFromXML(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.InterstitialAdView);

        final int N = a.getIndexCount();
        Clog.v(Clog.xmlLogTag, Clog.getString(R.string.found_n_in_xml, N));
        for (int i = 0; i < N; ++i) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.InterstitialAdView_placement_id) {
                setPlacementID(a.getString(attr));
                Clog.d(Clog.xmlLogTag, Clog.getString(R.string.placement_id,
                        a.getString(attr)));
            } else if (attr == R.styleable.InterstitialAdView_test) {
                Settings.getSettings().test_mode = a.getBoolean(attr, false);
                Clog.d(Clog.xmlLogTag,
                        Clog.getString(R.string.xml_set_test,
                                Settings.getSettings().test_mode));
            } else if (attr == R.styleable.InterstitialAdView_opens_native_browser) {
                Clog.d(Clog.xmlLogTag,
                        Clog.getString(R.string.xml_set_opens_native_browser));
                this.setOpensNativeBrowser(a.getBoolean(attr, false));
            }
        }
        a.recycle();
    }

    /**
     * Requests a new interstitial ad from the server and stores it in a local
     * queue. Please note, that interstitials have a timeout of 60 seconds. You
     * must show the interstitial (call 'show()') within 60 seconds of getting a
     * response, otherwise, the ad will not show.
     */
    @Override
    public boolean loadAd() {
        Clog.d(Clog.publicFunctionsLogTag, Clog.getString(R.string.load_ad_int));
        if (!isReadyToStart())
            return false;
        if (mAdFetcher != null) {
            // Load an interstitial ad
            mAdFetcher.stop();
            mAdFetcher.start();
            return true;
        }
        return false;
    }

    @Override
    void display(Displayable d) {
        if (d == null) {
            fail();
            return;
        }
        InterstitialAdView.q.add(new Pair<Long, Displayable>(System
                .currentTimeMillis(), d));
    }

    void interacted() {
        interacted = true;
        if (getAdActivity() != null) {
            getAdActivity().addCloseButton();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                         int bottom) {
        // leave empty so that we don't call super
    }

    // removes stale ads and returns whether or not a valid ad exists
    private boolean removeStaleAds(long now) {
        boolean validAdExists = false;
        ArrayList<Pair<Long, Displayable>> staleAdsList = new ArrayList<Pair<Long, Displayable>>();
        for (Pair<Long, Displayable> p : InterstitialAdView.q) {
            if (p == null || p.second == null
                    || now - p.first > InterstitialAdView.MAX_AGE) {
                staleAdsList.add(p);
            } else {
                // We've reached a valid ad, so we can stop looking
                validAdExists = true;
                break;
            }
        }
        // Clear the queue of invalid ads
        for (Pair<Long, Displayable> p : staleAdsList) {
            InterstitialAdView.q.remove(p);
        }
        return validAdExists;
    }

    /**
     * Checks the queue to see if there are any valid ads available.
     *
     * @return whether there is a valid ad available in the queue
     */
    public boolean isReady() {
        long now = System.currentTimeMillis();
        if (removeStaleAds(now)) {
            Pair<Long, Displayable> top = InterstitialAdView.q.peek();
            if (top != null && top.second instanceof MediatedDisplayable) {
                MediatedDisplayable mediatedDisplayable = (MediatedDisplayable) top.second;
                if (mediatedDisplayable.mAVC instanceof MediatedInterstitialAdViewController) {
                    MediatedInterstitialAdViewController mAVC = (MediatedInterstitialAdViewController) mediatedDisplayable.mAVC;
                    return mAVC.isReady();
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Pops ads from the queue until it finds one that has not exceeded the
     * timeout of 60 seconds, and displays it in a new activity. All ads in the
     * queue which have exceeded the timeout are removed.
     *
     * @return The number of remaining ads in the queue that do not exceed the
     *         timeout.
     */
    public int show() {
        Clog.d(Clog.publicFunctionsLogTag, Clog.getString(R.string.show_int));
        // Make sure there is an ad to show
        long now = System.currentTimeMillis();
        boolean validAdExists = removeStaleAds(now);

        //If the head of the queue is interstitial mediation, show that instead of our adactivity
        Pair<Long, Displayable> top = InterstitialAdView.q.peek();
        if (top != null && top.second instanceof MediatedDisplayable) {
            MediatedDisplayable mediatedDisplayable = (MediatedDisplayable) top.second;
            if (mediatedDisplayable.mAVC instanceof MediatedInterstitialAdViewController) {
                MediatedInterstitialAdViewController mAVC = (MediatedInterstitialAdViewController) mediatedDisplayable.mAVC;
                mAVC.show();

                //Pop the mediated view;
                InterstitialAdView.q.poll();
                return InterstitialAdView.q.size();
            }
        }

        // otherwise, launch our adActivity
        if (validAdExists) {
            Intent i = new Intent(getContext(), AdActivity.class);
            i.putExtra(InterstitialAdView.INTENT_KEY_TIME, now);
            i.putExtra(InterstitialAdView.INTENT_KEY_ORIENTATION, getContext().getResources()
                    .getConfiguration().orientation);
            i.putExtra(InterstitialAdView.INTENT_KEY_CLOSE_BUTTON_DELAY, closeButtonDelay);

            try {
				getContext().startActivity(i);
			} catch (ActivityNotFoundException e) {
				Clog.e(Clog.baseLogTag, "Did you insert com.appneus.opensd.AdActivity into AndroidManifest.xml ?");
			}

            return InterstitialAdView.q.size() - 1; // Return the number of ads remaining, less the one we're about to show
        }
        Clog.w(Clog.baseLogTag, Clog.getString(R.string.empty_queue));
        return InterstitialAdView.q.size();
    }

    /**
     * Returns an ArrayList of {@link Size}s which are allowed to be displayed.
     *
     * @return The ArrayList of {@link Size}s which are allowed to be displayed.
     */
    public ArrayList<Size> getAllowedSizes() {
        Clog.d(Clog.publicFunctionsLogTag,
                Clog.getString(R.string.get_allowed_sizes));
        return allowedSizes;
    }

    /**
     * Sets the ArrayList of {@link Size}s which are allowed to be displayed.
     * The allowed sizes is the list of the platform ad sizes which may be inserted into
     * an interstitial view. The default list is sufficient for most implementations. Custom
     * sizes may be added here.
     * @param allowed_sizes The ArrayList of {@link Size}s which are allowed to be
     *                      displayed.
     */
    public void setAllowedSizes(ArrayList<Size> allowed_sizes) {
        Clog.d(Clog.publicFunctionsLogTag,
                Clog.getString(R.string.set_allowed_sizes));
        allowedSizes = allowed_sizes;
    }

    /**
     * Sets the background Color to use behind the interstitial ad.
     * If left unspecified the default background is Black.
     */
    public void setBackgroundColor(int color) {
        Clog.d(Clog.publicFunctionsLogTag, Clog.getString(R.string.set_bg));
        backgroundColor = color;
    }

    /**
     * Gets the background Color to use behind the interstitial ad.
     *
     * @return The background Color to use behind the interstitial ad.
     */
    public int getBackgroundColor() {
        Clog.d(Clog.publicFunctionsLogTag, Clog.getString(R.string.get_bg));
        return backgroundColor;
    }

    /**
     * Destroys this InterstitialAdView object.
     */
    public void destroy() {
        Clog.d(Clog.publicFunctionsLogTag, Clog.getString(R.string.destroy_int));
        if (this.mAdFetcher != null)
            mAdFetcher.stop();
        InterstitialAdView.q.clear();
        InterstitialAdView.INTERSTITIALADVIEW_TO_USE = null;
    }

    /**
     * @return the time in milliseconds after an interstitial is displayed until
     *         the close button appears. Default is 10 seconds. 0 is disabled
     */
    public int getCloseButtonDelay() {
        return closeButtonDelay;
    }

    /**
     * Interstitial Ad's have a close button. The close button does not appear until the ad
     * has been view for 10 seconds by default. This method allows you to override the timeout. Settting
     * the value to 0 shows the close button with the ad. The maximum time allowed is 10 seconds. Any value
     * larger than that will cause 10 seconds to be used.
     * @param closeButtonDelay The time in milliseconds to wait before showing the close button.
     */
    public void setCloseButtonDelay(int closeButtonDelay) {
        this.closeButtonDelay = Math.min(closeButtonDelay, Settings.getSettings().DEFAULT_INTERSTITIAL_CLOSE_BUTTON_DELAY);
    }

    AdActivity getAdActivity() {
        return adActivity;
    }

    void setAdActivity(AdActivity adActivity) {
        this.adActivity = adActivity;
    }

    /**
     * A convenience class which holds a width and height in integers.
  	*/
     public class Size {
        private final int w;
        private final int h;

        Size(int w, int h) {
            this.w = w;
            this.h = h;
        }

        /**
         * @return The width, in pixels.
         */
        public int width() {
            return w;
        }

        /**
         * @return The height, in pixels.
         */
        public int height() {
            return h;
        }

        /**
         * Determines whether this size object fits inside a rectangle of the
         * given width and height
         *
         * @param width  The width to check against.
         * @param height The height to check against.
         * @return True, if the size fits inside the described rectangle,
         *         otherwise, false.
         */
        public boolean fitsIn(int width, int height) {
            return h < height && w < width;
        }
    }

    @Override
    boolean isBanner() {
        return false;
    }

    @Override
    boolean isInterstitial() {
        return true;
    }

    @Override
    void expand(int w, int h, boolean custom_close, final MRAIDImplementation caller) {
        if ((getAdActivity() == null) || (getAdActivity().layout == null))
            return;
        FrameLayout activityLayout = getAdActivity().layout;

        mraid_expand = true;
        if (!custom_close && (close == null)) {
            // Add a stock close button to the top right corner
            close = new ImageButton(activityLayout.getContext());
            close.setImageDrawable(getResources().getDrawable(
                    android.R.drawable.ic_menu_close_clear_cancel));
            FrameLayout.LayoutParams blp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.RIGHT
                    | Gravity.TOP);
            if (activityLayout.getChildAt(0) != null) {
                blp.rightMargin = (w - activityLayout.getChildAt(0).getMeasuredWidth()) / 2;
                blp.topMargin = (h - activityLayout.getChildAt(0).getMeasuredHeight()) / 2;
            }
            close.setLayoutParams(blp);
            close.setBackgroundColor(Color.TRANSPARENT);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    caller.close();
                }
            });
            activityLayout.addView(close);
        } else if (close != null) {
            if (custom_close) {
                close.setVisibility(GONE);
            } else {
                activityLayout.removeView(close);
                close.setVisibility(VISIBLE);
                activityLayout.addView(close);// Re-add to send to top
            }
        }
    }
}