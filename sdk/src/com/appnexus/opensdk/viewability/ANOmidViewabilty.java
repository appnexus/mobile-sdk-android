/*
 *    Copyright 2018 APPNEXUS INC
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
package com.appnexus.opensdk.viewability;

import android.content.Context;
import android.content.res.AssetManager;

import com.appnexus.opensdk.R;
import com.appnexus.opensdk.SDKSettings;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.StringUtil;
import com.iab.omid.library.appnexus.Omid;
import com.iab.omid.library.appnexus.adsession.Partner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class ANOmidViewabilty {

    private static ANOmidViewabilty omid_instance = null;
    public static final String OMID_PARTNER_NAME = "Appnexus";
    private static String OMID_JS_SERVICE_CONTENT = "";

    private static Partner appnexusPartner = null;

    public static ANOmidViewabilty getInstance() {
        if (omid_instance == null) {
            omid_instance = new ANOmidViewabilty();
            Clog.v(Clog.baseLogTag, Clog.getString(R.string.init));
        }
        return omid_instance;
    }

    private ANOmidViewabilty() {

    }


    public void activateOmidAndCreatePartner(Context applicationContext) {
        // Activate OMID if it is already not
        if (!SDKSettings.getOMEnabled())
            return;

        try {
            if (!Omid.isActive()) {
                Omid.activate(applicationContext);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }


        // If OMID active but partner is null then create partner
        if (Omid.isActive() && appnexusPartner == null) {
            try {
                appnexusPartner = Partner.createPartner(OMID_PARTNER_NAME, Settings.getSettings().sdkVersion);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        if (StringUtil.isEmpty(OMID_JS_SERVICE_CONTENT)) {
            try {
                OMID_JS_SERVICE_CONTENT =  StringUtil.getStringFromAsset("apn_omsdk.js", applicationContext);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public Partner getAppnexusPartner() {
        return appnexusPartner;
    }

    public String getOmidJsServiceContent() {
        return OMID_JS_SERVICE_CONTENT;
    }


}
