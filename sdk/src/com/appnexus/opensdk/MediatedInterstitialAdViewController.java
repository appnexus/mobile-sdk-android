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
* An object of this type is sent to the 3rd party SDK's MediatedInterstitialAdView object. The 3rd party
* SDK uses this object from within its interstitial view implementation to send events back to the AppNexus
* SDK
*
*/public class MediatedInterstitialAdViewController extends MediatedAdViewController implements Displayable {

    private Activity activity;

    static MediatedInterstitialAdViewController create(
            Activity activity, AdRequester requester,
            MediatedAd mediatedAd, AdViewListener listener) {
        MediatedInterstitialAdViewController out = new MediatedInterstitialAdViewController(activity, requester, mediatedAd, listener);
        return out.failed() ? null : out;
    }

    private MediatedInterstitialAdViewController(
            Activity activity, AdRequester requester, MediatedAd mediatedAd,
            AdViewListener listener) {
        super(requester, mediatedAd, listener);

        if (!isValid(MediatedInterstitialAdView.class))
            return;

        this.activity = activity;
    }

    void show() {
        if (mAV != null) {
            ((MediatedInterstitialAdView) mAV).show();
        }
    }

    @Override
    public View getView() {
        // if controller is valid, request an ad.
        // create() will never return a non-null, invalid controller
        Clog.d(Clog.mediationLogTag, Clog.getString(R.string.mediated_request));

        RESULT errorCode = null;

        startTimeout();
        try {
            ((MediatedInterstitialAdView) mAV).requestAd(this,
                    activity,
                    currentAd.getParam(),
                    currentAd.getId());
        } catch (Exception e) {
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.mediated_request_exception), e);
            errorCode = RESULT.INVALID_REQUEST;
        } catch (Error e) {
            // catch errors. exceptions will be caught above.
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.mediated_request_error), e);
            errorCode = RESULT.MEDIATED_SDK_UNAVAILABLE;
        }

        if (errorCode != null)
            onAdFailed(errorCode);

        return null;
    }
}
