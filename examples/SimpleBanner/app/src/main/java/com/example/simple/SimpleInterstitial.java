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

package com.example.simple;

import android.app.Activity;
import android.os.Bundle;

import com.appnexus.opensdk.AdListener;
import com.appnexus.opensdk.AdView;
import com.appnexus.opensdk.InterstitialAdView;
import com.appnexus.opensdk.ResultCode;

public class SimpleInterstitial extends Activity {
    InterstitialAdView iav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad);

        showInterstitialAd();
    }

    private void showInterstitialAd() {

        iav = new InterstitialAdView(this);

        iav.setPlacementID("1281482"); // Use this id to get Rich Media Interstitial ads
//        iav.setPlacementID("5706860"); // Use this id to get VAST Video Interstitial ads
        iav.setPlacementID("6061614");
        iav.setShouldServePSAs(false);
        iav.setOpensNativeBrowser(false);
        iav.setCloseButtonDelay(1000);

        iav.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded(AdView av) {
                InterstitialAdView iav = (InterstitialAdView) av;
                iav.show();
            }

            @Override
            public void onAdRequestFailed(AdView adView, ResultCode errorCode) {

            }

            @Override
            public void onAdExpanded(AdView adView) {

            }

            @Override
            public void onAdCollapsed(AdView adView) {

            }

            @Override
            public void onAdClicked(AdView adView) {

            }
        });

        iav.loadAd();
    }
}
