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

import com.appnexus.opensdk.BaseAdDispatcher;

/**
 * This interface is used by internal classes of SDK to send callbacks to the VideoAd class.
 * This should not be used by application.
 */

interface VideoAdDispatcher extends BaseAdDispatcher {

    /**
     * called when the ad play is started
     */
    public void onAdPlaying();

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
}
