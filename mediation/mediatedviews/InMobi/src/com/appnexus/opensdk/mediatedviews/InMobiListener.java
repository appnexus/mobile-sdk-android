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

import com.appnexus.opensdk.MediatedAdViewController;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.utils.Clog;
import com.inmobi.monetization.IMBanner;
import com.inmobi.monetization.IMBannerListener;
import com.inmobi.monetization.IMErrorCode;
import com.inmobi.monetization.IMInterstitial;
import com.inmobi.monetization.IMInterstitialListener;

import java.util.Map;

public class InMobiListener implements IMBannerListener, IMInterstitialListener {

    private final MediatedAdViewController mediatedAdViewController;
    private final String className;
    private boolean listenerWasCalled = false;

    public InMobiListener(MediatedAdViewController mediatedAdViewController, String className) {
        this.mediatedAdViewController = mediatedAdViewController;
        this.className = className;
    }

    private ResultCode errorCodeMapping(IMErrorCode imErrorCode) {
        ResultCode code = ResultCode.INTERNAL_ERROR;

        switch (imErrorCode) {

            case INVALID_REQUEST:
                code = ResultCode.INVALID_REQUEST;
                break;
            case INTERNAL_ERROR:
                break;
            case NO_FILL:
                code = ResultCode.UNABLE_TO_FILL;
                break;
            case DO_MONETIZE:
                break;
            case DO_NOTHING:
                break;
            case NETWORK_ERROR:
                code = ResultCode.NETWORK_ERROR;
                break;
        }

        return  code;
    }

    @Override
    public void onBannerRequestFailed(IMBanner imBanner, IMErrorCode imErrorCode) {
        if (!listenerWasCalled) {
            Clog.d(Clog.mediationLogTag,
                    className + " | InMobi - onBannerRequestFailed called with error message: " + imErrorCode.toString());

            if (mediatedAdViewController != null) {
                mediatedAdViewController.onAdFailed(errorCodeMapping(imErrorCode));
            }
            listenerWasCalled = true;
        } else {
            Clog.d(Clog.mediationLogTag, "InMobi listener getting called multiple times");
        }


    }

    @Override
    public void onBannerRequestSucceeded(IMBanner imBanner) {
        if (!listenerWasCalled) {
            if (mediatedAdViewController != null) {
                mediatedAdViewController.onAdLoaded();
            }
            listenerWasCalled = true;
        } else {
            Clog.d(Clog.mediationLogTag, "InMobi listener getting called multiple times");
        }

    }

    @Override
    public void onBannerInteraction(IMBanner imBanner, Map<String, String> stringStringMap) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdClicked();
        }
    }

    @Override
    public void onShowBannerScreen(IMBanner imBanner) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdExpanded();
        }
    }

    @Override
    public void onDismissBannerScreen(IMBanner imBanner) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdCollapsed();
        }
    }

    @Override
    public void onLeaveApplication(IMBanner imBanner) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdExpanded();
        }
    }

    @Override
    public void onInterstitialFailed(IMInterstitial imInterstitial, IMErrorCode imErrorCode) {
        Clog.d(Clog.mediationLogTag, className + " | InMobi - failed to load interstitial because: " + imErrorCode.toString());

        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdFailed(errorCodeMapping(imErrorCode));
        }
    }

    @Override
    public void onInterstitialLoaded(IMInterstitial imInterstitial) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdLoaded();
        }
    }

    @Override
    public void onShowInterstitialScreen(IMInterstitial imInterstitial) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdExpanded();
        }

    }

    @Override
    public void onDismissInterstitialScreen(IMInterstitial imInterstitial) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdCollapsed();
        }

    }

    @Override
    public void onInterstitialInteraction(IMInterstitial imInterstitial, Map<String, String> stringStringMap) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdClicked();
        }
    }

    @Override
    public void onLeaveApplication(IMInterstitial imInterstitial) {
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdExpanded();
        }
    }
}
