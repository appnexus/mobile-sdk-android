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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowWebView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;


/**
 * This tests if the API's in ANMultiAdRequest are functioning as expected.
 */
@Config(sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class,
                ShadowWebView.class, ShadowWebSettings.class, ShadowSettings.class, ShadowLog.class})
@RunWith(RobolectricTestRunner.class)
public class ANMultiAdRequestApiTest extends BaseRoboTest {

    ANMultiAdRequest anMultiAdRequest;
    BannerAdView bav;

    @Override
    public void setup() {
        super.setup();
        anMultiAdRequest = new ANMultiAdRequest(activity, 100, 1234, null);
        bav = new BannerAdView(activity);
    }

    @Test
    public void testGetMemberId() {
        assertEquals(100, anMultiAdRequest.getMemberId());
    }

    @Test
    public void testGetPublisherID() {
        assertEquals(1234, anMultiAdRequest.getPublisherId());
    }

    @Test
    public void testDefaultGetPublisherID() {
        anMultiAdRequest = new ANMultiAdRequest(activity, 100, null);
        assertEquals(0, anMultiAdRequest.getPublisherId());
        anMultiAdRequest = new ANMultiAdRequest(activity, 100, null, false);
        assertEquals(0, anMultiAdRequest.getPublisherId());
    }

    @Test
    public void testAddAdUnit() {
        assertEquals(anMultiAdRequest.getAdUnitList().size(), 0);
        anMultiAdRequest.addAdUnit(bav);
        ArrayList<WeakReference<Ad>> adUnitList = anMultiAdRequest.getAdUnitList();
        assertEquals(adUnitList.size(), 1);
        for (WeakReference<Ad> adWeakReference : adUnitList) {
            assertEquals(adWeakReference.get(), bav);
        }
    }

    @Test
    public void testNullAddAdUnit() {
        assertEquals(anMultiAdRequest.getAdUnitList().size(), 0);
        anMultiAdRequest.addAdUnit(null);
        ArrayList<WeakReference<Ad>> adUnitList = anMultiAdRequest.getAdUnitList();
        assertEquals(adUnitList.size(), 0);
    }

    @Test
    public void testAlreadyInProgressAddAdUnit() {
        assertEquals(anMultiAdRequest.getAdUnitList().size(), 0);
        anMultiAdRequest.addAdUnit(bav);
        ArrayList<WeakReference<Ad>> adUnitList = anMultiAdRequest.getAdUnitList();
        assertEquals(adUnitList.size(), 1);
        anMultiAdRequest.load();
        assertFalse(anMultiAdRequest.addAdUnit(new InterstitialAdView(activity)));
        adUnitList = anMultiAdRequest.getAdUnitList();
        assertEquals(adUnitList.size(), 1);
    }

    @Test
    public void testDifferentMemberIdAddAdUnit() {
        assertEquals(anMultiAdRequest.getAdUnitList().size(), 0);
        bav.setInventoryCodeAndMemberID(123, "1234");
        assertFalse(anMultiAdRequest.addAdUnit(bav));
        ArrayList<WeakReference<Ad>> adUnitList = anMultiAdRequest.getAdUnitList();
        assertEquals(0, adUnitList.size());
    }

    @Test
    public void testDuplicateAddAdUnit() {
        assertEquals(anMultiAdRequest.getAdUnitList().size(), 0);
        anMultiAdRequest.addAdUnit(bav);
        assertFalse(anMultiAdRequest.addAdUnit(bav));
        ArrayList<WeakReference<Ad>> adUnitList = anMultiAdRequest.getAdUnitList();
        assertEquals(adUnitList.size(), 1);
        for (WeakReference<Ad> adWeakReference : adUnitList) {
            assertEquals(adWeakReference.get(), bav);
        }
    }

    @Test
    public void testAddAdUnitBannerAutoRefreshInterval() {
        assertEquals(anMultiAdRequest.getAdUnitList().size(), 0);
        anMultiAdRequest.addAdUnit(bav);
        assertFalse(anMultiAdRequest.addAdUnit(bav));
        ArrayList<WeakReference<Ad>> adUnitList = anMultiAdRequest.getAdUnitList();
        assertEquals(adUnitList.size(), 1);
        for (WeakReference<Ad> adWeakReference : adUnitList) {
            assertEquals(adWeakReference.get(), bav);
        }
        assertEquals(0, bav.getAutoRefreshInterval());
    }

    @Test
    public void testRemoveAdUnit() {
        assertEquals(anMultiAdRequest.getAdUnitList().size(), 0);
        anMultiAdRequest.addAdUnit(bav);
        assertEquals(anMultiAdRequest.getAdUnitList().size(), 1);
        anMultiAdRequest.removeAdUnit(bav);
        assertEquals(anMultiAdRequest.getAdUnitList().size(), 0);
    }

