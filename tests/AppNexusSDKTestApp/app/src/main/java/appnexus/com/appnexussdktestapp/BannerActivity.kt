package appnexus.com.appnexussdktestapp

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.webkit.WebView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.test.espresso.idling.CountingIdlingResource
import appnexus.com.appnexussdktestapp.utility.Utils
import com.appnexus.opensdk.*
import com.appnexus.opensdk.utils.Settings
import com.appnexus.opensdk.utils.ViewUtil
import com.squareup.picasso.Picasso

class BannerActivity : AppCompatActivity(), AdListener {

    private var startTime: Long = 0L
    private var finalTime: Long = 0L
    private lateinit var nativeView: View
    var impressionLogged = false
    var shouldDisplay: Boolean = true
    val banner_id: Int = 1234
    lateinit var banner: BannerAdView
    var clickUrl: String? = ""
    var onAdImpression = false
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
        finalTime = System.currentTimeMillis()
        Toast.makeText(this, "Ad Failed: " + p1?.message, Toast.LENGTH_LONG).show()
        println(p1?.message)
        if (!idlingResource.isIdleNow)
            idlingResource.decrement()
    }

    override fun onLazyAdLoaded(adView: AdView?) {
    }

    override fun onAdImpression(adView: AdView?) {
        Toast.makeText(this, "Ad Impression", Toast.LENGTH_LONG).show()
        onAdImpression = true;
    }

    override fun onAdLoaded(ad: AdView?) {
        finalTime = System.currentTimeMillis()
        Toast.makeText(this, "AdLoaded", Toast.LENGTH_LONG).show()
        if (layout.childCount > 0)
            layout.removeAllViews()
        if (!shouldDisplay) {
            ad!!.visibility = View.GONE
        }
        layout.addView(ad)
        if (!idlingResource.isIdleNow)
            idlingResource.decrement()
    }

    override fun onAdLoaded(nativeAdResponse: NativeAdResponse?) {
        finalTime = System.currentTimeMillis()
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
//        triggerAdLoad("17982237", allowNativeDemand = true, creativeId = 162039377)
//        triggerAdLoad("15754688", allowNativeDemand = true, rendererId = 199, useNativeRenderer = true, creativeId = 154485807)


//        triggerAdLoad(
//            intent.getStringExtra("placement"),
//            width = intent.getIntExtra("width", 300),
//            height = intent.getIntExtra("height", 250),
//            useHttps = intent.getBooleanExtra("useHttps", true)
//        )
    }

    fun triggerAdLoad(placement: String?, width: Int = 300, height: Int = 250, useHttps: Boolean = true, allowNativeDemand: Boolean = false, allowVideoDemand: Boolean = false, rendererId: Int = -1, useNativeRenderer: Boolean = false, clickThroughAction: ANClickThroughAction = ANClickThroughAction.OPEN_SDK_BROWSER, resizeToFitContainer: Boolean = false, expandsToFitScreenWidth: Boolean = false, creativeId: Int? = null, bgTask: Boolean = false, visibility: Int = VISIBLE) {

        onAdImpression = false
        SDKSettings.enableBackgroundThreading(bgTask)
        Handler(Looper.getMainLooper()).post {

            idlingResource.increment()
            banner = BannerAdView(this)
            banner.visibility = visibility
            banner.id = banner_id
            banner.placementID = if (placement == null) "17982237" else placement
            banner.setAdSize(width, height)
            banner.setAllowNativeDemand(allowNativeDemand)
            banner.enableNativeRendering(useNativeRenderer)
            banner.loadsInBackground = false
            banner.clickThroughAction = clickThroughAction
            banner.allowVideoDemand = allowVideoDemand
            banner.resizeAdToFitContainer = resizeToFitContainer
            banner.expandsToFitScreenWidth = expandsToFitScreenWidth
            banner.setAllowBannerDemand(!(allowNativeDemand || allowVideoDemand))

            banner.adListener = this
            if(creativeId != null) {
                val utils = Utils()
                utils.setForceCreativeId(creativeId, banner = banner);
            }
            startTime = System.currentTimeMillis()
            banner.loadAd()
        }
    }

    private fun handleNativeResponse(response: NativeAdResponse?) {

        nativeView = View.inflate(this, R.layout.layout_native, null)
        val icon: ImageView = nativeView.findViewById(R.id.icon)
        val image: ImageView = nativeView.findViewById(R.id.image)
        val title: TextView = nativeView.findViewById(R.id.title)
        val desc: TextView = nativeView.findViewById(R.id.description)

        if (response?.iconUrl != null) Picasso.get().load(response.iconUrl).resize(40, 40).into(icon)
        if (response?.imageUrl != null) Picasso.get().load(response.imageUrl).into(image)
        title.setText(response?.title)
        desc.setText(response?.description)
        if (shouldDisplay) {
            layout.addView(nativeView)
        }
        NativeAdSDK.registerTracking(response, nativeView, object: NativeAdEventListener {
            override fun onAdImpression() {
                impressionLogged = true
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

        })
        if (!idlingResource.isIdleNow)
            idlingResource.decrement()
    }

    override fun onDestroy() {
        if (banner != null) {
            banner.activityOnDestroy()
        }
        super.onDestroy()
    }

    fun attachNative(display: Boolean = true) {
        Handler(Looper.getMainLooper()).post({
            if (!display) {
                nativeView.visibility = GONE
            } else {
                nativeView.visibility = VISIBLE
            }
            ViewUtil.removeChildFromParent(nativeView)
            layout.addView(nativeView)
        })
    }

    fun displayBanner(display: Boolean = true) {
        Handler(Looper.getMainLooper()).post({
            if (!display) {
                banner.visibility = GONE
            } else {
                banner.visibility = VISIBLE
            }
        })
    }

    fun getTime(): Long {
        val totalTime = finalTime - startTime
        Log.e("TOTAL TIME", "$totalTime")
        return totalTime
    }
}
