package com.appnexus.opensdk.testviews;

import android.content.Context;

import com.appnexus.opensdk.CSRAd;
import com.appnexus.opensdk.CSRController;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.util.Lock;

public class CSRNativeNoFill implements CSRAd {
    @Override
    public void requestAd(Context context, String payload, CSRController mBC, TargetingParameters tp) {
        Lock.explicitSleep(2);
        if (mBC != null) {
            mBC.onAdFailed(ResultCode.getNewInstance(ResultCode.UNABLE_TO_FILL));
        }
    }
}
