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

package appnexus.com.appnexussdktestapp.functional.native

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import appnexus.com.appnexussdktestapp.NativeImpressionActivity
import appnexus.com.appnexussdktestapp.R
import com.appnexus.opensdk.SDKSettings
import com.appnexus.opensdk.XandrAd
import com.microsoft.appcenter.espresso.Factory
import org.hamcrest.CoreMatchers.not
import org.junit.*
import org.junit.Assert.assertFalse
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class NativeImpressionTest {

    @get:Rule
    var reportHelper = Factory.getReportHelper()

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(NativeImpressionActivity::class.java, false, false)

    lateinit var nativeActivity: NativeImpressionActivity

    @Before
    fun setup() {
        XandrAd.reset()
        XandrAd.init(123, null, false, null)
        IdlingPolicies.setMasterPolicyTimeout(1, TimeUnit.MINUTES)
        IdlingPolicies.setIdlingResourceTimeout(1, TimeUnit.MINUTES)
        var intent = Intent()
        mActivityTestRule.launchActivity(intent)
        nativeActivity = mActivityTestRule.activity
        IdlingRegistry.getInstance().register(nativeActivity.idlingResource)
    }

    @After
    fun destroy() {
        IdlingRegistry.getInstance().unregister(nativeActivity.idlingResource)
        reportHelper.label("Stopping App")
    }

    /*
   * Test for Default Native Impression firing method.
   * */
    @Test
    fun nativeAdImpressionTest() {
        Assert.assertFalse(nativeActivity.didLogImpression)
        nativeActivity.shouldDisplay = false
        nativeActivity.triggerAdLoad("17982237", creativeId = 182426521)
        Thread.sleep(1000)
        var count = 0
        while (count < 5 && !nativeActivity.didLogImpression) {
            Thread.sleep(1000)
            count++
        }
        Assert.assertTrue(nativeActivity.didLogImpression)

        nativeActivity.changeVisibility(GONE)
        nativeActivity.attachNative()
        Thread.sleep(5000)
        Espresso.onView(ViewMatchers.withId(R.id.title))
            .check(ViewAssertions.matches(not(ViewMatchers.isDisplayed())))
        Espresso.onView(ViewMatchers.withId(R.id.description))
            .check(ViewAssertions.matches(not(ViewMatchers.isDisplayed())))
        Espresso.onView(ViewMatchers.withId(R.id.icon))
            .check(ViewAssertions.matches(not(ViewMatchers.isDisplayed())))
        Espresso.onView(ViewMatchers.withId(R.id.image))
            .check(ViewAssertions.matches(not(ViewMatchers.isDisplayed())))
        Assert.assertTrue(nativeActivity.didLogImpression)

        nativeActivity.changeVisibility(VISIBLE)

        Espresso.onView(ViewMatchers.withId(R.id.title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.description))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.icon))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.image))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Assert.assertTrue(nativeActivity.didLogImpression)
    }

    /*
    * Test for Native Viewable Impression firing method.
    * */
    @Test
    fun nativeAdImpressionTestViewableImpression() {
        XandrAd.init(10094, null, false, null)
        Assert.assertFalse(nativeActivity.didLogImpression)
        nativeActivity.shouldDisplay = false
        nativeActivity.triggerAdLoad("17982237", creativeId = 182426521)
        Thread.sleep(1000)
        Assert.assertFalse(nativeActivity.didLogImpression)
        nativeActivity.changeVisibility(GONE)
        nativeActivity.attachNative()
        Thread.sleep(5000)
        Espresso.onView(ViewMatchers.withId(R.id.title))
            .check(ViewAssertions.matches(not(ViewMatchers.isDisplayed())))
        Espresso.onView(ViewMatchers.withId(R.id.description))
            .check(ViewAssertions.matches(not(ViewMatchers.isDisplayed())))
        Espresso.onView(ViewMatchers.withId(R.id.icon))
            .check(ViewAssertions.matches(not(ViewMatchers.isDisplayed())))
        Espresso.onView(ViewMatchers.withId(R.id.image))
            .check(ViewAssertions.matches(not(ViewMatchers.isDisplayed())))
        Assert.assertFalse(nativeActivity.didLogImpression) // The Impression isn't fired yet

        nativeActivity.changeVisibility(VISIBLE)

        Espresso.onView(ViewMatchers.withId(R.id.title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.description))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.icon))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.image))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        var count = 0
        while (count < 5 && !nativeActivity.didLogImpression) {
            Thread.sleep(1000)
            count++
        }

        Assert.assertTrue(nativeActivity.didLogImpression)
    }

    /*
   * Test for Native Impression firing method.
   * */
    @Test
    fun nativeAdImpressionTestWithAttachedToDummyView() {
        Assert.assertFalse(nativeActivity.didLogImpression)
        nativeActivity.shouldDisplay = false
        nativeActivity.shouldAttachToDummy = true
        nativeActivity.triggerAdLoad("17982237", creativeId = 182426521)
        Thread.sleep(1000)
        var count = 0
        while (count < 5 && !nativeActivity.didLogImpression) {
            Thread.sleep(1000)
            count++
        }
        // New native method changes (BEGIN TO RENDER)
        Assert.assertTrue(nativeActivity.didLogImpression)

        nativeActivity.attachNative()
        Thread.sleep(1000)
        Espresso.onView(ViewMatchers.withId(R.id.title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Assert.assertTrue(nativeActivity.didLogImpression)
    }

    /*
   * Test for Default Native Impression firing method.
   * */
    @Test
    fun nativeAdImpressionTestAttachViewLater() {
        Assert.assertFalse(nativeActivity.didLogImpression)
        nativeActivity.shouldDisplay = false
        nativeActivity.triggerAdLoad("17982237", creativeId = 182426521)
        Assert.assertFalse(nativeActivity.didLogImpression)
        nativeActivity.attachNative()
        Thread.sleep(1000)
        Espresso.onView(ViewMatchers.withId(R.id.title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.description))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(5000)
        Espresso.onView(ViewMatchers.withId(R.id.icon))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.image))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Assert.assertTrue(nativeActivity.didLogImpression)
    }

    /*
  * Test for ONE_PX vs Default Native Impression firing method.
  * ONE_PX is preferred over Default Native Impression.
  * */
    @Test
    fun nativeAdDefaultImpressionTestWhenOnePxEnabled() {
        XandrAd.init(10094, null, false, null)
        Assert.assertFalse(nativeActivity.didLogImpression)
        nativeActivity.shouldDisplay = false
        nativeActivity.triggerAdLoad("17982237", creativeId = 182426521)
        Assert.assertFalse(nativeActivity.didLogImpression)
        Handler(Looper.getMainLooper()).post({
            nativeActivity.mainLayout.visibility = View.GONE
        })
        nativeActivity.attachNative()
        Thread.sleep(1000)
        Assert.assertFalse(nativeActivity.didLogImpression)
        Handler(Looper.getMainLooper()).post({
            nativeActivity.mainLayout.visibility = View.VISIBLE
        })
        Thread.sleep(1000)
        Espresso.onView(ViewMatchers.withId(R.id.title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.description))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(5000)
        Espresso.onView(ViewMatchers.withId(R.id.icon))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.image))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Assert.assertTrue(nativeActivity.didLogImpression)
    }

    /*
  * Test for ONE_PX vs Default Native Impression firing method.
  * ONE_PX is preferred over Default Native Impression.
  * */
    @Test
    fun nativeAdDefaultImpressionTestWhenOnePxEnabledWithAttachedView() {
        XandrAd.init(10094, null, false, null)
        Assert.assertFalse(nativeActivity.didLogImpression)
        nativeActivity.shouldDisplay = true
        nativeActivity.triggerAdLoad("17982237", creativeId = 182426521)
        Assert.assertFalse(nativeActivity.didLogImpression)
        Thread.sleep(1000)
        Espresso.onView(ViewMatchers.withId(R.id.title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.description))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Assert.assertTrue(nativeActivity.didLogImpression)
        Thread.sleep(5000)
        Espresso.onView(ViewMatchers.withId(R.id.icon))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.image))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}
