/*
 *    Copyright 2020 APPNEXUS INC
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
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.runner.AndroidJUnit4
import appnexus.com.appnexussdktestapp.BannerOMIDActivity
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
class BannerOMIDFriendlyObstructionTest {
    val Int.dp: Int
        get() = (this / Resources.getSystem().displayMetrics.density).toInt()
    val Int.px: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()

    @get:Rule
    var reportHelper = Factory.getReportHelper()

    @Rule
    @JvmField
    var mActivityTestRule = IntentsTestRule(BannerOMIDActivity::class.java, false, false)

    lateinit var bannerActivity: BannerOMIDActivity

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
    fun bannerLoadSize300x250Test() {


        Thread.sleep(2000)

        bannerActivity.triggerAdLoad("24087742", 300, 250)

        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(2)))
        Espresso.onView(ViewMatchers.withId(bannerActivity.banner_id))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Assert.assertTrue(
            "Wrong Ad Width",
            bannerActivity.banner.getChildAt(0).width.dp >= (bannerActivity.banner.adWidth - 1) ||
                    bannerActivity.banner.getChildAt(0).width.dp <= (bannerActivity.banner.adWidth + 1)
        )
        Assert.assertTrue(
            "Wrong Ad Height",
            bannerActivity.banner.getChildAt(0).height.dp >= (bannerActivity.banner.adHeight - 1) ||
                    bannerActivity.banner.getChildAt(0).height.dp <= (bannerActivity.banner.adHeight + 1)
        )

        Thread.sleep(5000)
        println("TEST EVENT DATA: " + bannerActivity.appEventData)
        Assert.assertTrue(bannerActivity.appEventData!!.contains( "\"percentageInView\":100",true))
        Thread.sleep(1000)
        
        bannerActivity.addObstructionView(View.GONE)

        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(3)))

        Thread.sleep(5000)
        println("TEST EVENT DATA: " + bannerActivity.appEventData)
        Assert.assertTrue(bannerActivity.appEventData!!.contains( "\"percentageInView\":100",true))
        Thread.sleep(1000)

        bannerActivity.addObstructionView(View.INVISIBLE)

        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(4)))

        Thread.sleep(5000)
        println("TEST EVENT DATA: " + bannerActivity.appEventData)
        Assert.assertTrue(bannerActivity.appEventData!!.contains( "\"percentageInView\":100",true))
        Thread.sleep(1000)

        var nonTransparentView = bannerActivity.addObstructionView(0.5f)

        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(5)))

        Thread.sleep(5000)
        println("TEST EVENT DATA: " + bannerActivity.appEventData)
        Assert.assertFalse(bannerActivity.appEventData!!.contains( "\"percentageInView\":100",true))
        Thread.sleep(1000)

        bannerActivity.addFriendlyObstruction(nonTransparentView)

        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(5)))

        Thread.sleep(5000)
        println("TEST EVENT DATA: " + bannerActivity.appEventData)
        Assert.assertTrue(bannerActivity.appEventData!!.contains( "\"percentageInView\":100",true))
        Thread.sleep(1000)

        bannerActivity.removeFriendlyObstruction(nonTransparentView)

        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(5)))

        Thread.sleep(5000)
        println("TEST EVENT DATA: " + bannerActivity.appEventData)
        Assert.assertFalse(bannerActivity.appEventData!!.contains( "\"percentageInView\":100",true))
        Thread.sleep(1000)

        bannerActivity.removeView(nonTransparentView)

        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(4)))

        Thread.sleep(5000)
        println("TEST EVENT DATA: " + bannerActivity.appEventData)
        Assert.assertTrue(bannerActivity.appEventData!!.contains( "\"percentageInView\":100",true))

        nonTransparentView = bannerActivity.addObstructionView(0.5f, 800, 800)

        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(5)))

        Thread.sleep(5000)
        println("TEST EVENT DATA: " + bannerActivity.appEventData)
        Assert.assertFalse(bannerActivity.appEventData!!.contains( "\"percentageInView\":100",true))
        Thread.sleep(1000)

        bannerActivity.addFriendlyObstruction(nonTransparentView)

        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(5)))

        Thread.sleep(5000)
        println("TEST EVENT DATA: " + bannerActivity.appEventData)
        Assert.assertTrue(bannerActivity.appEventData!!.contains( "\"percentageInView\":100",true))
        Thread.sleep(1000)

        bannerActivity.removeAllFriendlyObstructions()

        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(5)))

        Thread.sleep(5000)
        println("TEST EVENT DATA: " + bannerActivity.appEventData)
        Assert.assertFalse(bannerActivity.appEventData!!.contains( "\"percentageInView\":100",true))
        Thread.sleep(1000)
    }

}
