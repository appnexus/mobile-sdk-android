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

package com.appnexus.opensdk.testviews;

import android.app.Activity;
import android.os.Handler;
import android.view.View;

import com.appnexus.opensdk.MediatedBannerAdView;
import com.appnexus.opensdk.MediatedBannerAdViewController;
import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.util.Lock;
import com.appnexus.opensdk.utils.Settings;

import static com.appnexus.opensdk.ResultCode.UNABLE_TO_FILL;

public class CallbacksTestView implements MediatedBannerAdView {

    MediatedBannerAdViewController controller;

    @Override
    public View requestAd(MediatedBannerAdViewController mBC, Activity activity, String parameter, String uid, int width, int height, TargetingParameters tp) {
        int testNumber = Integer.parseInt(uid);
        long defaultWaitTime = 1000;
        controller = mBC;

        // For delayed Runnables
        Handler handler = new Handler();

        switch (testNumber) {
            // load multiple
            case 18:
                mBC.onAdLoaded();
                mBC.onAdLoaded();
                Lock.unpause();
                break;
            // timeout then call
            case 19:
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        controller.onAdLoaded();
                        Lock.unpause();
                    }
                }, Settings.MEDIATED_NETWORK_TIMEOUT + defaultWaitTime + 1000);
                break;
            // load then fail
            case 20:
                mBC.onAdLoaded();
                mBC.onAdFailed(UNABLE_TO_FILL);
                Lock.unpause();
                break;
            // fail then load
            case 21:
                mBC.onAdFailed(UNABLE_TO_FILL);
                mBC.onAdLoaded();
                Lock.unpause();
                break;
            // load then extras
            case 22:
                mBC.onAdLoaded();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        controller.onAdClicked();
                        controller.onAdExpanded();
                        controller.onAdCollapsed();
                        Lock.unpause();
                    }
                }, Settings.MEDIATED_NETWORK_TIMEOUT + defaultWaitTime);
                break;
            // fail then extra
            case 23:
                mBC.onAdFailed(UNABLE_TO_FILL);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        controller.onAdClicked();
                        controller.onAdExpanded();
                        controller.onAdCollapsed();
                        Lock.unpause();
                    }
                }, Settings.MEDIATED_NETWORK_TIMEOUT + defaultWaitTime);
                break;
            // fail multiple
            case 24:
                mBC.onAdFailed(UNABLE_TO_FILL);
                mBC.onAdFailed(UNABLE_TO_FILL);
                Lock.unpause();
                break;
            // load, then call extras after delay
            case 25:
                break;
            default:
                break;
        }
        return DummyView.getDummyView(activity);
    }

    @Override
    public void destroy() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {

    }
}
