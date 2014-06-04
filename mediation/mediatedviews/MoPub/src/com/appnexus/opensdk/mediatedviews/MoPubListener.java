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
import com.appnexus.opensdk.MediatedAdViewController;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.StringUtil;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubView;

public class MoPubListener implements MoPubView.BannerAdListener, MoPubInterstitial.InterstitialAdListener {

    private final MediatedAdViewController mediatedAdViewController;
    private final String className;

    public MoPubListener(MediatedAdViewController mediatedAdViewController, String className) {
        this.mediatedAdViewController = mediatedAdViewController;
        this.className = className;
    }

    // Sent when the banner has successfully retrieved an ad.
    public void onBannerLoaded(MoPubView banner) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdLoaded();
        }
    }

    // Sent when the banner has failed to retrieve an ad. You can use the MoPubErrorCode value to diagnose the cause of failure.
    public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
        Clog.d(Clog.mediationLogTag, className + " | MoPub - onBannerFailed called for MoPubView with ErrorCode: " + errorCode);
        handleMPErrorCode(errorCode);
    }

    // Sent when the user has tapped on the banner.
    public void onBannerClicked(MoPubView banner) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdClicked();
        }
    }

    // Sent when the banner has just taken over the screen.
    public void onBannerExpanded(MoPubView banner) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdExpanded();
        }
    }

    // Sent when an expanded banner has collapsed back to its original size.
    public void onBannerCollapsed(MoPubView banner) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdCollapsed();
        }
    }

    @Override
    public void onInterstitialLoaded(MoPubInterstitial interstitial) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdLoaded();
        }
    }

    @Override
    public void onInterstitialClicked(MoPubInterstitial interstitial) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdClicked();
        }
    }

    @Override
    public void onInterstitialDismissed(MoPubInterstitial interstitial) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdCollapsed();
        }
    }

    @Override
    public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
        Clog.d(Clog.mediationLogTag, "MoPub - onInterstitialFailed called for MoPubInterstitial with ErrorCode: " + errorCode);
        handleMPErrorCode(errorCode);
    }

    @Override
    public void onInterstitialShown(MoPubInterstitial interstitial) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdExpanded();
        }
    }

    public void handleMPErrorCode(MoPubErrorCode mpError) {
        ResultCode code = ResultCode.INTERNAL_ERROR;

        switch (mpError) {
            case INTERNAL_ERROR:
                code = ResultCode.INTERNAL_ERROR;
                break;
            case NO_FILL:
                code = ResultCode.UNABLE_TO_FILL;
                break;
            case SERVER_ERROR:
                code = ResultCode.NETWORK_ERROR;
                break;
            case CANCELLED:
                code = ResultCode.INTERNAL_ERROR;
                break;
            case ADAPTER_NOT_FOUND:
                code = ResultCode.MEDIATED_SDK_UNAVAILABLE;
                break;
            case ADAPTER_CONFIGURATION_ERROR:
                code = ResultCode.MEDIATED_SDK_UNAVAILABLE;
                break;
            case NETWORK_TIMEOUT:
                code = ResultCode.NETWORK_ERROR;
                break;
            case NETWORK_NO_FILL:
                code = ResultCode.UNABLE_TO_FILL;
                break;
            case NETWORK_INVALID_STATE:
                code = ResultCode.NETWORK_ERROR;
                break;
            case MRAID_LOAD_ERROR:
                code = ResultCode.INTERNAL_ERROR;
                break;
            case VIDEO_CACHE_ERROR:
                code = ResultCode.INTERNAL_ERROR;
                break;
            case VIDEO_DOWNLOAD_ERROR:
                code = ResultCode.NETWORK_ERROR;
                break;
            case UNSPECIFIED:
                code = ResultCode.INTERNAL_ERROR;
                break;
        }

        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdFailed(code);
        }
    }

    public static String keywordsFromTargetingParameters(TargetingParameters targetingParameters) {
        StringBuilder keywords = new StringBuilder();

        switch(targetingParameters.getGender()){
            case FEMALE:
                keywords.append("m_gender:M");
                keywords.append(",");
                break;
            case MALE:
                keywords.append("m_gender:F");
                keywords.append(",");
                break;
            default:
                break;
        }

        if (!StringUtil.isEmpty(targetingParameters.getAge())) {
            keywords.append("m_age:" + targetingParameters.getAge() + ",");
        }

        for (Pair<String, String> p : targetingParameters.getCustomKeywords()) {
            keywords.append(p.first + ":" + p.second + ",");
        }

        return keywords.toString();
    }
}