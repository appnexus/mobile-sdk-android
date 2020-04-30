/*
 *    Copyright 2015 APPNEXUS INC
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

import com.appnexus.opensdk.shadows.ShadowAsyncTaskNoExecutor;
import com.appnexus.opensdk.shadows.ShadowSettings;
import com.appnexus.opensdk.shadows.ShadowWebSettings;
import com.appnexus.opensdk.ut.UTRequestParameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowWebView;

import java.lang.reflect.Method;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertTrue;


/**
 * This tests if the options set on a ANMultiAdRequest are represented in the UT Post data in the right format.
 */
@Config(sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class,
                ShadowWebView.class, ShadowWebSettings.class, ShadowSettings.class, ShadowLog.class})
@RunWith(RobolectricTestRunner.class)
public class ANMultiAdRequestToRequestParametersTest extends BaseRoboTest {

    ANMultiAdRequest anMultiAdRequest;
    UTRequestParameters requestParameters;
    private BannerAdView bav;

    @Override
    public void setup() {
        super.setup();
        anMultiAdRequest = new ANMultiAdRequest(activity, 100, 123, null);
        bav = new BannerAdView(activity);
        bav.setAdSize(320, 50);
        anMultiAdRequest.addAdUnit(bav);
        requestParameters = anMultiAdRequest.getRequestParameters();
    }

    @Test
    public void testMemberId() {
        assertMARGlobalMemberIdInUTRequestParams();
    }

    @Test
    public void testPublisherId() {
        assertMARGlobalPublisherIdInUTRequestParams();
    }

    @Test
    public void testUserInfo() {

        // Assert Default UserInfo
        assertDefaultUserInfo();

        // Set User Info
        setUserInfo();

        // Assert User Info
        assertUserInfoRequestParameters();
    }

    @Test
    public void testMARGlobalCustomKeywords() {

        // Assert Default Custom Keywords
        assertDefaultCustomKeywords();

        // Set Custom Keywords
        setCustomKeywords();

        // Assert Custom Keywords
        assertAddedCustomKeywords();

        // Remove Custom Keywords
        removeCustomKeyword();

        // Assert Custom Keywords
        assertRemovedCustomKeywords();

        // Clear Custom Keywords
        clearCustomKeywords();

        // Assert Custom Keywords
        assertClearedCustomKeywords();

    }

    @Test
    public void testMARAdUnitCustomKeywords() {
        // Assert Default Custom Keywords
        assertDefaultCustomKeywords();

        // Set Banner Custom Keywords
        setBannerCustomKeywords();

        // Assert Custom Keywords
        assertAddedBannerCustomKeywords();
    }

    @Test
    public void testAdUnitList() {
        anMultiAdRequest.addAdUnit(bav);
        anMultiAdRequest.addAdUnit(new InterstitialAdView(activity));
        assertTagCountInUTRequestParams();
    }

    @Test
    public void testAttachedBannerAdViewToMarIndividualAdLoad() {
        anMultiAdRequest.addAdUnit(bav);
        anMultiAdRequest.addAdUnit(new InterstitialAdView(activity));
        assertBannerTagCountInUTRequestParams();
    }

    @Test
    public void testAttachedBannerAdViewMemberIdToMarIndividualAdLoad() {
        anMultiAdRequest.addAdUnit(bav);
        assertAttachedBannerGlobalMemberIdInUTRequestParams();
    }

    @Test
    public void testAttachedThenRemoveBannerAdViewMemberIdToMarIndividualAdLoad() {
        anMultiAdRequest.addAdUnit(bav);
        bav.setInventoryCodeAndMemberID(2000, "appnexus");
        assertAttachedBannerGlobalMemberIdInUTRequestParams();
        anMultiAdRequest.removeAdUnit(bav);
        assertRemovedBannerGlobalMemberIdInUTRequestParams();
    }

    @Test
    public void testAttachedBannerAdViewPublisherIdToMarIndividualAdLoad() {
        anMultiAdRequest.addAdUnit(bav);
        assertAttachedBannerGlobalPublisherIdInUTRequestParams();
    }

