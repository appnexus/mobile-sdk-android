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
import com.appnexus.opensdk.MediatedBannerAdViewController;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.utils.Clog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.doubleclick.AppEventListener;

public class GooglePlayAdListener extends AdListener implements AppEventListener {
    MediatedAdViewController mediatedAdViewController;
    String className;
    private boolean secondPriceIsHigher = false;

    public GooglePlayAdListener(MediatedAdViewController mediatedAdViewController, String className) {
        this.mediatedAdViewController = mediatedAdViewController;
        this.className = className;
    }

    @Override
    public void onAdClosed() {
        super.onAdClosed();
        printToClog("onAdClosed");
        // interstitials get this callback when the ad is closed, so ignore that
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdCollapsed();
        }
    }

    @Override
    public void onAdFailedToLoad(int errorCode) {
        super.onAdFailedToLoad(errorCode);
        printToClog("onAdFailedToLoad with error code " + errorCode);

        ResultCode code = ResultCode.INTERNAL_ERROR;

        switch (errorCode) {
            case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                code = ResultCode.INTERNAL_ERROR;
                break;
            case AdRequest.ERROR_CODE_INVALID_REQUEST:
                code = ResultCode.INVALID_REQUEST;
                break;
            case AdRequest.ERROR_CODE_NETWORK_ERROR:
                code = ResultCode.NETWORK_ERROR;
                break;
            case AdRequest.ERROR_CODE_NO_FILL:
                code = ResultCode.UNABLE_TO_FILL;
                break;
            default:
                break;
        }

        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdFailed(code);
        }
    }

    @Override
    public void onAdLeftApplication() {
        super.onAdLeftApplication();
        printToClog("onAdLeftApplication");
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdClicked();
        }
    }

    @Override
    public void onAdOpened() {
        super.onAdOpened();
        printToClog("onAdOpened");
        // interstitials get this callback when show is called, so ignore that
        if ((mediatedAdViewController != null)
                && (mediatedAdViewController instanceof MediatedBannerAdViewController)) {
            mediatedAdViewController.onAdExpanded();
        }
    }

    @Override
    public void onAdLoaded() {
        super.onAdLoaded();
        printToClog("onAdLoaded");
        if (mediatedAdViewController != null) {
            if (secondPriceIsHigher) {
                mediatedAdViewController.onAdFailed(ResultCode.UNABLE_TO_FILL);
            } else {
                mediatedAdViewController.onAdLoaded();
            }
        }

    }

    void printToClog(String s) {
        Clog.d(Clog.mediationLogTag, className + " - " + s);
    }

    void printToClogError(String s) {
        Clog.e(Clog.mediationLogTag, className + " - " + s);
    }

    @Override
    public void onAppEvent(String key, String value) {
        printToClog(String.format("onAppEvent triggered with key: %s and value: %s", key, value));
        if (key.equals("nobid") && value.equals("true")) {
            secondPriceIsHigher = true;
        }
    }
}