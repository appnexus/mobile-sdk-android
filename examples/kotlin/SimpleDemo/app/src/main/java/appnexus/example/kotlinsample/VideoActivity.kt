package appnexus.example.kotlinsample

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.appnexus.opensdk.ResultCode
import com.appnexus.opensdk.instreamvideo.*


class VideoActivity : AppCompatActivity(), VideoAdLoadListener, VideoAdPlaybackListener {

    private lateinit var baseContainer: RelativeLayout
    private lateinit var playButon: ImageButton
    private lateinit var videoPlayer: VideoView
    private lateinit var instreamVideoAd: VideoAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instream)
        initContentPlayer()

        instreamVideoAd = VideoAd(this, "17058950")
        instreamVideoAd.adLoadListener = this

        playButon.setOnClickListener {
            if (instreamVideoAd.isReady()) {
                instreamVideoAd.playAd(baseContainer)
            } else {
                videoPlayer.start()
            }
            playButon.visibility = View.INVISIBLE
        }

        // Set PlayBack Listener.
        instreamVideoAd.setVideoPlaybackListener(this)
        instreamVideoAd.loadAd()

    }

    // Required for Auto-Pausing the Ad on activity pause.
    override fun onPause() {
        super.onPause()
        instreamVideoAd.activityOnPause()
    }

    // Requried for Auto-Resuming the Ad on when Activity resumes
    override fun onResume() {
        super.onResume()
        instreamVideoAd.activityOnResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        instreamVideoAd.activityOnDestroy()
    }

    // VideoAdLoadListener - Start
    override fun onAdLoaded(videoAd: VideoAd) {
        log("Ad is ready. Hit on Play button to start")
        playButon.setVisibility(View.VISIBLE)
    }

    override fun onAdRequestFailed(videoAd: VideoAd, errorCode: ResultCode) {
        log("Video Ad Failed: " + errorCode.name)
        playButon.setVisibility(View.VISIBLE)
    }
    // VideoAdLoadListener - End


    // VideoAdPlaybackListener - Start
    override fun onAdPlaying(videoAd: VideoAd) {
        log("Ad play has started")
    }

    override fun onQuartile(view: VideoAd, quartile: Quartile) {
        log("onQuartile::$quartile")
    }

    override fun onAdCompleted(view: VideoAd, playbackState: VideoAdPlaybackListener.PlaybackCompletionState) {
        log("Video Ad Completed: " + playbackState.name);
        videoPlayer.start()
    }

    override fun onAdMuted(view: VideoAd, isMute: Boolean) {
        log("isAudioMute::$isMute")
    }

    override fun onAdClicked(adView: VideoAd) {
        log("Ad Clicked")
    }

    override fun onAdClicked(videoAd: VideoAd, clickUrl: String) {
        log("Ad Clicked with URL: $clickUrl")
    }
    // VideoAdPlaybackListener - End


    private fun initContentPlayer() {
        playButon = findViewById(R.id.play_button)
        videoPlayer = findViewById(R.id.video_player)
        baseContainer = findViewById(R.id.instream_container_Layout)
        videoPlayer.setVideoURI(Uri.parse(getString(R.string.content_url_1)))
        val controller = MediaController(this)
        videoPlayer.setMediaController(controller)
    }

    private fun log(msg: String){
        Log.d("VideoActivity",msg)
        Toast.makeText(this.applicationContext, msg, Toast.LENGTH_LONG).show()
    }

}
