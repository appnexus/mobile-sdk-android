package com.appnexus.opensdk.suite;



import com.appnexus.opensdk.ANAdResponseInfoBannerTests;
import com.appnexus.opensdk.ANAdResponseInfoBannerVideoTests;
import com.appnexus.opensdk.ANAdResponseInfoInterstitialTests;
import com.appnexus.opensdk.ANAdResponseInfoNativeTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ANAdResponseInfoBannerTests.class,
        ANAdResponseInfoBannerVideoTests.class,
        ANAdResponseInfoInterstitialTests.class,
        ANAdResponseInfoNativeTest.class,

})
public class AdResponseInfoTestSuite {
}
