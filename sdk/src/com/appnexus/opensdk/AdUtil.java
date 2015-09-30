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
package com.appnexus.opensdk;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.StringUtil;


public class AdUtil {


    public static boolean openBrowser(Context context, String clickThroughURL, boolean shouldOpenNativeBrowser){
        if (shouldOpenNativeBrowser){
            return openNativeBrowser(context, clickThroughURL);
        }else{
            return openInAppBrowser(context, clickThroughURL);
        }
    }


    static boolean openNativeBrowser(Context context, String url) {

        if (!StringUtil.isEmpty(url) && context != null) {
            try {
                Intent inAppBrowserIntent = new Intent(Intent.ACTION_VIEW);
                inAppBrowserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                inAppBrowserIntent.setData(Uri.parse(url));
                context.startActivity(inAppBrowserIntent);
                return true;
            }catch (ActivityNotFoundException e){
                Clog.w(Clog.baseLogTag, "Native browser not found.");
            }

        }
        return false;
    }


    static boolean openInAppBrowser(Context context, String clickThroughURL) {
        if (!StringUtil.isEmpty(clickThroughURL) && context != null) {
            Intent intent = new Intent(context, AdActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(AdActivity.INTENT_KEY_ACTIVITY_TYPE, AdActivity.ACTIVITY_TYPE_BROWSER);
            intent.putExtra(AdActivity.CLICK_URL, clickThroughURL);

            try {
                context.startActivity(intent);
                return true;
            } catch (ActivityNotFoundException e) {
                Clog.w(Clog.baseLogTag, Clog.getString(R.string.adactivity_missing, AdActivity.class.getName()));
            }
        }
        return false;

    }


}
