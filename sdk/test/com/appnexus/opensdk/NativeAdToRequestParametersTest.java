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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * This tests if the options set on a NativeAdRequest are represented in the UT Post data in the right format.
 */
@Config(sdk = 21,
        shadows = {ShadowAsyncTaskNoExecutor.class,
                ShadowWebView.class, ShadowWebSettings.class, ShadowSettings.class, ShadowLog.class})
@RunWith(RobolectricTestRunner.class)
public class NativeAdToRequestParametersTest extends BaseRoboTest {

    NativeAdRequest nativeAdRequest;

    @Override
    public void setup() {
        super.setup();
        nativeAdRequest = new NativeAdRequest(activity,"123456");

    }

    // Tests the value of ExternalUid
    @Test
    public void testSetExternalUId(){
        setExternalUId();
        assertSetExternalUId();

    }

    // Tests the value of ForceCreativeId
    @Test
    public void testSetForceCreativeId(){
        nativeAdRequest.setForceCreativeId(135482485);
        String postData = getRequestParametersPostData();
        assertTrue(postData.contains("\"force_creative_id\":135482485"));
    }

    // Tests the value of RendererId
    @Test
    public void testSetRendererId(){
        setRendererId();
        assertSetRendererId();
    }

    private void setExternalUId(){
        nativeAdRequest.setExternalUid("AppNexus");
    }

    private void assertSetExternalUId(){
        assertNotNull(nativeAdRequest.getExternalUid());
        assertEquals(nativeAdRequest.getExternalUid(), "AppNexus");

    }

    private void setRendererId(){
        nativeAdRequest.setRendererId(127);
    }

    private void assertSetRendererId(){
        assertEquals(nativeAdRequest.getRendererId(), 127);

    }
    // Primary size is always 1 x1.
    // Size Array = (1x1 size).
    // Allow smaller size should be false.
    @Test
    public void testDefaultSizes(){
        String postData = getRequestParametersPostData();
        assertTrue(postData.contains("\"primary_size\":{\"width\":1,\"height\":1},"));
        assertTrue(postData.contains("\"sizes\":[{\"width\":1,\"height\":1}],"));
        assertTrue(postData.contains("\"allow_smaller_sizes\":false,"));
    }

    // Testing the content_url in the post data.
    @Test
    public void testContentUrl(){
        nativeAdRequest.addCustomKeywords("content_url", "www.appnexus.com");

        String postData = getRequestParametersPostData();
        assertTrue(postData.contains("\"key\":\"content_url\",\"value\":[\"www.appnexus.com\"]"));
        nativeAdRequest.getRequestParameters().getCustomKeywords().contains("content_url");
    }

    /**
     * Validates the Traffic Source in the request
     *
     * @throws Exception
     */
    @Test
    public void testTrafficSourceCode() {
        assertNull(nativeAdRequest.getTrafficSourceCode());
        nativeAdRequest.setTrafficSourceCode("Xandr");
        assertEquals("Xandr", nativeAdRequest.getTrafficSourceCode());

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
        assertNull(nativeAdRequest.getExtInvCode());
        nativeAdRequest.setExtInvCode("Xandr");
        assertEquals("Xandr", nativeAdRequest.getExtInvCode());

        String postData = getRequestParametersPostData();
        assertTrue(postData.contains("\"ext_inv_code\":\"Xandr\""));
    }

    /**
     * getPostData method is private in UTRequestParameters using reflection to access it.
     * There might be better way of doing this but this works.!!
     * @return postData String
     */
    private String getRequestParametersPostData() {
        String postData = "";
        try {
            Method getPostData = UTRequestParameters.class.getDeclaredMethod("getPostData", null);
            getPostData.setAccessible(true);
            postData = (String) getPostData.invoke(getRequestParams(), null);
            System.out.println("postData = " + postData);

        }catch (Exception e){
            e.printStackTrace();
        }
        return postData;
    }


    // UTRequestParameters is private in NativeAdRequest get it through reflection.
    private UTRequestParameters getRequestParams(){
        UTRequestParameters requestParameters = null;
        try {
            Field field = NativeAdRequest.class.getDeclaredField("requestParameters");
            field.setAccessible(true);
            requestParameters = (UTRequestParameters) field.get(nativeAdRequest);
        }catch (Exception e){
            e.printStackTrace();
        }
        return requestParameters;
    }
}


