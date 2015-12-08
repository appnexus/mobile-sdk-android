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

package com.appnexus.opensdk;

import android.content.Context;
import android.graphics.Bitmap;

import com.appnexus.opensdk.utils.AdvertistingIDUtil;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.ImageService;

/**
 * Define the attributes used for requesting a native ad.
 */
public class NativeAdRequest implements Ad {
    private NativeAdRequestListener listener;
    private final RequestParameters requestParameters;
    private final AdFetcher mAdFetcher;
    private final NativeAdDispatcher dispatcher;
    private boolean loadImage;
    private boolean loadIcon;

    public NativeAdRequest(Context context, String placementID) {
        AdvertistingIDUtil.retrieveAndSetAAID(context);
        requestParameters = new RequestParameters(context);
        requestParameters.setPlacementID(placementID);
        requestParameters.setMediaType(MediaType.NATIVE);
        mAdFetcher = new AdFetcher(this);
        mAdFetcher.setPeriod(-1);
        dispatcher = new NativeAdDispatcher();
    }

    public NativeAdRequest(Context context, String inventoryCode, int memberID) {
        AdvertistingIDUtil.retrieveAndSetAAID(context);
        requestParameters = new RequestParameters(context);
        requestParameters.setInventoryCodeAndMemberID(memberID, inventoryCode);
        requestParameters.setMediaType(MediaType.NATIVE);
        mAdFetcher = new AdFetcher(this);
        mAdFetcher.setPeriod(-1);
        dispatcher = new NativeAdDispatcher();
    }

    /**
     * Retrieve the setting that determines whether or not the
     * device's native browser is used instead of the in-app
     * browser when the user clicks an ad.
     *
     * @return true if the device's native browser will be used; false otherwise.
     */
    public boolean getOpensNativeBrowser() {
        Clog.d(Clog.nativeLogTag, Clog.getString(
                R.string.get_opens_native_browser, requestParameters.getOpensNativeBrowser()));
        return requestParameters.getOpensNativeBrowser();
    }

    /**
     * Set this to true to disable the in-app browser.  This will
     * cause URLs to open in a native browser such as Chrome so
     * that when the user clicks on an ad, your app will be paused
     * and the native browser will open.  Set this to false to
     * enable the in-app browser instead (a lightweight browser
     * that runs within your app).  The default value is false.
     *
     * @param opensNativeBrowser
     */
    public void setOpensNativeBrowser(boolean opensNativeBrowser) {
        Clog.d(Clog.nativeLogTag, Clog.getString(
                R.string.set_opens_native_browser, opensNativeBrowser));
        requestParameters.setOpensNativeBrowser(opensNativeBrowser);
    }

    /**
     * Sets the placement id of the NativeAdRequest. The placement ID
     * identifies the location in your application where ads will
     * be shown.  You must have a valid, active placement ID to
     * monetize your application.
     *
     * @param placementID The placement ID to use.
     */
    public void setPlacementID(String placementID) {
        Clog.d(Clog.nativeLogTag, Clog.getString(
                R.string.set_placement_id, placementID));
        requestParameters.setPlacementID(placementID);
    }

    /**
     * Retrieve the placement id for ad request.
     *
     * @return The Placement ID
     */
    public String getPlacementID() {
        Clog.d(Clog.nativeLogTag, Clog.getString(
                R.string.get_placement_id, requestParameters.getPlacementID()));
        return requestParameters.getPlacementID();
    }

    /**
     * Sets the inventory code and member id of this native ad request. The
     * inventory code provides a more human readable way to identify the location
     * in your application where ads will be shown. Member id is required to for
     * using this feature. If both inventory code and placement id are presented,
     * inventory code will be used instead of placement id on the ad request.
     *
     * @param memberID      The member id that this native ad belongs to.
     * @param inventoryCode The inventory code of this native ad.
     */
    public void setInventoryCodeAndMemberID(int memberID, String inventoryCode) {
        requestParameters.setInventoryCodeAndMemberID(memberID, inventoryCode);
    }

    /**
     * Retrieve the member ID.
     *
     * @return the member id that this AdView belongs to.
     */
    public int getMemberID() {
        return requestParameters.getMemberID();
    }

    /**
     * Retrieve the inventory code.
     *
     * @return the current inventory code.
     */
    public String getInventoryCode() {
        return requestParameters.getInvCode();
    }

    /**
     * Set user's gender for targeting
     *
     * @param gender User's gender
     */
    public void setGender(AdView.GENDER gender) {
        Clog.d(Clog.nativeLogTag, Clog.getString(
                R.string.set_gender, gender.toString()));
        requestParameters.setGender(gender);
    }

    /**
     * Get the user's gender
     *
     * @return User's gender
     */
    public AdView.GENDER getGender() {
        Clog.d(Clog.nativeLogTag, Clog.getString(
                R.string.get_gender, requestParameters.getGender().toString()));
        return requestParameters.getGender();
    }

    /**
     * Set the age or age range of the user
     *
     * @param age User's age or age range
     */
    public void setAge(String age) {
        requestParameters.setAge(age);
    }

    /**
     * Get the age or age range for the ad request
     *
     * @return age
     */
    public String getAge() {
        return requestParameters.getAge();
    }

    /**
     * Add a custom keyword to the request URL for the ad.  This
     * is used to set custom targeting parameters within the
     * AppNexus platform.  You will be given the keys and values
     * to use by your AppNexus account representative or your ad
     * network.
     *
     * @param key   The key to add; this cannot be null or empty.
     * @param value The value to add; this cannot be null or empty.
     */
    public void addCustomKeywords(String key, String value) {
        requestParameters.addCustomKeywords(key, value);
    }

