package com.appnexus.opensdk;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.VideoView;

import com.appnexus.opensdk.shadows.ShadowApplication;
import com.appnexus.opensdk.shadows.ShadowMediaPlayer;
import com.appnexus.opensdk.shadows.ShadowVideoView;
import com.appnexus.opensdk.util.Lock;
import com.appnexus.opensdk.utils.Settings;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.httpclient.FakeHttp;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, shadows = {ShadowVideoView.class, ShadowMediaPlayer.class, ShadowApplication.class})
public class VideoActivitiesTest extends BaseViewAdTest {
    MockWebServer server;
    boolean serverStarted = false;
    AdActivity adActivity;
    VastVideoView videoView;

    @Override
    public void setup() {
        super.setup();
        server = new MockWebServer();
        try {
            server.start();
            serverStarted = true;
            HttpUrl url = server.url("/");
            Settings.BASE_URL_UT = url.toString();
        } catch (IOException e) {
        }
    }

    @Test
    public void testVideoAdActivityLaunchedCorrectly() {
        if (serverStarted) {
            // Feed the UT response
            server.enqueue(new MockResponse().setResponseCode(200).setBody(TestUTResponses.video()));
            requestManager = new InterstitialAdRequestManager(interstitialAdView);
            requestManager.execute();
            Robolectric.flushBackgroundThreadScheduler();
            Robolectric.flushForegroundThreadScheduler();
            // Feed the wrapper response
            FakeHttp.addPendingHttpResponse(200, TestUTResponses.vastInline());
            Robolectric.flushBackgroundThreadScheduler();
            Robolectric.flushForegroundThreadScheduler();
            // Show the video
            interstitialAdView.show();
            // check that correct AdActivity to show the video is created
            ShadowActivity shadowActivity = Shadows.shadowOf(activity);
            Intent intent = shadowActivity.getNextStartedActivity();
            assertEquals(intent.getComponent().getClassName(), AdActivity.class.getName());
            String adActivityType = intent.getStringExtra(AdActivity.INTENT_KEY_ACTIVITY_TYPE);
            assertNotNull(adActivityType);
            assertEquals(adActivityType, AdActivity.ACTIVITY_TYPE_VIDEO_INTERSTITIAL);
        }
    }

    private void startVideo() {
        // simulating the show() behavior on InterstitialAdView
        // create a video view and add it to InterstitialAdView adQueue
        videoView = new VastVideoView(activity, TestUTResponses.getVastAdModel());
        final long now = System.currentTimeMillis();
        interstitialAdView.getAdQueue().add(new InterstitialAdQueueEntry() {
            @Override
            public long getTime() {
                return now;
            }

            @Override
            public boolean isMediated() {
                return false;
            }

            @Override
            public MediatedAdViewController getMediatedAdViewController() {
                return null;
            }

            @Override
            public View getView() {
                return videoView;
            }
        });
        InterstitialAdView.INTERSTITIALADVIEW_TO_USE = interstitialAdView;
        // start the InterstitialVideoAdActivity
        Intent i = new Intent(activity, AdActivity.class);
        i.putExtra(AdActivity.INTENT_KEY_ACTIVITY_TYPE, AdActivity.ACTIVITY_TYPE_VIDEO_INTERSTITIAL);
        i.putExtra(InterstitialAdView.INTENT_KEY_TIME, now);
        i.putExtra(InterstitialAdView.INTENT_KEY_TIME, 5000); // 5 seconds
        adActivity = Robolectric.buildActivity(AdActivity.class).withIntent(i).create().get();
        boolean playing = videoView.isPlaying();
        assertTrue(videoView.isPlaying());
    }

    private void pauseVideo() {
        videoView.pause();
    }

    private void resumeVideo() {
        videoView.resume();
    }

    //    @Test
    public void testVideoView() {
        Uri videoUri = Uri.parse("android.resource://" + this.getClass().getPackage().getName() + "/" + R.raw.test_video);
        VideoView videoView = new VideoView(activity);
        ShadowVideoView shadowVideoView = (ShadowVideoView) Shadows.shadowOf(videoView);
        shadowVideoView.onCreateSurfaceHolderCallBack();
        videoView.setVideoURI(videoUri);
        videoView.start();
        Lock.pause(1000);
        boolean playing = videoView.isPlaying();
        assertTrue(playing);
    }

    private void pendBlankResponsesForTrackers(int i) {
        for (int j = 0; j < i; j++) {
            FakeHttp.addPendingHttpResponse(200, TestUTResponses.blank());
        }
    }

