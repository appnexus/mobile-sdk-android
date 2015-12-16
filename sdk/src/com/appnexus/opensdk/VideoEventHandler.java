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

import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.vastdata.AdModel;

import java.util.List;

public class VideoEventHandler {

    private static final float FIRST_QUARTER_MARKER = 0.25f;
    private static final float MID_POINT_MARKER = 0.50f;
    private static final float THIRD_QUARTER_MARKER = 0.75f;
    private boolean isFirstMarkHit;
    private boolean isSecondMarkHit;
    private boolean isThirdMarkHit;

    protected void trackRequestInBackground(final List<String> urls) {
        if (urls == null || urls.size() == 0) {
            return;
        }
        for (final String url : urls) {
            if (url != null && url.trim().length()>0) {
                new VastTracker(url).execute();
            }
        }
    }

    protected void handleVideoMuteEvent(VideoAdEventsListener videoAdListener, AdModel vastAd) {
        trackRequestInBackground(VastVideoUtil.getVastEventURLList(vastAd, VastVideoUtil.EVENT_MUTE));
        if (videoAdListener != null) {
            videoAdListener.onMuteVideo();
        }
    }

    protected void handleVideoUnmuteEvent(VideoAdEventsListener videoAdListener, AdModel vastAd) {
        trackRequestInBackground(VastVideoUtil.getVastEventURLList(vastAd, VastVideoUtil.EVENT_UNMUTE));
        if (videoAdListener != null) {
            videoAdListener.onUnMuteVideo();
        }
    }

    protected void handleVideoPauseEvent(VideoAdEventsListener videoAdListener, AdModel vastAd, int currentPosition) {
        trackRequestInBackground(VastVideoUtil.getVastEventURLList(vastAd, VastVideoUtil.EVENT_PAUSE));
        if (videoAdListener != null) {
            videoAdListener.onVideoPause(currentPosition);
        }
    }

    protected void handleVideoResumeEvent(VideoAdEventsListener videoAdListener, AdModel vastAd, int currentPosition) {
        trackRequestInBackground(VastVideoUtil.getVastEventURLList(vastAd, VastVideoUtil.EVENT_RESUME));
        if (videoAdListener != null) {
            videoAdListener.onVideoResume(currentPosition);
        }
    }

    protected void handleVideoStartEvent(VideoAdEventsListener videoAdListener, AdModel vastAd) {
        trackRequestInBackground(VastVideoUtil.getVastEventURLList(vastAd, VastVideoUtil.EVENT_START));
        if (videoAdListener != null) {
            videoAdListener.onVideoStart();
        }
    }

    protected void handleSkipEvent(VideoAdEventsListener videoAdListener, AdModel vastAd, int currentPosition) {
        trackRequestInBackground(VastVideoUtil.getVastEventURLList(vastAd, VastVideoUtil.EVENT_SKIP));
        if (videoAdListener != null) {
            videoAdListener.onVideoSkip(currentPosition);
        }
    }

    protected void handleVideoClickEvent(VideoAdEventsListener videoAdListener, MotionEvent e, AdModel vastAd) {
        if (videoAdListener != null) {
            videoAdListener.onVideoClick(e);
        }
        trackRequestInBackground(VastVideoUtil.getVastClickURLList(vastAd));
    }

    protected void handleVideoCompleteEvent(VideoAdEventsListener videoAdListener, AdModel vastAd) {
        trackRequestInBackground(VastVideoUtil.getVastEventURLList(vastAd, VastVideoUtil.EVENT_COMPLETE));
        if (videoAdListener != null) {
            videoAdListener.onVideoAdFinish();
        }
    }

    protected void trackQuartileEvents(int currentPosition, VideoAdEventsListener videoAdListener, AdModel vastAd, double videoLength) {
        try {

            double progressPercentage = (double) currentPosition / videoLength;

            if (progressPercentage > FIRST_QUARTER_MARKER && !isFirstMarkHit) {
                isFirstMarkHit = true;
                trackRequestInBackground(VastVideoUtil.getVastEventURLList(vastAd, VastVideoUtil.EVENT_FIRST_QUARTILE));
                Clog.i(Clog.vastLogTag, "Tracking First Quartile");
                if (videoAdListener != null) {
                    videoAdListener.onQuartileFinish(1);
                }
            }

            if (progressPercentage > MID_POINT_MARKER && !isSecondMarkHit) {
                isSecondMarkHit = true;
                trackRequestInBackground(VastVideoUtil.getVastEventURLList(vastAd, VastVideoUtil.EVENT_MIDPOINT));
                Clog.i(Clog.vastLogTag, "Tracking Second Quartile");
                if (videoAdListener != null) {
                    videoAdListener.onQuartileFinish(2);
                }
            }

            if (progressPercentage > THIRD_QUARTER_MARKER && !isThirdMarkHit) {
                isThirdMarkHit = true;
                trackRequestInBackground(VastVideoUtil.getVastEventURLList(vastAd, VastVideoUtil.EVENT_THIRD_QUARTILE));
                Clog.i(Clog.vastLogTag, "Tracking Third Quartile");
                if (videoAdListener != null) {
                    videoAdListener.onQuartileFinish(3);
                }
            }

        } catch (Exception e) {
            Clog.e(Clog.vastLogTag, "Exception occurred while tracking quartile events: " + e.getMessage());
        }
    }
}
