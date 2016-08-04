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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.ViewUtil;


class VideoEnabledWebChromeClient extends BaseWebChromeClient {
    CustomViewCallback customViewCallback;
    FrameLayout frame;
    Activity context;
    AdView adView;
    private AdWebView adWebView;

    public VideoEnabledWebChromeClient(Activity activity) {
        this.context = activity;
    }

    public VideoEnabledWebChromeClient(AdWebView adWebView) {
        this.context = (Activity) adWebView.getContextFromMutableContext();
        this.adWebView = adWebView;
        this.adView = this.adWebView.adView;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
        onShowCustomView(view, callback);
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        super.onShowCustomView(view, callback);

        if (context == null) {
            Clog.w(Clog.baseLogTag, Clog.getString(R.string.fullscreen_video_show_error));
            return;
        }
        ViewGroup root;
        if (adWebView != null) {
            root = (ViewGroup) adWebView.getRootView().findViewById(android.R.id.content);
        } else {
            root = (ViewGroup) context.findViewById(android.R.id.content);
        }

        if (root == null) {
            Clog.w(Clog.baseLogTag, Clog.getString(R.string.fullscreen_video_show_error));
            return;
        }

        customViewCallback = callback;
        if (view instanceof FrameLayout) {
            frame = (FrameLayout) view;
            frame.setClickable(true);
            frame.setBackgroundColor(Color.BLACK);
            try {
                addCloseButton(frame);
                root.addView(frame,
                        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT));
            } catch (Exception e) {
                Clog.d(Clog.baseLogTag, e.toString());
            }
        } else
            frame = null;
    }

    @Override
    public void onHideCustomView() {
        super.onHideCustomView();

        if ((context == null) || (frame == null)) {
            Clog.w(Clog.baseLogTag, Clog.getString(R.string.fullscreen_video_hide_error));
            return;
        }
        ViewGroup root;
        if (adWebView != null) {
            root = (ViewGroup) adWebView.getRootView().findViewById(android.R.id.content);
        } else {
            root = (ViewGroup) context.findViewById(android.R.id.content);
        }
        if (root == null) {
            Clog.w(Clog.baseLogTag, Clog.getString(R.string.fullscreen_video_hide_error));
            return;
        }

        root.removeView(frame);

        if (customViewCallback != null){
            // Try catch added to handle crash in 4.0.3 devices
            try {
                customViewCallback.onCustomViewHidden();
            }catch (NullPointerException e) {
                Clog.e(Clog.baseLogTag, "Exception calling customViewCallback  onCustomViewHidden: " + e.getMessage());
            }
        }

    }

    private void addCloseButton(FrameLayout layout) {
        final ImageButton close = new ImageButton(context);
        close.setImageDrawable(context.getResources().getDrawable(
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
                onHideCustomView();
            }
        });
        layout.addView(close);
    }

    //HTML5 Location Callbacks
    @Override
    public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
        Context dialogContext = (adWebView != null) ? ViewUtil.getTopContext(adWebView) : context;
        AlertDialog.Builder adb = new AlertDialog.Builder(dialogContext);

        String title = String.format(this.context.getResources().getString(R.string.html5_geo_permission_prompt_title), origin);

        adb.setTitle(title);
        adb.setMessage(R.string.html5_geo_permission_prompt);

        adb.setPositiveButton(R.string.allow, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.invoke(origin, true, true);
            }
        });

        adb.setNegativeButton(R.string.deny, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.invoke(origin, false, false);
            }
        });

        adb.create().show();

        // We're presenting a modal dialog view, so this is equivalent to an expand
        // suppress if already expanded in MRAID
        if ((adView != null) && !adView.isInterstitial() && !adView.isMRAIDExpanded()) {
            this.adView.getAdDispatcher().onAdExpanded();
        }
    }

    @Override
    public void onGeolocationPermissionsHidePrompt() {
        if ((adView != null) && !adView.isInterstitial() && !adView.isMRAIDExpanded()) {
            this.adView.getAdDispatcher().onAdCollapsed();
        }
    }

}