    @Test
    public void testPostMARLoadRemoveAdUnit() {
        assertEquals(anMultiAdRequest.getAdUnitList().size(), 0);
        anMultiAdRequest.addAdUnit(bav);
        assertEquals(anMultiAdRequest.getAdUnitList().size(), 1);
        anMultiAdRequest.load();
        anMultiAdRequest.onMARLoadCompleted();
        anMultiAdRequest.removeAdUnit(bav);
        assertEquals(anMultiAdRequest.getAdUnitList().size(), 0);
    }

    @Test
    public void testAlreadyInProgressRemoveAdUnit() {
        assertEquals(anMultiAdRequest.getAdUnitList().size(), 0);
        anMultiAdRequest.addAdUnit(bav);
        ArrayList<WeakReference<Ad>> adUnitList = anMultiAdRequest.getAdUnitList();
        assertEquals(adUnitList.size(), 1);
        anMultiAdRequest.load();
        anMultiAdRequest.removeAdUnit(bav);
        adUnitList = anMultiAdRequest.getAdUnitList();
        assertEquals(adUnitList.size(), 1);
    }

    @Test
    public void testRemoveNullAdUnit() {
        assertEquals(anMultiAdRequest.getAdUnitList().size(), 0);
        anMultiAdRequest.removeAdUnit(null);
        assertEquals(anMultiAdRequest.getAdUnitList().size(), 0);
    }


    @Test
    public void testUserInfo(){

        // Assert Default UserInfo
        assertDefaultUserInfo();

        // Set User Info
        setUserInfo();

        // Assert User Info
        assertUserInfo();

    }

    @Test
    public void testCustomKeywords(){

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

    private void clearCustomKeywords() {
        anMultiAdRequest.clearCustomKeywords();
    }

    private void removeCustomKeyword() {
        anMultiAdRequest.removeCustomKeyword("key3");
    }

    private void assertAddedCustomKeywords() {
        assertEquals(anMultiAdRequest.getCustomKeywords().size(), 3);
        assertEquals(anMultiAdRequest.getCustomKeywords().get(0).first, "key1");
        assertEquals(anMultiAdRequest.getCustomKeywords().get(0).second, "value1");
        assertEquals(anMultiAdRequest.getCustomKeywords().get(1).first, "key2");
        assertEquals(anMultiAdRequest.getCustomKeywords().get(1).second, "value2");
        assertEquals(anMultiAdRequest.getCustomKeywords().get(2).first, "key3");
        assertEquals(anMultiAdRequest.getCustomKeywords().get(2).second, "value3");
    }

    private void assertRemovedCustomKeywords() {
        assertEquals(anMultiAdRequest.getCustomKeywords().size(), 2);
        assertEquals(anMultiAdRequest.getCustomKeywords().get(0).first, "key1");
        assertEquals(anMultiAdRequest.getCustomKeywords().get(0).second, "value1");
        assertEquals(anMultiAdRequest.getCustomKeywords().get(1).first, "key2");
        assertEquals(anMultiAdRequest.getCustomKeywords().get(1).second, "value2");
    }

    private void assertClearedCustomKeywords() {
        assertEquals(anMultiAdRequest.getCustomKeywords().size(), 0);
    }

    private void setCustomKeywords() {
        anMultiAdRequest.addCustomKeywords("key1", "value1");
        anMultiAdRequest.addCustomKeywords("key2", "value2");
        anMultiAdRequest.addCustomKeywords("key3", "value3");
    }

    private void assertDefaultCustomKeywords() {
        assertEquals(anMultiAdRequest.getCustomKeywords().size(), 0);
    }


    private void setUserInfo() {
        setAge();
        setGender();
        setExternalUId();
    }

    private void assertDefaultUserInfo() {
        assertDefaultAge();
        assertDefaultGender();
        assertDefaultExternalUId();
    }

    private void assertDefaultAge() {
        assertNull(anMultiAdRequest.getAge());
    }

    private void assertDefaultGender() {
        assertEquals(anMultiAdRequest.getGender(), AdView.GENDER.UNKNOWN);
    }

    private void assertDefaultExternalUId() {
        assertNull(anMultiAdRequest.getExternalUid());
    }

    private void assertUserInfo() {
        assertAge();
        assertGender();
        assertExternalUId();
    }

    // Tests the value of ExternalUid
    @Test
    public void testSetExternalUId(){
        assertDefaultExternalUId();
        setExternalUId();
        assertExternalUId();

    }

    private void setExternalUId(){
        anMultiAdRequest.setExternalUid("AppNexus");
    }

    private void setAge() {
        anMultiAdRequest.setAge("20");
    }

    private void setGender() {
        anMultiAdRequest.setGender(AdView.GENDER.FEMALE);
    }

    private void assertExternalUId(){
        assertNotNull(anMultiAdRequest.getExternalUid());
        assertEquals(anMultiAdRequest.getExternalUid(), "AppNexus");
    }

    private void assertAge() {
        assertNotNull(anMultiAdRequest.getAge());
        assertEquals(anMultiAdRequest.getAge(), "20");
    }

    private void assertGender() {
        assertNotNull(anMultiAdRequest.getGender());
        assertEquals(anMultiAdRequest.getGender(), AdView.GENDER.FEMALE);
    }

}