    @Test
    public void testAttachedThenRemoveBannerAdViewPublisherToMarIndividualAdLoad() {
        anMultiAdRequest.addAdUnit(bav);
        bav.setPublisherId(1234);
        assertAttachedBannerGlobalPublisherIdInUTRequestParams();
        anMultiAdRequest.removeAdUnit(bav);
        assertRemovedBannerGlobalPublisherIdInUTRequestParams();
    }

    @Test
    public void testAttachedThenKilledBannerAdViewMemberIdToMarIndividualAdLoad() {
        anMultiAdRequest.addAdUnit(bav);
        bav.setInventoryCodeAndMemberID(2000, "appnexus");
        assertAttachedBannerGlobalMemberIdInUTRequestParams();
        killMarInstance();
        assertRemovedBannerGlobalMemberIdInUTRequestParams();
    }

    @Test
    public void testAttachedBannerGlobalCustomKeywords() {

        // Assert Default Custom Keywords
        assertAttachedBannerDefaultCustomKeywords();

        // Set MAR Custom Keywords
        setCustomKeywords();

        // Set Banner Custom Keywords
        setBannerCustomKeywords();

        // Assert Custom Keywords
        assertAttachedBannerAddedCustomKeywords();

        // Assert Custom Keywords
        assertAttachedBannerAddedMARCustomKeywords();

    }

    @Test
    public void testAttachedThenRemovedBannerGlobalCustomKeywords() {

        // Assert Default Custom Keywords
        assertAttachedBannerDefaultCustomKeywords();

        // Set MAR Custom Keywords
        setCustomKeywords();

        // Set Banner Custom Keywords
        setBannerCustomKeywords();

        // Assert Custom Keywords
        assertAttachedBannerAddedCustomKeywords();

        // Assert Custom Keywords
        assertAttachedBannerAddedMARCustomKeywords();

        anMultiAdRequest.removeAdUnit(bav);

        // Assert Custom Keywords
        assertAttachedBannerAddedCustomKeywords();

        // Assert Custom Keywords
        assertRemovedBannerMARCustomKeywords();

    }

    @Test
    public void testAttachedThenKilledBannerGlobalCustomKeywords() {

        // Assert Default Custom Keywords
        assertAttachedBannerDefaultCustomKeywords();

        // Set MAR Custom Keywords
        setCustomKeywords();

        // Set Banner Custom Keywords
        setBannerCustomKeywords();

        // Assert Custom Keywords
        assertAttachedBannerAddedCustomKeywords();

        // Assert Custom Keywords
        assertAttachedBannerAddedMARCustomKeywords();

        killMarInstance();

        // Assert Custom Keywords
        assertAttachedBannerAddedCustomKeywords();

        // Assert Custom Keywords
        assertRemovedBannerMARCustomKeywords();

    }



    @Test
    public void testAttachedBannerUserInfo() {

        // Assert Default UserInfo
        assertAttachedBannerDefaultUserInfo();

        // Set User Info
        setUserInfo();

        // Assert User Info
        assertAttachedBannerUserInfoRequestParameters();
    }

    @Test
    public void testAttachedThenRemovedBannerUserInfo() {

        // Assert Default UserInfo
        assertAttachedBannerDefaultUserInfo();

        // Set User Info
        setUserInfo();

        // Assert User Info
        assertAttachedBannerUserInfoRequestParameters();

        anMultiAdRequest.removeAdUnit(bav);

        // Assert Default UserInfo
        assertAttachedBannerDefaultUserInfo();
    }

    @Test
    public void testAttachedThenKilledBannerUserInfo() {

        // Assert Default UserInfo
        assertAttachedBannerDefaultUserInfo();

        // Set User Info
        setUserInfo();

        // Assert User Info
        assertAttachedBannerUserInfoRequestParameters();

        killMarInstance();

        // Assert Default UserInfo
        assertAttachedBannerDefaultUserInfo();
    }

    private void killMarInstance() {
        anMultiAdRequest = null;
        System.gc();
    }

