package com.appnexus.opensdk;

import android.app.Activity;

import com.appnexus.opensdk.util.MockMainActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertTrue;

@Config(sdk = 21)
@RunWith(RobolectricTestRunner.class)
public class ApplicationTest {

    @Before
    public void setup(){
        SDKSettings.setExternalExecutor(null);
    }

    @Test
    public void testActivityNonNull() throws Exception {
        Activity activity = Robolectric.buildActivity(MockMainActivity.class).create().get();
        assertTrue(activity != null);
    }
}