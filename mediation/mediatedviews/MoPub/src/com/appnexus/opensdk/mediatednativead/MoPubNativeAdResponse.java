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
import android.support.annotation.NonNull;
import android.view.View;

import com.appnexus.opensdk.NativeAdEventListener;
import com.appnexus.opensdk.NativeAdResponse;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.mopub.nativeads.NativeResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoPubNativeAdResponse implements NativeAdResponse{
    private String title;
    private String description;
    private String imageUrl;
    private String iconUrl;
    private String callToAction;
    private Bitmap icon;
    private Bitmap coverImage;
    private String socialContext;
    private Rating rating;
    private HashMap<String, String> nativeElements = new HashMap<String, String>();
    private boolean expired = false;
    private boolean registered = false;
    private NativeResponse nativeResponse;
    private NativeAdEventListener listener;
    private Runnable runnable;
    private View registeredView;
    private List<View> registeredClickables;


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
                if (nativeResponse != null) {
                    nativeResponse.destroy();
                    nativeResponse = null;
                }
                registeredView = null;
                registeredClickables = null;
            }
        };
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(runnable, Settings.NATIVE_AD_RESPONSE_EXPIRATION_TIME);
    }

    void setResources(@NonNull NativeResponse response) {
        this.nativeResponse = response;
        this.title = nativeResponse.getTitle();
        this.description = nativeResponse.getText();
        this.imageUrl = nativeResponse.getMainImageUrl();
        this.iconUrl = nativeResponse.getIconImageUrl();
        this.callToAction = nativeResponse.getCallToAction();
        try {
            this.rating = new Rating(nativeResponse.getStarRating(), 5.0);
        } catch (NullPointerException e) {
            this.rating = null;
        }
        if (nativeResponse.getExtras() != null && !nativeResponse.getExtras().isEmpty()) {
            // put extras in native response, MoPub returns String as Object
            for (Map.Entry<String, Object> entry : nativeResponse.getExtras().entrySet()) {
                try {
                    String value = (String) entry.getValue();
                    nativeElements.put(entry.getKey(), value);
                } catch (ClassCastException ignore) {
                }
            }
        }
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
    public HashMap<String, String> getNativeElements() {
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
        if (nativeResponse != null && ! registered) {
            nativeResponse.prepare(view);
            registeredView = view;
            registered = true;
        }
        this.listener = listener;
        return registered;
    }


    @Override
    public boolean registerViewList(View view, List<View> clickables, NativeAdEventListener listener) {
        if (nativeResponse != null && !registered) {
            registeredClickables = clickables;
            for (View clickable: clickables) {
                nativeResponse.prepare(clickable);
            }
        }
        this.listener = listener;
        return registered;
    }

    @Override
    public void unregisterViews() {
        if (hasExpired()) {
            Clog.d(Clog.mediationLogTag, "This NativeAdResponse has expired.");
        }
        if (nativeResponse != null) {
            if (registeredView != null) {
                nativeResponse.clear(registeredView);
            }
            if (registeredClickables != null && !registeredClickables.isEmpty()) {
                for (View view : registeredClickables) {
                    nativeResponse.clear(view);
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
        Handler handler = new Handler(Looper.getMainLooper());
        handler.removeCallbacks(runnable);
        handler.post(runnable);
    }
}
