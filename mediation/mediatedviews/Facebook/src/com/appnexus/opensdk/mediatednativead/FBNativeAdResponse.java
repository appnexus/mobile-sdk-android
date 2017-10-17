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

package com.appnexus.opensdk.mediatednativead;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.appnexus.opensdk.NativeAdEventListener;
import com.appnexus.opensdk.NativeAdResponse;
import com.appnexus.opensdk.utils.Settings;
import com.facebook.ads.NativeAd;

import java.util.HashMap;
import java.util.List;

public class FBNativeAdResponse implements NativeAdResponse {
    private String title;
    private String description;
    private String imageUrl;
    private String iconUrl;
    private String callToAction;
    private Bitmap coverImage;
    private Bitmap icon;
    private NativeAd nativeAd;
    private String socialContext;
    private String fullText = "";
    private String sponsporedBy = "";
    private Rating rating;
    private HashMap<String, Object> nativeElements = new HashMap<String, Object>();
    private boolean expired = false;
    private boolean registered = false;
    private NativeAdEventListener listener;
    private Runnable runnable;

    public FBNativeAdResponse(NativeAd ad) {
        this.nativeAd = ad;
        runnable = new Runnable() {
            @Override
            public void run() {
                if (coverImage != null) {
                    coverImage.recycle();
                    coverImage = null;
                }
                if (icon != null) {
                    icon.recycle();
                    icon = null;
                }
                listener = null;
                expired = true;
                if (nativeAd != null) {
                    nativeAd.setAdListener(null);
                    nativeAd.destroy();
                    nativeAd = null;
                }
                if(nativeElements != null && !nativeElements.isEmpty()){
                    nativeElements.clear();
                }
            }
        };
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(runnable, Settings.NATIVE_AD_RESPONSE_EXPIRATION_TIME);
    }

    @Override
    public Network getNetworkIdentifier() {
        return Network.FACEBOOK;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public Bitmap getImage() {
        return coverImage;
    }

    @Override
    public void setImage(Bitmap bitmap) {
        this.coverImage = bitmap;
    }

    @Override
    public String getIconUrl() {
        return iconUrl;
    }

    @Override
    public Bitmap getIcon() {
        return icon;
    }

    @Override
    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    @Override
    public String getCallToAction() {
        return callToAction;
    }

    @Override
    public HashMap<String, Object> getNativeElements() {
        return nativeElements;
    }

    @Override
    public String getSocialContext() {
        return socialContext;
    }

    @Override
    public Rating getAdStarRating() {
        return rating;
    }

    @Override
    public String getFullText() {
        return fullText;
    }

    @Override
    public String getSponsoredBy() {
        return sponsporedBy;
    }

    boolean setResources() {
        if (nativeAd != null && nativeAd.isAdLoaded()) {
            title = nativeAd.getAdTitle();
            description = nativeAd.getAdBody();
            nativeElements.put(FacebookNativeSettings.NATIVE_ELEMENT_OBJECT, nativeAd);
            if(nativeAd.getAdChoicesIcon() != null) {
                nativeElements.put(FacebookNativeSettings.KEY_ADCHOICES_ICON, nativeAd.getAdChoicesIcon());
            }
            if(nativeAd.getAdChoicesLinkUrl() != null) {
                nativeElements.put(FacebookNativeSettings.KEY_ADCHOICES_LINKURL, nativeAd.getAdChoicesLinkUrl());
            }
            if (nativeAd.getAdIcon() != null) {
                iconUrl = nativeAd.getAdIcon().getUrl();
            }
            if (nativeAd.getAdCoverImage() != null) {
                imageUrl = nativeAd.getAdCoverImage().getUrl();
            }
            callToAction = nativeAd.getAdCallToAction();
            socialContext = nativeAd.getAdSocialContext();
            if (nativeAd.getAdStarRating() != null) {
                rating = new Rating(nativeAd.getAdStarRating().getValue(),
                        nativeAd.getAdStarRating().getScale());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean hasExpired() {
        return expired;
    }

    @Override
    public boolean registerView(View view, NativeAdEventListener listener) {
        if (nativeAd != null && !registered && !expired) {
            nativeAd.registerViewForInteraction(view);
            registered = true;
            Handler handler = new Handler(Looper.getMainLooper());
            handler.removeCallbacks(runnable);
        }
        this.listener = listener;
        return registered;
    }

    @Override
    public boolean registerViewList(View view, List<View> clickables, NativeAdEventListener listener) {
        if (nativeAd != null && !registered && !expired) {
            nativeAd.registerViewForInteraction(view, clickables);
            registered = true;
            Handler handler = new Handler(Looper.getMainLooper());
            handler.removeCallbacks(runnable);
        }
        this.listener = listener;
        return registered;
    }

    NativeAdEventListener getListener() {
        return listener;
    }


    @Override
    public void unregisterViews() {
        if (nativeAd != null) {
            nativeAd.unregisterView();
        }
        destroy();
    }

    @Override
    public void destroy() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.removeCallbacks(runnable);
        handler.post(runnable);
    }
}
