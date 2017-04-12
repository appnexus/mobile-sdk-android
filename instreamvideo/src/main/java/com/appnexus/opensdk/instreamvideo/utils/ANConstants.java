/*
 *    Copyright 2016 APPNEXUS INC
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
package com.appnexus.opensdk.instreamvideo.utils;

import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;

public class ANConstants {
    public static final String CSM = "csm";
    public static final String SSM = "ssm";
    public static final String RTB = "rtb";
    public static final String CSM_VIDEO = "csmvideo";
    public static final String UTF_8 = "UTF-8";
    public static final String AD_TYPE_VIDEO = "video";
    public static final String AD_TYPE_HTML = "banner";
    public static final String MRAID_JS_FILENAME = "mraid.js";
    public static final String EXTRAS_KEY_MRAID = "MRAID";
    public static final String videoLogTag = Clog.baseLogTag + "-InStVideo";
    private static String WEBVIEW_PATH = "file:///android_asset/index.html";
    private static String DEBUG_WEBVIEW_PATH = "http://mobile.devnxs.net/debug/index.html?ast_debug=true";


    public static String getWebViewUrl() {
        return Settings.getSettings().debug_mode ? DEBUG_WEBVIEW_PATH: WEBVIEW_PATH;
    }
}
