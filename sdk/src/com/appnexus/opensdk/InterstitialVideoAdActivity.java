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
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.VastVideoUtil;
import com.appnexus.opensdk.utils.ViewUtil;

class InterstitialVideoAdActivity implements AdActivity.AdActivityImplementation {
    public static final int TEXT_SIZE = 17;
    private Activity adActivity;
    private TextView countdownTimerText;
    private RelativeLayout layout;
    private long now;
    private InterstitialAdView adView;
    private ImageButton closeButton;
    private VastVideoView videoView;
    private InterstitialVideoPlayer videoPlayer;
    private VastVideoConfiguration videoConfig;

    public InterstitialVideoAdActivity(Activity adActivity) {
        this.adActivity = adActivity;
    }

    @Override
    public void create() {
        layout = new RelativeLayout(adActivity);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(layoutParams);
        adActivity.setContentView(layout);

        // set 'now' variable to filter expired ads
        now = adActivity.getIntent().getLongExtra(InterstitialAdView.INTENT_KEY_TIME,
                System.currentTimeMillis());
        setIAdView(InterstitialAdView.INTERSTITIALADVIEW_TO_USE);

        addCountdownTimerTextView();

        addCloseButton();
    }

    private void addCountdownTimerTextView() {
        countdownTimerText = new TextView(adActivity);
        countdownTimerText.setTextSize(TEXT_SIZE);
        countdownTimerText.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        countdownTimerText.setTextColor(Color.WHITE);
        countdownTimerText.setLayoutParams(VastVideoUtil.getTimerPosition(videoConfig));
        countdownTimerText.setVisibility(View.GONE);
        layout.addView(countdownTimerText);
    }

    @Override
    public void backPressed() {
        // do nothing
    }

    @Override
    public void destroy() {
        // clean up video view
        if (videoView != null) {
           videoView.destroy();
        }

        // cleanup adView
        if (adView != null) {
            adView.setAdImplementation(null);
        }
    }

    @Override
    public void interacted() {
    }

    @Override
    public WebView getWebView() {
        return null;
    }

    private void setIAdView(InterstitialAdView av) {
        adView = av;
        if (adView == null) return;

        adView.setAdImplementation(this);
        videoConfig = adView.getVideoConfiguration();

        layout.setBackgroundColor(adView.getBackgroundColor());
        layout.removeAllViews();
        if (adView.getParent() != null) {
            ((ViewGroup) adView.getParent()).removeAllViews();
        }
        InterstitialAdQueueEntry iAQE = adView.getAdQueue().poll();

        // To be safe, ads from the future will be considered to have expired
        // if now-p.first is less than 0, the ad will be considered to be from the future
        while (iAQE != null
                && (now - iAQE.getTime() > InterstitialAdView.MAX_AGE || now-iAQE.getTime()<0)) {
            Clog.w(Clog.baseLogTag, Clog.getString(R.string.too_old));
            iAQE = adView.getAdQueue().poll();
        }
        if ((iAQE == null)
                || !(iAQE.getView() instanceof VastVideoView))
            return;

        playVastInterstitialAd(iAQE);
    }


    private void playVastInterstitialAd(InterstitialAdQueueEntry iAQE) {
        videoView = (VastVideoView) iAQE.getView();

        layout.addView(videoView);

        videoPlayer = new InterstitialVideoPlayer();
//        videoPlayer = new VastVideoPlayer();
        videoPlayer.setUpdateCountdownTimerListener(new IUpdateCountdownTimerListener() {
            @Override
            public void onUpdateCountdownTimer(String skipOffset) {
                countdownTimerText.setVisibility(View.VISIBLE);
                countdownTimerText.setText(skipOffset + "  ");
                countdownTimerText.bringToFront();
            }

            @Override
            public void onDisplayCloseButton() {
                countdownTimerText.setText("");
                countdownTimerText.setVisibility(View.GONE);
                closeButton.setVisibility(View.VISIBLE);
                closeButton.bringToFront();
            }
        });

        videoPlayer.setVideoAdListener(new VideoAdEventsListener() {
            @Override
            public void onVideoStart() {
                if (adView !=null && adView.getVideoAdEventsListener() != null) {
                    adView.getVideoAdEventsListener().onVideoStart();
                }

            }

            @Override
            public void onVideoPause(long currentPosition) {
                if (adView !=null && adView.getVideoAdEventsListener() != null) {
                    adView.getVideoAdEventsListener().onVideoPause(currentPosition);
                }
            }

            @Override
            public void onVideoResume(long currentPosition) {
                if (adView !=null && adView.getVideoAdEventsListener() != null) {
                    adView.getVideoAdEventsListener().onVideoResume(currentPosition);
                }
            }

            @Override
            public void onVideoSkip(long currentPosition) {
                if (adView !=null && adView.getVideoAdEventsListener() != null) {
                    adView.getVideoAdEventsListener().onVideoSkip(currentPosition);
                }
            }

            @Override
            public void onMuteVideo() {
                if (adView !=null && adView.getVideoAdEventsListener() != null) {
                    adView.getVideoAdEventsListener().onMuteVideo();
                }
            }

            @Override
            public void onUnMuteVideo() {
                if (adView !=null && adView.getVideoAdEventsListener() != null) {
                    adView.getVideoAdEventsListener().onUnMuteVideo();
                }
            }

            @Override
            public void onQuartileFinish(int videoQuartile) {
                if (adView !=null && adView.getVideoAdEventsListener() != null) {
                    adView.getVideoAdEventsListener().onQuartileFinish(videoQuartile);
                }
            }

            @Override
            public void onVideoPlayerEnterFullScreenMode() {
                if (adView !=null && adView.getVideoAdEventsListener() != null) {
                    adView.getVideoAdEventsListener().onVideoPlayerEnterFullScreenMode();
                }
            }

            @Override
            public void onVideoPlayerExitFullScreenMode() {
                if (adView !=null && adView.getVideoAdEventsListener() != null) {
                    adView.getVideoAdEventsListener().onVideoPlayerExitFullScreenMode();
                }
            }

            @Override
            public void onVideoClick(MotionEvent event) {
                if (adView !=null && adView.getVideoAdEventsListener() != null) {
                    adView.getVideoAdEventsListener().onVideoClick(event);
                }
            }

            @Override
            public void onVideoAdFinish() {
                if (adView !=null && adView.getVideoAdEventsListener() != null) {
                    adView.getVideoAdEventsListener().onVideoAdFinish();
                }
                if (adActivity != null) {
                    adActivity.finish();
                }
            }
        });


        videoPlayer.initiateVASTVideoPlayer(adActivity, videoView, layout, videoConfig);
    }

    // add the close button if it hasn't been added already
    private void addCloseButton() {
        if ((layout == null) || (closeButton != null)) return;

        closeButton = ViewUtil.createCloseButtonInRelativeLayout(adActivity, false);
        closeButton.setVisibility(View.GONE);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoPlayer != null) {
                    videoPlayer.skipVideo();
                } else if (adActivity != null) {
                    adActivity.finish();
                }
            }
        });

        layout.addView(closeButton);
    }

}
