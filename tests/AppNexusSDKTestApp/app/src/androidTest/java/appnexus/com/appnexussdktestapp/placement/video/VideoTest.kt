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

package appnexus.com.appnexussdktestapp.placement.video


import android.content.Intent
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.web.assertion.WebViewAssertions
import androidx.test.espresso.web.matcher.DomMatchers
import androidx.test.espresso.web.sugar.Web.onWebView
import androidx.test.espresso.web.webdriver.DriverAtoms.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import appnexus.com.appnexussdktestapp.R
import appnexus.com.appnexussdktestapp.VideoActivity
import com.appnexus.opensdk.XandrAd
import com.microsoft.appcenter.espresso.Factory
import org.junit.*
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@LargeTest
@RunWith(AndroidJUnit4::class)
class VideoTest {

    @get:Rule
    var reportHelper = Factory.getReportHelper()

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(VideoActivity::class.java, false, false)

    lateinit var videoActivity: VideoActivity

    @Before
    fun setup() {
        XandrAd.init(123, null, false, null)
        IdlingPolicies.setMasterPolicyTimeout(1, TimeUnit.MINUTES)
        IdlingPolicies.setIdlingResourceTimeout(1, TimeUnit.MINUTES)
        var intent = Intent()
        mActivityTestRule.launchActivity(intent)
        videoActivity = mActivityTestRule.activity
        IdlingRegistry.getInstance().register(videoActivity.idlingResource)
    }

    @After
    fun destroy() {
        IdlingRegistry.getInstance().unregister(videoActivity.idlingResource)
        reportHelper.label("Stopping App")
    }

    /*
    * Sanity Test for the Banner Ad of size 320x50
    * */
    @Test
    fun videoLoadPerformanceTest() {

        videoActivity.triggerAdLoad("17058950")

        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Assert.assertTrue(
            "Load time performance failure ${videoActivity.getTime()}",
            videoActivity.getTime() <= 2000
        )

        Thread.sleep(500)

        videoActivity.triggerAdLoad("17058950")

        Espresso.onView(ViewMatchers.withId(R.id.linearLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Assert.assertTrue(
            "Load time performance failure ${videoActivity.getTime()}",
            videoActivity.getTime() < 2000
        )
    }

    /*
    * Sanity Test for the Video Ad (Instream Video)
    * */
    @Test
    fun videoTest() {

        videoActivity.triggerAdLoad("17058950")

        onView(withId(R.id.play_button)).check(matches(isDisplayed()))

        onView(withId(R.id.play_button)).perform(ViewActions.click())

//        onWebView().forceJavascriptEnabled()
//        onWebView().inWindow(selectFrameByIndex(1))
//            .check(WebViewAssertions.webContent(DomMatchers.hasElementWithId("ad_indicator_text")))
//        onWebView().inWindow(selectFrameByIndex(1))
//            .check(WebViewAssertions.webContent(DomMatchers.hasElementWithId("ad_skip_text")))
//        onWebView().inWindow(selectFrameByIndex(1))
//            .check(WebViewAssertions.webContent(DomMatchers.hasElementWithId("skip_button")))
//        For functional test -> Clickthrough
//        onWebView().inWindow(selectFrameByIndex(1)).withElement(findElement(Locator.ID, "ad_indicator_text"))
//            .perform(webClick())

    }

    /*
    * Sanity Test for the VPAID Video Ad (Instream Video)
    * */
    @Test
    fun videoVPAIDTest() {

        videoActivity.triggerAdLoad("17058950")

        onView(withId(R.id.play_button)).check(matches(isDisplayed()))

        onView(withId(R.id.play_button)).perform(ViewActions.click())

        onWebView().forceJavascriptEnabled()
        onWebView().inWindow(selectFrameByIndex(1))
            .check(WebViewAssertions.webContent(DomMatchers.hasElementWithId("ad_indicator_text")))
        onWebView().inWindow(selectFrameByIndex(1))
            .check(WebViewAssertions.webContent(DomMatchers.hasElementWithId("ad_skip_text")))
        onWebView().inWindow(selectFrameByIndex(1))
            .check(WebViewAssertions.webContent(DomMatchers.hasElementWithId("skip_button")))

        //The timer divs and span are based on class and does not have any id, thus it can't be checked

//        For functional test -> Clickthrough
//        onWebView().inWindow(selectFrameByIndex(1)).withElement(findElement(Locator.ID, "ad_indicator_text"))
//            .perform(webClick())

    }
    
    // BG Testing

    /*
    * Sanity Test for the Video Ad (Instream Video)
    * */
    @Test
    fun videoBGTest() {

        videoActivity.triggerAdLoad("17058950", bgTask = true)

        onView(withId(R.id.play_button)).check(matches(isDisplayed()))

        onView(withId(R.id.play_button)).perform(ViewActions.click())

//        onWebView().forceJavascriptEnabled()
//        onWebView().inWindow(selectFrameByIndex(1))
//            .check(WebViewAssertions.webContent(DomMatchers.hasElementWithId("ad_indicator_text")))
//        onWebView().inWindow(selectFrameByIndex(1))
//            .check(WebViewAssertions.webContent(DomMatchers.hasElementWithId("ad_skip_text")))
//        onWebView().inWindow(selectFrameByIndex(1))
//            .check(WebViewAssertions.webContent(DomMatchers.hasElementWithId("skip_button")))
//        For functional test -> Clickthrough
//        onWebView().inWindow(selectFrameByIndex(1)).withElement(findElement(Locator.ID, "ad_indicator_text"))
//            .perform(webClick())

    }

    /*
    * Sanity Test for the VPAID Video Ad (Instream Video)
    * */
    @Test
    fun videoVPAIDBGTest() {

        videoActivity.triggerAdLoad("17058950", bgTask = true)

        onView(withId(R.id.play_button)).check(matches(isDisplayed()))

        onView(withId(R.id.play_button)).perform(ViewActions.click())

        onWebView().forceJavascriptEnabled()
        onWebView().inWindow(selectFrameByIndex(1))
            .check(WebViewAssertions.webContent(DomMatchers.hasElementWithId("ad_indicator_text")))
        onWebView().inWindow(selectFrameByIndex(1))
            .check(WebViewAssertions.webContent(DomMatchers.hasElementWithId("ad_skip_text")))
        onWebView().inWindow(selectFrameByIndex(1))
            .check(WebViewAssertions.webContent(DomMatchers.hasElementWithId("skip_button")))

        //The timer divs and span are based on class and does not have any id, thus it can't be checked

//        For functional test -> Clickthrough
//        onWebView().inWindow(selectFrameByIndex(1)).withElement(findElement(Locator.ID, "ad_indicator_text"))
//            .perform(webClick())

    }

}
