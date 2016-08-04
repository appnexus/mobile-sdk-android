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
import android.content.MutableContextWrapper;
import android.webkit.WebView;

import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.ViewUtil;

class MRAIDAdActivity implements AdActivity.AdActivityImplementation {
    private Activity adActivity;
    private AdWebView webView;

    private MRAIDImplementation mraidFullscreenImplementation = null;

    public MRAIDAdActivity(Activity adActivity) {
        this.adActivity = adActivity;
    }

    @Override
    public void create() {
        if ((AdView.mraidFullscreenContainer == null) || (AdView.mraidFullscreenImplementation == null)) {
            Clog.e(Clog.baseLogTag, "Launched MRAID Fullscreen activity with invalid properties");
            adActivity.finish();
            return;
        }

        // remove from any old parents to be safe
        ViewUtil.removeChildFromParent(AdView.mraidFullscreenContainer);
        adActivity.setContentView(AdView.mraidFullscreenContainer);
        if (AdView.mraidFullscreenContainer.getChildAt(0) instanceof AdWebView) {
            webView = (AdWebView) AdView.mraidFullscreenContainer.getChildAt(0);
        }
        // Update the context
        if(webView.getContext() instanceof MutableContextWrapper) {
            ((MutableContextWrapper) webView.getContext()).setBaseContext(adActivity);
        }
        mraidFullscreenImplementation = AdView.mraidFullscreenImplementation;
        mraidFullscreenImplementation.setFullscreenActivity(adActivity);

        if (AdView.mraidFullscreenListener != null) {
            AdView.mraidFullscreenListener.onCreateCompleted();
        }
    }

    @Override
    public void backPressed() {
        if (mraidFullscreenImplementation != null) {
            mraidFullscreenImplementation.setFullscreenActivity(null);
            mraidFullscreenImplementation.close();
        }
        mraidFullscreenImplementation = null;
    }


    @Override
    public void destroy() {
        // do nothing
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
}
