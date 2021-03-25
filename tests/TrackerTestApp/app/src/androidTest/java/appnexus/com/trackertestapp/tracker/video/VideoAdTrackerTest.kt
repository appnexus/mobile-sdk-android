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

package appnexus.com.trackertestapp.tracker.video


import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.web.sugar.Web.onWebView
import androidx.test.espresso.web.webdriver.DriverAtoms.*
import androidx.test.espresso.web.webdriver.Locator
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import appnexus.com.trackertestapp.*
import appnexus.com.trackertestapp.util.Util
import com.appnexus.opensdk.ut.UTConstants
import com.microsoft.appcenter.espresso.Factory
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@LargeTest
@RunWith(AndroidJUnit4::class)
class VideoAdTrackerTest {

    @get:Rule
    var reportHelper = Factory.getReportHelper()

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(VideoActivity::class.java, true, false)

    lateinit var videoActivity: VideoActivity

    lateinit var mockWebServer: MockWebServer

    lateinit var mockDispatcher: MockDispatcher

    @Before
    fun setup() {
        IdlingPolicies.setMasterPolicyTimeout(1, TimeUnit.MINUTES)
        IdlingPolicies.setIdlingResourceTimeout(1, TimeUnit.MINUTES)
        var intent = Intent()
        mActivityTestRule.launchActivity(intent)
        videoActivity = mActivityTestRule.activity
        Util.getWifiIp(videoActivity)
        Thread.sleep(TestResponsesUT.DELAY_IP)
        val ip = Util.getMobileIPAddress()
        mockWebServer = MockWebServer()
        mockDispatcher = MockDispatcher()
        mockDispatcher.adType = "BannerVideoAd"
        mockWebServer.start(ip,8080)
        mockWebServer.setDispatcher(mockDispatcher)

        val url: HttpUrl = mockWebServer.url("/")
        UTConstants.REQUEST_BASE_URL_UT = url.toString()
        println("URL: " + UTConstants.REQUEST_BASE_URL_UT)
        TestResponsesUT.setTestURL(url.toString())
        IdlingRegistry.getInstance().register(videoActivity.idlingResource)
    }

    @After
    fun destroy() {
        try {
            Handler(Looper.getMainLooper()).post {
                videoActivity.removeVideoAd()
            }
            mockWebServer.shutdown()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        IdlingRegistry.getInstance().unregister(videoActivity.idlingResource)
        reportHelper.label("Stopping App")
    }

    /*
     testVideoImpressionTrackerTestAd: To test the impression tracker is fired by the Video Ad.
     */
    @Test
    fun testVideoImpressionTrackerTestAd() {
        videoActivity.triggerAdLoad("14757590", creativeId = 162035356)
        Espresso.onView(ViewMatchers.withId(R.id.play_button)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.play_button)).perform(ViewActions.click())

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
     testVideoClickTrackerTestAd: To test the click tracker is fired by the Video Ad.
     */
    @Test
    fun testVideoClickTrackerTestAd() {
        videoActivity.triggerAdLoad("14757590", creativeId = 162035356)
        Espresso.onView(ViewMatchers.withId(R.id.play_button)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.play_button)).perform(ViewActions.click())

        onWebView().forceJavascriptEnabled()
        onWebView().inWindow(selectFrameByIndex(2)).withElement(findElement(Locator.ID, "ad_indicator_text"))
            .perform(webClick())
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
