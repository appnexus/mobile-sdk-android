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

package appnexus.com.trackertestapp.tracker.interstitial

import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import androidx.test.runner.AndroidJUnit4
import appnexus.com.trackertestapp.InterstitialActivity
import appnexus.com.trackertestapp.MockDispatcher
import appnexus.com.trackertestapp.TestResponsesUT
import appnexus.com.trackertestapp.util.Util
import com.appnexus.opensdk.AdActivity
import com.appnexus.opensdk.ut.UTConstants
import com.microsoft.appcenter.espresso.Factory
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import org.junit.Assert.assertTrue
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
class InterstitialAdTrackerTest {

    @get:Rule
    var reportHelper = Factory.getReportHelper()

    @Rule
    @JvmField
    var mActivityTestRule = IntentsTestRule(InterstitialActivity::class.java, true, false)

    lateinit var interstitialActivity: InterstitialActivity

    lateinit var mockWebServer: MockWebServer

    lateinit var mockDispatcher: MockDispatcher

    @Before
    fun setup() {
        IdlingPolicies.setMasterPolicyTimeout(1, TimeUnit.MINUTES)
        IdlingPolicies.setIdlingResourceTimeout(1, TimeUnit.MINUTES)
        var intent = Intent()
        mActivityTestRule.launchActivity(intent)
        interstitialActivity = mActivityTestRule.activity
        Util.getWifiIp(interstitialActivity)
        Thread.sleep(TestResponsesUT.DELAY_IP)
        val ip = Util.getMobileIPAddress()
        mockWebServer = MockWebServer()
        mockDispatcher = MockDispatcher()
        mockDispatcher.adType = "BannerAd"
        mockWebServer.start(ip,8080)
        mockWebServer.setDispatcher(mockDispatcher)

        val url: HttpUrl = mockWebServer.url("/")
        UTConstants.REQUEST_BASE_URL_UT = url.toString()
        println("URL: " + UTConstants.REQUEST_BASE_URL_UT)
        TestResponsesUT.setTestURL(url.toString())
        IdlingRegistry.getInstance().register(interstitialActivity.idlingResource)
    }

    @After
    fun destroy() {
        try {
            mockWebServer.shutdown()
            Handler(Looper.getMainLooper()).post {
                interstitialActivity.removeInterstitialAd()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        IdlingRegistry.getInstance().unregister(interstitialActivity.idlingResource)
        reportHelper.label("Stopping App")
    }

    /*
     testInterstitialImpressionTrackerTestAd: To test the impression tracker is fired by the Interstitial Ad.
    */
    @Test
    fun testInterstitialImpressionTrackerTestAd() {
        interstitialActivity.triggerAdLoad("17058950", creativeId = 166843001)
        intended(IntentMatchers.hasComponent(AdActivity::class.java.name))
        val closeButtonId = android.R.id.closeButton
        Espresso.onView(ViewMatchers.withId(closeButtonId)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(TestResponsesUT.DELAY)
        println("DISPATCH: LIST: " + mockDispatcher.arrRequests)

        var c = 0
        for (request in mockDispatcher.arrRequests) {
            if (request.startsWith("GET //it?")) {
                c++
                break
            }
        }
        assertTrue(c > 0)
    }

//    /*
//     testInterstitialClickTrackerTestAd: To test the click tracker is fired by the Interstitial Ad.
//    */
//    @Test
//    fun testInterstitialClickTrackerTestAd() {
//
//        interstitialActivity.triggerAdLoad("17058950", creativeId = 166843001)
//        intended(IntentMatchers.hasComponent(AdActivity::class.java.name))
//        val closeButtonId = android.R.id.closeButton
//        Espresso.onView(ViewMatchers.withId(closeButtonId)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//
//        for (i in 0..interstitialActivity.interstitial.childCount - 1) {
//            var child = interstitialActivity.interstitial.getChildAt(i)
//            if (child is WebView) {
//                Handler(Looper.getMainLooper()).post {
//                    var webviewClient = child.webViewClient
//                    child.webViewClient = object : WebViewClient() {
//
//                        override fun shouldOverrideUrlLoading(
//                            view: WebView?,
//                            url: String?
//                        ): Boolean {
//                            mockDispatcher.arrRequests.add(Uri.decode(url).toString())
//                            println("DISPATCH REQUEST: " + url)
//                            return webviewClient.shouldOverrideUrlLoading(view, url)
//                        }
//
//                        override fun shouldInterceptRequest(
//                            view: WebView?,
//                            url: String?
//                        ): WebResourceResponse? {
//                            mockDispatcher.arrRequests.add(Uri.decode(url).toString())
//                            println("DISPATCH REQUEST: " + url)
//                            return webviewClient.shouldInterceptRequest(view, url)
//                        }
//
//                        override fun onPageFinished(view: WebView?, url: String?) {
//                            webviewClient.onPageFinished(view, url)
//                        }
//
//                        override fun onReceivedError(
//                            view: WebView?,
//                            errorCode: Int,
//                            description: String?,
//                            failingUrl: String?
//                        ) {
//                            webviewClient.onReceivedError(view, errorCode, description, failingUrl)
//                        }
//
//                        override fun onReceivedSslError(
//                            view: WebView?,
//                            handler: SslErrorHandler?,
//                            error: SslError?
//                        ) {
//                            webviewClient.onReceivedSslError(view, handler, error)
//                        }
//
//                        override fun onLoadResource(view: WebView?, url: String?) {
//                            webviewClient.onLoadResource(view, url)
//                        }
//                    }
//                }
//            }
//        }
//
//        Espresso.onView(ViewMatchers.withId(interstitialActivity.interstitial_id)).perform(ViewActions.click())
//        Thread.sleep(TestResponsesUT.DELAY)
//        println("DISPATCH: LIST: " + mockDispatcher.arrRequests)
//
//        var c = 0
//        for (request in mockDispatcher.arrRequests) {
//            if (request.contains(UTConstants.REQUEST_BASE_URL_UT + "/click")) {
//                c++
//                break
//            }
//        }
//        assertTrue(c > 0)
//    }
}
