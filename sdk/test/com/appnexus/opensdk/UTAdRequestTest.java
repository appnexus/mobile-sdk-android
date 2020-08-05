package com.appnexus.opensdk;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import com.appnexus.opensdk.shadows.ShadowSettings;
import com.appnexus.opensdk.ut.UTAdRequest;
import com.appnexus.opensdk.ut.UTAdRequester;
import com.appnexus.opensdk.ut.UTAdResponse;
import com.appnexus.opensdk.ut.UTRequestParameters;
import com.appnexus.opensdk.ut.adresponse.BaseAdResponse;
import com.appnexus.opensdk.utils.Settings;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.util.ArrayList;
import java.util.LinkedList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNull;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21, shadows = {ShadowSettings.class, ShadowLog.class})
public class UTAdRequestTest extends BaseRoboTest implements UTAdRequester {

    public static final int MEMBER_ID = 5;
    public static final String INVENTORY_CODE = "test_inv_code";
    public static final String TEST_KEY_STATES = "states";
    public static final String TEST_KEY_MUSIC_CATEGORY = "category";

    public static final String TEST_VALUE_STATES_1 = "AZ";
    public static final String TEST_VALUE_STATES_2 = "NY";
    public static final String TEST_VALUE_MUSIC_CATEGORY_1 = "jazz";

    UTRequestParameters utRequestParameters;
    UTAdRequest utAdRequest;
    UTAdResponse response;
    boolean requesterReceivedServerResponse = false;
    public static final int PLACEMENT_ID = 123456;
    public static final int PUBLISHER_ID = 9876;
    public static final String EXTERNAL_UID = "b865df7e-097f-4167-8a5c-44d778e75ee6";

    //   IAB USPrivacy
    private static final String IAB_USPRIVACY_STRING = "IABUSPrivacy_String";
    private static final String US_PRIVACY = "us_privacy";


    @Override
    public void setup() {
        super.setup();
        utRequestParameters = new UTRequestParameters(activity);
        utRequestParameters.setPrimarySize(new AdSize(1, 1));
        Settings.getSettings().ua = "";

    }

    @Test
    public void testAuctionTimeout() throws JSONException, InterruptedException {
        SDKSettings.setAuctionTimeout(200);
        executionSteps();
        JSONObject postData = inspectPostData();
        assertTrue(doesAuctionTimeExist(postData));
        assertEquals(postData.getInt("auction_timeout_ms"),200);
        long auctionTimeout = SDKSettings.getAuctionTimeout();
        assertEquals(auctionTimeout, 200);


        SDKSettings.setAuctionTimeout(0);
        executionSteps();
        postData = inspectPostData();
        assertFalse(doesAuctionTimeExist(postData));
        auctionTimeout = SDKSettings.getAuctionTimeout();
        assertEquals(auctionTimeout, 0);

    }


    @Test
    public void testFBBidderTokenAttached() throws JSONException, InterruptedException {
        UTRequestParameters.FB_SETTINGS_CLASS = "com.appnexus.opensdk.mocks.MockFBSettings";
        executionSteps();
        JSONObject postData = inspectPostData();
        assertTrue(inspectFBSettingsData(postData));
        UTRequestParameters.FB_SETTINGS_CLASS = "com.appnexus.opensdk.csr.FBSettings";
        executionSteps();
        postData = inspectPostData();
        assertFalse(inspectFBSettingsData(postData));
    }

    /**
     * Tests InventoryCode and MemberID validity in the request
     *
     * @throws Exception
     */
    @Test
    public void testMemberIdAndInventoryCode() throws Exception {
        utRequestParameters.setInventoryCodeAndMemberID(MEMBER_ID, INVENTORY_CODE);
        executionSteps();
    }

    /**
     * Tests PlacementId validity in the request
     *
     * @throws Exception
     */
    @Test
    public void testPlacementId() throws Exception {
        utRequestParameters.setPlacementID(String.valueOf(PLACEMENT_ID));
        executionSteps();

        inspectRequestForValidId(false);
    }

    /**
     * Tests PublisherId validity in the request
     *
     * @throws Exception
     */
    @Test
    public void testPublisherId() throws Exception {
        utRequestParameters.setPublisherId(PUBLISHER_ID);
        executionSteps();
        inspectPublisherId(PUBLISHER_ID);
    }

