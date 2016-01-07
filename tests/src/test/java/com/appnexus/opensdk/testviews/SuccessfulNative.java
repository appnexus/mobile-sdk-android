package com.appnexus.opensdk.testviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import com.appnexus.opensdk.MediatedNativeAd;
import com.appnexus.opensdk.MediatedNativeAdController;
import com.appnexus.opensdk.NativeAdEventListener;
import com.appnexus.opensdk.NativeAdResponse;
import com.appnexus.opensdk.TargetingParameters;

import java.util.HashMap;
import java.util.List;

public class SuccessfulNative implements MediatedNativeAd {
    public static String TITLE = "test title";
    public static String DESCRIPTION = "test description";
    public static String ImageUrl = "test image url";

    @Override
    public void requestNativeAd(Context context, String uid, MediatedNativeAdController mBC, TargetingParameters tp) {
        if (mBC != null) {

            mBC.onAdLoaded(new NativeAdResponse() {
                @Override
                public Network getNetworkIdentifier() {
                    return Network.APPNEXUS;
                }

                @Override
                public String getTitle() {
                    return TITLE;
                }

                @Override
                public String getDescription() {
                    return DESCRIPTION;
                }

                @Override
                public String getImageUrl() {
                    return ImageUrl;
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

                @Override
                public HashMap<String, Object> getNativeElements() {
                    return null;
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
                    return false;
                }

                @Override
                public boolean registerView(View view, NativeAdEventListener listener) {
                    return false;
                }

                @Override
                public boolean registerViewList(View view, List<View> clickables, NativeAdEventListener listener) {
                    return false;
                }

                @Override
                public void unregisterViews() {

                }

                @Override
                public void destroy() {

                }
            });
        }
    }
}
