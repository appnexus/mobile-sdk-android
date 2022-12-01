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

package com.appnexus.opensdk;

import static android.os.Looper.getMainLooper;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.webkit.WebView;

import com.appnexus.opensdk.util.Lock;
import com.appnexus.opensdk.util.MockMainActivity;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.StringUtil;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadow.api.Shadow;
import org.robolectric.shadows.ShadowConnectivityManager;
import org.robolectric.shadows.ShadowNetworkInfo;
import org.robolectric.util.ReflectionHelpers;
import org.robolectric.util.Scheduler;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

@Config(sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricTestRunner.class)
public class XandrAdTest {

    private MockMainActivity activity;
    private Scheduler bgScheduler;
    private Scheduler uiScheduler;
    private int sdkVersion;
    private boolean success;

    private ConnectivityManager connectivityManager;
    private ShadowConnectivityManager shadowConnectivityManager;

    private InitListener initListener = new InitListener() {
        @Override
        public void onInitFinished(boolean success) {
            XandrAdTest.this.success = success;
        }
    };

    @Before
    public void setup() {
        XandrAd.reset();
        success = false;
        sdkVersion = ReflectionHelpers.getStaticField(Build.VERSION.class, "SDK_INT");
        Robolectric.getBackgroundThreadScheduler().reset();
        Robolectric.getForegroundThreadScheduler().reset();
        activity = Robolectric.buildActivity(MockMainActivity.class).create().start().resume().visible().get();
        shadowOf(activity).grantPermissions("android.permission.INTERNET");
        connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        // Not using Shadows.shadowOf(connectivityManager) because of Robolectric bug when using API23+
        // See: https://github.com/robolectric/robolectric/issues/1862
        shadowConnectivityManager = (ShadowConnectivityManager) Shadow.extract(connectivityManager);
        bgScheduler = Robolectric.getBackgroundThreadScheduler();
        uiScheduler = Robolectric.getForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        bgScheduler.pause();
        uiScheduler.pause();
    }

    @After
    public void tearDown() {
        activity.finish();
        shadowOf(getMainLooper()).quitUnchecked();
        bgScheduler.reset();
        uiScheduler.reset();
        Settings.getSettings().ua = null;
        ReflectionHelpers.setStaticField(Build.VERSION.class, "SDK_INT", sdkVersion);
        Clog.e("SDK_VERSION", sdkVersion + "");
    }

    @Test
    public void testXandrAdInit() {
        XandrAd.init(10094, activity, false, initListener);
        waitForTasks();
        Robolectric.getBackgroundThreadScheduler().advanceToLastPostedRunnable();
        Robolectric.getForegroundThreadScheduler().advanceToLastPostedRunnable();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(success);
    }

    @Test
    public void testXandrAdInitOffline() {
        success = true; // to make sure that onInitCompleted is triggered with false (success)
        goOffline();
        XandrAd.init(10094, activity, false, initListener);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertFalse(success);
    }

    private void scheduleTimerToCheckForTasks() {
        Timer timer = new Timer();
        final int[] counter = {330};
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                counter[0]--;
                if (uiScheduler.areAnyRunnable() || bgScheduler.areAnyRunnable() || counter[0] == 0) {
                    Lock.unpause();
                    this.cancel();
                }
            }
        }, 0, 100);
    }

    public void waitForTasks() {
        scheduleTimerToCheckForTasks();
        Lock.pause();
    }

    private void goOffline() {
        NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();
        Assert.assertTrue(activeInfo != null && activeInfo.isConnected());

        shadowConnectivityManager.setActiveNetworkInfo(
                ShadowNetworkInfo.newInstance(NetworkInfo.DetailedState.DISCONNECTED, ConnectivityManager.TYPE_MOBILE, 0, true, false)
        );
        NetworkInfo activeInfo2 = connectivityManager.getActiveNetworkInfo();
        Assert.assertTrue(activeInfo2 != null && !activeInfo2.isConnected());
    }
}
