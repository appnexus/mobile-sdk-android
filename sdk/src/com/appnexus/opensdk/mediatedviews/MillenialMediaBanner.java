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
import android.view.View;
import com.appnexus.opensdk.MediatedBannerAdView;
import com.appnexus.opensdk.MediatedBannerAdViewController;
import com.millennialmedia.android.*;

public class MillenialMediaBanner implements MediatedBannerAdView, RequestListener {
    MediatedBannerAdViewController mMediatedBannerAdViewController;

    public MillenialMediaBanner() {
    }

    @Override
    public View requestAd(MediatedBannerAdViewController mBC, Activity activity, String parameter, String uid,
                          int width, int height, View adSpace) {
        mMediatedBannerAdViewController = mBC;

        MMSDK.initialize(activity);

        MMAdView adView = new MMAdView(activity);
        adView.setApid(uid);
        adView.setWidth(width);
        adView.setHeight(height);

        MMRequest mmRequest = new MMRequest();
        adView.setMMRequest(mmRequest);
        adView.setListener(this);
        adView.getAd();

        return adView;
    }

    // occurs when ad is clicked and browser is launched.
    @Override
    public void MMAdOverlayLaunched(MMAd mmAd) {
        if (mMediatedBannerAdViewController != null)
            mMediatedBannerAdViewController.onAdExpanded();
    }

    @Override
    public void MMAdOverlayClosed(MMAd mmAd) {
        if (mMediatedBannerAdViewController != null)
            mMediatedBannerAdViewController.onAdCollapsed();
    }

    @Override
    public void MMAdRequestIsCaching(MMAd mmAd) {
    }

    @Override
    public void requestCompleted(MMAd mmAd) {
        if (mMediatedBannerAdViewController != null)
            mMediatedBannerAdViewController.onAdLoaded();
    }

    @Override
    public void requestFailed(MMAd mmAd, MMException e) {
        if (mMediatedBannerAdViewController != null)
            mMediatedBannerAdViewController.onAdFailed();
    }

    @Override
    public void onSingleTap(MMAd mmAd) {
        if (mMediatedBannerAdViewController != null)
            mMediatedBannerAdViewController.onAdClicked();
    }
}
