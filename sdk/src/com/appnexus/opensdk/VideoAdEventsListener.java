/**
 * Interface with video event callbacks for implementing class.
 */
package com.appnexus.opensdk;

import android.view.MotionEvent;


public interface VideoAdEventsListener {


	/**
	 * Gets called when video starts playing
	 */
	public void onVideoPlay();

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
	 * Gets called when the video ad is rewinded
	 * @param fromPosition the duration from video is rewinded
	 * @param toPosition the duration to video is rewinded
	 */
	public void onVideoPlayerRewind(long fromPosition, long toPosition);

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
