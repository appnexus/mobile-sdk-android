package com.appnexus.opensdk.suite;


import com.appnexus.opensdk.NativeAdSDKTest;
import com.appnexus.opensdk.NativeAdToRequestParametersTest;
import com.appnexus.opensdk.NativeFriendlyObstructionTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)

@Suite.SuiteClasses({
        NativeAdSDKTest.class,
        NativeFriendlyObstructionTests.class,
        NativeAdToRequestParametersTest.class,
})
public class NativeAdSDKTestSuite {
}
