package com.appnexus.opensdk.suite;


import com.appnexus.opensdk.ANAdResponseInfoBannerTests;
import com.appnexus.opensdk.AdListenerTest;
import com.appnexus.opensdk.BannerAdViewVideoLoadAdTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
        ANAdResponseInfoBannerTests.class,
        AdListenerTest.class,
        BannerAdViewVideoLoadAdTest.class,
})
public class Miscellaneous {
}
