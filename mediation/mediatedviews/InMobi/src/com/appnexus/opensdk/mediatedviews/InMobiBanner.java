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

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.appnexus.opensdk.MediatedBannerAdView;
import com.appnexus.opensdk.MediatedBannerAdViewController;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.mediatednativead.InMobiSettings;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.StringUtil;


/**
 * This class is the InMobi banner ad view adapter - it allows an application that integrates with
 * AppNexus to request a banner from InMobi Android SDK. A developer needs to set up an account in
 * the AppNexus console in order to request ads from InMobi. This class is never instantiated by the
 * application.
 */
public class InMobiBanner implements MediatedBannerAdView {
    com.inmobi.ads.InMobiBanner imBanner;


    /**
     * @param mBC       The controller to notify on load, failure, etc.
     * @param activity  The activity from which this method was called.
     * @param parameter An optional opaque string passed from the Ad Network Manager, this can be used to
     *                  defined SDK-specific parameters such as additional targeting information.  The
     *                  encoding of the contents of this string are entirely up to the implementation of
     *                  the third-party SDK adaptor.
     * @param uid       The network ID for this ad call.  This ID is opaque to the AppNexus SDK and its contents and
     *                  their encoding are up to the implementation of the third-party SDK.
     * @param width     The width of the advertisement in pixels as defined in the {@link BannerAdView} object
     *                  that initiated this call.
     * @param height    The height of the advertisement in pixels as defined in the {@link BannerAdView} object
     *                  that initiated this call.
     * @param tp        Targeting parameters that are set in AppNexus Public API
     * @return Banner View from InMobi to display
     */
    @Override
    public View requestAd(MediatedBannerAdViewController mBC, Activity activity, String parameter,
                          String uid, int width, int height, TargetingParameters tp) {
        if (mBC != null) {
            if (StringUtil.isEmpty(InMobiSettings.INMOBI_APP_ID)) {
                Clog.e(Clog.mediationLogTag, "InMobi mediation failed. Call InMobiSettings.setInMobiAppId(String key, Context context) to set the app id.");
                mBC.onAdFailed(ResultCode.MEDIATED_SDK_UNAVAILABLE);
                return null;
            }
            try {
                long placementID = Long.parseLong(uid);
                imBanner = new com.inmobi.ads.InMobiBanner(activity, placementID);
                imBanner.setEnableAutoRefresh(false);

                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(toPixelUnits(activity,width), toPixelUnits(activity,height));
                imBanner.setLayoutParams(lp);
                InMobiSettings.setTargetingParams(tp);

                imBanner.setListener(new InMobiListener(mBC, this.getClass().getSimpleName()));
                imBanner.load();
                return imBanner;
            } catch (NumberFormatException e) {
                mBC.onAdFailed(ResultCode.INVALID_REQUEST);
            }
        }

        return null;
    }

    @Override
    public void destroy() {
        if (imBanner != null) {
            imBanner.setListener(null);
            imBanner = null;
        }
    }

    @Override
    public void onPause() {
        //InMobi lacks a pause public api
    }

    @Override
    public void onResume() {
        //InMobi lacks a resume public api
    }

    @Override
    public void onDestroy() {
        destroy();
    }

    private int toPixelUnits(Activity activity, int dipUnit) {
        float density = activity.getResources().getDisplayMetrics().density;
        return Math.round(dipUnit * density);
    }
}
