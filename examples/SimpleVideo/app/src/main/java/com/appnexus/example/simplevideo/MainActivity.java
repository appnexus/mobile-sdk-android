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

        initContent();

        loadAd();

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



    private void loadAd() {
        // Initialize VideoAd
        videoAd = new VideoAd(this);

        // Set the Ad-Load Listener
        videoAd.setAdLoadListener(new VideoAdLoadListener() {
            @Override
            public void onAdLoaded(VideoAd videoAd) {
                Log.d(TAG, "onAdLoaded");
                Toast.makeText(getApplicationContext(), "onAdLoaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdRequestFailed(VideoAd videoAd, ResultCode errorCode) {
                Log.d(TAG, "onAdRequestFailed::" + errorCode);
                Toast.makeText(getApplicationContext(), "onAdRequestFailed", Toast.LENGTH_SHORT).show();
            }
        });


        // Load the vast document into the videoAd object.
        videoAd.loadVASTXML("<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\" standalone=\\\"yes\\\"?><VAST version=\\\"2.0\\\"><Ad id=\\\"116465495\\\"><Wrapper><AdSystem version=\\\"2.0\\\">Innovid Ads</AdSystem><VASTAdTagURI><![CDATA[http://rtr.innovid.com/r1.5554946ab01d97.36996823;cb=%25%CACHEBUSTER%25%25]]></VASTAdTagURI><Error><![CDATA[http://nym1-mobile.adnxs.com/vast_track/v2?info=YgAAAAMArgAFAQm_qqpbAAAAABGc0Fo202oTJhm_qqpbAAAAACDXvsQ3KAAwvgc4vgdAyOc9SNfg6QFQz-nVBlgBYgItLWgBcAF4AIABAIgBAJABwAKYATKgAQCoAde-xDc.&s=b7abf2cde62e99338b26e510c215d26d8f14d1cc&event_type=4&error_code=[ERRORCODE]]]></Error><Impression id=\\\"adnxs\\\"><![CDATA[http://nym1-mobile.adnxs.com/it?e=wqT_3QKNB6CNAwAAAwDWAAUBCL_Vqt0FEJyh67Kz2tqJJhi-5bHbqtOX4AYqNgkAAAECCPA_EQEHEAAA8D8ZCQkIAAAhCQkI8D8pEQkAMQkJqAAAMM_p1QY4vgdAvgdIAlDXvsQ3WMu7TmAAaJFAeKHRBIABAYoBA1VTRJIFBvDsmAEBoAEBqAEBsAEAuAEDwAEDyAEC0AEA2AEA4AEA8AEAigI8dWYoJ2EnLCAxNzk3ODY1LCAxNTM3OTExNDg3KTt1ZigncicsIDExNjQ2NTQ5NSwgMTUzNzkxMTQ4Nyk7kgLxASE1ekZFSEFpcDV1QUxFTmUteERjWUFDREx1MDR3QURnQVFBUkl2Z2RRei1uVkJsZ0FZS1lDYUFCd0FIZ0FnQUVBaUFFQWtBRUJtQUVCb0FFQnFBRURzQUVBdVFFcGk0aURBQUR3UDhFQktZdUlnd0FBOERfSkFWSE9SQVBheFFCQTJRRUFBQUFBBSgYLUFCQVBVQgUQKEpnQ0FLQUNBTFVDBRAETDAJCPBKTUFDQU1nQ0FPQUNBT2dDQVBnQ0FJQURBWkFEQUpnREFhZ0RxZWJnQzdvRENVNVpUVEk2TXpnek5PQURPUS4umgJJIUlRMWRpd2lwLvQALHk3dE9JQVFvQURFQQkBVER3UHpvSlRsbE5Nam96T0RNMFFEbEoJHPQAAUFBOEQ4LtgC6AfgAsfTAeoCPXBsYXkuZ29vZ2xlLmNvbS9zdG9yZS9hcHBzL2RldGFpbHM_aWQ9Y29tLmFwcG5leHVzLm9wZW5zZGthcHCAAwGIAwGQAwCYAxegAwGqAwDAA5AcyAMA0gMoCAoSJDYzMWI2ODk2LTE4YzQtNGViOS1hODM0LTY3NWRiYTg2NDg4YtgD_OBZ4AMA6AMC-AMAgAQAkgQGL3V0L3YymAQAogQLMTAuMS4xMi4xMDKoBMBRsgQRCAAQARjAAiAyKAEoAjAAOAK4BADABADIBADSBA05NTgjTllNMjozODM02gQCCADgBADwBNe-xDeCBRdjVsgAIIgFAZgFAKAF_xEBFAHABQDJBUn_FPA_0gUJCQkMnAAA2AUB4AUB8AUB-gUECAAQAJAGAZgGALgGAMEGAAAAAAAAAADIBgA.&s=324bbd95521d33c4a7aed45c4d04589661264be6&referrer=play.google.com%2Fstore%2Fapps%2Fdetails%3Fid%3Dcom.appnexus.opensdkapp]]></Impression><Creatives><Creative id=\\\"4486926\\\" AdID=\\\"116465495\\\"><Linear><TrackingEvents><Tracking event=\\\"start\\\"><![CDATA[http://nym1-mobile.adnxs.com/vast_track/v2?info=YgAAAAMArgAFAQm_qqpbAAAAABGc0Fo202oTJhm_qqpbAAAAACDXvsQ3KAAwvgc4vgdAyOc9SNfg6QFQz-nVBlgBYgItLWgBcAF4AIABAIgBAJABwAKYATKgAQCoAde-xDc.&s=b7abf2cde62e99338b26e510c215d26d8f14d1cc&event_type=2]]></Tracking><Tracking event=\\\"skip\\\"><![CDATA[http://nym1-mobile.adnxs.com/vast_track/v2?info=YgAAAAMArgAFAQm_qqpbAAAAABGc0Fo202oTJhm_qqpbAAAAACDXvsQ3KAAwvgc4vgdAyOc9SNfg6QFQz-nVBlgBYgItLWgBcAF4AIABAIgBAJABwAKYATKgAQCoAde-xDc.&s=b7abf2cde62e99338b26e510c215d26d8f14d1cc&event_type=3]]></Tracking><Tracking event=\\\"firstQuartile\\\"><![CDATA[http://nym1-mobile.adnxs.com/vast_track/v2?info=YgAAAAMArgAFAQm_qqpbAAAAABGc0Fo202oTJhm_qqpbAAAAACDXvsQ3KAAwvgc4vgdAyOc9SNfg6QFQz-nVBlgBYgItLWgBcAF4AIABAIgBAJABwAKYATKgAQCoAde-xDc.&s=b7abf2cde62e99338b26e510c215d26d8f14d1cc&event_type=5]]></Tracking><Tracking event=\\\"midpoint\\\"><![CDATA[http://nym1-mobile.adnxs.com/vast_track/v2?info=YgAAAAMArgAFAQm_qqpbAAAAABGc0Fo202oTJhm_qqpbAAAAACDXvsQ3KAAwvgc4vgdAyOc9SNfg6QFQz-nVBlgBYgItLWgBcAF4AIABAIgBAJABwAKYATKgAQCoAde-xDc.&s=b7abf2cde62e99338b26e510c215d26d8f14d1cc&event_type=6]]></Tracking><Tracking event=\\\"thirdQuartile\\\"><![CDATA[http://nym1-mobile.adnxs.com/vast_track/v2?info=YgAAAAMArgAFAQm_qqpbAAAAABGc0Fo202oTJhm_qqpbAAAAACDXvsQ3KAAwvgc4vgdAyOc9SNfg6QFQz-nVBlgBYgItLWgBcAF4AIABAIgBAJABwAKYATKgAQCoAde-xDc.&s=b7abf2cde62e99338b26e510c215d26d8f14d1cc&event_type=7]]></Tracking><Tracking event=\\\"complete\\\"><![CDATA[http://nym1-mobile.adnxs.com/vast_track/v2?info=YgAAAAMArgAFAQm_qqpbAAAAABGc0Fo202oTJhm_qqpbAAAAACDXvsQ3KAAwvgc4vgdAyOc9SNfg6QFQz-nVBlgBYgItLWgBcAF4AIABAIgBAJABwAKYATKgAQCoAde-xDc.&s=b7abf2cde62e99338b26e510c215d26d8f14d1cc&event_type=8]]></Tracking></TrackingEvents><VideoClicks><ClickTracking id=\\\"adnxs\\\"><![CDATA[http://nym1-mobile.adnxs.com/click?AAAAAAAA8D8AAAAAAADwPwAAAAAAAAAAAAAAAAAA8D8AAAAAAADwP5zQWjbTahMmvnJsq5pewAa_qqpbAAAAAM901QC-AwAAvgMAAAIAAABXH_EGy50TAAAAAABVU0QAVVNEAAEAAQARIAAAAAABAwMCAAAAAAAAOhfVnQAAAAA./cnd=%21IQ1diwip5uALENe-xDcYy7tOIAQoADEAAAAAAADwPzoJTllNMjozODM0QDlJAAAAAAAA8D8./cca=OTU4I05ZTTI6MzgzNA==/bn=75937/referrer=play.google.com%2Fstore%2Fapps%2Fdetails%3Fid%3Dcom.appnexus.opensdkapp/]]></ClickTracking></VideoClicks></Linear></Creative></Creatives></Wrapper></Ad></VAST>");


        // if App desires to handle click url by itself. Setting this will result in onAdClicked(VideoAd videoAd, String clickUrl) getting called when user click learn-more.
        //videoAd.setClickThroughAction(ANClickThroughAction.RETURN_URL);

        // Set PlayBack Listener.
        videoAd.setVideoPlaybackListener(new VideoAdPlaybackListener() {

            @Override
            public void onAdPlaying(final VideoAd videoAd) {
                Log.d(TAG, "onAdPlaying::");
            }

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
            public void onAdMuted(VideoAd view, boolean isMute) {
                Log.d(TAG, "isAudioMute::" + isMute);
            }

            @Override
            public void onAdClicked(VideoAd adView) {
                Log.d(TAG, "onAdClicked");
            }

            @Override
            public void onAdClicked(VideoAd videoAd, String clickUrl) {
                Log.d(TAG, "onAdClicked");
            }
        });
    }


    // This initializes the Actual Content Video.
    private void initContent() {
        infoText = findViewById(R.id.infotTextView);
        playButon =  findViewById(R.id.play_button);
        videoPlayer = findViewById(R.id.video_player);
        baseContainer = findViewById(R.id.container_layout);
        videoPlayer.setVideoURI(Uri.parse(getString(R.string.content_url_1)));

    }


    // Pass the Activity LifeCycle Callback's to VideoAd. This is very important for autoresuming the Video Ad after interruption.
    @Override
    public void onResume() {
        super.onResume();
        if(videoAd!=null) {
            videoAd.activityOnResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(videoAd!=null) {
            videoAd.activityOnPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(videoAd!=null) {
            videoAd.activityOnDestroy();
        }
    }




    // Handling Configuration Change.
    // You just need to resize the container for resizing the video ad for configuration change event. The VideoAd resizes by itself to fit the container.
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            goFullScreen();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
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