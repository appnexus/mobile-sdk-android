/*
 *    Copyright 2019 APPNEXUS INC
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

public class ANAdResponseInfo {

    String creativeId = "";
    AdType adType;
    String tagId = "";
    int memberID;
    String contentSource = "";
    String networkName = "";
    String auctionId = "";
    private double cpm;
    private double cpmPublisherCurrency;
    private String publisherCurrencyCode = "";


    /**
     * Retrieve the Creative Id  of the creative .
     *
     * @return the creativeId, returns empty string if not set
     */
    public String getCreativeId() {
        return creativeId;
    }

    public void setCreativeId(String creativeId) {
        this.creativeId = creativeId;
    }

    /**
     * Retrieve the AdType of the Ad being served
     * AdType can be UNKNOWN/BANNER/VIDEO/NATIVE
     *
     * @return AdType of the Creative, default value is set to UNKNOWN if not set
     */
    public AdType getAdType() {
        return adType;
    }

    public void setAdType(AdType adType) {
        this.adType = adType;
    }

    /**
     * Retrieve the Tag Id
     * @return the tag id of the Ad being served.
     * */
    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    /**
     * Retrieve the member ID.
     *
     * @return the member id that this Ad belongs to.
     */
    public int getBuyMemberId() {
        return memberID;
    }

    public void setBuyMemberId(int memberID) {
        this.memberID = memberID;
    }

    /**
     * Retrieve the content source
     *
     * @return the content source of the Ad being served.
     */
    public String getContentSource() {
        return contentSource;
    }

    public void setContentSource(String contentSource) {
        this.contentSource = contentSource;
    }

    /**
     * Retrieve the Network Name
     *
     * @return the network name of the mediated Ad being served.
     */
    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    /**
     * Retrieve the auctionId
     *
     * @return auctionId, the auction identifier is unique id generated for the ad request.
     */
    public String getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(String auctionId) {
        this.auctionId = auctionId;
    }

    /**
     * Retrieve the cpm
     * */
    public double getCpm() {
        return cpm;
    }

    public void setCpm(double cpm) {
        this.cpm = cpm;
    }

    /**
     * Retrieve the cpm publisher currency
     * */
    public double getCpmPublisherCurrency() {
        return cpmPublisherCurrency;
    }

    public void setCpmPublisherCurrency(double cpmPublisherCurrency) {
        this.cpmPublisherCurrency = cpmPublisherCurrency;
    }

    /**
     * Retrieve the publisher currency code
     * */
    public String getPublisherCurrencyCode() {
        return publisherCurrencyCode;
    }

    public void setPublisherCurrencyCode(String publisherCurrencyCode) {
        this.publisherCurrencyCode = publisherCurrencyCode;
    }

}
