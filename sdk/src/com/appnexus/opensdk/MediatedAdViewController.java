/*
 *    Copyright 2013 APPNEXUS INC
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

import android.os.AsyncTask;
import android.os.Build;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.HTTPGet;
import com.appnexus.opensdk.utils.HTTPResponse;

import java.util.LinkedList;

public abstract class MediatedAdViewController implements Displayable {

    public static enum RESULT {
        SUCCESS,
        INVALID_REQUEST,
        UNABLE_TO_FILL,
        MEDIATED_SDK_UNAVAILABLE,
        NETWORK_ERROR,
        INTERNAL_ERROR
    }

    boolean failed = false;
    Class<?> c;
    AdView owner;
    MediatedAdView mAV;
    AdRequester requester;
    LinkedList<MediatedAd> mediatedAds;
    MediatedAd currentAd;

    protected boolean errorCBMade = false;
    protected boolean successCBMade = false;

    protected boolean noMoreAds = false;

    protected MediatedAdViewController() {

    }

    protected MediatedAdViewController(AdView owner, AdResponse response) {
        //TODO: owner - second part is for testing when owner is null
        requester = owner != null ? owner.mAdFetcher : response.requester;
        mediatedAds = response.getMediatedAds();
        checkNext();
    }

    private void instantiateNewMediatedAd() {
        Clog.d(Clog.mediationLogTag, Clog.getString(
                R.string.instantiating_class, currentAd.getClassName()));
        errorCBMade = false;
        successCBMade = false;

        try {
            c = Class.forName(currentAd.getClassName());

        } catch (ClassNotFoundException e) {
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.class_not_found_exception));
            onAdFailed(RESULT.MEDIATED_SDK_UNAVAILABLE);
            return;
        }

        try {
            Object o = c.newInstance();
            mAV = (MediatedAdView) o;
            failed = false;
        } catch (InstantiationException e) {
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.instantiation_exception), e);
            onAdFailed(RESULT.MEDIATED_SDK_UNAVAILABLE);
        } catch (IllegalAccessException e) {
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.illegal_access_exception), e);
            onAdFailed(RESULT.MEDIATED_SDK_UNAVAILABLE);
        } catch (ClassCastException e) {
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.class_cast_exception), e);
            onAdFailed(RESULT.MEDIATED_SDK_UNAVAILABLE);
        }
    }

    // check for next mediated ad
    private void checkNext() {
        Clog.d(Clog.mediationLogTag, "checking for next ad");
        if ((mediatedAds != null) && !mediatedAds.isEmpty()) {
            currentAd = mediatedAds.pop();
            instantiateNewMediatedAd();
        }
        else {
            Clog.e(Clog.mediationLogTag, "No ads were available");
            noMoreAds = true;
            onAdFailed(RESULT.UNABLE_TO_FILL);
        }
    }

    //TODO: owner dependency
    public void onAdLoaded() {
        if ((owner != null) && owner.getAdListener() != null) {
            owner.getAdListener().onAdLoaded(owner);
        }
        if (!successCBMade) {
            successCBMade = true;
            fireResultCB(RESULT.SUCCESS);
        }
    }

    public void onAdFailed(MediatedAdViewController.RESULT reason) {
        // callback will be called by AdView
        this.failed = true;

        if (!errorCBMade) {
            fireResultCB(reason);
            errorCBMade = true;
        }
    }

    public void onAdExpanded() {
        if (owner.getAdListener() != null) {
            owner.getAdListener().onAdExpanded(owner);
        }
    }

    public void onAdCollapsed() {
        if (owner.getAdListener() != null) {
            owner.getAdListener().onAdCollapsed(owner);
        }
    }

    public void onAdClicked() {
        if (owner.getAdListener() != null) {
            owner.getAdListener().onAdClicked(owner);
        }
    }

    public boolean failed() {
        return failed;
    }

    private void fireResultCB(final RESULT result) {

        // if resultCB is empty don't do anything
        final String resultCB = currentAd.getResultCB();
        if ((resultCB == null) || resultCB.isEmpty()) {
            Clog.w(Clog.mediationLogTag, "resultCB was null or empty");
            return;
        }

        //fire call to result cb url
        HTTPGet<Void, Void, HTTPResponse> cb = new HTTPGet<Void, Void, HTTPResponse>() {
            @Override
            protected void onPostExecute(HTTPResponse httpResponse) {
                if (requester == null) {
                    Clog.e(Clog.httpRespLogTag, Clog.getString(R.string.fire_cb_requester_null));
                    return;
                } else if (httpResponse == null) {
                    Clog.e(Clog.httpRespLogTag, Clog.getString(R.string.fire_cb_response_null));
                    return;
                }
                Clog.d(Clog.httpRespLogTag, "fired result cb: " + getUrl());

                AdResponse response = new AdResponse(requester, httpResponse.getResponseBody(), httpResponse.getHeaders());
                if (response.containsAds()) {
                    Clog.d(Clog.mediationLogTag, "Received a response from server with new ads");
                    mediatedAds.clear();
                }

                requester.dispatchResponse(response);

                if (!noMoreAds)
                    checkNext();
            }

            @Override
            protected String getUrl() {
                return resultCB + "&reason=" + result.ordinal();
            }
        };

        // Spawn GET call
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            cb.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            cb.execute();
        }
    }
}
