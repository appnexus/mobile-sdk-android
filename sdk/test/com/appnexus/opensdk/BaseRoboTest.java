package com.appnexus.opensdk;

import android.app.Activity;

import com.appnexus.opensdk.shadows.ShadowAsyncTaskNoExecutor;
import com.appnexus.opensdk.shadows.ShadowSettings;
import com.appnexus.opensdk.ut.UTConstants;
import com.appnexus.opensdk.util.Lock;
import com.appnexus.opensdk.util.MockMainActivity;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import org.junit.After;
import org.junit.Before;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.util.Scheduler;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static android.os.Looper.getMainLooper;
import static org.robolectric.Shadows.shadowOf;

public class BaseRoboTest {
    public static final int placementID = 1;
    public static final int width = 320;
    public static final int height = 50;
    protected Activity activity;
    Scheduler uiScheduler, bgScheduler;
    public MockWebServer server;

    @Before
    public void setup() {
        SDKSettings.setExternalExecutor(null);
        Robolectric.getBackgroundThreadScheduler().reset();
        Robolectric.getForegroundThreadScheduler().reset();
        ShadowLog.stream = System.out;
        activity = Robolectric.buildActivity(MockMainActivity.class).create().start().resume().visible().get();
        shadowOf(activity).grantPermissions("android.permission.INTERNET");
        server= new MockWebServer();
        try {
            server.start();
            HttpUrl url= server.url("/");
            UTConstants.REQUEST_BASE_URL_UT = url.toString();
            UTConstants.REQUEST_BASE_URL_SIMPLE = url.toString();
            System.out.println(UTConstants.REQUEST_BASE_URL_UT);
            ShadowSettings.setTestURL(url.toString());
            TestResponsesUT.setTestURL(url.toString());
        } catch (IOException e) {
            System.out.print("IOException");
        }
        bgScheduler = Robolectric.getBackgroundThreadScheduler();
        uiScheduler = Robolectric.getForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        bgScheduler.pause();
        uiScheduler.pause();
    }


    @After
    public void tearDown() {
        UTConstants.REQUEST_BASE_URL_UT = "https://mediation.adnxs.com/ut/v3";
        UTConstants.REQUEST_BASE_URL_SIMPLE = "https://ib.adnxs-simple.com/ut/v3";
        activity.finish();
        System.out.println("REQUEST COUNT"+server.getRequestCount());
        shadowOf(getMainLooper()).quitUnchecked();
        try {
            server.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
        server = null;
        bgScheduler.reset();
        uiScheduler.reset();
    }

    private void scheduleTimerToCheckForTasks() {
        Timer timer = new Timer();
        final int[] counter = {330};
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                counter[0]--;
                if (uiScheduler.areAnyRunnable() || bgScheduler.areAnyRunnable() || counter[0] == 0) {
                    Lock.unpause();
                    this.cancel();
                }
            }
        }, 0, 100);
    }

    public void waitForTasks() {
        scheduleTimerToCheckForTasks();
        Lock.pause();
    }

    public void restartServer() {
        try {
            server.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }

        server= new MockWebServer();
        try {
            server.start();
            HttpUrl url= server.url("/");
            UTConstants.REQUEST_BASE_URL_UT = url.toString();
            UTConstants.REQUEST_BASE_URL_SIMPLE = url.toString();
            System.out.println(UTConstants.REQUEST_BASE_URL_UT);
            ShadowSettings.setTestURL(url.toString());
            TestResponsesUT.setTestURL(url.toString());
        } catch (IOException e) {
            System.out.print("IOException");
        }
    }
}