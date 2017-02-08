package com.appnexus.opensdk.instreamvideo.shadows;

import android.webkit.CookieManager;
import android.webkit.WebView;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

@Implements(CookieManager.class)
public class ShadowCookieManager extends org.robolectric.shadows.ShadowCookieManager {
    @Implementation
    public synchronized void setAcceptThirdPartyCookies(WebView webview, boolean accept) {
        accept = false;
    }
}
