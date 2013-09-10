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

public class MediatedBannerAdViewController extends MediatedAdViewController implements Displayable {

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
        super(owner, response);

        if (this.mAV == null || !(this.mAV instanceof MediatedBannerAdView)) {
            throw new Exception("Mediated view is null or not an instance of MediatedBannerAdView");
        }
        //TODO: refactor - this also depends on owner. what if owner is null? (for testing)
        try {
            placeableView = ((MediatedBannerAdView) mAV).requestAd(this, owner != null ? (Activity) owner.getContext() : null, param, uid, width, height, owner);
        } catch (Exception e) {
            Clog.e(Clog.mediationLogTag, "Exception in requestAd from mediated view", e);
            throw e;
        } catch (Error e) {
            // catch errors. exceptions will be caught above.
            Clog.e(Clog.mediationLogTag, "Error in requestAd from mediated view", e);
            onAdFailed(RESULT.MEDIATED_SDK_UNAVAILABLE);
        }

        if (placeableView == null) {
            Clog.e(Clog.mediationLogTag, "request for a mediated ad returned a null view");
            failed = true;
            onAdFailed(RESULT.UNABLE_TO_FILL);
        }
    }

    @Override
    public View getView() {
        return placeableView;
    }
}
