package appnexus.example.kotlinsample

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.webkit.WebView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.appnexus.opensdk.SDKSettings
import com.appnexus.opensdk.utils.Settings

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        Settings.getSettings().debug_mode = true
        Settings.getSettings().useHttps = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        val btnBanner: Button = findViewById(R.id.btnBanner)
        btnBanner.setOnClickListener { goToNextActivity(BannerActivity::class.java) }

        val btnInterstitial: Button = findViewById(R.id.btnInterstitial)
        btnInterstitial.setOnClickListener { goToNextActivity(InterstitialActivity::class.java) }

        val btnNative: Button = findViewById(R.id.btnNative)
        btnNative.setOnClickListener { goToNextActivity(NativeActivity::class.java) }

        val btnVideo: Button = findViewById(R.id.btnVideo)
        btnVideo.setOnClickListener { goToNextActivity(VideoActivity::class.java) }
    }

    private fun goToNextActivity(adClass: Class<out AppCompatActivity>) {
        startActivity(Intent(this, adClass))
    }
}
