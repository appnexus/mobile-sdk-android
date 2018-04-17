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

import static junit.framework.Assert.assertTrue;


/**
 * This tests if the options set on a BannerAdView are represented in the UT Post data in the right format.
 */
@Config(constants = BuildConfig.class, sdk = 21,
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




    // Setting MAX size should reset all the other size params set earlier
    @Test
    public void testSetMAXSizeOverRidesEverythingElse(){
        setAdSize();
        setAdSizesArray();
        setMaxSize();
        assertSetMaxSize();
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

