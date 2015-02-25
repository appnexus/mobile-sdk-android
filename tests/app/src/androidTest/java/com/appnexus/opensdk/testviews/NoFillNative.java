package com.appnexus.opensdk.testviews;

import android.content.Context;

import com.appnexus.opensdk.MediatedNativeAd;
import com.appnexus.opensdk.MediatedNativeAdController;
import com.appnexus.opensdk.NativeAdResponse;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.TargetingParameters;

public class NoFillNative implements MediatedNativeAd{
    @Override
    public NativeAdResponse requestNativeAd(Context context, String uid, MediatedNativeAdController mBC, TargetingParameters tp) {
        mBC.onAdFailed(ResultCode.UNABLE_TO_FILL);
        return null;
    }
}
