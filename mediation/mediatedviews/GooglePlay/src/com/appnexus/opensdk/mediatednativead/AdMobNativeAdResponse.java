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
package com.appnexus.opensdk.mediatednativead;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.appnexus.opensdk.BaseNativeAdResponse;
import com.appnexus.opensdk.NativeAdEventListener;
import com.appnexus.opensdk.ut.UTConstants;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

import java.util.HashMap;
import java.util.List;

public class AdMobNativeAdResponse extends BaseNativeAdResponse {
    private String title;
    private String description;
    private String imageUrl;
    private String iconUrl;
    private String callToAction;
    private Bitmap coverImage;
    private Bitmap icon;
    private String creativeId = "";
    private ImageSize mainImageSize = new ImageSize(-1, -1);
    private ImageSize iconSize = new ImageSize(-1, -1);
    private String additionalDescription = "";
    private String vastXML = "";
    private String privacyLink = "";

    private String sponsporedBy = "";
    private Rating rating;
    private HashMap<String, Object> nativeElements = new HashMap<String, Object>();
    private boolean expired = false;
    private boolean registered = false;
    private NativeAdEventListener listener;
    private Runnable expireRunnable;
    private Runnable aboutToExpireRunnable;

    private final NativeAd nativeAd;
    private Handler nativeExpireHandler;

    AdMobNativeAdResponse(final NativeAd ad) {
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
                if (AdMobNativeAdResponse.this.nativeAd != null) {
                    nativeAd.destroy();
                }
            }
        };

        aboutToExpireRunnable = new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.onAdAboutToExpire();
                }
                if (nativeExpireHandler != null) {
                    nativeExpireHandler.postDelayed(expireRunnable, getExpiryInterval(UTConstants.CSM, 0));
                }
            }
        };
        nativeExpireHandler = new Handler(Looper.getMainLooper());
        nativeExpireHandler.postDelayed(aboutToExpireRunnable, Settings.NATIVE_AD_RESPONSE_EXPIRATION_TIME_CSM_CSR);
        loadAssets();
    }

    private void loadAssets() {
        nativeElements.put(NATIVE_ELEMENT_OBJECT, nativeAd);
        if (nativeAd.getHeadline() != null) {
            title = nativeAd.getHeadline().toString();
        }
        if (nativeAd.getBody() != null) {
            description = nativeAd.getBody().toString();
        }
        if (nativeAd.getCallToAction() != null) {
            callToAction = nativeAd.getCallToAction().toString();
        }
        if (nativeAd.getIcon() != null) {
            NativeAd.Image iconImage = nativeAd.getIcon();
            if (iconImage.getUri() != null) {
                iconUrl = iconImage.getUri().toString();
            }
        }
        if(nativeAd.getImages() !=null) {
            List<NativeAd.Image> images = nativeAd.getImages();
            if (images != null && images.size() > 0) {
                NativeAd.Image image = images.get(0);
                if (image.getUri() != null) {
                    imageUrl = image.getUri().toString();
                }
            }
        }
        if (nativeAd.getStarRating() != null && nativeAd.getStarRating() > 0) {
            rating = new Rating(nativeAd.getStarRating(), 5.0);
        }
        if (nativeAd.getStore() != null) {
            nativeElements.put(AdMobNativeSettings.NATIVE_ELEMENT_STORE_KEY, nativeAd.getStore().toString());
        }
        if (nativeAd.getPrice() != null) {
            nativeElements.put(AdMobNativeSettings.NATIVE_ELEMENT_PRICE_KEY, nativeAd.getPrice());
        }
        if (nativeAd.getAdvertiser() != null) {
            nativeElements.put(AdMobNativeSettings.NATIVE_ELEMENT_ADVERTISER_KEY, nativeAd.getAdvertiser().toString());
        }
        Bundle bundle = nativeAd.getExtras();
        if (bundle != null && bundle.size() > 0) {
            for (String key : bundle.keySet()) {
                nativeElements.put(key, bundle.get(key));
            }
        }
    }

    @Override
    public Network getNetworkIdentifier() {
        return Network.ADMOB;
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
    public Rating getAdStarRating() {
        return rating;
    }

    @Override
    public String getSponsoredBy() {
        return sponsporedBy;
    }

    @Override
    public boolean hasExpired() {
        return expired;
    }

    private NativeAdView adView = null;

    @Override
    protected boolean registerView(View view, NativeAdEventListener listener) {
        if (view != null && !registered && !expired) {
            try {
                adView = (NativeAdView) view;
            } catch (ClassCastException e) {
                Clog.w(Clog.mediationLogTag, "The view registered for AdMob NativeAd has to be a subclass of com.google.android.gms.ads.nativead.NativeAdView");
            }
            if (adView != null) {
                adView.setNativeAd(nativeAd);
                // no way to pass on click action to listener
                this.listener = listener;
                registered = true;
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean registerViewList(View view, List<View> clickables, NativeAdEventListener listener) {
        return registerView(view, listener);
    }

    NativeAdEventListener getListener() {
        return listener;
    }


    @Override
    protected void unregisterViews() {
        if (expired) {
            Clog.d(Clog.mediationLogTag, "This NativeAdResponse has expired.");
        }
        if (adView != null) {
            adView = null;
        }
        destroy();
    }

    @Override
    protected boolean registerNativeAdEventListener(NativeAdEventListener listener) {
        this.listener = listener;
        return true;
    }

    @Override
    public void destroy() {
        super.destroy();
        if(nativeExpireHandler!=null) {
            removeExpiryCallbacks();
            nativeExpireHandler.post(expireRunnable);
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
        if (nativeExpireHandler != null) {
            nativeExpireHandler.removeCallbacks(aboutToExpireRunnable);
            nativeExpireHandler.removeCallbacks(expireRunnable);
        }
    }
}
