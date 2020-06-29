package com.appnexus.opensdk;

/*
/*
 *    Copyright 2020 APPNEXUS INC
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

import com.appnexus.opensdk.ut.UTAdResponse;
import com.appnexus.opensdk.ut.adresponse.BaseAdResponse;
import com.appnexus.opensdk.ut.adresponse.CSMSDKAdResponse;
import com.appnexus.opensdk.ut.adresponse.SSMHTMLAdResponse;
import org.junit.Test;
import java.util.LinkedList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class MediationTimeoutTest extends BaseViewAdTest {

    UTAdResponse utAdResponse;

    @Override
    public void setup() {
        super.setup();
    }


    /**
     * Test ssm banner server timeout
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
        SSMHTMLAdResponse baseSSMHTMLAdResponse = (SSMHTMLAdResponse) list.getFirst();
        assertEquals("ssm", baseSSMHTMLAdResponse.getContentSource());
        assertEquals(15000, baseSSMHTMLAdResponse.getNetworkTimeout());
    }

    /**
     * Test ssm banner default timeout for negative value
     *
     * @throws Exception
     */
    @Test
    public void testBannerSSMResponseDefaultTimeoutForNegativeValue() throws Exception {
        String bannerSSMString = TestResponsesUT.mediatedSSMBannerWithTimeoutZero();
        utAdResponse = new UTAdResponse(bannerSSMString, null, MediaType.BANNER, "v");

        assertNotNull(utAdResponse);
        LinkedList<BaseAdResponse> list = utAdResponse.getAdList();
        assertNotNull(utAdResponse.getAdList());
        SSMHTMLAdResponse baseSSMHTMLAdResponse = (SSMHTMLAdResponse) list.getFirst();
        assertEquals("ssm", baseSSMHTMLAdResponse.getContentSource());
        assertEquals(15000, baseSSMHTMLAdResponse.getNetworkTimeout());
    }

    /**
     * Test ssm banner default timeout
     *
     * @throws Exception
     */
    @Test
    public void testBannerSSMResponseDefaultTimeout() throws Exception {
        String bannerSSMString = TestResponsesUT.mediatedSSMBannerWithTimeoutZero();
        utAdResponse = new UTAdResponse(bannerSSMString, null, MediaType.BANNER, "v");

        assertNotNull(utAdResponse);
        LinkedList<BaseAdResponse> list = utAdResponse.getAdList();
        assertNotNull(utAdResponse.getAdList());
        SSMHTMLAdResponse baseSSMHTMLAdResponse = (SSMHTMLAdResponse) list.getFirst();
        assertEquals("ssm", baseSSMHTMLAdResponse.getContentSource());
        assertEquals(15000, baseSSMHTMLAdResponse.getNetworkTimeout());
    }

    /**
     * Test csm banner server timeout
     *
     * @throws Exception
     */
    @Test
    public void testBannerCSMResponse() throws Exception {
        String bannerCSMString = TestResponsesUT.mediatedSuccessfulBanner();

        utAdResponse = new UTAdResponse(bannerCSMString, null, MediaType.BANNER, "v");

        assertNotNull(utAdResponse);
        LinkedList<BaseAdResponse> list = utAdResponse.getAdList();
        assertNotNull(utAdResponse.getAdList());
        CSMSDKAdResponse baseCSMSDKAdResponse = (CSMSDKAdResponse) list.getFirst();
        assertEquals("csm", baseCSMSDKAdResponse.getContentSource());
        assertEquals(15000, baseCSMSDKAdResponse.getNetworkTimeout());
    }



    /**
     * Test csm banner default timeout
     *
     * @throws Exception
     */
    @Test
    public void testBannerCSMResponseDefaultTimeout() throws Exception {
        String bannerCSMString = TestResponsesUT.mediatedSuccessfulBannerTimeout();

        utAdResponse = new UTAdResponse(bannerCSMString, null, MediaType.BANNER, "v");

        assertNotNull(utAdResponse);
        LinkedList<BaseAdResponse> list = utAdResponse.getAdList();
        assertNotNull(utAdResponse.getAdList());
        CSMSDKAdResponse baseCSMSDKAdResponse = (CSMSDKAdResponse) list.getFirst();
        assertEquals("csm", baseCSMSDKAdResponse.getContentSource());
        assertEquals(15000, baseCSMSDKAdResponse.getNetworkTimeout());
    }


    /**
     * Test csm banner timeout set on Console
     *
     * @throws Exception
     */
    @Test
    public void testBannerCSMResponseNonZeroTimeout() throws Exception {
        String bannerCSMString = TestResponsesUT.mediatedSuccessfulBannerTimeoutNonZero();

        utAdResponse = new UTAdResponse(bannerCSMString, null, MediaType.BANNER, "v");

        assertNotNull(utAdResponse);
        LinkedList<BaseAdResponse> list = utAdResponse.getAdList();
        assertNotNull(utAdResponse.getAdList());
        CSMSDKAdResponse baseCSMSDKAdResponse = (CSMSDKAdResponse) list.getFirst();
        assertEquals("csm", baseCSMSDKAdResponse.getContentSource());
        assertEquals(200, baseCSMSDKAdResponse.getNetworkTimeout());
    }


    /**
     * Test csm banner default timeout for negative value
     *
     * @throws Exception
     */
    @Test
    public void testBannerCSMResponseDefaultTimeoutForNegativeValue() throws Exception {
        String bannerCSMString = TestResponsesUT.mediatedSuccessfulBannerTimeout();

        utAdResponse = new UTAdResponse(bannerCSMString, null, MediaType.BANNER, "v");

        assertNotNull(utAdResponse);
        LinkedList<BaseAdResponse> list = utAdResponse.getAdList();
        assertNotNull(utAdResponse.getAdList());
        CSMSDKAdResponse baseCSMSDKAdResponse = (CSMSDKAdResponse) list.getFirst();
        assertEquals("csm", baseCSMSDKAdResponse.getContentSource());
        assertEquals(15000, baseCSMSDKAdResponse.getNetworkTimeout());
    }


    /**
     * Test csm interstitial server timeout
     *
     * @throws Exception
     */
    @Test
    public void testInterstitialCSMResponse() throws Exception {
        String bannerCSMString = TestResponsesUT.mediatedSuccessfulInterstitial();

        utAdResponse = new UTAdResponse(bannerCSMString, null, MediaType.BANNER, "v");

        assertNotNull(utAdResponse);
        LinkedList<BaseAdResponse> list = utAdResponse.getAdList();
        assertNotNull(utAdResponse.getAdList());
        CSMSDKAdResponse baseCSMSDKAdResponse = (CSMSDKAdResponse) list.getFirst();
        assertEquals("csm", baseCSMSDKAdResponse.getContentSource());
        assertEquals(15000, baseCSMSDKAdResponse.getNetworkTimeout());
    }


    /**
     * Test csm interstitial default timeout for negative value
     *
     * @throws Exception
     */
    @Test
    public void testInterstitialCSMResponseDefaultTimeoutForNegativeValue() throws Exception {
        String bannerCSMString = TestResponsesUT.mediatedSuccessfulInterstitialTimeout();

        utAdResponse = new UTAdResponse(bannerCSMString, null, MediaType.BANNER, "v");

        assertNotNull(utAdResponse);
        LinkedList<BaseAdResponse> list = utAdResponse.getAdList();
        assertNotNull(utAdResponse.getAdList());
        CSMSDKAdResponse baseCSMSDKAdResponse = (CSMSDKAdResponse) list.getFirst();
        assertEquals("csm", baseCSMSDKAdResponse.getContentSource());
        assertEquals(15000, baseCSMSDKAdResponse.getNetworkTimeout());
    }



    /**
     * Test csm interstitial default timeout
     *
     * @throws Exception
     */
    @Test
    public void testInterstitialCSMResponseDefaultTimeout() throws Exception {
        String bannerCSMString = TestResponsesUT.mediatedSuccessfulInterstitialTimeout();

        utAdResponse = new UTAdResponse(bannerCSMString, null, MediaType.BANNER, "v");

        assertNotNull(utAdResponse);
        LinkedList<BaseAdResponse> list = utAdResponse.getAdList();
        assertNotNull(utAdResponse.getAdList());
        CSMSDKAdResponse baseCSMSDKAdResponse = (CSMSDKAdResponse) list.getFirst();
        assertEquals("csm", baseCSMSDKAdResponse.getContentSource());
        assertEquals(15000, baseCSMSDKAdResponse.getNetworkTimeout());
    }



    /**
     * Test csm native interstitial set on console
     *
     * @throws Exception
     */
    @Test
    public void testInterstitialCSMResponseNonZeroTimeout() throws Exception {
        String bannerCSMString = TestResponsesUT.mediatedSuccessfulInterstitialTimeoutNonZero();

        utAdResponse = new UTAdResponse(bannerCSMString, null, MediaType.BANNER, "v");

        assertNotNull(utAdResponse);
        LinkedList<BaseAdResponse> list = utAdResponse.getAdList();
        assertNotNull(utAdResponse.getAdList());
        CSMSDKAdResponse baseCSMSDKAdResponse = (CSMSDKAdResponse) list.getFirst();
        assertEquals("csm", baseCSMSDKAdResponse.getContentSource());
        assertEquals(200, baseCSMSDKAdResponse.getNetworkTimeout());
    }


    /**
     * Test csm native server timeout
     *
     * @throws Exception
     */
    @Test
    public void testNativeCSMResponse() throws Exception {
        String bannerCSMString = TestResponsesUT.mediatedSuccessfulNative();

        utAdResponse = new UTAdResponse(bannerCSMString, null, MediaType.BANNER, "v");

        assertNotNull(utAdResponse);
        LinkedList<BaseAdResponse> list = utAdResponse.getAdList();
        assertNotNull(utAdResponse.getAdList());
        CSMSDKAdResponse baseCSMSDKAdResponse = (CSMSDKAdResponse) list.getFirst();
        assertEquals("csm", baseCSMSDKAdResponse.getContentSource());
        assertEquals(15000, baseCSMSDKAdResponse.getNetworkTimeout());
    }


    /**
     * Test csm native default timeout for negative value
     *
     * @throws Exception
     */
    @Test
    public void testNativeCSMResponseDefaultTimeoutForNegativeValue() throws Exception {
        String bannerCSMString = TestResponsesUT.mediatedSuccessfulNativeTimeout();

        utAdResponse = new UTAdResponse(bannerCSMString, null, MediaType.BANNER, "v");

        assertNotNull(utAdResponse);
        LinkedList<BaseAdResponse> list = utAdResponse.getAdList();
        assertNotNull(utAdResponse.getAdList());
        CSMSDKAdResponse baseCSMSDKAdResponse = (CSMSDKAdResponse) list.getFirst();
        assertEquals("csm", baseCSMSDKAdResponse.getContentSource());
        assertEquals(15000, baseCSMSDKAdResponse.getNetworkTimeout());
    }




    /**
     * Test csm native timeout set on console
     *
     * @throws Exception
     */
    @Test
    public void testNativeCSMResponseNonZeroTimeout() throws Exception {
        String bannerCSMString = TestResponsesUT.mediatedSuccessfulNativeTimeoutNonZero();

        utAdResponse = new UTAdResponse(bannerCSMString, null, MediaType.BANNER, "v");

        assertNotNull(utAdResponse);
        LinkedList<BaseAdResponse> list = utAdResponse.getAdList();
        assertNotNull(utAdResponse.getAdList());
        CSMSDKAdResponse baseCSMSDKAdResponse = (CSMSDKAdResponse) list.getFirst();
        assertEquals("csm", baseCSMSDKAdResponse.getContentSource());
        assertEquals(200, baseCSMSDKAdResponse.getNetworkTimeout());
    }

}
