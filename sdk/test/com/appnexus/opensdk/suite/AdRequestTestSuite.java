package com.appnexus.opensdk.suite;



import com.appnexus.opensdk.AdActivityTest;
import com.appnexus.opensdk.AdFetcherTest;
import com.appnexus.opensdk.AdListenerTest;
import com.appnexus.opensdk.AdRequestToAdRequesterTest;
import com.appnexus.opensdk.AdViewFriendlyObstructionTests;
import com.appnexus.opensdk.AdViewRequestManagerTest;
import com.appnexus.opensdk.UTAdRequestTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        AdActivityTest.class,
        AdFetcherTest.class,
        AdListenerTest.class,
        AdRequestToAdRequesterTest.class,
        AdViewRequestManagerTest.class,
        AdViewFriendlyObstructionTests.class,
        UTAdRequestTest.class,
})
public class AdRequestTestSuite {
}
