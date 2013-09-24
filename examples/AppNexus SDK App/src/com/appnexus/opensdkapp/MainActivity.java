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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.*;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.TabHost.TabContentFactory;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.ClogListener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends FragmentActivity implements
        TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener, SettingsFragment.OnLoadAdClickedListener {

    private static final String SETTINGS_ID = "Settings";
    private static final String PREVIEW_ID = "Preview";
    private static final String DEBUG_ID = "Debug";

    public static enum TABS {
        SETTINGS,
        PREVIEW,
        DEBUG
    }

    private TabHost tabHost;
    private HashMap<String, TabInfo> mapTabInfo = new HashMap<String, MainActivity.TabInfo>();
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private View btnMore, btnLog;
    private View contentView;

    private PreviewFragment previewFrag;
    private DebugFragment debugFrag;
    private AlertDialog logDialog, progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(Constants.BASE_LOG_TAG, "App created");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        this.initialiseTabHost(savedInstanceState);
        if (savedInstanceState != null) {
            tabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }
        this.intialiseViewPager();

        tabHost.setOnTabChangedListener(this);

        tabHost.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d(Constants.BASE_LOG_TAG, view.toString());
                Log.d(Constants.BASE_LOG_TAG, motionEvent.getRawX() + ", " + motionEvent.getRawY());
                Log.d(Constants.BASE_LOG_TAG, btnLog.getLeft() + ", " + btnLog.getTop());
                return false;
            }
        });

        // Default to Preview tab
        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                onPageSelected(TABS.PREVIEW.ordinal());
