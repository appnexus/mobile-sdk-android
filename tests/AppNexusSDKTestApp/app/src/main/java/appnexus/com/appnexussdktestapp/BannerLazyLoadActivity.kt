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
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appnexus.opensdk.*
import com.appnexus.opensdk.utils.Clog

class BannerLazyLoadActivity : AppCompatActivity() {

    var bav: BannerAdView? = null
    var msg = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my)
        bav = BannerAdView(this)
        SDKSettings.useHttps(true)
        // This is your AppNexus placement ID.
        bav!!.placementID = "17058950"
        // Turning this on so we always get an ad during testing.
        bav!!.shouldServePSAs = false
        // By default ad clicks open in an in-app WebView.
        bav!!.clickThroughAction = ANClickThroughAction.OPEN_SDK_BROWSER
        // Get a 300x50 ad.
        bav!!.setAdSize(300, 250)
        // Resizes the container size to fit the banner ad
        bav!!.resizeAdToFitContainer = true
        // Set up a listener on this ad view that logs events.
        val adListener: AdListener = object : AdListener {
            override fun onAdRequestFailed(bav: AdView, errorCode: ResultCode) {
                if (errorCode == null) {
                    Clog.v("SIMPLEBANNER", "Call to loadAd failed")
                } else {
                    Clog.v("SIMPLEBANNER", "Ad request failed: $errorCode")
                }
            }

            override fun onAdLoaded(bav: AdView) {
                msg = "onAdLoaded"
                toast()
                Clog.v("SIMPLEBANNER", "The Ad Loaded!")
                Handler().postDelayed({ showAd() }, 5000)
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
                msg = "onAdLazyLoaded"
                toast()
            }
        }
        bav!!.adListener = adListener
        bav!!.enableWebviewLazyLoad(true)
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
        bav!!.layoutParams = layoutParams
        layout.addView(bav)
        msg += "Banner Added to the Screen."
        toast()
    }

    open fun removeFromParent() {
        if (bav!!.parent != null && bav!!.parent is ViewGroup) {
            (bav!!.parent as ViewGroup).removeView(bav)
            msg = "Banner removed From Parent, "
        }
    }

    open fun load() { // If auto-refresh is enabled (the default), a call to
// `FrameLayout.addView()` followed directly by
// `BannerAdView.loadAd()` will succeed.  However, if
// auto-refresh is disabled, the call to
// `BannerAdView.loadAd()` needs to be wrapped in a `Handler`
// block to ensure that the banner ad view is in the view
// hierarchy *before* the call to `loadAd()`.  Otherwise the
// visibility check in `loadAd()` will fail, and no ad will be
// shown.
        Handler().postDelayed({
            bav!!.loadAd()
            msg += " loadAd() triggered"
            toast()
        }, 0)
    }

    fun toggleLazyLoadAndReload(v: View?) {
        val tv = findViewById<View>(R.id.enableAndReload) as TextView
        bav!!.enableWebviewLazyLoad(!bav!!.isWebviewLazyLoadEnabled)
        if (bav!!.isWebviewLazyLoadEnabled) {
            tv.text = "Disable And Reload"
            msg = "Lazy Load Enabled"
        } else {
            tv.text = "Enable And Reload"
            msg = "Lazy Load Disabled"
        }
        load()
    }

    fun activateWebview(v: View?) {
        bav!!.loadWebview()
        msg = "Webview Activated"
        toast()
    }

    open fun toast() {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        Clog.e("LAZYLOAD", msg)
    }
}