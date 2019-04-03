package appnexus.com.appnexussdktestapp

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
import appnexus.com.appnexussdktestapp.utility.Utils
import com.appnexus.opensdk.*
import com.appnexus.opensdk.utils.Settings
import com.squareup.picasso.Picasso

class BannerActivity : AppCompatActivity(), AdListener {

    val banner_id: Int = 1234
    lateinit var banner: BannerAdView
    var idlingResource: CountingIdlingResource = CountingIdlingResource("Banner Load Count", true)

    override fun onAdClicked(p0: AdView?) {
        Toast.makeText(this, "Ad Clicked", Toast.LENGTH_LONG).show()
    }

    override fun onAdClicked(p0: AdView?, p1: String?) {
        Toast.makeText(this, "Ad Clicked with URL", Toast.LENGTH_LONG).show()
    }

    override fun onAdExpanded(p0: AdView?) {
        Toast.makeText(this, "Ad Expanded", Toast.LENGTH_LONG).show()
    }

    override fun onAdCollapsed(p0: AdView?) {
        Toast.makeText(this, "Ad Collapsed", Toast.LENGTH_LONG).show()
    }

    override fun onAdRequestFailed(p0: AdView?, p1: ResultCode?) {
        Toast.makeText(this, "Ad Failed: " + p1?.name, Toast.LENGTH_LONG).show()
        println(p1?.name)
        if (!idlingResource.isIdleNow)
            idlingResource.decrement()
    }

    override fun onAdLoaded(ad: AdView?) {
        Toast.makeText(this, "AdLoaded", Toast.LENGTH_LONG).show()
        if (layout.childCount > 0)
            layout.removeAllViews()
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

//        triggerAdLoad(
//            intent.getStringExtra("placement"),
//            width = intent.getIntExtra("width", 300),
//            height = intent.getIntExtra("height", 250),
//            useHttps = intent.getBooleanExtra("useHttps", true)
//        )
    }

    fun triggerAdLoad(placement: String?, width: Int = 300, height: Int = 250, useHttps: Boolean = true, allowNativeDemand: Boolean = false, allowVideoDemand: Boolean = false, creativeId: Int? = null) {

        Handler(Looper.getMainLooper()).post {

            idlingResource.increment()
            banner = BannerAdView(this)
            banner.id = banner_id
            banner.placementID = if (placement == null) "13255429" else placement
            banner.setAdSize(width, height)
            banner.allowNativeDemand = allowNativeDemand
            banner.allowVideoDemand = allowVideoDemand
            SDKSettings.useHttps(useHttps)
            banner.adListener = this
            if(creativeId != null) {
                val utils = Utils()
                utils.setForceCreativeId(creativeId, banner = banner);
            }
            banner.loadAd()
        }
    }

    private fun handleNativeResponse(response: NativeAdResponse?) {

        val nativeView: View = View.inflate(this, R.layout.layout_native, null)
        val icon: ImageView = nativeView.findViewById(R.id.icon)
        val image: ImageView = nativeView.findViewById(R.id.image)
        val title: TextView = nativeView.findViewById(R.id.title)
        val desc: TextView = nativeView.findViewById(R.id.description)

        if (response?.iconUrl != null) Picasso.get().load(response.iconUrl).resize(40, 40).into(icon)
        if (response?.imageUrl != null) Picasso.get().load(response.imageUrl).into(image)
        title.setText(response?.title)
        desc.setText(response?.description)
        layout.addView(nativeView)
        if (!idlingResource.isIdleNow)
            idlingResource.decrement()
    }
}
