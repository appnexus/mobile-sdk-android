package com.appnexus.opensdk;

import android.app.Activity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertTrue;

@Config(constants = BuildConfig.class, sdk = 21)
@RunWith(RobolectricGradleTestRunner.class)
public class ApplicationTest {

    @Test
    public void testActivityNonNull() throws Exception {
        Activity activity = Robolectric.buildActivity(MockMainActivity.class).create().get();
        assertTrue(activity != null);
    }
}