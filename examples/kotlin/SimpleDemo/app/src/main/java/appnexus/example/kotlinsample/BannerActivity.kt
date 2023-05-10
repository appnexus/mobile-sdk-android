package appnexus.example.kotlinsample

import android.os.Bundle

import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appnexus.opensdk.*


class BannerActivity : AppCompatActivity(), AdListener {

    private lateinit var banner: BannerAdView
    private lateinit var layout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banner)
        layout = findViewById(R.id.linearLayout)
        val adSizeArrayList: ArrayList<AdSize?> = ArrayList<AdSize?>()
        adSizeArrayList.add(AdSize(320, 250)) // Primary Size
        adSizeArrayList.add(AdSize( 400, 300))

        banner = BannerAdView(this)
        banner.adListener = this // AdListener
        banner.setAllowBannerDemand(false)
        banner.allowVideoDemand = true
        banner.placementID = "17058950" // PlacementID
        banner.adSizes = adSizeArrayList
       // banner.setForceCreativeId(182434863)// Landscape Creative
        banner.setForceCreativeId(414238306);// Portrait/Vertical Video Creative



        //New API Option - 1 - start
        // banner.resizeBannerVideoToFitContainer = true;
        //New API Option - 1 - end

        //New API Option  - 2 - start
        //banner.videoExpandsToFitScreenWidth = true //Expanding Video as per screen width
        //New API Option  - 2 - end


        //New API Option  - 3 - start
        //ANVideoPlayerSettings.getVideoPlayerSettings().portraitBannerVideoPlayerSize = AdSize(300,400)
        //ANVideoPlayerSettings.getVideoPlayerSettings().landscapeBannerVideoPlayerSize = AdSize(300,250)
        //ANVideoPlayerSettings.getVideoPlayerSettings().squareBannerVideoPlayerSize = AdSize(200,200)
        //New API Option  - 3 - end




        banner.loadAd()
        layout.addView(banner)
    }


    override fun onAdLoaded(adView: AdView?) {
        log("Banner:: Ad Loaded")
        if((adView is BannerAdView) && adView.adResponseInfo.adType == AdType.VIDEO) {
            log("Banner:: Width="+ adView.creativeWidth)
            log("Banner:: Height="+adView.creativeHeight)
            val orientation: VideoOrientation = adView.videoOrientation
            log("Banner:: Video Creative orientation"+orientation.name)



            //New API Option  - 4 - start
            // Actual Width and Height of the video creative is exposed to publisher app.
            log("Banner:: Video Creative width"+ adView.bannerVideoCreativeWidth)
            log("Banner:: Video Creative height"+ adView.bannerVideoCreativeHeight)
            //New API Option  - 4 - end
        }
    }

    override fun onAdLoaded(nativeAdResponse: NativeAdResponse?) {
        log("Native Ad Loaded")
    }

    override fun onAdClicked(adView: AdView?) {
        log("Ad Clicked")
    }

    override fun onAdClicked(adView: AdView?, clickUrl: String?) {
        log("Ad Clicked with URL: $clickUrl")
    }

    override fun onAdExpanded(adView: AdView?) {
        log("Ad Expanded")
    }

    override fun onAdCollapsed(adView: AdView?) {
        log("Ad Collapsed")
    }

    override fun onAdRequestFailed(adView: AdView?, resultCode: ResultCode?) {
        log("Ad Failed: " + resultCode?.message)
    }

    override fun onLazyAdLoaded(adView: AdView?) {
        log("Ad onLazyAdLoaded")
    }

    override fun onAdImpression(adView: AdView?) {
        log("Ad onAdImpression")
    }

    private fun log(msg: String){
        Log.d("BannerActivity",msg)
        Toast.makeText(this.applicationContext, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        if (banner != null) {
            banner.activityOnDestroy()
        }
        super.onDestroy()
    }


}
