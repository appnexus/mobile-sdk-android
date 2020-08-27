/*
 *    Copyright 2018 APPNEXUS INC
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

import android.content.Context;

import com.appnexus.opensdk.mocks.MockDefaultExecutorSupplier;
import com.appnexus.opensdk.shadows.ShadowAsyncTaskNoExecutor;
import com.appnexus.opensdk.shadows.ShadowCustomWebView;
import com.appnexus.opensdk.shadows.ShadowSettings;
import com.appnexus.opensdk.shadows.ShadowWebSettings;
import com.appnexus.opensdk.ut.adresponse.CSMSDKAdResponse;
import com.appnexus.opensdk.ut.adresponse.CSRAdResponse;
import com.appnexus.opensdk.ut.adresponse.SSMHTMLAdResponse;
import com.appnexus.opensdk.utils.AdvertisingIDUtil;
import com.appnexus.opensdk.utils.Clog;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;

@Config(sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class,
                ShadowCustomWebView.class, ShadowWebSettings.class, ShadowSettings.class, ShadowLog.class})
@RunWith(RobolectricTestRunner.class)
public class ExecutorForBackgroundTasksTests extends BaseViewAdTest {

    String url = "www.example.com";

    @Override
    public void setup() {
        super.setup();
    }

    @Override
    public void tearDown() {
        super.tearDown();
    }

    //This verifies that the AsyncTask for Request is being executed on the Correct Executor.
    @Test
    public void testClickTrackerExecutorForBackgroundTasks() {
        SDKSettings.setExternalExecutor(MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
        assertNotSame(ShadowAsyncTaskNoExecutor.getExecutor(), MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
        new ClickTracker(url).execute();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertEquals(ShadowAsyncTaskNoExecutor.getExecutor(), MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
    }

    @Test
    public void testLoadUrlWithMRAIDExecutorForBackgroundTasks() {
        SDKSettings.setExternalExecutor(MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
        assertNotSame(ShadowAsyncTaskNoExecutor.getExecutor(), MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
        try {
            Class<?> aClass = Class.forName("com.appnexus.opensdk.AdWebView");
            Method met = aClass.getDeclaredMethod("loadUrlWithMRAID", String.class);
            met.setAccessible(true);
            met.invoke(new AdWebView(bannerAdView, new AdViewRequestManager(bannerAdView)), url);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertEquals(ShadowAsyncTaskNoExecutor.getExecutor(), MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
    }

    @Test
    public void testFireTrackerExecutorForBackgroundTasks() {
        SDKSettings.setExternalExecutor(MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
        assertNotSame(ShadowAsyncTaskNoExecutor.getExecutor(), MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
        try {
            Class<?> aClass = Class.forName("com.appnexus.opensdk.RequestManager");
            Method met = aClass.getDeclaredMethod("fireTracker", String.class, String.class);
            met.setAccessible(true);
            met.invoke(new AdViewRequestManager(bannerAdView), url, Clog.getString(R.string.notify_url));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertEquals(ShadowAsyncTaskNoExecutor.getExecutor(), MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
    }

    @Test
    public void testInstantiateNewMediatedSSMAdExecutorForBackgroundTasks() {
        SDKSettings.setExternalExecutor(MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
        assertNotSame(ShadowAsyncTaskNoExecutor.getExecutor(), MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
        try {
            ArrayList<String> stringArrayList = new ArrayList<>();
            stringArrayList.add(url);
            SSMHTMLAdResponse ssmhtmlAdResponse = new SSMHTMLAdResponse(300, 250, "banner", url, stringArrayList, new ANAdResponseInfo());
            Class<?> aClass = Class.forName("com.appnexus.opensdk.MediatedSSMAdViewController");
            Method met = aClass.getDeclaredMethod("instantiateNewMediatedSSMAd", null);
            met.setAccessible(true);
            met.invoke(MediatedSSMAdViewController.create(bannerAdView, new AdViewRequestManager(bannerAdView), ssmhtmlAdResponse));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertEquals(ShadowAsyncTaskNoExecutor.getExecutor(), MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
    }

    @Test
    public void testResponseUrlExecuteExecutorForBackgroundTasks() {
        SDKSettings.setExternalExecutor(MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
        assertNotSame(ShadowAsyncTaskNoExecutor.getExecutor(), MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
        try {
            ResponseUrl.Builder builder = new ResponseUrl.Builder(url, ResultCode.getNewInstance(ResultCode.SUCCESS));
            ResponseUrl responseUrl = builder.build();
            Class<?> aClass = Class.forName("com.appnexus.opensdk.ResponseUrl");
            Method met = aClass.getDeclaredMethod("execute", null);
            met.setAccessible(true);
            met.invoke(responseUrl);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertEquals(ShadowAsyncTaskNoExecutor.getExecutor(), MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
    }

//    @Test
//    public void testSharedNetworkManagerStartTimerExecutorForBackgroundTasks() {
//        SDKSettings.setExternalExecutor(MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
//        assertNotSame(ShadowAsyncTaskNoExecutor.getExecutor(), MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
//        try {
//            SharedNetworkManager sharedNetworkManager = SharedNetworkManager.getInstance(bannerAdView.getContext());
//            Class<?> aClass = Class.forName("com.appnexus.opensdk.SharedNetworkManager");
//            Method met = aClass.getDeclaredMethod("addURL", String.class, Context.class);
//            met.setAccessible(true);
//            met.invoke(sharedNetworkManager, url, bannerAdView.getContext());
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        Robolectric.flushBackgroundThreadScheduler();
//        Robolectric.flushForegroundThreadScheduler();
//        assertEquals(ShadowAsyncTaskNoExecutor.getExecutor(), MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
//    }

    @Test
    public void testAdViewFireImpressionTrackerExecutorForBackgroundTasks() {
        SDKSettings.setExternalExecutor(MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
        assertNotSame(ShadowAsyncTaskNoExecutor.getExecutor(), MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
        try {
            Class<?> aClass = Class.forName("com.appnexus.opensdk.AdView");
            Method met = aClass.getDeclaredMethod("fireImpressionTracker", String.class);
            met.setAccessible(true);
            met.invoke(bannerAdView, url);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertEquals(ShadowAsyncTaskNoExecutor.getExecutor(), MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
    }

    @Test
    public void testMediatedNativeAdControllerFireTrackerExecutorForBackgroundTasks() {
        SDKSettings.setExternalExecutor(MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
        assertNotSame(ShadowAsyncTaskNoExecutor.getExecutor(), MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
        try {
            ArrayList<String> stringArrayList = new ArrayList<>();
            stringArrayList.add(url);
            CSMSDKAdResponse csmsdkAdResponse = new CSMSDKAdResponse(300, 250, "banner", url, stringArrayList, new ANAdResponseInfo(), null);
            MediatedNativeAdController mediatedNativeAdController = MediatedNativeAdController.create(csmsdkAdResponse, new AdViewRequestManager(bannerAdView));
            Class<?> aClass = Class.forName("com.appnexus.opensdk.MediatedNativeAdController");
            Method met = aClass.getDeclaredMethod("fireTracker", String.class);
            met.setAccessible(true);
            met.invoke(mediatedNativeAdController, url);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertEquals(ShadowAsyncTaskNoExecutor.getExecutor(), MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
    }

    @Test
    public void testCSRNativeBannerControllerFireTrackerExecutorForBackgroundTasks() {
        SDKSettings.setExternalExecutor(MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
        assertNotSame(ShadowAsyncTaskNoExecutor.getExecutor(), MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
        try {
            ArrayList<String> stringArrayList = new ArrayList<>();
            stringArrayList.add(url);
            // Create a RTB Banner Ad
            CSRAdResponse csrAdResponse = new CSRAdResponse(300, 250, "banner", url, stringArrayList, new ANAdResponseInfo(), null);
            CSRNativeBannerController csrNativeBannerController = new CSRNativeBannerController(csrAdResponse, new AdViewRequestManager(bannerAdView));
            Class<?> aClass = Class.forName("com.appnexus.opensdk.CSRNativeBannerController");
            Method met = aClass.getDeclaredMethod("fireTracker", String.class);
            met.setAccessible(true);
            met.invoke(csrNativeBannerController, url);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertEquals(ShadowAsyncTaskNoExecutor.getExecutor(), MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
    }

    @Test
    public void testAdvertisingIdUtilRetrieveAndSetAAIDExecutorForBackgroundTasks() {
        SDKSettings.setExternalExecutor(MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
        assertNotSame(ShadowAsyncTaskNoExecutor.getExecutor(), MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
        try {
            Class<?> aClass = Class.forName("com.appnexus.opensdk.utils.AdvertisingIDUtil");
            Method met = aClass.getDeclaredMethod("retrieveAndSetAAID", Context.class);
            met.setAccessible(true);
            met.invoke(new AdvertisingIDUtil(), bannerAdView.getContext());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertEquals(ShadowAsyncTaskNoExecutor.getExecutor(), MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
    }

}
