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
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.amazon.device.ads.*;
import com.appnexus.opensdk.MediatedAdViewController;
import com.appnexus.opensdk.MediatedBannerAdView;
import com.appnexus.opensdk.MediatedBannerAdViewController;
import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.StringUtil;

/**
 * This class is the Amazon banner adaptor it provides the functionality needed to allow
 * an application using the App Nexus SDK to load a banner ad through the Amazon API. The instantiation
 * of this class is done in response from the AppNexus server for a banner placement that is configured
 * to use Amazon to serve it. This class is never instantiated by the developer.
 * <p/>
 * This class also serves as an example of how to write a Mediation adaptor for the AppNexus
 * SDK.
 */
public class AmazonBanner implements MediatedBannerAdView, AdListener {
    MediatedBannerAdViewController mediatedBannerAdViewController = null;

    /**
     * Interface called by the AN SDK to request an ad from the mediating SDK.
     *
     * @param mBC       the object which will be called with events from the 3d party SDK
     * @param activity  the activity from which this is launched
     * @param parameter String parameter received from the server for instantiation of this object
     * @param uid       The 3rd party placement , in adMob this is the adUnitID
     * @param width     Width of the ad
     * @param height    Height of the ad
     */
    @Override
    public View requestAd(MediatedBannerAdViewController mBC, Activity activity, String parameter, String uid, int width, int height, TargetingParameters tp) {
        AdLayout adView = new AdLayout(activity, new AdSize(width, height));

        this.mediatedBannerAdViewController = mBC;

        adView.setListener(this);

        AdTargetingOptions targetingOptions = new AdTargetingOptions();
        if (tp != null) {
            if (!StringUtil.isEmpty(tp.getAge())) {
                try {
                    targetingOptions.setAge(Integer.parseInt(tp.getAge()));
                } catch (NumberFormatException e) {
                }
            }

            switch (tp.getGender()) {
                case MALE:
                    targetingOptions.setGender(AdTargetingOptions.Gender.MALE);
                    break;
                case FEMALE:
                    targetingOptions.setGender(AdTargetingOptions.Gender.FEMALE);
                    break;
                default:
                    targetingOptions.setGender(AdTargetingOptions.Gender.UNKNOWN);
                    break;
            }

            for (Pair<String, String> p : tp.getCustomKeywords()) {
                targetingOptions.setAdvancedOption(p.first, p.second);
            }

            targetingOptions.enableGeoLocation (tp.getLocation() != null);
        }

        //Amazon won't load ads unless layout parameters are set
        adView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));

        adView.loadAd(targetingOptions);

        return adView;
    }

    @Override
    public void destroy() {

    }

    @Override
    public void onAdLoaded(AdLayout adLayout, AdProperties adProperties) {
        Clog.d(Clog.mediationLogTag, "AmazonBanner - onAdLoaded: " + adLayout);
        if (mediatedBannerAdViewController != null) {
            mediatedBannerAdViewController.onAdLoaded();
        }
    }

    @Override
    public void onAdExpanded(AdLayout adLayout) {
        Clog.d(Clog.mediationLogTag, "AmazonBanner - onAdExpanded: " + adLayout);
        if (mediatedBannerAdViewController != null) {
            mediatedBannerAdViewController.onAdExpanded();
        }

    }

    @Override
    public void onAdCollapsed(AdLayout adLayout) {
        Clog.d(Clog.mediationLogTag, "AmazonBanner - onAdCollapsed: " + adLayout);
        if (mediatedBannerAdViewController != null) {
            mediatedBannerAdViewController.onAdCollapsed();
        }
    }

    @Override
    public void onAdFailedToLoad(AdLayout adLayout, AdError adError) {
        Clog.d(Clog.mediationLogTag, "AmazonBanner - onAdFailedToLoad: " + adError.getMessage());
        if (mediatedBannerAdViewController != null) {
            if (adError != null) {
                switch (adError.getCode()) {
                    case INTERNAL_ERROR:
                        mediatedBannerAdViewController.onAdFailed(MediatedAdViewController.RESULT.INTERNAL_ERROR);
                        break;
                    case NETWORK_ERROR:
                        mediatedBannerAdViewController.onAdFailed(MediatedAdViewController.RESULT.NETWORK_ERROR);
                        break;
                    case NO_FILL:
                        mediatedBannerAdViewController.onAdFailed(MediatedAdViewController.RESULT.UNABLE_TO_FILL);
                        break;
                    case REQUEST_ERROR:
                        mediatedBannerAdViewController.onAdFailed(MediatedAdViewController.RESULT.INVALID_REQUEST);
                        break;
                    default:
                        mediatedBannerAdViewController.onAdFailed(MediatedAdViewController.RESULT.INTERNAL_ERROR);
                        break;
                }
            } else {
                mediatedBannerAdViewController.onAdFailed(MediatedAdViewController.RESULT.INTERNAL_ERROR);
            }
        }
    }
}
