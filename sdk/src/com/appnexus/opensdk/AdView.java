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
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
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
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.StringUtil;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * The parent class of {@link InterstitialAdView} and {@link
 * BannerAdView}.  This may not be instantiated directly.  Its public
 * methods are accessed through one of its sub classes.
 *
 *
 */
public abstract class AdView extends FrameLayout {

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
    private Location location = null;
	boolean mraid_changing_size_or_visibility = false;
	AdListener adListener;
	private BrowserStyle browserStyle;
	private LinkedList<MediatedAd> mediatedAds;
	final Handler handler = new Handler(Looper.getMainLooper());
	private Displayable lastDisplayable;
	private AdListenerDispatch dispatcher;
    boolean loadedOffscreen = false;

	/**
	 * Begin Construction
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
		dispatcher = new AdView.AdListenerDispatch(handler);

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

	/**
	 * The view layout
	 */
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (mraid_changing_size_or_visibility) {
			mraid_changing_size_or_visibility = false;
			return;
		}
		if (!measured || changed) {
			// Convert to dips
			float density = getContext().getResources().getDisplayMetrics().density;
			measuredWidth = (int) ((right - left) / density + 0.5f);
			measuredHeight = (int) ((bottom - top) / density + 0.5f);
			if ((measuredHeight < height || measuredWidth < width)
					&& measuredHeight > 0 && measuredWidth > 0) {
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
			if (!measured && !loadedOffscreen) {
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
		if (StringUtil.isEmpty(placementID)) {
			Clog.e(Clog.baseLogTag, Clog.getString(R.string.no_placement_id));
			return false;
		}
		return true;
	}

	/**
	 * Loads a new ad, if the ad space is visible.  You should
	 * have called setPlacementID before invoking this method.
	 *
	 * @return true means the ad will begin loading; false otherwise.
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

    protected void loadAdOffscreen() {
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
	 *
	 * @return true means the ad will begin loading; false otherwise.
	 */
	public boolean loadAd(String placementID) {
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
			if (lastDisplayable instanceof MediatedAdViewController) {
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
	 *
	 * @return The current placement id.
	 */
	public String getPlacementID() {
		Clog.d(Clog.publicFunctionsLogTag,
				Clog.getString(R.string.get_placement_id, placementID));
		return placementID;
	}

	/**
	 * Sets the placement id of the AdView.  The placement ID
	 * identifies the location in your application where ads will
	 * be shown.  You must have a valid, active placement ID to
	 * monetize your application.
	 *
	 * @param placementID
	 *            The placement ID to use.
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

	int getContainerWidth() {
		return measuredWidth;
	}

	int getContainerHeight() {
		return measuredHeight;
	}

    // Used only by MRAID
    ImageButton close_button;
    View oldContent;
    Activity unexpandedActivity;
    protected void close(int w, int h, MRAIDImplementation caller){
        //For closing
        if(oldContent!= null && oldContent.getParent()!=null){
            ((ViewGroup)oldContent.getParent()).removeAllViewsInLayout();
        }
        if(unexpandedActivity!=null){
            unexpandedActivity.setContentView(oldContent);
        }
        if (caller.owner.isFullScreen) {
            ((FrameLayout)caller.owner.getParent()).removeAllViews();
            this.addView(caller.owner);

        }
        expand(w, h, true, null);
    }
    protected void expand(int w, int h, boolean custom_close,
                          final MRAIDImplementation caller) {
        mraid_changing_size_or_visibility = true;

        if (getLayoutParams() != null) {
            if (getLayoutParams().width > 0)
                getLayoutParams().width = w;
            if (getLayoutParams().height > 0)
                getLayoutParams().height = h;
        }
        if (!custom_close) {
            // Add a stock close_button button to the top right corner
            close_button = new ImageButton(this.getContext());
            close_button.setImageDrawable(getResources().getDrawable(
                    android.R.drawable.ic_menu_close_clear_cancel));
            FrameLayout.LayoutParams blp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.RIGHT
                    | Gravity.TOP);
            if(caller.owner.isFullScreen){
                //Make a new framelayout to contain webview and button
                FrameLayout fslayout = new FrameLayout(this.getContext());
                if(this.getChildCount()>0){
                    this.removeAllViews();
                }

                caller.owner.removeFromParent();
                fslayout.addView(caller.owner);
                fslayout.addView(close_button);
                if (this instanceof InterstitialAdView) {
                    unexpandedActivity = AdActivity.getCurrent_ad_activity();
                } else {
                    unexpandedActivity = (Activity) this.getContext();
                }
                oldContent= ((ViewGroup)unexpandedActivity.findViewById(android.R.id.content)).getChildAt(0);
                unexpandedActivity.setContentView(fslayout);
            }else{

                if(getChildAt(0)!=null){
                    blp.rightMargin = (this.getMeasuredWidth() - this.getChildAt(0)
                            .getMeasuredWidth()) / 2;
                }
                this.addView(close_button);
            }
            close_button.setLayoutParams(blp);
            close_button.setBackgroundColor(Color.TRANSPARENT);
            close_button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    caller.close();

                }
            });
        } else {
            if(close_button!=null){
                close_button.setVisibility(GONE);
            }

            //Expand and use custom close.
            if(caller!=null && caller.owner.isFullScreen){
                //Make a new framelayout to contain webview and button
                FrameLayout fslayout = new FrameLayout(this.getContext());

                if (this instanceof InterstitialAdView) {
                    AdActivity aa = AdActivity.getCurrent_ad_activity();
                    aa.layout.removeAllViews();
                } else {
                    if(this.getChildCount()>0){
                        this.removeAllViews();
                    }
                }
                fslayout.addView(caller.owner);
                if (this instanceof InterstitialAdView) {
                    unexpandedActivity = AdActivity.getCurrent_ad_activity();
                } else {
                    unexpandedActivity = (Activity) this.getContext();
                }
                oldContent= ((ViewGroup)unexpandedActivity.findViewById(android.R.id.content)).getChildAt(0);
                unexpandedActivity.setContentView(fslayout);
            }
        }
    }


    int fiftyPxAsDP=0;
    public void resize(int w, int h, int offset_x, int offset_y, MRAIDImplementation.CUSTOM_CLOSE_POSITION custom_close_position, boolean allow_offscrean,
                       final MRAIDImplementation caller) {
        mraid_changing_size_or_visibility = true;
        if (getLayoutParams() != null) {
            if (getLayoutParams().width > 0)
                getLayoutParams().width = w;
            if (getLayoutParams().height > 0)
                getLayoutParams().height = h;
        }
        // Add a stock close_button button to the top right corner
        if(close_button!=null && ((ViewGroup)close_button.getParent())!=null){
            ((ViewGroup)close_button.getParent()).removeView(close_button);
            close_button.setVisibility(GONE);
        }
        close_button = new ImageButton(this.getContext()){

            @Override
            public void onLayout(boolean changed, int left, int top, int right, int bottom){
                int i[] = new int[2];
                this.getLocationOnScreen(i);
                float scale = caller.owner.getContext().getResources().getDisplayMetrics().density;
                //Determine screen width and height
                Activity a = null;
                try{
                    a=(Activity) caller.owner.getContext();
                }catch(ClassCastException e){
                    return;
                }
                Point screen_size = new Point();
                a.getWindowManager().getDefaultDisplay().getSize(screen_size);

                float fifty_dp_as_px = 50*scale;
                if(i[0]<0 || i[0]>screen_size.x-fifty_dp_as_px ||
                   i[1]<0 || i[1]>screen_size.y-fifty_dp_as_px){
                    //Button is off screen, and must be relocated on screen

                    //Ensure there is a minimum 50x50 area to place the close button
                    if(caller.owner.getLeft() > 0 && caller.owner.getRight() > screen_size.x-fifty_dp_as_px &&
                       caller.owner.getTop() > 0 && caller.owner.getBottom() > screen_size.y-fifty_dp_as_px){
                        //Run like a chicken with no head
                        return; //Here be dragons
                    }

                    //Determine which edges particularly are clipped
                    boolean leftClipped=false;
                    boolean rightClipped=false;
                    boolean topClipped=false;
                    boolean bottomClipped=false;

                    if(caller.owner.getLeft()<0){
                        leftClipped=true;
                    }
                    if(caller.owner.getRight()>screen_size.x){
                        rightClipped=true;
                    }
                    if(caller.owner.getTop()<0){
                        topClipped=true;
                    }
                    if(caller.owner.getBottom()>screen_size.y){
                        bottomClipped=true;
                    }

                    //Just return if there's no clipping
                    if(!(leftClipped || rightClipped || topClipped || bottomClipped)){
                        return;
                    }

                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(this.getLayoutParams());

                    if(bottomClipped && rightClipped && !topClipped && !leftClipped){
                        //Top-left
                        lp.gravity = Gravity.TOP | Gravity.LEFT;
                    }else if(bottomClipped && leftClipped && !topClipped && !rightClipped){
                        //Top-right
                        lp.gravity = Gravity.TOP | Gravity.RIGHT;
                    }else if(topClipped && rightClipped && !bottomClipped && !leftClipped){
                        //Bottom-left
                        lp.gravity = Gravity.BOTTOM | Gravity.LEFT;
                    }else if(topClipped && leftClipped && !bottomClipped && !rightClipped){
                        //Bottom-right
                        lp.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                    }else if(!topClipped){
                        //Top center
                        lp.gravity = Gravity.TOP | Gravity.CENTER;
                    }else if(!bottomClipped){
                        //Bottom center
                        lp.gravity = Gravity.BOTTOM | Gravity.CENTER;
                    }else if(!rightClipped){
                        //Right center
                        lp.gravity = Gravity.RIGHT | Gravity.CENTER;
                    }else if(!leftClipped){
                        //Left center
                        lp.gravity = Gravity.LEFT | Gravity.CENTER;
                    }else{
                        lp.gravity = Gravity.CENTER;
                    }

                    this.setLayoutParams(lp);
                    close_button.setImageDrawable(getResources().getDrawable(
                            android.R.drawable.ic_menu_close_clear_cancel));
                }
            }
        };

        int grav = Gravity.RIGHT | Gravity.TOP;
        switch (custom_close_position) {
            case bottom_center:
                grav = Gravity.BOTTOM | Gravity.CENTER;
                break;
            case bottom_left:
                grav = Gravity.BOTTOM | Gravity.LEFT;
                break;
            case bottom_right:
                grav = Gravity.BOTTOM | Gravity.RIGHT;
                break;
            case center:
                grav = Gravity.CENTER;
                break;
            case top_center:
                grav = Gravity.TOP | Gravity.CENTER;
                break;
            case top_left:
                grav = Gravity.TOP | Gravity.LEFT;
                break;
            case top_right:
                grav = Gravity.TOP | Gravity.RIGHT;
                break;

        }

        if(!(fiftyPxAsDP>0)){
            final float scale = caller.owner.getContext().getResources().getDisplayMetrics().density;
            fiftyPxAsDP = (int)(50*scale);
        }

        FrameLayout.LayoutParams blp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT, grav);

        close_button.setLayoutParams(blp);
        close_button.setBackgroundColor(Color.TRANSPARENT);
        close_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                caller.close();

            }
        });
        if(this instanceof BannerAdView){
            this.addView(close_button);
        }else{
            AdActivity.getCurrent_ad_activity().layout.addView(close_button);
        }
    }
	/**
	 *
	 * @return true if the AdView is a {@link BannerAdView}.
	 */
	abstract boolean isBanner();

	/**
	 *
	 * @return true if the AdView is an {@link InterstitialAdView}.
	 */
	abstract boolean isInterstitial();

	/**
	 * Sets the currently installed listener that the SDK will send events to.
	 *
	 * @param listener
	 *            The {@link AdListener} object to use.
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

	void fail() {
		this.getAdDispatcher().onAdFailed(true);
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
				R.string.get_opens_native_browser, opensNativeBrowser));
		return opensNativeBrowser;
	}

	/**
	 * Set this to true to disable the in-app browser.  This will
	 * cause URLs to open in a native browser such as Chrome so
	 * that when the user clicks on an ad, your app will be paused
	 * and the native browser will open.  Set this to false to
	 * enable the in-app browser instead (a lightweight browser
	 * that runs within your app).  The default value is false.
	 *
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
	 * Retrieve the current PSA setting.  PSAs (Public Service
	 * Announcements) are ads for various causes or nonprofit
	 * organizations that can be served if there are no ads
	 * available.  You can turn this on and off with
	 * setShouldServePSAs.
	 *
	 * @return Whether this placement accepts PSAs if no ad is served.
	 */
	public boolean getShouldServePSAs() {
		return shouldServePSAs;
	}

	/**
	 * Allows overriding the platform behavior in the case there is no ad
	 * currently available. If set to true the platform will retrieve and
	 * display a PSA (Public Service Announcement) . Set the value to false it
	 * will return no ad.
	 *
	 * @param shouldServePSAs
	 *            Whether this placement is willing to accept a PSA if no other ad is available.
	 */
	public void setShouldServePSAs(boolean shouldServePSAs) {
		this.shouldServePSAs = shouldServePSAs;
	}

	/**
	 * Retrieve the reserve price.  The reserve price is the
	 * minimum price you will accept in order to show an ad.  A
	 * value of 0 indicates that there is no minimum.
	 *
	 * @return The reserve price.  A value of 0 indicates that no reserve is set.
	 */
	public float getReserve() {
		return reserve;
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
		this.reserve = reserve;
	}

	/**
	 * Retrieve the current user's age.  Note that this is a
	 * string as it may come in one of several formats: age, birth
	 * year, or age range.  The default value is an empty string.
	 *
	 * @return The current user's age.
	 */
	public String getAge() {
		return age;
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
		this.age = age;
	}

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    /**
	 *
	 * The user's gender.
	 *
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
		return gender;
	}

	/**
	 * Set the user's gender.  This should be set if the user's
	 * gender is known, as it can help make buying the ad space
	 * more attractive to advertisers.  The default value is
	 * UNKNOWN.
	 *
	 * @param gender
	 *            The user's gender.
	 */
	public void setGender(GENDER gender) {
		this.gender = gender;
	}

	/**
	 * Add a custom keyword to the request URL for the ad.  This
	 * is used to set custom targeting parameters within the
	 * AppNexus platform.  You will be given the keys and values
	 * to use by your AppNexus account representative or your ad
	 * network.
	 *
	 * @param key
	 *            The key to add; this cannot be null or empty.
	 * @param value
	 *            The value to add; this cannot be null or empty.
	 */
	public void addCustomKeywords(String key, String value) {
		if (StringUtil.isEmpty(key) || (value == null)) {
			return;
		}
		customKeywords.add(new Pair<String, String>(key, value));
	}

	/**
	 *
	 * Remove a custom keyword from the request URL for the ad.
	 * Use this to remove a keyword previously set using
	 * addCustomKeywords.
	 *
	 * @param key
	 *            The key to remove; this cannot be null or empty.
	 */
	public void removeCustomKeyword(String key) {
		if (StringUtil.isEmpty(key))
			return;

		for (int i = 0; i < customKeywords.size(); i++) {
			Pair<String, String> pair = customKeywords.get(i);
			if (pair.first.equals(key)) {
				customKeywords.remove(i);
				break;
			}
		}
	}

    protected TargetingParameters getTargetingParameters(){
        return new TargetingParameters(getAge(), getGender(), getCustomKeywords(), getLocation());
    }

	/**
	 * Retrieve the array of custom keywords associated with the
	 * current AdView.
	 *
	 * @return The current list of key-value pairs of custom
	 * keywords.
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

	/**
	 * Private class to bridge events from mediation to the user
	 * AdListener class.
	 *
	 */
	private class AdListenerDispatch implements AdViewListener {

		Handler handler;

		public AdListenerDispatch(Handler h) {
			handler = h;
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
			// wait until mediation waterfall is complete before calling
			// adListener
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
	}

	AdViewListener getAdDispatcher() {
		return this.dispatcher;
	}

	LinkedList<MediatedAd> getMediatedAds() {
		return mediatedAds;
	}

	void setMediatedAds(LinkedList<MediatedAd> mediatedAds) {
		this.mediatedAds = mediatedAds;
	}

	// returns the first mediated ad if available
	MediatedAd popMediatedAd() {
		return mediatedAds != null ? mediatedAds.removeFirst() : null;
	}


}
