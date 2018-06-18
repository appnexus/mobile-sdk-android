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

import com.appnexus.opensdk.ANClickThroughAction;

public interface VideoAdPlaybackListener {

    /**
     * Called when the ad play starts.
     * @param videoAd The {@link VideoAd} that loaded the ad.
     */
    public void onAdPlaying(VideoAd videoAd);

    /**
     * Called when a Quartile tracker is fired. This is for the App to track. SDK has alrady tracked it.
     * @param videoAd The {@link VideoAd} that loaded the ad.
     * @param quartile Quartile describing the type of quartile event fired.
     */
    public void onQuartile(VideoAd videoAd, Quartile quartile);

    /**
     * Called when the Video Ad is compled. Application is expected to take over from here.
     * @param videoAd The {@link VideoAd} that loaded the ad.
     * @param playbackCompletionStateState PlaybackCompletionState describing the reason for Completion event
     */
    public void onAdCompleted(VideoAd videoAd, PlaybackCompletionState playbackCompletionStateState);

    /**
     * Ad video has requested to mute or unmute audio.
     * @param ad The {@link VideoAd} that loaded the ad.
     * @param isMuted mentions if audio was muted or unmuted
     */
    public void onAdMuted(VideoAd ad, boolean isMuted);

    /**
     * Called when the ad being clicked
     * and the ClickThroughAction is set as either ANClickThroughAction.OPEN_DEVICE_BROWSER
     * or ANClickThroughAction.OPEN_SDK_BROWSER.
     * {@link ANClickThroughAction}
     * The current activity will be
     * paused as the user switches to the activity launched from the
     * ad interaction.  For example, the user may click a link that
     * opens a web browser.
     *
     * @param videoAd The {@link VideoAd} that loaded the ad.
     */
    public void onAdClicked(VideoAd videoAd);

    /**
     * Called when the ad being clicked
     * and the ClickThroughAction is set as ANClickThroughAction.RETURN_URL
     * {@link ANClickThroughAction}
     *
     * @param videoAd The {@link VideoAd} that loaded the ad,
     * @param clickUrl The click url corresponding to the ad.
     */
    public void onAdClicked(VideoAd videoAd, String clickUrl);


    /**
     * Video Completion states.
     */
    public enum PlaybackCompletionState {

        /**
         * Ad has completed
         */
        COMPLETED,
        /**
         * Ad was skipped.
         */
        SKIPPED,

        /**
         * Error during video playback
         */
        ERROR,
    }

}
