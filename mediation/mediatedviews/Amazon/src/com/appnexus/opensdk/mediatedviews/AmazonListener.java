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

import com.amazon.device.ads.Ad;
import com.amazon.device.ads.AdError;
import com.amazon.device.ads.AdListener;
import com.amazon.device.ads.AdProperties;
import com.appnexus.opensdk.MediatedAdViewController;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.utils.Clog;

class AmazonListener implements AdListener {
    final MediatedAdViewController mediatedAdViewController;
    final String className;

    public AmazonListener(MediatedAdViewController mvc, String className) {
        this.mediatedAdViewController = mvc;
        this.className = className;
    }


    @Override
    public void onAdDismissed(Ad ad) {
        printToClog("onDismissed");
        if (mediatedAdViewController != null) {
            // We do not differentiate between a dismissed ad and a collapsed expanded ad
            mediatedAdViewController.onAdCollapsed();
        }
    }
    @Override
    public void onAdLoaded(Ad adLayout, AdProperties adProperties) {
        printToClog("onLoaded");
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdLoaded();
        }
    }

    @Override
    public void onAdExpanded(Ad adLayout) {
        printToClog("onExpanded");
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdExpanded();
        }

    }

    @Override
    public void onAdCollapsed(Ad adLayout) {
        printToClog("onCollapsed");
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdCollapsed();
        }
    }

    @Override
    public void onAdFailedToLoad(Ad adLayout, AdError adError) {
        printToClogError(" onAdFailedToLoad: " + adError.getMessage());
        ResultCode code = ResultCode.INTERNAL_ERROR;

        if (adError != null) {
            switch (adError.getCode()) {
                case INTERNAL_ERROR:
                    code = ResultCode.INTERNAL_ERROR;
                    break;
                case NETWORK_ERROR:
                    code = ResultCode.NETWORK_ERROR;
                    break;
                case NO_FILL:
                    code = ResultCode.UNABLE_TO_FILL;
                    break;
                case REQUEST_ERROR:
                    code = ResultCode.INVALID_REQUEST;
                    break;
                default:
                    break;
            }
        }

        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdFailed(code);
        }
    }

    void printToClog(String s) {
        Clog.d(Clog.mediationLogTag, className + " - " + s);
    }

    void printToClogError(String s) {
        Clog.e(Clog.mediationLogTag, className + " - " + s);
    }
}
