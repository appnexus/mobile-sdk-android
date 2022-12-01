package appnexus.com.appnexussdktestapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.test.espresso.idling.CountingIdlingResource
import appnexus.com.appnexussdktestapp.utility.Utils
import com.appnexus.opensdk.*
import com.appnexus.opensdk.tasksmanager.TasksManager
import com.appnexus.opensdk.utils.Settings
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_native.*

class NativeActivity : AppCompatActivity(), NativeAdRequestListener, NativeAdEventListener {

    private var startTime: Long = 0L
    private var finalTime: Long = 0L
    var shouldDisplay: Boolean = true
    var didLogImpression: Boolean = false
    lateinit var nativeAdRequest: NativeAdRequest
    var idlingResource: CountingIdlingResource = CountingIdlingResource("Native Load Count", true)

    override fun onAdLoaded(nativeAdResponse: NativeAdResponse?) {
        finalTime = System.currentTimeMillis()
        Toast.makeText(this, "Native Ad Loaded", Toast.LENGTH_LONG).show()
        TasksManager.getInstance().executeOnMainThread {
            handleNativeResponse(nativeAdResponse)
        }
    }

    override fun onAdFailed(errorcode: ResultCode?, adResponseinfo: ANAdResponseInfo?) {
        finalTime = System.currentTimeMillis()
        if (!idlingResource.isIdleNow)
            idlingResource.decrement()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_native)
        Settings.getSettings().useHttps = true
//        triggerAdLoad("17982237", creativeId = 162039377)
//        triggerAdLoad(
//            intent.getStringExtra("placement"),
//            useHttps = intent.getBooleanExtra("useHttps", true)
//        )
    }

    fun triggerAdLoad(
        placement: String?,
        useHttps: Boolean = true,
        creativeId: Int? = null,
        bgTask: Boolean = false,
        useExecutor: Boolean = false
    ) {
        if (!shouldDisplay) {
            Handler(Looper.getMainLooper()).post({
                main_native.visibility = View.GONE
            })
        }
        SDKSettings.enableBackgroundThreading(bgTask)
        Handler(Looper.getMainLooper()).post {

            didLogImpression = false
            nativeAdRequest = NativeAdRequest(this, if (placement == null) "17982237" else placement)
            nativeAdRequest.placementID = if (placement == null) "17982237" else placement
            nativeAdRequest.listener = this
            if (creativeId != null) {
                val utils = Utils()
                utils.setForceCreativeId(creativeId, nativeAdRequest = nativeAdRequest);
            }

            startTime = System.currentTimeMillis()
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

        val icon: ImageView = findViewById(R.id.icon)
        val image: ImageView = findViewById(R.id.image)
        val title: TextView = findViewById(R.id.title)
        val desc: TextView = findViewById(R.id.description)

        if (response?.iconUrl != null) Picasso.get().load(response.iconUrl).resize(40, 40).into(icon)
        if (response?.imageUrl != null) Picasso.get().load(response.imageUrl).into(image)
        title.setText(response?.title)
        desc.setText(response?.description)
        if (!idlingResource.isIdleNow)
            idlingResource.decrement()

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

    fun getTime(): Long {
        val totalTime = finalTime - startTime
        Log.e("TOTAL TIME", "$totalTime")
        return totalTime
    }
}
