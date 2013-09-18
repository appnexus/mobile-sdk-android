/*
 *    Copyright 2013 APPNEXUS INC
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

package com.appnexus.opensdkapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.appnexus.opensdk.AdListener;
import com.appnexus.opensdk.AdView;
import com.appnexus.opensdk.BannerAdView;
import com.appnexus.opensdk.InterstitialAdView;
import com.appnexus.opensdk.utils.Clog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

public class PreviewFragment extends Fragment {
    private BannerAdView bav;
    private InterstitialAdView iav;
    private TextView bannerText;
    PullToRefreshScrollView pullToRefreshView;

    private static final int DEF_COLOR = Color.BLACK;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View out = inflater.inflate(R.layout.fragment_preview2, null);

        // locate members and set listeners
        bav = (BannerAdView) out.findViewById(R.id.banner);
        bav.setAdListener(bannerAdListener);

        bannerText = (TextView) out.findViewById(R.id.bannertext);

        iav = new InterstitialAdView(getActivity());
        iav.setAdListener(interstitialAdListener);

        loadNewAd();

        pullToRefreshView = (PullToRefreshScrollView) out.findViewById(R.id.pull_to_refresh);
        pullToRefreshView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                loadNewAd();
            }
        });

        return out;
    }

    public void loadNewAd() {
        Log.d(Constants.BASE_LOG_TAG, "Loading new ad");

        SettingsWrapper settingsWrapper = SettingsWrapper.getSettingsWrapperFromPrefs(getActivity());
        Clog.d(Constants.BASE_LOG_TAG, settingsWrapper.toString());

        if (settingsWrapper.isAdTypeBanner()) {
            // Load and display a banner
            bav.setAutoRefreshInterval(settingsWrapper.getRefreshPeriod());
            bav.setAdWidth(settingsWrapper.getWidth());
            bav.setAdHeight(settingsWrapper.getHeight());

            DisplayMetrics m = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(m);
            float d = m.density;

            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(bav.getLayoutParams());
            if (lp.width != -1) lp.width = (int) (bav.getAdWidth() * d + 0.5f);
            if (lp.height != -1) lp.height = (int) (bav.getAdHeight() * d + 0.5f);
            bav.setLayoutParams(lp);

            bav.setShouldServePSAs(settingsWrapper.isAllowPsas());
            bav.setOpensNativeBrowser(!settingsWrapper.isBrowserInApp());
            bav.setPlacementID(settingsWrapper.getPlacementId());
            bav.loadAd();
        }
        else {
            // Load and display an interstitial
            iav.setShouldServePSAs(settingsWrapper.isAllowPsas());
            iav.setOpensNativeBrowser(!settingsWrapper.isBrowserInApp());
            iav.setPlacementID(settingsWrapper.getPlacementId());
            int color = DEF_COLOR;

            // try to retrieve background color. default if not
            String backgroundHex = settingsWrapper.getBackgroundColor();
            if (backgroundHex.length() == 8) {
                try {
                    color = Color.parseColor("#" + backgroundHex);
                } catch (IllegalArgumentException e) {
                    Clog.d(Constants.BASE_LOG_TAG, "Invalid hex color");
                }
            }
            iav.setBackgroundColor(color);
            iav.setCloseButtonDelay(settingsWrapper.getCloseDelayPeriod());
            iav.loadAd();
        }
    }

    final private AdListener bannerAdListener = new AdListener() {
        @Override
        public void onAdRequestFailed(AdView adView) {
            toast("Ad request failed");
            pullToRefreshView.onRefreshComplete();
        }

        @Override
        public void onAdLoaded(AdView adView) {
                View v = getView();
                if (v == null) return;
                FrameLayout adframe = (FrameLayout) v.findViewById(
                        R.id.adframe);
                ScrollView.LayoutParams lp = new ScrollView.LayoutParams(
                        adframe.getLayoutParams());
                if (lp != null && adframe != null) {
                    lp.height = ScrollView.LayoutParams.WRAP_CONTENT;
                    adframe.setLayoutParams(lp);
                }
            bannerText.setVisibility(TextView.INVISIBLE);
            pullToRefreshView.onRefreshComplete();
            toast("Ad loaded");
        }

        @Override
        public void onAdExpanded(AdView adView) {
            toast("Ad expanded");
        }

        @Override
        public void onAdCollapsed(AdView adView) {
            toast("Ad collapsed");
        }

        @Override
        public void onAdClicked(AdView adView) {
            toast("Ad clicked; opening browser");
        }

        private void toast(String message) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            Clog.d(Constants.BASE_LOG_TAG, message);
        }
    };

    final private AdListener interstitialAdListener = new AdListener() {
        @Override
        public void onAdLoaded(AdView adView) {
            toast("Ad loaded");
            iav.show();
        }

        @Override
        public void onAdRequestFailed(AdView adView) {
            toast("Ad request failed");
        }

        @Override
        public void onAdExpanded(AdView adView) {
            toast("Ad expanded");
        }

        @Override
        public void onAdCollapsed(AdView adView) {
            toast("Ad collapsed");
        }

        @Override
        public void onAdClicked(AdView adView) {
            toast("Ad clicked; opening browser");
        }

        private void toast(String message) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            Clog.d(Constants.BASE_LOG_TAG, message);
        }
    };
}
