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
import com.appnexus.opensdk.mediatedviews.AdColonySettings;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.ViewUtil;
import com.jirbo.adcolony.AdColonyNativeAdListener;
import com.jirbo.adcolony.AdColonyNativeAdView;

import java.util.HashMap;
import java.util.List;

public class AdColonyNativeAdResponse implements NativeAdResponse{

    private AdColonyNativeAdView nativeAdView;
    private HashMap<String, Object> nativeElements;
    private Runnable expireRunnable;
    private boolean expired;
    private NativeAdEventListener listener;

    AdColonyNativeAdResponse(final AdColonyNativeAdView nativeAdView) {
        this.nativeAdView = nativeAdView;
        nativeAdView.withListener(new AdColonyNativeAdListener() {
            @Override
            public void onAdColonyNativeAdStarted(boolean expanded, AdColonyNativeAdView adColonyNativeAdView) {
                // if expanded is true, it indicates that ad is clicked and video view expands to a full screen
                if (expanded) {
                    if (listener != null) {
                        listener.onAdWasClicked();
                    }
                }
            }

            @Override
            public void onAdColonyNativeAdFinished(boolean expanded, AdColonyNativeAdView adColonyNativeAdView) {
                // if expanded is true, ad finishes from the full screen, else ad finishes without expanding
            }
        });
        setNativeElements();
        expireRunnable = new Runnable() {
            @Override
            public void run() {
                expired = true;

            }
        };
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(expireRunnable, Settings.NATIVE_AD_RESPONSE_EXPIRATION_TIME);
    }

    @Override
    public Network getNetworkIdentifier() {
        return Network.ADCOLONY;
    }

    @Override
    public String getTitle() {
        return nativeAdView.getTitle();
    }

    @Override
    public String getDescription() {
        return nativeAdView.getDescription();
    }

    @Override
    public String getImageUrl() {
        return null;
    }

    @Override
    public Bitmap getImage() {
        return null;
    }

    @Override
    public void setImage(Bitmap bitmap) {

    }

    @Override
    public String getIconUrl() {
        return null;
    }

    @Override
    public Bitmap getIcon() {
        return null;
    }

    @Override
    public void setIcon(Bitmap bitmap) {

    }

    @Override
    public String getCallToAction() {
        return null;
    }

    private void setNativeElements() {
        nativeElements = new HashMap<String, Object>();
        nativeElements.put(AdColonySettings.KEY_NATIVE_AD_VIEW, nativeAdView);
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
        return null;
    }

    @Override
    public boolean hasExpired() {
        return expired;
    }

    @Override
    public boolean registerView(View view, NativeAdEventListener listener) {
        this.listener = listener;
        // AdColony doesn't have a registration api, always return true
        return true;
    }

    @Override
    public boolean registerViewList(View view, List<View> clickables, NativeAdEventListener listener) {
        this.listener = listener;
        // AdColony doesn't have a registration api, always return true
        return true;
    }

    @Override
    public void unregisterViews() {
        destroy();
    }

    @Override
    public void destroy() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.removeCallbacks(expireRunnable);
        expired = true;
        if (nativeAdView != null) {
            ViewUtil.removeChildFromParent(nativeAdView.getAdvertiserImage());
            ViewUtil.removeChildFromParent(nativeAdView);
            nativeAdView.withListener(null);
            nativeAdView.destroy();
            nativeAdView = null;
        }

    }
}
