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
import android.os.*;
import android.util.Pair;
import android.view.*;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.WebviewUtil;

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

    FrameLayout layout;
    private WebView webView;
    private long now;
    private boolean close_added = false;
    private static Activity current_ad_activity = null;
    private InterstitialAdView adView;

    static Activity getCurrent_ad_activity() {
        return current_ad_activity;
    }

    private static void setCurrent_ad_activity(Activity current_ad_activity) {
        AdActivity.current_ad_activity = current_ad_activity;
    }

    @SuppressLint({"InlinedApi", "NewApi"})
    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        setCurrent_ad_activity(this);

        layout = new FrameLayout(this);

        // Lock the orientation
        AdActivity.lockOrientation(this);

        setContentView(layout);

        setIAdView(InterstitialAdView.INTERSTITIALADVIEW_TO_USE);
        now = getIntent().getLongExtra(InterstitialAdView.INTENT_KEY_TIME,
                System.currentTimeMillis());
        int closeButtonDelay = getIntent().getIntExtra(
                InterstitialAdView.INTENT_KEY_CLOSE_BUTTON_DELAY,
                Settings.getSettings().DEFAULT_INTERSTITIAL_CLOSE_BUTTON_DELAY);

        // Add a close button after a 10 second delay.
        closeButtonHandler.sendEmptyMessageDelayed(0, closeButtonDelay);
    }


    private final Handler closeButtonHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.obj instanceof FrameLayout) {
                if ((adView != null) && adView.interacted) {
                    addCloseButton();
                }
            }
        }
    };

    protected void finishIfNoInteraction() {
        if ((adView != null) && !adView.interacted) {
            finish();
        }

    }

    void handleMRAIDCollapse(MRAIDWebView m) {
        layout.addView(m);
    }

    void addCloseButton() {
        if (close_added) {
            return;
        }
        close_added = true;
        final ImageButton close = new ImageButton(this);
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
    static void lockOrientation(Activity a) {
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
        final int orientation = a.getResources().getConfiguration().orientation;

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

    @Override
    protected void onPause() {
        if (webView != null) WebviewUtil.onPause(webView);
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (webView != null) WebviewUtil.onResume(webView);
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
            adView.close = null;
        }
        super.onDestroy();
    }
}
