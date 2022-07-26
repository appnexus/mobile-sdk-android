package appnexus.com.appnexussdktestapp

/*
 *    Copyright 2014 APPNEXUS INC
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

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.test.espresso.idling.CountingIdlingResource
import appnexus.com.appnexussdktestapp.adapter.AdViewRecyclerAdapter
import appnexus.com.appnexussdktestapp.utility.Utils
import com.appnexus.opensdk.*
import com.appnexus.opensdk.mar.MultiAdRequestListener
import com.appnexus.opensdk.utils.Clog
import kotlinx.android.synthetic.main.activity_mar_load.*

class MARScaleLoadAndDisplayActivity : Activity() {
    companion object {
        val DISPLAY_AD = "DISPLAY_AD"
        val COUNT = "count"
    }

    private lateinit var layoutManager: LinearLayoutManager
    var onLazyLoadAdLoaded: Boolean = false
    var onLazyAdLoaded: Boolean = false
    private var displayAd = true
    var shouldDisplayNativeAd = true
    private val arrayListAd = ArrayList<Any?>()
    private val arrayListAdUnits = ArrayList<Ad>()
    private val arrayListMAR = ArrayList<ANMultiAdRequest>()
    internal var msg = ""
    lateinit var layout: LinearLayout
    internal var placementID = "17058950"

    internal var bannerCreativeId = 182424585 //166843311

    internal var idlingResource = CountingIdlingResource("MAR Load Count", true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mar_load)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerListAdView.layoutManager = layoutManager
        recyclerListAdView.adapter = AdViewRecyclerAdapter(arrayListAd, this)

        val count = intent.getIntExtra(COUNT, -1)
        displayAd = intent.getBooleanExtra(DISPLAY_AD, true)
        Clog.e("COUNT", count.toString())
        for (i in 0..count) {
            var mar = ANMultiAdRequest(this, 0, 0,
                object : MultiAdRequestListener {
                    override fun onMultiAdRequestCompleted() {
                        msg = "MAR Load Completed"
                        Clog.i("MAR TESTING", "IDLING RESOURCE IDLE: " + idlingResource.isIdleNow)
                        if (!idlingResource.isIdleNow)
                            idlingResource.decrement()
                        toast()
                    }

                    override fun onMultiAdRequestFailed(code: ResultCode) {
                        if (!idlingResource.isIdleNow)
                            idlingResource.decrement()
                        msg += code.message
                        toast()
                    }
                })
            mar.addAdUnit(setupBannerAd("RTB"))
            arrayListMAR.add(mar)
        }
        load()
    }

    internal fun load() {
        arrayListMAR.forEach({
            it.load()
        })
        Clog.e("MAR TESTING", "IDLING RESOURCE IDLE: " + idlingResource.isIdleNow)
        if (idlingResource.isIdleNow) {
            idlingResource.increment()
        }
    }

    private fun setupBannerAd(bidType: String, lazyLoad: Boolean = false): BannerAdView {
        val bav = BannerAdView(this)

        // This is your AppNexus placement ID.
        bav.placementID = placementID
        val util = Utils()
        util.setForceCreativeId(bannerCreativeId, banner = bav)

        bav.allowNativeDemand = false
        bav.allowVideoDemand = false
        bav.autoRefreshInterval = 0
        if (lazyLoad) {
            bav.enableLazyLoad()
        }
        // Turning this on so we always get an ad during testing.
        bav.shouldServePSAs = false

        // By default ad clicks open in an in-app WebView.
        bav.clickThroughAction = ANClickThroughAction.OPEN_SDK_BROWSER

        // Get a 300x50 ad.
        val adSizes = ArrayList<AdSize>()
        adSizes.add(AdSize(320, 50))
        bav.adSizes = adSizes

        bav.adListener = object : AdListener {
            override fun onAdRequestFailed(bav: AdView, errorCode: ResultCode?) {
                if (errorCode == null) {
                    Clog.e("SimpleSRM", "Call to loadAd failed")
                } else {
                    Clog.e("SimpleSRM", "Ad request failed: $errorCode")
                    msg += "Banner Ad Failed: $errorCode\n"
                    toast()
                }
                if (!idlingResource.isIdleNow()) {
                    idlingResource.decrement()
                }
            }

            override fun onLazyAdLoaded(adView: AdView?) {
                msg = "Banner Ad Lazy Loaded\n"
                toast()
                onLazyAdLoaded = true
                arrayListAd.add(adView)
                if (displayAd)
                    recyclerListAdView.adapter!!.notifyDataSetChanged()
                if (!idlingResource.isIdleNow)
                    idlingResource.decrement()
            }

            override fun onAdImpression(adView: AdView?) {
                msg += "Ad Impression"
                toast()
            }

            override fun onAdLoaded(av: AdView) {
                Clog.e("SimpleSRM", "The Ad Loaded!")
                //                if (av.getParent() != null && av.getParent() instanceof ViewGroup) {
                //                    ((ViewGroup) av.getParent()).removeAllViews();
                //                }
                msg += "Banner Ad Loaded\n"
                toast()
                if (!bav.isLazyLoadEnabled()) {
                    arrayListAd.add(av)
                } else {
                    onLazyLoadAdLoaded = true
                }
                if (displayAd)
                    recyclerListAdView.adapter!!.notifyDataSetChanged()
                if (!idlingResource.isIdleNow)
                    idlingResource.decrement()
            }

            override fun onAdLoaded(nativeAdResponse: NativeAdResponse) {
                Clog.v("SimpleSRM", "Ad onAdLoaded NativeAdResponse")
                msg += "Banner-Native Ad Loaded\n"
                toast()
                arrayListAd.add(nativeAdResponse)
                if (displayAd)
                    recyclerListAdView.adapter!!.notifyDataSetChanged()
                if (!idlingResource.isIdleNow)
                    idlingResource.decrement()
            }

            override fun onAdExpanded(bav: AdView) {
                Clog.v("SimpleSRM", "Ad expanded")
            }

            override fun onAdCollapsed(bav: AdView) {
                Clog.v("SimpleSRM", "Ad collapsed")
            }

            override fun onAdClicked(bav: AdView) {
                Clog.v("SimpleSRM", "Ad clicked; opening browser")
            }

            override fun onAdClicked(adView: AdView, clickUrl: String) {
                Clog.v("SimpleSRM", "onAdClicked with click URL")
            }
        }

        return bav

    }

    fun refreshForVisibility() {
        Handler(Looper.getMainLooper()).post({
            (recyclerListAdView.adapter as AdViewRecyclerAdapter).setShouldDisplay(
                shouldDisplayNativeAd
            )
        })
    }

    private fun toast() {
        if (displayAd) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
        Clog.e("TOAST", msg)
    }

    override fun onDestroy() {
        if (arrayListMAR != null) {
            arrayListMAR.forEach {
                if (it != null) {
                    if (it.adUnitList != null) {
                        it.adUnitList.forEach {
                            when (it) {
                                is BannerAdView -> it.activityOnDestroy()
                            }
                        }
                    }
                }
            }
        }
        super.onDestroy()
    }
}
