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

package com.example.simplevideo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Switch;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        AdVideoData.getInstance().init(this);
    }

    /**
     * Called when the user clicks the launch Demo button
     */
    public void launchDemo(View view) {
        Intent intent = new Intent(this, SimpleVideoActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AdVideoData.getInstance().loadAd();


        PackageManager pm = getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo("com.google.android.webview", 0);
            Log.i(TAG, "version name: " + pi.versionName);
            Log.i(TAG, "version code: " + pi.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Android System WebView is not found");
        }


        WebView webView = new WebView(getApplicationContext());
        String useragent = webView.getSettings().getUserAgentString();
        Log.i(TAG, "useragent: " + useragent);
    }
}
