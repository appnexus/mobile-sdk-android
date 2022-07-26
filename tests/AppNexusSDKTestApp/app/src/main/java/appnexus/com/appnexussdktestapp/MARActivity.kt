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

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.MediaController
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView

import androidx.test.espresso.idling.CountingIdlingResource
import appnexus.com.appnexussdktestapp.utility.Utils
import com.appnexus.opensdk.*

import com.appnexus.opensdk.instreamvideo.Quartile
import com.appnexus.opensdk.instreamvideo.VideoAd
import com.appnexus.opensdk.instreamvideo.VideoAdLoadListener
import com.appnexus.opensdk.instreamvideo.VideoAdPlaybackListener
import com.appnexus.opensdk.mar.MultiAdRequestListener
import com.appnexus.opensdk.utils.Clog

import java.util.ArrayList
import java.util.LinkedList

class MARActivity : Activity() {

    internal var msg = ""
    lateinit var tv: TextView
    lateinit var layout: LinearLayout
    lateinit var layoutBanner: LinearLayout
    lateinit var layoutVideo: LinearLayout
    lateinit var layoutNative: LinearLayout
    internal var placementID = "17058950"
    internal var bannerPlacementId = "17058950"
    internal var interstitialPlacementId = "17058950"
    internal var nativePlacementId = "17058950"
    internal var videoPlacementId = placementID
    internal var count = 1
    lateinit var iav: InterstitialAdView
    lateinit var bav: BannerAdView
    internal var anMultiAdRequest: ANMultiAdRequest? = null
    internal var anMultiAdRequest2: ANMultiAdRequest? = null
    internal var adRequest: NativeAdRequest? = null

