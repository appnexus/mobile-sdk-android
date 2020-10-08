/*
 *    Copyright 2015 APPNEXUS INC
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
import com.appnexus.opensdk.mediatednativead.InMobiSettings;
import com.appnexus.opensdk.utils.Clog;
import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiInterstitial;
import com.inmobi.ads.listeners.InterstitialAdEventListener;

import java.util.Map;

public class InMobiInterstitialAdListener extends InterstitialAdEventListener {

    private final MediatedAdViewController mediatedAdViewController;

    public InMobiInterstitialAdListener(MediatedAdViewController mediatedAdViewController) {
        this.mediatedAdViewController = mediatedAdViewController;
    }

    // Interstitial listener implementation

    @Override
    public void onRewardsUnlocked(InMobiInterstitial inMobiInterstitial, Map<Object, Object> map) {
        Clog.d(Clog.mediationLogTag, "InMobiInterstitial: onRewardsUnlocked" );

    }



    @Override
    public void onAdDisplayed(InMobiInterstitial inMobiInterstitial) {
        Clog.d(Clog.mediationLogTag, "InMobiInterstitial: onAdDisplayed" );
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdExpanded();
        }
    }

    @Override
    public void onAdDismissed(InMobiInterstitial inMobiInterstitial) {
        Clog.d(Clog.mediationLogTag, "InMobiInterstitial: onAdDismissed" );
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdCollapsed();
        }

    }

    @Override
    public void onAdClicked(InMobiInterstitial inMobiInterstitial, Map<Object, Object> map) {
        Clog.d(Clog.mediationLogTag, "InMobiInterstitial: onAdClicked" );
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdClicked();
        }

    }

    @Override
    public void onAdLoadSucceeded(InMobiInterstitial inMobiInterstitial) {
        Clog.d(Clog.mediationLogTag, "InMobiInterstitial: onAdLoadSucceeded" );
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdLoaded();
        }
    }

    @Override
    public void onAdLoadFailed(InMobiInterstitial inMobiInterstitial, InMobiAdRequestStatus inMobiAdRequestStatus) {
        Clog.e(Clog.mediationLogTag, "InMobiInterstitial: onAdLoadFailed" );
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdFailed(InMobiSettings.getResultCode(inMobiAdRequestStatus));
        }
    }


    @Override
    public void onUserLeftApplication(InMobiInterstitial inMobiInterstitial) {
        Clog.d(Clog.mediationLogTag, "InMobiInterstitial: onUserLeftApplication" );

    }

    @Override
    public void onAdReceived(InMobiInterstitial inMobiInterstitial) {
        Clog.d(Clog.mediationLogTag, "InMobiInterstitial: onAdReceived" );

    }

    @Override
    public void onAdDisplayFailed(InMobiInterstitial inMobiInterstitial) {
        Clog.e(Clog.mediationLogTag, "InMobiInterstitial: onAdDisplayFailed" );

    }

    @Override
    public void onAdWillDisplay(InMobiInterstitial inMobiInterstitial) {
        Clog.d(Clog.mediationLogTag, "InMobiInterstitial: onAdWillDisplay" );

    }
}
