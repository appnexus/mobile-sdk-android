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

package appnexus.com.appnexussdktestapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.test.espresso.idling.CountingIdlingResource
import com.appnexus.opensdk.*
import com.appnexus.opensdk.utils.Clog
import com.appnexus.opensdk.utils.ClogListener

class BannerLazyLoadActivity : AppCompatActivity(), AppEventListener {

    val banner_id: Int = 1234
    lateinit var banner: BannerAdView
    var onAdImpression = false
    var msg = ""
    var logListener = LogListener()
    var idlingResource: CountingIdlingResource = CountingIdlingResource("Banner Load Count", true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banner_lazy_load)
        Clog.registerListener(logListener)
        banner = BannerAdView(this)
        banner.appEventListener = this
        banner.id = banner_id
        // This is your AppNexus placement ID.
        banner.placementID = "17058950"
        // Turning this on so we always get an ad during testing.
        banner.shouldServePSAs = false
        // By default ad clicks open in an in-app WebView.
        banner.clickThroughAction = ANClickThroughAction.OPEN_SDK_BROWSER
        // Get a 300x50 ad.
        banner.setAdSize(300, 250)
        // Resizes the container size to fit the banner ad
        banner.resizeAdToFitContainer = true
        // Set up a listener on this ad view that logs events.
        val adListener: AdListener = object : AdListener {
            override fun onAdRequestFailed(bav: AdView, errorCode: ResultCode) {
                if (errorCode == null) {
                    Clog.v("SIMPLEBANNER", "Call to loadAd failed")
                } else {
                    Clog.v("SIMPLEBANNER", "Ad request failed: $errorCode")
                }
                if (!idlingResource.isIdleNow) {
                    idlingResource.decrement()
                }
            }

            override fun onAdLoaded(bav: AdView) {
                msg = "onAdLoaded"
                toast()
                Clog.v("SIMPLEBANNER", "The Ad Loaded!")
                Handler().postDelayed({ showAd() }, 5000)
                if (!idlingResource.isIdleNow) {
                    idlingResource.decrement()
                }
            }

            override fun onAdLoaded(nativeAdResponse: NativeAdResponse) {
                Clog.v("SIMPLEBANNER", "Ad onAdLoaded NativeAdResponse")
            }

            override fun onAdExpanded(bav: AdView) {
                Clog.v("SIMPLEBANNER", "Ad expanded")
            }

            override fun onAdCollapsed(bav: AdView) {
                Clog.v("SIMPLEBANNER", "Ad collapsed")
            }

            override fun onAdClicked(bav: AdView) {
                Clog.v("SIMPLEBANNER", "Ad clicked; opening browser")
            }

            override fun onAdClicked(adView: AdView, clickUrl: String) {
                Clog.v("SIMPLEBANNER", "onAdClicked with click URL")
            }

            override fun onLazyAdLoaded(adView: AdView) {
                msg = "onLazyAdLoaded"
                toast()
                if (!idlingResource.isIdleNow) {
                    idlingResource.decrement()
                }
            }

            override fun onAdImpression(adView: AdView?) {
                msg = "onAdImpression"
                onAdImpression = true
                toast()
            }
        }
        banner.adListener = adListener
        banner.enableLazyLoad()
        load()
    }

    open fun showAd() {
        removeFromParent()
        val layout =
            findViewById<View>(R.id.main_content) as RelativeLayout
        val layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
        banner.layoutParams = layoutParams
        layout.addView(banner)
        msg += "Banner Added to the Screen."
        toast()
    }

    open fun removeFromParent() {
        if (banner.parent != null && banner!!.parent is ViewGroup) {
            (banner.parent as ViewGroup).removeView(banner)
            msg = "Banner removed From Parent, "
        }
    }

    open fun load() {
        // If auto-refresh is enabled (the default), a call to
        // `FrameLayout.addView()` followed directly by
        // `BannerAdView.loadAd()` will succeed.  However, if
        // auto-refresh is disabled, the call to
        // `BannerAdView.loadAd()` needs to be wrapped in a `Handler`
        // block to ensure that the banner ad view is in the view
        // hierarchy *before* the call to `loadAd()`.  Otherwise the
        // visibility check in `loadAd()` will fail, and no ad will be
        // shown.
        if (idlingResource.isIdleNow) {
            idlingResource.increment()
        }
        onAdImpression = false
        Handler().postDelayed({
            banner!!.loadAd()
            msg += " loadAd() triggered"
            toast()
        }, 0)
    }

    fun toggleLazyLoadAndReload(v: View?) {
        val tv = findViewById<View>(R.id.enableAndReload) as TextView
        banner.enableLazyLoad()
        if (banner.isLazyLoadEnabled) {
            tv.text = "Disable And Reload"
            msg = "Lazy Load Enabled"
        } else {
            tv.text = "Enable And Reload"
            msg = "Lazy Load Disabled"
        }
        load()
    }

    fun activateWebview(v: View?) {
        if (idlingResource.isIdleNow) {
            idlingResource.increment()
        }
        banner.loadLazyAd()
        msg = "Webview Activated"
        toast()
    }

    open fun toast() {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        Clog.e("LAZYLOAD", msg)
    }

    class LogListener : ClogListener() {

        var logMsg: String = ""

        override fun onReceiveMessage(
            level: ClogListener.LOG_LEVEL?,
            LogTag: String?,
            message: String?
        ) {
            if (LogTag.equals("LAZYLOAD", true)) {
                logMsg += message + "\n"
            }
        }

        override fun onReceiveMessage(
            level: ClogListener.LOG_LEVEL?,
            LogTag: String?,
            message: String?,
            tr: Throwable?
        ) {

        }

        override fun getLogLevel(): ClogListener.LOG_LEVEL {
            return LOG_LEVEL.E
        }
    }

    fun displayBanner(display: Boolean = true) {
        Handler(Looper.getMainLooper()).post({
            if (!display) {
                banner.visibility = View.GONE
            } else {
                banner.visibility = View.VISIBLE
            }
        })
    }

    override fun onAppEvent(adView: AdView?, name: String?, data: String?) {
        Clog.e("LAZYLOAD", name)
//        appEvent = name
    }

    override fun onDestroy() {
        if (banner != null) {
            banner.activityOnDestroy()
        }
        super.onDestroy()
    }
}