package com.appnexus.opensdkdemo;

import android.test.ActivityInstrumentationTestCase2;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.appnexus.opensdkdemo.DemoMainActivityTest \
 * com.appnexus.opensdkdemo.tests/android.test.InstrumentationTestRunner
 */
public class DemoMainActivityTest extends ActivityInstrumentationTestCase2<DemoMainActivity> {

    public DemoMainActivityTest() {
        super("com.appnexus.opensdkdemo", DemoMainActivity.class);
    }

}
