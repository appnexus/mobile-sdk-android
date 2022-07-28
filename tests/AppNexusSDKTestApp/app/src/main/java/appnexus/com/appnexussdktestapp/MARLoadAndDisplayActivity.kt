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
import com.appnexus.opensdk.instreamvideo.VideoAd
import com.appnexus.opensdk.instreamvideo.VideoAdLoadListener
import com.appnexus.opensdk.mar.MultiAdRequestListener
import com.appnexus.opensdk.utils.Clog
import kotlinx.android.synthetic.main.activity_mar_load.*

class MARLoadAndDisplayActivity : Activity() {
    companion object {
        val DISPLAY_AD = "DISPLAY_AD"
        val AD_TYPE = "AD_TYPE"
        val BID_TYPE = "BID_TYPE"
    }

    private lateinit var layoutManager: LinearLayoutManager
    var onLazyLoadAdLoaded: Boolean = false
    var onLazyAdLoaded: Boolean = false
    private var displayAd = true
    var shouldDisplayNativeAd = true
    private val arrayListAd = ArrayList<Any?>()
    private val arrayListAdUnits = ArrayList<Ad>()
    internal var msg = ""
    lateinit var layout: LinearLayout
    //    internal var placementID = "17982237"
    internal var placementID = "17058950"
    //    internal var csmVideo = placementID
    internal var csmVideo = "18121480"
    internal var csmBanner = "18108595"
    internal var csmNative = "18108596"
    internal var csmInterstitial = "18108597"
    internal var nativePlacementId = placementID

    internal var videoCreativeId = 162035356
    internal var bannerCreativeId = 182424585 //166843311
    internal var nativeCreativeId = 162039377
    internal var interstitialCreativeId = 166843825

    internal var videoPlacementId = placementID
    internal var anMultiAdRequest: ANMultiAdRequest? = null

    internal var idlingResource = CountingIdlingResource("MAR Load Count", true)
    internal var multiAdRequestCompleted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mar_load)
