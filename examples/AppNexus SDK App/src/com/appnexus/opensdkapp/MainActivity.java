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

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import com.appnexus.opensdk.utils.Clog;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class MainActivity extends FragmentActivity implements
        TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {

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
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private View btnMore;
    private View btnLog;
    private View contentView;

    private DebugFragment debugFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                Log.d(Constants.LOG_TAG, view.toString());
                Log.d(Constants.LOG_TAG, motionEvent.getRawX() + ", " + motionEvent.getRawY());
                Log.d(Constants.LOG_TAG, btnLog.getLeft() + ", " + btnLog.getTop());
                return false;
            }
        });

        // Default to Settings tab
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
            }
        });

        contentView = getWindow().findViewById(Window.ID_ANDROID_CONTENT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.demo_main, menu);
        return false;
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
        fragments.add(Fragment.instantiate(this,
                PreviewFragment.class.getName()));
        debugFrag = (DebugFragment) Fragment.instantiate(this,
                DebugFragment.class.getName());
        fragments.add(debugFrag);

        this.mPagerAdapter = new PagerAdapter(
                super.getSupportFragmentManager(), fragments);

        this.mViewPager = (ViewPager) findViewById(R.id.viewpager);
        this.mViewPager.setAdapter(this.mPagerAdapter);
        this.mViewPager.setOnPageChangeListener(this);

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
        Clog.d(Constants.LOG_TAG, "page selected: " + arg0);
        this.tabHost.setCurrentTab(arg0);

        if (debugFrag != null)
            debugFrag.refresh();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(tabHost.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onTabChanged(String tabId) {
        int pos = this.tabHost.getCurrentTab();
        this.mViewPager.setCurrentItem(pos);
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
}
