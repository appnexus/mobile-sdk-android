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

package appnexus.com.trackertestapp.viewability.banner

import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.net.http.SslError
import android.os.Handler
import android.os.Looper
import android.webkit.SslErrorHandler
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
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
class BannerAdViewabilityTrackerTest {
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

    lateinit var mockWebServer: MockWebServer

    lateinit var mockDispatcher: MockDispatcher

    var omidSupported = false
    var sessionStart = false
    var environmentApp = false
    var adSessionTypeHTML = false
    var supportsClid = false
    var mediaTypeDisplay = false
    var partnerNameAppnexus = false
    var osAndroid = false
    var impressionTypeViewable = false
    var creativeTypeHtmlDisplay = false
    var omidJsInfo = false
    var appLibraryVersion = false
    var accessMode = false
    var percentageInView0 = false
    var percentageInView100 = false
    var geometryChange = false
    var versionEvent = false
    var sessionFinish = false


    @Before
    fun setup() {
        IdlingPolicies.setMasterPolicyTimeout(1, TimeUnit.MINUTES)
        IdlingPolicies.setIdlingResourceTimeout(1, TimeUnit.MINUTES)
        var intent = Intent()
        mActivityTestRule.launchActivity(intent)
        bannerActivity = mActivityTestRule.activity
        Util.getWifiIp(bannerActivity)
        Thread.sleep(TestResponsesUT.DELAY)
        val ip = Util.getMobileIPAddress()
        mockWebServer = MockWebServer()
        mockDispatcher = MockDispatcher()
        mockDispatcher.adType = "BannerAdOMID"
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

        omidSupported = false
        sessionStart = false
        environmentApp = false
        adSessionTypeHTML = false
        supportsClid = false
        mediaTypeDisplay = false
        partnerNameAppnexus = false
        osAndroid = false
        impressionTypeViewable = false
        creativeTypeHtmlDisplay = false
        omidJsInfo = false
        appLibraryVersion = false
        accessMode = false
        percentageInView0 = false
        percentageInView100 = false
        geometryChange = false
        versionEvent = false
        sessionFinish = false
    }

