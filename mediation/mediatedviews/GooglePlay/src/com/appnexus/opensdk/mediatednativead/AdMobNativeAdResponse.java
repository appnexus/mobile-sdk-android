package com.appnexus.opensdk.mediatednativead;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.appnexus.opensdk.NativeAdEventListener;
import com.appnexus.opensdk.NativeAdResponse;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdView;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;

import java.util.HashMap;
import java.util.List;

public class AdMobNativeAdResponse implements NativeAdResponse {


    private String title;
    private String description;
    private String imageUrl;
    private String iconUrl;
    private String callToAction;
    private Bitmap coverImage;
    private Bitmap icon;
    private String socialContext;
    private Rating rating;
    private HashMap<String, Object> nativeElements = new HashMap<String, Object>();
    private boolean expired = false;
    private boolean registered = false;
    private NativeAdEventListener listener;
    private Runnable runnable;

    private final NativeAd nativeAd;
    private final AdMobNativeSettings.AdMobNativeType type;

    AdMobNativeAdResponse(NativeAd ad, AdMobNativeSettings.AdMobNativeType type) {
        this.nativeAd = ad;
        this.type = type;
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
                if (AdMobNativeAdResponse.this.nativeAd != null) {
                    try {
                        switch (AdMobNativeAdResponse.this.type) {
                            case APP_INSTALL:
                                NativeAppInstallAd appInstallAd = (NativeAppInstallAd) AdMobNativeAdResponse.this.nativeAd;
                                appInstallAd.destroy();
                                break;
                            case CONTENT_AD:
                                NativeContentAd contentAd = (NativeContentAd) AdMobNativeAdResponse.this.nativeAd;
                                contentAd.destroy();
                                break;
                        }
                    } catch (ClassCastException e) {
                    }
                }
            }
        };
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(runnable, Settings.NATIVE_AD_RESPONSE_EXPIRATION_TIME);
        loadAssets();
    }

    private void loadAssets() {
        nativeElements.put(AdMobNativeSettings.NATIVE_ELEMENT_TYPE_KEY, type);
        switch (type) {
            case APP_INSTALL:
                NativeAppInstallAd appInstallAd = (NativeAppInstallAd) nativeAd;
                if (appInstallAd.getHeadline() != null) {
                    title = appInstallAd.getHeadline().toString();
                }
                if (appInstallAd.getBody() != null) {
                    description = appInstallAd.getBody().toString();
                }
                if (appInstallAd.getCallToAction() != null) {
                    callToAction = appInstallAd.getCallToAction().toString();
                }
                if (appInstallAd.getIcon() != null) {
                    NativeAd.Image iconImage = appInstallAd.getIcon();
                    if (iconImage.getUri() != null) {
                        iconUrl = iconImage.getUri().toString();
                    }
                }
                List<NativeAd.Image> images = appInstallAd.getImages();
                if (images != null && images.size() > 0) {
                    NativeAd.Image image = images.get(0);
                    if (image.getUri() != null) {
                        imageUrl = image.getUri().toString();
                    }
                }
                if (appInstallAd.getStarRating() > 0) {
                    rating = new Rating(appInstallAd.getStarRating(), 5.0);
                }
                if (appInstallAd.getStore() != null) {
                    nativeElements.put(AdMobNativeSettings.NATIVE_ELEMENT_STORE_KEY, appInstallAd.getStore().toString());
                }
                if (appInstallAd.getPrice() != null) {
                    nativeElements.put(AdMobNativeSettings.NATIVE_ELEMENT_PRICE_KEY, appInstallAd.getPrice());
                }
                Bundle bundle = appInstallAd.getExtras();
                if (bundle != null && bundle.size() > 0) {
                    for (String key : bundle.keySet()) {
                        nativeElements.put(key, bundle.get(key));
                    }
                }
                break;
            case CONTENT_AD:
                NativeContentAd contentAd = (NativeContentAd) nativeAd;
                if (contentAd.getHeadline() != null) {
                    title = contentAd.getHeadline().toString();
                }
                if (contentAd.getBody() != null) {
                    description = contentAd.getBody().toString();
                }
                if (contentAd.getCallToAction() != null) {
                    callToAction = contentAd.getCallToAction().toString();
                }
                if (contentAd.getLogo() != null) {
                    NativeAd.Image iconImage = contentAd.getLogo();
                    if (iconImage.getUri() != null) {
                        iconUrl = iconImage.getUri().toString();
                    }
                }
                List<NativeAd.Image> contentAdImages = contentAd.getImages();
                if (contentAdImages != null && contentAdImages.size() > 0) {
                    NativeAd.Image image = contentAdImages.get(0);
                    if (image.getUri() != null) {
                        imageUrl = image.getUri().toString();
                    }
                }
                if (contentAd.getAdvertiser() != null) {
                    nativeElements.put(AdMobNativeSettings.NATIVE_ELEMENT_ADVERTISER_KEY, contentAd.getAdvertiser().toString());
                }
                Bundle bundle1 = contentAd.getExtras();
                if (bundle1 != null && bundle1.size() > 0) {
                    for (String key : bundle1.keySet()) {
                        nativeElements.put(key, bundle1.get(key));
                    }
                }
                break;
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
    public String getSocialContext() {
        return socialContext;
    }

    @Override
    public Rating getAdStarRating() {
        return rating;
    }

    @Override
    public boolean hasExpired() {
        return expired;
    }

    private NativeAdView adView = null;

    @Override
    public boolean registerView(View view, NativeAdEventListener listener) {
        if (view != null && !registered) {
            try {
                switch (type) {
                    case APP_INSTALL:
                        adView = (NativeAppInstallAdView) view;
                        break;
                    case CONTENT_AD:
                        adView = (NativeContentAdView) view;
                        break;
                }
                adView.setNativeAd(nativeAd);
                // no way to pass on click action to listener
                this.listener = listener;
                registered = true;
                return true;
            } catch (ClassCastException e) {
                Clog.w(Clog.mediationLogTag, "The view registered for AdMob native response has to be a subclass of com.google.android.gms.ads.formats.NativeAdView");
            }
        }
        return true;
    }

    @Override
    public boolean registerViewList(View view, List<View> clickables, NativeAdEventListener listener) {
        return registerView(view, listener);
    }

    @Override
    public void unregisterViews() {
        if (adView != null) {
            adView.setNativeAd(null);
            adView = null;
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