    //    @Test
    public void testBaseCaseOfTrackersFiring() {
        pendBlankResponsesForTrackers(7);
        startVideo();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();
        // assert that the impression urls are fired
        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.IMPRESSION_URL_1));
        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.IMPRESSION_URL_2));
        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.START_URL));
        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.FIRST_QUARTILE));
        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.MID_POINT_URL));
        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.THIRD_QUARTILE_URL));
        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.COMPLETE_URL));
    }

    // Following cases won't happen in production
//    @Test
    public void testPauseAndResumeBeforeFirstQuartile() {
        pendBlankResponsesForTrackers(7);
        startVideo();
        Lock.pause(8000);
        pauseVideo();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();
        // assert that the impression urls are fired
        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.IMPRESSION_URL_1));
        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.IMPRESSION_URL_2));
        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.START_URL));
        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.FIRST_QUARTILE));
        assertFalse(FakeHttp.httpRequestWasMade(TestUTResponses.MID_POINT_URL));
        assertFalse(FakeHttp.httpRequestWasMade(TestUTResponses.THIRD_QUARTILE_URL));
        assertFalse(FakeHttp.httpRequestWasMade(TestUTResponses.COMPLETE_URL));
        resumeVideo();
        Lock.pause(22000);
        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.THIRD_QUARTILE_URL));
        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.COMPLETE_URL));
    }

    //    @Test
    public void testPauseAndResumeBeforeMidPoint() {
        pendBlankResponsesForTrackers(7);
        startVideo();
        Lock.pause(16000);
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();
        // assert that the impression urls are fired
        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.IMPRESSION_URL_1));
        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.IMPRESSION_URL_2));
        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.START_URL));
        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.FIRST_QUARTILE));
        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.MID_POINT_URL));
        assertFalse(FakeHttp.httpRequestWasMade(TestUTResponses.THIRD_QUARTILE_URL));
        assertFalse(FakeHttp.httpRequestWasMade(TestUTResponses.COMPLETE_URL));
        resumeVideo();
        Lock.pause(16000);
        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.THIRD_QUARTILE_URL));
        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.COMPLETE_URL));

    }

    //    @Test
    public void testPauseAndResumeBeforeThirdQuartile() {
        pendBlankResponsesForTrackers(7);
        startVideo();
        Lock.pause(23000);
        pauseVideo();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();
        // assert that the impression urls are fired
        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.IMPRESSION_URL_1));
        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.IMPRESSION_URL_2));
        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.START_URL));
        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.FIRST_QUARTILE));
        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.MID_POINT_URL));
        assertFalse(FakeHttp.httpRequestWasMade(TestUTResponses.THIRD_QUARTILE_URL));
        assertFalse(FakeHttp.httpRequestWasMade(TestUTResponses.COMPLETE_URL));
        pendBlankResponsesForTrackers(2);
        resumeVideo();
        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.THIRD_QUARTILE_URL));
        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.COMPLETE_URL));

    }

    //    @Test
    public void testPauseAndResumeBeforeComplete() {
        pendBlankResponsesForTrackers(7);
        startVideo();
        Lock.pause(28000);
        pauseVideo();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();
        // assert that the impression urls are fired
        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.IMPRESSION_URL_1));
        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.IMPRESSION_URL_2));
        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.START_URL));
        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.FIRST_QUARTILE));
        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.MID_POINT_URL));
        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.THIRD_QUARTILE_URL));
        assertFalse(FakeHttp.httpRequestWasMade(TestUTResponses.COMPLETE_URL));
        pendBlankResponsesForTrackers(1);
        resumeVideo();
        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.COMPLETE_URL));
    }

    //    @Test
    public void testClickThroughAndClickUrls() {
        pendBlankResponsesForTrackers(10);
        startVideo();
        // click on the view
        videoView.performClick();
        // test click trackers are fired
        // check that launched ad activity is having
    }

    //    @Test
    public void testSkipVideo() {
        // test can't skip before offset
        // after skip skip url is fired
    }

    //    @Test
    public void testMuteButton() {
        startVideo();
        // check by default it's muted
        // perform click on the mute/unmute button
        // check not muted anymore, and unmute event is tracked
        // perform click on the mute/unmute button
        // check muted again, and mute event is tracked
    }


    @Override
    public void tearDown() {
        super.tearDown();
        try {
            server.shutdown();
        } catch (IOException e) {
        }
    }

    // TODO find a shorter video file for faster test
}

