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

import com.appnexus.opensdk.MediatedInterstitialAdView;
import com.appnexus.opensdk.MediatedInterstitialAdViewController;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.utils.Clog;
import com.smartadserver.android.library.exception.SASAdTimeoutException;
import com.smartadserver.android.library.exception.SASNoAdToDeliverException;
import com.smartadserver.android.library.model.SASAdElement;
import com.smartadserver.android.library.model.SASAdPlacement;
import com.smartadserver.android.library.ui.SASInterstitialManager;

/**
 * This class is the SmartAdServer interstitial adapter. It provides the functionality needed to allow
 * an application using the App Nexus SDK to load an interstitial ad through the SmartAdServer SDK. The
 * instantiation of this class is done in response from the AppNexus server for an interstitial
 * placement that is configured to use SmartAdServer to serve it. This class is never directly instantiated
 * by the developer.
 */

public class SmartAdServerInterstitialAdView extends SmartAdServerBaseAdapter implements MediatedInterstitialAdView {

    // Tag for logging purposes
    private static final String TAG = SmartAdServerInterstitialAdView.class.getSimpleName();

    // the Smart interstitial manager
    private SASInterstitialManager interstitialManager;

    @Override
    public void requestAd(final MediatedInterstitialAdViewController mIC, Activity activity, String parameter, String uid, TargetingParameters tp) {

        // Configure (if needed) the SDK and retrieve the Ad placement
        SASAdPlacement adPlacement = configureSDKAndGetAdPlacement(activity, uid, tp);

        if (adPlacement == null) {
            mIC.onAdFailed(ResultCode.getNewInstance(ResultCode.INTERNAL_ERROR));
            return;
        }

        // Instantiate SASInterstitialManager instance
        interstitialManager = new SASInterstitialManager(activity, adPlacement);

        // Set the Interstitial Listener
        interstitialManager.setInterstitialListener(new SASInterstitialManager.InterstitialListener() {
            @Override
            public void onInterstitialAdLoaded(SASInterstitialManager sasInterstitialManager, SASAdElement sasAdElement) {
                Clog.i(TAG, "SmartAdServer: Interstitial loading completed");
                if (mIC != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mIC.onAdLoaded();
                        }
                    });

                }
            }

            @Override
            public void onInterstitialAdFailedToLoad(SASInterstitialManager sasInterstitialManager, Exception e) {
                Clog.e(TAG, "SmartAdServer: Interstitial Loading failed: " + e.getMessage());
                final ResultCode code;
                if (e instanceof SASNoAdToDeliverException) {
                    // no ad to deliver
                    code = ResultCode.getNewInstance(ResultCode.UNABLE_TO_FILL);
                } else if (e instanceof SASAdTimeoutException) {
                    // ad request timeout translates to network error
                    code = ResultCode.getNewInstance(ResultCode.NETWORK_ERROR);
                } else {
                    code = ResultCode.getNewInstance(ResultCode.INTERNAL_ERROR);
                }

                if (mIC != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mIC.onAdFailed(code);
                        }
                    });
                }
            }

            @Override
            public void onInterstitialAdShown(SASInterstitialManager sasInterstitialManager) {
                Clog.i(TAG, "SmartAdServer: Interstitial shown");
                if (mIC != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mIC.onAdExpanded();
                        }
                    });
                }
            }

            @Override
            public void onInterstitialAdFailedToShow(SASInterstitialManager sasInterstitialManager, Exception e) {
                Clog.i(TAG, "SmartAdServer: Interstitial failed to show: " + e.getMessage());
                // No equivalent of Failed to show for AppNexus.
            }

            @Override
            public void onInterstitialAdClicked(SASInterstitialManager sasInterstitialManager) {
                Clog.i(TAG, "SmartAdServer: Interstitial clicked");
                if (mIC != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mIC.onAdClicked();
                        }
                    });
                }
            }

            @Override
            public void onInterstitialAdDismissed(SASInterstitialManager sasInterstitialManager) {
                Clog.i(TAG, "SmartAdServer: Interstitial dismissed");
                if (mIC != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mIC.onAdCollapsed();
                        }
                    });
                }
            }

            @Override
            public void onInterstitialAdVideoEvent(SASInterstitialManager sasInterstitialManager, int i) {
                Clog.i(TAG, "SmartAdServer: Interstitial AdVideoEvent: " + i);
                // No equivalent
            }
        });

        // Load interstitial
        interstitialManager.loadAd();

    }

    @Override
    public void show() {
        if (isReady()) {
            // Show only if ready
            interstitialManager.show();
        }
    }

    @Override
    public boolean isReady() {
        return interstitialManager.isShowable();
    }

    @Override
    public void destroy() {
        if (interstitialManager != null) {
            interstitialManager.onDestroy();
            interstitialManager = null;
        }
    }


    @Override
    public void onPause() {
        //not supported by the SASInterstitialManager class
    }

    @Override
    public void onResume() {
        //not supported by the SASInterstitialManager class
    }

    @Override
    public void onDestroy() {
        destroy();
    }
}