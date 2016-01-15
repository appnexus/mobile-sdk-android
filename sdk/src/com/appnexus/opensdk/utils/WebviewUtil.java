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

import android.annotation.SuppressLint;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.DateUtils;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class WebviewUtil {

    /**
     * Convenience method to set generic WebView settings
     *
     * @param webView webView to apply settings to
     */
    @SuppressLint("SetJavaScriptEnabled")
    public static void setWebViewSettings(WebView webView) {
        if (webView == null) {
            return;
        }
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager cm = CookieManager.getInstance();
            if (cm != null) {
                cm.setAcceptThirdPartyCookies(webView, true);
            } else {
                Clog.d(Clog.baseLogTag, "Failed to set Webview to accept 3rd party cookie");
            }
        }
    }

    /**
     * Call WebView onResume in API version safe manner
     *
     * @param wv The webview to invoke onResume on
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
     * @param wv The webview to invoke onPause on
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


    /**
     * Synchronize the uuid2 cookie to the Webview Cookie Jar
     * This is only done if there is no present cookie.
     *
     * @param cookies Cookies to sync
     */
    public static void cookieSync(List<Cookie> cookies) {
        if (cookies == null || cookies.isEmpty()) return;
        CookieManager cm = CookieManager.getInstance();
        if (cm == null) {
            Clog.i(Clog.httpRespLogTag, "Unable to find a CookieManager");
            return;
        }
        try {
            StringBuilder sb = new StringBuilder();
            for (Cookie c : cookies) {
                if (!Settings.AN_UUID.equals(c.getName())) {
                    continue;
                }
                String existingUUID = getExistingANUUID(cm);
                if (existingUUID != null && !existingUUID.contains(c.getValue())) {
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
                    Clog.i(Clog.httpRespLogTag, "set-cookie: " + sb.toString());
                    cm.setCookie(Settings.COOKIE_DOMAIN, sb.toString());
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        // CookieSyncManager is deprecated in API 21 Lollipop
                        CookieSyncManager csm = CookieSyncManager.getInstance();
                        if (csm == null) {
                            Clog.i(Clog.httpRespLogTag,
                                    "Unable to find a CookieSyncManager");
                            return;
                        }
                        csm.sync();
                    } else {
                        cm.flush();
                    }
                }
            }
        } catch (IllegalStateException ise) {
        } catch (Exception e) {
        }

    }

    private static final String VERSION_ZERO_HEADER = "Set-cookie";

    private static final String VERSION_ONE_HEADER = "Set-cookie2";

    /**
     * Synchronize the uuid2 cookie to the Webview Cookie Jar
     * This is only done if there is no present cookie.
     *
     * @param headers headers to extract cookies from for syncing
     */
    @SuppressWarnings("deprecation")
    public static void httpCookieSync(Map<String, List<String>> headers) {
        if (headers == null || headers.isEmpty()) return;
        CookieManager cm = CookieManager.getInstance();
        if (cm == null) {
            Clog.i(Clog.httpRespLogTag, "Unable to find a CookieManager");
            return;
        }
        try {
            String existingUUID = getExistingANUUID(cm);

            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                String key = entry.getKey();
                // Only "Set-cookie" and "Set-cookie2" pair will be parsed
                if (key != null && (key.equalsIgnoreCase(VERSION_ZERO_HEADER)
                        || key.equalsIgnoreCase(VERSION_ONE_HEADER))) {
                    for (String cookieStr : entry.getValue()) {
                        if (!StringUtil.isEmpty(cookieStr) && cookieStr.contains(Settings.AN_UUID)) {
                            // pass uuid2 to WebView Cookie jar if it's empty or outdated
                            if (existingUUID == null || !cookieStr.contains(existingUUID)) {
                                Clog.i(Clog.httpRespLogTag, "set-cookie: " + cookieStr);
                                cm.setCookie(Settings.COOKIE_DOMAIN, cookieStr);
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                                    // CookieSyncManager is deprecated in API 21 Lollipop
                                    CookieSyncManager csm = CookieSyncManager.getInstance();
                                    if (csm == null) {
                                        Clog.i(Clog.httpRespLogTag,
                                                "Unable to find a CookieSyncManager");
                                        return;
                                    }
                                    csm.sync();
                                } else {
                                    cm.flush();
                                }
                            }
                        }
                    }
                }
            }
        } catch (IllegalStateException ise) {
        } catch (Exception e) {
        }
    }

    public static String getCookie() {
        CookieManager cm = CookieManager.getInstance();
        if (cm == null) {
            Clog.i(Clog.httpRespLogTag, "Unable to find a CookieManager");
            return null;
        }
        return cm.getCookie(Settings.COOKIE_DOMAIN);
    }

    private static String getExistingANUUID(CookieManager cm) {
        if (cm != null) {
            String wvcookie = cm.getCookie(Settings.COOKIE_DOMAIN);
            String[] existingCookies = wvcookie.split("; ");
            for (String cookie : existingCookies) {
                if (cookie != null && cookie.contains(Settings.AN_UUID)) {
                    return cookie;
                }
            }
        }
        return null;
    }
}
