package com.appnexus.opensdk.suite;


import com.appnexus.opensdk.ANVideoPlayerDefaultSettingsTest;
import com.appnexus.opensdk.ANVideoPlayerSettingsTest;
import com.appnexus.opensdk.ApplicationTest;
import com.appnexus.opensdk.BaseNativeTest;
import com.appnexus.opensdk.BaseViewAdTest;
import com.appnexus.opensdk.ClogListenerTest;
import com.appnexus.opensdk.DefaultSettingsTest;
import com.appnexus.opensdk.ExecutorForBackgroundTasksTests;
import com.appnexus.opensdk.HashingFunctionsTest;
import com.appnexus.opensdk.SDKSettingsTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        BaseNativeTest.class,
        BaseViewAdTest.class,
        DefaultSettingsTest.class,
        ClogListenerTest.class,
        ExecutorForBackgroundTasksTests.class,
        HashingFunctionsTest.class,
        ANVideoPlayerDefaultSettingsTest.class,
        ANVideoPlayerSettingsTest.class,
        ApplicationTest.class,
        SDKSettingsTest.class,
})
public class DefaultSettingsTestSuite {
}
