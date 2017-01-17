/*
 *    Copyright 2017 APPNEXUS INC
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
import com.smartadserver.android.library.exception.SASAdTimeoutException;
import com.smartadserver.android.library.exception.SASNoAdToDeliverException;
import com.smartadserver.android.library.model.SASAdElement;
import com.smartadserver.android.library.ui.SASAdView;

public class SmartAdServerListener implements SASAdView.AdResponseHandler {

    private final MediatedAdViewController mediatedAdViewController;
    private final String className;

    // For SMART there is no seperation of Load and Show so no API for detecting if AdReady this boolean is for the same.
    private boolean isAdLoaded = false;


    public SmartAdServerListener(MediatedAdViewController mediatedAdViewController, String className) {
        this.mediatedAdViewController = mediatedAdViewController;
        this.className = className;
    }

    @Override
    public void adLoadingCompleted(SASAdElement sasAdElement) {
        Clog.i(Clog.mediationLogTag, "SmartAdServer: Ad loading completed");
        if (mediatedAdViewController != null) {
            setAdLoaded(true);
            mediatedAdViewController.onAdLoaded();
        }
    }

    @Override
    public void adLoadingFailed(Exception e) {
        Clog.i(Clog.mediationLogTag, "SmartAdServer: Ad loading failed: " + e.getMessage());
        ResultCode code = ResultCode.INTERNAL_ERROR;
        if (e instanceof SASNoAdToDeliverException) {
            // no ad to deliver
            code = ResultCode.UNABLE_TO_FILL;
        } else if (e instanceof SASAdTimeoutException) {
            // ad request timeout translates to  network error
            code=ResultCode.NETWORK_ERROR;
        }
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdFailed(code);
        }
    }




    public void onClicked(){
        Clog.i(Clog.mediationLogTag, "SmartAdServer: Ad clicked");
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdClicked();
        }
    }

    public void onExpanded(){
        Clog.i(Clog.mediationLogTag, "SmartAdServer: Ad Expanded");
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdExpanded();
        }
    }


    public void onCollapsed(){
        Clog.i(Clog.mediationLogTag, "SmartAdServer: Ad Collapsed");
        if (mediatedAdViewController != null) {
            mediatedAdViewController.onAdCollapsed();
        }
    }


    public boolean isAdLoaded() {
        return isAdLoaded;
    }

    public void setAdLoaded(boolean adLoaded) {
        isAdLoaded = adLoaded;
    }


}
