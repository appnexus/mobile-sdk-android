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

public class ChartboostListener implements ChartboostDelegateBridge.ChartboostListener {
    String location;
    MediatedAdViewController mAV;

    public ChartboostListener(String location, MediatedAdViewController mAV) {
        this.location = location;
        this.mAV = mAV;
    }

    @Override
    public void didCacheInterstitial(String location) {
        if (this.location.equals(location)) {
            if (mAV != null) {
                mAV.onAdLoaded();
            }
        }
    }

    @Override
    public void didFailToLoadInterstitial(String location, ResultCode code) {
        if (this.location.equals(location)) {
            if (mAV != null) {
                mAV.onAdFailed(code);
            }
        }
    }

    @Override
    public void didDismissInterstitial(String location) {
        if (this.location.equals(location)) {
            if (mAV != null) {
                mAV.onAdCollapsed();
            }

        }
    }

    @Override
    public void didCloseInterstitial(String location) {
        if (this.location.equals(location)) {
            if (mAV != null) {
                mAV.onAdCollapsed();
            }
            // remove this listener from ChartboostDelegateBridge once the ad is closed
            ChartboostDelegateBridge.getInstance().remove(location, this);
        }
    }

    @Override
    public void didClickInterstitial(String location) {
        if (this.location.equals(location)) {
            if (mAV != null) {
                mAV.onAdClicked();
            }
        }
    }
}
