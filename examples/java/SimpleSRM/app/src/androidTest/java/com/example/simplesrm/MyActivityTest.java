package com.example.simplesrm;

import android.content.Intent;

import androidx.test.espresso.IdlingPolicies;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.appnexus.opensdk.Ad;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static org.hamcrest.Matchers.anything;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4.class)
public class MyActivityTest {

    @Rule
    public ActivityTestRule mActivityTestRule = new ActivityTestRule(MyActivity.class, false, false);

    MyActivity myActivity;


    @Before
    public void setup() {
        IdlingPolicies.setMasterPolicyTimeout(1, TimeUnit.MINUTES);
        IdlingPolicies.setIdlingResourceTimeout(1, TimeUnit.MINUTES);
        Intent intent = new Intent();
        mActivityTestRule.launchActivity(intent);
        myActivity = (MyActivity) mActivityTestRule.getActivity();
        IdlingRegistry.getInstance().register(myActivity.idlingResource);
    }

    @After
    public void destroy() {
        IdlingRegistry.getInstance().unregister(myActivity.idlingResource);
    }

    @Test
    public void testMARWeakReference() {
        Assert.assertFalse(myActivity.multiAdRequestCompleted);
        myActivity.load();
//        Assert.assertTrue(myActivity.multiAdRequestCompleted);
        myActivity.adRequest = null;
        myActivity.load();
        onView(isRoot()).check(matches(anything()));
        Assert.assertTrue("MultiAdRequest is still in progress, Idling Resource isn't working properly", myActivity.multiAdRequestCompleted);
    }

    @Test
    public void testConcurrentMARLoad() {
        Assert.assertFalse(myActivity.multiAdRequestCompleted);
        myActivity.load();
//        Assert.assertTrue(myActivity.multiAdRequestCompleted);
        myActivity.adRequest = null;
        myActivity.load();
        myActivity.load2();
        onView(isRoot()).check(matches(anything()));
        Assert.assertTrue("MultiAdRequest is still in progress, Idling Resource isn't working properly", myActivity.multiAdRequestCompleted);
        Assert.assertTrue("MultiAdRequest2 is still in progress, Idling Resource isn't working properly", myActivity.multiAdRequestCompleted2);
    }

    @Test
    public void testMARDeallocatedAdUnit() {
        Assert.assertFalse(myActivity.multiAdRequestCompleted);
        myActivity.load();
//        Assert.assertTrue(myActivity.multiAdRequestCompleted);
        myActivity.adRequest = null;
        myActivity.load();
        onView(isRoot()).check(matches(anything()));
        ArrayList<WeakReference<Ad>> adUnitList = myActivity.anMultiAdRequest.getAdUnitList();
        for (WeakReference<Ad> adWeakReference : adUnitList) {
            Ad ad = adWeakReference.get();
            if (ad != null) {
                Assert.assertFalse(adWeakReference.get() == myActivity.adRequest);
            }
        }
        Assert.assertTrue("MultiAdRequest is still in progress, Idling Resource isn't working properly", myActivity.multiAdRequestCompleted);
    }

    @Test
    public void testMARDeallocateAdUnitWhileTheLoadIsActive() {
        Assert.assertFalse(myActivity.multiAdRequestCompleted);
        myActivity.load();
//        Assert.assertTrue(myActivity.multiAdRequestCompleted);
        myActivity.adRequest = null;
        onView(isRoot()).check(matches(anything()));
        ArrayList<WeakReference<Ad>> adUnitList = myActivity.anMultiAdRequest.getAdUnitList();
        for (WeakReference<Ad> adWeakReference : adUnitList) {
            Ad ad = adWeakReference.get();
            if (ad != null) {
                Assert.assertFalse(adWeakReference.get() == myActivity.adRequest);
            }
        }
        Assert.assertTrue("MultiAdRequest is still in progress, Idling Resource isn't working properly", myActivity.multiAdRequestCompleted);
    }

}