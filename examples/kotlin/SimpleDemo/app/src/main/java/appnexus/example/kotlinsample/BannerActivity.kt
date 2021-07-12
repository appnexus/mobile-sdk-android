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

        banner = BannerAdView(this)
        banner.placementID = "17058950" // PlacementID
        banner.setForceCreativeId(270957916)
        banner.setAdSize(300, 250) // Size
        banner.adListener = this // AdListener
        banner.loadAd()
        layout.addView(banner)
    }


    override fun onAdLoaded(ad: AdView?) {
        log("Banner Ad Loaded")
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

    private fun log(msg: String){
        Log.d("BannerActivity",msg)
        Toast.makeText(this.applicationContext, msg, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        if (banner != null) {
            banner.activityOnDestroy()
        }
        super.onDestroy()
    }


}