    /**
     * Tests Invalid PublisherId validity in the request
     *
     * @throws Exception
     */
    @Test
    public void testInvalidPublisherId() throws Exception {
        utRequestParameters.setPublisherId(-1);
        executionSteps();
        JSONObject postData = inspectPostData();
        assertFalse(postData.has("publisher_id"));
    }

    /**
     * Tests CustomKeyWords validity in the request
     * Single Key and single Value
     *
     * @throws Exception
     */
    @Test
    public void testCustomKeywords() throws Exception {
        String stringToTest = String.format("[{\"key\":\"%1$s\",\"value\":[\"%2$s\"]}]", TEST_KEY_STATES, TEST_VALUE_STATES_1);
        utRequestParameters.addCustomKeywords(TEST_KEY_STATES, TEST_VALUE_STATES_1);
        executionSteps();
        JSONObject postData = inspectPostData();
        inspectCustomKeywords(stringToTest, postData);
    }


    /**
     * Tests CustomKeyWords validity in the request
     * Single Key and Multiple Value
     *
     * @throws Exception
     */
    @Test
    public void testCustomKeywords_MultipleValues() throws Exception {
        String stringToTest = String.format("[{\"key\":\"%1$s\",\"value\":[\"%2$s\",\"%3$s\"]}]", TEST_KEY_STATES, TEST_VALUE_STATES_1, TEST_VALUE_STATES_2);
        utRequestParameters.addCustomKeywords(TEST_KEY_STATES, TEST_VALUE_STATES_1);
        utRequestParameters.addCustomKeywords(TEST_KEY_STATES, TEST_VALUE_STATES_2);
        executionSteps();
        JSONObject postData = inspectPostData();
        inspectCustomKeywords(stringToTest, postData);
    }


    /**
     * Tests CustomKeyWords validity in the request
     * Multiple Key and Multiple Value
     *
     * @throws Exception
     */
    @Test
    public void testCustomKeywords_MultipleKeys_And_Multiple_Values() throws Exception {
        String stringToTest = String.format("[{\"key\":\"%1$s\",\"value\":[\"%2$s\",\"%3$s\"]},{\"key\":\"%4$s\",\"value\":[\"%5$s\"]}]", TEST_KEY_STATES, TEST_VALUE_STATES_1, TEST_VALUE_STATES_2, TEST_KEY_MUSIC_CATEGORY, TEST_VALUE_MUSIC_CATEGORY_1);
        utRequestParameters.addCustomKeywords(TEST_KEY_STATES, TEST_VALUE_STATES_1);
        utRequestParameters.addCustomKeywords(TEST_KEY_STATES, TEST_VALUE_STATES_2);
        utRequestParameters.addCustomKeywords(TEST_KEY_MUSIC_CATEGORY, TEST_VALUE_MUSIC_CATEGORY_1);
        executionSteps();
        JSONObject postData = inspectPostData();
        inspectCustomKeywords(stringToTest, postData);
    }


    /**
     * Tests CustomKeyWords validity in the request
     * Multiple Key and Multiple Value
     *
     * @throws Exception
     */
    @Test
    public void testNoCustomKeywords() throws Exception {
        executionSteps();
        JSONObject postData = inspectPostData();
        assertFalse(postData.has("keywords"));
    }


    /**
     * Validates the age in the request
     *
     * @throws Exception
     */
    @Test
    public void testUserAge() throws Exception {
        String ageToTest = "25";
        utRequestParameters.setAge(ageToTest);

        executionSteps();

        JSONObject postData = inspectPostData();

        inspectUserAge(ageToTest, postData);
    }

    @Test
    public void testMaxDuration() throws Exception {
        int maxDuration = 180;
        utRequestParameters.setVideoAdMaxDuration(maxDuration);
        utRequestParameters.setMediaType(MediaType.INSTREAM_VIDEO);
        executionSteps();

        JSONObject postData = inspectPostData();
        JSONObject tag = getTagsData(postData);
        inspectMaxDuration(maxDuration, tag);
    }

    @Test
    public void testMinDuration() throws Exception {
        int minDuration = 10;
        utRequestParameters.setVideoAdMinDuration(minDuration);
        utRequestParameters.setMediaType(MediaType.INSTREAM_VIDEO);
        executionSteps();

        JSONObject postData = inspectPostData();
        JSONObject tag = getTagsData(postData);
        inspectMinDuration(minDuration, tag);
    }

