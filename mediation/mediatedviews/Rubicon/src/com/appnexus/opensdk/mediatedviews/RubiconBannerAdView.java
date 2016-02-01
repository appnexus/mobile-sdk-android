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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.appnexus.opensdk.MediatedBannerAdView;
import com.appnexus.opensdk.MediatedBannerAdViewController;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.TargetingParameters;
import com.rfm.sdk.RFMAdRequest;
import com.rfm.sdk.RFMAdView;
import com.rfm.sdk.RFMAdViewListener;
import com.rfm.sdk.RFMConstants;
import com.rfm.util.RFMLog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class is the MoPub banner adaptor it provides the functionality needed to allow
 * an application using the AppNexus SDK to load a banner ad through the MoPub SDK. The instantiation
 * of this class is done in response from the AppNexus server for a banner placement that is configured
 * to use MoPub to serve it. This class is never directly instantiated by the application.
 * <p/>
 * This class also serves as an example of how to write a Mediation adaptor for the AppNexus
 * SDK.
 */
public class RubiconBannerAdView implements MediatedBannerAdView {

    private RFMAdView adView;

    @Override
    public View requestAd(MediatedBannerAdViewController mBC, Activity activity, String parameter, String uid,
                          int width, int height, TargetingParameters targetingParameters) {
        String adId = null;
        String serverName = null;
        String pubId = null;
        try {
            if(uid != null){
                JSONObject idObject = new JSONObject(uid);
                adId = idObject.getString(RubiconSettings.AD_ID);
                serverName = idObject.getString(RubiconSettings.SERVER_NAME);
                pubId = idObject.getString(RubiconSettings.PUB_ID);
            }else{
                mBC.onAdFailed(ResultCode.INVALID_REQUEST);
            }
        } catch (JSONException e) {
            mBC.onAdFailed(ResultCode.INVALID_REQUEST);
        }

        RFMAdViewListener  adViewListener = new RubiconListener(mBC, this.getClass().getSimpleName());

        RFMAdRequest rfmAdRequest = new RFMAdRequest();
        rfmAdRequest.setRFMParams(serverName, pubId, adId);
        rfmAdRequest.setAdDimensionParams(width, height);

        adView = new RFMAdView(activity);
        adView.setRFMAdViewListener(adViewListener);
        adView.enableHWAcceleration(true);

        /**
         * TODO: These 3 lines need to be removed
         */
        RFMLog.setRFMLogLevel(RFMLog.INFO);
        rfmAdRequest.setRFMAdMode(RFMConstants.RFM_AD_MODE_TEST);
        rfmAdRequest.setRFMTestAdId(adId);

        if (targetingParameters != null) {
            if (targetingParameters.getLocation() != null) {
                rfmAdRequest.setLocation(targetingParameters.getLocation());
            }
            //Optional Ad Targeting info
            rfmAdRequest.setTargetingParams(RubiconSettings.getTargetingParams(targetingParameters));
        }

        adView.setMinimumWidth(width);
        adView.setMinimumHeight(height);
        adView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));

        adView.requestRFMAd(rfmAdRequest);
        return adView;
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

}
