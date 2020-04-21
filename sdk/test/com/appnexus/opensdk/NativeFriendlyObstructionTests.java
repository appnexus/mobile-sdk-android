/*
 *    Copyright 2020 APPNEXUS INC
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


import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import com.appnexus.opensdk.shadows.ShadowAsyncTaskNoExecutor;
import com.appnexus.opensdk.shadows.ShadowSettings;
import com.appnexus.opensdk.shadows.ShadowWebSettings;
import com.appnexus.opensdk.util.Lock;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.viewability.ANOmidAdSession;
import com.squareup.okhttp.mockwebserver.MockResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowWebView;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@Config(sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class,
                ShadowWebView.class, ShadowWebSettings.class, ShadowSettings.class, ShadowLog.class})
@RunWith(RobolectricTestRunner.class)
public class NativeFriendlyObstructionTests extends BaseNativeTest {

    @Override
    public void setup() {
        super.setup();
        Settings.getSettings().ua = "";
//        NativeAdSDK.count = 0;
    }

//    @Test
//    public void testNativeAddFriendlyObstruction() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
//        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.anNative())); // First queue a regular HTML banner response
//        SDKSettings.setOMEnabled(true);
//        executeNativeRequest();
//        assertFriendlyObstruction(0);
//        View v1 = new View(activity);
//        View v2 = new View(activity);
//        View v3 = new View(activity);
//        List<View> views = new ArrayList<>();
//        views.add(v1);
//        views.add(v2);
//        views.add(v3);
//        Field expired = ANNativeAdResponse.class.getDeclaredField("expired");
//        expired.setAccessible(true);
//        expired.setBoolean(nativeAdResponse, false);
//
//        View nativeView = new View(activity);
//        nativeView.setTag(R.string.native_tag);
//        BaseNativeAdResponse baseNativeAdResponse = (BaseNativeAdResponse) nativeAdResponse;
//        Field anOmidAdSession = BaseNativeAdResponse.class.getDeclaredField("anOmidAdSession");
//        ANOmidAdSession session = (ANOmidAdSession) anOmidAdSession.get(baseNativeAdResponse);
//        ANOmidAdSession omidAdSession = mock(session.getClass());
//        NativeAdSDK.registerTracking(nativeAdResponse, nativeView, null, views);
//        shadowOf(Looper.getMainLooper()).getScheduler().advanceToNextPostedRunnable();
//        verify(omidAdSession, times(3)).addFriendlyObstruction(v1);
//        verify(omidAdSession, times(1)).addFriendlyObstruction(v2);
//        verify(omidAdSession, times(1)).addFriendlyObstruction(v3);
////        assertFriendlyObstruction(3);
//        NativeAdSDK.registerTracking(nativeAdResponse, nativeView, null);
//        shadowOf(Looper.getMainLooper()).getScheduler().advanceToNextPostedRunnable();
//        omidAdSession = mock(ANOmidAdSession.class);
//        verify(omidAdSession, times(0)).addFriendlyObstruction(v1);
//        verify(omidAdSession, times(0)).addFriendlyObstruction(v2);
//        verify(omidAdSession, times(0)).addFriendlyObstruction(v3);
////        assertFriendlyObstruction(0);
//    }

    private void assertFriendlyObstruction(int count) throws NullPointerException {
//        int size = NativeAdSDK.count;
//        assertEquals(count, size);
    }

    private void executeNativeRequest() {
        adRequest.loadAd();
        Lock.pause(1000);
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
    }

    @Override
    public void tearDown() {
        super.tearDown();
        if (response != null) {
            response.destroy();
        }
    }

    private Handler getHandler() {
        final Handler handler = mock(Handler.class);
        when(handler.sendMessageAtTime(any(Message.class), anyLong())).thenAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Message msg = invocation.getArgument(0);
                msg.getCallback().run();
                return null;
            }
        });
        return handler;
    }
}