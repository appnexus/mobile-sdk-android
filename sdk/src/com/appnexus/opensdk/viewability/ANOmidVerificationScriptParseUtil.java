/*
 *    Copyright 2019 APPNEXUS INC
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


import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.JsonUtil;
import com.iab.omid.library.appnexus.adsession.VerificationScriptResource;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.*;

public class ANOmidVerificationScriptParseUtil {


    private static final String KEY_VIEWABILITY = "viewability";
    private static final String RESPONSE_KEY_CONFIG = "config";
    private static final Pattern PATTERN_SRC_VALUE = Pattern.compile("src=\"(.*?)\"");
    private static final Pattern PATTERN_VENDORKEY_VALUE = Pattern.compile("vk=(.*?);");
    private static final String KEY_HASH = "#";


    /**
     * Process the metadata of Viewability Config from ad server response
     *
     * @param adObject JsonObject that contains info of viewability
     * @return ANVerificationScriptResource if no issue happened during processing
     */
    public static VerificationScriptResource parseViewabilityObjectfromAdObject(JSONObject adObject){

        JSONObject viewabilityObject = JsonUtil.getJSONObject(adObject,KEY_VIEWABILITY);

        if (viewabilityObject == null) {
            return null;
        }

        String configString = JsonUtil.getJSONString(viewabilityObject, RESPONSE_KEY_CONFIG);
        if (configString.equalsIgnoreCase("")) {
            return null;
        }

        VerificationScriptResource omidverificationScriptResource;
        try {
            //Extracts everything inbetween src=""
            Matcher srcStringMatcher = PATTERN_SRC_VALUE.matcher(configString);
            srcStringMatcher.find(0);
            String src = srcStringMatcher.group(1);


            String verificationScriptResource[] = src.split(KEY_HASH, 2);
            URL url = new URL(verificationScriptResource[0]);
            String params = verificationScriptResource[1];

            Matcher vkStringMatcher = PATTERN_VENDORKEY_VALUE.matcher(params);
            vkStringMatcher.find(0);
            String vendorKey = vkStringMatcher.group(1);
            omidverificationScriptResource =
                    VerificationScriptResource.createVerificationScriptResourceWithParameters(vendorKey,
                            url, params);

        }catch (Exception e){
            Clog.e(Clog.baseLogTag, " Exception: " + e.getMessage());
            return null;
        }
        return omidverificationScriptResource;
    }

}
