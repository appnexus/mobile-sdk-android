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

import android.graphics.Color;

import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import com.appnexus.opensdk.utils.Clog;


class VideoChromeClient extends WebChromeClient {

    private VideoAd owner;
    CustomViewCallback customViewCallback;
    FrameLayout frame;

    public VideoChromeClient(VideoAd owner) {
        this.owner = owner;
    }


    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        Clog.v(Clog.jsLogTag,
                Clog.getString(R.string.console_message,
                        consoleMessage.message(),
                        consoleMessage.lineNumber(),
                        consoleMessage.sourceId()));
        return true;
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message,
                             JsResult result) {
        Clog.v(Clog.jsLogTag,
                Clog.getString(R.string.js_alert, message, url));
        result.confirm();
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onShowCustomView(View view, int requestedOrientation, WebChromeClient.CustomViewCallback callback) {
        onShowCustomView(view, callback);
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        super.onShowCustomView(view, callback);
        if (owner == null) {
            Clog.w(Clog.baseLogTag, Clog.getString(R.string.fullscreen_video_show_error));
            return;
        }
        ViewGroup root = null;
        if (owner != null) {
            root = owner.getVideoAdView();
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
                root.addView(frame);
            } catch (Exception e) {
                Clog.d(Clog.baseLogTag, e.toString());
            }
        } else {
            frame = null;
        }
    }

    @Override
    public void onHideCustomView() {
        super.onHideCustomView();
        if ((owner == null) || (frame == null)) {
            Clog.w(Clog.baseLogTag, Clog.getString(R.string.fullscreen_video_hide_error));
            return;
        }
        ViewGroup root = null;
        if (owner != null) {
            root = owner.getVideoAdView();
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
}
