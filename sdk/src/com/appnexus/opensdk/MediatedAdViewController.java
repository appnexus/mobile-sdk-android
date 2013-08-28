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

import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.HTTPGet;
import com.appnexus.opensdk.utils.HTTPResponse;

public class MediatedAdViewController {

    public static enum RESULT {
        SUCCESS,
        INVALID_REQUEST,
        UNABLE_TO_FILL,
        MEDIATED_SDK_UNAVAILABLE,
        NETWORK_ERROR,
        INTERNAL_ERROR
    }

    int width;
    int height;
    boolean failed = false;
    String uid;
    String className;
    String param;
    String resultCB;
    Class<?> c;
    AdView owner;
    MediatedAdView mAV;

    protected boolean errorCBMade = false;
    protected boolean successCBMade = false;

    protected MediatedAdViewController() {

    }

    protected MediatedAdViewController(AdView owner, AdResponse response) throws Exception {
        width = response.getWidth();
        height = response.getHeight();
        uid = response.getMediatedUID();
        className = response.getMediatedViewClassName();
        param = response.getParameter();
        resultCB = response.getMediatedResultCB();
        this.owner = owner;

        Clog.d(Clog.mediationLogTag, Clog.getString(R.string.instantiating_class, className));

        try {
            c = Class.forName(className);

        } catch (ClassNotFoundException e) {
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.class_not_found_exception));
            fireResultCB(RESULT.MEDIATED_SDK_UNAVAILABLE);
            throw e;
        }

        try {
            mAV = (MediatedAdView) c.newInstance();
        } catch (InstantiationException e) {
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.instantiation_exception));
            failed = true;
            fireResultCB(RESULT.MEDIATED_SDK_UNAVAILABLE);
            throw e;
        } catch (IllegalAccessException e) {
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.illegal_access_exception));
            failed = true;
            fireResultCB(RESULT.MEDIATED_SDK_UNAVAILABLE);
            throw e;
        }
    }

    public void onAdLoaded() {
        if (owner.getAdListener() != null) {
            owner.getAdListener().onAdLoaded(owner);
        }
        if (!successCBMade) {
            successCBMade = true;
            fireResultCB(RESULT.SUCCESS);
        }
    }

    public void onAdFailed(MediatedAdViewController.RESULT reason) {
        if (owner.getAdListener() != null) {
            owner.getAdListener().onAdRequestFailed(owner);
        }
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

    private void fireResultCB(final MediatedAdViewController.RESULT result) {

        //fire call to result cb url
        HTTPGet<Void, Void, HTTPResponse> cb = new HTTPGet<Void, Void, HTTPResponse>() {
            @Override
            protected void onPostExecute(HTTPResponse response) {
                AdFetcher f = MediatedAdViewController.this.owner.mAdFetcher;
                f.dispatchResponse(new AdResponse(f, response.getResponseBody(), response.getHeaders()));
            }

            @Override
            protected String getUrl() {
                return resultCB + "&reason=" + result;
            }
        };

        cb.execute();
    }
}
