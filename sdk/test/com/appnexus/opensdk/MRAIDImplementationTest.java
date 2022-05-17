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

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.appnexus.opensdk.shadows.ShadowAsyncTaskNoExecutor;
import com.appnexus.opensdk.shadows.ShadowSettings;
import com.appnexus.opensdk.shadows.ShadowWebSettings;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowWebView;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@Config(sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class,
                ShadowWebView.class, ShadowWebSettings.class, ShadowSettings.class, ShadowLog.class})
@RunWith(RobolectricTestRunner.class)
public class MRAIDImplementationTest extends BaseViewAdTest {
    MRAIDImplementation implementation;
    MockAdWebView mockAdWebView;

    @Override
    public void setup() {
        super.setup();
        mockAdWebView = new MockAdWebView(bannerAdView);
        implementation = new MRAIDImplementation(mockAdWebView);
        implementation.supportsPictureAPI = true;
        implementation.supportsCalendar = true;
    }

    @Test
    public void testInitialization() {
        assertEquals(implementation.owner, mockAdWebView);
    }

    @Test
    public void testANJAMDispatchAppEvent() {
        String eventName = "testEvent";
        String eventData = "testData";
        String anjamCall = String.format("anjam://DispatchAppEvent?event=%s&data=%s", eventName, eventData);
        ANJAMImplementation.handleUrl(mockAdWebView, anjamCall);

        MockAdDispatcher mockAdDispatcher = (MockAdDispatcher) mockAdWebView.adView.getAdDispatcher();
        System.out.println("Verifying app event callback");
        assertTrue(mockAdDispatcher.appEventOccurred);
        System.out.println("App event received!");

        System.out.println("Validating event name");
        assertEquals(mockAdDispatcher.eventName, eventName);
        System.out.println("event name validated!");

        System.out.println("Validating event data");
        assertEquals(mockAdDispatcher.eventData, eventData);
        System.out.println("event data validated!");
    }


    @Test
    public void testMRAIDOpenSuccessSDKBrowser() {
        String uri = "http://www.appnexus.com";
        String mraidCall = String.format("mraid://open?uri=%s", uri);
        //Default ANClickThroughAction is ANClickThroughAction.OPEN_SDK_BROWSER
        implementation.dispatch_mraid_call(mraidCall, true);
        assertEquals(mockAdWebView.testString, uri);

        MockAdDispatcher mockAdDispatcher = (MockAdDispatcher) mockAdWebView.adView.getAdDispatcher();
        assertEquals(ANClickThroughAction.OPEN_SDK_BROWSER, mockAdWebView.adView.getClickThroughAction());
        assertTrue(mockAdDispatcher.adClicked);
        assertFalse(mockAdDispatcher.adClickedWithUrl);
    }

    @Test
    public void testMRAIDOpenSuccessDeviceBrowser() {
        String uri = "http://www.appnexus.com";
        String mraidCall = String.format("mraid://open?uri=%s", uri);
        mockAdWebView.adView.setClickThroughAction(ANClickThroughAction.OPEN_DEVICE_BROWSER);
        implementation.dispatch_mraid_call(mraidCall, true);
        assertEquals(mockAdWebView.testString, uri);

        MockAdDispatcher mockAdDispatcher = (MockAdDispatcher) mockAdWebView.adView.getAdDispatcher();
        assertEquals(ANClickThroughAction.OPEN_DEVICE_BROWSER, mockAdWebView.adView.getClickThroughAction());
        assertTrue(mockAdDispatcher.adClicked);
        assertFalse(mockAdDispatcher.adClickedWithUrl);
    }

    @Test
    public void testMRAIDOpenSuccessReturnUrl() {
        String uri = "http://www.appnexus.com";
        String mraidCall = String.format("mraid://open?uri=%s", uri);
        mockAdWebView.adView.setClickThroughAction(ANClickThroughAction.RETURN_URL);
        implementation.dispatch_mraid_call(mraidCall, true);
        assertEquals(mockAdWebView.testString, uri);
        MockAdDispatcher mockAdDispatcher = (MockAdDispatcher) mockAdWebView.adView.getAdDispatcher();
        assertEquals(ANClickThroughAction.RETURN_URL, mockAdWebView.adView.getClickThroughAction());
        assertFalse(mockAdDispatcher.adClicked);
        assertTrue(mockAdDispatcher.adClickedWithUrl);
    }

    @Test
    public void testMRAIDOpenFailure() {
        String uri = "";
        String mraidCall = String.format("mraid://open?uri=%s", uri);
        implementation.dispatch_mraid_call(mraidCall, true);
        assertEquals(mockAdWebView.testString, "default");

        MockAdDispatcher mockAdDispatcher = (MockAdDispatcher) mockAdWebView.adView.getAdDispatcher();
        assertFalse(mockAdDispatcher.adClicked);
        assertFalse(mockAdDispatcher.adClickedWithUrl);
    }

