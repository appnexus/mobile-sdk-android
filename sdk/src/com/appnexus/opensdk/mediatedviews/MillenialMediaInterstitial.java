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
import com.millennialmedia.android.*;

public class MillenialMediaInterstitial implements MediatedInterstitialAdView, RequestListener {
    MMInterstitial iad;
    MediatedInterstitialAdViewController mMediatedInterstitialAdViewController;


    @Override
    public void requestAd(MediatedInterstitialAdViewController mIC, Activity activity, String parameter, String uid) {
        mMediatedInterstitialAdViewController = mIC;

        MMSDK.initialize(activity);

        iad = new MMInterstitial(activity);
        iad.setApid(uid);
        iad.setListener(this);
        iad.fetch();

        return;
    }

    @Override
    public void show() {
        if (iad != null && iad.isAdAvailable())
            iad.display();
    }

    @Override
    public void MMAdOverlayLaunched(MMAd mmAd) {
        if (mMediatedInterstitialAdViewController != null)
            mMediatedInterstitialAdViewController.onAdExpanded();
    }

    // this callback doesn't seem to work (MM's fault)
    @Override
    public void MMAdOverlayClosed(MMAd mmAd) {
        if (mMediatedInterstitialAdViewController != null)
            mMediatedInterstitialAdViewController.onAdCollapsed();
    }

    // equivalent to a "interstitial is loading" state
    @Override
    public void MMAdRequestIsCaching(MMAd mmAd) {
    }

    @Override
    public void requestCompleted(MMAd mmAd) {
        if (mMediatedInterstitialAdViewController != null)
            mMediatedInterstitialAdViewController.onAdLoaded();
    }

    @Override
    public void requestFailed(MMAd mmAd, MMException e) {
        if (mMediatedInterstitialAdViewController != null)
            mMediatedInterstitialAdViewController.onAdFailed(MediatedInterstitialAdViewController.RESULT.INTERNAL_ERROR);
    }

    // this also doesn't work..
    @Override
    public void onSingleTap(MMAd mmAd) {
        if (mMediatedInterstitialAdViewController != null)
            mMediatedInterstitialAdViewController.onAdClicked();
    }
}