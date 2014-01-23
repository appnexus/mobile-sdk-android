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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;
import android.view.*;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.StringUtil;
import com.appnexus.opensdk.utils.WebviewUtil;

import java.lang.ref.WeakReference;
import java.util.Locale;

/**
 * This is the main ad activity.  You must add a reference to this to
 * your app's AndroidManifest.xml file.  {@link BrowserActivity} also
 * needs to be added allow the in-app browser functionality:
 * <pre>
 * {@code
 * <application>
 *   <activity android:name="com.appnexus.opensdk.AdActivity" />
 *   <activity android:name="com.appnexus.opensdk.BrowserActivity" />
 * </application>
 * }
 * </pre>
 */
public class AdActivity extends Activity {

    protected FrameLayout layout;
    private WebView webView;
    private long now;
    private boolean close_added = false;
    private static AdActivity current_ad_activity = null;
    private static AdActivity mraidFullscreenActivity = null;
    private MRAIDImplementation mraidFullscreenImplementation = null;
    private InterstitialAdView adView;
    static final int CLOSE_BUTTON_MESSAGE_ID = 8000;

    static AdActivity getCurrent_ad_activity() {
        return current_ad_activity;
    }

    private static void setCurrent_ad_activity(AdActivity current_ad_activity) {
        AdActivity.current_ad_activity = current_ad_activity;
    }

    public static AdActivity getMraidFullscreenActivity() {
        return mraidFullscreenActivity;
    }

    public static void setMraidFullscreenActivity(AdActivity mraidFullscreenActivity) {
        AdActivity.mraidFullscreenActivity = mraidFullscreenActivity;
    }

    @SuppressLint({"InlinedApi", "NewApi"})
    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        String activityType = getIntent().
                getStringExtra(InterstitialAdView.INTENT_KEY_ACTIVITY_TYPE);
        if (StringUtil.isEmpty(activityType)) {
            Clog.e(Clog.baseLogTag, "AdActivity launched with no type");
            finish();
        } else if (activityType.equals(InterstitialAdView.ACTIVITY_TYPE_INTERSTITIAL)) {
            setCurrent_ad_activity(this);

            layout = new FrameLayout(this);

            // Lock the orientation
            AdActivity.lockToCurrentOrientation(this);

            setContentView(layout);

            setIAdView(InterstitialAdView.INTERSTITIALADVIEW_TO_USE);
            now = getIntent().getLongExtra(InterstitialAdView.INTENT_KEY_TIME,
                    System.currentTimeMillis());
            int closeButtonDelay = getIntent().getIntExtra(
                    InterstitialAdView.INTENT_KEY_CLOSE_BUTTON_DELAY,
                    Settings.getSettings().DEFAULT_INTERSTITIAL_CLOSE_BUTTON_DELAY);

            // Add a close button after a delay.
            closeButtonHandler.sendEmptyMessageDelayed(CLOSE_BUTTON_MESSAGE_ID, closeButtonDelay);
        } else if (activityType.equals(InterstitialAdView.ACTIVITY_TYPE_MRAID)) {
            setMraidFullscreenActivity(this);
            setContentView(AdView.mraidFullscreenContainer);
            mraidFullscreenImplementation = AdView.mraidFullscreenImplementation;
            AdView.mraidFullscreenContainer = null;
            AdView.mraidFullscreenImplementation = null;
        }

