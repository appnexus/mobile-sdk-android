/*
 *    Copyright 2015 APPNEXUS INC
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
import com.appnexus.opensdk.mediatedviews.YahooFlurrySettings;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.flurry.android.ads.FlurryAdNative;

import java.util.HashMap;
import java.util.List;

public class YahooFlurryNativeAdResponse implements NativeAdResponse {

    private FlurryAdNative adNative;
    private String title;
    private String callToAction;
    private Bitmap icon;
    private Bitmap coverImage;
    private Rating rating;
    private HashMap<String, Object> nativeElements = new HashMap<String, Object>();
    private boolean expired = false;
    private boolean registered = false;
    private NativeAdEventListener listener;
    private String fullText = "";
    private String sponsporedBy = "";
    private Runnable expireRunnable;
    private String creativeId = "";

    private Handler yHNativeExpireHandler;
    private View registeredView;
    private List<View> registeredClickables;

    private static String ONE_STAR = "20/100";
    private static String TWO_STAR = "40/100";
    private static String THREE_STAR = "60/100";
    private static String FOUR_STAR = "80/100";
    private static String FIVE_STAR = "100/100";
    private static String HEADLINE = "headline";
    private static String SUMMARY = "summary";
    private static String APPRATING = "appRating";

    static YahooFlurryNativeAdResponse create(FlurryAdNative adNative) {
        if (adNative != null) {
            YahooFlurryNativeAdResponse response = new YahooFlurryNativeAdResponse();
            response.adNative = adNative;
            response.nativeElements.put(YahooFlurrySettings.NATIVE_ELEMENT_OBJECT, adNative);
            if (adNative.getAsset(HEADLINE) != null) {
                response.title = adNative.getAsset(HEADLINE).getValue();
            }
            if (adNative.getAsset(SUMMARY) != null) {
                response.callToAction = adNative.getAsset(SUMMARY).getValue();
            }
            if (adNative.getAsset(APPRATING) != null) {
                String rating = adNative.getAsset(APPRATING).getValue();
                if (rating != null) {
                    if (rating.equals(ONE_STAR)) {
                        response.rating = new Rating(1, 5);
                    } else if (rating.equals(TWO_STAR)) {
                        response.rating = new Rating(2, 5);
                    } else if (rating.equals(THREE_STAR)) {
                        response.rating = new Rating(3, 5);
                    } else if (rating.equals(FOUR_STAR)) {
                        response.rating = new Rating(4, 5);
                    } else if (rating.equals(FIVE_STAR)) {
                        response.rating = new Rating(5, 5);
                    }
                }
            }
            if (adNative.getAsset(YahooFlurrySettings.ADVERTISER_NAME) != null) {
                response.nativeElements.put(YahooFlurrySettings.ADVERTISER_NAME, adNative.getAsset(YahooFlurrySettings.ADVERTISER_NAME));
            }
            if (adNative.getAsset(YahooFlurrySettings.APP_CATEGORY) != null) {
                response.nativeElements.put(YahooFlurrySettings.APP_CATEGORY, adNative.getAsset(YahooFlurrySettings.APP_CATEGORY));
            }
            if (adNative.getAsset(YahooFlurrySettings.SECURE_BRANDING_LOGO) != null) {
                response.nativeElements.put(YahooFlurrySettings.SECURE_BRANDING_LOGO, adNative.getAsset(YahooFlurrySettings.SECURE_BRANDING_LOGO));
            }
            if (adNative.getAsset(YahooFlurrySettings.SECURE_HQ_BRANDING_LOGO) != null) {
                response.nativeElements.put(YahooFlurrySettings.SECURE_HQ_BRANDING_LOGO, adNative.getAsset(YahooFlurrySettings.SECURE_HQ_BRANDING_LOGO));
            }
            if (adNative.getAsset(YahooFlurrySettings.SECURE_ORIGINAL_IMAGE) != null) {
                response.nativeElements.put(YahooFlurrySettings.SECURE_ORIGINAL_IMAGE, adNative.getAsset(YahooFlurrySettings.SECURE_ORIGINAL_IMAGE));
            }
            if (adNative.getAsset(YahooFlurrySettings.SECURE_HQ_IMAGE) != null) {
                response.nativeElements.put(YahooFlurrySettings.SECURE_HQ_IMAGE, adNative.getAsset(YahooFlurrySettings.SECURE_HQ_IMAGE));
            }
            if (adNative.getAsset(YahooFlurrySettings.SECURE_IMAGE) != null) {
                response.nativeElements.put(YahooFlurrySettings.SECURE_IMAGE, adNative.getAsset(YahooFlurrySettings.SECURE_IMAGE));
            }
            if (adNative.getAsset(YahooFlurrySettings.SECURE_RATING_IMG) != null) {
                response.nativeElements.put(YahooFlurrySettings.SECURE_RATING_IMG, adNative.getAsset(YahooFlurrySettings.SECURE_RATING_IMG));
            }
            if (adNative.getAsset(YahooFlurrySettings.SECURE_HQ_RATING_IMG) != null) {
                response.nativeElements.put(YahooFlurrySettings.SECURE_HQ_RATING_IMG, adNative.getAsset(YahooFlurrySettings.SECURE_HQ_RATING_IMG));
            }
            if (adNative.getAsset(YahooFlurrySettings.SHOW_RATING) != null) {
                response.nativeElements.put(YahooFlurrySettings.SHOW_RATING, adNative.getAsset(YahooFlurrySettings.SHOW_RATING));
            }
            return response;
        }
        return null;
    }

    private YahooFlurryNativeAdResponse() {
        expireRunnable = new Runnable() {
            @Override
            public void run() {
                expired = true;
                listener = null;
                if (adNative != null) {
                    adNative.removeTrackingView();
                    adNative.destroy();
                    adNative = null;
                }
                if(nativeElements != null && !nativeElements.isEmpty()){
                    nativeElements.clear();
                }
                registeredView = null;
                registeredClickables = null;
            }
        };
        yHNativeExpireHandler = new Handler(Looper.getMainLooper());
        yHNativeExpireHandler.postDelayed(expireRunnable, Settings.NATIVE_AD_RESPONSE_EXPIRATION_TIME);
    }

    @Override
    public Network getNetworkIdentifier() {
        return Network.YAHOO;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return null;
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
    public String getCreativeId() {
        return this.creativeId;
    }

    @Override
    public void setCreativeId(String creativeId) {
        this.creativeId = creativeId;
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
    public String getSocialContext() {
        return null;
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
        if (view != null && !registered && !expired) {
            this.listener = listener;
            adNative.setTrackingView(view);
            this.registeredView = view;
            // remove queued expiration steps on the SDK side if assets are being used.
            if(yHNativeExpireHandler!=null) {
                yHNativeExpireHandler.removeCallbacks(expireRunnable);
            }
            registered = true;
        }
        return registered;
    }

    @Override
    public boolean registerViewList(View view, List<View> clickables, NativeAdEventListener listener) {
        Clog.i(Clog.mediationLogTag, "Yahoo Flurry native ad does not provide api to register clickables.");
        return registerView(view, listener);
    }

    void onAdClicked() {
        if (listener != null) {
            listener.onAdWasClicked();
        }
    }

    void onAdWillLeaveApp() {
        if (listener != null) {
            listener.onAdWillLeaveApplication();
        }
    }

    @Override
    public void unregisterViews() {
        destroy();
    }

    @Override
    public void destroy() {
        if(yHNativeExpireHandler!=null) {
            yHNativeExpireHandler.removeCallbacks(expireRunnable);
            yHNativeExpireHandler.post(expireRunnable);
        }
        expired = true;
    }

    @Override
    public ImageSize getImageSize() {
        return null;
    }

    @Override
    public String getAdditionalDescription() {
        return "";
    }
}
