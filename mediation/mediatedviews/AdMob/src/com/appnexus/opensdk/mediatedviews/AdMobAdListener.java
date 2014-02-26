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
import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;

public class AdMobAdListener implements AdListener {
    MediatedAdViewController mediatedAdViewController;
    Class clazz;

    public AdMobAdListener(MediatedAdViewController mediatedAdViewController, Class clazz) {
        this.mediatedAdViewController = mediatedAdViewController;
        this.clazz = clazz;
    }

    @Override
    public void onReceiveAd(Ad ad) {
        printToClog("onReceiveAd: 0" + ad);
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdLoaded();
        }
    }

    @Override
    public void onFailedToReceiveAd(Ad ad, AdRequest.ErrorCode errorCode) {
        printToClog("onFailedToReceiveAd: " + ad + " error code: " + errorCode);

        MediatedAdViewController.RESULT code = MediatedAdViewController.RESULT.INTERNAL_ERROR;

        switch (errorCode) {
            case INTERNAL_ERROR:
                code = MediatedAdViewController.RESULT.INTERNAL_ERROR;
                break;
            case INVALID_REQUEST:
                code = MediatedAdViewController.RESULT.INVALID_REQUEST;
                break;
            case NETWORK_ERROR:
                code = MediatedAdViewController.RESULT.NETWORK_ERROR;
                break;
            case NO_FILL:
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
    public void onPresentScreen(Ad ad) {
        printToClog("onPresentScreen: " + ad);
        // interstitials get this callback when show is called, so ignore that
        if ((mediatedAdViewController != null)
                && (mediatedAdViewController instanceof MediatedBannerAdViewController)) {
            mediatedAdViewController.onAdExpanded();
        }
    }

    @Override
    public void onDismissScreen(Ad ad) {
        printToClog("onDismissScreen: " + ad);
        // interstitials get this callback when the ad is closed, so ignore that
        if ((mediatedAdViewController != null)
                && (mediatedAdViewController instanceof MediatedBannerAdViewController)) {
            mediatedAdViewController.onAdCollapsed();
        }
    }

    @Override
    public void onLeaveApplication(Ad ad) {
        printToClog("onLeaveApplication: " + ad);
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdClicked();
        }
    }

    private void printToClog(String s) {
        Clog.d(Clog.mediationLogTag, clazz.getSimpleName() + " - " + s);
    }
}
