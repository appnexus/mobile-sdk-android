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
import com.inmobi.ads.InMobiBanner;
import com.inmobi.ads.InMobiInterstitial;

import java.util.Map;

public class InMobiListener implements InMobiBanner.BannerAdListener, InMobiInterstitial.InterstitialAdListener2 {

    private final MediatedAdViewController mediatedAdViewController;
    private final String className;

    public InMobiListener(MediatedAdViewController mediatedAdViewController, String className) {
        this.mediatedAdViewController = mediatedAdViewController;
        this.className = className;
    }

    // Banner Listener implementation

    @Override
    public void onAdLoadSucceeded(InMobiBanner inMobiBanner) {
        Clog.d(Clog.mediationLogTag, "InMobiBanner: onAdLoadSucceeded" );
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdLoaded();
        }
    }

    @Override
    public void onAdLoadFailed(InMobiBanner inMobiBanner, InMobiAdRequestStatus inMobiAdRequestStatus) {
        Clog.e(Clog.mediationLogTag, "InMobiBanner: onAdLoadFailed" );
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdFailed(InMobiSettings.getResultCode(inMobiAdRequestStatus));
        }
    }

    @Override
    public void onAdDisplayed(InMobiBanner inMobiBanner) {
        Clog.d(Clog.mediationLogTag, "InMobiBanner: onAdDisplayed" );
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdExpanded();
        }
    }

    @Override
    public void onAdDismissed(InMobiBanner inMobiBanner) {
        Clog.d(Clog.mediationLogTag, "InMobiBanner: onAdDismissed" );
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdCollapsed();
        }
    }

    @Override
    public void onAdInteraction(InMobiBanner inMobiBanner, Map<Object, Object> map) {
        Clog.d(Clog.mediationLogTag, "InMobiBanner: onAdInteraction" );
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdClicked();
        }
    }

    @Override
    public void onUserLeftApplication(InMobiBanner inMobiBanner) {
        Clog.d(Clog.mediationLogTag, "InMobiBanner: onUserLeftApplication" );

    }

    @Override
    public void onAdRewardActionCompleted(InMobiBanner inMobiBanner, Map<Object, Object> map) {
        Clog.d(Clog.mediationLogTag, "InMobiBanner: onAdRewardActionCompleted" );

    }

    // Interstitial listener implementation

    @Override
    public void onAdRewardActionCompleted(InMobiInterstitial inMobiInterstitial, Map<Object, Object> map) {
        Clog.d(Clog.mediationLogTag, "InMobiInterstitial: onAdRewardActionCompleted" );

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
    public void onAdInteraction(InMobiInterstitial inMobiInterstitial, Map<Object, Object> map) {
        Clog.d(Clog.mediationLogTag, "InMobiInterstitial: onAdInteraction" );
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
