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
import android.view.View;

import com.appnexus.opensdk.MediatedBannerAdView;
import com.appnexus.opensdk.MediatedBannerAdViewController;
import com.appnexus.opensdk.TargetingParameters;

public class OOMBannerView implements MediatedBannerAdView {
    @Override
    public View requestAd(MediatedBannerAdViewController mBC, Activity activity, String parameter, String uid, int width, int height, TargetingParameters tp) {
        throw new OutOfMemoryError("Out of memory!");
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
