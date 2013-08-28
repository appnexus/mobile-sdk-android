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

public class MediatedBannerAdViewController extends MediatedAdViewController implements Displayable {

    View placeableView;

    static public MediatedBannerAdViewController create(AdView owner, AdResponse response) {
        MediatedBannerAdViewController out;
        try {
            out = new MediatedBannerAdViewController(owner, response);
        } catch (Exception e) {
            return null;
        }
        if (out.mAV == null || !(out.mAV instanceof MediatedBannerAdView)) {
            return null;
        }
        return out;

    }

    private MediatedBannerAdViewController(AdView owner, AdResponse response) throws Exception {
        super(owner, response);

        placeableView = ((MediatedBannerAdView) mAV).requestAd(this, (Activity) owner.getContext(), param, uid, width, height, owner);
    }

    @Override
    public View getView() {
        return placeableView;
    }
}