//                MainActivity.this.onTabChanged(PREVIEW_ID);
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(Constants.BASE_LOG_TAG, "App paused");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (logDialog != null)
            logDialog.dismiss();
        Clog.unregisterListener(logTabClogListener);
        Log.v(Constants.BASE_LOG_TAG, "App destroyed");
    }

    @Override
    protected void onResume() {
        super.onResume();
//        checkToUploadLogFile();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.demo_main, menu);
        return false;
    }

    public void showLogDialog() {
        //TODO progress dialog

        progressDialog = ProgressDialog.show(MainActivity.this, null, "Loading logs", true, false);

        readFromFile();

    }

    private void initialiseTabHost(Bundle args) {
        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();
        TabInfo tabInfo = null;

        MainActivity.AddTab(this, this.tabHost,
                this.tabHost.newTabSpec(SETTINGS_ID).setIndicator(SETTINGS_ID),
                (tabInfo = new TabInfo(SETTINGS_ID, SettingsFragment.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        MainActivity.AddTab(this, this.tabHost,
                this.tabHost.newTabSpec(PREVIEW_ID).setIndicator(PREVIEW_ID),
                (tabInfo = new TabInfo(PREVIEW_ID, PreviewFragment.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        MainActivity.AddTab(this, this.tabHost,
                this.tabHost.newTabSpec(DEBUG_ID).setIndicator(DEBUG_ID),
                (tabInfo = new TabInfo(DEBUG_ID, DebugFragment.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
    }

    private void intialiseViewPager() {

        List<Fragment> fragments = new Vector<Fragment>();
        fragments.add(Fragment.instantiate(this,
                SettingsFragment.class.getName()));
        previewFrag = (PreviewFragment) Fragment.instantiate(this,
                PreviewFragment.class.getName());
        fragments.add(previewFrag);
        debugFrag = (DebugFragment) Fragment.instantiate(this,
                DebugFragment.class.getName());
        fragments.add(debugFrag);

        this.pagerAdapter = new PagerAdapter(
                super.getSupportFragmentManager(), fragments);

        this.viewPager = (ViewPager) findViewById(R.id.viewpager);
        this.viewPager.setAdapter(this.pagerAdapter);
        this.viewPager.setOnPageChangeListener(this);

    }

    private static void AddTab(MainActivity activity, TabHost tabHost,
                               TabHost.TabSpec tabSpec, TabInfo tabInfo) {
        // Attach a Tab view factory to the spec
        tabSpec.setContent(activity.new TabFactory(activity));
        tabHost.addTab(tabSpec);
    }

    private class TabInfo {
        private String tag;

        TabInfo(String tag, Class<?> clazz, Bundle args) {
            this.tag = tag;
        }

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

        if (debugFrag != null)
            debugFrag.refresh();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(tabHost.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onTabChanged(String tabId) {
        int pos = this.tabHost.getCurrentTab();
        this.viewPager.setCurrentItem(pos);
    }

    // special handling for our "native" log button
    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
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
//        readFromFile();
        clearLogFile();
        previewFrag.loadNewAd();
    }

    /**
     * for managing the log file
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
        public boolean isVerboseLevelEnabled() {
            return true;
        }

        @Override
        public boolean isDebugLevelEnabled() {
            return true;
        }

        @Override
        public boolean isInfoLevelEnabled() {
            return true;
        }

        @Override
        public boolean isWarningLevelEnabled() {
            return true;
        }

        @Override
        public boolean isErrorLevelEnabled() {
            return true;
        }

        private String buildBasicLogMessage(LOG_LEVEL level, String LogTag, String message) {
            int pid = android.os.Process.myPid();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS:");
            String dateString = formatter.format(new Date());

            String messageFormat = "%s    (%d) %s/%s    %s\n";

            return String.format(messageFormat, dateString, pid, level, LogTag, message);
        }
    };

    synchronized public void clearLogFile() {
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(openFileOutput(Constants.LOG_FILENAME, Context.MODE_PRIVATE));
            out.writeUTF("");
            out.close();
            Clog.d(Constants.BASE_LOG_TAG, "Log file cleared");
        } catch (IOException e) {
            Clog.e(Constants.BASE_LOG_TAG, "IOException when clearing log file", e);
        } finally {
            if (out != null)
                try {
                    out.close();
                } catch (IOException e) {
                    Clog.e(Constants.BASE_LOG_TAG, "IOException when closing log file output stream in clear", e);
                }
        }
    }

    synchronized public void writeToFile(String message) {
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(openFileOutput(Constants.LOG_FILENAME, Context.MODE_APPEND));
            out.writeUTF(message);
            out.close();
        } catch (IOException e) {
            Log.e(Constants.BASE_LOG_TAG, "IOException when writing to log file", e);
        } finally {
            if (out != null)
                try {
                    out.close();
                } catch (IOException e) {
                    Log.e(Constants.BASE_LOG_TAG, "IOException when closing log file output stream", e);
                }
        }
    }

    synchronized public void readFromFile() {
        Clog.d(Constants.BASE_LOG_TAG, "Reading log file");

        AsyncTask<Void, Void, ArrayList<String>> task = new AsyncTask<Void, Void, ArrayList<String>>() {

            @Override
            protected void onPostExecute(ArrayList<String> logs) {
                super.onPostExecute(logs);
                final StringBuilder sb = new StringBuilder();

                for (int i = logs.size() - 1;
                     (sb.length() < Constants.LOG_MAX_CHAR) && (i > 0); i--) {
                    sb.append(logs.get(i));
                }

                RelativeLayout dialogLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.dialog_log, null);
                View frame = dialogLayout.findViewById(R.id.frame);
                TextView txtAppLogs = (TextView) frame.findViewById(R.id.log_txt_applogs);
                txtAppLogs.setText(sb.toString());
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
                        .show();
            }

            @Override
            protected ArrayList<String> doInBackground(Void... voids) {
                int count = 0;
                ArrayList<String> logs = new ArrayList<String>(Constants.LOG_MAX_LINES);

                DataInputStream in = null;

                try {
                    in = new DataInputStream(openFileInput(Constants.LOG_FILENAME));

                    String storedMessages;

                    while ((count < Constants.LOG_MAX_LINES) &&
                            (in.available() > 0)
                            && ((storedMessages = in.readUTF()) != null)) {
                        logs.add(storedMessages);
                        count++;
                    }
                    in.close();
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

        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            task.execute();
        }

    }

    public ClogListener getClogListener() {
        return logTabClogListener;
    }

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

                Prefs prefs = new Prefs(getBaseContext());
                prefs.writeLong(Prefs.KEY_LAST_LOG_UPLOAD, System.currentTimeMillis());
                prefs.applyChanges();
            }
        }
    }
}
