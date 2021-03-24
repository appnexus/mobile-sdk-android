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

package appnexus.com.trackertestapp.tracker.banner

import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.net.http.SslError
import android.os.Handler
import android.os.Looper
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import appnexus.com.trackertestapp.BannerActivity
import appnexus.com.trackertestapp.MockDispatcher
import appnexus.com.trackertestapp.R
import appnexus.com.trackertestapp.TestResponsesUT
import appnexus.com.trackertestapp.util.Util
import com.appnexus.opensdk.SDKSettings
import com.appnexus.opensdk.ut.UTConstants
import com.microsoft.appcenter.espresso.Factory
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.Matchers.not
import org.junit.*
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class BannerAdTrackerTest {
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
        mockDispatcher.adType = "BannerAd"
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
        SDKSettings.setCountImpressionOn1pxRendering(false)
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
     testBannerImpressionTrackerTestAdDefault: To test the impression tracker is fired by the Banner Ad.
     */
    @Test
    fun testBannerImpressionTrackerTestAdDefault() {

        bannerActivity.triggerAdLoad("14847003", 300, 250, creativeId = 156075267)

        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(1)))
        Espresso.onView(ViewMatchers.withId(bannerActivity.banner_id))
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
     testBannerImpressionTrackerTestAdCountOnAdLoad: To test the impression tracker is fired by the Banner Ad.
     countImpressionOnAdLoad = true
     */
    @Test
    fun testBannerImpressionTrackerTestAdCountOnAdLoad() {

        bannerActivity.shouldDisplay = false
        bannerActivity.triggerAdLoad(
            "14847003",
            300,
            250,
            creativeId = 156075267,
            countImpressionOnAdLoad = true
        )

        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(1)))
        Espresso.onView(ViewMatchers.withId(bannerActivity.banner_id))
            .check(ViewAssertions.matches(not(ViewMatchers.isDisplayed())))

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
    testBannerImpressionTrackerTestAdLazyLoad: To test the impression tracker is fired by the Banner Ad.
    enableLazyLoad = true
    */
    @Test
    fun testBannerImpressionTrackerTestAdLazyLoad() {

        bannerActivity.shouldDisplay = false
        bannerActivity.triggerAdLoad("14847003", 300, 250, creativeId = 156075267, lazyLoad = true)

        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(0)))

        var c = 0
        for (request in mockDispatcher.arrRequests) {
            if (request.startsWith("GET //it?")) {
                c++
                break
            }
        }

        Assert.assertTrue(c == 0)

        triggerLazyLoad()

        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(1)))
        Espresso.onView(ViewMatchers.withId(bannerActivity.banner_id))
            .check(ViewAssertions.matches(not(ViewMatchers.isDisplayed())))

        Thread.sleep(TestResponsesUT.DELAY)
        println("DISPATCH: LIST: " + mockDispatcher.arrRequests)

        for (request in mockDispatcher.arrRequests) {
            if (request.startsWith("GET //it?")) {
                c++
                break
            }
        }

        Assert.assertTrue(c > 0)
    }

    /*
     testBannerImpressionTrackerTestOnePx: To test the impression tracker is fired by the Banner Ad.
     SDKSettings.setCountImpressionOn1pxRendering(true)
     */
    @Test
    fun testBannerImpressionTrackerTestOnePx() {

        bannerActivity.shouldDisplay = false
        SDKSettings.setCountImpressionOn1pxRendering(true)
        bannerActivity.triggerAdLoad("14847003", 300, 250, creativeId = 156075267)

        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(1)))
        Espresso.onView(ViewMatchers.withId(bannerActivity.banner_id))
            .check(ViewAssertions.matches(not(ViewMatchers.isDisplayed())))

        Thread.sleep(TestResponsesUT.DELAY)
        println("DISPATCH: LIST: " + mockDispatcher.arrRequests)

        var c = 0
        for (request in mockDispatcher.arrRequests) {
            if (request.startsWith("GET //it?")) {
                c++
                break
            }
        }

        Assert.assertTrue(c == 0)

        setVisibility(View.VISIBLE)

        Thread.sleep(TestResponsesUT.DELAY)
        println("DISPATCH: LIST: " + mockDispatcher.arrRequests)

        for (request in mockDispatcher.arrRequests) {
            if (request.startsWith("GET //it?")) {
                c++
                break
            }
        }

        Assert.assertTrue(c > 0)
    }

    /*
    testBannerImpressionTrackerTestAdCountOnAdLoadPreferredOverOnePx: To test the impression tracker is fired by the Banner Ad.
    countImpressionOnAdLoad = true
    SDKSettings.setCountImpressionOn1pxRendering(true)
    */
    @Test
    fun testBannerImpressionTrackerTestAdCountOnAdLoadPreferredOverOnePx() {

        SDKSettings.setCountImpressionOn1pxRendering(true)
        bannerActivity.shouldDisplay = false
        bannerActivity.triggerAdLoad(
            "14847003",
            300,
            250,
            creativeId = 156075267,
            countImpressionOnAdLoad = true
        )

        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(1)))
        Espresso.onView(ViewMatchers.withId(bannerActivity.banner_id))
            .check(ViewAssertions.matches(not(ViewMatchers.isDisplayed())))

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
    testBannerImpressionTrackerTestAdCountOnAdLoadPreferredOverLazyLoad: To test the impression tracker is fired by the Banner Ad.
    countImpressionOnAdLoad = true
    enableLazyLoad = true
    */
    @Test
    fun testBannerImpressionTrackerTestAdCountOnAdLoadPreferredOverLazyLoad() {

        bannerActivity.shouldDisplay = false
        bannerActivity.triggerAdLoad(
            "14847003",
            300,
            250,
            creativeId = 156075267,
            countImpressionOnAdLoad = true,
            lazyLoad = true
        )

        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(0)))

        var c = 0
        for (request in mockDispatcher.arrRequests) {
            if (request.startsWith("GET //it?")) {
                c++
                break
            }
        }

        Assert.assertTrue(c == 0)

        triggerLazyLoad()

        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(1)))
        Espresso.onView(ViewMatchers.withId(bannerActivity.banner_id))
            .check(ViewAssertions.matches(not(ViewMatchers.isDisplayed())))

        Thread.sleep(TestResponsesUT.DELAY)
        println("DISPATCH: LIST: " + mockDispatcher.arrRequests)

        for (request in mockDispatcher.arrRequests) {
            if (request.startsWith("GET //it?")) {
                c++
                break
            }
        }

        Assert.assertTrue(c > 0)
    }

    /*
    testBannerImpressionTrackerTestOnePxPreferredOverLazyLoad: To test the impression tracker is fired by the Banner Ad.
    enableLazyLoad = true
    SDKSettings.setCountImpressionOn1pxRendering(true)
    */
    @Test
    fun testBannerImpressionTrackerTestOnePxPreferredOverLazyLoad() {

        bannerActivity.shouldDisplay = false
        SDKSettings.setCountImpressionOn1pxRendering(true)
        bannerActivity.triggerAdLoad("14847003", 300, 250, creativeId = 156075267, lazyLoad = true)

        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(0)))

        var c = 0
        for (request in mockDispatcher.arrRequests) {
            if (request.startsWith("GET //it?")) {
                c++
                break
            }
        }

        Assert.assertTrue(c == 0)

        triggerLazyLoad()

        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(1)))
        Espresso.onView(ViewMatchers.withId(bannerActivity.banner_id))
            .check(ViewAssertions.matches(not(ViewMatchers.isDisplayed())))

        Thread.sleep(TestResponsesUT.DELAY)
        println("DISPATCH: LIST: " + mockDispatcher.arrRequests)

        for (request in mockDispatcher.arrRequests) {
            if (request.startsWith("GET //it?")) {
                c++
                break
            }
        }

        Assert.assertTrue(c == 0)

        setVisibility(View.VISIBLE)

        Thread.sleep(TestResponsesUT.DELAY)
        println("DISPATCH: LIST: " + mockDispatcher.arrRequests)

        for (request in mockDispatcher.arrRequests) {
            if (request.startsWith("GET //it?")) {
                c++
                break
            }
        }

        Assert.assertTrue(c > 0)
    }

    /*
    testBannerImpressionTrackerTestAdCountOnAdLoadPreferredOverAllOtherFlags: To test the impression tracker is fired by the Banner Ad.
    SDKSettings.setCountImpressionOn1pxRendering(true)
    enableLazyLoad = true
    countImpressionOnAdLoad = true
    */
    @Test
    fun testBannerImpressionTrackerTestAdCountOnAdLoadPreferredOverAllOtherFlags() {

        bannerActivity.shouldDisplay = false
        SDKSettings.setCountImpressionOn1pxRendering(true)
        bannerActivity.triggerAdLoad(
            "14847003",
            300,
            250,
            creativeId = 156075267,
            countImpressionOnAdLoad = true,
            lazyLoad = true
        )

        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(0)))

        var c = 0
        for (request in mockDispatcher.arrRequests) {
            if (request.startsWith("GET //it?")) {
                c++
                break
            }
        }

        Assert.assertTrue(c == 0)

        triggerLazyLoad()

        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(1)))
        Espresso.onView(ViewMatchers.withId(bannerActivity.banner_id))
            .check(ViewAssertions.matches(not(ViewMatchers.isDisplayed())))

        Thread.sleep(TestResponsesUT.DELAY)
        println("DISPATCH: LIST: " + mockDispatcher.arrRequests)

        for (request in mockDispatcher.arrRequests) {
            if (request.startsWith("GET //it?")) {
                c++
                break
            }
        }

        Assert.assertTrue(c > 0)
    }

    /*
     testBannerClickTrackerTestAd: To test the click tracker is fired by the Banner Ad.
     */
    @Test
    fun testBannerClickTrackerTestAd() {

        bannerActivity.triggerAdLoad("14847003", 300, 250, creativeId = 156075267)

        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(1)))
        Espresso.onView(ViewMatchers.withId(bannerActivity.banner_id))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        for (i in 0..bannerActivity.banner.childCount - 1) {
            var child = bannerActivity.banner.getChildAt(i)
            if (child is WebView) {
                Handler(Looper.getMainLooper()).post {
                    var webviewClient = child.webViewClient
                    child.webViewClient = object : WebViewClient() {

                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            url: String?
                        ): Boolean {
                            mockDispatcher.arrRequests.add(Uri.decode(url).toString())
                            println("DISPATCH REQUEST: " + url)
                            return webviewClient.shouldOverrideUrlLoading(view, url)
                        }

                        override fun shouldInterceptRequest(
                            view: WebView?,
                            url: String?
                        ): WebResourceResponse? {
                            mockDispatcher.arrRequests.add(Uri.decode(url).toString())
                            println("DISPATCH REQUEST: " + url)
                            return webviewClient.shouldInterceptRequest(view, url)
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            webviewClient.onPageFinished(view, url)
                        }

                        override fun onReceivedError(
                            view: WebView?,
                            errorCode: Int,
                            description: String?,
                            failingUrl: String?
                        ) {
                            webviewClient.onReceivedError(view, errorCode, description, failingUrl)
                        }

                        override fun onReceivedSslError(
                            view: WebView?,
                            handler: SslErrorHandler?,
                            error: SslError?
                        ) {
                            webviewClient.onReceivedSslError(view, handler, error)
                        }

                        override fun onLoadResource(view: WebView?, url: String?) {
                            webviewClient.onLoadResource(view, url)
                        }
                    }
                }
            }
        }

        Thread.sleep(TestResponsesUT.DELAY)
        Espresso.onView(ViewMatchers.withId(bannerActivity.banner_id)).perform(ViewActions.click())


        println("DISPATCH: LIST: " + mockDispatcher.arrRequests)

        var c = 0
        for (request in mockDispatcher.arrRequests) {
            if (request.contains(UTConstants.REQUEST_BASE_URL_UT + "/click")) {
                c++
                break
            }
        }
        Assert.assertTrue(c > 0)
    }

    private fun setVisibility(visibility: Int) {
        Handler(Looper.getMainLooper()).post({
            bannerActivity.banner.visibility = visibility
        })
    }

    private fun triggerLazyLoad() {
        Handler(Looper.getMainLooper()).post({
            bannerActivity.loadLazyAd()
        })
    }

}
