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
import android.view.View;

import com.appnexus.opensdk.MediatedBannerAdView;
import com.appnexus.opensdk.MediatedBannerAdViewController;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.utils.Clog;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;

/**
 * This class is the Facebook banner adapter - it provides the functionality needed to allow
 * an application using the AppNexus SDK to load a banner ad through the Facebook Audience Network SDK. The instantiation
 * of this class is done in response from the AppNexus server for a banner placement that is configured
 * to use Facebook to serve it. This class is never directly instantiated by the application.
 *
 * This class also serves as an example of how to write a Mediation adaptor for the AppNexus
 * SDK.
 *
 */
public class FacebookBanner implements MediatedBannerAdView {
    private AdView adView;

    @Override
    public View requestAd(MediatedBannerAdViewController mBC, Activity activity, String parameter, String uid, int width, int height, TargetingParameters tp) {
        FacebookListener fbListener = new FacebookListener(mBC, this.getClass().getSimpleName());
        AdSize adSize;
        if (width == 320 && height == 50) {
            adSize = AdSize.BANNER_320_50;
        } else if (height == 50) {
            adSize = AdSize.BANNER_HEIGHT_50;
        } else if (height == 90) {
            adSize = AdSize.BANNER_HEIGHT_90;
        } else if (height == 250) {
            adSize = AdSize.RECTANGLE_HEIGHT_250;
        } else {
            Clog.e(Clog.mediationLogTag, "Facebook - Attempted to instantiate with size other than the allowed size of 320x50, -1x50, -1x90, -1x250");
            mBC.onAdFailed(ResultCode.UNABLE_TO_FILL);
            return null;
        }
        adView = new AdView(activity, uid, adSize);
        adView.setAdListener(fbListener);
        adView.loadAd();
        return adView;
    }

    @Override
    public void destroy() {
        if (adView != null) {
            adView.destroy();
            try {
                adView.setAdListener(null);
            } catch (NullPointerException npe) {
                //Facebook's rate limiting makes this hard to test
                //catch npe to be safe
            }
            adView = null;
        }
    }

    @Override
    public void onPause() {
        //Facebook lacks a pause public api
    }

    @Override
    public void onResume() {
        //Facebook lacks a resume public api
    }

    @Override
    public void onDestroy() {
        destroy();
    }
}