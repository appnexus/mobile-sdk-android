package com.appnexus.opensdk;

import android.util.Log;

import com.appnexus.opensdk.adresponsedata.BaseAdResponse;
import com.appnexus.opensdk.shadows.ShadowAsyncTaskNoExecutor;
import com.appnexus.opensdk.util.Lock;
import com.appnexus.opensdk.utils.Settings;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@Config(constants = BuildConfig.class, sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class})
@RunWith(RobolectricGradleTestRunner.class)
public class VideoImpressionTrackingTest extends BaseRoboTest {
    MockWebServer server;
    boolean serverStarted = false;

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
    public void testVideoRendered() throws InterruptedException {
        if (serverStarted) {
            server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponses.UTV2()));
            InterstitialAdView iav = new InterstitialAdView(activity);
            iav.setPlacementID("1");
            // Clear AAID
            Robolectric.flushForegroundThreadScheduler();
            Robolectric.flushBackgroundThreadScheduler();
            iav.loadAd();
            waitForTasks();
            Robolectric.flushForegroundThreadScheduler();
            Robolectric.flushBackgroundThreadScheduler();
            Lock.pause(1000);

            Robolectric.flushForegroundThreadScheduler(); // request manager receives ut response

            Robolectric.flushBackgroundThreadScheduler(); // parse VAST response in the background thread

            Robolectric.flushForegroundThreadScheduler(); // loads AdModel and initiate video view

            Robolectric.flushBackgroundThreadScheduler();
            Robolectric.flushForegroundThreadScheduler();

            RecordedRequest recordedRequest = server.takeRequest();
            assertNotNull(recordedRequest);
            iav.getAdDispatcher().onAdLoaded(new AdResponse() {
                @Override
                public MediaType getMediaType() {
                    return MediaType.INTERSTITIAL;
                }

                @Override
                public boolean isMediated() {
                    return false;
                }

                @Override
                public Displayable getDisplayable() {
                    return new VastVideoView(activity, null);
                }

                @Override
                public NativeAdResponse getNativeAdResponse() {
                    return null;
                }

                @Override
                public BaseAdResponse getResponseData() {
                    return null;
                }

                @Override
                public void destroy() {

                }
            });
        }

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

