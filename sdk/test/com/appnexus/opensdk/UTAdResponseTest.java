package com.appnexus.opensdk;

import com.appnexus.opensdk.shadows.ShadowSettings;
import com.appnexus.opensdk.ut.UTAdResponse;
import com.appnexus.opensdk.ut.UTConstants;
import com.appnexus.opensdk.ut.adresponse.BaseAdResponse;
import com.appnexus.opensdk.ut.adresponse.CSMSDKAdResponse;
import com.appnexus.opensdk.ut.adresponse.CSRAdResponse;
import com.appnexus.opensdk.ut.adresponse.RTBNativeAdResponse;
import com.appnexus.opensdk.ut.adresponse.RTBVASTAdResponse;
import com.appnexus.opensdk.ut.adresponse.SSMHTMLAdResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.util.HashMap;
import java.util.LinkedList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21, shadows = {ShadowSettings.class, ShadowLog.class})
public class UTAdResponseTest extends BaseRoboTest {

    UTAdResponse utAdResponse;

    @Override
    public void setup() {
        super.setup();

    }

    @Test
    public void testCSRNative() {
        utAdResponse = new UTAdResponse(TestResponsesUT.csrNativeSuccessful(), null, MediaType.NATIVE, "v");
        assertNotNull(utAdResponse);
        LinkedList<BaseAdResponse> list = utAdResponse.getAdList();
        assertEquals(1, list.size());
        assertEquals(MediaType.NATIVE, utAdResponse.getMediaType());
        BaseAdResponse response = list.get(0);
        assertEquals("csr", response.getContentSource());
        CSRAdResponse csr = (CSRAdResponse) response;
        assertEquals("com.appnexus.opensdk.testviews.CSRNativeSuccessful", csr.getClassName());
        assertEquals("{\"placement_id\":\"333673923704415_469697383435401\"}", csr.getPayload());
    }

    /**
     * Tests no ad response
     *
     * @throws Exception
     */
    @Test
    public void testNOResponse() throws Exception {

        String bannerString = TestResponsesUT.noResponse();
        utAdResponse = new UTAdResponse(bannerString, null, MediaType.BANNER, "v");

        assertNotNull(utAdResponse);
        LinkedList<BaseAdResponse> list = utAdResponse.getAdList();
        assertEquals(MediaType.BANNER, utAdResponse.getMediaType());
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
        utAdResponse = new UTAdResponse(bannerString, null, MediaType.BANNER, "v");

        assertNotNull(utAdResponse);
        LinkedList<BaseAdResponse> list = utAdResponse.getAdList();
        assertEquals(MediaType.BANNER, utAdResponse.getMediaType());
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
        utAdResponse = new UTAdResponse(bannerString, null, MediaType.BANNER, "v");

        assertNotNull(utAdResponse);
        LinkedList<BaseAdResponse> list = utAdResponse.getAdList();
        assertNotNull(utAdResponse.getAdList());
        while (!list.isEmpty()) {
            BaseAdResponse baseAdResponse = (BaseAdResponse) list.removeFirst();
            assertEquals("rtb", baseAdResponse.getContentSource());
            assertEquals("6332753", baseAdResponse.getAdResponseInfo().getCreativeId());
        }
    }


    /**
     * Tests rtb banner Video response
     *
     * @throws Exception
     */
    @Test
    public void testBannerVideoResponse() throws Exception {

        String bannerString = TestResponsesUT.rtbVASTVideo();
        utAdResponse = new UTAdResponse(bannerString, null, MediaType.BANNER, "v");

        assertNotNull(utAdResponse);
        LinkedList<BaseAdResponse> list = utAdResponse.getAdList();
        assertNotNull(utAdResponse.getAdList());
        while (!list.isEmpty()) {
            RTBVASTAdResponse vastAdResponse = (RTBVASTAdResponse) list.removeFirst();
            assertEquals("rtb", vastAdResponse.getContentSource());
            assertEquals("video", vastAdResponse.getAdType());
            assertTrue(vastAdResponse.getAdContent().contains("<VAST version=\"2.0\">"));
            HashMap<String, Object> extras = vastAdResponse.getExtras();
            assertTrue(extras.containsKey("MRAID"));
            assertTrue(extras.containsValue(true));
        }
    }


