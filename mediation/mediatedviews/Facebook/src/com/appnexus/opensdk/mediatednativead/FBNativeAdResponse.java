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
import android.view.ViewGroup;

import com.appnexus.opensdk.BaseNativeAdResponse;
import com.appnexus.opensdk.NativeAdEventListener;
import com.appnexus.opensdk.ut.UTConstants;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;

import java.util.HashMap;
import java.util.List;

public class FBNativeAdResponse extends BaseNativeAdResponse {
    private String title;
    private String description;
    private String callToAction;
    private Bitmap coverImage;
    private Bitmap icon;
    private NativeAd nativeAd;
    private String sponsporedBy = "";
    private Rating rating;
    private HashMap<String, Object> nativeElements = new HashMap<String, Object>();
    private boolean expired = false;
    private boolean registered = false;
    private NativeAdEventListener listener;
    private Runnable expireRunnable;
    private Handler fbNativeExpireHandler;
    private Runnable aboutToExpireRunnable;
    private String creativeId = "";
    private ImageSize mainImageSize = new ImageSize(-1, -1);
    private ImageSize iconSize = new ImageSize(-1, -1);
    private String additionalDescription = "";
    private String vastXML = "";
    private String privacyLink = "";

    private MediaView adMediaView = null;
    private MediaView adIconView = null;

    public FBNativeAdResponse(NativeAd ad) {
        this.nativeAd = ad;
        expireRunnable = new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.onAdExpired();
                }

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
                    nativeAd.destroy();
                    nativeAd = null;
                }
                if(nativeElements != null && !nativeElements.isEmpty()){
                    nativeElements.clear();
                }
            }
        };

        aboutToExpireRunnable = new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.onAdAboutToExpire();
                }
                if (fbNativeExpireHandler != null) {
                    fbNativeExpireHandler.postDelayed(expireRunnable, getExpiryInterval(UTConstants.CSM, 0));
                }
            }
        };

        fbNativeExpireHandler = new Handler(Looper.getMainLooper());
        fbNativeExpireHandler.postDelayed(aboutToExpireRunnable, getAboutToExpireTime(UTConstants.CSM, 0));
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
        return "";
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
    public String getCreativeId() {
        return this.creativeId;
    }

    @Override
    public void setCreativeId(String creativeId) {
        this.creativeId = creativeId;
    }

    @Override
    public String getIconUrl() {
        return "";
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
    public Rating getAdStarRating() {
        return rating;
    }

    @Override
    public String getSponsoredBy() {
        return sponsporedBy;
    }

    boolean setResources() {
        if (nativeAd != null && nativeAd.isAdLoaded()) {
            title = nativeAd.getAdHeadline();
            description = nativeAd.getAdBodyText();
            sponsporedBy = nativeAd.getSponsoredTranslation();
            nativeElements.put(NATIVE_ELEMENT_OBJECT, nativeAd);
            if(nativeAd.getAdChoicesIcon() != null) {
                nativeElements.put(FacebookNativeSettings.KEY_ADCHOICES_ICON, nativeAd.getAdChoicesIcon());
            }
            if(nativeAd.getAdChoicesLinkUrl() != null) {
                nativeElements.put(FacebookNativeSettings.KEY_ADCHOICES_LINKURL, nativeAd.getAdChoicesLinkUrl());
            }
            callToAction = nativeAd.getAdCallToAction();
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

    private boolean getMediaViewsForRegisterView(View view) {

        if(view instanceof ViewGroup){
            ViewGroup subViews =  (ViewGroup)view;
            for (int i = 0; i < subViews.getChildCount(); i++) {
                final View subview = subViews.getChildAt(i);
                if (subview instanceof MediaView) {
                    if (subview.getTag() != null) {
                        adIconView = (MediaView) subview;
                    } else {
                        adMediaView = (MediaView) subview;
                    }
                }else if(subview instanceof ViewGroup){
                    getMediaViewsForRegisterView(subview);
                }
                if(adMediaView != null && adIconView != null){
                    break;
                }
            }
        }
        if(adMediaView != null){
            return true;
        }
        return false;
    }

    @Override
    protected boolean registerView(View view, NativeAdEventListener listener) {
        if (nativeAd != null && !registered && !expired) {
            if(getMediaViewsForRegisterView(view)) {
                if (adIconView != null) {
                    nativeAd.registerViewForInteraction(view, adMediaView, adIconView);
                } else {
                    nativeAd.registerViewForInteraction(view, adMediaView);
                }
                registered = true;
            }
        }
        this.listener = listener;
        return registered;
    }

    @Override
    protected boolean registerViewList(View view, List<View> clickables, NativeAdEventListener listener) {
        if (nativeAd != null && !registered && !expired) {
            if(getMediaViewsForRegisterView(view)) {
               if (adIconView != null) {
                    nativeAd.registerViewForInteraction(view, adMediaView, adIconView ,clickables);
                }else{
                    nativeAd.registerViewForInteraction(view, adMediaView , clickables);
                }
                registered = true;
            }
        }
        this.listener = listener;
        return registered;
    }

    NativeAdEventListener getListener() {
        return listener;
    }


    @Override
    protected void unregisterViews() {
        if (nativeAd != null) {
            nativeAd.unregisterView();
        }
        destroy();
    }

    @Override
    public void destroy() {
        super.destroy();
        if(fbNativeExpireHandler!=null) {
            removeExpiryCallbacks();
            fbNativeExpireHandler.post(expireRunnable);
        }
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
        if(fbNativeExpireHandler!=null) {
            fbNativeExpireHandler.removeCallbacks(expireRunnable);
            fbNativeExpireHandler.removeCallbacks(aboutToExpireRunnable);
        }
    }

    @Override
    protected boolean registerNativeAdEventListener(NativeAdEventListener listener) {
        this.listener = listener;
        return true;
    }
}
