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

import java.util.LinkedList;

public class MediatedInterstitialAdViewController extends MediatedAdViewController implements Displayable {

    Activity activity;

    static public MediatedInterstitialAdViewController create(
            Activity activity, AdRequester requester,
            LinkedList<MediatedAd> mediatedAds, MediatedAdViewControllerListener listener) {
        MediatedInterstitialAdViewController out = new MediatedInterstitialAdViewController(activity, requester, mediatedAds, listener);
        return out.failed() ? null : out;
    }

    protected MediatedInterstitialAdViewController(
            Activity activity, AdRequester requester, LinkedList<MediatedAd> mediatedAds,
            MediatedAdViewControllerListener listener) {
        super(requester, mediatedAds, listener);

        if (this.mAV == null || !(this.mAV instanceof MediatedInterstitialAdView)) {
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.instance_exception, getClass().getCanonicalName()));
            onAdFailed(RESULT.MEDIATED_SDK_UNAVAILABLE);
            return;
        }

        this.activity = activity;
    }

    protected void show() {
        if (mAV != null) {
            ((MediatedInterstitialAdView) mAV).show();
        }
    }

    @Override
    public View getView() {
        Clog.d(Clog.mediationLogTag, Clog.getString(R.string.mediated_request));
        try {
            ((MediatedInterstitialAdView) mAV).requestAd(this,
                    activity,
                    currentAd.getParam(),
                    currentAd.getId());
        } catch (Exception e) {
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.mediated_request_exception), e);
            onAdFailed(RESULT.INVALID_REQUEST);
        } catch (Error e) {
            // catch errors. exceptions will be caught above.
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.mediated_request_error), e);
            onAdFailed(RESULT.MEDIATED_SDK_UNAVAILABLE);
        }

        return null;
    }
}
