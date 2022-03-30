package com.appnexus.opensdk.suite;


import com.appnexus.opensdk.ANMultiAdRequestApiTest;
import com.appnexus.opensdk.ANMultiAdRequestLoadTests;
import com.appnexus.opensdk.ANMultiAdRequestToRequestParametersTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ANMultiAdRequestApiTest.class,
        ANMultiAdRequestToRequestParametersTest.class,
        ANMultiAdRequestLoadTests.class,

})
public class MARTestSuite {
}
