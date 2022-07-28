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
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import appnexus.com.appnexussdktestapp.MARLoadAndDisplayActivity
import appnexus.com.appnexussdktestapp.R
import appnexus.com.appnexussdktestapp.util.Utility.Companion.checkVisibilityDetectorMap
import appnexus.com.appnexussdktestapp.util.Utility.Companion.resetVisibilityDetector
import com.appnexus.opensdk.SDKSettings
import com.appnexus.opensdk.XandrAd
import kotlinx.android.synthetic.main.activity_mar_load.*
import org.hamcrest.Matchers.not
import org.junit.*
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class MARCombinationVisibilityDetectorTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MARLoadAndDisplayActivity::class.java, false, false)

    internal lateinit var myActivity: MARLoadAndDisplayActivity
    internal var arrayListAdType = ArrayList<String>()
    internal var arrayListBidType = ArrayList<String>()


    @Before
    fun setup() {
        XandrAd.reset()
        XandrAd.init(123, null, false, null)
        IdlingPolicies.setMasterPolicyTimeout(1, TimeUnit.MINUTES)
        IdlingPolicies.setIdlingResourceTimeout(1, TimeUnit.MINUTES)
        arrayListAdType.clear()
        arrayListBidType.clear()
        resetVisibilityDetector()
    }

    @After
    fun destroy() {
        IdlingRegistry.getInstance().unregister(myActivity.idlingResource)
    }
    @Test
    fun testMARCombinationTwoRTBBanner() {
        XandrAd.init(10094, null, false, null)
        Thread.sleep(2000)
        setupTwoRTBBanner()
        val intent = Intent()
        intent.putExtra(MARLoadAndDisplayActivity.AD_TYPE, arrayListAdType)
        intent.putExtra(MARLoadAndDisplayActivity.BID_TYPE, arrayListBidType)
        mActivityTestRule.launchActivity(intent)
        myActivity = mActivityTestRule.getActivity() as MARLoadAndDisplayActivity
        setVisibility(View.GONE)
        IdlingRegistry.getInstance().register(myActivity.idlingResource)

        checkVisibilityDetectorMap(0, myActivity)

        Espresso.onView(ViewMatchers.withId(R.id.recyclerListAdView))
            .check(ViewAssertions.matches(not(ViewMatchers.isDisplayed())))

        Thread.sleep(2000)

        checkVisibilityDetectorMap(2, myActivity)

        Thread.sleep(2000)

        setVisibility(View.VISIBLE)

        Thread.sleep(2000)

        checkVisibilityDetectorMap(0, myActivity)


        Assert.assertTrue(
            "MultiAdRequest is still in progress, Idling Resource isn't working properly",
            myActivity.multiAdRequestCompleted
        )
    }

    private fun setVisibility(visibility: Int) {
        Handler(Looper.getMainLooper()).post({
            myActivity.recyclerListAdView.visibility = visibility
        })
    }

    @Test
    fun testMARCombinationFourRTBBanner() {
        XandrAd.init(10094, null, false, null)
        Thread.sleep(2000)
        setupTwoRTBBanner()
        setupTwoRTBBanner()
        val intent = Intent()
        intent.putExtra(MARLoadAndDisplayActivity.AD_TYPE, arrayListAdType)
        intent.putExtra(MARLoadAndDisplayActivity.BID_TYPE, arrayListBidType)
        mActivityTestRule.launchActivity(intent)
        myActivity = mActivityTestRule.getActivity() as MARLoadAndDisplayActivity
        setVisibility(View.GONE)
        IdlingRegistry.getInstance().register(myActivity.idlingResource)

        checkVisibilityDetectorMap(0, myActivity)

        Espresso.onView(ViewMatchers.withId(R.id.recyclerListAdView))
            .check(ViewAssertions.matches(not(ViewMatchers.isDisplayed())))

        Thread.sleep(2000)

        checkVisibilityDetectorMap(4, myActivity)

        Thread.sleep(2000)

        setVisibility(View.VISIBLE)

        Thread.sleep(1000)

        myActivity.recyclerListAdView.smoothScrollToPosition(3)

        Thread.sleep(5000)

        checkVisibilityDetectorMap(0, myActivity)


        Assert.assertTrue(
            "MultiAdRequest is still in progress, Idling Resource isn't working properly",
            myActivity.multiAdRequestCompleted
        )
    }

    @Test
    fun testMARCombinationTwelveRTBBanner() {
        XandrAd.init(10094, null, false, null)
        Thread.sleep(2000)
        for (i in 0..5) {
            setupTwoRTBBanner()
        }
        val intent = Intent()
        intent.putExtra(MARLoadAndDisplayActivity.AD_TYPE, arrayListAdType)
        intent.putExtra(MARLoadAndDisplayActivity.BID_TYPE, arrayListBidType)
        mActivityTestRule.launchActivity(intent)
        myActivity = mActivityTestRule.getActivity() as MARLoadAndDisplayActivity
        setVisibility(View.GONE)
        IdlingRegistry.getInstance().register(myActivity.idlingResource)

        checkVisibilityDetectorMap(0, myActivity)

        Espresso.onView(ViewMatchers.withId(R.id.recyclerListAdView))
            .check(ViewAssertions.matches(not(ViewMatchers.isDisplayed())))

        Thread.sleep(2000)

        checkVisibilityDetectorMap(12, myActivity)

        Thread.sleep(2000)

        setVisibility(View.VISIBLE)

        Thread.sleep(1000)

        myActivity.recyclerListAdView.smoothScrollToPosition(5)

        Thread.sleep(5000)

        checkVisibilityDetectorMap(6, myActivity)

        myActivity.recyclerListAdView.smoothScrollToPosition(11)

        Thread.sleep(5000)

        checkVisibilityDetectorMap(0, myActivity)

        Assert.assertTrue(
            "MultiAdRequest is still in progress, Idling Resource isn't working properly",
            myActivity.multiAdRequestCompleted
        )
    }

    @Test
    fun testMARCombinationTwoRTBNative() {
        XandrAd.init(10094, null, false, null)
        Thread.sleep(2000)
        setupTwoRTBNative()
        val intent = Intent()
        intent.putExtra(MARLoadAndDisplayActivity.AD_TYPE, arrayListAdType)
        intent.putExtra(MARLoadAndDisplayActivity.BID_TYPE, arrayListBidType)
        mActivityTestRule.launchActivity(intent)
        myActivity = mActivityTestRule.getActivity() as MARLoadAndDisplayActivity
        myActivity.shouldDisplayNativeAd = false
        checkVisibilityDetectorMap(0, myActivity)
        IdlingRegistry.getInstance().register(myActivity.idlingResource)

        Espresso.onView(ViewMatchers.withId(R.id.recyclerListAdView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        checkVisibilityDetectorMap(2, myActivity)

        myActivity.shouldDisplayNativeAd = true
        myActivity.refreshForVisibility()

        Thread.sleep(5000)

        checkVisibilityDetectorMap(0, myActivity)


        Assert.assertTrue(
            "MultiAdRequest is still in progress, Idling Resource isn't working properly",
            myActivity.multiAdRequestCompleted
        )
    }

    @Test
    fun testMARCombinationFourRTBNative() {
        XandrAd.init(10094, null, false, null)
        Thread.sleep(2000)
        setupTwoRTBNative()
        setupTwoRTBNative()
        val intent = Intent()
        intent.putExtra(MARLoadAndDisplayActivity.AD_TYPE, arrayListAdType)
        intent.putExtra(MARLoadAndDisplayActivity.BID_TYPE, arrayListBidType)
        mActivityTestRule.launchActivity(intent)
        myActivity = mActivityTestRule.getActivity() as MARLoadAndDisplayActivity
        myActivity.shouldDisplayNativeAd = false
        checkVisibilityDetectorMap(0, myActivity)
        IdlingRegistry.getInstance().register(myActivity.idlingResource)

        Espresso.onView(ViewMatchers.withId(R.id.recyclerListAdView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Thread.sleep(2000)

        checkVisibilityDetectorMap(4, myActivity)

        Thread.sleep(2000)

        myActivity.shouldDisplayNativeAd = true
        myActivity.refreshForVisibility()

        Thread.sleep(1000)

        myActivity.recyclerListAdView.smoothScrollToPosition(3)

        Thread.sleep(5000)

        checkVisibilityDetectorMap(0, myActivity)


        Assert.assertTrue(
            "MultiAdRequest is still in progress, Idling Resource isn't working properly",
            myActivity.multiAdRequestCompleted
        )
    }

//    @Test
//    fun testMARCombinationTwelveRTBNative() {
//        XandrAd.init(10094, null, false, null)
//        Thread.sleep(2000)
//        for (i in 0..5) {
//            setupTwoRTBNative()
//        }
//        val intent = Intent()
//        intent.putExtra(MARLoadAndDisplayActivity.AD_TYPE, arrayListAdType)
//        intent.putExtra(MARLoadAndDisplayActivity.BID_TYPE, arrayListBidType)
//        mActivityTestRule.launchActivity(intent)
//        myActivity = mActivityTestRule.getActivity() as MARLoadAndDisplayActivity
//        myActivity.shouldDisplayNativeAd = false
//        IdlingRegistry.getInstance().register(myActivity.idlingResource)
//
//        checkVisibilityDetectorMap(0, myActivity)
//
//        Espresso.onView(ViewMatchers.withId(R.id.recyclerListAdView))
//            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//
//        Thread.sleep(2000)
//
//        checkVisibilityDetectorMap(myActivity.recyclerListAdView.childCount, myActivity)
//
//        Thread.sleep(2000)
//
//        myActivity.shouldDisplayNativeAd = true
//        myActivity.refreshForVisibility()
//
//        Thread.sleep(1000)
//
//        myActivity.recyclerListAdView.smoothScrollToPosition(myActivity.recyclerListAdView.childCount + 1)
//
//        Thread.sleep(5000)
//
//        checkVisibilityDetectorMap(myActivity.recyclerListAdView.childCount, myActivity)
//
//        myActivity.recyclerListAdView.smoothScrollToPosition(11)
//
//        Thread.sleep(5000)
//
//        checkVisibilityDetectorMap(0, myActivity)
//
//        Assert.assertTrue(
//            "MultiAdRequest is still in progress, Idling Resource isn't working properly",
//            myActivity.multiAdRequestCompleted
//        )
//    }

    private fun setupTwoRTBBanner() {
        addBanner()
        addRTB()
        addBanner()
        addRTB()
    }

    private fun setupTwoRTBNative() {
        addNative()
        addRTB()
        addNative()
        addRTB()
    }

    private fun addRTB() {
        arrayListBidType.add("RTB")
    }

    private fun addBanner() {
        arrayListAdType.add("Banner")
    }

    private fun addNative() {
        arrayListAdType.add("Native")
    }

}