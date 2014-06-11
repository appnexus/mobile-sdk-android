/*
 *    Copyright 2014 APPNEXUS INC
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
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.InterstitialAdListener;

public class FacebookListener implements AdListener, InterstitialAdListener {

    private final MediatedAdViewController mediatedAdViewController;
    private final String className;

    public FacebookListener(MediatedAdViewController mediatedAdViewController, String className) {
        this.mediatedAdViewController = mediatedAdViewController;
        this.className = className;
    }

    @Override
    public void onError(Ad ad, AdError adError) {
        Clog.d(Clog.mediationLogTag, className + " | Facebook - onError called for AdView with error message " + adError.getErrorMessage());
        ResultCode code;

        if (adError.getErrorCode() == AdError.NO_FILL.getErrorCode()) {
            code = ResultCode.UNABLE_TO_FILL;
        } else if (adError.getErrorCode() == AdError.LOAD_TOO_FREQUENTLY.getErrorCode()) {
            code = ResultCode.INVALID_REQUEST;
        } else if (adError.getErrorCode() == AdError.INTERNAL_ERROR.getErrorCode()) {
            code = ResultCode.INTERNAL_ERROR;
        } else if (adError.getErrorCode() == AdError.MISSING_PROPERTIES.getErrorCode()) {
            code = ResultCode.INVALID_REQUEST;
        } else {
            code = ResultCode.INTERNAL_ERROR;
        }

        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdFailed(code);
        }
    }

    @Override
    public void onAdLoaded(Ad ad) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdLoaded();
        }
    }

    @Override
    public void onAdClicked(Ad ad) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdClicked();
        }
    }

    @Override
    public void onInterstitialDismissed(Ad ad) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdCollapsed();
        }
    }

    @Override
    public void onInterstitialDisplayed(Ad ad) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdExpanded();
        }
    }

}
