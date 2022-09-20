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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowWebView;

import java.lang.reflect.Method;
import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;


/**
 * This tests if the options set on a BannerAdView are represented in the UT Post data in the right format.
 */
@Config(sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class,
                ShadowWebView.class, ShadowWebSettings.class, ShadowSettings.class, ShadowLog.class})
@RunWith(RobolectricTestRunner.class)
public class BannerAdToRequestParametersTest extends BaseRoboTest {

    BannerAdView bannerAdView;
    UTRequestParameters requestParameters;

    @Override
    public void setup() {
        super.setup();
        bannerAdView = new BannerAdView(activity);
        requestParameters = bannerAdView.requestParameters;

        // This would later be over-ridden by test specific values
        bannerAdView.setAdSize(320,50);

    }

    // Setting setAdSize should do
    // Set primary_size = size
    // Add size to sizes array
    // Set allow_smaller_sizes to false
    @Test
    public void testSetAdSize(){
        setAdSize();
        assertSetAdSize();
    }


    // Setting sizes using setAdSizesArray should set the first size as primary_size.
    // All sizes set should be there in sizes array
    // Set allow_smaller_sizes to false
    @Test
    public void testSetAdSizesArray(){

        setAdSizesArray();
        assertSetAdSizesArray();
    }


    //Setting MAX size should
    //Set primary_size = max_size
    //Add max_size to sizes array
    //Set allow_smaller_sizes to true
    @Test
    public void testSetMaxSize(){
        setMaxSize();
        assertSetMaxSize();
    }




    // Setting setAdSize should reset all the other size params set earlier
    @Test
    public void testSetAdSizeOverRidesEverythingElse(){
        setAdSizesArray();
        setMaxSize();
        setAdSize();
        assertSetAdSize();
    }


    // Setting setAdSize should reset all the other size params set earlier
    @Test
    public void testSetAdSizeArrayOverRidesEverythingElse(){
        setAdSize();
        setMaxSize();
        setAdSizesArray();
        assertSetAdSizesArray();
    }

    // Test setAllowVideo
    @Test
    public void testSetAllowVideo(){
        assertEquals(false,bannerAdView.getAllowVideoDemand());
        String bannerPostData = getRequestParametersPostData();
        assertTrue(bannerPostData.contains("\"allowed_media_types\":[1]"));


        bannerAdView.setAllowVideoDemand(true);
        assertEquals(true,bannerAdView.getAllowVideoDemand());
        String bannerVideoPostData = getRequestParametersPostData();
        assertTrue(bannerVideoPostData.contains("\"allowed_media_types\":[1,4]"));
    }


    // Test setAllowBanner
    @Test
    public void testSetAllowBanner(){
        assertEquals(false,bannerAdView.getAllowVideoDemand());
        String bannerPostData = getRequestParametersPostData();
        assertTrue(bannerPostData.contains("\"allowed_media_types\":[1]"));

        bannerAdView.setAllowBannerDemand(false);
        bannerPostData = getRequestParametersPostData();
        assertTrue(bannerPostData.contains("\"allowed_media_types\":[]"));


        bannerAdView.setAllowVideoDemand(true);
        assertEquals(true,bannerAdView.getAllowVideoDemand());
        bannerPostData = getRequestParametersPostData();
        assertTrue(bannerPostData.contains("\"allowed_media_types\":[4]"));

        bannerAdView.setAllowVideoDemand(false);
        bannerAdView.setAllowNativeDemand(true);
        assertEquals(true,bannerAdView.getAllowNativeDemand());
        bannerPostData = getRequestParametersPostData();
        assertTrue(bannerPostData.contains("\"allowed_media_types\":[12]"));

        bannerAdView.setAllowBannerDemand(true);
        assertEquals(false,bannerAdView.getAllowVideoDemand());
        bannerPostData = getRequestParametersPostData();
        assertTrue(bannerPostData.contains("\"allowed_media_types\":[1,12]"));

        bannerAdView.setAllowHighImpactDemand(true);
        bannerAdView.setAllowNativeDemand(false);
        assertEquals(true,bannerAdView.getAllowHighImpactDemand());
        bannerPostData = getRequestParametersPostData();
        assertTrue(bannerPostData.contains("\"allowed_media_types\":[1,11]"));



    }


