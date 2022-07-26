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

package appnexus.com.appnexussdktestapp.functional.mar

import android.content.Intent
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import appnexus.com.appnexussdktestapp.MARLoadAndDisplayActivity
import appnexus.com.appnexussdktestapp.R
import com.appnexus.opensdk.XandrAd
import org.junit.*
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class MARScaleTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MARLoadAndDisplayActivity::class.java, false, false)

    internal lateinit var myActivity: MARLoadAndDisplayActivity
    internal var arrayListAdType = ArrayList<String>()
    internal var arrayListBidType = ArrayList<String>()


    @Before
    fun setup() {
        XandrAd.init(123, null, false, null)
        IdlingPolicies.setMasterPolicyTimeout(1, TimeUnit.MINUTES)
        IdlingPolicies.setIdlingResourceTimeout(1, TimeUnit.MINUTES)
        arrayListAdType.clear()
        arrayListBidType.clear()
    }

    @After
    fun destroy() {
        IdlingRegistry.getInstance().unregister(myActivity.idlingResource)
    }

    @Test
    fun testMARCombinationTwelveRTBBanner() {
        setupTwelveRTBBanner()
        val intent = Intent()
        intent.putExtra(MARLoadAndDisplayActivity.AD_TYPE, arrayListAdType)
        intent.putExtra(MARLoadAndDisplayActivity.BID_TYPE, arrayListBidType)
        mActivityTestRule.launchActivity(intent)
        myActivity = mActivityTestRule.getActivity() as MARLoadAndDisplayActivity
        IdlingRegistry.getInstance().register(myActivity.idlingResource)

        Espresso.onView(ViewMatchers.withId(R.id.recyclerListAdView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Assert.assertTrue(
            "MultiAdRequest is still in progress, Idling Resource isn't working properly",
            myActivity.multiAdRequestCompleted)
    }

    @Test
    fun testMARCombinationTwelveRTBInterstitial() {
        setupTwelveRTBInterstitial()
        val intent = Intent()
        intent.putExtra(MARLoadAndDisplayActivity.AD_TYPE, arrayListAdType)
        intent.putExtra(MARLoadAndDisplayActivity.BID_TYPE, arrayListBidType)
        mActivityTestRule.launchActivity(intent)
        myActivity = mActivityTestRule.getActivity() as MARLoadAndDisplayActivity
        IdlingRegistry.getInstance().register(myActivity.idlingResource)

        Espresso.onView(ViewMatchers.withId(R.id.recyclerListAdView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Assert.assertTrue(
            "MultiAdRequest is still in progress, Idling Resource isn't working properly",
            myActivity.multiAdRequestCompleted)
    }

    @Test
    fun testMARCombinationTwelveRTBNative() {
        setupTwelveRTBNative()
        val intent = Intent()
        intent.putExtra(MARLoadAndDisplayActivity.AD_TYPE, arrayListAdType)
        intent.putExtra(MARLoadAndDisplayActivity.BID_TYPE, arrayListBidType)
        mActivityTestRule.launchActivity(intent)
        myActivity = mActivityTestRule.getActivity() as MARLoadAndDisplayActivity
        IdlingRegistry.getInstance().register(myActivity.idlingResource)

        Espresso.onView(ViewMatchers.withId(R.id.recyclerListAdView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Assert.assertTrue(
            "MultiAdRequest is still in progress, Idling Resource isn't working properly",
            myActivity.multiAdRequestCompleted)
    }

    @Test
    fun testMARCombinationTwelveRTBVideo() {
        setupTwelveRTBVideo()
        val intent = Intent()
        intent.putExtra(MARLoadAndDisplayActivity.AD_TYPE, arrayListAdType)
        intent.putExtra(MARLoadAndDisplayActivity.BID_TYPE, arrayListBidType)
        mActivityTestRule.launchActivity(intent)
        myActivity = mActivityTestRule.getActivity() as MARLoadAndDisplayActivity
        IdlingRegistry.getInstance().register(myActivity.idlingResource)

        Espresso.onView(ViewMatchers.withId(R.id.recyclerListAdView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Assert.assertTrue(
            "MultiAdRequest is still in progress, Idling Resource isn't working properly",
            myActivity.multiAdRequestCompleted)
    }

    @Test
    fun testMARCombinationTwelveCSMBanner() {
        setupTwelveCSMBanner()
        val intent = Intent()
        intent.putExtra(MARLoadAndDisplayActivity.AD_TYPE, arrayListAdType)
        intent.putExtra(MARLoadAndDisplayActivity.BID_TYPE, arrayListBidType)
        mActivityTestRule.launchActivity(intent)
        myActivity = mActivityTestRule.getActivity() as MARLoadAndDisplayActivity
        IdlingRegistry.getInstance().register(myActivity.idlingResource)

        Espresso.onView(ViewMatchers.withId(R.id.recyclerListAdView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Assert.assertTrue(
            "MultiAdRequest is still in progress, Idling Resource isn't working properly",
            myActivity.multiAdRequestCompleted)
    }

    @Test
    fun testMARCombinationTwelveCSMInterstitial() {
        setupTwelveCSMInterstitial()
        val intent = Intent()
        intent.putExtra(MARLoadAndDisplayActivity.AD_TYPE, arrayListAdType)
        intent.putExtra(MARLoadAndDisplayActivity.BID_TYPE, arrayListBidType)
        mActivityTestRule.launchActivity(intent)
        myActivity = mActivityTestRule.getActivity() as MARLoadAndDisplayActivity
        IdlingRegistry.getInstance().register(myActivity.idlingResource)

        Espresso.onView(ViewMatchers.withId(R.id.recyclerListAdView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Assert.assertTrue(
            "MultiAdRequest is still in progress, Idling Resource isn't working properly",
            myActivity.multiAdRequestCompleted)
    }

    @Test
    fun testMARCombinationTwelveCSMNative() {
        setupTwelveCSMNative()
        val intent = Intent()
        intent.putExtra(MARLoadAndDisplayActivity.AD_TYPE, arrayListAdType)
        intent.putExtra(MARLoadAndDisplayActivity.BID_TYPE, arrayListBidType)
        mActivityTestRule.launchActivity(intent)
        myActivity = mActivityTestRule.getActivity() as MARLoadAndDisplayActivity
        IdlingRegistry.getInstance().register(myActivity.idlingResource)

        Espresso.onView(ViewMatchers.withId(R.id.recyclerListAdView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Assert.assertTrue(
            "MultiAdRequest is still in progress, Idling Resource isn't working properly",
            myActivity.multiAdRequestCompleted)
    }

    @Test
    fun testMARCombinationTwelveCSMVideo() {
        setupTwelveCSMVideo()
        val intent = Intent()
        intent.putExtra(MARLoadAndDisplayActivity.AD_TYPE, arrayListAdType)
        intent.putExtra(MARLoadAndDisplayActivity.BID_TYPE, arrayListBidType)
        mActivityTestRule.launchActivity(intent)
        myActivity = mActivityTestRule.getActivity() as MARLoadAndDisplayActivity
        IdlingRegistry.getInstance().register(myActivity.idlingResource)

        Espresso.onView(ViewMatchers.withId(R.id.recyclerListAdView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Assert.assertTrue(
            "MultiAdRequest is still in progress, Idling Resource isn't working properly",
            myActivity.multiAdRequestCompleted)
    }

    @Test
    fun testMARCombinationTwelveRTBCombination() {
        setupTwelveRTBCombination()
        val intent = Intent()
        intent.putExtra(MARLoadAndDisplayActivity.AD_TYPE, arrayListAdType)
        intent.putExtra(MARLoadAndDisplayActivity.BID_TYPE, arrayListBidType)
        mActivityTestRule.launchActivity(intent)
        myActivity = mActivityTestRule.getActivity() as MARLoadAndDisplayActivity
        IdlingRegistry.getInstance().register(myActivity.idlingResource)

        Espresso.onView(ViewMatchers.withId(R.id.recyclerListAdView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Assert.assertTrue(
            "MultiAdRequest is still in progress, Idling Resource isn't working properly",
            myActivity.multiAdRequestCompleted)
    }

    @Test
    fun testMARCombinationTwelveCSMCombination() {
        setupTwelveCSMCombination()
        val intent = Intent()
        intent.putExtra(MARLoadAndDisplayActivity.AD_TYPE, arrayListAdType)
        intent.putExtra(MARLoadAndDisplayActivity.BID_TYPE, arrayListBidType)
        mActivityTestRule.launchActivity(intent)
        myActivity = mActivityTestRule.getActivity() as MARLoadAndDisplayActivity
        IdlingRegistry.getInstance().register(myActivity.idlingResource)

        Espresso.onView(ViewMatchers.withId(R.id.recyclerListAdView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Assert.assertTrue(
            "MultiAdRequest is still in progress, Idling Resource isn't working properly",
            myActivity.multiAdRequestCompleted)
    }

//    @Test
//    fun testMARCombinationMaxRTBBanner() {
//        var count = 12
//        setupTwelveRTBBanner()
//        do {
//            if (::myActivity.isInitialized) {
//                myActivity.finish()
//            }
//            setupTwelveRTBBanner()
//            val intent = Intent()
//            intent.putExtra(MARLoadAndDisplayActivity.AD_TYPE, arrayListAdType)
//            intent.putExtra(MARLoadAndDisplayActivity.BID_TYPE, arrayListBidType)
//            intent.putExtra(MARLoadAndDisplayActivity.DISPLAY_AD, false);
//            mActivityTestRule.launchActivity(intent)
//            myActivity = mActivityTestRule.getActivity() as MARLoadAndDisplayActivity
//            IdlingRegistry.getInstance().register(myActivity.idlingResource)
//
//            Espresso.onView(ViewMatchers.withId(R.id.recyclerListAdView))
//                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//
//            count++
//        } while (myActivity.multiAdRequestCompleted)
//        Assert.assertFalse(
//            "MultiAdRequest is still in progress, Idling Resource isn't working properly",
//            myActivity.multiAdRequestCompleted
//        )
//        println("Total Count: " + count)
//    }
//
//    @Test
//    fun testMARMaxRTBCombination() {
//        var count = 12
//        setupTwelveRTBCombination()
//        do {
//            if (::myActivity.isInitialized) {
//                myActivity.finish()
//            }
//            setupTwelveRTBCombination()
//            val intent = Intent()
//            intent.putExtra(MARLoadAndDisplayActivity.AD_TYPE, arrayListAdType)
//            intent.putExtra(MARLoadAndDisplayActivity.BID_TYPE, arrayListBidType)
//            intent.putExtra(MARLoadAndDisplayActivity.DISPLAY_AD, false);
//            mActivityTestRule.launchActivity(intent)
//            myActivity = mActivityTestRule.getActivity() as MARLoadAndDisplayActivity
//            IdlingRegistry.getInstance().register(myActivity.idlingResource)
//
//            Espresso.onView(ViewMatchers.withId(R.id.recyclerListAdView))
//                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//
//            count++
//        } while (myActivity.multiAdRequestCompleted)
//        Assert.assertFalse(
//            "MultiAdRequest is still in progress, Idling Resource isn't working properly",
//            myActivity.multiAdRequestCompleted
//        )
//        println("Total Count: " + count)
//    }

    private fun setupTwelveRTBBanner() {
        for (i in 0..11) {
            addBanner()
            addRTB()
        }
    }

    private fun setupTwelveRTBInterstitial() {
        for (i in 0..11) {
            addInterstitial()
            addRTB()
        }
    }

    private fun setupTwelveRTBNative() {
        for (i in 0..11) {
            addNative()
            addRTB()
        }
    }

    private fun setupTwelveRTBVideo() {
        for (i in 0..11) {
            addVideo()
            addRTB()
        }
    }

    private fun setupTwelveCSMBanner() {
        for (i in 0..11) {
            addBanner()
            addCSM()
        }
    }

    private fun setupTwelveCSMInterstitial() {
        for (i in 0..11) {
            addInterstitial()
            addCSM()
        }
    }

    private fun setupTwelveCSMNative() {
        for (i in 0..11) {
            addNative()
            addCSM()
        }
    }

    private fun setupTwelveCSMVideo() {
        for (i in 0..11) {
            addVideo()
            addCSM()
        }
    }

    private fun setupTwelveRTBCombination() {
        for (i in 0..3) {
            addBanner()
            addRTB()
            addInterstitial()
            addRTB()
            addNative()
            addRTB()
            addVideo()
            addRTB()
        }
    }

    private fun setupTwelveCSMCombination() {
        for (i in 0..3) {
            addBanner()
            addCSM()
            addInterstitial()
            addCSM()
            addNative()
            addCSM()
            addVideo()
            addCSM()
        }
    }

    private fun addRTB() {
        arrayListBidType.add("RTB")
    }

    private fun addCSM() {
        arrayListBidType.add("CSM")
    }

    private fun addInterstitial() {
        arrayListAdType.add("Interstitial")
    }

    private fun addNative() {
        arrayListAdType.add("Native")
    }

    private fun addVideo() {
        arrayListAdType.add("Video")
    }

    private fun addBanner() {
        arrayListAdType.add("Banner")
    }

}