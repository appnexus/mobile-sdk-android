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

package com.appnexus.opensdk.instreamvideo;

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
 * This tests if the API's in VideoAd are functioning as expected.
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
        videoAd.setAdMinDuration(minDuration);
        videoAd.setAdMaxDuration(maxDuration);

        inspectVideoDuration(minDuration, maxDuration);
    }

    private void inspectVideoDuration(int minDuration, int maxDuration){
        Assert.assertEquals(videoAd.getAdMaxDuration(), maxDuration);

        Assert.assertEquals(videoAd.getAdMinDuration(), minDuration);
    }
}
