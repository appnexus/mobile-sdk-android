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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.appnexus.opensdk.MediatedBannerAdView;
import com.appnexus.opensdk.MediatedBannerAdViewController;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.StringUtil;
import com.smartadserver.android.library.SASBannerView;
import com.smartadserver.android.library.ui.SASAdView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class is the SmartAdServer Banner adapter. It provides the functionality needed to allow
 * an application using the App Nexus SDK to load a Banner ad through the SmartAdServer SDK. The
 * instantiation of this class is done in response from the AppNexus server for an banner
 * placement that is configured to use SmartAdServer to serve it. This class is never directly instantiated
 * by the developer.
 */

public class SmartAdServerBannerAdView implements MediatedBannerAdView {

    SASBannerView sasBannerView;
    SmartAdServerListener sasBannerListener = null;
    public static final String SITE_ID = "site_id";
    public static final String PAGE_ID = "page_id";
    public static final String FORMAT_ID = "format_id";


    @Override
    public View requestAd(MediatedBannerAdViewController mBC, Activity activity, String parameter, String uid, int width, int height, TargetingParameters tp) {
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
                mBC.onAdFailed(ResultCode.INVALID_REQUEST);
                return null;
            }
        } catch (JSONException e) {
            mBC.onAdFailed(ResultCode.INVALID_REQUEST);
            return null;
        }
        // Handler class to be notified of ad loading outcome
        sasBannerListener = new SmartAdServerListener(mBC, this.getClass().getSimpleName());

        // instantiate SASBannerView that will perform the Smart ad call
        sasBannerView = new SASBannerView(activity) {
            /**
             * Overriden to notify the ad was clicked
             */
            @Override
            public void open(String url) {
                super.open(url);
                if (isAdWasOpened()) {
                    sasBannerListener.onClicked();
                }
            }
        };

        // add state change listener to detect when ad is closed
        sasBannerView.addStateChangeListener(new SASAdView.OnStateChangeListener() {
            boolean wasOpened = false;
            public void onStateChanged(
                    SASAdView.StateChangeEvent stateChangeEvent) {
                switch (stateChangeEvent.getType()) {
                    case SASAdView.StateChangeEvent.VIEW_EXPANDED:
                        Clog.i(Clog.mediationLogTag, "SmartAdServer: MRAID state : EXPANDED");
                        // ad was expanded
                        sasBannerListener.onExpanded();
                        wasOpened = true;
                        break;
                    case SASAdView.StateChangeEvent.VIEW_DEFAULT:
                        Clog.i(Clog.mediationLogTag, "SmartAdServer: MRAID state : DEFAULT");
                        // ad was collapsed
                        if (wasOpened) {
                            sasBannerListener.onCollapsed();
                            wasOpened = false;
                        }
                        break;
                }
            }
        });



        sasBannerView.setMinimumWidth(width);
        sasBannerView.setMinimumHeight(height);
        sasBannerView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, toPixelUnits(activity,height), Gravity.CENTER));
        if (tp != null) {
            if (tp.getLocation() != null) {
                sasBannerView.setLocation(tp.getLocation());
            }
        }
        // Load banner ad with appropriate parameters (siteID,pageID,formatID,master,targeting,adResponseHandler)
        sasBannerView.loadAd(Integer.parseInt(site_id), page_id, Integer.parseInt(format_id), true, "", sasBannerListener);

        return sasBannerView;
    }

    @Override
    public void destroy() {
        if (sasBannerView != null) {
            sasBannerView.onDestroy();
            sasBannerView = null;
        }
    }

    @Override
    public void onPause() {
        //SASBannerView lacks a pause public api
    }

    @Override
    public void onResume() {
        //SASBannerView lacks a resume public api
    }

    @Override
    public void onDestroy() {
        destroy();
    }


    private int toPixelUnits(Activity activity, int dipUnit) {
        float density = activity.getResources().getDisplayMetrics().density;
        return Math.round(dipUnit * density);
    }
}
