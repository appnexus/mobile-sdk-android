package com.appnexus.opensdk.mopubmediation;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.appnexus.opensdk.BannerAdView;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubView;

public class DemoActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

//        MoPubView banner = new MoPubView(this);
//        banner.setBackgroundColor(android.R.color.holo_blue_light);
//        banner.setMinimumHeight(50);
//        banner.setMinimumWidth(320);
//        banner.setAdUnitId("7d34c9e3fc42461590b8adebe4293fe0");
//
////        BannerAdView banner = new BannerAdView(this);
////        banner.setAdHeight(50);
////        banner.setAdWidth(320);
////        banner.setPlacementID("1281482");
//
//        ((ViewGroup) findViewById(R.id.main)).addView(banner, new FrameLayout.LayoutParams(1000, 500));
//
//        banner.loadAd();
        MoPubInterstitial interstitial = new MoPubInterstitial(this, "0faa9157ff764cb1b3676f8f11964970");
        interstitial.setInterstitialAdListener(new MoPubInterstitial.InterstitialAdListener() {
            @Override
            public void onInterstitialLoaded(MoPubInterstitial interstitial) {
                if (interstitial.isReady())
                    interstitial.show();
            }

            @Override
            public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
            }

            @Override
            public void onInterstitialShown(MoPubInterstitial interstitial) {
            }

            @Override
            public void onInterstitialClicked(MoPubInterstitial interstitial) {
            }

            @Override
            public void onInterstitialDismissed(MoPubInterstitial interstitial) {
            }
        });
        interstitial.load();
    }
}
