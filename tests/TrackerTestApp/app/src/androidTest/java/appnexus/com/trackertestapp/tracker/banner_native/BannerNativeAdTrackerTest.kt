/*
 *    Copyright 2021 APPNEXUS INC
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

package appnexus.com.trackertestapp.tracker.banner_native

import android.content.Intent
import android.content.res.Resources
import android.os.Handler
import android.os.Looper
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import appnexus.com.trackertestapp.BannerActivity
import appnexus.com.trackertestapp.MockDispatcher
import appnexus.com.trackertestapp.R
import appnexus.com.trackertestapp.TestResponsesUT
import appnexus.com.trackertestapp.util.Util
import com.appnexus.opensdk.ut.UTConstants
import com.microsoft.appcenter.espresso.Factory
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class BannerNativeAdTrackerTest {
    val Int.dp: Int
        get() = (this / Resources.getSystem().displayMetrics.density).toInt()
    val Int.px: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()

    @get:Rule
    var reportHelper = Factory.getReportHelper()

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(BannerActivity::class.java, true, false)

    lateinit var bannerActivity: BannerActivity

    lateinit var mockWebServer: MockWebServer

    lateinit var mockDispatcher: MockDispatcher

    @Before
    fun setup() {
        IdlingPolicies.setMasterPolicyTimeout(1, TimeUnit.MINUTES)
        IdlingPolicies.setIdlingResourceTimeout(1, TimeUnit.MINUTES)
        var intent = Intent()
        mActivityTestRule.launchActivity(intent)
        bannerActivity = mActivityTestRule.activity
        Util.getWifiIp(bannerActivity)
        Thread.sleep(TestResponsesUT.DELAY_IP)
        val ip = Util.getMobileIPAddress()
        mockWebServer = MockWebServer()
        mockDispatcher = MockDispatcher()
        mockDispatcher.adType = "NativeAd"
        mockWebServer.start(ip, 8080)
        mockWebServer.setDispatcher(mockDispatcher)

        val url: HttpUrl = mockWebServer.url("/")
        UTConstants.REQUEST_BASE_URL_UT = url.toString()
        println("URL: " + UTConstants.REQUEST_BASE_URL_UT)
        TestResponsesUT.setTestURL(url.toString())
        IdlingRegistry.getInstance().register(bannerActivity.idlingResource)

    }

    @After
    fun destroy() {
        try {
            Handler(Looper.getMainLooper()).post {
                bannerActivity.removeBannerAd()
            }
            Thread.sleep(TestResponsesUT.DELAY_IP)
            mockWebServer.shutdown()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        IdlingRegistry.getInstance().unregister(bannerActivity.idlingResource)
        reportHelper.label("Stopping App")
    }

    /*
     testBannerNativeImpressionTrackerTestAd: To test the impression tracker is fired by the Banner Native Ad.
     */
    @Test
    fun testBannerNativeImpressionTrackerTestAd() {
        bannerActivity.triggerAdLoad("20331545", allowNativeDemand = true)
        Espresso.onView(ViewMatchers.withId(R.id.title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.description))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.icon))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.image))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Thread.sleep(TestResponsesUT.DELAY)
        println("DISPATCH: LIST: " + mockDispatcher.arrRequests)

        var c = 0
        for (request in mockDispatcher.arrRequests) {
            if (request.startsWith("GET //it?")) {
                c++
                break
            }
        }

        Assert.assertTrue(c > 0)
    }

    /*
     testBannerNativeClickTrackerTestAd: To test the click tracker is fired by the Banner Native Ad.
     */
    @Test
    fun testBannerNativeClickTrackerTestAd() {
        bannerActivity.triggerAdLoad("20331545", allowNativeDemand = true)
        Espresso.onView(ViewMatchers.withId(R.id.title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.description))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.icon))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.image))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.main_native)).perform(ViewActions.click())
        Thread.sleep(TestResponsesUT.DELAY)
        println("DISPATCH: LIST: " + mockDispatcher.arrRequests)

        var c = 0
        for (request in mockDispatcher.arrRequests) {
            if (request.startsWith("GET //click?")) {
                c++
                break
            }
        }
        Assert.assertTrue(c > 0)
    }

}