    /**
     * Tests rtb banner Native response
     *
     * @throws Exception
     */
    @Test
    public void testBannerNativeResponse() throws Exception {

        String bannerString = TestResponsesUT.anNative();
        utAdResponse = new UTAdResponse(bannerString, null, MediaType.BANNER, "v");

        assertNotNull(utAdResponse);
        LinkedList<BaseAdResponse> list = utAdResponse.getAdList();
        assertNotNull(utAdResponse.getAdList());
        while (!list.isEmpty()) {
            RTBNativeAdResponse nativeAdResponse = (RTBNativeAdResponse) list.removeFirst();
            assertEquals(UTConstants.RTB, nativeAdResponse.getContentSource());
            assertEquals(UTConstants.AD_TYPE_NATIVE, nativeAdResponse.getAdType());
            assertTrue(nativeAdResponse.getNativeAdResponse().getIconUrl().contains("http://path_to_icon.com"));
            assertTrue(nativeAdResponse.getNativeAdResponse().getImageUrl().contains("http://path_to_main.com"));
            assertEquals(false, nativeAdResponse.getNativeAdResponse().isOpenNativeBrowser());
            assertEquals(true, nativeAdResponse.getNativeAdResponse().getLoadsInBackground());
            assertEquals("47772560", nativeAdResponse.getAdResponseInfo().getCreativeId());
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

        utAdResponse = new UTAdResponse(bannerCSMString, null, MediaType.BANNER, "v");

        assertNotNull(utAdResponse);
        LinkedList<BaseAdResponse> list = utAdResponse.getAdList();
        assertNotNull(utAdResponse.getAdList());
        System.out.println("Printing first");
        CSMSDKAdResponse baseCSMSDKAdResponse = (CSMSDKAdResponse) list.getFirst();
        assertEquals("csm", baseCSMSDKAdResponse.getContentSource());
        assertEquals("44863345", baseCSMSDKAdResponse.getAdResponseInfo().getCreativeId());
        System.out.println("Printing second");
        BaseAdResponse baseAdResponse = (BaseAdResponse) list.getLast();
        assertEquals("rtb", baseAdResponse.getContentSource());
        assertEquals("6332753", baseAdResponse.getAdResponseInfo().getCreativeId());

    }

    /**
     * Tests no content csm banner response
     *
     * @throws Exception
     */
    @Test
    public void testNoBannerCSMResponse() throws Exception {
        String bannerCSMString = TestResponsesUT.mediatedNoFillBanner();
        utAdResponse = new UTAdResponse(bannerCSMString, null, MediaType.BANNER, "v");

        assertNotNull(utAdResponse);
        LinkedList<BaseAdResponse> list = utAdResponse.getAdList();
        assertNotNull(utAdResponse.getAdList());
        System.out.println("Printing first");
        CSMSDKAdResponse baseCSMSDKAdResponse = (CSMSDKAdResponse) list.getFirst();
        assertEquals("csm", baseCSMSDKAdResponse.getContentSource());
        assertNull(baseCSMSDKAdResponse.getAdContent());
        assertEquals("44863345", baseCSMSDKAdResponse.getAdResponseInfo().getCreativeId());


    }

    /**
     * Tests ssm banner response
     *
     * @throws Exception
     */
    @Test
    public void testBannerSSMResponse() throws Exception {
        String bannerSSMString = TestResponsesUT.mediatedSSMBanner();
        utAdResponse = new UTAdResponse(bannerSSMString, null, MediaType.BANNER, "v");

        assertNotNull(utAdResponse);
        LinkedList<BaseAdResponse> list = utAdResponse.getAdList();
        assertNotNull(utAdResponse.getAdList());
        System.out.println("Printing first");
        SSMHTMLAdResponse baseSSMHTMLAdResponse = (SSMHTMLAdResponse) list.getFirst();
        assertEquals("ssm", baseSSMHTMLAdResponse.getContentSource());
        assertEquals((TestResponsesUT.SSM_URL), baseSSMHTMLAdResponse.getAdUrl());
        assertEquals("44863345", baseSSMHTMLAdResponse.getAdResponseInfo().getCreativeId());
    }

    /**
     * Tests no ssm banner response
     *
     * @throws Exception
     */
    @Test
    public void testBannerSSMNoURLResponse() throws Exception {
        String bannerSSMString = TestResponsesUT.mediatedNoSSMBanner();
        utAdResponse = new UTAdResponse(bannerSSMString, null, MediaType.BANNER, "v");

        assertNotNull(utAdResponse);
        @SuppressWarnings("UnusedAssignment") LinkedList<BaseAdResponse> list = utAdResponse.getAdList();
    }


    @Override
    public void tearDown() {
        super.tearDown();
    }
}
