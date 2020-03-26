/*
 *    Copyright 2018 APPNEXUS INC
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.appnexus.opensdk;

import android.content.Intent;

import com.appnexus.opensdk.shadows.ShadowAsyncTaskNoExecutor;
import com.appnexus.opensdk.shadows.ShadowCustomWebView;
import com.appnexus.opensdk.shadows.ShadowSettings;
import com.appnexus.opensdk.shadows.ShadowWebSettings;
import com.squareup.okhttp.mockwebserver.MockResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowLooper;

import static android.os.Looper.getMainLooper;
import static com.appnexus.opensdk.AdActivity.ACTIVITY_TYPE_INTERSTITIAL;
import static com.appnexus.opensdk.AdActivity.INTENT_KEY_ACTIVITY_TYPE;
import static com.appnexus.opensdk.InterstitialAdView.INTENT_KEY_AUTODISMISS_DELAY;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

@Config(sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class,
                ShadowCustomWebView.class, ShadowSettings.class, ShadowLog.class})
@RunWith(RobolectricTestRunner.class)
public class InterstitialAdViewLoadAdTest extends BaseViewAdTest {

    @Override
    public void setup() {
        super.setup();
    }

    @Override
    public void tearDown() {
        super.tearDown();
    }

    @Test
    public void testANInterstitialWithoutAutoDismissAdDelay() {

        setInterstitialShowonLoad(true);
        setAutoDismissDelay(false);
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner())); // First queue a regular HTML banner response
        assertTrue(interstitialAdView.getAdType() == AdType.UNKNOWN); // First tests if ad_type is UNKNOWN initially
        executeInterstitialRequest();

        //Checking if onAdLoaded is called or not
        assertTrue(adLoaded);


        //Creating shadow of the required activity
        ShadowActivity shadowActivity = shadowOf(activity);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        //Checking if an AdActivity is opened or not
        assertEquals(AdActivity.class.getCanonicalName(), startedIntent.getComponent().getClassName());

        //Checking if an INTENT_KEY_ACTIVITY_TYPE is equivalent to INTERSTITIAL or not
        assertEquals(startedIntent.getStringExtra(INTENT_KEY_ACTIVITY_TYPE), ACTIVITY_TYPE_INTERSTITIAL);

        //Checking if an INTENT_KEY_AUTODISMISS_DELAY is equual to 5 or not
        assertEquals(startedIntent.getIntExtra(INTENT_KEY_AUTODISMISS_DELAY, 0), -1);
    }

    @Test
    public void testANInterstitialWithAutoDismissAdDelay() throws InterruptedException {

        setInterstitialShowonLoad(true);
        setAutoDismissDelay(true);
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.banner())); // First queue a regular HTML banner response
        assertTrue(interstitialAdView.getAdType() == AdType.UNKNOWN); // First 2tests if ad_type is UNKNOW initially
        executeInterstitialRequest();

        //Checking if onAdLoaded is called or not
        assertTrue(adLoaded);

        //Creating shadow of the required activity
        ShadowActivity shadowActivity = shadowOf(activity);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        //Checking if an AdActivity is opened or not
        assertEquals(AdActivity.class.getCanonicalName(), startedIntent.getComponent().getClassName());

        //Checking if an INTENT_KEY_ACTIVITY_TYPE is equivalent to INTERSTITIAL or not
        assertEquals(startedIntent.getStringExtra(INTENT_KEY_ACTIVITY_TYPE), ACTIVITY_TYPE_INTERSTITIAL);

        //Checking if an INTENT_KEY_AUTODISMISS_DELAY is equual to 5 or not
        assertEquals(startedIntent.getIntExtra(INTENT_KEY_AUTODISMISS_DELAY, 0), 5);
    }

    private void executeInterstitialRequest() {
        interstitialAdView.loadAd();

        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

//        ShadowLooper shadowLooper = shadowOf(getMainLooper());
//        if (!shadowLooper.isIdle()) {
//            shadowLooper.idle();
//        }
//        RuntimeEnvironment.getMasterScheduler().advanceToNextPostedRunnable();
    }
}
