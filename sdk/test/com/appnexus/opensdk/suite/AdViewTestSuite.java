package com.appnexus.opensdk.suite;


import com.appnexus.opensdk.BannerAdToRequestParametersTest;
import com.appnexus.opensdk.BannerAdViewLoadAdTests;
import com.appnexus.opensdk.BannerAdViewTest;
import com.appnexus.opensdk.BannerImpressionTests;
import com.appnexus.opensdk.InterstitialAdToRequestParametersTest;
import com.appnexus.opensdk.InterstitialAdViewLoadAdTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        BannerAdViewLoadAdTests.class,
        BannerAdToRequestParametersTest.class,
        BannerAdViewTest.class,
        BannerImpressionTests.class,
        InterstitialAdToRequestParametersTest.class,
        InterstitialAdViewLoadAdTest.class,
})
public class AdViewTestSuite {
}
