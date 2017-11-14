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
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.mopub.nativeads.BaseNativeAd;
import com.mopub.nativeads.NativeAd;
import com.mopub.nativeads.StaticNativeAd;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoPubNativeAdResponse implements NativeAdResponse {
    private String title;
    private String description;
    private String imageUrl;
    private String iconUrl;
    private String callToAction;
    private Bitmap icon;
    private Bitmap coverImage;
    private String socialContext;
    private String fullText = "";
    private String sponsporedBy = "";
    private Rating rating;
    private HashMap<String, Object> nativeElements = new HashMap<String, Object>();
    private boolean expired = false;
    private boolean registered = false;
    private NativeAd mopubNativeAd;
    private NativeAdEventListener listener;
    private Runnable runnable;
    private View registeredView;
    private List<View> registeredClickables;
    private Handler mopubNativeExpireHandler;


    public MoPubNativeAdResponse() {
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
                if (mopubNativeAd != null) {
                    mopubNativeAd.destroy();
                    mopubNativeAd = null;
                }
                registeredView = null;
                registeredClickables = null;
            }
        };
        mopubNativeExpireHandler = new Handler(Looper.getMainLooper());
        mopubNativeExpireHandler.postDelayed(runnable, Settings.NATIVE_AD_RESPONSE_EXPIRATION_TIME);
    }

    boolean setResources(NativeAd response) {
        if (response != null) {
            this.mopubNativeAd = response;
            BaseNativeAd baseNativeAd = response.getBaseNativeAd();
            if (!(baseNativeAd instanceof StaticNativeAd)) {
                return false;
            }
            final StaticNativeAd staticNativeAd = (StaticNativeAd) baseNativeAd;

            this.title = staticNativeAd.getTitle();
            this.description = staticNativeAd.getText();
            this.imageUrl = staticNativeAd.getMainImageUrl();
            this.iconUrl = staticNativeAd.getIconImageUrl();
            this.callToAction = staticNativeAd.getCallToAction();
            if (staticNativeAd.getStarRating() != null) {
                this.rating = new Rating(staticNativeAd.getStarRating(), 5.0);
            }
            if (!staticNativeAd.getExtras().isEmpty()) {
                // put extras in native response, MoPub returns String as Object
                for (Map.Entry<String, Object> entry : staticNativeAd.getExtras().entrySet()) {
                    nativeElements.put(entry.getKey(), entry.getValue());
                }
            }
            nativeElements.put(MoPubNativeSettings.NATIVE_ELEMENT_OBJECT, response);
            if (staticNativeAd.getPrivacyInformationIconImageUrl() != null) {
                nativeElements.put(MoPubNativeSettings.KEY_PRIVACYINFO_ICONURL, staticNativeAd.getPrivacyInformationIconImageUrl());
            }
            if (staticNativeAd.getPrivacyInformationIconClickThroughUrl() != null) {
                nativeElements.put(MoPubNativeSettings.KEY_PRIVACYINFO_LINKURL, staticNativeAd.getPrivacyInformationIconClickThroughUrl());
            }
            return true;
        }
        return false;
    }

    @Override
    public Network getNetworkIdentifier() {
        return Network.MOPUB;
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
    public void setIcon(Bitmap bitmap) {
        this.icon = bitmap;
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

    @Override
    public boolean hasExpired() {
        return expired;
    }


    @Override
    public boolean registerView(View view, NativeAdEventListener listener) {
        if (mopubNativeAd != null && !registered && !expired) {
            mopubNativeAd.prepare(view);
            registeredView = view;
            if(mopubNativeExpireHandler!=null){
                mopubNativeExpireHandler.removeCallbacks(runnable);
            }
            registered = true;
        }
        this.listener = listener;
        return registered;
    }


    @Override
    public boolean registerViewList(View view, List<View> clickables, NativeAdEventListener listener) {
        if (mopubNativeAd != null && !registered && !expired) {
            registeredClickables = clickables;
            for (View clickable : clickables) {
                mopubNativeAd.prepare(clickable);
            }
            if(mopubNativeExpireHandler!=null) {
                mopubNativeExpireHandler.removeCallbacks(runnable);
            }
            registered = true;
        }
        this.listener = listener;
        return registered;
    }

    @Override
    public void unregisterViews() {
        if (hasExpired()) {
            Clog.d(Clog.mediationLogTag, "This NativeAdResponse has expired.");
        }
        if (mopubNativeAd != null) {
            if (registeredView != null) {
                mopubNativeAd.clear(registeredView);
            }
            if (registeredClickables != null && !registeredClickables.isEmpty()) {
                for (View view : registeredClickables) {
                    mopubNativeAd.clear(view);
                }
            }
        }
        destroy();

    }

    void onAdClicked() {
        if (listener != null) {
            listener.onAdWasClicked();
        }
    }

    void onAdWillLeaveApplication() {
        if (listener != null) {
            listener.onAdWillLeaveApplication();
        }
    }


    @Override
    public void destroy() {
        if(mopubNativeExpireHandler!=null) {
            mopubNativeExpireHandler.removeCallbacks(runnable);
            mopubNativeExpireHandler.post(runnable);
        }
    }
}