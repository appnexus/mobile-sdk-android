/*
 *    Copyright 2017 APPNEXUS INC
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
package com.appnexus.opensdk.mediatedviews;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.appnexus.opensdk.MediatedBannerAdView;
import com.appnexus.opensdk.MediatedBannerAdViewController;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.ViewUtil;
import com.smartadserver.android.library.exception.SASAdTimeoutException;
import com.smartadserver.android.library.exception.SASNoAdToDeliverException;
import com.smartadserver.android.library.model.SASAdElement;
import com.smartadserver.android.library.model.SASAdPlacement;
import com.smartadserver.android.library.ui.SASBannerView;

/**
 * This class is the SmartAdServer Banner adapter. It provides the functionality needed to allow
 * an application using the App Nexus SDK to load a Banner ad through the SmartAdServer SDK. The
 * instantiation of this class is done in response from the AppNexus server for an banner
 * placement that is configured to use SmartAdServer to serve it. This class is never directly instantiated
 * by the developer.
 */

public class SmartAdServerBannerAdView extends SmartAdServerBaseAdapter implements MediatedBannerAdView {

    // Tag for logging purposes
    private static final String TAG = SmartAdServerBannerAdView.class.getSimpleName();

    // the Smart banner view
    private SASBannerView bannerView;

    @Override
    public View requestAd(final MediatedBannerAdViewController mBC, final Activity activity, String parameter, String uid, final int width, final int height, TargetingParameters tp) {

        // Configure (if needed) the SDK and retrieve the Ad placement
        SASAdPlacement adPlacement = configureSDKAndGetAdPlacement(activity, uid, tp);

        if (adPlacement == null) {
            mBC.onAdFailed(ResultCode.getNewInstance(ResultCode.INTERNAL_ERROR));
            return null;
        }

        // Instantiate the Smart Banner view
        bannerView = new SASBannerView(activity){
            /**
             * Overriden to force banner size to received admob size if not expanded
             * @param params
             */
            @Override
            public void setLayoutParams(ViewGroup.LayoutParams params) {
                if (!bannerView.isExpanded()) {
                    params.width = ViewUtil.getValueInPixel(activity.getApplicationContext(),width);
                    params.height = ViewUtil.getValueInPixel(activity.getApplicationContext(),height);
                }
                super.setLayoutParams(params);
            }
        };

        // Set the banner listener
        bannerView.setBannerListener(new SASBannerView.BannerListener() {
            @Override
            public void onBannerAdLoaded(SASBannerView sasBannerView, SASAdElement sasAdElement) {
                Clog.i(TAG, "SmartAdServer: Banner loaded");
                if (mBC != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mBC.onAdLoaded();
                        }
                    });
                }
            }

            @Override
            public void onBannerAdFailedToLoad(SASBannerView sasBannerView, Exception e) {
                Clog.e(TAG, "SmartAdServer: Banner failed to load: " + e.getMessage());
                final ResultCode code;
                if (e instanceof SASNoAdToDeliverException) {
                    // no ad to deliver
                    code = ResultCode.getNewInstance(ResultCode.UNABLE_TO_FILL);
                } else if (e instanceof SASAdTimeoutException) {
                    // ad request timeout translates to  network error
                    code = ResultCode.getNewInstance(ResultCode.NETWORK_ERROR);
                } else {
                    code = ResultCode.getNewInstance(ResultCode.INTERNAL_ERROR);
                }
                if (mBC != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mBC.onAdFailed(code);
                        }
                    });
                }
            }

            @Override
            public void onBannerAdClicked(SASBannerView sasBannerView) {
                Clog.i(TAG, "SmartAdServer: Banner clicked");
                if (mBC != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mBC.onAdClicked();
                        }
                    });
                }
            }

            @Override
            public void onBannerAdExpanded(SASBannerView sasBannerView) {
                Clog.i(TAG, "SmartAdServer: Banner expanded");
                if (mBC != null) {
                    mBC.onAdExpanded();
                }
            }

            @Override
            public void onBannerAdCollapsed(SASBannerView sasBannerView) {
                Clog.i(TAG, "SmartAdServer: Banner collapsed");
                if (mBC != null) {
                    mBC.onAdCollapsed();
                }
            }

            @Override
            public void onBannerAdResized(SASBannerView sasBannerView) {
                Clog.i(TAG, "SmartAdServer: Banner resized");
                // No equivalent
            }

            @Override
            public void onBannerAdClosed(SASBannerView sasBannerView) {
                Clog.i(TAG, "SmartAdServer: Banner closed");
                if (mBC != null) {
                    mBC.onAdCollapsed();
                }
            }

            @Override
            public void onBannerAdVideoEvent(SASBannerView sasBannerView, int i) {
                Clog.i(TAG, "SmartAdServer: Banner video event: " + i);
                // No equivalent
            }
        });

        Clog.logTime(getClass().getSimpleName() + " - requestAd");
        // Load the banner
        bannerView.loadAd(adPlacement);

        return bannerView;
    }

    @Override
    public void destroy() {
        if (bannerView != null) {
            bannerView.onDestroy();
            bannerView = null;
        }
    }

    @Override
    public void onPause() {
        //not supported by the SASBannerView class
    }

    @Override
    public void onResume() {
        //not supported by the SASBannerView class
    }

    @Override
    public void onDestroy() {
        destroy();
    }
}