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
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.appnexus.opensdk.MediatedBannerAdView;
import com.appnexus.opensdk.MediatedBannerAdViewController;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.utils.StringUtil;
import com.rfm.sdk.RFMAdRequest;
import com.rfm.sdk.RFMAdView;
import com.rfm.sdk.RFMAdViewListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RubiconBannerAdView implements MediatedBannerAdView {

    private RFMAdView adView;
    public static final String AD_ID = "adId";
    public static final String SERVER_NAME = "serverName";
    public static final String PUB_ID = "pubId";

    @Override
    public View requestAd(MediatedBannerAdViewController mBC, Activity activity, String parameter, String uid,
                          int width, int height, TargetingParameters targetingParameters) {
        String adId = null;
        String serverName = null;
        String pubId = null;
        try {
            if (!StringUtil.isEmpty(uid)) {
                JSONObject idObject = new JSONObject(uid);
                adId = idObject.getString(AD_ID);
                serverName = idObject.getString(SERVER_NAME);
                pubId = idObject.getString(PUB_ID);
            } else {
                mBC.onAdFailed(ResultCode.INVALID_REQUEST);
                return null;
            }
        } catch (JSONException e) {
            mBC.onAdFailed(ResultCode.INVALID_REQUEST);
            return null;
        }

        RFMAdViewListener adViewListener = new RubiconListener(mBC, this.getClass().getSimpleName());

        RFMAdRequest rfmAdRequest = new RFMAdRequest();
        rfmAdRequest.setRFMParams(serverName, pubId, adId);
        rfmAdRequest.setAdDimensionParams(width, height);

        adView = new RFMAdView(activity);
        adView.setRFMAdViewListener(adViewListener);
        adView.enableHWAcceleration(true);


        if (targetingParameters != null) {
            if (targetingParameters.getLocation() != null) {
                rfmAdRequest.setLocation(targetingParameters.getLocation());
            }
            //Optional Ad Targeting info
            rfmAdRequest.setTargetingParams(getTargetingParams(targetingParameters));
        }

        adView.setMinimumWidth(width);
        adView.setMinimumHeight(height);
        adView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));

        adView.requestRFMAd(rfmAdRequest);
        return adView;
    }

    @NonNull
    private HashMap<String, String> getTargetingParams(TargetingParameters targetingParameters) {
        HashMap<String,String> targetingKeywords = new HashMap<String, String>();

        targetingKeywords.put("GENDER", targetingParameters.getGender().toString());
        if(targetingParameters.getAge() != null) {
            targetingKeywords.put("AGE", targetingParameters.getAge());
        }

        if(targetingParameters.getCustomKeywords() != null) {
            for (Pair<String, String> keyword : targetingParameters.getCustomKeywords()) {
                targetingKeywords.put(keyword.first, keyword.second);
            }
        }
        return targetingKeywords;
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
