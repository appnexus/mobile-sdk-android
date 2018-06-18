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
import com.appnexus.opensdk.ResultCode;

/**
 * This interface is used by internal classes of SDK to send callbacks to the VideoAd class.
 * This should not be used by application.
 */

interface VideoAdDispatcher {
    /**
     * Called when an ad is ready to be used, indicates a successful ad request
     */
    public void onAdLoaded();

    /**
     * Called when the Ad Request has ended in a failure
     *
     * @param errorCode the error code describing the failure.
     */
    public void onAdFailed(ResultCode errorCode);

    /**
     * called when the ad play is started
     */
    public void onAdPlaying();

    /**
     * Called when the ad being clicked
     * and the ClickThroughAction is set as either ANClickThroughAction.OPEN_DEVICE_BROWSER
     * or ANClickThroughAction.OPEN_SDK_BROWSER
     * {@link ANClickThroughAction}
     */
    public void onAdClicked();

    /**
     * Called when Ad video is skipped
     */
    public void onVideoSkip();

    /**
     * Called when Quartile events are fired in the
     *
     * @param quartile Quartile object
     */
    public void onQuartile(Quartile quartile);

    /**
     * Called when the ad being clicked
     */
    public void onAdCompleted();

    /**
     * Called when Video ad requested to mute or unmute.
     */
    public void isAudioMute(boolean isMute);

    /**
     * Called when there is an error in playback
     */
    public void onPlaybackError();

    /**
     * Called when the ad being clicked
     * and the ClickThroughAction is set as ANClickThroughAction.RETURN_URL
     * {@link ANClickThroughAction}
     */
    public void onAdClicked(String clickUrl);
}