    private void assertMARGlobalMemberIdInUTRequestParams() {
        String postData = getRequestParametersPostData();
        assertTrue(postData.contains("\"member_id\":100"));
        int memberId = -1;
        try {
            JSONObject json = new JSONObject(postData);
            memberId = json.getInt("member_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        assertEquals(100, memberId);
    }

    private void assertMARGlobalPublisherIdInUTRequestParams() {
        String postData = getRequestParametersPostData();
        assertTrue(postData.contains("\"publisher_id\":123"));
        int publisherId = -1;
        try {
            JSONObject json = new JSONObject(postData);
            publisherId = json.getInt("publisher_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        assertEquals(123, publisherId);
    }

    private void assertAttachedBannerGlobalMemberIdInUTRequestParams() {
        String postData = getBannerRequestParametersPostData();
        assertTrue(postData.contains("\"member_id\":100"));
        int memberId = -1;
        try {
            JSONObject json = new JSONObject(postData);
            memberId = json.getInt("member_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        assertEquals(100, memberId);
    }

    private void assertAttachedBannerGlobalPublisherIdInUTRequestParams() {
        String postData = getBannerRequestParametersPostData();
        assertFalse(postData.contains("\"publisher_id\":1234"));
        assertMARGlobalPublisherIdInUTRequestParams();
    }

    private void assertRemovedBannerGlobalMemberIdInUTRequestParams() {
        String postData = getBannerRequestParametersPostData();
        assertTrue(postData.contains("\"member_id\":2000"));
        int memberId = -1;
        try {
            JSONObject json = new JSONObject(postData);
            memberId = json.getInt("member_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        assertEquals(2000, memberId);
    }

    private void assertRemovedBannerGlobalPublisherIdInUTRequestParams() {
        String postData = getBannerRequestParametersPostData();
        assertTrue(postData.contains("\"publisher_id\":1234"));
        int publisherId = -1;
        try {
            JSONObject json = new JSONObject(postData);
            publisherId = json.getInt("publisher_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        assertEquals(1234, publisherId);
    }

    private void assertTagCountInUTRequestParams() {
        String postData = getRequestParametersPostData();
        JSONObject tag1 = null, tag2 = null;
        try {
            JSONObject json = new JSONObject(postData);
            JSONArray tags = json.getJSONArray("tags");
            assertEquals(2, tags.length());
            tag1 = (JSONObject) tags.get(0);
            tag2 = (JSONObject) tags.get(1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        assertTrue(tag1.toString().contains("\"allowed_media_types\":[1]"));
        assertTrue(tag2.toString().contains("\"allowed_media_types\":[1,3]"));
    }

    private void assertBannerTagCountInUTRequestParams() {
        String postData = getBannerRequestParametersPostData();
        JSONObject tag1 = null;
        try {
            JSONObject json = new JSONObject(postData);
            JSONArray tags = json.getJSONArray("tags");
            assertEquals(1, tags.length());
            tag1 = (JSONObject) tags.get(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        assertTrue(tag1.toString().contains("\"allowed_media_types\":[1]"));
    }

    private void clearCustomKeywords() {
        anMultiAdRequest.clearCustomKeywords();
    }

    private void removeCustomKeyword() {
        anMultiAdRequest.removeCustomKeyword("key3");
    }

    private void assertAddedCustomKeywords() {
        String postData = getRequestParametersPostData();
        JSONArray keywords = null, val1 = null;
        String key1 = null;
        try {
            JSONObject json = new JSONObject(postData);
            keywords = json.getJSONArray("keywords");
            JSONObject firstKeyword = (JSONObject) keywords.get(0);
            key1 = firstKeyword.getString("key");
            val1 = firstKeyword.getJSONArray("value");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        assertNotNull(keywords);
        assertEquals(3, keywords.length());
        assertNotNull(key1);
        assertEquals("key1", key1);
        assertNotNull(val1);
        assertEquals(1, val1.length());
        try {
            assertEquals("value1", val1.get(0));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        assertTrue(postData.contains("\"keywords\":[{\"key\":\"key1\",\"value\":[\"value1\"]},{\"key\":\"key2\",\"value\":[\"value2\"]},{\"key\":\"key3\",\"value\":[\"value3\"]}]"));
    }

    private void assertAddedBannerCustomKeywords() {
        String postData = getRequestParametersPostData();
        JSONArray keywords = null, val1 = null;
        String key1 = null;
        JSONObject tag = null;
        try {
            JSONObject json = new JSONObject(postData);
            JSONArray tags = json.getJSONArray("tags");
            tag = (JSONObject) tags.get(0);
            keywords = tag.getJSONArray("keywords");
            JSONObject firstKeyword = (JSONObject) keywords.get(0);
            key1 = firstKeyword.getString("key");
            val1 = firstKeyword.getJSONArray("value");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        assertNotNull(keywords);
        assertEquals(2, keywords.length());
        assertNotNull(key1);
        assertEquals("keyBanner1", key1);
        assertNotNull(val1);
        assertEquals(1, val1.length());
        try {
            assertEquals("Banner1", val1.get(0));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        assertTrue(tag.toString().contains("\"keywords\":[{\"key\":\"keyBanner1\",\"value\":[\"Banner1\"]},{\"key\":\"keyBanner2\",\"value\":[\"Banner2\"]}]"));
    }

    private void assertAttachedBannerAddedCustomKeywords() {
        String postData = getBannerRequestParametersPostData();
        JSONArray keywords = null, val1 = null;
        String key1 = null;
        JSONObject tag = null;
        try {
            JSONObject json = new JSONObject(postData);
            JSONArray tags = json.getJSONArray("tags");
            tag = (JSONObject) tags.get(0);
            keywords = tag.getJSONArray("keywords");
            JSONObject firstKeyword = (JSONObject) keywords.get(0);
            key1 = firstKeyword.getString("key");
            val1 = firstKeyword.getJSONArray("value");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        assertNotNull(keywords);
        assertEquals(2, keywords.length());
        assertNotNull(key1);
        assertEquals("keyBanner1", key1);
        assertNotNull(val1);
        assertEquals(1, val1.length());
        try {
            assertEquals("Banner1", val1.get(0));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        assertTrue(tag.toString().contains("\"keywords\":[{\"key\":\"keyBanner1\",\"value\":[\"Banner1\"]},{\"key\":\"keyBanner2\",\"value\":[\"Banner2\"]}]"));
    }

    private void assertAttachedBannerAddedMARCustomKeywords() {
        String postData = getBannerRequestParametersPostData();
        JSONArray keywords = null, val1 = null;
        String key1 = null;
        try {
            JSONObject json = new JSONObject(postData);
            keywords = json.getJSONArray("keywords");
            JSONObject firstKeyword = (JSONObject) keywords.get(0);
            key1 = firstKeyword.getString("key");
            val1 = firstKeyword.getJSONArray("value");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        assertNotNull(keywords);
        assertEquals(3, keywords.length());
        assertNotNull(key1);
        assertEquals("key1", key1);
        assertNotNull(val1);
        assertEquals(1, val1.length());
        try {
            assertEquals("value1", val1.get(0));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        assertTrue(postData.contains("\"keywords\":[{\"key\":\"key1\",\"value\":[\"value1\"]},{\"key\":\"key2\",\"value\":[\"value2\"]},{\"key\":\"key3\",\"value\":[\"value3\"]}]"));
    }


    private void assertRemovedCustomKeywords() {
        String postData = getRequestParametersPostData();
        assertTrue(postData.contains("\"keywords\":[{\"key\":\"key1\",\"value\":[\"value1\"]},{\"key\":\"key2\",\"value\":[\"value2\"]}]"));
    }

    private void assertClearedCustomKeywords() {
        String postData = getRequestParametersPostData();
        assertFalse(postData.contains("\"keywords\""));
    }

    private void setCustomKeywords() {
        anMultiAdRequest.addCustomKeywords("key1", "value1");
        anMultiAdRequest.addCustomKeywords("key2", "value2");
        anMultiAdRequest.addCustomKeywords("key3", "value3");
    }

    private void setBannerCustomKeywords() {
        bav.addCustomKeywords("keyBanner1", "Banner1");
        bav.addCustomKeywords("keyBanner2", "Banner2");
    }

    private void assertDefaultCustomKeywords() {
        String postData = getRequestParametersPostData();
        assertFalse(postData.contains("\"keywords\":[{\"key\":\"key1\",\"value\":[\"value1\"]},{\"key\":\"key2\",\"value\":[\"value2\"]},{\"key\":\"key3\",\"value\":[\"value3\"]}]}"));
    }

    private void assertAttachedBannerDefaultCustomKeywords() {
        String postData = getBannerRequestParametersPostData();
        assertFalse(postData.contains("\"keywords\":[{\"key\":\"keyBanner1\",\"value\":[\"Banner1\"]},{\"key\":\"keyBanner2\",\"value\":[\"Banner2\"]}]"));
        assertFalse(postData.contains("\"keywords\":[{\"key\":\"key1\",\"value\":[\"value1\"]},{\"key\":\"key2\",\"value\":[\"value2\"]},{\"key\":\"key3\",\"value\":[\"value3\"]}]}"));
        assertFalse(postData.contains("\"keywords\""));

    }

    private void assertRemovedBannerMARCustomKeywords() {
        String postData = getBannerRequestParametersPostData();
        assertFalse(postData.contains("\"keywords\":[{\"key\":\"key1\",\"value\":[\"value1\"]},{\"key\":\"key2\",\"value\":[\"value2\"]},{\"key\":\"key3\",\"value\":[\"value3\"]}]}"));
        assertTrue(postData.contains("\"keywords\":[{\"key\":\"keyBanner1\",\"value\":[\"Banner1\"]},{\"key\":\"keyBanner2\",\"value\":[\"Banner2\"]}]"));
    }

    private void setUserInfo() {
        setAge();
        setGender();
        setExternalUId();
    }

    private void assertUserInfoRequestParameters() {
        String postData = getRequestParametersPostData();
        assertTrue(postData.contains("\"user\":{\"age\":20,\"gender\":2,\"language\":\"en\",\"external_uid\":\"AppNexus\"}"));
    }

    private void assertDefaultUserInfo() {
        String postData = getRequestParametersPostData();
        assertTrue(postData.contains("\"user\":{\"gender\":0,\"language\":\"en\"}"));
    }

    private void assertAttachedBannerUserInfoRequestParameters() {
        String postData = getBannerRequestParametersPostData();
        assertTrue(postData.contains("\"user\":{\"age\":20,\"gender\":2,\"language\":\"en\",\"external_uid\":\"AppNexus\"}"));
    }

    private void assertAttachedBannerDefaultUserInfo() {
        String postData = getBannerRequestParametersPostData();
        assertTrue(postData.contains("\"user\":{\"gender\":0,\"language\":\"en\"}"));
    }

    private void setExternalUId() {
        anMultiAdRequest.setExternalUid("AppNexus");
    }

    private void setAge() {
        anMultiAdRequest.setAge("20");
    }

    private void setGender() {
        anMultiAdRequest.setGender(AdView.GENDER.FEMALE);
    }

    private void assertExternalUId() {
        assertNotNull(anMultiAdRequest.getExternalUid());
        assertEquals(anMultiAdRequest.getExternalUid(), "AppNexus");
    }

    public UTRequestParameters getRequestParameters() {
        return requestParameters;
    }


    /**
     * getPostData method is private in UTRequestParameters using reflection to access it.
     * There might be better way of doing this but this works.!!
     *
     * @return
     */
    private String getRequestParametersPostData() {
        String postData = "";
        try {
            Method getPostData = UTRequestParameters.class.getDeclaredMethod("getPostData", null);
            getPostData.setAccessible(true);
            postData = (String) getPostData.invoke(requestParameters, null);
            System.out.println("postData = " + postData);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return postData;
    }

    /**
     * getPostData method is private in UTRequestParameters using reflection to access it.
     * There might be better way of doing this but this works.!!
     *
     * @return
     */
    private String getBannerRequestParametersPostData() {
        String postData = "";
        try {
            Method getPostData = UTRequestParameters.class.getDeclaredMethod("getPostData", null);
            getPostData.setAccessible(true);
            postData = (String) getPostData.invoke(bav.getRequestParameters(), null);
            System.out.println("postData = " + postData);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return postData;
    }
}
