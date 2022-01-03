package com.appnexus.opensdk.suite;


import com.appnexus.opensdk.MediatedBannerAdViewControllerTest;
import com.appnexus.opensdk.MediatedInterstitialAdViewControllerTest;
import com.appnexus.opensdk.MediatedNativeAdViewControllerTest;
import com.appnexus.opensdk.MediatedSSMAdViewControllerTest;
import com.appnexus.opensdk.MediationCallbacksTest;
import com.appnexus.opensdk.MediationTimeoutTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        MediatedBannerAdViewControllerTest.class,
        MediatedInterstitialAdViewControllerTest.class,
        MediatedNativeAdViewControllerTest.class,
        MediatedSSMAdViewControllerTest.class,
        MediationTimeoutTest.class,
        MediationCallbacksTest.class,
})
public class MediationTestSuite {
}
