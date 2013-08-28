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

import android.app.Activity;
import com.appnexus.opensdk.MediatedInterstitialAdView;
import com.appnexus.opensdk.MediatedInterstitialAdViewController;
import com.appnexus.opensdk.utils.Clog;
import com.millennialmedia.android.*;

public class MillenialMediaInterstitial implements MediatedInterstitialAdView, RequestListener {
    MMInterstitial iad;
    MediatedInterstitialAdViewController mMediatedInterstitialAdViewController;

    @Override
    public void requestAd(MediatedInterstitialAdViewController mIC, Activity activity, String parameter, String uid) {
        if (mIC == null) {
            Clog.e(Clog.mediationLogTag, "MillenialMediaInterstitial - requestAd called with null controller");
            return;
        } else if (activity == null) {
            Clog.e(Clog.mediationLogTag, "MillenialMediaInterstitial - requestAd called with null activity");
            return;
        }
        Clog.d(Clog.mediationLogTag, String.format("MillenialMediaInterstitial - requesting an interstitial ad: %s, %s, %s, %s", mIC.toString(), activity.toString(), parameter, uid));

        mMediatedInterstitialAdViewController = mIC;

        MMSDK.initialize(activity);

        iad = new MMInterstitial(activity);
        iad.setApid(uid);
        iad.setListener(this);
        iad.fetch();
    }

    @Override
    public void show() {
        Clog.d(Clog.mediationLogTag, "MillenialMediaInterstitial - show called");
        if (iad == null) {
            Clog.e(Clog.mediationLogTag, "MillenialMediaInterstitial - show called while interstitial ad view was null");
            return;
        }
        if (!iad.isAdAvailable()) {
            Clog.e(Clog.mediationLogTag, "MillenialMediaInterstitial - show called while interstitial ad was unavailable");
            return;
        }

        if (iad.display(true))
            Clog.d(Clog.mediationLogTag, "MillenialMediaInterstitial - display called successfully");
        else
            Clog.e(Clog.mediationLogTag, "MillenialMediaInterstitial - display failed");
    }

    @Override
    public void MMAdOverlayLaunched(MMAd mmAd) {
        Clog.d(Clog.mediationLogTag, "MillenialMediaInterstitial - MMAdOverlayLaunched: " + mmAd.toString());
        if (mMediatedInterstitialAdViewController != null)
            mMediatedInterstitialAdViewController.onAdExpanded();
    }

    @Override
    public void MMAdOverlayClosed(MMAd mmAd) {
        Clog.d(Clog.mediationLogTag, "MillenialMediaInterstitial - MMAdOverlayClosed: " + mmAd.toString());
        if (mMediatedInterstitialAdViewController != null)
            mMediatedInterstitialAdViewController.onAdCollapsed();
    }

    @Override
    public void MMAdRequestIsCaching(MMAd mmAd) {
        Clog.d(Clog.mediationLogTag, "MillenialMediaInterstitial - MMAdRequestIsCaching: " + mmAd.toString());
    }

    @Override
    public void requestCompleted(MMAd mmAd) {
        Clog.d(Clog.mediationLogTag, "MillenialMediaInterstitial - requestCompleted: " + mmAd.toString());
        if (mMediatedInterstitialAdViewController != null)
            mMediatedInterstitialAdViewController.onAdLoaded();
    }

    @Override
    public void requestFailed(MMAd mmAd, MMException e) {
        Clog.d(Clog.mediationLogTag, String.format("MillenialMediaInterstitial - requestFailed: %s with error %s", mmAd.toString(), e));
        if (mMediatedInterstitialAdViewController != null)
            mMediatedInterstitialAdViewController.onAdFailed(MediatedInterstitialAdViewController.RESULT.INTERNAL_ERROR);
    }

    // this also doesn't work..
    @Override
    public void onSingleTap(MMAd mmAd) {
        Clog.d(Clog.mediationLogTag, "MillenialMediaInterstitial - onSingleTap: " + mmAd.toString());
        if (mMediatedInterstitialAdViewController != null)
            mMediatedInterstitialAdViewController.onAdClicked();
    }
}