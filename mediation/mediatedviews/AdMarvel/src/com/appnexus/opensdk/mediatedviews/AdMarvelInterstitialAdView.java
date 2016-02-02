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

import com.admarvel.android.ads.AdMarvelInterstitialAds;
import com.appnexus.opensdk.MediatedInterstitialAdView;
import com.appnexus.opensdk.MediatedInterstitialAdViewController;
import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * This class is the AdMarvel interstitial adaptor it provides the functionality needed to allow
 * an application using the AppNexus SDK to load an interstitial ad through the AdMarvel SDK. The instantiation
 * of this class is done in response from the AppNexus server for a interstitial placement that is configured
 * to use AdMarvel to serve it. This class is never directly instantiated by the application.
 * <p/>
 * This class also serves as an example of how to write a Mediation adaptor for the AppNexus
 * SDK.
 */
public class AdMarvelInterstitialAdView implements MediatedInterstitialAdView {
    private AdMarvelInterstitialAds adMarvelInterstitialAds;
    private WeakReference<Activity> weakReference;
    private AdMarvelListener adMarvelListener;


    @Override
    public void requestAd(MediatedInterstitialAdViewController mIC, Activity activity, String parameter, String uid, TargetingParameters targetingParameters) {
        if (mIC != null) {
            if (!StringUtil.isEmpty(uid)) {
                try {
                    JSONObject ids = new JSONObject(uid);
                    String partnerId = ids.getString(AdMarvelListener.PARTNER_ID);
                    String siteId = ids.getString(AdMarvelListener.SITE_ID);
                    AdMarvelInterstitialAds.setEnableClickRedirect(true);
                    weakReference = new WeakReference<Activity>(activity);
                    adMarvelInterstitialAds = new AdMarvelInterstitialAds(activity);
                    adMarvelListener =  new AdMarvelListener(mIC, this.getClass().getSimpleName());
                    adMarvelInterstitialAds.setListener(adMarvelListener);
                    adMarvelInterstitialAds.requestNewInterstitialAd(activity, AdMarvelListener.getTargetingParameters(targetingParameters), partnerId, siteId);
                    return;
                } catch (JSONException e) {
                }
            }
            Clog.d(Clog.mediationLogTag, "AdMarvel partner id is not set, aborting ad request.");
            mIC.onAdFailed(ResultCode.INVALID_REQUEST);
        }
    }

    @Override
    public void show() {
        Activity activity = weakReference.get();
        if (isReady() && activity != null && adMarvelListener != null) {
            adMarvelInterstitialAds.displayInterstitial(activity, adMarvelListener.getSdkAdNetwork(), adMarvelListener.getAdMarvelAd());
        }
    }

    @Override
    public boolean isReady() {
        if (adMarvelInterstitialAds != null) {
            return adMarvelInterstitialAds.isInterstitialAdAvailable();
        }
        return false;
    }

    @Override
    public void destroy() {
        adMarvelInterstitialAds = null;
        adMarvelListener = null;
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