    @Test
    public void testMinAndMinDuration() throws Exception {
        int minDuration = 10;
        int maxDuration = 180;
        utRequestParameters.setVideoAdMinDuration(minDuration);
        utRequestParameters.setVideoAdMaxDuration(maxDuration);
        utRequestParameters.setMediaType(MediaType.INSTREAM_VIDEO);
        executionSteps();

        JSONObject postData = inspectPostData();
        JSONObject tag = getTagsData(postData);
        inspectMaxandMinDuration(minDuration, maxDuration, tag);
    }

    @Test
    public void testNoDuration() throws Exception {

        utRequestParameters.setMediaType(MediaType.INSTREAM_VIDEO);
        executionSteps();

        JSONObject postData = inspectPostData();
        JSONObject tag = getTagsData(postData);
        inspectNoDuration(tag);
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

        utRequestParameters.setGender(genderToTest);
        executionSteps();

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
        utRequestParameters.setSizes(allowedSizes);
        executionSteps();

        JSONObject postData = inspectPostData();
        JSONObject tag = getTagsData(postData);


        inspectSizes(allowedSizes, tag);
    }


    /**
     * Tests whether allowed sizes data is passed correctly in the request
     *
     * @throws Exception
     */
    @Test
    public void testDefaultAllowedSizes() throws Exception {

        ArrayList<AdSize> allowedSizes = new ArrayList<AdSize>();

        executionSteps();

        JSONObject postData = inspectPostData();
        JSONObject tag = getTagsData(postData);

        inspectSizes(allowedSizes, tag);
    }

    /**
     * Tests the value of allowed ad types ,ExternalUid
     *
     * @throws Exception
     */
    @Test
    public void testAdTypes() throws Exception {
        utRequestParameters.setPlacementID(String.valueOf(PLACEMENT_ID));
        utRequestParameters.setExternalUid(EXTERNAL_UID);
        utRequestParameters.setMediaType(MediaType.BANNER);
        executionSteps();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();

        JSONObject postData = inspectPostData();
        JSONObject tag = getTagsData(postData);
        assertTrue(tag.has("id"));
        assertEquals(PLACEMENT_ID, tag.getInt("id"));

        JSONObject users = getUserData(postData);
        assertTrue(users.has("external_uid"));
        assertEquals(EXTERNAL_UID, users.getString("external_uid"));

        assertTrue(tag.has("allowed_media_types"));
        JSONArray allowedAdTypes = tag.getJSONArray("allowed_media_types");
        assertNotNull(allowedAdTypes);
        assertEquals(1, allowedAdTypes.length());
        assertEquals(1, allowedAdTypes.getInt(0));
    }

    /**
     * Checks whether the request has pre-bid enabled
     *
     * @throws Exception
     */
    @Test
    public void testForPrebid() throws Exception {
        utRequestParameters.setPlacementID(String.valueOf(PLACEMENT_ID));
        // NO PSA's for video
        //owner.setShouldServePSAs(false);
        executionSteps();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();

        JSONObject postData = inspectPostData();
        JSONObject tag = getTagsData(postData);
        assertTrue(tag.has("id"));
        assertEquals(PLACEMENT_ID, tag.getInt("id"));

        assertTrue(tag.has("prebid"));
        assertEquals(false, tag.getBoolean("prebid"));
    }


    /**
     * Test gdpr_consent in /ut request body
     *
     * @throws Exception
     */
    @Test
    public void testGDPRSettings() throws Exception {
        executionSteps();
        JSONObject postDataBeforeGDPRValueSet = inspectPostData();
        assertFalse(postDataBeforeGDPRValueSet.has("gdpr_consent"));

        ANGDPRSettings.setConsentRequired(activity, true);
        ANGDPRSettings.setConsentString(activity, "fooBar");
        executionSteps();
        JSONObject postDataWithGDPRValueSet = inspectPostData();
        assertEquals(true, postDataWithGDPRValueSet.getJSONObject("gdpr_consent").getBoolean("consent_required"));
        assertEquals("fooBar", postDataWithGDPRValueSet.getJSONObject("gdpr_consent").getString("consent_string"));
    }

