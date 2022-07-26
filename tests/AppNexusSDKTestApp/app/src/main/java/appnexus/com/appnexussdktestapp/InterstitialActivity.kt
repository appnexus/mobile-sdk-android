package appnexus.com.appnexussdktestapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.test.espresso.idling.CountingIdlingResource
import appnexus.com.appnexussdktestapp.utility.Utils
import com.appnexus.opensdk.*
import com.appnexus.opensdk.utils.Settings

class InterstitialActivity : AppCompatActivity(), AdListener {

    var isAdCollapsed: Boolean = false
    var isAdExpanded: Boolean = false
    var idlingResource: CountingIdlingResource = CountingIdlingResource("Interstitial Load Counter", true)
    val interstitial_id: Int = 1235
    lateinit var interstitial: InterstitialAdView
    var autoDismiss: Int = -1

    override fun onAdClicked(p0: AdView?) {
        Toast.makeText(this, "Ad Clicked", Toast.LENGTH_LONG).show()
    }

    override fun onAdClicked(p0: AdView?, p1: String?) {
        Toast.makeText(this, "Ad Clicked with URL", Toast.LENGTH_LONG).show()
    }

    override fun onAdExpanded(p0: AdView?) {
        Toast.makeText(this, "Ad Expanded", Toast.LENGTH_LONG).show()
        isAdExpanded = true
    }

    override fun onAdCollapsed(p0: AdView?) {
        Toast.makeText(this, "Ad Collapsed", Toast.LENGTH_LONG).show()
        isAdCollapsed = true
//        if (!idlingResource.isIdleNow)
//            idlingResource.decrement()
    }

    override fun onAdRequestFailed(p0: AdView?, p1: ResultCode?) {
        Toast.makeText(this, "Ad Failed: " + p1?.message, Toast.LENGTH_LONG).show()
        println(p1?.message)
        if (!idlingResource.isIdleNow)
            idlingResource.decrement()
    }

    override fun onLazyAdLoaded(adView: AdView?) {
    }

    override fun onAdImpression(adView: AdView?) {
        Toast.makeText(this, "Ad Impression", Toast.LENGTH_LONG).show()
    }

    override fun onAdLoaded(ad: AdView?) {
        Toast.makeText(this, "AdLoaded", Toast.LENGTH_LONG).show()
        if (!idlingResource.isIdleNow)
            idlingResource.decrement()
        showAd()
    }

    private fun showAd() {
        interstitial.showWithAutoDismissDelay(autoDismiss)
//        idlingResource.increment()
    }

    override fun onAdLoaded(p0: NativeAdResponse?) {
        Toast.makeText(this, "Native Ad Loaded", Toast.LENGTH_LONG).show()
        if (!idlingResource.isIdleNow)
            idlingResource.decrement()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banner)
        Settings.getSettings().useHttps = true
//        triggerAdLoad("17982237", autoDismiss = 5)
//        triggerAdLoad(
//            intent.getStringExtra("placement"),
//            useHttps = intent.getBooleanExtra("useHttps", false),
//            autoDismiss = intent.getIntExtra("autoDismiss", -1)
//        )
    }

    fun triggerAdLoad(placement: String?, useHttps: Boolean = true, autoDismiss: Int = -1, closeButtonDelay: Int = 1, creativeId: Int? = null, bgTask: Boolean = false) {

        SDKSettings.enableBackgroundThreading(bgTask)
        Handler(Looper.getMainLooper()).post {

            this.autoDismiss = autoDismiss
            interstitial = InterstitialAdView(this)
            interstitial.id = interstitial_id
            interstitial.placementID = if (placement == null) "17982237" else placement
            interstitial.adListener = this
            interstitial.closeButtonDelay = closeButtonDelay
            if(creativeId != null) {
                val utils = Utils()
                utils.setForceCreativeId(creativeId, interstitial = interstitial);
            }
            interstitial.loadAd()
            idlingResource.increment()

        }
    }

    override fun onDestroy() {
        if (interstitial != null) {
            interstitial.activityOnDestroy()
        }
        super.onDestroy()
    }
}