    @Test
    public void testMRAIDExpandSuccess() {
        int width = -1;
        int height = -1;
        String useCustomClose = "false";
        String allowOrientationChange = "true";
        String forceOrientation = "none";

        String mraidCall = String.format("mraid://expand?w=%d&h=%d&useCustomClose=%s&allow_orientation_change=%s&force_orientation=%s",
                width, height, useCustomClose, allowOrientationChange, forceOrientation);
        implementation.dispatch_mraid_call(mraidCall, true);

        assertEquals(mockAdWebView.width, width);
        assertEquals(mockAdWebView.height, height);
        assertEquals(mockAdWebView.customClose, false);
        assertEquals(mockAdWebView.allowOrientationChange, true);
        assertEquals(mockAdWebView.orientation, AdActivity.OrientationEnum.none);

        assertTrue(implementation.expanded);

        MockAdDispatcher mockAdDispatcher = (MockAdDispatcher) mockAdWebView.adView.getAdDispatcher();
        assertTrue(mockAdDispatcher.adExpanded);
    }

    @Test
    public void testMRAIDResizeSuccess() {
        // set to -1 to get around screen size check
        int width = -1;
        int height = -1;
        int offsetX = 5;
        int offsetY = 5;
        String customClosePosition = "top-left";
        String allowOffscreen = "true";

        String mraidCall = String.format("mraid://resize?w=%d&h=%d&offset_x=%d&offset_y=%d&custom_close_position=%s&allow_offscreen=%s",
                width, height, offsetX, offsetY, customClosePosition, allowOffscreen);

        implementation.dispatch_mraid_call(mraidCall, true);

        assertEquals(mockAdWebView.width, width);
        assertEquals(mockAdWebView.height, height);
        assertEquals(mockAdWebView.offsetX, offsetX);
        assertEquals(mockAdWebView.offsetY, offsetY);
        assertEquals(mockAdWebView.customClosePosition, MRAIDImplementation.CUSTOM_CLOSE_POSITION.top_left);
        assertEquals(mockAdWebView.allowOffscreen, true);

        assertTrue(implementation.resized);

        MockAdDispatcher mockAdDispatcher = (MockAdDispatcher) mockAdWebView.adView.getAdDispatcher();
        assertTrue(mockAdDispatcher.adClicked);
    }


    @Test
    public void testMRAIDCloseToHidden() {
        String mraidCall = "mraid://close";
        implementation.dispatch_mraid_call(mraidCall, true);

        assertTrue(mockAdWebView.hidden);
    }

    // storePicture: creates popup dialog, cannot test

    @Test
    public void testMRAIDPlayVideoSuccess() {
        // Robolectric framework overrides intent errors for data type, so any uri will do
        String uri = "http://www.appnexus.com";
        String mraidCall = String.format("mraid://playVideo?uri=%s", uri);
        implementation.dispatch_mraid_call(mraidCall, true);

        MockAdDispatcher mockAdDispatcher = (MockAdDispatcher) mockAdWebView.adView.getAdDispatcher();
        assertTrue(mockAdDispatcher.adClicked);
    }

    @Test
    public void testMRAIDPlayVideoFail() {
        String uri = "";
        String mraidCall = String.format("mraid://playVideo?uri=%s", uri);
        implementation.dispatch_mraid_call(mraidCall, true);

        MockAdDispatcher mockAdDispatcher = (MockAdDispatcher) mockAdWebView.adView.getAdDispatcher();
        assertFalse(mockAdDispatcher.adClicked);
    }

    @Test
    public void testMRAIDCreateCalendarEventSuccess() {
        String event = "{\"id\":1}";
        String mraidCall = String.format("mraid://createCalendarEvent?p=%s", event);
        implementation.dispatch_mraid_call(mraidCall, true);

        MockAdDispatcher mockAdDispatcher = (MockAdDispatcher) mockAdWebView.adView.getAdDispatcher();
        assertTrue(mockAdDispatcher.adClicked);
    }

    @Test
    public void testMRAIDCreateCalendarEventFailure() {
        String event = "";
        String mraidCall = String.format("mraid://createCalendarEvent?p=%s", event);
        implementation.dispatch_mraid_call(mraidCall, true);

        MockAdDispatcher mockAdDispatcher = (MockAdDispatcher) mockAdWebView.adView.getAdDispatcher();
        assertFalse(mockAdDispatcher.adClicked);
    }

