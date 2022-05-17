package appnexus.example.kotlinsample

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appnexus.opensdk.*

class InterstitialActivity : AppCompatActivity(), AdListener {

    private lateinit var interstitial: InterstitialAdView

    private fun showAd() {
        interstitial.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banner)
        interstitial = InterstitialAdView(this)
        interstitial.placementID = "17058950"
        interstitial.adListener = this
        interstitial.loadAd()
    }

    override fun onAdLoaded(ad: AdView?) {
        log("Interstitial Ad Loaded")
        showAd()
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
        finish()
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
        Log.d("InterstitialActivity",msg)
        Toast.makeText(this.applicationContext, msg, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        if (interstitial != null) {
            interstitial.activityOnDestroy()
        }
        super.onDestroy()
    }

}
