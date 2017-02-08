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

package com.example.simplevideo;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.appnexus.opensdk.instreamvideo.Quartile;
import com.appnexus.opensdk.instreamvideo.VideoAd;
import com.appnexus.opensdk.instreamvideo.VideoAdPlaybackListener;

public class SimpleVideoActivity extends Activity implements VideoAdPlaybackListener {

    public static final String TAG = SimpleVideoActivity.class.getName();
    static VideoAd videoAdView;
    private TextView infoText;
    private TextView ad_ready;
    private TextView ad_failed;
    private TextView first_quartile;
    private TextView mid_quartile;
    private TextView third_quartile;
    private TextView ad_skipped;
    private TextView ad_completed;
    private TextView ad_mute;
    private ViewGroup.LayoutParams oldParams;

    // Content video player.
    private static VideoView videoPlayer;
    // Ad Container.
    private RelativeLayout baseContainer;

    // Controller
    static MediaController controller = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_player_activity);
        videoPlayer = (VideoView) findViewById(R.id.video_player);
        baseContainer = (RelativeLayout) findViewById(R.id.container_layout);
        videoPlayer.setVideoURI(Uri.parse(getString(R.string.content_url_1)));
        controller = new MediaController(this);
        videoPlayer.setMediaController(controller);
        videoAdView = AdVideoData.getInstance().getVideoAd();
        videoAdView.setVideoPlaybackListener(this);

        ad_ready = (TextView) findViewById(R.id.ad_ready);
        if (videoAdView.isReady()) {
            updateUI(ad_ready);
        } else {
            updateUI(ad_failed);
        }
        first_quartile = (TextView) findViewById(R.id.first_quartile);
        mid_quartile = (TextView) findViewById(R.id.mid_quartile);
        third_quartile = (TextView) findViewById(R.id.third_quartile);
        ad_skipped = (TextView) findViewById(R.id.ad_skipped);
        ad_completed = (TextView) findViewById(R.id.ad_completed);
        ad_failed = (TextView) findViewById(R.id.ad_failed);
        infoText = (TextView) findViewById(R.id.infotTextView);
        ad_mute = (TextView) findViewById(R.id.ad_mute);

        final ImageButton playButon = (ImageButton) findViewById(R.id.play_button);
        playButon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (videoAdView.isReady()) {
                    videoAdView.playAd(baseContainer);
                } else {
                    videoPlayer.start();
                }
                playButon.setVisibility(View.GONE);
            }
        });

    }


    @Override
    public void onQuartile(VideoAd view, Quartile quartile) {
        Log.d(TAG, "onQuartile::" + quartile);
        if (Quartile.QUARTILE_FIRST == quartile) {
            updateUI(first_quartile);
        } else if (Quartile.QUARTILE_MID == quartile) {
            updateUI(mid_quartile);
        } else if (Quartile.QUARTILE_THIRD == quartile) {
            updateUI(third_quartile);
        }
    }

    @Override
    public void onAdCompleted(VideoAd view, PlaybackCompletionState playbackState) {
        Log.d(TAG, "onAdCompleted::playbackState" + playbackState);
        if (playbackState == PlaybackCompletionState.COMPLETED) {
            updateUI(ad_completed);
        } else if (playbackState == PlaybackCompletionState.SKIPPED) {
            updateUI(ad_skipped);
        }
        controller.show();
        videoPlayer.start();
    }

    @Override
    public void onAdMuted(VideoAd view, boolean isMute){
        Log.d(TAG, "isAudioMute");
        if(isMute){
            ad_mute.setText("AUDIO MUTE");
        } else {
            ad_mute.setText("AUDIO UnMUTE");
        }

        updateUI(ad_mute);
    }

    @Override
    public void onAdClicked(VideoAd adView) {
        Log.d(TAG, "onAdClicked");
    }

    private static void updateUI(TextView textView) {
        if (textView != null) {
            textView.setTextColor(Color.GREEN);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        videoAdView.activityOnResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        videoAdView.activityOnPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        videoAdView.activityOnDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            goFullScreen();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            goBacktoNormal();
        }
    }

    public void goFullScreen() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        hideAllExceptvideo();
        RelativeLayout.LayoutParams paramsLandscape = new RelativeLayout.LayoutParams(width, height);
        oldParams = baseContainer.getLayoutParams();
        baseContainer.setLayoutParams(paramsLandscape);
    }


    public void goBacktoNormal() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        unhideHidden();
        if (oldParams != null) {
            baseContainer.setLayoutParams(oldParams);
        }
    }

    private void unhideHidden() {
        infoText.setVisibility(View.VISIBLE);
        ad_ready.setVisibility(View.VISIBLE);
        ad_failed.setVisibility(View.VISIBLE);
        first_quartile.setVisibility(View.VISIBLE);
        mid_quartile.setVisibility(View.VISIBLE);
        third_quartile.setVisibility(View.VISIBLE);
        ad_skipped.setVisibility(View.VISIBLE);
        ad_completed.setVisibility(View.VISIBLE);
        ad_mute.setVisibility(View.VISIBLE);
    }

    private void hideAllExceptvideo() {
        infoText.setVisibility(View.GONE);
        ad_ready.setVisibility(View.GONE);
        ad_failed.setVisibility(View.GONE);
        first_quartile.setVisibility(View.GONE);
        mid_quartile.setVisibility(View.GONE);
        third_quartile.setVisibility(View.GONE);
        ad_skipped.setVisibility(View.GONE);
        ad_completed.setVisibility(View.GONE);
        ad_mute.setVisibility(View.GONE);
    }
}
