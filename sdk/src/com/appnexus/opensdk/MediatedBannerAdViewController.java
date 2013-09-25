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

    View placeableView;

    static public MediatedBannerAdViewController create(AdView owner, LinkedList<MediatedAd> mediatedAds) {
        MediatedBannerAdViewController out = new MediatedBannerAdViewController(owner, mediatedAds);
        return out.failed() ? null : out;
    }

    private MediatedBannerAdViewController(AdView owner, LinkedList<MediatedAd> mediatedAds) {
        super(owner, mediatedAds);

        if (this.mAV == null || !(this.mAV instanceof MediatedBannerAdView)) {
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.instance_exception, getClass().getCanonicalName()));
            onAdFailed(RESULT.MEDIATED_SDK_UNAVAILABLE);
            return;
        }
        //TODO: refactor - this also depends on owner. what if owner is null? (for testing)
        try {
            placeableView = ((MediatedBannerAdView) mAV).requestAd(this,
                    owner != null ? (Activity) owner.getContext() : null,
                    currentAd.getParam(),
                    currentAd.getId(),
                    currentAd.getWidth(),
                    currentAd.getHeight(),
                    owner);
        } catch (Exception e) {
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.mediated_request_exception), e);
            onAdFailed(RESULT.INVALID_REQUEST);
        } catch (Error e) {
            // catch errors. exceptions will be caught above.
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.mediated_request_error), e);
            onAdFailed(RESULT.MEDIATED_SDK_UNAVAILABLE);
        }

        if (placeableView == null) {
            Clog.e(Clog.mediationLogTag, Clog.getString(R.string.mediated_view_null));
            onAdFailed(RESULT.UNABLE_TO_FILL);
        }
    }

    @Override
    public View getView() {
        return placeableView;
    }
}