    /**
     * Test ANGDPRSettings.reset()
     *
     * @throws Exception
     */
    @Test
    public void testGDPRSettingsReset() throws Exception {
        executionSteps();
        JSONObject postDataBeforeGDPRValueSet = inspectPostData();
        assertFalse(postDataBeforeGDPRValueSet.has("gdpr_consent"));

        SDKSettings.setAAID("1234", true);
        ANGDPRSettings.setConsentRequired(activity, true);
        ANGDPRSettings.setConsentString(activity, "fooBar");
        ANGDPRSettings.setPurposeConsents(activity, "1");
        executionSteps();
        JSONObject postDataWithGDPRValueSet = inspectPostData();
        assertEquals(true, postDataWithGDPRValueSet.getJSONObject("gdpr_consent").getBoolean("consent_required"));
        assertEquals("fooBar", postDataWithGDPRValueSet.getJSONObject("gdpr_consent").getString("consent_string"));
        assertEquals("1", ANGDPRSettings.getDeviceAccessConsent(activity));
        ANGDPRSettings.reset(activity);
        executionSteps();
        postDataWithGDPRValueSet = inspectPostData();
        assertEquals(false, postDataWithGDPRValueSet.has("gdpr_consent"));
        assertNull(ANGDPRSettings.getDeviceAccessConsent(activity));
    }

    /**
     * Test ANGDPRSettings.reset()
     *
     * @throws Exception
     */
    @Test
    public void testGDPRSettingsResetAndAssignEmptyConsent() throws Exception {
        executionSteps();
        JSONObject postDataBeforeGDPRValueSet = inspectPostData();
        assertFalse(postDataBeforeGDPRValueSet.has("gdpr_consent"));

        SDKSettings.setAAID("1234", true);
        ANGDPRSettings.setConsentRequired(activity, true);
        ANGDPRSettings.setConsentString(activity, "fooBar");
        ANGDPRSettings.setPurposeConsents(activity, "1");
        executionSteps();
        JSONObject postDataWithGDPRValueSet = inspectPostData();
        assertEquals(true, postDataWithGDPRValueSet.getJSONObject("gdpr_consent").getBoolean("consent_required"));
        assertEquals("fooBar", postDataWithGDPRValueSet.getJSONObject("gdpr_consent").getString("consent_string"));
        assertEquals("1", ANGDPRSettings.getDeviceAccessConsent(activity));
        ANGDPRSettings.reset(activity);
        executionSteps();
        postDataWithGDPRValueSet = inspectPostData();
        assertEquals(false, postDataWithGDPRValueSet.has("gdpr_consent"));
        assertNull(ANGDPRSettings.getDeviceAccessConsent(activity));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        prefs.edit().putString("IABTCF_PurposeConsents", "").apply();
        executionSteps();
        postDataWithGDPRValueSet = inspectPostData();
        assertEquals(false, postDataWithGDPRValueSet.has("gdpr_consent"));
        assertNull(ANGDPRSettings.getDeviceAccessConsent(activity));
    }

    /*
     * Test AAID with Consent Required set as true
     * and Purpose Consent set as true in /ut request body
     * @throws Exception
     */
    @Test
    public void testAAIDWithConsentRequiredTrueAndPurposeConsentTrue() throws Exception {
        executionSteps();
        JSONObject postDataBeforeGDPRValueSet = inspectPostData();
        assertFalse(postDataBeforeGDPRValueSet.has("gdpr_consent"));

        SDKSettings.setAAID("1234", true);
        ANGDPRSettings.setConsentRequired(activity,true);
        ANGDPRSettings.setPurposeConsents(activity,"1");
        executionSteps();
        JSONObject postDataWithGDPRValueSet = inspectPostData();
        System.out.println("POST DATA: " + postDataWithGDPRValueSet);
        JSONObject device = postDataWithGDPRValueSet.getJSONObject("device");
        JSONObject device_id = device.getJSONObject("device_id");
        assertNotNull(device_id);
        String aaid = device_id.getString("aaid");
        assertNotNull(aaid);

        assertEquals("1234", aaid);
        assertEquals(true, postDataWithGDPRValueSet.getJSONObject("gdpr_consent").getBoolean("consent_required"));
    }

