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
import android.content.Intent;
import android.content.MutableContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.*;
import android.widget.*;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.StringUtil;
import com.appnexus.opensdk.utils.ViewUtil;

import java.util.LinkedList;

public class BrowserAdActivity implements AdActivity.AdActivityImplementation {
    public static LinkedList<WebView> BROWSER_QUEUE = new LinkedList<WebView>();
    private Activity adActivity;
    private WebView webView;

    public BrowserAdActivity(Activity adActivity) {
        this.adActivity = adActivity;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressWarnings("deprecation")
    @SuppressLint({"SetJavaScriptEnabled", "NewApi"})
    @Override
    public void create() {
        adActivity.setContentView(R.layout.activity_in_app_browser);

        webView = BROWSER_QUEUE.poll();
        if ((webView == null) || (webView.getSettings() == null)) {
            finishAdActivity();
            return;
        }
        if(webView.getContext() instanceof MutableContextWrapper) {
            ((MutableContextWrapper) webView.getContext()).setBaseContext(adActivity);
        }
        WebView webViewSpace = (WebView) adActivity.findViewById(R.id.web_view);
        ViewGroup.LayoutParams spaceParams = webViewSpace.getLayoutParams();
        ViewGroup webViewSpaceParent = ((ViewGroup) webViewSpace.getParent());
        int index = webViewSpaceParent.indexOfChild(webViewSpace);
        webViewSpaceParent.removeView(webViewSpace);
        // remove from any parents, just in case
        ViewUtil.removeChildFromParent(webView);
        webView.setLayoutParams(spaceParams);
        webViewSpaceParent.addView(webView, index);

        final ImageButton back = (ImageButton) adActivity.findViewById(R.id.browser_back);
        final ImageButton forward = (ImageButton) adActivity.findViewById(R.id.browser_forward);
        ImageButton openBrowser = (ImageButton) adActivity.findViewById(R.id.open_browser);
        ImageButton refresh = (ImageButton) adActivity.findViewById(R.id.browser_refresh);
        final ProgressBar progressBar = (ProgressBar) adActivity.findViewById(R.id.progress_bar);

        // button settings
        back.setEnabled(false);
        forward.setEnabled(false);

        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Drawable play = adActivity.getResources().getDrawable(android.R.drawable.ic_media_play).mutate();
            back.setScaleX(-1);
            back.setLayoutDirection(ImageButton.LAYOUT_DIRECTION_RTL);
            back.setImageDrawable(play);
        } else {
            back.post(new Runnable() {
                public void run() {
                    Bitmap pbmp = BitmapFactory.decodeResource(adActivity.getResources(),
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

        String id = adActivity.getIntent().getStringExtra("bridgeid");

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
                Clog.d(Clog.browserLogTag,
                        Clog.getString(R.string.opening_native_current));
                openNativeIntent(webView.getUrl());
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Clog.v(Clog.browserLogTag,
                        Clog.getString(R.string.opening_url, url));
                if (url.startsWith("http")) {
                    return false;
                } else {
                    openNativeIntent(url);
                    return true;
                }
            }

            @Override
            public void onPageFinished(WebView webview, String url) {
                back.setEnabled(webview.canGoBack());
                forward.setEnabled(webview.canGoForward());

                CookieSyncManager csm = CookieSyncManager.getInstance();
                if (csm != null) csm.sync();
            }
        });

        webView.setWebChromeClient(new VideoEnabledWebChromeClient(adActivity) {
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
    }

    private void openNativeIntent(String url) {
        Uri launch_url = StringUtil.isEmpty(url) ? null :  Uri.parse(url);

        if (launch_url == null) {
            Clog.w(Clog.browserLogTag,
                    Clog.getString(R.string.opening_url_failed, url));
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW, launch_url);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            adActivity.startActivity(i);
            destroy();
            finishAdActivity();
        } catch (ActivityNotFoundException e) {
            Clog.w(Clog.browserLogTag,
                    Clog.getString(R.string.opening_url_failed, url));
        }
    }

    @Override
    public void backPressed() {

    }

    @Override
    public void destroy() {
        if (webView == null) {
            return;
        }
        ViewUtil.removeChildFromParent(webView);
        webView.destroy();
    }

    @Override
    public void interacted() {
        // do nothing
    }

    @Override
    public void browserLaunched() {
        // do nothing
    }

    @Override
    public WebView getWebView() {
        return webView;
    }

    private void finishAdActivity() {
        adActivity.finish();
    }
}
