package appnexus.example.kotlinsample

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appnexus.opensdk.*

class NativeActivity : AppCompatActivity(),NativeAdRequestListener,NativeAdEventListener {

    private lateinit var nativeAdRequest: NativeAdRequest
    private lateinit var nativeAdResponse: NativeAdResponse
    private lateinit var nativeContainer: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_native)
        nativeContainer = findViewById(R.id.an_native_container)

        nativeAdRequest = NativeAdRequest(this, "17058950")
        nativeAdRequest.shouldLoadImage(true)
        nativeAdRequest.shouldLoadIcon(true)
        nativeAdRequest.listener = this
        nativeAdRequest.loadAd()
    }

    // NativeAdRequestListener - Start
    override fun onAdLoaded(response: NativeAdResponse) {
        log("Native Ad Loaded")

        nativeAdResponse = response
        val icon: ImageView = findViewById(R.id.an_icon)
        val image: ImageView = findViewById(R.id.an_image)
        val title: TextView = findViewById(R.id.an_title)
        val sponsoredBy: TextView = findViewById(R.id.an_sponsoredBy)
        val description: TextView = findViewById(R.id.an_description)
        val clickThrough: Button = findViewById(R.id.an_clickThrough)


        icon.setImageBitmap(nativeAdResponse.icon)
        image.setImageBitmap(nativeAdResponse.image)
        title.text = nativeAdResponse.title
        sponsoredBy.text = "Sponsored By: ${nativeAdResponse.sponsoredBy}"
        description.text = nativeAdResponse.description
        clickThrough.text = nativeAdResponse.callToAction;

        NativeAdSDK.unRegisterTracking(nativeContainer) // if re-using the same container for showing multiple ad's need to first unregister, before registering.
        NativeAdSDK.registerTracking(nativeAdResponse, nativeContainer, mutableListOf(clickThrough) as List<View>?,this)
    }

    override fun onAdFailed(errorcode: ResultCode, adResponseinfo:ANAdResponseInfo) {
        log("Native Ad Failed: " + errorcode.name)
    }
    // NativeAdRequestListener - End


    // NativeAdEventListener - Start
    override fun onAdWasClicked() {
        log("Native Ad Clicked")
    }

    override fun onAdWillLeaveApplication() {
        log("Native Ad will leave application")
    }

    override fun onAdWasClicked(clickUrl: String, fallbackURL: String) {
        log("Native Ad Clicked with URL: $clickUrl ::: fallbackURL: $fallbackURL")
    }
    // NativeAdEventListener - End

    private fun log(msg: String){
        Log.d("NativeActivity",msg)
        Toast.makeText(this.applicationContext, msg, Toast.LENGTH_LONG).show()
    }

}
