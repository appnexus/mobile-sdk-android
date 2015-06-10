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

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;

import com.appnexus.opensdk.NativeAdEventListener;
import com.appnexus.opensdk.NativeAdResponse;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.JsonUtil;
import com.appnexus.opensdk.utils.Settings;
import com.inmobi.monetization.IMNative;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class InMobiNativeAdResponse implements NativeAdResponse{
    private IMNative imNative;
    private String title;
    private String description;
    private String imageUrl;
    private String iconUrl;
    private String callToAction;
    private Bitmap icon;
    private Bitmap coverImage;
    private String socialContext;
    private Rating rating;
    private String landingUrl;
    private HashMap<String, Object> nativeElements = new HashMap<String, Object>();
    private boolean expired = false;
    private boolean registered = false;
    private NativeAdEventListener nativeAdEventlistener;
    private Runnable runnable;
    private View registeredView;
    private List<View> registeredClickables;
    private View.OnClickListener clickListener;

    public InMobiNativeAdResponse() {
        runnable = new Runnable() {
            @Override
            public void run() {
                if (coverImage != null) {
                    coverImage.recycle();;
                    coverImage = null;
                }
                if (icon != null) {
                    icon.recycle();
                    icon = null;
                }
                nativeAdEventlistener = null;
                expired = true;
                if (imNative != null) {
                    imNative.detachFromView();
                    imNative = null;
                }
                registeredView = null;
                registeredClickables = null;
            }
        };
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(runnable, Settings.NATIVE_AD_RESPONSE_EXPIRATION_TIME);

    }

    boolean setResources(final IMNative imNative) {
        this.imNative = imNative;
        if (imNative.getContent() == null || imNative.getContent().trim().isEmpty()) {
            return false;
        }
        JSONObject response;
        try {
            // parse Json response and create an native response
            response = new JSONObject(imNative.getContent());
        } catch (JSONException e){
            return false;
        }
        title = JsonUtil.getJSONString(response, InMobiSettings.KEY_TITLE);
        callToAction = JsonUtil.getJSONString(response, InMobiSettings.KEY_CALL_TO_ACTION);
        description = JsonUtil.getJSONString(response, InMobiSettings.KEY_DESCRIPTION);
        JSONObject iconObject = JsonUtil.getJSONObject(response, InMobiSettings.KEY_ICON);
        iconUrl = JsonUtil.getJSONString(iconObject, InMobiSettings.KEY_URL);
        JSONObject imageObject = JsonUtil.getJSONObject(response, InMobiSettings.KEY_IMAGE);
        imageUrl = JsonUtil.getJSONString(imageObject, InMobiSettings.KEY_URL);
        if (JsonUtil.getJSONDouble(response, InMobiSettings.KEY_RATING) >= 0) {
            rating = new Rating(JsonUtil.getJSONDouble(response, InMobiSettings.KEY_RATING), 5);
        }
        landingUrl = JsonUtil.getJSONString(response, InMobiSettings.KEY_LANDING_URL);
        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imNative.handleClick(null); // no additional params passed in for click tracking
                onAdClicked();
                if (v != null && landingUrl != null && !landingUrl.isEmpty()) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(landingUrl));
                    browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    onAdWillLeaveApplication();
                    v.getContext().startActivity(browserIntent);
                }
            }
        };
        return true;
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

    @Override
    public boolean registerView(View view, NativeAdEventListener listener) {
        if (imNative != null && !registered) {
            imNative.attachToView((ViewGroup) view);
            view.setOnClickListener(clickListener);
            registeredView = view;
            registered = true;
        }
        this.nativeAdEventlistener = listener;
        return registered;
    }

    @Override
    public boolean registerViewList(View view, List<View> clickables, NativeAdEventListener listener) {
        if (imNative != null && !registered) {
            imNative.attachToView((ViewGroup) view);
            for (View clickable: clickables) {
                clickable.setOnClickListener(clickListener);
            }
            registeredView = view;
            registeredClickables = clickables;
            registered = true;
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
        Handler handler = new Handler(Looper.getMainLooper());
        handler.removeCallbacks(runnable);
        handler.post(runnable);
    }

    void onAdClicked() {
        if (nativeAdEventlistener != null) {
            nativeAdEventlistener.onAdWasClicked();
        }
    }

    void onAdWillLeaveApplication() {
        if (nativeAdEventlistener != null) {
            nativeAdEventlistener.onAdWillLeaveApplication();
        }
    }

}
