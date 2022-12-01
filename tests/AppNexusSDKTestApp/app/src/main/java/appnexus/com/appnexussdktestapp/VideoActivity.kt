package appnexus.com.appnexussdktestapp

import android.app.PendingIntent.getActivity
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.*
import androidx.test.espresso.idling.CountingIdlingResource
import appnexus.com.appnexussdktestapp.utility.Utils
import com.appnexus.opensdk.*
import com.appnexus.opensdk.instreamvideo.Quartile
import com.appnexus.opensdk.instreamvideo.VideoAd
import com.appnexus.opensdk.instreamvideo.VideoAdLoadListener
import com.appnexus.opensdk.instreamvideo.VideoAdPlaybackListener
import com.appnexus.opensdk.utils.Clog
import com.appnexus.opensdk.utils.Settings

class VideoActivity : AppCompatActivity(), VideoAdLoadListener {

    private var startTime: Long = 0L
    private var finalTime: Long = 0L
    private lateinit var baseContainer: RelativeLayout
    private lateinit var playButon: ImageButton
    private lateinit var videoPlayer: VideoView
    private lateinit var context: Context

    override fun onAdLoaded(videoAd: VideoAd?) {
        finalTime = System.currentTimeMillis()
        Toast.makeText(
            this, "Ad is ready. Hit on Play button to start",
            Toast.LENGTH_SHORT
        ).show()
        playButon.setVisibility(View.VISIBLE)
        idlingResource.decrement()
    }

    override fun onAdRequestFailed(videoAd: VideoAd?, errorCode: ResultCode?) {
        finalTime = System.currentTimeMillis()
        Toast.makeText(this, "Ad Failed: " + errorCode?.message, Toast.LENGTH_LONG).show()
        println(errorCode?.message)
        idlingResource.decrement()
    }

    lateinit var video: VideoAd
    var idlingResource: CountingIdlingResource = CountingIdlingResource("Banner Load Count", true)


    lateinit var layout: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banner)
        context = this
        Settings.getSettings().useHttps = true
        Settings.getSettings().debug_mode = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        layout = findViewById(R.id.linearLayout)
//        triggerAdLoad(
//            intent.getStringExtra("placement"),
//            useHttps = intent.getBooleanExtra("useHttps", true)
//        )
    }

    fun triggerAdLoad(placement: String?, useHttps: Boolean = true, creativeId: Int? = null, bgTask: Boolean = false) {
        Handler(Looper.getMainLooper()).post {
            SDKSettings.enableBackgroundThreading(bgTask)
            video = VideoAd(this, if (placement == null) "13989299" else placement)
            video.adLoadListener = this
            // Load and display a Video
            // Video Ad elements
            val instreamVideoLayout = layoutInflater.inflate(R.layout.fragment_preview_instream, null)
            playButon = instreamVideoLayout.findViewById(R.id.play_button) as ImageButton
            videoPlayer = instreamVideoLayout.findViewById(R.id.video_player) as VideoView
            baseContainer = instreamVideoLayout.findViewById(R.id.instream_container_Layout) as RelativeLayout
            layout.addView(baseContainer)

            baseContainer.layoutParams.height = 1000
            baseContainer.layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT

            videoPlayer.setVideoURI(Uri.parse(getString(R.string.content_url_1)))
            var controller = MediaController(this)
            videoPlayer.setMediaController(controller)

            playButon.setOnClickListener {
                if (video.isReady()) {
                    video.playAd(baseContainer)
                    idlingResource.increment()
                } else {
                    videoPlayer.start()
                }
                playButon.visibility = View.INVISIBLE
            }

            if(creativeId != null) {
                Clog.e("VIDEO", creativeId.toString())
                val utils = Utils()
                utils.setForceCreativeId(creativeId, video = video);
            }

            // Set PlayBack Listener.
            video.setVideoPlaybackListener(object : VideoAdPlaybackListener {

                override fun onAdPlaying(videoAd: VideoAd) {
//                setIdleState(true)
                    if (!idlingResource.isIdleNow)
                        Handler(Looper.getMainLooper()).postDelayed(Runnable { idlingResource.decrement() }, 2000)
                    Toast.makeText(context, "OnAdPlaying", Toast.LENGTH_SHORT).show()
//                Clog.d(Constants.BASE_LOG_TAG, "onAdPlaying::")
                }

                override fun onQuartile(view: VideoAd, quartile: Quartile) {
//                Clog.d(Constants.BASE_LOG_TAG, "onQuartile::$quartile")
                }

                override fun onAdCompleted(
                    view: VideoAd,
                    playbackState: VideoAdPlaybackListener.PlaybackCompletionState
                ) {
                    if (playbackState == VideoAdPlaybackListener.PlaybackCompletionState.COMPLETED) {
//                    Clog.d(Constants.BASE_LOG_TAG, "adCompleted::playbackState")
                    } else if (playbackState == VideoAdPlaybackListener.PlaybackCompletionState.SKIPPED) {
//                    Clog.d(Constants.BASE_LOG_TAG, "adSkipped::")
                    }
                    videoPlayer.start()
                }

                override fun onAdMuted(view: VideoAd, isMute: Boolean) {
//                Clog.d(Constants.BASE_LOG_TAG, "isAudioMute::$isMute")
                }

                override fun onAdClicked(adView: VideoAd) {
//                Clog.d(Constants.BASE_LOG_TAG, "onAdClicked")
                }

                override fun onAdClicked(videoAd: VideoAd, clickUrl: String) {
//                Clog.d(Constants.BASE_LOG_TAG, "onAdClicked::clickUrl$clickUrl")
//                toast("onAdClicked::clickUrl$clickUrl")
                }
            })

            startTime = System.currentTimeMillis()
            video.loadAd()
            idlingResource.increment()
        }
    }

    override fun onDestroy() {
        if (video != null) {
            video.activityOnDestroy()
        }
        super.onDestroy()
    }

    fun getTime(): Long {
        val totalTime = finalTime - startTime
        Log.e("TOTAL TIME", "$totalTime")
        return totalTime
    }

}
