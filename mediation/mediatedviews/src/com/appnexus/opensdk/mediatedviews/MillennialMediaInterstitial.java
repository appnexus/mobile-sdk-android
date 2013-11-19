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
import com.appnexus.opensdk.MediatedInterstitialAdView;
import com.appnexus.opensdk.MediatedInterstitialAdViewController;
import com.appnexus.opensdk.utils.Clog;
import com.millennialmedia.android.MMInterstitial;
import com.millennialmedia.android.MMSDK;

/**
 * This class is the Millennial Media interstitial adaptor it provides the functionality needed to allow 
 * an application using the App Nexus SDK to load an interstitial ad through the Millennial Media SDK. The instantiation 
 * of this class is done in response from the AppNexus server for a banner placement that is configured 
 * to use MM  to serve it. This class is never instantiated by the developer. 
 * 
 * This class also serves as an example of how to write a Mediation adaptor for the AppNexus 
 * SDK. 
 *
 */
public class MillennialMediaInterstitial implements MediatedInterstitialAdView {

    private MMInterstitial iad;

    @Override
    public void requestAd(MediatedInterstitialAdViewController mIC, Activity activity, String parameter, String uid) {
        if (mIC == null) {
            Clog.e(Clog.mediationLogTag, "MillennialMediaInterstitial - requestAd called with null controller");
            return;
        }

        if (activity == null) {
            Clog.e(Clog.mediationLogTag, "MillennialMediaInterstitial - requestAd called with null activity");
            return;
        }
        Clog.d(Clog.mediationLogTag, String.format("MillennialMediaInterstitial - requesting an interstitial ad: [%s, %s]", parameter, uid));

        MMSDK.initialize(activity);

        iad = new MMInterstitial(activity);
        iad.setApid(uid);
        iad.setListener(new MillennialMediaListener(mIC, getClass().getSimpleName()));

        if (!iad.isAdAvailable()) {
            iad.fetch();
        } else {
            Clog.w(Clog.mediationLogTag, "MillennialMediaInterstitial - ad was available from cache. show it instead of fetching");
            mIC.onAdLoaded();
        }
    }

    @Override
    public void show() {
        Clog.d(Clog.mediationLogTag, "MillennialMediaInterstitial - show called");
        if (iad == null) {
            Clog.e(Clog.mediationLogTag, "MillennialMediaInterstitial - show called while interstitial ad view was null");
            return;
        }
        if (!iad.isAdAvailable()) {
            Clog.e(Clog.mediationLogTag, "MillennialMediaInterstitial - show called while interstitial ad was unavailable");
            return;
        }

        if (iad.display(false))
            Clog.d(Clog.mediationLogTag, "MillennialMediaInterstitial - display called successfully");
        else
            Clog.e(Clog.mediationLogTag, "MillennialMediaInterstitial - display failed");
    }
}