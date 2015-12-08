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

package com.appnexus.opensdk.mediatedviews;

import com.appnexus.opensdk.MediatedAdViewController;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.utils.Clog;
import com.millennialmedia.InlineAd;
import com.millennialmedia.InterstitialAd;

/**
 * This class provides the bridge for the Millennial Media's SDK events to the AppNexus SDK events.
 * This class is used internally by the Millennial Media mediation adaptor.
 */
class MillennialMediaListener implements InlineAd.InlineListener, InterstitialAd.InterstitialListener {

    private final MediatedAdViewController mediatedAdViewController;
    private final String className;

    public MillennialMediaListener(MediatedAdViewController mediatedAdViewController, String className) {
        this.mediatedAdViewController = mediatedAdViewController;
        this.className = className;
    }


    void printToClog(String s) {
        Clog.d(Clog.mediationLogTag, className + " - " + s);
    }

    void printToClogWarn(String s) {
        Clog.w(Clog.mediationLogTag, className + " - " + s);
    }

    void printToClogError(String s) {
        Clog.e(Clog.mediationLogTag, className + " - " + s);
    }

    // InLine Listener

    @Override
    public void onRequestSucceeded(InlineAd inlineAd) {
        printToClog("requestSucceeded: " + inlineAd);
        if (mediatedAdViewController != null)
            mediatedAdViewController.onAdLoaded();
    }

    @Override
    public void onRequestFailed(InlineAd inlineAd, InlineAd.InlineErrorStatus inlineErrorStatus) {
        printToClog("requestFailed: " + inlineAd + " with error: " + inlineErrorStatus.getDescription());

        if (mediatedAdViewController != null)
            mediatedAdViewController.onAdFailed(getResultCode(inlineErrorStatus.getErrorCode()));
    }

    @Override
    public void onClicked(InlineAd inlineAd) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdClicked();
        }
    }

    @Override
    public void onResize(InlineAd inlineAd, int i, int i1) {

    }

    @Override
    public void onResized(InlineAd inlineAd, int i, int i1, boolean b) {

    }

    @Override
    public void onExpanded(InlineAd inlineAd) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdExpanded();
        }
    }

    @Override
    public void onCollapsed(InlineAd inlineAd) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdCollapsed();
        }
    }

    @Override
    public void onAdLeftApplication(InlineAd inlineAd) {

    }

    // Interstitial Ad Listener

    @Override
    public void onLoaded(InterstitialAd interstitialAd) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdLoaded();
        }

    }

    @Override
    public void onLoadFailed(InterstitialAd interstitialAd, InterstitialAd.InterstitialErrorStatus interstitialErrorStatus) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdFailed(getResultCode(interstitialErrorStatus.getErrorCode()));
        }

    }

    @Override
    public void onShown(InterstitialAd interstitialAd) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdExpanded();
        }

    }

    @Override
    public void onShowFailed(InterstitialAd interstitialAd, InterstitialAd.InterstitialErrorStatus interstitialErrorStatus) {

    }

    @Override
    public void onClosed(InterstitialAd interstitialAd) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdCollapsed();
        }
    }

    @Override
    public void onClicked(InterstitialAd interstitialAd) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdClicked();
        }
    }

    @Override
    public void onAdLeftApplication(InterstitialAd interstitialAd) {

    }

    @Override
    public void onExpired(InterstitialAd interstitialAd) {

    }

    private ResultCode getResultCode(int error) {
        ResultCode code = ResultCode.INTERNAL_ERROR;

        switch (error) {
            case 1: // ADAPTER_NOT_FOUND
                break;
            case 2: // NO_NETWORK
                code = ResultCode.NETWORK_ERROR;
                break;
            case 3: // INIT_FAILED
                break;
            case 4: // DISPLAY_FAILED
                break;
            case 5: // LOAD_FAILED
                code = ResultCode.UNABLE_TO_FILL;
                break;
            case 6: // LOAD_TIMED_OUT
                code = ResultCode.UNABLE_TO_FILL;
                break;
            case 7: // UNKNOWN
                break;
            case 201: // EXPIRED
                code = ResultCode.UNABLE_TO_FILL;
                break;
            case 202: // NOT_LOADED
                code = ResultCode.UNABLE_TO_FILL;
                break;
            case 203: // ALREADY_LOADED
                break;
        }
        return code;
    }
}
