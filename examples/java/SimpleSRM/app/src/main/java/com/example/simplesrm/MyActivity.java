/*
 *    Copyright 2014 APPNEXUS INC
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

package com.example.simplesrm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.test.espresso.idling.CountingIdlingResource;

import com.appnexus.opensdk.ANAdResponseInfo;
import com.appnexus.opensdk.ANClickThroughAction;
import com.appnexus.opensdk.ANMultiAdRequest;
import com.appnexus.opensdk.Ad;
import com.appnexus.opensdk.AdListener;
import com.appnexus.opensdk.AdSize;
import com.appnexus.opensdk.AdView;
import com.appnexus.opensdk.BannerAdView;
import com.appnexus.opensdk.InterstitialAdView;
import com.appnexus.opensdk.NativeAdRequest;
import com.appnexus.opensdk.NativeAdRequestListener;
import com.appnexus.opensdk.NativeAdResponse;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.SDKSettings;
import com.appnexus.opensdk.instreamvideo.Quartile;
import com.appnexus.opensdk.instreamvideo.VideoAd;
import com.appnexus.opensdk.instreamvideo.VideoAdLoadListener;
import com.appnexus.opensdk.instreamvideo.VideoAdPlaybackListener;
import com.appnexus.opensdk.mar.MultiAdRequestListener;
import com.appnexus.opensdk.utils.Clog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;

public class MyActivity extends Activity {

    String msg = "";
    TextView tv;
    LinearLayout layout;
    LinearLayout layoutBanner, layoutVideo, layoutNative;
    String placementID = "17058950";
    String bannerPlacementId = "17982237";
    String interstitialPlacementId = "7067108";
    String nativePlacementId = "9505207";
    String videoPlacementId = nativePlacementId = bannerPlacementId = interstitialPlacementId = placementID;
    int count = 1;
    InterstitialAdView iav;
    BannerAdView bav;
    ANMultiAdRequest anMultiAdRequest = null;
    ANMultiAdRequest anMultiAdRequest2 = null;
    NativeAdRequest adRequest;

    CountingIdlingResource idlingResource = new CountingIdlingResource("MAR Load Count", true);
    boolean multiAdRequestCompleted = false;
    boolean multiAdRequestCompleted2 = false;
    private VideoAd videoAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        bannerPlacementId = interstitialPlacementId = nativePlacementId = videoPlacementId = placementID;

//        layout = findViewById(R.id.main_content);
        layout = findViewById(R.id.adContent);
        layoutBanner = findViewById(R.id.layoutBanner);
        layoutNative = findViewById(R.id.layoutNative);
        layoutVideo = findViewById(R.id.layoutVideo);
        tv = findViewById(R.id.tv);

        iav = setupInterstitialAd();

        videoAd = setupVideoAd();

        bav = setupBannerAd();

        adRequest = setupNativeAd();

        // Set up a listener on this ad view that logs events.
//        AdListener adListener =

//        layout.addView(bav);

        iav.setAdListener(new AdListener() {
            @Override
            public void onAdRequestFailed(AdView iav, ResultCode errorCode) {
                if (errorCode == null) {
                    msg += "Interstitial Ad Failed\n";
                } else {
                    msg += "Interstitial Ad Failed: " + errorCode + "\n";
                }
                toast();
            }

            @Override
            public void onAdLoaded(final AdView av) {
                if (av instanceof InterstitialAdView) {
                    msg += "Interstitial Ad Loaded\n";
                    iav.show();
                }
                toast();
            }

            @Override
            public void onAdLoaded(NativeAdResponse nativeAdResponse) {
                msg += "Banner-Native Ad Loaded\n";
                toast();
                handleNativeResponse(nativeAdResponse);
            }

            @Override
            public void onAdExpanded(AdView bav) {
                Clog.v("SimpleSRM", "Ad expanded");
            }

            @Override
            public void onAdCollapsed(AdView bav) {
                Clog.v("SimpleSRM", "Ad collapsed");
            }

            @Override
            public void onAdClicked(AdView bav) {
                Clog.v("SimpleSRM", "Ad clicked; opening browser");
            }

            @Override
            public void onAdClicked(AdView adView, String clickUrl) {
                Clog.v("SimpleSRM", "onAdClicked with click URL");
            }

            @Override
            public void onLazyAdLoaded(AdView adView) {

            }

            @Override
            public void onAdImpression(AdView adView) {
                Clog.v("SimpleSRM", "onAdImpression");
            }
        });

//        List<Ad> adUnitList = new ArrayList<>();
//        adUnitList.add(bav);
//        adUnitList.add(iav);
//        adUnitList.add(adRequest);
//        adUnitList.add(videoAd);

        anMultiAdRequest2 = new ANMultiAdRequest(this, 0, 0, new MultiAdRequestListener() {
            @Override
            public void onMultiAdRequestCompleted() {
                msg += "MAR 2 Load Completed";
                multiAdRequestCompleted2 = true;
                Clog.i("MAR TESTING", "IDLING RESOURCE IDLE: " + idlingResource.isIdleNow());
                if (!idlingResource.isIdleNow())
                    idlingResource.decrement();
                toast();
            }

            @Override
            public void onMultiAdRequestFailed(ResultCode code) {
                msg += code.getMessage();
                toast();
            }
        });
        anMultiAdRequest2.addAdUnit(setupVideoAd());
        anMultiAdRequest2.addAdUnit(setupNativeAd());
        anMultiAdRequest2.addAdUnit(setupBannerAd());
        anMultiAdRequest2.addAdUnit(setupInterstitialAd());
//        anMultiAdRequest2.load();


        anMultiAdRequest = new ANMultiAdRequest(this, 0, 0,
                new MultiAdRequestListener() {
                    @Override
                    public void onMultiAdRequestCompleted() {
                        msg += "MAR Load Completed";
                        multiAdRequestCompleted = true;
                        Clog.i("MAR TESTING", "IDLING RESOURCE IDLE: " + idlingResource.isIdleNow());
                        if (!idlingResource.isIdleNow())
                            idlingResource.decrement();
                        toast();
                    }

                    @Override
                    public void onMultiAdRequestFailed(ResultCode code) {
                        msg += code.getMessage();
                        toast();
                    }
                });
        anMultiAdRequest.addAdUnit(bav);
        anMultiAdRequest.addAdUnit(iav);
        anMultiAdRequest.addAdUnit(adRequest);
        anMultiAdRequest.addAdUnit(videoAd);
//        anMultiAdRequest.addAdUnit(setupVideoAd());
//        bav = null;
//        System.gc();
//        iav = null;
//        adRequest = null;
        load();
//        bav.loadAd();

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msg = "";
                toast();
//                switch (count) {
//                    case 1:
//                        bav.loadAd();
//                        break;
//                    case 2:
//                        iav.loadAd();
//                        break;
//                    case 3:
//                        adRequest.loadAd();
//                        break;
//                    case 4:
//                        videoAd.loadAd();
//                        break;
//                    default:
//                        anMultiAdRequest.load();
//                        break;
//                }
//                count++;
                load();
            }
        });

//        // If auto-refresh is enabled (the default), a call to
//        // `FrameLayout.addView()` followed directly by
//        // `BannerAdView.loadAd()` will succeed.  However, if
//        // auto-refresh is disabled, the call to
//        // `BannerAdView.loadAd()` needs to be wrapped in a `Handler`
//        // block to ensure that the banner ad view is in the view
//        // hierarchy *before* the call to `loadAd()`.  Otherwise the
//        // visibility check in `loadAd()` will fail, and no ad will be
//        // shown.
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                bav.loadAd();
//            }
//        }, 0);
    }

    void load() {
        multiAdRequestCompleted = false;
        Clog.e("MAR TESTING", "IDLING RESOURCE IDLE: " + idlingResource.isIdleNow());
        if (idlingResource.isIdleNow()) {
            idlingResource.increment();
        }
        anMultiAdRequest.load();
    }

    void load2() {
        multiAdRequestCompleted2 = false;
        Clog.e("MAR TESTING", "IDLING RESOURCE IDLE: " + idlingResource.isIdleNow());
        if (idlingResource.isIdleNow()) {
            idlingResource.increment();
        }
        anMultiAdRequest2.load();
    }

    private NativeAdRequest setupNativeAd() {
        NativeAdRequest adRequest = new NativeAdRequest(this, nativePlacementId);
        adRequest.shouldLoadImage(false);
        adRequest.shouldLoadIcon(false);
        adRequest.setListener(new NativeAdRequestListener() {
            @Override
            public void onAdLoaded(NativeAdResponse response) {
                msg += "Native Ad Loaded\n";
                toast();
                handleNativeResponse(response);
            }

            @Override
            public void onAdFailed(ResultCode errorcode, ANAdResponseInfo adResponseInfo) {
                msg += "Native Ad Failed:" + errorcode + "\n";
                toast();
            }
        });
        return adRequest;
    }

    private InterstitialAdView setupInterstitialAd() {
        final InterstitialAdView iav = new InterstitialAdView(this);

        iav.setPlacementID(interstitialPlacementId);
        return iav;
    }

    private BannerAdView setupBannerAd() {
        final BannerAdView bav = new BannerAdView(this);

        // This is your AppNexus placement ID.
        bav.setPlacementID(bannerPlacementId);

        bav.setAllowNativeDemand(false);
        bav.setAllowVideoDemand(false);
        bav.setAutoRefreshInterval(0);

        // Turning this on so we always get an ad during testing.
        bav.setShouldServePSAs(false);

        // By default ad clicks open in an in-app WebView.
        bav.setClickThroughAction(ANClickThroughAction.OPEN_SDK_BROWSER);

        // Get a 300x50 ad.
        ArrayList<AdSize> adSizes = new ArrayList<>();
        adSizes.add(new AdSize(320, 50));
        bav.setAdSizes(adSizes);
//        bav.setAdSize(320, 50);

        // Resizes the container size to fit the banner ad
        bav.setResizeAdToFitContainer(true);

//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.MATCH_PARENT);
//        bav.setLayoutParams(layoutParams);

        bav.setAdListener(new AdListener() {
            @Override
            public void onAdRequestFailed(AdView bav, ResultCode errorCode) {
                if (errorCode == null) {
                    Clog.v("SimpleSRM", "Call to loadAd failed");
                } else {
                    Clog.v("SimpleSRM", "Ad request failed: " + errorCode);
                    msg += "Banner Ad Failed: " + errorCode + "\n";
                    toast();
                }
            }

            @Override
            public void onAdLoaded(final AdView av) {
                Clog.v("SimpleSRM", "The Ad Loaded!");
//                if (av.getParent() != null && av.getParent() instanceof ViewGroup) {
//                    ((ViewGroup) av.getParent()).removeAllViews();
//                }
                msg += "Banner Ad Loaded\n";
                toast();
                layoutBanner.removeAllViews();
                layoutBanner.addView(av);
            }

            @Override
            public void onAdLoaded(NativeAdResponse nativeAdResponse) {
                Clog.v("SimpleSRM", "Ad onAdLoaded NativeAdResponse");
                msg += "Banner-Native Ad Loaded\n";
                toast();
                handleNativeResponse(nativeAdResponse);
            }

            @Override
            public void onAdExpanded(AdView bav) {
                Clog.v("SimpleSRM", "Ad expanded");
            }

            @Override
            public void onAdCollapsed(AdView bav) {
                Clog.v("SimpleSRM", "Ad collapsed");
            }

            @Override
            public void onAdClicked(AdView bav) {
                Clog.v("SimpleSRM", "Ad clicked; opening browser");
            }

            @Override
            public void onAdClicked(AdView adView, String clickUrl) {
                Clog.v("SimpleSRM", "onAdClicked with click URL");
            }

            @Override
            public void onLazyAdLoaded(AdView adView) {

            }

            @Override
            public void onAdImpression(AdView adView) {
                Clog.v("SimpleSRM", "onAdImpression");
            }
        });

//        layoutBanner.removeAllViews();
//        layoutBanner.addView(bav);
        return bav;

    }

    private VideoAd setupVideoAd() {
        final VideoAd videoAd;
        // Load and display a Video
        // Video Ad elements
        final View instreamVideoLayout = getLayoutInflater().inflate(R.layout.fragment_preview_instream, null);
        final ImageButton playButon = (ImageButton) instreamVideoLayout.findViewById(R.id.play_button);
        final VideoView videoPlayer = (VideoView) instreamVideoLayout.findViewById(R.id.video_player);
        final RelativeLayout baseContainer = (RelativeLayout) instreamVideoLayout.findViewById(R.id.instream_container_Layout);

        layoutVideo.removeAllViews();
        layoutVideo.addView(baseContainer);

        baseContainer.getLayoutParams().height = 1000;
        baseContainer.getLayoutParams().width = RelativeLayout.LayoutParams.MATCH_PARENT;

        videoPlayer.setVideoURI(Uri.parse("https://acdn.adnxs.com/mobile/video_test/content/Scenario.mp4"));
        MediaController controller = new MediaController(this);
        videoPlayer.setMediaController(controller);

        videoAd = new VideoAd(this, videoPlacementId);
        videoAd.clearCustomKeywords();

        videoAd.setAdLoadListener(new VideoAdLoadListener() {
            @Override
            public void onAdLoaded(VideoAd adView) {
                Clog.d("VideoAd", "AD READY");
                msg += "Video Ad Loaded\n";
                toast();
                Toast.makeText(MyActivity.this, "Ad is ready. Hit on Play button to start",
                        Toast.LENGTH_SHORT).show();
                playButon.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdRequestFailed(VideoAd adView, ResultCode errorCode) {
                Clog.d("VideoAd", "AD FAILED::");
                msg += "Video Ad Failed\n";
                toast();
                Toast.makeText(MyActivity.this, "AD FAILED::", Toast.LENGTH_SHORT).show();
                playButon.setVisibility(View.VISIBLE);
            }
        });

        playButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoAd.isReady()) {
                    videoAd.playAd(baseContainer);
                } else {
                    videoPlayer.start();
                }
                playButon.setVisibility(View.INVISIBLE);
            }
        });

//        videoAd.loadAd();
        // Set PlayBack Listener.
        videoAd.setVideoPlaybackListener(new VideoAdPlaybackListener() {

            @Override
            public void onAdPlaying(final VideoAd videoAd) {
                Clog.d("VideoAd", "onAdPlaying::");
            }

            @Override
            public void onQuartile(VideoAd view, Quartile quartile) {
                Clog.d("VideoAd", "onQuartile::" + quartile);
            }

            @Override
            public void onAdCompleted(VideoAd view, PlaybackCompletionState playbackState) {
                if (playbackState == PlaybackCompletionState.COMPLETED) {
                    Clog.d("VideoAd", "adCompleted::playbackState");
                } else if (playbackState == PlaybackCompletionState.SKIPPED) {
                    Clog.d("VideoAd", "adSkipped::");
                }
                videoPlayer.start();
            }

            @Override
            public void onAdMuted(VideoAd view, boolean isMute) {
                Clog.d("VideoAd", "isAudioMute::" + isMute);
            }

            @Override
            public void onAdClicked(VideoAd adView) {
                Clog.d("VideoAd", "onAdClicked");
            }

            @Override
            public void onAdClicked(VideoAd videoAd, String clickUrl) {
                Clog.d("VideoAd", "onAdClicked::clickUrl" + clickUrl);
                msg += "Video AdClicked::clickUrl - " + clickUrl;
            }
        });
        return videoAd;
    }

    private void toast() {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Clog.e("TOAST", msg);
        tv.setText(msg);
    }

    private void handleNativeResponse(NativeAdResponse response) {
        NativeAdBuilder builder = new NativeAdBuilder(this);
        if (response.getIcon() != null)
            builder.setIconView(response.getIcon());
        if (response.getImage() != null)
            builder.setImageView(response.getImage());
        builder.setTitle(response.getTitle());
        builder.setDescription(response.getDescription());
        builder.setCallToAction(response.getCallToAction());
        builder.setSponsoredBy(response.getSponsoredBy());

        if (response.getAdStarRating() != null) {
            builder.setAdStartValue((String.valueOf(response.getAdStarRating().getValue())) + "/" + String.valueOf(response.getAdStarRating().getScale()));
        }

        // register all the views
        if (builder.getContainer() != null && builder.getContainer().getParent() != null)
            ((ViewGroup) builder.getContainer().getParent()).removeAllViews();

        final RelativeLayout nativeContainer = builder.getContainer();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                layoutNative.removeAllViews();
                layoutNative.addView(nativeContainer);
            }
        });
    }

    class NativeAdBuilder {
        RelativeLayout nativeAdContainer;
        LinearLayout iconAndTitle;
        LinearLayout customViewLayout;
        ImageView imageView;
        ImageView iconView;
        TextView title;
        TextView description;
        TextView callToAction;
        TextView adStarRating;
        TextView socialContext;
        TextView sponsoredBy;
        View customView; // Any Mediated network requiring to render there own view for impression tracking would go in here.
        LinkedList<View> views;


        @SuppressLint("NewApi")
        NativeAdBuilder(Context context) {
            nativeAdContainer = new RelativeLayout(context);

            iconAndTitle = new LinearLayout(context);
            iconAndTitle.setId(View.generateViewId());
            iconAndTitle.setOrientation(LinearLayout.HORIZONTAL);
            iconView = new ImageView(context);
            iconAndTitle.addView(iconView);
            title = new TextView(context);
            iconAndTitle.addView(title);
            nativeAdContainer.addView(iconAndTitle);


            imageView = new ImageView(context);
            imageView.setId(View.generateViewId());
            RelativeLayout.LayoutParams imageView_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            imageView_params.addRule(RelativeLayout.BELOW, iconAndTitle.getId());


            description = new TextView(context);
            description.setId(View.generateViewId());
            RelativeLayout.LayoutParams description_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            description_params.addRule(RelativeLayout.BELOW, imageView.getId());


            callToAction = new TextView(context);
            callToAction.setId(View.generateViewId());
            RelativeLayout.LayoutParams callToAction_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            callToAction_params.addRule(RelativeLayout.BELOW, description.getId());


            adStarRating = new TextView(context);
            adStarRating.setId(View.generateViewId());
            RelativeLayout.LayoutParams adStarRating_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            adStarRating_params.addRule(RelativeLayout.BELOW, callToAction.getId());

            socialContext = new TextView(context);
            socialContext.setId(View.generateViewId());
            RelativeLayout.LayoutParams socialContext_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            socialContext_params.addRule(RelativeLayout.BELOW, adStarRating.getId());


            sponsoredBy = new TextView(context);
            sponsoredBy.setId(View.generateViewId());
            RelativeLayout.LayoutParams sponsoredBy_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            sponsoredBy_params.addRule(RelativeLayout.BELOW, socialContext.getId());


            customViewLayout = new LinearLayout(context);
            customViewLayout.setId(View.generateViewId());
            customViewLayout.setOrientation(LinearLayout.HORIZONTAL);
            RelativeLayout.LayoutParams customViewLayout_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            customViewLayout_params.addRule(RelativeLayout.BELOW, sponsoredBy.getId());


            nativeAdContainer.addView(description, description_params);
            nativeAdContainer.addView(imageView, imageView_params);
            nativeAdContainer.addView(callToAction, callToAction_params);
            nativeAdContainer.addView(adStarRating, adStarRating_params);
            nativeAdContainer.addView(socialContext, socialContext_params);
            nativeAdContainer.addView(sponsoredBy, sponsoredBy_params);
            nativeAdContainer.addView(customViewLayout, customViewLayout_params);
        }

        public void setImageView(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }

        public void setIconView(Bitmap bitmap) {
            iconView.setImageBitmap(bitmap);
        }


        public void setCustomView(View customView) {
            this.customViewLayout.addView(customView);
        }

        public void setTitle(String title) {
            this.title.setText(title);
        }

        public void setDescription(String description) {
            this.description.setText(description);
        }

        public void setCallToAction(String callToAction) {
            this.callToAction.setText(callToAction);
        }

        public void setSocialContext(String socialContext) {
            this.socialContext.setText(socialContext);
        }

        public void setSponsoredBy(String sponsoredBy) {
            this.sponsoredBy.setText(sponsoredBy);
        }

        public void setAdStartValue(String value) {
            this.adStarRating.setText(value);
        }

        public RelativeLayout getContainer() {
            return nativeAdContainer;
        }

        public LinkedList<View> getAllViews() {
            if (views == null) {
                views = new LinkedList<View>();
                views.add(imageView);
                views.add(iconView);
                views.add(title);
                views.add(description);
                views.add(callToAction);
                views.add(adStarRating);
                views.add(socialContext);
                views.add(sponsoredBy);
            }
            return views;
        }
    }

    @Override
    protected void onDestroy() {
        if (anMultiAdRequest != null) {
            for (WeakReference<Ad> adRef: anMultiAdRequest.getAdUnitList()) {
                Ad ad  = adRef.get();
                if (ad instanceof BannerAdView) {
                    ((BannerAdView) ad).activityOnDestroy();
                } else if (ad instanceof InterstitialAdView){
                    ((InterstitialAdView) ad).activityOnDestroy();
                } else if (ad instanceof VideoAd) {
                    ((VideoAd) ad).activityOnDestroy();
                }
            }
        }

        if (anMultiAdRequest2 != null) {
            for (WeakReference<Ad> adRef: anMultiAdRequest2.getAdUnitList()) {
                Ad ad  = adRef.get();
                if (ad instanceof BannerAdView) {
                    ((BannerAdView) ad).activityOnDestroy();
                } else if (ad instanceof InterstitialAdView){
                    ((InterstitialAdView) ad).activityOnDestroy();
                } else if (ad instanceof VideoAd) {
                    ((VideoAd) ad).activityOnDestroy();
                }
            }
        }
        super.onDestroy();
    }
}
