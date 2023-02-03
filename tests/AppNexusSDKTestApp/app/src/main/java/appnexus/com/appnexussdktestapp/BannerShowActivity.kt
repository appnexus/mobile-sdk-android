package appnexus.com.appnexussdktestapp

import android.os.Build
import android.os.Bundle
import android.webkit.WebView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.test.espresso.idling.CountingIdlingResource
import com.appnexus.opensdk.BannerAdView
import com.appnexus.opensdk.utils.Settings

class BannerShowActivity : AppCompatActivity() {

    lateinit var banner: BannerAdView
    var clickUrl: String? = ""
    var onAdImpression = false
    var idlingResource: CountingIdlingResource = CountingIdlingResource("Banner Load Count", true)

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

        displayBanner(BannerInitActivity.banner)
    }

    override fun onDestroy() {
        if (banner != null) {
            banner.activityOnDestroy()
        }
        super.onDestroy()
    }

    private fun displayBanner(bannerAdView: BannerAdView) {
        layout.addView(bannerAdView)
    }
}
