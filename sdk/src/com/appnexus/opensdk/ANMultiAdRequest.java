/*
 *    Copyright 2019 APPNEXUS INC
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
import android.util.Pair;

import com.appnexus.opensdk.mar.MultiAdRequestListener;
import com.appnexus.opensdk.ut.UTRequestParameters;
import com.appnexus.opensdk.utils.Clog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ANMultiAdRequest {


    private Context context;
    private AdFetcher mAdFetcher;
    private boolean isMARRequestInProgress = false;
    private MultiAdRequestListener multiAdRequestListener;
    private UTRequestParameters utRequestParameters;

    private ANMultiAdRequest() {
    }

    /**
     * Initialise and load the Multi Ad Request with Ad List
     *
     * @param context
     * @param memberId
     * @param publisherId
     * @param ads
     * @param multiAdRequestListener
     * @param loadOnInit
     */
    public ANMultiAdRequest(Context context, int memberId, int publisherId, MultiAdRequestListener multiAdRequestListener, boolean loadOnInit, Ad... ads) {
        mAdFetcher = new AdFetcher(this);
        this.context = context;
        this.multiAdRequestListener = multiAdRequestListener;
        utRequestParameters = new UTRequestParameters(context);
        utRequestParameters.setMemberID(memberId);
        utRequestParameters.setPublisherId(publisherId);
        if (ads != null && ads.length > 0) {
            for (Ad ad : ads) {
                addAdUnit(ad);
            }
        }
        if (loadOnInit) {
            load();
        }
    }

    /**
     * Initialise and load the Multi Ad Request with Ad List
     *
     * @param context
     * @param memberId
     * @param ads
     * @param multiAdRequestListener
     * @param loadOnInit
     */
    public ANMultiAdRequest(Context context, int memberId, MultiAdRequestListener multiAdRequestListener, boolean loadOnInit, Ad... ads) {
        this(context, memberId, 0, multiAdRequestListener, loadOnInit, ads);
    }

    /**
     * Initialise the Multi Ad Request
     *
     * @param context
     * @param memberId
     * @param multiAdRequestListener
     */
    public ANMultiAdRequest(Context context, int memberId, MultiAdRequestListener multiAdRequestListener) {
        this(context, memberId, 0, multiAdRequestListener, false);
    }

    /**
     * Initialise the Multi Ad Request
     *
     * @param context
     * @param memberId
     * @param publisherId
     * @param multiAdRequestListener
     */
    public ANMultiAdRequest(Context context, int memberId, int publisherId, MultiAdRequestListener multiAdRequestListener) {
        this(context, memberId, publisherId, multiAdRequestListener, false);
    }


    // For End User
    public boolean addAdUnit(Ad adUnit) {

        //Check if the AdUnit is not null
        if (adUnit == null) {
            Clog.e(Clog.SRMLogTag, "addAdUnit Failed: AdUnit cannot be null");
            return false;
        }

        //Check if the Request is not in progress
        if (isMARRequestInProgress) {
            Clog.e(Clog.SRMLogTag, "addAdUnit Failed: MultiAdRequest already in progress");
            return false;
        }

        //Reads properties of AdUnit: Member ID
        //Reject AdUnit if Member ID is set, but does not match MAR Member ID  or  if it does not match the Member ID of any other AdUnit.
        if (utRequestParameters.getMemberID() != 0 && adUnit.getRequestParameters().getMemberID() != 0 && adUnit.getRequestParameters().getMemberID() != utRequestParameters.getMemberID()) {
            Clog.e(Clog.SRMLogTag, "addAdUnit Failed: Member ID mismatch");
            return false;
        }

        //Reads properties of AdUnit: Publisher ID
        //Reject AdUnit if Publisher ID is set, but does not match MAR Publisher ID  or  if it does not match the Publisher ID of any other AdUnit.
        if (utRequestParameters.getPublisherId() != 0 && adUnit.getRequestParameters().getPublisherId() != 0 && adUnit.getRequestParameters().getPublisherId() != utRequestParameters.getPublisherId()) {
            Clog.e(Clog.SRMLogTag, "addAdUnit Failed: Publisher ID mismatch");
            return false;
        }

        //Reject AdUnit if delegate is already set
        if (adUnit.getMultiAdRequest() == null) {
            if (adUnit instanceof BannerAdView) {
                //Set Auto Refresh Timer to zero (to disable auto refresh)
                ((BannerAdView) adUnit).setAutoRefreshInterval(0);
            }
            adUnit.getMultiAd().associateWithMultiAdRequest(this);
            //Add AdUnit to the internal list
            utRequestParameters.addAdUnit(new WeakReference<>(adUnit));
            return true;
        } else {
            Clog.e(Clog.SRMLogTag, "addAdUnit Failed: This Ad is already linked to another MultiAdRequest");
            return false;
        }
    }

    // For End User
    public void removeAdUnit(Ad adUnit) {

        //Check if the AdUnit is not null
        if (adUnit == null) {
            Clog.e(Clog.SRMLogTag, "removeAdUnit Failed: AdUnit cannot be null");
            return;
        }

        if (isMARRequestInProgress) {
            Clog.e(Clog.SRMLogTag, "removeAdUnit Failed: MultiAdRequest already in progress");
            return;
        }
        adUnit.getMultiAd().disassociateFromMultiAdRequest();
        utRequestParameters.removeAdUnit(adUnit);
    }

    // For End User
    public boolean load() {

        // Restricting the usage of MultiAdRequest if the AdUnit List is empty.
        if (getAdUnitList().size() == 0) {
            Clog.e(Clog.SRMLogTag, "MultiAdRequest can be made only after adding the AdUnits to it");
            return false;
        }

        if (mAdFetcher != null) {

            // Reload Ad Fetcher to get new ad at user's request
            mAdFetcher.stop();
            mAdFetcher.clearDurations();

            initAds();
            //Setting the isMARRequestInProgress to true
            isMARRequestInProgress = true;
            mAdFetcher.start();
            return true;
        }
        return false;
    }

    /**
     * This API is used to stop the Multi Ad Request, before the request is completed.
     * */
    public void stop() {
        if (isMARRequestInProgress) {
            if (mAdFetcher != null) {
                // Stop Ad Fetcher
                mAdFetcher.stop();
                mAdFetcher.clearDurations();
            }
            onRequestFailed(ResultCode.getNewInstance(ResultCode.REQUEST_INTERRUPTED_BY_USER));
        } else {
            Clog.e(Clog.SRMLogTag, "Unable to stop Multi Ad Request. It is already processed.");
        }
    }

    private void initAds() {
        ArrayList<WeakReference<Ad>> adUnitList = new ArrayList();
        adUnitList.addAll(getAdUnitList());
        for (WeakReference<Ad> ads: adUnitList) {
            Ad ad = ads.get();
            if (ad != null && ad.getMultiAd() != null) {
                ad.getMultiAd().init();
            }
        }
    }

    // Internal API
    public ArrayList<WeakReference<Ad>> getAdUnitList() {
        return utRequestParameters.getAdUnitList();
    }

    /**
     * @return Member Id that is set to this instance of ANMultiAdRequest
     * */
    public int getMemberId() {
        return utRequestParameters.getMemberID();
    }

    /**
     * @return Publisher Id that is set to this instance of ANMultiAdRequest
     * */
    public int getPublisherId() {
        return utRequestParameters.getPublisherId();
    }

    // For End User
    public void setAge(String age) {
        utRequestParameters.setAge(age);
    }

    // For End User
    public String getAge() {
        return utRequestParameters.getAge();
    }

    // For End User
    public void setGender(AdView.GENDER gender) {
        utRequestParameters.setGender(gender);
    }

    // For End User
    public AdView.GENDER getGender() {
        return utRequestParameters.getGender();
    }

    // For End User

    @Deprecated
    /**
     * Retrieve the externalUID that was previously set.
     *
     * @return externalUID.
     */
    public String getExternalUid() {
        return utRequestParameters.getExternalUid();
    }

    // For End User

    @Deprecated
    /**
     * Set the current user's externalUID
     *
     * @param externalUid .
     * @deprecated  Use ({@link SDKSettings}.setPublisherUserId)
     */
    public void setExternalUid(String externalUid) {
        utRequestParameters.setExternalUid(externalUid);
    }

    // For End User

    /**
     * Add the custom keyword
     *
     * @param key
     * @param value
     */
    public void addCustomKeywords(String key, String value) {
        utRequestParameters.addCustomKeywords(key, value);
    }

    // For End User

    /**
     * Remove the custom keyword based on the key
     *
     * @param key
     */
    public void removeCustomKeyword(String key) {
        utRequestParameters.removeCustomKeyword(key);
    }

    // For End User

    /**
     * Clear all the custom keywords set to this MultiAdRequest
     */
    public void clearCustomKeywords() {
        utRequestParameters.clearCustomKeywords();
    }

    public ArrayList<Pair<String, String>> getCustomKeywords() {
        return utRequestParameters.getCustomKeywords();
    }

    public Context getContext() {
        return context;
    }

    public boolean isMARRequestInProgress() {
        return isMARRequestInProgress;
    }

    public UTRequestParameters getRequestParameters() {
        return utRequestParameters;
    }

    public void onMARLoadCompleted() {
        isMARRequestInProgress = false;
        if (ANMultiAdRequest.this.multiAdRequestListener != null) {
            ANMultiAdRequest.this.multiAdRequestListener.onMultiAdRequestCompleted();
        }
    }

    public void onRequestFailed(ResultCode code) {
        isMARRequestInProgress = false;
        if (multiAdRequestListener != null) {
            multiAdRequestListener.onMultiAdRequestFailed(code);
        }
    }

}