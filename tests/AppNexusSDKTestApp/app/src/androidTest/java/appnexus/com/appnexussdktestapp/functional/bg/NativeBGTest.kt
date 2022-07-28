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

package appnexus.com.appnexussdktestapp.functional.bg

import android.content.Intent
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.runner.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import appnexus.com.appnexussdktestapp.NativeActivity
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
class NativeBGTest {

    @get:Rule
    var reportHelper = Factory.getReportHelper()

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(NativeActivity::class.java, false, false)

    lateinit var nativeActivity: NativeActivity

    @Before
    fun setup() {
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
    * Sanity Test for the Native Ad
    * */
    @Test
    fun nativeAdLoadTestBGTaskOnExecutor() {

        nativeActivity.triggerAdLoad("17058950", bgTask = true, useExecutor = true)
        Espresso.onView(ViewMatchers.withId(R.id.title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.description))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(5000)
        Espresso.onView(ViewMatchers.withId(R.id.icon))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.image))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    /*
    * Sanity Test for the Native Ad
    * */
    @Test
    fun nativeAdLoadTestBGTaskOnMainThread() {

        nativeActivity.triggerAdLoad("17058950",  bgTask = true)
        Espresso.onView(ViewMatchers.withId(R.id.title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.description))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(5000)
        Espresso.onView(ViewMatchers.withId(R.id.icon))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.image))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}
