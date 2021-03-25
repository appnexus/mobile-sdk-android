package appnexus.com.trackertestapp

import android.net.Uri
import com.appnexus.opensdk.ut.UTConstants
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

class MockDispatcher : Dispatcher() {

    lateinit var adType: String
    var arrRequests = ArrayList<String>()
    override fun dispatch(request: RecordedRequest?): MockResponse {
        println("DISPATCH: " + request?.path)
        println("DISPATCH: " + request?.requestLine)
        if (!arrRequests.contains(request?.requestLine)) {
            request?.requestLine?.let { arrRequests.add(Uri.decode(it).toString()) }
        }
        when (adType) {
            "BannerNativeRenderAd" -> return MockResponse().setBody(TestResponsesUT.BANNER_NATIVE_RENDERER).setResponseCode(200)
            "BannerAd" -> return MockResponse().setBody(TestResponsesUT.BANNER_AD).setResponseCode(200)
            "BannerAdOMID" -> return MockResponse().setBody(TestResponsesUT.BANNER_OMID_Ad).setResponseCode(200)
            "BannerVideoAd" -> return MockResponse().setBody(TestResponsesUT.BANNER_VIDEO_AD).setResponseCode(200)
            "NativeAd" -> return MockResponse().setBody(TestResponsesUT.NATIVE_AD).setResponseCode(200)
            else -> {
                return MockResponse().setBody("").setResponseCode(200)
            }
        }
      }

}