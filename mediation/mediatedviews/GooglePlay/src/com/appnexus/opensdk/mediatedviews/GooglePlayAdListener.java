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
import com.appnexus.opensdk.utils.Clog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;

public class GooglePlayAdListener extends AdListener {
    MediatedAdViewController mediatedAdViewController;
    Class clazz;

    public GooglePlayAdListener(MediatedAdViewController mediatedAdViewController, Class clazz) {
        this.mediatedAdViewController = mediatedAdViewController;
        this.clazz = clazz;
    }

    @Override
    public void onAdClosed() {
        super.onAdClosed();
        printToClog("onAdClosed");
        // interstitials get this callback when the ad is closed, so ignore that
        if ((mediatedAdViewController != null)
                && (mediatedAdViewController instanceof MediatedBannerAdViewController)) {
            mediatedAdViewController.onAdCollapsed();
        }
    }

    @Override
    public void onAdFailedToLoad(int errorCode) {
        super.onAdFailedToLoad(errorCode);
        printToClog("onAdFailedToLoad with error code " + errorCode);

        MediatedAdViewController.RESULT code = MediatedAdViewController.RESULT.INTERNAL_ERROR;

        switch (errorCode) {
            case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                code = MediatedAdViewController.RESULT.INTERNAL_ERROR;
                break;
            case AdRequest.ERROR_CODE_INVALID_REQUEST:
                code = MediatedAdViewController.RESULT.INVALID_REQUEST;
                break;
            case AdRequest.ERROR_CODE_NETWORK_ERROR:
                code = MediatedAdViewController.RESULT.NETWORK_ERROR;
                break;
            case AdRequest.ERROR_CODE_NO_FILL:
                code = MediatedAdViewController.RESULT.UNABLE_TO_FILL;
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
            mediatedAdViewController.onAdLoaded();
        }
    }

    private void printToClog(String s) {
        Clog.d(Clog.mediationLogTag, clazz.getSimpleName() + " - " + s);
    }
}
