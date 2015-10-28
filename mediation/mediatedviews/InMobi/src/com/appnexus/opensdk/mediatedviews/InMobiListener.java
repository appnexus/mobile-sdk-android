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
import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiBanner;
import com.inmobi.ads.InMobiInterstitial;

import java.util.Map;

public class InMobiListener implements InMobiBanner.BannerAdListener, InMobiInterstitial.InterstitialAdListener {

    private final MediatedAdViewController mediatedAdViewController;
    private final String className;

    public InMobiListener(MediatedAdViewController mediatedAdViewController, String className) {
        this.mediatedAdViewController = mediatedAdViewController;
        this.className = className;
    }

    // Banner Listener implementation

    @Override
    public void onAdLoadSucceeded(InMobiBanner inMobiBanner) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdLoaded();
        }
    }

    @Override
    public void onAdLoadFailed(InMobiBanner inMobiBanner, InMobiAdRequestStatus inMobiAdRequestStatus) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdFailed(InMobiSettings.getResultCode(inMobiAdRequestStatus));
        }
    }

    @Override
    public void onAdDisplayed(InMobiBanner inMobiBanner) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdExpanded();
        }
    }

    @Override
    public void onAdDismissed(InMobiBanner inMobiBanner) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdCollapsed();
        }
    }

    @Override
    public void onAdInteraction(InMobiBanner inMobiBanner, Map<Object, Object> map) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdClicked();
        }
    }

    @Override
    public void onUserLeftApplication(InMobiBanner inMobiBanner) {

    }

    @Override
    public void onAdRewardActionCompleted(InMobiBanner inMobiBanner, Map<Object, Object> map) {

    }

    // Interstitial listener implementation

    @Override
    public void onAdRewardActionCompleted(InMobiInterstitial inMobiInterstitial, Map<Object, Object> map) {

    }

    @Override
    public void onAdDisplayed(InMobiInterstitial inMobiInterstitial) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdExpanded();
        }
    }

    @Override
    public void onAdDismissed(InMobiInterstitial inMobiInterstitial) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdCollapsed();
        }

    }

    @Override
    public void onAdInteraction(InMobiInterstitial inMobiInterstitial, Map<Object, Object> map) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdClicked();
        }

    }

    @Override
    public void onAdLoadSucceeded(InMobiInterstitial inMobiInterstitial) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdLoaded();
        }
    }

    @Override
    public void onAdLoadFailed(InMobiInterstitial inMobiInterstitial, InMobiAdRequestStatus inMobiAdRequestStatus) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdFailed(InMobiSettings.getResultCode(inMobiAdRequestStatus));
        }
    }

    @Override
    public void onUserLeftApplication(InMobiInterstitial inMobiInterstitial) {

    }
}
