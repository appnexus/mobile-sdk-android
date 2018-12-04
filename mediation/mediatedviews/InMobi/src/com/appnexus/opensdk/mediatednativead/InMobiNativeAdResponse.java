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
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.JsonUtil;
import com.appnexus.opensdk.utils.Settings;
import com.inmobi.ads.InMobiNative;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class InMobiNativeAdResponse implements NativeAdResponse {
    private InMobiNative imNative;
    private String title;
    private String description;
    private String imageUrl;
    private String iconUrl;
    private String callToAction;
    private Bitmap icon;
    private Bitmap coverImage;
    private String sponsporedBy = "";
    private String[] impressionTrackers;
    private String creativeId = "";
    private ImageSize mainImageSize = new ImageSize(-1, -1);
    private ImageSize iconSize = new ImageSize(-1, -1);
    private String additionalDescription = "";
    private String vastXML = "";
    private String privacyLink = "";

    private Rating rating;
    private String landingUrl; // This is not exposed as of now. Click is done through reportAdClickAndOpenLandingPage since AppNexus SDK autohandles click.
    private HashMap<String, Object> nativeElements = new HashMap<String, Object>();
    private boolean expired = false;
    private boolean registered = false;
    private NativeAdEventListener nativeAdEventlistener;
    private Runnable runnable;
    private View registeredView;
    private List<View> registeredClickables;
    private View.OnClickListener clickListener;
    private Handler inMobiNativeExpireHandler;

    public InMobiNativeAdResponse() {
        runnable = new Runnable() {
            @Override
            public void run() {
                if (coverImage != null) {
                    coverImage.recycle();
                    ;
                    coverImage = null;
                }
                if (icon != null) {
                    icon.recycle();
                    icon = null;
                }
                nativeAdEventlistener = null;
                expired = true;
                if (imNative != null) {
                    imNative = null;
                }
                if(nativeElements != null && !nativeElements.isEmpty()){
                    nativeElements.clear();
                }
                registeredView = null;
                registeredClickables = null;
            }
        };
        inMobiNativeExpireHandler = new Handler(Looper.getMainLooper());
        inMobiNativeExpireHandler.postDelayed(runnable, Settings.NATIVE_AD_RESPONSE_EXPIRATION_TIME);

    }

    boolean setResources(final InMobiNative imNative) {
        this.imNative = imNative;
        try {
            nativeElements.put(NATIVE_ELEMENT_OBJECT, imNative);

            // Directly referenced from getters
            title = imNative.getAdTitle();
            description = imNative.getAdDescription();
            callToAction = imNative.getAdCtaText();
            landingUrl = imNative.getAdLandingPageUrl();


            // Extracted out of getCustomAdContent
            JSONObject response = imNative.getCustomAdContent();
            int length = JsonUtil.getJSONString(response, InMobiSettings.IMPRESSION_TRACKERS).length();
            impressionTrackers = JsonUtil.getJSONString(response, InMobiSettings.IMPRESSION_TRACKERS).substring(2,length-2).split("\",\"");
            JSONObject iconObject = JsonUtil.getJSONObject(response, InMobiSettings.KEY_ICON);
            iconUrl = JsonUtil.getJSONString(iconObject, InMobiSettings.KEY_URL);
            JSONObject imageObject = JsonUtil.getJSONObject(response, InMobiSettings.KEY_IMAGE);
            imageUrl = JsonUtil.getJSONString(imageObject, InMobiSettings.KEY_URL);
            if (JsonUtil.getJSONDouble(response, InMobiSettings.KEY_RATING) >= 0) {
                rating = new Rating(JsonUtil.getJSONDouble(response, InMobiSettings.KEY_RATING), 5);
            }


            clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imNative.reportAdClickAndOpenLandingPage(); // no additional params passed in for click tracking
                }
            };
            return true;
        } catch (Exception e) {
            // Catches JSONException for parsing,
            // ClassCastException for String casting,
            // NPE for null imNative
        }
        return false;
    }

    @Override
    public Network getNetworkIdentifier() {
        return Network.INMOBI;
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
    public String getCreativeId() {
        return this.creativeId;
    }

    @Override
    public void setCreativeId(String creativeId) {
        this.creativeId = creativeId;
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

    NativeAdEventListener getListener() {
        return nativeAdEventlistener;
    }

    @Override
    public boolean registerView(View view, NativeAdEventListener listener) {
        if (imNative != null && !registered && !expired) {
            view.setOnClickListener(clickListener);
            registeredView = view;
            registered = true;
            if (inMobiNativeExpireHandler != null) {
                inMobiNativeExpireHandler.removeCallbacks(runnable);
            }
        }
        this.nativeAdEventlistener = listener;
        return registered;
    }

    @Override
    public boolean registerViewList(View view, List<View> clickables, NativeAdEventListener listener) {
        if (imNative != null && !registered && !expired) {
            for (View clickable : clickables) {
                clickable.setOnClickListener(clickListener);
            }
            registeredView = view;
            registeredClickables = clickables;
            registered = true;
            if (inMobiNativeExpireHandler != null) {
                inMobiNativeExpireHandler.removeCallbacks(runnable);
            }
        }
        this.nativeAdEventlistener = listener;
        return registered;
    }

    @Override
    public void unregisterViews() {
        if (hasExpired()) {
            Clog.d(Clog.mediationLogTag, "This NativeAdResponse has expired.");
        }
        destroy();
    }

    @Override
    public void destroy() {
        if (inMobiNativeExpireHandler != null) {
            inMobiNativeExpireHandler.removeCallbacks(runnable);
            inMobiNativeExpireHandler.post(runnable);
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
}