    /**
     * Test USPrivacy_consent in /ut request body
     *
     * @throws Exception
     */
    @Test
    public void testUSPrivacySettings() throws Exception {

        //  US Privacy is not set
        ANUSPrivacySettings.reset(activity);
        executionSteps();
        JSONObject postDataBeforeUSPrivacyValueSet = inspectPostData();
        assertFalse(postDataBeforeUSPrivacyValueSet.has(IAB_USPRIVACY_STRING));


        //  US Privacy is set using ANConsentSettings.setIABUSPrivacyString
        ANUSPrivacySettings.setUSPrivacyString(activity, "1ynn");
        executionSteps();
        JSONObject postDataUSPrivacyValueSet = inspectPostData();
        assertEquals("1ynn", postDataUSPrivacyValueSet.getString(US_PRIVACY));


        // Reset ANConsentSettings.resetUSPrivacyConsent
        ANUSPrivacySettings.reset(activity);

        //  US Privacy is set using IABUSPrivacy_StringKey
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putString(IAB_USPRIVACY_STRING, "1nnn").apply();
        executionSteps();
        JSONObject postDataUSPrivacyValueSetUsingDefaultKey = inspectPostData();
        assertEquals("1nnn", postDataUSPrivacyValueSetUsingDefaultKey.getString(US_PRIVACY));

        //  Remove IABUSPrivacy_StringKey
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(activity);
        if (pref.contains(IAB_USPRIVACY_STRING)) {
            pref.edit().remove(IAB_USPRIVACY_STRING).apply();
        }

        // Set USPrivacy as Empty String
        ANUSPrivacySettings.setUSPrivacyString(activity, "");
        executionSteps();
        JSONObject postDataEmptyUSPrivacyValueSet = inspectPostData();
        assertFalse(postDataEmptyUSPrivacyValueSet.has(US_PRIVACY));

    }


    @Override
    public void tearDown() {
        super.tearDown();
//        try {
//            if (server != null) {
//                server.shutdown();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void executionSteps() {
        utAdRequest = new UTAdRequest(this);
        server.enqueue(new MockResponse().setResponseCode(200).setBody(TestResponsesUT.blank()));
        utAdRequest.execute();
        waitForTasks();
        clearAAIDAsyncTasks();
    }


    /**
     * -------------------------- Helper methods --------------------------------
     */

    private void inspectSizes(ArrayList<AdSize> allowedSizes, JSONObject tag) throws JSONException {

        System.out.println("Checking sizes validity...");
        assertTrue(tag.has("sizes"));
        JSONArray sizes = tag.getJSONArray("sizes");
        assertEquals(allowedSizes.size(), sizes.length());

        for (int i = 0; i < sizes.length(); i++) {
            JSONObject size = sizes.getJSONObject(i);
            assertNotNull(size);
            System.out.println("Validating size: (" + allowedSizes.get(i).width() + " , " + allowedSizes.get(i).height() + ")");
            assertTrue(size.has("width"));
            assertEquals(allowedSizes.get(i).width(), size.getInt("width"));
            assertTrue(size.has("height"));
            assertEquals(allowedSizes.get(i).height(), size.getInt("height"));
            System.out.println("Size is valid!");
        }

        System.out.println("Sizes validity passed!");
    }


    private void inspectRequestForValidId(boolean isMemberIdAndInvCodeAvailable) throws InterruptedException, JSONException {
        JSONObject postData = inspectPostData();
        JSONObject tag = getTagsData(postData);

        if (isMemberIdAndInvCodeAvailable) {
            System.out.println("Testing Inventory Code...");
            assertTrue(tag.has("code"));
            assertEquals(INVENTORY_CODE, tag.getString("code"));
            System.out.println("Inventory Code validity passed!");

            System.out.println("Testing Member Id...");
            assertTrue(postData.has("member_id"));
            assertEquals(MEMBER_ID, postData.getInt("member_id"));
            System.out.println("Member Id validity passed!");

        } else {
            System.out.println("Testing Placement Id...");
            assertTrue(tag.has("id"));
            assertEquals(PLACEMENT_ID, tag.getInt("id"));
            System.out.println("Placement Id validity passed!");
        }
    }

    private void inspectUserAge(String ageToTest, JSONObject postData) throws JSONException {
        System.out.println("Checking age validity...");
        JSONObject userObject = postData.getJSONObject("user");
        assertNotNull(userObject);
        assertNotNull(userObject.getString("age"));
        assertEquals(ageToTest, userObject.getString("age"));
        System.out.println("Age validity test passed!");
    }

    private void inspectMaxDuration(int maxDuration, JSONObject tagData) throws JSONException {

        System.out.println("Checking max duration...");


        JSONObject videoObject = tagData.getJSONObject("video");
        assertNotNull(videoObject);
        assertNotNull(videoObject.getInt("maxduration"));
        assertEquals(maxDuration, videoObject.getInt("maxduration"));
        System.out.println("max duration validity test passed!");

    }

    private void inspectPublisherId(int publisherId) throws JSONException, InterruptedException {
        JSONObject postData = inspectPostData();
        System.out.println("Checking Publisher ID...");
        assertTrue(postData.has("publisher_id"));
        assertEquals(publisherId, postData.getInt("publisher_id"));
        System.out.println("Publisher ID test passed!");
    }

