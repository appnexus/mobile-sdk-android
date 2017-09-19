package com.appnexus.opensdk;

import com.appnexus.opensdk.shadows.ShadowSettings;
import com.appnexus.opensdk.ut.UTAdResponse;
import com.appnexus.opensdk.ut.adresponse.BaseAdResponse;
import com.appnexus.opensdk.ut.adresponse.CSMSDKAdResponse;
import com.appnexus.opensdk.ut.adresponse.SSMHTMLAdResponse;
import com.appnexus.opensdk.util.RoboelectricTestRunnerWithResources;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.util.LinkedList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

@RunWith(RoboelectricTestRunnerWithResources.class)
@Config(constants = BuildConfig.class, sdk = 21, shadows = {ShadowSettings.class,ShadowLog.class})
public class UTAdResponseTest extends BaseRoboTest{

    UTAdResponse utAdResponse;

    @Override
    public void setup() {
        super.setup();

    }



    /**
     * Tests no ad response
     *
     * @throws Exception
     */
    @Test
    public void testNOResponse() throws Exception {

        String bannerString = TestResponsesUT.noResponse();
        utAdResponse = new UTAdResponse(bannerString,null,MediaType.BANNER, "v");

        assertNotNull(utAdResponse);
        LinkedList<BaseAdResponse> list = utAdResponse.getAdList();
        assertEquals(MediaType.BANNER,utAdResponse.getMediaType());
        assertNull(utAdResponse.getNoAdUrl());
        assertNull(list);
    }

    /**
     * Tests no rtb banner response
     *
     * @throws Exception
     */
    @Test
    public void testRTBNOAdResponse() throws Exception {

        String bannerString = TestResponsesUT.blankBanner();
        utAdResponse = new UTAdResponse(bannerString,null,MediaType.BANNER, "v");

        assertNotNull(utAdResponse);
        LinkedList<BaseAdResponse> list = utAdResponse.getAdList();
        assertEquals(MediaType.BANNER,utAdResponse.getMediaType());
        assertNotNull(utAdResponse.getNoAdUrl());
        assertTrue(list.isEmpty());
    }

    /**
     * Tests rtb banner response
     *
     * @throws Exception
     */
    @Test
    public void testBannerResponse() throws Exception {

        String bannerString = TestResponsesUT.banner();
        utAdResponse = new UTAdResponse(bannerString,null,MediaType.BANNER,"v");

        assertNotNull(utAdResponse);
        LinkedList<BaseAdResponse> list = utAdResponse.getAdList();
        assertNotNull(utAdResponse.getAdList());
        while(!list.isEmpty()){
            BaseAdResponse baseAdResponse = (BaseAdResponse) list.removeFirst();
            assertEquals("rtb",baseAdResponse.getContentSource());

        }
    }

    /**
     * Tests csm & rtb banner response
     *
     * @throws Exception
     */
    @Test
    public void testBannerCSMResponse() throws Exception {
        String bannerCSMString = TestResponsesUT.noFillCSM_RTBBanner();

        utAdResponse = new UTAdResponse(bannerCSMString,null,MediaType.BANNER, "v");

        assertNotNull(utAdResponse);
        LinkedList<BaseAdResponse> list = utAdResponse.getAdList();
        assertNotNull(utAdResponse.getAdList());
        System.out.println("Printing first");
        CSMSDKAdResponse baseCSMSDKAdResponse = (CSMSDKAdResponse) list.getFirst();
        assertEquals("csm", baseCSMSDKAdResponse.getContentSource());
        System.out.println("Printing second");
        BaseAdResponse baseAdResponse = (BaseAdResponse) list.getLast();
        assertEquals("rtb",baseAdResponse.getContentSource());

    }

    /**
     * Tests no content csm banner response
     *
     * @throws Exception
     */
    @Test
    public void testNoBannerCSMResponse() throws Exception {
        String bannerCSMString = TestResponsesUT.mediatedNoFillBanner();
        utAdResponse = new UTAdResponse(bannerCSMString,null,MediaType.BANNER, "v");

        assertNotNull(utAdResponse);
        LinkedList<BaseAdResponse> list = utAdResponse.getAdList();
        assertNotNull(utAdResponse.getAdList());
        System.out.println("Printing first");
        CSMSDKAdResponse baseCSMSDKAdResponse = (CSMSDKAdResponse) list.getFirst();
        assertEquals("csm", baseCSMSDKAdResponse.getContentSource());
        assertNull(baseCSMSDKAdResponse.getAdContent());


    }

    /**
     * Tests ssm banner response
     *
     * @throws Exception
     */
    @Test
    public void testBannerSSMResponse() throws Exception {
        String bannerSSMString = TestResponsesUT.mediatedSSMBanner();
        utAdResponse = new UTAdResponse(bannerSSMString,null,MediaType.BANNER, "v");

        assertNotNull(utAdResponse);
        LinkedList<BaseAdResponse> list = utAdResponse.getAdList();
        assertNotNull(utAdResponse.getAdList());
        System.out.println("Printing first");
        SSMHTMLAdResponse baseSSMHTMLAdResponse = (SSMHTMLAdResponse) list.getFirst();
        assertEquals("ssm", baseSSMHTMLAdResponse.getContentSource());
        assertEquals((TestResponsesUT.SSM_URL), baseSSMHTMLAdResponse.getAdUrl());
    }

    /**
     * Tests no ssm banner response
     *
     * @throws Exception
     */
    @Test
    public void testBannerSSMNoURLResponse() throws Exception {
        String bannerSSMString = TestResponsesUT.mediatedNoSSMBanner();
        utAdResponse = new UTAdResponse(bannerSSMString,null,MediaType.BANNER, "v");

        assertNotNull(utAdResponse);
        @SuppressWarnings("UnusedAssignment") LinkedList<BaseAdResponse> list = utAdResponse.getAdList();
    }



    @Override
    public void tearDown() {
        super.tearDown();
    }
}
