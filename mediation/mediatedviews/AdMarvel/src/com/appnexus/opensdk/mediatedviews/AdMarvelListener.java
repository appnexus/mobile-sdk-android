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

import android.util.Pair;

import com.admarvel.android.ads.AdMarvelActivity;
import com.admarvel.android.ads.AdMarvelAd;
import com.admarvel.android.ads.AdMarvelInterstitialAds;
import com.admarvel.android.ads.AdMarvelUtils;
import com.admarvel.android.ads.AdMarvelVideoActivity;
import com.admarvel.android.ads.AdMarvelView;
import com.appnexus.opensdk.MediatedAdViewController;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class AdMarvelListener implements AdMarvelView.AdMarvelViewListener, AdMarvelInterstitialAds.AdMarvelInterstitialAdListener {
    static final String SITE_ID = "site_id";
    static final String PARTNER_ID = "partner_id";

    private static final String KEYWORDS = "KEYWORDS";
    private static final String GEOLOCATION = "GEOLOCATION";
    private static final String AGE = "AGE";
    private static final String GENDER = "GENDER";


    private final MediatedAdViewController mediatedAdViewController;
    private final String className;

    public AdMarvelListener(MediatedAdViewController mediatedAdViewController, String className) {
        this.mediatedAdViewController = mediatedAdViewController;
        this.className = className;
    }


    public static Map<String, Object> getTargetingParameters(TargetingParameters targetingParameters) {
        Map<String, Object> targetParams = new HashMap<String, Object>();
        if (targetingParameters != null) {
            switch (targetingParameters.getGender()) {
                case FEMALE:
                    targetParams.put(GENDER, "female");
                    break;
                case MALE:
                    targetParams.put(GENDER, "male");
                    break;
                default:
                    break;
            }

            if (!StringUtil.isEmpty(targetingParameters.getAge())) {
                targetParams.put(AGE, targetingParameters.getAge());
            }

            StringBuilder sb = new StringBuilder();
            for (Pair<String, String> p : targetingParameters.getCustomKeywords()) {
                if (!StringUtil.isEmpty(p.first)) {
                    sb.append(p.first).append(" ");
                }
                if (!StringUtil.isEmpty(p.second)) {
                    sb.append(p.second).append(" ");
                }
            }
            String keywords = sb.toString();
            if (!StringUtil.isEmpty(keywords)) {
                targetParams.put(KEYWORDS, keywords);
            }

            if (targetingParameters.getLocation() != null) {
                String location = targetingParameters.getLocation().getLatitude() + "," + targetingParameters.getLocation().getLongitude();
                targetParams.put(GEOLOCATION, location);
            }
        }

        return targetParams;
    }

    /**
     * Implementation of AdMarvelViewListener
     */

    @Override
    public void onReceiveAd(AdMarvelView adMarvelView) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdLoaded();
        }

    }

    @Override
    public void onFailedToReceiveAd(AdMarvelView adMarvelView, int i, AdMarvelUtils.ErrorReason errorReason) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdFailed(getResultCode(errorReason));
        }
    }

    @Override
    public void onClickAd(AdMarvelView adMarvelView, String s) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdClicked();
        }

    }

    @Override
    public void onRequestAd(AdMarvelView adMarvelView) {
        // do nothing
    }

    @Override
    public void onExpand(AdMarvelView adMarvelView) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdExpanded();
        }
    }

    @Override
    public void onClose(AdMarvelView adMarvelView) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdCollapsed();
        }
    }

    /**
     * Implementation of AdMarvelInterstitialAdListener
     */

    @Override
    public void onRequestInterstitialAd(AdMarvelInterstitialAds adMarvelInterstitialAds) {
        // do nothing
    }

    private AdMarvelUtils.SDKAdNetwork sdkAdNetwork = null;
    private AdMarvelAd adMarvelAd = null;

    AdMarvelAd getAdMarvelAd() {
        return adMarvelAd;
    }

    AdMarvelUtils.SDKAdNetwork getSdkAdNetwork() {
        return sdkAdNetwork;
    }

    @Override
    public void onReceiveInterstitialAd(AdMarvelUtils.SDKAdNetwork sdkAdNetwork, AdMarvelInterstitialAds adMarvelInterstitialAds, AdMarvelAd adMarvelAd) {
        if (mediatedAdViewController != null) {
            this.sdkAdNetwork = sdkAdNetwork;
            this.adMarvelAd = adMarvelAd;
            mediatedAdViewController.onAdLoaded();
        }
    }

    @Override
    public void onFailedToReceiveInterstitialAd(AdMarvelUtils.SDKAdNetwork sdkAdNetwork, AdMarvelInterstitialAds adMarvelInterstitialAds, int i, AdMarvelUtils.ErrorReason errorReason) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdFailed(getResultCode(errorReason));
        }
    }

    @Override
    public void onCloseInterstitialAd(AdMarvelInterstitialAds adMarvelInterstitialAds) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdCollapsed();
        }

    }

    @Override
    public void onAdmarvelActivityLaunched(AdMarvelActivity adMarvelActivity, AdMarvelInterstitialAds adMarvelInterstitialAds) {
    }

    @Override
    public void onAdMarvelVideoActivityLaunched(AdMarvelVideoActivity adMarvelVideoActivity, AdMarvelInterstitialAds adMarvelInterstitialAds) {
    }

    @Override
    public void onClickInterstitialAd(String s, AdMarvelInterstitialAds adMarvelInterstitialAds) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdClicked();
        }
    }

    @Override
    public void onInterstitialDisplayed(AdMarvelInterstitialAds adMarvelInterstitialAds) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdExpanded();
        }
    }

    private ResultCode getResultCode(AdMarvelUtils.ErrorReason errorReason) {
        ResultCode resultCode = ResultCode.INTERNAL_ERROR;
        switch (errorReason) {
            case SITE_ID_OR_PARTNER_ID_NOT_PRESENT:
                resultCode = ResultCode.INVALID_REQUEST;
                break;
            case SITE_ID_AND_PARTNER_ID_DO_NOT_MATCH:
                resultCode = ResultCode.INVALID_REQUEST;
                break;
            case BOT_USER_AGENT_FOUND:
                break;
            case NO_BANNER_FOUND:
                break;
            case NO_AD_FOUND:
                resultCode = ResultCode.UNABLE_TO_FILL;
                break;
            case NO_USER_AGENT_FOUND:
                break;
            case SITE_ID_NOT_PRESENT:
                resultCode = ResultCode.INVALID_REQUEST;
                break;
            case PARTNER_ID_NOT_PRESENT:
                resultCode = ResultCode.INVALID_REQUEST;
                break;
            case NO_NETWORK_CONNECTIVITY:
                resultCode = ResultCode.NETWORK_ERROR;
                break;
            case NETWORK_CONNECTIVITY_DISRUPTED:
                resultCode = ResultCode.NETWORK_ERROR;
                break;
            case AD_REQUEST_XML_PARSING_EXCEPTION:
                break;
            case AD_REQUEST_IN_PROCESS_EXCEPTION:
                break;
            case AD_UNIT_NOT_ABLE_TO_RENDER:
                break;
            case AD_REQUEST_MISSING_XML_ELEMENTS:
                break;
            case AD_REQUEST_SDK_TYPE_UNSUPPORTED:
                break;
            case AD_UNIT_NOT_ABLE_TO_LOAD:
                resultCode = ResultCode.UNABLE_TO_FILL;
                break;
            case AD_UNIT_IN_DISPLAY_STATE:
                break;
        }
        return resultCode;
    }

}