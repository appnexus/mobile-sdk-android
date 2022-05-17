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
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.runner.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import appnexus.com.appnexussdktestapp.BannerActivity
import appnexus.com.appnexussdktestapp.R
import com.microsoft.appcenter.espresso.Factory
import org.junit.*

import org.junit.runner.RunWith
import org.junit.Rule
import java.util.concurrent.TimeUnit


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class BannerVideoTest {
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
    * Sanity Test for the Banner Video (Outstream Video)
    * */
    @Test
    fun bannerVideoLoadTest() {

        bannerActivity.triggerAdLoad("17058950", allowVideoDemand = true)

        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(1)))
        Espresso.onView(ViewMatchers.withId(bannerActivity.banner_id))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    /*
    * Sanity Test for the Banner Video (Outstream Video)
    * */
    @Test
    fun bannerVPAIDVideoLoadTest() {

        bannerActivity.triggerAdLoad("17058950", allowVideoDemand = true)

        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(1)))
        Espresso.onView(ViewMatchers.withId(bannerActivity.banner_id))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    // BG Testing

    /*
    * Sanity Test for the Banner Video (Outstream Video)
    * */
    @Test
    fun bannerVideoLoadBGTest() {

        bannerActivity.triggerAdLoad("17058950", allowVideoDemand = true,  bgTask = true)

        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(1)))
        Espresso.onView(ViewMatchers.withId(bannerActivity.banner_id))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    /*
    * Sanity Test for the Banner Video (Outstream Video)
    * */
    @Test
    fun bannerVPAIDVideoLoadBGTest() {

        bannerActivity.triggerAdLoad("17058950", allowVideoDemand = true,  bgTask = true)

        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(1)))
        Espresso.onView(ViewMatchers.withId(bannerActivity.banner_id))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}
