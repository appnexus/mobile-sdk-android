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
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.appnexus.opensdk.MediatedBannerAdView;
import com.appnexus.opensdk.MediatedBannerAdViewController;
import com.appnexus.opensdk.utils.Clog;
import com.millennialmedia.android.MMAdView;
import com.millennialmedia.android.MMRequest;
import com.millennialmedia.android.MMSDK;

public class MillennialMediaBanner implements MediatedBannerAdView {

    @Override
    public View requestAd(MediatedBannerAdViewController mBC, Activity activity, String parameter, String uid,
                          int width, int height) {
        if (mBC == null) {
            Clog.e(Clog.mediationLogTag, "MillennialMediaBanner - requestAd called with null controller");
            return null;
        }

        if (activity == null) {
            Clog.e(Clog.mediationLogTag, "MillennialMediaBanner - requestAd called with null activity");
            return null;
        }
        Clog.d(Clog.mediationLogTag, String.format("MillennialMediaBanner - requesting an ad: [%s, %s, %dx%d]", parameter, uid, width, height));

        MMSDK.initialize(activity);

        MMAdView adView = new MMAdView(activity);        
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
        adView.setMMRequest(mmRequest);
        adView.setListener(new MillennialMediaListener(mBC, getClass().getSimpleName()));
        adView.getAd();

        return adView;
    }
}
