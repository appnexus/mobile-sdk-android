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
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.appnexus.opensdk.MediatedBannerAdView;
import com.appnexus.opensdk.MediatedBannerAdViewController;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.TargetingParameters;
import com.millennialmedia.InlineAd;
import com.millennialmedia.MMException;
import com.millennialmedia.MMSDK;
import com.millennialmedia.UserData;

/**
 * This class is the Millennial Media banner adaptor it provides the functionality needed to allow
 * an application using the AppNexus SDK to load a banner ad through the Millennial Media SDK. The instantiation
 * of this class is done in response from the AppNexus server for a banner placement that is configured
 * to use MM to serve it. This class is never directly instantiated by the application.
 * <p/>
 * This class also serves as an example of how to write a Mediation adaptor for the AppNexus
 * SDK.
 */
public class MillennialMediaBanner implements MediatedBannerAdView {
    InlineAd inlineAd;

    @Override
    public View requestAd(MediatedBannerAdViewController mBC, Activity activity, String parameter, String uid,
                          int width, int height, TargetingParameters targetingParameters) {
        if (activity != null) {
            try {
                MillennialMediaListener mmListener = new MillennialMediaListener(mBC, super.getClass().getSimpleName());
                mmListener.printToClog(String.format("requesting an ad: [%s, %s, %dx%d]", parameter, uid, width, height));

                MMSDK.initialize(activity);
                if (!MillennialMediaSettings.siteId.isEmpty()) {
                    MMSDK.setAppInfo(MillennialMediaSettings.getAppInfo());
                }

                // SDK must be initialized first before creating userdata instance
                UserData userData = MillennialMediaSettings.getUserData(targetingParameters, activity);
                FrameLayout adContainer = new FrameLayout(activity);
                adContainer.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                inlineAd = InlineAd.createInstance(uid, adContainer);
                inlineAd.setListener(mmListener);
                InlineAd.AdSize size;
                if (width == 320 && height == 50) {
                    size = InlineAd.AdSize.BANNER;
                } else if (width == 468 && height == 60) {
                    size = InlineAd.AdSize.FULL_BANNER;
                } else if (width == 320 && height == 100) {
                    size = InlineAd.AdSize.LARGE_BANNER;
                } else if (width == 728 && height == 90) {
                    size = InlineAd.AdSize.LEADERBOARD;
                } else if (width == 300 && height == 250) {
                    size = InlineAd.AdSize.MEDIUM_RECTANGLE;
                } else {
                    size = new InlineAd.AdSize(width, height);
                }
                final InlineAd.InlineAdMetadata inlineAdMetadata = new InlineAd.InlineAdMetadata().setAdSize(size);
                MMSDK.setUserData(userData);
                inlineAd.request(inlineAdMetadata);
                return adContainer;
            } catch (MMException e) {
                if (mBC != null) {
                    mBC.onAdFailed(ResultCode.INTERNAL_ERROR);
                }
            }

        }
        return null;
    }

    @Override
    public void destroy() {
        //No available API
        if (inlineAd != null) {
            inlineAd = null;
        }
    }

    @Override
    public void onPause() {
        //No available API
    }

    @Override
    public void onResume() {
        //No available API
    }

    @Override
    public void onDestroy() {
        destroy(); //No available API
    }
}
