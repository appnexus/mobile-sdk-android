package com.appnexus.opensdk.testviews;

import android.content.Context;

import com.appnexus.opensdk.CSRAd;
import com.appnexus.opensdk.CSRController;
import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.mocks.MockFBNativeBannerAdResponse;
import com.appnexus.opensdk.util.Lock;

public class CSRNativeSuccessful implements CSRAd {

    private CSRController mBC;

    @Override
    public void requestAd(Context context, String payload, CSRController mBC, TargetingParameters tp) {
        Lock.explicitSleep(2);
        this.mBC = mBC;
        if (mBC != null) {
            mBC.onAdLoaded(new MockFBNativeBannerAdResponse(mBC));
        }
    }


}
