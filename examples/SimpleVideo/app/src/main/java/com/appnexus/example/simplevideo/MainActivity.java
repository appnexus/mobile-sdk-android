/*
 *    Copyright 2017 APPNEXUS INC
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

package com.appnexus.example.simplevideo;

import android.app.Activity;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.instreamvideo.Quartile;
import com.appnexus.opensdk.instreamvideo.VideoAd;
import com.appnexus.opensdk.instreamvideo.VideoAdLoadListener;
import com.appnexus.opensdk.instreamvideo.VideoAdPlaybackListener;


public class MainActivity extends Activity {

    public static final String TAG = MainActivity.class.getName();
    private TextView infoText;
    private ImageButton playButon;
    private ViewGroup.LayoutParams oldParams;

    // The Ad Video instance.
    // Its important to create this as a Instance variable to make sure its not removed Garbage Collected.
    private VideoAd videoAd;

    // Content video player.
    private static VideoView videoPlayer;

    // Ad Container.
    private RelativeLayout baseContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        infoText = (TextView) findViewById(R.id.infotTextView);
        playButon = (ImageButton) findViewById(R.id.play_button);
        videoPlayer = (VideoView) findViewById(R.id.video_player);
        baseContainer = (RelativeLayout) findViewById(R.id.container_layout);
        videoPlayer.setVideoURI(Uri.parse(getString(R.string.content_url_1)));

        // Initialize VideoAd
        videoAd =new VideoAd(this,"9924002");

        // Set the Ad-Load Listener
        videoAd.setAdLoadListener(new VideoAdLoadListener() {
            @Override
            public void onAdLoaded(VideoAd videoAd) {
                Log.d(TAG, "onAdLoaded");
                Toast.makeText(getApplicationContext(),"onAdLoaded",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdRequestFailed(VideoAd videoAd, ResultCode errorCode) {
                Log.d(TAG, "onAdRequestFailed::"+errorCode);
                Toast.makeText(getApplicationContext(),"onAdRequestFailed",Toast.LENGTH_SHORT).show();
            }
        });

        //Load the Ad.
        videoAd.loadAd();


        // Set PlayBack Listener.
        videoAd.setVideoPlaybackListener(new VideoAdPlaybackListener() {

            @Override
            public void onQuartile(VideoAd view, Quartile quartile) {
                Log.d(TAG, "onQuartile::" + quartile);
            }

            @Override
            public void onAdCompleted(VideoAd view, PlaybackCompletionState playbackState) {
                Log.d(TAG, "onAdCompleted::playbackState" + playbackState);
                videoPlayer.start();
            }

            @Override
            public void onAdMuted(VideoAd view, boolean isMute){
                Log.d(TAG, "isAudioMute::"+isMute);
            }

            @Override
            public void onAdClicked(VideoAd adView) {
                Log.d(TAG, "onAdClicked");
            }
        });




        playButon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (videoAd.isReady()) {
                    // Play the VideoAd by passing the container.
                    videoAd.playAd(baseContainer);
                } else {
                    videoPlayer.start();
                }
                playButon.setVisibility(View.GONE);
            }
        });

    }



    // Pass the Activity LifeCycle Callback's to VideoAd. This is very important for autoresuming the Video Ad after interruption.
    @Override
    public void onResume() {
        super.onResume();
        videoAd.activityOnResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        videoAd.activityOnPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        videoAd.activityOnDestroy();
    }



    // Handling Configuration Change.
    // You just need to resize the container for resizing the video ad for configuration change event. The VideoAd resizes by itself to fit the container.
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
        infoText.setVisibility(View.GONE);
        RelativeLayout.LayoutParams paramsLandscape = new RelativeLayout.LayoutParams(width, height);
        oldParams = baseContainer.getLayoutParams();
        baseContainer.setLayoutParams(paramsLandscape);
    }


    public void goBacktoNormal() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        infoText.setVisibility(View.VISIBLE);
        if (oldParams != null) {
            baseContainer.setLayoutParams(oldParams);
        }
    }
}
