/*
 *    Copyright 2015 APPNEXUS INC
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

import android.view.MotionEvent;


public interface VideoAdEventsListener {


	/**
	 * Gets called when video starts playing
	 */
	public void onVideoStart();

	/**
	 * Gets called when video is paused
	 * 
	 * @param currentPosition
	 *            the duration when video paused
	 */
	public void onVideoPause(long currentPosition);

	/**
	 * Gets called when video is resumed
	 * 
	 * @param currentPosition
	 *            the duration when video resumed
	 */
	public void onVideoResume(long currentPosition);

	/**
	 * Gets called when video is skipped
	 * 
	 * @param currentPosition
	 *            the duration when video skipped
	 */
	public void onVideoSkip(long currentPosition);

	/**
	 * Gets called when video mute
	 */
	public void onMuteVideo();

	/**
	 * Gets called when video unmute
	 */
	public void onUnMuteVideo();

	/**
	 * Gets called when the video ad finishes quartile.
	 * @param videoQuartile
	 */
	public void onQuartileFinish(int videoQuartile);

	/**
	 * Gets called when the video Enter full screen mode
	 */
	public void onVideoPlayerEnterFullScreenMode();

	/**
	 * Gets called when the video exits full screen mode
	 */
	public void onVideoPlayerExitFullScreenMode();

	/**
	 * Gets called when the video ad is clicked
	 * 
	 * @param event
	 */
	public void onVideoClick(MotionEvent event);

	/**
	 * Gets called when the video is finished playing
	 */
	public void onVideoAdFinish();

}
