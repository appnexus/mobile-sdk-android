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
        } catch (Error e) {
			Clog.e(Clog.mediationLogTag, "Error in instantiating mediated view", e);
			//TODO: fix this hack - placed here because the request ad function is different
			fireResultCB(RESULT.MEDIATED_SDK_UNAVAILABLE, response.requester, response.getMediatedResultCB());
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
		//TODO: What do we do if placeableView is null on return and they don't report it?
        placeableView = ((MediatedBannerAdView) mAV).requestAd(this, owner != null ? (Activity) owner.getContext() : null, param, uid, width, height, owner);
		checkView();
    }

    @Override
    public View getView() {
        return placeableView;
    }

	private void checkView() {
		//TODO: what to do here? should this code be here? interstitials will always "fail". need to refactor controller?
		if (getView() == null) {
			Clog.e(Clog.mediationLogTag, "request for a mediated ad returned a null view");
			failed = true;
			onAdFailed(RESULT.UNABLE_TO_FILL);
		}
	}
}
