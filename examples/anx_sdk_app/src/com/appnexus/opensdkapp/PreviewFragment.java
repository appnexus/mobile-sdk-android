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

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
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
    private FrameLayout adFrame;
    PullToRefreshScrollView pullToRefreshView;

    private static final int DEF_COLOR = Color.BLACK;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View out = inflater.inflate(R.layout.fragment_preview, null);

        // locate members and set listeners
        adFrame = (FrameLayout) out.findViewById(R.id.adframe);

        bav = (BannerAdView) out.findViewById(R.id.banner);
        bav.setAdListener(adListener);

        bannerText = (TextView) out.findViewById(R.id.bannertext);

        iav = new InterstitialAdView(getActivity());
        iav.setAdListener(adListener);

        pullToRefreshView = (PullToRefreshScrollView) out.findViewById(R.id.pull_to_refresh);
        pullToRefreshView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                loadNewAd();
            }
        });

        pullToRefreshView.setPullToRefreshOverScrollEnabled(true);
        // try to load an ad on start
        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                loadNewAd();
            }
        }.sendEmptyMessage(0);

        return out;
    }

    public void loadNewAd() {
        Log.d(Constants.BASE_LOG_TAG, "Loading new ad");
        //resetBanner();

        Context context = getActivity();
        if (context == null) {
            Clog.e(Constants.PREFS_TAG, "activity context null. don't load ad");
            return;
        }

        SettingsWrapper settingsWrapper = SettingsWrapper.getSettingsWrapperFromPrefs(context);
        Clog.d(Constants.BASE_LOG_TAG, settingsWrapper.toString());

        if (settingsWrapper.isAdTypeBanner()) {
            // Load and display a banner
            bav.setAutoRefreshInterval(settingsWrapper.getRefreshPeriod());
            bav.setAdSize(settingsWrapper.getWidth(),settingsWrapper.getHeight());


            bav.setShouldServePSAs(settingsWrapper.isAllowPsas());
            bav.setOpensNativeBrowser(!settingsWrapper.isBrowserInApp());
            bav.setPlacementID(settingsWrapper.getPlacementId());
            bav.setGender(settingsWrapper.getGender());
            bav.setAge(settingsWrapper.getAge());
            bav.addCustomKeywords("pcode", settingsWrapper.getZip());

            if (!bav.loadAd()) {
                adListener.onAdRequestFailed(null);
            }
        } else {
            bav.setAutoRefreshInterval(0);
            bav.setVisibility(View.GONE);
            bannerText.setVisibility(TextView.VISIBLE);
            // Load and display an interstitial
            iav.setShouldServePSAs(settingsWrapper.isAllowPsas());
            iav.setOpensNativeBrowser(!settingsWrapper.isBrowserInApp());
            iav.setPlacementID(settingsWrapper.getPlacementId());
            iav.setGender(settingsWrapper.getGender());
            iav.setAge(settingsWrapper.getAge());
            iav.addCustomKeywords("pcode", settingsWrapper.getZip());

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
            if (!iav.loadAd()) {
                adListener.onAdRequestFailed(null);
            }
        }
    }

    private void resetBanner() {
        if (bav != null) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) bav.getLayoutParams();
            adFrame.removeView(bav);
            if(!bav.getExpandsToFitScreenWidth()){
                bav = new BannerAdView(getActivity());
                bav.setAdListener(adListener);
                bav.setLayoutParams(lp);
            }else{
                bav = new BannerAdView(getActivity());
                bav.setExpandsToFitScreenWidth(true);
                bav.setAdListener(adListener);
                bav.setLayoutParams(lp);
            }
            adFrame.addView(bav, 0);
        }
    }

    final private AdListener adListener = new AdListener() {
        @Override
        public void onAdRequestFailed(AdView adView) {
            pullToRefreshView.onRefreshComplete();
            toast("Ad request failed");
        }

        @Override
        public void onAdLoaded(AdView adView) {
            if (adView == bav) {
                View v = getView();
                if (v == null) return;
                FrameLayout adframe = (FrameLayout) v.findViewById(
                        R.id.adframe);
                ScrollView.LayoutParams lp = new ScrollView.LayoutParams(
                        adframe.getLayoutParams());
                lp.height = ScrollView.LayoutParams.WRAP_CONTENT;
                if(!bav.getExpandsToFitScreenWidth()){
                    adframe.setLayoutParams(lp);
                }

                DisplayMetrics m = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(m);
                float d = m.density;

                if(!bav.getExpandsToFitScreenWidth()){
                    FrameLayout.LayoutParams bannerlp = new FrameLayout.LayoutParams(bav.getLayoutParams());
                    bannerlp.gravity = Gravity.CENTER_HORIZONTAL;
                    if (bannerlp.width != -1) bannerlp.width = (int) (bav.getAdWidth() * d + 0.5f);
                    if (bannerlp.height != -1) bannerlp.height = (int) (bav.getAdHeight() * d + 0.5f);
                }

                bannerText.setVisibility(TextView.INVISIBLE);
            } else if (adView == iav) {
                if (iav.isReady()) {
                    toast("Interstitial ad ready, calling show()");
                    iav.show();
                } else {
                    toast("Interstitial ad not ready");
                }
            }

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
            if (getActivity() != null)
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            Clog.d(Constants.BASE_LOG_TAG, message);
        }
    };
}
