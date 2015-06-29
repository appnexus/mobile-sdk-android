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

import com.appnexus.opensdk.MediatedBannerAdView;
import com.appnexus.opensdk.MediatedBannerAdViewController;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.TargetingParameters;
import com.vdopia.ads.lw.LVDOAdSize;
import com.vdopia.ads.lw.LVDOAdView;

/**
 * This class is the Vdopia banner adapter - it provides the functionality needed to allow an
 * application using the AppNexus SDK to load a banner ad through the Vdopia publisher SDK.
 * The instantiation of this class is done in response from the AppNexus server for an banner placement
 * that is configured to use Vdopia to serve it. This class is never directly instantiated by the
 * application.
 * <p/>
 * This class also serves as an example of how to write a Mediation adaptor for the AppNexus
 * SDK.
 */
public class VdopiaBanner implements MediatedBannerAdView {
    LVDOAdView adView;

    /**
     * @param mBC       The controller to notify on load, failure, etc.
     * @param activity  The activity from which this method was
     *                  called.
     * @param parameter An optional opaque string passed from the
     *                  Ad Network Manager, this can be used to
     *                  defined SDK-specific parameters such as
     *                  additional targeting information.  The
     *                  encoding of the contents of this string
     *                  are entirely up to the implementation of
     *                  the third-party SDK adaptor.
     * @param uid       The network ID for this ad call.  This ID is
     *                  opaque to the AppNexus SDK and its contents and
     *                  their encoding are up to the implementation of
     *                  the third-party SDK.
     * @param width     The width of the advertisement in pixels as
     *                  defined in the {@link BannerAdView} object
     *                  that initiated this call.
     * @param height    The height of the advertisement in pixels as
     *                  defined in the {@link BannerAdView} object
     *                  that initiated this call.
     * @param tp        Targeting parameters passed from AppNexus SDK, set
     *                  by the developer.
     * @return View from the ad network
     */
    @Override
    public void requestAd(MediatedBannerAdViewController mBC, Activity activity, String parameter,
                          String uid, int width, int height, TargetingParameters tp) {
        if (mBC != null) {
            if (width == 300 && height == 250) {
                adView = new LVDOAdView(activity, LVDOAdSize.IAB_MRECT, uid);
            } else if (width == 728 && height == 90) {
                adView = new LVDOAdView(activity, LVDOAdSize.IAB_LEADERBOARD, uid);
            } else if (width == 320 && height == 50) {
                adView = new LVDOAdView(activity, LVDOAdSize.BANNER, uid);
            } else {
                mBC.onAdFailed(ResultCode.INVALID_REQUEST);
                return;
            }

            VdopiaListener listener = new VdopiaListener(mBC, this.getClass().getSimpleName());
            adView.setAdListener(listener);
            mBC.setView(adView);
            adView.loadAd(VdopiaSettings.buildRequest(tp));
        }
    }

    @Override
    public void destroy() {
        if (adView != null) {
            adView.setAdListener(null);
            adView = null;
        }
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {

    }

}
