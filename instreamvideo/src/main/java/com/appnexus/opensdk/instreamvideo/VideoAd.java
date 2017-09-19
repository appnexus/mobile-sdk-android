/*
 *    Copyright 2016 APPNEXUS INC
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
package com.appnexus.opensdk.instreamvideo;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Pair;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.appnexus.opensdk.AdSize;
import com.appnexus.opensdk.AdView;
import com.appnexus.opensdk.MediaType;
import com.appnexus.opensdk.ut.UTRequestParameters;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.utils.Clog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class VideoAd implements VideoAdInterface {


    private UTRequestParameters requestParameters;
    private VideoAdPlaybackListener videoPlaybackListener;
    private VideoAdLoadListener adLoadListener;
    private final VideoAdFetcher mVideoAdFetcher;
    boolean isLoading = false;

    boolean validAdExists = false;
    private VideoAdViewDispatcher dispatcher;
    private WeakReference<Context> weakContext;
    private InstreamVideoView videoAdView;
    private BrowserStyle browserStyle;
    boolean doesLoadingInBackground = true;
    private boolean showLoadingIndicator = true;


    public VideoAd(Context context, String placementID) {
        weakContext = new WeakReference<Context>(context);
        requestParameters = new UTRequestParameters(getContext());
        requestParameters.setPlacementID(placementID);
        requestParameters.setMediaType(MediaType.INSTREAM_VIDEO);
        mVideoAdFetcher = new VideoAdFetcher(this);
        // setting the period to -1 disables autorefresh
        mVideoAdFetcher.setPeriod(-1);
        dispatcher = new VideoAdViewDispatcher();
        videoAdView = new InstreamVideoView(getContext());
        this.setAllowedSizes();

    }

    public VideoAd(Context context, String inventoryCode, int memberID) {
        weakContext = new WeakReference<Context>(context);
        requestParameters = new UTRequestParameters(getContext());
        requestParameters.setInventoryCodeAndMemberID(memberID, inventoryCode);
        requestParameters.setMediaType(MediaType.INSTREAM_VIDEO);
        mVideoAdFetcher = new VideoAdFetcher(this);
        // setting the period to -1 disables autorefresh
        mVideoAdFetcher.setPeriod(-1);
        dispatcher = new VideoAdViewDispatcher();
        videoAdView = new InstreamVideoView(getContext());
        this.setAllowedSizes();
    }


    Context getContext() {
        return weakContext.get();
    }

    /**
     * Retrieve the setting that determines whether or not the
     * device's native browser is used instead of the in-app
     * browser when the user clicks an ad.
     *
     * @return true if the device's native browser will be used; false otherwise.
     */
    public boolean getOpensNativeBrowser() {
        Clog.d(Clog.videoLogTag, Clog.getString(
                R.string.set_placement_id, requestParameters.getOpensNativeBrowser()));
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
     * @param opensNativeBrowser
     */
    public void setOpensNativeBrowser(boolean opensNativeBrowser) {
        Clog.d(Clog.videoLogTag, Clog.getString(
                R.string.set_opens_native_browser, opensNativeBrowser));
        requestParameters.setOpensNativeBrowser(opensNativeBrowser);
    }

    /**
     * Sets the placement id of the VideoAd. The placement ID
     * identifies the location in your application where ads will
     * be shown.  You must have a valid, active placement ID to
     * monetize your application.
     *
     * @param placementID The placement ID to use.
     */
    public void setPlacementID(String placementID) {
        Clog.d(Clog.videoLogTag, Clog.getString(
                R.string.set_placement_id, placementID));
        requestParameters.setPlacementID(placementID);
    }

    /**
     * Retrieve the placement id for ad request.
     *
     * @return The Placement ID
     */
    public String getPlacementID() {
        Clog.d(Clog.videoLogTag, Clog.getString(
                R.string.get_placement_id, requestParameters.getPlacementID()));
        return requestParameters.getPlacementID();
    }

    /**
     * Sets the inventory code and member id of this Video ad request. The
     * inventory code provides a more human readable way to identify the location
     * in your application where ads will be shown. Member id is required to for
     * using this feature. If both inventory code and placement id are presented,
     * inventory code will be used instead of placement id on the ad request.
     *
     * @param memberID      The member id that this Video ad belongs to.
     * @param inventoryCode The inventory code of this Video ad.
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
     * Set user's gender for targeting
     *
     * @param gender User's gender
     */
    public void setGender(AdView.GENDER gender) {
        Clog.d(Clog.videoLogTag, Clog.getString(
                R.string.set_gender, gender.toString()));
        requestParameters.setGender(gender);
    }

    /**
     * Get the user's gender
     *
     * @return User's gender
     */
    public AdView.GENDER getGender() {
        Clog.d(Clog.videoLogTag, Clog.getString(
                R.string.get_gender, requestParameters.getGender().toString()));
        return requestParameters.getGender();
    }

    /**
     * Set the age or age range of the user
     *
     * @param age User's age or age range
     */
    public void setAge(String age) {
        requestParameters.setAge(age);
    }

    /**
     * Get the age or age range for the ad request
     *
     * @return age
     */
    public String getAge() {
        return requestParameters.getAge();
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
     * Register a listener for ad success/fail to load notification events
     *
     * @param adLoadListener The Listener to register
     */
    public void setAdLoadListener(VideoAdLoadListener adLoadListener) {
        this.adLoadListener = adLoadListener;
    }

    /**
     * Get the listener that listens the state of the request
     *
     * @return The registered ad-load listener
     */
    public VideoAdLoadListener getAdLoadListener() {
        return this.adLoadListener;
    }


    /**
     * Register a videoPlaybackListener for Video Events callback like Quartiles/Play/Pause/Completed.
     *
     * @param videoPlaybackListener The VideoAdPlaybackListener listener to register
     */
    public void setVideoPlaybackListener(VideoAdPlaybackListener videoPlaybackListener) {
        this.videoPlaybackListener = videoPlaybackListener;
    }

    /**
     * Get the listener that listens the state of the video
     *
     * @return The registered video event listener
     */
    public VideoAdPlaybackListener getVideoPlaybackListener() {
        return this.videoPlaybackListener;
    }


    UTRequestParameters getRequestParameters() {
        return requestParameters;
    }


    public MediaType getMediaType() {
        return requestParameters.getMediaType();
    }


    public boolean isReadyToStart() {
        return requestParameters.isReadyForRequest();
    }

    /**
     * Call this to request a VideoAd for parameters described by this object.
     */

    public boolean loadAd() {
        if (isLoading) {
            Clog.e(Clog.videoLogTag, "Still loading last Video ad , won't load a new ad");
            return false;
        }

        // Before calling loadAd we make sure that the views are not attached to any parents because of previous loadAd call.
        videoAdView.clearSelf();

        if (requestParameters.isReadyForRequest()) {
            mVideoAdFetcher.stop();
            mVideoAdFetcher.clearDurations();
            mVideoAdFetcher.start();
            isLoading = true;
            return true;
        }
        return false;
    }

    /**
     * Call this to remove the video ad view from the container.
     */

    public void removeAd() {
        reset();
    }

    protected VideoAdDispatcher getAdDispatcher() {
        return dispatcher;
    }


    protected void setAllowedSizes() {
        Clog.d(Clog.videoLogTag,
                Clog.getString(com.appnexus.opensdk.R.string.set_allowed_sizes));
        AdSize oneByOneSize = new AdSize(1, 1);
        ArrayList<AdSize> allowed_sizes = new ArrayList<AdSize>();
        allowed_sizes.add(oneByOneSize);
        requestParameters.setSizes(allowed_sizes);
        requestParameters.setPrimarySize(oneByOneSize);
        requestParameters.setAllowSmallerSizes(false);
    }

    /**
     * Checks the queue to see if there is a valid video ad available.
     *
     * @return <code>true</code> if there is a valid ad available in
     * the queue, <code>false</code> otherwise.
     */
    public boolean isReady() {
        return validAdExists;
    }

    /**
     * Internal class to post process VideoAd events
     */
    class VideoAdViewDispatcher implements VideoAdDispatcher {


        @Override
        public void onAdLoaded() {
            isLoading = false;
            validAdExists = true;
            if (adLoadListener != null) {
                adLoadListener.onAdLoaded(VideoAd.this);
            }
        }

        @Override
        public void onAdFailed(ResultCode errorCode) {
            isLoading = false;
            validAdExists = false;
            if (adLoadListener != null) {
                adLoadListener.onAdRequestFailed(VideoAd.this, errorCode);
            }
        }

        @Override
        public void onAdClicked() {
            if (videoPlaybackListener != null) {
                videoPlaybackListener.onAdClicked(VideoAd.this);
            }

        }

        @Override
        public void onVideoSkip() {
            reset();
            validAdExists = false;
            if (videoPlaybackListener != null) {
                videoPlaybackListener.onAdCompleted(VideoAd.this, VideoAdPlaybackListener.PlaybackCompletionState.SKIPPED);
            }
        }

        @Override
        public void onQuartile(Quartile quartile) {
            if (videoPlaybackListener != null) {
                videoPlaybackListener.onQuartile(VideoAd.this, quartile);
            }
        }

        @Override
        public void onAdCompleted() {
            reset();
            validAdExists = false;
            if (videoPlaybackListener != null) {
                videoPlaybackListener.onAdCompleted(VideoAd.this, VideoAdPlaybackListener.PlaybackCompletionState.COMPLETED);
            }
        }

        @Override
        public void isAudioMute(boolean isMute) {
            if (videoPlaybackListener != null) {
                videoPlaybackListener.onAdMuted(VideoAd.this, isMute);
            }
        }

        @Override
        public void onPlaybackError() {
            reset();
            validAdExists = false;
            if (videoPlaybackListener != null) {
                videoPlaybackListener.onAdCompleted(VideoAd.this, VideoAdPlaybackListener.PlaybackCompletionState.ERROR);
            }
        }
    }


    public void activityOnDestroy() {
        if (this.videoAdView != null) {
            this.videoAdView.onDestroy();
        }
    }


    public void activityOnPause() {
        if (this.videoAdView != null) {
            this.videoAdView.onPause();
        }
    }


    public void activityOnResume() {
        if (this.videoAdView != null) {
            this.videoAdView.onResume();
        }
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


    BrowserStyle getBrowserStyle() {
        return browserStyle;
    }

    protected void setBrowserStyle(BrowserStyle browserStyle) {
        this.browserStyle = browserStyle;
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
     * Get whether or not the Video Ad should show the loading indicator
     * after being pressed, but before able to launch the browser.
     * <p/>
     * Default is true
     *
     * @return true if the loading indicator will be displayed, else false
     */
    public boolean getShowLoadingIndicator() {
        return showLoadingIndicator;
    }

    /**
     * Set whether or not the Video Ad should show the loading indicator
     * after being pressed, but before able to launch the browser.
     * <p/>
     * Default is true
     *
     * @param show True if you desire the loading indicator to be displayed, else set to false
     */
    public void setShowLoadingIndicator(boolean show) {
        showLoadingIndicator = show;
    }


    InstreamVideoView getVideoAdView() {
        return videoAdView;
    }


    /**
     * Call this by passing a Container(FrameLayout) when you want the VideoAd to Play.
     *
     * @param layout The layout in which the Video Ad will be attached to. Pass a FrameLayout.
     */
    public void playAd(FrameLayout layout) {
        if (videoPlaybackListener == null) {
            // error message
            Clog.e(Clog.videoLogTag, "No VideoAdPlaybackListener set. A valid PlaybackListener should be set using setVideoPlaybackListener(videoPlaybackListener) before calling playAd()");
            return;
        }
        if (videoAdView == null) {
            // error message
            Clog.e(Clog.videoLogTag, "Ad View is null");
            videoPlaybackListener.onAdCompleted(VideoAd.this, VideoAdPlaybackListener.PlaybackCompletionState.ERROR);
        } else {
            videoAdView.playAd(layout);
        }
    }

    /**
     * Call this by passing a Container(RelativeLayout) when you want the VideoAd to Play.
     *
     * @param layout The layout in which the Video Ad will be attached to. Pass a RelativeLayout.
     */
    public void playAd(RelativeLayout layout) {
        if (videoPlaybackListener == null) {
            // error message
            Clog.e(Clog.videoLogTag, "No VideoAdPlaybackListener set. A valid PlaybackListener should be set using setVideoPlaybackListener(videoPlaybackListener) before calling playAd()");
            return;
        }
        if (videoAdView == null) {
            // error message
            Clog.e(Clog.videoLogTag, "Ad View is null");
            videoPlaybackListener.onAdCompleted(VideoAd.this, VideoAdPlaybackListener.PlaybackCompletionState.ERROR);
        } else {
            videoAdView.playAd(layout);
        }
    }

    void reset() {
        validAdExists = false;
        if (videoAdView != null) {
            videoAdView.clearSelf();
        }
    }
}
