package appnexus.com.appnexussdktestapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.appnexus.opensdk.AdView
import com.appnexus.opensdk.BannerAdView
import com.appnexus.opensdk.InterstitialAdView
import com.appnexus.opensdk.NativeAdResponse
import com.appnexus.opensdk.instreamvideo.Quartile
import com.appnexus.opensdk.instreamvideo.VideoAd
import com.appnexus.opensdk.instreamvideo.VideoAdPlaybackListener
import com.appnexus.opensdk.utils.Clog
import com.appnexus.opensdk.utils.ViewUtil
import com.xandr.lazyloaddemo.R
import kotlinx.android.synthetic.main.layout_ad_view.view.*
import java.util.*

class AdViewRecyclerAdapter(val items: ArrayList<Any?>, val context: Context) :
    RecyclerView.Adapter<AdViewRecyclerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.layout_ad_view, parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentAd = items.get(position)
        if (currentAd is NativeAdResponse) {
            handleNativeResponse(currentAd, holder.layoutMain)
        } else if (currentAd is AdView) {
            if (currentAd is InterstitialAdView) {
                holder.layoutMain.setOnClickListener({
                    currentAd.show()
                })
            } else {

                if (currentAd is BannerAdView) {
                    if (currentAd.isLazyLoadEnabled && currentAd.getTag(
                            R.string.button_tag
                        ) == null
                    ) {
                        val btn = Button(context)
                        btn.setText("Activate")
                        btn.setOnClickListener {
                            Clog.e("LAZYLOAD", "Webview Activated")
                            currentAd.loadLazyAd()
                            currentAd.setTag(R.string.button_tag, btn)
                        }
                        currentAd.setTag(R.string.button_tag, true)
                        holder.layoutMain.addView(btn)
                    } else {
                        if (currentAd.getTag(R.string.button_tag) is Button && currentAd.isLazyLoadEnabled) {
                            ViewUtil.removeChildFromParent(currentAd.getTag(R.string.button_tag) as Button)
                            ViewUtil.removeChildFromParent(currentAd)
                            holder.layoutMain.addView(currentAd)
                            Clog.e("LAZYLOAD", "Banner Added to the parent view")
                            currentAd.post({
                                Clog.e("WIDTH", "${currentAd.width}")
                                Clog.e("HEIGHT", "${currentAd.height}")
                                currentAd.invalidate()
                                currentAd.visibility = View.VISIBLE
                            })
                        } else {
                            ViewUtil.removeChildFromParent(currentAd)
                            holder.layoutMain.addView(currentAd)
                            Clog.e("LAZYLOAD", "Banner Added to the parent view")
                            currentAd.post({
                                Clog.e("WIDTH", "${currentAd.width}")
                                Clog.e("HEIGHT", "${currentAd.height}")
                                currentAd.invalidate()
                                currentAd.visibility = View.VISIBLE
                            })
                        }
                    }
                }
            }
        } else if (currentAd is VideoAd) {
            handleVideoAd(currentAd, holder.layoutMain)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val layoutMain = view.layoutMain
    }

    private fun handleVideoAd(videoAd: VideoAd, layoutVideo: LinearLayout) {
        // Load and display a Video
        // Video Ad elements
        val instreamVideoLayout =
            LayoutInflater.from(context).inflate(R.layout.fragment_preview_instream, null)
        val playButon = instreamVideoLayout.findViewById(R.id.play_button) as ImageButton
        playButon.visibility = View.VISIBLE
        val videoPlayer = instreamVideoLayout.findViewById(R.id.video_player) as VideoView
        val baseContainer =
            instreamVideoLayout.findViewById(R.id.instream_container_Layout) as RelativeLayout

        layoutVideo.removeAllViews()
        layoutVideo.addView(baseContainer)

        baseContainer.layoutParams.height = 1000
        baseContainer.layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT

        videoPlayer.setVideoURI(Uri.parse("https://acdn.adnxs.com/mobile/video_test/content/Scenario.mp4"))
        val controller = MediaController(context)
        videoPlayer.setMediaController(controller)
        playButon.setOnClickListener {
            if (videoAd.isReady) {
                videoAd.playAd(baseContainer)
            } else {
                videoPlayer.start()
            }
            playButon.visibility = View.INVISIBLE
        }

        // Set PlayBack Listener.
        videoAd.videoPlaybackListener = object : VideoAdPlaybackListener {

            override fun onAdPlaying(videoAd: VideoAd) {
                Clog.d("VideoAd", "onAdPlaying::")
            }

            override fun onQuartile(view: VideoAd, quartile: Quartile) {
                Clog.d("VideoAd", "onQuartile::$quartile")
            }

            override fun onAdCompleted(
                view: VideoAd,
                playbackState: VideoAdPlaybackListener.PlaybackCompletionState
            ) {
                if (playbackState == VideoAdPlaybackListener.PlaybackCompletionState.COMPLETED) {
                    Clog.d("VideoAd", "adCompleted::playbackState")
                } else if (playbackState == VideoAdPlaybackListener.PlaybackCompletionState.SKIPPED) {
                    Clog.d("VideoAd", "adSkipped::")
                }
                videoPlayer.start()
            }

            override fun onAdMuted(view: VideoAd, isMute: Boolean) {
                Clog.d("VideoAd", "isAudioMute::$isMute")
            }

            override fun onAdClicked(adView: VideoAd) {
                Clog.d("VideoAd", "onAdClicked")
            }

            override fun onAdClicked(videoAd: VideoAd, clickUrl: String) {
                Clog.d("VideoAd", "onAdClicked::clickUrl$clickUrl")
            }
        }
    }

    private fun handleNativeResponse(response: NativeAdResponse, layoutNative: LinearLayout) {
        val builder = NativeAdBuilder(context)
        if (response.icon != null)
            builder.setIconView(response.icon)
        if (response.image != null)
            builder.setImageView(response.image)
        builder.setTitle(response.title)
        builder.setDescription(response.description)
        builder.setCallToAction(response.callToAction)
        builder.setSponsoredBy(response.sponsoredBy)

        if (response.adStarRating != null) {
            builder.setAdStartValue(response.adStarRating.value.toString() + "/" + response.adStarRating.scale.toString())
        }

        // register all the views
        if (builder.container != null && builder.container!!.parent != null)
            (builder.container!!.parent as ViewGroup).removeAllViews()

        val nativeContainer = builder.container
        Handler().post {
            layoutNative.removeAllViews()
            layoutNative.addView(nativeContainer)
        }
    }

    internal inner class NativeAdBuilder @SuppressLint("NewApi")
    constructor(context: Context) {
        var container: RelativeLayout? = null
        var iconAndTitle: LinearLayout
        var customViewLayout: LinearLayout
        var imageView: ImageView
        var iconView: ImageView
        var title: TextView
        var description: TextView
        var callToAction: TextView
        var adStarRating: TextView
        var socialContext: TextView
        var sponsoredBy: TextView
        var customView =
            null // Any Mediated network requiring to render there own view for impression tracking would go in here.
        var views: LinkedList<View>? = null

        val allViews: LinkedList<View>
            get() {
                if (views == null) {
                    views = LinkedList()
                    views!!.add(imageView)
                    views!!.add(iconView)
                    views!!.add(title)
                    views!!.add(description)
                    views!!.add(callToAction)
                    views!!.add(adStarRating)
                    views!!.add(socialContext)
                    views!!.add(sponsoredBy)
                }
                return views as LinkedList<View>
            }


        init {
            container = RelativeLayout(context)

            iconAndTitle = LinearLayout(context)
            iconAndTitle.id = View.generateViewId()
            iconAndTitle.orientation = LinearLayout.HORIZONTAL
            iconView = ImageView(context)
            iconAndTitle.addView(iconView)
            title = TextView(context)
            iconAndTitle.addView(title)
            container!!.addView(iconAndTitle)


            imageView = ImageView(context)
            imageView.id = View.generateViewId()
            val imageView_params = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            imageView_params.addRule(RelativeLayout.BELOW, iconAndTitle.id)


            description = TextView(context)
            description.id = View.generateViewId()
            val description_params = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            description_params.addRule(RelativeLayout.BELOW, imageView.id)


            callToAction = TextView(context)
            callToAction.id = View.generateViewId()
            val callToAction_params = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            callToAction_params.addRule(RelativeLayout.BELOW, description.id)


            adStarRating = TextView(context)
            adStarRating.id = View.generateViewId()
            val adStarRating_params = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            adStarRating_params.addRule(RelativeLayout.BELOW, callToAction.id)

            socialContext = TextView(context)
            socialContext.id = View.generateViewId()
            val socialContext_params = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            socialContext_params.addRule(RelativeLayout.BELOW, adStarRating.id)


            sponsoredBy = TextView(context)
            sponsoredBy.id = View.generateViewId()
            val sponsoredBy_params = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            sponsoredBy_params.addRule(RelativeLayout.BELOW, socialContext.id)


            customViewLayout = LinearLayout(context)
            customViewLayout.id = View.generateViewId()
            customViewLayout.orientation = LinearLayout.HORIZONTAL
            val customViewLayout_params = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            customViewLayout_params.addRule(RelativeLayout.BELOW, sponsoredBy.id)


            container!!.addView(description, description_params)
            container!!.addView(imageView, imageView_params)
            container!!.addView(callToAction, callToAction_params)
            container!!.addView(adStarRating, adStarRating_params)
            container!!.addView(socialContext, socialContext_params)
            container!!.addView(sponsoredBy, sponsoredBy_params)
            container!!.addView(customViewLayout, customViewLayout_params)
        }

        fun setImageView(bitmap: Bitmap) {
            imageView.setImageBitmap(bitmap)
        }

        fun setIconView(bitmap: Bitmap) {
            iconView.setImageBitmap(bitmap)
        }


        fun setCustomView(customView: View) {
            this.customViewLayout.addView(customView)
        }

        fun setTitle(title: String) {
            this.title.text = title
        }

        fun setDescription(description: String) {
            this.description.text = description
        }

        fun setCallToAction(callToAction: String) {
            this.callToAction.text = callToAction
        }

        fun setSocialContext(socialContext: String) {
            this.socialContext.text = socialContext
        }

        fun setSponsoredBy(sponsoredBy: String) {
            this.sponsoredBy.text = sponsoredBy
        }

        fun setAdStartValue(value: String) {
            this.adStarRating.text = value
        }
    }

}