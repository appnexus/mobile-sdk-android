package appnexus.com.trackertestapp

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.webkit.WebView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.test.espresso.idling.CountingIdlingResource
import appnexus.com.trackertestapp.utility.Utils
import com.appnexus.opensdk.*
import com.appnexus.opensdk.utils.Settings
import com.squareup.picasso.Picasso

class BannerActivity : AppCompatActivity(), AdListener, AppEventListener, NativeAdEventListener {

    val banner_id: Int = 1234
    lateinit var banner: BannerAdView
    var clickUrl: String? = ""
    var shouldDisplay = true
    var idlingResource: CountingIdlingResource = CountingIdlingResource("Banner Load Count", true)

    override fun onAdClicked(p0: AdView?) {
        clickUrl = ""
        Toast.makeText(this, "Ad Clicked", Toast.LENGTH_LONG).show()
    }

    override fun onAdClicked(p0: AdView?, p1: String?) {
        clickUrl = p1
        Toast.makeText(this, "Ad Clicked with URL", Toast.LENGTH_LONG).show()
    }

    override fun onAdExpanded(p0: AdView?) {
        Toast.makeText(this, "Ad Expanded", Toast.LENGTH_LONG).show()
    }

    override fun onAdCollapsed(p0: AdView?) {
        Toast.makeText(this, "Ad Collapsed", Toast.LENGTH_LONG).show()
    }

    override fun onAdRequestFailed(p0: AdView?, p1: ResultCode?) {
        Toast.makeText(this, "Ad Failed: " + p1?.message, Toast.LENGTH_LONG).show()
    }

    override fun onLazyAdLoaded(adView: AdView?) {
        if (!idlingResource.isIdleNow)
            idlingResource.decrement()
    }

    override fun onAdLoaded(ad: AdView?) {
        Toast.makeText(this, "AdLoaded", Toast.LENGTH_LONG).show()
        if (layout.childCount > 0)
            layout.removeAllViews()
        if (!shouldDisplay) {
            banner.visibility = View.GONE
        }
        layout.addView(ad)
        if (!idlingResource.isIdleNow)
            idlingResource.decrement()
    }

    override fun onAdLoaded(nativeAdResponse: NativeAdResponse?) {
        Toast.makeText(this, "Native Ad Loaded", Toast.LENGTH_LONG).show()
        handleNativeResponse(nativeAdResponse)
    }


    lateinit var layout: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banner)
        layout = findViewById(R.id.linearLayout)

        Settings.getSettings().debug_mode = true
        Settings.getSettings().useHttps = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
    }

    fun triggerAdLoad(placement: String?, width: Int = 300, height: Int = 250, useHttps: Boolean = true, allowNativeDemand: Boolean = false, allowVideoDemand: Boolean = false, rendererId: Int = -1, useNativeRenderer: Boolean = false, clickThroughAction: ANClickThroughAction = ANClickThroughAction.OPEN_SDK_BROWSER, resizeToFitContainer: Boolean = false, expandsToFitScreenWidth: Boolean = false, creativeId: Int? = null, bgTask: Boolean = false, countImpressionOnAdLoad: Boolean = false, onePx: Boolean = false, lazyLoad: Boolean = false) {

        SDKSettings.enableBackgroundThreading(bgTask)
        Handler(Looper.getMainLooper()).post {

            idlingResource.increment()
            banner = BannerAdView(this)
            banner.countImpressionOnAdLoad = countImpressionOnAdLoad
            if (lazyLoad) {
                banner.enableLazyLoad()
            }
            banner.appEventListener = this
            banner.id = banner_id
            banner.placementID = if (placement == null) "17982237" else placement
            banner.setAdSize(width, height)
            banner.setAllowBannerDemand(true)
            banner.setAllowNativeDemand(allowNativeDemand, rendererId)
            banner.enableNativeRendering(useNativeRenderer)
            banner.loadsInBackground = false
            banner.clickThroughAction = ANClickThroughAction.RETURN_URL
            banner.allowVideoDemand = allowVideoDemand
            banner.resizeAdToFitContainer = resizeToFitContainer
            banner.expandsToFitScreenWidth = expandsToFitScreenWidth
            banner.adListener = this
            if(creativeId != null) {
                val utils = Utils()
                utils.setForceCreativeId(creativeId, banner = banner);
            }
            banner.loadAd()
        }
    }

    fun removeBannerAd(){
        banner.removeAllViews()
        banner.destroy()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (banner != null){
            banner.destroy()
        }
    }

    fun loadLazyAd() {
        if (banner.isLazyLoadEnabled) {
            banner.loadLazyAd()
            idlingResource.increment()
        }
    }

    private fun handleNativeResponse(response: NativeAdResponse?) {

        val nativeView: View = View.inflate(this, R.layout.layout_native, null)
        val icon: ImageView = nativeView.findViewById(R.id.icon)
        val image: ImageView = nativeView.findViewById(R.id.image)
        val title: TextView = nativeView.findViewById(R.id.title)
        val desc: TextView = nativeView.findViewById(R.id.description)

        if (response != null) {
        title.setText(response.title)
        desc.setText(response.description)
        layout.addView(nativeView)
        if (!idlingResource.isIdleNow)
            idlingResource.decrement()
            if (response.iconUrl != null) {
                Picasso.get()
                    .load(response.iconUrl)
                    .resize(50, 50)
                    .placeholder(R.drawable.download)
                    .error(R.drawable.images)
                    .into(icon)
            }
            if (response.imageUrl != null) {
                Picasso.get()
                    .load(response.imageUrl)
                    .placeholder(R.drawable.download)
                    .error(R.drawable.images)
                    .into(image)
            }
        }

        NativeAdSDK.registerTracking(response, findViewById(R.id.main_native), this)
    }

    override fun onAdImpression() {
    }

    override fun onAdAboutToExpire() {

    }

    override fun onAdWasClicked() {

    }

    override fun onAdWasClicked(clickUrl: String?, fallbackURL: String?) {
    }

    override fun onAdExpired() {
    }

    override fun onAdWillLeaveApplication() {
    }

    override fun onAppEvent(adView: AdView?, name: String?, data: String?) {

    }

}
