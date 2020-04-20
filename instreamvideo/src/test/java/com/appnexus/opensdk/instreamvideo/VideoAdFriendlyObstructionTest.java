/*
 *    Copyright 2020 APPNEXUS INC
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

import android.view.View;

import com.appnexus.opensdk.SDKSettings;
import com.appnexus.opensdk.instreamvideo.shadows.ShadowAsyncTaskNoExecutor;
import com.appnexus.opensdk.instreamvideo.shadows.ShadowCustomWebView;
import com.appnexus.opensdk.instreamvideo.shadows.ShadowSettings;
import com.appnexus.opensdk.instreamvideo.shadows.ShadowWebSettings;
import com.appnexus.opensdk.instreamvideo.util.Lock;
import com.squareup.okhttp.mockwebserver.MockResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * This tests if the Friendly Obstruction API's in VideoAd are functioning as expected.
 */
@Config(sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class, ShadowWebSettings.class, ShadowCustomWebView.class, ShadowSettings.class, ShadowLog.class})
@RunWith(RobolectricTestRunner.class)
public class VideoAdFriendlyObstructionTest extends BaseRoboTest {

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
    public void testBannerAddFriendlyObstruction() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestUTResponses.video())); // First queue a regular HTML banner response
        assertFriendlyObstruction(videoAd, 0);
        SDKSettings.setOMEnabled(true);
        View v1 = new View(activity);
        View v2 = new View(activity);
        View v3 = new View(activity);
        videoAd.addFriendlyObstruction(v1);
        videoAd.addFriendlyObstruction(v2);
        videoAd.addFriendlyObstruction(v3);
        executeVideoRequest();
        assertFriendlyObstruction(videoAd, 3);
    }

    @Test
    public void testBannerAddAndRemoveFriendlyObstruction() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestUTResponses.video())); // First queue a regular HTML banner response
        assertFriendlyObstruction(videoAd, 0);
        SDKSettings.setOMEnabled(true);
        View v1 = new View(activity);
        View v2 = new View(activity);
        View v3 = new View(activity);
        View v4 = new View(activity);
        videoAd.addFriendlyObstruction(v1);
        videoAd.addFriendlyObstruction(v2);
        videoAd.addFriendlyObstruction(v3);
        videoAd.addFriendlyObstruction(v4);
        executeVideoRequest();
        assertFriendlyObstruction(videoAd, 4);
        videoAd.removeFriendlyObstruction(v1);
        assertFriendlyObstruction(videoAd, 3);
    }

    @Test
    public void testBannerAddAndRemoveAllFriendlyObstruction() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestUTResponses.video())); // First queue a regular HTML banner response
        assertFriendlyObstruction(videoAd, 0);
        SDKSettings.setOMEnabled(true);
        View v1 = new View(activity);
        View v2 = new View(activity);
        View v3 = new View(activity);
        View v4 = new View(activity);
        videoAd.addFriendlyObstruction(v1);
        videoAd.addFriendlyObstruction(v2);
        videoAd.addFriendlyObstruction(v3);
        videoAd.addFriendlyObstruction(v4);
        executeVideoRequest();
        assertFriendlyObstruction(videoAd, 4);
        videoAd.removeAllFriendlyObstructions();
        assertFriendlyObstruction(videoAd, 0);
    }

    private void assertFriendlyObstruction(VideoAd adView, int count) {
        try {
            ArrayList<WeakReference<View>> viewList = adView.getFriendlyObstructionList();
            assertEquals(count, viewList.size());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void executeVideoRequest() {
        videoAd.loadAd();
        Lock.pause(1000);
        waitForTasks();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();

        waitForTasks();
        Robolectric.getBackgroundThreadScheduler().advanceToNextPostedRunnable();
        Robolectric.getForegroundThreadScheduler().advanceToNextPostedRunnable();
    }

}
