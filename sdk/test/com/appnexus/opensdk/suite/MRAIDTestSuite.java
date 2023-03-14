package com.appnexus.opensdk.suite;


import com.appnexus.opensdk.MRAIDImplementationTest;
import com.appnexus.opensdk.MRAIDTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        MRAIDTest.class,
        MRAIDImplementationTest.class,
})
public class MRAIDTestSuite {
}