//        if (idlingResource.isIdleNow) {
//            idlingResource.increment()
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerListAdView.layoutManager = layoutManager
        recyclerListAdView.adapter = AdViewRecyclerAdapter(arrayListAd, this)


        anMultiAdRequest = ANMultiAdRequest(this, 0, 0,
            object : MultiAdRequestListener {
                override fun onMultiAdRequestCompleted() {
                    msg = "MAR Load Completed"
                    multiAdRequestCompleted = true
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
        val stringArrayListExtra = intent.getStringArrayListExtra(AD_TYPE)
        val bidArrayListExtra = intent.getStringArrayListExtra(BID_TYPE)
        displayAd = intent.getBooleanExtra(DISPLAY_AD, true)
        Clog.e("AD List", stringArrayListExtra.toString())
        Clog.e("AD List Bid", bidArrayListExtra.toString())
        stringArrayListExtra.forEachIndexed({ index, str ->
            if (str.equals("Interstitial", true)) {
                idlingResource.increment()
                arrayListAdUnits.add(setupInterstitialAd(bidArrayListExtra.get(index)))
            } else if (str.equals("Banner", true)) {
                idlingResource.increment()
                arrayListAdUnits.add(setupBannerAd(bidArrayListExtra.get(index)))
            } else if (str.equals("Banner-LazyLoad", true)) {
                idlingResource.increment()
                arrayListAdUnits.add(setupBannerAd(bidArrayListExtra.get(index), true))
            } else if (str.equals("Native", true)) {
                idlingResource.increment()
                arrayListAdUnits.add(setupNativeAd(bidArrayListExtra.get(index)))
            } else if (str.equals("Video", true)) {
                idlingResource.increment()
                arrayListAdUnits.add(setupVideoAd(bidArrayListExtra.get(index)))
            }
        })
        load()
    }

    internal fun load() {
        arrayListAdUnits.forEach({
            anMultiAdRequest!!.addAdUnit(it)
        })
        multiAdRequestCompleted = false
        Clog.e("MAR TESTING", "IDLING RESOURCE IDLE: " + idlingResource.isIdleNow)
        if (idlingResource.isIdleNow) {
            idlingResource.increment()
        }
        anMultiAdRequest!!.load()
    }

    private fun setupNativeAd(bidType: String): NativeAdRequest {

        if (bidType.equals("csm", true)) {
            nativePlacementId = csmNative
        } else {
            nativePlacementId = placementID
        }

        val adRequest = NativeAdRequest(this, nativePlacementId)

        if (bidType.equals("rtb", true)) {
            val utils = Utils()
            utils.setForceCreativeId(nativeCreativeId, nativeAdRequest = adRequest)
        }

        if (displayAd) {
            adRequest.shouldLoadImage(true)
            adRequest.shouldLoadIcon(true)
        } else {
            adRequest.shouldLoadImage(false)
            adRequest.shouldLoadIcon(false)
        }
        adRequest.listener = object : NativeAdRequestListener {
            override fun onAdLoaded(response: NativeAdResponse) {
                msg += "Native Ad Loaded\n"
                toast()
                arrayListAd.add(response)
                (recyclerListAdView.adapter as AdViewRecyclerAdapter).setShouldDisplay(
                    shouldDisplayNativeAd
                )
                if (displayAd)
                    recyclerListAdView.adapter!!.notifyDataSetChanged()
                if (!idlingResource.isIdleNow)
                    idlingResource.decrement()
            }

            override fun onAdFailed(errorcode: ResultCode, adResponseInfo: ANAdResponseInfo?) {
                msg += "Native Ad Failed:$errorcode\n"
                toast()
                if (!idlingResource.isIdleNow)
                    idlingResource.decrement()
            }
        }
        return adRequest
    }

    private fun setupInterstitialAd(bidType: String): InterstitialAdView {
        val iav = InterstitialAdView(this)

        if (bidType.equals("csm", true)) {
            iav.placementID = csmInterstitial
        } else {
            iav.placementID = placementID
            val utils = Utils()
            utils.setForceCreativeId(bannerCreativeId, interstitial = iav)
        }

        iav.adListener = object : AdListener {
            override fun onAdRequestFailed(iav: AdView, errorCode: ResultCode?) {
                if (errorCode == null) {
                    msg += "Interstitial Ad Failed\n"
                } else {
                    msg += "Interstitial Ad Failed: $errorCode\n"
                }
                if (!idlingResource.isIdleNow)
                    idlingResource.decrement()
                toast()
            }

            override fun onLazyAdLoaded(adView: AdView?) {
            }

            override fun onAdImpression(adView: AdView?) {
                msg = "Ad Impression"
                toast()
            }

            override fun onAdLoaded(av: AdView) {
                msg += "Interstitial Ad Loaded\n"
                toast()
                arrayListAd.add(av)
                if (displayAd)
                    recyclerListAdView.adapter!!.notifyDataSetChanged()
                if (!idlingResource.isIdleNow)
                    idlingResource.decrement()
            }

            override fun onAdLoaded(nativeAdResponse: NativeAdResponse) {
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

        return iav
    }

    private fun setupBannerAd(bidType: String, lazyLoad: Boolean = false): BannerAdView {
        val bav = BannerAdView(this)

        // This is your AppNexus placement ID.
        if (bidType.equals("csm", true)) {
            bav.placementID = csmBanner
        } else {
            bav.placementID = placementID
            val util = Utils()
            util.setForceCreativeId(bannerCreativeId, banner = bav)
        }

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
        //        bav.setAdSize(320, 50);

        // Resizes the container size to fit the banner ad
//        bav.resizeAdToFitContainer = true
//        bav.expandsToFitScreenWidth = true

        //        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
        //                LinearLayout.LayoutParams.MATCH_PARENT);
        //        bav.setLayoutParams(layoutParams);

        bav.adListener = object : AdListener {
            override fun onAdRequestFailed(bav: AdView, errorCode: ResultCode?) {
                if (errorCode == null) {
                    Clog.v("SimpleSRM", "Call to loadAd failed")
                } else {
                    Clog.v("SimpleSRM", "Ad request failed: $errorCode")
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
                    Handler(Looper.getMainLooper()).post({
                        recyclerListAdView.adapter!!.notifyDataSetChanged()
                    })
                if (!idlingResource.isIdleNow)
                    idlingResource.decrement()
            }

            override fun onAdImpression(adView: AdView?) {
                msg = "Ad Impression"
                toast()
            }

            override fun onAdLoaded(av: AdView) {
                Clog.v("SimpleSRM", "The Ad Loaded!")
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

    private fun setupVideoAd(bidType: String): VideoAd {
        val videoAd: VideoAd

        if (bidType.equals("csm", true)) {
            videoPlacementId = csmVideo
        } else {
            videoPlacementId = placementID
        }

        videoAd = VideoAd(this, videoPlacementId)

        if (bidType.equals("rtb", true)) {
            val utils = Utils()
            utils.setForceCreativeId(videoCreativeId, video = videoAd);
        }

        videoAd.clearCustomKeywords()

        videoAd.adLoadListener = object : VideoAdLoadListener {
            override fun onAdLoaded(adView: VideoAd) {
                Clog.d("VideoAd", "AD READY")
                msg += "Video Ad Loaded\n"
                toast()
                if (displayAd) {
                    Toast.makeText(
                        this@MARLoadAndDisplayActivity, "Ad is ready. Hit on Play button to start",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                arrayListAd.add(adView)
                if (displayAd)
                    recyclerListAdView.adapter!!.notifyDataSetChanged()
                if (!idlingResource.isIdleNow)
                    idlingResource.decrement()
            }

            override fun onAdRequestFailed(adView: VideoAd, errorCode: ResultCode) {
                Clog.d("VideoAd", "AD FAILED::")
                msg += "Video Ad Failed\n"
                toast()
//                Toast.makeText(this@MARLoadAndDisplayActivity, "AD FAILED::", Toast.LENGTH_SHORT)
//                    .show()
                if (!idlingResource.isIdleNow)
                    idlingResource.decrement()
            }
        }

        return videoAd
    }

    fun refreshForVisibility() {
        Handler(Looper.getMainLooper()).post({
            (recyclerListAdView.adapter as AdViewRecyclerAdapter).setShouldDisplay(shouldDisplayNativeAd)
        })
    }

    private fun toast() {
        if (displayAd) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
        Clog.e("TOAST", msg)
        Clog.e("LAZYLOAD", msg)
    }

    override fun onDestroy() {
        if (anMultiAdRequest != null) {
            anMultiAdRequest!!.adUnitList.forEach {
                var ad = it.get()
                if (ad != null) {
                    when (it) {
                        is BannerAdView -> it.activityOnDestroy()
                        is InterstitialAdView -> it.activityOnDestroy()
                        is VideoAd -> it.activityOnDestroy()
                    }
                }
            }
        }
        super.onDestroy()
    }
}
