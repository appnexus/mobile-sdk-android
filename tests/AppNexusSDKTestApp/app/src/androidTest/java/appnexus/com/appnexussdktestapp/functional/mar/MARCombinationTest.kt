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
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import appnexus.com.appnexussdktestapp.MARLoadAndDisplayActivity
import appnexus.com.appnexussdktestapp.R
import com.appnexus.opensdk.XandrAd
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.*
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class MARCombinationTest {

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
    fun testMARCombinationTwoRTBBanner() {
        setupTwoRTBBanner()
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
    fun testMARCombinationTwoBannerRTBAndCSM() {
        setupTwoBannerRTBAndCSM()
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
    fun testMARCombinationTwoRTBInterstitial() {
        setupTwoRTBInterstitial()
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
    fun testMARCombinationTwoInterstitialRTBAndCSM() {
        setupTwoInterstitialRTBAndCSM()
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
    fun testMARCombinationTwoRTBNative() {
        setupTwoRTBNative()
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
    fun testMARCombinationTwoNativeRTBAndCSM() {
        setupTwoNativeRTBAndCSM()
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
    fun testMARCombinationTwoRTBVideo() {
        setupTwoRTBVideo()
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
    fun testMARCombinationTwoVideoRTBAndCSM() {
        setupTwoVideoRTBAndCSM()
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
    fun testMARCombinationAllRTB() {
        setupAllRTB()
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
    fun testMARCombinationRTBBannerWithLazyLoad() {
        setupRTBBannerWithLazyLoad()
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

        assertTrue(myActivity.onLazyAdLoaded)
        assertFalse(myActivity.onLazyLoadAdLoaded)


        Thread.sleep(2000)
        Espresso.onView(ViewMatchers.withText("Activate")).perform(ViewActions.click())
        Thread.sleep(8000)

        assertTrue(myActivity.onLazyAdLoaded)
        assertTrue(myActivity.onLazyLoadAdLoaded)
    }

    @Test
    fun testMARCombinationAllRTBWithBannerLazyLoad() {
        setupAllRTBWithBannerLazyLoad()
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

        assertTrue(myActivity.onLazyAdLoaded)
        assertFalse(myActivity.onLazyLoadAdLoaded)


        Thread.sleep(5000)
        Espresso.onView(ViewMatchers.withText("Activate")).perform(ViewActions.click())
        Thread.sleep(8000)

        assertTrue(myActivity.onLazyAdLoaded)
        assertTrue(myActivity.onLazyLoadAdLoaded)
    }

    @Test
    fun testMARCombinationAllCSM() {
        setupAllCSM()
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
    fun testMARCombinationVideoCSMWithOtherRTB() {
        setupVideoCSMWithOtherRTB()
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
    fun testMARCombinationBannerCSMWithOtherRTB() {
        setupBannerCSMWithOtherRTB()
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
    fun testMARCombinationInterstitialCSMWithOtherRTB() {
        setupInterstitialCSMWithOtherRTB()
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
    fun testMARCombinationNativeCSMWithOtherRTB() {
        setupNativeCSMWithOtherRTB()
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
    fun testMARCombinationVideoAndBannerCSMWithOtherRTB() {
        setupVideoAndBannerCSMWithOtherRTB()
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
    fun testMARCombinationVideoAndInterstitialCSMWithOtherRTB() {
        setupVideoAndInterstitialCSMWithOtherRTB()
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
    fun testMARCombinationVideoAndNativeCSMWithOtherRTB() {
        setupVideoAndNativeCSMWithOtherRTB()
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
    fun testMARCombinationBannerAndInterstitialCSMWithOtherRTB() {
        setupBannerAndInterstitialCSMWithOtherRTB()
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
    fun testMARCombinationBannerAndNativeCSMWithOtherRTB() {
        setupBannerAndNativeCSMWithOtherRTB()
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
    fun testMARCombinationInterstitialAndNativeCSMWithOtherRTB() {
        setupInterstitialAndNativeCSMWithOtherRTB()
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
    fun testMARCombinationBannerRTBWithOtherCSM() {
        setupBannerRTBWithOtherCSM()
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
    fun testMARCombinationInterstitialRTBWithOtherCSM() {
        setupInterstitialRTBWithOtherCSM()
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
    fun testMARCombinationNativeRTBWithOtherCSM() {
        setupNativeRTBWithOtherCSM()
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
    fun testMARCombinationVideoRTBWithOtherCSM() {
        setupVideoRTBWithOtherCSM()
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

    private fun setupTwoRTBBanner() {
        addBanner()
        addRTB()
        addBanner()
        addRTB()
    }

    private fun setupRTBBannerWithLazyLoad() {
        addBannerLazyLoad()
        addRTB()
    }

    private fun setupTwoBannerRTBAndCSM() {
        addBanner()
        addRTB()
        addBanner()
        addCSM()
    }

    private fun setupTwoRTBInterstitial() {
        addInterstitial()
        addRTB()
        addInterstitial()
        addRTB()
    }

    private fun setupTwoInterstitialRTBAndCSM() {
        addInterstitial()
        addRTB()
        addInterstitial()
        addCSM()
    }

    private fun setupTwoRTBNative() {
        addNative()
        addRTB()
        addNative()
        addRTB()
    }

    private fun setupTwoNativeRTBAndCSM() {
        addNative()
        addRTB()
        addNative()
        addCSM()
    }

    private fun setupTwoRTBVideo() {
        addVideo()
        addRTB()
        addVideo()
        addRTB()
    }

    private fun setupTwoVideoRTBAndCSM() {
        addVideo()
        addRTB()
        addVideo()
        addCSM()
    }

    private fun setupAllRTB() {
        addVideo()
        addRTB()
        addBanner()
        addRTB()
        addInterstitial()
        addRTB()
        addNative()
        addRTB()
    }

    private fun setupAllRTBWithBannerLazyLoad() {
        addVideo()
        addRTB()
        addBanner()
        addRTB()
        addInterstitial()
        addRTB()
        addNative()
        addRTB()
        addBannerLazyLoad()
        addRTB()
    }

    private fun setupAllCSM() {
        addVideo()
        addCSM()
        addBanner()
        addCSM()
        addInterstitial()
        addCSM()
        addNative()
        addCSM()
    }

    private fun setupVideoCSMWithOtherRTB() {
        addVideo()
        addCSM()
        addBanner()
        addRTB()
        addInterstitial()
        addRTB()
        addNative()
        addRTB()
    }

    private fun setupBannerCSMWithOtherRTB() {
        addBanner()
        addCSM()
        addVideo()
        addRTB()
        addInterstitial()
        addRTB()
        addNative()
        addRTB()
    }

    private fun setupInterstitialCSMWithOtherRTB() {
        addInterstitial()
        addCSM()
        addBanner()
        addRTB()
        addVideo()
        addRTB()
        addNative()
        addRTB()
    }

    private fun setupNativeCSMWithOtherRTB() {
        addInterstitial()
        addRTB()
        addBanner()
        addRTB()
        addVideo()
        addRTB()
        addNative()
        addCSM()
    }

    private fun setupVideoAndBannerCSMWithOtherRTB() {
        addVideo()
        addCSM()
        addBanner()
        addCSM()
        addInterstitial()
        addRTB()
        addNative()
        addRTB()
    }

    private fun setupVideoAndInterstitialCSMWithOtherRTB() {
        addVideo()
        addCSM()
        addBanner()
        addRTB()
        addInterstitial()
        addCSM()
        addNative()
        addRTB()
    }

    private fun setupVideoAndNativeCSMWithOtherRTB() {
        addVideo()
        addCSM()
        addBanner()
        addRTB()
        addInterstitial()
        addRTB()
        addNative()
        addCSM()
    }

    private fun setupBannerAndInterstitialCSMWithOtherRTB() {
        addBanner()
        addCSM()
        addVideo()
        addRTB()
        addInterstitial()
        addCSM()
        addNative()
        addRTB()
    }

    private fun setupBannerAndNativeCSMWithOtherRTB() {
        addBanner()
        addCSM()
        addVideo()
        addRTB()
        addInterstitial()
        addRTB()
        addNative()
        addCSM()
    }

    private fun setupInterstitialAndNativeCSMWithOtherRTB() {
        addInterstitial()
        addCSM()
        addBanner()
        addRTB()
        addVideo()
        addRTB()
        addNative()
        addCSM()
    }

    private fun setupBannerRTBWithOtherCSM() {
        addInterstitial()
        addCSM()
        addBanner()
        addRTB()
        addVideo()
        addCSM()
        addNative()
        addCSM()
    }

    private fun setupInterstitialRTBWithOtherCSM() {
        addInterstitial()
        addRTB()
        addBanner()
        addCSM()
        addVideo()
        addCSM()
        addNative()
        addCSM()
    }

    private fun setupNativeRTBWithOtherCSM() {
        addInterstitial()
        addCSM()
        addBanner()
        addCSM()
        addVideo()
        addCSM()
        addNative()
        addRTB()
    }

    private fun setupVideoRTBWithOtherCSM() {
        addInterstitial()
        addCSM()
        addBanner()
        addCSM()
        addVideo()
        addRTB()
        addNative()
        addCSM()
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

    private fun addBannerLazyLoad() {
        arrayListAdType.add("Banner-LazyLoad")
    }

}