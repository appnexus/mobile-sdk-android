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

package com.example.simplebanner;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.appnexus.opensdk.ANClickThroughAction;
import com.appnexus.opensdk.AdListener;
import com.appnexus.opensdk.AdView;
import com.appnexus.opensdk.BannerAdView;
import com.appnexus.opensdk.InitListener;
import com.appnexus.opensdk.NativeAdResponse;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.SDKSettings;
import com.appnexus.opensdk.XandrAd;
import com.appnexus.opensdk.utils.Clog;

public class MyActivity extends Activity {

    BannerAdView bav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        XandrAd.init(10094, this, true, new InitListener() {
            @Override
            public void onInitFinished(boolean success) {
                Toast.makeText(MyActivity.this, "Init Completed with " + success, Toast.LENGTH_SHORT).show();
            }
        });

        bav = new BannerAdView(this);

        Log.d("sdkVersion", "sdkVersion: "+ SDKSettings.getSDKVersion());
        // This is your AppNexus placement ID.
        bav.setPlacementID("17058950");

        // Turning this on so we always get an ad during testing.
        bav.setShouldServePSAs(false);

        // By default ad clicks open in an in-app WebView.
        bav.setClickThroughAction(ANClickThroughAction.OPEN_SDK_BROWSER);

        // Get a 300x50 ad.
        bav.setAdSize(300, 250);

        // Resizes the container size to fit the banner ad
        bav.setResizeAdToFitContainer(true);

        // Set up a listener on this ad view that logs events.
        AdListener adListener = new AdListener() {
            @Override
            public void onAdRequestFailed(AdView bav, ResultCode errorCode) {
                if (errorCode == null) {
                    Clog.v("SIMPLEBANNER", "Call to loadAd failed");
                } else {
                    Clog.v("SIMPLEBANNER", "Ad request failed: " + errorCode);
                }
            }

            @Override
            public void onAdLoaded(AdView bav) {
                Clog.v("SIMPLEBANNER", "The Ad Loaded!");
            }

            @Override
            public void onAdLoaded(NativeAdResponse nativeAdResponse) {
                Clog.v("SIMPLEBANNER", "Ad onAdLoaded NativeAdResponse");
            }

            @Override
            public void onAdExpanded(AdView bav) {
                Clog.v("SIMPLEBANNER", "Ad expanded");
            }

            @Override
            public void onAdCollapsed(AdView bav) {
                Clog.v("SIMPLEBANNER", "Ad collapsed");
            }

            @Override
            public void onAdClicked(AdView bav) {
                Clog.v("SIMPLEBANNER", "Ad clicked; opening browser");
            }

            @Override
            public void onAdClicked(AdView adView, String clickUrl) {
                Clog.v("SIMPLEBANNER", "onAdClicked with click URL");
            }

            @Override
            public void onLazyAdLoaded(AdView adView) {

            }

            @Override
            public void onAdImpression(AdView adView) {
                Clog.v("SIMPLEBANNER", "onAdImpression");
            }
        };

        bav.setAdListener(adListener);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.main_content);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        bav.setLayoutParams(layoutParams);
        layout.addView(bav);

        // If auto-refresh is enabled (the default), a call to
        // `FrameLayout.addView()` followed directly by
        // `BannerAdView.loadAd()` will succeed.  However, if
        // auto-refresh is disabled, the call to
        // `BannerAdView.loadAd()` needs to be wrapped in a `Handler`
        // block to ensure that the banner ad view is in the view
        // hierarchy *before* the call to `loadAd()`.  Otherwise the
        // visibility check in `loadAd()` will fail, and no ad will be
        // shown.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bav.loadAd();
            }
        }, 0);
    }

    @Override
    protected void onDestroy() {
        if (bav != null) {
            bav.activityOnDestroy();
        }
        super.onDestroy();
    }
}
