package com.appnexus.opensdk.testviews;

import android.content.Context;

import com.appnexus.opensdk.MediatedNativeAd;
import com.appnexus.opensdk.MediatedNativeAdController;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.TargetingParameters;

public class NoFillNative implements MediatedNativeAd{
    @Override
    public void requestNativeAd(Context context, String uid, MediatedNativeAdController mBC, TargetingParameters tp) {
        if (mBC != null) {
            mBC.onAdFailed(ResultCode.UNABLE_TO_FILL);
        }

    }
}
