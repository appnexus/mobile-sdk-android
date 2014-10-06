package com.appnexus.opensdk;

import android.app.Activity;

import com.appnexus.opensdk.OpenSDKUnitTestsActivity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertTrue;

@Config(manifest = "./src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class ApplicationTest{

    @Test
    public void testActivityNonNull() throws Exception{
        Activity activity = Robolectric.buildActivity(OpenSDKUnitTestsActivity.class).create().get();
        assertTrue(activity != null);
    }
}