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

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;


/**
 * This tests if the API's in BannerAdView are functioning as expected.
 */
@Config(sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class,
                ShadowWebView.class, ShadowWebSettings.class, ShadowSettings.class, ShadowLog.class})
@RunWith(RobolectricTestRunner.class)
public class BannerAdViewTest extends BaseRoboTest {

    BannerAdView bannerAdView;
    @Override
    public void setup() {
        super.setup();
        bannerAdView = new BannerAdView(activity);
        bannerAdView.setPlacementID("123456");
        bannerAdView.setAdSize(320, 50);
    }


    /******************* AdSize Tests START********************************************************/
    @Test
    public void testSetAdSize(){

        // Only Set AdSize
        setAdSize();
        assertSetAdSize();


        // Test setAdSize over writes the values previously set using setAdSizesArray
        setAdSizesArray();
        setAdSize();
        assertSetAdSize();


        // setAdSize over writes the values previously set using setMaxSizeArray
        setMazSize();
        setAdSize();
        assertSetAdSize();
    }


    @Test
    public void testSetAdSizesArray(){

        setAdSizesArray();
        assertSetAdSizesArray();


        // setAdSizesArray over writes the values previously set using setAdSize
        setAdSize();
        setAdSizesArray();
        assertSetAdSizesArray();


        // setAdSizesArray over writes the values previously set using setMazSize
        setMazSize();
        setAdSizesArray();
        assertSetAdSizesArray();
    }



    @Test
    public void testSetMaxSize(){
        setMazSize();
        assertSetMaxSize();


        // setMazSize over writes the values previously set using setAdSize
        setAdSize();
        setMazSize();
        assertSetMaxSize();


        // setMazSize over writes the values previously set using setAdSizesArray
        setAdSizesArray();
        setMazSize();
        assertSetMaxSize();


    }

    // Tests the functionality of the public facing API for enabling the Lazy Load works as expected
    @Test
    public void testEnableLazyLoad(){
        assertFalse(bannerAdView.isLazyLoadEnabled());
        assertTrue(bannerAdView.enableLazyLoad());
        assertTrue(bannerAdView.isLazyLoadEnabled());
    }

    @Test
    public void testEnableLazyLoadWhenAlreadyEnabled(){
        assertFalse(bannerAdView.isLazyLoadEnabled());
        bannerAdView.enableLazyLoad();
        assertTrue(bannerAdView.isLazyLoadEnabled());
        // This call to enableLazyLoad does not make any changes (as the Lazy Load has already been enabled), and returns false
        assertFalse(bannerAdView.enableLazyLoad());
        assertTrue(bannerAdView.isLazyLoadEnabled());
    }

    @Test
    public void testEnableLazyLoadWhenAdRequestHasAlreadyBeenStarted(){
        assertFalse(bannerAdView.isLazyLoadEnabled());
        bannerAdView.loadAd();
        // Does not enable the Lazy Load as the AdRequest is alredy in progress
        assertFalse(bannerAdView.enableLazyLoad());
        assertFalse(bannerAdView.isLazyLoadEnabled());
    }

    /**
     * Validates the Traffic Source in the request
     *
     * @throws Exception
     */
    @Test
    public void testTrafficSourceCode() {
        assertNull(bannerAdView.getTrafficSourceCode());
        bannerAdView.setTrafficSourceCode("Xandr");
        assertEquals("Xandr", bannerAdView.getTrafficSourceCode());
    }

    /**
     * Validates the Ext Inv Code in the request
     *
     * @throws Exception
     */
    @Test
    public void testExtInvCode() {
        assertNull(bannerAdView.getExtInvCode());
        bannerAdView.setExtInvCode("Xandr");
        assertEquals("Xandr", bannerAdView.getExtInvCode());
    }

    private void setAdSize(){
        bannerAdView.setAdSize(720,90);
    }

    private void setAdSizesArray(){
        ArrayList<AdSize> adSizeArrayList = new ArrayList<AdSize>();
        adSizeArrayList.add(new AdSize(10,10));
        adSizeArrayList.add(new AdSize(320,50));
        adSizeArrayList.add(new AdSize(300,250));
        bannerAdView.setAdSizes(adSizeArrayList);
    }


    private void setMazSize(){
        bannerAdView.setMaxSize(1080,720);
    }



    private void assertSetAdSize(){
        assertTrue(bannerAdView.getAdWidth() == 720);
        assertTrue(bannerAdView.getAdHeight() == 90);
        assertTrue(bannerAdView.getAdSizes().size() == 1);
        assertTrue(bannerAdView.getAdSizes().get(0).width() == 720);
        assertTrue(bannerAdView.getAdSizes().get(0).height() == 90);
        assertTrue(bannerAdView.getMaxWidth() == -1);
        assertTrue(bannerAdView.getMaxHeight() == -1);
    }


    private void assertSetAdSizesArray(){
        assertTrue(bannerAdView.getAdWidth() == 10);
        assertTrue(bannerAdView.getAdHeight() == 10);

        assertTrue(bannerAdView.getAdSizes().size() == 3);
        assertTrue(bannerAdView.getAdSizes().get(0).width() == 10);
        assertTrue(bannerAdView.getAdSizes().get(0).height() == 10);
        assertTrue(bannerAdView.getAdSizes().get(1).width() == 320);
        assertTrue(bannerAdView.getAdSizes().get(1).height() == 50);
        assertTrue(bannerAdView.getAdSizes().get(2).width() == 300);
        assertTrue(bannerAdView.getAdSizes().get(2).height() == 250);

        assertTrue(bannerAdView.getMaxWidth() == -1);
        assertTrue(bannerAdView.getMaxHeight() == -1);
    }

    private void assertSetMaxSize(){
        assertTrue(bannerAdView.getAdWidth() == -1);
        assertTrue(bannerAdView.getAdHeight() == -1);
        assertTrue(bannerAdView.getAdSizes().size() == 0);

        assertTrue(bannerAdView.getMaxWidth() == 1080);
        assertTrue(bannerAdView.getMaxHeight() == 720);
    }

    /******************* AdSize Tests END********************************************************/
}