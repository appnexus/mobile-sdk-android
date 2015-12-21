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

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.StringUtil;
import com.appnexus.opensdk.utils.WebviewUtil;


class AdUtil {

    public static boolean openBrowser(Context context, String clickThroughURL, boolean shouldOpenInNativeBrowser, boolean shouldLoadInBackground, boolean shouldShowLoadingIndicator, final DialogInterface.OnCancelListener clickCancelListener){
        if (shouldOpenInNativeBrowser){
            return openNativeBrowser(context, clickThroughURL);
        }else{
            if(shouldLoadInBackground) {
                return openInAppRedirectBrowser(context, clickThroughURL, shouldShowLoadingIndicator, clickCancelListener);
            }else {
                return openInAppBrowser(context, clickThroughURL);
            }
        }
    }

    private static boolean openInAppRedirectBrowser(Context context, String clickThroughURL, boolean shouldShowLoadingIndicator, final DialogInterface.OnCancelListener clickCancelListener) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        final RedirectBrowserWebview out = new RedirectBrowserWebview(context, progressDialog);
        out.loadUrl(clickThroughURL);
        out.setVisibility(View.GONE);

        if(shouldShowLoadingIndicator) {
            //Show a dialog box
            progressDialog.setCancelable(true);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    out.stopLoading();
                    if(clickCancelListener != null){
                        clickCancelListener.onCancel(dialogInterface);
                    }
                }
            });
            progressDialog.setMessage(context.getResources().getString(R.string.loading));
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }
        return true;
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

    // returns success or failure
    private static boolean checkForApp(Context context, String url) {
        if (url.contains("://play.google.com") || (!url.startsWith("http") && !url.startsWith("about:blank"))) {
            Clog.i(Clog.baseLogTag, Clog.getString(R.string.opening_app_store));
            return openNativeBrowser(context, url);
        }

        return false;
    }

    private static void loadInAppBrowser(WebView fwdWebView, Context context) {
        Class<?> activity_clz = AdActivity.getActivityClass();

        Intent intent = new Intent(context, activity_clz);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(AdActivity.INTENT_KEY_ACTIVITY_TYPE, AdActivity.ACTIVITY_TYPE_BROWSER);

        BrowserAdActivity.BROWSER_QUEUE.add(fwdWebView);

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Clog.w(Clog.baseLogTag, Clog.getString(R.string.adactivity_missing, activity_clz.getName()));
            BrowserAdActivity.BROWSER_QUEUE.remove();
        }
    }

    static class RedirectBrowserWebview extends WebView {

        private boolean stopLoading = false;
        @SuppressLint("SetJavaScriptEnabled")
        public RedirectBrowserWebview(final Context context, final ProgressDialog progressDialog) {
            super(context);
            stopLoading = false;
            WebviewUtil.setWebViewSettings(this);
            this.setWebViewClient(new WebViewClient() {
                private boolean isOpeningAppStore = false;


                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    Clog.v(Clog.browserLogTag, "Redirecting to URL: " + url);
                    isOpeningAppStore = checkForApp(context, url);

                    if (isOpeningAppStore) {
                        if(progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }

                    return isOpeningAppStore;
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    Clog.v(Clog.browserLogTag, "Opening URL: " + url);

                    if(progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }

                    if (isOpeningAppStore) {
                        isOpeningAppStore = false;
                        RedirectBrowserWebview.this.destroy();
                        return;
                    }

                    if(stopLoading){
                        RedirectBrowserWebview.this.destroy();
                        return;
                    }

                    RedirectBrowserWebview.this.setVisibility(View.VISIBLE);
                    loadInAppBrowser(RedirectBrowserWebview.this, context);
                }
            });
        }

        /**
         * Construct a new WebView with layout parameters.
         *
         * @param context A Context object used to access application assets.
         * @param attrs   An AttributeSet passed to our parent.
         */
        public RedirectBrowserWebview(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public void stopLoading() {
            super.stopLoading();
            stopLoading = true;
        }
    }

}
