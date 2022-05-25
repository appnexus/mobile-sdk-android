/*
 *    Copyright 2020 APPNEXUS INC
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

package com.appnexus.opensdk.csr;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;

import com.appnexus.opensdk.BaseNativeAdResponse;
import com.appnexus.opensdk.NativeAdEventListener;
import com.appnexus.opensdk.ut.UTConstants;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeBannerAd;

import java.util.HashMap;
import java.util.List;

public class FBNativeBannerAdResponse extends BaseNativeAdResponse {
    private String title;
    private String description;
    private String callToAction;
    private Bitmap coverImage;
    private Bitmap icon;
    private NativeBannerAd nativeBannerAd;
    private String sponsporedBy = "";
    private Rating rating;
    private HashMap<String, Object> nativeElements = new HashMap<String, Object>();
    private boolean expired = false;
    private boolean registered = false;
    NativeAdEventListener nativeAdEventListener = null;
    private Runnable expireRunnable;
    private Handler fbNativeExpireHandler;
    private String creativeId = "";
    private ImageSize mainImageSize = new ImageSize(-1, -1);
    private ImageSize iconSize = new ImageSize(-1, -1);
    private String additionalDescription = "";
    private String vastXML = "";
    private String privacyLink = "";
    private Runnable aboutToExpireRunnable;

    static FBNativeBannerAdResponse createResponse(NativeBannerAd ad) {
        if (ad != null && ad.isAdLoaded()) {
            return new FBNativeBannerAdResponse(ad);
        } else {
            return null;
        }
    }


    FBNativeBannerAdResponse(NativeBannerAd ad) {
        this.nativeBannerAd = ad;
        title = nativeBannerAd.getAdHeadline();
        description = nativeBannerAd.getAdBodyText();
        sponsporedBy = nativeBannerAd.getSponsoredTranslation();
        nativeElements.put(NATIVE_ELEMENT_OBJECT, nativeBannerAd);
        if (nativeBannerAd.getAdChoicesIcon() != null) {
            nativeElements.put(FBSettings.KEY_ADCHOICES_ICON, nativeBannerAd.getAdChoicesIcon());
        }
        if (nativeBannerAd.getAdChoicesLinkUrl() != null) {
            nativeElements.put(FBSettings.KEY_ADCHOICES_LINKURL, nativeBannerAd.getAdChoicesLinkUrl());
        }
        callToAction = nativeBannerAd.getAdCallToAction();
        expireRunnable = new Runnable() {
            @Override
            public void run() {
                if (nativeAdEventListener != null) {
                    nativeAdEventListener.onAdExpired();
                }
                if (coverImage != null) {
                    coverImage.recycle();
                    coverImage = null;
                }
                if (icon != null) {
                    icon.recycle();
                    icon = null;
                }
                nativeAdEventListener = null;
                expired = true;
                if (nativeBannerAd != null) {
                    nativeBannerAd.destroy();
                    nativeBannerAd = null;
                }
                if (nativeElements != null && !nativeElements.isEmpty()) {
                    nativeElements.clear();
                }
            }
        };

        aboutToExpireRunnable = new Runnable() {
            @Override
            public void run() {
                if (nativeAdEventListener != null) {
                    nativeAdEventListener.onAdAboutToExpire();
                }
                if (fbNativeExpireHandler != null) {
                    fbNativeExpireHandler.postDelayed(expireRunnable, getExpiryInterval(UTConstants.CSR, 0));
                }
            }
        };

        fbNativeExpireHandler = new Handler(Looper.getMainLooper());
        fbNativeExpireHandler.postDelayed(aboutToExpireRunnable, getAboutToExpireTime(UTConstants.CSR, 0));
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
        if (this.nativeBannerAd != null) {
            this.nativeBannerAd.unregisterView();
            this.nativeBannerAd.destroy();
            this.nativeBannerAd = null;
        }
        destroy();
    }

    @Override
    public void destroy() {
        super.destroy();
        nativeAdEventListener = null;
        if (fbNativeExpireHandler != null) {
            removeExpiryCallbacks();
            fbNativeExpireHandler.post(expireRunnable);
        }
    }

    public void unregisterView() {
        unregisterViews();
    }

    public boolean registerView(View containerView, MediaView iconView, NativeAdEventListener l) {
        if (!expired && this.nativeBannerAd != null) {
            this.nativeBannerAd.registerViewForInteraction(containerView, iconView);
            registerViewforOMID(containerView);
            nativeAdEventListener = l;
            return true;
        }
        return false;
    }

    public boolean registerView(View containerView, MediaView iconView, List<View> clickables, NativeAdEventListener l) {
        if (!expired && this.nativeBannerAd != null) {
            this.nativeBannerAd.registerViewForInteraction(containerView, iconView, clickables);
            registerViewforOMID(containerView);
            nativeAdEventListener = l;
            return true;
        }
        return false;
    }

    public boolean registerView(View containerView, ImageView iconView, NativeAdEventListener l) {
        if (!expired && this.nativeBannerAd != null) {
            this.nativeBannerAd.registerViewForInteraction(containerView, iconView);
            registerViewforOMID(containerView);
            nativeAdEventListener = l;
            return true;
        }
        return false;
    }

    public boolean registerView(View containerView, ImageView iconView, List<View> clickables, NativeAdEventListener l) {
        if (!expired && this.nativeBannerAd != null) {
            this.nativeBannerAd.registerViewForInteraction(containerView, iconView, clickables);
            registerViewforOMID(containerView);
            nativeAdEventListener = l;
            return true;
        }
        return false;
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
        return null;
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
        return null;
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
    public Rating getAdStarRating() {
        return null;
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
    public ImageSize getImageSize() {
        return mainImageSize;
    }

    @Override
    public String getAdditionalDescription() {
        return additionalDescription;
    }

    @Override
    public ImageSize getIconSize() {
        return iconSize;
    }

    @Override
    public String getVastXml() {
        return vastXML;
    }

    @Override
    public String getPrivacyLink() {
        return privacyLink;
    }

    protected void removeExpiryCallbacks() {
        if (fbNativeExpireHandler != null) {
            fbNativeExpireHandler.removeCallbacks(expireRunnable);
            fbNativeExpireHandler.removeCallbacks(aboutToExpireRunnable);
        }
    }

    @Override
    protected boolean registerNativeAdEventListener(NativeAdEventListener listener) {
        this.nativeAdEventListener = listener;
        return true;
    }
}
