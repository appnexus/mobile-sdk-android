package com.xandr.lazyloaddemo

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
import android.webkit.WebView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import appnexus.com.appnexussdktestapp.adapter.AdViewRecyclerAdapter
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
    }

    private var displayAd = true
    private val arrayListAd = ArrayList<Any?>()
    private val arrayListAdUnits = ArrayList<Ad>()
    internal var msg = ""
    internal var placementID = "17058950"
    internal var nativePlacementId = placementID

    internal var videoCreativeId = 162035356
    internal var bannerCreativeId = 166843311
    internal var nativeCreativeId = 162039377

    internal var videoPlacementId = placementID
    internal var anMultiAdRequest: ANMultiAdRequest? = null

    internal var multiAdRequestCompleted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mar_load)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        var layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerListAdView.layoutManager = layoutManager
        recyclerListAdView.adapter = AdViewRecyclerAdapter(arrayListAd, this)


        anMultiAdRequest = ANMultiAdRequest(this, 0,
            object : MultiAdRequestListener {
                override fun onMultiAdRequestCompleted() {
                    msg = "MAR Load Completed"
                    multiAdRequestCompleted = true
                    toast()
                }

                override fun onMultiAdRequestFailed(code: ResultCode) {
                    msg += code.message
                    toast()
                }
            })
        val stringArrayListExtra = intent.getStringArrayListExtra(AD_TYPE)
        displayAd = intent.getBooleanExtra(DISPLAY_AD, true)
        Clog.e("AD List", stringArrayListExtra.toString())
        stringArrayListExtra.forEachIndexed({ index, str ->
            if (str.equals("Interstitial", true)) {
                arrayListAdUnits.add(setupInterstitialAd())
            } else if (str.equals("Banner", true)) {
                arrayListAdUnits.add(setupBannerAd())
            } else if (str.equals("Banner-LazyLoad", true)) {
                arrayListAdUnits.add(setupBannerAd(true))
            } else if (str.equals("Native", true)) {
                arrayListAdUnits.add(setupNativeAd())
            } else if (str.equals("Video", true)) {
                arrayListAdUnits.add(setupVideoAd())
            }
        })
        load()
    }

    internal fun load() {
        arrayListAdUnits.forEach({
            anMultiAdRequest!!.addAdUnit(it)
        })
        multiAdRequestCompleted = false
        anMultiAdRequest!!.load()
    }

    private fun setupNativeAd(): NativeAdRequest {

        nativePlacementId = placementID

        val adRequest = NativeAdRequest(this, nativePlacementId)

        if (displayAd) {
            adRequest.shouldLoadImage(true)
            adRequest.shouldLoadIcon(true)
        } else {
            adRequest.shouldLoadImage(false)
            adRequest.shouldLoadIcon(false)
        }
        adRequest.listener = object : NativeAdRequestListener {
            override fun onAdLoaded(response: NativeAdResponse) {
                msg = "Native Ad Loaded\n"
                toast()
                arrayListAd.add(response)
                if (displayAd)
                    recyclerListAdView.adapter!!.notifyDataSetChanged()
            }

            override fun onAdFailed(errorcode: ResultCode, anAdResponseInfo: ANAdResponseInfo) {
                msg += "Native Ad Failed:${errorcode.message}\n"
                toast()
            }
        }
        return adRequest
    }

    private fun setupInterstitialAd(): InterstitialAdView {
        val iav = InterstitialAdView(this)

        iav.placementID = placementID

        iav.adListener = object : AdListener {
            override fun onAdRequestFailed(iav: AdView, errorCode: ResultCode?) {
                if (errorCode == null) {
                    msg += "Interstitial Ad Failed\n"
                } else {
                    msg += "Interstitial Ad Failed: ${errorCode.message}\n"
                }
                toast()
            }

            override fun onLazyAdLoaded(adView: AdView?) {
            }

            override fun onAdImpression(adView: AdView?) {
                msg += "onAdImpression"
                toast()
            }

            override fun onAdLoaded(av: AdView) {
                msg = "Interstitial Ad Loaded\n"
                toast()
                arrayListAd.add(av)
                if (displayAd)
                    recyclerListAdView.adapter!!.notifyDataSetChanged()
            }

            override fun onAdLoaded(nativeAdResponse: NativeAdResponse) {
                msg = "Banner-Native Ad Loaded\n"
                toast()
                arrayListAd.add(nativeAdResponse)
                if (displayAd)
                    recyclerListAdView.adapter!!.notifyDataSetChanged()
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
        }

        return iav
    }

    private fun setupBannerAd(lazyLoad: Boolean = false): BannerAdView {
        val bav = BannerAdView(this)

        // This is your AppNexus placement ID.
        bav.placementID = placementID

        bav.autoRefreshInterval = 0
        if (lazyLoad) {
            bav.enableLazyLoad()
        }
        // Turning this on so we always get an ad during testing.
        bav.shouldServePSAs = false

        // By default ad clicks open in an in-app WebView.
        bav.clickThroughAction = ANClickThroughAction.OPEN_SDK_BROWSER

        // Get a 300x50 ad.
        bav.setAdSize(300, 250)
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
                    Clog.v("LazyLoadDemo", "Call to loadAd failed")
                } else {
                    Clog.v("LazyLoadDemo", "Ad request failed: $errorCode")
                    msg += "Banner Ad Failed: ${errorCode.message}\n"
                    toast()
                }
            }

            override fun onLazyAdLoaded(adView: AdView?) {
                msg = "Banner Ad Lazy Loaded\n"
                toast()
                arrayListAd.add(adView)
                if (displayAd)
                    recyclerListAdView.adapter!!.notifyDataSetChanged()
            }

            override fun onAdImpression(adView: AdView?) {
                msg = "onAdImpression"
                toast()
            }

            override fun onAdLoaded(av: AdView) {
                Clog.v("LazyLoadDemo", "The Ad Loaded!")
                //                if (av.getParent() != null && av.getParent() instanceof ViewGroup) {
                //                    ((ViewGroup) av.getParent()).removeAllViews();
                //                }
                msg = "Banner Ad Loaded\n"
                toast()
                if (!bav.isLazyLoadEnabled()) {
                    arrayListAd.add(av)
                }
                if (displayAd)
                    recyclerListAdView.adapter!!.notifyDataSetChanged()
            }

            override fun onAdLoaded(nativeAdResponse: NativeAdResponse) {
                Clog.v("LazyLoadDemo", "Ad onAdLoaded NativeAdResponse")
                msg = "Banner-Native Ad Loaded\n"
                toast()
                arrayListAd.add(nativeAdResponse)
                if (displayAd)
                    recyclerListAdView.adapter!!.notifyDataSetChanged()
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
        }

        return bav

    }

    private fun setupVideoAd(): VideoAd {
        val videoAd: VideoAd

        videoPlacementId = placementID

        videoAd = VideoAd(this, videoPlacementId)

        videoAd.clearCustomKeywords()

        videoAd.adLoadListener = object : VideoAdLoadListener {
            override fun onAdLoaded(adView: VideoAd) {
                Clog.d("VideoAd", "AD READY")
                msg = "Video Ad Loaded\n"
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
            }

            override fun onAdRequestFailed(adView: VideoAd, errorCode: ResultCode) {
                Clog.d("VideoAd", "AD FAILED::")
                msg += "Video Ad Failed\n"
                toast()
            }
        }

        return videoAd
    }

    private fun toast() {
        if (displayAd) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
        Clog.e("TOAST", msg)
        Clog.e("LAZYLOAD", msg)
    }
}
