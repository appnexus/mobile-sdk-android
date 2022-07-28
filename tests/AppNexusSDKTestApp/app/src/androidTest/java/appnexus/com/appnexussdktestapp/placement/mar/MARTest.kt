/*
 *    Copyright 2019 APPNEXUS INC
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

package appnexus.com.appnexussdktestapp.placement.mar

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import appnexus.com.appnexussdktestapp.MARActivity
import com.appnexus.opensdk.XandrAd
import org.hamcrest.Matchers.anything
import org.junit.*
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class MARTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MARActivity::class.java, false, false)

    internal lateinit var myActivity: MARActivity


    @Before
    fun setup() {
        XandrAd.init(123, null, false, null)
        IdlingPolicies.setMasterPolicyTimeout(1, TimeUnit.MINUTES)
        IdlingPolicies.setIdlingResourceTimeout(1, TimeUnit.MINUTES)
        val intent = Intent()
        mActivityTestRule.launchActivity(intent)
        myActivity = mActivityTestRule.getActivity() as MARActivity
        IdlingRegistry.getInstance().register(myActivity.idlingResource)
    }

    @After
    fun destroy() {
        IdlingRegistry.getInstance().unregister(myActivity.idlingResource)
    }

    @Test
    fun testMARWeakReference() {
        Assert.assertFalse(myActivity.multiAdRequestCompleted)
        myActivity.load()
        //        Assert.assertTrue(myActivity.multiAdRequestCompleted);
        myActivity.adRequest = null
        myActivity.load()
        onView(isRoot()).check(matches(anything()))
        Assert.assertTrue(
            "MultiAdRequest is still in progress, Idling Resource isn't working properly",
            myActivity.multiAdRequestCompleted
        )
    }

    @Test
    fun testConcurrentMARLoad() {
        Assert.assertFalse(myActivity.multiAdRequestCompleted)
        myActivity.load()
        //        Assert.assertTrue(myActivity.multiAdRequestCompleted);
        myActivity.adRequest = null
        myActivity.load()
        myActivity.load2()
        onView(isRoot()).check(matches(anything()))
        Assert.assertTrue(
            "MultiAdRequest is still in progress, Idling Resource isn't working properly",
            myActivity.multiAdRequestCompleted
        )
        Assert.assertTrue(
            "MultiAdRequest2 is still in progress, Idling Resource isn't working properly",
            myActivity.multiAdRequestCompleted2
        )
    }

    @Test
    fun testMARDeallocatedAdUnit() {
        Assert.assertFalse(myActivity.multiAdRequestCompleted)
        myActivity.load()
        //        Assert.assertTrue(myActivity.multiAdRequestCompleted);
        myActivity.adRequest = null
        myActivity.load()
        onView(isRoot()).check(matches(anything()))
        val adUnitList = myActivity.anMultiAdRequest!!.getAdUnitList()
        for (adWeakReference in adUnitList) {
            val ad = adWeakReference.get()
            if (ad != null) {
                Assert.assertFalse(adWeakReference.get() === myActivity.adRequest)
            }
        }
        Assert.assertTrue(
            "MultiAdRequest is still in progress, Idling Resource isn't working properly",
            myActivity.multiAdRequestCompleted
        )
    }

    @Test
    fun testMARDeallocateAdUnitWhileTheLoadIsActive() {
        Assert.assertFalse(myActivity.multiAdRequestCompleted)
        myActivity.load()
        //        Assert.assertTrue(myActivity.multiAdRequestCompleted);
        myActivity.adRequest = null
        onView(isRoot()).check(matches(anything()))
        val adUnitList = myActivity.anMultiAdRequest!!.getAdUnitList()
        for (adWeakReference in adUnitList) {
            val ad = adWeakReference.get()
            if (ad != null) {
                Assert.assertFalse(adWeakReference.get() === myActivity.adRequest)
            }
        }
        Assert.assertTrue(
            "MultiAdRequest is still in progress, Idling Resource isn't working properly",
            myActivity.multiAdRequestCompleted
        )
    }


    // BG Testing

    @Test
    fun testMARWeakReferenceBG() {
        Assert.assertFalse(myActivity.multiAdRequestCompleted)
        myActivity.load(bgTask = true)
        //        Assert.assertTrue(myActivity.multiAdRequestCompleted);
        myActivity.adRequest = null
        myActivity.load(bgTask = true)
        onView(isRoot()).check(matches(anything()))
        Assert.assertTrue(
            "MultiAdRequest is still in progress, Idling Resource isn't working properly",
            myActivity.multiAdRequestCompleted
        )
    }

    @Test
    fun testConcurrentMARLoadBG() {
        Assert.assertFalse(myActivity.multiAdRequestCompleted)
        myActivity.load(bgTask = true)
        //        Assert.assertTrue(myActivity.multiAdRequestCompleted);
        myActivity.adRequest = null
        myActivity.load(bgTask = true)
        myActivity.load2(bgTask = true)
        onView(isRoot()).check(matches(anything()))
        Assert.assertTrue(
            "MultiAdRequest is still in progress, Idling Resource isn't working properly",
            myActivity.multiAdRequestCompleted
        )
        Assert.assertTrue(
            "MultiAdRequest2 is still in progress, Idling Resource isn't working properly",
            myActivity.multiAdRequestCompleted2
        )
    }

    @Test
    fun testMARDeallocatedAdUnitBG() {
        Assert.assertFalse(myActivity.multiAdRequestCompleted)
        myActivity.load(bgTask = true)
        //        Assert.assertTrue(myActivity.multiAdRequestCompleted);
        myActivity.adRequest = null
        myActivity.load(bgTask = true)
        onView(isRoot()).check(matches(anything()))
        val adUnitList = myActivity.anMultiAdRequest!!.getAdUnitList()
        for (adWeakReference in adUnitList) {
            val ad = adWeakReference.get()
            if (ad != null) {
                Assert.assertFalse(adWeakReference.get() === myActivity.adRequest)
            }
        }
        Assert.assertTrue(
            "MultiAdRequest is still in progress, Idling Resource isn't working properly",
            myActivity.multiAdRequestCompleted
        )
    }

    @Test
    fun testMARDeallocateAdUnitWhileTheLoadIsActiveBG() {
        Assert.assertFalse(myActivity.multiAdRequestCompleted)
        myActivity.load(bgTask = true)
        //        Assert.assertTrue(myActivity.multiAdRequestCompleted);
        myActivity.adRequest = null
        onView(isRoot()).check(matches(anything()))
        val adUnitList = myActivity.anMultiAdRequest!!.getAdUnitList()
        for (adWeakReference in adUnitList) {
            val ad = adWeakReference.get()
            if (ad != null) {
                Assert.assertFalse(adWeakReference.get() === myActivity.adRequest)
            }
        }
        Assert.assertTrue(
            "MultiAdRequest is still in progress, Idling Resource isn't working properly",
            myActivity.multiAdRequestCompleted
        )
    }

}