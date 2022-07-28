package com.appnexus.opensdk.mocks;

import android.graphics.Bitmap;
import android.view.View;

import com.appnexus.opensdk.ANAdResponseInfo;
import com.appnexus.opensdk.BaseNativeAdResponse;
import com.appnexus.opensdk.CSRController;
import com.appnexus.opensdk.NativeAdEventListener;

import java.util.HashMap;
import java.util.List;

public class MockFBNativeBannerAdResponse extends BaseNativeAdResponse {
    private CSRController callback;
    private ANAdResponseInfo adResponseInfo;

    public MockFBNativeBannerAdResponse(CSRController callback) {
        this.callback = callback;
    }

    public void logImpression() {
        if (this.callback != null) {
            this.callback.onAdImpression(null);
        }
    }

    public void clickAd() {
        if (this.callback != null) {
            this.callback.onAdClicked();
        }

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

    }

    @Override
    protected boolean registerNativeAdEventListener(NativeAdEventListener listener) {
        return false;
    }

    @Override
    public Network getNetworkIdentifier() {
        return Network.FACEBOOK;
    }

    @Override
    public String getTitle() {
        return "Hello World";
    }

    @Override
    public String getDescription() {
        return "Welcome to native ads world.";
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
    public ANAdResponseInfo getAdResponseInfo() {
        return adResponseInfo;
    }

    @Override
    public void setAdResponseInfo(ANAdResponseInfo adResponseInfo) {
        this.adResponseInfo = adResponseInfo;
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
        return "More";
    }

    @Override
    public HashMap<String, Object> getNativeElements() {
        return new HashMap<>();
    }

    @Override
    public Rating getAdStarRating() {
        return new Rating(1, 5);
    }

    @Override
    public String getSponsoredBy() {
        return "AppNexus";
    }

    @Override
    public boolean hasExpired() {
        return false;
    }

    @Override
    public void destroy() {

    }

    @Override
    public ImageSize getImageSize() {
        return new ImageSize(300, 250);
    }

    @Override
    public String getAdditionalDescription() {
        return "Welcome again!";
    }

    @Override
    public ImageSize getIconSize() {
        return new ImageSize(40, 40);
    }

    @Override
    public String getVastXml() {
        return "";
    }

    @Override
    public String getPrivacyLink() {
        return "";
    }
}
