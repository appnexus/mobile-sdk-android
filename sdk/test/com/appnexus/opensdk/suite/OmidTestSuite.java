package com.appnexus.opensdk.suite;


import com.appnexus.opensdk.ANOmidViewabiltyTests;
import com.appnexus.opensdk.viewability.ANOMIDNativeViewabilityTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
        ANOmidViewabiltyTests.class,
        ANOMIDNativeViewabilityTests.class
})
public class OmidTestSuite {
}
