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

import android.os.Handler;
import android.os.Looper;

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
    private static final Handler handler = new Handler(Looper.getMainLooper());

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

    private static void postOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    // InLine Listener

    @Override
    public void onRequestSucceeded(InlineAd inlineAd) {
        printToClog("requestSucceeded: " + inlineAd);
        if (mediatedAdViewController != null) {
            postOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mediatedAdViewController.onAdLoaded();
                }
            });
        }
    }

    @Override
    public void onRequestFailed(InlineAd inlineAd, InlineAd.InlineErrorStatus inlineErrorStatus) {
        printToClog("requestFailed: " + inlineAd + " with error: " + inlineErrorStatus.getDescription());
        final ResultCode resultCode = getResultCode(inlineErrorStatus.getErrorCode());
        if (mediatedAdViewController != null){
            postOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mediatedAdViewController.onAdFailed(resultCode);
                }
            });
        }

    }

    @Override
    public void onClicked(InlineAd inlineAd) {
        if (mediatedAdViewController != null) {
            postOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mediatedAdViewController.onAdClicked();
                }
            });
        }
    }

    @Override
    public void onResize(InlineAd inlineAd, int i, int i1) {
        printToClog("onResize: "+inlineAd);
    }

    @Override
    public void onResized(InlineAd inlineAd, int i, int i1, boolean b) {
        printToClog("onResized: "+inlineAd);
    }

    @Override
    public void onExpanded(InlineAd inlineAd) {
        if (mediatedAdViewController != null) {
            postOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mediatedAdViewController.onAdExpanded();
                }
            });
        }
    }

    @Override
    public void onCollapsed(InlineAd inlineAd) {
        if (mediatedAdViewController != null) {
            postOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mediatedAdViewController.onAdCollapsed();
                }
            });
        }
    }

    @Override
    public void onAdLeftApplication(InlineAd inlineAd) {
        printToClog("onAdLeftApplication: "+inlineAd);
    }

    // Interstitial Ad Listener

    @Override
    public void onLoaded(InterstitialAd interstitialAd) {
        if (mediatedAdViewController != null) {
            postOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mediatedAdViewController.onAdLoaded();
                }
            });
        }

    }

    @Override
    public void onLoadFailed(InterstitialAd interstitialAd, InterstitialAd.InterstitialErrorStatus interstitialErrorStatus) {
        final ResultCode resultCode = getResultCode(interstitialErrorStatus.getErrorCode());
        if (mediatedAdViewController != null) {
            postOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mediatedAdViewController.onAdFailed(resultCode);
                }
            });
        }
    }

    @Override
    public void onShown(InterstitialAd interstitialAd) {
        if (mediatedAdViewController != null) {
            postOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mediatedAdViewController.onAdExpanded();
                }
            });
        }

    }

    @Override
    public void onShowFailed(InterstitialAd interstitialAd, InterstitialAd.InterstitialErrorStatus interstitialErrorStatus) {
        printToClog("onShowFailed: "+interstitialAd + "interstitialErrorStatus: "+interstitialErrorStatus );
    }

    @Override
    public void onClosed(InterstitialAd interstitialAd) {
        if (mediatedAdViewController != null) {
            postOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mediatedAdViewController.onAdCollapsed();
                }
            });
        }
    }

    @Override
    public void onClicked(InterstitialAd interstitialAd) {
        if (mediatedAdViewController != null) {
            postOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mediatedAdViewController.onAdClicked();
                }
            });
        }
    }

    @Override
    public void onAdLeftApplication(InterstitialAd interstitialAd) {
        printToClog("onAdLeftApplication: "+interstitialAd);
    }

    @Override
    public void onExpired(InterstitialAd interstitialAd) {
        printToClog("onExpired: "+interstitialAd);
    }

    private ResultCode getResultCode(int error) {
        ResultCode code = ResultCode.getNewInstance(ResultCode.INTERNAL_ERROR);

        switch (error) {
            case 1: // ADAPTER_NOT_FOUND
                break;
            case 2: // NO_NETWORK
                code = ResultCode.getNewInstance(ResultCode.NETWORK_ERROR);
                break;
            case 3: // INIT_FAILED
                break;
            case 4: // DISPLAY_FAILED
                break;
            case 5: // LOAD_FAILED
                code = ResultCode.getNewInstance(ResultCode.UNABLE_TO_FILL);
                break;
            case 6: // LOAD_TIMED_OUT
                code = ResultCode.getNewInstance(ResultCode.UNABLE_TO_FILL);
                break;
            case 7: // UNKNOWN
                break;
            case 201: // EXPIRED
                code = ResultCode.getNewInstance(ResultCode.UNABLE_TO_FILL);
                break;
            case 202: // NOT_LOADED
                code = ResultCode.getNewInstance(ResultCode.UNABLE_TO_FILL);
                break;
            case 203: // ALREADY_LOADED
                break;
        }
        return code;
    }
}