        CookieSyncManager.createInstance(this);
        CookieSyncManager csm = CookieSyncManager.getInstance();
        if (csm != null) csm.startSync();
    }

    /**
     * Keep a weak reference to the AdActivity to prevent circular dependency
     * between handler and Activity
     *
     */
    static class CloseButtonHandler extends Handler {
        WeakReference<AdActivity> activity_weak;
        public CloseButtonHandler(AdActivity activity) {
            activity_weak = new WeakReference<AdActivity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            AdActivity activity = activity_weak.get();
            if (activity != null && msg.what == CLOSE_BUTTON_MESSAGE_ID) {
                activity.addCloseButton();
            }
        }
    }

    private final CloseButtonHandler closeButtonHandler = new CloseButtonHandler(this);

    protected void finishIfNoInteraction() {
        if ((adView != null) && !adView.interacted) {
            finish();
        }

    }

    void handleMRAIDCollapse(MRAIDWebView m) {
        layout.addView(m);
    }

    ImageButton close;
    void addCloseButton() {
        if (layout == null) return;

        if ((close != null) && close_added) {
            if (close.getParent() == null) {
                layout.addView(close);
            }
            close.setVisibility(View.VISIBLE);
            return;
        }

        close = new ImageButton(this);
        close_added = true;
        close.setImageDrawable(getResources().getDrawable(
                android.R.drawable.ic_menu_close_clear_cancel));
        FrameLayout.LayoutParams blp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.RIGHT
                | Gravity.TOP);
        close.setLayoutParams(blp);
        close.setBackgroundColor(Color.TRANSPARENT);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //If this button is being added because the ad was clicked, and the ad is an expandable mraid ad, don't show the button until the ad is collapsed
        if(adView.isMRAIDExpanded()){
            close.setVisibility(View.GONE);
        }
        layout.addView(close);
    }

    private void setIAdView(InterstitialAdView av) {
        if (layout != null) {
            layout.setBackgroundColor(av.getBackgroundColor());
            layout.removeAllViews();
            if (av.getParent() != null) {
                ((ViewGroup) av.getParent()).removeAllViews();
            }
            Pair<Long, Displayable> p = InterstitialAdView.q.poll();
            while (p != null && p.second != null
                    && now - p.first > InterstitialAdView.MAX_AGE) {
                Clog.w(Clog.baseLogTag, Clog.getString(R.string.too_old));
                p = InterstitialAdView.q.poll();
            }
            if ((p == null) || (p.second == null)
                    || !(p.second.getView() instanceof WebView))
                return;
            webView = (WebView) p.second.getView();
            layout.addView(webView);
        }

        if (av != null) {
            av.setAdActivity(this);
        }
        adView = av;
    }

    @SuppressLint({"InlinedApi", "DefaultLocale"})
    static void lockToCurrentOrientation(Activity a) {
        final int orientation = a.getResources().getConfiguration().orientation;
        setOrientation(a, orientation);
    }

    protected static void unlockOrientation(Activity a) {
        a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    private static void setOrientation(Activity a, int orientation) {
        // Fix an accelerometer bug with kindle fire HDs
        boolean isKindleFireHD = false;
        String device = Settings.getSettings().deviceModel
                .toUpperCase(Locale.US);
        String make = Settings.getSettings().deviceMake.toUpperCase(Locale.US);
        if (make.equals("AMAZON")
                && (device.equals("KFTT") || device.equals("KFJWI") || device
                .equals("KFJWA"))) {
            isKindleFireHD = true;
        }
        Display d = ((WindowManager) a.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay();

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.FROYO) {
                a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                int rotation = d.getRotation();
                if (rotation == android.view.Surface.ROTATION_90
                        || rotation == android.view.Surface.ROTATION_180) {
                    a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                } else {
                    a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.FROYO) {
                a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                int rotation = d.getRotation();
                if (!isKindleFireHD) {
                    if (rotation == android.view.Surface.ROTATION_0
                            || rotation == android.view.Surface.ROTATION_90) {
                        a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    } else {
                        a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    }
                } else {
                    if (rotation == android.view.Surface.ROTATION_0
                            || rotation == android.view.Surface.ROTATION_90) {
                        a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    } else {
                        a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }
                }
            }
        }
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

    @Override
    protected void onPause() {
        if (webView != null) WebviewUtil.onPause(webView);
        CookieSyncManager csm = CookieSyncManager.getInstance();
        if (csm != null) {
            csm.stopSync();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (webView != null) WebviewUtil.onResume(webView);
        CookieSyncManager csm = CookieSyncManager.getInstance();
        if (csm != null) {
            csm.startSync();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            if (webView.getParent() != null)
                ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
        }
        if (adView != null) {
            adView.close_button = null;
        }

        super.onDestroy();
    }

    protected void MRAIDClose() {
        mraidFullscreenImplementation = null;
        this.finish();
    }

    @Override
    public void onBackPressed() {
        if (this == mraidFullscreenActivity) {
            mraidFullscreenActivity = null;
            if (mraidFullscreenImplementation != null) {
                mraidFullscreenImplementation.close();
            }
            mraidFullscreenImplementation = null;
        }
        super.onBackPressed();
    }
}
