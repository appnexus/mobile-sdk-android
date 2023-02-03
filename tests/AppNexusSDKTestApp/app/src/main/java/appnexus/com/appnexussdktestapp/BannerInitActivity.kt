package appnexus.com.appnexussdktestapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.webkit.WebView
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.test.espresso.idling.CountingIdlingResource
import appnexus.com.appnexussdktestapp.utility.Utils
import com.appnexus.opensdk.*
import com.appnexus.opensdk.utils.Settings

class BannerInitActivity : AppCompatActivity(), AdListener {

    private var startTime: Long = 0L
    private var finalTime: Long = 0L
    val banner_id: Int = 1234
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
//        if (layout.childCount > 0)
//            layout.removeAllViews()
//        if (!shouldDisplay) {
//            ad!!.visibility = View.GONE
//        }-
//        layout.addView(ad)
        if (!idlingResource.isIdleNow)
            idlingResource.decrement()
        var btn = findViewById<Button>(R.id.btnMoveToNextActivity)
        btn.isEnabled = true
        btn.setOnClickListener {
            startActivity(Intent(this@BannerInitActivity, BannerShowActivity::class.java))
        }
    }

    override fun onAdLoaded(nativeAdResponse: NativeAdResponse?) {
        finalTime = System.currentTimeMillis()
        Toast.makeText(this, "Native Ad Loaded", Toast.LENGTH_LONG).show()
    }

    lateinit var layout: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banner_init)
        layout = findViewById(R.id.linearLayout)

        XandrAd.init(10094, this, true) {
            Toast.makeText(this, "Init Finished with $it", Toast.LENGTH_SHORT).show()
        }

        Settings.getSettings().debug_mode = true
        Settings.getSettings().useHttps = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        triggerAdLoad("17058950")
//        triggerAdLoad("15754688", allowNativeDemand = true, rendererId = 199, useNativeRenderer = true, creativeId = 154485807)


//        triggerAdLoad(
//            intent.getStringExtra("placement"),
//            width = intent.getIntExtra("width", 300),
//            height = intent.getIntExtra("height", 250),
//            useHttps = intent.getBooleanExtra("useHttps", true)
//        )
    }

    private fun triggerAdLoad(placement: String?, width: Int = 300, height: Int = 250, useHttps: Boolean = true, allowNativeDemand: Boolean = false, allowVideoDemand: Boolean = false, rendererId: Int = -1, useNativeRenderer: Boolean = false, clickThroughAction: ANClickThroughAction = ANClickThroughAction.OPEN_SDK_BROWSER, resizeToFitContainer: Boolean = false, expandsToFitScreenWidth: Boolean = false, creativeId: Int? = null, bgTask: Boolean = false, visibility: Int = VISIBLE) {

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

    override fun onDestroy() {
        if (banner != null) {
            banner.activityOnDestroy()
        }
        super.onDestroy()
    }

    companion object {
        lateinit var banner: BannerAdView
    }
}