    internal var idlingResource = CountingIdlingResource("MAR Load Count", true)
    internal var multiAdRequestCompleted = false
    internal var multiAdRequestCompleted2 = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my)

        videoPlacementId = placementID
        nativePlacementId = videoPlacementId
        interstitialPlacementId = nativePlacementId
        bannerPlacementId = interstitialPlacementId

        //        layout = findViewById(R.id.main_content);
        layout = findViewById(R.id.adContent)
        layoutBanner = findViewById(R.id.layoutBanner)
        layoutNative = findViewById(R.id.layoutNative)
        layoutVideo = findViewById(R.id.layoutVideo)
        tv = findViewById(R.id.tv)

        iav = setupInterstitialAd()

        val videoAd = setupVideoAd()

        bav = setupBannerAd()

        adRequest = setupNativeAd()

        // Set up a listener on this ad view that logs events.
        //        AdListener adListener =

        //        layout.addView(bav);

        iav.adListener = object : AdListener {
            override fun onAdRequestFailed(iav: AdView, errorCode: ResultCode?) {
                if (errorCode == null) {
                    msg += "Interstitial Ad Failed\n"
                } else {
                    msg += "Interstitial Ad Failed: $errorCode\n"
                }
                toast()
            }

            override fun onLazyAdLoaded(adView: AdView?) {
            }

            override fun onAdImpression(adView: AdView?) {
                msg = "Ad Impression"
                toast()
            }

            override fun onAdLoaded(av: AdView) {
                if (av is InterstitialAdView) {
                    msg += "Interstitial Ad Loaded\n"
                    iav.show()
                }
                toast()
            }

            override fun onAdLoaded(nativeAdResponse: NativeAdResponse) {
                msg += "Banner-Native Ad Loaded\n"
                toast()
                handleNativeResponse(nativeAdResponse)
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

        //        List<Ad> adUnitList = new ArrayList<>();
        //        adUnitList.add(bav);
        //        adUnitList.add(iav);
        //        adUnitList.add(adRequest);
        //        adUnitList.add(videoAd);

        anMultiAdRequest2 = ANMultiAdRequest(this, 0, 0, object : MultiAdRequestListener {
            override fun onMultiAdRequestCompleted() {
                msg += "MAR 2 Load Completed"
                multiAdRequestCompleted2 = true
                Clog.i("MAR TESTING", "IDLING RESOURCE IDLE: " + idlingResource.isIdleNow)
                if (!idlingResource.isIdleNow)
                    idlingResource.decrement()
                toast()
            }

            override fun onMultiAdRequestFailed(code: ResultCode) {
                msg += code.message
                toast()
            }
        })
        anMultiAdRequest2!!.addAdUnit(setupVideoAd())
        anMultiAdRequest2!!.addAdUnit(setupNativeAd())
        anMultiAdRequest2!!.addAdUnit(setupBannerAd())
        anMultiAdRequest2!!.addAdUnit(setupInterstitialAd())
        //        anMultiAdRequest2.load();


        anMultiAdRequest = ANMultiAdRequest(this, 0, 0,
            object : MultiAdRequestListener {
                override fun onMultiAdRequestCompleted() {
                    msg += "MAR Load Completed"
                    multiAdRequestCompleted = true
                    Clog.i("MAR TESTING", "IDLING RESOURCE IDLE: " + idlingResource.isIdleNow)
                    if (!idlingResource.isIdleNow)
                        idlingResource.decrement()
                    toast()
                }

                override fun onMultiAdRequestFailed(code: ResultCode) {
                    msg += code.message
                    toast()
                }
            })
        anMultiAdRequest!!.addAdUnit(bav)
        anMultiAdRequest!!.addAdUnit(iav)
        anMultiAdRequest!!.addAdUnit(adRequest);
        anMultiAdRequest!!.addAdUnit(videoAd);
        //        anMultiAdRequest.addAdUnit(setupVideoAd());
        //        bav = null;
        //        System.gc();
        //        iav = null;
        //        adRequest = null;
        //        load();
        //        bav.loadAd();

        tv.setOnClickListener {
            msg = ""
            toast()
            //                switch (count) {
            //                    case 1:
            //                        bav.loadAd();
            //                        break;
            //                    case 2:
            //                        iav.loadAd();
            //                        break;
            //                    case 3:
            //                        adRequest.loadAd();
            //                        break;
            //                    case 4:
            //                        videoAd.loadAd();
            //                        break;
            //                    default:
            //                        anMultiAdRequest.load();
            //                        break;
            //                }
            //                count++;
            load()
        }

        //        // If auto-refresh is enabled (the default), a call to
        //        // `FrameLayout.addView()` followed directly by
        //        // `BannerAdView.loadAd()` will succeed.  However, if
        //        // auto-refresh is disabled, the call to
        //        // `BannerAdView.loadAd()` needs to be wrapped in a `Handler`
        //        // block to ensure that the banner ad view is in the view
        //        // hierarchy *before* the call to `loadAd()`.  Otherwise the
        //        // visibility check in `loadAd()` will fail, and no ad will be
        //        // shown.
        //        new Handler().postDelayed(new Runnable() {
        //            @Override
        //            public void run() {
        //                bav.loadAd();
        //            }
        //        }, 0);
    }

    internal fun load(bgTask: Boolean = false) {
        multiAdRequestCompleted = false
        Clog.e("MAR TESTING", "IDLING RESOURCE IDLE: " + idlingResource.isIdleNow)
        if (idlingResource.isIdleNow) {
            idlingResource.increment()
        }
        Handler(Looper.getMainLooper()).post({
            anMultiAdRequest!!.load()
        })
    }

    internal fun load2(bgTask: Boolean = false) {
        multiAdRequestCompleted2 = false
        Clog.e("MAR TESTING", "IDLING RESOURCE IDLE: " + idlingResource.isIdleNow)
        if (idlingResource.isIdleNow) {
            idlingResource.increment()
        }
        Handler(Looper.getMainLooper()).post({
            anMultiAdRequest2!!.load()
        })
    }

    private fun setupNativeAd(): NativeAdRequest {
        val adRequest = NativeAdRequest(this, nativePlacementId)
        adRequest.shouldLoadImage(false)
        adRequest.shouldLoadIcon(false)
        adRequest.listener = object : NativeAdRequestListener {
            override fun onAdLoaded(response: NativeAdResponse) {
                msg += "Native Ad Loaded\n"
                toast()
                handleNativeResponse(response)
            }

            override fun onAdFailed(errorcode: ResultCode, adResponseInfo: ANAdResponseInfo) {
                msg += "Native Ad Failed:$errorcode\n"
                toast()
            }
        }
        return adRequest
    }

    private fun setupInterstitialAd(): InterstitialAdView {
        val iav = InterstitialAdView(this)

        val util = Utils()
        util.setForceCreativeId(166843825, interstitial = iav)

        iav.placementID = interstitialPlacementId
        return iav
    }

    private fun setupBannerAd(): BannerAdView {
        val bav = BannerAdView(this)

        // This is your AppNexus placement ID.
        bav.placementID = bannerPlacementId
        val util = Utils()
        util.setForceCreativeId(166843825, banner = bav)

        bav.allowNativeDemand = false
        bav.allowVideoDemand = false
        bav.autoRefreshInterval = 0

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
        bav.resizeAdToFitContainer = true

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
            }

            override fun onLazyAdLoaded(adView: AdView?) {
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
            }

            override fun onAdLoaded(nativeAdResponse: NativeAdResponse) {
                Clog.v("SimpleSRM", "Ad onAdLoaded NativeAdResponse")
                msg += "Banner-Native Ad Loaded\n"
                toast()
                handleNativeResponse(nativeAdResponse)
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

        layoutBanner.removeAllViews()
        layoutBanner.addView(bav)
        return bav

    }

    private fun setupVideoAd(): VideoAd {
        val videoAd: VideoAd
        // Load and display a Video
        // Video Ad elements
        val instreamVideoLayout = layoutInflater.inflate(R.layout.fragment_preview_instream, null)
        val playButon = instreamVideoLayout.findViewById(R.id.play_button) as ImageButton
        val videoPlayer = instreamVideoLayout.findViewById(R.id.video_player) as VideoView
        val baseContainer =
            instreamVideoLayout.findViewById(R.id.instream_container_Layout) as RelativeLayout

        layoutVideo.removeAllViews()
        layoutVideo.addView(baseContainer)

        baseContainer.layoutParams.height = 1000
        baseContainer.layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT

        videoPlayer.setVideoURI(Uri.parse("https://acdn.adnxs.com/mobile/video_test/content/Scenario.mp4"))
        val controller = MediaController(this)
        videoPlayer.setMediaController(controller)

        videoAd = VideoAd(this, videoPlacementId)
        videoAd.clearCustomKeywords()

        videoAd.adLoadListener = object : VideoAdLoadListener {
            override fun onAdLoaded(adView: VideoAd) {
                Clog.d("VideoAd", "AD READY")
                msg += "Video Ad Loaded\n"
                toast()
                Toast.makeText(
                    this@MARActivity, "Ad is ready. Hit on Play button to start",
                    Toast.LENGTH_SHORT
                ).show()
                playButon.visibility = View.VISIBLE
            }

            override fun onAdRequestFailed(adView: VideoAd, errorCode: ResultCode) {
                Clog.d("VideoAd", "AD FAILED::")
                msg += "Video Ad Failed\n"
                toast()
                Toast.makeText(this@MARActivity, "AD FAILED::", Toast.LENGTH_SHORT).show()
                playButon.visibility = View.VISIBLE
            }
        }

        playButon.setOnClickListener {
            if (videoAd.isReady) {
                videoAd.playAd(baseContainer)
            } else {
                videoPlayer.start()
            }
            playButon.visibility = View.INVISIBLE
        }

        //        videoAd.loadAd();
        // Set PlayBack Listener.
        videoAd.videoPlaybackListener = object : VideoAdPlaybackListener {

            override fun onAdPlaying(videoAd: VideoAd) {
                Clog.d("VideoAd", "onAdPlaying::")
            }

            override fun onQuartile(view: VideoAd, quartile: Quartile) {
                Clog.d("VideoAd", "onQuartile::$quartile")
            }

            override fun onAdCompleted(
                view: VideoAd,
                playbackState: VideoAdPlaybackListener.PlaybackCompletionState
            ) {
                if (playbackState == VideoAdPlaybackListener.PlaybackCompletionState.COMPLETED) {
                    Clog.d("VideoAd", "adCompleted::playbackState")
                } else if (playbackState == VideoAdPlaybackListener.PlaybackCompletionState.SKIPPED) {
                    Clog.d("VideoAd", "adSkipped::")
                }
                videoPlayer.start()
            }

            override fun onAdMuted(view: VideoAd, isMute: Boolean) {
                Clog.d("VideoAd", "isAudioMute::$isMute")
            }

            override fun onAdClicked(adView: VideoAd) {
                Clog.d("VideoAd", "onAdClicked")
            }

            override fun onAdClicked(videoAd: VideoAd, clickUrl: String) {
                Clog.d("VideoAd", "onAdClicked::clickUrl$clickUrl")
                msg += "Video AdClicked::clickUrl - $clickUrl"
            }
        }
        return videoAd
    }

    private fun toast() {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        Clog.e("TOAST", msg)
        tv.text = msg
    }

    private fun handleNativeResponse(response: NativeAdResponse) {
        val builder = NativeAdBuilder(this)
        if (response.icon != null)
            builder.setIconView(response.icon)
        if (response.image != null)
            builder.setImageView(response.image)
        builder.setTitle(response.title)
        builder.setDescription(response.description)
        builder.setCallToAction(response.callToAction)
        builder.setSponsoredBy(response.sponsoredBy)

        if (response.adStarRating != null) {
            builder.setAdStartValue(response.adStarRating.value.toString() + "/" + response.adStarRating.scale.toString())
        }

        // register all the views
        if (builder.container != null && builder.container!!.parent != null)
            (builder.container!!.parent as ViewGroup).removeAllViews()

        val nativeContainer = builder.container
        Handler().post {
            layoutNative.removeAllViews()
            layoutNative.addView(nativeContainer)
        }
    }

    internal inner class NativeAdBuilder @SuppressLint("NewApi")
    constructor(context: Context) {
        var container: RelativeLayout? = null
        var iconAndTitle: LinearLayout
        var customViewLayout: LinearLayout
        var imageView: ImageView
        var iconView: ImageView
        var title: TextView
        var description: TextView
        var callToAction: TextView
        var adStarRating: TextView
        var socialContext: TextView
        var sponsoredBy: TextView
        var customView = null // Any Mediated network requiring to render there own view for impression tracking would go in here.
        var views: LinkedList<View>? = null

        val allViews: LinkedList<View>
            get() {
                if (views == null) {
                    views = LinkedList()
                    views!!.add(imageView)
                    views!!.add(iconView)
                    views!!.add(title)
                    views!!.add(description)
                    views!!.add(callToAction)
                    views!!.add(adStarRating)
                    views!!.add(socialContext)
                    views!!.add(sponsoredBy)
                }
                return views as LinkedList<View>
            }


        init {
            container = RelativeLayout(context)

            iconAndTitle = LinearLayout(context)
            iconAndTitle.id = View.generateViewId()
            iconAndTitle.orientation = LinearLayout.HORIZONTAL
            iconView = ImageView(context)
            iconAndTitle.addView(iconView)
            title = TextView(context)
            iconAndTitle.addView(title)
            container!!.addView(iconAndTitle)


            imageView = ImageView(context)
            imageView.id = View.generateViewId()
            val imageView_params = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            imageView_params.addRule(RelativeLayout.BELOW, iconAndTitle.id)


            description = TextView(context)
            description.id = View.generateViewId()
            val description_params = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            description_params.addRule(RelativeLayout.BELOW, imageView.id)


            callToAction = TextView(context)
            callToAction.id = View.generateViewId()
            val callToAction_params = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            callToAction_params.addRule(RelativeLayout.BELOW, description.id)


            adStarRating = TextView(context)
            adStarRating.id = View.generateViewId()
            val adStarRating_params = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            adStarRating_params.addRule(RelativeLayout.BELOW, callToAction.id)

            socialContext = TextView(context)
            socialContext.id = View.generateViewId()
            val socialContext_params = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            socialContext_params.addRule(RelativeLayout.BELOW, adStarRating.id)


            sponsoredBy = TextView(context)
            sponsoredBy.id = View.generateViewId()
            val sponsoredBy_params = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            sponsoredBy_params.addRule(RelativeLayout.BELOW, socialContext.id)


            customViewLayout = LinearLayout(context)
            customViewLayout.id = View.generateViewId()
            customViewLayout.orientation = LinearLayout.HORIZONTAL
            val customViewLayout_params = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            customViewLayout_params.addRule(RelativeLayout.BELOW, sponsoredBy.id)


            container!!.addView(description, description_params)
            container!!.addView(imageView, imageView_params)
            container!!.addView(callToAction, callToAction_params)
            container!!.addView(adStarRating, adStarRating_params)
            container!!.addView(socialContext, socialContext_params)
            container!!.addView(sponsoredBy, sponsoredBy_params)
            container!!.addView(customViewLayout, customViewLayout_params)
        }

        fun setImageView(bitmap: Bitmap) {
            imageView.setImageBitmap(bitmap)
        }

        fun setIconView(bitmap: Bitmap) {
            iconView.setImageBitmap(bitmap)
        }


        fun setCustomView(customView: View) {
            this.customViewLayout.addView(customView)
        }

        fun setTitle(title: String) {
            this.title.text = title
        }

        fun setDescription(description: String) {
            this.description.text = description
        }

        fun setCallToAction(callToAction: String) {
            this.callToAction.text = callToAction
        }

        fun setSocialContext(socialContext: String) {
            this.socialContext.text = socialContext
        }

        fun setSponsoredBy(sponsoredBy: String) {
            this.sponsoredBy.text = sponsoredBy
        }

        fun setAdStartValue(value: String) {
            this.adStarRating.text = value
        }
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

        if (anMultiAdRequest2 != null) {
            anMultiAdRequest2!!.adUnitList.forEach {
                var ad = it.get()
                if (ad != null) {
                    when (ad) {
                        is BannerAdView -> ad.activityOnDestroy()
                        is InterstitialAdView -> ad.activityOnDestroy()
                        is VideoAd -> ad.activityOnDestroy()
                    }
                }
            }
        }
        super.onDestroy()
    }
}
