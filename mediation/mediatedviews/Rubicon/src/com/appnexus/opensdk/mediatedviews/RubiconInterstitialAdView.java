/*
 *    Copyright 2016 APPNEXUS INC
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
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.appnexus.opensdk.MediatedInterstitialAdView;
import com.appnexus.opensdk.MediatedInterstitialAdViewController;
import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.utils.Clog;
import com.rfm.sdk.RFMAdRequest;
import com.rfm.sdk.RFMAdView;
import com.rfm.sdk.RFMConstants;
import com.rfm.sdk.RFMInterstitialAdViewListener;
import com.rfm.util.RFMLog;

import java.util.HashMap;

/**
 * This class is the MoPub banner adaptor it provides the functionality needed to allow
 * an application using the AppNexus SDK to load a banner ad through the MoPub SDK. The instantiation
 * of this class is done in response from the AppNexus server for a banner placement that is configured
 * to use MoPub to serve it. This class is never directly instantiated by the application.
 * <p/>
 * This class also serves as an example of how to write a Mediation adaptor for the AppNexus
 * SDK.
 */
public class RubiconInterstitialAdView implements MediatedInterstitialAdView {
    public static final String DEFAULT_SERVER_NAME = "http://mrp.rubiconproject.com/";
    private RFMAdView adView;
    public static final String DEFAULT_AD_ID = "281844F0497A0130031D123139244773";
    public static final String DEFAULT_PUB_ID = "111008";


    private String getCustomKeywords(TargetingParameters targetingParameters){
        String customKeywordString = "";
        for (Pair<String,String> keyword:targetingParameters.getCustomKeywords()) {
            customKeywordString = customKeywordString + keyword.first + "=" +keyword.second + ",";
        }
        if(customKeywordString.trim().length() > 0){
            customKeywordString = customKeywordString.substring(0, customKeywordString.length()-1);
        }
        return customKeywordString;
    }

    @Override
    public void destroy() {
        if (adView != null) {
            adView.setRFMAdViewListener(null);
            adView.rfmAdViewDestroy();
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
        destroy();
    }

    @Override
    public void requestAd(MediatedInterstitialAdViewController mIC, Activity activity, String parameter, String uid, TargetingParameters targetingParameters) {
        RFMInterstitialAdViewListener adViewListener = new RubiconListener(mIC, this.getClass().getSimpleName());
        adView = new RFMAdView(activity);
        RFMAdRequest rfmAdRequest = new RFMAdRequest();
        rfmAdRequest.setRFMParams(DEFAULT_SERVER_NAME, DEFAULT_PUB_ID, DEFAULT_AD_ID);

        rfmAdRequest.setAdDimensionParams(-1, -1);
        rfmAdRequest.setRFMAdAsInterstitial(true);
        adView.setRFMAdViewListener(adViewListener);
        adView.enableHWAcceleration(true);

        /**
         * TODO: NEED TO BE REMOVED
         */
        RFMLog.setRFMLogLevel(RFMLog.INFO);
        rfmAdRequest.setRFMAdMode(RFMConstants.RFM_AD_MODE_TEST);
        rfmAdRequest.setRFMTestAdId(DEFAULT_AD_ID);

        if (targetingParameters != null) {
            if (targetingParameters.getLocation() != null) {
                rfmAdRequest.setLocation(targetingParameters.getLocation());
            }

            //Optional Ad Targeting info
            HashMap<String,String> mTargetingInfo = new HashMap<String, String>();
            mTargetingInfo.put("GENDER", targetingParameters.getGender().toString());
            mTargetingInfo.put("AGE", targetingParameters.getAge());
            if(targetingParameters.getCustomKeywords().size() > 0) {
                mTargetingInfo.put("NBA_KV", getCustomKeywords(targetingParameters));
            }

//            rfmAdRequest.setTargetingParams(mTargetingInfo);
        }

        adView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER));

        adView.requestRFMAd(rfmAdRequest);
    }


    @Override
    public void show() {
        Clog.d(Clog.mediationLogTag, "show called");
        if (!this.isReady()) {
            Clog.d(Clog.mediationLogTag, "show called while interstitial ad view was unavailable");
        }

    }

    @Override
    public boolean isReady() {
        return (adView != null) && (adView.isAdAvailableToDisplay());
    }
}
