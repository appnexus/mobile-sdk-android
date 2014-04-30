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
import com.appnexus.opensdk.utils.Clog;

/**
* An object of this type is sent to the third-party SDK's {@link
* MediatedInterstitialAdView} object.  The third-party SDK uses this
* object from within its interstitial view implementation to send
* events back to the AppNexus SDK.
*/

public class MediatedInterstitialAdViewController extends MediatedAdViewController {

    static MediatedInterstitialAdViewController create(
            Activity activity, AdRequester requester,
            MediatedAd mediatedAd, AdViewListener listener) {
        MediatedInterstitialAdViewController out = new MediatedInterstitialAdViewController(activity, requester, mediatedAd, listener);
        return out.hasFailed ? null : out;
    }

    private MediatedInterstitialAdViewController(
            Activity activity, AdRequester requester, MediatedAd mediatedAd,
            AdViewListener listener) {
        super(requester, mediatedAd, listener);

        if (!isValid(MediatedInterstitialAdView.class))
            return;

        // if controller is valid, request an ad.
        Clog.d(Clog.mediationLogTag, Clog.getString(R.string.mediated_request));

        ResultCode errorCode = null;

        TargetingParameters tp = null;
        try{
            AdView av = requester.getOwner();
            if (av!= null){
                tp = av.getTargetingParameters();
            }
        }catch(ClassCastException e){

        } finally {
            if (tp == null) {
                tp = new TargetingParameters();
            }
        }

        startTimeout();
        try {
            if(activity!=null){
                ((MediatedInterstitialAdView) mAV).requestAd(this,
                        activity,
                        currentAd.getParam(),
                        currentAd.getId(),
                        tp);
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

        if (errorCode != null)
            onAdFailed(errorCode);
    }

    void show() {
        if (mAV != null) {
            ((MediatedInterstitialAdView) mAV).show();
        }
    }

    boolean isReady() {
        return ((MediatedInterstitialAdView) mAV).isReady();
    }
}
