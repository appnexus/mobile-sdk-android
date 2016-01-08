package com.appnexus.opensdk;

import android.content.Context;

import com.appnexus.opensdk.shadows.ShadowAsyncTaskNoExecutor;
import com.appnexus.opensdk.shadows.ShadowWebSettings;
import com.appnexus.opensdk.utils.ANConstants;
import com.appnexus.opensdk.utils.Settings;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowWebView;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class,
                ShadowWebView.class, ShadowWebSettings.class})
public class UTAdRequestTest extends BaseRoboTest {

        AdFetcher adFetcher;
    public static final int PLACEMENT_ID = 1234;

        @Override
        public void setup() {
                super.setup();
                MockAdOwner owner = new MockAdOwner(activity);
                owner.addCustomKeywords("testkey","testValue");
                owner.setPlacementID(""+PLACEMENT_ID);
                clearAAIDAsyncTasks();
                adFetcher = new AdFetcher(owner);
        }

        private void clearAAIDAsyncTasks() {
                Robolectric.flushBackgroundThreadScheduler();
                Robolectric.flushForegroundThreadScheduler();
        }

        @Test
        public void testUTPostRequest() throws Exception {

            setupMockServer();

            adFetcher.start();
            waitForTasks();
            Robolectric.flushForegroundThreadScheduler();
            Robolectric.flushBackgroundThreadScheduler();

            RecordedRequest recordedRequest = server.takeRequest();
            // verify that request method is POST
            String method = recordedRequest.getMethod();
            assertNotNull(method);
            assertEquals("POST", method.toUpperCase());
            // verify that default keys/values are presented in post data
            assertNotNull(recordedRequest.getBody());
            String requestBody = recordedRequest.getBody().readUtf8();
            assertNotNull(requestBody);
            JSONObject postData = new JSONObject(requestBody);
            assertTrue(postData.has(UTAdRequest.TAGS));
            assertTrue(postData.has(UTAdRequest.USER));
            assertTrue(postData.has(UTAdRequest.DEVICE));
            assertTrue(postData.has(UTAdRequest.APP));
            assertTrue(postData.has(UTAdRequest.KEYWORDS));
            JSONArray tags = postData.getJSONArray(UTAdRequest.TAGS);
            assertNotNull(tags);
            assertEquals(1, tags.length());
            JSONObject tag = (JSONObject) tags.get(0);
            assertTrue(tag.has(UTAdRequest.TAG_ID));
            assertEquals(PLACEMENT_ID, tag.getInt(UTAdRequest.TAG_ID));
            assertTrue(tag.has(UTAdRequest.TAG_SIZES));
            JSONArray sizes = tag.getJSONArray(UTAdRequest.TAG_SIZES);
            assertNotNull(sizes);
            assertEquals(1, sizes.length());
            JSONObject size = sizes.getJSONObject(0);
            assertNotNull(size);
            assertTrue(size.has(UTAdRequest.SIZE_WIDTH));
            assertEquals(300, size.getInt(UTAdRequest.SIZE_WIDTH));
            assertTrue(size.has(UTAdRequest.SIZE_HEIGHT));
            assertEquals(250, size.getInt(UTAdRequest.SIZE_HEIGHT));
            assertTrue(tag.has(UTAdRequest.TAG_ALLOW_SMALLER_SIZES));
            assertEquals(false, tag.getBoolean(UTAdRequest.TAG_ALLOW_SMALLER_SIZES));
            assertTrue(tag.has(UTAdRequest.TAG_ALLOWED_MEDIA_AD_TYPES));
            JSONArray allowedAdTypes = tag.getJSONArray(UTAdRequest.TAG_ALLOWED_MEDIA_AD_TYPES);
            assertNotNull(allowedAdTypes);
            assertEquals(2, allowedAdTypes.length());
            assertEquals(ANConstants.AD_TYPE_HTML, allowedAdTypes.getString(0));
            assertTrue(tag.has(UTAdRequest.TAG_PREBID));
            assertEquals(false, tag.getBoolean(UTAdRequest.TAG_PREBID));
            assertTrue(tag.has(UTAdRequest.TAG_DISABLE_PSA));
            assertEquals(true, tag.getBoolean(UTAdRequest.TAG_DISABLE_PSA));
    }

    private void setupMockServer() throws IOException {
        server = new MockWebServer();
        server.start();

        HttpUrl url = server.url("/");
        Settings.BASE_URL_UT_V2 = url.toString();
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestUTResponses.blank()));

    }

    @Override
    public void tearDown() {
        super.tearDown();
        try {
            if(server != null) {
                server.shutdown();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class MockAdOwner extends InterstitialAdView {

                public MockAdOwner(Context context) {
                        super(context);
                }

                @Override
                public boolean isReadyToStart() {
                        return true;
                }

        }

}
