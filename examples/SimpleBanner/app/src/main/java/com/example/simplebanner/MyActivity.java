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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.RelativeLayout;

import com.appnexus.opensdk.AdListener;
import com.appnexus.opensdk.AdView;
import com.appnexus.opensdk.BannerAdView;
import com.appnexus.opensdk.InAppBrowserType;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.utils.Clog;

public class MyActivity extends Activity {

    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        context = this;

        final BannerAdView bav = new BannerAdView(this);

        // This is your AppNexus placement ID.
        bav.setPlacementID("1326299");

        // Turning this on so we always get an ad during testing.
        bav.setShouldServePSAs(true);

        // Set opens native Browser to false. which is also the SDK default value.
        // This will open In-Appbrowser for click
        bav.setOpensNativeBrowser(false);

        // Set the InAppBrowserType to custom if you want to show custom WebView/Landing pages for click through.
        // setting this will call onHandleClick with the click url.
        bav.setInAppBrowserType(InAppBrowserType.CUSTOM);

        // Get a 300x50 ad.
        bav.setAdSize(300, 250);

        // Resizes the container size to fit the banner ad
        bav.setResizeAdToFitContainer(true);

        bav.setExternalUID("YOUR_EXTERNAL_UID");

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
            public void onHandleClick(AdView adView, String clickURL) {
                Clog.v("SIMPLEBANNER", "onHandleClick; opening Custom Browser");
                Intent intent = new Intent(adView.getContext(), CustomLandingPage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("URL", clickURL);
                context.startActivity(intent);
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
}
