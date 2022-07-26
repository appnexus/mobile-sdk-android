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
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.runner.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import appnexus.com.appnexussdktestapp.BannerActivity
import appnexus.com.appnexussdktestapp.R
import com.appnexus.opensdk.InitListener
import com.appnexus.opensdk.XandrAd
import com.microsoft.appcenter.espresso.Factory
import org.hamcrest.CoreMatchers.not
import org.junit.*
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class BannerImpressionTest {
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
    * Sanity Test for the Banner Viewable Impression
    * */
    @Test
    fun bannerViewableImpression() {

        XandrAd.init(10094, null, false, null)
        Thread.sleep(2000)

        Assert.assertFalse(bannerActivity.onAdImpression)

        bannerActivity.triggerAdLoad("17058950", 300, 250)

        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(1)))
        Espresso.onView(ViewMatchers.withId(bannerActivity.banner_id))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        var count = 0
        while (count < 5 && !bannerActivity.onAdImpression) {
            Thread.sleep(1000)
            count++
        }

        Assert.assertTrue(bannerActivity.onAdImpression)
    }

    /*
    * Test for the Eligible Banner Viewable Impression when Visibility is GONE
    * and then the Visibility is changed to VISIBLE
    * */
    @Test
    fun bannerViewableImpressionVisibilityGone() {

        XandrAd.init(10094, null, false, null)
        Thread.sleep(2000)

        Assert.assertFalse(bannerActivity.onAdImpression)
        bannerActivity.shouldDisplay = false

        bannerActivity.triggerAdLoad("17058950", 300, 250, visibility = View.GONE)

        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(1)))
        Espresso.onView(ViewMatchers.withId(bannerActivity.banner_id))
            .check(ViewAssertions.matches(not(ViewMatchers.isDisplayed())))

        assertTrue(XandrAd.isEligibleForViewableImpression(10094))

        var count = 0
        while (count < 5 && !bannerActivity.onAdImpression) {
            Thread.sleep(1000)
            count++
        }

        assertFalse(bannerActivity.onAdImpression)

        bannerActivity.displayBanner()
        count = 0
        while (count < 5 && !bannerActivity.onAdImpression) {
            Thread.sleep(1000)
            count++
        }

        Assert.assertTrue(bannerActivity.onAdImpression)
    }

    // -- DEFAULT Impression tracking

    /*
    * Sanity Test for the Banner Default Impression
    * */
    @Test
    fun bannerDefaultImpression() {

        Thread.sleep(2000)

        Assert.assertFalse(bannerActivity.onAdImpression)

        bannerActivity.triggerAdLoad("17058950", 300, 250)

        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(1)))
        Espresso.onView(ViewMatchers.withId(bannerActivity.banner_id))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Assert.assertTrue(bannerActivity.onAdImpression)
    }

    /*
    * Sanity Test for the Banner Default Impression with invalid Member ID
    * */
    @Test
    fun bannerDefaultImpressionWithInvalidMemberId() {

        XandrAd.init(123, null, false, null)
        Thread.sleep(2000)

        Assert.assertFalse(bannerActivity.onAdImpression)

        bannerActivity.shouldDisplay = false

        bannerActivity.triggerAdLoad("17058950", 300, 250)

        var count = 0
        while (count < 5 && !bannerActivity.onAdImpression) {
            Thread.sleep(1000)
            count++
        }

        Assert.assertTrue(bannerActivity.onAdImpression)

        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(1)))
        Espresso.onView(ViewMatchers.withId(bannerActivity.banner_id))
            .check(ViewAssertions.matches(not(ViewMatchers.isDisplayed())))


    }

}
