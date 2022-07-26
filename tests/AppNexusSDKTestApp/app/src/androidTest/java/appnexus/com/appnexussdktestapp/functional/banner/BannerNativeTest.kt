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

package appnexus.com.appnexussdktestapp.functional.banner

import android.content.Intent
import android.content.res.Resources
import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.runner.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import appnexus.com.appnexussdktestapp.BannerActivity
import appnexus.com.appnexussdktestapp.R
import com.appnexus.opensdk.ANClickThroughAction
import com.appnexus.opensdk.AdActivity
import com.appnexus.opensdk.SDKSettings
import com.appnexus.opensdk.XandrAd
import com.appnexus.opensdk.utils.StringUtil
import com.appnexus.opensdk.utils.ViewUtil
import com.microsoft.appcenter.espresso.Factory
import org.junit.*

import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class BannerNativeTest {
    val Int.dp: Int
        get() = (this / Resources.getSystem().displayMetrics.density).toInt()
    val Int.px: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()

    @get:Rule
    var reportHelper = Factory.getReportHelper()

    @Rule
    @JvmField
    var mActivityTestRule = IntentsTestRule(BannerActivity::class.java, false, false)

    lateinit var bannerActivity: BannerActivity

    @Before
    fun setup() {
        XandrAd.reset()
        XandrAd.init(123, null, false, null)
        IdlingPolicies.setMasterPolicyTimeout(1, TimeUnit.MINUTES)
        IdlingPolicies.setIdlingResourceTimeout(1, TimeUnit.MINUTES)
        var intent = Intent()
        mActivityTestRule.launchActivity(intent)
        bannerActivity = mActivityTestRule.activity
        IdlingRegistry.getInstance().register(bannerActivity.idlingResource)
    }

    @After
    fun destroy() {
        IdlingRegistry.getInstance().unregister(bannerActivity.idlingResource)
        reportHelper.label("Stopping App")
    }

    /*
    * Test for the Invalid Renderer Url for Banner Native Ad (NativeAssemblyRenderer)
    * */
    @Test
    fun bannerNativeAssemblyRendererLoadWrongRendererUrlTest() {

        bannerActivity.triggerAdLoad("17058950", allowNativeDemand = true, rendererId = 502, useNativeRenderer = true)
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(1)))
        Espresso.onView(ViewMatchers.withId(R.id.title))
            .check(ViewAssertions.matches(isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.description))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withText("What is in the Name....")).check(matches(isDisplayed()));
        onView(withText("The person who said \"What is in the Name\" wrote his name below the quote...")).check(matches(isDisplayed()));
        Thread.sleep(5000)
        Espresso.onView(ViewMatchers.withId(R.id.icon))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.image))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    /*
    * Test Clickthrough Action - SDKBrowser
    * */
    @Test
    fun bannerNativeAssemblyRendererClickThroughSDKBrowserTest() {

        bannerActivity.triggerAdLoad("17058950", allowNativeDemand = true, rendererId = 502, useNativeRenderer = true, clickThroughAction = ANClickThroughAction.OPEN_SDK_BROWSER)
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(1)))
        Espresso.onView(ViewMatchers.withId(bannerActivity.banner_id))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(bannerActivity.banner_id))
            .perform(ViewActions.click())

        //check if the triggered intent is pointing to the AdActivity
        Intents.intended(IntentMatchers.hasComponent(AdActivity::class.java.name))
    }

    /*
    * Test Clickthrough Action - DeviceBrowser
    * */
    @Test
    fun bannerNativeAssemblyRendererClickThroughDeviceBrowserTest() {

        bannerActivity.triggerAdLoad("17058950", allowNativeDemand = true, rendererId = 502, useNativeRenderer = true, clickThroughAction = ANClickThroughAction.OPEN_DEVICE_BROWSER)
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(1)))
        Espresso.onView(ViewMatchers.withId(bannerActivity.banner_id))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(bannerActivity.banner_id))
            .perform(ViewActions.click())

        //check if the triggered intent has Action set as ACTION_VIEW
        Intents.intended(IntentMatchers.hasAction(Intent.ACTION_VIEW))
    }


    /*
    * Test Clickthrough Action - ReturnUrl
    * */
    @Test
    fun bannerNativeAssemblyRendererClickThroughReturnUrlTest() {

        bannerActivity.triggerAdLoad("17058950", allowNativeDemand = true, rendererId = 502, useNativeRenderer = true, clickThroughAction = ANClickThroughAction.RETURN_URL)
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(1)))
        Espresso.onView(ViewMatchers.withId(bannerActivity.banner_id))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(bannerActivity.banner_id))
            .perform(ViewActions.click())

        Assert.assertFalse(StringUtil.isEmpty(bannerActivity.clickUrl))
    }

    /*
    * ResizeToFitContainerSize Test for the Banner Native Assembly Renderer Ad
    * */
    @Test
    fun bannerNativeAssemblyRendererResizeToFitContainerTest() {

        bannerActivity.triggerAdLoad("17058950", width = 300, height = 250, allowNativeDemand = true, rendererId = 502, useNativeRenderer = true, resizeToFitContainer = true, expandsToFitScreenWidth = false)
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(1)))
        Espresso.onView(ViewMatchers.withId(bannerActivity.banner_id))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        var width = ViewUtil.getValueInDP(bannerActivity, bannerActivity.banner.width)
        var height = ViewUtil.getValueInDP(bannerActivity, bannerActivity.banner.height)
