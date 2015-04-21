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
import com.vdopia.ads.lw.LVDOAd;
import com.vdopia.ads.lw.LVDOAdListener;
import com.vdopia.ads.lw.LVDOAdRequest;

public class VdopiaListener implements LVDOAdListener {
    MediatedAdViewController mAV;
    String className;

    public VdopiaListener(MediatedAdViewController mAV, String className) {
        this.mAV = mAV;
        this.className = className;
    }

    @Override
    public void onReceiveAd(LVDOAd lvdoAd) {
        if (mAV != null) {
            mAV.onAdLoaded();
        }
    }

    @Override
    public void onFailedToReceiveAd(LVDOAd lvdoAd, LVDOAdRequest.LVDOErrorCode lvdoErrorCode) {
        if (mAV != null) {
            ResultCode errorCode = ResultCode.INTERNAL_ERROR;

            switch (lvdoErrorCode) {
                case INVALID_REQUEST:
                    errorCode = ResultCode.INVALID_REQUEST;
                    break;
                case NO_FILL:
                    errorCode = ResultCode.UNABLE_TO_FILL;
                    break;
                case NETWORK_ERROR:
                    errorCode = ResultCode.NETWORK_ERROR;
                    break;
                case INTERNAL_ERROR:
                    errorCode = ResultCode.INTERNAL_ERROR;
                    break;
            }

            mAV.onAdFailed(errorCode);
        }

    }

    @Override
    public void onPresentScreen(LVDOAd lvdoAd) {
        Clog.d(Clog.mediationLogTag, className + " present ad on the screen");
    }

    @Override
    public void onDismissScreen(LVDOAd lvdoAd) {
        if (mAV != null) {
            mAV.onAdCollapsed();
        }
    }

    @Override
    public void onLeaveApplication(LVDOAd lvdoAd) {
        Clog.d(Clog.mediationLogTag, className + " leaving application");
        if (mAV != null) {
            mAV.onAdClicked();
        }
    }


}
