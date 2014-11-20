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

package com.appnexus.opensdk.mediatedviews;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import com.appnexus.opensdk.MediatedBannerAdView;
import com.appnexus.opensdk.MediatedBannerAdViewController;
import com.appnexus.opensdk.TargetingParameters;
import com.millennialmedia.android.MMAdView;
import com.millennialmedia.android.MMRequest;
import com.millennialmedia.android.MMSDK;

import java.util.HashMap;

/**
 * This class is the Millennial Media banner adaptor it provides the functionality needed to allow
 * an application using the AppNexus SDK to load a banner ad through the Millennial Media SDK. The instantiation
 * of this class is done in response from the AppNexus server for a banner placement that is configured
 * to use MM to serve it. This class is never directly instantiated by the application.
 *
 * This class also serves as an example of how to write a Mediation adaptor for the AppNexus
 * SDK.
 *
 */
public class MillennialMediaBanner implements MediatedBannerAdView {
    MMAdView adView=null;
    @Override
    public View requestAd(MediatedBannerAdViewController mBC, Activity activity, String parameter, String uid,
                          int width, int height, TargetingParameters targetingParameters) {
        MillennialMediaListener mmListener = new MillennialMediaListener(mBC, super.getClass().getSimpleName());
        mmListener.printToClog(String.format("requesting an ad: [%s, %s, %dx%d]", parameter, uid, width, height));

        MMSDK.initialize(activity);

        adView = new MMAdView(activity);
        adView.setApid(uid);
        adView.setWidth(width);
        adView.setHeight(height);

        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        int wpx = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, displayMetrics);
        int hpx = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, displayMetrics);

        //Fix the AdView dimensions so we don't show any white padding to the left and right
        ViewGroup.LayoutParams lps = new ViewGroup.LayoutParams(wpx, hpx);

        adView.setLayoutParams(lps);

        MMRequest mmRequest = new MMRequest();


        switch(targetingParameters.getGender()){
            case UNKNOWN:
                mmRequest.setGender(MMRequest.GENDER_OTHER);
                break;
            case FEMALE:
                mmRequest.setGender(MMRequest.GENDER_FEMALE);
                break;
            case MALE:
                mmRequest.setGender(MMRequest.GENDER_MALE);
                break;
        }

        if(targetingParameters.getAge()!=null){
            mmRequest.setAge(targetingParameters.getAge());
        }

        HashMap<String, String> mv = new HashMap<String, String>();
        for(Pair<String, String> p : targetingParameters.getCustomKeywords()){
            mv.put(p.first, p.second);
        }
        if(targetingParameters.getLocation()!=null){
            MMRequest.setUserLocation(targetingParameters.getLocation());
        }
        mmRequest.setMetaValues(mv);

        adView.setMMRequest(mmRequest);
        adView.setListener(mmListener);
        adView.getAd();

        return adView;
    }

    @Override
    public void destroy() {
        //No available API
        if(adView!=null){
            try {
                adView.setListener(null);
            }catch(NullPointerException npe){
                //since the interstitials cause NPEs
                //guard against banner as well to be safe
            }
            adView=null;
        }
    }

    @Override
    public void onPause() {
        //No available API
    }

    @Override
    public void onResume() {
        //No available API
    }

    @Override
    public void onDestroy() {
        destroy(); //No available API
    }
}