//        Assert.assertTrue("Width Resize Assertion Failure expected = 300, actual = " + width, width >= 299 && width <= 301)
        Assert.assertTrue("Height Resize Assertion Failure expected = 250, actual = " + height, height >= 249 && height <= 251)
    }

    /*
    * ExpandToFitScreenWidth Test for the Banner Native Assembly Renderer Ad
    * */
    @Test
    fun bannerNativeAssemblyRendererExpandToFitScreenTest() {

        bannerActivity.triggerAdLoad("17058950", width = 300, height = 250, allowNativeDemand = true, rendererId = 502, useNativeRenderer = true, expandsToFitScreenWidth = true)
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(1)))
        Espresso.onView(ViewMatchers.withId(bannerActivity.banner_id))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        var width = ViewUtil.getValueInDP(bannerActivity, bannerActivity.banner.width)
        Assert.assertTrue("Screen Width Resize Assertion Failure expected = " + ViewUtil.getScreenSizeAsDP(bannerActivity)[0] + ", actual = " + width, width == ViewUtil.getScreenSizeAsDP(bannerActivity)[0])
    }

    /*
    * Sanity Test for the Banner Native Ad Impression
    * */
    @Test
    fun bannerNativeLoadImpressionTest() {

        bannerActivity.triggerAdLoad("17058950", allowNativeDemand = true)
        onView(ViewMatchers.withId(R.id.linearLayout))
            .check(matches(isDisplayed()))
        Assert.assertTrue(bannerActivity.impressionLogged)
    }

    /*
    * Sanity Test for the Banner Native Ad Impression when Contianer view is attached later
    * */
    @Test
    fun bannerNativeLoadImpressionTestWithViewAttachedLater() {

        bannerActivity.shouldDisplay = false
        bannerActivity.triggerAdLoad("17058950", allowNativeDemand = true)
        onView(ViewMatchers.withId(R.id.linearLayout))
            .check(matches(isDisplayed()))
        Thread.sleep(1000)
        // This logic has been changed now
        // Does not wait for the view to be attached to the Window
        // We need to assert that the impression must have been logged until here
        Assert.assertTrue(bannerActivity.impressionLogged)
//        bannerActivity.attachNative()
//        Thread.sleep(1000)
//        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
//            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
//            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(1)))
//        Assert.assertTrue(bannerActivity.impressionLogged)
    }

    /*
    * Sanity Test for the Banner Native Ad Impression
    * */
    @Test
    fun bannerNativeLoadImpressionTestOnePx() {

        XandrAd.init(10094, null, false) { }
        bannerActivity.triggerAdLoad("17058950", allowNativeDemand = true)
        Thread.sleep(1000)
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(1)))
        Assert.assertTrue(bannerActivity.impressionLogged)
    }

    /*
    * Sanity Test for the Banner Native Ad Impression when Container view is attached later
    * */
    @Test
    fun bannerNativeLoadImpressionTestOnePxWithViewAttachedLater() {
        XandrAd.init(10094, null, false, null)
        bannerActivity.shouldDisplay = false
        bannerActivity.triggerAdLoad("17058950", allowNativeDemand = true)
        onView(ViewMatchers.withId(R.id.linearLayout))
            .check(matches(isDisplayed()))
        Assert.assertFalse(bannerActivity.impressionLogged)
        bannerActivity.attachNative(false)
        Thread.sleep(1000)
        Assert.assertFalse(bannerActivity.impressionLogged)
        bannerActivity.attachNative()
        Thread.sleep(1000)
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(1)))
        Assert.assertTrue(bannerActivity.impressionLogged)
    }
}
