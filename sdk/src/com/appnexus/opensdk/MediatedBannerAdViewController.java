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

public class MediatedBannerAdViewController extends MediatedAdViewController implements Displayable {

    private View placeableView;

    static public MediatedBannerAdViewController create(
            Activity activity, AdRequester requester,
            LinkedList<MediatedAd> mediatedAds, MediatedAdViewControllerListener listener) {
        MediatedBannerAdViewController out = new MediatedBannerAdViewController(activity, requester, mediatedAds, listener);
        return out.failed() ? null : out;
    }

    protected MediatedBannerAdViewController(
            Activity activity, AdRequester requester, LinkedList<MediatedAd> mediatedAds,
            MediatedAdViewControllerListener listener) {
        super(requester, mediatedAds, listener);

        if (!isValid(getClass()))
            return;

        // if controller is valid, request an ad
        Clog.d(Clog.mediationLogTag, Clog.getString(R.string.mediated_request));

        RESULT errorCode = null;

        try {
            placeableView = ((MediatedBannerAdView) mAV).requestAd(this,
                    activity,
                    currentAd.getParam(),
                    currentAd.getId(),
                    currentAd.getWidth(),
                    currentAd.getHeight());
        } catch (Exception e) {
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.mediated_request_exception), e);
            errorCode = RESULT.INVALID_REQUEST;
        } catch (Error e) {
            // catch errors. exceptions will be caught above.
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.mediated_request_error), e);
            errorCode = RESULT.MEDIATED_SDK_UNAVAILABLE;
        }

        if (placeableView == null) {
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.mediated_view_null));
            errorCode = RESULT.UNABLE_TO_FILL;
        }

        if (errorCode != null) {
            onAdFailed(errorCode);
        }
    }

    @Override
    public View getView() {
        return placeableView;
    }
}
