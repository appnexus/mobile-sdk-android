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

import android.os.Build;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.appnexus.opensdk.tasksmanager.TasksManager;
import com.appnexus.opensdk.util.MockMainActivity;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.StringUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.util.ReflectionHelpers;
import org.robolectric.util.Scheduler;

import static android.os.Looper.getMainLooper;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

@Config(sdk = Build.VERSION_CODES.M)
@RunWith(RobolectricTestRunner.class)
public class SDKSettingsTest {

    private MockMainActivity activity;
    private Scheduler bgScheduler;
    private Scheduler uiScheduler;
    private int sdkVersion;

    @Before
    public void setup() {
        sdkVersion = ReflectionHelpers.getStaticField(Build.VERSION.class, "SDK_INT");
        Robolectric.getBackgroundThreadScheduler().reset();
        Robolectric.getForegroundThreadScheduler().reset();
        activity = Robolectric.buildActivity(MockMainActivity.class).create().start().resume().visible().get();
        shadowOf(activity).grantPermissions("android.permission.INTERNET");
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
    public void testSDKVersion() {
        assertNotNull(SDKSettings.getSDKVersion());
//        assertEquals(SDKSettings.getSDKVersion(),  BuildConfig.VERSION_NAME);
    }

    @Test
    public void testSDKSettingsInitBelowJELLY_BEAN() {
        ReflectionHelpers.setStaticField(Build.VERSION.class, "SDK_INT", 16);
        assertTrue(StringUtil.isEmpty(Settings.getSettings().ua));
        SDKSettings.init(activity, null);
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        WebView webView = new WebView(activity);
        assertNotNull(Settings.getSettings().ua);
        assertEquals(Settings.getSettings().ua, (webView.getSettings().getUserAgentString()));
    }

    @Test
    public void testSDKSettingsInitAboveHONEY_COMB() {
        ReflectionHelpers.setStaticField(Build.VERSION.class, "SDK_INT", 18);
        assertTrue(StringUtil.isEmpty(Settings.getSettings().ua));
        SDKSettings.init(activity, null);
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        WebView userAgentWebView = new WebView(activity);
        assertNotNull(Settings.getSettings().ua);
        assertNotEquals(Settings.getSettings().ua, (userAgentWebView.getSettings().getUserAgentString()));
    }
}
