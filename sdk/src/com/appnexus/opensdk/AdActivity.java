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

package com.appnexus.opensdk;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.WindowManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.StringUtil;
import com.appnexus.opensdk.utils.WebviewUtil;

import java.util.Locale;

/**
 * This is the main ad activity.  You must add a reference to this to
 * your app's AndroidManifest.xml file:
 * <pre>
 * {@code
 * <application>
 *   <activity android:name="com.appnexus.opensdk.AdActivity" />
 * </application>
 * }
 * </pre>
 */
public class AdActivity extends Activity {

    interface AdActivityImplementation {
        void create();
        void backPressed();
        void destroy();
        void interacted();
        void browserLaunched();
        WebView getWebView();
    }

    private AdActivityImplementation implementation;

    //Intent Keys
    public static final String INTENT_KEY_ACTIVITY_TYPE = "ACTIVITY_TYPE";
    static final String ACTIVITY_TYPE_INTERSTITIAL = "INTERSTITIAL";
    public static final String ACTIVITY_TYPE_BROWSER = "BROWSER";
    static final String ACTIVITY_TYPE_MRAID = "MRAID";

    static Class overrideActivity = AdActivity.class;

    public static Class getActivityClass() {
        return overrideActivity;
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        String activityType = getIntent().
                getStringExtra(INTENT_KEY_ACTIVITY_TYPE);
        if (StringUtil.isEmpty(activityType)) {
            Clog.e(Clog.baseLogTag, Clog.getString(R.string.adactivity_no_type));
            finish();
        } else if (ACTIVITY_TYPE_INTERSTITIAL.equals(activityType)) {
            implementation = new InterstitialAdActivity(this);
            implementation.create();
        } else if (ACTIVITY_TYPE_BROWSER.equals(activityType)) {
            implementation = new BrowserAdActivity(this);
            implementation.create();
        } else if (ACTIVITY_TYPE_MRAID.equals(activityType)) {
            implementation = new MRAIDAdActivity(this);
            implementation.create();
        }

        CookieSyncManager.createInstance(this);
        CookieSyncManager csm = CookieSyncManager.getInstance();
        if (csm != null) csm.startSync();
    }

    @Override
    protected void onPause() {
        if (implementation != null) {
            WebviewUtil.onPause(implementation.getWebView());
        }
        CookieSyncManager csm = CookieSyncManager.getInstance();
        if (csm != null) csm.stopSync();
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (implementation != null) {
            WebviewUtil.onResume(implementation.getWebView());
        }
        CookieSyncManager csm = CookieSyncManager.getInstance();
        if (csm != null) csm.startSync();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (implementation != null) {
            implementation.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (implementation != null) {
            implementation.backPressed();
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    protected void interacted() {
        if (implementation != null) {
            implementation.interacted();
        }
    }

    // Static methods for locking orientation

    protected static void lockToCurrentOrientation(Activity a) {
        final int orientation = a.getResources().getConfiguration().orientation;
        setOrientation(a, orientation);
    }

    protected static void lockToConfigOrientation(Activity a, int orientation) {
        setOrientation(a, orientation);
    }

    protected static void unlockOrientation(Activity a) {
        a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private static void setOrientation(Activity a, int orientation)
    {
        boolean  isKindleFireHD  = false;  // Fix an accelerometer bug with kindle fire HDs

        String  device   = Settings.getSettings() .deviceModel .toUpperCase(Locale.US);
        String  make     = Settings.getSettings() .deviceMake  .toUpperCase(Locale.US);

        if (        make.equals("AMAZON")
                && (device.equals("KFTT") || device.equals("KFJWI") || device.equals("KFJWA")) )
        {
            isKindleFireHD = true;
        }

        if( (a != null) && !a.isFinishing() )
        {
            Display  d         = ((WindowManager) a.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            int      rotation  = d.getRotation();


            if (orientation == Configuration.ORIENTATION_PORTRAIT)
            {
                if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
                    a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                } else {
                    if (rotation == android.view.Surface.ROTATION_180) {
                        a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                    } else {
                        a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    }
                }
            }

            else if (orientation == Configuration.ORIENTATION_LANDSCAPE)
            {
                if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
                    a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                } else {
                    if (isKindleFireHD) {
                        if (rotation == android.view.Surface.ROTATION_0 || rotation == android.view.Surface.ROTATION_90) {
                            a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                        } else {
                            a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                        }

                    } else {
                        if (rotation == android.view.Surface.ROTATION_0 || rotation == android.view.Surface.ROTATION_90) {
                            a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        } else {
                            a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                        }
                    }
                }
            } //endif -- orientation is PORTRAIT; else LANDSCAPE
        } //endif -- a && a
    }

    enum OrientationEnum {
        portrait,
        landscape,
        none
    }

    protected static void lockToMRAIDOrientation(Activity a, OrientationEnum e) {
        int orientation = a.getResources().getConfiguration().orientation;

        switch (e) {
            // none is currently never passed
            case none:
                a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                return;
            case landscape:
                orientation = Configuration.ORIENTATION_LANDSCAPE;
                break;
            case portrait:
                orientation = Configuration.ORIENTATION_PORTRAIT;
                break;
        }

        setOrientation(a, orientation);
    }
}
