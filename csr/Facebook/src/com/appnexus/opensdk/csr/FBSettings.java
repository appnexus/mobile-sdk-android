/*
 *    Copyright 2020 APPNEXUS INC
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

package com.appnexus.opensdk.csr;

import android.content.Context;

import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.BidderTokenProvider;
import com.appnexus.opensdk.utils.Clog;

public class FBSettings {
    // key to retrieve AdChoices related objects
    public static final String KEY_ADCHOICES_ICON = "AdChoicesIcon";
    public static final String KEY_ADCHOICES_LINKURL = "AdChoicesLinkUrl";

    public static String getBidderToken(Context context) {
        if (AudienceNetworkAds.isInitialized(context)) {
            return BidderTokenProvider.getBidderToken(context);
        } else {
            Clog.e(Clog.csrLogTag, "FAN SDK must be initialised to get the Bidder Token");
            return null;
        }
    }
}