    @Test
    public void testMRAIDSetOrientationPropertiesSuccess() {
        String allowOrientationChange = "true";
        String forceOrientation = "none";
        String mraidCall = String.format("mraid://setOrientationProperties?allow_orientation_change=%s&force_orientation=%s",
                allowOrientationChange, forceOrientation);

        implementation.expanded = true;
        implementation.dispatch_mraid_call(mraidCall, true);
    }

    @Test
    public void testMRAIDAudioVolumeChangeEventOnScreen() {
        assertEquals("default", mockAdWebView.testString);
        implementation.webViewFinishedLoading(mockAdWebView, mockAdWebView.initialMraidStateString);
        implementation.isViewable = true;
        String mraidCall = String.format("mraid://audioVolumeChange");
        implementation.dispatch_mraid_call(mraidCall, true);
        assertEquals("javascript:window.mraid.util.audioVolumeChangeEvent({\"volumePercentage\":0.0})", mockAdWebView.testString);
    }

    static class MockAdWebView extends AdWebView {
        String testString = "default";
        int width, height, offsetX, offsetY;
        boolean customClose, allowOrientationChange, allowOffscreen;
        AdActivity.OrientationEnum orientation;
        MRAIDImplementation.CUSTOM_CLOSE_POSITION customClosePosition;
        boolean hidden;

        public MockAdWebView(AdView owner) {
            super(new MockBannerAdView(owner.getContext()), null);
            this.setLayoutParams(new FrameLayout.LayoutParams(0, 0));
        }

        @Override
        void expand(int w, int h, boolean cust_close, MRAIDImplementation caller, boolean allowOrientationChange, AdActivity.OrientationEnum forceOrientation) {
            width = w;
            height = h;
            customClose = cust_close;
            this.allowOrientationChange = allowOrientationChange;
            orientation = forceOrientation;
        }

        @Override
        void hide() {
            hidden = true;
        }

        @Override
        void close() {
            super.close();
        }

        @Override
        public View getView() {
            return super.getView();
        }

        @Override
        public boolean failed() {
            return super.failed();
        }

        @Override
        public void resize(int w, int h, int offset_x, int offset_y, MRAIDImplementation.CUSTOM_CLOSE_POSITION custom_close_position, boolean allow_offscreen) {
            width = w;
            height = h;
            offsetX = offset_x;
            offsetY = offset_y;
            customClosePosition = custom_close_position;
            allowOffscreen = allow_offscreen;
        }

        @Override
        void handleClickUrl(String url) {
            testString = url;
            if (adView.getClickThroughAction() == ANClickThroughAction.RETURN_URL)
                fireAdClickedWithReturnUrl(null);
            else {
                fireAdClicked();
            }
        }

        @Override
        protected boolean loadURLInCorrectBrowser(String url) {
            testString = url;
            return true;
        }

        @Override
        protected void onWindowVisibilityChanged(int visibility) {
            super.onWindowVisibilityChanged(visibility);
        }

        @Override
        public void loadUrl(String url) {
            if (!url.startsWith("javascript"))
                testString = url;
        }

        @Override
        public void evaluateJavascript(String script, @Nullable ValueCallback<String> resultCallback) {
            super.evaluateJavascript(script, resultCallback);
            testString = script;
        }
    }

    static class MockBannerAdView extends BannerAdView {
        MockAdDispatcher adDispatcher;

        public MockBannerAdView(Context context) {
            super(context);
            adDispatcher = new MockAdDispatcher();
        }

        @Override
        public AdDispatcher getAdDispatcher() {
            return adDispatcher;
        }
    }

    static class MockAdDispatcher implements AdDispatcher {
        boolean adLoaded, adFailed, adExpanded, adCollapsed, adClicked, appEventOccurred, adClickedWithUrl, adImpression;

        String eventName, eventData;
        private ANAdResponseInfo mAdResponseInfo;

        @Override
        public void onAdLoaded(AdResponse ad) {
            adLoaded = true;
        }

        @Override
        public void onAdLoaded() {

        }

        @Override
        public void onAdFailed(ResultCode errorCode, ANAdResponseInfo adResponseInfo) {
//            setAdResponseInfo(adResponseInfo);
            adFailed = true;
        }

        @Override
        public void onAdExpanded() {
            adExpanded = true;
        }

        @Override
        public void onAdCollapsed() {
            adCollapsed = true;
        }

        @Override
        public void onAdClicked() {
            adClicked = true;
        }

        @Override
        public void onAppEvent(String name, String data) {
            appEventOccurred = true;
            eventName = name;
            eventData = data;
        }

        @Override
        public void toggleAutoRefresh() {
            //@FIXME
        }

        @Override
        public void onLazyAdLoaded(ANAdResponseInfo adResponseInfo) {

        }

        @Override
        public void onAdClicked(String clickUrl) {
            adClickedWithUrl = true;
        }

        @Override
        public void onAdImpression() {
            adImpression = true;
        }
    }
}
