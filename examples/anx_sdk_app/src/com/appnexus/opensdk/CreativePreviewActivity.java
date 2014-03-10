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

package com.appnexus.opensdk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.HTTPGet;
import com.appnexus.opensdk.utils.HTTPResponse;
import com.appnexus.opensdk.utils.ViewUtil;
import com.appnexus.opensdkapp.R;

import java.net.URLDecoder;

public class CreativePreviewActivity extends Activity {

    private String adType, url;
    int width, height;

    LinearLayout adFrame;
    AdView adView;

    private AlertDialog errorDialog;

    private static final String BANNER = "banner";
    private static final String INTERSTITIAL = "interstitial";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (errorDialog != null) {
            errorDialog.dismiss();
            errorDialog = null;
        }

        if (parseIntentData(intent)) return;

        setContentView(R.layout.activity_creativepreview);

        adFrame = (LinearLayout) findViewById(R.id.creativepreview_adframe);
        Button reloadButton = (Button) findViewById(R.id.creativepreview_btn_reload);
        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runGetAdContent();
            }
        });

        runGetAdContent();
    }

    // returns true if failed
    private boolean parseIntentData(Intent intent) {
        // reset any stale data
        adType = null;
        url = null;
        width = 0;
        height = 0;
        adView = null;

        boolean failed = false;
        StringBuilder errorSB = new StringBuilder();
        if (intent == null) {
            return true;
        }

        Clog.d(Clog.baseLogTag, "Creative Preview launched with data: " + intent.getDataString());

        Uri data = intent.getData();

        adType = data.getHost();
        String w = data.getQueryParameter("w");
        String h = data.getQueryParameter("h");
        String url = data.getQueryParameter("url");
        try {
            this.url = URLDecoder.decode(url, "UTF-8");
        } catch (Exception e) {
            this.url = null;
            errorSB.append("Failure decoding required 'url' parameter\n");
            failed = true;
        }

        if (!BANNER.equals(adType) && !INTERSTITIAL.equals(adType)) {
            errorSB.append("Host should be either 'banner' or 'interstitial'\n");
        }

        try {
            width = Integer.parseInt(w);
        } catch (NumberFormatException e) {
            width = 0;
            errorSB.append("Error parsing required 'w' width parameter \n");
            failed = true;
        }

        try {
            height = Integer.parseInt(h);
        } catch (NumberFormatException e) {
            height = 0;
            errorSB.append("Error parsing required 'h' height parameter\n");
            failed = true;
        }

        if (failed) showErrorDialog(errorSB.toString());
        return failed;
    }

    private void showErrorDialog(String errorMessage) {
        errorDialog = new AlertDialog.Builder(this)
                .setTitle("Error with Intent Data")
                .setMessage(errorMessage)
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                    }
                })
                .show();
    }

    private void runGetAdContent() {
        HTTPGet<Void, Void, HTTPResponse> request = new HTTPGet<Void, Void, HTTPResponse>() {
            @Override
            protected void onPostExecute(HTTPResponse response) {
                Clog.d(Clog.baseLogTag, "Opening Creative Preview for: " + url);

                ViewUtil.removeChildFromParent(adView);

                // default is banner
                adView = INTERSTITIAL.equals(adType)
                        ? new InterstitialAdView(CreativePreviewActivity.this)
                        : new BannerAdView(CreativePreviewActivity.this, 0);

                if (adView instanceof BannerAdView) {
                    adView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    ((LinearLayout.LayoutParams) adView.getLayoutParams()).gravity = Gravity.CENTER;
                    adFrame.addView(adView);
                }

                adView.loadAdFromHtml(response.getResponseBody(), width, height);
                if (adView instanceof InterstitialAdView) {
                    ((InterstitialAdView) adView).show();
                }
            }

            @Override
            protected String getUrl() {
                return url;
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            request.execute();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (errorDialog != null) {
            errorDialog.dismiss();
            errorDialog = null;
        }
    }
}
