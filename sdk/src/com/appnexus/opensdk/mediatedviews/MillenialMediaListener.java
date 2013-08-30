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
import com.appnexus.opensdk.MediatedInterstitialAdViewController;
import com.appnexus.opensdk.utils.Clog;
import com.millennialmedia.android.MMAd;
import com.millennialmedia.android.MMException;
import com.millennialmedia.android.RequestListener;

public class MillenialMediaListener implements RequestListener {

    MediatedAdViewController mAVC;
    String className;

    public MillenialMediaListener(MediatedAdViewController mAVC, String className) {
        this.mAVC = mAVC;
        this.className = className;
        Clog.d(Clog.mediationLogTag, String.format("New MillenialMediaListener created for [%s %s]", className, mAVC));
    }

    // occurs when ad is clicked and browser is launched.
    // or when an interstitial is launched
    @Override
    public void MMAdOverlayLaunched(MMAd mmAd) {
        Clog.d(Clog.mediationLogTag, String.format("%s - MMAdOverlayLaunched: %s", className, mmAd));
        if (mAVC != null)
            mAVC.onAdExpanded();
    }

    // this callback doesn't work...
    @Override
    public void MMAdOverlayClosed(MMAd mmAd) {
        Clog.d(Clog.mediationLogTag, String.format("%s - MMAdOverlayClosed: %s", className, mmAd));
        if (mAVC != null)
            mAVC.onAdCollapsed();
    }

    @Override
    public void MMAdRequestIsCaching(MMAd mmAd) {
        Clog.d(Clog.mediationLogTag, String.format("%s - MMAdRequestIsCaching: %s", className, mmAd));
    }

    @Override
    public void requestCompleted(MMAd mmAd) {
        Clog.d(Clog.mediationLogTag, String.format("%s - requestCompleted: %s", className, mmAd));
        if (mAVC != null)
            mAVC.onAdLoaded();
    }

    @Override
    public void requestFailed(MMAd mmAd, MMException e) {
        Clog.d(Clog.mediationLogTag, String.format("%s - requestFailed: %s with error %s", className, mmAd, e));
        MediatedAdViewController.RESULT code = MediatedInterstitialAdViewController.RESULT.INTERNAL_ERROR;

        switch (e.getCode()) {
            case MMException.INVALID_PARAMETER:
                code = MediatedAdViewController.RESULT.INVALID_REQUEST;
                break;
            case MMException.INNER_EXCEPTION:
                code = MediatedAdViewController.RESULT.INTERNAL_ERROR;
                break;
            case MMException.MAIN_THREAD_REQUIRED:
                code = MediatedAdViewController.RESULT.INTERNAL_ERROR;
                break;
            case MMException.REQUEST_TIMEOUT:
                code = MediatedAdViewController.RESULT.NETWORK_ERROR;
                break;
            case MMException.REQUEST_NO_NETWORK:
                code = MediatedAdViewController.RESULT.NETWORK_ERROR;
                break;
            case MMException.REQUEST_IN_PROGRESS:
                code = MediatedAdViewController.RESULT.INVALID_REQUEST;
                break;
            case MMException.REQUEST_ALREADY_CACHING:
                code = MediatedAdViewController.RESULT.INVALID_REQUEST;
                break;
            case MMException.REQUEST_NOT_FILLED:
                code = MediatedAdViewController.RESULT.UNABLE_TO_FILL;
                break;
            case MMException.REQUEST_BAD_RESPONSE:
                code = MediatedAdViewController.RESULT.INVALID_REQUEST;
                break;
            case MMException.REQUEST_NOT_PERMITTED:
                code = MediatedAdViewController.RESULT.INVALID_REQUEST;
                break;
            case MMException.CACHE_NOT_EMPTY:
                code = MediatedAdViewController.RESULT.INVALID_REQUEST;
                break;
            case MMException.DISPLAY_AD_NOT_READY:
                code = MediatedAdViewController.RESULT.UNABLE_TO_FILL;
                break;
            case MMException.DISPLAY_AD_EXPIRED:
                code = MediatedAdViewController.RESULT.UNABLE_TO_FILL;
                break;
            case MMException.DISPLAY_AD_NOT_FOUND:
                code = MediatedAdViewController.RESULT.UNABLE_TO_FILL;
                break;
            case MMException.DISPLAY_AD_ALREADY_DISPLAYED:
                code = MediatedAdViewController.RESULT.UNABLE_TO_FILL;
                break;
            case MMException.DISPLAY_AD_NOT_PERMITTED:
                code = MediatedAdViewController.RESULT.UNABLE_TO_FILL;
                break;
            case MMException.AD_BROKEN_REFERENCE:
                code = MediatedAdViewController.RESULT.INTERNAL_ERROR;
                break;
            case MMException.AD_NO_ACTIVITY:
                code = MediatedAdViewController.RESULT.INTERNAL_ERROR;
                break;
            case MMException.UNKNOWN_ERROR:
                code = MediatedAdViewController.RESULT.INTERNAL_ERROR;
                break;
        }

        if (mAVC != null)
            mAVC.onAdFailed(code);
    }

    // this callback doesn't work for MMInterstitials
    @Override
    public void onSingleTap(MMAd mmAd) {
        Clog.d(Clog.mediationLogTag, String.format("%s - onSingleTap: %s", className, mmAd));
        if (mAVC != null)
            mAVC.onAdClicked();
    }
}
