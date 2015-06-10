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

package com.appnexus.opensdk.mediatednativead;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.Pair;
import android.view.Display;
import android.view.WindowManager;

import com.appnexus.opensdk.MediatedNativeAd;
import com.appnexus.opensdk.MediatedNativeAdController;
import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.mediatedviews.AdColonySettings;
import com.appnexus.opensdk.utils.StringUtil;
import com.jirbo.adcolony.AdColony;
import com.jirbo.adcolony.AdColonyNativeAdListener;
import com.jirbo.adcolony.AdColonyNativeAdView;

/**
 * This class is the AdColony native view adapter. It provides the functionality needed to allow
 * an application using the App Nexus SDK to load an native ad through the AdColony SDK. The
 * instantiation of this class is done in response from the AppNexus server for an native
 * placement that is configured to use AdColony to serve it. This class is never directly instantiated
 * by the developer.
 */

public class AdColonyNativeAd implements MediatedNativeAd {

    @Override
    public void requestNativeAd(Context context, String uid, MediatedNativeAdController mBC, TargetingParameters tp) {
        if (mBC != null) {

            String zoneStatus = AdColony.statusForZone(uid);
            if (AdColonySettings.isActive(zoneStatus)) {
                int width = -1;
                // Retrieve the desired width of video view from custom keywords
                // width should be passed in as pixel, not dp
                if (tp != null && tp.getCustomKeywords() != null) {
                    for (Pair<String, String> p : tp.getCustomKeywords()) {
                        if (p.first.equals(AdColonySettings.KEY_NATIVE_AD_WIDTH)) {
                            if (!StringUtil.isEmpty(p.second)) {
                                try {
                                    width = Integer.valueOf(p.second);
                                } catch (NumberFormatException ignore) {
                                }
                            }
                        }
                    }
                }

                if (width <= 0) {
                    // Set AdColony video view's width to screen width
                    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                    Display display = wm.getDefaultDisplay();

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
                        width = display.getWidth();
                    } else {
                        Point size = new Point();
                        display.getSize(size);
                        width = size.x;
                    }
                }

                final AdColonyNativeAdView nativeAdView = new AdColonyNativeAdView((Activity) context, uid, width);
                mBC.onAdLoaded(new AdColonyNativeAdResponse(nativeAdView));
            } else {
                AdColonySettings.AdColonyStatus status = AdColonySettings.AdColonyStatus.getStatus(zoneStatus);
                mBC.onAdFailed(AdColonySettings.errorCodeForStatus(status));
            }
        }

    }
}
