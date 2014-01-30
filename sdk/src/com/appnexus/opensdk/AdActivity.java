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
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.StringUtil;
import com.appnexus.opensdk.utils.WebviewUtil;

import java.lang.ref.WeakReference;
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

    private WebView webView;

    // Interstitial Activity
    protected FrameLayout layout;
    private boolean isInterstitial = false;
    private long now;
    private InterstitialAdView adView;
    private static final int CLOSE_BUTTON_MESSAGE_ID = 8000;
    private ImageButton close;
    private boolean close_added = false;


    // Browser Activity
    private ProgressBar progressBar;

    // MRAID Activity
    private boolean isMRAID = false;
    private MRAIDImplementation mraidFullscreenImplementation = null;

    //Intent Keys
    static final String INTENT_KEY_ACTIVITY_TYPE = "ACTIVITY_TYPE";
    static final String ACTIVITY_TYPE_INTERSTITIAL = "INTERSTITIAL";
    static final String ACTIVITY_TYPE_BROWSER = "BROWSER";
    static final String ACTIVITY_TYPE_MRAID = "MRAID";

    @SuppressLint({"InlinedApi", "NewApi"})
    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        String activityType = getIntent().
                getStringExtra(INTENT_KEY_ACTIVITY_TYPE);
        if (StringUtil.isEmpty(activityType)) {
            Clog.e(Clog.baseLogTag, "AdActivity launched with no type");
            finish();
        } else if (activityType.equals(ACTIVITY_TYPE_INTERSTITIAL)) {
            createInterstitialAdActivity();
        } else if (activityType.equals(ACTIVITY_TYPE_BROWSER)) {
            createBrowserActivity();
        } else if (activityType.equals(ACTIVITY_TYPE_MRAID)) {
            createMRAIDAdActivity();
        }

        CookieSyncManager.createInstance(this);
        CookieSyncManager csm = CookieSyncManager.getInstance();
        if (csm != null) csm.startSync();
    }

    private void createInterstitialAdActivity() {
        isInterstitial = true;
        layout = new FrameLayout(this);

        // Lock the orientation
        AdActivity.lockToCurrentOrientation(this);

        setContentView(layout);

        now = getIntent().getLongExtra(InterstitialAdView.INTENT_KEY_TIME,
                System.currentTimeMillis());
        setIAdView(InterstitialAdView.INTERSTITIALADVIEW_TO_USE);
        int closeButtonDelay = getIntent().getIntExtra(
                InterstitialAdView.INTENT_KEY_CLOSE_BUTTON_DELAY,
                Settings.getSettings().DEFAULT_INTERSTITIAL_CLOSE_BUTTON_DELAY);

        // Add a close button after a delay.
        CloseButtonHandler closeButtonHandler = new CloseButtonHandler(this);
        closeButtonHandler.sendEmptyMessageDelayed(CLOSE_BUTTON_MESSAGE_ID, closeButtonDelay);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressWarnings("deprecation")
    @SuppressLint({"SetJavaScriptEnabled", "NewApi"})
    private void createBrowserActivity() {
        setContentView(R.layout.activity_in_app_browser);

        webView = (WebView) findViewById(R.id.web_view);
        final ImageButton back = (ImageButton) findViewById(R.id.browser_back);
        final ImageButton forward = (ImageButton) findViewById(R.id.browser_forward);
        ImageButton openBrowser = (ImageButton) findViewById(R.id.open_browser);
        ImageButton refresh = (ImageButton) findViewById(R.id.browser_refresh);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        // button settings
        back.setEnabled(false);
        forward.setEnabled(false);

        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Drawable play = getResources().getDrawable(android.R.drawable.ic_media_play).mutate();
            back.setScaleX(-1);
            back.setLayoutDirection(ImageButton.LAYOUT_DIRECTION_RTL);
            back.setImageDrawable(play);
        } else {
            back.post(new Runnable() {
                public void run() {
                    Bitmap pbmp = BitmapFactory.decodeResource(getResources(),
                            android.R.drawable.ic_media_play);
                    forward.setImageBitmap(pbmp);
                    Matrix x = new Matrix();
                    back.setScaleType(ImageView.ScaleType.MATRIX);
                    x.postRotate(180.0f);

                    Bitmap rotated = Bitmap.createBitmap(pbmp, 0, 0,
                            pbmp.getWidth(), pbmp.getHeight(), x, true);
                    back.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    forward.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    back.setImageBitmap(rotated);
                }
            });
        }

        String url = getIntent().getStringExtra("url");
        String id = getIntent().getStringExtra("bridgeid");

        if (id != null) {
            AdView.BrowserStyle style = null;
            for (Pair<String, AdView.BrowserStyle> p : AdView.BrowserStyle.bridge) {
                if (p.first.equals(id)) {
                    style = p.second;
                    AdView.BrowserStyle.bridge.remove(p);
                }
            }
            if (style != null) {
                if (sdk >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    back.setBackground(style.backButton);
                    forward.setBackground(style.forwardButton);
                    refresh.setBackground(style.refreshButton);
                } else {
                    back.setBackgroundDrawable(style.backButton);
                    forward.setBackgroundDrawable(style.forwardButton);
                    refresh.setBackgroundDrawable(style.refreshButton);
                }
            }
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.goBack();
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.goForward();
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.reload();
            }
        });

        openBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Clog.d(Clog.baseLogTag,
                        Clog.getString(R.string.opening_native_current));
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(webView
                        .getUrl()));
                startActivity(i);
                finish();
            }
        });

        // webView settings
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http")) {
                    Clog.d(Clog.baseLogTag,
                            Clog.getString(R.string.opening_url, url));
                    return false;
                } else {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    try {
                        startActivity(i);
                        finish();
                    } catch (ActivityNotFoundException e) {
                        Clog.w(Clog.browserLogTag,
                                Clog.getString(R.string.opening_url_failed, url));
                    }
                    return true;
                }
            }

            @Override
            public void onPageFinished(WebView webview, String url) {
                back.setEnabled(webview.canGoBack());
                forward.setEnabled(webview.canGoForward());

                CookieSyncManager csm = CookieSyncManager.getInstance();
                if (csm != null) {
                    csm.sync();
                }
            }
        });

        webView.setWebChromeClient(new VideoEnabledWebChromeClient(this) {
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                super.onShowCustomView(view, callback);
                if (view instanceof FrameLayout) {
                    FrameLayout frame = (FrameLayout) view;
                    if (frame.getFocusedChild() instanceof VideoView) {
                        VideoView video = (VideoView) frame.getFocusedChild();
                        frame.removeView(video);
                        ((Activity) webView.getContext()).setContentView(video);
                        video.start();
                    }
                }
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Clog.w(Clog.browserLogTag,
                        Clog.getString(R.string.console_message,
                                consoleMessage.message(),
                                consoleMessage.lineNumber(),
                                consoleMessage.sourceId()));
                return true;
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                Clog.w(Clog.browserLogTag,
                        Clog.getString(R.string.js_alert, message, url));
                result.confirm();
                return true;
            }

            @Override
            public void onProgressChanged(WebView view, int progress) {
                if ((progress < 100)
                        && (progressBar.getVisibility() == ProgressBar.GONE)) {
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                }
                progressBar.setProgress(progress);
                if (progress == 100) {
                    progressBar.setVisibility(ProgressBar.GONE);
                }
            }
        });

        webView.loadUrl(url);
    }

    private void createMRAIDAdActivity() {
        isMRAID = true;
        if ((AdView.mraidFullscreenContainer == null) || (AdView.mraidFullscreenImplementation == null)) {
            Clog.e(Clog.baseLogTag, "Launched MRAID Fullscreen activity with invalid properties");
            finish();
            return;
        }

        // remove from any old parents to be safe
        if (AdView.mraidFullscreenContainer.getParent() != null) {
            ((ViewGroup) AdView.mraidFullscreenContainer.getParent())
                    .removeView(AdView.mraidFullscreenContainer);
        }
        setContentView(AdView.mraidFullscreenContainer);
        if (AdView.mraidFullscreenContainer.getChildAt(0) instanceof WebView) {
            webView = (WebView) AdView.mraidFullscreenContainer.getChildAt(0);
        }
        mraidFullscreenImplementation = AdView.mraidFullscreenImplementation;
        mraidFullscreenImplementation.setFullscreenActivity(this);

        if (AdView.mraidFullscreenListener != null) {
            AdView.mraidFullscreenListener.onCreateCompleted();
        }
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
        if (av != null) {
            av.setAdActivity(this);

            if (layout != null) {
                layout.setBackgroundColor(av.getBackgroundColor());
                layout.removeAllViews();
                if (av.getParent() != null) {
                    ((ViewGroup) av.getParent()).removeAllViews();
                }
                Pair<Long, Displayable> p = av.getAdQueue().poll();
                while ((p != null) && (p.second != null)
                        && ((now - p.first) > InterstitialAdView.MAX_AGE)) {
                    Clog.w(Clog.baseLogTag, Clog.getString(R.string.too_old));
                    p = av.getAdQueue().poll();
                }
                if ((p == null) || (p.second == null)
                        || !(p.second.getView() instanceof WebView))
                    return;
                webView = (WebView) p.second.getView();
                layout.addView(webView);
            }
        }

        adView = av;
    }

    static void lockToCurrentOrientation(Activity a) {
        final int orientation = a.getResources().getConfiguration().orientation;
        setOrientation(a, orientation);
    }

    protected static void unlockOrientation(Activity a) {
        a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
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
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
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
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
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
        WebviewUtil.onPause(webView);
        CookieSyncManager csm = CookieSyncManager.getInstance();
        if (csm != null) {
            csm.stopSync();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        WebviewUtil.onResume(webView);
        CookieSyncManager csm = CookieSyncManager.getInstance();
        if (csm != null) {
            csm.startSync();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // handling mraid fullscreen rotations
        if (isMRAID) {
            super.onDestroy();
            return;
        }

        if (webView != null) {
            if (webView.getParent() != null)
                ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
        }

        if (isInterstitial && adView != null) {
            adView.close_button = null;
            adView.setAdActivity(null);
        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (isMRAID) {
            if (mraidFullscreenImplementation != null) {
                mraidFullscreenImplementation.setFullscreenActivity(null);
                mraidFullscreenImplementation.close();
            }
            mraidFullscreenImplementation = null;
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }
}
