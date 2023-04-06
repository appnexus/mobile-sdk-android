package com.appnexus.opensdk;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.Context;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.appnexus.opensdk.shadows.ShadowSettings;
import com.appnexus.opensdk.ut.UTAdRequest;
import com.appnexus.opensdk.ut.UTAdRequester;
import com.appnexus.opensdk.ut.UTAdResponse;
import com.appnexus.opensdk.ut.UTRequestParameters;
import com.appnexus.opensdk.ut.adresponse.BaseAdResponse;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.viewability.ANOmidViewabilty;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import androidx.annotation.NonNull;

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

    private String GEO_OVERRIDE = "geoOverride";
    private String COUNTRY_CODE = "countryCode";
    private String ZIP = "zip";

    @Override
    public void setup() {
        super.setup();
        utRequestParameters = new UTRequestParameters(activity);
        utRequestParameters.setPrimarySize(new AdSize(1, 1));
        Settings.getSettings().ua = "";
        SDKSettings.disableAAIDUsage(false);
        SDKSettings.setDoNotTrack(false);
        SDKSettings.setPublisherUserId("");
    }

    @Test
    public void testUTRequestForOMIDEnableBannerAd() throws JSONException, InterruptedException {
        utRequestParameters.setMediaType(MediaType.BANNER);
        verifyOMIDSignalEnable("banner_frameworks");
        verifyOMIDSignalDisableForFramework("native_frameworks");
        verifyOMIDSignalDisableForFramework("video_frameworks");

    }

    @Test
    public void testUTRequestForOMIDEnableBannerNativeVideoAd() throws JSONException, InterruptedException {
        utRequestParameters.setMediaType(MediaType.BANNER);
        utRequestParameters.setBannerEnabled(true);
        utRequestParameters.setBannerVideoEnabled(true);
        utRequestParameters.setBannerNativeEnabled(true);
        verifyOMIDSignalEnable("banner_frameworks");
        verifyOMIDSignalEnable("native_frameworks");
        verifyOMIDSignalEnable("video_frameworks");
    }


    @Test
    public void testUTRequestForOMIDEnableVideoNativeVideoAd() throws JSONException, InterruptedException {
        utRequestParameters.setMediaType(MediaType.BANNER);
        utRequestParameters.setBannerNativeEnabled(true);
        utRequestParameters.setBannerVideoEnabled(true);
        utRequestParameters.setBannerEnabled(false);
        verifyOMIDSignalDisableForFramework("banner_frameworks");
        verifyOMIDSignalEnable("native_frameworks");
        verifyOMIDSignalEnable("video_frameworks");
    }

    @Test
    public void testUTRequestForOMIDEnableBannerVideoAd() throws JSONException, InterruptedException {
        utRequestParameters.setMediaType(MediaType.BANNER);
        utRequestParameters.setBannerEnabled(true);
        utRequestParameters.setBannerVideoEnabled(true);
        verifyOMIDSignalEnable("banner_frameworks");
        verifyOMIDSignalEnable("video_frameworks");
        verifyOMIDSignalDisableForFramework("native_frameworks");
    }
    @Test
    public void testUTRequestForOMIDEnableNativeAd()throws JSONException, InterruptedException {
        utRequestParameters.setMediaType(MediaType.NATIVE);
        verifyOMIDSignalEnable("native_frameworks");
    }

    @Test
    public void testUTRequestForOMIDEnableInterstitialAd()throws JSONException, InterruptedException {
        utRequestParameters.setMediaType(MediaType.INTERSTITIAL);
        verifyOMIDSignalEnable("banner_frameworks");
    }

    @Test
    public void testUTRequestForOMIDEnableVideoAd()throws JSONException, InterruptedException {
        utRequestParameters.setMediaType(MediaType.INSTREAM_VIDEO);
        verifyOMIDSignalEnable("video_frameworks");
    }

    private void verifyOMIDSignalEnable(String framework)throws JSONException, InterruptedException {
        executionSteps();
        JSONObject postData = inspectPostData();
        assertTrue(doesIABSupportExist(postData));
        assertNotNull(postData.getJSONObject("iab_support"));
        assertEquals(postData.getJSONObject("iab_support").getString("omidpn"), ANOmidViewabilty.OMID_PARTNER_NAME);
        assertEquals(postData.getJSONObject("iab_support").getString("omidpv"), Settings.getSettings().sdkVersion);

        JSONObject tag = getTagsData(postData);
        assertTrue(doesOMIDFrameworkExist(tag, framework));
        JSONArray omid_frameworks = tag.getJSONArray(framework);
        assertNotNull(omid_frameworks);
        assertEquals(1, omid_frameworks.length());
        assertEquals(6, omid_frameworks.getInt(0));
    }

    private void verifyOMIDSignalDisableForFramework(String framework)throws JSONException, InterruptedException {
        executionSteps();
        JSONObject postData = inspectPostData();
        JSONObject tag = getTagsData(postData);
        assertFalse(doesOMIDFrameworkExist(tag,framework));
    }

    private void verifyOMIDSignalDisable(String framework)throws JSONException, InterruptedException {
        executionSteps();
        JSONObject postData = inspectPostData();
        assertFalse(doesIABSupportExist(postData));
        JSONObject tag = getTagsData(postData);
        assertFalse(doesOMIDFrameworkExist(tag,framework));
    }
    @Test
    public void testUTRequestForOMIDDisableBannerAd() throws JSONException, InterruptedException {
        SDKSettings.setOMEnabled(false);
        utRequestParameters.setMediaType(MediaType.BANNER);
        verifyOMIDSignalDisable("banner_frameworks");
    }

    @Test
    public void testUTRequestForOMIDDisableBannerNativeVideoAd() throws JSONException, InterruptedException {
        SDKSettings.setOMEnabled(false);
        utRequestParameters.setMediaType(MediaType.BANNER);
        utRequestParameters.setBannerEnabled(true);
        utRequestParameters.setBannerVideoEnabled(true);
        utRequestParameters.setBannerNativeEnabled(true);
        verifyOMIDSignalDisable("banner_frameworks");
        verifyOMIDSignalDisable("native_frameworks");
        verifyOMIDSignalDisable("video_frameworks");
    }


    @Test
    public void testUTRequestForOMIDDisableBannerVideoAd() throws JSONException, InterruptedException {
        SDKSettings.setOMEnabled(false);
        utRequestParameters.setMediaType(MediaType.BANNER);
        utRequestParameters.setBannerEnabled(true);
        utRequestParameters.setBannerVideoEnabled(true);
        verifyOMIDSignalDisable("banner_frameworks");
        verifyOMIDSignalDisable("video_frameworks");
    }
    @Test
    public void testUTRequestForOMIDDisableNativeAd()throws JSONException, InterruptedException {
        SDKSettings.setOMEnabled(false);
        utRequestParameters.setMediaType(MediaType.NATIVE);
        verifyOMIDSignalDisable("native_frameworks");
    }

    @Test
    public void testUTRequestForOMIDDisableInterstitialAd()throws JSONException, InterruptedException {
        SDKSettings.setOMEnabled(false);
        utRequestParameters.setMediaType(MediaType.INTERSTITIAL);
        verifyOMIDSignalDisable("banner_frameworks");
    }

    @Test
    public void testUTRequestForOMIDDisableVideoAd()throws JSONException, InterruptedException {
        SDKSettings.setOMEnabled(false);
        utRequestParameters.setMediaType(MediaType.INSTREAM_VIDEO);
        verifyOMIDSignalDisable("video_frameworks");
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
     * Validates the Default Traffic Source in the request
     *
     * @throws Exception
     */
    @Test
    public void testDefaultTrafficSourceCode() throws Exception {
        executionSteps();

        JSONObject postData = inspectPostData();
        JSONObject tags = getTagsData(postData);
        assertFalse(tags.has("traffic_source_code"));
    }

    /**
     * Validates the Traffic Source in the request
     *
     * @throws Exception
     */
    @Test
    public void testTrafficSourceCode() throws Exception {
        utRequestParameters.setTrafficSourceCode("Xandr");
        executionSteps();

        JSONObject postData = inspectPostData();
        JSONObject tags = getTagsData(postData);
        String trafficSourceCode = tags.getString("traffic_source_code");
        assertEquals("Xandr", trafficSourceCode);
    }

    /**
     * Validates the Default Eternal Inventory Code in the request
     *
     * @throws Exception
     */
    @Test
    public void testDefaultExtInvCode() throws Exception {
        executionSteps();
        JSONObject postData = inspectPostData();
        JSONObject tags = getTagsData(postData);
        assertFalse(tags.has("ext_inv_code"));
    }

    /**
     * Validates the External Inventory Code in the request
     *
     * @throws Exception
     */
    @Test
    public void testExtInvCode() throws Exception {
        utRequestParameters.setExtInvCode("Xandr");
        executionSteps();

        JSONObject postData = inspectPostData();
        JSONObject tags = getTagsData(postData);
        String extInvCode = tags.getString("ext_inv_code");
        assertEquals("Xandr", extInvCode);
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
        SDKSettings.setPublisherUserId(EXTERNAL_UID);
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
        assertTrue(Settings.getSettings().deviceAccessAllowed); // Default case with no GDPR settings device access should be allowed

        ANGDPRSettings.setConsentRequired(activity, true);
        ANGDPRSettings.setConsentString(activity, "fooBar");
        executionSteps();
        JSONObject postDataWithGDPRValueSet = inspectPostData();
        assertEquals(true, postDataWithGDPRValueSet.getJSONObject("gdpr_consent").getBoolean("consent_required"));
        assertEquals("fooBar", postDataWithGDPRValueSet.getJSONObject("gdpr_consent").getString("consent_string"));
        assertFalse(Settings.getSettings().deviceAccessAllowed); // When consent required set to true and no purpose consent string deviceAccess is not allowed
    }

    /**
     * Test GPP in /ut request body
     *
     * @throws Exception
     */
    @Test
    public void testGPPSettings() throws Exception {
        executionSteps();
        JSONObject postDataBeforeGPPValueSet = inspectPostData();
        assertFalse(postDataBeforeGPPValueSet.has("privacy"));

        setGPPString(activity, "gppString");
        setGPPSID(activity, "1_2_3");
        executionSteps();
        JSONObject postDataWithGPPValueSet = inspectPostData();
        assertEquals("gppString", postDataWithGPPValueSet.getJSONObject("privacy").getString("gpp"));
        JSONArray testArr = postDataWithGPPValueSet.getJSONObject("privacy").getJSONArray("gpp_sid");
        for (int i = 0; i < testArr.length(); i++) {
            assertEquals(i+1, testArr.get(i));
        }
    }



    /**
     * Test addtl_consent in /ut request body
     *
     * @throws Exception
     */
    @Test
    public void testGooggleACMConsentString() throws Exception {

        ANGDPRSettings.setConsentRequired(activity, true);

        executionSteps();
        JSONObject postDataBeforeGDPRValueSet = inspectPostData();
        assertEquals(true, postDataBeforeGDPRValueSet.getJSONObject("gdpr_consent").getBoolean("consent_required"));
        assertEquals("{\"consent_required\":true,\"consent_string\":\"\"}",postDataBeforeGDPRValueSet.getJSONObject("gdpr_consent").toString());


        PreferenceManager.getDefaultSharedPreferences(activity).edit().putString("IABTCF_AddtlConsent", "123479").apply(); // invalid ACM string
        executionSteps();
        JSONObject postDataWithGDPRValueInvalidACMSet = inspectPostData();
        assertEquals(true, postDataWithGDPRValueInvalidACMSet.getJSONObject("gdpr_consent").getBoolean("consent_required"));
        assertEquals("{\"consent_required\":true,\"consent_string\":\"\"}",postDataBeforeGDPRValueSet.getJSONObject("gdpr_consent").toString());


        PreferenceManager.getDefaultSharedPreferences(activity).edit().putString("IABTCF_AddtlConsent", "1~7.12.35.62.66.70.89.93.108").apply();
        executionSteps();
        JSONObject postDataWithGDPRValueSet = inspectPostData();
        assertEquals(true, postDataWithGDPRValueSet.getJSONObject("gdpr_consent").getBoolean("consent_required"));
        JSONArray arry = new JSONArray();
        arry.put(7);
        arry.put(12);
        arry.put(35);
        arry.put(62);
        arry.put(66);
        arry.put(70);
        arry.put(89);
        arry.put(93);
        arry.put(108);
        assertEquals(arry, postDataWithGDPRValueSet.getJSONObject("gdpr_consent").getJSONArray("addtl_consent"));
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
     API To verify : disableAAIDUsage
     If disableAAIDUsage is set to default value(false) then the device_id should not be nil
     */
    @Test
    public void testUTRequestDisableAAIDsageDefault() throws Exception {
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
    }

    /**
     API To verify : disableAAIDUsage
     If disableAAIDUsage is set to false value then the device_id should not be nil
     */
    @Test
    public void testUTRequestDisableAAIDsageSetTofalse() throws Exception {
        executionSteps();
        JSONObject postDataBeforeGDPRValueSet = inspectPostData();
        assertFalse(postDataBeforeGDPRValueSet.has("gdpr_consent"));
        SDKSettings.setAAID("1234", true);
        SDKSettings.disableAAIDUsage(false);
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
    }


    /**
     API To verify : disableAAIDUsage
     If disableAAIDUsage is set to true value then the device_id should not be nil
     */
    @Test
    public void testUTRequestDisableAAIDsageSetTotrue() throws Exception {
        executionSteps();
        JSONObject postDataBeforeGDPRValueSet = inspectPostData();
        assertFalse(postDataBeforeGDPRValueSet.has("gdpr_consent"));
        SDKSettings.setAAID("1234", true);
        SDKSettings.disableAAIDUsage(true);
        ANGDPRSettings.setConsentRequired(activity,true);
        ANGDPRSettings.setPurposeConsents(activity,"1");
        executionSteps();
        JSONObject postDataWithGDPRValueSet = inspectPostData();
        System.out.println("POST DATA: " + postDataWithGDPRValueSet);
        JSONObject device = postDataWithGDPRValueSet.getJSONObject("device");
        assertFalse(device.has("device_id"));
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

    /**
     * Test GeoOverride parameters in /ut request body
     *
     * @throws Exception
     */
    @Test
    public void testGeoOverrideParams() throws Exception {

        // Default params
        executionSteps();
        JSONObject postDataBefore = inspectPostData();
        assertFalse(postDataBefore.has(GEO_OVERRIDE));


        // Set the params
        SDKSettings.setGeoOverrideCountryCode("US");
        SDKSettings.setGeoOverrideZipCode("123456");
        executionSteps();
        JSONObject postData = inspectPostData();
        assertTrue(postData.has(GEO_OVERRIDE));
        JSONObject geoOverride = postData.getJSONObject(GEO_OVERRIDE);
        assertTrue(geoOverride.has(COUNTRY_CODE));
        assertTrue(geoOverride.has(ZIP));
        assertTrue(geoOverride.getString(COUNTRY_CODE).equals("US"));
        assertTrue(geoOverride.getString(ZIP).equals("123456"));

        // Reset the params
        SDKSettings.setGeoOverrideCountryCode("");
        SDKSettings.setGeoOverrideZipCode("");
        executionSteps();
        JSONObject postDataReset = inspectPostData();
        assertFalse(postDataReset.has(GEO_OVERRIDE));
    }



    /**
     * Test Publisher User Id parameters in /ut request body
     *
     * @throws Exception
     */
    @Test
    public void testPublisherUserID() throws Exception {

        // Default params, External_uid will not be present in the POST DATA
        executionSteps();
        JSONObject postDataBefore = inspectPostData();
        JSONObject userBefore = postDataBefore.getJSONObject("user");
        assertFalse(userBefore.has("external_uid"));

        // setPublisherUserId and make sure  external_uid is present in the POST DATA
        SDKSettings.setPublisherUserId("test-publisheruserid");
        executionSteps();
        JSONObject postDataAfter = inspectPostData();
        JSONObject userAfter = postDataAfter.getJSONObject("user");
        assertTrue(userAfter.has("external_uid"));
        assertTrue(userAfter.getString("external_uid").equals("test-publisheruserid"));

        // Set and Clear the setPublisherUserId and make sure  external_uid is is not present in POST DATA
        SDKSettings.setPublisherUserId("test-publisheruserid-foo");
        SDKSettings.setPublisherUserId("");
        executionSteps();
        JSONObject postDataClear = inspectPostData();
        JSONObject userClear = postDataClear.getJSONObject("user");
        assertFalse(userClear.has("external_uid"));


        // Set both setExternalUid and setPublisherUserId and make sure  setPublisherUserId overrides setExternalUid in the POST DATA
        SDKSettings.setPublisherUserId("test-publisheruserid-bar-xyz");
        executionSteps();
        JSONObject postDataDoBoth = inspectPostData();
        JSONObject userDoBoth = postDataDoBoth.getJSONObject("user");
        assertTrue(userDoBoth.has("external_uid"));
        assertTrue(userDoBoth.getString("external_uid").equals("test-publisheruserid-bar-xyz"));
    }

    /**
     * Test Do Not Track parameters in /ut request body
     *
     * @throws Exception
     */
    @Test
    public void testDoNotTrack() throws Exception {

        // Default params, dnt will not be present in the POST DATA
        executionSteps();
        JSONObject postDataBefore = inspectPostData();
        JSONObject userBefore = postDataBefore.getJSONObject("user");
        assertFalse(userBefore.has("dnt"));

        // setDoNotTrack to false and make sure  dnt  is not present in the POST DATA
        SDKSettings.setDoNotTrack(false);
        executionSteps();
        JSONObject postDataAfterSetToFalse = inspectPostData();
        JSONObject userAfterSetToFalse = postDataAfterSetToFalse.getJSONObject("user");
        assertFalse(userAfterSetToFalse.has("dnt"));

        // setDoNotTrack to true and make sure  dnt  is  present in the POST DATA
        SDKSettings.setDoNotTrack(true);
        executionSteps();
        JSONObject postDataAfterSetToTrue = inspectPostData();
        JSONObject userAfterSetToTrue = postDataAfterSetToTrue.getJSONObject("user");
        assertTrue(userAfterSetToTrue.has("dnt"));
        assertTrue(userAfterSetToTrue.getBoolean("dnt"));

    }

    /**
     * Test User Id parameters in /ut request body
     *
     * @throws Exception
     */
    @Test
    public void testUserIds() throws Exception {
        List<ANUserId> userIds = new ArrayList<>();

        ANUserId tradeDeskUserID = new ANUserId(ANUserId.Source.THE_TRADE_DESK, "sdksettings-userid-ttd-foobar");
        userIds.add(tradeDeskUserID);

        ANUserId criteoUserId = new ANUserId(ANUserId.Source.CRITEO, "sdksettings-userid-Criteo-foobar");
        userIds.add(criteoUserId);

        ANUserId netIdUserID = new ANUserId(ANUserId.Source.NETID, "sdksettings-userid-netid-foobar");
        userIds.add(netIdUserID);

        ANUserId liveRampUserID = new ANUserId(ANUserId.Source.LIVERAMP, "sdksettings-userid-liveramp-foobar");
        userIds.add(liveRampUserID);

        ANUserId UID2UserId = new ANUserId(ANUserId.Source.UID2, "sdksettings-userid-uid2-foobar");
        userIds.add(UID2UserId);

        ANUserId genericUserID = new ANUserId("Generic Source", "sdksettings-userid-generic-foobar");
        userIds.add(genericUserID);



        // Default params, euid node will not be present in the POST DATA
        executionSteps();
        JSONObject postDataBefore = inspectPostData();
        assertFalse(postDataBefore.has("eids"));


        SDKSettings.setUserIds(userIds);
        executionSteps();
        JSONObject postDataAftersetExternalIds = inspectPostData();
        JSONArray euidArrayAftersetExternalIds = postDataAftersetExternalIds.getJSONArray("eids");
        assertNotNull(euidArrayAftersetExternalIds);
        assertEquals(userIds.size(), euidArrayAftersetExternalIds.length());
        assertTrue(euidArrayAftersetExternalIds.toString().contains("{\"source\":\"criteo.com\",\"id\":\"sdksettings-userid-Criteo-foobar\"}"));
        assertTrue(euidArrayAftersetExternalIds.toString().contains("{\"source\":\"netid.de\",\"id\":\"sdksettings-userid-netid-foobar\"}"));
        assertTrue(euidArrayAftersetExternalIds.toString().contains("{\"source\":\"liveramp.com\",\"id\":\"sdksettings-userid-liveramp-foobar\"}"));
        assertTrue(euidArrayAftersetExternalIds.toString().contains("{\"source\":\"adserver.org\",\"id\":\"sdksettings-userid-ttd-foobar\",\"rti_partner\":\"TDID\"}"));
        assertTrue(euidArrayAftersetExternalIds.toString().contains("{\"source\":\"uidapi.com\",\"id\":\"sdksettings-userid-uid2-foobar\",\"rti_partner\":\"UID2\"}"));
        assertTrue(euidArrayAftersetExternalIds.toString().contains("{\"source\":\"Generic Source\",\"id\":\"sdksettings-userid-generic-foobar\"}"));

        // setExternalIds Map and later reset it and make sure  euid is not present in the POST DATA
        SDKSettings.setUserIds(userIds);
        SDKSettings.setUserIds(null);
        executionSteps();
        JSONObject postDataReset = inspectPostData();
        assertFalse(postDataReset.has("eids"));

    }

    /**
     * Test User Id comparison with parameters in /ut request body
     *
     * @throws Exception
     */
    @Test
    public void testUserIdsComparison() throws Exception {
        ANUserId tradeDeskUserID = new ANUserId(ANUserId.Source.THE_TRADE_DESK, "sdksettings-userid-ttd-foobar");
        ANUserId tradeDeskUserIDDup = new ANUserId(ANUserId.Source.THE_TRADE_DESK, "sdksettings-userid-ttd-foobar");
        assertTrue(tradeDeskUserID.equals(tradeDeskUserIDDup));

        ANUserId criteoUserId = new ANUserId(ANUserId.Source.CRITEO, "sdksettings-userid-Criteo-foobar");
        ANUserId criteoUserIdDup = new ANUserId(ANUserId.Source.CRITEO, "sdksettings-userid-Criteo-foobar");
        assertTrue(criteoUserId.equals(criteoUserIdDup));

        ANUserId netIdUserID = new ANUserId(ANUserId.Source.NETID, "sdksettings-userid-netid-foobar");
        ANUserId netIdUserIDDup = new ANUserId(ANUserId.Source.NETID, "sdksettings-userid-netid-foobar");
        assertTrue(netIdUserID.equals(netIdUserIDDup));

        ANUserId liveRampUserID = new ANUserId(ANUserId.Source.LIVERAMP, "sdksettings-userid-liveramp-foobar");
        ANUserId liveRampUserIDDup = new ANUserId(ANUserId.Source.LIVERAMP, "sdksettings-userid-liveramp-foobar");
        assertTrue(liveRampUserID.equals(liveRampUserIDDup));

        ANUserId UID2UserId = new ANUserId(ANUserId.Source.UID2, "sdksettings-userid-uid2-foobar");
        ANUserId UID2UserIdDup = new ANUserId(ANUserId.Source.UID2, "sdksettings-userid-uid2-foobar");
        assertTrue(UID2UserId.equals(UID2UserIdDup));

        ANUserId genericUserID = new ANUserId("Generic Source", "sdksettings-userid-generic-foobar");
        ANUserId genericUserIDDup = new ANUserId("Generic Source", "sdksettings-userid-generic-foobar");
        assertTrue(genericUserID.equals(genericUserIDDup));
    }

    /**
     * Test User Id toString()
     *
     * @throws Exception
     */
    @Test
    public void testUserIdsToString() throws Exception {
        ANUserId tradeDeskUserID = new ANUserId(ANUserId.Source.THE_TRADE_DESK, "sdksettings-userid-ttd-foobar");
        ANUserId criteoUserId = new ANUserId(ANUserId.Source.CRITEO, "sdksettings-userid-Criteo-foobar");
        ANUserId netIdUserID = new ANUserId(ANUserId.Source.NETID, "sdksettings-userid-netid-foobar");
        ANUserId liveRampUserID = new ANUserId(ANUserId.Source.LIVERAMP, "sdksettings-userid-liveramp-foobar");
        ANUserId UID2UserId = new ANUserId(ANUserId.Source.UID2, "sdksettings-userid-uid2-foobar");
        ANUserId genericUserID = new ANUserId("Generic Source", "sdksettings-userid-generic-foobar");

        assertEquals(criteoUserId.toString(), "{\"source\":\"criteo.com\",\"id\":\"sdksettings-userid-Criteo-foobar\"}");
        assertEquals(netIdUserID.toString(), "{\"source\":\"netid.de\",\"id\":\"sdksettings-userid-netid-foobar\"}");
        assertEquals(liveRampUserID.toString(), "{\"source\":\"liveramp.com\",\"id\":\"sdksettings-userid-liveramp-foobar\"}");
        assertEquals(tradeDeskUserID.toString(), "{\"source\":\"adserver.org\",\"id\":\"sdksettings-userid-ttd-foobar\"}");
        assertEquals(UID2UserId.toString(), "{\"source\":\"uidapi.com\",\"id\":\"sdksettings-userid-uid2-foobar\"}");
        assertEquals(genericUserID.toString(), "{\"source\":\"Generic Source\",\"id\":\"sdksettings-userid-generic-foobar\"}");
    }



    @Override
    public void tearDown() {
        super.tearDown();
        SDKSettings.setOMEnabled(true);
        ANGDPRSettings.reset(activity);
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

    private boolean doesIABSupportExist(JSONObject postData) throws JSONException {
        System.out.println("Checking if iab_support exists...");
        if (postData.has("iab_support")) {
            return true;
        }
        return false;
    }

    private boolean doesOMIDFrameworkExist(JSONObject postData, String omidFramework) throws JSONException {
        System.out.println("Checking if "+omidFramework+" exists...");
        if (postData.has(omidFramework)) {
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

    /**
     * Set the consent string in the SDK
     *
     * @param gppString
     */
    private void setGPPString(Context context, String gppString) {
        if(!TextUtils.isEmpty(gppString) && context != null) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("IABGPP_HDR_GppString", gppString).apply();
        }
    }


    /**
     * Set the consentRequired value in the SDK
     *
     * @param gppSid underscore (_) separated sid string
     */
    private void setGPPSID(Context context, String gppSid) {
        if (context != null) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("IABGPP_GppSID", gppSid).apply();
        }
    }
}
