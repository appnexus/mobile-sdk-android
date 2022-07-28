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

package com.xandr.lazyloaddemo

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appnexus.opensdk.*
import com.appnexus.opensdk.utils.Clog
import kotlinx.android.synthetic.main.activity_banner_lazy_load.*

class BannerLazyLoadActivity : AppCompatActivity() {

    lateinit var banner: BannerAdView
    var msg = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banner_lazy_load)
        setupBanner()
    }

    private fun setupBanner() {
        banner = BannerAdView(this)
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
        // Disabling the Auto Refresh
        banner.autoRefreshInterval = 0
        // Set up a listener on this ad view that logs events.
        val adListener: AdListener = object : AdListener {
            override fun onAdRequestFailed(bav: AdView, errorCode: ResultCode) {
                if (errorCode == null) {
                    Clog.v("LazyLoadDemo", "Call to loadAd failed")
                } else {
                    Clog.v("LazyLoadDemo", "Ad request failed: $errorCode")
                }
            }

            override fun onAdLoaded(bav: AdView) {
                msg = "onAdLoaded"
                toast()
                Clog.v("LazyLoadDemo", "The Ad Loaded!")
                showAd()
            }

            override fun onAdLoaded(nativeAdResponse: NativeAdResponse) {
                Clog.v("LazyLoadDemo", "Ad onAdLoaded NativeAdResponse")
            }

            override fun onAdExpanded(bav: AdView) {
                Clog.v("LazyLoadDemo", "Ad expanded")
            }

            override fun onAdCollapsed(bav: AdView) {
                Clog.v("LazyLoadDemo", "Ad collapsed")
            }

            override fun onAdClicked(bav: AdView) {
                Clog.v("LazyLoadDemo", "Ad clicked; opening browser")
            }

            override fun onAdClicked(adView: AdView, clickUrl: String) {
                Clog.v("LazyLoadDemo", "onAdClicked with click URL")
            }

            override fun onLazyAdLoaded(adView: AdView) {
                msg = "onLazyAdLoaded"
                toast()
            }

            override fun onAdImpression(adView: AdView?) {
                msg = "onAdImpression"
                toast()
            }
        }
        banner.adListener = adListener
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
        Handler().postDelayed({
            banner!!.loadAd()
            msg += " loadAd() triggered"
            toast()
        }, 0)
    }

    fun loadBanner(v: View?) {
        banner.enableLazyLoad()
        load()
//        v!!.setEnabled(false)
    }

    fun loadLazyAd(v: View?) {
        banner.loadLazyAd()
        msg = "Webview Activated"
        toast()
//        v!!.setEnabled(false)
    }

    open fun toast() {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        tvInfo.setText(msg)
        Clog.e("LAZYLOAD", msg)
    }
}