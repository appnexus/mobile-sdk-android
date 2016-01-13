package com.appnexus.opensdk;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Pair;

import com.appnexus.opensdk.utils.ANConstants;
import com.appnexus.opensdk.utils.Settings;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class UTAdRequestTest extends BaseRoboTest {

    public static final int MEMBER_ID = 5;
    public static final String INVENTORY_CODE = "test_inv_code";
    public static final String TEST_KEY = "testKey";
    public static final String TEST_VALUE = "testValue";
    public static final int DEFAULT_WIDTH = 300;
    public static final int DEFAULT_HEIGHT = 250;
    AdFetcher adFetcher;
    MockAdOwner owner;
    MockWebServer server;
    public static final int PLACEMENT_ID = 1234;

    @Override
    public void setup() {
        super.setup();
        try {
            setupMockServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        owner = new MockAdOwner(activity);
    }

    /**
     * Tests InventoryCode and MemberID validity in the request
     *
     * @throws Exception
     */
    @Test
    public void testMemberIdAndInventoryCode() throws Exception {
        owner.setInventoryCodeAndMemberID(MEMBER_ID, INVENTORY_CODE);
        clearAAIDAsyncTasks();

        adFetcher = new AdFetcher(owner);
        adFetcher.start();

        waitForTasks();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();

        inspectRequestForValidId(true);
    }

    /**
     * Tests PlacementId validity in the request
     *
     * @throws Exception
     */
    @Test
    public void testPlacementId() throws Exception {
        owner.setPlacementID(String.valueOf(PLACEMENT_ID));
        clearAAIDAsyncTasks();
        adFetcher = new AdFetcher(owner);
        adFetcher.start();

        waitForTasks();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();

        inspectRequestForValidId(false);
    }

    /**
     * Tests PlacementId validity in the request
     *
     * @throws Exception
     */
    @Test
    public void testCustomKeywords() throws Exception {
        owner.addCustomKeywords("key1", "value1");
        Pair<String, String> keywordsToTest1 = new Pair<>("key1", "value1");

        owner.addCustomKeywords("key2", "value2");
        Pair<String, String> keywordsToTest2 = new Pair<>("key2", "value2");

        ArrayList<Pair> keywordsList = new ArrayList<>();
        keywordsList.add(keywordsToTest1);
        keywordsList.add(keywordsToTest2);

        clearAAIDAsyncTasks();
        adFetcher = new AdFetcher(owner);
        adFetcher.start();

        waitForTasks();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();

        JSONObject postData = inspectPostData();

        inspectCustomKeywords(keywordsList, postData);
    }


    /**
     * Validates the age in the request
     *
     * @throws Exception
     */
    @Test
    public void testUserAge() throws Exception {
        String ageToTest = "25";
        owner.setAge(ageToTest);

        clearAAIDAsyncTasks();
        adFetcher = new AdFetcher(owner);
        adFetcher.start();

        waitForTasks();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();

        JSONObject postData = inspectPostData();

        inspectUserAge(ageToTest, postData);
    }


    /**
     * Validates the Gender in the request
     *
     * @throws Exception
     */
    @Test
    public void testUserGender() throws Exception {
        AdView.GENDER genderToTest = AdView.GENDER.FEMALE;
        int genderInt = getGenderInt(genderToTest);

        owner.setGender(genderToTest);
        clearAAIDAsyncTasks();
        adFetcher = new AdFetcher(owner);
        adFetcher.start();

        waitForTasks();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();

        JSONObject postData = inspectPostData();

        inspectUserGender(genderInt, postData);
    }


    /**
     * Tests whether allowed sizes data is passed correctly in the request
     *
     * @throws Exception
     */
    @Test
    public void testAllowedSizes() throws Exception {

        ArrayList<AdSize> allowedSizes = new ArrayList<AdSize>();
        AdSize adSize = new AdSize(300, 250);
        allowedSizes.add(adSize);
        AdSize adSize2 = new AdSize(480, 800);
        allowedSizes.add(adSize2);
        owner.setAllowedSizes(allowedSizes);
        clearAAIDAsyncTasks();
        adFetcher = new AdFetcher(owner);
        adFetcher.start();

        waitForTasks();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();

        JSONObject postData = inspectPostData();
        JSONObject tag = getTagsData(postData);

        inspectSizes(allowedSizes, tag);
    }

    /**
     * Tests whether allowed sizes data is passed correctly in the request
     * @throws Exception
     */
    @Test
    public void testDefaultAllowedSizes() throws Exception{

        ArrayList<AdSize> allowedSizes = new ArrayList<AdSize>();
        allowedSizes.add(new AdSize(DEFAULT_WIDTH, DEFAULT_HEIGHT));

        clearAAIDAsyncTasks();
        adFetcher = new AdFetcher(owner);
        adFetcher.start();

        waitForTasks();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();

        JSONObject postData = inspectPostData();
        JSONObject tag = getTagsData(postData);

        inspectSizes(allowedSizes, tag);
    }

    /**
     * Validates if PSA is enabled
     *
     * @throws InterruptedException
     * @throws JSONException
     */
    @Test
    public void testPSAEnabled() throws InterruptedException, JSONException {
        System.out.println("Enabling PSA flag in public API...");
        inspectPSAFlag(true);
    }


    /**
     * Validates if PSA is disabled
     *
     * @throws InterruptedException
     * @throws JSONException
     */
    @Test
    public void testPSADisabled() throws InterruptedException, JSONException {
        System.out.println("Disabling PSA flag in public API...");
        inspectPSAFlag(false);
    }

    /**
     * Tests the value of allowed ad types
     *
     * @throws Exception
     */
    @Test
    public void testAdTypes() throws Exception {
        owner.setPlacementID(String.valueOf(PLACEMENT_ID));
        clearAAIDAsyncTasks();
        adFetcher = new AdFetcher(owner);
        adFetcher.start();
        waitForTasks();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();

        JSONObject postData = inspectPostData();
        JSONObject tag = getTagsData(postData);
        assertTrue(tag.has(UTAdRequest.TAG_ID));
        assertEquals(PLACEMENT_ID, tag.getInt(UTAdRequest.TAG_ID));

        assertTrue(tag.has(UTAdRequest.TAG_ALLOWED_MEDIA_AD_TYPES));
        JSONArray allowedAdTypes = tag.getJSONArray(UTAdRequest.TAG_ALLOWED_MEDIA_AD_TYPES);
        assertNotNull(allowedAdTypes);
        assertEquals(2, allowedAdTypes.length());
        assertEquals(ANConstants.AD_TYPE_HTML, allowedAdTypes.getString(0));
        assertEquals(ANConstants.AD_TYPE_VIDEO, allowedAdTypes.getString(1));
    }

    /**
     * Checks whether the request has pre-bid enabled
     *
     * @throws Exception
     */
    @Test
    public void testForPrebid() throws Exception {
        owner.setPlacementID(String.valueOf(PLACEMENT_ID));
        owner.setShouldServePSAs(false);
        clearAAIDAsyncTasks();
        adFetcher = new AdFetcher(owner);
        adFetcher.start();
        waitForTasks();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();

        JSONObject postData = inspectPostData();
        JSONObject tag = getTagsData(postData);
        assertTrue(tag.has(UTAdRequest.TAG_ID));
        assertEquals(PLACEMENT_ID, tag.getInt(UTAdRequest.TAG_ID));

        assertTrue(tag.has(UTAdRequest.TAG_PREBID));
        assertEquals(false, tag.getBoolean(UTAdRequest.TAG_PREBID));
    }


    @Override
    public void tearDown() {
        super.tearDown();
        try {
            if (server != null) {
                server.shutdown();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * -------------------------- Helper methods --------------------------------
     */

    private void inspectSizes(ArrayList<AdSize> allowedSizes, JSONObject tag) throws JSONException {
        System.out.println("Checking sizes validity...");
        assertTrue(tag.has(UTAdRequest.TAG_SIZES));
        JSONArray sizes = tag.getJSONArray(UTAdRequest.TAG_SIZES);
        assertNotNull(sizes);
        assertEquals(allowedSizes.size(), sizes.length());

        for (int i = 0; i < sizes.length(); i++) {
            JSONObject size = sizes.getJSONObject(i);
            assertNotNull(size);
            System.out.println("Validating size: (" + allowedSizes.get(i).width() + " , " + allowedSizes.get(i).height() + ")");
            assertTrue(size.has(UTAdRequest.SIZE_WIDTH));
            assertEquals(allowedSizes.get(i).width(), size.getInt(UTAdRequest.SIZE_WIDTH));
            assertTrue(size.has(UTAdRequest.SIZE_HEIGHT));
            assertEquals(allowedSizes.get(i).height(), size.getInt(UTAdRequest.SIZE_HEIGHT));
            System.out.println("Size is valid!");
        }

        assertTrue(tag.has(UTAdRequest.TAG_ALLOW_SMALLER_SIZES));
        assertEquals(false, tag.getBoolean(UTAdRequest.TAG_ALLOW_SMALLER_SIZES));
        System.out.println("Sizes validity passed!");
    }


    private void inspectRequestForValidId(boolean isMemberIdAndInvCodeAvailable) throws InterruptedException, JSONException {
        JSONObject postData = inspectPostData();
        JSONObject tag = getTagsData(postData);

        if (isMemberIdAndInvCodeAvailable) {
            System.out.println("Testing Inventory Code...");
            assertTrue(tag.has(UTAdRequest.TAG_CODE));
            assertEquals(INVENTORY_CODE, tag.getString(UTAdRequest.TAG_CODE));
            System.out.println("Inventory Code validity passed!");

            System.out.println("Testing Member Id...");
            assertTrue(postData.has(UTAdRequest.MEMBER_ID));
            assertEquals(MEMBER_ID, postData.getInt(UTAdRequest.MEMBER_ID));
            System.out.println("Member Id validity passed!");

        } else {
            System.out.println("Testing Placement Id...");
            assertTrue(tag.has(UTAdRequest.TAG_ID));
            assertEquals(PLACEMENT_ID, tag.getInt(UTAdRequest.TAG_ID));
            System.out.println("Placement Id validity passed!");
        }
    }

    private void inspectUserAge(String ageToTest, JSONObject postData) throws JSONException {
        System.out.println("Checking age validity...");
        JSONObject userObject = postData.getJSONObject(UTAdRequest.USER);
        assertNotNull(userObject);
        assertNotNull(userObject.getString(UTAdRequest.USER_AGE));
        assertEquals(ageToTest, userObject.getString(UTAdRequest.USER_AGE));
        System.out.println("Age validity test passed!");
    }

    private JSONObject getTagsData(JSONObject postData) throws JSONException {
        JSONArray tags = postData.getJSONArray(UTAdRequest.TAGS);
        assertNotNull(tags);
        assertEquals(1, tags.length());
        return (JSONObject) tags.get(0);
    }

    @NonNull
    private JSONObject inspectPostData() throws InterruptedException, JSONException {
        System.out.println("Testing POST data...");
        RecordedRequest recordedRequest = server.takeRequest();
        String method = recordedRequest.getMethod();
        assertNotNull(method);
        assertEquals("POST", method.toUpperCase());
        assertNotNull(recordedRequest.getBody());
        String requestBody = recordedRequest.getBody().readUtf8();
        assertNotNull(requestBody);
        JSONObject postData = new JSONObject(requestBody);
        assertTrue(postData.has(UTAdRequest.TAGS));
        assertTrue(postData.has(UTAdRequest.USER));
        assertTrue(postData.has(UTAdRequest.DEVICE));
        assertTrue(postData.has(UTAdRequest.APP));

        System.out.println("POST data validity passed!");
        return postData;
    }


    private void clearAAIDAsyncTasks() {
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
    }

    private void inspectCustomKeywords(ArrayList<Pair> keywordsList, JSONObject postData) throws JSONException {
        System.out.println("Checking custom keywords validity...");
        assertTrue(postData.has(UTAdRequest.KEYWORDS));

        JSONArray keywordsJsonArray = postData.getJSONArray(UTAdRequest.KEYWORDS);
        for (int i = 0; i < keywordsJsonArray.length(); i++) {
            assertNotNull(keywordsJsonArray.get(i));

            assertEquals(keywordsList.get(i).first, ((JSONObject) keywordsJsonArray.get(i)).getString("key"));
            assertEquals(keywordsList.get(i).second, ((JSONObject) keywordsJsonArray.get(i)).getString("value"));
        }
        System.out.println("Custom keywords validity test passed!");
    }


    private void inspectUserGender(int genderInt, JSONObject postData) throws JSONException {
        System.out.println("Checking Gender validity...");
        JSONObject userObject = postData.getJSONObject(UTAdRequest.USER);
        assertNotNull(userObject);
        assertNotNull(userObject.getString(UTAdRequest.USER_GENDER));
        assertEquals(genderInt, userObject.getInt(UTAdRequest.USER_GENDER));
        System.out.println("Gender validity test passed!");
    }


    private int getGenderInt(AdView.GENDER genderToTest) {
        int gender = 0;
        switch (genderToTest) {
            case FEMALE:
                gender = 2;
                break;
            case MALE:
                gender = 1;
                break;
            case UNKNOWN:
                gender = 0;
                break;
        }
        return gender;
    }


    private void inspectPSAFlag(boolean shouldEnablePSA) throws InterruptedException, JSONException {
        System.out.println("Validating PSA flag...");
        owner.setPlacementID(String.valueOf(PLACEMENT_ID));
        owner.setShouldServePSAs(shouldEnablePSA);
        clearAAIDAsyncTasks();

        adFetcher = new AdFetcher(owner);
        adFetcher.start();
        waitForTasks();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();

        JSONObject postData = inspectPostData();
        JSONObject tag = getTagsData(postData);
        assertTrue(tag.has(UTAdRequest.TAG_ID));
        assertEquals(PLACEMENT_ID, tag.getInt(UTAdRequest.TAG_ID));

        assertTrue(tag.has(UTAdRequest.TAG_DISABLE_PSA));
        assertEquals(!shouldEnablePSA, tag.getBoolean(UTAdRequest.TAG_DISABLE_PSA));

        System.out.println("Is PSA Disabled: " + tag.getBoolean(UTAdRequest.TAG_DISABLE_PSA));
        System.out.println("PSA validity test passed!");
    }


    private void setupMockServer() throws IOException {
        server = new MockWebServer();
        server.start();

        HttpUrl url = server.url("/");
        Settings.BASE_URL_UT_V2 = url.toString();
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestUTResponses.blank()));
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
