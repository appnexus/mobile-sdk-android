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

package com.appnexus.opensdk.testviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import com.appnexus.opensdk.ANAdResponseInfo;
import com.appnexus.opensdk.BaseNativeAdResponse;
import com.appnexus.opensdk.MediatedNativeAd;
import com.appnexus.opensdk.MediatedNativeAdController;
import com.appnexus.opensdk.NativeAdEventListener;
import com.appnexus.opensdk.NativeAdResponse;
import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.util.Lock;

import java.util.HashMap;
import java.util.List;

public class MediatedNativeSuccessful2 implements MediatedNativeAd {
    public static String TITLE = "test title2";
    public static String DESCRIPTION = "test description2";
    public static String ADDITIONAL_DESCRIPTION = "";
    public static String ImageUrl = "test image url2";
    public static String SponsoredBy = "test sponsored by";
    public static boolean didPass;
    private NativeAdResponse.ImageSize mainImageSize = new NativeAdResponse.ImageSize(-1, -1);
    private NativeAdResponse.ImageSize iconSize = new NativeAdResponse.ImageSize(-1, -1);
    private ANAdResponseInfo adResponseInfo;

    @Override
    public void requestNativeAd(Context context, String parameterString, String uid, MediatedNativeAdController mBC, TargetingParameters tp) {
        if (mBC != null) {
            didPass = true;
            Lock.explicitSleep(2); // This is for generating latency and total latency in the response url
            mBC.onAdLoaded(new BaseNativeAdResponse() {
                @Override
                public Network getNetworkIdentifier() {
                    return Network.APPNEXUS;
                }

                @Override
                public String getTitle() {
                    return TITLE;
                }

                @Override
                public String getDescription() {
                    return DESCRIPTION;
                }

                @Override
                public String getImageUrl() {
                    return ImageUrl;
                }

                @Override
                public Bitmap getImage() {
                    return null;
                }

                @Override
                public void setImage(Bitmap bitmap) {

                }

                @Override
                public ANAdResponseInfo getAdResponseInfo() {
                    return adResponseInfo;
                }

                @Override
                public void setAdResponseInfo(ANAdResponseInfo adResponseInfo) {
                    MediatedNativeSuccessful2.this.adResponseInfo = adResponseInfo;
                }

                @Override
                public String getIconUrl() {
                    return null;
                }

                @Override
                public Bitmap getIcon() {
                    return null;
                }

                @Override
                public void setIcon(Bitmap bitmap) {

                }

                @Override
                public String getCallToAction() {
                    return null;
                }

                @Override
                public String getSponsoredBy() { return SponsoredBy; }

                @Override
                public HashMap<String, Object> getNativeElements() {
                    return null;
                }

                @Override
                public Rating getAdStarRating() {
                    return null;
                }

                @Override
                public boolean hasExpired() {
                    return false;
                }

                @Override
                protected boolean registerView(View view, NativeAdEventListener listener) {
                    return false;
                }

                @Override
                protected boolean registerViewList(View view, List<View> clickables, NativeAdEventListener listener) {
                    return false;
                }

                @Override
                protected void unregisterViews() {

                }

                @Override
                protected boolean registerNativeAdEventListener(NativeAdEventListener listener) {
                    return false;
                }

                @Override
                public void destroy() {

                }

                @Override
                public ImageSize getImageSize() {
                    return mainImageSize;
                }

                @Override
                public String getAdditionalDescription() {
                    return ADDITIONAL_DESCRIPTION;
                }

                @Override
                public ImageSize getIconSize() {
                    return iconSize;
                }

                @Override
                public String getVastXml() {
                    return null;
                }

                @Override
                public String getPrivacyLink() {
                    return null;
                }
            });
        }
    }
}
