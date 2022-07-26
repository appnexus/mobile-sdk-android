package appnexus.com.trackertestapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.test.espresso.idling.CountingIdlingResource
import appnexus.com.trackertestapp.utility.Utils
import com.appnexus.opensdk.*
import com.appnexus.opensdk.tasksmanager.TasksManager
import com.appnexus.opensdk.utils.Settings
import com.squareup.picasso.Picasso
import java.util.concurrent.ExecutorService

class NativeActivity : AppCompatActivity(), NativeAdRequestListener, NativeAdEventListener {

    var didLogImpression: Boolean = false
    lateinit var nativeAdRequest: NativeAdRequest
    var idlingResource: CountingIdlingResource = CountingIdlingResource("Native Load Count", true)
    lateinit var nativeResponse: NativeAdResponse

    override fun onAdLoaded(nativeAdResponse: NativeAdResponse?) {
        Toast.makeText(this, "Native Ad Loaded", Toast.LENGTH_LONG).show()
        TasksManager.getInstance().executeOnMainThread {
            handleNativeResponse(nativeAdResponse)
        }
    }

    override fun onAdFailed(errorcode: ResultCode?, adResponseinfo: ANAdResponseInfo?) {
        Toast.makeText(this, "Native Ad Failed", Toast.LENGTH_LONG).show()
        if (!idlingResource.isIdleNow)
            idlingResource.decrement()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_native)
        Settings.getSettings().useHttps = true
    }

    override fun onDestroy() {
        super.onDestroy()
        if (nativeResponse != null){
            nativeResponse.destroy()
        }
    }

    fun removeNativeAd(){
        if (nativeResponse != null){
            nativeResponse.destroy()
        }
    }

    fun triggerAdLoad(
        placement: String?,
        useHttps: Boolean = true,
        creativeId: Int? = null,
        bgTask: Boolean = false,
        useExecutor: Boolean = false
    ) {
        SDKSettings.enableBackgroundThreading(bgTask)
        Handler(Looper.getMainLooper()).post {

            didLogImpression = false
            nativeAdRequest = NativeAdRequest(this, if (placement == null) "17982237" else placement)
            nativeAdRequest.clickThroughAction = ANClickThroughAction.RETURN_URL
            nativeAdRequest.placementID = if (placement == null) "17982237" else placement
            nativeAdRequest.listener = this
            if (creativeId != null) {
                val utils = Utils()
                utils.setForceCreativeId(creativeId, nativeAdRequest = nativeAdRequest);
            }


            if (useExecutor) {
                TasksManager.getInstance().executeOnBackgroundThread({
                    nativeAdRequest.loadAd()
                })
            } else {
                nativeAdRequest.loadAd()
            }
            idlingResource.increment()
        }
    }

    private fun handleNativeResponse(response: NativeAdResponse?) {
        if (response != null) {
            nativeResponse = response
        val icon: ImageView = findViewById(R.id.icon)
        val image: ImageView = findViewById(R.id.image)
        val title: TextView = findViewById(R.id.title)
        val desc: TextView = findViewById(R.id.description)
        title.setText(response?.title)
        desc.setText(response?.description)
        if (!idlingResource.isIdleNow)
            idlingResource.decrement()


            if (response.iconUrl != null){
                Picasso.get()
                    .load(response.iconUrl)
                    .resize(50, 50)
                    .placeholder(R.drawable.download)
                    .error(R.drawable.images)
                    .into(icon)
            }
            if (response.imageUrl != null){
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
        didLogImpression = true
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
}
