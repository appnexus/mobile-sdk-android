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
import com.inmobi.ads.listeners.BannerAdEventListener;

import java.util.Map;

public class InMobiBannerAdListener extends BannerAdEventListener{

    private final MediatedAdViewController mediatedAdViewController;

    public InMobiBannerAdListener(MediatedAdViewController mediatedAdViewController) {
        this.mediatedAdViewController = mediatedAdViewController;
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
    public void onAdClicked(InMobiBanner inMobiBanner, Map<Object, Object> map) {
        Clog.d(Clog.mediationLogTag, "InMobiBanner: onAdClicked" );
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdClicked();
        }
    }

    @Override
    public void onUserLeftApplication(InMobiBanner inMobiBanner) {
        Clog.d(Clog.mediationLogTag, "InMobiBanner: onUserLeftApplication" );

    }

    @Override
    public void onRewardsUnlocked(InMobiBanner inMobiBanner, Map<Object, Object> map) {
        Clog.d(Clog.mediationLogTag, "InMobiBanner: onRewardsUnlocked" );
    }

}
