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

package appnexus.com.appnexussdktestapp.placement.banner

import android.content.Intent
import android.content.res.Resources
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import appnexus.com.appnexussdktestapp.BannerActivity
import appnexus.com.appnexussdktestapp.R
import com.appnexus.opensdk.XandrAd
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
    var mActivityTestRule = ActivityTestRule(BannerActivity::class.java, false, false)

    lateinit var bannerActivity: BannerActivity

    @Before
    fun setup() {
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
    * Sanity Test for the Banner Ad of size 320x50
    * */
    @Test
    fun bannerNativeLoadPerformanceTest() {

        bannerActivity.triggerAdLoad("17058950", allowNativeDemand = true)

        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Assert.assertTrue(
            "Load time performance failure ${bannerActivity.getTime()}",
            bannerActivity.getTime() < 2000
        )

        Thread.sleep(500)

        bannerActivity.triggerAdLoad("17058950", allowNativeDemand = true)

        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Assert.assertTrue(
            "Load time performance failure ${bannerActivity.getTime()}",
            bannerActivity.getTime() < 2000
        )
    }

    /*
    * Sanity Test for the Banner Native Ad
    * */
    @Test
    fun bannerNativeLoadTest() {

        bannerActivity.triggerAdLoad("17058950", allowNativeDemand = true)
        onView(ViewMatchers.withId(R.id.linearLayout))
            .check(matches(isDisplayed()))
        onView(ViewMatchers.withId(R.id.linearLayout))
            .check(matches(ViewMatchers.hasChildCount(1)))
        onView(ViewMatchers.withId(R.id.title))
            .check(matches(isDisplayed()))
        onView(ViewMatchers.withId(R.id.description))
            .check(matches(isDisplayed()))
//        onView(withText("What is in the Name...."))
//            .check(matches(isDisplayed()));
//        onView(withText("The person who said \"What is in the Name\" wrote his name below the quote..."))
//            .check(matches(isDisplayed()));
        Thread.sleep(5000)
        onView(ViewMatchers.withId(R.id.icon))
            .check(matches(isDisplayed()))
        onView(ViewMatchers.withId(R.id.image))
            .check(matches(isDisplayed()))
    }

    /*
    * Sanity Test for the Banner Native Assembly Renderer Ad
    * */
    @Test
    fun bannerNativeAssemblyRendererLoadTest() {
        bannerActivity.triggerAdLoad("17058950", allowNativeDemand = true, rendererId = 502, useNativeRenderer = true)
        onView(ViewMatchers.withId(R.id.linearLayout))
            .check(matches(isDisplayed()))
        onView(ViewMatchers.withId(R.id.linearLayout))
            .check(matches(ViewMatchers.hasChildCount(1)))
        bannerActivity.layout.getChildAt(0)
        onView(ViewMatchers.withId(bannerActivity.banner.id))
            .check(matches(isDisplayed()))
    }
    
    // BG Testing

    /*
* Sanity Test for the Banner Native Ad
* */
    @Test
    fun bannerNativeLoadBGTest() {

        bannerActivity.triggerAdLoad("17058950", allowNativeDemand = true, bgTask = true)
        onView(ViewMatchers.withId(R.id.linearLayout))
            .check(matches(isDisplayed()))
        onView(ViewMatchers.withId(R.id.linearLayout))
            .check(matches(ViewMatchers.hasChildCount(1)))
        onView(ViewMatchers.withId(R.id.title))
            .check(matches(isDisplayed()))
        onView(ViewMatchers.withId(R.id.description))
            .check(matches(isDisplayed()))
//        onView(withText("What is in the Name...."))
//            .check(matches(isDisplayed()));
//        onView(withText("The person who said \"What is in the Name\" wrote his name below the quote..."))
//            .check(matches(isDisplayed()));
        Thread.sleep(5000)
        onView(ViewMatchers.withId(R.id.icon))
            .check(matches(isDisplayed()))
        onView(ViewMatchers.withId(R.id.image))
            .check(matches(isDisplayed()))
    }

    /*
    * Sanity Test for the Banner Native Assembly Renderer Ad
    * */
    @Test
    fun bannerNativeAssemblyRendererLoadBGTest() {
        bannerActivity.triggerAdLoad("17058950", allowNativeDemand = true, rendererId = 502, useNativeRenderer = true, bgTask = true)
        onView(ViewMatchers.withId(R.id.linearLayout))
            .check(matches(isDisplayed()))
        onView(ViewMatchers.withId(R.id.linearLayout))
            .check(matches(ViewMatchers.hasChildCount(1)))
        bannerActivity.layout.getChildAt(0)
        onView(ViewMatchers.withId(bannerActivity.banner.id))
            .check(matches(isDisplayed()))
    }
}
