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
package com.appnexus.opensdk.utils;

import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.DateUtils;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

public class WebviewUtil {

    /**
     * Call WebView onResume in API version safe manner
     * 
     * @param wv
     */
    public static void onResume(WebView wv) {
        if (wv == null) {
            return;
        }
        try {
            Method onResume = WebView.class.getDeclaredMethod("onResume");
            onResume.invoke(wv);
        } catch (Exception e) {
            // Expect this exception in API < 11
        }
    }

    /**
     * Call WebView onPause in API version safe manner
     * 
     * @param wv
     */
    public static void onPause(WebView wv) {
        if (wv == null) {
            return;
        }
        try {
            Method onPause = WebView.class.getDeclaredMethod("onPause");
            onPause.invoke(wv);
        } catch (Exception e) {
            // Expect this exception in API < 11
        }
    }

    /***
     * Synchronize the uuid2 cookie to the Webview Cookie Jar
     * This is only done if there is no present cookie.  
     * @param cookies
     */
    public static void cookieSync(List<Cookie> cookies) {
        try {
            CookieManager cm = CookieManager.getInstance();
            if (cm == null) {
                Clog.i(Clog.httpRespLogTag, "Unable to find a CookieManager");
                return;
            }
            String wvcookie = cm.getCookie(Settings.BASE_URL);
            if (!StringUtil.isEmpty(wvcookie)) {
                Clog.d(Clog.httpRespLogTag, "Webview already has our cookie");
                return;
            }
            CookieSyncManager csm = CookieSyncManager.getInstance();
            if (csm == null) {
                Clog.i(Clog.httpRespLogTag,
                        "Unable to find a CookieSyncManager");
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (Cookie c : cookies) {
                if (!Settings.AN_UUID.equals(c.getName())) {
                    continue;
                }
                if (!StringUtil.isEmpty(c.getDomain())) {
                    sb.append("domain=").append(c.getDomain()).append("; ");
                }
                if (!StringUtil.isEmpty(c.getPath())) {
                    sb.append("path=").append(c.getPath()).append("; ");
                }
                sb.append(c.getName()).append('=').append(c.getValue())
                        .append("; ");

                Date d = c.getExpiryDate();
                if (d != null && d.getTime() > 0) {
                    sb.append("expires=").append(DateUtils.formatDate(d))
                            .append("; ");
                }
                if (c.isSecure()) {
                    sb.append("secure");
                } else {
                    sb.append("HttpOnly");
                }
                Clog.d(Clog.httpRespLogTag, "set-cookie: " + sb.toString());
                cm.setCookie(Settings.COOKIE_DOMAIN, sb.toString());
                csm.sync();
            }
        } catch (IllegalStateException ise) {
        } catch (Exception e) {
        }

    }
}
