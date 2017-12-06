package com.appnexus.opensdk.instreamvideo;

/**
 * Created by ppuviarasu on 12/5/17.
 */

import com.appnexus.opensdk.instreamvideo.shadows.ShadowAsyncTaskNoExecutor;
import com.appnexus.opensdk.instreamvideo.shadows.ShadowSettings;
import com.appnexus.opensdk.instreamvideo.shadows.ShadowWebSettings;
import com.appnexus.opensdk.instreamvideo.util.RoboelectricTestRunnerWithResources;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowWebView;

/**
 * This tests if the API's in BannerAdView are functioning as expected.
 */
@Config(constants = com.appnexus.opensdk.instreamvideo.BuildConfig.class, sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class,
                ShadowWebView.class, ShadowWebSettings.class, ShadowSettings.class, ShadowLog.class})
@RunWith(RoboelectricTestRunnerWithResources.class)
public class VideoAdTest extends BaseRoboTest {

    VideoAd videoAd;

    @Override
    public void setup() {
        super.setup();
        videoAd = new VideoAd(activity,"12345");
    }

    @Override
    public void tearDown(){
        super.tearDown();
        videoAd = null;
    }

    @Test
    public void testVideoDuration() throws Exception {
        int minDuration = 10;
        int maxDuration = 100;
        videoAd.setVideoAdMinDuration(minDuration);
        videoAd.setVideoAdMaxDuration(maxDuration);

        inspectVideoDuration(minDuration, maxDuration);
    }

    private void inspectVideoDuration(int minDuration, int maxDuration){
        Assert.assertEquals(videoAd.getVideoAdMaxDuration(), maxDuration);

        Assert.assertEquals(videoAd.getVideoAdMinDuration(), minDuration);
    }
}
