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

package com.appnexus.opensdkapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.*;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.TabHost.TabContentFactory;
import com.appnexus.opensdk.AndroidAdvertisingIDUtil;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.ClogListener;
import com.appnexus.opensdk.utils.Settings;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class MainActivity extends FragmentActivity implements
        TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener, SettingsFragment.OnLoadAdClickedListener {

    private static final String SETTINGS_ID = "Settings";
    private static final String PREVIEW_ID = "Preview";
    private static final String DEBUG_ID = "Debug";

    private static final int NUM_TABS = 3;

    public static enum TABS {
        SETTINGS,
        PREVIEW,
        DEBUG
    }

    private TabHost tabHost;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private View btnMore, btnLog;
    private View contentView;

    private AlertDialog logDialog, progressDialog;

    private boolean isShowingLogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Clog.v(Constants.BASE_LOG_TAG, "App created");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        initTabHost();
        initViewPager();

        tabHost.setOnTabChangedListener(this);

        // Default to Preview tab
        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                onPageSelected(TABS.PREVIEW.ordinal());
            }
        }.sendEmptyMessage(0);

        btnMore = findViewById(R.id.btn_log);
        btnLog = findViewById(R.id.log_extension);

        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnLog.getVisibility() == View.VISIBLE)
                    btnLog.setVisibility(View.GONE);
                else
                    btnLog.setVisibility(View.VISIBLE);
            }
        });

        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.GONE);
                showLogDialog();
            }
        });

        contentView = getWindow().findViewById(Window.ID_ANDROID_CONTENT);

        Clog.registerListener(logTabClogListener);

        getAAID();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Clog.v(Constants.BASE_LOG_TAG, "App paused");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (logDialog != null)
            logDialog.dismiss();
        if (progressDialog != null)
            progressDialog.dismiss();
        Clog.unregisterListener(logTabClogListener);
        Clog.v(Constants.BASE_LOG_TAG, "App destroyed");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Clog.v(Constants.BASE_LOG_TAG, "App resumed");
