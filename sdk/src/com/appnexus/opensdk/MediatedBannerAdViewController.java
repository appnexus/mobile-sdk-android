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

import android.app.Activity;
import android.view.View;

import com.appnexus.opensdk.utils.Clog;

/**
 * An object of this type is sent to the third-party SDK's {@link
 * MediatedBannerAdView} object.  The third-party SDK uses this object
 * from within its banner view implementation to send events back to
 * the AppNexus SDK.
 */

public class MediatedBannerAdViewController extends MediatedAdViewController {
    static MediatedBannerAdViewController create(
            Activity activity, AdRequester requester,
            MediatedAd mediatedAd, AdDispatcher listener) {
        MediatedBannerAdViewController out = new MediatedBannerAdViewController(activity, requester, mediatedAd, listener);
        return out.hasFailed ? null : out;
    }

    @Override
    boolean isReady(){
        return true;
    }

    //Required for interstitials only
    @Override
    void show() {
        return;
    }

    private MediatedBannerAdViewController(
            Activity activity, AdRequester requester, MediatedAd mediatedAd,
            AdDispatcher listener) {
        super(requester, mediatedAd, listener, MediaType.BANNER);

        if (!isValid(MediatedBannerAdView.class))
            return;

        // if controller is valid, request an ad
        Clog.d(Clog.mediationLogTag, Clog.getString(R.string.mediated_request));

        ResultCode errorCode = null;

        startTimeout();
        markLatencyStart();

        try {
            if(activity!=null && !destroyed){
                View viewFromMediatedAdaptor = ((MediatedBannerAdView) mAV).requestAd(this,
                        activity,
                        currentAd.getParam(),
                        currentAd.getId(),
                        currentAd.getWidth(),
                        currentAd.getHeight(),
                        getTargetingParameters());
                mediatedDisplayable.setView(viewFromMediatedAdaptor);
            }else{
                Clog.e(Clog.mediationLogTag, Clog.getString(R.string.mediated_request_null_activity));
                errorCode = ResultCode.INTERNAL_ERROR;
            }
        } catch (Exception e) {
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.mediated_request_exception), e);
            errorCode = ResultCode.INTERNAL_ERROR;
        } catch (Error e) {
            // catch errors. exceptions will be caught above.
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.mediated_request_error), e);
            errorCode = ResultCode.INTERNAL_ERROR;
        }

        if ((errorCode == null) && (mediatedDisplayable.getView() == null)) {
            // To check that if by accident instantiated an interstitial ad
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.mediated_view_null));
            errorCode = ResultCode.INTERNAL_ERROR;
        }

        if (errorCode != null) {
            onAdFailed(errorCode);
        }
    }

    @Override
    public void onDestroy() {
        destroyed=true;
        if(mAV!=null) {
            mAV.onDestroy();
        }
    }

    @Override
    public void onPause() {
        if(mAV!=null) {
            mAV.onPause();
        }
    }

    @Override
    public void onResume() {
        if(mAV!=null){
            mAV.onResume();
        }
    }
}
