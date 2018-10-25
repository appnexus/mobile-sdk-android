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
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.appnexus.opensdk.MediatedInterstitialAdView;
import com.appnexus.opensdk.MediatedInterstitialAdViewController;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.StringUtil;
import com.smartadserver.android.library.SASInterstitialView;
import com.smartadserver.android.library.model.SASAdElement;
import com.smartadserver.android.library.ui.SASAdView;
import com.smartadserver.android.library.util.SASUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class is the SmartAdServer interstitial adapter. It provides the functionality needed to allow
 * an application using the App Nexus SDK to load an interstitial ad through the SmartAdServer SDK. The
 * instantiation of this class is done in response from the AppNexus server for an interstitial
 * placement that is configured to use SmartAdServer to serve it. This class is never directly instantiated
 * by the developer.
 */

public class SmartAdServerInterstitialAdView implements MediatedInterstitialAdView {


    public static final String SITE_ID = "site_id";
    public static final String PAGE_ID = "page_id";
    public static final String FORMAT_ID = "format_id";


    SASInterstitialView sasInterstitialView;

    // Container view for offscreen interstitial loading (as SASInterstitialView is displayed immediately after successful loading)
    FrameLayout interstitialContainer;

    SmartAdServerListener smartAdListener = null;


    @Override
    public void requestAd(MediatedInterstitialAdViewController mIC, Activity activity, String parameter, String uid, TargetingParameters tp) {

        String site_id = "";
        String page_id = "";
        String format_id = "";
        try {
            if (!StringUtil.isEmpty(uid)) {
                JSONObject idObject = new JSONObject(uid);
                site_id = idObject.getString(SITE_ID);
                page_id = idObject.getString(PAGE_ID);
                format_id = idObject.getString(FORMAT_ID);
            } else {
                mIC.onAdFailed(ResultCode.INVALID_REQUEST);
                return;
            }
        } catch (JSONException e) {
            mIC.onAdFailed(ResultCode.INVALID_REQUEST);
            return;
        }

        smartAdListener = new SmartAdServerListener(mIC, this.getClass().getSimpleName());

        // instantiate SASInterstitialView that will perform the Smart ad call
        sasInterstitialView = new SASInterstitialView(activity) {
            /**
             * Overriden to notify the ad was clicked
             */
            @Override
            public void open(String url) {
                super.open(url);
                if (isAdWasOpened()) {
                    smartAdListener.onClicked();
                }
            }
        };

        // add state change listener to detect when ad is closed or loaded and expanded (=ready to be displayed)
        sasInterstitialView.addStateChangeListener(new SASAdView.OnStateChangeListener() {
            boolean wasOpened = false;
            public void onStateChanged(
                    SASAdView.StateChangeEvent stateChangeEvent) {
                switch (stateChangeEvent.getType()) {
                    case SASAdView.StateChangeEvent.VIEW_HIDDEN:
                        Clog.i(Clog.mediationLogTag, "SmartAdServer: MRAID state : HIDDEN");
                        // ad was closed
                        ViewParent parent = interstitialContainer.getParent();
                        if (parent instanceof ViewGroup) {
                            ((ViewGroup) parent).removeView(interstitialContainer);
                        }
                        if (wasOpened) {
                            smartAdListener.onCollapsed();
                            wasOpened = false;
                        }
                        break;
                    case SASAdView.StateChangeEvent.VIEW_EXPANDED:
                        Clog.i(Clog.mediationLogTag, "SmartAdServer: MRAID state : EXPANDED");
                        // ad was expanded
                        smartAdListener.onExpanded();
                        wasOpened = true;
                        break;
                    case SASAdView.StateChangeEvent.VIEW_DEFAULT:
                        // ad was collapsed
                        Clog.i(Clog.mediationLogTag, "SmartAdServer: MRAID state : DEFAULT");
                        if (wasOpened) {
                            smartAdListener.onCollapsed();
                            wasOpened = false;
                        }
                        break;
                }
            }
        });


        // create the (offscreen) FrameLayout that the SASInterstitialView will expand into
        if (interstitialContainer == null) {
            interstitialContainer = new FrameLayout(activity);
            interstitialContainer.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

        sasInterstitialView.setExpandParentContainer(interstitialContainer);

        // detect layout changes to update padding
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            sasInterstitialView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    // on layout change, add a globalLayoutListener to apply padding once layout is done (and not to early)
                    sasInterstitialView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            sasInterstitialView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            Rect r = new Rect();
                            ViewParent parentView = interstitialContainer.getParent();
                            if (parentView instanceof View) {
                                ((View) parentView).getWindowVisibleDisplayFrame(r);
                                int topPadding = r.top;
                                // handle navigation bar overlay by adding padding
                                int leftPadding = r.left;
                                int bottomPadding = Math.max(0, ((View) parentView).getHeight() - r.bottom);
                                int rightPadding = Math.max(0, ((View) parentView).getWidth() - r.right);
                                interstitialContainer.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);
                            }
                        }
                    });
                }
            });
        }

        if (tp != null) {
            if (tp.getLocation() != null) {
                sasInterstitialView.setLocation(tp.getLocation());
            }
        }
        // Now request ad for this SASInterstitialView
        sasInterstitialView.loadAd(Integer.parseInt(site_id), page_id, Integer.parseInt(format_id), true,
                "", smartAdListener);


    }

    @Override
    public void show() {
        // find the rootView where to add the interstitialContainer
        View rootContentView = null;
        Context context = sasInterstitialView.getContext();
        if (context instanceof Activity) {
            // try to find root view via Activity if available
            rootContentView = ((Activity)context).getWindow().getDecorView();
        }

        // now actually add the interstitialContainer including appropriate padding fir status/navigation bars
        if (rootContentView instanceof ViewGroup) {
            ((ViewGroup)rootContentView).addView(interstitialContainer);
        }
    }

    @Override
    public boolean isReady() {
        return smartAdListener.isAdLoaded();
    }

    @Override
    public void destroy() {
        if (sasInterstitialView != null) {
            sasInterstitialView.setExpandParentContainer(null);
            sasInterstitialView.onDestroy();
            sasInterstitialView = null;
        }
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {
        destroy();
    }
}
