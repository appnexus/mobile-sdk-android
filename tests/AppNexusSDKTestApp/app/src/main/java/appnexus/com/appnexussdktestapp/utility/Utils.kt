package appnexus.com.appnexussdktestapp.utility

import com.appnexus.opensdk.BannerAdView
import com.appnexus.opensdk.InterstitialAdView
import com.appnexus.opensdk.MediaType
import com.appnexus.opensdk.NativeAdRequest
import com.appnexus.opensdk.instreamvideo.VideoAd
import com.appnexus.opensdk.ut.UTRequestParameters
import com.appnexus.opensdk.utils.Clog
import java.lang.reflect.InvocationTargetException

class Utils {

    fun setForceCreativeId(
        creative_id: Int,
        banner: BannerAdView? = null,
        interstitial: InterstitialAdView? = null,
        nativeAdRequest: NativeAdRequest? = null,
        video: VideoAd? = null
    ) {
        try {
            var requestParameters: UTRequestParameters? = null

            if (video != null) {
                val videoAd = VideoAd::class.java
                val met = videoAd.getDeclaredMethod("getRequestParameters")
                met.isAccessible = true
                requestParameters = met.invoke(video) as UTRequestParameters
            } else if (banner != null) {
                requestParameters = banner?.getRequestParameters()
            } else if (interstitial != null) {
                requestParameters = interstitial?.getRequestParameters()
            } else if (nativeAdRequest != null) {
                requestParameters = nativeAdRequest?.getRequestParameters()
            }

            Clog.e("REQ_PARAMS", requestParameters.toString())

            val utParams = UTRequestParameters::class.java
            val met = utParams.getDeclaredMethod("setForceCreativeId", Int::class.javaPrimitiveType)
            met.isAccessible = true
            met.invoke(requestParameters, creative_id)


        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }

    }

    fun method(): Int {
        return 0
    }
}