    private void inspectMinDuration(int minDuration, JSONObject tagData) throws JSONException {

        System.out.println("Checking min duration...");


        JSONObject videoObject = tagData.getJSONObject("video");
        assertNotNull(videoObject);
        assertNotNull(videoObject.getInt("minduration"));
        assertEquals(minDuration, videoObject.getInt("minduration"));
        System.out.println("min duration validity test passed!");

    }

    private void inspectNoDuration(JSONObject tagData) throws JSONException {

        System.out.println("Null video object check...");

        assertTrue(tagData.isNull("video"));

        System.out.println("Null video object test passed!");


    }

    private void inspectMaxandMinDuration(int minDuration, int maxDuration, JSONObject tagData) throws JSONException {

        System.out.println("Checking max and min duration...");


        JSONObject videoObject = tagData.getJSONObject("video");
        assertNotNull(videoObject);
        assertNotNull(videoObject.getInt("maxduration"));
        assertEquals(maxDuration, videoObject.getInt("maxduration"));

        assertNotNull(videoObject.getInt("minduration"));
        assertEquals(minDuration, videoObject.getInt("minduration"));

        System.out.println("min and min duration validity test passed!");

    }

    private boolean inspectFBSettingsData(JSONObject postData) throws JSONException {
        System.out.println("Checking if FB bidderToken exists...");
        if (postData.has("tpuids")) {
            JSONArray tupids = postData.getJSONArray("tpuids");
            return tupids.getJSONObject(0).getString("user_id").equals("ThisIsMockFBBidderToken")
                    && tupids.getJSONObject(0).getString("provider").equals("audienceNetwork");
        }


        return false;
    }

    private boolean doesAuctionTimeExist(JSONObject postData) throws JSONException {
        System.out.println("Checking if AuctionTimeout exists...");
        if (postData.has("auction_timeout_ms")) {
            return true;
        }
        return false;
    }


    private JSONObject getTagsData(JSONObject postData) throws JSONException {
        JSONArray tags = postData.getJSONArray("tags");
        assertNotNull(tags);
        assertEquals(1, tags.length());
        return (JSONObject) tags.get(0);
    }

    private JSONObject getUserData(JSONObject postData) throws JSONException {
        JSONObject user = postData.getJSONObject("user");
        assertNotNull(user);
        return user;
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
        assertTrue(postData.has("tags"));
        assertTrue(postData.has("user"));
        assertTrue(postData.has("device"));
        assertTrue(postData.has("app"));

        System.out.println("POST data validity passed!");
        return postData;
    }


    private void clearAAIDAsyncTasks() {
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
    }

    private void inspectCustomKeywords(String keywordString, JSONObject postData) throws JSONException {
        System.out.println("Checking custom keywords validity...");
        assertTrue(postData.has("tags"));
        JSONArray tags = postData.getJSONArray("tags");
        JSONObject tag = tags.getJSONObject(0);
        assertTrue(tag.has("keywords"));
        JSONArray keyWordObject = tag.getJSONArray("keywords");
        assertNotNull(keyWordObject);
        assertEquals(keywordString, keyWordObject.toString());
        System.out.println("Custom keywords validity test passed!");
    }


    private void inspectUserGender(int genderInt, JSONObject postData) throws JSONException {
        System.out.println("Checking Gender validity...");
        JSONObject userObject = postData.getJSONObject("user");
        assertNotNull(userObject);
        assertNotNull(userObject.getString("gender"));
        assertEquals(genderInt, userObject.getInt("gender"));
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


    long time;

    @Override
    public void failed(ResultCode code, ANAdResponseInfo adResponseInfo) {

    }

    @Override
    public void onReceiveAd(AdResponse ad) {

    }

    @Override
    public void cancel() {

    }

    @Override
    public void execute() {

    }

    @Override
    public LinkedList<BaseAdResponse> getAdList() {
        return null;
    }

    @Override
    public UTRequestParameters getRequestParams() {
        return utRequestParameters;
    }

    @Override
    public void onReceiveUTResponse(UTAdResponse response) {
        if (response != null && response.getAdList() != null && !response.getAdList().isEmpty()) {
            requesterReceivedServerResponse = true;
        }else{
            failed(ResultCode.getNewInstance(ResultCode.UNABLE_TO_FILL), response.getAdResponseInfo());
        }
        this.response = response;
    }

    @Override
    public void continueWaterfall(ResultCode reason) {

    }

    @Override
    public void nativeRenderingFailed() {
    }
}
