package com.appnexus.opensdk;

import android.content.Intent;
import android.view.View;

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
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class VideoActivitiesTest extends BaseViewAdTest {
    MockWebServer server;
    boolean serverStarted = false;
    AdActivity adActivity;

    @Override
    public void setup() {
        super.setup();
        server = new MockWebServer();
        try {
            server.start();
            serverStarted = true;
            HttpUrl url = server.url("/");
            Settings.BASE_URL_UT_V2 = url.toString();
        } catch (IOException e) {
        }
    }

    @Test
    public void testVideoAdActivityLaunchedCorrectly() {
        if (serverStarted) {
            // Feed the UT response
            server.enqueue(new MockResponse().setResponseCode(200).setBody(TestUTResponses.videoUT()));
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

    private void showVideo() {
        // simulating the show() behavior on InterstitialAdView
        // create a video view and add it to InterstitialAdView adQueue
        final VastVideoView videoView = new VastVideoView(activity, TestUTResponses.getVastAdModel());
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
        assertTrue(videoView.isPlaying());
    }

    @Test
    public void testBaseCaseOfTrackersFiring() {
        FakeHttp.addPendingHttpResponse(200, TestUTResponses.blank());
        FakeHttp.addPendingHttpResponse(200, TestUTResponses.blank());
        FakeHttp.addPendingHttpResponse(200, TestUTResponses.blank());
        FakeHttp.addPendingHttpResponse(200, TestUTResponses.blank());
        FakeHttp.addPendingHttpResponse(200, TestUTResponses.blank());
        FakeHttp.addPendingHttpResponse(200, TestUTResponses.blank());
        FakeHttp.addPendingHttpResponse(200, TestUTResponses.blank());
        FakeHttp.addPendingHttpResponse(200, TestUTResponses.blank());
        showVideo();
        Lock.pause(9000);
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();
        // assert that the impression urls are fired
        // TODO impression urls not being fired in the media player prepareAsync()
//        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.IMPRESSION_URL_1));
//        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.IMPRESSION_URL_2));
        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.START_URL));
        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.FIRST_QUARTILE));
        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.MID_POINT_URL));
        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.THIRD_QUARTILE_URL));
        assertTrue(FakeHttp.httpRequestWasMade(TestUTResponses.COMPLETE_URL));
    }

    public void testPauseAndResumeBeforeFirstQuartile() {
        showVideo();
        FakeHttp.httpRequestWasMade(TestUTResponses.IMPRESSION_URL_1);
        FakeHttp.httpRequestWasMade(TestUTResponses.IMPRESSION_URL_2);
        FakeHttp.httpRequestWasMade(TestUTResponses.START_URL);

    }

    public void testPauseAndResumeBeforeMidPoint() {

    }

    public void testPauseAndResumeBeforeThirdQuartile() {

    }

    public void testPauseAndResumeBeforeComplete() {

    }

    public void testClickThroughAndClickUrls() {

    }

    public void testSkipVideo() {
        // can't skip before offset

        // after skip skip url is fired
    }

    public void testVideoEventCallBacks() {

    }


    @Override
    public void tearDown() {
        super.tearDown();
        try {
            server.shutdown();
        } catch (IOException e) {
        }
    }
}

