/*
 *    Copyright 2012 APPNEXUS INC
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
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.runner.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import appnexus.com.appnexussdktestapp.BannerActivity
import appnexus.com.appnexussdktestapp.BannerLazyLoadActivity
import appnexus.com.appnexussdktestapp.R
import com.appnexus.opensdk.ANClickThroughAction
import com.appnexus.opensdk.AdActivity
import com.appnexus.opensdk.utils.StringUtil
import com.appnexus.opensdk.utils.ViewUtil
import com.microsoft.appcenter.espresso.Factory
import org.hamcrest.CoreMatchers
import org.junit.*

import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class BannerLazyLoadTest {
    val Int.dp: Int
        get() = (this / Resources.getSystem().displayMetrics.density).toInt()
    val Int.px: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()

    @get:Rule
    var reportHelper = Factory.getReportHelper()

    @Rule
    @JvmField
    var mActivityTestRule = IntentsTestRule(BannerLazyLoadActivity::class.java, false, false)

    lateinit var bannerActivity: BannerLazyLoadActivity

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
    * Test for the Invalid Renderer Url for Banner Native Ad (NativeAssemblyRenderer)
    * */
    @Test
    fun bannerLazyLoad() {

        Thread.sleep(2000)

        Espresso.onView(ViewMatchers.withId(R.id.main_content))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.main_content))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(2)))
//        Espresso.onView(ViewMatchers.withId(bannerActivity.banner_id))
//            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))

        onView(withId(R.id.activateWebview)).perform(ViewActions.click())

        Thread.sleep(5000)

        Espresso.onView(ViewMatchers.withId(R.id.main_content))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(3)))
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

        println("LAZY LOAD: " + bannerActivity.logListener.logMsg)
    }

}
