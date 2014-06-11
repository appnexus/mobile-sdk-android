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
            MediatedAd mediatedAd, AdViewListener listener) {
        MediatedBannerAdViewController out = new MediatedBannerAdViewController(activity, requester, mediatedAd, listener);
        return out.hasFailed ? null : out;
    }

    private MediatedBannerAdViewController(
            Activity activity, AdRequester requester, MediatedAd mediatedAd,
            AdViewListener listener) {
        super(requester, mediatedAd, listener);

        if (!isValid(MediatedBannerAdView.class))
            return;

        // if controller is valid, request an ad
        Clog.d(Clog.mediationLogTag, Clog.getString(R.string.mediated_request));

        ResultCode errorCode = null;

        startTimeout();

        TargetingParameters tp=null;
        try{
            AdView av = requester.getOwner();
            if(av !=null){
                tp = av.getTargetingParameters();
            }
        } catch(ClassCastException e){
        } finally {
            if (tp == null) {
                tp = new TargetingParameters();
            }
        }

        try {
            if(activity!=null){
                View viewFromMediatedAdaptor = ((MediatedBannerAdView) mAV).requestAd(this,
                        activity,
                        currentAd.getParam(),
                        currentAd.getId(),
                        currentAd.getWidth(),
                        currentAd.getHeight(),
                        tp);
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
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.mediated_view_null));
            errorCode = ResultCode.INTERNAL_ERROR;
        }

        if (errorCode != null) {
            onAdFailed(errorCode);
        }
    }

}
