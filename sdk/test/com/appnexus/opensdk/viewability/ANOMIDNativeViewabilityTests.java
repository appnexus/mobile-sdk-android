/*
 *    Copyright 2019 APPNEXUS INC
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
package com.appnexus.opensdk.viewability;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.appnexus.opensdk.BaseNativeTest;
import com.appnexus.opensdk.NativeAdSDK;
import com.appnexus.opensdk.SDKSettings;
import com.appnexus.opensdk.TestResponsesUT;
import com.appnexus.opensdk.XandrAd;
import com.appnexus.opensdk.mocks.MockDefaultExecutorSupplier;
import com.appnexus.opensdk.shadows.ShadowAsyncTaskNoExecutor;
import com.appnexus.opensdk.shadows.ShadowOMIDNativeWebView;
import com.appnexus.opensdk.shadows.ShadowSettings;
import com.appnexus.opensdk.util.Lock;
import com.appnexus.opensdk.utils.Settings;
import com.iab.omid.library.appnexus.adsession.VerificationScriptResource;
import com.squareup.okhttp.mockwebserver.MockResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertTrue;

@Config(sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class,
                  ShadowLog.class, ShadowSettings.class, ShadowLog.class, ShadowOMIDNativeWebView.class})
@RunWith(RobolectricTestRunner.class)
public class ANOMIDNativeViewabilityTests extends BaseNativeTest {


    LinearLayout dummyNativeView;

    public void assertAdLoaded(Boolean loaded) {
        assertTrue(adLoaded || adFailed);
        assertTrue(loaded | !adFailed);
        assertTrue(loaded | adLoaded);
    }

    public void assertAdFailed(Boolean loadFailed) {
        assertTrue(adLoaded || adFailed);
        assertTrue(loadFailed | !adLoaded);
        assertTrue(loadFailed | adFailed);
    }

    @Override
    public void setup() {
        super.setup();
        Settings.getSettings().ua = "";
        ShadowOMIDNativeWebView.omidFinishSession = "";
        ShadowOMIDNativeWebView.omidImpressionString = "";
        ShadowOMIDNativeWebView.omidInitString = "";
        ShadowOMIDNativeWebView.omidStartSession = "";
    }

    private void assertVerificationScriptResourceRTB() {
        ANOmidAdSession anOmidAdSession = getOMIDAdSession();
        VerificationScriptResource verificationScriptResource = anOmidAdSession.verificationScriptResources.get(0);
        assertTrue(verificationScriptResource.getResourceUrl().toString().equalsIgnoreCase("https://acdn.adnxs.com/mobile/omsdk/test/omid-validation-verification-script-1.2.5.js"));
        assertTrue(verificationScriptResource.getVendorKey().equalsIgnoreCase("dummyVendor"));
        assertTrue(verificationScriptResource.getVerificationParameters().equalsIgnoreCase("v;vk=dummyVendor;tv=cet=0;cecb="));
    }

    private void assertVerificationScriptResourceNativeRenderer() {
        ANOmidAdSession anOmidAdSession = getOMIDAdSession();
        VerificationScriptResource verificationScriptResource = anOmidAdSession.verificationScriptResources.get(0);
        assertTrue(verificationScriptResource.getResourceUrl().toString().equalsIgnoreCase("https://acdn.adnxs.com/mobile/omsdk/test/omid-validation-verification-script-1.2.5.js"));
        assertTrue(verificationScriptResource.getVendorKey().equalsIgnoreCase("dummyVendorRenderer"));
        assertTrue(verificationScriptResource.getVerificationParameters().equalsIgnoreCase("v;vk=dummyVendorRenderer;tv=cet=0;cecb="));
    }


    private void assertVerificationScriptResourceCSM() {
        ANOmidAdSession anOmidAdSession = getOMIDAdSession();
        VerificationScriptResource verificationScriptResource = anOmidAdSession.verificationScriptResources.get(0);
        assertTrue(verificationScriptResource.getResourceUrl().toString().equalsIgnoreCase("https://acdn.adnxs.com/mobile/omsdk/test/omid-validation-verification-script-1.2.5.js"));
        assertTrue(verificationScriptResource.getVendorKey().equalsIgnoreCase("dummyVendorCSM"));
        assertTrue(verificationScriptResource.getVerificationParameters().equalsIgnoreCase("v;vk=dummyVendorCSM;tv=cet=0;cecb="));
    }

    //This verifies that the AsyncTask for Request is being executed on the Correct Executor.
    @Test
    public void testRequestExecutorForBackgroundTasks() {
        SDKSettings.setExternalExecutor(MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
        assertNotSame(ShadowAsyncTaskNoExecutor.getExecutor(), MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
        adRequest.loadAd();
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertEquals(ShadowAsyncTaskNoExecutor.getExecutor(), MockDefaultExecutorSupplier.getInstance().forBackgroundTasks());
    }

    @Test
    public void testOmidNativeJSEventsRTB() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.anOMIDNativeRTB()));
        adRequest.loadAd();
        Lock.pause(1000);
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.getForegroundThreadScheduler().runOneTask();
        assertAdLoaded(true);
        assertVerificationScriptResourceRTB();
        assertANOMIDAdSessionPresent();
        attachNativeAdToViewAndRegisterTracking();
        assertOMIDSessionStartRTB();
        NativeAdSDK.unRegisterTracking(dummyNativeView);
        Lock.pause(1000);
        waitForTasks();
        Robolectric.getForegroundThreadScheduler().advanceToLastPostedRunnable();
        assertOMIDSessionFinish();
    }


    @Test
    public void testOmidNativeRendererJSEvents() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.anNativeRenderer()));
        adRequest.loadAd();
        Lock.pause(1000);
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.getForegroundThreadScheduler().runOneTask();
        assertAdLoaded(true);
        assertVerificationScriptResourceNativeRenderer();
        assertANOMIDAdSessionPresent();
        attachNativeAdToViewAndRegisterTracking();
        assertOMIDSessionStartRenderer();
        NativeAdSDK.unRegisterTracking(dummyNativeView);
        Lock.pause(1000);
        waitForTasks();
        Robolectric.getForegroundThreadScheduler().advanceToLastPostedRunnable();
        assertOMIDSessionFinish();
    }



    @Test
    public void testOmidNativeJSEventsCSM() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.mediatedSuccessfulNative()));
        adRequest.loadAd();
        Lock.pause(1000);
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        waitForTasks();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.getForegroundThreadScheduler().runOneTask();
        assertAdLoaded(true);
        assertVerificationScriptResourceCSM();
        assertANOMIDAdSessionPresent();
        attachNativeAdToViewAndRegisterTracking();
        assertOMIDSessionStartCSM();
        NativeAdSDK.unRegisterTracking(dummyNativeView);
        Lock.pause(1000);
        waitForTasks();
        Robolectric.getForegroundThreadScheduler().advanceToLastPostedRunnable();
        assertOMIDSessionFinish();
    }



    private void assertANOMIDAdSessionPresent() {
        assertTrue(getOMIDAdSession()!=null);
    }

    private void assertOMIDSessionStartRTB(){
        assertTrue(ShadowOMIDNativeWebView.omidInitString.contains("if(window.omidBridge!==undefined){omidBridge.init({\"impressionOwner\":\"native\",\"mediaEventsOwner\":\"none\",\"creativeType\":\"nativeDisplay\",\"impressionType\":\"viewable\",\"isolateVerificationScripts\":false})}"));
        assertTrue(ShadowOMIDNativeWebView.omidStartSession.contains("{\"dummyVendor\":\"v;vk=dummyVendor;tv=cet=0;cecb=\"}"));
    }

    private void assertOMIDSessionStartRenderer(){
        assertTrue(ShadowOMIDNativeWebView.omidInitString.contains("if(window.omidBridge!==undefined){omidBridge.init({\"impressionOwner\":\"native\",\"mediaEventsOwner\":\"none\",\"creativeType\":\"nativeDisplay\",\"impressionType\":\"viewable\",\"isolateVerificationScripts\":false})}"));
        assertTrue(ShadowOMIDNativeWebView.omidStartSession.contains("{\"dummyVendorRenderer\":\"v;vk=dummyVendorRenderer;tv=cet=0;cecb=\"}"));
    }

    private void assertOMIDSessionStartCSM(){
        assertTrue(ShadowOMIDNativeWebView.omidInitString.contains("if(window.omidBridge!==undefined){omidBridge.init({\"impressionOwner\":\"native\",\"mediaEventsOwner\":\"none\",\"creativeType\":\"nativeDisplay\",\"impressionType\":\"viewable\",\"isolateVerificationScripts\":false})}"));
        assertTrue(ShadowOMIDNativeWebView.omidStartSession.contains("{\"dummyVendorCSM\":\"v;vk=dummyVendorCSM;tv=cet=0;cecb=\"}"));
    }

    private void assertOMIDSessionFinish() {
        assertTrue(ShadowOMIDNativeWebView.omidFinishSession.contains("omidBridge.finishSession()"));
    }

    private void attachNativeAdToViewAndRegisterTracking() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(320, 50);

        dummyNativeView = new LinearLayout(activity);
        dummyNativeView.setLayoutParams(layoutParams);
        dummyNativeView.setVisibility(View.VISIBLE);

        final ViewGroup viewGroup = ((ViewGroup) activity.getWindow().getDecorView().getRootView());
        viewGroup.addView(dummyNativeView);

        NativeAdSDK.registerTracking(response,dummyNativeView,this);
        Lock.pause(1000);
        waitForTasks();
        Robolectric.getForegroundThreadScheduler().advanceToLastPostedRunnable();
    }


    @Override
    public void tearDown() {
        super.tearDown();
        if (response != null) {
            response.destroy();
        }
        dummyNativeView = null;
    }


}
