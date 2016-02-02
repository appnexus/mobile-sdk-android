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

package com.appnexus.opensdk.mediatedviews;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.admarvel.android.ads.AdMarvelView;
import com.appnexus.opensdk.MediatedBannerAdView;
import com.appnexus.opensdk.MediatedBannerAdViewController;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * This class is the AdMarvel banner adaptor it provides the functionality needed to allow
 * an application using the AppNexus SDK to load a banner ad through the AdMarvel SDK. The instantiation
 * of this class is done in response from the AppNexus server for a banner placement that is configured
 * to use AdMarvel to serve it. This class is never directly instantiated by the application.
 * <p/>
 * This class also serves as an example of how to write a Mediation adaptor for the AppNexus
 * SDK.
 */
public class AdMarvelBannerAdView implements MediatedBannerAdView {
    private AdMarvelView adView;
    private WeakReference<Activity> activityWeakReference;

    @Override
    public View requestAd(MediatedBannerAdViewController mBC, Activity activity, String parameter, String uid,
                          int width, int height, TargetingParameters targetingParameters) {
        if (mBC != null) {
            if (!StringUtil.isEmpty(uid)) {
                try {
                    JSONObject ids = new JSONObject(uid);
                    String partnerId = ids.getString(AdMarvelListener.PARTNER_ID);
                    String siteId = ids.getString(AdMarvelListener.SITE_ID);
                    activityWeakReference = new WeakReference<Activity>(activity);
                    adView = new AdMarvelView(activity);
                    adView.setEnableClickRedirect(true);
                    adView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                    adView.setListener(new AdMarvelListener(mBC, this.getClass().getSimpleName()));
                    adView.requestNewAd(AdMarvelListener.getTargetingParameters(targetingParameters), partnerId, siteId, activity);
                    return adView;
                } catch (JSONException e) {
                }
            }
            Clog.d(Clog.mediationLogTag, "AdMarvel ids are not passed in properly, aborting ad request.");
            mBC.onAdFailed(ResultCode.INVALID_REQUEST);
        }
        return null;
    }

    @Override
    public void destroy() {
        if (adView != null) {
            adView.destroy();
        }
    }

    @Override
    public void onPause() {
        Activity activity = activityWeakReference.get();
        if (adView != null && activity != null) {
            adView.pause(activity);
        }
    }

    @Override
    public void onResume() {
        Activity activity = activityWeakReference.get();
        if (adView != null && activity != null) {
            adView.resume(activity);
        }
    }

    @Override
    public void onDestroy() {
        destroy();
    }

}
