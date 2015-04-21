/*
 *    Copyright 2015 APPNEXUS INC
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

package com.appnexus.opensdk.mediatedviews;

import android.util.Pair;

import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.utils.StringUtil;
import com.vdopia.ads.lw.LVDOAdRequest;

public class VdopiaSettings {
    public static LVDOAdRequest buildRequest(TargetingParameters tp) {
        LVDOAdRequest adRequest = new LVDOAdRequest();

        if (tp != null) {
            if (tp.getAge() != null) {
                adRequest.setAge(tp.getAge());
            }

            switch (tp.getGender()) {
                case UNKNOWN:
                    adRequest.setGender(LVDOAdRequest.LVDOGender.UNKNOWN);
                    break;
                case MALE:
                    adRequest.setGender(LVDOAdRequest.LVDOGender.MALE);
                    break;
                case FEMALE:
                    adRequest.setGender(LVDOAdRequest.LVDOGender.FEMALE);
                    break;
            }

            if (tp.getLocation() != null) {
                adRequest.setLocation(tp.getLocation());
            }

            for (Pair<String, String> p : tp.getCustomKeywords()) {
                if (!StringUtil.isEmpty(p.second)) {
                    adRequest.addKeyword(p.second);
                }
            }

        }

        return adRequest;
    }
}