    // Test setAllowNative
    @Test
    public void testSetAllowNative(){
        assertEquals(false,bannerAdView.getAllowNativeDemand());
        String bannerPostData = getRequestParametersPostData();
        assertTrue(bannerPostData.contains("\"allowed_media_types\":[1]"));

        bannerAdView.setAllowNativeDemand(true);
        assertEquals(true,bannerAdView.getAllowNativeDemand());
        String bannerNativePostData = getRequestParametersPostData();
        assertTrue(bannerNativePostData.contains("\"allowed_media_types\":[1,12]"));
    }

    // Test setAllowNative and setAllowVideo
    @Test
    public void testSetAllowVideoAndNative(){
        assertEquals(false,bannerAdView.getAllowNativeDemand());
        assertEquals(false,bannerAdView.getAllowVideoDemand());
        String bannerPostData = getRequestParametersPostData();
        assertTrue(bannerPostData.contains("\"allowed_media_types\":[1]"));


        bannerAdView.setAllowNativeDemand(true);
        bannerAdView.setAllowVideoDemand(true);
        assertEquals(true,bannerAdView.getAllowNativeDemand());
        assertEquals(true,bannerAdView.getAllowVideoDemand());
        String bannerNativePostData = getRequestParametersPostData();
        assertTrue(bannerNativePostData.contains("\"allowed_media_types\":[1,4,12]"));
    }

    // Test setAllowHighImpact
    @Test
    public void testSetAllowHighImpact(){
        assertEquals(false,bannerAdView.getAllowHighImpactDemand());
        String bannerPostData = getRequestParametersPostData();
        assertTrue(bannerPostData.contains("\"allowed_media_types\":[1]"));

        bannerAdView.setAllowHighImpactDemand(true);
        assertEquals(true,bannerAdView.getAllowHighImpactDemand());
        String bannerNativePostData = getRequestParametersPostData();
        assertTrue(bannerNativePostData.contains("\"allowed_media_types\":[1,11]"));
    }

    // Test setAllowNative and setAllowHighImpact
    @Test
    public void testSetAllowNativeAndHighImpact(){
        assertEquals(false,bannerAdView.getAllowNativeDemand());
        assertEquals(false,bannerAdView.getAllowHighImpactDemand());
        String bannerPostData = getRequestParametersPostData();
        assertTrue(bannerPostData.contains("\"allowed_media_types\":[1]"));


        bannerAdView.setAllowNativeDemand(true);
        bannerAdView.setAllowHighImpactDemand(true);
        assertEquals(true,bannerAdView.getAllowNativeDemand());
        assertEquals(true,bannerAdView.getAllowHighImpactDemand());
        String bannerNativePostData = getRequestParametersPostData();
        assertTrue(bannerNativePostData.contains("\"allowed_media_types\":[1,11,12]"));
    }

    // Test setAllowVideo and setAllowHighImpact
    @Test
    public void testSetAllowVideoAndHighImpact(){
        assertEquals(false,bannerAdView.getAllowVideoDemand());
        assertEquals(false,bannerAdView.getAllowHighImpactDemand());
        String bannerPostData = getRequestParametersPostData();
        assertTrue(bannerPostData.contains("\"allowed_media_types\":[1]"));


        bannerAdView.setAllowVideoDemand(true);
        bannerAdView.setAllowHighImpactDemand(true);
        assertEquals(true,bannerAdView.getAllowVideoDemand());
        assertEquals(true,bannerAdView.getAllowHighImpactDemand());
        String bannerNativePostData = getRequestParametersPostData();
        assertTrue(bannerNativePostData.contains("\"allowed_media_types\":[1,4,11]"));
    }

    // Test setAllowVideo and setAllowHighImpact
    @Test
    public void testSetAllowVideoNativeAndHighImpact(){
        assertEquals(false,bannerAdView.getAllowNativeDemand());
        assertEquals(false,bannerAdView.getAllowVideoDemand());
        assertEquals(false,bannerAdView.getAllowHighImpactDemand());
        String bannerPostData = getRequestParametersPostData();
        assertTrue(bannerPostData.contains("\"allowed_media_types\":[1]"));

        bannerAdView.setAllowNativeDemand(true);
        bannerAdView.setAllowVideoDemand(true);
        bannerAdView.setAllowHighImpactDemand(true);
        assertEquals(true,bannerAdView.getAllowNativeDemand());
        assertEquals(true,bannerAdView.getAllowVideoDemand());
        assertEquals(true,bannerAdView.getAllowHighImpactDemand());
        String bannerNativePostData = getRequestParametersPostData();
        assertTrue(bannerNativePostData.contains("\"allowed_media_types\":[1,4,11,12]"));
    }


