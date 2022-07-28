package appnexus.com.appnexussdktestapp

/*
 *    Copyright 2020 APPNEXUS INC
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.webkit.WebView
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.test.espresso.idling.CountingIdlingResource
import appnexus.com.appnexussdktestapp.utility.Utils
import com.appnexus.opensdk.*
import com.appnexus.opensdk.utils.Clog
import com.appnexus.opensdk.utils.Settings
import com.squareup.picasso.Picasso
import org.json.JSONObject

class BannerOMIDActivity : AppCompatActivity(), AdListener, AppEventListener {

    private var msg: String = ""
    var appEventData: String? = ""
    var appEventName: String? = ""
    val banner_id: Int = 1234
    lateinit var banner: BannerAdView
    var clickUrl: String? = ""
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
        println(p1?.message)
        if (!idlingResource.isIdleNow)
            idlingResource.decrement()
    }

    override fun onLazyAdLoaded(adView: AdView?) {
    }

    override fun onAdImpression(adView: AdView?) {
        Toast.makeText(this, "Ad Impression", Toast.LENGTH_LONG).show()
    }

    override fun onAdLoaded(ad: AdView?) {
        Toast.makeText(this, "AdLoaded", Toast.LENGTH_LONG).show()
        if (layout.childCount > 0)
            layout.removeAllViews()
        layout.addView(ad)
        addObstructionView(0.0f);
        if (!idlingResource.isIdleNow)
            idlingResource.decrement()
    }

    override fun onAdLoaded(nativeAdResponse: NativeAdResponse?) {
        Toast.makeText(this, "Native Ad Loaded", Toast.LENGTH_LONG).show()
        handleNativeResponse(nativeAdResponse)
    }

    lateinit var layout: RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banner_omid)
        layout = findViewById(R.id.linearLayout)

        Settings.getSettings().debug_mode = true
        Settings.getSettings().useHttps = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        // Used for Manual testing (Trackers)
//        triggerAdLoad("17982237", allowNativeDemand = true, creativeId = 162039377)
//        triggerAdLoad("15754688", allowNativeDemand = true, rendererId = 199, useNativeRenderer = true, creativeId = 154485807)
//        triggerAdLoad(
//            intent.getStringExtra("placement"),
//            width = intent.getIntExtra("width", 300),
//            height = intent.getIntExtra("height", 250),
//            useHttps = intent.getBooleanExtra("useHttps", true)
//        )
    }

    fun triggerAdLoad(placement: String?, width: Int = 300, height: Int = 250, useHttps: Boolean = true, allowNativeDemand: Boolean = false, allowVideoDemand: Boolean = false, rendererId: Int = -1, useNativeRenderer: Boolean = false, clickThroughAction: ANClickThroughAction = ANClickThroughAction.OPEN_SDK_BROWSER, resizeToFitContainer: Boolean = false, expandsToFitScreenWidth: Boolean = false, creativeId: Int? = null) {

        Handler(Looper.getMainLooper()).post {

            idlingResource.increment()
            banner = BannerAdView(this)
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
            banner.autoRefreshInterval = 0
            banner.adListener = this
            banner.appEventListener = this
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

    override fun onAppEvent(adView: AdView?, name: String?, data: String?) {
        Clog.e ("APPEVENT NAME", name + ", DATA: " + data)
        if (data!!.contains("geometryChange", true)) {
            appEventName = name
            appEventData = data
            var obj = JSONObject (data.substring(data.indexOf("{")))
            val jsonObject = obj.getJSONObject("data").getJSONObject("adView")
            msg += " percentageInView = " + jsonObject.getString("percentageInView") + ", reasons = " + jsonObject.get("reasons")
            toast(msg)
            if (!idlingResource.isIdleNow)
                idlingResource.decrement()
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        Clog.e("MESSAGE", msg)
    }

    fun addObstructionView(alpha: Float, w: Int = 200, h: Int = 200) : View {
        var view = View(this)
        var params = RelativeLayout.LayoutParams(w, h)
        view.layoutParams = params
        view.setBackgroundColor(Color.parseColor("#000000"))
        view.alpha = alpha

        Handler(Looper.getMainLooper()).post {
            layout.addView(view)
            msg = "Added View with ALPHA set as $alpha"
        }
        return view
    }

    fun addObstructionView(visibility: Int) : View {
        var view = View(this)
        var params = RelativeLayout.LayoutParams(200, 200)
        view.layoutParams = params
        view.setBackgroundColor(Color.parseColor("#000000"))
        view.visibility = visibility
        var visString = when (visibility) {
            0 -> "VISIBLE"
            4 -> "INVISIBLE"
            8 -> "GONE"
            else -> "NONE"
        }

        View.GONE


        Handler(Looper.getMainLooper()).post {
            layout.addView(view)
            msg = "Added View with VISIBILITY set as $visString"
            toast(msg)
        }
        return view

    }

    fun addFriendlyObstruction(nonTransparentView: View) {
        if (banner != null) {
            banner.addFriendlyObstruction(nonTransparentView)
            msg = "Added as Friendly Obstruction"
        }
    }

    fun removeFriendlyObstruction(nonTransparentView: View) {
        if (banner != null) {
            banner.removeFriendlyObstruction(nonTransparentView)
            msg = "Removed as Friendly Obstruction"
        }
    }

    fun removeAllFriendlyObstructions() {
        if (banner != null) {
            banner.removeAllFriendlyObstructions()
            msg = "Removed all Friendly Obstructions"
        }
    }

    fun removeView(nonTransparentView: View) {
        Handler(Looper.getMainLooper()).post {
            layout.removeView(nonTransparentView)
            msg = "Removed View"
        }
    }

    override fun onDestroy() {
        if (banner != null) {
            banner.activityOnDestroy()
        }
        super.onDestroy()
    }
}