//        checkToUploadLogFile();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.demo_main, menu);
        return false;
    }

    private void initTabHost() {
        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();

        MainActivity.AddTab(this, this.tabHost,
                this.tabHost.newTabSpec(SETTINGS_ID).setIndicator(SETTINGS_ID));
        MainActivity.AddTab(this, this.tabHost,
                this.tabHost.newTabSpec(PREVIEW_ID).setIndicator(PREVIEW_ID));
        MainActivity.AddTab(this, this.tabHost,
                this.tabHost.newTabSpec(DEBUG_ID).setIndicator(DEBUG_ID));
    }

    private void initViewPager() {

        List<Fragment> fragments = new Vector<Fragment>();
        fragments.add(Fragment.instantiate(this,
                SettingsFragment.class.getName()));
        fragments.add(Fragment.instantiate(this,
                PreviewFragment.class.getName()));
        fragments.add(Fragment.instantiate(this,
                DebugFragment.class.getName()));

        this.pagerAdapter = new PagerAdapter(
                super.getSupportFragmentManager(), fragments);

        this.viewPager = (ViewPager) findViewById(R.id.viewpager);
        this.viewPager.setAdapter(this.pagerAdapter);
        this.viewPager.setOnPageChangeListener(this);
        this.viewPager.setOffscreenPageLimit(NUM_TABS);
    }

    private static void AddTab(MainActivity activity, TabHost tabHost,
                               TabHost.TabSpec tabSpec) {
        // Attach a Tab view factory to the spec
        tabSpec.setContent(activity.new TabFactory(activity));
        tabHost.addTab(tabSpec);
    }

    class TabFactory implements TabContentFactory {

        private final Context mContext;

        public TabFactory(Context context) {
            mContext = context;
        }

        public View createTabContent(String tag) {
            View v = new View(mContext);

            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }

    }

    /**
     * ViewPager listener
     */

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int arg0) {
        Clog.v(Constants.BASE_LOG_TAG, "page selected: " + arg0);
        this.tabHost.setCurrentTab(arg0);

        DebugFragment debugFrag = (DebugFragment) pagerAdapter.getItem(TABS.DEBUG.ordinal());
        if (debugFrag != null)
            debugFrag.refresh();
        else
            Clog.e(Constants.BASE_LOG_TAG, "DebugFragment object was null");

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(tabHost.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * TabHost listener
     */

    @Override
    public void onTabChanged(String tabId) {
        int pos = this.tabHost.getCurrentTab();
        this.viewPager.setCurrentItem(pos);
    }

    /**
     * Special handling for our "native" log button
     */

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        // don't handle specially if log button is not showing
        if (btnLog.getVisibility() == View.GONE)
            return super.dispatchTouchEvent(motionEvent);

        float x = motionEvent.getRawX() - contentView.getLeft();
        float y = motionEvent.getRawY() - contentView.getTop();

        // if the user presses btnMore, don't handle specially, btnMore will handle it
        if ((btnMore.getTop() < y) && (btnMore.getBottom() > y) &&
                (btnMore.getLeft() < x) && (x < btnMore.getRight())) {
            return super.dispatchTouchEvent(motionEvent);
        }

        // if the user presses outside the bounds of btnLog, "close it"
        if (y < (btnLog.getTop()) || (btnLog.getBottom() < y) ||
                (x < btnLog.getLeft()) || (btnLog.getRight() < x)) {
            btnLog.setVisibility(View.GONE);
        }

        return super.dispatchTouchEvent(motionEvent);
    }

    @Override
    public void onLoadAdClicked() {
        PreviewFragment previewFrag = (PreviewFragment) pagerAdapter.getItem(TABS.PREVIEW.ordinal());
        if (previewFrag != null)
            previewFrag.loadNewAd();
        else
            Clog.e(Constants.BASE_LOG_TAG, "PreviewFragment object was null");
    }

    /**
     * ClogListener for log screen
     */

    final ClogListener logTabClogListener = new ClogListener() {

        @Override
        public void onReceiveMessage(LOG_LEVEL level, String LogTag, String message) {
            writeToFile(buildBasicLogMessage(level, LogTag, message));
        }


        @Override
        public void onReceiveMessage(LOG_LEVEL level, String LogTag, String message, Throwable tr) {
            String messageWithTr = buildBasicLogMessage(level, LogTag, message);
            StringBuilder trSb = new StringBuilder();
            StackTraceElement[] trElements = tr.getStackTrace();

            for (StackTraceElement e : trElements) {
                trSb.append(e.toString()).append("\n");
            }

            messageWithTr = messageWithTr + trSb.toString();
            writeToFile(messageWithTr);
        }

        @Override
        public LOG_LEVEL getLogLevel() {
            return LOG_LEVEL.V;
        }

        private String buildBasicLogMessage(LOG_LEVEL level, String LogTag, String message) {
            int pid = android.os.Process.myPid();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS:");
            String dateString = formatter.format(new Date());

            String messageFormat = "%s    (%d) %s/%s    %s";

            return String.format(messageFormat, dateString, pid, level, LogTag, message);
        }
    };

    /**
     * Android Advertising ID
     */

    private void getAAID() {
        AndroidAdvertisingIDUtil util = new AndroidAdvertisingIDUtil() {
            @Override
            public void onRetrievedID(String androidAdvertisingID, boolean isLimitAdTrackingEnabled) {
                Clog.d(Constants.BASE_LOG_TAG, "Setting aaid: " + androidAdvertisingID + " " + isLimitAdTrackingEnabled);
                Settings.setAAID(isLimitAdTrackingEnabled ? null : androidAdvertisingID);
            }

            @Override
            public void onFailedToRetrieveID() {
                Clog.d(Constants.BASE_LOG_TAG, "Failed to retrieve aaid");
                Settings.setAAID(null);
            }
        };
        util.getID(this);
    }
    /**
     * Log file management code
     */

    synchronized public void clearLogFile() {
        PrintWriter out = null;
        try {
            out = new PrintWriter(openFileOutput(Constants.LOG_FILENAME, Context.MODE_PRIVATE));
            out.write("");
            Clog.d(Constants.BASE_LOG_TAG, "Log file cleared");
        } catch (IOException e) {
            Clog.e(Constants.BASE_LOG_TAG, "IOException when clearing log file", e);
        } finally {
            if (out != null)
                out.close();
        }
    }

    synchronized public void writeToFile(String message) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(openFileOutput(Constants.LOG_FILENAME, Context.MODE_APPEND));
            out.println(message);
        } catch (IOException e) {
            Log.e(Constants.BASE_LOG_TAG, "IOException when writing to log file", e);
        } finally {
            if (out != null)
                out.close();
        }
    }

    @SuppressLint("NewApi")
    synchronized public void readFromFile() {
        Clog.d(Constants.BASE_LOG_TAG, "Reading log file");

        ReadLogFileTask task = new ReadLogFileTask();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            task.execute();
        }

    }

    /**
     * Upload log file to server every 24 hours
     */

    private void checkToUploadLogFile() {
        long lastLogUploadTime = Prefs.getLastLogUpload(getBaseContext());
        long currentTime = System.currentTimeMillis();
        long oneDayInMillis = 86400000;
        if (currentTime - lastLogUploadTime > oneDayInMillis) {
            Clog.d(Constants.BASE_LOG_TAG, "Last log upload was more than a day ago. Check wifi");

            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
                Clog.d(Constants.BASE_LOG_TAG, "Wifi is available. Upload log file.");

                //TODO: implement file upload

                boolean fileUploadSuccessful = false;

                if (fileUploadSuccessful) {
                    Prefs prefs = new Prefs(getBaseContext());
                    prefs.writeLong(Prefs.KEY_LAST_LOG_UPLOAD, System.currentTimeMillis());
                    prefs.applyChanges();

                    clearLogFile();
                }
            }
        }
    }

    public void showLogDialog() {
        if (!isShowingLogs) {
            progressDialog = ProgressDialog.show(MainActivity.this, null, "Loading logs", true, false);
            readFromFile();
            isShowingLogs = true;
        }
    }

    /**
     * Read logs asynchronously
     */

    private class ReadLogFileTask extends AsyncTask<Void, Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            int count = 0;
            ArrayList<String> logs = new ArrayList<String>(Constants.LOG_MAX_LINES);

            BufferedReader in = null;

            try {
                in = new BufferedReader(new FileReader(getFilesDir() + "/" + Constants.LOG_FILENAME));

                String storedMessages;

                while ((count < Constants.LOG_MAX_LINES)
                        && ((storedMessages = in.readLine()) != null)) {
                    logs.add(storedMessages + "\n");
                    count++;
                }
            } catch (IOException e) {
                Clog.e(Constants.BASE_LOG_TAG, "IOException when reading from log file", e);
            } finally {
                if (in != null)
                    try {
                        in.close();
                    } catch (IOException e) {
                        Clog.e(Constants.BASE_LOG_TAG, "IOException when closing log file input stream", e);
                    }
            }
            return logs;
        }

        /**
         * create the log dialog to display the logs
         */

        @Override
        protected void onPostExecute(ArrayList<String> logs) {
            super.onPostExecute(logs);
            final StringBuilder sb = new StringBuilder();

            RelativeLayout dialogLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.dialog_log, null);
            LinearLayout frame = (LinearLayout) dialogLayout.findViewById(R.id.frame);
            for (int i = logs.size() - 1;
                 (sb.length() < Constants.LOG_MAX_CHAR) && (i > -1); i--) {
                String s = logs.get(i);
                sb.append(s);

                TextView tv = new TextView(dialogLayout.getContext());
                tv.setText(s);
                tv.setTextSize(11);

                if(s.contains(") D/")){
                    tv.setTextColor(Color.BLUE);
                }else if(s.contains(") E/")){
                    tv.setTextColor(Color.RED);
                }else if(s.contains(") W/")){
                    tv.setTextColor(Color.parseColor("#FFA824")); //Dark yellow so your eyes don't fall out
                }else if(s.contains(") I/")){
                    tv.setTextColor(Color.GREEN);
                }

                frame.addView(tv);

            }
            Button btnEmailLogs = (Button) dialogLayout.findViewById(R.id.log_btn_email);
            btnEmailLogs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        emailIntent.setType("message/rfc822");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());

                        startActivity(Intent.createChooser(emailIntent, "Select an app with which to send the debug information"));
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(MainActivity.this, "No E-Mail App Installed!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            if (progressDialog != null)
                progressDialog.cancel();

            logDialog = new AlertDialog.Builder(MainActivity.this)
                    .setView(dialogLayout)
                    .setOnCancelListener(logOnCancel)
                    .show();

            // make it fullscreen
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(logDialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            logDialog.getWindow().setAttributes(lp);
        }
    }

    final DialogInterface.OnCancelListener logOnCancel = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialogInterface) {
            isShowingLogs = false;
            //TODO: remove this
            clearLogFile();
        }
    };
}