    /**
     * Remove a custom keyword from the request URL for the ad.
     * Use this to remove a keyword previously set using
     * addCustomKeywords.
     *
     * @param key The key to remove; this cannot be null or empty.
     */
    public void removeCustomKeyword(String key) {
        requestParameters.removeCustomKeyword(key);
    }

    /**
     * Clear all custom keywords from the request URL.
     */
    public void clearCustomKeywords() {
        requestParameters.clearCustomKeywords();
    }

    /**
     * Call to override the image resource auto download feature. If True (the default value)
     * the SDK will automatically download the Image resource and the onAdLoaded will only be called
     * after the resource has been downloaded. If you return False the Image will not automatically
     * load but you can download the image manually be retrieving the URL of the image using
     * getImageUrl() in {@link NativeAdResponse}
     */
    public void shouldLoadImage(boolean flag) {
        loadImage = flag;
    }

    /**
     * Call to override the icon resource auto download feature. If True (the default value)
     * the SDK will automatically download the Icon image resource and the onAdLoaded will only be called
     * after the resource has been downloaded. If you return False the Image will not automatically
     * load but you can download the icon image manually be retrieving the URL of the image using
     * getIconUrl() in {@link NativeAdResponse}
     */
    public void shouldLoadIcon(boolean flag) {
        loadIcon = flag;
    }

    /**
     * Register a listener for ad success/fail to load notification events
     *
     * @param listener The RequestListener to register
     */
    public void setListener(NativeAdRequestListener listener) {
        this.listener = listener;
    }

    /**
     * Set the listener that listens the state of the request
     *
     * @return The registered request listener
     */
    public NativeAdRequestListener getListener() {
        return this.listener;
    }

    RequestParameters getRequestParameters() {
        return requestParameters;
    }

    @Override
    public MediaType getMediaType() {
        return requestParameters.getMediaType();
    }

    @Override
    public boolean isReadyToStart() {
        return this.listener != null && requestParameters.isReadyForRequest();
    }

    /**
     * Call this to request a native ad for parameters described by this object.
     */
    @Override
    public boolean loadAd() {
        if (listener == null) {
            // error message
            Clog.e(Clog.nativeLogTag, "No listener installed for this request, won't load a new ad");
            return false;
        }
        if (isLoading) {
            Clog.e(Clog.nativeLogTag, "Still loading last native ad , won't load a new ad");
            return false;
        }

        if (requestParameters.isReadyForRequest()) {
            mAdFetcher.stop();
            mAdFetcher.clearDurations();
            mAdFetcher.start();
            isLoading = true;
            return true;
        }
        return false;
    }

    boolean isLoading = false;

    /**
     * Internal class to post process NativeAd image downloading
     */
    class NativeAdDispatcher implements ImageService.ImageServiceListener, AdDispatcher {
        ImageService imageService;
        NativeAdResponse response;


        @Override
        public void onAllImageDownloadsFinish() {
            if (listener != null) {
                listener.onAdLoaded(response);
            } else {
                response.destroy();
            }
            imageService = null;
            response = null;
            isLoading = false;
        }

        void onAdLoaded(final NativeAdResponse response) {

        }

        @Override
        public void onAdLoaded(final AdResponse ad) {
            if (!ad.getMediaType().equals(MediaType.NATIVE)) {
                onAdFailed(ResultCode.INTERNAL_ERROR);
            } else {
                final NativeAdResponse response = ad.getNativeAdResponse();
                if (!loadImage && !loadIcon) {
                    if (listener != null) {
                        listener.onAdLoaded(response);
                    } else {
                        response.destroy();
                    }
                    isLoading = false;
                    return;
                }
                imageService = new ImageService();
                this.response = response;
                if (loadImage) {
                    ImageService.ImageReceiver imageReceiver = new ImageService.ImageReceiver() {
                        @Override
                        public void onReceiveImage(Bitmap image) {
                            response.setImage(image);
                        }

                        @Override
                        public void onFail() {
                            Clog.e(Clog.httpRespLogTag, "Image downloading failed for url " + response.getImageUrl());
                        }
                    };
                    imageService.registerImageReceiver(imageReceiver, response.getImageUrl());
                }
                if (loadIcon) {
                    ImageService.ImageReceiver iconReceiver = new ImageService.ImageReceiver() {
                        @Override
                        public void onReceiveImage(Bitmap image) {
                            response.setIcon(image);
                        }

                        @Override
                        public void onFail() {
                            Clog.e(Clog.httpRespLogTag, "Image downloading failed for url " + response.getIconUrl());
                        }
                    };
                    imageService.registerImageReceiver(iconReceiver, response.getIconUrl());
                }
                imageService.registerNotification(this);
                imageService.execute();
            }
        }

        @Override
        public void onAdFailed(ResultCode resultCode) {
            if (listener != null) {
                listener.onAdFailed(resultCode);
            }
            isLoading = false;
        }

        @Override
        public void onAdExpanded() {

        }

        @Override
        public void onAdCollapsed() {

        }

        @Override
        public void onAdClicked() {

        }

        @Override
        public void onAppEvent(String name, String data) {

        }
    }

    @Override
    public AdDispatcher getAdDispatcher() {
        return dispatcher;
    }
}
