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

    protected MediatedAdViewController() {

    }

    protected MediatedAdViewController(AdView owner, AdResponse response) throws Exception {
        //TODO: owner - second part is for testing when owner is null
        requester = owner != null ? owner.mAdFetcher : response.requester;
        mediatedAds = response.getMediatedAds();

        if ((mediatedAds != null) && !mediatedAds.isEmpty()) {
            currentAd = mediatedAds.pop();
            instantiateNewMediatedAd();
        }
        else {
            Clog.e(Clog.mediationLogTag, "No ads were available");
            failed = true;
        }
    }

    private void instantiateNewMediatedAd() throws Exception {
        Clog.d(Clog.mediationLogTag, Clog.getString(
                R.string.instantiating_class, currentAd.getClassName()));

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
            fail(RESULT.MEDIATED_SDK_UNAVAILABLE);
        } catch (IllegalAccessException e) {
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.illegal_access_exception), e);
            fail(RESULT.MEDIATED_SDK_UNAVAILABLE);
        } catch (ClassCastException e) {
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.class_cast_exception), e);
            fail(RESULT.MEDIATED_SDK_UNAVAILABLE);
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

        if ((mediatedAds != null) && !mediatedAds.isEmpty()) {
            currentAd = mediatedAds.pop();
            try {
                instantiateNewMediatedAd();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            Clog.e(Clog.mediationLogTag, "No more ads available");
            failed = true;
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

    protected void fail(RESULT result) {
        failed = true;
        onAdFailed(result);
    }

    private void fireResultCB(final RESULT result) {

        // if resultCB is empty don't do anything
        final String resultCB = currentAd.getResultCB();
        if ((resultCB == null) || resultCB.isEmpty()) {
            return;
        }

        //fire call to result cb url
        HTTPGet<Void, Void, HTTPResponse> cb = new HTTPGet<Void, Void, HTTPResponse>() {
            @Override
            protected void onPostExecute(HTTPResponse response) {
                if (requester == null) {
                    Clog.e(Clog.httpRespLogTag, Clog.getString(R.string.fire_cb_requester_null));
                    return;
                } else if (response == null) {
                    Clog.e(Clog.httpRespLogTag, Clog.getString(R.string.fire_cb_response_null));
                    return;
                }

                requester.dispatchResponse(new AdResponse(requester, response.getResponseBody(), response.getHeaders()));
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
