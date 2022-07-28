package appnexus.com.appnexussdktestapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.test.espresso.idling.CountingIdlingResource
import appnexus.com.appnexussdktestapp.utility.Utils
import com.appnexus.opensdk.*
import com.appnexus.opensdk.tasksmanager.TasksManager
import com.appnexus.opensdk.utils.Settings
import com.appnexus.opensdk.utils.ViewUtil
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_impression_native.*

class NativeImpressionActivity : AppCompatActivity(), NativeAdRequestListener,
    NativeAdEventListener {

    var shouldAttachToDummy: Boolean = false
    var shouldDisplay: Boolean = true
    var didLogImpression: Boolean = false
    lateinit var nativeAdRequest: NativeAdRequest
    var idlingResource: CountingIdlingResource = CountingIdlingResource("Native Load Count", true)
    lateinit var mainLayout: View

    override fun onAdLoaded(nativeAdResponse: NativeAdResponse?) {
        Toast.makeText(this, "Native Ad Loaded", Toast.LENGTH_LONG).show()
        TasksManager.getInstance().executeOnMainThread {
            handleNativeResponse(nativeAdResponse)
        }
    }

    override fun onAdFailed(errorcode: ResultCode?, adResponseinfo: ANAdResponseInfo?) {
        if (!idlingResource.isIdleNow)
            idlingResource.decrement()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_impression_native)
        mainLayout = View.inflate(this, R.layout.layout_native, null);
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
        if (shouldDisplay) {
            Handler(Looper.getMainLooper()).post({
                main_parent.addView(mainLayout)
            })
        }

        if (shouldAttachToDummy) {
            Handler(Looper.getMainLooper()).post({
                ViewUtil.removeChildFromParent(mainLayout)
                LinearLayout(this).addView(mainLayout)
            })
        }


        SDKSettings.enableBackgroundThreading(bgTask)
        Handler(Looper.getMainLooper()).post {

            didLogImpression = false
            nativeAdRequest =
                NativeAdRequest(this, if (placement == null) "17982237" else placement)
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

        val icon: ImageView = mainLayout.findViewById(R.id.icon)
        val image: ImageView = mainLayout.findViewById(R.id.image)
        val title: TextView = mainLayout.findViewById(R.id.title)
        val desc: TextView = mainLayout.findViewById(R.id.description)

        if (response?.iconUrl != null) Picasso.get().load(response.iconUrl).resize(40, 40)
            .into(icon)
        if (response?.imageUrl != null) Picasso.get().load(response.imageUrl).into(image)
        title.setText(response?.title)
        desc.setText(response?.description)
        NativeAdSDK.registerTracking(response, mainLayout, this)
        if (!idlingResource.isIdleNow)
            idlingResource.decrement()

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

    fun attachNative() {
        Handler(Looper.getMainLooper()).post({
            ViewUtil.removeChildFromParent(mainLayout)
            main_parent.addView(mainLayout)
        })
    }

    fun changeVisibility(visible: Int) {
        Handler(Looper.getMainLooper()).post({
            mainLayout?.visibility = visible
        })
    }
}
