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

/**
 * This is the interface a mediation adaptor must implement for
 * requesting native ads.  The mediation interface allows third-party
 * SDKs to be called by the AppNexus SDK.  To integrate a third-party
 * SDK, create a class that implements
 * <code>MediatedNativeAd</code>.  Implement the required method
 * and configure it within the AppNexus Ad Network Manager to be
 * called whenever the targeting matches the conditions defined in the
 * Ad Network Manager. (The Ad Network Manager is a web application
 * that AppNexus platform members can use to work with ad networks
 * that are not on the platform.)
 */
public interface MediatedNativeAd {

    /**
     * The AppNexus SDK will call this method to ask the
     * third-party SDK to request an ad from its network.  The
     * AppNexus SDK expects to be notified of events through the
     * {@link MediatedNativeAdController}.  Note that once a
     * requestAd call has been made, the AppNexus SDK expects
     * onAdLoaded or onAdFailed to be called through the {@link
     * MediatedNativeAdController} within 15 seconds or the
     * mediation call is considered to have failed.
     *
     * @param mBC     The controller to notify on load, failure, etc.
     * @param context The activity from which this method was
     *                called.
     * @param tp      Targeting parameters passed from SDK to adapter.wqw
     * @param uid     The network ID for this ad call.  This ID is
     *                opaque to the AppNexus SDK and its contents and
     *                their encoding are up to the implementation of
     *                the third-party SDK.
     */

    public void requestNativeAd(Context context, String uid, MediatedNativeAdController mBC, TargetingParameters tp);
}
