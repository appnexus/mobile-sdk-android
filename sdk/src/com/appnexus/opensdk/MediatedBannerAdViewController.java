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

import android.app.Activity;
import android.view.View;
import com.appnexus.opensdk.utils.HTTPGet;
import com.appnexus.opensdk.utils.HTTPResponse;

public class MediatedBannerAdViewController implements Displayable {

    public static enum RESULT{
        SUCCESS,
        INVALID_REQUEST,
        UNABLE_TO_FILL,
        MEDIATED_SDK_UNAVAILABLE,
        NETWORK_ERROR,
        INTERNAL_ERROR
    }

    AdView owner;
    int width;
    int height;
    String uid;
    String className;
    String param;
    boolean failed = false;
    String resultCB;

    Class<?> c;
    MediatedBannerAdView mAV;

    View placeableView;

    static public MediatedBannerAdViewController create(AdView owner, AdResponse response) {
        MediatedBannerAdViewController out;
        try {
            out = new MediatedBannerAdViewController(owner, response);
        } catch (Exception e) {
            return null;
        }
        return out;

    }

    private MediatedBannerAdViewController(AdView owner, AdResponse response) throws Exception {
        width = response.getWidth();
        height = response.getHeight();
        uid = response.getMediatedUID();
        className = response.getMediatedViewClassName();
        param = response.getParameter();
        this.owner = owner;

        resultCB = response.getMediatedResultCB();

        try {
            c = Class.forName(className);

        } catch (ClassNotFoundException e) {
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.class_not_found_exception));
            throw e;
        }

        try {
            mAV = (MediatedBannerAdView) c.newInstance();
        } catch (InstantiationException e) {
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.instantiation_exception));
            failed = true;
            throw e;
        } catch (IllegalAccessException e) {
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.illegal_access_exception));
            failed = true;
            throw e;
        }
        placeableView = mAV.requestAd(this, (Activity) owner.getContext(), param, uid, width, height, owner);
    }

    @Override
    public View getView() {
        return placeableView;
    }

    @Override
    public boolean failed() {
        return failed;
    }

    public void onAdLoaded() {
        if (owner.getAdListener() != null) {
            owner.getAdListener().onAdLoaded(owner);
        }

        fireResultCB(RESULT.SUCCESS);
    }

    public void onAdFailed(MediatedBannerAdViewController.RESULT reason) {
        if (owner.getAdListener() != null) {
            owner.getAdListener().onAdRequestFailed(owner);
        }
        this.failed = true;

        fireResultCB(reason);
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

    private void fireResultCB(final MediatedBannerAdViewController.RESULT result){

        //fire call to result cb url
        HTTPGet<Void, Void, HTTPResponse> cb = new HTTPGet<Void, Void, HTTPResponse>() {
            @Override
            protected void onPostExecute(HTTPResponse response) {
                AdFetcher f = MediatedBannerAdViewController.this.owner.mAdFetcher;
                f.dispatchResponse(new AdResponse(f, response.getResponseBody(), response.getHeaders()));
            }

            @Override
            protected String getUrl() {
                return resultCB+"&reason="+result;
            }
        };

        cb.execute();
    }
}
