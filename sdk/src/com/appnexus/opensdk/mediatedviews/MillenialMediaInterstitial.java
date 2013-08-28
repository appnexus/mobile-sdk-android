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
import com.millennialmedia.android.MMInterstitial;
import com.millennialmedia.android.MMSDK;

public class MillenialMediaInterstitial implements MediatedInterstitialAdView {

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
        iad.setListener(new MillenialMediaListener(mMediatedInterstitialAdViewController, getClass().getSimpleName()));
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
}