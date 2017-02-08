/*
 *    Copyright 2016 APPNEXUS INC
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

package com.example.simplevideo;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.instreamvideo.VideoAd;
import com.appnexus.opensdk.instreamvideo.VideoAdLoadListener;


//This is a singleton class which helps in holding the VideoAd object across activities.
public final class AdVideoData {
    public static final String TAG = AdVideoData.class.getName();
    private static AdVideoData instance = null;

    private VideoAd videoAd;

    private AdVideoData() {
    }

    public static synchronized AdVideoData getInstance() {
        if (instance == null) instance = new AdVideoData();
        return instance;
    }


    void init(final Context context) {
        //9924002  - Shawn android RTB
        //9858067  - VPAID
        //9904418  - CSM/RTB
        //10072555 - Mediation
        //5712441  - CSM Desktop Placement 3-CSM+1RTB.
        //10588595 - Only Tremor
        videoAd = new VideoAd(context, "10588595");
        videoAd.setAge("25");
        videoAd.setAdLoadListener(new VideoAdLoadListener() {
            @Override
            public void onAdLoaded(VideoAd adView) {
                Log.i(TAG, "onAdLoaded");
                Toast.makeText(context,"AD_READY",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdRequestFailed(VideoAd adView, ResultCode errorCode) {
                Log.i(TAG, "onAdRequestFailed errorCode=" + errorCode);
                Toast.makeText(context,"AD_FAILED::"+errorCode,Toast.LENGTH_SHORT).show();
            }
        });
        videoAd.setOpensNativeBrowser(false);

    }

    void loadAd(){
        videoAd.loadAd();
    }


    public VideoAd getVideoAd() {
        return videoAd;
    }


}
