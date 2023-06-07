package com.appnexus.opensdk.suite;

import com.appnexus.opensdk.ANAdResponseInfoBannerTests;
import com.appnexus.opensdk.ANAdResponseInfoBannerVideoTests;
import com.appnexus.opensdk.ANAdResponseInfoInterstitialTests;
import com.appnexus.opensdk.ANAdResponseInfoNativeTest;
import com.appnexus.opensdk.AdListenerTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ANAdResponseInfoInterstitialTests.class,
        ANAdResponseInfoNativeTest.class,
        ANAdResponseInfoBannerTests.class,
        AdListenerTest.class,
})
public class AdResponseInfoTestSuite {
}