    // Setting MAX size should reset all the other size params set earlier
    @Test
    public void testSetMAXSizeOverRidesEverythingElse(){
        setAdSize();
        setAdSizesArray();
        setMaxSize();
        assertSetMaxSize();
    }

    // Testing the content_url in the post data.
    @Test
    public void testContentUrl(){
        bannerAdView.addCustomKeywords("content_url", "www.appnexus.com");

        String postData = getRequestParametersPostData();
        assertTrue(postData.contains("\"key\":\"content_url\",\"value\":[\"www.appnexus.com\"]"));
        bannerAdView.getRequestParameters().getCustomKeywords().contains("content_url");
    }

    // Testing the force creative Id in the post data.
    @Test
    public void testForceCreativeId(){
        bannerAdView.setForceCreativeId(135482485);

        String postData = getRequestParametersPostData();
        assertTrue(postData.contains("\"force_creative_id\":135482485"));
    }

    @Test
    public void testIsNativeAssemblyRendererEnabled(){
        useNativeRenderer(true);
        assertIsNativeAssemblyRendererEnabled(true);

        useNativeRenderer(false);
        assertIsNativeAssemblyRendererEnabled(false);
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

        String postData = getRequestParametersPostData();
        assertTrue(postData.contains("\"traffic_source_code\":\"Xandr\""));
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

        String postData = getRequestParametersPostData();
        assertTrue(postData.contains("\"ext_inv_code\":\"Xandr\""));
    }

    private void useNativeRenderer(boolean isNativeAssemblyRendererEnabled) {
        bannerAdView.enableNativeRendering(isNativeAssemblyRendererEnabled);
    }

    private void assertIsNativeAssemblyRendererEnabled(boolean isNativeAssemblyRendererEnabled){
        assertEquals(isNativeAssemblyRendererEnabled, bannerAdView.isNativeRenderingEnabled());
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


    private void setMaxSize(){
        bannerAdView.setMaxSize(1080,720);
    }

    private void assertSetAdSize(){
        String postData = getRequestParametersPostData();
        assertTrue(postData.contains("\"primary_size\":{\"width\":720,\"height\":90},"));
        assertTrue(postData.contains("\"sizes\":[{\"width\":720,\"height\":90}],"));
        assertTrue(postData.contains("\"allow_smaller_sizes\":false,"));
    }


    private void assertSetAdSizesArray(){
        String postData = getRequestParametersPostData();
        assertTrue(postData.contains("\"primary_size\":{\"width\":10,\"height\":10},"));
        assertTrue(postData.contains("\"sizes\":[{\"width\":10,\"height\":10},{\"width\":320,\"height\":50},{\"width\":300,\"height\":250}],"));
        assertTrue(postData.contains("\"allow_smaller_sizes\":false,"));
    }

    private void assertSetMaxSize(){
        String postData = getRequestParametersPostData();
        assertTrue(postData.contains("\"primary_size\":{\"width\":1080,\"height\":720},"));
        assertTrue(postData.contains("\"sizes\":[{\"width\":1080,\"height\":720}],"));
        assertTrue(postData.contains("\"allow_smaller_sizes\":true,"));
    }

    public UTRequestParameters getRequestParameters() {
        return requestParameters;
    }


    /**
     * getPostData method is private in UTRequestParameters using reflection to access it.
     * There might be better way of doing this but this works.!!
     * @return
     */
    private String getRequestParametersPostData() {
        String postData = "";
        try {
            Method getPostData = UTRequestParameters.class.getDeclaredMethod("getPostData", null);
            getPostData.setAccessible(true);
            postData = (String) getPostData.invoke(requestParameters, null);
            System.out.println("postData = " + postData);

        }catch (Exception e){
            e.printStackTrace();
        }
        return postData;
    }
}
