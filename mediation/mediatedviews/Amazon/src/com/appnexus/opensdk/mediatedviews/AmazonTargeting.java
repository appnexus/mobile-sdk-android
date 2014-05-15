/*
 *    Copyright 2014 APPNEXUS INC
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

import com.amazon.device.ads.Ad;
import com.amazon.device.ads.AdTargetingOptions;
import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.utils.StringUtil;

class AmazonTargeting {

    /**
     * Create the Amazon Targeting parameters based on the AppNexus targeting
     * parameters and the optional server side overrides for timeout and floor
     * price
     * 
     * @param ad
     *            The Ad for which these targeting parameters will apply
     * @param tp
     *            The AppNexus targeting parameters
     * @param json_parameter
     *            The optional parameters string from Ad Network Manager (Server
     *            side overrides)
     * @return The Amazon targeting parameters
     */
    static AdTargetingOptions createTargeting(Ad ad, TargetingParameters tp,
            String json_parameter) {
        AdTargetingOptions targetingOptions = new AdTargetingOptions();
        AmazonParameters ssParams = new AmazonParameters(json_parameter);

        if (tp != null) {

            if (!StringUtil.isEmpty(tp.getAge())) {
                try {
                    targetingOptions.setAge(Integer.parseInt(tp.getAge()));
                } catch (NumberFormatException e) {
                }
            }

            for (Pair<String, String> p : tp.getCustomKeywords()) {
                targetingOptions.setAdvancedOption(p.first, p.second);
            }

            targetingOptions.enableGeoLocation(tp.getLocation() != null);

            if (ssParams.hasFloor) {
                targetingOptions.setFloorPrice(ssParams.floor);
            }
        }

        if (ssParams.hasTimeout) {
            ad.setTimeout(ssParams.timeout);
        }
        return targetingOptions;
    }

}
