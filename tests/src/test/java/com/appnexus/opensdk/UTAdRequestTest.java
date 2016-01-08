package com.appnexus.opensdk;

import com.appnexus.opensdk.UTAdRequest;
import com.appnexus.opensdk.utils.ANConstants;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21)
public class UTAdRequestTest extends BaseRoboTest {

    public static final int PLACEMENT_ID = 1234;

    @Test
    public void testUTPostRequest() throws Exception {
            UTAdRequest request = new UTAdRequest(null);
            server.enqueue(new MockResponse().setResponseCode(200).setBody(""));
            request.execute();
            waitForTasks();

            Robolectric.flushBackgroundThreadScheduler();
            Robolectric.flushForegroundThreadScheduler();
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
            assertEquals(width, size.getInt(UTAdRequest.SIZE_WIDTH));
            assertTrue(size.has(UTAdRequest.SIZE_HEIGHT));
            assertEquals(height, size.getInt(UTAdRequest.SIZE_HEIGHT));
            assertTrue(tag.has(UTAdRequest.TAG_ALLOW_SMALLER_SIZES));
            assertEquals(false, tag.getBoolean(UTAdRequest.TAG_ALLOW_SMALLER_SIZES));
            assertTrue(tag.has(UTAdRequest.TAG_ALLOWED_MEDIA_AD_TYPES));
            JSONArray allowedAdTypes = tag.getJSONArray(UTAdRequest.TAG_ALLOWED_MEDIA_AD_TYPES);
            assertNotNull(allowedAdTypes);
            assertEquals(1, allowedAdTypes.length());
            assertEquals(ANConstants.AD_TYPE_HTML, allowedAdTypes.getString(0));
            assertTrue(tag.has(UTAdRequest.TAG_PREBID));
            assertEquals(false, tag.getBoolean(UTAdRequest.TAG_PREBID));
            assertTrue(tag.has(UTAdRequest.TAG_DISABLE_PSA));
            assertEquals(true, tag.getBoolean(UTAdRequest.TAG_DISABLE_PSA));


    }

}
