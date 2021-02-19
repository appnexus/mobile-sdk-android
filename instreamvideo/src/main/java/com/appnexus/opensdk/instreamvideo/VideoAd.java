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
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.appnexus.opensdk.ANAdResponseInfo;
import com.appnexus.opensdk.ANClickThroughAction;
import com.appnexus.opensdk.ANMultiAdRequest;
import com.appnexus.opensdk.Ad;
import com.appnexus.opensdk.AdFetcher;
import com.appnexus.opensdk.AdSize;
import com.appnexus.opensdk.AdType;
import com.appnexus.opensdk.AdView;
import com.appnexus.opensdk.AdViewRequestManager;
import com.appnexus.opensdk.MediaType;
import com.appnexus.opensdk.MultiAd;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.SDKSettings;
import com.appnexus.opensdk.VideoOrientation;
import com.appnexus.opensdk.tasksmanager.TasksManager;
import com.appnexus.opensdk.ut.UTAdRequester;
import com.appnexus.opensdk.ut.UTConstants;
import com.appnexus.opensdk.ut.UTRequestParameters;
import com.appnexus.opensdk.ut.adresponse.BaseAdResponse;
import com.appnexus.opensdk.ut.adresponse.RTBVASTAdResponse;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.viewability.ANOmidViewabilty;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class VideoAd implements Ad, MultiAd {


    private UTRequestParameters requestParameters;
    private VideoAdPlaybackListener videoPlaybackListener;
    private VideoAdLoadListener adLoadListener;
    private final AdFetcher mVideoAdFetcher;
    boolean isLoading = false;

    boolean validAdExists = false;
    private VideoAdViewDispatcher dispatcher;
    private WeakReference<Context> weakContext;
    private InstreamVideoView videoAdView;
    boolean doesLoadingInBackground = true;
    private boolean showLoadingIndicator = true;
    private ANAdResponseInfo adResponseInfo;

    public VideoAd(Context context, String placementID) {
        weakContext = new WeakReference<Context>(context);
        requestParameters = new UTRequestParameters(getContext());
        requestParameters.setPlacementID(placementID);
        requestParameters.setMediaType(MediaType.INSTREAM_VIDEO);
        mVideoAdFetcher = new AdFetcher(this);
        // setting the period to -1 disables autorefresh
        mVideoAdFetcher.setPeriod(-1);
        dispatcher = new VideoAdViewDispatcher();
        videoAdView = new InstreamVideoView(getContext());
        this.setAllowedSizes();
        ANOmidViewabilty.getInstance().activateOmidAndCreatePartner(context.getApplicationContext());

    }

    public VideoAd(Context context, String inventoryCode, int memberID) {
        weakContext = new WeakReference<Context>(context);
        requestParameters = new UTRequestParameters(getContext());
        requestParameters.setInventoryCodeAndMemberID(memberID, inventoryCode);
        requestParameters.setMediaType(MediaType.INSTREAM_VIDEO);
        mVideoAdFetcher = new AdFetcher(this);
        // setting the period to -1 disables autorefresh
        mVideoAdFetcher.setPeriod(-1);
        dispatcher = new VideoAdViewDispatcher();
        videoAdView = new InstreamVideoView(getContext());
        this.setAllowedSizes();
        ANOmidViewabilty.getInstance().activateOmidAndCreatePartner(context.getApplicationContext());
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
     * @deprecated Use getClickThroughAction instead
     * Refer {@link ANClickThroughAction}
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
     * @deprecated Use setClickThroughAction instead
     * Refer {@link ANClickThroughAction}
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
     * Retrieve the Publisher ID.
     *
     * @return the Publisher id that this NativeAdRequest belongs to.
     */
    public int getPublisherId() {
        return requestParameters.getPublisherId();
    }

    /**
     * Retrieve the Publisher ID.
     *
     * @@param publisherId the Publisher id that this NativeAdRequest belongs to.
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
     * use this to get the min duration set for the video tag
     *
     * @return min duration set in seconds
     */

    public int getAdMinDuration() {
        return requestParameters.getVideoAdMinDuration();
    }

    /**
     * use this to set the min duration for the video tag
     *
     * @param minDuration the min duration value in seconds
     */

    public void setAdMinDuration(int minDuration) {
        requestParameters.setVideoAdMinDuration(minDuration);
    }

    /**
     * use this to get the max duration set for the video tag
     *
     * @return max duration value in seconds
     */
    public int getAdMaxDuration() {
        return requestParameters.getVideoAdMaxDuration();
    }

    /**
     * use this to set the max duration for the video tag
     *
     * @param maxDuration the max duration value in seconds
     */
    public void setAdMaxDuration(int maxDuration) {
        requestParameters.setVideoAdMaxDuration(maxDuration);
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


    @Override
    public UTRequestParameters getRequestParameters() {
        return requestParameters;
    }


    @Override
    public MediaType getMediaType() {
        return requestParameters.getMediaType();
    }


    @Override
    public boolean isReadyToStart() {
        return requestParameters.isReadyForRequest();
    }

    /**
     * Call this to request a VideoAd for parameters described by this object.
     */
    @Override
    public boolean loadAd() {
        if (isLoading) {
            Clog.e(Clog.videoLogTag, "Still loading last Video ad , won't load a new ad");
            return false;
        }

        // Before calling loadAd we make sure that the views are not attached to any parents because of previous loadAd call.
        init();

        if (requestParameters.isReadyForRequest()) {
            mVideoAdFetcher.stop();
            mVideoAdFetcher.clearDurations();
            mVideoAdFetcher.start();
            isLoading = true;
            return true;
        }
        return false;
    }


    protected void loadAdFromVAST(String VASTXML, int width, int height) {
        // load an ad directly from VASTXML
        VideoWebView output = new VideoWebView(this.getContext(), this, null);
        RTBVASTAdResponse response = new RTBVASTAdResponse(width, height, AdType.VIDEO.toString(), null, null, getAdResponseInfo());
        response.setAdContent(VASTXML);
        response.setContentSource(UTConstants.RTB);
        response.addToExtras(UTConstants.EXTRAS_KEY_MRAID, true);
        getVideoAdView().setVideoWebView(output);
        output.loadAd(response);
    }

    /**
     * Call this to remove the video ad view from the container.
     */

    public void removeAd() {
        reset();
    }

    @Override
    public VideoAdDispatcher getAdDispatcher() {
        return dispatcher;
    }

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
     * Returns the ANClickThroughAction that is used for this VideoAd.
     *
     * @return {@link ANClickThroughAction}
     */
    public ANClickThroughAction getClickThroughAction() {
        return requestParameters.getClickThroughAction();
    }


    /**
     * Determines what action to take when the user clicks on an ad.
     * If set to ANClickThroughAction.OPEN_DEVICE_BROWSER/ANClickThroughAction.OPEN_SDK_BROWSER then,
     * VideoAdPlaybackListener.onAdClicked(VideoAd videoAd) will be triggered and corresponding browser will load the click url.
     * If set to ANClickThroughAction.RETURN_URL then,
     * VideoAdPlaybackListener.onAdClicked(VideoAd videoAd, String clickUrl) will be triggered with clickUrl as its argument.
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
     * Internal class to post process VideoAd events
     */
    class VideoAdViewDispatcher implements VideoAdDispatcher {


        @Override
        public void onAdLoaded() {
            if (SDKSettings.isBackgroundThreadingEnabled()) {
                TasksManager.getInstance().executeOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        processAdLoaded();
                    }
                });
            } else {
                processAdLoaded();
            }
        }

        private void processAdLoaded() {
            isLoading = false;
            validAdExists = true;
            addFriendlyObstructions();
            if (adLoadListener != null) {
                adLoadListener.onAdLoaded(VideoAd.this);
            }
        }

        @Override
        public void onAdFailed(final ResultCode errorCode, final ANAdResponseInfo adResponseInfo) {
            if (SDKSettings.isBackgroundThreadingEnabled()) {
                TasksManager.getInstance().executeOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        processAdFailed(errorCode, adResponseInfo);
                    }
                });
            } else {
                processAdFailed(errorCode, adResponseInfo);
            }
        }

        private void processAdFailed(ResultCode errorCode, ANAdResponseInfo adResponseInfo) {
            isLoading = false;
            validAdExists = false;
            setAdResponseInfo(adResponseInfo);
            if (adLoadListener != null) {
                adLoadListener.onAdRequestFailed(VideoAd.this, errorCode);
            }
        }

        @Override
        public void onAdPlaying() {
            if (videoPlaybackListener != null) {
                videoPlaybackListener.onAdPlaying(VideoAd.this);
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

        @Override
        public void onAdClicked(String clickUrl) {
            if (videoPlaybackListener != null) {
                videoPlaybackListener.onAdClicked(VideoAd.this, clickUrl);
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


    public void pauseAd() {
        if (videoAdView != null) {
            videoAdView.pauseAd();
        }
    }


    public void resumeAd() {
        if (videoAdView != null) {
            videoAdView.resumeAd();
        }
    }

    void reset() {
        validAdExists = false;
        if (videoAdView != null) {
            videoAdView.clearSelf();
        }
    }

    public String getCreativeURL() {
        if (videoAdView != null) {
            return videoAdView.getCreativeURL();
        }

        return "";
    }

    public String getVastURL() {
        if (videoAdView != null) {
            return videoAdView.getVastURL();
        }

        return "";
    }

    public int getVideoAdDuration() {
        if (videoAdView != null) {
            return videoAdView.getVideoAdDuration();
        }

        return 0;
    }

    @Deprecated
    /**
     * Retrieve the Creative Id  of the creative .
     *
     * @return the creativeId
     * @deprecated use ({@link ANAdResponseInfo}.getCreativeId)
     */
    public String getCreativeId() {
        if (getAdResponseInfo() != null) {
            return getAdResponseInfo().getCreativeId();
        }
        return "";
    }

    /**
     * Set AppNexus CreativeId that you want to display on this AdUnit for debugging/testing purpose.
     *
     * @param forceCreativeId of the creative.
     */
    public void setForceCreativeId(int forceCreativeId) {
        requestParameters.setForceCreativeId(forceCreativeId);
    }

    public String getVastXML() {
        if (videoAdView != null) {
            return videoAdView.getVastXML();
        }

        return "";
    }

    /**
     * Get the Orientation of the Video rendered using the BannerAdView
     *
     * @return Default VideoOrientation value UNKNOWN, which indicates that aspectRatio can't be retrieved for this video.
     */
    public VideoOrientation getVideoOrientation() {
        return videoAdView.getVideoOrientation();
    }

    /**
     * Asynchronously calls the VideoAd and gets the current ad play elapsed time in milliseconds.
     * \resultCallback\ will be invoked with the result.
     * Works only on KITKAT and above.
     *
     * @param resultCallback A callback to be invoked when the execution is complete
     *                       completes with the result of the execution (if any).
     *                       May be null or empty string.
     */
    public void getAdPlayElapsedTime(ResultCallback resultCallback) {
        if (videoAdView != null) {
            videoAdView.getAdPlayElapsedTime(resultCallback);
        }
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

    @Override
    public void initiateVastAdView(final BaseAdResponse response, final AdViewRequestManager adViewRequestManager) {
        if (SDKSettings.isBackgroundThreadingEnabled()) {
            TasksManager.getInstance().executeOnMainThread(new Runnable() {
                @Override
                public void run() {
                    initWebView(response, adViewRequestManager);
                }
            });
        } else {
            initWebView(response, adViewRequestManager);
        }

    }

    private void initWebView(BaseAdResponse response, AdViewRequestManager adViewRequestManager) {
        Clog.d(Clog.videoLogTag, "Creating WebView for::" + response.getContentSource());
        setAdResponseInfo(response.getAdResponseInfo());
        VideoWebView adVideoView = new VideoWebView(getContext(), this, adViewRequestManager);
        getVideoAdView().setVideoWebView(adVideoView);
        adVideoView.loadAd(response);
    }

    @Override
    public void setRequestManager(UTAdRequester requester) {
        mVideoAdFetcher.setRequestManager(requester);
    }

    @Override
    public void init() {
        if (videoAdView != null) {
            videoAdView.clearSelf();
        }
        adResponseInfo = null;
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
        if (videoAdView != null) {
            videoAdView.addFriendlyObstruction(view);
        }
    }

    /**
     * For removing Friendly Obstruction View
     *
     * @param view to be removed
     */
    public void removeFriendlyObstruction(View view) {
        if (videoAdView != null) {
            videoAdView.removeFriendlyObstruction(view);
        }
    }

    /**
     * For clearing the Friendly Obstruction Views
     */
    public void removeAllFriendlyObstructions() {
        if (videoAdView != null) {
            videoAdView.removeAllFriendlyObstructions();
        }
    }

    protected ArrayList<WeakReference<View>> getFriendlyObstructionList() {
        if (videoAdView != null) {
            return videoAdView.getFriendlyObstructionList();
        }
        return null;
    }

    private void addFriendlyObstructions() {
        if (getFriendlyObstructionList() == null || videoAdView == null) {
            return;
        }
        for (WeakReference<View> viewWeakReference : getFriendlyObstructionList()) {
            if (viewWeakReference.get() != null) {
                videoAdView.addFriendlyObstruction(viewWeakReference.get());
            }
        }
    }

}
