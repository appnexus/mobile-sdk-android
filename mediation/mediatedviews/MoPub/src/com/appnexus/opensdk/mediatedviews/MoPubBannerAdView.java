/*
 *    Copyright 2014 APPNEXUS INC
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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.appnexus.opensdk.MediatedBannerAdView;
import com.appnexus.opensdk.MediatedBannerAdViewController;
import com.appnexus.opensdk.TargetingParameters;
import com.mopub.mobileads.MoPubView;

/**
 * This class is the MoPub banner adaptor it provides the functionality needed to allow
 * an application using the AppNexus SDK to load a banner ad through the MoPub SDK. The instantiation
 * of this class is done in response from the AppNexus server for a banner placement that is configured
 * to use MoPub to serve it. This class is never directly instantiated by the application.
 *
 * This class also serves as an example of how to write a Mediation adaptor for the AppNexus
 * SDK.
 *
 */
public class MoPubBannerAdView implements MediatedBannerAdView {
    private MoPubView adView;
    private MoPubListener mpListener;
    @Override
    public View requestAd(MediatedBannerAdViewController mBC, Activity activity, String parameter, String uid,
                          int width, int height, TargetingParameters targetingParameters) {
        mpListener = new MoPubListener(mBC, this.getClass().getSimpleName());
        adView = new MoPubView(activity);
        adView.setAdUnitId(uid);
        adView.setBannerAdListener(mpListener);
        adView.setAutorefreshEnabled(false);

        if (targetingParameters != null) {
            if (targetingParameters.getLocation() != null) {
                adView.setLocation(targetingParameters.getLocation());
            }

            String keywords = MoPubListener.keywordsFromTargetingParameters(targetingParameters);
            adView.setKeywords(keywords);
        }

        adView.setMinimumWidth(width);
        adView.setMinimumHeight(height);
        adView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER));

        adView.loadAd();
        return adView;
    }

    @Override
    public void destroy() {
        if (adView != null) {
            adView.setBannerAdListener(null);
            mpListener=null;
            adView.destroy();
            adView=null;
        }
    }

    @Override
    public void onPause() {
        //Mopub lacks a pause public api
    }

    @Override
    public void onResume() {
        //Mopub lacks a resume public api
    }

    @Override
    public void onDestroy() {
        destroy();
    }

}