    fun setupAndLoadBanner(){
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
                break
            }
        }
    }

    /*
    testBannerOMIDEventSupportedIsYes: To test the OMID is supported tracker is fired by the Banner Ad.
    */
    @Test
    fun testBannerOMIDEventSupportedIsYes() {
        setupAndLoadBanner()
        Thread.sleep(TestResponsesUT.DELAY)
        println("DISPATCH: LIST: " + mockDispatcher.arrRequests)
        for (request in mockDispatcher.arrRequests) {
            if (request.contains("OmidSupported[true]")) {
                omidSupported = true
                break
            }
        }
        Assert.assertTrue(omidSupported)
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    /*
     testBannerOMIDEventSessionStart: To test the OMID is Session Start & few related events are fired by the Banner Ad.
     */
    @Test
    fun testBannerOMIDEventSessionStart() {
        setupAndLoadBanner()
        Thread.sleep(TestResponsesUT.DELAY)
        println("DISPATCH: LIST: " + mockDispatcher.arrRequests)
        for (request in mockDispatcher.arrRequests) {
            if (request.contains("\"type\":\"sessionStart\"")) {
                sessionStart = true
            }
            if (request.contains("\"environment\":\"app\"")) {
                environmentApp = true
            }
            if (request.contains("\"adSessionType\":\"html\"")) {
                adSessionTypeHTML = true
            }
            if (request.contains("\"supports\":[\"clid\",\"vlid\"]")) {
                supportsClid = true
            }
            if (request.contains("\"mediaType\":\"display\"")) {
                mediaTypeDisplay = true
            }
            if (request.contains("\"partnerName\":\"Appnexus\"")) {
                partnerNameAppnexus = true
            }
            if (request.contains("\"os\":\"Android\"")) {
                osAndroid = true
            }
            if (request.contains("\"impressionType\":\"viewable\"")) {
                impressionTypeViewable = true
            }
            if (request.contains("\"creativeType\":\"htmlDisplay\"")) {
                creativeTypeHtmlDisplay = true
            }
            if (request.contains("\"omidJsInfo\":{\"omidImplementer\":\"omsdk\",\"serviceVersion\":\"1.3.7-iab2228\"}")) {
                omidJsInfo = true
            }
            if (request.contains("\"app\":{\"libraryVersion\":\"1.3.7-Appnexus\",\"appId\":\"appnexus.com.trackertestapp\"}")) {
                appLibraryVersion = true
            }
            if (request.contains("\"accessMode\":\"limited\"")) {
                accessMode = true
            }
        }
        Assert.assertTrue(sessionStart)
        Assert.assertTrue(environmentApp)
        Assert.assertTrue(adSessionTypeHTML)
        Assert.assertTrue(supportsClid)
        Assert.assertTrue(mediaTypeDisplay)
        Assert.assertTrue(partnerNameAppnexus)
        Assert.assertTrue(osAndroid)
        Assert.assertTrue(impressionTypeViewable)
        Assert.assertTrue(creativeTypeHtmlDisplay)
        Assert.assertTrue(omidJsInfo)
        Assert.assertTrue(appLibraryVersion)
        Assert.assertTrue(accessMode)

        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    /*
     testBannerOMIDEventPercentageViewableZero: To test the OMID is 0% Viewable
     */
    @Test
    fun testBannerOMIDEventPercentageViewableZero() {
        setupAndLoadBanner()
        Thread.sleep(TestResponsesUT.DELAY)
        println("DISPATCH: LIST: " + mockDispatcher.arrRequests)

        for (request in mockDispatcher.arrRequests) {
            if (request.contains("\"percentageInView\":0")) {
                percentageInView0 = true
            }
            if (request.contains("\"type\":\"geometryChange\"")) {
                geometryChange = true
            }
        }

        Assert.assertTrue(percentageInView0)
        Assert.assertTrue(geometryChange)

        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    /*
     testBannerOMIDEventViewable100Percentage: To test the OMID is 100% Viewable
     */
    @Test
    fun testBannerOMIDEventPercentageViewable100() {
        setupAndLoadBanner()
        Thread.sleep(TestResponsesUT.DELAY)
        println("DISPATCH: LIST: " + mockDispatcher.arrRequests)

        for (request in mockDispatcher.arrRequests) {
            if (request.contains("\"percentageInView\":100")) {
                percentageInView100 = true
            }
            if (request.contains("\"type\":\"geometryChange\"")) {
                geometryChange = true
            }
        }

        Assert.assertTrue(percentageInView100)
        Assert.assertTrue(geometryChange)

        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    /*
     testBannerOMIDVersionEvent: To verify the OMID version
     */
    @Test
    fun testBannerOMIDVersionEvent() {
        setupAndLoadBanner()
        Thread.sleep(TestResponsesUT.DELAY)
        println("DISPATCH: LIST: " + mockDispatcher.arrRequests)

        for (request in mockDispatcher.arrRequests) {
            if (request.contains("\"libraryVersion\":\"1.3.7-Appnexus\"")) {
                versionEvent = true
                break
            }
        }

        Assert.assertTrue(versionEvent)
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    /*
     testBannerOMIDTypeImpression: To test the OMID Media Type and impressionType
     */
    @Test
    fun testBannerOMIDTypeImpression() {
        setupAndLoadBanner()
        Thread.sleep(TestResponsesUT.DELAY)
        println("DISPATCH: LIST: " + mockDispatcher.arrRequests)

        for (request in mockDispatcher.arrRequests) {
            if (request.contains("\"impressionType\":\"viewable\"")) {
                impressionTypeViewable = true
            }
            if (request.contains("\"mediaType\":\"display\"")) {
                mediaTypeDisplay = true
            }
            if (request.contains("\"creativeType\":\"htmlDisplay\"")) {
                creativeTypeHtmlDisplay = true
            }
        }

        Assert.assertTrue(impressionTypeViewable)
        Assert.assertTrue(mediaTypeDisplay)
        Assert.assertTrue(creativeTypeHtmlDisplay)
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    /*
     testBannerOMIDSessionFinish: To verify Session finish getting fired
     */
    @Test
    fun testBannerOMIDSessionFinish() {
        setupAndLoadBanner()
        Thread.sleep(TestResponsesUT.DELAY)
        println("DISPATCH: LIST: " + mockDispatcher.arrRequests)
        runOnUiThread {
            bannerActivity.removeBannerAd()
        }
        Thread.sleep(TestResponsesUT.DELAY)

        for (request in mockDispatcher.arrRequests) {
            if (request.contains("\"type\":\"sessionFinish\"")) {
                sessionFinish = true
                break
            }
        }

        Assert.assertTrue(sessionFinish)
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

}
